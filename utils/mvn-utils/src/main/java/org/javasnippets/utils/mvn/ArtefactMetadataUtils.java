package org.javasnippets.utils.mvn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Util to read Manifest-Metadata.
 * 
 * @author ckroeger
 */
public class ArtefactMetadataUtils {

	/**
	 * Returns manifest {@link Attributes} of a {@link InputStream} of a single
	 * Jar-File.
	 * 
	 * @param source
	 *            {@link InputStream} (!NULL)
	 * @return manifest-{@link Attributes} or null if no manifest exists
	 * @throws IOException
	 */
	public static Attributes getManifestAttributes(InputStream source)
			throws IOException {
		@SuppressWarnings("resource")
		JarInputStream jarStream = new JarInputStream(source);
		Manifest mf = jarStream.getManifest();
		if (mf == null) {
			return null;
		}
		Attributes mainAttributes = mf.getMainAttributes();
		return mainAttributes;
	}

	/**
	 * Creates a {@link List} of {@link MavenArtefactInfo}-Object from a
	 * web-archive.
	 * 
	 * @param archive
	 *            a web-archive file (!NULL)
	 * @return a list of {@link MavenArtefactInfo}-Objects (!NULL)
	 * @throws ZipException
	 * @throws IOException
	 */
	public static List<MavenArtefactInfo> listMavenArtefactInfos(File archive)
			throws ZipException, IOException {
		List<MavenArtefactInfo> listOfArtefactInfos = new ArrayList<MavenArtefactInfo>();
		ZipFile zipFile = new ZipFile(archive);
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				String name = zipEntry.getName();
				// System.out.println(name);
				if (!name.endsWith(".jar")) {
					continue;
				}
				InputStream inputStream = zipFile.getInputStream(zipEntry);
				Attributes manifestAttributes = getManifestAttributes(inputStream);
				if (manifestAttributes == null) {
					continue;
				}
				MavenArtefactInfo mavenArtefactInfo = new MavenArtefactInfo(
						manifestAttributes);
				listOfArtefactInfos.add(mavenArtefactInfo);
				closeQuietly(inputStream);
			}
		} finally {
			zipFile.close();
		}
		return listOfArtefactInfos;
	}

	/**
	 * Closes the stream silently.
	 * 
	 * @param streamToClose
	 *            {@link InputStream} to close (NULLABLE)
	 */
	private static void closeQuietly(InputStream streamToClose) {
		if (streamToClose == null) {
			return;
		}
		try {
			streamToClose.close();
		} catch (IOException e) {
			/* ignore */
		}
	}
}
