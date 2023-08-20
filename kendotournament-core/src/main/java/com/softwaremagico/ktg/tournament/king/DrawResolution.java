package com.softwaremagico.ktg.tournament.king;

public enum DrawResolution {

	OLDEST_ELIMINATED("oldest"),

	BOTH_ELIMINATED("both"),

	NEWEST_ELIMINATED("newest");

	private String tag;

	private DrawResolution(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public static DrawResolution getFromTag(String tag) {
		for (DrawResolution drawResolution : DrawResolution.values()) {
			if (drawResolution.getTag().equals(tag)) {
				return drawResolution;
			}
		}
		return null;
	}
}
