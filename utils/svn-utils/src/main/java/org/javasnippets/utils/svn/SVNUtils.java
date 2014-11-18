package org.javasnippets.utils.svn;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

/**
 * SVNUtility-Class for SVN-Operations
 * @author ckroeger
 */
public class SVNUtils {

	/**
	 * Does a SVN-checkout to workingCopyDirectoryPath from the given svn-url.
	 * 
	 * @param workingCopyDirectory
	 *            {@link File} of local directory for checkout
	 * @param url
	 *            svn-repository-url
	 * @param revision
	 *            revision
	 * @throws SVNException
	 *             SVNException occurs on SVN-communication-failures
	 */
	public static void checkout(File workingCopyDirectory, SVNURL url,
			SVNRevision revision) throws SVNException {
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		try {
			final SvnCheckout checkout = svnOperationFactory.createCheckout();
			checkout.setSingleTarget(SvnTarget.fromFile(workingCopyDirectory));
			checkout.setSource(SvnTarget.fromURL(url));
			if (revision != null) {
				checkout.setRevision(revision);
			}
			checkout.run();
		} finally {
			svnOperationFactory.dispose();
		}
	}

	/**
	 * Does a SVN-checkout to workingCopyDirectoryPath from the given svn-url.
	 * 
	 * @param workingCopyDirectoryPath
	 *            local directory for checkout (!EMPTY)
	 * @param url
	 *            svn-repository-url (!EMPTY)
	 * @param revision
	 *            desired revision or -1 for HEAD-revision
	 * @throws SVNException
	 *             occurs on SVN-communication-failures
	 */
	public static void checkout(String workingCopyDirectoryPath, String url,
			int revision) throws SVNException {
		{ // sanity-checks
			validateNotEmpty(workingCopyDirectoryPath);
			validateNotEmpty(url);
		}
		File checkoutDir = checkAndDeleteIfExists(workingCopyDirectoryPath);
		SVNURL svnUrl = SVNURL.parseURIEncoded(url);
		SVNRevision svnRevision = SVNRevision.HEAD;
		if (revision > 1) {
			svnRevision = SVNRevision.create(revision);
		}
		checkout(checkoutDir, svnUrl, svnRevision);
	}

	/**
	 * Checks if given dir-path exists, if exists it will be deleted.
	 * 
	 * @param workingCopyDirectoryPath
	 *            Path to check
	 * @return {@link File} of deleted dir.
	 * @throws IllegalArgumentException
	 *             If dir not exists or not accessable.
	 */
	private static File checkAndDeleteIfExists(String workingCopyDirectoryPath) {
		File checkoutDir = new File(workingCopyDirectoryPath);
		String absolutePath = checkoutDir.getAbsolutePath();
		if (checkoutDir.exists()) {
			validateTrue(checkoutDir.isDirectory(), String.format(
					"given path is no directory: %s", absolutePath));
			validateTrue(checkoutDir.canWrite(),
					String.format("can not write in path: %s", absolutePath));
			validateTrue(deleteDirectory(checkoutDir),
					String.format("can not delete in path: %s", absolutePath));
		}
		return checkoutDir;
	}

	/**
	 * Validate the given boolean.
	 * 
	 * @param bool
	 *            boolean to check
	 * @param msg
	 *            Message when validation fails
	 * 
	 * @throws IllegalArgumentException
	 *             when boolean is false
	 */
	private static void validateTrue(boolean bool, String msg) {
		if (!bool) {
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Validates a String not to be empty.
	 * 
	 * @param anyString
	 *            String to validate
	 * @throws IllegalArgumentException
	 *             when String is null or empty
	 */
	private static void validateNotEmpty(String anyString) {
		if (anyString == null || anyString.isEmpty()) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Deletes a given directory.
	 * 
	 * @param path
	 *            {@link File} of a directory-
	 * @return true if succeeds
	 */
	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}
