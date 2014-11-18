package org.javasnippets.utils.svn;

import java.io.File;

import org.javasnippets.utils.svn.SVNUtils;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNURL;

public class SVNUtilsTest {

	@Test(enabled=false)
	public void testCheckout() throws Exception {
		String urlStr = "http://host:80/svn/repo1/trunk/dev/";
		SVNURL svnUrl = SVNURL.parseURIEncoded(urlStr);
		File workingCopyDirectory = new File("c:/tmp/test-checkout/repo1");
		SVNUtils.deleteDirectory(workingCopyDirectory);
		SVNUtils.checkout(workingCopyDirectory, svnUrl, null);
	}
	
	@Test(enabled=false)
	public void testCheckout2() throws Exception {
		String urlStr = "http://host:80/svn/repo1/trunk/dev/";
		String checkoutDir = "c:/tmp/test-checkout/repro1-167";
		SVNUtils.checkout(checkoutDir, urlStr, 167);
	}

}
