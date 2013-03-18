/**
 * @(#)IdegaSelector.java    1.0.0 11:51:30 AM
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *      Licensee  shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package org.chiba.xml.xforms.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.ns.NamespaceConstants;
import org.chiba.xml.xforms.XFormsConstants;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.chiba.ChibaConstants;
import com.idega.chiba.web.xml.xforms.connector.context.ContextXmlResolver;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.xml.XmlUtil;

/**
 * Class description goes here.
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.com">Martynas Stakė</a></p>
 * <p>You can expect to find some test cases notice in the end of the file.</p>
 *
 * @version 1.0.0 Oct 23, 2012
 * @author martynasstake
 */
public class IdegaSelector extends Selector {

private ContextXmlResolver cxr = null;
	
	protected ContextXmlResolver getContextXmlResolver(String expression) {
		if (this.cxr == null && !StringUtil.isEmpty(expression)) {
			this.cxr = new ContextXmlResolver();
			this.cxr.setURI(expression);
		}
		
		return this.cxr;
	}
	
	private Node resolvedNode = null;
	
	@Deprecated
	protected Node getResolvedNode(String expression) {
		if (this.resolvedNode != null) {
			return this.resolvedNode;
		}
		
		if (StringUtil.isEmpty(expression)) {
			return null;
		}
		
		Object object = null;
		try {
			object = getContextXmlResolver(expression).resolve();
			
			if (object instanceof Node) {
				this.resolvedNode = (Node) object;
			}
		} catch (XFormsException e) {
			LOGGER.warn("Failed to resolve context of: " + IdegaItemset.class);
		}
		
		return this.resolvedNode;
	}
	
	@Deprecated
	protected List<Node> getChoiceListDataNodes(List<Node> nodes) {
		if (ListUtil.isEmpty(nodes)) {
			return null;
		}
		
		List<Node> nodesList = new ArrayList<Node>();
		for (Node node : nodes) {
			List<Node> nodesInside = getChoiceListDataNodes(node);
			if (ListUtil.isEmpty(nodesInside)) {
				continue;
			}
			
			nodesList.addAll(nodesInside);
		}
		
		return nodesList;
	}
	
	@Deprecated
	protected List<Node> getChoiceListDataNodes(Node node) {
		if (node == null) {
			return null;
		}
		
		List<Node> nodeList = new ArrayList<Node>();
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item == null) {
				continue;
			}
			
			if ("choiceListData".equals(item.getNodeName())) {
				nodeList.add(item);
			}
		}
		
		return nodeList;
	}
	
	@Deprecated
	protected List<Node> getLocalizedEntriesNodes(List<Node> nodes) {
		if (ListUtil.isEmpty(nodes)) {
			return null;
		}
		
		List<Node> nodesList = new ArrayList<Node>();
		for (Node node : nodes) {
			List<Node> nodesInside = getLocalizedEntriesNodes(node);
			if (ListUtil.isEmpty(nodesInside)) {
				continue;
			}
			
			nodesList.addAll(nodesInside);
		}
		
		return nodesList;
	}
	
	@Deprecated
	protected List<Node> getLocalizedEntriesNodes(Node node) {
		if (node == null) {
			return null;
		}
		
		List<Node> nodeList = new ArrayList<Node>();
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item == null) {
				continue;
			}
			
			if ("localizedEntries".equals(item.getNodeName())) {
				nodeList.add(item);
			}
		}
		
		return nodeList;
	}

	@Deprecated
	protected List<Node> getItemNodes(List<Node> nodes) {
		if (ListUtil.isEmpty(nodes)) {
			return null;
		}
		
		List<Node> nodesList = new ArrayList<Node>();
		for (Node node : nodes) {
			List<Node> nodesInside = getItemNodes(node);
			if (ListUtil.isEmpty(nodesInside)) {
				continue;
			}
			
			nodesList.addAll(nodesInside);
		}
		
		return nodesList;
	}
	
	@Deprecated
	protected List<Element> getConvertedItems(List<Node> items) {
		if (ListUtil.isEmpty(items)) {
			return null;
		}
		
		List<Element> convertedItems = new ArrayList<Element>();
		for (Node item : items) {
			Element convertedItem = getConvertedItem(item);
			if (convertedItem == null) {
				continue;
			}
			
			convertedItems.add(convertedItem);
		}
		
		return convertedItems;
	}
	
	/**
	 * 
	 * @param expression - bean name and method name to resolve, for example:
	 * "expXml:aquaCultureService.speciesGroupsMap". Not <code>null</code>. 
	 * @param owner {@link Document}, which should have items;
	 * @param locale of {@link Element}s or <code>null</code> if current locale
	 * is needed.
	 * @return {@link List} of items or <code>null</code> on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public List<Element> getItems(String expression, Document owner, 
			Locale locale) {
		if (StringUtil.isEmpty(expression)) {
			getLogger().warn("No expression given, no items will be resolved");
			return null;
		}
		
		Map<Locale, Map<String, String>> items = getContextXmlResolver(
				expression).resolveItems(expression);
		return getConvertedAndLocalizedItems(items, locale, owner);
	}
	
	/**
	 * <p>Gets {@link Element}s by {@link Locale}.</p>
	 * @param locale of {@link Element}s or <code>null</code> if current locale
	 * is needed.
	 * @param items which should be converted;
	 * @param owner {@link Document}, which should have items;
	 * @return {@link List} of items or <code>null</code> on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public List<Element> getConvertedAndLocalizedItems(
			Map<Locale, Map<String, String>> items, Locale locale, 
			Document owner) {
		
		if (MapUtil.isEmpty(items)) {
			return null;
		}
		
		if (locale == null) {
			locale = CoreUtil.getCurrentLocale();
		}
		
		return getConvertedItems(items.get(locale), owner);
	}
	
	/**
	 * <p>Creates Xforms elements, which has {@link ChibaConstants#NAMESPACE_URL}
	 * and can be passed to XForms document.
	 * </p>
	 * @param items which should be converted;
	 * @param owner {@link Document}, which should have items;
	 * @return {@link List} of items or <code>null</code> on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public List<Element> getConvertedItems(Map<String, String> items, 
			Document owner) {
		if (MapUtil.isEmpty(items)) {
			return null;
		}
		
		List<Element> convertedItems = new ArrayList<Element>();
		for (Map.Entry<String, String> entry : items.entrySet()) {
			Element convertedItem = getConvertedItem(
					entry.getValue(), 
					entry.getKey(), owner);

			if (convertedItem == null) {
				continue;
			}
			
			convertedItems.add(convertedItem);
		}
		
		return convertedItems;
	}
	
	/**
	 * <p>Creates Xforms element, which has {@link ChibaConstants#NAMESPACE_URL}
	 * and can be passed to XForms document.
	 * </p>
	 * @param label of item;
	 * @param value of item;
	 * @param owner of created element, if <code>null</code>, new 
	 * {@link Document} will be created.
	 * @return {@link Element} representing {@link Item}.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public Element getConvertedItem(String label, String value, 
			Document owner) {
		return createXFormsItemElement(label, value, owner);
	}
	
	@Deprecated
	protected Element getConvertedItem(Node node) {
		if (node == null) {
			return null;
		}
		
		if (!"item".equals(node.getNodeName())) {
			return null;
		}
		
		Node itemLabel = null;
		Node itemValue = null;
		
		NodeList nodelist = node.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			Node item = nodelist.item(i);
			if (item == null) {
				continue;
			}
			
			if ("itemValue".equals(item.getNodeName())) {
				itemValue = item;
			}
			
			if ("itemLabel".equals(item.getNodeName())) {
				itemLabel = item;
			}
		}
		
		return createXFormsItemElement(
					itemLabel.getTextContent(), 
					itemValue.getTextContent(), null);
	}
	
	/**
	 * <p>Creates Xforms element, which has {@link ChibaConstants#NAMESPACE_URL}
	 * and can be passed to XForms document.
	 * </p>
	 * @param label of item;
	 * @param value of item;
	 * @param owner of created element, if <code>null</code>, new 
	 * {@link Document} will be created.
	 * @return {@link Element} representing {@link Item}.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public Element createXFormsItemElement(String label, String value, Document owner) {
		if (owner == null) {
			owner = DOMUtil.newDocument(Boolean.TRUE, Boolean.TRUE);
			owner.createAttributeNS(ChibaConstants.NAMESPACE_URL, 
					ChibaConstants.NAMESPACE_PREFIX);
		}
		
		Element convertedItem = owner.createElementNS(
				ChibaConstants.NAMESPACE_URL, 
				ChibaConstants.NODENAME_ITEM_WITH_NAMESPACE_PREFIX);
		
		Element convertedLabel = owner.createElementNS(
				ChibaConstants.NAMESPACE_URL, 
				ChibaConstants.NODENAME_LABEL_WITH_NAMESPACE_PREFIX);
		convertedLabel.setTextContent(label);
		
		Element convertedValue = owner.createElementNS(
				ChibaConstants.NAMESPACE_URL, 
				ChibaConstants.NODENAME_VALUE_WITH_NAMESPACE_PREFIX);
		convertedValue.setTextContent(value);
		
		convertedItem.appendChild(convertedLabel);
		convertedItem.appendChild(convertedValue);
		
		try {
			owner.adoptNode(convertedItem);
		} catch (org.w3c.dom.DOMException e) {
			convertedItem = (Element) owner.importNode(convertedItem, Boolean.TRUE);
		}
		
		return convertedItem;
	}
	
	@Deprecated
	protected List<Node> getItemNodes(Node node) {
		if (node == null) {
			return null;
		}
		
		List<Node> items = new ArrayList<Node>();
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item == null) {
				continue;
			}
			
			if ("item".equals(item.getNodeName())) {
				items.add(item);
			}
		}
		
		return items;
	}
	
	@Deprecated
	protected List<Node> getAllItemNodes(String expression) {
		return getItemNodes(
				getLocalizedEntriesNodes(
						getChoiceListDataNodes(getResolvedNode(expression))));
	}
	
	protected Item initializeSelectorItem(int position, Element itemElement, 
			Element mainElement, Model model) throws XFormsException {
        // detect reference node
        Node before = DOMUtil.findNthChildNS(mainElement, 
        		NamespaceConstants.XFORMS_NS, XFormsConstants.ITEM, position);

        if (before == null) {
            before = DOMUtil.findFirstChildNS(mainElement, 
            		NamespaceConstants.CHIBA_NS, "data");
        }

        Document owner = mainElement.getOwnerDocument();
        Node nodeToImport = null;
        if (owner != null) {
        	nodeToImport = owner.importNode(itemElement, true);
        }
        
        // create selector item
        mainElement.insertBefore(nodeToImport, before);

        // initialize selector item
        Item item = (Item) this.container
        		.getElementFactory().createXFormsElement(
        				(Element) nodeToImport, model
        		);
        item.setPosition(position);
        item.setGeneratedId(this.container.generateId());
        item.registerId();
        item.init();

        return item;
    }
 
	/**
	 * <p>Adds and initializes {@link Item}s to element and model, 
	 * given in constructor.</p>
	 * @param items to add, not <code>null</code>..
	 * @return {@link List} of initialized {@link Item}s or <code>null</code>
	 * on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	protected List<Item> appendItems(Element element, Model model, 
			List<Element> items) {

		if (ListUtil.isEmpty(items)) {
			LOGGER.warn("No items, element or model passed!");
			return null;
		}
		
		List<Node> selectNodes = XmlUtil.getChildNodes(
				element, 
				ChibaConstants.NAMESPACE_URL, 
				XFormsConstants.VALUE, null,
				null);
		
		Set<String> existingValues = new HashSet<String>(selectNodes.size());
		for (Node node : selectNodes) {
			existingValues.add(node.getTextContent());
		}
		
		
		List<Item> xformsItems = new ArrayList<Item>(items.size()); 
		for (Element item : items) {
			NodeList values = item.getElementsByTagName("xf:value");
			if (values == null || values.getLength() > 1) {
				continue;
			}
			
			Node value = values.item(0);
			if (value == null || existingValues.contains(value.getTextContent())) {
				continue;
			}
			
			try {
				xformsItems.add(initializeSelectorItem(
						items.indexOf(item) + 1, item, element, model)
						);
			} catch (XFormsException e) {
				LOGGER.warn("Unable to initialize item: " + item, e);
			}
		}
		
		return xformsItems;
	}
	
	/**
	 * <p>Takes idega:select1 from parent of this element if and only if 
	 * this idega:select1 is in *:repeat tag.</p>
	 * @return {@link Node} of idega:select1 or <code>null</code> on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 * @deprecated <p>This method is hack or nonsense, no matter how you call 
	 * it. It gives another object of this type.
	 * It is highly recommended to find other solution for rewriting 
	 * original idega:select1 node.</p>
	 */
	@Deprecated
	public Node getOriginal() {
		String bindName = this.element.getAttribute(ChibaConstants.ATTRIBUTE_BIND);
		
		int numberOfChilds = 0;
		NodeList list = this.element.getChildNodes();
		if (list != null) {
			numberOfChilds = list.getLength();
		}
		
		Node group = this.element.getParentNode();
		if (group == null || 
				!ChibaConstants.NODENAME_GROUP.equals(group.getLocalName())) {
			LOGGER.info("idega:select1 is not in group, original won't be changed.");
			return null;
		}
		
		Node repeat = group.getParentNode();
		if (repeat == null || 
				!ChibaConstants.NODENAME_REPEAT.equals(repeat.getLocalName())) {
			LOGGER.info("idega:select1 is not in repeat, original won't be changed.");
			return null;
		}
		
		List<Node> selectNodes = XmlUtil.getChildNodes(
				repeat instanceof Element ? (Element) repeat : null, 
				ChibaConstants.NAMESPACE_URL_IDEGA, 
				ChibaConstants.NODENAME_SELECT1, ChibaConstants.ATTRIBUTE_BIND,
				bindName);
		
		if (ListUtil.isEmpty(selectNodes)) {
			LOGGER.info("No nodes found by given criteria.");
			return null;
		}
		
		Node originalSelect = null;
		
		for (Node select : selectNodes) {
			NodeList childNodes = select.getChildNodes();
			if (numberOfChilds != childNodes.getLength()) {
				originalSelect = select;
			}
		}
		
		return originalSelect;
	}
	
	/**
	 * @param element
	 * @param model
	 */
	public IdegaSelector(Element element, Model model) {
		super(element, model);
		
		String expression = element.getAttribute("src");
		if (StringUtil.isEmpty(expression)) {
			LOGGER.info("No expression were given in src attribute. Nothing " +
					"additional will be loaded.");
			return;
		}
		
		String id = this.container.generateId();
		try {
			setGeneratedId(id);
		} catch (XFormsException e1) {
			getLogger().warn("Unable to set select1 generated id: " + id, 
					e1);
		}
		
		if (XmlUtil.getParentElement(element, REPEAT) != null) {
			Element parentElement = XmlUtil.getParentElement(element, GROUP);
			if (parentElement != null) {
				try {
					setRepeatItemId(parentElement.getAttribute("id"));
				} catch (XFormsException e) {
					getLogger().warn("Unable to set repeat-item id", e);
				}
			}
		}

		if (ListUtil.isEmpty(appendItems(this.element, this.model, getItems(
				expression,	element.getOwnerDocument(), null)))) {
			return;
		}
		
		Node original = getOriginal();
		if (original != null) {
			appendItems((Element) original, this.model, getItems(
				expression,	element.getOwnerDocument(), null));
		}
	}
}
