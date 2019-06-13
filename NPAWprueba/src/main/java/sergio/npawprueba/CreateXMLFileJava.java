/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sergio.npawprueba;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author sergio
 */
public class CreateXMLFileJava {

    public static final String xmlFilePath = "/home/sergio/NetBeansProjects/NPAWprueba/NPAWprueba/src/xml/response.xml";

    public static void generateXMLResponse(String host, String ping) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("q");
            document.appendChild(root);

            // set an attribute to staff element
            Attr cluster = document.createAttribute("h");
            cluster.setValue(host);
            root.setAttributeNode(cluster);

            Attr pingTime = document.createAttribute("pt");
            pingTime.setValue(ping);
            root.setAttributeNode(pingTime);

            Attr viewCode = document.createAttribute("c");
            viewCode.setValue("7xnj85f06yqswc5x");
            root.setAttributeNode(viewCode);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(CreateXMLFileJava.class.getName()).log(Level.SEVERE, null, ex);
            }
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging
            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (TransformerException ex) {
            Logger.getLogger(CreateXMLFileJava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CreateXMLFileJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
