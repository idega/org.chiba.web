package com.idega.chiba.process.converters;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2007/09/27 16:25:42 $ by $Author: civilis $
 */
public class StringConverter implements DataConverter {

	public Object convert(Element o) {

		String txt = o.getTextContent();
		return "".equals(txt) ? null : txt;
	}
	public Element revert(Object o, Element e) {
	
		NodeList childNodes = e.getChildNodes();
		
		List<Node> childs2Remove = new ArrayList<Node>();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			
			Node child = childNodes.item(i);
			
			if(child != null && (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.ELEMENT_NODE))
				childs2Remove.add(child);
		}
		
		for (Node node : childs2Remove)
			e.removeChild(node);
		
		Node txtNode = e.getOwnerDocument().createTextNode(o instanceof String ? (String)o : o.toString());
		e.appendChild(txtNode);
		
		return e;
	}
}