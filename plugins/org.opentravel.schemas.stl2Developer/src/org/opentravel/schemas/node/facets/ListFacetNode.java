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

import org.opentravel.schemacompiler.model.TLFacetType;
import org.opentravel.schemacompiler.model.TLListFacet;
import org.opentravel.schemas.node.PropertyNodeType;

/**
 * Used for Detail and Summary List Facets.
 * 
 * @author Dave Hollander
 * 
 */
public class ListFacetNode extends FacetNode {

	public ListFacetNode(TLListFacet tlObj) {
		super(tlObj);
	}

	@Override
	public boolean isSimpleListFacet() {
		return (((TLListFacet) getTLModelObject()).getFacetType().equals(TLFacetType.SIMPLE)) ? true : false;
	}

	@Override
	public boolean isDetailListFacet() {
		return (((TLListFacet) getTLModelObject()).getFacetType().equals(TLFacetType.DETAIL)) ? true : false;
	}

	@Override
	public boolean isValidParentOf(PropertyNodeType type) {
		return false;
	}

}