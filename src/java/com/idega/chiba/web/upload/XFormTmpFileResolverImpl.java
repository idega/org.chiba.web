package com.idega.chiba.web.upload;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.core.file.tmp.TmpFileResolver;
import com.idega.core.file.tmp.TmpFileResolverType;
import com.idega.core.file.tmp.TmpFilesModifyStrategy;
import com.idega.util.CoreConstants;
import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.6 $
 * 
 *          Last modified: $Date: 2009/02/20 14:25:15 $ by $Author: civilis $
 */
@Scope("singleton")
@TmpFileResolverType("xformVariables")
@Service("xfAttResResolver")
public class XFormTmpFileResolverImpl implements TmpFileResolver {

	public static String UPLOADS_PATH;
	private static final String VARIABLE_NAME_VAR = "variableName";
	private final XPathUtil entriesXPUT;
	private final XPathUtil entryFilesXPath;
	private final XPathUtil byVariableNameXpath;

	public XFormTmpFileResolverImpl() {
		entriesXPUT = new XPathUtil("./child::entry");
		entryFilesXPath = new XPathUtil(".//entry[@filename]");
		byVariableNameXpath = new XPathUtil(
				".//node()[@mapping = $variableName]");
	}

	protected Collection<URI> resolveReplaceFilesUris(String identifier,
			Object resource, TmpFilesModifyStrategy replaceStrategy) {

		if (!(resource instanceof Node)) {

			Logger.getLogger(getClass().getName()).log(
					Level.WARNING,
					"Wrong resource provided. Expected of type "
							+ Node.class.getName()
							+ ", but got "
							+ (resource != null ? resource.getClass().getName()
									: null));
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

		if (entries != null && entries.getLength() != 0) {

			ArrayList<URI> uris = new ArrayList<URI>(entries.getLength());

			for (int i = 0; i < entries.getLength(); i++) {

				Node item = entries.item(i).getChildNodes().item(0);
				String uriStr = item.getTextContent();

				if (!CoreConstants.EMPTY.equals(uriStr)
						&& !uriStr.startsWith(CoreConstants.NEWLINE)) {

					try {
						URI uri = new URI(uriStr);

						if (replaceStrategy != null)
							uri = replaceStrategy.executeMove(uri, item);

						if (uri != null)
							uris.add(uri);

					} catch (Exception e) {
						Logger.getLogger(getClass().getName()).log(
								Level.WARNING,
								"Exception while decoding and creating uri from uri string. Skipping. Uri="
										+ uriStr, e);
					}
				}
			}

			return uris;
		}

		return new ArrayList<URI>(0);
	}

	public Collection<URI> resolveFilesUris(String identifier, Object resource) {

		return resolveReplaceFilesUris(identifier, resource, null);
	}

	public void replaceAllFiles(Object resource,
			TmpFilesModifyStrategy replaceStrategy) {

		resolveReplaceFilesUris(null, resource, replaceStrategy);
	}

	public String getRealBasePath() {

		return UPLOADS_PATH;
	}

	public void setRealBasePath(String basePath) {

		UPLOADS_PATH = basePath;
	}

	public String getContextPath() {

		return "xformsAttachments/";
	}

	public Collection<File> getFilesToCleanUp(String identifier, Object resource) {

		if (!(resource instanceof Node)) {

			Logger.getLogger(getClass().getName()).log(
					Level.WARNING,
					"Wrong resource provided. Expected of type "
							+ Node.class.getName() + ", but got "
							+ resource.getClass().getName());
			return new ArrayList<File>(0);
		}

		NodeList entries = entryFilesXPath.getNodeset((Node) resource);

		if (entries != null && entries.getLength() != 0) {

			ArrayList<File> filesToCleanup = new ArrayList<File>(entries
					.getLength());
			String realBasePath = getRealBasePath();

			for (int i = 0; i < entries.getLength(); i++) {

				String uriStr = entries.item(i).getTextContent();

				if (!CoreConstants.EMPTY.equals(uriStr)) {

					if (uriStr.contains(realBasePath)) {

						String[] spltd = uriStr.split(realBasePath);
						String leftover = spltd[spltd.length - 1];
						String ff = leftover.substring(0, leftover
								.indexOf(CoreConstants.SLASH));

						if (ff.startsWith(CoreConstants.SLASH))
							ff = ff.substring(1);

						String complURI = realBasePath + ff;

						filesToCleanup.add(new File(complURI));
					}
				}
			}

			return filesToCleanup;
		}

		return new ArrayList<File>(0);
	}

	protected Element getUploadsElement(String identifier, Node context) {

		if (context instanceof Element
				&& identifier.equals(((Element) context)
						.getAttribute("mapping"))) {

			return (Element) context;
		}

		synchronized (byVariableNameXpath) {

			byVariableNameXpath.setVariable(VARIABLE_NAME_VAR, identifier);
			Element n = (Element) byVariableNameXpath.getNode(context);
			return n;
		}
	}

	public void uploadToTmpLocation(String pathDirRelativeToBase,
			String fileName, InputStream inputStream) {
		throw new UnsupportedOperationException(
				"Unsupported, use default tmp files resolver");
	}

	public String getTmpUploadDir(String pathDirRelativeToBase) {
		throw new UnsupportedOperationException(
				"Unsupported, use default tmp files resolver");
	}

	public File getFile(String pathDirRelativeToBase, String fileName) {
		throw new UnsupportedOperationException(
				"Unsupported, use default tmp files resolver");
	}
}