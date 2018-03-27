package vn.ngh.epubreader.core;

import java.util.ArrayList;

public class EpubBook {
    public ArrayList<Manifest> manifestList = new ArrayList<Manifest>();
    public ArrayList<String> spineList = new ArrayList<String>();
    public String coverPath = null;
    public String tocPath = null;

    public String path = null;

    public int totalPage;
    public String title;
    public String author;
    public String subject;
    public String publisher;
    public String id;


    public static class Manifest {
        public String _id;
        public String href;
        public String mediaType;

        public Manifest() {
        }

        public Manifest(String id, String href, String mediaType) {
            this._id = id;
            this.href = href;
            this.mediaType = mediaType;
        }
    }
}
