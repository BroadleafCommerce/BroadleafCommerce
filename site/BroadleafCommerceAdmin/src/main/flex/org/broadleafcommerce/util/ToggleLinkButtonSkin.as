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
package org.broadleafcommerce.util
{
	import mx.skins.halo.LinkButtonSkin;

	public class ToggleLinkButtonSkin extends LinkButtonSkin
	{
		public function ToggleLinkButtonSkin()
		{
			super();
		}

        override protected function updateDisplayList(w:Number, h:Number):void {
            super.updateDisplayList(w, h);

            var cornerRadius:Number = getStyle("cornerRadius");
            var rollOverColor:uint = getStyle("rollOverColor");
            var selectionColor:uint = getStyle("selectionColor");

            graphics.clear();

            switch (name) {
                case "upSkin":
                    // Draw invisible shape so we have a hit area.
                    drawRoundRect(
                        0, 0, w, h, cornerRadius,
                        0, 0);
                    break;

                case "selectedUpSkin":
                case "selectedOverSkin":
                case "overSkin":
                    drawRoundRect(
                        0, 0, w, h, cornerRadius,
                        rollOverColor, 1);
                    break;

                case "selectedDownSkin":
                case "downSkin":
                    drawRoundRect(
                        0, 0, w, h, cornerRadius,
                        selectionColor, 1);
                    break;

                case "selectedDisabledSkin":
                case "disabledSkin":
                    // Draw invisible shape so we have a hit area.
                    drawRoundRect(
                        0, 0, w, h, cornerRadius,
                        0, 0);
                    break;
            }
        }
	}
}