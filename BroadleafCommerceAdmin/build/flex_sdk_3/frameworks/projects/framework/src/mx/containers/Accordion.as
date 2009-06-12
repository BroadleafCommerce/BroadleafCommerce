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
import flash.display.DisplayObjectContainer;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Rectangle;
import flash.ui.Keyboard;
import flash.utils.getTimer;
import mx.automation.IAutomationObject;
import mx.containers.accordionClasses.AccordionHeader;
import mx.controls.Button;
import mx.core.ClassFactory;
import mx.core.ComponentDescriptor;
import mx.core.Container;
import mx.core.ContainerCreationPolicy;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFactory;
import mx.managers.IFocusManagerComponent;
import mx.managers.IHistoryManagerClient;
import mx.core.IInvalidating;
import mx.core.IUIComponent;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.effects.Effect;
import mx.effects.Tween;
import mx.events.ChildExistenceChangedEvent;
import mx.events.FlexEvent;
import mx.events.IndexChangedEvent;
import mx.graphics.RoundedRectangle;
import mx.managers.HistoryManager;
import mx.styles.StyleManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleProxy;

use namespace mx_internal;

[RequiresDataBinding(true)]

[IconFile("Accordion.png")]

/**
 *  Dispatched when the selected child container changes.
 *
 *  @eventType mx.events.IndexChangedEvent.CHANGE
 *  @helpid 3012
 *  @tiptext change event
 */
[Event(name="change", type="mx.events.IndexChangedEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Specifies the alpha transparency values used for the background fill of components.
 *  You should set this to an Array of either two or four numbers.
 *  Elements 0 and 1 specify the start and end values for
 *  an alpha gradient.
 *  If elements 2 and 3 exist, they are used instead of elements 0 and 1
 *  when the component is in a mouse-over state.
 *  The global default value is <code>[ 0.60, 0.40, 0.75, 0.65 ]</code>.
 *  Some components, such as the ApplicationControlBar container,
 *  have a different default value. For the ApplicationControlBar container, 
 *  the default value is <code>[ 0.0, 0.0 ]</code>.
 */
[Style(name="fillAlphas", type="Array", arrayType="Number", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Specifies the colors used to tint the background fill of the component.
 *  You should set this to an Array of either two or four uint values
 *  that specify RGB colors.
 *  Elements 0 and 1 specify the start and end values for
 *  a color gradient.
 *  If elements 2 and 3 exist, they are used instead of elements 0 and 1
 *  when the component is in a mouse-over state.
 *  For a flat-looking control, set the same color for elements 0 and 1
 *  and for elements 2 and 3,
 *  The default value is
 *  <code>[ 0xFFFFFF, 0xCCCCCC, 0xFFFFFF, 0xEEEEEE ]</code>.
 *  <p>Some components, such as the ApplicationControlBar container,
 *  have a different default value. For the ApplicationControlBar container, 
 *  the default value is <code>[ 0xFFFFFF, 0xFFFFFF ]</code>.</p>
 */
[Style(name="fillColors", type="Array", arrayType="uint", format="Color", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Specifies the alpha transparency value of the focus skin.
 *  
 *  @default 0.4
 */
[Style(name="focusAlpha", type="Number", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Specifies which corners of the focus rectangle should be rounded.
 *  This value is a space-separated String that can contain any
 *  combination of <code>"tl"</code>, <code>"tr"</code>, <code>"bl"</code>
 *  and <code>"br"</code>.
 *  For example, to specify that the right side corners should be rounded,
 *  but the left side corners should be square, use <code>"tr br"</code>.
 *  The <code>cornerRadius</code> style property specifies
 *  the radius of the rounded corners.
 *  The default value depends on the component class; if not overridden for
 *  the class, default value is <code>"tl tr bl br"</code>.
 * 
 */
[Style(name="focusRoundedCorners", type="String", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Skin used to draw the focus rectangle.
 *
 *  @default mx.skins.halo.HaloFocusRect
 */
[Style(name="focusSkin", type="Class", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Thickness, in pixels, of the focus rectangle outline.
 *
 *  @default 2
 */
[Style(name="focusThickness", type="Number", format="Length", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Name of the CSS style declaration that specifies styles for the accordion
 *  headers (tabs).
 * 
 *  <p>You can use this class selector to set the values of all the style properties 
 *  of the AccordionHeader class, including <code>fillAlphas</code>, <code>fillColors</code>, 
 *  <code>focusAlpha</code>, <code>focusRounderCorners</code>, 
 *  <code>focusSkin</code>, <code>focusThickness</code>, and <code>selectedFillColors</code>.</p>
 */
[Style(name="headerStyleName", type="String", inherit="no")]

/**
 *  Number of pixels between children in the horizontal direction.
 *  The default value is 8.
 */
[Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

/**
 *  Height of each accordion header, in pixels.
 *  The default value is automatically calculated based on the font styles for
 *  the header.
 */
[Style(name="headerHeight", type="Number", format="Length", inherit="no")]

/**
 *  Duration, in milliseconds, of the animation from one child to another.
 *  The default value is 250.
 */
[Style(name="openDuration", type="Number", format="Time", inherit="no")]

/**
 *  Tweening function used by the animation from one child to another.
 */
[Style(name="openEasingFunction", type="Function", inherit="no")]

/**
 *  Number of pixels between the container's bottom border and its content area.
 *  The default value is -1, so the bottom border of the last header
 *  overlaps the Accordion container's bottom border.
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the container's top border and its content area.
 *  The default value is -1, so the top border of the first header
 *  overlaps the Accordion container's top border.
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

/**
 *  The two colors used to tint the background of the component
 *  when in its selected state.
 *  Pass the same color for both values for "flat" looking control.
 *  The default value is <code>undefined</code>, which means the colors
 *  are derived from <code>themeColor</code>.
 */
[Style(name="selectedFillColors", type="Array", arrayType="uint", format="Color", inherit="no", deprecatedReplacement="headerStyleName", deprecatedSince="3.0")]

/**
 *  Color of header text when rolled over.
 *  The default value is 0x2B333C.
 */
[Style(name="textRollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Color of selected text.
 *  The default value is 0x2B333C.
 */
[Style(name="textSelectedColor", type="uint", format="Color", inherit="yes")]

/**
 *  Number of pixels between children in the vertical direction.
 *  The default value is -1, so the top and bottom borders
 *  of adjacent headers overlap.
 */
[Style(name="verticalGap", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="autoLayout", kind="property")]
[Exclude(name="clipContent", kind="property")]
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

/*
[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]
*/
[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="selectedIndex", destination="selectedIndex")]

[DefaultTriggerEvent("change")]

/**
 *  An Accordion navigator container has a collection of child containers,
 *  but only one of them at a time is visible.
 *  It creates and manages navigator buttons (accordion headers), which you use
 *  to navigate between the children.
 *  There is one navigator button associated with each child container,
 *  and each navigator button belongs to the Accordion container, not to the child.
 *  When the user clicks a navigator button, the associated child container
 *  is displayed.
 *  The transition to the new child uses an animation to make it clear to
 *  the user that one child is disappearing and a different one is appearing.
 *
 *  <p>The Accordion container does not extend the ViewStack container,
 *  but it implements all the properties, methods, styles, and events
 *  of the ViewStack container, such as <code>selectedIndex</code>
 *  and <code>selectedChild</code>.</p>
 *
 *  <p>An Accordion container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>The width and height of the currently active child.</td>
 *        </tr>
 *        <tr>
 *           <td>Container resizing rules</td>
 *           <td>Accordion containers are only sized once to fit the size of the first child container by default. 
 *               They do not resize when you navigate to other child containers by default. 
 *               To force Accordion containers to resize when you navigate to a different child container, 
 *               set the resizeToContent property to true.</td>
 *        </tr>
 *        <tr>
 *           <td>Child sizing rules</td>
 *           <td>Children are sized to their default size. The child is clipped if it is larger than the Accordion container. 
 *               If the child is smaller than the Accordion container, it is aligned to the upper-left corner of the 
 *               Accordion container.</td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>-1 pixel for the top, bottom, left, and right values.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Accordion&gt;</code> tag inherits all of the
 *  tag attributes of its superclass, with the exception of scrolling-related
 *  attributes, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:Accordion
 *    <strong>Properties</strong>
 *    headerRenderer="<i>IFactory</i>"
 *    historyManagementEnabled="true|false"
 *    resizeToContent="false|true"
 *    selectedIndex="undefined"
 *  
 *    <strong>Styles</strong>
 *    headerHeight="depends on header font styles"
 *    headerStyleName="<i>No default</i>"
 *    horizontalGap="8"
 *    openDuration="250"
 *    openEasingFunction="undefined"
 *    paddingBottom="-1"
 *    paddingTop="-1"
 *    textRollOverColor="0xB333C"
 *    textSelectedColor="0xB333C"
 *    verticalGap="-1"
 *  
 *    <strong>Events</strong>
 *    change="<i>No default</i>"
 *    &gt;
 *      ...
 *      <i>child tags</i>
 *      ...
 *  &lt;/mx:Accordion&gt;
 *  </pre>
 *
 *  @includeExample examples/AccordionExample.mxml
 *
 *  @see mx.containers.accordionClasses.AccordionHeader
 *
 *  @tiptext Accordion allows for navigation between different child views
 *  @helpid 3013
 */
public class Accordion extends Container implements IHistoryManagerClient, IFocusManagerComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Base for all header names (_header0 - _headerN).
     */
    private static const HEADER_NAME_BASE:String = "_header";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Accordion()
    {
        super();

        headerRenderer = new ClassFactory(AccordionHeader);

        // Most views can't take focus, but an accordion can.
        // However, it draws its own focus indicator on the
        // header for the currently selected child view.
        // Container() has set tabEnabled false, so we
        // have to set it back to true.
        tabEnabled = true;

        // Accordion always clips content, it just handles it by itself
        super.clipContent = false;

        addEventListener(ChildExistenceChangedEvent.CHILD_ADD, childAddHandler);
        addEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, childRemoveHandler);

        showInAutomationHierarchy = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Is the accordian currently sliding between views?
     */
    private var bSliding:Boolean = false;

    /**
     *  @private
     */
    private var initialSelectedIndex:int = -1;

    /**
     *  @private
     *  If true, call HistoryManager.save() when setting currentIndex.
     */
    private var bSaveState:Boolean = false;

    /**
     *  @private
     */
    private var bInLoadState:Boolean = false;

    /**
     *  @private
     */
    private var firstTime:Boolean = true;
    
    /**
     *  @private
     */
    private var showFocusIndicator:Boolean = false;

    /**
     *  @private
     *  Cached tween properties to speed up tweening calculations.
     */
    private var tweenViewMetrics:EdgeMetrics;
    private var tweenContentWidth:Number;
    private var tweenContentHeight:Number;
    private var tweenOldSelectedIndex:int;
    private var tweenNewSelectedIndex:int;
    private var tween:Tween;

    /**
     *  @private
     *  We'll measure ourselves once and then store the results here
     *  for the lifetime of the ViewStack.
     */
    private var accMinWidth:Number;
    private var accMinHeight:Number;
    private var accPreferredWidth:Number;
    private var accPreferredHeight:Number;
    
    /**
     *  @private
     *  When a child is added or removed, this flag is set true
     *  and it causes a re-measure.  
     */
    private var childAddedOrRemoved:Boolean = false;

    /**
     *  @private
     *  Remember which child has an overlay mask, if any.
     */
    private var overlayChild:IUIComponent;

    /**
     *  @private
     *  Keep track of the overlay's targetArea
     */
    private var overlayTargetArea:RoundedRectangle;

    /**
     *  @private
     */
    private var layoutStyleChanged:Boolean = false;

    /**
     *  @private
     */
    private var currentDissolveEffect:Effect;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoLayout
    //----------------------------------

    // Don't allow user to set autoLayout because
    // there are problems if deferred instantiation
    // runs at the same time as an effect. (Bug 79174)

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get autoLayout():Boolean
    {
        return true;
    }

    /**
     *  @private
     */
    override public function set autoLayout(value:Boolean):void
    {
    }

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of an Accordion is calculated
     *  for the label of the first header.
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
        
        var header0:Button = getHeaderAt(0);
        var result:Number = header0.y + header0.baselinePosition;
        
        if (isEmpty)
        {
            removeChildAt(0);
            validateNow();
        }
        
        return result;
    }

    //----------------------------------
    //  clipContent
    //----------------------------------

    // We still need to ensure the clip mask is *never* created for an
    // Accordion.

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get clipContent():Boolean
    {
        return true; // Accordion does clip, it just does it itself
    }

    /**
     *  @private
     */
    override public function set clipContent(value:Boolean):void
    {
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get horizontalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     */
    override public function set horizontalScrollPolicy(value:String):void
    {
    }

    //----------------------------------
    //  verticalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get verticalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     */
    override public function set verticalScrollPolicy(value:String):void
    {
    }

    //--------------------------------------------------------------------------
    //
    // Public properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _focusedIndex:int = -1;

    /**
     *  @private
     */
    mx_internal function get focusedIndex():int
    {
        return _focusedIndex;
    }

    //----------------------------------
    //  contentHeight
    //----------------------------------

    /**
     *  The height of the area, in pixels, in which content is displayed.
     *  You can override this getter if your content
     *  does not occupy the entire area of the container.
     */
    protected function get contentHeight():Number
    {
        // Start with the height of the entire accordion.
        var contentHeight:Number = unscaledHeight;

        // Subtract the heights of the top and bottom borders.
        var vm:EdgeMetrics = viewMetricsAndPadding;
        contentHeight -= vm.top + vm.bottom;

        // Subtract the header heights.
        var verticalGap:Number = getStyle("verticalGap");
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            contentHeight -= getHeaderAt(i).height;

            if (i > 0)
                contentHeight -= verticalGap;
        }
        
        return contentHeight;
    }

    //----------------------------------
    //  contentWidth
    //----------------------------------

    /**
     *  The width of the area, in pixels, in which content is displayed.
     *  You can override this getter if your content
     *  does not occupy the entire area of the container.
     */
    protected function get contentWidth():Number
    {
        // Start with the width of the entire accordion.
        var contentWidth:Number = unscaledWidth;

        // Subtract the widths of the left and right borders.
        var vm:EdgeMetrics = viewMetricsAndPadding;
        contentWidth -= vm.left + vm.right;

        contentWidth -= getStyle("paddingLeft") +
                        getStyle("paddingRight");

        return contentWidth;
    }

    //----------------------------------
    //  headerRenderer
    //----------------------------------

    /**
     *  @private
     *  Storage for the headerRenderer property.
     */
    private var _headerRenderer:IFactory;

    [Bindable("headerRendererChanged")]

    /**
     *  A factory used to create the navigation buttons for each child.
     *  The default value is a factory which creates a
     *  <code>mx.containers.accordionClasses.AccordionHeader</code>. The
     *  created object must be a subclass of Button and implement the
     *  <code>mx.core.IDataRenderer</code> interface. The <code>data</code>
     *  property is set to the content associated with the header.
     *
     *  @see mx.containers.accordionClasses.AccordionHeader
     */
    public function get headerRenderer():IFactory
    {
        return _headerRenderer;
    }

    /**
     *  @private
     */
    public function set headerRenderer(value:IFactory):void
    {
        _headerRenderer = value;

        dispatchEvent(new Event("headerRendererChanged"));
    }   
   
    //----------------------------------
    //  historyManagementEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for historyManagementEnabled property.
     */
    private var _historyManagementEnabled:Boolean = true;

    /**
     *  @private
     */
    private var historyManagementEnabledChanged:Boolean = false;

    [Inspectable(defaultValue="true")]

    /**
     *  If set to <code>true</code>, this property enables history management
     *  within this Accordion container.
     *  As the user navigates from one child to another,
     *  the browser remembers which children were visited.
     *  The user can then click the browser's Back and Forward buttons
     *  to move through this navigation history.
     *
     *  @default true
     *
     *  @see mx.managers.HistoryManager
     */
    public function get historyManagementEnabled():Boolean
    {
        return _historyManagementEnabled;
    }

    /**
     *  @private
     */
    public function set historyManagementEnabled(value:Boolean):void
    {
        if (value != _historyManagementEnabled)
        {
            _historyManagementEnabled = value;
            historyManagementEnabledChanged = true;

            invalidateProperties();
        }
    }

    //----------------------------------
    //  resizeToContent
    //----------------------------------

    /**
     *  @private
     *  Storage for the resizeToContent property.
     */
    private var _resizeToContent:Boolean = false;

    [Inspectable(defaultValue="false")]

    /**
     *  If set to <code>true</code>, this Accordion automatically resizes to
     *  the size of its current child.
     * 
     *  @default false
     */
    public function get resizeToContent():Boolean
    {
        return _resizeToContent;
    }

    /**
     *  @private
     */
    public function set resizeToContent(value:Boolean):void
    {
        if (value != _resizeToContent)
        {
            _resizeToContent = value;

            if (value)
                invalidateSize();
        }
    }

    //----------------------------------
    //  selectedChild
    //----------------------------------

    [Bindable("valueCommit")]

    /**
     *  A reference to the currently visible child container.
     *  The default value is a reference to the first child.
     *  If there are no children, this property is <code>null</code>.
     *
     *  <p><b>Note:</b> You can only set this property in an ActionScript statement, 
     *  not in MXML.</p>
     *
     *  @tiptext Specifies the child view that is currently displayed
     *  @helpid 3401
     */
    public function get selectedChild():Container
    {
        if (selectedIndex == -1)
            return null;

        return Container(getChildAt(selectedIndex));
    }

    /**
     *  @private
     */
    public function set selectedChild(value:Container):void
    {
        var newIndex:int = getChildIndex(DisplayObject(value));

        if (newIndex >= 0 && newIndex < numChildren)
            selectedIndex = newIndex;
    }

    //----------------------------------
    //  selectedIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectedIndex and selectedChild properties.
     */
    private var _selectedIndex:int = -1;

    /**
     *  @private
     */
    private var proposedSelectedIndex:int = -1;

    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  The zero-based index of the currently visible child container.
     *  Child indexes are in the range 0, 1, 2, ..., n - 1, where n is the number
     *  of children.
     *  The default value is 0, corresponding to the first child.
     *  If there are no children, this property is <code>-1</code>.
     *
     *  @default 0
     *
     *  @tiptext Specifies the index of the child view that is currently displayed
     *  @helpid 3402
     */
    public function get selectedIndex():int
    {
        if (proposedSelectedIndex != -1)
            return proposedSelectedIndex;

        return _selectedIndex;
    }

    /**
     *  @private
     */
    public function set selectedIndex(value:int):void
    {
        // Bail if new index isn't a number.
        if (value == -1)
            return;

        // Bail if the index isn't changing.
        if (value == _selectedIndex)
            return;

        // Propose the specified value as the new value for selectedIndex.
        // It gets applied later when commitProperties() calls commitSelectedIndex().
        // The proposed value can be "out of range", because the children
        // may not have been created yet, so the range check is handled
        // in commitSelectedIndex(), not here. Other calls to this setter
        // can change the proposed index before it is committed. Also,
        // childAddHandler() proposes a value of 0 when it creates the first
        // child, if no value has yet been proposed.
        proposedSelectedIndex = value;
        invalidateProperties();

        // Set a flag which will cause the History Manager to save state
        // the next time measure() is called.
        if (historyManagementEnabled && _selectedIndex != -1 && !bInLoadState)
            bSaveState = true;

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function createComponentsFromDescriptors(
                                    recurse:Boolean = true):void
    {
        // The easiest way to handle the ContainerCreationPolicy.ALL policy is to let
        // Container's implementation of createComponents handle it.
        if (actualCreationPolicy == ContainerCreationPolicy.ALL)
        {
            super.createComponentsFromDescriptors();
            return;
        }

        // If the policy is ContainerCreationPolicy.AUTO, Accordion instantiates its children
        // immediately, but not any grandchildren. The children of
        // the selected child will get created in instantiateSelectedChild().
        // Why not create the grandchildren of the selected child by calling
        //    createComponentFromDescriptor(childDescriptors[i], i == selectedIndex);
        // in the loop below? Because one of this Accordion's childDescriptors
        // may be for a Repeater, in which case the following loop over the
        // childDescriptors is not the same as a loop over the children.
        // In particular, selectedIndex is supposed to specify the nth
        // child, not the nth childDescriptor, and the 2nd parameter of
        // createComponentFromDescriptor() should make the recursion happen
        // on the nth child, not the nth childDescriptor.
        var numChildrenBefore:int = numChildren;

        if (childDescriptors)
        {
            var n:int = childDescriptors.length;
            for (var i:int = 0; i < n; i++)
            {
                var descriptor:ComponentDescriptor =
                    ComponentDescriptor(childDescriptors[i]);

                createComponentFromDescriptor(descriptor, false);
            }
        }

        numChildrenCreated = numChildren - numChildrenBefore;

        processedDescriptors = true;
    }

    /**
     *  @private
     */
    override public function setChildIndex(child:DisplayObject,
                                           newIndex:int):void
    {
        var oldIndex:int = getChildIndex(child);

        // Check boundary conditions first
        if (oldIndex == -1 || newIndex < 0)
            return;

        var nChildren:int = numChildren;
        if (newIndex >= nChildren)
            newIndex = nChildren - 1;

        // Next, check for no move
        if (newIndex == oldIndex)
            return;

        // De-select the old selected index header
        var oldSelectedHeader:Button = getHeaderAt(selectedIndex);
        if (oldSelectedHeader)
        {
            oldSelectedHeader.selected = false;
            drawHeaderFocus(_focusedIndex, false);
        }

        // Adjust the depths and _childN references of the affected children.
        super.setChildIndex(child, newIndex);

        // Shuffle the headers
        shuffleHeaders(oldIndex, newIndex);

        // Select the new selected index header
        var newSelectedHeader:Button = getHeaderAt(selectedIndex);
        if (newSelectedHeader)
        {
            newSelectedHeader.selected = true;
            drawHeaderFocus(_focusedIndex, showFocusIndicator);
        }

        // Make sure the new selected child is instantiated
        instantiateChild(selectedChild);
    }

    /**
     *  @private
     */
    private function shuffleHeaders(oldIndex:int, newIndex:int):void
    {
        var i:int;

        // Adjust the _headerN references of the affected headers.
        // Note: Algorithm is the same as Container.setChildIndex().
        var header:Button = getHeaderAt(oldIndex);
        if (newIndex < oldIndex)
        {
            for (i = oldIndex; i > newIndex; i--)
            {
                getHeaderAt(i - 1).name = HEADER_NAME_BASE + i;
            }
        }
        else
        {
            for (i = oldIndex; i < newIndex; i++)
            {
                getHeaderAt(i + 1).name = HEADER_NAME_BASE + i;
            }
        }
        header.name = HEADER_NAME_BASE + newIndex;
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (historyManagementEnabledChanged)
        {
            if (historyManagementEnabled)
                HistoryManager.register(this);
            else
                HistoryManager.unregister(this);

            historyManagementEnabledChanged = false;
        }

        commitSelectedIndex();
        
        if (firstTime)
        {
            firstTime = false;
            
            // Add "addedToStage" and "removedFromStage" listeners so we can 
            // register/un-register from the history manager when this component
            // is added or removed from the display list.
            addEventListener(Event.ADDED_TO_STAGE, addedToStageHandler, false, 0, true);
            addEventListener(Event.REMOVED_FROM_STAGE, removedFromStageHandler, false, 0, true);
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var minWidth:Number = 0;
        var minHeight:Number = 0;
        var preferredWidth:Number = 0;
        var preferredHeight:Number = 0;

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var headerHeight:Number = getHeaderHeight();

        // In general, we only measure once and thereafter use cached values.
        // There are three exceptions: when resizeToContent is true,
        // when a layout style like headerHeight changes,
        // and when a child is added or removed.
        //
        // We need to copy the cached values into the measured fields
        // again to handle the case where scaleX or scaleY is not 1.0.
        // When the Accordion is zoomed, code in UIComponent.measureSizes
        // scales the measuredWidth/Height values every time that
        // measureSizes is called.  (bug 100749)
        if (accPreferredWidth && !_resizeToContent &&
            !layoutStyleChanged && !childAddedOrRemoved)
        {
            measuredMinWidth = accMinWidth;
            measuredMinHeight = accMinHeight;
            measuredWidth = accPreferredWidth;
            measuredHeight = accPreferredHeight;
            return;
        }

        layoutStyleChanged = false;
        childAddedOrRemoved = false;

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var button:Button = getHeaderAt(i);
            var child:IUIComponent = IUIComponent(getChildAt(i));

            minWidth = Math.max(minWidth, button.minWidth);
            minHeight += headerHeight;
            preferredWidth = Math.max(preferredWidth, minWidth);
            preferredHeight += headerHeight;

            // The headers preferredWidth is messing up the accordion measurement. This may not
            // be needed anyway because we're still using the headers minWidth to determine our overall
            // minWidth.

            if (i == selectedIndex)
            {
                preferredWidth = Math.max(preferredWidth, child.getExplicitOrMeasuredWidth());
                preferredHeight += child.getExplicitOrMeasuredHeight();

                minWidth = Math.max(minWidth, child.minWidth);
                minHeight += child.minHeight;
            }

        }

        // Add space for borders and margins
        var vm:EdgeMetrics = viewMetricsAndPadding;
        var widthPadding:Number = vm.left + vm.right;
        var heightPadding:Number = vm.top + vm.bottom;

        // Need to adjust the widthPadding if paddingLeft and paddingRight are negative numbers
        // (see explanation in updateDisplayList())
        if (paddingLeft < 0)
            widthPadding -= paddingLeft;

        if (paddingRight < 0)
            widthPadding -= paddingRight;

        minWidth += widthPadding;
        preferredWidth += widthPadding;
        minHeight += heightPadding;
        preferredHeight += heightPadding;

        measuredMinWidth = minWidth;
        measuredMinHeight = minHeight;
        measuredWidth = preferredWidth;
        measuredHeight = preferredHeight;

        // If we're called before instantiateSelectedChild, then bail.
        // We'll be called again later (instantiateSelectedChild calls
        // invalidateSize), and we don't want to load values into the
        // cache until we're fully initialized.  (bug 102639)
        // This check was moved from the beginning of this function to
        // here to fix bugs 103665/104213.
        if (selectedChild && Container(selectedChild).numChildrenCreated == -1)
            return;

        // Don't remember sizes if we don't have any children
        if (numChildren == 0)
            return;

        accMinWidth = minWidth;
        accMinHeight = minHeight;
        accPreferredWidth = preferredWidth;
        accPreferredHeight = preferredHeight;
    }

    /**
     *  @private
     *  Arranges the layout of the accordion contents.
     *
     *  @tiptext Arranges the layout of the Accordion's contents
     *  @helpid 3017
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        // Don't do layout if we're tweening because the tweening
        // code is handling it.
        if (tween)
            return;

        // Measure the border.
        var bm:EdgeMetrics = borderMetrics;
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingTop:Number = getStyle("paddingTop");
        var verticalGap:Number = getStyle("verticalGap");

        // Determine the width and height of the content area.
        var localContentWidth:Number = calcContentWidth();
        var localContentHeight:Number = calcContentHeight();

        // Arrange the headers, the content clips,
        // based on selectedIndex.
        var x:Number = bm.left + paddingLeft;
        var y:Number = bm.top + paddingTop;

        // Adjustments. These are required since the default halo
        // appearance has verticalGap and all margins set to -1
        // so the edges of the headers overlap each other and the
        // border of the accordion. These overlaps cause problems with
        // the content area clipping, so we adjust for them here.
        var contentX:Number = x;
        var adjContentWidth:Number = localContentWidth;
        var headerHeight:Number = getHeaderHeight();

        if (paddingLeft < 0)
        {
            contentX -= paddingLeft;
            adjContentWidth += paddingLeft;
        }

        if (paddingRight < 0)
            adjContentWidth += paddingRight;

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var header:Button = getHeaderAt(i);
            var content:IUIComponent = IUIComponent(getChildAt(i));

            header.move(x, y);
            header.setActualSize(localContentWidth, headerHeight);
            y += headerHeight;

            if (i == selectedIndex)
            {
                content.move(contentX, y);
                content.visible = true;

                var contentW:Number = adjContentWidth;
                var contentH:Number = localContentHeight;

                if (!isNaN(content.percentWidth))
                {
                    if (contentW > content.maxWidth)
                        contentW = content.maxWidth;
                }
                else
                {
                    if (contentW > content.getExplicitOrMeasuredWidth())
                        contentW = content.getExplicitOrMeasuredWidth();
                }

                if (!isNaN(content.percentHeight))
                {
                    if (contentH > content.maxHeight)
                        contentH = content.maxHeight;
                }
                else
                {
                    if (contentH > content.getExplicitOrMeasuredHeight())
                        contentH = content.getExplicitOrMeasuredHeight();
                }

                if (content.width != contentW ||
                    content.height != contentH)
                {
                    content.setActualSize(contentW, contentH);
                }

                y += localContentHeight;
            }
            else
            {
                content.move(contentX, i < selectedIndex
                        ? y : y - localContentHeight);
                content.visible = false;
            }

            y += verticalGap;
        }

        // Make sure blocker is in front
        if (blocker)
            rawChildren.setChildIndex(blocker, numChildren - 1);

        // refresh the focus rect, the dimensions might have changed.
        drawHeaderFocus(_focusedIndex, showFocusIndicator);
    }

    /**
     *  @private
     */
    override mx_internal function setActualCreationPolicies(policy:String):void
    {
        super.setActualCreationPolicies(policy);

        // If the creation policy is switched to ContainerCreationPolicy.ALL and our createComponents
        // function has already been called (we've created our children but not
        // all our grandchildren), then create all our grandchildren now (bug 99160).
        if (policy == ContainerCreationPolicy.ALL && numChildren > 0)
        {
            var n:int = numChildren;
            for (var i:int = 0; i < n; i++)
            {
                Container(getChildAt(i)).createComponentsFromDescriptors();
            }
        }
    }

    /**
     *  @private
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        super.focusInHandler(event);
        
        showFocusIndicator = focusManager.showFocusIndicator;
        // When the accordion has focus, the Focus Manager
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
        
        showFocusIndicator = false;
        if (focusManager && event.target == this)
            focusManager.defaultButtonEnabled = true;
    }

    /**
     *  @private
     */
    override public function drawFocus(isFocused:Boolean):void
    {
        drawHeaderFocus(_focusedIndex, isFocused);
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);

        if (!styleProp ||
            styleProp == "headerStyleName" ||
            styleProp == "styleName")
        {
            var headerStyleName:Object = getStyle("headerStyleName");
            var header:Button;
            
            if (headerStyleName)
            {
                if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                {               
                    var headerStyleDecl:CSSStyleDeclaration = 
                        StyleManager.getStyleDeclaration("." + headerStyleName);
                    if (headerStyleDecl)
                    {
                        // Need to reset the header style declaration and 
                        // regenerate their style cache
                        for (var i:int = 0; i < numChildren; i++)
                        {
                            header = getHeaderAt(i);
                            if (header)
                            {
                                header.styleDeclaration = headerStyleDecl;
                                header.regenerateStyleCache(true);
                                header.styleChanged(null);
                            }
                        }
                    }
                }
                else
                {
                    for (var j:int = 0; j < numChildren; j++)
                    {
                        header = getHeaderAt(j);
                        if (header)
                        {
                            header.styleName = headerStyleName;
                        }
                    }
                }
            }
        }
        else if (StyleManager.isSizeInvalidatingStyle(styleProp))
        {
            layoutStyleChanged = true;
        }
    }

    /**
     *  @private
     *  When asked to create an overlay mask, create it on the selected child
     *  instead.  That way, the chrome around the edge of the Accordion (e.g. the
     *  header buttons) is not occluded by the overlay mask (bug 99029).
     */
    override mx_internal function addOverlay(color:uint, targetArea:RoundedRectangle = null):void
    {
        // As we're switching the currently-selected child, don't
        // allow two children to both have an overlay at the same time.
        // This is done because it makes accounting a headache.  If there's
        // a legitimate reason why two children both need overlays, this
        // restriction could be relaxed.
        if (overlayChild)
            removeOverlay();

        // Remember which child has an overlay, so that we don't inadvertently
        // create an overlay on one child and later try to remove the overlay
        // of another child. (bug 100731)
        overlayChild = selectedChild as IUIComponent;

        if (!overlayChild)
            return;

        overlayColor = color;
        overlayTargetArea = targetArea;

        if (selectedChild && selectedChild.numChildrenCreated == -1) // No children have been created
        {
            // Wait for the childrenCreated event before creating the overlay
            selectedChild.addEventListener(FlexEvent.INITIALIZE,
                                           initializeHandler);
        }
        else // Children already exist
        {
            initializeHandler(null);
        }
    }

    /**
     *  @private
     *  Called when we are running a Dissolve effect
     *  and the initialize event has been dispatched
     *  or the children already exist
     */
    private function initializeHandler(event:FlexEvent):void
    {
        UIComponent(overlayChild).addOverlay(overlayColor, overlayTargetArea);
    }

    /**
     *  @private
     *  Handle key down events
     */
    override mx_internal function removeOverlay():void
    {
        if (overlayChild)
        {
            UIComponent(overlayChild).removeOverlay();
            overlayChild = null;
        }
    }

    // -------------------------------------------------------------------------
    // StateInterface
    // -------------------------------------------------------------------------

    /**
     *  @copy mx.managers.IHistoryManagerClient#saveState()
     */
    public function saveState():Object
    {
        var index:int = _selectedIndex == -1 ? 0 : _selectedIndex;
        return { selectedIndex: index };
    }

    /**
     *  @copy mx.managers.IHistoryManagerClient#loadState()
     */
    public function loadState(state:Object):void
    {
        var newIndex:int = state ? int(state.selectedIndex) : 0;

        if (newIndex == -1)
            newIndex = initialSelectedIndex;

        if (newIndex == -1)
            newIndex = 0;

        if (newIndex != _selectedIndex)
        {
            // When loading a new state, we don't want to
            // save our current state in the history stack.
            bInLoadState = true;
            selectedIndex = newIndex;
            bInLoadState = false;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Public methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns a reference to the navigator button for a child container.
     *
     *  @param index Zero-based index of the child.
     *
     *  @return Button object representing the navigator button.
     */
    public function getHeaderAt(index:int):Button
    {
        return Button(rawChildren.getChildByName(HEADER_NAME_BASE + index));
    }

    /**
     *  @private
     *  Returns the height of the header control. All header controls are the same
     *  height.
     */
    private function getHeaderHeight():Number
    {
        var headerHeight:Number = getStyle("headerHeight");
        
        if (isNaN(headerHeight))
        {
            headerHeight = 0;
            
            if (numChildren > 0)
                headerHeight = getHeaderAt(0).measuredHeight;
        }
        
        return headerHeight;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Private methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Utility method to create the segment header
     */
    private function createHeader(content:DisplayObject, i:int):void
    {
        // Before creating the header, un-select the currently selected
        // header. We will be selecting the correct header below.
        if (selectedIndex != -1 && getHeaderAt(selectedIndex))
            getHeaderAt(selectedIndex).selected = false;

        // Create the header.
        // Notes:
        // 1) An accordion maintains a reference to
        // the header for its Nth child as _headerN. These references are
        // juggled when children and their headers are re-indexed or
        // removed, to ensure that _headerN is always a reference the
        // header for the Nth child.
        // 2) Always create the header with the index of the last item.
        // If needed, the headers will be shuffled below.
        var header:Button = Button(headerRenderer.newInstance());
        header.name = HEADER_NAME_BASE + (numChildren - 1);
        
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            header.styleName = this;
        }
        
        var headerStyleName:String = getStyle("headerStyleName");
        if (headerStyleName)
        {
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            {
                 var headerStyleDecl:CSSStyleDeclaration = StyleManager.
                        getStyleDeclaration("." + headerStyleName);
                        
                if (headerStyleDecl)
                    header.styleDeclaration = headerStyleDecl;
            }
            else
            {
                header.styleName = headerStyleName;
            }
        }
 
        header.addEventListener(MouseEvent.CLICK, headerClickHandler);

        IDataRenderer(header).data = content;

        if (content is Container)
        {
            var contentContainer:Container = Container(content);

            header.label = contentContainer.label;
            if (contentContainer.icon)
                header.setStyle("icon", contentContainer.icon);

            // If the child has a toolTip, transfer it to the header.
            var toolTip:String = contentContainer.toolTip;
            if (toolTip && toolTip != "")
            {
                header.toolTip = toolTip;
                contentContainer.toolTip = null;
            }
        }

        rawChildren.addChild(header);

        // If the newly added child isn't at the end of our child list, shuffle
        // the headers accordingly.
        if (i != numChildren - 1)
            shuffleHeaders(numChildren - 1, i);

        // Make sure the correct header is selected
        if (selectedIndex != -1 && getHeaderAt(selectedIndex))
            getHeaderAt(selectedIndex).selected = true;
    }

    /**
     *  @private
     */
    private function calcContentWidth():Number
    {
        // Start with the width of the entire accordion.
        var contentWidth:Number = unscaledWidth;

        // Subtract the widths of the left and right borders.
        var vm:EdgeMetrics = viewMetricsAndPadding;
        contentWidth -= vm.left + vm.right;

        return contentWidth;
    }

    /**
     *  @private
     */
    private function calcContentHeight():Number
    {
        // Start with the height of the entire accordion.
        var contentHeight:Number = unscaledHeight;

        // Subtract the heights of the top and bottom borders.
        var vm:EdgeMetrics = viewMetricsAndPadding;
        contentHeight -= vm.top + vm.bottom;

        // Subtract the header heights.
        var verticalGap:Number = getStyle("verticalGap");
        var headerHeight:Number = getHeaderHeight();

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            contentHeight -= headerHeight;

            if (i > 0)
                contentHeight -= verticalGap;
        }

        return contentHeight;
    }

    /**
     *  @private
     */
    private function drawHeaderFocus(headerIndex:int, isFocused:Boolean):void
    {
        if (headerIndex != -1)
            getHeaderAt(headerIndex).drawFocus(isFocused);
    }

    /**
     *  @private
     */
    private function headerClickHandler(event:Event):void
    {
        var header:Button = Button(event.currentTarget);
        var oldIndex:int = selectedIndex;
        // content is placed onto the button so we have to access it via []
        selectedChild = Container(IDataRenderer(header).data);
        var newIndex:int = selectedIndex;
        if (oldIndex != newIndex)
            dispatchChangeEvent(oldIndex, newIndex, event);
    }

    /**
     *  @private
     */
    private function commitSelectedIndex():void
    {
        if (proposedSelectedIndex == -1)
            return;

        var newIndex:int = proposedSelectedIndex;
        proposedSelectedIndex = -1;

        // The selectedIndex must be undefined if there are no children,
        // even if a selectedIndex has been proposed.
        if (numChildren == 0)
        {
            _selectedIndex = -1;
            return;
        }

        // Ensure that the new index is in bounds.
        if (newIndex < 0)
            newIndex = 0;
        else if (newIndex > numChildren - 1)
            newIndex = numChildren - 1;

        // Remember the old index.
        var oldIndex:int = _selectedIndex;

        // Bail if the index isn't changing.
        if (newIndex == oldIndex)
            return;

        // If we are currently playing a Dissolve effect, end it and restart it again
        currentDissolveEffect = null;

        if (isEffectStarted)
        {
            var dissolveInstanceClass:Class = Class(systemManager.getDefinitionByName("mx.effects.effectClasses.DissolveInstance"));
        
            for (var i:int = 0; i < _effectsStarted.length; i++)
            {
                // Avoid referencing the DissolveInstance class directly, so that
                // we don't create an unwanted linker dependency.
                if (dissolveInstanceClass && _effectsStarted[i] is dissolveInstanceClass)
                {
                    // If we find the dissolve, save a pointer to the parent effect and end the instance
                    currentDissolveEffect = _effectsStarted[i].effect;
                    _effectsStarted[i].end();
                    break;
                }
            }
        }

        // Unfocus the old header.
        if (_focusedIndex != newIndex)
            drawHeaderFocus(_focusedIndex, false);

        // Deselect the old header.
        if (oldIndex != -1)
            getHeaderAt(oldIndex).selected = false;

        // Commit the new index.
        _selectedIndex = newIndex;

        // Remember our initial selected index so we can
        // restore to our default state when the history
        // manager requests it.
        if (initialSelectedIndex == -1)
            initialSelectedIndex = _selectedIndex;

        // Select the new header.
        getHeaderAt(newIndex).selected = true;

        if (_focusedIndex != newIndex)
        {
            // Focus the new header.
            _focusedIndex = newIndex;
            drawHeaderFocus(_focusedIndex, showFocusIndicator);
        }

        if (bSaveState)
        {
            HistoryManager.save();
            bSaveState = false;
        }

        if (getStyle("openDuration") == 0 || oldIndex == -1)
        {
            // Need to set the new index to be visible here
            // in order for effects to work.
            Container(getChildAt(newIndex)).setVisible(true);

            // Now that the effects have been triggered, we can hide the
            // current view until it is properly sized and positioned below.
            Container(getChildAt(newIndex)).setVisible(false, true);
            if (oldIndex != -1)
                Container(getChildAt(oldIndex)).setVisible(false);

            instantiateChild(selectedChild);
        }
        else
        {
            if (tween)
                tween.endTween();

            startTween(oldIndex, newIndex);
        }
    }

    /**
     *  @private
     */
    private function instantiateChild(child:Container):void
    {
        // fix for bug#137430
        // when the selectedChild index is -1 (invalid value due to any reason)
        // selectedContainer will not be valid. Before we proceed
        // we need to make sure of its validity.
        if (!child)
            return;

        // Performance optimization: don't call createComponents if we know
        // that createComponents has already been called.
        if (child && child.numChildrenCreated == -1)
            child.createComponentsFromDescriptors();

        // Do the initial measurement/layout pass for the newly-instantiated
        // descendants.
        invalidateSize();
        invalidateDisplayList();

        if (child is IInvalidating)
            IInvalidating(child).invalidateSize();
    }

    /**
     *  @private
     */
    private function dispatchChangeEvent(oldIndex:int,
                                         newIndex:int,
                                         cause:Event = null):void
    {
        var indexChangeEvent:IndexChangedEvent =
            new IndexChangedEvent(IndexChangedEvent.CHANGE);
        indexChangeEvent.oldIndex = oldIndex;
        indexChangeEvent.newIndex = newIndex;
        indexChangeEvent.relatedObject = getChildAt(newIndex);
        indexChangeEvent.triggerEvent = cause;
        dispatchEvent(indexChangeEvent);
    }

    /**
     *  @private
     */
    private function startTween(oldSelectedIndex:int, newSelectedIndex:int):void
    {
        bSliding = true;

        // To improve the animation performance, we set up some invariants
        // used in onTweenUpdate. (Some of these, like contentHeight, are
        // too slow to recalculate at every tween step.)
        tweenViewMetrics = viewMetricsAndPadding;
        tweenContentWidth = calcContentWidth();
        tweenContentHeight = calcContentHeight();
        tweenOldSelectedIndex = oldSelectedIndex;
        tweenNewSelectedIndex = newSelectedIndex;

        // A single instance of Tween drives the animation.
        var openDuration:Number = getStyle("openDuration");
        tween = new Tween(this, 0, tweenContentHeight, openDuration);

        var easingFunction:Function = getStyle("openEasingFunction") as Function;
        if (easingFunction != null)
            tween.easingFunction = easingFunction;

        // Ideally, all tweening should be managed by the EffectManager.  Since
        // this tween isn't managed by the EffectManager, we need this alternate
        // mechanism to tell the EffectManager that we're tweening.  Otherwise, the
        // EffectManager might try to play another effect that animates the same
        // properties.
        if (oldSelectedIndex != -1)
            Container(getChildAt(oldSelectedIndex)).tweeningProperties = ["x", "y", "width", "height"];
        Container(getChildAt(newSelectedIndex)).tweeningProperties = ["x", "y", "width", "height"];

        // If the content of the new child hasn't been created yet, set the new child
        // to the content width/height. This way any background color will show up
        // properly during the animation.
        var newSelectedChild:Container = Container(getChildAt(newSelectedIndex));
        if (newSelectedChild.numChildren == 0)
        {
            var paddingLeft:Number = getStyle("paddingLeft");
            var contentX:Number = borderMetrics.left + (paddingLeft > 0 ? paddingLeft : 0);

            newSelectedChild.move(contentX, newSelectedChild.y);
            newSelectedChild.setActualSize(tweenContentWidth, tweenContentHeight);
        }

        UIComponent.suspendBackgroundProcessing();
    }

    /**
     *  @private
     */
    mx_internal function onTweenUpdate(value:Number):void
    {
        // Fetch the tween invariants we set up in startTween.
        var vm:EdgeMetrics = tweenViewMetrics;
        var contentWidth:Number = tweenContentWidth;
        var contentHeight:Number = tweenContentHeight;
        var oldSelectedIndex:int = tweenOldSelectedIndex;
        var newSelectedIndex:int = tweenNewSelectedIndex;

        // The tweened value is the height of the new content area, which varies
        // from 0 to the contentHeight. As the new content area grows, the
        // old content area shrinks.
        var newContentHeight:Number = value;
        var oldContentHeight:Number = contentHeight - value;

        // These offsets for the Y position of the content clips make the content
        // clips appear to be pushed up and pulled down.
        var oldOffset:Number = oldSelectedIndex < newSelectedIndex ?
                               -newContentHeight :
                               newContentHeight;
        var newOffset:Number = newSelectedIndex > oldSelectedIndex ?
                               oldContentHeight :
                               -oldContentHeight;

        // Loop over all the headers to arrange them vertically.
        // The loop is intentionally over ALL the headers, not just the ones that
        // need to move; this makes the animation look equally smooth
        // regardless of how many headers are moving.
        // We also reposition the two visible content clips.
        var y:Number = vm.top;
        var verticalGap:Number = getStyle("verticalGap");
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var header:Button = getHeaderAt(i);
            var content:Container = Container(getChildAt(i));

            header.$y = y;
            y += header.height;

            if (i == oldSelectedIndex)
            {
                content.cacheAsBitmap = true;
                content.scrollRect = new Rectangle(0, -oldOffset,
                        contentWidth, contentHeight);
                content.visible = true;
                y += oldContentHeight;

            }
            else if (i == newSelectedIndex)
            {
                content.cacheAsBitmap = true;
                content.scrollRect = new Rectangle(0, -newOffset,
                        contentWidth, contentHeight);
                content.visible = true;
                y += newContentHeight;
            }

            y += verticalGap;
        }
    }

    /**
     *  @private
     */
    mx_internal function onTweenEnd(value:Number):void
    {
        bSliding = false;

        var oldSelectedIndex:int = tweenOldSelectedIndex;

        var vm:EdgeMetrics = tweenViewMetrics;

        var verticalGap:Number = getStyle("verticalGap");
        var headerHeight:Number = getHeaderHeight();

        var localContentWidth:Number = calcContentWidth();
        var localContentHeight:Number = calcContentHeight();

        var y:Number = vm.top;
        var content:Container;

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var header:Button = getHeaderAt(i);
            header.$y = y;
            y += headerHeight;

            if (i == selectedIndex)
            {
                content = Container(getChildAt(i));
                content.cacheAsBitmap = false;
                content.scrollRect = null;
                content.visible = true;
                y += localContentHeight;
            }
            y += verticalGap;
        }

        if (oldSelectedIndex != -1)
        {
            content = Container(getChildAt(oldSelectedIndex));
            content.cacheAsBitmap = false;
            content.scrollRect = null;
            content.visible = false;
            content.tweeningProperties = null;
        }

        // Delete the temporary tween invariants we set up in startTween.
        tweenViewMetrics = null;
        tweenContentWidth = NaN;
        tweenContentHeight = NaN;
        tweenOldSelectedIndex = 0;
        tweenNewSelectedIndex = 0;

        tween = null;

        UIComponent.resumeBackgroundProcessing();

        Container(getChildAt(selectedIndex)).tweeningProperties = null;

        // If we interrupted a Dissolve effect, restart it here
        if (currentDissolveEffect)
        {
            if (currentDissolveEffect.target != null)
            {
                currentDissolveEffect.play();
            }
            else
            {
                currentDissolveEffect.play([this]);
            }
        }

        // Let the screen render the last frame of the animation before
        // we begin instantiating the new child.
        callLater(instantiateChild, [selectedChild]);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Handles "keyDown" event.
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        // Only listen for events that have come from the accordion itself.
        if (event.target != this)
            return;

        var prevValue:int = selectedIndex;

        switch (event.keyCode)
        {
            case Keyboard.PAGE_DOWN:
            {
                drawHeaderFocus(_focusedIndex, false);
                _focusedIndex = selectedIndex = (selectedIndex < numChildren - 1
                                 ? selectedIndex + 1
                                 : 0);
                drawHeaderFocus(_focusedIndex, true);
                event.stopPropagation();
                dispatchChangeEvent(prevValue, selectedIndex, event);
                break;
            }

            case Keyboard.PAGE_UP:
            {
                drawHeaderFocus(_focusedIndex, false);
                _focusedIndex = selectedIndex = (selectedIndex > 0 ?
                                selectedIndex - 1 :
                                numChildren - 1);
                drawHeaderFocus(_focusedIndex, true);
                event.stopPropagation();
                dispatchChangeEvent(prevValue, selectedIndex, event);
                break;
            }

            case Keyboard.HOME:
            {
                drawHeaderFocus(_focusedIndex, false);
                _focusedIndex = selectedIndex = 0;
                drawHeaderFocus(_focusedIndex, true);
                event.stopPropagation();
                dispatchChangeEvent(prevValue, selectedIndex, event);
                break;
            }

            case Keyboard.END:
            {
                drawHeaderFocus(_focusedIndex, false);
                _focusedIndex = selectedIndex = numChildren - 1;
                drawHeaderFocus(_focusedIndex, true);
                event.stopPropagation();
                dispatchChangeEvent(prevValue, selectedIndex, event);
                break;
            }

            case Keyboard.DOWN:
            case Keyboard.RIGHT:
            {
                drawHeaderFocus(_focusedIndex, false);
                _focusedIndex = (_focusedIndex < numChildren - 1
                                 ? _focusedIndex + 1
                                 : 0);
                drawHeaderFocus(_focusedIndex, true);
                event.stopPropagation();
                break;
            }

            case Keyboard.UP:
            case Keyboard.LEFT:
            {
                drawHeaderFocus(_focusedIndex, false);
                _focusedIndex = (_focusedIndex > 0 ?
                                 _focusedIndex - 1 :
                                 numChildren - 1);
                drawHeaderFocus(_focusedIndex, true);
                event.stopPropagation();
                break;
            }

            case Keyboard.SPACE:
            case Keyboard.ENTER:
            {
                event.stopPropagation();
                if (_focusedIndex != selectedIndex)
                {
                    selectedIndex = _focusedIndex;
                    dispatchChangeEvent(prevValue, selectedIndex, event);
                }
                break;
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Handles "addedToStage" event
     */
    private function addedToStageHandler(event:Event):void
    {
        if (historyManagementEnabled)
                HistoryManager.register(this);
    }
    
    /**
     *  @private
     *  Handles "removedFromStage" event
     */
    private function removedFromStageHandler(event:Event):void
    {
        HistoryManager.unregister(this);
    }
    
    /**
     *  @private
     */
    private function childAddHandler(event:ChildExistenceChangedEvent):void
    {
        childAddedOrRemoved = true;
        
        var child:DisplayObject = event.relatedObject;
        
        // Accordion creates all of its children initially invisible.
        // They are made as they become the selected child.
        child.visible = false;

        // Create the header associated with this child.
        createHeader(child, getChildIndex(child));

        // If the child's label or icon changes, Accordion needs to know so that
        // the header label can be updated. This event is handled by
        // labelChanged().
        // note: weak listeners
        child.addEventListener("labelChanged", labelChangedHandler, false, 0, true);
        child.addEventListener("iconChanged",  iconChangedHandler,  false, 0, true);

        // If we just created the first child and no selected index has
        // been proposed, then propose this child to be selected.
        if (numChildren == 1 && proposedSelectedIndex == -1)
        {
            selectedIndex = 0;

            // Select the new header.
            var newHeader:Button = getHeaderAt(0);
            newHeader.selected = true;

            // Focus the new header.
            _focusedIndex = 0;
            drawHeaderFocus(_focusedIndex, showFocusIndicator);
        }
        
        if (child as IAutomationObject);
            IAutomationObject(child).showInAutomationHierarchy = true;
    }

    /**
     *  @private
     */
    private function childRemoveHandler(event:ChildExistenceChangedEvent):void
    {
        childAddedOrRemoved = true;
        
        if (numChildren == 0)
            return;

        var child:DisplayObject = event.relatedObject;
        var oldIndex:int = selectedIndex;
        var newIndex:int;
        var index:int = getChildIndex(child);
        
        var nChildren:int = numChildren - 1;
            // number of children remaining after child has been removed

        rawChildren.removeChild(getHeaderAt(index));

        // Shuffle all higher numbered headers down.
        for (var i:int = index; i < nChildren; i++)
        {
            getHeaderAt(i + 1).name = HEADER_NAME_BASE + i;
        }

        // If we just deleted the only child, the accordion is now empty,
        // and no child is now selected.
        if (nChildren == 0)
        {
            // There's no need to go through all of commitSelectedIndex(),
            // and it wouldn't do the right thing, anyway, because
            // it bails immediately if numChildren is 0.
            newIndex = _focusedIndex = -1;
        }

        else if (index > selectedIndex)
        {
            return;
        }

        // If we deleted a child before the selected child, the
        // index of that selected child is now 1 less than it was,
        // but the selected child itself hasn't changed.
        else if (index < selectedIndex)
        {
            newIndex = oldIndex - 1;
        }

        // Now handle the case that we deleted the selected child
        // and there is another child that we must select.
        else if (index == selectedIndex)
        {
            // If it was the last child, select the previous one.
            // Otherwise, select the next one. This next child now
            // has the same index as the one we just deleted,
            if (index == nChildren)
            {
                newIndex = oldIndex - 1;
                
                // if something's selected, instantiate it
                if (newIndex != -1)
                    instantiateChild(Container(getChildAt(newIndex)));
            }
            else
            {
                newIndex = oldIndex;
                
                // can't do selectedChild because we need to actually do
                // newIndex + 1 because currently that's what the index 
                // of the child is (SDK-12622) since this one isn't
                // actually removed from the display list yet
                instantiateChild(Container(getChildAt(newIndex+1)));
            }

            // Select the new selected index header.
            var newHeader:Button = getHeaderAt(newIndex);
            newHeader.selected = true;
            
        }

        if (_focusedIndex > index)
        {
            _focusedIndex--;
            drawHeaderFocus(_focusedIndex, showFocusIndicator);
        }
        else if (_focusedIndex == index)
        {
            if (index == nChildren)
                _focusedIndex--;
            drawHeaderFocus(_focusedIndex, showFocusIndicator);
        }

        _selectedIndex = newIndex;

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));

    }

    /**
     *  @private
     *  Handles "labelChanged" event.
     */
    private function labelChangedHandler(event:Event):void
    {
        var child:DisplayObject = DisplayObject(event.target);
        var childIndex:int = getChildIndex(child);
        var header:Button = getHeaderAt(childIndex);
        header.label = Container(event.target).label;
    }

    /**
     *  @private
     *  Handles "iconChanged" event.
     */
    private function iconChangedHandler(event:Event):void
    {
        var child:DisplayObject = DisplayObject(event.target);
        var childIndex:int = getChildIndex(child);
        var header:Button = getHeaderAt(childIndex);
        header.setStyle("icon", Container(event.target).icon);
        //header.icon = Container(event.target).icon;
    }
}

}
