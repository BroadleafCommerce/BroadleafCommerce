/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.util.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  This class allows us to round Big Decimals to 2 decimal places, generally for
 *  marshalling purposes.  This is to be used with <code>javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters</code>.
 */
public class BigDecimalRoundingAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public BigDecimal unmarshal(String s) throws Exception {
        return new BigDecimal(s);
    }

    @Override
    public String marshal(BigDecimal bigDecimal) throws Exception {
        return bigDecimal.setScale(2, RoundingMode.UP).toString();
    }
}
