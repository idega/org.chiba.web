package com.idega.chiba.web.upload;

import java.io.InputStream;
import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.idega.chiba.ChibaConstants;
import com.idega.core.file.tmp.TmpFileResolver;
import com.idega.core.file.tmp.TmpFileResolverType;
import com.idega.core.file.tmp.TmpFilesModifyStrategy;
import com.idega.core.test.base.IdegaBaseTest;
import com.idega.util.CoreConstants;
import com.idega.util.xml.XPathUtil;
import com.idega.util.xml.XmlUtil;


/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/08/07 09:33:32 $ by $Author: civilis $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class XFormTmpFileResolverImplTest extends IdegaBaseTest {

	@Autowired
	@TmpFileResolverType("xformVariables")
	private TmpFileResolver tmpFileResolver;
	@Autowired
	@TmpFileResolverType(ChibaConstants.XFORM_REPOSITORY)
	private TmpFilesModifyStrategy tmpFilesModifyStrategy;
	
	@Test
	public void testResolveAllFilesUris() throws Exception {
		
		ClassPathResource cpr = new ClassPathResource("submissionInstance2AttachmentsStart.xml", getClass());
		URI startSubmissionUri = cpr.getURI();
		InputStream is = cpr.getInputStream();
		cpr = new ClassPathResource("XFormTmpFileResolverImplTest-context.xml", getClass());
		URI f2uri = cpr.getURI();
		final Document submissionInstance;
		
		try {
			submissionInstance = XmlUtil.getDocumentBuilder().parse(is);
			
		} finally {
			
			if(is != null)
				is.close();
		}
		
		XPathUtil entryFilesXPath = new XPathUtil(".//entry[@filename]");
		NodeList filesNodes = entryFilesXPath.getNodeset(submissionInstance);
//		expecting 2 items
		filesNodes.item(0).setTextContent(startSubmissionUri.toString());
		filesNodes.item(1).setTextContent(f2uri.toString());
		
		tmpFileResolver.replaceAllFiles(submissionInstance, tmpFilesModifyStrategy);
		
		filesNodes = entryFilesXPath.getNodeset(submissionInstance);
//		expecting 2 items
		assertEquals(CoreConstants.REPOSITORY + ":/files/forms/saved/uploadedFiles/upload/submissionInstance2AttachmentsStart.xml", filesNodes.item(0).getTextContent());
		assertEquals(CoreConstants.REPOSITORY + ":/files/forms/saved/uploadedFiles/upload/XFormTmpFileResolverImplTest-context.xml", filesNodes.item(1).getTextContent());
	}
}