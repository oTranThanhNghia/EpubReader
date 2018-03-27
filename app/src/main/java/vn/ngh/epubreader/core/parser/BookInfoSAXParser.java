package vn.ngh.epubreader.core.parser;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import vn.ngh.epubreader.core.EpubBook;


public class BookInfoSAXParser extends DefaultHandler {
    private EpubBook bookInfo = new EpubBook();
    private String mStartTag;
    private boolean isFoundTOC = false;
    private String cover_id = null;
    private boolean isTitle = false;
    private boolean isCreator = false;
    private boolean isPublisher = false;
    private boolean isSubject = false;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if ("dc:title".equalsIgnoreCase(qName)) {
            isTitle = true;
        }
        if ("dc:creator".equalsIgnoreCase(qName)) {
            isCreator = true;
        }
        if ("dc:publisher".equalsIgnoreCase(qName)) {
            isPublisher = true;
        }
        if ("dc:subject".equalsIgnoreCase(qName)) {
            isSubject = true;
        }

        if ("meta".equalsIgnoreCase(localName)) {
            if (attributes.getValue("name").equals("cover")) {
                cover_id = attributes.getValue("content");
            }
        }

        mStartTag = localName;
        if ("item".equalsIgnoreCase(localName)) {
            String id = attributes.getValue("id");
            if (id != null && id.equalsIgnoreCase(cover_id)) {
                bookInfo.coverPath = attributes.getValue("href");
            }
            String href = attributes.getValue("href");
            String mediaType = attributes.getValue("media-type");

            bookInfo.manifestList
                    .add(new EpubBook.Manifest(id, href, mediaType));

            // Get toc.ncx path
            if (!isFoundTOC && id.equalsIgnoreCase("ncx")) {
                bookInfo.tocPath = href;
                isFoundTOC = true;
            }
        } else if ("itemref".equalsIgnoreCase(localName)) {
            String idref = attributes.getValue("idref");
            bookInfo.spineList.add(idref);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        if (isTitle) {
            bookInfo.title = new String(ch, start, length);
            isTitle = false;
        }
        if (isCreator) {
            bookInfo.author = new String(ch, start, length);
            isCreator = false;
        }
        if (isSubject) {
            bookInfo.subject = new String(ch, start, length);
            isSubject = false;
        }
        if (isPublisher) {
            bookInfo.publisher = new String(ch, start, length);
            isPublisher = false;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (mStartTag.equalsIgnoreCase(localName)) {
            mStartTag = "";
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * Returns BookInfo
     *
     * @param opfPath
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public EpubBook getBookInfo(String opfPath)
            throws ParserConfigurationException, SAXException, IOException, FileNotFoundException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(new InputSource(new FileReader(new File(opfPath))));
        return bookInfo;
    }

}
