package yf.project;
//package com.deb.mapreduce;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParserReview {

	private Document doc;

	public XMLParserReview(String fXmlFile) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(fXmlFile));

		doc = builder.parse(is);
	}

	public String getHashR() {
		NodeList nList = doc.getElementsByTagName("hash");
		return nList.item(0).getTextContent();
	}

	public List<String> getReviews() {
		List<String> returnStringList = new ArrayList<String>();
		NodeList nList = doc.getElementsByTagName("review");
		for (int index = 0; index < nList.getLength(); index++) {
			returnStringList.add(nList.item(index).getTextContent().toLowerCase());
		}
		return returnStringList;
	}

}
