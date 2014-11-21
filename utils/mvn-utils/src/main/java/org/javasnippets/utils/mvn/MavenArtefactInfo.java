package org.javasnippets.utils.mvn;

import java.util.jar.Attributes;

/**
 * Metadata-Object for Maven-Artefacts.
 *
 * @author ckroeger
 */
public class MavenArtefactInfo {

	private static final String IMPLEMENTATION = "Implementation-";
	private static final String BUILD = "Build-";

	private int svnRevision = -1;
	private String artefactId = null;
	private String groupId = null;
	private String version = null;
	private String buildTimestamp = null;
	private String buildVersion = null;
	private String vendor = null;

	/**
	 * Creates a new Object with Maven-Metadata based on given manifest-
	 * {@link Attributes}.
	 * 
	 * @param manifestAttributes
	 *            manifest-{@link Attributes} (!NULL)
	 */
	public MavenArtefactInfo(Attributes manifestAttributes) {
		if (manifestAttributes == null) {
			throw new IllegalArgumentException();
		}
		this.artefactId = manifestAttributes.getValue(IMPLEMENTATION + "Title");
		this.groupId = manifestAttributes
				.getValue(IMPLEMENTATION + "Vendor-Id");
		this.version = manifestAttributes.getValue(IMPLEMENTATION + "Version");
		this.vendor = manifestAttributes.getValue(IMPLEMENTATION + "Vendor");

		this.buildTimestamp = manifestAttributes.getValue(BUILD + "Timestamp");
		this.buildVersion = manifestAttributes.getValue(BUILD + "Version");

		String value = manifestAttributes.getValue("svn-revision");
		if (value != null && !"".equals(value)) {
			this.svnRevision = Integer.parseInt(value);
		}
	}

	/**
	 * Determines the svn-revision from manifest-file.
	 *
	 * @return svn-revision from manifest-file.
	 */
	public int getSvnRevision() {
		return svnRevision;
	}

	/**
	 * Determines the maven artefact-id from manifest-file.
	 *
	 * @return maven artefact-id from manifest-file
	 */
	public String getArtefactId() {
		return artefactId;
	}

	/**
	 * Determines the maven group-id from manifest-file.
	 *
	 * @return maven group-id from manifest-file
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Determines the maven artefact-version from manifest-file.
	 *
	 * @return maven artefact-version from manifest-file
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Determines the build-timestamp from manifest-file.
	 *
	 * @return build-timestamp from manifest-file
	 */
	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	/**
	 * Determines the build-version from manifest-file.
	 *
	 * @return build-version from manifest-file
	 */
	public String getBuildVersion() {
		return buildVersion;
	}

	/**
	 * Determines the vendor from manifest-file.
	 *
	 * @return vendor from manifest-file
	 */
	public String getVendor() {
		return vendor;
	}

	@Override
	public String toString() {
		return "MavenArtefactInfo [svnRevision=" + svnRevision
				+ ", artefactId=" + artefactId + ", groupId=" + groupId
				+ ", version=" + version + ", buildTimestamp=" + buildTimestamp
				+ ", buildVersion=" + buildVersion + ", vendor=" + vendor + "]";
	}

}
