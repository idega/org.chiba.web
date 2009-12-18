package org.chiba.session;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.chiba.adapter.ui.XSLTGenerator;
import org.chiba.web.IWBundleStarter;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xslt.impl.CachingTransformerService;
import org.chiba.xml.xslt.impl.FileResourceResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;

public class IdegaXFormSerializer extends DefaultSerializer {

	private ChibaBean processor;
	
	public IdegaXFormSerializer(ChibaBean processor) {
		super(processor);
		
		this.processor = processor;
	}

	@Override
	public Document serialize() throws IOException {
        Document result;
        try {
            result = serializeHostDocument();
        } catch (XFormsException e) {
            throw new IOException("Error while serializing host document: " + e.getMessage());
        } catch (TransformerException e) {
            throw new IOException("Error while transforming host document: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI: " + e.getMessage());
        }
        return result;
    }

    private Document serializeHostDocument() throws XFormsException, TransformerException, URISyntaxException {
        Document in = this.processor.getXMLContainer();
        Document out = DOMUtil.newDocument(true,false);

        //resetting internal DOM to original state
        resetForm(in, out);
        inlineInstances(out);

        return out;
    }

    @SuppressWarnings("unchecked")
	private void inlineInstances(Document out) {
        JXPathContext context = JXPathContext.newContext(out);
        List<Model> models = this.processor.getContainer().getModels();
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);

            List<Instance> instances = model.getInstances();
            for (int j = 0; j < instances.size(); j++) {
                Instance instance = instances.get(j);
                String id = instance.getId();

                //get node from out
                String search = "//*[@id='" + id + "']";
                Node outInstance = (Node) context.getPointer(search).getNode();
                Node imported = out.adoptNode(instance.getInstanceDocument().getDocumentElement());
                outInstance.appendChild(imported);
            }
        }
    }

    private void resetForm(Document in, Document out) throws TransformerException, URISyntaxException, XFormsException {
        CachingTransformerService transformerService = new CachingTransformerService(new FileResourceResolver());
        transformerService.setTransformerFactory(TransformerFactory.newInstance());
        
        IWBundle chibaBundle = IWMainApplication.getDefaultIWMainApplication().getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
        String path = chibaBundle.getResourcesRealPath() + "/xslt/reset.xsl";
        String xslFilePath = "file:" + path;
        transformerService.getTransformer(new URI(xslFilePath));

        XSLTGenerator generator = new XSLTGenerator();
        generator.setTransformerService(transformerService);
        generator.setStylesheetURI(new URI(xslFilePath));
        generator.setInput(in);
        generator.setOutput(out);
        generator.generate();
    }
}