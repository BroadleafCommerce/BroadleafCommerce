/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.other.domain;

import org.broadleafcommerce.profile.vendor.service.type.ContainerShapeType;

/**
 * An extendible enumeration of container shape types.
 * 
 * @author jfischer
 */
public class OtherContainerShapeType extends ContainerShapeType {

    private static final long serialVersionUID = 1L;

    public static final OtherContainerShapeType CIRCLE  = new OtherContainerShapeType("CIRCLE", "Circle");
    public static final OtherContainerShapeType SQUARE = new OtherContainerShapeType("SQUARE", "Square");
    public static final OtherContainerShapeType RECTANGLE = new OtherContainerShapeType("RECTANGLE", "Rectangle");

    public OtherContainerShapeType() {
        //do nothing
    }

    public OtherContainerShapeType(final String type, final String friendlyType) {
        super(type, friendlyType);
    }

}
