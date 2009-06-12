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

package mx.containers
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import mx.controls.Button;
import mx.controls.TabBar;
import mx.core.Container;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.IInvalidating;
import mx.core.IProgrammaticSkin;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.events.ItemClickEvent;
import mx.managers.IFocusManagerComponent;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

// The fill related styles are applied to the children
// of the TabNavigator, ie: the TabBar
include "../styles/metadata/FillStyles.as"

// The focus styles are applied to the TabNavigator itself.
include "../styles/metadata/FocusStyles.as"

/**
 *  Name of CSS style declaration that specifies styles for the first tab.
 *  If this is unspecified, the default value
 *  of the <code>tabStyleName</code> style property is used.
 */
[Style(name="firstTabStyleName", type="String", inherit="no")]

/**
 *  Horizontal positioning of tabs at the top of this TabNavigator container.
 *  The possible values are <code>"left"</code>, <code>"center"</code>,
 *  and <code>"right"</code>.
 *  The default value is <code>"left"</code>.
 *
 *  <p>If the value is <code>"left"</code>, the left edge of the first tab
 *  is aligned with the left edge of the TabNavigator container.
 *  If the value is <code>"right"</code>, the right edge of the last tab
 *  is aligned with the right edge of the TabNavigator container.
 *  If the value is <code>"center"</code>, the tabs are centered on the top
 *  of the TabNavigator container.</p>
 *
 *  <p>To see a difference between the alignments,
 *  the total width of all the tabs must be less than
 *  the width of the TabNavigator container.</p>
 */
[Style(name="horizontalAlign", type="String", enumeration="left,center,right", inherit="no")]

/**
 *  Separation between tabs, in pixels.
 *  The default value is -1, so that the borders of adjacent tabs overlap.
 */
[Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

/**
 *  Name of CSS style declaration that specifies styles for the last tab.
 *  If this is unspecified, the default value
 *  of the <code>tabStyleName</code> style property is used.
 */
[Style(name="lastTabStyleName", type="String", inherit="no")]

/**
 *  Name of CSS style declaration that specifies styles for the text
 *  of the selected tab.
 */
[Style(name="selectedTabTextStyleName", type="String", inherit="no")]

/**
 *  Height of each tab, in pixels.
 *  The default value is <code>undefined</code>.
 *  When this property is <code>undefined</code>, the height of each tab is
 *  determined by the font styles applied to this TabNavigator container.
 *  If you set this property, the specified value overrides this calculation.
 */
[Style(name="tabHeight", type="Number", format="Length", inherit="no")]

/**
 *  Name of CSS style declaration that specifies styles for the tabs.
 *  
 *  @default undefined
 */
[Style(name="tabStyleName", type="String", inherit="no")]

/**
 *  Width of each tab, in pixels.
 *  The default value is <code>undefined</code>.
 *  When this property is <code>undefined</code>, the width of each tab is
 *  determined by the width of its label text, using the font styles applied
 *  to this TabNavigator container.
 *  If the total width of the tabs would be greater than the width of the
 *  TabNavigator container, the calculated tab width is decreased, but
 *  only to a minimum of 30 pixels.
 *  If you set this property, the specified value overrides this calculation.
 *
 *  <p>The label text on a tab is truncated if it does not fit in the tab.
 *  If a tab label is truncated, a tooltip with the full label text is
 *  displayed when a user rolls the mouse over the tab.</p>
 */
[Style(name="tabWidth", type="Number", format="Length", inherit="no")]

/**
 *  The horizontal offset, in pixels, of the tab bar from the left edge 
 *  of the TabNavigator container. 
 *  A positive value moves the tab bar to the right. A negative
 *  value move the tab bar to the left. 
 * 
 *  @default 0 
 */
[Style(name="tabOffset", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="defaultButton", kind="property")]
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

[Exclude(name="fillAlphas", kind="style")]
[Exclude(name="fillColors", kind="style")]
[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("TabNavigator.png")]

/**
 *  The TabNavigator container extends the ViewStack container by including
 *  a TabBar container for navigating between its child containers.
 *
 *  <p>Like a ViewStack container, a TabNavigator container has a collection
 *  of child containers, in which only one child at a time is visible.
 *  Flex automatically creates a TabBar container at the top of the
 *  TabNavigator container, with a tab corresponding to each child container.
 *  Each tab can have its own label and icon.
 *  When the user clicks a tab, the corresponding child container becomes
 *  visible as the selected child of the TabNavigator container.</p>
 *
 *  <p>When you change the currently visible child container,
 *  you can use the <code>hideEffect</code> property of the container being
 *  hidden and the <code>showEffect</code> property of the newly visible child
 *  container to apply an effect to the child containers.
 *  The TabNavigator container waits for the <code>hideEffect</code> of the
 *  child container being hidden to complete before it reveals the new child
 *  container.
 *  You can interrupt a currently playing effect if you change the
 *  <code>selectedIndex</code> property of the TabNavigator container
 *  while an effect is playing. </p>
 *  
 *  <p>To define the appearance of tabs in a TabNavigator, you can define style properties in a 
 *  Tab type selector, as the following example shows:</p>
 *  <pre>
 *  &lt;mx:Style&gt;
 *    Tab {
 *       fillColors: #006699, #cccc66;
 *       upSkin: ClassReference("CustomSkinClass");
 *       overSkin: ClassReference("CustomSkinClass");
 *       downSkin: ClassReference("CustomSkinClass");
 *    }  
 *  &lt;/mx:Style&gt;
 *  </pre>
 * 
 *  <p>The Tab type selector defines values on the hidden mx.controls.tabBarClasses.Tab 
 *  class. The default values for the Tab type selector are defined in the 
 *  defaults.css file.</p>
 * 
 *  <p>You can also define the styles in a class selector that you specify using 
 *  the <code>tabStyleName</code> style property; for example:</p>
 *  <pre>
 *  &lt;mx:Style&gt;
 *    TabNavigator {
 *       tabStyleName:myTabStyle;
 *    }
 *
 *    .myTabStyle {
 *       fillColors: #006699, #cccc66;
 *       upSkin: ClassReference("CustomSkinClass");
 *       overSkin: ClassReference("CustomSkinClass");
 *       downSkin: ClassReference("CustomSkinClass");
 *    }
 *  &lt;/mx:Style&gt;
 *  </pre>
 *
 *  <p>A TabNavigator container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>The default or explicit width and height of the first active child 
 *               plus the tabs, at their default or explicit heights and widths. 
 *               Default tab height is determined by the font, style, and skin applied 
 *               to the TabNavigator container.</td>
 *        </tr>
 *        <tr>
 *           <td>Container resizing rules</td>
 *           <td>By default, TabNavigator containers are only sized once to fit the size 
 *               of the first child container. They do not resize when you navigate to 
 *               other child containers. To force TabNavigator containers to resize when 
 *               you navigate to a different child container, set the resizeToContent 
 *               property to true.</td>
 *        </tr>
 *        <tr>
 *           <td>Child layout rules</td>
 *           <td>If the child is larger than the TabNavigator container, it is clipped. If 
 *               the child is smaller than the TabNavigator container, it is aligned to 
 *               the upper-left corner of the TabNavigator container.</td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>0 pixels for the top, bottom, left, and right values.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:TabNavigator&gt;</code> tag inherits all of the
 *  tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:TabNavigator
 *    <b>Styles</b>
 *    fillAlphas="[0.60, 0.40, 0.75, 0.65]"
 *    fillColors="[0xFFFFFF, 0xCCCCCC, 0xFFFFFF, 0xEEEEEE]"
 *    firstTabStyleName="<i>Value of the</i> <code>tabStyleName</code> <i>property</i>"
 *    focusAlpha="0.4"
 *    focusRoundedCorners="tl tr bl br"
 *    horizontalAlign="left|center|right"
 *    horizontalGap="-1"
 *    lastTabStyleName="<i>Value of the</i> <code>tabStyleName</code> <i>property</i>"
 *    selectedTabTextStyleName="undefined"
 *    tabHeight="undefined"
 *    tabOffset="0"
 *    tabStyleName="<i>Name of CSS style declaration that specifies styles for the tabs</i>"
 *    tabWidth="undefined"
 *    &gt;
 *      ...
 *      <i>child tags</i>
 *      ...
 *  &lt;/mx:TabNavigator&gt;
 *  </pre>
 *
 *  @includeExample examples/TabNavigatorExample.mxml
 *
 *  @see mx.containers.ViewStack
 *  @see mx.controls.TabBar
 */
public class TabNavigator extends ViewStack implements IFocusManagerComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const MIN_TAB_WIDTH:Number = 30;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function TabNavigator()
    {
        super();

        // Most views can't take focus, but a TabNavigator can.
        // Container.init() has set tabEnabled false, so we
        // have to set it back to true.
        tabEnabled = true;

        historyManagementEnabled = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of a TabNavigator is calculated
	 *  for the label of the first tab.
  	 *  If there are no children, a child is temporarily added
  	 *  to do the computation.
     */
    override public function get baselinePosition():Number
    {
    	if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
    		return super.baselinePosition;
	    
		if (!validateBaselinePosition())
			return NaN;

	    var isEmpty:Boolean = numChildren == 0;
	    if (isEmpty)
	    {
	    	var child0:Container = new Container();
	    	addChild(child0);
	    	validateNow();
	    }
	    
	    var tab0:Button = getTabAt(0);
	    var result:Number = tabBar.y + tab0.y + tab0.baselinePosition;
	    
	    if (isEmpty)
	    {
	   		removeChildAt(0);
	   		validateNow();
	    }
	    
	    return result;
    }

    //----------------------------------
    //  contentHeight
    //----------------------------------

    /**
     *  @private
     */
    override protected function get contentHeight():Number
    {
        var vm:EdgeMetrics = viewMetricsAndPadding;

        var vmTop:Number = vm.top;
        var vmBottom:Number = vm.bottom;

        if (isNaN(vmTop))
            vmTop = 0;
        if (isNaN(vmBottom))
            vmBottom = 0;

        return unscaledHeight - tabBarHeight - vmTop - vmBottom;
    }

    //----------------------------------
    //  contentY
    //----------------------------------

    /**
     *  @private
     */
    override protected function get contentY():Number
    {
        var paddingTop:Number = getStyle("paddingTop");

        if (isNaN(paddingTop))
            paddingTop = 0;

        return tabBarHeight + paddingTop;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  tabBarHeight
    //----------------------------------

    /**
     *  A reference to the TabBar inside this TabNavigator.
     */
    protected var tabBar:TabBar;

    //----------------------------------
    //  tabBarHeight
    //----------------------------------

    /**
     *  @private
     *  Height of the tab.
     */
    private function get tabBarHeight():Number
    {
        var tabHeight:Number = getStyle("tabHeight");

        if (isNaN(tabHeight))
            tabHeight = tabBar.getExplicitOrMeasuredHeight();

        return tabHeight - borderMetrics.top;
    }

	//----------------------------------
    //  tabBarStyleFilters
    //----------------------------------

    /**
     *  The set of styles to pass from the TabNavigator to the tabBar.
     *  @see mx.styles.StyleProxy
     *  @review
     */
    protected function get tabBarStyleFilters():Object
    {
    	return _tabBarStyleFilters;
    }
    
    private static var _tabBarStyleFilters:Object =
    {
    	"firstTabStyleName" : "firstTabStyleName",
    	"horizontalAlign" : "horizontalAlign",
    	"horizontalGap" : "horizontalGap",
    	"lastTabStyleName" : "lastTabStyleName",
    	"selectedTabTextStyleName" : "selectedTabTextStyleName",
    	"tabStyleName" : "tabStyleName",
    	"tabWidth" : "tabWidth",
    	"verticalAlign" : "verticalAlign",
    	"verticalGap" : "verticalGap"
    }; 

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!tabBar)
        {
            tabBar = new TabBar();
            tabBar.name = "tabBar";
            tabBar.focusEnabled = false;
            tabBar.styleName = new StyleProxy(this, tabBarStyleFilters);
            rawChildren.addChild(tabBar);
            
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            {
            	tabBar.setStyle("paddingTop", 0);
            	tabBar.setStyle("paddingBottom", 0);
				tabBar.setStyle("borderStyle", "none");         	
            }
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        // Things get a bit tricky here... we need to
        // wait until our children have been instantiated
        // before we can attach the tab bar to us.
        if (tabBar && tabBar.dataProvider != this &&
            numChildren > 0 && getChildAt(0))
        {
            tabBar.dataProvider = this;
        }
    }

    /**
     *  Calculates the default sizes and mininum and maximum values of this
     *  TabNavigator container.
     *  See the <code>UIComponent.measure()</code> method for more information
     *  about the <code>measure()</code> method.
     *
     *  <p>The TabNavigator container uses the same measurement logic as the
     *  <code>ViewStack</code> container, with two modifications:
     *  First, it increases the value of the
     *  <code>measuredHeight</code> and
     *  <code>measuredMinHeight</code> properties to accomodate the tabs.
     *  Second, it increases the value of the
     *  <code>measuredWidth</code> property if necessary
     *  to ensure that each tab can be at least 30 pixels wide.</p>
     * 
     *  @see mx.core.UIComponent#measure()
     *  @see mx.containers.ViewStack#measure()
     */
    override protected function measure():void
    {
        // Only measure once. Thereafter, we'll just use cached values.
        // We need to copy the cached values into the measured fields
        // again to handle the case where scaleX or scaleY is not 1.0.
        // When the TabNavigator is zoomed, code in UIComponent.measureSizes
        // scales the measuredWidth/Height values every time that
        // measureSizes is called.  (bug 100749)

        // This must be done before the call to super.measure(), otherwise
        // we don't get the first measurement correct.
        if (vsPreferredWidth && !resizeToContent)
        {
            measuredMinWidth = vsMinWidth;
            measuredMinHeight = vsMinHeight;
            measuredWidth = vsPreferredWidth;
            measuredHeight = vsPreferredHeight;
            return;
        }

        super.measure();

        var addedHeight:Number = tabBarHeight;
        measuredMinHeight += addedHeight;
        measuredHeight += addedHeight;

        // Make sure there is at least enough room
        // to draw all tabs at their minimum size.
        var tabWidth:Number = getStyle("tabWidth");
        if (isNaN(tabWidth))
            tabWidth = 0;

        var minTabBarWidth:Number =
            numChildren * Math.max(tabWidth, MIN_TAB_WIDTH);

        // Add view metrics.
        var vm:EdgeMetrics = viewMetrics;
        minTabBarWidth += (vm.left + vm.right);

        // Add horizontal gaps.
        if (numChildren > 1)
            minTabBarWidth += (getStyle("horizontalGap") * (numChildren - 1));

        if (measuredWidth < minTabBarWidth)
            measuredWidth = minTabBarWidth;

        // If we're called before instantiateSelectedChild, then bail.
        // We'll be called again later (instantiateSelectedChild calls
        // invalidateSize), and we don't want to load values into the
        // cache until we're fully initialized.  (bug 102639)
        if (selectedChild && Container(selectedChild).numChildrenCreated == -1)
            return;

        // Don't remember sizes if we don't have any children
        if (numChildren == 0)
            return;

        vsMinWidth = measuredMinWidth;
        vsMinHeight = measuredMinHeight;
        vsPreferredWidth = measuredWidth;
        vsPreferredHeight = measuredHeight;
    }

    /**
     *  Responds to size changes by setting the positions and sizes
     *  of this container's tabs and children.
     *
     *  For more information about the <code>updateDisplayList()</code> method,
     *  see the <code>UIComponent.updateDisplayList()</code> method.
     *
     *  <p>A TabNavigator container positions its TabBar container at the top.
     *  The width of the TabBar is set to the width of the
     *  TabNavigator, and the height of the TabBar is set
     *  based on the <code>tabHeight</code> property.</p>
     *
     *  <p>A TabNavigator container positions and sizes its child containers
     *  underneath the TabBar, using the same logic as in
     *  ViewStack container.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     * 
     *  @see mx.core.UIComponent#updateDisplayList()
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var bm:EdgeMetrics = borderMetrics;
        var vm:EdgeMetrics = viewMetrics;
        var w:Number = unscaledWidth - vm.left - vm.right;

        var th:Number = tabBarHeight + bm.top;
        var pw:Number = tabBar.getExplicitOrMeasuredWidth();
        tabBar.setActualSize(Math.min(w, pw), th);
        var leftOffset:Number = getStyle("tabOffset");

        switch (getStyle("horizontalAlign"))
        {
        case "left":
            tabBar.move(0 + leftOffset, tabBar.y);
            break;
        case "right":
            tabBar.move(unscaledWidth - tabBar.width + leftOffset, tabBar.y);
            break;
        case "center":
            tabBar.move((unscaledWidth - tabBar.width) / 2 + leftOffset, tabBar.y);
        }
    }

    /**
     *  @private
     */
    override public function drawFocus(isFocused:Boolean):void
    {
        // Superclass sets up standard focus glow.
        super.drawFocus(isFocused);

        if (!parent)
            return;
            
        // Clip the glow so it doesn't include the tabs
        var focusObj:DisplayObject = IUIComponent(parent).focusPane;
        if (isFocused && !isEffectStarted)
        {
            // Normally the focus skin is in front of the object. For TabNavigator
            // we want it behind.
            if (focusObj)
            {
                if (parent is Container)
                {
                    var n:int = Container(parent).rawChildren.numChildren;
                    var fci:int = Container(parent).firstChildIndex;
                    // make sure we don't set it past the last index.  This happens
                    // if all content children are in a contentpane
                    Container(parent).rawChildren.setChildIndex(
                        focusObj, Math.max(0, (fci == n) ? n - 1 : fci));
                }
                else
                {
                    parent.setChildIndex(focusObj, 0);
                }
            }
        }
        else
        {
            if (focusObj)
            {
                // Move the focus skin back in front of the children, where it
                // was before we drew focus.
                if (parent is Container)
                {
                    Container(parent).rawChildren.setChildIndex(
                            focusObj, Container(parent).rawChildren.numChildren - 1);
                }
                else
                {
                    parent.setChildIndex(focusObj, parent.numChildren - 1);
                }
            }
        }

        tabBar.drawFocus(isFocused);
    }

    /**
     *  @private
     */
    override protected function adjustFocusRect(
                                    object:DisplayObject = null):void
    {
        // Superclass does most of the work
        super.adjustFocusRect(object);

        // Adjust the focus rect so it is below the tabs
        var focusObj:IFlexDisplayObject = IFlexDisplayObject(getFocusObject());

        if (focusObj)
        {
            focusObj.setActualSize(focusObj.width, focusObj.height - tabBarHeight);
            focusObj.move(focusObj.x, focusObj.y + tabBarHeight);

            if (focusObj is IInvalidating)
                IInvalidating(focusObj).validateNow();

            else if (focusObj is IProgrammaticSkin)
                IProgrammaticSkin(focusObj).validateNow();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: Container
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function layoutChrome(unscaledWidth:Number,
                                             unscaledHeight:Number):void
    {
        super.layoutChrome(unscaledWidth, unscaledHeight);

        // Move our border so it leaves room for the tabs
        if (border)
        {
            var borderOffset:Number = tabBarHeight;
            border.setActualSize(unscaledWidth, unscaledHeight - borderOffset);
            border.move(0, borderOffset);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the tab of the navigator's TabBar control at the specified
     *  index.
     *
     *  @param index Index in the navigator's TabBar control.
     *
     *  @return The tab at the specified index.
     */
    public function getTabAt(index:int):Button
    {
        return Button(tabBar.getChildAt(index));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        super.focusInHandler(event);
        
        // When the TabNavigator has focus, the Focus Manager
        // should not treat the Enter key as a click on
        // the default pushbutton.
        if (event.target == this)
            focusManager.defaultButtonEnabled = false;
    }

    /**
     *  @private
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        super.focusOutHandler(event);

        if (focusManager && event.target == this)
            focusManager.defaultButtonEnabled = true;
    }

    import flash.ui.Keyboard;
    
    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (focusManager.getFocus() == this)
        {
            // Redispatch the event from the TabBar so that it can handle it.
            tabBar.dispatchEvent(event);
        }
    }

    /**
     *  @private
     */
    mx_internal function getTabBar():TabBar
    {
        return tabBar;
    }

}

}
