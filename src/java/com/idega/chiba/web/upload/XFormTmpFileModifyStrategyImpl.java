package com.idega.chiba.web.upload;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import com.idega.chiba.ChibaConstants;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.tmp.TmpFileResolverType;
import com.idega.core.file.tmp.TmpFilesModifyStrategy;
import com.idega.core.file.util.FileInfo;
import com.idega.core.file.util.FileURIHandler;
import com.idega.core.file.util.FileURIHandlerFactory;
import com.idega.core.test.base.IdegaBaseTest;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.StringHandler;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/30 13:31:50 $ by $Author: civilis $
 */

@Service
@TmpFileResolverType(ChibaConstants.XFORM_REPOSITORY)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class XFormTmpFileModifyStrategyImpl extends DefaultSpringBean implements TmpFilesModifyStrategy {

	static final String REPOSITORY_STORE_PATH = "/files/forms/saved/uploadedFiles/";
	static final String REPOSITORY_SCHEME = CoreConstants.REPOSITORY;

	@Autowired
	private FileURIHandlerFactory fileURIHandlerFactory;

	@Override
	public URI executeMove(URI uri, Object resource) {
		if (!(resource instanceof Node))
			throw new IllegalArgumentException("Wrong type resource argument provided = "+resource.getClass().getName()+" instead of " +
					Node.class.getName());

		Node item = (Node) resource;

		FileURIHandler furihandler = getFileURIHandlerFactory().getHandler(uri);
		InputStream fis = null;
		URI newUri = null;

		try {
			FileInfo fInfo = furihandler.getFileInfo(uri);

			//	Trying to resolve parent folder, and reuse that
			String path = uri.getPath();

			String parentFolder = resolveParentFolder(path);

			if(parentFolder == null)
				parentFolder = String.valueOf(System.currentTimeMillis());

			//	Building repository store path
			String folder = REPOSITORY_STORE_PATH+parentFolder+CoreConstants.SLASH;
			String fileName = fInfo.getFileName();
			fileName = StringHandler.stripNonRomanCharacters(fileName, new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_', '.'});
			fileName = StringHandler.removeWhiteSpace(fileName);
			fInfo.setFileName(fileName);
			newUri = new URI(REPOSITORY_SCHEME, folder+fileName, null);

			item.setTextContent(newUri.toString());

			if (System.getProperty(IdegaBaseTest.testSystemProp) == null) {
				fis = furihandler.getFile(uri);
				if (!getRepositoryService().uploadFileAndCreateFoldersFromStringAsRoot(folder, fileName, fis, null)) {
					Logger.getLogger(getClass().getName()).warning("Error while moving file resource from uri="+uri+" to uri="+newUri);
				}
			}
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while resolving file by uri="+uri+", skipping", e);
			newUri = null;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while moving file resource from uri="+uri+" to uri="+newUri, e);
			newUri = null;
		} finally {
			IOUtil.close(fis);
		}

		return newUri;
	}

	private String resolveParentFolder(String path) {

		String parentFolder = null;

		if(path.contains(CoreConstants.SLASH)) {

			String[] vals = path.split(CoreConstants.SLASH);

			if(vals.length > 1) {

				parentFolder = vals[vals.length-2];
			}
		}

		return !CoreConstants.EMPTY.equals(parentFolder) ? parentFolder : null;
	}

	public FileURIHandlerFactory getFileURIHandlerFactory() {
		return fileURIHandlerFactory;
	}

}