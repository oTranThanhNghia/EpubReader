/**
 * Copyright (C) 2009 Samsung Electronics Co., Ltd. All rights reserved.
 * <p>
 * Mobile Communication Division,
 * Digital Media & Communications Business, Samsung Electronics Co., Ltd.
 * <p>
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 * <p>
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */
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

import vn.ngh.epubreader.core.EpubCommon;
import vn.ngh.epubreader.core.TableOfContent;


public class TocSAXParser extends DefaultHandler {
    private int order = 0;
    private String mStartTag;
    private String prevUrl;

    private TableOfContent toc = new TableOfContent();
    private TableOfContent.Chapter chapter = new TableOfContent.Chapter();

    private EpubCommon engine;

    public TocSAXParser(EpubCommon en) {
        this.engine = en;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        mStartTag = localName;

        if ("content".equalsIgnoreCase(localName)) {
            String src = attributes.getValue("src");
            int index = src.lastIndexOf("#");
            int startPage, endPage;
            String url = (index > 0) ? src.substring(0, index) : src;
            String anchor = (index > 0) ? src.substring(index + 1) : "";
            try {
                startPage = Integer.valueOf(attributes.getValue("startPage"));
            } catch (Exception e) {
                startPage = 0;
            }
            try {
                endPage = Integer.valueOf(attributes.getValue("endPage"));
            } catch (Exception e) {
                endPage = 0;
            }

            if (!url.equals(prevUrl)) {

                chapter.setOrder(++order);
                chapter.setUrl(url);
                chapter.setAnchor(anchor);
                chapter.setStartPage(startPage);
                chapter.setEndPage(endPage);

                // add this navPoint to TOC
                toc.addChapter(chapter);
                chapter = new TableOfContent.Chapter();
            }

            prevUrl = url;

        } else if ("meta".equalsIgnoreCase(localName)) {
            String name = attributes.getValue("name");
            if (name.equalsIgnoreCase("dtb:uid")) {
                toc.setUid(attributes.getValue("content"));
            } else if (name.equalsIgnoreCase("dtb:depth")) {
                toc.setDepth(attributes.getValue("content"));
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);

        StringBuffer sb = new StringBuffer();
        for (int i = start; i < (start + length); i++) {
            sb.append(ch[i]);
        }
        String value = sb.toString();

        if ("text".equalsIgnoreCase(mStartTag)) {
            chapter.setTitle(value);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);

        if (mStartTag.equalsIgnoreCase(localName)) {
            mStartTag = "";
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * Returns TOC
     *
     * @param tocPath
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public TableOfContent getTableOfContents(String tocPath)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(new InputSource(new FileReader(new File(tocPath))));
        return toc;
    }

}
