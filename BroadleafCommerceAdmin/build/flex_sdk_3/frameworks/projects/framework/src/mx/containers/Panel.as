////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Rectangle;
import flash.text.TextLineMetrics;
import flash.utils.getQualifiedClassName;
import mx.automation.IAutomationObject;
import mx.containers.utilityClasses.BoxLayout;
import mx.containers.utilityClasses.CanvasLayout;
import mx.containers.utilityClasses.ConstraintColumn;
import mx.containers.utilityClasses.ConstraintRow;
import mx.containers.utilityClasses.IConstraintLayout;
import mx.containers.utilityClasses.Layout;
import mx.controls.Button;
import mx.core.Container;
import mx.core.ContainerLayout;
import mx.core.EdgeMetrics;
import mx.core.EventPriority;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IUIComponent;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UIComponentCachePolicy;
import mx.core.UITextField;
import mx.core.UITextFormat;
import mx.core.mx_internal;
import mx.effects.EffectManager;
import mx.events.CloseEvent;
import mx.managers.ISystemManager;
import mx.styles.ISimpleStyleClient;
import mx.styles.IStyleClient;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/AlignStyles.as";
include "../styles/metadata/GapStyles.as";
include "../styles/metadata/ModalTransparencyStyles.as";

/**
 *  Alpha of the title bar, control bar and sides of the Panel.
 *  The default value is 0.4.
 */
[Style(name="borderAlpha", type="Number", inherit="no")]

/**
 *  Thickness of the bottom border of the Panel control.
 *  If this style is not set and the Panel control contains a ControlBar
 *  control, the bottom border thickness equals the thickness of the top border
 *  of the panel; otherwise the bottom border thickness equals the thickness
 *  of the left border.
 *
 *  @default NaN
 */
[Style(name="borderThicknessBottom", type="Number", format="Length", inherit="no")]

/**
 *  Thickness of the left border of the Panel.
 *
 *  @default 10
 */
[Style(name="borderThicknessLeft", type="Number", format="Length", inherit="no")]

/**
 *  Thickness of the right border of the Panel.
 *
 *  @default 10
 */
[Style(name="borderThicknessRight", type="Number", format="Length", inherit="no")]

/**
 *  Thickness of the top border of the Panel.
 *
 *  @default 2
 */
[Style(name="borderThicknessTop", type="Number", format="Length", inherit="no")]

/**
 *  Name of the CSS style declaration that specifies styles to apply to 
 *  any control bar child subcontrol.
 * 
 *  @default null
 */
[Style(name="controlBarStyleName", type="String", inherit="no")]

/**
 *  Radius of corners of the window frame.
 *
 *  @default 4
 */
[Style(name="cornerRadius", type="Number", format="Length", inherit="no")]

/**
 *  Boolean property that controls the visibility
 *  of the Panel container's drop shadow.
 *
 *  @default true
 */
[Style(name="dropShadowEnabled", type="Boolean", inherit="no")]

/**
 *  Array of two colors used to draw the footer
 *  (area for the ControlBar container) background. 
 *  The first color is the top color. 
 *  The second color is the bottom color.
 *  The default values are <code>null</code>, which
 *  makes the control bar background the same as
 *  the panel background.
 *
 *  @default null
 */
[Style(name="footerColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  Array of two colors used to draw the header.
 *  The first color is the top color.
 *  The second color is the bottom color.
 *  The default values are <code>null</code>, which
 *  makes the header background the same as the
 *  panel background.
 *
 *  @default null
 */
[Style(name="headerColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  Height of the header.
 *  The default value is based on the style of the title text.
 */
[Style(name="headerHeight", type="Number", format="Length", inherit="no")]

/**
 *  Alphas used for the highlight fill of the header.
 *
 *  @default [0.3,0]
 */
[Style(name="highlightAlphas", type="Array", arrayType="Number", inherit="no")]

/**
 *  Number of pixels between the container's lower border
 *  and its content area.
 *
 *  @default 0
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the container's top border
 *  and its content area.
 *
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

/**
 *  Flag to enable rounding for the bottom two corners of the container.
 *  Does not affect the upper two corners, which are normally round. 
 *  To configure the upper corners to be square, 
 *  set <code>cornerRadius</code> to 0.
 *
 *  @default false
 */
[Style(name="roundedBottomCorners", type="Boolean", inherit="no")]

/**
 *  Direction of drop shadow.
 *  Possible values are <code>"left"</code>, <code>"center"</code>,
 *  and <code>"right"</code>.
 *
 *  @default "center"
 */
[Style(name="shadowDirection", type="String", enumeration="left,center,right", inherit="no")]

/**
 *  Distance of drop shadow.
 *  Negative values move the shadow above the panel.
 *
 *  @default 2
 */
[Style(name="shadowDistance", type="Number", format="Length", inherit="no")]

/**
 *  Style declaration name for the status in the title bar.
 *
 *  @default "windowStatus"
 */
 
[Style(name="statusStyleName", type="String", inherit="no")]

/**
 *  The title background skin.
 *
 *  @default mx.skins.halo.TitleBackground
 */
[Style(name="titleBackgroundSkin", type="Class", inherit="no")]

/**
 *  Style declaration name for the text in the title bar.
 *  The default value is <code>"windowStyles"</code>,
 *  which causes the title to have boldface text.
 *
 *  @default "windowStyles"
 */
[Style(name="titleStyleName", type="String", inherit="no")]

//--------------------------------------
//  Effects
//--------------------------------------

/**
 *  Specifies the effect to play after a Resize effect finishes playing.
 *  To disable the default Dissolve effect, so that the children are hidden
 *  instantaneously, set the value of the
 *  <code>resizeEndEffect</code> property to <code>"none"</code>.  
 *
 *  @default "Dissolve"
 */
[Effect(name="resizeEndEffect", event="resizeEnd")]

/**
 *  Specifies the effect to play before a Resize effect begins playing.
 *  To disable the default Dissolve effect, so that the children are hidden
 *  instantaneously, set the value of the
 *  <code>resizeStartEffect</code> property to <code>"none"</code>.  
 *
 *  @default "Dissolve"
 */
[Effect(name="resizeStartEffect", event="resizeStart")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.PanelAccImpl")]

[IconFile("Panel.png")]

/**
 *  A Panel container consists of a title bar, a caption, a border,
 *  and a  content area for its children.
 *  Typically, you use Panel containers to wrap top-level application modules.
 *  For example, you could include a shopping cart in a Panel container.
 *  
 *  <p>The Panel container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Height is large enough to hold all of its children at the default height of the children, 
 *               plus any vertical gaps between the children, the top and bottom padding, the top and bottom borders, 
 *               and the title bar.<br/>
 *               Width is the larger of the default width of the widest child plus the left and right padding of the 
 *               container, or the width of the title text, plus the border.</td>
 *        </tr>
 *        <tr>
 *           <td>Padding</td>
 *           <td>4 pixels for the top, bottom, left, and right values.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:Panel&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Panel
 *   <strong>Properties</strong>
 *   layout="vertical|horizontal|absolute"
 *   status=""
 *   title=""
 *   titleIcon="null"
 *   <strong>Styles</strong>
 *   borderAlpha="0.4"
 *   borderThicknessBottom="NaN"
 *   borderThicknessLeft="10"
 *   borderThicknessRight="10"
 *   borderThicknessTop="2"
 *   controlBarStyleName="null"
 *   cornerRadius="4"
 *   dropShadowEnabled="true|false"
 *   footerColors="null"
 *   headerColors="null"
 *   headerHeight="<i>Based on style of title</i>"
 *   highlightAlphas="[0.3,0]"
 *   horizontalAlign="left|center|right"
 *   horizontalGap="8"
 *   modalTransparency="0.5"
 *   modalTransparencyBlur="3"
 *   modalTransparencyColor="#DDDDDD"
 *   modalTransparencyDuration="100"
 *   paddingBottom="0"
 *   paddingTop="0"
 *   roundedBottomCorners="false|true"
 *   shadowDirection="center|left|right"
 *   shadowDistance="2"
 *   statusStyleName="windowStatus"
 *   titleBackgroundSkin="TitleBackground"
 *   titleStyleName="windowStyles"
 *   verticalAlign="top|middle|bottom"
 *   verticalGap="6"
 *   <strong>Effects</strong>
 *   resizeEndEffect="Dissolve"
 *   resizeStartEffect="Dissolve"
 *   &gt;
 *      ...
 *      <i>child tags</i>
 *      ...
 *  &lt;/mx:Panel&gt;
 *  </pre>
 *  
 *  @includeExample examples/SimplePanelExample.mxml
 *
 *  @see mx.containers.ControlBar
 *  @see mx.containers.VBox
 */
public class Panel extends Container
    implements IConstraintLayout, IFontContextComponent
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
    private static const HEADER_PADDING:Number = 14;

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by PanelAccImpl.
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
    public function Panel()
    {
        super();

        // If the user hasn't explicitly set a resizeStartEffect
        // or resizeEndEffect for this Panel, then we want to use
        // the Dissolve effect (which is specified in the Panel's
        // style sheet). Unfortunately, there's no automated mechanism
        // to hook up the EffectManager, so I need to do that here.
        // Also see the comment in defaults.css.
        addEventListener("resizeStart", EffectManager.eventHandler,
                         false, EventPriority.EFFECT);
        addEventListener("resizeEnd", EffectManager.eventHandler,
                         false, EventPriority.EFFECT);

        layoutObject = new BoxLayout();
        layoutObject.target = this;
        
        showInAutomationHierarchy = true;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var layoutObject:Layout;

    /**
     *  @private
     *  Is there a close button? Panel itself never has one,
     *  but its subclass TitleWindow can set this flag to true.
     */
    mx_internal var _showCloseButton:Boolean = false;

    /**
     *  @private
     *  A reference to this Panel container's title bar skin.
     *  This is a child of the titleBar.
     */
    mx_internal var titleBarBackground:IFlexDisplayObject;

    /**
     *  @private
     *  A reference to this Panel container's title icon.
     */
    mx_internal var titleIconObject:Object = null;

    /**
     *  @private
     *  A reference to this Panel container's close button, if any.
     *  This is a sibling of the titleBar, not its child.
     */
    mx_internal var closeButton:Button; 

    /**
     *  @private
     *  true until the component has finished initializing
     */
    private var initializing:Boolean = true;

    /**
     *  @private
     */
    private var panelViewMetrics:EdgeMetrics;

    /**
     *  @private
     *  Horizontal location where the user pressed the mouse button
     *  on the titlebar to start dragging, relative to the original
     *  horizontal location of the Panel.
     */
    private var regX:Number;
    
    /**
     *  @private
     *  Vertical location where the user pressed the mouse button
     *  on the titlebar to start dragging, relative to the original
     *  vertical location of the Panel.
     */
    private var regY:Number;

    /**
     *  @private
     */
    private var checkedForAutoSetRoundedCorners:Boolean;
    
    /** 
     *  @private
     */
    private var autoSetRoundedCorners:Boolean;
    
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
     *  The baselinePosition of a Panel is calculated for its title.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return super.baselinePosition;
        
        if (!validateBaselinePosition())
            return NaN;

        return titleBar.y + titleTextField.y + titleTextField.baselinePosition;
    }

    //----------------------------------
    //  cacheAsBitmap
    //----------------------------------

    /**
     *  @private
     */
    override public function set cacheAsBitmap(value:Boolean):void
    {
        super.cacheAsBitmap = value;

        // If we got cached, create the content pane so the content area of
        // this panel gets cached with an opaque background.
        if (cacheAsBitmap && !contentPane
                && cachePolicy != UIComponentCachePolicy.OFF
                && getStyle("backgroundColor"))
        {
            createContentPane();
            invalidateDisplayList();
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

        if (titleTextField)
            titleTextField.enabled = value;

        if (statusTextField)
            statusTextField.enabled = value;
        
        if (controlBar)
            controlBar.enabled = value;
        
        if (closeButton)
            closeButton.enabled = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  _closeButtonStyleFilters
    //----------------------------------

   
    private static var _closeButtonStyleFilters:Object = 
    {
        "closeButtonUpSkin" : "closeButtonUpSkin", 
        "closeButtonOverSkin" : "closeButtonOverSkin",
        "closeButtonDownSkin" : "closeButtonDownSkin",
        "closeButtonDisabledSkin" : "closeButtonDisabledSkin",
        "closeButtonSkin" : "closeButtonSkin",
        "repeatDelay" : "repeatDelay",
        "repeatInterval" : "repeatInterval"
    };
    
    /**
     *  The set of styles to pass from the Panel to the close button.
     *  @see mx.styles.StyleProxy
     *  @review
     */
    protected function get closeButtonStyleFilters():Object
    {
        return _closeButtonStyleFilters;
    }

    //----------------------------------
    //  constraintColumns
    //----------------------------------

    [ArrayElementType("mx.containers.utilityClasses.ConstraintColumn")]
    [Inspectable(arrayType="mx.containers.utilityClasses.ConstraintColumn")]
    
    /**
     *  @private
     *  Storage for the constraintColumns property.
     */
    private var _constraintColumns:Array = [];
    
    /**
     *  @copy mx.containers.utilityClasses.IConstraintLayout#constraintColumns
     */
    public function get constraintColumns():Array
    {
        return _constraintColumns;
    }
    
    /**
     *  @private
     */
    public function set constraintColumns(value:Array):void
    {
        if (value != _constraintColumns)
        {
            var n:int = value.length;
            for (var i:int = 0; i < n; i++)
            {
                ConstraintColumn(value[i]).container = this;
            }
            _constraintColumns = value;

            invalidateSize();
            invalidateDisplayList();
        }
    }

    //----------------------------------
    //  constraintRows
    //----------------------------------

    [ArrayElementType("mx.containers.utilityClasses.ConstraintRow")]
    [Inspectable(arrayType="mx.containers.utilityClasses.ConstraintRow")]
    
    /**
     *  @private
     *  Storage for the constraintRows property.
     */
    private var _constraintRows:Array = [];
    
    /**
     *  @copy mx.containers.utilityClasses.IConstraintLayout#constraintRows
     */
    public function get constraintRows():Array
    {
        return _constraintRows;
    }
    
    /**
     *  @private
     */
    public function set constraintRows(value:Array):void
    {
        if (value != _constraintRows)
        {
            var n:int = value.length;
            for (var i:int = 0; i < n; i++)
            {
                ConstraintRow(value[i]).container = this;
            }
            _constraintRows = value;

            invalidateSize();
            invalidateDisplayList();
        }
    }        
       
    //----------------------------------
    //  controlBar
    //----------------------------------

    /**
     *  A reference to this Panel container's control bar, if any.
     */
    protected var controlBar:IUIComponent;
    
    /**
     *  Proxy to the controlBar property which is protected and can't be accessed externally
     */
    mx_internal function get _controlBar():IUIComponent
    {
        return controlBar;
    }
        
    //----------------------------------
    //  fontContext
    //----------------------------------
    
    /**
     *  @inheritDoc 
     */
    public function get fontContext():IFlexModuleFactory
    {
        return moduleFactory;
    }

    /**
     *  @private
     */
    public function set fontContext(moduleFactory:IFlexModuleFactory):void
    {
        this.moduleFactory = moduleFactory;
    }
        
    //----------------------------------
    //  layout
    //----------------------------------

    /**
     *  @private
     *  Storage for the layout property.
     */
    private var _layout:String = ContainerLayout.VERTICAL;

    [Bindable("layoutChanged")]
    [Inspectable(category="General", enumeration="vertical,horizontal,absolute", defaultValue="vertical")]

    /**
     *  Specifies the layout mechanism used for this container. 
     *  Panel containers can use <code>"vertical"</code>, <code>"horizontal"</code>, 
     *  or <code>"absolute"</code> positioning. 
     *  Vertical positioning lays out the child components vertically from
     *  the top of the container to the bottom in the specified order.
     *  Horizontal positioning lays out the child components horizontally
     *  from the left of the container to the right in the specified order.
     *  Absolute positioning does no automatic layout and requires you to
     *  explicitly define the location of each child component. 
     *
     *  @default "vertical"
     */
    public function get layout():String
    {
        return _layout;
    }

    /**
     *  @private
     */
    public function set layout(value:String):void
    {
        if (_layout != value)
        {
            _layout = value;

            if (layoutObject)
                // Set target to null for cleanup.
                layoutObject.target = null;

            if (_layout == ContainerLayout.ABSOLUTE)
                layoutObject = new CanvasLayout();
            else
            {
                layoutObject = new BoxLayout();

                if (_layout == ContainerLayout.VERTICAL)
                    BoxLayout(layoutObject).direction
                        = BoxDirection.VERTICAL;
                else
                    BoxLayout(layoutObject).direction
                        = BoxDirection.HORIZONTAL;
            }

            if (layoutObject)
                layoutObject.target = this;

            invalidateSize();
            invalidateDisplayList();

            dispatchEvent(new Event("layoutChanged"));
        }
    }

    //----------------------------------
    //  status
    //----------------------------------

    /**
     *  @private
     *  Storage for the status property.
     */
    private var _status:String = "";
    
    /**
     *  @private
     */
    private var _statusChanged:Boolean = false;
    
    [Bindable("statusChanged")]
    [Inspectable(category="General", defaultValue="")]

    /**
     *  Text in the status area of the title bar.
     *
     *  @default ""
     */
    public function get status():String
    {
        return _status;
    }

    /**
     *  @private
     */
    public function set status(value:String):void
    {
        _status = value;
        _statusChanged = true;
        
        invalidateProperties();
        
        dispatchEvent(new Event("statusChanged"));
    }
    
    //----------------------------------
    //  statusTextField
    //----------------------------------

    /**
     *  The UITextField sub-control that displays the status.
     *  The status field is a child of the <code>titleBar</code> sub-control.
     * 
     *  @see #titleBar
     */
    protected var statusTextField:IUITextField;

    //----------------------------------
    //  title
    //----------------------------------

    /**
     *  @private
     *  Storage for the title property.
     */ 
    private var _title:String = "";
    
    /**
     *  @private
     */
    private var _titleChanged:Boolean = false;

    [Bindable("titleChanged")]
    [Inspectable(category="General", defaultValue="")]

    /**
     *  Title or caption displayed in the title bar.
     *
     *  @default ""
     *
     *  @tiptext Gets or sets the title/caption displayed in the title bar
     *  @helpid 3991
     */
    public function get title():String
    {
        return _title;
    }

    /**
     *  @private
     */
    public function set title(value:String):void
    {
        _title = value;
        _titleChanged = true;
        
        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();
        
        dispatchEvent(new Event("titleChanged"));
    }
    
    //----------------------------------
    //  titleBar
    //----------------------------------

    /**
     *  The TitleBar sub-control that displays the Panel container's title bar.
     */
    protected var titleBar:UIComponent;

    //----------------------------------
    //  titleIcon
    //----------------------------------

    /**
     *  @private
     *  Storage for the titleIcon property.
     */ 
    private var _titleIcon:Class;
    
    /**
     *  @private
     */
    private var _titleIconChanged:Boolean = false;

    [Bindable("titleIconChanged")]
    [Inspectable(category="General", defaultValue="", format="EmbeddedFile")]

    /**
     *  The icon displayed in the title bar.
     *
     *  @default null
     */
    public function get titleIcon():Class
    {
        return _titleIcon;
    }

    /**
     *  @private
     */
    public function set titleIcon(value:Class):void
    {
        _titleIcon = value;
        _titleIconChanged = true;
        
        invalidateProperties();
        invalidateSize();
        
        dispatchEvent(new Event("titleIconChanged"));
    }

    //----------------------------------
    //  titleTextField
    //----------------------------------

    /**
     *  The UITextField sub-control that displays the title.
     *  The title field is a child of the <code>titleBar</code> sub-control.
     * 
     *  @see #titleBar
     */
    protected var titleTextField:IUITextField;

    //----------------------------------
    //  usePadding
    //----------------------------------

    /**
     *  @private
     */
    override mx_internal function get usePadding():Boolean
    {
        // We use margins for all layouts except absolute.
        return layout != ContainerLayout.ABSOLUTE;
    }

    //----------------------------------
    //  viewMetrics
    //----------------------------------

    /**
     *  @private  
     *
     *  Returns the thickness of the edges of the object, including  
     *  the border, title bar, and scroll bars, if visible.
     *  @return Object with left, right, top, and bottom properties
     *  containing the edge thickness, in pixels.
     */
    override public function get viewMetrics():EdgeMetrics
    {
        var vm:EdgeMetrics = super.viewMetrics;
        
        // The getViewMetrics function needs to return its own object.
        // Rather than allocating a new one each time, we'll allocate
        // one once and then hold a pointer to it.
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            if (!panelViewMetrics)
                panelViewMetrics = new EdgeMetrics(0, 0, 0, 0);
            vm = panelViewMetrics;
    
            var o:EdgeMetrics = super.viewMetrics;
            var bt:Number = getStyle("borderThickness");
            var btl:Number = getStyle("borderThicknessLeft");
            var btt:Number = getStyle("borderThicknessTop");
            var btr:Number = getStyle("borderThicknessRight");
            var btb:Number = getStyle("borderThicknessBottom");
    
            // Add extra space to edges (was margins).
            vm.left = o.left + (isNaN(btl) ? bt : btl);
            vm.top = o.top + (isNaN(btt) ? bt : btt);
            vm.right = o.right + (isNaN(btr) ? bt : btr);
            // Bottom is a special case. If borderThicknessBottom is NaN,
            // use btl if we don't have a control bar or btt if we do.
            vm.bottom = o.bottom + (isNaN(btb) ? 
                (controlBar && !isNaN(btt) ? btt : isNaN(btl) ? bt : btl) : 
                btb);
                    
            // Since the header covers the solid portion of the border,  
            // we need to use the larger of borderThickness or headerHeight
            var hHeight:Number = getHeaderHeight();
            if (!isNaN(hHeight))
                vm.top += hHeight;
    
            if (controlBar && controlBar.includeInLayout)
                vm.bottom += controlBar.getExplicitOrMeasuredHeight();
        }
        return vm;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: DisplayObjectContainer
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function getChildIndex(child:DisplayObject):int
    {
        // This override of getChildIndex() fixes a bug #116637.
        // The FocusManager incorrectly calls this function for the controlBar
        // with isContentAPI as true.  But it's really our fault, because we
        // first add the controlBar as a child (so it gets into the
        // FocusManager's list of focusable objects) and then abduct it.  This,
        // in combination with the contentPane, leads to a runtime error.
        // Simply returning numChildren (meaning "after last child") is
        // harmless and resolves the issue.
        if (controlBar && child == controlBar)
            return numChildren;
        else
            return super.getChildIndex(child);
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
        if (Panel.createAccessibilityImplementation != null)
            Panel.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     *  Create child objects.
     */
    override protected function createChildren():void
    {
        super.createChildren();

        // Create the title bar over the top of the floating border.
        if (!titleBar)
        {
            titleBar = new UIComponent();
            titleBar.visible = false;
        
            titleBar.addEventListener(MouseEvent.MOUSE_DOWN,
                                      titleBar_mouseDownHandler);

            rawChildren.addChild(titleBar);
        }

        // Create the titleBarBackground as a child of the titleBar.
        if (!titleBarBackground)
        {
            var titleBarBackgroundClass:Class =
                getStyle("titleBackgroundSkin");
            if (titleBarBackgroundClass)
            {
                titleBarBackground = new titleBarBackgroundClass();

                var backgroundUIComponent:IStyleClient =
                    titleBarBackground as IStyleClient;     
                if (backgroundUIComponent)
                {
                    backgroundUIComponent.setStyle("backgroundImage",
                                                   undefined);
                }

                var backgroundStyleable:ISimpleStyleClient =
                    titleBarBackground as ISimpleStyleClient;
                if (backgroundStyleable)
                    backgroundStyleable.styleName = this;

                titleBar.addChild(DisplayObject(titleBarBackground));
            }
        }

        createTitleTextField(-1);
        createStatusTextField(-1);
        
        // Create the closeButton as a child of the titleBar.
        if (!closeButton)
        {
            closeButton = new Button();
            closeButton.styleName = new StyleProxy(this, closeButtonStyleFilters);

            closeButton.upSkinName = "closeButtonUpSkin";
            closeButton.overSkinName = "closeButtonOverSkin";
            closeButton.downSkinName = "closeButtonDownSkin";
            closeButton.disabledSkinName = "closeButtonDisabledSkin";
            closeButton.skinName = "closeButtonSkin";
            closeButton.explicitWidth = closeButton.explicitHeight = 16;
            
            closeButton.focusEnabled = false;
            closeButton.visible = false;
            closeButton.enabled = enabled;
            
            closeButton.addEventListener(MouseEvent.CLICK,
                                          closeButton_clickHandler);

            // Add the close button on top of the title/status.
            titleBar.addChild(closeButton);
            closeButton.owner = this;
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (hasFontContextChanged())
        {
            // Re-create the text fields in case the new font
            // is in a  different SWF then the current one.
            var childIndex:int;
            if (titleTextField)
            {
                childIndex = titleBar.getChildIndex(DisplayObject(titleTextField));
                removeTitleTextField();
                createTitleTextField(childIndex);
                
                _titleChanged = true;
            }
            
            if (statusTextField)
            {
                childIndex = titleBar.getChildIndex(DisplayObject(statusTextField));
                removeStatusTextField();
                createStatusTextField(childIndex);
                
                _statusChanged = true;
            }
        }
      
        if (_titleChanged)
        {
            _titleChanged = false;
            titleTextField.text = _title;
            
            // Don't call layoutChrome() if we  haven't initialized,
            // because it causes commit/measure/layout ordering problems
            // for children of the control bar.
            if (initialized)
                layoutChrome(unscaledWidth, unscaledHeight);
        }

        if (_titleIconChanged)
        {
            _titleIconChanged = false;

            if (titleIconObject)
            {
                titleBar.removeChild(DisplayObject(titleIconObject));
                titleIconObject = null;
            }

            if (_titleIcon)
            {
                titleIconObject = new _titleIcon();
                titleBar.addChild(DisplayObject(titleIconObject));
            }

            // Don't call layoutChrome() if we  haven't initialized,
            // because it causes commit/measure/layout ordering problems
            // for children of the control bar.
            if (initialized)
                layoutChrome(unscaledWidth, unscaledHeight);
        }
        
        if (_statusChanged)
        {
            _statusChanged = false;
            statusTextField.text = _status;
            
            // Don't call layoutChrome() if we  haven't initialized,
            // because it causes commit/measure/layout ordering problems
            // for children of the control bar.
            if (initialized)
                layoutChrome(unscaledWidth, unscaledHeight);
        }
    }
    
    /**
     *  Calculates the default mininum and maximum sizes
     *  of the Panel container.
     *  For more information
     *  about the <code>measure()</code> method, see the <code>
     *  UIComponent.measure()</code> method.
     *
     *  <p>The <code>measure()</code> method first calls
     *  <code>VBox.measure()</code> method, and then ensures that the
     *  <code>measuredWidth</code> and
     *  <code>measuredMinWidth</code> properties are wide enough
     *  to display the title and the ControlBar.</p>
     * 
     *  @see mx.core.UIComponent#measure()
     */ 
    override protected function measure():void
    {
        super.measure();

        layoutObject.measure();

        var textSize:Rectangle = measureHeaderText();
        var textWidth:Number = textSize.width;
        var textHeight:Number = textSize.height;
        
        var bm:EdgeMetrics =
            FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 ?
            borderMetrics :
            EdgeMetrics.EMPTY;
        textWidth += bm.left + bm.right;    
        
        var offset:Number = 5;
        textWidth += offset * 2;

        if (titleIconObject)
            textWidth += titleIconObject.width;
        
        if (closeButton)
            textWidth += closeButton.getExplicitOrMeasuredWidth() + 6;

        measuredMinWidth = Math.max(textWidth, measuredMinWidth);
        measuredWidth = Math.max(textWidth, measuredWidth);
        
        if (controlBar && controlBar.includeInLayout)
        {
            var controlWidth:Number = controlBar.getExplicitOrMeasuredWidth() +
                                      bm.left + bm.right;
            
            measuredWidth =
                Math.max(measuredWidth, controlWidth);
        }
    }       
    
    /**
     *  @private
     *  Draw by making everything visible, then laying out.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        layoutObject.updateDisplayList(unscaledWidth, unscaledHeight);

        if (border)
            border.visible = true;
        titleBar.visible = true;            
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var allStyles:Boolean = !styleProp || styleProp == "styleName";
        
        super.styleChanged(styleProp);
        
        if (allStyles || styleProp == "titleStyleName")
        {
            if (titleTextField)
            {
                var titleStyleName:String = getStyle("titleStyleName");
                titleTextField.styleName = titleStyleName;
            }
        }
        
        if (allStyles || styleProp == "statusStyleName")
        {
            if (statusTextField)
            {
                var statusStyleName:String = getStyle("statusStyleName");
                statusTextField.styleName = statusStyleName;
            }
        }
        
        if (allStyles || styleProp == "controlBarStyleName")
        {
            if (controlBar && controlBar is ISimpleStyleClient)
            {
                var controlBarStyleName:String = getStyle("controlBarStyleName");
                ISimpleStyleClient(controlBar).styleName = controlBarStyleName;
            }
        }
        
        if (allStyles || styleProp == "titleBackgroundSkin")
        {
            if (titleBar)
            {
                var titleBackgroundSkinClass:Class =
                    getStyle("titleBackgroundSkin");
                
                if (titleBackgroundSkinClass)
                {
                    // Remove existing background
                    if (titleBarBackground)
                    {
                        titleBar.removeChild(DisplayObject(titleBarBackground));
                        titleBarBackground = null;
                    }
                    
                    titleBarBackground = new titleBackgroundSkinClass();

                    var backgroundUIComponent:IStyleClient =
                        titleBarBackground as IStyleClient;         
                    if (backgroundUIComponent)
                    {
                        backgroundUIComponent.setStyle("backgroundImage",
                                                       undefined);
                    }

                var backgroundStyleable:ISimpleStyleClient =
                    titleBarBackground as ISimpleStyleClient;
                if (backgroundStyleable)
                    backgroundStyleable.styleName = this;

                    titleBar.addChildAt(DisplayObject(titleBarBackground), 0);
                }
            }
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
        
        // Special case for the default borderSkin to inset the chrome content
        // by the borderThickness when borderStyle is "solid", "inset" or "outset". 
        // We use getQualifiedClassName to avoid bringing in a dependency on 
        // mx.skins.halo.PanelSkin. 
        var em:EdgeMetrics = EdgeMetrics.EMPTY;
        var bt:Number = getStyle("borderThickness"); 
        if (getQualifiedClassName(border) == "mx.skins.halo::PanelSkin" &&
            getStyle("borderStyle") != "default" && bt) 
        {
            em = new EdgeMetrics(bt, bt, bt, bt);
        }
        
        // Remove the borderThickness from the border metrics,
        // since the header and control bar overlap any solid border.
        var bm:EdgeMetrics =
            FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 ?
            borderMetrics :
            em;      
        
        var x:Number = bm.left;
        var y:Number = bm.top;
        
        var headerHeight:Number = getHeaderHeight();
        if (headerHeight > 0 && height >= headerHeight)
        {
            var titleBarWidth:Number = unscaledWidth - bm.left - bm.right;
            
            showTitleBar(true);
            titleBar.mouseChildren = true;
            titleBar.mouseEnabled = true;
            
            // Draw an invisible rect in the tileBar. This ensures we
            // will get clicks, even if the background is transparent 
            // and there is no title.
            var g:Graphics = titleBar.graphics;
            g.clear();
            g.beginFill(0xFFFFFF, 0);
            g.drawRect(0, 0, titleBarWidth, headerHeight);
            g.endFill();
            
            // Position the titleBar.
            titleBar.move(x, y);
            titleBar.setActualSize(titleBarWidth, headerHeight);

            // Position the titleBarBackground within the titleBar.
            titleBarBackground.move(0, 0);
            IFlexDisplayObject(titleBarBackground).setActualSize(
                titleBarWidth, headerHeight);

            // Set the close button next to the upper-right corner,
            // offset by the border thickness.
            closeButton.visible = _showCloseButton;
            if (_showCloseButton)
            {
                closeButton.setActualSize(
                    closeButton.getExplicitOrMeasuredWidth(),
                    closeButton.getExplicitOrMeasuredHeight());

                closeButton.move(
                    unscaledWidth - x - bm.right - 10 -
                    closeButton.getExplicitOrMeasuredWidth(),
                    (headerHeight -
                    closeButton.getExplicitOrMeasuredHeight()) / 2);
            }

            var leftOffset:Number = 10;
            var rightOffset:Number = 10;
            var h:Number;
            var offset:Number;

            // Set the position of the title icon.
            if (titleIconObject)
            {
                h = titleIconObject.height;
                offset = (headerHeight - h) / 2;
                titleIconObject.move(leftOffset, offset);
                leftOffset += titleIconObject.width + 4;
            }

            // Set the position of the title text. 
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                h = titleTextField.nonZeroTextHeight;
            else 
                h = titleTextField.getUITextFormat().measureText(titleTextField.text).height;
            offset = (headerHeight - h) / 2;

            var borderWidth:Number = bm.left + bm.right;            
            titleTextField.move(leftOffset, offset - 1);
            titleTextField.setActualSize(Math.max(0,
                                   unscaledWidth - leftOffset -
                                   rightOffset - borderWidth),
                                   h + UITextField.TEXT_HEIGHT_PADDING);

            // Set the position of the status text.
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                h = statusTextField.textHeight;
            else
                h = statusTextField.text != "" ? statusTextField.getUITextFormat().measureText(statusTextField.text).height : 0;
            offset = (headerHeight - h) / 2;
            var statusX:Number = unscaledWidth - rightOffset - 4 -
                                 borderWidth - statusTextField.textWidth;
            if (_showCloseButton)
                statusX -= (closeButton.getExplicitOrMeasuredWidth() + 4);
            statusTextField.move(statusX, offset - 1);
            statusTextField.setActualSize(
                statusTextField.textWidth + 8,
                statusTextField.textHeight + UITextField.TEXT_HEIGHT_PADDING);

            // Make sure the status text isn't too long.
            // We do simple clipping here.
            var minX:Number = titleTextField.x + titleTextField.textWidth + 8;
            if (statusTextField.x < minX)
            {
                // Show as much as we can.
                statusTextField.width = Math.max(statusTextField.width -
                                        (minX - statusTextField.x), 0);
                statusTextField.x = minX;
            }
        }
        else
        {
            if (titleBar)
            {
                showTitleBar(false);                
                titleBar.mouseChildren = false;
                titleBar.mouseEnabled = false;
            }
        }
        
        if (controlBar)
        {
            var cx:Number = controlBar.x;
            var cy:Number = controlBar.y;
            var cw:Number = controlBar.width;
            var ch:Number = controlBar.height;

            controlBar.setActualSize(
                unscaledWidth - (bm.left + bm.right),
                controlBar.getExplicitOrMeasuredHeight());

            controlBar.move(
                bm.left,
                unscaledHeight - bm.bottom -
                controlBar.getExplicitOrMeasuredHeight());

            if (controlBar.includeInLayout)
                // Hide the control bar if it is spilling out.
                controlBar.visible = controlBar.y >= bm.top;

            // If the control bar's position or size changed, redraw.  This
            // fixes a refresh bug (when the control bar vacates some space,
            // the new space appears blank).
            if (cx != controlBar.x ||
                cy != controlBar.y ||
                cw != controlBar.width ||
                ch != controlBar.height)
            {
                invalidateDisplayList();
            }
        }
    }

    /**
     *  @private
     */
    override public function createComponentsFromDescriptors(
                                recurse:Boolean = true):void
    {
        super.createComponentsFromDescriptors();
        
        if (numChildren == 0)
        {
            setControlBar(null);
            return;
        }
        
        // If the last content child is a ControlBar, change it
        // from being a content child to a chrome child;
        // i.e., move it to the rawChildren collection.
        var lastChild:IUIComponent = IUIComponent(getChildAt(numChildren - 1));
        if (lastChild is ControlBar)
        {
            var oldChildDocument:Object = lastChild.document;
            
            if (contentPane)
            {
                contentPane.removeChild(DisplayObject(lastChild));
            }
            else
            {
                removeChild(DisplayObject(lastChild));
            }       
            // Restore the original document. Otherwise, when we re-add the child when the Panel is
            // a custom component, the child will use the custom component as the document instead of
            // using the document in which the child was declared.
            lastChild.document = oldChildDocument;
            rawChildren.addChild(DisplayObject(lastChild));
            setControlBar(lastChild);
        }
        else
        {
            setControlBar(null);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Creates the title text field child
     *  and adds it as a child of this component.
     * 
     *  @param childIndex The index of where to add the child.
     *  If -1, the text field is appended to the end of the list.
     */
    mx_internal function createTitleTextField(childIndex:int):void
    {
        // Create the titleTextField as a child of the titleBar.
        if (!titleTextField)
        {
            titleTextField = IUITextField(createInFontContext(UITextField));
            titleTextField.selectable = false;
    
            if (childIndex == -1)
                titleBar.addChild(DisplayObject(titleTextField));
            else 
                titleBar.addChildAt(DisplayObject(titleTextField), childIndex);

            var titleStyleName:String = getStyle("titleStyleName"); 
            titleTextField.styleName = titleStyleName;
            titleTextField.text = title;
            titleTextField.enabled = enabled;
        }
    }

    /**
     *  @private
     *  Removes the title text field from this component.
     */
    mx_internal function removeTitleTextField():void
    {
        if (titleBar && titleTextField)
        {
            titleBar.removeChild(DisplayObject(titleTextField));
            titleTextField = null;
        }
    }
    
    /**
     *  @private
     *  Creates the status text field child
     *  and adds it as a child of this component.
     * 
     *  @param childIndex The index of where to add the child.
     *  If -1, the text field is appended to the end of the list.
     */
    mx_internal function createStatusTextField(childIndex:int):void
    {
        // Create the statusTextField as a child of the titleBar.
        if (titleBar && !statusTextField)
        {
            statusTextField = IUITextField(createInFontContext(UITextField));
            statusTextField.selectable = false;
            
            if (childIndex == -1)
                titleBar.addChild(DisplayObject(statusTextField));                
            else 
                titleBar.addChildAt(DisplayObject(statusTextField), childIndex);

            var statusStyleName:String = getStyle("statusStyleName");
            statusTextField.styleName = statusStyleName;
            statusTextField.text = status;
            statusTextField.enabled = enabled;
        }
    }
    
    /**
     *  @private
     *  Removes the status text field from this component.
     */
    mx_internal function removeStatusTextField():void
    {
        if (titleBar && statusTextField)
        {
            titleBar.removeChild(DisplayObject(statusTextField));
            statusTextField = null;
        }
    }

    /**
     *  @private.
     *  Returns a Rectangle containing the largest piece of header
     *  text (can be either the title or status, whichever is bigger).
     */
    private function measureHeaderText():Rectangle
    {
        var textWidth:Number = 20;
        var textHeight:Number = 14;

        var textFormat:UITextFormat;
        var metrics:TextLineMetrics;
        
        if (titleTextField && titleTextField.text)
        {
            titleTextField.validateNow();
            textFormat = titleTextField.getUITextFormat();
            metrics = textFormat.measureText(titleTextField.text, false);
            textWidth = metrics.width;
            textHeight = metrics.height;
        }
        
        if (statusTextField && statusTextField.text)
        {
            statusTextField.validateNow();
            textFormat = statusTextField.getUITextFormat();
            metrics = textFormat.measureText(statusTextField.text, false);
            textWidth = Math.max(textWidth, metrics.width);
            textHeight = Math.max(textHeight, metrics.height);
        }

        return new Rectangle(0, 0, Math.round(textWidth), Math.round(textHeight));
    }
    
    /**
     *  Returns the height of the header.
     * 
     *  @return The height of the header, in pixels.     
     */
    protected function getHeaderHeight():Number
    {
        var headerHeight:Number = getStyle("headerHeight");
        
        if (isNaN(headerHeight))
            headerHeight = measureHeaderText().height + HEADER_PADDING;
        
        return headerHeight;
    }
    
    /**
     *  @private
     *  Proxy to getHeaderHeight() for PanelSkin
     *  since we can't change its function signature
     */
    mx_internal function getHeaderHeightProxy():Number
    {
        return getHeaderHeight();
    }

    /**
     *  @private
     */
    private function showTitleBar(show:Boolean):void
    {
        titleBar.visible = show;
        
        var n:int = titleBar.numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:DisplayObject = titleBar.getChildAt(i);
            child.visible = show;
        }               
    }   

    /**
     *  @private
     */
    private function setControlBar(newControlBar:IUIComponent):void
    {
        if (newControlBar == controlBar)
            return;
            
        controlBar = newControlBar;     
        
        // If roundedBottomCorners is set locally, don't auto-set
        // it when the controlbar is added/removed.
        if (!checkedForAutoSetRoundedCorners)
        {
            checkedForAutoSetRoundedCorners = true;
            autoSetRoundedCorners = styleDeclaration ? 
                    styleDeclaration.getStyle("roundedBottomCorners") === undefined : 
                    true;
        }
        
        if (autoSetRoundedCorners)
            setStyle("roundedBottomCorners", controlBar != null);

        var controlBarStyleName:String = getStyle("controlBarStyleName");
        
        if (controlBarStyleName && controlBar is ISimpleStyleClient)
            ISimpleStyleClient(controlBar).styleName = controlBarStyleName;
        
        if (controlBar)
            controlBar.enabled = enabled;
        if (controlBar is IAutomationObject)
            IAutomationObject(controlBar).showInAutomationHierarchy = false;

        invalidateViewMetricsAndPadding();
        invalidateSize();
        invalidateDisplayList();
    }
    
    /**
     *  Called when the user starts dragging a Panel
     *  that has been popped up by the PopUpManager.
     */
    protected function startDragging(event:MouseEvent):void
    {
        regX = event.stageX - x;
        regY = event.stageY - y;
        
        systemManager.addEventListener(
            MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler, true);

        systemManager.addEventListener(
            MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);

        systemManager.stage.addEventListener(
            Event.MOUSE_LEAVE, stage_mouseLeaveHandler);
    }

    /**
     *  Called when the user stops dragging a Panel
     *  that has been popped up by the PopUpManager.
     */
    protected function stopDragging():void
    {
        systemManager.removeEventListener(
            MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler, true);

        systemManager.removeEventListener(
            MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);

        systemManager.stage.removeEventListener(
            Event.MOUSE_LEAVE, stage_mouseLeaveHandler);

        regX = NaN;
        regY = NaN;
    }

    /**
     *  @private
     *  Some other components which use a Panel as an internal
     *  subcomponent need access to the title bar,
     *  but can't access the titleBar var because it is protected
     *  and therefore available only to subclasses.
     */
    mx_internal function getTitleBar():UIComponent
    {
        return titleBar;
    }

    /**
     *  @private
     *  Some other components which use a Panel as an internal
     *  subcomponent need access to the UITextField that displays the title,
     *  but can't access the titleTextField var because it is protected
     *  and therefore available only to subclasses.
     */
    mx_internal function getTitleTextField():IUITextField
    {
        return titleTextField;
    }

    /**
     *  @private
     *  Some other components which use a Panel as an internal
     *  subcomponent need access to the UITextField that displays the status,
     *  but can't access the statusTextField var because it is protected
     *  and therefore available only to subclasses.
     */
    mx_internal function getStatusTextField():IUITextField
    {
        return statusTextField;
    }

    /**
     *  @private
     *  Some other components which use a Panel as an internal
     *  subcomponent need access to the control bar,
     *  but can't access the controlBar var because it is protected
     *  and therefore available only to subclasses.
     */
    mx_internal function getControlBar():IUIComponent
    {
        return controlBar;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function titleBar_mouseDownHandler(event:MouseEvent):void
    {
        // A mouseDown on the closeButton will bubble up to the titleBar,
        // but it shouldn't start a drag; it should simply start the
        // normal mouse/Button interaction.
        if (event.target == closeButton)
            return;

        if (enabled && isPopUp && isNaN(regX))
            startDragging(event);
    }

    /**
     *  @private
     */
    private function systemManager_mouseMoveHandler(event:MouseEvent):void
    {
        // during a drag, only the Panel should get mouse move events
        // (e.g., prevent objects 'beneath' it from getting them -- see bug 187569)
        // we don't check the target since this is on the systemManager and the target
        // changes a lot -- but this listener only exists during a drag.
        event.stopImmediatePropagation();
        
        move(event.stageX - regX, event.stageY - regY);
    }

    /**
     *  @private
     */
    private function systemManager_mouseUpHandler(event:MouseEvent):void
    {
        if (!isNaN(regX))
            stopDragging();
    }

    /**
     *  @private
     */
    private function stage_mouseLeaveHandler(event:Event):void
    {
        if (!isNaN(regX))
            stopDragging();
    }

    /**
     *  @private
     */
    private function closeButton_clickHandler(event:MouseEvent):void
    {
        dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }
}

}
