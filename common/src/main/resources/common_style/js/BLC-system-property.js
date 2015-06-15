/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
(function($, BLC) {

    BLC.systemProperty = {
        urlFragmentSeparator : "BLC_PROP:url.fragment.separator"
    }

})(jQuery, BLC);

$(document).ready(function() {

    /**
     * This function is updating the value input field for system properties so
     * that if the property is a boolean then the user has radio buttons instead
     * of just a normal text field.
     */
    function updateValueInput(event) {
        var $this = $(this).is('select') ? $(this) : $('select');
        var $boolean = $('.system-property-value-boolean');
        var $string = $('.system-property-value-string');
        if ($string.is('div') && $boolean.is('div')) {

            var value = $this.val()
            if (value === undefined) {
                return;
            }
            value = value.toLowerCase();

            if (value === "boolean_type") {
                $string.hide();
                $boolean.show();
                $string.find('input').attr('disabled', 'disabled');
                $boolean.find('input').removeAttr('disabled');
            } else {
                $boolean.hide()
                $string.show();
                $boolean.find('input').attr('disabled', 'disabled');
                $string.find('input').removeAttr('disabled');
            }
        }
    }

    updateValueInput();

    // Sets onchange listener so that the input is updated at the right time.
    $('body').on('change', 'select', updateValueInput);

});
