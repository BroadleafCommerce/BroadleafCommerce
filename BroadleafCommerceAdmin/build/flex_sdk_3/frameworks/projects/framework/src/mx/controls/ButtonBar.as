////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.EventPhase;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;
import mx.containers.BoxDirection;
import mx.controls.buttonBarClasses.ButtonBarButton;
import mx.core.ClassFactory;
import mx.core.EdgeMetrics;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.events.ChildExistenceChangedEvent;
import mx.events.ItemClickEvent;
import mx.managers.IFocusManagerComponent;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when a user clicks a button.
 *  This event is only dispatched if the <code>dataProvider</code> property
 *  does not refer to a ViewStack container.
 *
 *  @eventType mx.events.ItemClickEvent.ITEM_CLICK
 */
[Event(name="itemClick", type="mx.events.ItemClickEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Height of each button, in pixels.
 *  If undefined, the height of each button is determined by the font styles
 *  applied to the container.
 *  If you set this property, the specified value overrides this calculation.
 */
[Style(name="buttonHeight", type="Number", format="Length", inherit="no")]

/**
 *  Name of CSS style declaration that specifies styles for the buttons.
 */
[Style(name="buttonStyleName", type="String", inherit="no")]

/**
 *  Width of each button, in pixels.
 *  If undefined, the default width of each button is calculated from its label text.
 */
[Style(name="buttonWidth", type="Number", format="Length", inherit="no")]

/**
 *  Name of CSS style declaration that specifies styles for the first button.
 *  If this is unspecified, the default value
 *  of the <code>buttonStyleName</code> style property is used.
 */
[Style(name="firstButtonStyleName", type="String", inherit="no")]

/**
 * Horizontal alignment of all buttons within the ButtonBar. Since individual 
 * buttons stretch to fill the entire ButtonBar, this style is only useful if you
 * use the buttonWidth style and the combined widths of the buttons are less than
 * than the width of the ButtonBar.
 * Possible values are <code>"left"</code>, <code>"center"</code>,
 * and <code>"right"</code>.
 *
 * @default "center"
 */
[Style(name="horizontalAlign", type="String", enumeration="left,center,right", inherit="no")]

/**
 *  Number of pixels between children in the horizontal direction.
 *
 *  @default 0
 */
[Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

/**
 *  Name of CSS style declaration that specifies styles for the last button.
 *  If this is unspecified, the default value
 *  of the <code>buttonStyleName</code> style property is used.
 *//**
 * Vertical alignment of all buttons within the ButtonBar. Since individual 
 * buttons stretch to fill the entire ButtonBar, this style is only useful if you
 * use the buttonHeight style and the combined heights of the buttons are less than
 * than the width of the ButtonBar.
 * Possible values are <code>"top"</code>, <code>"middle"</code>,
 * and <code>"bottom"</code>.
 *
 * @default "middle"
 */
[Style(name="lastButtonStyleName", type="String", inherit="no")]

/**
 * Vertical alignment of all buttons within the ButtonBar. Since individual 
 * buttons stretch to fill the entire ButtonBar, this style is only useful if you
 * use the buttonHeight style and the combined heights of the buttons are less than
 * than the width of the ButtonBar.
 * Possible values are <code>"top"</code>, <code>"middle"</code>,
 * and <code>"bottom"</code>.
 *
 * @default "middle"
 */
[Style(name="verticalAlign", type="String", enumeration="top,middle,bottom", inherit="no")]

/**
 *  Number of pixels between children in the vertical direction.
 *
 *  @default 0
 */
[Style(name="verticalGap", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="horizontalLineScrollSize", kind="property")]
[Exclude(name="horizontalPageScrollSize", kind="property")]
[Exclude(name="horizontalScrollBar", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="horizontalScrollPosition", kind="property")]
[Exclude(name="maxHorizontalScrollPosition", kind="property")]
[Exclude(name="maxVerticalScrollPosition", kind="property")]
[Exclude(name="verticalLineScrollSize", kind="property")]
[Exclude(name="verticalPageScrollSize", kind="property")]
[Exclude(name="verticalScrollBar", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]
[Exclude(name="verticalScrollPosition", kind="property")]

[Exclude(name="scroll", kind="event")]
[Exclude(name="click", kind="event")]

[Exclude(name="backgroundAlpha", kind="style")]
[Exclude(name="backgroundAttachment", kind="style")]
[Exclude(name="backgroundColor", kind="style")]
[Exclude(name="backgroundImage", kind="style")]
[Exclude(name="backgroundSize", kind="style")]
[Exclude(name="borderColor", kind="style")]
[Exclude(name="borderSides", kind="style")]
[Exclude(name="borderSkin", kind="style")]
[Exclude(name="borderStyle", kind="style")]
[Exclude(name="borderThickness", kind="style")]
[Exclude(name="cornerRadius", kind="style")]
[Exclude(name="dropShadowColor", kind="style")]
[Exclude(name="dropShadowEnabled", kind="style")]
[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="shadowCapColor", kind="style")]
[Exclude(name="shadowColor", kind="style")]
[Exclude(name="shadowDirection", kind="style")]
[Exclude(name="shadowDistance", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultProperty("dataProvider")]

[IconFile("ButtonBar.png")]

[MaxChildren(0)]

/**
 *  The ButtonBar control defines a horizontal or vertical group of 
 *  logically related push buttons with a common look and navigation.
 *
 *  <p>A push button is one that does not remember its selected state
 *  when selected.
 *  The typical use for a push button in a button bar is for grouping
 *  a set of related buttons together, which gives them a common look
 *  and navigation, and handling the logic for the <code>click</code> event
 *  in a single place. </p>
 *
 *  <p>The ButtonBar control creates Button controls based on the value of 
 *  its <code>dataProvider</code> property. 
 *  Even though ButtonBar is a subclass of Container, do not use methods such as 
 *  <code>Container.addChild()</code> and <code>Container.removeChild()</code> 
 *  to add or remove Button controls. 
 *  Instead, use methods such as <code>addItem()</code> and <code>removeItem()</code> 
 *  to manipulate the <code>dataProvider</code> property. 
 *  The ButtonBar control automatically adds or removes the necessary children based on 
 *  changes to the <code>dataProvider</code> property.</p>
 *
 *  <p>To control the styling of the buttons of the ButtonBar control, use the 
 *  <code>buttonStyleName</code>, <code>firstButtonStyleName</code>, 
 *  and <code>lastButtonStyleName</code> style properties; 
 *  do not try to style the individual Button controls 
 *  that make up the ButtonBar control.</p>
 *
 *  <p>You can use the ToggleButtonBar control to define a group
 *  of toggle buttons.</p>
 *
 *  <p>ButtonBar control has the following default characteristics:</p>
 *  <table class="innertable">
 *     <tr>
 *        <th>Characteristic</th>
 *        <th>Description</th>
 *     </tr>
 *     <tr>
 *        <td>Preferred size</td>
 *        <td>Wide enough to contain all buttons with their label text and icons, if any, plus any padding and separators, and high enough to accommodate the button height.</td>
 *     </tr>
 *     <tr>
 *        <td>Control resizing rules</td>
 *        <td>The controls do not resize by default. Specify percentage sizes if you want your ButtonBar to resize based on the size of its parent container.</td>
 *     </tr>
 *     <tr>
 *        <td>Padding</td>
 *        <td>0 pixels for the top, bottom, left, and right properties.</td>
 *     </tr>
 *  </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ButtonBar&gt;</code> tag inherits all the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:ButtonBar
 *    <b>Styles</b>
 *    buttonHeight="undefined"
 *    buttonStyleName="<i>Name of CSS style declaration, which specifies
 *    styles for the buttons</i>"
 *    buttonWidth="undefined"
 *    firstButtonStyleName="<i>The value of</i> <code>buttonStyleName</code>"
 *    focusAlpha="0.4"
 *    focusRoundedCorners="tl tr bl br"
 *    horizontalAlign="center|left|right"
 *    horizontalGap="0"
 *    lastButtonStyleName="<i>The value of</i> <code>buttonStyleName</code>"
 *    verticalAlign="middle|top|bottom"
 *    verticalGap="0"
 *     
 *    <b>Events</b>
 *    itemClick="<i>No default</i>"
 *    &gt;
 *    ...
 *       <i>child tags</i>
 *    ...
 *  &lt;/mx:ButtonBar&gt;
 *  </pre>
 *
 *  @see mx.controls.ToggleButtonBar
 *  @see mx.controls.LinkBar
 *  @includeExample examples/ButtonBarExample.mxml
 */
 public class ButtonBar extends NavBar implements IFocusManagerComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function ButtonBar()
    {
        super();

        tabEnabled = true;

        navItemFactory = new ClassFactory(ButtonBarButton);

        // Add event listeners for scaleX/scaleY changed.
        // Since we hard-code sizes into our children,
        // scaling can cause rounding errors so we need
        // to clear our hard-coded values whenever the scale changes.
        addEventListener("scaleXChanged", scaleChangedHandler);
        addEventListener("scaleYChanged", scaleChangedHandler);
        
        addEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, 
                         childRemoveHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Internal flag to indicate when a click event has been triggered
     *  programmatically, as opposed to an actual user click.
     *  This happens when the button selection happens by keyboard navigation
     *  or when selectedIndex is set programmatically.
     *  When this is true, the focus rect shouldn't be drawn
     *  for the currently selected button.
     */
    mx_internal var simulatedClickTriggerEvent:Event = null;

    /**
     *  @private
     *  Name of style used to specify buttonStyleName.
     *  Overridden by TabBar.
     */
    mx_internal var buttonStyleNameProp:String = "buttonStyleName";

    /**
     *  @private
     *  Name of style used to specify buttonStyleName.
     *  Overridden by TabBar.
     */
    mx_internal var firstButtonStyleNameProp:String = "firstButtonStyleName";

    /**
     *  @private
     *  Name of style used to specify buttonStyleName.
     *  Overridden by TabBar.
     */
    mx_internal var lastButtonStyleNameProp:String = "lastButtonStyleName";

    /**
     *  @private
     *  Name of style used to specify buttonWidth.
     *  Overridden by TabBar.
     */
    mx_internal var buttonWidthProp:String = "buttonWidth";

    /**
     *  @private
     *  Name of style used to specify buttonHeight.
     *  Overridden by TabBar.
     */
    mx_internal var buttonHeightProp:String = "buttonHeight";

    /**
     *  @private
     *  Flag indicating whether buttons widths should be recalculated.
     */
    private var recalcButtonWidths:Boolean = false;

    /**
     *  @private
     *  Flag indicating whether buttons heights should be recalculated.
     */
    private var recalcButtonHeights:Boolean = false;

    /**
     *  @private
     *  The value of the unscaledWidth parameter during the most recent
     *  call to updateDisplayList
     */
    private var oldUnscaledWidth:Number;

    /**
     *  @private
     *  The value of the unscaledHeight parameter during the most recent
     *  call to updateDisplayList
     */
    private var oldUnscaledHeight:Number;

    /**
     *  @private
     *  Index of currently focused child.
     */
    mx_internal var focusedIndex:int = 0;

    /**
     *  @private
     *  Flag indicating whether direction has changed.
     */
    private var directionChanged:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  borderMetrics
    //----------------------------------

    /**
     *  @private
     */
    override public function get borderMetrics():EdgeMetrics
    {
        return EdgeMetrics.EMPTY;
    }

    //----------------------------------
    //  direction
    //----------------------------------

    [Bindable("directionChanged")]
    [Inspectable(category="General", enumeration="vertical,horizontal", defaultValue="horizontal")]

    /**
     *  @private
     */
    override public function set direction(value:String):void
    {
        if (initialized && value != direction)
        {
            directionChanged = true;
            invalidateProperties();
        }

        super.direction = value;
    }

    //----------------------------------
    //  viewMetrics
    //----------------------------------

    /**
     *  @private
     */
    override public function get viewMetrics():EdgeMetrics
    {
        return EdgeMetrics.EMPTY;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var allStyles:Boolean = styleProp == null || styleProp == "styleName";

        super.styleChanged(styleProp);

        if (allStyles ||
            styleProp == buttonStyleNameProp ||
            styleProp == firstButtonStyleNameProp ||
            styleProp == lastButtonStyleNameProp)
        {
            var buttonStyleName:String = getStyle(buttonStyleNameProp);
            var firstButtonStyleName:String = getStyle(firstButtonStyleNameProp);
            var lastButtonStyleName:String = getStyle(lastButtonStyleNameProp);
            
            if (!buttonStyleName)
                buttonStyleName = "ButtonBarButton";
            if (!firstButtonStyleName)
                firstButtonStyleName = buttonStyleName;
            if (!lastButtonStyleName)
                lastButtonStyleName = buttonStyleName;
            
            var newStyleName:String;
            
            var n:int = numChildren;
            for (var i:int = 0; i < n; i++)
            {
                if (i == 0)
                    newStyleName = firstButtonStyleName;
                else if (i == (n - 1))
                    newStyleName = lastButtonStyleName;
                else
                    newStyleName = buttonStyleName;
                
                Button(getChildAt(i)).styleName = newStyleName;
            }
            
            recalcButtonWidths = recalcButtonHeights = true;
        }

        if (styleProp == buttonWidthProp)
            recalcButtonWidths = true;
        else if (styleProp == buttonHeightProp)
            recalcButtonHeights = true;
            
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (directionChanged)
        {
            directionChanged = false;

            // refresh skins
            var n:int = numChildren;
            for (var i:int = 0; i < n; i++)
                Button(getChildAt(i)).changeSkins();
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var vm:EdgeMetrics = viewMetricsAndPadding;

        measuredWidth = calcFullWidth() + vm.left + vm.right;
        measuredHeight = calcFullHeight() + vm.top + vm.bottom;

        // If explicit button sizes are specified, our preferred sizes are our
        // minimum sizes.
        if (getStyle(buttonWidthProp))
            measuredMinWidth = measuredWidth;
        if (getStyle(buttonHeightProp))
            measuredMinHeight = measuredHeight;

    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // We pre-process our child sizes, so we call super.updateDisplayList() later.
        
        const isHorizontal:Boolean = (direction == BoxDirection.HORIZONTAL);
        const isVertical:Boolean = !isHorizontal;

        var buttonWidth:Number = getStyle(buttonWidthProp);
        var buttonHeightStyle:Number = getStyle(buttonHeightProp);
        var buttonHeight:Number = buttonHeightStyle;

        var vm:EdgeMetrics = viewMetricsAndPadding;

        var n:int = numChildren;
        var horizontalGap:Number = getStyle("horizontalGap");
        var verticalGap:Number = getStyle("verticalGap");
        var totalHorizontalGap:Number = isHorizontal && numChildren > 0 ? 
        								horizontalGap * (n - 1) : 0;
        var totalVerticalGap:Number = isVertical && numChildren > 0 ? 
        							  verticalGap * (n - 1) : 0;
        var w:Number = unscaledWidth - vm.left - vm.right - totalHorizontalGap;
        var h:Number = unscaledHeight - vm.top - vm.bottom - totalVerticalGap;
        if (!w || !h)
            return;

        if (border)
            border.visible = false;

        if (unscaledWidth != oldUnscaledWidth)
        {
            recalcButtonWidths = true;
            oldUnscaledWidth = unscaledWidth;
        }

        if (unscaledHeight != oldUnscaledHeight)
        {
            recalcButtonHeights = true;
            oldUnscaledHeight = unscaledHeight;
        }

        var i:int;
        var c:Button;

        var excessSpace:Number;

        // See if we need to recalculate the button widths
        if (recalcButtonWidths)
        {
            recalcButtonWidths = false;

            if (isNaN(buttonWidth) && isVertical)
                buttonWidth = w;

            excessSpace = w - (calcFullWidth() - totalHorizontalGap);
            var averageWidth:int = n > 0 ? w / n : 0;
            // Number of children larger than average width
            var nLarge:int = 0;

            // Sum of all preferred widths (required if excessSpace != 0)
            var tw:Number = 0;
            // Sum of all preferred widths of children
            // smaller than average width.
            var tSmall:int = 0;
            if (excessSpace != 0 && isHorizontal)
            {
                for (i = 0; i < n; i++)
                {
                    c = Button(getChildAt(i));
                    if (isNaN(c.explicitWidth))
                    {
                        var mw:int = c.measuredWidth;
                        tw += mw;
                        if (mw > averageWidth)
                            nLarge++;
                        else
                            tSmall += mw;
                    }
                }
            }
            else
            {
                tw = w;
            }

            for (i = 0; i < n; i++)
            {
                c = Button(getChildAt(i));
                if (isNaN(c.explicitWidth))
                {
                    c.minWidth = 0;
                    if (!isNaN(buttonWidth))
                    {
                            c.minWidth = c.maxWidth = buttonWidth;
                            c.percentWidth = buttonWidth / Math.min(w, tw) * 100;
                    }

                    // Assign measured width to children smaller than average.
                    // Distribute the remaining width to others.
                    else if (excessSpace < 0)
                    {
                        var assignedWidth:int = c.measuredWidth;
                        if (assignedWidth > averageWidth)
                            assignedWidth = (w - tSmall) / nLarge;

                        c.percentWidth = Number(assignedWidth) / w * 100;
                    }

                    // If they fit comfortably with extra space left,
                    // expand them.
                    else if (excessSpace > 0)
                    {
                        c.percentWidth = c.measuredWidth / tw * 100;
                    }

                    else
                    {
                        c.percentWidth = NaN;
                    }

                    // If vertical, expand to fit horizontally.
                    if (isVertical)
                        c.percentWidth = 100;
                }
            }
        }

        // See if we need to recalculate the button heights
        if (recalcButtonHeights)
        {
            recalcButtonHeights = false;

            if (isNaN(buttonHeight) && isHorizontal)
                buttonHeight = h;

            excessSpace = h - (calcFullHeight() - totalVerticalGap);

            // Sum of all preferred heights (required if excessSpace != 0).
            var th:Number = 0;
            if (excessSpace != 0 && isVertical)
            {
                for (i = 0; i < n; i++)
                {
                    c = Button(getChildAt(i));
                    if (isNaN(c.explicitHeight))
                        th += c.measuredHeight;
                }
            }

            for (i = 0; i < n; i++)
            {
                c = Button(getChildAt(i));
                if (isNaN(c.explicitHeight))
                {
                    c.minHeight = 0;
                    if (!isNaN(buttonHeight))
                    {
                        c.minHeight = buttonHeight;
                        c.percentHeight = buttonHeight / Math.min(th, h) * 100;
                    }
                    if (!isNaN(buttonHeightStyle))
                        c.maxHeight = buttonHeightStyle;

                    // If horizontal, expand to fit vertically.
                    if (isHorizontal)
                        c.percentHeight = 100;

                    // If they won't fit, squeeze them in.
                    else if (excessSpace < 0)
                        c.percentHeight = c.measuredHeight / th * 100;

                    // If they fit comfortably with extra space left,
                    // expand them.
                    else if (excessSpace > 0)
                        c.percentHeight = c.measuredHeight / th * 100;

                    else
                        c.percentHeight = NaN;
                }
            }
        }
        
        // Since we pre-process our child dimensions, we call super.updateDisplayList()
        // last.
        super.updateDisplayList(unscaledWidth, unscaledHeight);
    }

    /**
     *  @private
     */
    override public function drawFocus(isFocused:Boolean):void
    {
        drawButtonFocus(focusedIndex, isFocused);
    }

    /**
     *  @private
     */
    override protected function createNavItem(
                                        label:String,
                                        icon:Class = null):IFlexDisplayObject
    {
        var newButton:Button = Button(navItemFactory.newInstance());

        // Set tabEnabled to false so individual buttons don't get focus.
        newButton.focusEnabled = false;

        var buttonStyleName:String = getStyle(buttonStyleNameProp);
        var firstButtonStyleName:String = getStyle(firstButtonStyleNameProp);
        var lastButtonStyleName:String = getStyle(lastButtonStyleNameProp);

        if (!buttonStyleName)
            buttonStyleName = "ButtonBarButton";
        if (!firstButtonStyleName)
            firstButtonStyleName = buttonStyleName;
        if (!lastButtonStyleName)
            lastButtonStyleName = buttonStyleName;

        var n:int = numChildren;
        if (n == 0)
        {
            newButton.styleName = buttonStyleName;
        }
        else
        {
            newButton.styleName = lastButtonStyleName;
            var cssStyleDeclaration:CSSStyleDeclaration =
                StyleManager.getStyleDeclaration("." + lastButtonStyleName);

            if (cssStyleDeclaration &&
                !cssStyleDeclaration.getStyle("focusRoundedCorners"))
            {
                newButton.setStyle("focusRoundedCorners", "tr br");
            }

            // Refresh the skins for the last button that was in this position.
            var first:Boolean = (n == 1);
            var lastButton:Button = Button(getChildAt(first ? 0 : n - 1));

            if (first)
            {
                lastButton.styleName = firstButtonStyleName;
                cssStyleDeclaration =
                    StyleManager.getStyleDeclaration("." + firstButtonStyleName);

                if (cssStyleDeclaration &&
                    !cssStyleDeclaration.getStyle("focusRoundedCorners"))
                {
                    lastButton.setStyle("focusRoundedCorners", "tl bl");
                }
            }
            else
            {
                lastButton.styleName = buttonStyleName;
                cssStyleDeclaration =
                    StyleManager.getStyleDeclaration("." + buttonStyleName);

                if (cssStyleDeclaration &&
                    !cssStyleDeclaration.getStyle("focusRoundedCorners"))
                {
                    lastButton.setStyle("focusRoundedCorners", "");
                }
            }

            lastButton.changeSkins();
            lastButton.invalidateDisplayList();
        }

        newButton.label = label;
        newButton.setStyle("icon", icon);

        newButton.addEventListener(MouseEvent.CLICK, clickHandler);

        addChild(newButton);

        recalcButtonWidths = recalcButtonHeights = true;

        return newButton;
    }

    /**
     *  @private
     */
    override protected function resetNavItems():void
    {
        recalcButtonWidths = recalcButtonHeights = true;

        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function calcFullWidth():Number
    {
        var n:int = numChildren;
        var gap:Number = 0;

        if (n == 0)
            return 0;
        else if (n > 1)
            gap = getStyle("horizontalGap");

        var horizontal:Boolean = (direction == BoxDirection.HORIZONTAL);

        var buttonWidth:Number = getStyle(buttonWidthProp);

        var child:IUIComponent = IUIComponent(getChildAt(0));

        var w:Number;
        if (buttonWidth)
            w = isNaN(child.explicitWidth) ? buttonWidth : child.explicitWidth;
        else
            w = child.getExplicitOrMeasuredWidth();

        for (var i:int = 1; i < n; i++)
        {
            child = IUIComponent(getChildAt(i));

            var cw:Number;
            if (buttonWidth)
            {
                cw = isNaN(child.explicitWidth) ?
                     buttonWidth :
                     child.explicitWidth;
            }
            else
            {
                cw = child.getExplicitOrMeasuredWidth();
            }

            if (horizontal)
                w += (gap + cw);
            else
                w = Math.max(w, cw);
        }

        return w;
    }

    /**
     *  @private
     */
    private function calcFullHeight():Number
    {
        var n:int = numChildren;
        var gap:Number;

        if (n == 0)
            return 0;
        else if (n > 1)
            gap = getStyle("verticalGap");

        var vertical:Boolean = (direction == BoxDirection.VERTICAL);

        var buttonHeight:Number = getStyle(buttonHeightProp);

        var child:IUIComponent = IUIComponent(getChildAt(0));

        var h:Number;
        if (buttonHeight)
        {
            h = isNaN(child.explicitHeight) ?
                buttonHeight :
                child.explicitHeight;
        }
        else
        {
            h = child.getExplicitOrMeasuredHeight();
        }

        for (var i:int = 1; i < n; i++)
        {
            child = IUIComponent(getChildAt(i));

            var ch:Number;
            if (buttonHeight)
            {
                ch = isNaN(child.explicitHeight) ?
                     buttonHeight :child.explicitHeight;
            }
            else
            {
                ch = child.getExplicitOrMeasuredHeight();
            }

            if (vertical)
                h += (gap + ch);
            else
                h = Math.max(h, ch);
        }

        return h;
    }

    /**
     *  @private
     *  Returns the previous valid child index, or -1 if there are no children.
     *  Used by keyboard navigation.
     */
    mx_internal function prevIndex(index:int):int
    {
        var n:int = numChildren;
        return index == 0 ? n - 1 : index - 1;
    }

    /**
     *  @private
     *  Returns the next valid child index, or -1 if there are no children.
     *  Used by keyboard navigation.
     */
    mx_internal function nextIndex(index:int):int
    {
        var n:int = numChildren;
        if (n == 0)
            return -1;
        else
            return index == n - 1 ? 0 : index + 1;
    }

    /**
     *  @private
     */
    mx_internal function drawButtonFocus(index:int, focused:Boolean):void
    {
        if (index < numChildren)
        {
            var b:Button = Button(getChildAt(index));
            b.drawFocus(focused && focusManager.showFocusIndicator);

            // internal event for accessibility
            if (focused)
                dispatchEvent(new Event("focusDraw"));

            // If the button is losing focus, set its phase to UP,
            // which will cause a redraw.
            if (!focused && b.phase != ButtonPhase.UP)
                b.phase = ButtonPhase.UP;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        // Ignore events that bubble up from the child ButtonBarButtons.
        // such as the one we redispatch below from the focused child
        // when the SPACE key is released.
        if (event.eventPhase != EventPhase.AT_TARGET)
            return;

        switch (event.keyCode)
        {
            case Keyboard.DOWN:
            case Keyboard.RIGHT:
            {
                focusManager.showFocusIndicator = true;
                drawButtonFocus(focusedIndex, false);
                focusedIndex = nextIndex(focusedIndex);

                if (focusedIndex != -1)
                {
                    drawButtonFocus(focusedIndex, true);
                }

                event.stopPropagation();
                break;
            }

            case Keyboard.UP:
            case Keyboard.LEFT:
            {
                focusManager.showFocusIndicator = true;
                drawButtonFocus(focusedIndex, false);
                focusedIndex = prevIndex(focusedIndex);

                if (focusedIndex != -1)
                {
                    drawButtonFocus(focusedIndex, true);
                }

                event.stopPropagation();
                break;
            }

            case Keyboard.SPACE:
            {
                if (focusedIndex != -1)
                {
                    // Redispatch from the focused ButtonBarButton
                    // to get it to appear pressed.
                    var child:Button = Button(getChildAt(focusedIndex));
                    child.dispatchEvent(event);
                }

                event.stopPropagation();
                break;
            }
        }
    }

    /**
     *  @private
     */
    override protected function keyUpHandler(event:KeyboardEvent):void
    {
        // Ignore events that bubble up from the child ButtonBarButtons.
        // such as the one we redispatch below from the focused child
        // when the SPACE key is released.
        if (event.eventPhase != EventPhase.AT_TARGET)
            return;

        switch (event.keyCode)
        {
            case Keyboard.SPACE:
            {
                if (focusedIndex != -1)
                {
                    // Redispatch from the focused ButtonBarButton
                    // to get it to appear released.
                    var child:Button = Button(getChildAt(focusedIndex));
                    child.dispatchEvent(event);
                }

                event.stopPropagation();
                break;
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: NavBar
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function clickHandler(event:MouseEvent):void
    {
        if (simulatedClickTriggerEvent == null)
        {
            focusedIndex = getChildIndex(DisplayObject(event.currentTarget));
            drawButtonFocus(focusedIndex, true);
        }

        super.clickHandler(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function childRemoveHandler(event:ChildExistenceChangedEvent):void
    {   
        var child:DisplayObject = event.relatedObject;
        var index:int = getChildIndex(child);
        var n:int = numChildren;
        if (n < 2)
        {
            // Don't bother if it's the last child.
            return;
        }

        var buttonStyleName:String = getStyle(buttonStyleNameProp);
        var firstButtonStyleName:String = getStyle(firstButtonStyleNameProp);
        var lastButtonStyleName:String = getStyle(lastButtonStyleNameProp);

        if (!buttonStyleName)
            buttonStyleName = "buttonBarButtonStyle";
        if (!firstButtonStyleName)
            firstButtonStyleName = buttonStyleName;
        if (!lastButtonStyleName)
            lastButtonStyleName = buttonStyleName;

        // Refresh the skins for the last button that was in this position.
        if (index == 0 || index == n - 1)
        {
            var button:Button = Button(getChildAt(index == n - 1 ? n - 2 : 0));
            
            button.styleName = index == 0 ?
                               firstButtonStyleName :
                               lastButtonStyleName;
            
            button.changeSkins();
            button.invalidateDisplayList();
        }
    }

    /**
     *  @private
     */
    private function scaleChangedHandler(event:Event):void
    {
        // This is called whenever scaleX or scaleY is changed.
        // We need to clear out the preferredWidth/preferredHeight
        // of our children since scaling can cause rounding errors
        // which then are not corrected when un-scaled.

        for (var i:int = 0; i < numChildren; i++)
        {
            var child:Button = getChildAt(i) as Button;
            if (child)
            {
                child.explicitWidth = NaN;
                child.minWidth = NaN;
                child.maxWidth = NaN;

                child.explicitHeight = NaN;
                child.minHeight = NaN;
                child.maxHeight = NaN;
            }
        }
    }
}

}
