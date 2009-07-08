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
package org.broadleafcommerce.util;

import java.math.BigDecimal;

public class UnitOfMeasureUtil {

    public static BigDecimal convertKilogramsToPounds(BigDecimal kilograms) {
        return kilograms.multiply(BigDecimal.valueOf(0.45359237));
    }

    public static BigDecimal convertPoundsToKilograms(BigDecimal pounds) {
        return pounds.multiply(BigDecimal.valueOf(2.20462262185));
    }

    public static BigDecimal convertPoundsToOunces(BigDecimal pounds) {
        return pounds.multiply(BigDecimal.valueOf(16));
    }

    public static BigDecimal convertOuncesToPounds(BigDecimal ounces) {
        return ounces.multiply(BigDecimal.valueOf(0.0625));
    }

    public static BigDecimal convertFeetToMeters(BigDecimal feet) {
        return feet.multiply(BigDecimal.valueOf(0.3048));
    }

    public static BigDecimal convertMetersToFeet(BigDecimal meters) {
        return meters.multiply(BigDecimal.valueOf(3.28084));
    }

    public static BigDecimal convertInchesToFeet(BigDecimal inches) {
        return inches.multiply(BigDecimal.valueOf(0.083333));
    }

    public static BigDecimal convertFeetToInches(BigDecimal feet) {
        return feet.multiply(BigDecimal.valueOf(12));
    }
}
