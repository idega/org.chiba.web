package com.idega.chiba.process.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/09/23 06:58:25 $ by $Author: civilis $
 */
public class DateConverter implements DataConverter {
	
	private SimpleDateFormat dateFormatter;
	private static final String dateFormat = "yyyy-MM-dd";
	
	
	public DateConverter() {
		dateFormatter = new SimpleDateFormat(dateFormat);
	}

	public Object convert(Element o) {

		String dateStr = o.getTextContent();
		
		try {
			return dateFormatter.parse(dateStr);
			
		} catch (ParseException e) {
			throw new RuntimeException("Exception while parsing date string ("+dateStr+") for format: "+dateFormat, e);
		}
	}
	public Element revert(Object o, Element e) {

		if(!(o instanceof Date))
			throw new IllegalArgumentException("Wrong class object provided for DateConverter: "+o.getClass().getName()+". Should be java.util.Date");
		
		NodeList childNodes = e.getChildNodes();
		
		List<Node> childs2Remove = new ArrayList<Node>();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			
			Node child = childNodes.item(i);
			
			if(child != null && (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.ELEMENT_NODE))
				childs2Remove.add(child);
		}
		
		for (Node node : childs2Remove)
			e.removeChild(node);
		
		Node txtNode = e.getOwnerDocument().createTextNode(dateFormatter.format((Date)o));
		e.appendChild(txtNode);
		
		return e;
	}
}