/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opentravel.schemas.wizards;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.opentravel.schemas.node.Node;
import org.opentravel.schemas.node.ServiceNode;
import org.opentravel.schemas.node.interfaces.INode;
import org.opentravel.schemas.node.libraries.LibraryNode;
import org.opentravel.schemas.node.properties.AttributeReferenceNode;
import org.opentravel.schemas.node.properties.ElementReferenceNode;
import org.opentravel.schemas.node.resources.ActionFacet;
import org.opentravel.schemas.node.resources.ResourceNode;
import org.opentravel.schemas.node.typeProviders.ChoiceFacetNode;
import org.opentravel.schemas.node.typeProviders.ContextualFacetNode;
import org.opentravel.schemas.node.typeProviders.SimpleTypeNode;
import org.opentravel.schemas.node.typeProviders.VWA_Node;
import org.opentravel.schemas.properties.Messages;
import org.opentravel.schemas.trees.type.BusinessObjectOnlyTypeFilter;
import org.opentravel.schemas.trees.type.ContextualFacetOwnersTypeFilter;
import org.opentravel.schemas.trees.type.CoreAndChoiceObjectOnlyTypeFilter;
import org.opentravel.schemas.trees.type.LibraryOnlyTypeFilter;
import org.opentravel.schemas.trees.type.TypeSelectionFilter;
import org.opentravel.schemas.trees.type.TypeTreeIdReferenceTypeOnlyFilter;
import org.opentravel.schemas.trees.type.TypeTreeSimpleAssignableOnlyFilter;
import org.opentravel.schemas.trees.type.TypeTreeSimpleTypeOnlyFilter;
import org.opentravel.schemas.trees.type.TypeTreeVWASimpleTypeOnlyFilter;
import org.opentravel.schemas.trees.type.TypeTreeVersionSelectionFilter;
import org.opentravel.schemas.types.TypeProvider;
import org.opentravel.schemas.types.TypeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wizard to allow user to select a type for the passed node objects. Selected type is assigned if wizard returns true.
 * 
 * Uses the passed node or first node of the list to set filters for simple/complex/vwa types.
 * 
 * @author Dave Hollander, Agnieszka Janowska
 * 
 */
public class TypeSelectionWizard extends Wizard implements IDoubleClickListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(TypeSelectionWizard.class);

	private Node curNode = null;
	private ArrayList<Node> curNodeList = null;
	private ArrayList<Node> setNodeList = new ArrayList<>();

	private TypeSelectionPage selectionPage;
	private WizardDialog dialog;
	private boolean dontFinish = false; // see run() - use only as converting to action class.

	// private TypeSelectionMode mode = null;
	// public enum TypeSelectionMode {
	// SERVICE,
	// SIMPLE_ASSIGNABLE,
	// SIMPLE,
	// VWA,
	// ID_REFERENCE,
	// VERSIONS,
	// RESOURCE,
	// CORE_AND_CHOICE,
	// CONTEXTUAL_FACET,
	// LIBRARIES
	// }
	//
	// private TypeSelectionMode setMode(Node n) {
	// if (n != null)
	// if (n instanceof ActionFacet)
	// return TypeSelectionMode.CORE_AND_CHOICE;
	// else if (n instanceof ResourceNode)
	// return TypeSelectionMode.RESOURCE;
	// else if (n.getLaterVersions() != null)
	// return TypeSelectionMode.VERSIONS;
	// else if (n.getOwningComponent() instanceof VWA_Node)
	// if (!(n instanceof AttributeReferenceNode))
	// return TypeSelectionMode.VWA;
	// else
	// return TypeSelectionMode.ID_REFERENCE;
	// else if (n instanceof SimpleTypeNode)
	// return TypeSelectionMode.SIMPLE;
	// else if (n.isOnlySimpleTypeUser())
	// return TypeSelectionMode.SIMPLE_ASSIGNABLE;
	// else if (n instanceof ServiceNode)
	// return TypeSelectionMode.SERVICE;
	// else if (n instanceof ElementReferenceNode)
	// return TypeSelectionMode.ID_REFERENCE;
	// else if (n instanceof ContextualFacetNode)
	// return TypeSelectionMode.CONTEXTUAL_FACET;
	// else if (n instanceof LibraryNode)
	// return TypeSelectionMode.LIBRARIES;
	// return null;
	// }

	/**
	 * Type selection wizard to select a node to assign as a type.
	 * 
	 * @param nodeList
	 *            is a list of nodes to assign the selected type to. The nodes are examined to select tree view filters.
	 */
	@Deprecated
	public TypeSelectionWizard(ArrayList<Node> nodeList) {
		// this((Node) null);
		super();
		curNodeList = nodeList;
		// LOGGER.debug("Type Selection Wizard initialized for nodelist.");
	}

	/**
	 * Type selection wizard to select a node to assign as a type.
	 * 
	 * @param n
	 *            the node to assign to. Type of node selects the filter to use on tree view.
	 */
	public TypeSelectionWizard(final Node n) {
		super();
		curNodeList = new ArrayList<>();
		if (n != null && n.isEditable())
			curNodeList.add(n);
		// LOGGER.debug("Type Selection Wizard initialized for node.");
	}

	// TODO - make this to work. Perhaps a typeSelectionWizard2().
	//
	// Problem is that the assumption of a current selection is deeply embedded in this wizard
	// and its filters and pages
	//
	//
	// public void addPages2() {
	// if (mode == null)
	// return; // TODO - How to exit wizard?
	//
	// // Create a type selection page
	// String pageName = Messages.getString("wizard.typeSelection.pageName.component");
	// String title = Messages.getString("wizard.typeSelection.title.component");
	// String description = Messages.getString("wizard.typeSelection.description.component");
	//
	// switch (mode) {
	// case SERVICE:
	// pageName = Messages.getString("wizard.typeSelection.pageName.service");
	// title = Messages.getString("wizard.typeSelection.title.service");
	// description = Messages.getString("wizard.typeSelection.description.service");
	// break;
	// case RESOURCE:
	// pageName = Messages.getString("wizard.typeSelection.pageName.resource");
	// title = Messages.getString("wizard.typeSelection.title.resource");
	// description = Messages.getString("wizard.typeSelection.description.resource");
	// break;
	// case LIBRARIES:
	// pageName = Messages.getString("wizard.typeSelection.pageName.library");
	// title = Messages.getString("wizard.typeSelection.title.library");
	// description = Messages.getString("wizard.typeSelection.description.library");
	// break;
	// case CONTEXTUAL_FACET:
	// // FIXME
	// if (setNodeList.get(0) instanceof ChoiceFacetNode) {
	// pageName = Messages.getString("wizard.typeSelection.pageName.contextualFacet");
	// title = Messages.getString("wizard.typeSelection.title.contextualChoiceFacet");
	// description = Messages.getString("wizard.typeSelection.description.contextualChoiceFacet");
	// } else {
	// pageName = Messages.getString("wizard.typeSelection.pageName.contextualFacet");
	// title = Messages.getString("wizard.typeSelection.title.contextualFacet");
	// description = Messages.getString("wizard.typeSelection.description.contextualFacet");
	// }
	// break;
	// default:
	// break;
	// }
	// selectionPage = new TypeSelectionPage(pageName, title, description, null, setNodeList);
	//
	// // Set the filters based on type of passed node.
	// // FIXME - versions and contextual facet use Node as parameter
	// switch (mode) {
	// case CORE_AND_CHOICE:
	// selectionPage.setTypeSelectionFilter(new CoreAndChoiceObjectOnlyTypeFilter(null));
	// break;
	// case VERSIONS:
	// selectionPage.setTypeSelectionFilter(new TypeTreeVersionSelectionFilter(curNodeList.get(0)));
	// break;
	// case SIMPLE_ASSIGNABLE:
	// selectionPage.setTypeSelectionFilter(new TypeTreeSimpleAssignableOnlyFilter());
	// break;
	// case SIMPLE:
	// selectionPage.setTypeSelectionFilter(new TypeTreeSimpleTypeOnlyFilter());
	// break;
	// case VWA:
	// selectionPage.setTypeSelectionFilter(new TypeTreeVWASimpleTypeOnlyFilter());
	// break;
	// case SERVICE:
	// selectionPage.setTypeSelectionFilter(new BusinessObjectOnlyTypeFilter(null));
	// break;
	// case RESOURCE:
	// selectionPage.setTypeSelectionFilter(new BusinessObjectOnlyTypeFilter(null));
	// break;
	// case CONTEXTUAL_FACET:
	// selectionPage.setTypeSelectionFilter(
	// new ContextualFacetOwnersTypeFilter((ContextualFacetNode) setNodeList.get(0)));
	// break;
	// case ID_REFERENCE:
	// selectionPage.setTypeSelectionFilter(new TypeTreeIdReferenceTypeOnlyFilter());
	// break;
	// case LIBRARIES:
	// selectionPage.setTypeSelectionFilter(new LibraryOnlyTypeFilter());
	// default:
	// selectionPage.setTypeSelectionFilter(new TypeSelectionFilter());
	// }
	//
	// selectionPage.addDoubleClickListener(this);
	// addPage(selectionPage);
	// }

	@Override
	public void addPages() {
		// LOGGER.debug("Adding Selection Page.");
		// TypeSelectionMode mode = null;

		// Make sure all the nodes are non-null and editable
		// and set lowest common denominator: simple, vwa and complex.
		boolean service = false;
		boolean simpleAssignable = false;
		boolean simple = false;
		boolean vwa = false;
		boolean idReference = false;
		boolean versions = false;
		boolean resource = false;
		boolean coreAndChoice = false;
		boolean contextualFacet = false;
		boolean libraries = false;

		// TODO - should not be worried about if it is editable
		// FIXME - how does a list of nodes impact the selection?
		// TODO - delegate getting filter to nodes
		if (curNodeList != null) {
			for (Node n : curNodeList) {
				if (n != null && n.isEditable()) {
					setNodeList.add(0, n); // why in front of list?
					if (n instanceof ActionFacet)
						coreAndChoice = true;
					else if (n instanceof ResourceNode)
						resource = true;
					else if (n.getLaterVersions() != null)
						versions = true;
					else if (n.getOwningComponent() instanceof VWA_Node)
						if (!(n instanceof AttributeReferenceNode))
							vwa = true;
						else
							idReference = true;
					else if (n instanceof SimpleTypeNode)
						simple = true;
					else if (n.isOnlySimpleTypeUser())
						simpleAssignable = true;
					else if (n instanceof ServiceNode)
						service = true;
					else if (n instanceof ElementReferenceNode)
						idReference = true;
					else if (n instanceof ContextualFacetNode)
						contextualFacet = true;
					else if (n instanceof LibraryNode)
						libraries = true;
				}
			}
		}
		// Exit when selected nodes are all read-only
		if (setNodeList.size() <= 0)
			return; // TODO - How to exit wizard?

		// Create a type selection page
		String pageName = Messages.getString("wizard.typeSelection.pageName.component");
		String title = Messages.getString("wizard.typeSelection.title.component");
		String description = Messages.getString("wizard.typeSelection.description.component");
		if (service) {
			pageName = Messages.getString("wizard.typeSelection.pageName.service");
			title = Messages.getString("wizard.typeSelection.title.service");
			description = Messages.getString("wizard.typeSelection.description.service");
		}
		if (resource) {
			pageName = Messages.getString("wizard.typeSelection.pageName.resource");
			title = Messages.getString("wizard.typeSelection.title.resource");
			description = Messages.getString("wizard.typeSelection.description.resource");
		}
		if (libraries) {
			pageName = Messages.getString("wizard.typeSelection.pageName.library");
			title = Messages.getString("wizard.typeSelection.title.library");
			description = Messages.getString("wizard.typeSelection.description.library");
		}
		if (contextualFacet) {
			if (setNodeList.get(0) instanceof ChoiceFacetNode) {
				pageName = Messages.getString("wizard.typeSelection.pageName.contextualFacet");
				title = Messages.getString("wizard.typeSelection.title.contextualChoiceFacet");
				description = Messages.getString("wizard.typeSelection.description.contextualChoiceFacet");
			} else {
				pageName = Messages.getString("wizard.typeSelection.pageName.contextualFacet");
				title = Messages.getString("wizard.typeSelection.title.contextualFacet");
				description = Messages.getString("wizard.typeSelection.description.contextualFacet");
			}
		}
		selectionPage = new TypeSelectionPage(pageName, title, description, null, setNodeList);

		// Set the filters based on type of passed node.
		if (coreAndChoice)
			selectionPage.setTypeSelectionFilter(new CoreAndChoiceObjectOnlyTypeFilter(null));
		else if (versions)
			selectionPage.setTypeSelectionFilter(new TypeTreeVersionSelectionFilter(curNodeList.get(0)));
		else if (simpleAssignable)
			selectionPage.setTypeSelectionFilter(new TypeTreeSimpleAssignableOnlyFilter());
		else if (simple)
			selectionPage.setTypeSelectionFilter(new TypeTreeSimpleTypeOnlyFilter());
		else if (vwa)
			selectionPage.setTypeSelectionFilter(new TypeTreeVWASimpleTypeOnlyFilter());
		else if (service)
			selectionPage.setTypeSelectionFilter(new BusinessObjectOnlyTypeFilter(null));
		else if (resource)
			selectionPage.setTypeSelectionFilter(new BusinessObjectOnlyTypeFilter(null));
		else if (contextualFacet)
			selectionPage.setTypeSelectionFilter(
					new ContextualFacetOwnersTypeFilter((ContextualFacetNode) setNodeList.get(0)));
		else if (idReference)
			selectionPage.setTypeSelectionFilter(new TypeTreeIdReferenceTypeOnlyFilter());
		else if (libraries)
			selectionPage.setTypeSelectionFilter(new LibraryOnlyTypeFilter());
		else
			selectionPage.setTypeSelectionFilter(new TypeSelectionFilter());

		selectionPage.addDoubleClickListener(this);
		addPage(selectionPage);
	}

	// According to the link, this belongs in the wizard not page
	// http://dev.eclipse.org/viewcvs/viewvc.cgi/org.eclipse.jface.snippets/Eclipse%20JFace%20Snippets/org/eclipse/jface/snippets/wizard/Snippet047WizardWithLongRunningOperation.java?view=markup
	@Override
	public boolean canFinish() {
		return selectionPage.getSelectedNode() == null ? false : true;
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		if (canFinish()) {
			performFinish();
			dialog.close();
		}
	}

	/**
	 * @return the setNodeList which is the filtered copy of the source list
	 */
	public ArrayList<Node> getList() {
		return setNodeList;
	}

	/**
	 * @return the setNodeList which is the filtered copy of the source list
	 */
	public Node getSelection() {
		return selectionPage == null ? null : selectionPage.getSelectedNode();
	}

	// This code is in the AssignTypeAction.execute().
	// TODO - eliminate after refactoring other invokers.
	@Override
	public boolean performFinish() {
		if (getSelection() == null)
			return false;
		if (dontFinish)
			return true;

		INode sn = selectionPage.getSelectedNode();
		if (setNodeList != null) {
			for (INode cn : selectionPage.getCurNodeList()) {
				// LOGGER.debug("Assigning " + sn.getName() + " to list node " + cn.getName());
				((TypeUser) cn).setAssignedType((TypeProvider) sn);
			}
		} else if (curNode != null) {
			// LOGGER.debug("Assigning " + selectionPage.getSelectedNode() + " to node " + curNode.getName());
			((TypeUser) curNode).setAssignedType((TypeProvider) selectionPage.getSelectedNode());
		} else
			return false;
		return true;
	}

	/**
	 * Run the wizard but DO NOT assign the types. Usage if (wizard.run(OtmRegistry.getActiveShell())) {
	 * AssignTypeAction.execute(wizard.getList(), wizard.getSelection()); }
	 * 
	 * @return
	 */
	public boolean run(final Shell shell) {
		if (curNode == null && curNodeList == null) {
			LOGGER.warn("Early Exit - no node(s) to post.");
			return false; // DO Nothing
		}

		dontFinish = true;
		dialog = new WizardDialog(shell, this);
		dialog.setPageSize(700, 600);
		dialog.create();
		int result = dialog.open();
		return result == org.eclipse.jface.window.Window.OK ? true : false;
		// return true;
	}

	/**
	 * Run the wizard AND do assign the type.
	 * 
	 * @param assign
	 *            - if true assign the type to the node on finish
	 * @return
	 */
	@Deprecated
	public boolean run(final Shell shell, boolean assign) {
		if (curNode == null && curNodeList == null) {
			LOGGER.warn("Early Exit - no node(s) to post.");
			return false; // DO Nothing
		}

		dontFinish = true;
		if (assign)
			dontFinish = false;
		dialog = new WizardDialog(shell, this);
		dialog.setPageSize(700, 600);
		dialog.create();
		dialog.open();
		return true;
	}

}
