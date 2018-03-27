package vn.ngh.epubreader.core;


import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;

import vn.ngh.epubreader.core.parser.BookInfoSAXParser;
import vn.ngh.epubreader.core.parser.ContainerSAXParser;
import vn.ngh.epubreader.core.parser.TocSAXParser;
import vn.ngh.epubreader.utils.ZipUtil;


public class EpubCommon {
    private String mEpubPath;
    private String mUnzipPath;
    private String mContentPath;
    private String mBasePath;
    private String mBaseUrl;
    private String mTocPath;

    /**
     * Constructor
     *
     * @param bookPath
     */
    public EpubCommon(String bookPath, String contentPath,
                      boolean deleteOld) {
        mEpubPath = bookPath;
        // 1. get unzip path
        String vefDir = mEpubPath.substring(0,
                mEpubPath.lastIndexOf("/") + 1);
        String vefName = mEpubPath.substring(
                mEpubPath.lastIndexOf("/") + 1, mEpubPath.lastIndexOf("."));
        mUnzipPath = vefDir + "" + vefName;

        // unzip if not exists
        File unzipDir = new File(mUnzipPath);
        if (!unzipDir.exists() || deleteOld) {
            try {
                // Unzip book files
                File f = new File(bookPath);
                if (f.exists()) {
                    ZipUtil.unzipAll(f, unzipDir);
                } else {
                    throw new FileNotFoundException("not found :" + bookPath);
                }
                // Get content path
                mContentPath = parseContentPath(mUnzipPath);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        // 2. get content path if not parse file
        if (mContentPath == null || mContentPath.equalsIgnoreCase("")) {
            if (contentPath != null) {
                mContentPath = contentPath;
            } else {
                mContentPath = parseContentPath(mUnzipPath);
            }
        }

        // 3. get base url
        mBaseUrl = mBasePath = mContentPath.substring(0,
                mContentPath.lastIndexOf("/"));

    }

    /**
     * Parses container.xml to get content path
     *
     * @param rootDir
     */
    public String parseContentPath(String rootDir) {
        String contentPath = null;
        try {
            String containerPath = mUnzipPath + "/META-INF/container.xml";
            ContainerSAXParser csp = new ContainerSAXParser();
            contentPath = rootDir + "/" + csp.getRootFilePath(containerPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return contentPath;
    }

    /**
     * Parses content.opf
     *
     * @return
     * @throws Exception
     */
    public EpubBook parseBookInfo() throws Exception {
        EpubBook bookInfo = new EpubBook();

        BookInfoSAXParser bookInfoParser = new BookInfoSAXParser();
        try {
            bookInfo = bookInfoParser.getBookInfo(mContentPath);

            bookInfo.path = mEpubPath;
            mTocPath = mBasePath + "/" + bookInfo.tocPath;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookInfo;
    }

    /**
     * Parses toc.ncx
     *
     * @return
     */
    public TableOfContent parseTableOfContent() {
        TableOfContent toc = new TableOfContent();

        TocSAXParser parser = new TocSAXParser(this);
        try {
            toc = parser.getTableOfContents(mTocPath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toc;
    }

    /**
     * Get Base URL
     *
     * @return
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public String getBasePath() {
        return mBasePath;
    }

    public void setBasePath(String basePath) {
        mBasePath = basePath;
    }

    /**
     * Get Content Path
     *
     * @return
     */
    public String getContentPath() {
        return mContentPath;
    }

    /**
     * Gets ePub Unzip Path
     *
     * @return
     */
    public String getUnzipPath() {
        return mUnzipPath;
    }


    public String getContentChapter(String chapterPath) {
        try {
            File file = new File(chapterPath);
            String resultStr = IOUtils.toString(new FileInputStream(file),
                    Charset.defaultCharset());
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
