package org.chiba.xml.xforms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.chiba.session.IdegaXFormSerializer;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import com.idega.util.IOUtil;
import com.idega.util.xml.XmlUtil;

public class IdegaChibaBean extends ChibaBean {

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		IdegaXFormSerializer serializer = new IdegaXFormSerializer(this);
		Document serializedForm = serializer.serialize();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			XmlUtil.prettyPrintDOM(serializedForm, output);
			objectOutput.write(output.toByteArray());
			
			objectOutput.flush();
			objectOutput.close();
		} catch (TransformerException e) {
			throw new IOException("Error during serialization transform: " + e.getMessage());
		} catch (Exception e) {
			throw new IOException("Some fatal error occurred when writing Document into output stream: " + e.getMessage());
		} finally {
			IOUtil.close(output);
		}
	}
	
	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		//	TODO: use real length!
        byte[] buffer = new byte[5242880];	//	5 MB now...
        
        int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = objectInput.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		
		byte[] realData = new byte[offset];
		System.arraycopy(buffer, 0, realData, 0, realData.length);
		InputStream stream = null;
		Document host = null;
		try {
			stream = new ByteArrayInputStream(realData);
			host = XmlUtil.getXMLDocument(stream);
			setXMLContainer(host.getDocumentElement());
		} catch (XFormsException e) {
			throw new IOException("An XForms error occurred when passing the host document: " + e.getMessage());
		} catch (Exception e) {
			throw new IOException("Some fatal error occurred when parsing bytes: " + e.getMessage());
		} finally {
			IOUtil.close(stream);
			buffer = null;
			realData = null;
		}
	}
}