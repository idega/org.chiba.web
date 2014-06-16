package com.idega.chiba.web.upload;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.chiba.ChibaConstants;
import com.idega.core.file.tmp.TmpFileResolver;
import com.idega.core.file.tmp.TmpFileResolverType;
import com.idega.core.file.tmp.TmpFilesModifyStrategy;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.6 $
 *
 *          Last modified: $Date: 2009/02/20 14:25:15 $ by $Author: civilis $
 */

@Service("xfAttResResolver")
@Scope(BeanDefinition.SCOPE_SINGLETON)
@TmpFileResolverType("xformVariables")
public class XFormTmpFileResolverImpl implements TmpFileResolver {

	public static String UPLOADS_PATH;

	private static final String VARIABLE_NAME_VAR = "variableName";
	private static final Logger LOGGER = Logger.getLogger(XFormTmpFileResolverImpl.class.getName());

	private final XPathUtil entriesXPUT, entryFilesXPath, byVariableNameXpath;

	public XFormTmpFileResolverImpl() {
		entriesXPUT = new XPathUtil("./child::entry");
		entryFilesXPath = new XPathUtil(".//entry[@filename]");
		byVariableNameXpath = new XPathUtil(".//node()[@mapping = $variableName]");
	}

	protected Collection<URI> resolveReplaceFilesUris(String identifier, Object resource, TmpFilesModifyStrategy replaceStrategy) {
		if (!(resource instanceof Node)) {
			LOGGER.warning("Wrong resource provided. Expected of type "	+ Node.class.getName() + ", but got " +
					(resource == null ? "null" : resource.getClass().getName()));
			return new ArrayList<URI>(0);
		}

		final NodeList entries;
		final Node instance = (Node) resource;

		if (identifier == null) {
			entries = entryFilesXPath.getNodeset(instance);
		} else {
			Element node = getUploadsElement(identifier, instance);
			entries = entriesXPUT.getNodeset(node);
		}
		if (entries == null || entries.getLength() == 0)
			return new ArrayList<URI>(0);

		Collection<URI> uris = new ArrayList<URI>(entries.getLength());
		for (int i = 0; i < entries.getLength(); i++) {
			Node item = entries.item(i).getChildNodes().item(0);
			String uriStr = item.getTextContent();
			uriStr = uriStr.trim();

			if (StringUtil.isEmpty(uriStr) || uriStr.startsWith(CoreConstants.NEWLINE)) {
				LOGGER.warning("Provided URI string is empty or not defined: '" + uriStr + "'");
				continue;
			}

			try {
				URI uri = new URI(uriStr);
				if (replaceStrategy != null)
					uri = replaceStrategy.executeMove(uri, item);

				if (uri != null)
					uris.add(uri);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception while decoding and creating URI object (" + URI.class.getName() + ") from string '" + uriStr +
						"'. Skipping!", e);
			}
		}

		return uris;
	}

	@Override
	public Collection<URI> resolveFilesUris(String identifier, Object resource) {
		return resolveReplaceFilesUris(identifier, resource, null);
	}

	@Override
	public void replaceAllFiles(Object resource, TmpFilesModifyStrategy replaceStrategy) {
		resolveReplaceFilesUris(null, resource, replaceStrategy);
	}

	@Override
	public String getRealBasePath() {
		return UPLOADS_PATH;
	}

	@Override
	public void setRealBasePath(String basePath) {
		UPLOADS_PATH = basePath;
	}

	@Override
	public String getContextPath() {
		return "xformsAttachments/";
	}

	@Override
	public Collection<File> getFilesToCleanUp(String identifier, Object resource) {
		if (!(resource instanceof Node)) {
			LOGGER.warning("Wrong resource provided. Expected of type " + Node.class.getName() + ", but got " + resource.getClass().getName());
			return new ArrayList<File>(0);
		}

		NodeList entries = entryFilesXPath.getNodeset((Node) resource);
		if (entries == null || entries.getLength() == 0)
			return new ArrayList<File>(0);

		Collection<File> filesToCleanup = new ArrayList<File>(entries.getLength());
		String realBasePath = getRealBasePath();
		for (int i = 0; i < entries.getLength(); i++) {
			String uriStr = entries.item(i).getTextContent();
			if (StringUtil.isEmpty(uriStr)) {
				LOGGER.warning("Provided file path is empty or not defined: '" + uriStr + "'");
				continue;
			}

			if (uriStr.contains(realBasePath)) {
				String[] spltd = uriStr.split(realBasePath);
				String leftover = spltd[spltd.length - 1];
				String ff = leftover.substring(0, leftover.indexOf(CoreConstants.SLASH));
				if (ff.startsWith(CoreConstants.SLASH))
					ff = ff.substring(1);

				String complURI = realBasePath + ff;
				filesToCleanup.add(new File(complURI));
			}
		}

		return filesToCleanup;
	}

	protected Element getUploadsElement(String identifier, Node context) {
		if (context instanceof Element && identifier.equals(((Element) context).getAttribute(ChibaConstants.MAPPING)))
			return (Element) context;

		byVariableNameXpath.setVariable(VARIABLE_NAME_VAR, identifier);
		Element n = (Element) byVariableNameXpath.getNode(context);
		return n;
	}

	@Override
	public void uploadToTmpLocation(String pathDirRelativeToBase, String fileName, InputStream inputStream) {
		throw new UnsupportedOperationException("Unsupported, use default tmp files resolver");
	}

	@Override
	public String getTmpUploadDir(String pathDirRelativeToBase) {
		throw new UnsupportedOperationException("Unsupported, use default tmp files resolver");
	}

	@Override
	public File getFile(String pathDirRelativeToBase, String fileName) {
		throw new UnsupportedOperationException("Unsupported, use default tmp files resolver");
	}

	@Override
	public void uploadToTmpLocation(String pathDirRelativeToBase, String fileName, InputStream inputStream, boolean closeStream) {
		throw new UnsupportedOperationException("Unsupported, use default tmp files resolver");
	}
}