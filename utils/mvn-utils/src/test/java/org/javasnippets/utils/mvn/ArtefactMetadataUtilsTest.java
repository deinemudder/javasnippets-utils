package org.javasnippets.utils.mvn;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.jar.Attributes;
import java.util.zip.ZipException;

import org.testng.annotations.Test;

public class ArtefactMetadataUtilsTest {

	String FILE = "test1.jar";
	String WARFILE = "test.war";

	@Test
	public void testGetManifestEntries() throws Exception {
		String path = getPathOfResource(FILE);
		FileInputStream fis = new FileInputStream(path);
		Attributes entries = ArtefactMetadataUtils.getManifestAttributes(fis);
		String value = entries.getValue("svn-revision");
		System.out.println(value);
	}

	@Test
	public void testListMavenArtefactInfos() throws ZipException, IOException {
		String path = getPathOfResource(WARFILE);
		File archive = new File(path);
		List<MavenArtefactInfo> listMavenArtefactInfos = ArtefactMetadataUtils
				.listMavenArtefactInfos(archive);
		for (MavenArtefactInfo mavenArtefactInfo : listMavenArtefactInfos) {
			boolean show = check(mavenArtefactInfo);
			if (show) {
				System.out.println(mavenArtefactInfo.toString());
				System.out
						.println("******************************************************************");
			}
		}
	}

	private String getPathOfResource(String resourcePath) {
		URL resource = ArtefactMetadataUtilsTest.class.getResource("/"+resourcePath);
		String path = resource.getFile();
		return path;
	}

	private boolean check(MavenArtefactInfo mavenArtefactInfo) {
		String groupId = mavenArtefactInfo.getGroupId();
		if(groupId==null){
			return false;
		}
		boolean show = groupId.startsWith("org.");
		return show;
	}
}
