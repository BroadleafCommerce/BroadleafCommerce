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

package mx.core
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Graphics;
import flash.display.InteractiveObject;
import flash.display.Loader;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.text.TextField;
import flash.text.TextLineMetrics;
import flash.ui.Keyboard;
import flash.utils.getDefinitionByName;

import mx.binding.BindingManager;
import mx.controls.Button;
import mx.controls.HScrollBar;
import mx.controls.VScrollBar;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.scrollClasses.ScrollBar;
import mx.events.ChildExistenceChangedEvent;
import mx.events.FlexEvent;
import mx.events.IndexChangedEvent;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.events.ScrollEventDirection;
import mx.graphics.RoundedRectangle;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerContainer;
import mx.managers.ILayoutManagerClient;
import mx.managers.ISystemManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.ISimpleStyleClient;
import mx.styles.IStyleClient;
import mx.styles.StyleManager;
import mx.styles.StyleProtoChain;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched after a child has been added to a container.
 *
 *  <p>The childAdd event is dispatched when the <code>addChild()</code>
 *  or <code>addChildAt()</code> method is called.
 *  When a container is first created, the <code>addChild()</code>
 *  method is automatically called for each child component declared
 *  in the MXML file.
 *  The <code>addChildAt()</code> method is automatically called
 *  whenever a Repeater object adds or removes child objects.
 *  The application developer may also manually call these
 *  methods to add new children.</p>
 *
 *  <p>At the time when this event is sent, the child object has been
 *  initialized, but its width and height have not yet been calculated,
 *  and the child has not been drawn on the screen.
 *  If you want to be notified when the child has been fully initialized
 *  and rendered, then register as a listener for the child's
 *  <code>creationComplete</code> event.</p>
 *
 *  @eventType mx.events.ChildExistenceChangedEvent.CHILD_ADD
 */
[Event(name="childAdd", type="mx.events.ChildExistenceChangedEvent")]

/**
 *  Dispatched after the index (among the container children) 
 *  of a container child changes.
 *  This event is only dispatched for the child specified as the argument to 
 *  the <code>setChildIndex()</code> method; it is not dispatched 
 *  for any other child whose index changes as a side effect of the call 
 *  to the <code>setChildIndex()</code> method.
 *
 *  <p>The child's index is changed when the
 *  <code>setChildIndex()</code> method is called.</p>
 *
 *  @eventType mx.events.IndexChangedEvent.CHILD_INDEX_CHANGE
 */
[Event(name="childIndexChange", type="mx.events.IndexChangedEvent")]

/**
 *  Dispatched before a child of a container is removed.
 *
 *  <p>This event is delivered when any of the following methods is called:
 *  <code>removeChild()</code>, <code>removeChildAt()</code>,
 *  or <code>removeAllChildren()</code>.</p>
 *
 *  @eventType mx.events.ChildExistenceChangedEvent.CHILD_REMOVE
 */
[Event(name="childRemove", type="mx.events.ChildExistenceChangedEvent")]

/**
 *  Dispatched when the <code>data</code> property changes.
 *
 *  <p>When a container is used as a renderer in a List or other components,
 *  the <code>data</code> property is used pass to the container 
 *  the data to display.</p>
 *
 *  @eventType mx.events.FlexEvent.DATA_CHANGE
 */
[Event(name="dataChange", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the user manually scrolls the container.
 *
 *  <p>The event is dispatched when the scroll position is changed using
 *  either the mouse (e.g. clicking on the scrollbar's "down" button)
 *  or the keyboard (e.g., clicking on the down-arrow key).
 *  However, this event is not dispatched if the scroll position
 *  is changed programatically (e.g., setting the value of the
 *  <code>horizontalScrollPosition</code> property).
 *  The <code>viewChanged</code> event is delivered whenever the
 *  scroll position is changed, either manually or programatically.</p>
 *
 *  <p>At the time when this event is dispatched, the scrollbar has
 *  been updated to the new position, but the container's child objects
 *  have not been shifted to reflect the new scroll position.</p>
 *
 *  @eventType mx.events.ScrollEvent.SCROLL
 */
[Event(name="scroll", type="mx.events.ScrollEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/BarColorStyle.as"
include "../styles/metadata/BorderStyles.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  If a background image is specified, this style specifies
 *  whether it is fixed with regard to the viewport (<code>"fixed"</code>)
 *  or scrolls along with the content (<code>"scroll"</code>).
 *
 *  @default "scroll"
 */
[Style(name="backgroundAttachment", type="String", inherit="no")]

/**
 *  The alpha value for the overlay that is placed on top of the
 *  container when it is disabled.
 */
[Style(name="disabledOverlayAlpha", type="Number", inherit="no")]

/**
 *  The name of the horizontal scrollbar style.
 *
 *  @default undefined
 */
[Style(name="horizontalScrollBarStyleName", type="String", inherit="no")]

/**
 *  The name of the vertical scrollbar style.
 *
 *  @default undefined
 */
[Style(name="verticalScrollBarStyleName", type="String", inherit="no")]

/**
 *  Number of pixels between the container's bottom border
 *  and the bottom of its content area.
 *
 *  @default 0
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the container's top border
 *  and the top of its content area.
 *
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

[ResourceBundle("core")]

/**
 *  The Container class is an abstract base class for components that
 *  controls the layout characteristics of child components.
 *  You do not create an instance of Container in an application.
 *  Instead, you create an instance of one of Container's subclasses,
 *  such as Canvas or HBox.
 *
 *  <p>The Container class contains the logic for scrolling, clipping,
 *  and dynamic instantiation. 
 *  It contains methods for adding and removing children.
 *  It also contains the <code>getChildAt()</code> method, and the logic
 *  for drawing the background and borders of containers.</p>
 *
 *  @mxml
 *
 *  Flex Framework containers inherit the following attributes from the Container
 *  class:</p>
 *
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *    <strong>Properties</strong>
 *    autoLayout="true|false"
 *    clipContent="true|false"
 *    creationIndex="undefined"
 *    creationPolicy="auto|all|queued|none"
 *    defaultButton="<i>No default</i>"
 *    horizontalLineScrollSize="5"
 *    horizontalPageScrollSize="0"
 *    horizontalScrollBar="null"
 *    horizontalScrollPolicy="auto|on|off"
 *    horizontalScrollPosition="0"
 *    icon="undefined"
 *    label=""
 *    verticalLineScrollSize="5"
 *    verticalPageScrollSize="0"
 *    verticalScrollBar="null"
 *    verticalScrollPolicy="auto|on|off"
 *    verticalScrollPosition="0"
 * 
 *    <strong>Styles</strong>
 *    backgroundAlpha="1.0"
 *    backgroundAttachment="scroll"
 *    backgroundColor="undefined"
 *    backgroundDisabledColor="undefined"
 *    backgroundImage="undefined"
 *    backgroundSize="auto" 
 *    <i>    For the Application container only,</i> backgroundSize="100%"
 *    barColor="undefined"
 *    borderColor="0xAAB3B3"
 *    borderSides="left top right bottom"
 *    borderSkin="mx.skins.halo.HaloBorder"
 *    borderStyle="inset"
 *    borderThickness="1"
 *    color="0x0B333C"
 *    cornerRadius="0"
 *    disabledColor="0xAAB3B3"
 *    disbledOverlayAlpha="undefined"
 *    dropShadowColor="0x000000"
 *    dropShadowEnabled="false"
 *    fontAntiAliasType="advanced"
 *    fontfamily="Verdana"
 *    fontGridFitType="pixel"
 *    fontSharpness="0""
 *    fontSize="10"
 *    fontStyle="normal"
 *    fontThickness="0"
 *    fontWeight="normal"
 *    horizontalScrollBarStyleName="undefined"
 *    paddingBottom="0"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    paddingTop="0"
 *    shadowDirection="center"
 *    shadowDistance="2"
 *    textAlign="left"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    verticalScrollBarStyleName="undefined"
 * 
 *    <strong>Events</strong>
 *    childAdd="<i>No default</i>"
 *    childIndexChange="<i>No default</i>"
 *    childRemove="<i>No default</i>"
 *    dataChange="<i>No default</i>"
 *    scroll="<i>No default</i>"
 *    &gt;
 *      ...
 *      <i>child tags</i>
 *      ...
 *  &lt;/mx:<i>tagname</i>&gt;
 *  </pre>
 */
public class Container extends UIComponent
                       implements IContainer, IDataRenderer, 
                       IFocusManagerContainer, IListItemRenderer,
                       IRawChildrenContainer
{
    include "../core/Version.as"

    //--------------------------------------------------------------------------
    //
    //  Notes: Child management
    //
    //--------------------------------------------------------------------------

    /*

        Although at the level of a Flash DisplayObjectContainer, all
        children are equal, in a Flex Container some children are "more
        equal than others". (George Orwell, "Animal Farm")

        In particular, Flex distinguishes between content children and
        non-content (or "chrome") children. Content children are the kind
        that can be specified in MXML. If you put several controls
        into a VBox, those are its content children. Non-content children
        are the other ones that you get automatically, such as a
        background/border, scrollbars, the titlebar of a Panel,
        AccordionHeaders, etc.

        Most application developers are uninterested in non-content children,
        so Container overrides APIs such as numChildren and getChildAt()
        to deal only with content children. For example, Container, keeps
        its own _numChildren counter.

        Container assumes that content children are contiguous, and that
        non-content children come before or after the content children.
        In order words, Container partitions DisplayObjectContainer's
        index range into three parts:

        A B C D E F G H I
        0 1 2 3 4 5 6 7 8    <- index for all children
              0 1 2 3        <- index for content children

        The content partition contains the content children D E F G.
        The pre-content partition contains the non-content children
        A B C that always stay before the content children.
        The post-content partition contains the non-content children
        H I that always stay after the content children.

        Container maintains two state variables, _firstChildIndex
        and _numChildren, which keep track of the partitioning.
        In this example, _firstChildIndex would be 3 and _numChildren
        would be 4.

    */

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  See changedStyles, below
     */
    private static const MULTIPLE_PROPERTIES:String = "<MULTIPLE>";

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Container()
    {
        super();

        // By default, containers cannot receive focus but their children can.
        tabChildren = true;
        tabEnabled = false;
        
        showInAutomationHierarchy = false;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  Child creation vars
    //----------------------------------

    /**
     *  The creation policy of this container. 
     *  This property is useful when the container inherits its creation policy 
     *  from its parent container.
     */
    protected var actualCreationPolicy:String;

    /**
     *  @private
     */
    private var numChildrenBefore:int;

    /**
     *  @private
     */
    private var recursionFlag:Boolean = true;

    //----------------------------------
    //  Layout vars
    //----------------------------------

    /**
     *  @private
     *  Remember when a child has been added or removed.
     *  When that occurs, we want to run the LayoutManager
     *  (even if autoLayout is false).
     */
    private var forceLayout:Boolean = false;

    /**
     *  @private
     */
    mx_internal var doingLayout:Boolean = false;

    //----------------------------------
    //  Style vars
    //----------------------------------

    /**
     *  @private
     *  If this value is non-null, then we need to recursively notify children
     *  that a style property has changed.  If one style property has changed,
     *  this field holds the name of the style that changed.  If multiple style
     *  properties have changed, then the value of this field is
     *  Container.MULTIPLE_PROPERTIES.
     */
    private var changedStyles:String = null;

    //----------------------------------
    //  Scrolling vars
    //----------------------------------

    /**
     *  @private
     */
    private var _creatingContentPane:Boolean = false;

    /**
     *  Containers use an internal content pane to control scrolling. 
     *  The <code>creatingContentPane</code> is <code>true</code> while the container is creating 
     *  the content pane so that some events can be ignored or blocked.
     */
    public function get creatingContentPane():Boolean
    {
        return _creatingContentPane;
    }
    public function set creatingContentPane(value:Boolean):void
    {
        _creatingContentPane = value;
    }

    /**
     *  @private
     *  A box that takes up space in the lower right corner,
     *  between the horizontal and vertical scrollbars.
     */
    protected var whiteBox:Shape;

    /**
     *  @private
     */
    mx_internal var contentPane:Sprite = null;

    /**
     *  @private
     *  Flags that remember what work to do during the next updateDisplayList().
     */
    private var scrollPropertiesChanged:Boolean = false;
    private var scrollPositionChanged:Boolean = true;
    private var horizontalScrollPositionPending:Number;
    private var verticalScrollPositionPending:Number;

    /**
     *  @private
     *  Cached values describing the total size of the content being scrolled
     *  and the size of the area in which the scrolled content is displayed.
     */
    private var scrollableWidth:Number = 0;
    private var scrollableHeight:Number = 0;
    private var viewableWidth:Number = 0;
    private var viewableHeight:Number = 0;

    //----------------------------------
    //  Other vars
    //----------------------------------

    /**
     *  @private
     *  The border/background object.
     */
    mx_internal var border:IFlexDisplayObject;

    /**
     *  @private
     *  Sprite used to block user input when the container is disabled.
     */
    mx_internal var blocker:Sprite;

    /**
     *  @private
     *  Keeps track of the number of mouse events we are listening for
     */
    private var mouseEventReferenceCount:int = 0;

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
     *  The baselinePosition of a Container is calculated
     *  as if there was a UITextField using the Container's styles
     *  whose top is at viewMetrics.top.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            // If we have a verticalAlignment of top,
            // then return the baseline of our first child
            if (getStyle("verticalAlign") == "top" && numChildren > 0)
            {
                var child:IUIComponent = getChildAt(0) as IUIComponent;
                if (child)
                    return child.y + child.baselinePosition;
            }
    
            return super.baselinePosition;
        }
        
        if (!validateBaselinePosition())
            return NaN;

        // Unless the height is very small, the baselinePosition
        // of a generic Container is calculated as if there was
        // a UITextField using the Container's styles
        // whose top is at viewMetrics.top.
        // If the height is small, the baselinePosition is calculated
        // as if there were text within whose ascent the Container
        // is vertically centered.
        // At the crossover height, these two calculations
        // produce the same result.

        var lineMetrics:TextLineMetrics = measureText("Wj");

        if (height < 2 * viewMetrics.top + 4 + lineMetrics.ascent)
            return int(height + (lineMetrics.ascent - height) / 2);
        
        return viewMetrics.top + 2 + lineMetrics.ascent;
    }

    //----------------------------------
    //  contentMouseX
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#contentMouseX
     */
    override public function get contentMouseX():Number
    {
        if (contentPane)
            return contentPane.mouseX;
        
        return super.contentMouseX;
    }
    
    //----------------------------------
    //  contentMouseY
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#contentMouseY
     */
    override public function get contentMouseY():Number
    {
        if (contentPane)
            return contentPane.mouseY;
        
        return super.contentMouseY;
    }

    //----------------------------------
    //  doubleClickEnabled
    //----------------------------------

    /**
     *  @private
     *  Propagate to children.
     */
    override public function set doubleClickEnabled(value:Boolean):void
    {
        super.doubleClickEnabled = value;

        if (contentPane)
        {
            var n:int = contentPane.numChildren;
            for (var i:int = 0; i < n; i++)
            {
                var child:InteractiveObject =
                    contentPane.getChildAt(i) as InteractiveObject;
                if (child)
                    child.doubleClickEnabled = value;
            }
        }
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        super.enabled = value;

        // Scrollbars must be enabled/disabled when this container is.
        if (horizontalScrollBar)
            horizontalScrollBar.enabled = value;
        if (verticalScrollBar)
            verticalScrollBar.enabled = value;

        invalidateProperties();
    }

    //----------------------------------
    //  focusPane
    //----------------------------------

    /**
     *  @private
     *  Storage for the focusPane property.
     */
    private var _focusPane:Sprite;

    /**
     *  @private
     *  Focus pane associated with this object.
     *  An object has a focus pane when one of its children has got focus.
     */
    override public function get focusPane():Sprite
    {
        return _focusPane;
    }

    /**
     *  @private
     */
    override public function set focusPane(o:Sprite):void
    {
        // The addition or removal of the focus sprite should not trigger
        // a measurement/layout pass.  Temporarily set the invalidation flags,
        // so that calls to invalidateSize() and invalidateDisplayList() have
        // no effect.
        var oldInvalidateSizeFlag:Boolean = invalidateSizeFlag;
        var oldInvalidateDisplayListFlag:Boolean = invalidateDisplayListFlag;
        invalidateSizeFlag = true;
        invalidateDisplayListFlag = true;

        if (o)
        {
            rawChildren.addChild(o);

            o.x = 0;
            o.y = 0;
            o.scrollRect = null;

            _focusPane = o;
        }
        else
        {
            rawChildren.removeChild(_focusPane);

            _focusPane = null;
        }

        if (o && contentPane)
        {
            o.x = contentPane.x;
            o.y = contentPane.y;
            o.scrollRect = contentPane.scrollRect;
        }

        invalidateSizeFlag = oldInvalidateSizeFlag;
        invalidateDisplayListFlag = oldInvalidateDisplayListFlag;
    }

    //----------------------------------
    //  $numChildren
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the numChildren property, which can be useful since components
     *  can override numChildren and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $numChildren():int
    {
        return super.numChildren;
    }

    //----------------------------------
    //  numChildren
    //----------------------------------

    /**
     *  @private
     *  Storage for the numChildren property.
     */
    mx_internal var _numChildren:int = 0;

    /**
     *  Number of child components in this container.
     *
     *  <p>The number of children is initially equal
     *  to the number of children declared in MXML.
     *  At runtime, new children may be added by calling
     *  <code>addChild()</code> or <code>addChildAt()</code>,
     *  and existing children may be removed by calling
     *  <code>removeChild()</code>, <code>removeChildAt()</code>,
     *  or <code>removeAllChildren()</code>.</p>
     */
    override public function get numChildren():int
    {
        return contentPane ? contentPane.numChildren : _numChildren;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoLayout
    //----------------------------------

    /**
     *  @private
     *  Storage for the autoLayout property.
     */
    private var _autoLayout:Boolean = true;

    [Inspectable(defaultValue="true")]

    /**
     *  If <code>true</code>, measurement and layout are done
     *  when the position or size of a child is changed.
     *  If <code>false</code>, measurement and layout are done only once,
     *  when children are added to or removed from the container.
     *
     *  <p>When using the Move effect, the layout around the component that
     *  is moving does not readjust to fit that the Move effect animates.
     *  Setting a container's <code>autoLayout</code> property to
     *  <code>true</code> has no effect on this behavior.</p>
     *
     *  <p>The Zoom effect does not work when the <code>autoLayout</code> 
     *  property is <code>false</code>.</p>
     *
     *  <p>The <code>autoLayout</code> property does not apply to
     *  Accordion or ViewStack containers.</p>
     * 
     *  @default true
     */
    public function get autoLayout():Boolean
    {
        return _autoLayout;
    }

    /**
     *  @private
     */
    public function set autoLayout(value:Boolean):void
    {
        _autoLayout = value;

        // If layout is being turned back on, trigger a layout to occur now.
        if (value)
        {
            invalidateSize();
            invalidateDisplayList();

            var p:IInvalidating = parent as IInvalidating;
            if (p)
            {
                p.invalidateSize();
                p.invalidateDisplayList();
            }
        }
    }

    //----------------------------------
    //  borderMetrics
    //----------------------------------

    /**
     *  Returns an EdgeMetrics object that has four properties:
     *  <code>left</code>, <code>top</code>, <code>right</code>,
     *  and <code>bottom</code>.
     *  The value of each property is equal to the thickness of one side
     *  of the border, expressed in pixels.
     *
     *  <p>Unlike <code>viewMetrics</code>, this property is not
     *  overriden by subclasses of Container.</p>
     */
    public function get borderMetrics():EdgeMetrics
    {
        return border && border is IRectangularBorder ?
               IRectangularBorder(border).borderMetrics :
               EdgeMetrics.EMPTY;
    }

    //----------------------------------
    //  childDescriptors
    //----------------------------------

    /**
     *  @private
     *  Storage for the childDescriptors property.
     *  This variable is initialized in the construct() method
     *  using the childDescriptors in the initObj, which is autogenerated.
     *  If this Container was not created by createComponentFromDescriptor(),
     *  its childDescriptors property is null.
     */
    private var _childDescriptors:Array /* of UIComponentDescriptor */;

    /**
     *  Array of UIComponentDescriptor objects produced by the MXML compiler.
     *
     *  <p>Each UIComponentDescriptor object contains the information 
     *  specified in one child MXML tag of the container's MXML tag.
     *  The order of the UIComponentDescriptor objects in the Array
     *  is the same as the order of the child tags.
     *  During initialization, the child descriptors are used to create
     *  the container's child UIComponent objects and its Repeater objects, 
     *  and to give them the initial property values, event handlers, effects, 
     *  and so on, that were specified in MXML.</p>
     *
     *  @see mx.core.UIComponentDescriptor
     */
    public function get childDescriptors():Array /* of UIComponentDescriptor */
    {
        return _childDescriptors;
    }

    //----------------------------------
    //  childRepeaters
    //----------------------------------

    /**
     *  @private
     *  Storage for the childRepeaters property.
     */
    private var _childRepeaters:Array;

    /**
     *  @private
     *  An array of the Repeater objects found within this container.
     */
    mx_internal function get childRepeaters():Array
    {
        return _childRepeaters;
    }

    /**
     *  @private
     */
    mx_internal function set childRepeaters(value:Array):void
    {
        _childRepeaters = value;
    }

    //----------------------------------
    //  clipContent
    //----------------------------------

    /**
     *  @private
     *  Storage for the clipContent property.
     */
    private var _clipContent:Boolean = true;

    [Inspectable(defaultValue="true")]

    /**
     *  Whether to apply a clip mask if the positions and/or sizes
     *  of this container's children extend outside the borders of
     *  this container.
     *  If <code>false</code>, the children of this container
     *  remain visible when they are moved or sized outside the
     *  borders of this container.
     *  If <code>true</code>, the children of this container are clipped.
     *
     *  <p>If <code>clipContent</code> is <code>false</code>, then scrolling
     *  is disabled for this container and scrollbars will not appear.
     *  If <code>clipContent</code> is true, then scrollbars will usually
     *  appear when the container's children extend outside the border of
     *  the container.
     *  For additional control over the appearance of scrollbars,
     *  see <code>horizontalScrollPolicy</code> and <code>verticalScrollPolicy</code>.</p>
     * 
     *  @default true
     */
    public function get clipContent():Boolean
    {
        return _clipContent;
    }

    /**
     *  @private
     */
    public function set clipContent(value:Boolean):void
    {
        if (_clipContent != value)
        {
            _clipContent = value;

            invalidateDisplayList();
        }
    }

    //----------------------------------
    //  createdComponents
    //----------------------------------

    /**
     *  @private
     *  Internal variable used to keep track of the components created
     *  by this Container.  This is different than the list maintained
     *  by DisplayObjectContainer, because it includes Repeaters.
     */
    private var _createdComponents:Array;

    /**
     *  @private
     *  An array of all components created by this container including
     *  Repeater components.
     */
    mx_internal function get createdComponents():Array
    {
        return _createdComponents;
    }

    /**
     *  @private
     */
    mx_internal function set createdComponents(value:Array):void
    {
        _createdComponents = value;
    }

    //----------------------------------
    //  creationIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the creationIndex property.
     */
    private var _creationIndex:int = -1;

    [Inspectable(defaultValue="undefined")]

    /**
     *  Specifies the order to instantiate and draw the children
     *  of the container.
     *
     *  <p>This property can only be used when the <code>creationPolicy</code>
     *  property is set to <code>ContainerCreationPolicy.QUEUED</code>.
     *  Otherwise, it is ignored.</p>
     *
     *  @default -1
     */
    public function get creationIndex():int
    {
        return _creationIndex;
    }

    /**
     *  @private
     */
    public function set creationIndex(value:int):void
    {
        _creationIndex = value;
    }

    //----------------------------------
    //  creationPolicy
    //----------------------------------

    /**
     *  @private
     *  Storage for the creationPolicy property.
     *  This variable is initialized in the construct() method
     *  using the childDescriptors in the initObj, which is autogenerated.
     *  If this Container was not created by createComponentFromDescriptor(),
     *  its childDescriptors property is null.
     */
    private var _creationPolicy:String;

    [Inspectable(enumeration="all,auto,queued,none")]

    /**
     *  The child creation policy for this Container.
     *  ActionScript values can be <code>ContainerCreationPolicy.AUTO</code>, 
     *  <code>ContainerCreationPolicy.ALL</code>,
     *  <code>ContainerCreationPolicy.NONE</code>, 
     *  or <code>ContainerCreationPolicy.QUEUED</code>.
     *  MXML values can be <code>"auto"</code>, <code>"all"</code>, 
     *  <code>"none"</code>, or <code>"queued"</code>.
     *
     *  <p>If no <code>creationPolicy</code> is specified for a container,
     *  that container inherits its parent's <code>creationPolicy</code>.
     *  If no <code>creationPolicy</code> is specified for the Application,
     *  it defaults to <code>ContainerCreationPolicy.AUTO</code>.</p>
     *
     *  <p>A <code>creationPolicy</code> of <code>ContainerCreationPolicy.AUTO</code> means
     *  that the container delays creating some or all descendants
     *  until they are needed, a process which is known as <i>deferred
     *  instantiation</i>.
     *  This policy produces the best startup time because fewer
     *  UIComponents are created initially.
     *  However, this introduces navigation delays when a user navigates
     *  to other parts of the application for the first time.
     *  Navigator containers such as Accordion, TabNavigator, and ViewStack
     *  implement the <code>ContainerCreationPolicy.AUTO</code> policy by creating all their
     *  children immediately, but wait to create the deeper descendants
     *  of a child until it becomes the selected child of the navigator
     *  container.</p>
     *
     *  <p>A <code>creationPolicy</code> of <code>ContainerCreationPolicy.ALL</code> means
     *  that the navigator containers immediately create deeper descendants
     *  for each child, rather than waiting until that child is
     *  selected. For single-view containers such as a VBox container,
     *  there is no difference  between the <code>ContainerCreationPolicy.AUTO</code> and
     *  <code>ContainerCreationPolicy.ALL</code> policies.</p>
     *
     *  <p>A <code>creationPolicy</code> of <code>ContainerCreationPolicy.QUEUED</code> means
     *  that the container is added to a creation queue rather than being
     *  immediately instantiated and drawn.
     *  When the application processes the queued container, it creates
     *  the children of the container and then waits until the children
     *  have been created before advancing to the next container in the
     *  creation queue.</p>
     *
     *  <p>A <code>creationPolicy</code> of <code>ContainerCreationPolicy.NONE</code> means
     *  that the container creates none of its children.
     *  In that case, it is the responsibility of the MXML author
     *  to create the children by calling the
     *  <code>createComponentsFromDescriptors()</code> method.</p>
     */
    public function get creationPolicy():String
    {
        return _creationPolicy;
    }

    /**
     *  @private
     */
    public function set creationPolicy(value:String):void
    {
        _creationPolicy = value;

        setActualCreationPolicies(value);
    }

    //----------------------------------
    //  defaultButton
    //----------------------------------

    /**
     *  @private
     *  Storage for the defaultButton property.
     */
    private var _defaultButton:IFlexDisplayObject;

    [Inspectable(category="General")]

    /**
     *  The Button control designated as the default button
     *  for the container.
     *  When controls in the container have focus, pressing the
     *  Enter key is the same as clicking this Button control.
     *
     *  @default null
     */
    public function get defaultButton():IFlexDisplayObject
    {
        return _defaultButton;
    }

    /**
     *  @private
     */
    public function set defaultButton(value:IFlexDisplayObject):void
    {
        _defaultButton = value;
        ContainerGlobals.focusedContainer = null;
    }

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property.
     */
    private var _data:Object;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  The <code>data</code> property lets you pass a value
     *  to the component when you use it in an item renderer or item editor.
     *  You typically use data binding to bind a field of the <code>data</code>
     *  property to a property of this component.
     *
     *  <p>You do not set this property in MXML.</p>
     *
     *  @default null
     *  @see mx.core.IDataRenderer
     */
    public function get data():Object
    {
        return _data;
    }

    /**
     *  @private
     */
    public function set data(value:Object):void
    {
        _data = value;

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));

        invalidateDisplayList();
    }

    //----------------------------------
    //  firstChildIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the firstChildIndex property.
     */
    private var _firstChildIndex:int = 0;

    /**
     *  @private
     *  The index of the first content child,
     *  when dealing with both content and non-content children.
     */
    mx_internal function get firstChildIndex():int
    {
        return _firstChildIndex;
    }

    //----------------------------------
    //  horizontalLineScrollSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the horizontalLineScrollSize property.
     */
    private var _horizontalLineScrollSize:Number = 5;

    [Bindable("horizontalLineScrollSizeChanged")]
    [Inspectable(defaultValue="5")]

    /**
     *  Number of pixels to move when the left- or right-arrow
     *  button in the horizontal scroll bar is pressed.
     *  
     *  @default 5
     */
    public function get horizontalLineScrollSize():Number
    {
        return _horizontalLineScrollSize;
    }

    /**
     *  @private
     */
    public function set horizontalLineScrollSize(value:Number):void
    {
        scrollPropertiesChanged = true;

        _horizontalLineScrollSize = value;

        invalidateDisplayList();

        dispatchEvent(new Event("horizontalLineScrollSizeChanged"));
    }

    //----------------------------------
    //  horizontalPageScrollSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the horizontalPageScrollSize property.
     */
    private var _horizontalPageScrollSize:Number = 0;

    [Bindable("horizontalPageScrollSizeChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  Number of pixels to move when the track in the
     *  horizontal scroll bar is pressed.
     *  A value of 0 means that the page size
     *  will be calculated to be a full screen.
     * 
     *  @default 0
     */
    public function get horizontalPageScrollSize():Number
    {
        return _horizontalPageScrollSize;
    }

    /**
     *  @private
     */
    public function set horizontalPageScrollSize(value:Number):void
    {
        scrollPropertiesChanged = true;

        _horizontalPageScrollSize = value;

        invalidateDisplayList();

        dispatchEvent(new Event("horizontalPageScrollSizeChanged"));
    }

    //----------------------------------
    //  horizontalScrollBar
    //----------------------------------

    /**
     *  @private
     *  The horizontal scrollbar (null if not present).
     */
    private var _horizontalScrollBar:ScrollBar;

    /**
     *  The horizontal scrollbar used in this container.
     *  This property is null if no horizontal scroll bar
     *  is currently displayed.
     *  In general you do not access this property directly.
     *  Manipulation of the <code>horizontalScrollPolicy</code> 
     *  and <code>horizontalScrollPosition</code>
     *  properties should provide sufficient control over the scroll bar.
     */
    public function get horizontalScrollBar():ScrollBar
    {
        return _horizontalScrollBar;
    }

    /**
     *  @private
     */
    public function set horizontalScrollBar(value:ScrollBar):void
    {
        _horizontalScrollBar = value;
    }

    //----------------------------------
    //  horizontalScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the horizontalScrollPosition property.
     */
    private var _horizontalScrollPosition:Number = 0;

    [Bindable("scroll")]
    [Bindable("viewChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  The current position of the horizontal scroll bar.
     *  This is equal to the distance in pixels between the left edge
     *  of the scrollable surface and the leftmost piece of the surface
     *  that is currently visible.
     *  
     *  @default 0
     */
    public function get horizontalScrollPosition():Number
    {
        if (!isNaN(horizontalScrollPositionPending))
            return horizontalScrollPositionPending;
        return _horizontalScrollPosition;
    }

    /**
     *  @private
     */
    public function set horizontalScrollPosition(value:Number):void
    {
        if (_horizontalScrollPosition == value)
            return;

        // Note: We can't use maxHorizontalScrollPosition to clamp the value here.
        // The horizontalScrollBar may not exist yet,
        // or its maxPos might change during layout.
        // (For example, you could set the horizontalScrollPosition of a childless container,
        // then add a child which causes it to have a scrollbar.)
        // The horizontalScrollPosition gets clamped to the range 0 through maxHorizontalScrollPosition
        // late, in the updateDisplayList() method, just before the scrollPosition
        // of the horizontalScrollBar is set.

        _horizontalScrollPosition = value;
        scrollPositionChanged = true;
        if (!initialized)
            horizontalScrollPositionPending = value;

        invalidateDisplayList();

        dispatchEvent(new Event("viewChanged"));
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    /**
     *  @private
     *  Storage for the horizontalScrollPolicy property.
     */
    mx_internal var _horizontalScrollPolicy:String = ScrollPolicy.AUTO;

    [Bindable("horizontalScrollPolicyChanged")]
    [Inspectable(category="General", enumeration="off,on,auto", defaultValue="auto")]

    /**
     *  Specifies whether the horizontal scroll bar is always present,
     *  always absent, or automatically added when needed.
     *  ActionScript values can be <code>ScrollPolicy.ON</code>, <code>ScrollPolicy.OFF</code>,
     *  and <code>ScrollPolicy.AUTO</code>. 
     *  MXML values can be <code>"on"</code>, <code>"off"</code>,
     *  and <code>"auto"</code>.
     *
     *  <p>Setting this property to <code>ScrollPolicy.OFF</code> also prevents the
     *  <code>horizontalScrollPosition</code> property from having an effect.</p>
     *
     *  <p>Note: This property does not apply to the ControlBar container.</p>
     *
     *  <p>If the <code>horizontalScrollPolicy</code> is <code>ScrollPolicy.AUTO</code>,
     *  the horizontal scroll bar appears when all of the following
     *  are true:</p>
     *  <ul>
     *    <li>One of the container's children extends beyond the left
     *      edge or right edge of the container.</li>
     *    <li>The <code>clipContent</code> property is <code>true</code>.</li>
     *    <li>The width and height of the container are large enough to
     *      reasonably accommodate a scroll bar.</li>
     *  </ul>
     *
     *  @default ScrollPolicy.AUTO
     */
    public function get horizontalScrollPolicy():String
    {
        return _horizontalScrollPolicy;
    }

    /**
     *  @private
     */
    public function set horizontalScrollPolicy(value:String):void
    {
        if (_horizontalScrollPolicy != value)
        {
            _horizontalScrollPolicy = value;

            invalidateDisplayList();

            dispatchEvent(new Event("horizontalScrollPolicyChanged"));
        }
    }

    //----------------------------------
    //  icon
    //----------------------------------

    /**
     *  @private
     *  Storage for the icon property.
     */
    private var _icon:Class = null;

    [Bindable("iconChanged")]
    [Inspectable(category="General", defaultValue="", format="EmbeddedFile")]

    /**
     *  The Class of the icon displayed by some navigator
     *  containers to represent this Container.
     *
     *  <p>For example, if this Container is a child of a TabNavigator,
     *  this icon appears in the corresponding tab.
     *  If this Container is a child of an Accordion,
     *  this icon appears in the corresponding header.</p>
     *
     *  <p>To embed the icon in the SWF file, use the &#64;Embed()
     *  MXML compiler directive:</p>
     *
     *  <pre>
     *    icon="&#64;Embed('filepath')"
     *  </pre>
     *
     *  <p>The image can be a JPEG, GIF, PNG, SVG, or SWF file.</p>
     *
     *  @default null
     */
    public function get icon():Class
    {
        return _icon;
    }

    /**
     *  @private
     */
    public function set icon(value:Class):void
    {
        _icon = value;

        dispatchEvent(new Event("iconChanged"));
    }

    //----------------------------------
    //  label
    //----------------------------------

    /**
     *  @private
     *  Storage for the label property.
     */
    private var _label:String = "";

    [Bindable("labelChanged")]
    [Inspectable(category="General", defaultValue="")]

    /**
     *  The text displayed by some navigator containers to represent
     *  this Container.
     *
     *  <p>For example, if this Container is a child of a TabNavigator,
     *  this string appears in the corresponding tab.
     *  If this Container is a child of an Accordion,
     *  this string appears in the corresponding header.</p>
     *
     *  @default ""
     */
    public function get label():String
    {
        return _label;
    }

    /**
     *  @private
     */
    public function set label(value:String):void
    {
        _label = value;

        dispatchEvent(new Event("labelChanged"));
    }

    //----------------------------------
    //  maxHorizontalScrollPosition
    //----------------------------------

    /**
     *  The largest possible value for the
     *  <code>horizontalScrollPosition</code> property.
     *  Defaults to 0 if the horizontal scrollbar is not present.
     */
    public function get maxHorizontalScrollPosition():Number
    {
        return horizontalScrollBar ?
               horizontalScrollBar.maxScrollPosition :
               Math.max(scrollableWidth - viewableWidth, 0);
    }

    //----------------------------------
    //  maxVerticalScrollPosition
    //----------------------------------

    /**
     *  The largest possible value for the
     *  <code>verticalScrollPosition</code> property.
     *  Defaults to 0 if the vertical scrollbar is not present.
     */
    public function get maxVerticalScrollPosition():Number
    {
        return verticalScrollBar ?
               verticalScrollBar.maxScrollPosition :
               Math.max(scrollableHeight - viewableHeight, 0);
    }

    //----------------------------------
    //  numChildrenCreated
    //----------------------------------

    /**
     *  @private
     */
    private var _numChildrenCreated:int = -1;

    /**
     *  @private
     *  The number of children created inside this container.
     *  The default value is 0.
     */
    mx_internal function get numChildrenCreated():int
    {
        return _numChildrenCreated;
    }

    /**
     *  @private
     */
    mx_internal function set numChildrenCreated(value:int):void
    {
        _numChildrenCreated = value;
    }

    //----------------------------------
    //  numRepeaters
    //----------------------------------

    /**
     *  @private 
     *  The number of Repeaters in this Container.
     *
     *  <p>This number includes Repeaters that are immediate children of this
     *  container and Repeaters that are nested inside other Repeaters.
     *  Consider the following example:</p>
     *
     *  <pre>
     *  &lt;mx:HBox&gt;
     *    &lt;mx:Repeater dataProvider="[1, 2]"&gt;
     *      &lt;mx:Repeater dataProvider="..."&gt;
     *        &lt;mx:Button/&gt;
     *      &lt;/mx:Repeater&gt;
     *    &lt;/mx:Repeater&gt;
     *  &lt;mx:HBox&gt;
     *  </pre>
     *
     *  <p>In this example, the <code>numRepeaters</code> property
     *  for the HBox would be set equal to 3 -- one outer Repeater
     *  and two inner repeaters.</p>
     *
     *  <p>The <code>numRepeaters</code> property does not include Repeaters
     *  that are nested inside other containers.
     *  Consider this example:</p>
     *
     *  <pre>
     *  &lt;mx:HBox&gt;
     *    &lt;mx:Repeater dataProvider="[1, 2]"&gt;
     *      &lt;mx:VBox&gt;
     *        &lt;mx:Repeater dataProvider="..."&gt;
     *          &lt;mx:Button/&gt;
     *        &lt;/mx:Repeater&gt;
     *      &lt;/mx:VBox&gt;
     *    &lt;/mx:Repeater&gt;
     *  &lt;mx:HBox&gt;
     *  </pre>
     *
     *  <p>In this example, the <code>numRepeaters</code> property
     *  for the outer HBox would be set equal to 1 -- just the outer repeater.
     *  The two inner VBox containers would also have a
     *  <code>numRepeaters</code> property equal to 1 -- one Repeater
     *  per VBox.</p>
     */
    mx_internal function get numRepeaters():int
    {
        return childRepeaters ? childRepeaters.length : 0;
    }

    //----------------------------------
    //  rawChildren
    //----------------------------------

    /**
     *  @private
     *  The single IChildList object that's always returned
     *  from the rawChildren property, below.
     */
    private var _rawChildren:ContainerRawChildrenList;

    /**
     *  A container typically contains child components, which can be enumerated
     *  using the <code>Container.getChildAt()</code> method and 
     *  <code>Container.numChildren</code> property.  In addition, the container
     *  may contain style elements and skins, such as the border and background.
     *  Flash Player and AIR do not draw any distinction between child components
     *  and skins.  They are all accessible using the player's 
     *  <code>getChildAt()</code> method  and
     *  <code>numChildren</code> property.  
     *  However, the Container class overrides the <code>getChildAt()</code> method 
     *  and <code>numChildren</code> property (and several other methods) 
     *  to create the illusion that
     *  the container's children are the only child components.
     *
     *  <p>If you need to access all of the children of the container (both the
     *  content children and the skins), then use the methods and properties
     *  on the <code>rawChildren</code> property instead of the regular Container methods. 
     *  For example, use the <code>Container.rawChildren.getChildAt())</code> method.
     *  However, if a container creates a ContentPane Sprite object for its children,
     *  the <code>rawChildren</code> property value only counts the ContentPane, not the
     *  container's children.
     *  It is not always possible to determine when a container will have a ContentPane.</p>
     * 
     *  <p><b>Note:</b>If you call the <code>addChild</code> or 
     *  <code>addChildAt</code>method of the <code>rawChildren</code> object,
     *  set <code>tabEnabled = false</code> on the component that you have added.
     *  Doing so prevents users from tabbing to the visual-only component
     *  that you have added.</p>
     */
    public function get rawChildren():IChildList
    {
        if (!_rawChildren)
            _rawChildren = new ContainerRawChildrenList(this);

        return _rawChildren;
    }

    //----------------------------------
    //  usePadding
    //----------------------------------

    /**
     *  @private
     */
    mx_internal function get usePadding():Boolean
    {
        // Containers, by default, always use padding.
        return true;
    }

    //----------------------------------
    //  verticalLineScrollSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the verticalLineScrollSize property.
     */
    private var _verticalLineScrollSize:Number = 5;

    [Bindable("verticalLineScrollSizeChanged")]
    [Inspectable(defaultValue="5")]

    /**
     *  Number of pixels to scroll when the up- or down-arrow
     *  button in the vertical scroll bar is pressed,
     *  or when you scroll by using the mouse wheel.
     *  
     *  @default 5
     */
    public function get verticalLineScrollSize():Number
    {
        return _verticalLineScrollSize;
    }

    /**
     *  @private
     */
    public function set verticalLineScrollSize(value:Number):void
    {
        scrollPropertiesChanged = true;

        _verticalLineScrollSize = value;

        invalidateDisplayList();

        dispatchEvent(new Event("verticalLineScrollSizeChanged"));
    }

    //----------------------------------
    //  verticalPageScrollSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the verticalPageScrollSize property.
     */
    private var _verticalPageScrollSize:Number = 0;

    [Bindable("verticalPageScrollSizeChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  Number of pixels to scroll when the track
     *  in the vertical scroll bar is pressed.
     *  A value of 0 means that the page size
     *  will be calculated to be a full screen.
     * 
     *  @default 0   
     */
    public function get verticalPageScrollSize():Number
    {
        return _verticalPageScrollSize;
    }

    /**
     *  @private
     */
    public function set verticalPageScrollSize(value:Number):void
    {
        scrollPropertiesChanged = true;

        _verticalPageScrollSize = value;

        invalidateDisplayList();

        dispatchEvent(new Event("verticalPageScrollSizeChanged"));
    }

    //----------------------------------
    //  verticalScrollBar
    //----------------------------------

    /**
     *  @private
     *  The vertical scrollbar (null if not present).
     */
    private var _verticalScrollBar:ScrollBar;

    /**
     *  The vertical scrollbar used in this container.
     *  This property is null if no vertical scroll bar
     *  is currently displayed.
     *  In general you do not access this property directly.
     *  Manipulation of the <code>verticalScrollPolicy</code> 
     *  and <code>verticalScrollPosition</code>
     *  properties should provide sufficient control over the scroll bar.
     */
    public function get verticalScrollBar():ScrollBar
    {
        return _verticalScrollBar;
    }

    /**
     *  @private
     */
    public function set verticalScrollBar(value:ScrollBar):void
    {
        _verticalScrollBar = value;
    }

    //----------------------------------
    //  verticalScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the verticalScrollPosition property.
     */
    private var _verticalScrollPosition:Number = 0;

    [Bindable("scroll")]
    [Bindable("viewChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  The current position of the vertical scroll bar.
     *  This is equal to the distance in pixels between the top edge
     *  of the scrollable surface and the topmost piece of the surface
     *  that is currently visible.
     *
     *  @default 0
     */
    public function get verticalScrollPosition():Number
    {
        if (!isNaN(verticalScrollPositionPending))
            return verticalScrollPositionPending;

        return _verticalScrollPosition;
    }

    /**
     *  @private
     */
    public function set verticalScrollPosition(value:Number):void
    {
        if (_verticalScrollPosition == value)
            return;

        // Note: We can't use maxVerticalScrollPosition to clamp the value here.
        // The verticalScrollBar may not exist yet,
        // or its maxPos might change during layout.
        // (For example, you could set the verticalScrollPosition of a childless container,
        // then add a child which causes it to have a scrollbar.)
        // The verticalScrollPosition gets clamped to the range 0 through maxVerticalScrollPosition
        // late, in the updateDisplayList() method, just before the scrollPosition
        // of the verticalScrollBar is set.

        _verticalScrollPosition = value;
        scrollPositionChanged = true;
        if (!initialized)
            verticalScrollPositionPending = value;

        invalidateDisplayList();

        dispatchEvent(new Event("viewChanged"));
    }

    //----------------------------------
    //  verticalScrollPolicy
    //----------------------------------

    /**
     *  @private
     *  Storage for the verticalScrollPolicy property.
     */
    mx_internal var _verticalScrollPolicy:String = ScrollPolicy.AUTO;

    [Bindable("verticalScrollPolicyChanged")]
    [Inspectable(category="General", enumeration="off,on,auto", defaultValue="auto")]

    /**
     *  Specifies whether the vertical scroll bar is always present,
     *  always absent, or automatically added when needed.
     *  Possible values are <code>ScrollPolicy.ON</code>, <code>ScrollPolicy.OFF</code>,
     *  and <code>ScrollPolicy.AUTO</code>.
     *  MXML values can be <code>"on"</code>, <code>"off"</code>,
     *  and <code>"auto"</code>.
     *
     *  <p>Setting this property to <code>ScrollPolicy.OFF</code> also prevents the
     *  <code>verticalScrollPosition</code> property from having an effect.</p>
     *
     *  <p>Note: This property does not apply to the ControlBar container.</p>
     *
     *  <p>If the <code>verticalScrollPolicy</code> is <code>ScrollPolicy.AUTO</code>,
     *  the vertical scroll bar appears when all of the following
     *  are true:</p>
     *  <ul>
     *    <li>One of the container's children extends beyond the top
     *      edge or bottom edge of the container.</li>
     *    <li>The <code>clipContent</code> property is <code>true</code>.</li>
     *    <li>The width and height of the container are large enough to
     *      reasonably accommodate a scroll bar.</li>
     *  </ul>
     *
     *  @default ScrollPolicy.AUTO
     */
    public function get verticalScrollPolicy():String
    {
        return _verticalScrollPolicy;
    }

    /**
     *  @private
     */
    public function set verticalScrollPolicy(value:String):void
    {
        if (_verticalScrollPolicy != value)
        {
            _verticalScrollPolicy = value;

            invalidateDisplayList();

            dispatchEvent(new Event("verticalScrollPolicyChanged"));
        }
    }

    //----------------------------------
    //  viewMetrics
    //----------------------------------

    /**
     *  @private
     *  Offsets including borders and scrollbars
     */
    private var _viewMetrics:EdgeMetrics;

    /**
     *  Returns an object that has four properties: <code>left</code>,
     *  <code>top</code>, <code>right</code>, and <code>bottom</code>.
     *  The value of each property equals the thickness of the chrome
     *  (visual elements) around the edge of the container. 
     *
     *  <p>The chrome includes the border thickness.
     *  If the <code>horizontalScrollPolicy</code> or <code>verticalScrollPolicy</code> 
     *  property value is <code>ScrollPolicy.ON</code>, the
     *  chrome also includes the thickness of the corresponding
     *  scroll bar. If a scroll policy is <code>ScrollPolicy.AUTO</code>,
     *  the chrome measurement does not include the scroll bar thickness, 
     *  even if a scroll bar is displayed.</p>
     *
     *  <p>Subclasses of Container should override this method, so that
     *  they include other chrome to be taken into account when positioning
     *  the Container's children.
     *  For example, the <code>viewMetrics</code> property for the
     *  Panel class should return an object whose <code>top</code> property
     *  includes the thickness of the Panel container's title bar.</p>
     */
    public function get viewMetrics():EdgeMetrics
    {
        var bm:EdgeMetrics = borderMetrics;

        // If scrollPolicy is ScrollPolicy.ON, then the scrollbars are accounted for
        // during both measurement and layout.
        //
        // If scrollPolicy is ScrollPolicy.AUTO, then scrollbars are ignored during
        // measurement.  Otherwise, the entire layout of the app could change
        // everytime that the scrollbars turn on or off.
        //
        // However, we do take the width of scrollbars into account when laying
        // out our children.  That way, children that have a percentage width or
        // percentage height will only expand to consume space that's left over
        // after leaving room for the scrollbars.
        var verticalScrollBarIncluded:Boolean =
            verticalScrollBar != null &&
            (doingLayout || verticalScrollPolicy == ScrollPolicy.ON);
        var horizontalScrollBarIncluded:Boolean =
            horizontalScrollBar != null &&
            (doingLayout || horizontalScrollPolicy == ScrollPolicy.ON);
        if (!verticalScrollBarIncluded && !horizontalScrollBarIncluded)
            return bm;

        // The viewMetrics property needs to return its own object.
        // Rather than allocating a new one each time, we'll allocate one once
        // and then hold a reference to it.
        if (!_viewMetrics)
        {
            _viewMetrics = bm.clone();
        }
        else
        {
            _viewMetrics.left = bm.left;
            _viewMetrics.right = bm.right;
            _viewMetrics.top = bm.top;
            _viewMetrics.bottom = bm.bottom;
        }

        if (verticalScrollBarIncluded)
            _viewMetrics.right += verticalScrollBar.minWidth;
        if (horizontalScrollBarIncluded)
            _viewMetrics.bottom += horizontalScrollBar.minHeight;

        return _viewMetrics;
    }

    //----------------------------------
    //  viewMetricsAndPadding
    //----------------------------------

    /**
     *  @private
     *  Cached value containing the view metrics plus the object's margins.
     */
    private var _viewMetricsAndPadding:EdgeMetrics;

    /**
     *  Returns an object that has four properties: <code>left</code>,
     *  <code>top</code>, <code>right</code>, and <code>bottom</code>.
     *  The value of each property is equal to the thickness of the chrome
     *  (visual elements)
     *  around the edge of the container plus the thickness of the object's margins.
     *
     *  <p>The chrome includes the border thickness.
     *  If the <code>horizontalScrollPolicy</code> or <code>verticalScrollPolicy</code> 
     *  property value is <code>ScrollPolicy.ON</code>, the
     *  chrome also includes the thickness of the corresponding
     *  scroll bar. If a scroll policy is <code>ScrollPolicy.AUTO</code>,
     *  the chrome measurement does not include the scroll bar thickness, 
     *  even if a scroll bar is displayed.</p>
     */
    public function get viewMetricsAndPadding():EdgeMetrics
    {
        // If this object has scrollbars, and if the verticalScrollPolicy
        // is not ScrollPolicy.ON, then the view metrics change
        // depending on whether we're doing layout or not.
        // In that case, we can't use a cached value.
        // In all other cases, use the cached value if it exists.
        if (_viewMetricsAndPadding &&
            (!horizontalScrollBar ||
             horizontalScrollPolicy == ScrollPolicy.ON) &&
            (!verticalScrollBar ||
             verticalScrollPolicy == ScrollPolicy.ON))
        {
            return _viewMetricsAndPadding;
        }

        if (!_viewMetricsAndPadding)
            _viewMetricsAndPadding = new EdgeMetrics();

        var o:EdgeMetrics = _viewMetricsAndPadding;
        var vm:EdgeMetrics = viewMetrics;

        o.left = vm.left + getStyle("paddingLeft");
        o.right = vm.right + getStyle("paddingRight");
        o.top = vm.top + getStyle("paddingTop");
        o.bottom = vm.bottom + getStyle("paddingBottom");

        return o;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: EventDispatcher
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  If we add a mouse event, then we need to add a mouse shield
     *  to us and to all our children
     *  The mouseShield style is a non-inheriting style
     *  that is used by the view.
     *  The mouseShieldChildren style is an inherting style
     *  that is used by the children views.
     */
    override public function addEventListener(
                                    type:String, listener:Function,
                                    useCapture:Boolean = false,
                                    priority:int = 0,
                                    useWeakReference:Boolean = false):void
    {
        super.addEventListener(type, listener, useCapture,
                               priority, useWeakReference);

        // If we are a mouse event, then create a mouse shield.
        if (type == MouseEvent.CLICK ||
            type == MouseEvent.DOUBLE_CLICK ||
            type == MouseEvent.MOUSE_DOWN ||
            type == MouseEvent.MOUSE_MOVE ||
            type == MouseEvent.MOUSE_OVER ||
            type == MouseEvent.MOUSE_OUT ||
            type == MouseEvent.MOUSE_UP ||
            type == MouseEvent.MOUSE_WHEEL)
        {
            if (mouseEventReferenceCount < 0x7FFFFFFF /* int_max */ &&
                mouseEventReferenceCount++ == 0)
            {
                setStyle("mouseShield", true);
                setStyle("mouseShieldChildren", true);
            }
        }
    }

    /**
     *  @private
     *  Remove the mouse shield if we no longer listen to any mouse events
     */
    override public function removeEventListener(
                                    type:String, listener:Function,
                                    useCapture:Boolean = false):void
    {
        super.removeEventListener(type, listener, useCapture);

        // If we are a mouse event,
        // then decrement the mouse shield reference count.
        if (type == MouseEvent.CLICK ||
            type == MouseEvent.DOUBLE_CLICK ||
            type == MouseEvent.MOUSE_DOWN ||
            type == MouseEvent.MOUSE_MOVE ||
            type == MouseEvent.MOUSE_OVER ||
            type == MouseEvent.MOUSE_OUT ||
            type == MouseEvent.MOUSE_UP ||
            type == MouseEvent.MOUSE_WHEEL)
        {
            if (mouseEventReferenceCount > 0 &&
                --mouseEventReferenceCount == 0)
            {
                setStyle("mouseShield", false);
                setStyle("mouseShieldChildren", false);
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: DisplayObjectContainer
    //
    //--------------------------------------------------------------------------

    /**
     *  Adds a child DisplayObject to this Container.
     *  The child is added after other existing children,
     *  so that the first child added has index 0,
     *  the next has index 1, an so on.
     *
     *  <p><b>Note: </b>While the <code>child</code> argument to the method
     *  is specified as of type DisplayObject, the argument must implement
     *  the IUIComponent interface to be added as a child of a container.
     *  All Flex components implement this interface.</p>
     *
     *  <p>Children are layered from back to front.
     *  In other words, if children overlap, the one with index 0
     *  is farthest to the back, and the one with index
     *  <code>numChildren - 1</code> is frontmost.
     *  This means the newly added children are layered
     *  in front of existing children.</p>
     *
     *  @param child The DisplayObject to add as a child of this Container.
     *  It must implement the IUIComponent interface.
     *
     *  @return The added child as an object of type DisplayObject. 
     *  You typically cast the return value to UIComponent, 
     *  or to the type of the added component.
     *
     *  @see mx.core.IUIComponent
     *
     *  @tiptext Adds a child object to this container.
     */
    override public function addChild(child:DisplayObject):DisplayObject
    {
        return addChildAt(child, numChildren);

        /*
        addingChild(child);

        if (contentPane)
            contentPane.addChild(child);
        else
            $addChild(child);

        childAdded(child);

        return child;
        */
    }

    /**
     *  Adds a child DisplayObject to this Container.
     *  The child is added at the index specified.
     *
     *  <p><b>Note: </b>While the <code>child</code> argument to the method
     *  is specified as of type DisplayObject, the argument must implement
     *  the IUIComponent interface to be added as a child of a container.
     *  All Flex components implement this interface.</p>
     *
     *  <p>Children are layered from back to front.
     *  In other words, if children overlap, the one with index 0
     *  is farthest to the back, and the one with index
     *  <code>numChildren - 1</code> is frontmost.
     *  This means the newly added children are layered
     *  in front of existing children.</p>
     *
     *  <p>When you add a new child at an index that is already occupied
     *  by an old child, it doesn't replace the old child; instead the
     *  old child and the ones after it "slide over" and have their index
     *  incremented by one.
     *  For example, suppose a Container contains the children
     *  (A, B, C) and you add D at index 1.
     *  Then the container will contain (A, D, B, C).
     *  If you want to replace an old child, you must first remove it
     *  before adding the new one.</p>
     *
     *  @param child The DisplayObject to add as a child of this Container.
     *  It must implement the IUIComponent interface.
     *
     *  @param index The index to add the child at.
     *
     *  @return The added child as an object of type DisplayObject. 
     *  You typically cast the return value to UIComponent, 
     *  or to the type of the added component.
     *
     *  @see mx.core.IUIComponent
     */
    override public function addChildAt(child:DisplayObject,
                                        index:int):DisplayObject
    {
        var formerParent:DisplayObjectContainer = child.parent;
        if (formerParent && !(formerParent is Loader))
            formerParent.removeChild(child);
            
        addingChild(child);

        // Add the child to either this container or its contentPane.
        // The player will dispatch an "added" event from the child
        // after it has been added, so all "added" handlers execute here.
        if (contentPane)
            contentPane.addChildAt(child, index);
        else
            $addChildAt(child, _firstChildIndex + index);

        childAdded(child);

        if ((child is UIComponent) && UIComponent(child).isDocument)
            BindingManager.setEnabled(child, true);

        return child;
    }

    /**
     *  Removes a child DisplayObject from the child list of this Container.
     *  The removed child will have its <code>parent</code>
     *  property set to null. 
     *  The child will still exist unless explicitly destroyed.
     *  If you add it to another container,
     *  it will retain its last known state.
     *
     *  @param child The DisplayObject to remove.
     *
     *  @return The removed child as an object of type DisplayObject. 
     *  You typically cast the return value to UIComponent, 
     *  or to the type of the removed component.
     */
    override public function removeChild(child:DisplayObject):DisplayObject
    {
        if (child is IDeferredInstantiationUIComponent && 
            IDeferredInstantiationUIComponent(child).descriptor)
        {
            // if child's descriptor is present, it means child was created
            // with MXML.  Need to go through and remove component in 
            // createdComponents so there is no memory leak by keeping 
            // a reference to the removed child (SDK-12506)
            
            if (createdComponents)
            {
                var n:int = createdComponents.length;
                for(var i:int = 0; i < n; i++)
                {
                    if (createdComponents[i] === child)
                    {
                        // delete this reference
                        createdComponents.splice(i, 1);
                    }
                }
            }
        }

        removingChild(child);

        if ((child is UIComponent) && UIComponent(child).isDocument)
            BindingManager.setEnabled(child, false);

        // Remove the child from either this container or its contentPane.
        // The player will dispatch a "removed" event from the child
        // before it is removed, so all "removed" handlers execute here.
        if (contentPane)
            contentPane.removeChild(child);
        else
            $removeChild(child);

        childRemoved(child);

        return child;
    }

    /**
     *  Removes a child DisplayObject from the child list of this Container
     *  at the specified index.
     *  The removed child will have its <code>parent</code>
     *  property set to null. 
     *  The child will still exist unless explicitly destroyed.
     *  If you add it to another container,
     *  it will retain its last known state.
     *
     *  @param index The child index of the DisplayObject to remove.
     *
     *  @return The removed child as an object of type DisplayObject. 
     *  You typically cast the return value to UIComponent, 
     *  or to the type of the removed component.
     */
    override public function removeChildAt(index:int):DisplayObject
    {
        return removeChild(getChildAt(index));

        /*

        Shouldn't implement removeChildAt() in terms of removeChild().

        */
    }

    /**
     *  Gets the <i>n</i>th child component object.
     *
     *  <p>The children returned from this method include children that are
     *  declared in MXML and children that are added using the
     *  <code>addChild()</code> or <code>addChildAt()</code> method.</p>
     *
     *  @param childIndex Number from 0 to (numChildren - 1).
     *
     *  @return Reference to the child as an object of type DisplayObject. 
     *  You typically cast the return value to UIComponent, 
     *  or to the type of a specific Flex control, such as ComboBox or TextArea.
     */
    override public function getChildAt(index:int):DisplayObject
    {
        if (contentPane)
        {
            return contentPane.getChildAt(index);
        }
        else
        {
            // The DisplayObjectContainer implementation of getChildAt()
            // in the Player throws this error if the index is bad,
            // so we should too.
//          if (index < 0 || index >= _numChildren)
//              throw new RangeError("The supplied index is out of bounds");

            return super.getChildAt(_firstChildIndex + index);
        }
    }

    /**
     *  Returns the child whose <code>name</code> property is the specified String.
     *
     *  @param name The identifier of the child.
     *
     *  @return The DisplayObject representing the child as an object of type DisplayObject.
     *  You typically cast the return value to UIComponent, 
     *  or to the type of a specific Flex control, such as ComboBox or TextArea.
     */
    override public function getChildByName(name:String):DisplayObject
    {
        if (contentPane)
        {
            return contentPane.getChildByName(name);
        }
        else
        {
            var child:DisplayObject = super.getChildByName(name);
            if (!child)
                return null;

            // Check if the child is in the index range for content children.
            var index:int = super.getChildIndex(child) - _firstChildIndex;
            if (index < 0 || index >= _numChildren)
                return null;

            return child;
        }
    }

    /**
     *  Gets the zero-based index of a specific child.
     *
     *  <p>The first child of the container (i.e.: the first child tag
     *  that appears in the MXML declaration) has an index of 0,
     *  the second child has an index of 1, and so on.
     *  The indexes of a container's children determine
     *  the order in which they get laid out.
     *  For example, in a VBox the child with index 0 is at the top,
     *  the child with index 1 is below it, etc.</p>
     *
     *  <p>If you add a child by calling the <code>addChild()</code> method,
     *  the new child's index is equal to the largest index among existing
     *  children plus one.
     *  You can insert a child at a specified index by using the
     *  <code>addChildAt()</code> method; in that case the indices of the
     *  child previously at that index, and the children at higher indices,
     *  all have their index increased by 1 so that all indices fall in the
     *  range from 0 to <code>(numChildren - 1)</code>.</p>
     *
     *  <p>If you remove a child by calling <code>removeChild()</code>
     *  or <code>removeChildAt()</code> method, then the indices of the
     *  remaining children are adjusted so that all indices fall in the
     *  range from 0 to <code>(numChildren - 1)</code>.</p>
     *
     *  <p>If <code>myView.getChildIndex(myChild)</code> returns 5,
     *  then <code>myView.getChildAt(5)</code> returns myChild.</p>
     *
     *  <p>The index of a child may be changed by calling the
     *  <code>setChildIndex()</code> method.</p>
     *
     *  @param child Reference to child whose index to get.
     *
     *  @return Number between 0 and (numChildren - 1).
     */
    override public function getChildIndex(child:DisplayObject):int
    {
        if (contentPane)
        {
            return contentPane.getChildIndex(child);
        }
        else
        {
            var index:int = super.getChildIndex(child) - _firstChildIndex;

            // The DisplayObjectContainer implementation of getChildIndex()
            // in the Player throws this error if the child isn't a child,
            // so we should too.
//          if (index < 0 || index >= _numChildren)
//              throw new ArgumentError("The DisplayObject supplied must be a child of the caller.");

            return index;
        }
    }

    /**
     *  Sets the index of a particular child.
     *  See the <code>getChildIndex()</code> method for a
     *  description of the child's index.
     *
     *  @param child Reference to child whose index to set.
     *
     *  @param newIndex Number that indicates the new index.
     *  Must be an integer between 0 and (numChildren - 1).
     */
    override public function setChildIndex(child:DisplayObject, newIndex:int):void
    {
        var oldIndex:int;

        var eventOldIndex:int = oldIndex;
        var eventNewIndex:int = newIndex;

        if (contentPane)
        {
            contentPane.setChildIndex(child, newIndex);

            if (_autoLayout || forceLayout)
                invalidateDisplayList();
        }
        else
        {
            oldIndex = super.getChildIndex(child);

            // Offset the index, to leave room for skins before the list of children
            newIndex += _firstChildIndex;

            if (newIndex == oldIndex)
                return;

            // Change the child's index, shifting around other children to make room
            super.setChildIndex(child, newIndex);

            invalidateDisplayList();
            
            eventOldIndex = oldIndex - _firstChildIndex;
            eventNewIndex = newIndex - _firstChildIndex;
        }

        // Notify others that the child index has changed
        var event:IndexChangedEvent = new IndexChangedEvent(IndexChangedEvent.CHILD_INDEX_CHANGE);
        event.relatedObject = child;
        event.oldIndex = eventOldIndex;
        event.newIndex = eventNewIndex;
        dispatchEvent(event);

        dispatchEvent(new Event("childrenChanged"));
    }
  
    /**
     *  @private
     */
    override public function contains(child:DisplayObject):Boolean
    {
        if (contentPane)
            return contentPane.contains(child);
        else
            return super.contains(child);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function initialize():void
    {
        // Until component templating is implemented, the childDescriptors
        // come either from the top-level descriptor in the component itself
        // (i.e., the defined children) or from a descriptor for an instance
        // of that component in some other component or app (i.e., the
        // instance children). At this point _childDescriptors already
        // contains the instance children if there are any; but if the
        // document defines any children, we have to use them instead.

        if (isDocument && documentDescriptor && !processedDescriptors)
        {
            // NOTE: documentDescriptor.properties is a potentially
            // expensive function call, so do it only once.
            var props:* = documentDescriptor.properties;
            if (props && props.childDescriptors)
            {
                if (_childDescriptors)
                {
                    var message:String = resourceManager.getString(
                        "core", "multipleChildSets_ClassAndInstance");
                    throw new Error(message);
                }
                else
                {
                    _childDescriptors = props.childDescriptors;
                }
            }
        }

        super.initialize();
    }

    /**
     *  @private
     *  Create components that are children of this Container.
     */
    override protected function createChildren():void
    {
        super.createChildren();

        // Create the border/background object.
        createBorder();

        // To save ourselves an extra layout pass, check to see
        // if the scrollbars will definitely be needed.
        // If so, create them now.
        createOrDestroyScrollbars(
            horizontalScrollPolicy == ScrollPolicy.ON,
            verticalScrollPolicy == ScrollPolicy.ON,
            horizontalScrollPolicy == ScrollPolicy.ON ||
            verticalScrollPolicy == ScrollPolicy.ON);

        // Determine the child-creation policy (ContainerCreationPolicy.AUTO,
        // ContainerCreationPolicy.ALL, or ContainerCreationPolicy.NONE).
        // If the author has specified a policy, use it.
        // Otherwise, use the parent's policy.
        // This must be set before createChildren() gets called.
        if (creationPolicy != null)
        {
            actualCreationPolicy = creationPolicy;
        }
        else if (parent is Container)
        {
            if (Container(parent).actualCreationPolicy ==
                ContainerCreationPolicy.QUEUED)
            {
                actualCreationPolicy = ContainerCreationPolicy.AUTO;
            }
            else
            {
                actualCreationPolicy = Container(parent).actualCreationPolicy;
            }
        }

        // It is ok for actualCreationPolicy to be null. Popups require it.

        if (actualCreationPolicy == ContainerCreationPolicy.NONE)
        {
            actualCreationPolicy = ContainerCreationPolicy.AUTO;
        }
        else if (actualCreationPolicy == ContainerCreationPolicy.QUEUED)
        {
            var mainApp:Application = parentApplication ?
                                      Application(parentApplication) :
                                      Application(Application.application);
            
            mainApp.addToCreationQueue(this, creationIndex, null, this);
        }
        else if (recursionFlag)
        {
            // Create whatever children are appropriate. If any were
            // previously created, they don't get re-created.
            createComponentsFromDescriptors();
        }

        // If autoLayout is initially false, we still want to do
        // measurement once (even if we don't have any children)
        if (autoLayout == false)
            forceLayout = true;

        // weak references
        UIComponentGlobals.layoutManager.addEventListener(
            FlexEvent.UPDATE_COMPLETE, layoutCompleteHandler, false, 0, true);
    }

    /**
     *  @private
     *  Override to NOT set precessedDescriptors.
     */
    override protected function initializationComplete():void
    {
        // Don't call super.initializationComplete().
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (changedStyles)
        {
            // If multiple properties have changed, set styleProp to null.
            // Otherwise, set it to the name of the style that has changed.
            var styleProp:String = changedStyles == MULTIPLE_PROPERTIES ?
                                   null :
                                   changedStyles;

            super.notifyStyleChangeInChildren(styleProp, true);
            
            changedStyles = null;
        }

        createOrDestroyBlocker();
    }

    /**
     *  @private
     */
    override public function validateSize(recursive:Boolean = false):void
    {
        // If autoLayout is turned off and we haven't recently created
        // or destroyed any children, then we're not doing any
        // measurement or layout.
        // Return false indicating that the measurements haven't changed.
        if (autoLayout == false && forceLayout == false)
        {
            if (recursive)
            {
                var n:int = super.numChildren;
                for (var i:int = 0; i < n; i++)
                {
                    var child:DisplayObject = super.getChildAt(i);
                    if (child is ILayoutManagerClient )
                        ILayoutManagerClient (child).validateSize(true);
                }
            }
            adjustSizesForScaleChanges();
        }
        else
        {
            super.validateSize(recursive);
        }
    }

    /**
     *  @private
     */
    override public function validateDisplayList():void
    {
        // trace(">>Container validateLayoutPhase " + this);
        
        var vm:EdgeMetrics;

        // If autoLayout is turned off and we haven't recently created or
        // destroyed any children, then don't do any layout
        if (_autoLayout || forceLayout)
        {
            doingLayout = true;
            super.validateDisplayList();
            doingLayout = false;
        }
        else
        {
            // Layout borders, Panel headers, and other border chrome.
            layoutChrome(unscaledWidth, unscaledHeight);
        }

        // Set this to block requeuing when sizing children.
        invalidateDisplayListFlag = true;

        // Based on the positions of the children, determine
        // whether a clip mask and scrollbars are needed.
        if (createContentPaneAndScrollbarsIfNeeded())
        {
            // Redo layout if scrollbars just got created or destroyed (because
            // now we may have more or less space).
            if (_autoLayout || forceLayout)
            {
                doingLayout = true;
                super.validateDisplayList();
                doingLayout = false;
            }

            // If a scrollbar was created, that may precipitate the need
            // for a second scrollbar, so run it a second time.
            createContentPaneAndScrollbarsIfNeeded();
        }

        // The relayout performed by the above calls
        // to super.validateDisplayList() may result
        // in new max scroll positions that are less
        // than previously-set scroll positions.
        // For example, when a maximally-scrolled container
        // is resized to be larger, the new max scroll positions
        // are reduced and the current scroll positions
        // will be invalid unless we clamp them.
        if (clampScrollPositions())
            scrollChildren();
        
        if (contentPane)
        {
            vm = viewMetrics;

            // Set the position and size of the overlay .
            if (overlay)
            {
                overlay.x = 0;
                overlay.y = 0;
                overlay.width = unscaledWidth;
                overlay.height = unscaledHeight;
            }

            // Set the positions and sizes of the scrollbars.
            if (horizontalScrollBar || verticalScrollBar)
            {
                // Get the view metrics and remove the thickness
                // of the scrollbars from the view metrics.
                // We can't simply get the border metrics,
                // because some subclass (e.g.: Window)
                // might add to the metrics.
                if (verticalScrollBar &&
                    verticalScrollPolicy == ScrollPolicy.ON)
                {
                    vm.right -= verticalScrollBar.minWidth;
                }
                if (horizontalScrollBar &&
                    horizontalScrollPolicy == ScrollPolicy.ON)
                {
                    vm.bottom -= horizontalScrollBar.minHeight;
                }

                if (horizontalScrollBar)
                {
                    var w:Number = unscaledWidth - vm.left - vm.right;
                    if (verticalScrollBar)
                        w -= verticalScrollBar.minWidth;

                    horizontalScrollBar.setActualSize(
                        w, horizontalScrollBar.minHeight);
                    
                    horizontalScrollBar.move(vm.left,
                                             unscaledHeight - vm.bottom -
                                             horizontalScrollBar.minHeight);
                }

                if (verticalScrollBar)
                {
                    var h:Number = unscaledHeight - vm.top - vm.bottom;
                    if (horizontalScrollBar)
                        h -= horizontalScrollBar.minHeight;

                    verticalScrollBar.setActualSize(
                        verticalScrollBar.minWidth, h);

                    verticalScrollBar.move(unscaledWidth - vm.right -
                                           verticalScrollBar.minWidth,
                                           vm.top);
                }

                // Set the position of the box
                // that covers the gap between the scroll bars.
                if (whiteBox)
                {
                    whiteBox.x = verticalScrollBar.x;
                    whiteBox.y = horizontalScrollBar.y;
                }
            }

            contentPane.x = vm.left;
            contentPane.y = vm.top;

            if (focusPane)
            {
                focusPane.x = vm.left
                focusPane.y = vm.top;
            }

            scrollChildren();
        }

        invalidateDisplayListFlag = false;

        // that blocks UI input as well as draws an alpha overlay.
        // Make sure the blocker is correctly positioned and sized here.
        if (blocker)
        {
            vm = viewMetrics;

            var bgColor:Object = enabled ?
                                 null :
                                 getStyle("backgroundDisabledColor");
            if (bgColor === null || isNaN(Number(bgColor)))
                bgColor = getStyle("backgroundColor");

            if (bgColor === null || isNaN(Number(bgColor)))
                bgColor = 0xFFFFFF;

            var blockerAlpha:Number = getStyle("disabledOverlayAlpha");
            
            if (isNaN(blockerAlpha))
                blockerAlpha = 0.6;
                
            blocker.x = vm.left;
            blocker.y = vm.top;

            var widthToBlock:Number = unscaledWidth - (vm.left + vm.right);
            var heightToBlock:Number = unscaledHeight - (vm.top + vm.bottom);

            blocker.graphics.clear();
            blocker.graphics.beginFill(uint(bgColor), blockerAlpha);
            blocker.graphics.drawRect(0, 0, widthToBlock, heightToBlock);
            blocker.graphics.endFill();

            // Blocker must be in front of everything
            rawChildren.setChildIndex(blocker, rawChildren.numChildren - 1);
        }

        // trace("<<Container internalValidateDisplayList " + this);
    }

    /**
     *  Respond to size changes by setting the positions and sizes
     *  of this container's children.
     *
     *  <p>See the <code>UIComponent.updateDisplayList()</code> method for more information
     *  about the <code>updateDisplayList()</code> method.</p>
     *
     *  <p>The <code>Container.updateDisplayList()</code> method sets the position
     *  and size of the Container container's border.
     *  In every subclass of Container, the subclass's <code>updateDisplayList()</code>
     *  method should call the <code>super.updateDisplayList()</code> method,
     *  so that the border is positioned properly.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     *
     *  @see mx.core.UIComponent
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        layoutChrome(unscaledWidth, unscaledHeight);

        if (scrollPositionChanged)
        {
            clampScrollPositions();

            scrollChildren();

            scrollPositionChanged = false;
        }

        if (scrollPropertiesChanged)
        {
            if (horizontalScrollBar)
            {
                horizontalScrollBar.lineScrollSize = horizontalLineScrollSize;
                horizontalScrollBar.pageScrollSize = horizontalPageScrollSize;
            }

            if (verticalScrollBar)
            {
                verticalScrollBar.lineScrollSize = verticalLineScrollSize;
                verticalScrollBar.pageScrollSize = verticalPageScrollSize;
            }

            scrollPropertiesChanged = false;
        }

        if (contentPane && contentPane.scrollRect)
        {
            // Draw content pane

            var backgroundColor:Object = enabled ?
                                         null :
                                         getStyle("backgroundDisabledColor");

            if (backgroundColor === null || isNaN(Number(backgroundColor)))
                backgroundColor = getStyle("backgroundColor");

            var backgroundAlpha:Number = getStyle("backgroundAlpha");

            if (!_clipContent ||
                isNaN(Number(backgroundColor)) ||
                backgroundColor === "" ||
                (!(horizontalScrollBar || verticalScrollBar) && !cacheAsBitmap))
            {
                backgroundColor = null;
            }

            // If there's a backgroundImage or background, unset
            // opaqueBackground.
            else if (getStyle("backgroundImage") ||
                     getStyle("background"))
            {
                backgroundColor = null;
            }

            // If the background is not opaque, unset opaqueBackground.
            else if (backgroundAlpha != 1)
            {
                backgroundColor = null;
            }

            contentPane.opaqueBackground = backgroundColor;

            // Set cacheAsBitmap only if opaqueBackground is also set (to avoid
            // text anti-aliasing issue with device text on Windows).
            contentPane.cacheAsBitmap = (backgroundColor != null);
        }
    }

    /**
     *  @copy mx.core.UIComponent#contentToGlobal()
     */
    override public function contentToGlobal(point:Point):Point
    {
        if (contentPane)
            return contentPane.localToGlobal(point);
        
        return localToGlobal(point);
    }
    
    /**
     *  @copy mx.core.UIComponent#globalToContent()
     */
    override public function globalToContent(point:Point):Point
    {
        if (contentPane)
            return contentPane.globalToLocal(point);
        
        return globalToLocal(point);
    }

    /**
     *  @copy mx.core.UIComponent#contentToLocal()
     */
    override public function contentToLocal(point:Point):Point
    {
        if (!contentPane)
            return point;
        
        point = contentToGlobal(point);
        return globalToLocal(point);
    }
    
    /**
     *  @copy mx.core.UIComponent#localToContent()
     */
    override public function localToContent(point:Point):Point
    {
        if (!contentPane)
            return point;
        
        point = localToGlobal(point);
        return globalToContent(point);
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var allStyles:Boolean = styleProp == null || styleProp == "styleName";

        // Check to see if this is one of the style properties that is known
        // to affect page layout.
        if (allStyles || StyleManager.isSizeInvalidatingStyle(styleProp))
        {
            // Some styles, such as horizontalAlign and verticalAlign,
            // affect the layout of this object's children without changing the
            // view's size.  This function forces the view to be remeasured
            // and layed out.
            invalidateDisplayList();
        }
 
        // Replace the borderSkin
        if (allStyles || styleProp == "borderSkin")
        {
            if (border)
            {
                rawChildren.removeChild(DisplayObject(border));
                border = null;
                createBorder();
            }
        }
        
        // Create a border object, if none previously existed and
        // one is needed now.
        if (allStyles ||
            styleProp == "borderStyle" ||
            styleProp == "backgroundColor" ||
            styleProp == "backgroundImage" ||
            styleProp == "mouseShield" ||
            styleProp == "mouseShieldChildren")
        {
            createBorder();
        }

        super.styleChanged(styleProp);

        // Check to see if this is one of the style properties that is known.
        // to affect page layout.
        if (allStyles ||
            StyleManager.isSizeInvalidatingStyle(styleProp))
        {
            invalidateViewMetricsAndPadding();
        }

        if (allStyles || styleProp == "horizontalScrollBarStyleName")
        {
            if (horizontalScrollBar && horizontalScrollBar is ISimpleStyleClient)
            {
                var horizontalScrollBarStyleName:String =
                    getStyle("horizontalScrollBarStyleName");
                ISimpleStyleClient(horizontalScrollBar).styleName =
                    horizontalScrollBarStyleName;
            }
        }

        if (allStyles || styleProp == "verticalScrollBarStyleName")
        {
            if (verticalScrollBar && verticalScrollBar is ISimpleStyleClient)
            {
                var verticalScrollBarStyleName:String =
                    getStyle("verticalScrollBarStyleName");
                ISimpleStyleClient(verticalScrollBar).styleName =
                    verticalScrollBarStyleName;
            }
        }
    }

    /**
     *  @private
     *  Call the styleChanged method on children of this container
     *
     *  Notify chrome children immediately, and recursively call this
     *  function for all descendants of the chrome children.  We recurse
     *  regardless of the recursive flag because one of the descendants
     *  might have a styleName property that points to this object.
     *
     *  If recursive is true, then also notify content children ... but
     *  do it later.  Notification is deferred so that multiple calls to
     *  setStyle can be batched up into one traversal.
     */
    override public function notifyStyleChangeInChildren(
                                styleProp:String, recursive:Boolean):void
    {
        // Notify chrome children immediately, recursively calling this
        // this function
        var n:int = super.numChildren;
        for (var i:int = 0; i < n; i++)
        {
            // Is this a chrome child?
            if (contentPane ||
                i < _firstChildIndex ||
                i >= _firstChildIndex + _numChildren)
            {
                var child:ISimpleStyleClient = super.getChildAt(i) as ISimpleStyleClient;
                if (child)
                {
                    child.styleChanged(styleProp);
                    if (child is IStyleClient)
                        IStyleClient(child).notifyStyleChangeInChildren(styleProp, recursive);
                }
            }
        }

        // If recursive, then remember to notify the content children later
        if (recursive)
        {
            // If multiple styleProps have changed, set changedStyles to
            // MULTIPLE_PROPERTIES.  Otherwise, set it to the name of the
            // changed property.
            changedStyles = (changedStyles != null || styleProp == null) ?
                MULTIPLE_PROPERTIES : styleProp;
            invalidateProperties();
        }
    }

    /**
     *  @private
     */
    override public function regenerateStyleCache(recursive:Boolean):void
    {
        super.regenerateStyleCache(recursive);

        if (contentPane)
        {
            // Do the same thing as UIComponent, but don't check the child's index to
            // ascertain that it's a content child (we already know that here).

            var n:int = contentPane.numChildren;
            for (var i:int = 0; i < n; i++)
            {
                var child:DisplayObject = getChildAt(i);

                if (recursive && child is UIComponent)
                {
                    // Does this object already have a proto chain?  If not,
                    // there's no need to regenerate a new one.
                    if (UIComponent(child).inheritingStyles != UIComponent.STYLE_UNINITIALIZED)
                        UIComponent(child).regenerateStyleCache(recursive);
                }
                else if (child is IUITextField && IUITextField(child).inheritingStyles)
                {
                    StyleProtoChain.initTextField(IUITextField(child));
                }
            }
        }
    }
    
    /**
     *  Used internally by the Dissolve Effect to add the overlay to the chrome of a container. 
     */
    override protected function attachOverlay():void
    {
        rawChildren_addChild(overlay);
    }

    /**
     *  Fill an overlay object which is always the topmost child in the container.
     *  This method is used
     *  by the Dissolve effect; never call it directly. It is called
     *  internally by the <code>addOverlay()</code> method.
     *
     *  The Container fills the overlay object so it covers the viewable area returned
     *  by the <code>viewMetrics</code> property and uses the <code>cornerRadius</code> style.
     */
    override mx_internal function fillOverlay(overlay:UIComponent, color:uint,
                                              targetArea:RoundedRectangle = null):void
    {
        var vm:EdgeMetrics = viewMetrics;
        var cornerRadius:Number = 0; //getStyle("cornerRadius");

        if (!targetArea)
        {
            targetArea = new RoundedRectangle(
                vm.left, vm.top,
                unscaledWidth - vm.right - vm.left,
                unscaledHeight - vm.bottom - vm.top,cornerRadius);
        }
        
        if (isNaN(targetArea.x) || isNaN(targetArea.y) ||
            isNaN(targetArea.width) || isNaN(targetArea.height) ||
            isNaN(targetArea.cornerRadius))
            return;
        
        var g:Graphics = overlay.graphics;
        g.clear();
        g.beginFill(color);
        g.drawRoundRect(targetArea.x, targetArea.y,
                        targetArea.width, targetArea.height,
                        targetArea.cornerRadius * 2,
                        targetArea.cornerRadius * 2);
        g.endFill();
    }

    /**
     *  Executes all the data bindings on this Container. Flex calls this method
     *  automatically once a Container has been created to cause any data bindings that
     *  have destinations inside of it to execute.
     *
     *  Workaround for MXML container/bindings problem (177074):
     *  override Container.executeBindings() to prefer descriptor.document over parentDocument in the
     *  call to BindingManager.executeBindings().
     *
     *  This should always provide the correct behavior for instances created by descriptor, and will
     *  provide the original behavior for procedurally-created instances. (The bug may or may not appear
     *  in the latter case.)
     *
     *  A more complete fix, guaranteeing correct behavior in both non-DI and reparented-component
     *  scenarios, is anticipated for updater 1.
     *
     *  @param recurse If <code>false</code>, then only execute the bindings
     *  on this Container. 
     *  If <code>true</code>, then also execute the bindings on this
     *  container's children, grandchildren,
     *  great-grandchildren, and so on.
     */
    override public function executeBindings(recurse:Boolean = false):void
    {
        var bindingsHost:Object = descriptor && descriptor.document ? descriptor.document : parentDocument;
        BindingManager.executeBindings(bindingsHost, id, this);

        if (recurse)
            executeChildBindings(recurse);
    }

    /**
     *  @private
     *  Prepare the Object for printing
     *
     *  @see mx.printing.FlexPrintJob
     */
    override public function prepareToPrint(target:IFlexDisplayObject):Object
    {
        var rect:Rectangle = (contentPane &&  contentPane.scrollRect) ? contentPane.scrollRect : null;

        if (rect)
            contentPane.scrollRect = null;

        super.prepareToPrint(target);

        return rect;
    }

    /**
     *  @private
     *  After printing is done
     *
     *  @see mx.printing.FlexPrintJob
     */
    override public function finishPrint(obj:Object, target:IFlexDisplayObject):void
    {
        if (obj)
            contentPane.scrollRect = Rectangle(obj);

        super.finishPrint(obj,target);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Child management
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override mx_internal function addingChild(child:DisplayObject):void
    {
        // Throw an RTE if child is not an IUIComponent.
        var uiChild:IUIComponent = IUIComponent(child);

        // Set the child's virtual parent, nestLevel, document, etc.
        super.addingChild(child);
        
        invalidateSize();
        invalidateDisplayList();

        if (!contentPane)
        {
            // If this is the first content child, then any chrome
            // that already exists is positioned in front of it.
            // If other content children already existed, then set the
            // depth of this object to be just behind the existing
            // content children.
            if (_numChildren == 0)
                _firstChildIndex = super.numChildren;

            // Increment the number of content children.
            _numChildren++;
        }

        if (contentPane && !autoLayout)
        {
            forceLayout = true;
            // weak reference
            UIComponentGlobals.layoutManager.addEventListener(
                FlexEvent.UPDATE_COMPLETE, layoutCompleteHandler, false, 0, true);
        }
    }

    /**
     *  @private
     */
    override mx_internal function childAdded(child:DisplayObject):void
    {
        dispatchEvent(new Event("childrenChanged"));

        var event:ChildExistenceChangedEvent =
            new ChildExistenceChangedEvent(
            ChildExistenceChangedEvent.CHILD_ADD);
        event.relatedObject = child;
        dispatchEvent(event);

        child.dispatchEvent(new FlexEvent(FlexEvent.ADD));
                
        super.childAdded(child); // calls createChildren()
    }

    /**
     *  @private
     */
    override mx_internal function removingChild(child:DisplayObject):void
    {
        super.removingChild(child);

        child.dispatchEvent(new FlexEvent(FlexEvent.REMOVE));

        var event:ChildExistenceChangedEvent =
            new ChildExistenceChangedEvent(
            ChildExistenceChangedEvent.CHILD_REMOVE);
        event.relatedObject = child;
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    override mx_internal function childRemoved(child:DisplayObject):void
    {
        super.childRemoved(child);
        
        invalidateSize();
        invalidateDisplayList();

        if (!contentPane)
        {
            _numChildren--;

            if (_numChildren == 0)
                _firstChildIndex = super.numChildren;
        }

        if (contentPane && !autoLayout)
        {
            forceLayout = true;
            // weak reference
            UIComponentGlobals.layoutManager.addEventListener(
                FlexEvent.UPDATE_COMPLETE, layoutCompleteHandler, false, 0, true);
        }

        dispatchEvent(new Event("childrenChanged"));
    }

    [Bindable("childrenChanged")]
    
    /**
     *  Returns an Array of DisplayObject objects consisting of the content children 
     *  of the container.
     *  This array does <b>not</b> include the DisplayObjects that implement 
     *  the container's display elements, such as its border and 
     *  the background image.
     *
     *  @return Array of DisplayObject objects consisting of the content children 
     *  of the container.
     * 
     *  @see #rawChildren
     */
    public function getChildren():Array
    {
        var results:Array = [];
        
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            results.push(getChildAt(i));
        }

        return results;
    }

    /**
     *  Removes all children from the child list of this container.
     */
    public function removeAllChildren():void
    {
        while (numChildren > 0)
        {
            removeChildAt(0);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Deferred instantiation
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  For containers, we need to ensure that at most one set of children
     *  has been specified for the component.
     *  There are two ways to specify multiple sets of children:
     *  a) the component itself, as well as an instance of the component,
     *  might specify children;
     *  b) both a base and derived component might specify children.
     *  Case (a) is handled in initialize(), above.
     *  Case (b) is handled here.
     *  This method is called in overrides of initialize()
     *  that are generated for MXML components.
     */
    mx_internal function setDocumentDescriptor(desc:UIComponentDescriptor):void
    {
        if (processedDescriptors)
            return;

        if (_documentDescriptor && _documentDescriptor.properties.childDescriptors)
        {
            if (desc.properties.childDescriptors)
            {
                var message:String = resourceManager.getString(
                    "core", "multipleChildSets_ClassAndSubclass");
                throw new Error(message);
            }
        }
        else
        {
            _documentDescriptor = desc;
            _documentDescriptor.document = this;
        }
    }

    /**
     *  @private
     *  Used by subclasses, so must be public.
     */
    mx_internal function setActualCreationPolicies(policy:String):void
    {
        actualCreationPolicy = policy;

        // Recursively set the actualCreationPolicy of all descendant
        // containers which have an undefined creationPolicy.
        var childPolicy:String = policy;

        if (policy == ContainerCreationPolicy.QUEUED)
            childPolicy = ContainerCreationPolicy.AUTO;

        //trace("setActualCreationPolicies policy", policy, "childPolicy", childPolicy);

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IFlexDisplayObject = IFlexDisplayObject(getChildAt(i));
            if (child is Container)
            {
                var childContainer:Container = Container(child);
                if (childContainer.creationPolicy == null)
                    childContainer.setActualCreationPolicies(childPolicy);
            }
        }
    }

    /**
     *  Iterate through the Array of <code>childDescriptors</code>,
     *  and call the <code>createComponentFromDescriptor()</code> method for each one.
     *  
     *  <p>If the value of the container's <code>creationPolicy</code> property is
     *  <code>ContainerCreationPolicy.ALL</code>, then this method is called
     *  automatically during the initialization sequence.</p>
     *  
     *  <p>If the value of the container's <code>creationPolicy</code> is
     *  <code>ContainerCreationPolicy.AUTO</code>,
     *  then this method is called automatically when the
     *  container's children are about to become visible.</p>
     *  
     *  <p>If the value of the container's <code>creationPolicy</code> property is
     *  <code>ContainerCreationPolicy.NONE</code>,
     *  then you should call this function
     *  when you want to create this container's children.</p>
     *
     *  @param recurse If <code>true</code>, recursively
     *  create components.
     */
    public function createComponentsFromDescriptors(
                        recurse:Boolean = true):void
    {
        numChildrenBefore = numChildren;

        createdComponents = [];

        var n:int = childDescriptors ? childDescriptors.length : 0;
        for (var i:int = 0; i < n; i++)
        {
            var component:IFlexDisplayObject =
                createComponentFromDescriptor(childDescriptors[i], recurse);
            
            createdComponents.push(component);
        }

        if (creationPolicy == ContainerCreationPolicy.QUEUED ||
            creationPolicy == ContainerCreationPolicy.NONE)
        {
            UIComponentGlobals.layoutManager.usePhasedInstantiation = false;
        }

        numChildrenCreated = numChildren - numChildrenBefore;

        processedDescriptors = true;
    }

   /**
     *  Given a single UIComponentDescriptor, create the corresponding
     *  component and add the component as a child of this Container.
     *  
     *  <p>This method instantiates the new object but does not add it to the display list, so the object does not 
     *  appear on the screen by default. To add the new object to the display list, call the <code>validateNow()</code>
     *  method on the container after calling the <code>createComponentFromDescriptor()</code> method,
     *  as the following example shows:
     *  <pre>
     *  myVBox.createComponentFromDescriptor(myVBox.childDescriptors[0],false);
     *  myVBox.validateNow();
     *  </pre>
     *  </p>
     *  
     *  <p>Alternatively, you can call the <code>createComponentsFromDescriptors()</code> method on the 
     *  container to create all components at one time. You are not required to call the <code>validateNow()</code>
     *  method after calling the <code>createComponentsFromDescriptors()</code> method.</p>
     *  
     *
     *  @param descriptorOrIndex The UIComponentDescriptor for the
     *  component to be created. This argument is either a
     *  UIComponentDescriptor object or the index of one of the container's
     *  children (an integer between 0 and n-1, where n is the total
     *  number of children of this container).
     *
     *  @param recurse If <code>false</code>, create this component
     *  but none of its children.
     *  If <code>true</code>, after creating the component, Flex calls
     *  the <code>createComponentsFromDescriptors()</code> method to create all or some
     *  of its children, based on the value of the component's <code>creationPolicy</code> property.
     *
     *  @see mx.core.UIComponentDescriptor
     */
    public function createComponentFromDescriptor(
                            descriptor:ComponentDescriptor,
                            recurse:Boolean):IFlexDisplayObject
    {
        // If recurse is 'false', we create this component but none
        // of its children.

        // If recurse is 'true', after creating the component we call
        // createComponentsFromDescriptors() to create all or some
        // of its children, based on the component's
        // actualContainerCreationPolicy.

        var childDescriptor:UIComponentDescriptor =
            UIComponentDescriptor(descriptor);

        var childProperties:Object = childDescriptor.properties;

        // This function could be asked to create the same child component
        // twice.  That's fine if the child component is inside a repeater.
        // In other cases, though, we want to avoid creating the same child
        // twice.
        //
        // The hasChildMatchingDescriptor function is a bit expensive, so
        // we try to avoid calling it if we know we're inside the first call
        // to createComponents.
        if ((numChildrenBefore != 0 || numChildrenCreated != -1) &&
            childDescriptor.instanceIndices == null &&
            hasChildMatchingDescriptor(childDescriptor))
        {
            return null;
        }

        // Turn on the three-frame instantiation scheme.
        UIComponentGlobals.layoutManager.usePhasedInstantiation = true;

        // Create the new child object and give it a unique name
        var childType:Class = childDescriptor.type;
        var child:IDeferredInstantiationUIComponent = new childType();
        child.id = childDescriptor.id;
        if (child.id && child.id != "")
            child.name = child.id;

        // Copy property values from the descriptor
        // to the newly created component.
        child.descriptor = childDescriptor;
        if (childProperties.childDescriptors && child is Container)
        {
            Container(child)._childDescriptors =
                childProperties.childDescriptors;
            delete childProperties.childDescriptors;
        }
        for (var p:String in childProperties)
        {
            child[p] = childProperties[p];
        }

        // Set a flag indicating whether we should call
        // this function recursively.
        if (child is Container)
            Container(child).recursionFlag = recurse;

        if (childDescriptor.instanceIndices)
        {
            if (child is IRepeaterClient)
            {
                var rChild:IRepeaterClient = IRepeaterClient(child);
                rChild.instanceIndices = childDescriptor.instanceIndices;
                rChild.repeaters = childDescriptor.repeaters;
                rChild.repeaterIndices = childDescriptor.repeaterIndices;
            }
        }

        if (child is IStyleClient)
        {
            var scChild:IStyleClient = IStyleClient(child);

            // Initialize the CSSStyleDeclaration.
            // It is used by initProtoChain(), which is called by addChild().
            if (childDescriptor.stylesFactory != null)
            {
                if (!scChild.styleDeclaration)
                    scChild.styleDeclaration = new CSSStyleDeclaration();
                scChild.styleDeclaration.factory =
                    childDescriptor.stylesFactory;
            }
        }

        // For each event, register the handle method, which is specified in
        // the descriptor by name, as one of the child's event listeners.
        var childEvents:Object = childDescriptor.events;
        if (childEvents)
        {
            for (var eventName:String in childEvents)
            {
                var eventHandler:String = childEvents[eventName];
                child.addEventListener(eventName,
                                       childDescriptor.document[eventHandler]);
            }
        }

        // For each effect, register the EffectManager as an event listener
        var childEffects:Array = childDescriptor.effects;
        if (childEffects)
            child.registerEffects(childEffects);

        if (child is IRepeaterClient)
            IRepeaterClient(child).initializeRepeaterArrays(this);

        // If an MXML id was specified, create a property with this name on
        // the MXML document object whose value references the child.
        // This should be the last step in initializing the child, so that
        // it can't be referenced until initialization is complete.
        // However, it must be done before executing executeBindings().
        child.createReferenceOnParentDocument(
            IFlexDisplayObject(childDescriptor.document));

        if (!child.document)
            child.document = childDescriptor.document;

        // Repeaters don't get added as children of the Container,
        // so they have their own initialization sequence.
        if (child is IRepeater)
        {
            // Add this repeater to the list maintained by the parent
            // container
            if (!childRepeaters)
                childRepeaters = [];
            childRepeaters.push(child);

            // The Binding Manager may have some data that it wants to bind to
            // various properties of the newly created repeater.
            child.executeBindings();

            IRepeater(child).initializeRepeater(this, recurse);
        }
        else
        {
            // This needs to run before child.executeBindings(), because
            // executeBindings() depends on the parent being set.
            addChild(DisplayObject(child));

            child.executeBindings();

            if (creationPolicy == ContainerCreationPolicy.QUEUED ||
                creationPolicy == ContainerCreationPolicy.NONE)
            {
                child.addEventListener(FlexEvent.CREATION_COMPLETE,
                                       creationCompleteHandler);
            }
        }

        // Return a reference to the child UIComponent that was just created.
        return child;
    }

    /**
     *  @private
     */
    private function hasChildMatchingDescriptor(
                            descriptor:UIComponentDescriptor):Boolean
    {
        // Optimization: If the descriptor has an id but no such id
        // reference exists on the document, then there are no children
        // in this container with that descriptor.
        // (On the other hand, if there IS an id reference on the document,
        // we can't be sure that it is for a child of this container. It
        // could be an indexed reference in which some instances are
        // in other containers. This happens when you have
        // <Repeater>
        //     <VBox>
        //         <Button>
        var id:String = descriptor.id;
        if (id != null && document[id] == null)
            return false;

        var n:int = numChildren;
        var i:int;
        
        // Iterate over this container's children, looking for one
        // that matches this descriptor
        for (i = 0; i < n; i++)
        {
            var child:IUIComponent = IUIComponent(getChildAt(i));
            if (child is IDeferredInstantiationUIComponent &&
                IDeferredInstantiationUIComponent(child)
                    .descriptor == descriptor)
            {
                return true;
            }
        }

        // Also check this container's Repeaters, if there are any.
        if (childRepeaters)
        {
            n = childRepeaters.length;
            for (i = 0; i < n; i++)
            {
                if (IDeferredInstantiationUIComponent(childRepeaters[i])
                        .descriptor == descriptor)
                {
                    return true;
                }
            }
        }

        return false;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Support for rawChildren access
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This class overrides addChild() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_addChild(child:DisplayObject):DisplayObject
    {
        // This method is only used to implement rawChildren.addChild(),
        // so the child being added is assumed to be a non-content child.
        // (You would use just addChild() to add a content child.)
        // If there are no content children, the new child is placed
        // in the pre-content partition.
        // If there are content children, the new child is placed
        // in the post-content partition.
        if (_numChildren == 0)
            _firstChildIndex++;

        super.addingChild(child);
        $addChild(child);
        super.childAdded(child);

        dispatchEvent(new Event("childrenChanged"));

        return child;
    }

    /**
     *  @private
     *  This class overrides addChildAt() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_addChildAt(child:DisplayObject,
                                                index:int):DisplayObject
    {
        if (_firstChildIndex < index &&
            index < _firstChildIndex + _numChildren + 1)
        {
            _numChildren++;
        }
        else if (index <= _firstChildIndex)
        {
            _firstChildIndex++;
        }

        super.addingChild(child);
        $addChildAt(child, index);
        super.childAdded(child);

        dispatchEvent(new Event("childrenChanged"));

        return child;
    }

    /**
     *  @private
     *  This class overrides removeChild() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_removeChild(
                                child:DisplayObject):DisplayObject
    {
        var index:int = rawChildren_getChildIndex(child);
        return rawChildren_removeChildAt(index);
    }

    /**
     *  @private
     *  This class overrides removeChildAt() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_removeChildAt(index:int):DisplayObject
    {
        var child:DisplayObject = super.getChildAt(index);

        super.removingChild(child);
        $removeChildAt(index);
        super.childRemoved(child);

        if (_firstChildIndex < index &&
            index < _firstChildIndex + _numChildren)
        {
            _numChildren--;
        }
        else if (_numChildren == 0 || index < _firstChildIndex)
        {
            _firstChildIndex--;
        }

        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("childrenChanged"));

        return child;
    }

    /**
     *  @private
     *  This class overrides getChildAt() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_getChildAt(index:int):DisplayObject
    {
        return super.getChildAt(index);
    }

    /**
     *  @private
     *  This class overrides getChildByName() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_getChildByName(name:String):DisplayObject
    {
        return super.getChildByName(name);
    }

    /**
     *  @private
     *  This class overrides getChildIndex() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_getChildIndex(child:DisplayObject):int
    {
        return super.getChildIndex(child);
    }

    /**
     *  @private
     *  This class overrides setChildIndex() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_setChildIndex(child:DisplayObject,
                                                   newIndex:int):void
    {
        var oldIndex:int = super.getChildIndex(child);

        super.setChildIndex(child, newIndex);

        // Is this a piece of chrome that was previously before
        // the content children and is now after them in the list?
        if (oldIndex < _firstChildIndex && newIndex >= _firstChildIndex)
        {
            _firstChildIndex--;
        }

        // Is this a piece of chrome that was previously after
        // the content children and is now before them in the list?
        else if (oldIndex >= _firstChildIndex && newIndex <= _firstChildIndex)
        {
            _firstChildIndex++
        }

        dispatchEvent(new Event("childrenChanged"));
    }
   
    /**
     *  @private
     *  This class overrides getObjectsUnderPoint() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_getObjectsUnderPoint(pt:Point):Array
    {
        return super.getObjectsUnderPoint(pt);
    }
  
    /**
     *  @private
     *  This class overrides contains() to deal with only content children,
     *  so in order to implement the rawChildren property we need
     *  a parallel method that deals with all children.
     */
    mx_internal function rawChildren_contains(child:DisplayObject):Boolean
    {
        return super.contains(child);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Chrome management
    //
    //--------------------------------------------------------------------------

    /**
     *  Respond to size changes by setting the positions and sizes
     *  of this container's borders.
     *  This is an advanced method that you might override
     *  when creating a subclass of Container.
     *
     *  <p>Flex calls the <code>layoutChrome()</code> method when the
     *  container is added to a parent container using the <code>addChild()</code> method,
     *  and when the container's <code>invalidateDisplayList()</code> method is called.</p>
     *
     *  <p>The <code>Container.layoutChrome()</code> method is called regardless of the
     *  value of the <code>autoLayout</code> property.</p>
     *
     *  <p>The <code>Container.layoutChrome()</code> method sets the
     *  position and size of the Container container's border.
     *  In every subclass of Container, the subclass's <code>layoutChrome()</code>
     *  method should call the <code>super.layoutChrome()</code> method,
     *  so that the border is positioned properly.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     */
    protected function layoutChrome(unscaledWidth:Number,
                                    unscaledHeight:Number):void
    {
        // Border covers the whole thing.
        if (border)
        {
            updateBackgroundImageRect();

            border.move(0, 0);
            border.setActualSize(unscaledWidth, unscaledHeight);
        }
    }

    /**
     *  Creates the container's border skin 
     *  if it is needed and does not already exist.
     */
    protected function createBorder():void
    {
        if (!border && isBorderNeeded())
        {
            var borderClass:Class = getStyle("borderSkin");

            if (borderClass != null)
            {
                border = new borderClass();
                border.name = "border";

                if (border is IUIComponent)
                    IUIComponent(border).enabled = enabled;
                if (border is ISimpleStyleClient)
                    ISimpleStyleClient(border).styleName = this;

                // Add the border behind all the children.
                rawChildren.addChildAt(DisplayObject(border), 0);

                invalidateDisplayList();
            }
        }
    }

    /**
     *  @private
     */
    private function isBorderNeeded():Boolean
    {
        //trace("isBorderNeeded",this,"ms",getStyle("mouseShield"),"borderStyle",getStyle("borderStyle"));

        // If the borderSkin is a custom class, always assume the border is needed.
        var c:Class = getStyle("borderSkin");
        
        // Lookup the HaloBorder class by name to avoid a linkage dependency.
        // Note: this code assumes HaloBorder is the default border skin. If this is changed
        // in defaults.css, it must also be changed here.
        try
        {
            if (c != getDefinitionByName("mx.skins.halo::HaloBorder"))
                return true;
        }
        catch(e:Error)
        {
            return true;
        }
            
        var v:Object = getStyle("borderStyle");
        if (v)
        {
            // If borderStyle is "none", then only create a border if the mouseShield style is true
            // (meaning that there is a mouse event listener on this view). We don't create a border
            // if our parent's mouseShieldChildren style is true.
            if ((v != "none") || (v == "none" && getStyle("mouseShield")))
            {
                return true;
            }
        }

        v = getStyle("backgroundColor");
        if (v !== null && v !== "")
            return true;

        v = getStyle("backgroundImage");
        return v != null && v != "";
    }

    /**
     *  @private
     */
    mx_internal function invalidateViewMetricsAndPadding():void
    {
        _viewMetricsAndPadding = null;
    }

    /**
     *  @private
     */
    private function createOrDestroyBlocker():void
    {
        // If this container is being enabled and a blocker exists,
        // remove it. If this container is being disabled and a
        // blocker doesn't exist, create it.
        if (enabled)
        {
            if (blocker)
            {
                rawChildren.removeChild(blocker);
                blocker = null;
            }
        }
        else
        {
            if (!blocker)
            {
                blocker = new FlexSprite();
                blocker.name = "blocker";
                blocker.mouseEnabled = true;
                rawChildren.addChild(blocker);

                blocker.addEventListener(MouseEvent.CLICK,
                                         blocker_clickHandler);

                // If the focus is a child of ours, we clear it here.
                var o:DisplayObject =
                    focusManager ?
                    DisplayObject(focusManager.getFocus()) :
                    null;

                while (o)
                {
                    if (o == this)
                    {
                        var sm:ISystemManager = systemManager;
                        if (sm && sm.stage)
                            sm.stage.focus = null;
                        break;
                    }
                    o = o.parent;
                }
            }
        }
    }

    /**
     *  @private
     */
    private function updateBackgroundImageRect():void
    {
        var rectBorder:IRectangularBorder = border as IRectangularBorder;

        if (!rectBorder)
            return;

        // If viewableWidth and viewableHeight are 0, we don't have any
        // scrollbars or clipped content.
        if (viewableWidth == 0 && viewableHeight == 0)
        {
            rectBorder.backgroundImageBounds = null;
            return;
        }

        var vm:EdgeMetrics = viewMetrics;
        var bkWidth:Number = viewableWidth ? viewableWidth :
                    unscaledWidth - vm.left - vm.right;
        var bkHeight:Number = viewableHeight ? viewableHeight :
                    unscaledHeight - vm.top - vm.bottom;

        if (getStyle("backgroundAttachment") == "fixed")
        {
            rectBorder.backgroundImageBounds = new Rectangle(vm.left, vm.top,
                                                bkWidth, bkHeight);
        }
        else
        {
            rectBorder.backgroundImageBounds = new Rectangle(vm.left, vm.top,
                    Math.max(scrollableWidth, bkWidth),
                    Math.max(scrollableHeight, bkHeight));
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Scrolling
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function createContentPaneAndScrollbarsIfNeeded():Boolean
    {
        var bounds:Rectangle;
        var changed:Boolean;

        // No mask is needed if clipping isn't active
        if (_clipContent)
        {
            // Get the new scrollable width, which is the equal to the right
            // edge of the rightmost child.  Also get the new scrollable height.
            bounds = getScrollableRect();

            // Create or destroy scrollbars if necessary, and update the
            // properties of the scrollbars.
            changed = createScrollbarsIfNeeded(bounds);

            if (border)
                updateBackgroundImageRect();

            return changed;
        }
        else
        {
            changed = createOrDestroyScrollbars(false, false, false);

            // Get scrollableWidth and scrollableHeight for scrollChildren()
            bounds = getScrollableRect();
            scrollableWidth = bounds.right;
            scrollableHeight = bounds.bottom;

            if (changed && border)
                updateBackgroundImageRect();

            return changed;
        }
    }

    /**
     *  @private
     */
    mx_internal function getScrollableRect():Rectangle
    {
        var left:Number = 0;
        var top:Number = 0;
        var right:Number = 0;
        var bottom:Number = 0;

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:DisplayObject = getChildAt(i);

            if (child is IUIComponent && !IUIComponent(child).includeInLayout)
                continue;

            left = Math.min(left, child.x);
            top = Math.min(top, child.y);

            // width/height can be NaN if using percentages and
            // hasn't been layed out yet.
            if (!isNaN(child.width))
                right = Math.max(right, child.x + child.width);
            if (!isNaN(child.height))
                bottom = Math.max(bottom, child.y + child.height);
        }

        // Add in the right/bottom margins and view metrics.
        var vm:EdgeMetrics = viewMetrics;

        var bounds:Rectangle = new Rectangle();
        bounds.left = left;
        bounds.top = top;
        bounds.right = right;
        bounds.bottom = bottom;

        if (mx_internal::usePadding)
        {
            bounds.right += getStyle("paddingRight");
            bounds.bottom += getStyle("paddingBottom");
        }

        return bounds;
    }

    /**
     *  @private
     */
    private function createScrollbarsIfNeeded(bounds:Rectangle):Boolean
    {
        var newScrollableWidth:Number = bounds.right;
        var newScrollableHeight:Number = bounds.bottom;
        var newViewableWidth:Number = unscaledWidth;
        var newViewableHeight:Number = unscaledHeight;
        var hasNegativeCoords:Boolean = bounds.left < 0 || bounds.top < 0;

        var vm:EdgeMetrics = viewMetrics;

        // Several of the layout managers round floating-point numbers
        // down, using Math.floor().
        // The rounded-down width value is passed to UIComponent.setActualSize,
        // which does the following:
        //
        //   unscaledWidth = w / scaleX
        //
        // Suppose "w" was originally 91.9 but the layout manager
        // rounded it down to 91. Suppose scaleX is 0.01.
        // Then unscaledWidth is 91/0.01 = 9100, but it would have been
        // 91.9/0.01 = 9190 if it weren't for the rounding.
        // To undo the effect of the rounding, we'll add a fudge factor to
        // newViewableWidth. That way, we don't display unwanted scrollbars.
        if (scaleX != 1.0)
            newViewableWidth += 1.0 / Math.abs(scaleX);
        if (scaleY != 1.0)
            newViewableHeight += 1.0 / Math.abs(scaleY);

        newViewableWidth = Math.floor(newViewableWidth);
        newViewableHeight = Math.floor(newViewableHeight);
        newScrollableWidth = Math.floor(newScrollableWidth);
        newScrollableHeight = Math.floor(newScrollableHeight);

        if (horizontalScrollBar && horizontalScrollPolicy != ScrollPolicy.ON)
            newViewableHeight -= horizontalScrollBar.minHeight;
        if (verticalScrollBar && verticalScrollPolicy != ScrollPolicy.ON)
            newViewableWidth -= verticalScrollBar.minWidth;

        newViewableWidth -= (vm.left + vm.right);
        newViewableHeight -= (vm.top + vm.bottom);
        
        var needHorizontal:Boolean =
            horizontalScrollPolicy == ScrollPolicy.ON;
        var needVertical:Boolean =
            verticalScrollPolicy == ScrollPolicy.ON;

        var needContentPane:Boolean =
            needHorizontal ||
            needVertical ||
            hasNegativeCoords ||
            overlay != null ||
            vm.left > 0 ||
            vm.top > 0;

        // These "if" statements are tuned for the most common case,
        // which is that the Container does not need scrollbars.
        if (newViewableWidth < newScrollableWidth)
        {
            needContentPane = true;

            // Don't display scrollbars if the Container is so small
            // that scrollbars would occlude everything else
            // or the scrollbar buttons would overlap.
            if (horizontalScrollPolicy == ScrollPolicy.AUTO &&
                unscaledHeight - vm.top - vm.bottom >= 18 &&
                unscaledWidth - vm.left - vm.right >= 32)
            {
                needHorizontal = true;
            }
        }
        if (newViewableHeight < newScrollableHeight)
        {
            needContentPane = true;

            if (verticalScrollPolicy == ScrollPolicy.AUTO &&
                unscaledWidth - vm.left - vm.right >= 18 &&
                unscaledHeight - vm.top - vm.bottom >= 32)
            {
                needVertical = true;
            }
        }

        // Fix for 106095. The logic here says "if removing the scrollbars
        // would make enough room to display the view's children, then remove
        // the scrollbars".
        if (needHorizontal &&
            needVertical &&
            horizontalScrollPolicy == ScrollPolicy.AUTO &&
            verticalScrollPolicy == ScrollPolicy.AUTO &&
            horizontalScrollBar &&
            verticalScrollBar &&
            newViewableWidth + verticalScrollBar.minWidth >= newScrollableWidth &&
            newViewableHeight + horizontalScrollBar.minHeight >= newScrollableHeight)
        {
            needHorizontal = needVertical = false;
        }

        // If the vertical scrollbar is going to be removed anyway, and
        // removing it would also free up enough space for the contents to fit
        // horizontally, then there's no need for the horizontal scrollbar
        // either.
        else if (needHorizontal &&
                 !needVertical &&
                 verticalScrollBar &&
                 horizontalScrollPolicy == ScrollPolicy.AUTO &&
                 newViewableWidth + verticalScrollBar.minWidth >= newScrollableWidth)
        {
            needHorizontal = false;
        }

        var changed:Boolean = createOrDestroyScrollbars(
            needHorizontal, needVertical, needContentPane);

        if ((scrollableWidth != newScrollableWidth ||
             viewableWidth != newViewableWidth) ||
             changed)
        {
            if (horizontalScrollBar)
            {
                horizontalScrollBar.setScrollProperties(
                    newViewableWidth, 0,
                    newScrollableWidth - newViewableWidth, horizontalPageScrollSize);
                scrollPositionChanged = true;   
            }
            
            viewableWidth = newViewableWidth;
            scrollableWidth = newScrollableWidth;
        }

        if ((scrollableHeight != newScrollableHeight ||
             viewableHeight != newViewableHeight) ||
             changed)
        {
        
            if (verticalScrollBar)
            {
                verticalScrollBar.setScrollProperties(
                    newViewableHeight, 0,
                    newScrollableHeight-newViewableHeight, verticalPageScrollSize);

                scrollPositionChanged = true;       
            }       
                    
            viewableHeight = newViewableHeight;
            scrollableHeight = newScrollableHeight;
        }

        return changed;
    }

    /**
     *  @private
     */
    private function createOrDestroyScrollbars(
                            needHorizontal:Boolean,
                            needVertical:Boolean,
                            needContentPane:Boolean):Boolean
    {
        var changed:Boolean = false;
        var fm:IFocusManager;

        if (needHorizontal || needVertical || needContentPane)
            createContentPane();

        // Create or destroy horizontal scrollbar.
        if (needHorizontal)
        {
            if (!horizontalScrollBar)
            {
                horizontalScrollBar = new HScrollBar();
                horizontalScrollBar.name = "horizontalScrollBar";

                var horizontalScrollBarStyleName:String =
                    getStyle("horizontalScrollBarStyleName");
                if (horizontalScrollBarStyleName && horizontalScrollBar is ISimpleStyleClient)
                    ISimpleStyleClient(horizontalScrollBar).styleName = horizontalScrollBarStyleName;

                rawChildren.addChild(DisplayObject(horizontalScrollBar));
                horizontalScrollBar.lineScrollSize = horizontalLineScrollSize;
                horizontalScrollBar.pageScrollSize = horizontalPageScrollSize;
                horizontalScrollBar.addEventListener(ScrollEvent.SCROLL, horizontalScrollBar_scrollHandler);
                horizontalScrollBar.enabled = enabled;
                if (horizontalScrollBar is IInvalidating)
                    IInvalidating(horizontalScrollBar).validateNow();

                invalidateDisplayList();
                invalidateViewMetricsAndPadding();

                changed = true;

                if (!verticalScrollBar)
                    addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);
            }
        }
        else
        {
            if (horizontalScrollBar)
            {
                horizontalScrollBar.removeEventListener(
                    ScrollEvent.SCROLL,
                    horizontalScrollBar_scrollHandler);

                rawChildren.removeChild(DisplayObject(horizontalScrollBar));
                horizontalScrollBar = null;

                viewableWidth = scrollableWidth = 0;

                if (_horizontalScrollPosition != 0)
                {
                    _horizontalScrollPosition = 0;
                    scrollPositionChanged = true;
                }

                invalidateDisplayList();
                invalidateViewMetricsAndPadding();

                changed = true;

                fm = focusManager;
                if (!verticalScrollBar && (!fm || fm.getFocus() != this))
                    removeEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);
            }
        }

        // Create or destroy vertical scrollbar.
        if (needVertical)
        {
            if (!verticalScrollBar)
            {
                verticalScrollBar = new VScrollBar();
                verticalScrollBar.name = "verticalScrollBar";

                var verticalScrollBarStyleName:String =
                    getStyle("verticalScrollBarStyleName");
                if (verticalScrollBarStyleName && verticalScrollBar is ISimpleStyleClient)
                    ISimpleStyleClient(verticalScrollBar).styleName = verticalScrollBarStyleName;

                rawChildren.addChild(DisplayObject(verticalScrollBar));
                verticalScrollBar.lineScrollSize = verticalLineScrollSize;
                verticalScrollBar.pageScrollSize = verticalPageScrollSize;
                verticalScrollBar.addEventListener(ScrollEvent.SCROLL, verticalScrollBar_scrollHandler);
                verticalScrollBar.enabled = enabled;
                if (verticalScrollBar is IInvalidating)
                    IInvalidating(verticalScrollBar).validateNow();

                invalidateDisplayList();
                invalidateViewMetricsAndPadding();

                changed = true;

                if (!horizontalScrollBar)
                    addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);

                // Listen for "mouseWheel" events on myself or any of my children
                addEventListener(MouseEvent.MOUSE_WHEEL, mouseWheelHandler);
            }
        }
        else
        {
            if (verticalScrollBar)
            {
                verticalScrollBar.removeEventListener(ScrollEvent.SCROLL, verticalScrollBar_scrollHandler);

                rawChildren.removeChild(DisplayObject(verticalScrollBar));
                verticalScrollBar = null;

                viewableHeight = scrollableHeight = 0;

                if (_verticalScrollPosition != 0)
                {
                    _verticalScrollPosition = 0;
                    scrollPositionChanged = true;
                }

                invalidateDisplayList();
                invalidateViewMetricsAndPadding();

                changed = true;
                
                fm = focusManager;
                if (!horizontalScrollBar && (!fm || fm.getFocus() != this))
                    removeEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);

                removeEventListener(MouseEvent.MOUSE_WHEEL, mouseWheelHandler);
            }
        }

        // Create or destroy the whiteBox.
        // If both scrollBars are active, there's an empty space
        // between the two scrollBars in the lower right corner.
        // The whiteBox fills that space, so that the container's
        // children aren't visible when they scroll underneath.
        if (horizontalScrollBar && verticalScrollBar)
        {
            if (!whiteBox)
            {
                whiteBox = new FlexShape();
                whiteBox.name = "whiteBox";

                var g:Graphics = whiteBox.graphics;
                g.beginFill(0xFFFFFF);
                g.drawRect(0, 0, verticalScrollBar.minWidth, horizontalScrollBar.minHeight);
                g.endFill()

                rawChildren.addChild(whiteBox);
            }
        }
        else
        {
            if (whiteBox)
            {
                rawChildren.removeChild(whiteBox);
                whiteBox = null;
            }
        }

        return changed;
    }
    
    /**
     *  @private
     *  Ensures that horizontalScrollPosition is in the range
     *  from 0 through maxHorizontalScrollPosition and that
     *  verticalScrollPosition is in the range from 0 through
     *  maxVerticalScrollPosition.
     *  Returns true if either horizontalScrollPosition or
     *  verticalScrollPosition was changed to ensure this.
     */
    private function clampScrollPositions():Boolean
    {
        var changed:Boolean = false;
        
        // Clamp horizontalScrollPosition to the range
        // 0 through maxHorizontalScrollPosition.
        // If horizontalScrollBar doesn't exist,
        // maxHorizontalScrollPosition will be 0.
        if (_horizontalScrollPosition < 0)
        {
            _horizontalScrollPosition = 0;
            changed = true;
        }
        else if (_horizontalScrollPosition > maxHorizontalScrollPosition)
        {
            _horizontalScrollPosition = maxHorizontalScrollPosition;
            changed = true;
        }

        // Set the position of the horizontal scrollbar's thumb.
        if (horizontalScrollBar &&
            horizontalScrollBar.scrollPosition != _horizontalScrollPosition)
        {
            horizontalScrollBar.scrollPosition = _horizontalScrollPosition;
        }

        // Clamp verticalScrollPosition to the range
        // 0 through maxVerticalScrollPosition.
        // If verticalScrollBar doesn't exist,
        // maxVerticalScrollPosition will be 0.
        if (_verticalScrollPosition < 0)
        {
            _verticalScrollPosition = 0;
            changed = true;
        }
        else if (_verticalScrollPosition > maxVerticalScrollPosition)
        {
            _verticalScrollPosition = maxVerticalScrollPosition;
            changed = true;
        }

        // Set the position of the vertical scrollbar's thumb.
        if (verticalScrollBar &&
            verticalScrollBar.scrollPosition != _verticalScrollPosition)
        {
            verticalScrollBar.scrollPosition = _verticalScrollPosition;
        }
        
        return changed;
    }

    /**
     *  @private
     */
    mx_internal function createContentPane():void
    {
        if (contentPane)
            return;

        creatingContentPane = true;
        
        // Reparent the children.  Get the number before we create contentPane
        // because that changes logic of how many children we have
        var n:int = numChildren;

        var newPane:Sprite = new FlexSprite();
        newPane.name = "contentPane";
        newPane.tabChildren = true;

        // Place content pane above border and background image but below
        // all other chrome.
        var childIndex:int;
        if (border)
        {
            childIndex = rawChildren.getChildIndex(DisplayObject(border)) + 1;
            if (border is IRectangularBorder && IRectangularBorder(border).hasBackgroundImage)
                childIndex++;
        }
        else
        {
            childIndex = 0;
        }
        rawChildren.addChildAt(newPane, childIndex);

        for (var i:int = 0; i < n; i++)
        {
            // use super because contentPane now exists and messes up getChildAt();
            var child:IUIComponent =
                IUIComponent(super.getChildAt(_firstChildIndex));
            newPane.addChild(DisplayObject(child));
            child.parentChanged(newPane);
            _numChildren--; // required
        }

        contentPane = newPane;

        creatingContentPane = false

        // UIComponent sets $visible to false. If we don't make it true here,
        // nothing shows up. Making this true should be harmless, as the
        // container itself should be false, and so should all its children.
        contentPane.visible = true;
    }
    
    /**
     *  Positions the container's content area relative to the viewable area 
     *  based on the horizontalScrollPosition and verticalScrollPosition properties. 
     *  Content that doesn't appear in the viewable area gets clipped. 
     *  This method should be overridden by subclasses that have scrollable 
     *  chrome in the content area.
     */
    protected function scrollChildren():void
    {
        if (!contentPane)
            return;

        var vm:EdgeMetrics = viewMetrics;

        var x:Number = 0;
        var y:Number = 0;
        var w:Number = unscaledWidth - vm.left - vm.right;
        var h:Number = unscaledHeight - vm.top - vm.bottom;

        if (_clipContent)
        {
            x += _horizontalScrollPosition;
        
            if (horizontalScrollBar)
                 w = viewableWidth;
 
            y += _verticalScrollPosition;
            
            if (verticalScrollBar)
                h = viewableHeight;
        }
        else
        {
            w = scrollableWidth;
            h = scrollableHeight;
        }

        // If we have enough space to display everything, don't set
        // scrollRect.
        var sr:Rectangle = getScrollableRect();
        if (x == 0 && y == 0                            // Not scrolled
                && w >= sr.right && h >= sr.bottom &&   // Vertical content visible
                sr.left >= 0 && sr.top >= 0 && _forceClippingCount <= 0)            // No negative coordinates
        {
            contentPane.scrollRect = null;
            contentPane.opaqueBackground = null;
            contentPane.cacheAsBitmap = false;
        }
        else
        {
            contentPane.scrollRect = new Rectangle(x, y, w, h);
        }

        if (focusPane)
            focusPane.scrollRect = contentPane.scrollRect;

        if (border && border is IRectangularBorder &&
            IRectangularBorder(border).hasBackgroundImage)
        {
            IRectangularBorder(border).layoutBackgroundImage();
        }
    }

    /**
     *  @private
     */
    private function dispatchScrollEvent(direction:String,
                                         oldPosition:Number,
                                         newPosition:Number,
                                         detail:String):void
    {
        var event:ScrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
        event.direction = direction;
        event.position = newPosition;
        event.delta = newPosition - oldPosition;
        event.detail = detail;
        dispatchEvent(event);
    }
    
    /**
     *  @private
     *  Used by a child component to force clipping during a Move effect
     */
    private var _forceClippingCount:int;

    mx_internal function set forceClipping(value:Boolean):void
    {
        if (_clipContent) // Can only force clipping if clipContent == true
        {
            if (value)
                _forceClippingCount++
            else
                _forceClippingCount--;
                
            createContentPane();
            scrollChildren();
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods: Binding
    //
    //--------------------------------------------------------------------------

    /**
     *  Executes the bindings into this Container's child UIComponent objects.
     *  Flex calls this method automatically once a Container has been created.
     *
     *  @param recurse If <code>false</code>, then only execute the bindings
     *  on the immediate children of this Container. 
     *  If <code>true</code>, then also execute the bindings on this
     *  container's grandchildren,
     *  great-grandchildren, and so on.
     */
    public function executeChildBindings(recurse:Boolean):void
    {
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IUIComponent = IUIComponent(getChildAt(i));
            if (child is IDeferredInstantiationUIComponent)
            {
                IDeferredInstantiationUIComponent(child).
                    executeBindings(recurse);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        // If a text field currently has focus, it is handling all arrow keys.
        // We shouldn't also scroll this Container.
        var focusObj:Object = getFocus();
        if (focusObj is TextField)
            return;

        var direction:String;
        var oldPos:Number;

        if (verticalScrollBar)
        {
            direction = ScrollEventDirection.VERTICAL;
            oldPos = verticalScrollPosition;

            switch (event.keyCode)
            {
                case Keyboard.DOWN:
                {
                    verticalScrollPosition += verticalLineScrollSize;
                    dispatchScrollEvent(direction, oldPos,
                                        verticalScrollPosition,
                                        ScrollEventDetail.LINE_DOWN);
                    event.stopPropagation();
                    break;
                }

                case Keyboard.UP:
                {
                    verticalScrollPosition -= verticalLineScrollSize;
                    dispatchScrollEvent(direction, oldPos,
                                        verticalScrollPosition,
                                        ScrollEventDetail.LINE_UP);
                    event.stopPropagation();
                    break;
                }

                case Keyboard.PAGE_UP:
                {
                    verticalScrollPosition -= verticalPageScrollSize;
                    dispatchScrollEvent(direction, oldPos,
                                        verticalScrollPosition,
                                        ScrollEventDetail.PAGE_UP);
                    event.stopPropagation();
                    break;
                }

                case Keyboard.PAGE_DOWN:
                {
                    verticalScrollPosition += verticalPageScrollSize;
                    dispatchScrollEvent(direction, oldPos,
                                        verticalScrollPosition,
                                        ScrollEventDetail.PAGE_DOWN);
                    event.stopPropagation();
                    break;
                }

                case Keyboard.HOME:
                {
                    verticalScrollPosition =
                        verticalScrollBar.minScrollPosition;
                    dispatchScrollEvent(direction, oldPos,
                                        verticalScrollPosition,
                                        ScrollEventDetail.AT_TOP);

                    event.stopPropagation();
                    break;
                }

                case Keyboard.END:
                {
                    verticalScrollPosition =
                        verticalScrollBar.maxScrollPosition;
                    dispatchScrollEvent(direction, oldPos,
                                        verticalScrollPosition,
                                        ScrollEventDetail.AT_BOTTOM);
                    event.stopPropagation();
                    break;
                }
            }
        }

        if (horizontalScrollBar)
        {
            direction = ScrollEventDirection.HORIZONTAL;
            oldPos = horizontalScrollPosition;

            switch (event.keyCode)
            {
                case Keyboard.LEFT:
                {
                    horizontalScrollPosition -= horizontalLineScrollSize;
                    dispatchScrollEvent(direction, oldPos,
                                        horizontalScrollPosition,
                                        ScrollEventDetail.LINE_LEFT);
                    event.stopPropagation();
                    break;
                }

                case Keyboard.RIGHT:
                {
                    horizontalScrollPosition += horizontalLineScrollSize;
                    dispatchScrollEvent(direction, oldPos,
                                        horizontalScrollPosition,
                                        ScrollEventDetail.LINE_RIGHT);
                    event.stopPropagation();
                    break;
                }
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
     *  This method copied verbatim from mx.core.ScrollControlBase.
     */
    private function mouseWheelHandler(event:MouseEvent):void
    {
        // If this Container has a vertical scrollbar, then handle the event
        // and prevent further bubbling
        if (verticalScrollBar)
        {
            event.stopPropagation();

            var scrollDirection:int = event.delta <= 0 ? 1 : -1;

            var lineScrollSize:int = verticalScrollBar ?
                                     verticalScrollBar.lineScrollSize :
                                     1;

            // Make sure we scroll by at least one line
            var scrollAmount:Number =
                Math.max(Math.abs(event.delta), lineScrollSize);

            // Multiply by 3 to make scrolling a little faster
            var oldPosition:Number = verticalScrollPosition;
            verticalScrollPosition += 3 * scrollAmount * scrollDirection;

            dispatchScrollEvent(ScrollEventDirection.VERTICAL,
                                oldPosition, verticalScrollPosition,
                                event.delta <= 0 ?
                                ScrollEventDetail.LINE_UP :
                                ScrollEventDetail.LINE_DOWN);
        }
    }

    /**
     *  @private
     *  This function is called when the LayoutManager finishes running.
     *  Clear the forceLayout flag that was set earlier.
     */
    private function layoutCompleteHandler(event:FlexEvent):void
    {
        UIComponentGlobals.layoutManager.removeEventListener(
            FlexEvent.UPDATE_COMPLETE, layoutCompleteHandler);
        forceLayout = false;

        var needToScrollChildren:Boolean = false;

        if (!isNaN(horizontalScrollPositionPending))
        {
            if (horizontalScrollPositionPending < 0)
                horizontalScrollPositionPending = 0;
            else if (horizontalScrollPositionPending > maxHorizontalScrollPosition)
                horizontalScrollPositionPending = maxHorizontalScrollPosition;

            // Set the position of the horizontal scrollbar's thumb.
            if (horizontalScrollBar &&
                horizontalScrollBar.scrollPosition !=
                horizontalScrollPositionPending)
            {
                _horizontalScrollPosition = horizontalScrollPositionPending;
                horizontalScrollBar.scrollPosition =
                    horizontalScrollPositionPending;
                needToScrollChildren = true;
            }

            horizontalScrollPositionPending = NaN;
        }

        if (!isNaN(verticalScrollPositionPending))
        {
            // Clamp verticalScrollPosition to the range 0 through maxVerticalScrollPosition.
            // If verticalScrollBar doesn't exist, maxVerticalScrollPosition will be 0.
            if (verticalScrollPositionPending < 0)
                verticalScrollPositionPending = 0;
            else if (verticalScrollPositionPending > maxVerticalScrollPosition)
                verticalScrollPositionPending = maxVerticalScrollPosition;

            // Set the position of the vertical scrollbar's thumb.
            if (verticalScrollBar && verticalScrollBar.scrollPosition != verticalScrollPositionPending)
            {
                _verticalScrollPosition = verticalScrollPositionPending;
                verticalScrollBar.scrollPosition = verticalScrollPositionPending;
                needToScrollChildren = true;
            }

            verticalScrollPositionPending = NaN;
        }

        if (needToScrollChildren)
            scrollChildren();
    }

    /**
     *  @private
     */
    private function creationCompleteHandler(event:FlexEvent):void
    {
        numChildrenCreated--;
        if (numChildrenCreated <= 0)
            dispatchEvent(new FlexEvent("childrenCreationComplete"));
    }

    /**
     *  @private
     *  This method is called if the user interactively moves
     *  the horizontal scrollbar thumb.
     */
    private function horizontalScrollBar_scrollHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            var oldPos:Number = horizontalScrollPosition;
            horizontalScrollPosition = horizontalScrollBar.scrollPosition;

            dispatchScrollEvent(ScrollEventDirection.HORIZONTAL,
                                oldPos,
                                horizontalScrollPosition,
                                ScrollEvent(event).detail);
        }
    }

    /**
     *  @private
     *  This method is called if the user interactively moves
     *  the vertical scrollbar thumb.
     */
    private function verticalScrollBar_scrollHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            var oldPos:Number = verticalScrollPosition;
            verticalScrollPosition = verticalScrollBar.scrollPosition;

            dispatchScrollEvent(ScrollEventDirection.VERTICAL,
                                oldPos,
                                verticalScrollPosition,
                                ScrollEvent(event).detail);
        }
    }

    /**
     *  @private
     */
    private function blocker_clickHandler(event:Event):void
    {
        // Swallow click events from blocker.
        event.stopPropagation();
    }
}

}
