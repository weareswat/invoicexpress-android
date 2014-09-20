package pt.rupeal.invoicexpress.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;

import android.content.Context;

public class InvoiceXpressParser {
	
	private Context context;
	
	public InvoiceXpressParser(Context context) {
		this.context = context;
	}
	
	public Document getDomElement(String xml) throws InvoiceXpressException {
        
		try {
 
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(inputStream);
			
			return doc;
 
        } catch (ParserConfigurationException e) {
        	throw new InvoiceXpressException(context, R.string.error_parser_xml_unexpected, InvoiceXpressErrorType.ERROR);
        } catch (SAXException e) {
    		throw new InvoiceXpressException(context, R.string.error_parser_xml_unexpected, InvoiceXpressErrorType.ERROR);
        } catch (IOException e) {
        	throw new InvoiceXpressException(context, R.string.error_parser_xml_unexpected, InvoiceXpressErrorType.ERROR);
        }
	}
	
	public String getValue(Element elem, String str) {
	    NodeList n = elem.getElementsByTagName(str);
	    
	    for (int i = 0; i < n.getLength(); i++) {
	    	if(elem.getNodeName().equals(n.item(i).getParentNode().getNodeName())){
	    		return this.getElementValue(n.item(i));
	    	}
		}
	    
	    return "";
	}
	 
	private final String getElementValue(Node node) {
         Node child = null;
         
         if( node != null){
             if (node.hasChildNodes()){
                 for(child = node.getFirstChild(); child != null; child = child.getNextSibling()){
                     if(child.getNodeType() == Node.TEXT_NODE){
                         return child.getNodeValue();
                     }
                 }
             }
         }
         
         return "";
	}
	
	public Node getNode(Element elem, String nodeName) {
		Node child = null;
		
		if( elem != null){
            if (elem.hasChildNodes()){
                for(child = elem.getFirstChild(); child != null; child = child.getNextSibling()){
                    if(child.getNodeName().equals(nodeName)){
                        return child;
                    }
                }
            }
        }
		
		return null;
	}
	
	public List<Node> getChildNodes(Element elem, String nodeName, int index) {
		List<Node> newNodeList = new ArrayList<Node>();
		NodeList nodeList = elem.getElementsByTagName(nodeName).item(index).getChildNodes();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(nodeList.item(i) instanceof Element) {
				newNodeList.add(node);
			}
		}
		
		return newNodeList;
	}

}
