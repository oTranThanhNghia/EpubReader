package vn.ngh.epubreader.core.parser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ContainerSAXParser extends DefaultHandler {
    private String rootFilePath;

    private String mStartTag;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        mStartTag = localName;

        if ("rootfile".equalsIgnoreCase(localName)) {
            String path = attributes.getValue("full-path");
            rootFilePath = path;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (mStartTag.equalsIgnoreCase(localName)) {
            mStartTag = "";
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * Returns Root File Path
     *
     * @param containerPath
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String getRootFilePath(String containerPath) throws ParserConfigurationException, SAXException,
            IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(new InputSource(new FileReader(new File(containerPath))));
        return rootFilePath;
    }
}
