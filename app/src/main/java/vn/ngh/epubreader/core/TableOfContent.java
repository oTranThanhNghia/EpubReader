package vn.ngh.epubreader.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Table of Content
 */
public class TableOfContent implements Serializable {

	/**
	 * https://stackoverflow.com/a/36007392
	 */
	private static final long serialVersionUID = -3060848330941680098L;

	private String uid;
	private String depth;
	private ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
	private ArrayList<String> imagePathList = new ArrayList<String>();
	private ArrayList<String> audioPathList = new ArrayList<String>();

	public ArrayList<String> getImagePathList() {
		return imagePathList;
	}

	public void addImagePath(String imagePath) {
		this.imagePathList.add(imagePath);
	}

	public ArrayList<String> getAudioPathList() {
		return audioPathList;
	}

	public String getAudioPathForChapper(int index) {
		if (audioPathList != null && audioPathList.size() > index) {
			return audioPathList.get(index);
		}
		return null;
	}

	public void addAudioPath(String audioPath) {
		this.audioPathList.add(audioPath);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public void addChapter(final Chapter chapter) {
		this.chapterList.add(chapter);
	}

	public Chapter getChapter(final int num) {
		int chapNum = num;
		if (chapNum >= chapterList.size()) {
			chapNum = chapterList.size() - 1;
		}
		if (chapNum <= 0)
			chapNum = 0;

		try {
			return chapterList.get(chapNum);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Chapter> getChapterList() {
		return chapterList;
	}

	public String[] getTitleArray() {
		int totalSize = getTotalSize();
		String[] titles = new String[totalSize];
		for (int i = 0; i < totalSize; i++) {
			Chapter chap = chapterList.get(i);
			titles[i] = chap.getTitle();
		}
		return titles;
	}

	public String[] getUrlArray() {
		int totalSize = getTotalSize();
		String[] urls = new String[totalSize];
		for (int i = 0; i < totalSize; i++) {
			Chapter chap = chapterList.get(i);
			urls[i] = chap.getUrl();
		}
		return urls;
	}

	public int getTotalSize() {
		return chapterList.size();
	}

	/**
	 * Chapter
	 */
	@SuppressWarnings("serial")
	public static class Chapter implements Serializable {

		private String title;
		private String url;
		private String anchor;
		private int order;
		private int startPage = 0;
		private int endPage = 0;

		public void setTitle(String title) {
			this.title = title;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getTitle() {
			return title;
		}

		public String getUrl() {
			return url;
		}

		public String getAnchor() {
			return anchor;
		}

		public void setAnchor(String anchor) {
			this.anchor = anchor;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public void setStartPage(int pageNo) {
			this.startPage = pageNo;
		}

		public int getStartPage() {
			return this.startPage;
		}

		public void setEndPage(int pageNo) {
			this.endPage = pageNo;
		}

		public int getEndPage() {
			return this.endPage;
		}
	}

	@Override
	public String toString() {
		return "TableOfContent [uid=" + uid + ", depth=" + depth
				+ ", chapterList=" + chapterList + "]";
	}

}
