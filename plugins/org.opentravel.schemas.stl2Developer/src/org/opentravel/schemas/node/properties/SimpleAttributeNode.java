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
import org.opentravel.schemas.modelObject.TLnSimpleAttribute;
import org.opentravel.schemas.node.INode;
import org.opentravel.schemas.node.Node;
import org.opentravel.schemas.node.NodeFactory;
import org.opentravel.schemas.node.PropertyNodeType;
import org.opentravel.schemas.properties.Images;
import org.opentravel.schemas.types.TypeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opentravel.schemacompiler.model.TLFacetOwner;
import org.opentravel.schemacompiler.model.TLModelElement;

/**
 * A property node that represents a simple property of a core or value with attributes object. See
 * {@link NodeFactory#newComponentMember(INode, Object)}
 * 
 * @author Dave Hollander
 * 
 */

public class SimpleAttributeNode extends PropertyNode implements TypeUser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAttributeNode.class);

    public SimpleAttributeNode(TLModelElement tlObj, INode parent) {
        super(tlObj, parent, PropertyNodeType.SIMPLE);

        if (parent != null) {
            TLModelElement tlOwner = ((Node) parent.getParent()).getTLModelObject();
            if ((tlOwner instanceof TLFacetOwner) || (tlObj instanceof TLnSimpleAttribute))
                ((TLnSimpleAttribute) tlObj).setParentObject(tlOwner);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opentravel.schemas.node.Node#isTypeUser()
     */
    // Not needed because it implements typeUser()
    // @Override
    // public boolean isTypeUser() {
    // return true;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.opentravel.schemas.node.PropertyNode#createProperty(org.opentravel.schemas.node.Node)
     */
    @Override
    public INode createProperty(Node type) {
        // Need for DND but can't actually create a property, just set the type.
        setAssignedType(type);
        return this;
        // LOGGER.error("Tried to create a new simple property.");
        // throw new IllegalAccessError("Tried to create new simple property.");
        // return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opentravel.schemas.node.Node#isSimpleTypeUser()
     */
    @Override
    public boolean isOnlySimpleTypeUser() {
        return true;
    }

    @Override
    public Image getImage() {
        return Images.getImageRegistry().get(Images.XSDAttribute);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opentravel.schemas.node.INode#getLabel()
     */
    @Override
    public String getLabel() {
        return modelObject.getLabel() == null ? "" : modelObject.getLabel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opentravel.schemas.node.PropertyNode#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        LOGGER.debug("Tried to set the name of a simple property.");
    }

}