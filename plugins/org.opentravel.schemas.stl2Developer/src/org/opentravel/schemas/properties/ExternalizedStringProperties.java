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
package org.opentravel.schemas.properties;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;

/**
 * @author Agnieszka Janowska
 * 
 */
public class ExternalizedStringProperties implements StringProperties {

    private final Map<PropertyType, String> properties;

    public ExternalizedStringProperties(final String prefix) {
        properties = new EnumMap<PropertyType, String>(PropertyType.class);
        for (final PropertyType prop : PropertyType.values()) {
            properties.put(prop, Messages.getString(prefix + "." + prop));
        }
    }

    public ExternalizedStringProperties(final Map<PropertyType, String> propertiesNames) {
        properties = new EnumMap<PropertyType, String>(PropertyType.class);
        for (final Entry<PropertyType, String> entry : propertiesNames.entrySet()) {
            properties.put(entry.getKey(), Messages.getString(entry.getValue()));
        }
    }

    @Override
    public StringProperties set(final PropertyType propType, final String value) {
        properties.put(propType, value);
        return this;
    }

    @Override
    public String get(final PropertyType propType) {
        return properties.get(propType);
    }

    /**
     * Factory method
     * 
     * @param textPropString
     *            property name for text
     * @param tooltipPropString
     *            property name for tooltip
     * @return instance of {@link ExternalizedStringProperties}
     */
    @SuppressWarnings("serial")
    public static StringProperties create(final String textPropString,
            final String tooltipPropString) {
        return new ExternalizedStringProperties(new EnumMap<PropertyType, String>(
                PropertyType.class) {
            {
                put(PropertyType.TEXT, textPropString);
                put(PropertyType.TOOLTIP, tooltipPropString);
            }
        });
    }

    public void initializeAction(IAction action) {
        action.setText(get(PropertyType.TEXT));
        action.setToolTipText(get(PropertyType.TOOLTIP));
        action.setImageDescriptor(Images.getImageRegistry().getDescriptor(get(PropertyType.IMAGE)));

    }

}
