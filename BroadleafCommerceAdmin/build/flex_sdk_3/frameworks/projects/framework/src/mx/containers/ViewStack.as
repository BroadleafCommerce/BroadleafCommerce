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
import mx.automation.IAutomationObject;
import mx.core.Container;
import mx.core.ContainerCreationPolicy;
import mx.core.EdgeMetrics;
import mx.managers.IHistoryManagerClient;
import mx.core.IInvalidating;
import mx.core.IUIComponent;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.effects.Effect;
import mx.effects.EffectManager;
import mx.events.ChildExistenceChangedEvent;
import mx.events.EffectEvent;
import mx.events.FlexEvent;
import mx.events.IndexChangedEvent;
import mx.graphics.RoundedRectangle;
import mx.managers.HistoryManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the selected child container changes.
 *
 *  @eventType mx.events.IndexChangedEvent.CHANGE
 */
[Event(name="change", type="mx.events.IndexChangedEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/GapStyles.as"

/**
 *  Number of pixels between the container's bottom border and its content area.
 *  The default value is 0.
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the container's top border and its content area.
 *  The default value is 0.
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="autoLayout", kind="property")]
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

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]
[Exclude(name="scroll", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]
[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("ViewStack.png")]

/**
 *  A ViewStack navigator container consists of a collection of child
 *  containers stacked on top of each other, where only one child
 *  at a time is visible.
 *  When a different child container is selected, it seems to replace
 *  the old one because it appears in the same location.
 *  However, the old child container still exists; it is just invisible.
 *
 *  <p>A ViewStack container does not provide a user interface
 *  for selecting which child container is currently visible.
 *  Typically, you set its <code>selectedIndex</code> or
 *  <code>selectedChild</code> property in ActionScript in response to
 *  some user action. Alternately, you can associate a LinkBar, TabBar or ToggleButtonBar
 *  container with a ViewStack container to provide a navigation interface.
 *  To do so, specify the ViewStack container as the value of the
 *  <code>dataProvider</code> property of the LinkBar, TabBar or
 *  ToggleButtonBar container.</p>
 *
 *  <p>You might decide to use a more complex navigator container than the
 *  ViewStack container, such as a TabNavigator container or Accordion
 *  container. In addition to having a collection of child containers,
 *  these containers provide their own user interface controls
 *  for navigating between their children.</p>
 *
 *  <p>When you change the currently visible child container, 
 *  you can use the <code>hideEffect</code> property of the container being
 *  hidden and the <code>showEffect</code> property of the newly visible child
 *  container to apply an effect to the child containers.
 *  The ViewStack container waits for the <code>hideEffect</code> of the child
 *  container being hidden  to complete before it reveals the new child
 *  container. 
 *  You can interrupt a currently playing effect if you change the 
 *  <code>selectedIndex</code> property of the ViewStack container 
 *  while an effect is playing.</p>
 *
 *  <p>The ViewStack container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>The width and height of the initial active child.</td>
 *        </tr>
 *        <tr>
 *           <td>Container resizing rules</td>
 *           <td>By default, ViewStack containers are sized only once to fit the size of the 
 *               first child container. They do not resize when you navigate to other child 
 *               containers. To force ViewStack containers to resize when you navigate 
 *               to a different child container, set the resizeToContent property to true.</td>
 *        </tr>
 *        <tr>
 *           <td>Child sizing rules</td>
 *           <td>Children are sized to their default size. If the child is larger than the ViewStack 
 *               container, it is clipped. If the child is smaller than the ViewStack container, 
 *               it is aligned to the upper-left corner of the ViewStack container.</td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>0 pixels for top, bottom, left, and right values.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ViewStack&gt;</code> tag inherits the
 *  tag attributes of its superclass, with the exception of scrolling-related
 *  attributes, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:ViewStack
 *    <b>Properties</b>
 *    historyManagementEnabled="false|true"
 *    resizeToContent="false|true"
 *    selectedIndex="0"
 *    
 *    <b>Styles</b>
 *    horizontalGap="8"
 *    paddingBottom="0"
 *    paddingTop="0"
 *    verticalGap="6"
 *    
 *    <b>Events</b>
 *    change="<i>No default</i>"
 *    &gt;
 *      ...
 *      <i>child tags</i>
 *      ...
 *  &lt;/mx:ViewStack&gt;
 *  </pre>
 *
 *  @includeExample examples/ViewStackExample.mxml
 *
 *  @see mx.controls.LinkBar
 *  @see mx.managers.HistoryManager
 *  @see mx.managers.LayoutManager
 */
public class ViewStack extends Container implements IHistoryManagerClient
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
    public function ViewStack()
    {
        super();

        addEventListener(ChildExistenceChangedEvent.CHILD_ADD, childAddHandler);
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
     */
    private var needToInstantiateSelectedChild:Boolean = false;

    /**
     *  @private
     *  This flag gets set when selectedIndex is set.
     *  Later, when measure()
     *  is called, it causes the HistoryManager to save the state.
     */
    private var bSaveState:Boolean = false;

    /**
     *  @private
     *  This flag gets set by loadState().
     *  It prevents the newly restored state from being saved.
     */
    private var bInLoadState:Boolean = false;
    
    /**
     *  @private
     *  True until commitProperties has been called at least once.
     */
    private var firstTime:Boolean = true;

    /**
     *  @private
     *  We'll measure ourselves once and then store the results here
     *  for the lifetime of the ViewStack
     */
    mx_internal var vsMinWidth:Number;
    mx_internal var vsMinHeight:Number;
    mx_internal var vsPreferredWidth:Number;
    mx_internal var vsPreferredHeight:Number;

    /**
     *  @private
     *  Remember which child has an overlay mask, if any.
     */
    private var overlayChild:Container;

    /**
     *  @private
     *  Keep track of the overlay's targetArea
     */
    private var overlayTargetArea:RoundedRectangle;

    /**
     *  @private
     *  Store the last selectedIndex
     */
    private var lastIndex:int = -1;

    /**
     *  @private
     *  Whether a change event has to be dispatched in commitProperties()
     */
    private var dispatchChangeEventPending:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoLayout
    //----------------------------------

    /**
     *  @private
     *  autoLayout is always true for ViewStack.
     */
    override public function get autoLayout():Boolean
    {
        return true;
    }

    /**
     *  @private
     *  autoLayout is always true for ViewStack
     *  and can't be changed by this setter.
     *
     *  We can probably find a way to make autoLayout work with Accordion
     *  and ViewStack, but right now there are problems if deferred
     *  instantiation runs at the same time as an effect. (Bug 79174)
     */
    override public function set autoLayout(value:Boolean):void
    {
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  horizontalScrollPolicy is always OFF for ViewStack.
     */
    override public function get horizontalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     *  horizontalScrollPolicy is always OFF for ViewStack
     *  and can't be changed by this setter.
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
     *  verticalScrollPolicy is always OFF for ViewStack.
     */
    override public function get verticalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     *  verticalScrollPolicy is always OFF for ViewStack
     *  and can't be changed by this setter.
     */
    override public function set verticalScrollPolicy(value:String):void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  contentHeight
    //----------------------------------

    /**
     *  The height of the area, in pixels, in which content is displayed.
     *  You can override this getter if your content
     *  does not occupy the entire area of the ViewStack container.
     */
    protected function get contentHeight():Number
    {
        var vm:EdgeMetrics = viewMetricsAndPadding;
        return unscaledHeight - vm.top - vm.bottom;
    }

    //----------------------------------
    //  contentWidth
    //----------------------------------

    /**
     *  The width of the area, in pixels, in which content is displayed.
     *  You can override this getter if your content
     *  does not occupy the entire area of the ViewStack container.
     */
    protected function get contentWidth():Number
    {
        var vm:EdgeMetrics = viewMetricsAndPadding;
        return unscaledWidth - vm.left - vm.right;
    }

    //----------------------------------
    //  contentX
    //----------------------------------

    /**
     *  The x coordinate of the area of the ViewStack container
     *  in which content is displayed, in pixels.
     *  The default value is equal to the value of the
     *  <code>paddingLeft</code> style property,
     *  which has a default value of 0.
     *
     *  Override the <code>get()</code> method if you do not want
     *  your content to start layout at x = 0.
     */
    protected function get contentX():Number
    {
        return getStyle("paddingLeft");
    }

    //----------------------------------
    //  contentY
    //----------------------------------

    /**
     *  The y coordinate of the area of the ViewStack container
     *  in which content is displayed, in pixels.
     *  The default value is equal to the value of the
     *  <code>paddingTop</code> style property,
     *  which has a default value of 0.
     *
     *  Override the <code>get()</code> method if you do not want
     *  your content to start layout at y = 0.
     */
    protected function get contentY():Number
    {
        return getStyle("paddingTop");
    }

    //----------------------------------
    //  historyManagementEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the historyManagementEnabled property.
     */
    mx_internal var _historyManagementEnabled:Boolean = false;

    /**
     *  @private
     */
    private var historyManagementEnabledChanged:Boolean = false;

    [Inspectable(defaultValue="true")]

    /**
     *  If <code>true</code>, enables history management
     *  within this ViewStack container.
     *  As the user navigates from one child to another,
     *  the browser remembers which children were visited.
     *  The user can then click the browser's Back and Forward buttons
     *  to move through this navigation history.
     *
     *  @default false
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

    /**
     *  If <code>true</code>, the ViewStack container automatically
     *  resizes to the size of its current child.
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
     *  The default is a reference to the first child.
     *  If there are no children, this property is <code>null</code>.
     * 
     *  <p><strong>Note:</strong> You can only set this property in an
     *  ActionScript statement, not in MXML.</p>
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
    public function set selectedChild(
                            value:Container):void
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
     *  Storage for the selectedIndex property.
     */
    private var _selectedIndex:int = -1;

    /**
     *  @private
     */
    private var proposedSelectedIndex:int = -1;

    /**
     *  @private
     */
    private var initialSelectedIndex:int = -1;

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General")]

    /**
     *  The zero-based index of the currently visible child container.
     *  Child indexes are in the range 0, 1, 2, ..., n - 1,
     *  where <i>n</i> is the number of children.
     *  The default value is 0, corresponding to the first child.
     *  If there are no children, the value of this property is <code>-1</code>.
     * 
     *  <p><strong>Note:</strong> When you add a new child to a ViewStack 
     *  container, the <code>selectedIndex</code> property is automatically 
     *  adjusted, if necessary, so that the selected child remains selected.</p>
     */
    public function get selectedIndex():int
    {
        return proposedSelectedIndex == -1 ?
               _selectedIndex :
               proposedSelectedIndex;
    }

    /**
     *  @private
     */
    public function set selectedIndex(value:int):void
    {
        // Bail if the index isn't changing.
        if (value == selectedIndex)
            return;

        // Propose the specified value as the new value for selectedIndex.
        // It gets applied later when measure() calls commitSelectedIndex().
        // The proposed value can be "out of range", because the children
        // may not have been created yet, so the range check is handled
        // in commitSelectedIndex(), not here. Other calls to this setter
        // can change the proposed index before it is committed. Also,
        // childAddHandler() proposes a value of 0 when it creates the first
        // child, if no value has yet been proposed.
        proposedSelectedIndex = value;
        invalidateProperties();

        // Set a flag which will cause the HistoryManager to save state
        // the next time measure() is called.
        if (historyManagementEnabled && _selectedIndex != -1 && !bInLoadState)
            bSaveState = true;

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

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

        if (proposedSelectedIndex != -1)
        {
            commitSelectedIndex(proposedSelectedIndex);
            proposedSelectedIndex = -1;
        }

        if (needToInstantiateSelectedChild)
        {
            instantiateSelectedChild();
            needToInstantiateSelectedChild = false;
        }

        // Dispatch the change event only after the child has been
        // instantiated.
        if (dispatchChangeEventPending)
        {
            dispatchChangeEvent(lastIndex, selectedIndex);
            dispatchChangeEventPending = false;
        }
        
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
     *  Calculates the default sizes and minimum and maximum values of the
     *  ViewStack container.
     *  For more information about the <code>measure()</code> method,
     *  see the <code>UIComponent.measure()</code> method.
     *
     *  <p>The default size of a ViewStack container is the default size
     *  of its currently selected child, plus the padding and borders.
     *  If the ViewStack container has no children, its default size
     *  is just large enough for its padding and borders.</p>
     *
     *  <p>The minimum size of a ViewStack container is the minimum size
     *  of its currently selected child, plus the padding and borders.
     *  If the ViewStack container has no children, its minimum size
     *  is just large enough for its padding and borders.</p>
     *
     *  <p>This method does not change the maximum size of a ViewStack
     *  container - it remains unbounded.</p>
     * 
     *  @see mx.core.UIComponent#measure()
     */
    override protected function measure():void
    {
        super.measure();

        // A ViewStack measures itself based only on its selectedChild.
        // The minimum, maximum, and preferred sizes are those of the
        // selected child, plus the borders and margins.
        var minWidth:Number = 0;
        var minHeight:Number = 0;
        var preferredWidth:Number = 0;
        var preferredHeight:Number = 0;

        // Only measure once.  Thereafter, we'll just use cached values.
        //
        // We need to copy the cached values into the measured fields
        // again to handle the case where scaleX or scaleY is not 1.0.
        // When the ViewStack is zoomed, code in UIComponent.measureSizes
        // scales the measuredWidth/Height values every time that
        // measureSizes is called.  (bug 100749)
        if (vsPreferredWidth && !_resizeToContent)
        {
            measuredMinWidth = vsMinWidth;
            measuredMinHeight = vsMinHeight;
            measuredWidth = vsPreferredWidth;
            measuredHeight = vsPreferredHeight;
            return;
        }

        if (numChildren > 0 && selectedIndex != -1)
        {
            var child:Container =
                Container(getChildAt(selectedIndex));

            minWidth = child.minWidth;
            preferredWidth = child.getExplicitOrMeasuredWidth();

            minHeight = child.minHeight;
            preferredHeight = child.getExplicitOrMeasuredHeight();
        }

        var vm:EdgeMetrics = viewMetricsAndPadding;

        var wPadding:Number = vm.left + vm.right;
        minWidth += wPadding;
        preferredWidth += wPadding;

        var hPadding:Number = vm.top + vm.bottom;
        minHeight += hPadding;
        preferredHeight += hPadding;

        measuredMinWidth = minWidth;
        measuredMinHeight = minHeight;
        measuredWidth = preferredWidth;
        measuredHeight = preferredHeight;

        // If we're called before instantiateSelectedChild, then bail.
        // We'll be called again later (instantiateSelectedChild calls
        // invalidateSize), and we don't want to load values into the
        // cache until we're fully initialized.  (bug 102639)
        // This check was moved from the beginning of this function to
        // here to fix bug 103665.
        if (selectedChild && Container(selectedChild).numChildrenCreated == -1)
            return;

        // Don't remember sizes if we don't have any children
        if (numChildren == 0)
            return;

        vsMinWidth = minWidth;
        vsMinHeight = minHeight;
        vsPreferredWidth = preferredWidth;
        vsPreferredHeight = preferredHeight;
    }

    /**
     *  Responds to size changes by setting the positions and sizes
     *  of this container's children.
     *  For more information about the <code>updateDisplayList()</code> method,
     *  see the <code>UIComponent.updateDisplayList()</code> method.
     *
     *  <p>Only one of its children is visible at a time, therefore,
     *  a ViewStack container positions and sizes only that child.</p>
     *
     *  <p>The selected child is positioned in the ViewStack container's
     *  upper-left corner, and allows for the ViewStack container's
     *  padding and borders. </p>
     *
     *  <p>If the selected child has a percentage <code>width</code> or
     *  <code>height</code> value, it is resized in that direction
     *  to fill the specified percentage of the ViewStack container's
     *  content area (i.e., the region inside its padding).</p>
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

        var nChildren:int = numChildren;
        var w:Number = contentWidth;
        var h:Number = contentHeight;
        var left:Number = contentX;
        var top:Number = contentY;

        // Stretch the selectedIndex to fill our size
        if (selectedIndex != -1)
        {
            var child:Container =
                Container(getChildAt(selectedIndex));

            var newWidth:Number = w;
            var newHeight:Number = h;

            if (!isNaN(child.percentWidth))
            {
                if (newWidth > child.maxWidth)
                    newWidth = child.maxWidth;
            }
            else
            {
                if (newWidth > child.explicitWidth)
                    newWidth = child.explicitWidth;
            }

            if (!isNaN(child.percentHeight))
            {
                if (newHeight > child.maxHeight)
                    newHeight = child.maxHeight;
            }
            else
            {
                if (newHeight > child.explicitHeight)
                    newHeight = child.explicitHeight;
            }

            // Don't send events for the size/move. The set visible below
            if (child.width != newWidth || child.height != newHeight)
                child.setActualSize(newWidth, newHeight);
            if (child.x != left || child.y != top)
                child.move(left, top);

            // Now that the child is properly sized and positioned it
            // can be shown.
            child.visible = true;
        }
    }

    /**
     *  @private
     *  When asked to create an overlay mask, create it on the selected child
     *  instead. That way, the chrome around the edge of the ViewStack
     *  (e.g. the tabs in a TabNavigator) is not occluded by the overlay mask
     *  (Bug 99029)
     */
    override mx_internal function addOverlay(color:uint,
                                        targetArea:RoundedRectangle = null):void
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
        overlayChild = selectedChild;
        if (!overlayChild)
            return;

        overlayColor = color;
        overlayTargetArea = targetArea;

        if (selectedChild &&
            selectedChild.numChildrenCreated == -1)
            // No children have been created
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
     */
    override mx_internal function removeOverlay():void
    {
        if (overlayChild)
        {
            UIComponent(overlayChild).removeOverlay();
            overlayChild = null;
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
    override mx_internal function setActualCreationPolicies(policy:String):void
    {
        super.setActualCreationPolicies(policy);

        // If the creation policy is switched to ContainerCreationPolicy.ALL
        // and our createComponentsFromDescriptors() method has already been
        // called (we've created our children but not all our grandchildren),
        // then create all our grandchildren now. (Bug 99160)
        if (policy == ContainerCreationPolicy.ALL && numChildren > 0)
        {
            for (var i:int = 0; i < numChildren; i++)
            {
                var containerChild:Container =
                    getChildAt(i) as Container;

                if (containerChild && containerChild.numChildrenCreated == -1)
                    containerChild.createComponentsFromDescriptors();
            }
        }
    }

    /**
     *  @private
     */
    override public function createComponentsFromDescriptors(
                                    recurse:Boolean = true):void
    {
        // The easiest way to handle the ContainerCreationPolicy.ALL policy
        // is to let Container's implementation of
        // createComponentsFromDescriptors() handle it.
        if (actualCreationPolicy == ContainerCreationPolicy.ALL)
        {
            super.createComponentsFromDescriptors();
            return;
        }

        // If the policy is ContainerCreationPolicy.AUTO, ViewStack
        // instantiates its children immediately, but not any grandchildren.
        // The children of the selected child will get created in
        // instantiateSelectedChild().
        // Why not create the grandchildren of the selected child by calling
        //    createComponentFromDescriptor(childDescriptors[i],
        //                                  i == selectedIndex);
        // in the loop below? Because one of this ViewStacks's childDescriptors
        // may be for a Repeater, in which case the following loop over the
        // childDescriptors is not the same as a loop over the children.
        // In particular, selectedIndex is supposed to specify the nth
        // child, not the nth childDescriptor, and the 2nd parameter of
        // createComponentFromDescriptor() should make the recursion happen
        // on the nth child, not the nth childDescriptor.
        var numChildrenBefore:int = numChildren;
        var n:int = childDescriptors ? childDescriptors.length : 0;
        for (var i:int = 0; i < n; i++)
        {
            createComponentFromDescriptor(childDescriptors[i], false);
        }

        numChildrenCreated = numChildren - numChildrenBefore;

        processedDescriptors = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: IHistoryManagerClient
    //
    //--------------------------------------------------------------------------

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
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Commits the selected index. This function is called during the commit 
     *  properties phase when the selectedIndex (or selectedItem) property
     *  has changed.
     */
    protected function commitSelectedIndex(newIndex:int):void
    {
        // The selectedIndex must be -1 if there are no children,
        // even if a selectedIndex has been proposed.
        if (numChildren == 0)
        {
            _selectedIndex = -1;
            return;
        }

        // If there are children, ensure that the new index is in bounds.
        if (newIndex < 0)
            newIndex = 0;
        else if (newIndex > numChildren - 1)
            newIndex = numChildren - 1;

        // Stop all currently playing effects
        if (lastIndex != -1 && lastIndex < numChildren)
            Container(getChildAt(lastIndex)).endEffectsStarted();
        
        if (_selectedIndex != -1)
            selectedChild.endEffectsStarted();

        // Remember the old index.
        lastIndex = _selectedIndex;

        // Bail if the index isn't changing.
        if (newIndex == lastIndex)
            return;

        // Commit the new index.
        _selectedIndex = newIndex;

        // Remember our initial selected index so we can
        // restore to our default state when the history
        // manager requests it.
        if (initialSelectedIndex == -1)
            initialSelectedIndex = _selectedIndex;

        // Only dispatch a change event if we're going to and from
        // a valid index
        if (lastIndex != -1 && newIndex != -1)
            dispatchChangeEventPending = true;

        var listenForEffectEnd:Boolean = false;

        if (lastIndex != -1 && lastIndex < numChildren)
        {
            var currentChild:Container = Container(getChildAt(lastIndex));

            currentChild.setVisible(false); // Hide the current child

            if (currentChild.getStyle("hideEffect"))
            {
                var hideEffect:Effect = EffectManager.lastEffectCreated; // This should be the hideEffect

                if (hideEffect)
                {
                    hideEffect.addEventListener(EffectEvent.EFFECT_END, hideEffectEndHandler);
                    listenForEffectEnd = true;
                }
            }
        }

        // If we don't have to wait for a hide effect to finish
        if (!listenForEffectEnd)
            hideEffectEndHandler(null);
    }

    private function hideEffectEndHandler(event:EffectEvent):void
    {
        if (event)
            event.currentTarget.removeEventListener(EffectEvent.EFFECT_END, hideEffectEndHandler);

        // Give any change handlers a chance to act before we
        // instantiate our pane (which eats up all the processing cycles)
        needToInstantiateSelectedChild = true;
        invalidateProperties();

        if (bSaveState)
        {
            HistoryManager.save();
            bSaveState = false;
        }
    }

    /**
     *  @private
     */
    private function instantiateSelectedChild():void
    {
        if (!selectedChild)
            return;

        // Performance optimization: don't call createComponents if we know
        // that createComponents has already been called.
        if (selectedChild && selectedChild.numChildrenCreated == -1)
        {
            if (initialized)  // Only listen if the ViewStack has already been initialized.
                selectedChild.addEventListener(FlexEvent.CREATION_COMPLETE,childCreationCompleteHandler);
            selectedChild.createComponentsFromDescriptors(true);
        }

        // Do the initial measurement/layout pass for the
        // newly-instantiated descendants.

        if (selectedChild is IInvalidating)
            IInvalidating(selectedChild).invalidateSize();

        invalidateSize();
        invalidateDisplayList();
    }

    /**
     *  @private
     */
    private function dispatchChangeEvent(oldIndex:int, newIndex:int):void
    {
        var event:IndexChangedEvent =
            new IndexChangedEvent(IndexChangedEvent.CHANGE);
        event.oldIndex = oldIndex;
        event.newIndex = newIndex;
        event.relatedObject = getChildAt(newIndex);
        dispatchEvent(event);
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
     *  Called when we are running a Dissolve effect
     *  and the initialize event has been dispatched
     *  or the children already exist
     */
    private function initializeHandler(event:FlexEvent):void
    {
        overlayChild.removeEventListener(FlexEvent.INITIALIZE,
                                         initializeHandler);

        UIComponent(overlayChild).addOverlay(overlayColor, overlayTargetArea);
    }

    /**
     *  @private
     *  Handles when the new selectedChild has finished being created.
     */
    private function childCreationCompleteHandler(event:FlexEvent):void
    {
        event.target.removeEventListener(FlexEvent.CREATION_COMPLETE,childCreationCompleteHandler);
        event.target.dispatchEvent(new FlexEvent(FlexEvent.SHOW));

    }

    /**
     *  @private
     */
    private function childAddHandler(event:ChildExistenceChangedEvent):void
    {
        var child:DisplayObject = event.relatedObject;
        var index:int = getChildIndex(child);

        if (child is IUIComponent)
        {
            var uiChild:IUIComponent = IUIComponent(child);
            // ViewStack creates all of its children initially invisible.
            // They are made as they become the selected child.
            uiChild.visible = false;
        }

        // If we just created the first child and no selected index has
        // been proposed, then propose this child to be selected.
        if (numChildren == 1 && proposedSelectedIndex == -1)
        {
            proposedSelectedIndex = 0;
            invalidateProperties();
        }           
        else if (index <= _selectedIndex && numChildren > 1)
        {
            selectedIndex++;
        }

        if (child as IAutomationObject);
            IAutomationObject(child).showInAutomationHierarchy = true;
        
    }

    /**
     *  @private
     *  When a child is removed, adjust the selectedIndex such that the current
     *  child remains selected; or if the current child was removed, then the
     *  next (or previous) child gets automatically selected; when the last
     *  remaining child is removed, the selectedIndex is set to -1.
     */
    private function childRemoveHandler(event:ChildExistenceChangedEvent):void
    {
        var child:DisplayObject = event.relatedObject;
        var index:int = getChildIndex(child);
        
        // Handle the simple case.
        if (index > selectedIndex)
            return;

        var currentSelectedIndex:int = selectedIndex;

        // This matches one of the two conditions:
        // 1. a view before the current was deleted, or
        // 2. the current view was deleted and it was also
        //    at the end of the stack.
        // In both cases, we need to decrement selectedIndex.
        if (index < currentSelectedIndex ||
            currentSelectedIndex == (numChildren - 1))
        {
            // If the selectedIndex was already 0, it should go to -1.
            // -1 is a special value; in order to avoid runtime errors
            // in various methods, we need to skip the range checking in
            // commitSelectedIndex() and set _selectedIndex explicitly here.
            if (currentSelectedIndex == 0)
            {
                selectedIndex = -1;
                _selectedIndex = -1;
            }
            else
            {
                selectedIndex--;
            }
        }
        else if (index == currentSelectedIndex)
        {
            // If we're deleting the currentSelectedIndex and there is another
            // child after it, it will become the new selected child so we
            // need to make sure it is instantiated.
            needToInstantiateSelectedChild = true;
            invalidateProperties();
        }
    }
}

}

