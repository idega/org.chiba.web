/**
 * @(#)IdegaItemset.java    1.0.0 2:42:00 PM
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
 *     �Licensee� shall also mean the individual using or installing 
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
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.events.ChibaEventNames;
import org.chiba.xml.ns.NamespaceConstants;
import org.chiba.xml.xforms.XFormsConstants;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.chiba.web.xml.xforms.connector.context.ContextXmlResolver;
import com.idega.util.ListUtil;
import com.idega.util.xml.XmlUtil;

/**
 * <p>Idega extension for {@link Itemset}. Adds support for spring expressions.
 * </p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.com">Martynas Stakė</a></p>
 * <p>You can expect to find some test cases notice in the end of the file.</p>
 *
 * @version 1.0.0 Oct 16, 2012
 * @author martynasstake
 */
public class IdegaItemset extends Itemset {
	
	public IdegaItemset(Element element, Model model) {
		super(element, model);
	}
	
//	@Override
//	protected void initializeItemset() throws XFormsException {
//		/*super.initializeItemset();
//		if (true) {
//			return;
//		}*/
//		// initialize positional items
//		List<Element> items = getConvertedItems(getAllItemNodes());
//		
//        int count = items.size();
//        this.items = new ArrayList(count);
//
//        if (getLogger().isDebugEnabled()) {
//            getLogger().debug(this + " init: initializing " + count + " selector item(s)");
//        }
//        
//        for (int position = 1; position < count + 1; position++) {
//            this.items.add(initializeSelectorItem(position, items.get(position-1)));
//        }
//        
//        System.out.println("");
//	}


	
	
//	private void disposeSelectorItem(Item item) throws XFormsException {
//        // dispose selector item
//        Element element = item.getElement();
//        int position = item.getPosition();
//        item.dispose();
//        this.element.removeChild(element);
//
//        if (this.model.isReady()) {
//            // dispatch internal chiba event
//            HashMap map = new HashMap();
//            map.put("originalId", this.originalId != null ? this.originalId : this.id);
//            map.put("position", String.valueOf(position));
//            this.container.dispatch(this.target, ChibaEventNames.ITEM_DELETED, map);
//        }
//    }
	
//	@Override
//	protected void updateItemset() throws XFormsException {
//		/*super.updateItemset();
//		if (true) {
//			return;
//		}*/
//        List<Element> items = getConvertedItems(getAllItemNodes());
//		
//		// check nodeset count
//        int count = items.size();
//        int size = this.items.size();
//
//        if (count < size) {
//            // remove obsolete selector items
//            if (getLogger().isDebugEnabled()) {
//                getLogger().debug(this + " update: disposing " + (size - count) + " selector item(s)");
//            }
//            for (int position = size; position > count; position--) {
//                disposeSelectorItem((Item) this.items.remove(position - 1));
//            }
//        }
//
//        if (count > size) {
//            // add missing selector items
//            if (getLogger().isDebugEnabled()) {
//                getLogger().debug(this + " update: initializing " + (count - size) + " selector item(s)");
//            }
//            
//            for (int position = size + 1; position <= count; position++) {
//                this.items.add(initializeSelectorItem(position, items.get(position-1)));
//            }
//        }
//	}

	@Override
	protected void initializePrototype() {
		super.initializePrototype();
		
		// remove itemset prototype
//        DOMUtil.removeAllChildren(getPrototype());
//		
//		List<Element> items = getConvertedItems(getAllItemNodes());
//		if (ListUtil.isEmpty(items)) {
//			return;
//		}
//		
//		for (Element item : items) {
//			try {
//				initializeSelectorItem(items.indexOf(item) + 1, item);
//			} catch (XFormsException e) {
//				LOGGER.warn("Unable to initialize item: " + item, e);
//			}
//			
//			Document owner = getPrototype().getOwnerDocument();
//	        Node nodeToImport = null;
//	        if (owner != null) {
//	        	nodeToImport = owner.importNode(item, true);
//	        }
//			
//			getPrototype().insertBefore(nodeToImport, null);
//		}
	}
	
	
}
