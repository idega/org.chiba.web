package com.idega.chiba.process.converters;

import java.util.ArrayList;
import java.util.List;

import org.chiba.xml.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/09/23 06:58:25 $ by $Author: civilis $
 */
public class CollectionConverter implements DataConverter {

	private final String listItemElementName = "item";
	
	public Object convert(Element o) {

		@SuppressWarnings("unchecked")
		List<Element> childElements = DOMUtil.getChildElements(o);
		
		List<String> values = new ArrayList<String>();
		
		if(childElements == null || childElements.isEmpty())
			return values;
		
		for (Element element : childElements)
			values.add(element.getTextContent());
		
		return values;
	}
	public Element revert(Object o, Element e) {

		if(!(o instanceof List))
			throw new IllegalArgumentException("Wrong class object provided for CollectionConverter: "+o.getClass().getName()+". Should be java.util.List");
		
		NodeList childNodes = e.getChildNodes();
		
		List<Node> childs2Remove = new ArrayList<Node>();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			
			Node child = childNodes.item(i);
			
			if(child != null && (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.ELEMENT_NODE))
				childs2Remove.add(child);
		}
		
		for (Node node : childs2Remove)
			e.removeChild(node);

		@SuppressWarnings("unchecked")
		List<String> values = (List<String>)o;
		
		for (String value : values) {

			if(value == null)
				continue;
			
			Element listItem = e.getOwnerDocument().createElement(listItemElementName);
			listItem.appendChild(e.getOwnerDocument().createTextNode(value));
			e.appendChild(listItem);
		}
		
		return e;
	}
}