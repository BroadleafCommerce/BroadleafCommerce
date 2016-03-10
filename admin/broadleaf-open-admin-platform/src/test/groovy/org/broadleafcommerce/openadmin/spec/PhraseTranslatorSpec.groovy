/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.spec

import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator
import org.broadleafcommerce.openadmin.web.rulebuilder.statement.Expression
import org.broadleafcommerce.openadmin.web.rulebuilder.statement.PhraseTranslator
import spock.lang.Specification


/**
 * @author Elbert Bautista (elbertbautista)
 */
class PhraseTranslatorSpec extends Specification {

    def "Boolean test with Map Field Separator"() {
        setup: "Initialize the PhraseTranslator and test data"
        PhraseTranslator translator = new PhraseTranslator()
        String phrase = "MvelHelper.convertField(\"BOOLEAN\",orderItem.?product.?getProductAttributes()[\"myboolean\"])==true"

        when: "PhraseTranslator is executed"
        Expression expression = translator.createExpression(phrase);

        then: "The correct expression is produced"
        expression.field.equals("product.getProductAttributes()---myboolean");
        expression.operator.equals(BLCOperator.EQUALS);
        expression.value.equals("true");
    }

    def "Decimal test with Map Field Separator"() {
        setup: "Initialize the PhraseTranslator and test data"
        PhraseTranslator translator = new PhraseTranslator()
        String phrase = "MvelHelper.convertField(\"DECIMAL\",orderItem.?product.?getProductAttributes()[\"mymoney\"])>0"

        when: "PhraseTranslator is executed"
        Expression expression = translator.createExpression(phrase);

        then: "The correct expression is produced"
        expression.field.equals("product.getProductAttributes()---mymoney");
        expression.operator.equals(BLCOperator.GREATER_THAN);
        expression.value.equals("0");
    }

    def "Legacy CollectionUtils intersection test with Map Field Separator"() {
        setup: "Initialize the PhraseTranslator and test data"
        PhraseTranslator translator = new PhraseTranslator()
        String phrase = "CollectionUtils.intersection(orderItem.?product.?getProductAttributes()[\"myenum\"],[\"test1\",\"test2\"]).size()>0"

        when: "PhraseTranslator is executed"
        Expression expression = translator.createExpression(phrase);

        then: "The correct expression is produced"
        expression.field.equals("product.getProductAttributes()---myenum");
        expression.operator.equals(BLCOperator.COLLECTION_IN);
        expression.value.equals("[\"test1\",\"test2\"]");
    }

    def "Bean Null-Safe CollectionUtils intersection test with Map Field Separator"() {
        setup: "Initialize the PhraseTranslator and test data"
        PhraseTranslator translator = new PhraseTranslator()
        String phrase = "CollectionUtils.intersection(orderItem.?product.?getProductAttributes().?get(\"myenum\").?getValue(),[\"test1\",\"test2\"]).size()>0"

        when: "PhraseTranslator is executed"
        Expression expression = translator.createExpression(phrase);

        then: "The correct expression is produced"
        expression.field.equals("product.getProductAttributes()---myenum");
        expression.operator.equals(BLCOperator.COLLECTION_IN);
        expression.value.equals("[\"test1\",\"test2\"]");
    }

    def "MVELHelper Null-Safe CollectionUtils intersection test with Map Field Separator"() {
        setup: "Initialize the PhraseTranslator and test data"
        PhraseTranslator translator = new PhraseTranslator()
        String phrase = "CollectionUtils.intersection(orderItem.?product.?getProductAttributes().?get(\"myenum\").?value,[\"test1\",\"test2\"]).size()>0"

        when: "PhraseTranslator is executed"
        Expression expression = translator.createExpression(phrase);

        then: "The correct expression is produced"
        expression.field.equals("product.getProductAttributes()---myenum");
        expression.operator.equals(BLCOperator.COLLECTION_IN);
        expression.value.equals("[\"test1\",\"test2\"]");
    }

}