/**
 * @(#)IdegaDeleteAction.java    1.0.0 10:54:15
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1. License Grant: Upon completion of this agreement, the source 
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
 * 2. License Limitations
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
 * 3. Copyright: Manufacturer's source code is copyrighted and contains 
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
 * 4. Warranty & Risks: Although efforts have been made to assure that the 
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
package com.idega.chiba.web.xml.xforms.elements.action;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.xml.events.XFormsEventNames;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.action.DeleteAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import com.idega.util.StringUtil;

/**
 * <p>Class for items deletion in local data sources.</p> 
 * <p>
 * You can report about problems to: <a
 * href="mailto:martynas@idega.com">Martynas Stakė</a>
 * </p>
 * <p>
 * You can expect to find some test cases notice in the end of the file.
 * </p>
 * 
 * @version 1.0.0 2011.12.10
 * @author martynas
 */
public class IdegaDeleteAction extends AbstractBoundAction {

	protected static Log LOGGER = LogFactory.getLog(DeleteAction.class);

	/**
	 * Creates a idega:delete action implementation.
	 * 
	 * @param element
	 *            the element.
	 * @param model
	 *            the context model.
	 */
	public IdegaDeleteAction(Element element, Model model) {
		super(element, model);
	}

	// implementation of 'org.chiba.xml.xforms.core.Binding'

	/**
	 * Returns the binding expression.
	 * 
	 * @return the binding expression.
	 */
	public String getBindingExpression() {
		return getXFormsAttribute(NODESET_ATTRIBUTE);
	}

	// implementation of 'org.chiba.xml.xforms.action.XFormsAction'

	/**
	 * Performs the <code>delete</code> action for all nodes.
	 * 
	 * @throws XFormsException
	 *             if an error occurred during <code>delete</code> processing.
	 */
	public void perform() throws XFormsException {
		super.perform();

		// get instance and nodeset information
		Instance instance = this.model.getInstance(getInstanceId());
		String pathExpression = getLocationPath();
		int contextSize = instance.countNodeset(pathExpression);
		if (contextSize == 0) {
			getLogger().warn(
					this + " perform: nodeset '" + pathExpression
							+ "' is empty");
			return;
		}

		String path = null;
		// since jxpath doesn't provide a means for evaluating an expression
		// in a certain context, we use a trick here: the expression will be
		// evaluated during getPointer and the result stored as a variable
		JXPathContext context = instance.getInstanceContext();
		boolean lenient = context.isLenient();
		context.setLenient(true);
		context.getPointer(pathExpression
				+ "[chiba:declare('delete-position', 1)]");
		context.setLenient(lenient);

		// since jxpath's round impl is buggy (returns 0 for NaN) we perform
		// 'round' manually
		double value = ((Double) context
				.getValue("number(chiba:undeclare('delete-position'))"))
				.doubleValue();
		long position = Math.round(value);
		if (Double.isNaN(value) || position < 1 || position > contextSize) {
			getLogger().warn(
					this + " perform: expression '1' does not point to an existing node");
			return;
		}

		path = new StringBuffer(pathExpression).append('[').append(position)
				.append(']').toString();

		// delete specified node and dispatch notification event
		while (instance.existsNode(path)) {
			try {
				instance.deleteNode(path);
				getLogger().info("Node deleted in delete action:" + path);
			} catch (JXPathException e) {
				getLogger().warn("Unable to delete:" + path);
			}
		}

		if (StringUtil.isEmpty(path)) {
			return;
		}

		this.container.dispatch(instance.getTarget(), XFormsEventNames.DELETE,
				path);

		// update behaviour
		doRebuild(true);
		doRecalculate(true);
		doRevalidate(true);
		doRefresh(true);
	}
}
