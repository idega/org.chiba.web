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
			FileInfo finfo = furihandler.getFileInfo(uri);

//			trying to resolve parent folder, and reuse that
			String path = uri.getPath();

			String parentFolder = resolveParentFolder(path);

			if(parentFolder == null)
				parentFolder = String.valueOf(System.currentTimeMillis());

//			building slide store path
			String folder = REPOSITORY_STORE_PATH+parentFolder+CoreConstants.SLASH;
			newUri = new URI(REPOSITORY_SCHEME, folder+finfo.getFileName(), null);

			item.setTextContent(newUri.toString());

			if(System.getProperty(IdegaBaseTest.testSystemProp) == null) {
				fis = furihandler.getFile(uri);
				getRepositoryService().uploadFileAndCreateFoldersFromStringAsRoot(folder, finfo.getFileName(), fis, null);
			}

		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while resolving file by uri="+uri+", skipping", e);
			newUri = null;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while moving file resource from uri="+uri+" to uri="+newUri, e);
			newUri = null;
		} finally {

			if(fis != null)
				try { fis.close(); } catch (Exception e) { Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception closing input stream", e);}
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