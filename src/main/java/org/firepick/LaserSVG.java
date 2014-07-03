package org.firepick;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LaserSVG {
  static final String outputEncoding = "UTF-8";
  HashSet<String> lineMap = new HashSet<String>();

  public static void help() {
    System.out.println("-----------------------------------------");
    System.out.println("laser-svg: Laser Cutting SVG File Cleaner");
    System.out.println("-----------------------------------------");
    System.out.println();
    System.out.println("USAGE");
    System.out.println("  java -jar target/laser-svg*jar messy.svg > clean.svg");
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      help();
      return;
    }

    String filename = args[0];

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(new File(filename));
    LaserSVG visitor = new LaserSVG();
    visitor.scanElement(doc.getDocumentElement());

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(doc),
      new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
  }

  public boolean scanLine(Element line) {
    String x1 = line.getAttribute("x1");
    String y1 = line.getAttribute("y1");
    String x2 = line.getAttribute("x2");
    String y2 = line.getAttribute("y2");
    String key;
    int cmp1 = x1.compareTo(x2);
    if (cmp1 < 0) {
      key = x1 + "," + y1 + ":" + x2 + "," + y2;
    } else if (cmp1 == 0) {
      int cmp2 = y1.compareTo(y2);
      if (cmp2 < 0) {
        key = x1 + "," + y1 + ":" + x2 + "," + y2;
      } else if (cmp2 == 0) {
        return true; // degenerate line is a point
      } else {
        key = x2 + "," + y2 + ":" + x1 + "," + y1;
      }
    } else {
      key = x2 + "," + y2 + ":" + x1 + "," + y1;
    }

    //System.out.println(key);
    boolean duplicate = lineMap.contains(key);
    if (!duplicate) {
      lineMap.add(key);
    }
    return duplicate;
  }

  public boolean scanElement(Element elt) {
    if ("line".equals(elt.getTagName())) {
      if (scanLine(elt)) {
        return true;
      }
    }
    List<Node> dupList = new ArrayList<Node>();
    for (Node kid = elt.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
      if (kid instanceof Element) {
        if (scanElement((Element) kid)) {
          dupList.add(kid);
        }
      }
    }
    for (Node kid : dupList) {
      elt.removeChild(kid);
    }
    return false;
  }
}

