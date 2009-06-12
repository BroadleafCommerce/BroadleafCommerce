////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import mx.core.FlexVersion;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/IconColorStyles.as"

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="emphasized", kind="property")]
[Exclude(name="toggle", kind="property")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.CheckBoxAccImpl")]

[DefaultBindingProperty(source="selected", destination="selected")]

[DefaultTriggerEvent("click")]

[IconFile("CheckBox.png")]

/**
 *  The CheckBox control consists of an optional label and a small box
 *  that can contain a check mark or not. 
 *  You can place the optional text label to the left, right, top, or bottom
 *  of the CheckBox.
 *  When a user clicks a CheckBox control or its associated text,
 *  the CheckBox control changes its state
 *  from checked to unchecked or from unchecked to checked.
 *  CheckBox controls gather a set of true or false values
 *  that are not mutually exclusive.
 *
 *  <p>The CheckBox control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>A size large enough to hold the label</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>No limit</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:CheckBox&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:CheckBox
 *    <strong>Styles</strong>
 *    disabledIconColor="0x999999"
 *    iconColor="0x2B333C"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/CheckBoxExample.mxml
 */
public class CheckBox extends Button
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by CheckBoxAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function CheckBox()
    {
        super();

        // Button variables.
        _toggle = true;
        
        // Used for the old padding logic
        centerContent = false;
        extraSpacing = 8;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  emphasized
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  A CheckBox doesn't have an emphasized state, so _emphasized
     *  is set false in the constructor and can't be changed via this setter.
     */
    override public function set emphasized(value:Boolean):void
    {
    }

    //----------------------------------
    //  toggle
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  A CheckBox is always toggleable by definition, so _toggle is set
     *  true in the constructor and can't be changed via this setter.
     */
    override public function set toggle(value:Boolean):void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (CheckBox.createAccessibilityImplementation != null)
            CheckBox.createAccessibilityImplementation(this);
    }
    
    /**
     *  @private
     *  Returns the height that will accomodate the text and icon.
     */
    override protected function measure():void
    {
        super.measure();

        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            var textHeight:Number = measureText(label).height;
            var iconHeight:Number = currentIcon ? currentIcon.height : 0;
    
            var h:Number = 0;
    
            if (labelPlacement == ButtonLabelPlacement.LEFT ||
                labelPlacement == ButtonLabelPlacement.RIGHT)
            {
                h = Math.max(textHeight, iconHeight);
            }
            else
            {
                h = textHeight + iconHeight;
    
                var verticalGap:Number = getStyle("verticalGap");
                if (iconHeight != 0 && !isNaN(verticalGap))
                    h += verticalGap;
            }
    
            measuredMinHeight = measuredHeight = Math.max(h, 18);
        }
    }
}

}
