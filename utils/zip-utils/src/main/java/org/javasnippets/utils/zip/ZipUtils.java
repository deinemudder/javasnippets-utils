package org.javasnippets.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility-Class for Zip-File-Operations.
 * 
 * @author ckroeger
 */
public class ZipUtils {

	public static void zipFolder(String destFile, String sourceFolder,
			String[] foldersToIgnore) {
		List<String> fileList = new ArrayList<String>();
		File node = new File(sourceFolder);
		Set<String> ignoredFolders = new HashSet<String>(
				Arrays.asList(foldersToIgnore));
		generateFileList(node, node, fileList, ignoredFolders);
		zipFileListEntries(destFile, fileList, node, null);
	}

	private static void zipFileListEntries(String zipFile,
			List<String> fileList, File node, String rootFolder) {
		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);

			System.out.println("Output to Zip : " + zipFile);
			FileInputStream in = null;

			for (String file : fileList) {
				System.out.println("File Added : " + file);
				String zipEntryPath = rootFolder != null ? rootFolder
						+ File.separator + file : file;
				ZipEntry ze = new ZipEntry(zipEntryPath);
				zos.putNextEntry(ze);
				try {
					in = new FileInputStream(node.getAbsolutePath()
							+ File.separator + file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				} finally {
					in.close();
				}
			}

			zos.closeEntry();
			System.out.println("Folder successfully compressed");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void generateFileList(File node, File rootNode,
			List<String> fileList, Set<String> ignoredFolders) {
		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node, rootNode));

		}

		if (node.isDirectory()) {
			boolean processFolder = !ignoredFolders.contains(node.getName());
			if (processFolder) {
				String[] subNote = node.list();
				for (String filename : subNote) {
					generateFileList(new File(node, filename), rootNode,
							fileList, ignoredFolders);
				}
			}
		}
	}

	private static String generateZipEntry(File node, File rootNode) {
		String file = node.toString();
		return file.substring(rootNode.toString().length() + 1, file.length());
	}

	public static String[] readJarManifestEntries(String[] manifestKeys, File jarFile)
			throws IOException {
		if (manifestKeys == null) {
			return null;
		}
		JarFile jar = null;
		String[] ret = new String[manifestKeys.length];
		try {
			jar = new JarFile(jarFile);
			Manifest manifest = jar.getManifest();
			Attributes attributes = manifest.getMainAttributes();
			for (int i = 0; i < ret.length; i++) {
				ret[i] = attributes.getValue(manifestKeys[i]);
			}
		} finally {
			if (jar != null)
				jar.close();
		}
		return ret;
	}
}
