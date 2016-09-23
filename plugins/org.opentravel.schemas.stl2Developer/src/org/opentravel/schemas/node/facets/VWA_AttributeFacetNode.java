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
package org.opentravel.schemas.node.facets;

import org.opentravel.schemas.modelObject.TLValueWithAttributesFacet;
import org.opentravel.schemas.node.PropertyNodeType;

/**
 * Used for Request, Response and Notification Facets.
 * 
 * @author Dave Hollander
 * 
 */
public class VWA_AttributeFacetNode extends FacetNode {

	public VWA_AttributeFacetNode(TLValueWithAttributesFacet tlObj) {
		super(tlObj);
	}

	@Override
	public boolean isAssignable() {
		return false; // vwa facet can't be assigned independently of the VWA
	}

	@Override
	public boolean isTypeProvider() {
		return false; // can't be assigned therefore is not a type provider
	}

	@Override
	public boolean isValidParentOf(PropertyNodeType type) {
		return PropertyNodeType.getVWA_PropertyTypes().contains(type);
	}

}