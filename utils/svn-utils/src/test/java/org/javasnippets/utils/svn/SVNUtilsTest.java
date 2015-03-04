package org.javasnippets.utils.svn;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.javasnippets.utils.svn.SVNUtils;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;

public class SVNUtilsTest {

	@Test(enabled = false)
	public void testCheckout() throws Exception {
		String urlStr = "http://host:80/svn/repo1/trunk/dev/";
		SVNURL svnUrl = SVNURL.parseURIEncoded(urlStr);
		File workingCopyDirectory = new File("c:/tmp/test-checkout/repo1");
		SVNUtils.deleteDirectory(workingCopyDirectory);
		SVNUtils.checkout(workingCopyDirectory, svnUrl, null);
	}

	@Test(enabled = false)
	public void testCheckout2() throws Exception {
		String urlStr = "http://host:80/svn/repo1/trunk/dev/";
		String checkoutDir = "c:/tmp/test-checkout/repro1-167";
		SVNUtils.checkout(checkoutDir, urlStr, 167);
	}

	@Test(enabled = false)
	public void testLog() throws Exception {
		String urlStr = "http://host:80/svn/repo1/trunk/dev/";
		SVNURL svnUrl = SVNURL.parseURIEncoded(urlStr);
		Date date1 = getDate(2015, 1, 7);
		Date date2 = new Date();
		List<SVNLogEntry> svnLog = SVNUtils.svnLog(svnUrl, date1, date2);
		for (SVNLogEntry svnLogEntry : svnLog) {
			SVNProperties revisionProperties = svnLogEntry
					.getRevisionProperties();
			long revision = svnLogEntry.getRevision();
			String logMessage = revisionProperties
					.getStringValue(SVNRevisionProperty.LOG);
			logMessage=StringUtils.substringBefore(logMessage, "\n");
			String author = revisionProperties
					.getStringValue(SVNRevisionProperty.AUTHOR);
			
			Date date = svnLogEntry.getDate();
			String dateStr = SimpleDateFormat.getInstance().format(date);
			String msg = String.format("%d|%s|%s|%s", revision, dateStr, author,
					logMessage);
			System.out.println(msg);
		}
	}

	private Date getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		Date date = cal.getTime();
		return date;
	}

}
