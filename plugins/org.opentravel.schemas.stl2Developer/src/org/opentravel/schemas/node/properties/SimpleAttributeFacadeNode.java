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
package org.opentravel.schemas.node.properties;

import org.eclipse.swt.graphics.Image;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLModelElement;
import org.opentravel.schemas.node.ComponentNodeType;
import org.opentravel.schemas.node.ModelNode;
import org.opentravel.schemas.node.Node;
import org.opentravel.schemas.node.facets.SimpleFacetFacadeNode;
import org.opentravel.schemas.node.interfaces.FacadeInterface;
import org.opentravel.schemas.node.interfaces.INode;
import org.opentravel.schemas.properties.Images;
import org.opentravel.schemas.types.TypeProvider;
import org.opentravel.schemas.types.TypeUserHandler;

/**
 * A property node that represents a simple property of a core or value with attributes objects. The TL Object is the
 * parent {@link SimpleFacetFacade}'s tlObject.
 * <p>
 * The type is never null, but may be the Empty type.
 * 
 * @author Dave Hollander
 * 
 */
public abstract class SimpleAttributeFacadeNode extends PropertyNode implements FacadeInterface {
	// private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAttributeNode.class);

	protected TypeProvider emptyNode = null;

	public SimpleAttributeFacadeNode(SimpleFacetFacadeNode parent) {
		super();

		assert parent != null;
		this.parent = parent;
		tlObj = parent.getTLModelObject();
		typeHandler = new TypeUserHandler(this);
	}

	@Override
	public void addToTL(final PropertyOwnerInterface owner, final int index) {
	}

	@Override
	public boolean canAssign(Node type) {
		return type instanceof TypeProvider ? ((TypeProvider) type).isAssignableToSimple() : false;
	}

	@Override
	public INode createProperty(Node type) {
		return null;
	}

	@Override
	public Node get() {
		return Node.GetNode(getTLModelObject());
	}

	// @Override
	// public TypeProvider getAssignedType() {
	// return getTLSimpleType();
	// }

	@Override
	public abstract NamedEntity getAssignedTLNamedEntity();

	@Override
	public ComponentNodeType getComponentNodeType() {
		return ComponentNodeType.SIMPLE_ATTRIBUTE;
	}

	public TypeProvider getEmptyNode() {
		if (emptyNode == null)
			emptyNode = (TypeProvider) ModelNode.getEmptyNode();
		assert emptyNode != null;
		return emptyNode;
	}

	// @Override
	// public String getEquivalent(String context) {
	// return getEquivalentHandler().get(context);
	// }
	//
	// @Override
	// public IValueWithContextHandler getEquivalentHandler() {
	// if (equivalentHandler == null)
	// equivalentHandler = new EqExOneValueHandler(this, ValueWithContextType.EQUIVALENT);
	// return equivalentHandler;
	// }
	//
	// @Override
	// public String getExample(String context) {
	// return getExampleHandler().get(context);
	// }
	//
	// @Override
	// public IValueWithContextHandler getExampleHandler() {
	// if (exampleHandler == null)
	// exampleHandler = new EqExOneValueHandler(this, ValueWithContextType.EXAMPLE);
	// return exampleHandler;
	// }

	@Override
	public Image getImage() {
		return Images.getImageRegistry().get(Images.XSDAttribute);
	}

	@Override
	public abstract String getName();

	@Override
	public SimpleFacetFacadeNode getParent() {
		return (SimpleFacetFacadeNode) parent;
	}

	@Override
	public abstract TLModelElement getTLModelObject();

	/**
	 * Use {@link #getAssignedType()}
	 */
	@Override
	@Deprecated
	public Node getType() {
		return (Node) getAssignedType();
	}

	@Override
	public boolean hasNavChildren(boolean deep) {
		return false;
	}

	@Override
	public boolean isEnabled_AddProperties() {
		return false;
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

	/**
	 * Simple Attribute Properties are new to a chain if their parent is new. Override the behavior in the property
	 * class.
	 */
	@Override
	public boolean isNewToChain() {
		if (getChain() == null || super.isNewToChain())
			return true; // the parent is new so must be its properties
		return false;
	}

	@Override
	public boolean isOnlySimpleTypeUser() {
		return true;
	}

	@Override
	public boolean isRenameable() {
		return false; // name must come from owning object
	}

	// @Override
	// public boolean setAssignedType(TypeProvider type) {
	// if (type instanceof AliasNode)
	// type = (TypeProvider) ((Node) type).getOwningComponent();
	// if (type.isAssignableToSimple())
	// setTLSimpleType(type.getTLModelObject());
	// else
	// return false;
	// return true;
	// }
	// @Override
	// public boolean setAssignedType(TLModelElement tla) {
	// // if (tla.isAssignableToSimple())
	// setTLSimpleType(tla);
	// // else
	// // return false;
	// return true;
	// }

	@Override
	public IValueWithContextHandler setEquivalent(String example) {
		getEquivalentHandler().set(example, null);
		return equivalentHandler;
	}

	@Override
	public IValueWithContextHandler setExample(String example) {
		getExampleHandler().set(example, null);
		return exampleHandler;
	}

	@Override
	public void setName(String name) {
	}

	/**
	 * @return the assigned type provider directly from the TL Object
	 */
	protected abstract TypeProvider getTLSimpleType();

	@Override
	protected void moveDownTL() {
	}

	@Override
	protected void moveUpTL() {
	}

	@Override
	protected void removeFromTL() {
	}

	// protected abstract void setTLSimpleType(TLModelElement type);

}