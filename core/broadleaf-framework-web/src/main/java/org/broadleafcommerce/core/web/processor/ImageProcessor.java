/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.web.processor;

import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * A Thymeleaf processor that will add the image name as the default alt text.
 *
 *
 * @author jocanas (Jerry Ocanas)
 */
@Component("blImageProcessor")
public class ImageProcessor extends AbstractElementProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ImageProcessor() {
        super("img");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {

        String src = element.getAttributeValue("src");
        String alt = element.getAttributeValue("alt");

        if(isExpression(src)){
            src = (String) StandardExpressionProcessor.processExpression(arguments, src);
        }
        if(alt != null){
            if(isExpression(alt)){
                alt = parseImgName((String) StandardExpressionProcessor.processExpression(arguments, alt));
            }
        } else {
            alt = parseImgName(src);
        }
        element.setAttribute("src", src);
        element.setAttribute("alt", alt);

        // Replace the <blc:img> node to a normal <img> node
        Element newElement = element.cloneElementNodeWithNewName(element.getParent(), "img", false);
        newElement.setRecomputeProcessorsImmediately(true);
        element.getParent().insertAfter(element, newElement);
        element.getParent().removeChild(element);

        return ProcessorResult.OK;
    }

    protected String parseImgName(String imgName){
        String [] parts = imgName.split("/");
        if(parts.length <= 1) { return imgName; }

        for(String name: parts){
            if(name.contains(".")){
                name = name.split("\\.")[0];
                return name.replace("-", " ");
            }
        }
        return null;
    }

    protected boolean isExpression(String subject){
        if(subject.indexOf("{") > 0 || subject.indexOf("}") > 0){
            return true;
        }
        return false;
    }

}
