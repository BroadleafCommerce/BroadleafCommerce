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

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.text.TextLineMetrics;
import flash.ui.Keyboard;
import flash.utils.Timer;
import mx.controls.dataGridClasses.DataGridListData;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IBorder;
import mx.core.IButton;
import mx.core.IDataRenderer;
import mx.core.IFlexAsset;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IInvalidating;
import mx.core.IRectangularBorder;
import mx.core.IUIComponent;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.MoveEvent;
import mx.managers.IFocusManagerComponent;
import mx.states.State;
import mx.styles.ISimpleStyleClient;
import mx.core.IStateClient;
import mx.core.IProgrammaticSkin;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the user presses the Button control.
 *  If the <code>autoRepeat</code> property is <code>true</code>,
 *  this event is dispatched repeatedly as long as the button stays down.
 *
 *  @eventType mx.events.FlexEvent.BUTTON_DOWN
 */
[Event(name="buttonDown", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the <code>selected</code> property 
 *  changes for a toggle Button control. A toggle Button control means that the 
 *  <code>toggle</code> property is set to <code>true</code>. 
 * 
 *  For the RadioButton controls, this event is dispatched when the <code>selected</code> 
 *  property changes.
 * 
 *  For the CheckBox controls, this event is dispatched only when the 
 *  user interacts with the control by using the mouse.
 *
 *  @eventType flash.events.Event.CHANGE
 */
[Event(name="change", type="flash.events.Event")]

/**
 *  Dispatched when the <code>data</code> property changes.
 *
 *  <p>When you use a component as an item renderer,
 *  the <code>data</code> property contains the data to display.
 *  You can listen for this event and update the component
 *  when the <code>data</code> property changes.</p>
 * 
 *  @eventType mx.events.FlexEvent.DATA_CHANGE
 */
[Event(name="dataChange", type="mx.events.FlexEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/FocusStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/SkinStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Gap between the label and icon, when the <code>labelPlacement</code> property
 *  is set to <code>left</code> or <code>right</code>.
 * 
 *  @default 2
 */
[Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the component's bottom border
 *  and the bottom of its content area.
 *  
 *  @default 0 
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the component's top border
 *  and the top of its content area.
 *  
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

/**
 *  Number of milliseconds to wait after the first <code>buttonDown</code>
 *  event before repeating <code>buttonDown</code> events at each 
 *  <code>repeatInterval</code>.
 * 
 *  @default 500
 */
[Style(name="repeatDelay", type="Number", format="Time", inherit="no")]

/**
 *  Number of milliseconds between <code>buttonDown</code> events
 *  if the user presses and holds the mouse on a button.
 *  
 *  @default 35
 */
[Style(name="repeatInterval", type="Number", format="Time", inherit="no")]

/**
 *  Text color of the label as the user moves the mouse pointer over the button.
 *  
 *  @default 0x2B333C
 */
[Style(name="textRollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Text color of the label as the user presses it.
 *  
 *  @default 0x000000
 */
[Style(name="textSelectedColor", type="uint", format="Color", inherit="yes")]

/**
 *  Gap between the button's label and icon when the <code>labelPlacement</code>
 *  property is set to <code>"top"</code> or <code>"bottom"</code>.
 * 
 *  @default 2
 */
[Style(name="verticalGap", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Skins
//--------------------------------------

/**
 *  Name of the class to use as the default skin for the background and border. 
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="skin", type="Class", inherit="no", states="up, over, down, disabled, selectedUp, selectedOver, selectedDown, selectedDisabled")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the button is not selected and the mouse is not over the control.
 *  
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="upSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the button is not selected and the mouse is over the control.
 *  
 *  @default "mx.skins.halo.ButtonSkin" 
 */
[Style(name="overSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the button is not selected and the mouse button is down.
 *  
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="downSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the button is not selected and is disabled.
 * 
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="disabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when a toggle button is selected and the mouse is not over the control.
 * 
 *  @default "mx.skins.halo.ButtonSkin" 
 */
[Style(name="selectedUpSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when a toggle button is selected and the mouse is over the control.
 *  
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="selectedOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when a toggle button is selected and the mouse button is down.
 *  
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="selectedDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when a toggle button is selected and disabled.
 * 
 *  @default "mx.skins.halo.ButtonSkin"
 */
[Style(name="selectedDisabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the default icon. 
 *  Setting any other icon style overrides this setting.
 *  
 *  @default null 
 */
[Style(name="icon", type="Class", inherit="no", states="up, over, down, disabled, selectedUp, selectedOver, selectedDown, selectedDisabled")]

/**
 *  Name of the class to use as the icon when a toggle button is not 
 *  selected and the mouse is not over the button.
 * 
 *  @default null 
 */
[Style(name="upIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon when the button is not 
 *  selected and the mouse is over the control.
 * 
 *  @default null 
 */
[Style(name="overIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon when the button is not 
 *  selected and the mouse button is down.
 * 
 *  @default null 
 */
[Style(name="downIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon
 *  when the button is disabled and not selected.
 * 
 *  @default null 
 */
[Style(name="disabledIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon
 *  when the button is selected and the mouse button is up.
 * 
 *  @default null 
 */
[Style(name="selectedUpIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon
 *  when the button is selected and the mouse is over the control.
 * 
 *  @default null 
 */
[Style(name="selectedOverIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon
 *  when the button is selected and the mouse button is down.
 * 
 *  @default null 
 */
[Style(name="selectedDownIcon", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon
 *  when the button is selected and disabled.
 * 
 *  @default null 
 */
[Style(name="selectedDisabledIcon", type="Class", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.ButtonAccImpl")]

[DefaultBindingProperty(source="selected", destination="label")]

[DefaultTriggerEvent("click")]

[IconFile("Button.png")]

/**
 *  The Button control is a commonly used rectangular button.
 *  Button controls look like they can be pressed.
 *  They can have a text label, an icon, or both on their face.
 *
 *  <p>Buttons typically use event listeners to perform an action 
 *  when the user selects the control. When a user clicks the mouse 
 *  on a Button control, and the Button control is enabled, 
 *  it dispatches a <code>click</code> event and a <code>buttonDown</code> event. 
 *  A button always dispatches events such as the <code>mouseMove</code>, 
 *  <code>mouseOver</code>, <code>mouseOut</code>, <code>rollOver</code>, 
 *  <code>rollOut</code>, <code>mouseDown</code>, and 
 *  <code>mouseUp</code> events whether enabled or disabled.</p>
 *
 *  <p>You can customize the look of a Button control
 *  and change its functionality from a push button to a toggle button.
 *  You can change the button appearance by using a skin
 *  for each of the button's states.</p>
 *
 *  <p>The label of a Button control uses a bold typeface. If you embed 
 *  a font that you want to use for the label of the Button control, you must 
 *  embed the bold typeface; for example:</p>
 * 
 *  <pre>
 *  &lt;mx:style&gt;
 *    &#64;font-face {
 *      src:url("../MyFont-Bold.ttf");        
 *      fontFamily: myFont;
 *      fontWeight: bold;
 *    }
 *   .myBoldStyle {
 *      fontFamily: myFont;
 *      fontWeight: bold;
 *    } 
 *  &lt;/mx:style&gt;
 *  ...
 *  &lt;mx:Button ... styleName="myBoldStyle"/&gt;
 *  </pre>
 *  
 *  <p>The Button control has the following default characteristics:</p>
 *  <table class="innertable">
 *     <tr><th>Characteristic</th><th>Description</th></tr>
 *     <tr><td>Default size</td><td>A size large enough to hold the label text, and any icon</td></tr>
 *     <tr><td>Minimum size</td><td>0 pixels</td></tr>
 *     <tr><td>Maximum size</td><td>No limit</td></tr>
 *  </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Button&gt;</code> tag inherits all the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:Button
 *    <b>Properties</b>
 *    autoRepeat="false|true"
 *    emphasized="false|true"
 *    fontContext="<i>IFontModuleFactory</i>"
 *    label=""
 *    labelPlacement="right|left|bottom|top"
 *    selected="false|true"
 *    selectedField="null"
 *    stickyHighlighting="false|true"
 *    toggle="false|true"
 * 
 *    <b>Styles</b>
 *    borderColor="0xAAB3B3"
 *    color="0x0B333C"
 *    cornerRadius="4"
 *    disabledColor="0xAAB3B3"
 *    disabledIcon="null"
 *    disabledSkin="mx.skins.halo.ButtonSkin"
 *    downIcon="null"
 *    downSkin="mx.skins.halo.ButtonSkin"
 *    fillAlphas="[0.6, 0.4]"
 *    fillColors="[0xE6EEEE, 0xFFFFFF]"
 *    focusAlpha="0.5"
 *    focusRoundedCorners"tl tr bl br"
 *    fontAntiAliasType="advanced"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel"
 *    fontSharpness="0"
 *    fontSize="10"
 *    fontStyle="normal|italic"
 *    fontThickness="0"
 *    fontWeight="bold|normal"
 *    highlightAlphas="[0.3, 0.0]"
 *    horizontalGap="2"
 *    icon="null"
 *    kerning="false|true"
 *    leading="2"
 *    letterSpacing="0"
 *    overIcon="null"
 *    overSkin="mx.skins.halo.ButtonSkin"
 *    paddingBottom="0"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    paddingTop="0"
 *    repeatDelay="500"
 *    repeatInterval="35"
 *    selectedDisabledIcon="null"
 *    selectedDisabledSkin="mx.skins.halo.ButtonSkin"
 *    selectedDownIcon="null"
 *    selectedDownSkin="mx.skins.halo.ButtonSkin"
 *    selectedOverIcon="null"
 *    selectedOverSkin="mx.skins.halo.ButtonSkin"
 *    selectedUpIcon="null"
 *    selectedUpSkin="mx.skins.halo.ButtonSkin"
 *    skin="mx.skins.halo.ButtonSkin"
 *    textAlign="center|left|right"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    textRollOverColor="0x2B333C"
 *    textSelectedColor="0x000000"
 *    upIcon="null"
 *    upSkin="mx.skins.halo.ButtonSkin"
 *    verticalGap="2"
 * 
 *    <b>Events</b>
 *    buttonDown="<i>No default</i>"
 *    change="<i>No default</i>"
 *    dataChange="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/ButtonExample.mxml
 */
public class Button extends UIComponent
       implements IDataRenderer, IDropInListItemRenderer,
       IFocusManagerComponent, IListItemRenderer,
       IFontContextComponent, IButton
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by ButtonAccImpl.
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
    public function Button()
    {
        super();

        // DisplayObjectContainer properties.
        // Setting mouseChildren to false ensure that mouse events
        // are dispatched from the Button itself,
        // not from its skins, icons, or TextField.
        // One reason for doing this is that if you press the mouse button
        // while over the TextField and release the mouse button while over
        // a skin or icon, we want the player to dispatch a "click" event.
        // Another is that if mouseChildren were true and someone uses
        // Sprites rather than Shapes for the skins or icons,
        // then we we wouldn't get a click because the current skin or icon
        // changes between the mouseDown and the mouseUp.
        // (This doesn't happen even when mouseChildren is true if the skins
        // and icons are Shapes, because Shapes never dispatch mouse events;
        // they are dispatched from the Button in this case.)
        mouseChildren = false;

        // Register for player events.
        addEventListener(MouseEvent.ROLL_OVER, rollOverHandler);
        addEventListener(MouseEvent.ROLL_OUT, rollOutHandler);
        addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
        addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
        addEventListener(MouseEvent.CLICK, clickHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Skins for the various states (falseUp, trueOver, etc.)
     *  are created just-in-time as they are needed.
     *  Each skin is a child Sprite of this Button.
     *  Each skin has a name property indicating which skin it is;
     *  for example, the instance of the class specified by the falseUpSkin
     *  style has the name "falseUpSkin" and can be found using
     *  getChildByName(). Note that there is no falseUpSkin property
     *  of Button containing a reference to this skin instance.
     *  This array contains references to all skins that have been created,
     *  for looping over them; without this array we wouldn't know
     *  which of the children are the skins.
     *  New skins are created and added to this array in viewSkin().
     */
    private var skins:Array /* of Sprite */ = [];

    /**
     *  @private
     *  A reference to the current skin.
     *  Set by viewSkin().
     */
    mx_internal var currentSkin:IFlexDisplayObject;

    /**
     *  @private
     *  Skins for the various states (falseUp, trueOver, etc.)
     *  are created just-in-time as they are needed.
     *  Each icon is a child Sprite of this Button.
     *  Each icon has a name property indicating which icon it is;
     *  for example, the instance of the class specified by the falseUpIcon
     *  style has the name "falseUpIcon" and can be found using
     *  getChildByName(). Note that there is no falseUpIcon property
     *  of Button containing a reference to this icon instance.
     *  This array contains references to all icons that have been created,
     *  for looping over them; without this array we wouldn't know
     *  which of the children are the icons.
     *  New icons are created and added to this array in viewIcon().
     */
    private var icons:Array /* of Sprite */ = [];

    /**
     *  @private
     *  A reference to the current icon.
     *  Set by viewIcon().
     */
    mx_internal var currentIcon:IFlexDisplayObject;

    /**
     *  @private
     *  Timer for doing auto-repeat.
     */
    private var autoRepeatTimer:Timer;

    /**
     *  @private
     *  Number used to offset the label and/or icon
     *  when button is pressed.
     */
    mx_internal var buttonOffset:Number = 0;
    
    /**
     *  @private
     *  used by old measure/layout logic
     */
    mx_internal var centerContent:Boolean = true;

    /**
     *  @private
     *  used by old measure/layout logic
     */
    mx_internal var extraSpacing:Number = 10 + 10;
    
     /**
     *  @private
     */
    mx_internal static var TEXT_WIDTH_PADDING:Number = UITextField.TEXT_WIDTH_PADDING + 1;

    /**
     *  @private
     */
    private var styleChangedFlag:Boolean = true;

    /**
     *  @private
     *  The measured width of the first skin loaded.
     */
    private var skinMeasuredWidth:Number;

    /**
     *  @private
     *  The measured height of the first skin loaded.
     */
    private var skinMeasuredHeight:Number;

    /**
     *  @private
     *  The value of the unscaledWidth parameter during the most recent
     *  call to updateDisplayList
     */
    private var oldUnscaledWidth:Number;

    /**
     *  @private
     *  Flags that will block default data/listData behavior
     */
    private var selectedSet:Boolean;
    private var labelSet:Boolean;

    /**
     *  @private
     *  Flags used to save information about the skin and icon styles
     */
    mx_internal var checkedDefaultSkin:Boolean = false;
    mx_internal var defaultSkinUsesStates:Boolean = false;  
    mx_internal var checkedDefaultIcon:Boolean = false;
    mx_internal var defaultIconUsesStates:Boolean = false;

    /**
     *  @private
     *  Skin names.
     *  Allows subclasses to re-define the skin property names.
     */
    mx_internal var skinName:String = "skin"; 
    mx_internal var upSkinName:String = "upSkin";
    mx_internal var overSkinName:String = "overSkin";
    mx_internal var downSkinName:String = "downSkin";
    mx_internal var disabledSkinName:String = "disabledSkin";
    mx_internal var selectedUpSkinName:String = "selectedUpSkin";
    mx_internal var selectedOverSkinName:String = "selectedOverSkin";
    mx_internal var selectedDownSkinName:String = "selectedDownSkin";
    mx_internal var selectedDisabledSkinName:String = "selectedDisabledSkin";

    /**
     *  @private
     *  Icon names.
     *  Allows subclasses to re-define the icon property names.
     */
    mx_internal var iconName:String = "icon";
    mx_internal var upIconName:String = "upIcon";
    mx_internal var overIconName:String = "overIcon";
    mx_internal var downIconName:String = "downIcon";
    mx_internal var disabledIconName:String = "disabledIcon";
    mx_internal var selectedUpIconName:String = "selectedUpIcon";
    mx_internal var selectedOverIconName:String = "selectedOverIcon";
    mx_internal var selectedDownIconName:String = "selectedDownIcon";
    mx_internal var selectedDisabledIconName:String = "selectedDisabledIcon";

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
     *  The baselinePosition of a Button is calculated for its label.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            var t:String = label;
            if (!t)
                t = "Wj";

            // If we're dirty, force a relayout here
            // so that our internal text field is positioned first;
            // otherwise we get a stale value.
            validateNow();

            // If we havent specified a label and the placement is either top
            // or bottom, we will set the baseline to less that half the height.
            if (!label &&
                (labelPlacement == ButtonLabelPlacement.TOP ||
                 labelPlacement == ButtonLabelPlacement.BOTTOM))
            {
                var lineMetrics:TextLineMetrics = measureText(t);
                return (measuredHeight - lineMetrics.height) / 2 +
                       lineMetrics.ascent;
            }

            return textField.y + measureText(t).ascent;
        }

        if (!validateBaselinePosition())
            return NaN;

        return textField.y + textField.baselinePosition;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     */
    private var enabledChanged:Boolean = false;

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     *  This is called whenever the enabled state changes.
     */
    override public function set enabled(value:Boolean):void
    {
        if (super.enabled == value)
            return;

        super.enabled = value;
        enabledChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  textField
    //----------------------------------

    /**
     *  The internal UITextField object that renders the label of this Button.
     * 
     *  @default null 
     */
    protected var textField:IUITextField;

    //----------------------------------
    //  toolTip
    //----------------------------------

    /**
     *  @private
     */
    private var toolTipSet:Boolean = false;

    [Inspectable(category="General", defaultValue="null")]

    /**
     *  @private
     */
    override public function set toolTip(value:String):void
    {
        super.toolTip = value;

        if (value)
        {
            toolTipSet = true;
        }
        else
        {
            toolTipSet = false;
            invalidateDisplayList();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoRepeat
    //----------------------------------

    /**
     *  @private
     *  Storage for the autoRepeat property.
     */
    private var _autoRepeat:Boolean = false;

    [Inspectable(defaultValue="false")]

    /**
     *  Specifies whether to dispatch repeated <code>buttonDown</code>
     *  events if the user holds down the mouse button.
     *
     *  @default false
     */
    public function get autoRepeat():Boolean
    {
        return _autoRepeat;
    }

    /**
     *  @private
     */
    public function set autoRepeat(value:Boolean):void
    {
        _autoRepeat = value;

        if (value)
        {
            // Create a Timer object for driving the autorepeat.
            // The duration gets set in mouseDownHandler and reset
            // in autoRepeatTimer_timerDelayHandler, because
            // there is a delay before the first autorepeat
            // and then a possibly different interval
            // between subsequent ones.
            autoRepeatTimer = new Timer(1);
        }
        else
        {
            autoRepeatTimer = null;
        }
    }

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property;
     */
    private var _data:Object;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  The <code>data</code> property lets you pass a value
     *  to the component when you use it as an item renderer or item editor.
     *  You typically use data binding to bind a field of the <code>data</code>
     *  property to a property of this component.
     *
     *  <p>When you use the control as a drop-in item renderer or drop-in
     *  item editor, Flex automatically writes the current value of the item
     *  to the <code>selected</code> property of this control.</p>
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
        var newSelected:*;
        var newLabel:*;

        _data = value;

        if (_listData && _listData is DataGridListData)
        {
            newSelected = _data[DataGridListData(_listData).dataField];

            newLabel = "";
        }
        else if (_listData)
        {
            if (selectedField)
                newSelected = _data[selectedField];

            newLabel = _listData.label;
        }
        else
        {
            newSelected = _data;
        }

        if (newSelected !== undefined && !selectedSet)
        {
            selected = newSelected as Boolean;
            selectedSet = false;
        }
        if (newLabel !== undefined && !labelSet)
        {
            label = newLabel;
            labelSet = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  emphasized
    //----------------------------------

    /**
     *  @private
     *  Storage for the emphasized property.
     */
    mx_internal var _emphasized:Boolean = false;

    /**
     *  @private
     */
    private var emphasizedChanged:Boolean = false;


    [Inspectable(category="General", defaultValue="false")]

    /**
     *  Draws a thick border around the Button control
     *  when the control is in its up state if <code>emphasized</code>
     *  is set to <code>true</code>. 
     *
     *  @default false
     */
    public function get emphasized():Boolean
    {
        return _emphasized;
    }

    /**
     *  @private
     */
    public function set emphasized(value:Boolean):void
    {
        _emphasized = value;
        emphasizedChanged = true;        

        invalidateDisplayList();
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
    //  label
    //----------------------------------

    /**
     *  @private
     *  Storage for label property.
     */
    private var _label:String = "";

    /**
     *  @private
     */
    private var labelChanged:Boolean = false;

    [Bindable("labelChanged")]
    [Inspectable(category="General", defaultValue="")]

    /**
     *  Text to appear on the Button control.
     *
     *  <p>If the label is wider than the Button control,
     *  the label is truncated and terminated by an ellipsis (...).
     *  The full label displays as a tooltip
     *  when the user moves the mouse over the Button control.
     *  If you have also set a tooltip by using the <code>tooltip</code>
     *  property, the tooltip is displayed rather than the label text.</p>
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
        labelSet = true;

        if (_label != value)
        {
            _label = value;
            labelChanged = true;

            invalidateSize();
            invalidateDisplayList();

            dispatchEvent(new Event("labelChanged"));
        }
    }

    //----------------------------------
    //  labelPlacement
    //----------------------------------

    /**
     *  @private
     *  Storage for labelPlacement property.
     */
    mx_internal var _labelPlacement:String = ButtonLabelPlacement.RIGHT;

    [Bindable("labelPlacementChanged")]
    [Inspectable(category="General", enumeration="left,right,top,bottom", defaultValue="right")]

    /**
     *  Orientation of the label in relation to a specified icon.
     *  Valid MXML values are <code>right</code>, <code>left</code>,
     *  <code>bottom</code>, and <code>top</code>.
     *
     *  <p>In ActionScript, you can use the following constants
     *  to set this property:
     *  <code>ButtonLabelPlacement.RIGHT</code>,
     *  <code>ButtonLabelPlacement.LEFT</code>,
     *  <code>ButtonLabelPlacement.BOTTOM</code>, and
     *  <code>ButtonLabelPlacement.TOP</code>.</p>
     *
     *  @default ButtonLabelPlacement.RIGHT
     */
    public function get labelPlacement():String
    {
        return _labelPlacement;
    }

    /**
     *  @private
     */
    public function set labelPlacement(value:String):void
    {
        _labelPlacement = value;

        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("labelPlacementChanged"));
    }

    //-----------------------------------
    //  listData
    //-----------------------------------

    /**
     *  @private
     *  Storage for the listData property.
     */
    private var _listData:BaseListData;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  When a component is used as a drop-in item renderer or drop-in
     *  item editor, Flex initializes the <code>listData</code> property
     *  of the component with the appropriate data from the list control.
     *  The component can then use the <code>listData</code> property
     *  to initialize the <code>data</code> property
     *  of the drop-in item renderer or drop-in item editor.
     *
     *  <p>You do not set this property in MXML or ActionScript;
     *  Flex sets it when the component is used as a drop-in item renderer
     *  or drop-in item editor.</p>
     *
     *  @default null
     *  @see mx.controls.listClasses.IDropInListItemRenderer
     */
    public function get listData():BaseListData
    {
        return _listData;
    }

    /**
     *  @private
     */
    public function set listData(value:BaseListData):void
    {
        _listData = value;
    }

    //----------------------------------
    //  phase
    //----------------------------------

    /**
     *  @private
     *  Mouse and focus events set this to
     *  ButtonPhase.UP, ButtonPhase.OVER, or ButtonPhase.DOWN.
     */
    private var _phase:String = ButtonPhase.UP;

    /**
     *  @private
     */
    mx_internal function get phase():String
    {
        return _phase;
    }

    /**
     *  @private
     */
    mx_internal function set phase(value:String):void
    {
        _phase = value;
        
        invalidateSize();
        invalidateDisplayList();
    }

    //----------------------------------
    //  selected
    //----------------------------------

    /**
     *  @private
     *  Storage for selected property.
     */
    mx_internal var _selected:Boolean = false;

    [Bindable("click")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="false")]

    /**
     *  Indicates whether a toggle button is toggled
     *  on (<code>true</code>) or off (<code>false</code>).
     *  This property can be set only if the <code>toggle</code> property
     *  is set to <code>true</code>.
     *
     *  <p>For a CheckBox control, indicates whether the box
     *  is displaying a check mark. For a RadioButton control, 
     *  indicates whether the control is selected.</p>
     *
     *  <p>The user can change this property by clicking the control,
     *  but you can also set the property programmatically.</p>
     *
     *  <p>In previous versions, If the <code>toggle</code> property 
     *  was set to <code>true</code>, changing this property also dispatched 
     *  a <code>change</code> event. Starting in version 3.0, setting this 
     *  property programmatically only dispatches a 
     *  <code>valueCommit</code> event.</p>
     *
     *  @default false
     */
    public function get selected():Boolean
    {
        return _selected;
    }

    /**
     *  @private
     */
    public function set selected(value:Boolean):void
    {
        selectedSet = true;
        setSelected(value, true);
    }

    mx_internal function setSelected(value:Boolean, 
                                     isProgrammatic:Boolean = false):void
    {
        if (_selected != value)
        {
            _selected = value;

            invalidateDisplayList();
            
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            {
                 if (toggle)
                    dispatchEvent(new Event(Event.CHANGE));
            }
            else
            {
                if (toggle && !isProgrammatic)
                    dispatchEvent(new Event(Event.CHANGE));
            }
            dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
        }
    }

    //----------------------------------
    //  selectedField
    //----------------------------------

    /**
     *  The name of the field in the <code>data</code> property which specifies
     *  the value of the Button control's <code>selected</code> property. 
     *  You can set this property when you use the Button control in an item renderer.
     *  The default value is null, which means that the Button control does 
     *  not set its selected state based on a property in the <code>data</code> property.
     *
     *  @default null
     */
    public var selectedField:String = null;

    //----------------------------------
    //  stickyHighlighting
    //----------------------------------

    /**
     *  If <code>false</code>, the Button displays its down skin
     *  when the user presses it but changes to its over skin when
     *  the user drags the mouse off of it.
     *  If <code>true</code>, the Button displays its down skin
     *  when the user presses it, and continues to display this skin
     *  when the user drags the mouse off of it.
     *
     *  <p>Button subclasses, such as the SliderThumb and ScrollThumb classes
     *  or the up and down arrows of a ScrollBar, set 
     *  this property to <code>true</code>.</p>
     *
     *  @default false
     */
    public var stickyHighlighting:Boolean = false;

    //----------------------------------
    //  toggle
    //----------------------------------

    /**
     *  @private
     *  Storage for toggle property.
     */
    mx_internal var _toggle:Boolean = false;

    /**
     *  @private
     */
    mx_internal var toggleChanged:Boolean = false;

    [Bindable("toggleChanged")]
    [Inspectable(category="General", defaultValue="false")]

    /**
     *  Controls whether a Button is in a toggle state or not. 
     * 
     *  If <code>true</code>, clicking the button toggles it
     *  between a selected and an unselected state.
     *  You can get or set this state programmatically
     *  by using the <code>selected</code> property.
     *
     *  If <code>false</code>, the button does not stay pressed
     *  after the user releases it.
     *  In this case, its <code>selected</code> property
     *  is always <code>false</code>.
     *  Buttons like this are used for performing actions.
     *
     *  When <code>toggle</code> is set to <code>false</code>,
     *  <code>selected</code> is forced to <code>false</code>
     *  because only toggle buttons can be selected.
     *
     *  @default false
     */
    public function get toggle():Boolean
    {
        return _toggle;
    }

    /**
     *  @private
     */
    public function set toggle(value:Boolean):void
    {
        _toggle = value;
        toggleChanged = true;

        invalidateProperties();
        invalidateDisplayList();

        dispatchEvent(new Event("toggleChanged"));
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
        if (Button.createAccessibilityImplementation != null)
            Button.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        // Create a UITextField to display the label.
        if (!textField)
        {
            textField = IUITextField(createInFontContext(UITextField));
            textField.styleName = this;
            addChild(DisplayObject(textField));
        }
 
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

         // if the font changed and we already created the textfield, we will need to 
        // destory it so it can be re-created, possibly in a different swf context.
        if (hasFontContextChanged() && textField != null)
        {
            removeChild(DisplayObject(textField));
            textField = null;
        }
        
         // Create a UITextField to display the label.
        if (!textField)
        {
            textField = IUITextField(createInFontContext(UITextField));
            textField.styleName = this;
            addChild(DisplayObject(textField));

            enabledChanged = true;
            toggleChanged = true;
        }

        if (!initialized)
        {
            viewSkin();
            viewIcon();
        }

        if (enabledChanged)
        {
            textField.enabled = enabled;
            
            if (currentIcon && currentIcon is IUIComponent)
                IUIComponent(currentIcon).enabled = enabled;
            
            enabledChanged = false;
        }

        if (toggleChanged)
        {
            // If the button is no longer toggleable,
            // deselect it.
            if (!toggle)
                selected = false;
            toggleChanged = false;
        }
    }

    /**
     *  @private 
     *  Old version of the measure function
     */
    private function previousVersion_measure():void
    {
        super.measure();

        var textWidth:Number = 0;
        var textHeight:Number = 0;

        if (label)
        {
            var lineMetrics:TextLineMetrics = measureText(label);
            textWidth = lineMetrics.width;
            textHeight = lineMetrics.height;

            var paddingLeft:Number = getStyle("paddingLeft");
            var paddingRight:Number = getStyle("paddingRight");
            var paddingTop:Number = getStyle("paddingTop");
            var paddingBottom:Number = getStyle("paddingBottom");

            textWidth += paddingLeft + paddingRight + getStyle("textIndent"); 
            textHeight += paddingTop + paddingBottom; 
        }

        // If the current skin defines a borderMetrics property,
        // then use it; otherwise, use a default value.
        var bm:EdgeMetrics;
        try
        {
            bm = currentSkin["borderMetrics"];
        }
        catch(e:Error)
        {
		    bm = new EdgeMetrics(3, 3, 3, 3);
		}
    
        var tempCurrentIcon:IFlexDisplayObject = getCurrentIcon();  
        var iconWidth:Number = tempCurrentIcon ? tempCurrentIcon.width : 0;
        var iconHeight:Number = tempCurrentIcon ? tempCurrentIcon.height : 0;

        var w:Number = 0;
        var h:Number = 0;

        if (labelPlacement == ButtonLabelPlacement.LEFT ||
            labelPlacement == ButtonLabelPlacement.RIGHT)
        {
            w = textWidth + iconWidth;
            if (iconWidth != 0)
            {
                var horizontalGap:Number = getStyle("horizontalGap");
                w += (horizontalGap - 2 );
            }
            h = Math.max(textHeight, iconHeight + 6);
        }
        else
        {
            w = Math.max(textWidth, iconWidth);
            h = textHeight + iconHeight;
            if (iconHeight != 0)
                h += getStyle("verticalGap");
        }

        if (bm)
        {
            w += bm.left + bm.right;
            h += bm.top + bm.bottom
        }

        // Pad with additional spacing, but only if we have a label.
        if (label && label.length != 0)
            w += extraSpacing;
        else
            w += 6;

        // Use the larger of the measured sizes and the skin's preferred sizes.
        // Each skin should override measure() with their measuredWidth
        // and measuredHeight.
        if (currentSkin && (isNaN(skinMeasuredWidth) || isNaN(skinMeasuredHeight)))
        {
            skinMeasuredWidth = currentSkin.measuredWidth;
            skinMeasuredHeight = currentSkin.measuredHeight;
        }

        if (!isNaN(skinMeasuredWidth))
            w = Math.max(skinMeasuredWidth, w);

        if (!isNaN(skinMeasuredHeight))
            h = Math.max(skinMeasuredHeight, h);

        measuredMinWidth = measuredWidth = w;
        measuredMinHeight = measuredHeight = h;
    }
    
    /**
     *  @private
     */
    override protected function measure():void
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            previousVersion_measure();
            return;
        }
        
        super.measure();

        var textWidth:Number = 0;
        var textHeight:Number = 0;

        if (label)
        {
            var lineMetrics:TextLineMetrics = measureText(label);
            textWidth = lineMetrics.width + TEXT_WIDTH_PADDING;
            textHeight = lineMetrics.height + UITextField.TEXT_HEIGHT_PADDING;
        }
    
        var tempCurrentIcon:IFlexDisplayObject = getCurrentIcon();  
        var iconWidth:Number = tempCurrentIcon ? tempCurrentIcon.width : 0;
        var iconHeight:Number = tempCurrentIcon ? tempCurrentIcon.height : 0;
        var w:Number = 0;
        var h:Number = 0;

        if (labelPlacement == ButtonLabelPlacement.LEFT ||
            labelPlacement == ButtonLabelPlacement.RIGHT)
        {
            w = textWidth + iconWidth;
            if (textWidth && iconWidth)
                w += getStyle("horizontalGap");
            h = Math.max(textHeight, iconHeight);
        }
        else
        {
            w = Math.max(textWidth, iconWidth);
            h = textHeight + iconHeight;
            if (textHeight && iconHeight)
                h += getStyle("verticalGap");
        }

        // Add padding. !!!Need a hack here to only add padding if we don't
        // have text or icon. This is required to make small buttons (like scroll
        // arrows and numeric stepper buttons) look correct.
        if (textWidth || iconWidth)
        {
            w += getStyle("paddingLeft") + getStyle("paddingRight");
            h += getStyle("paddingTop") + getStyle("paddingBottom");
        }
        
        var bm:EdgeMetrics = currentSkin &&
                             currentSkin is IBorder && !(currentSkin is IFlexAsset) ?
                             IBorder(currentSkin).borderMetrics :
                             null;
        
        if (bm)
        {
            w += bm.left + bm.right;
            h += bm.top + bm.bottom
        }
        
        // Use the larger of the measured sizes and the skin's preferred sizes.
        // Each skin should override measure() with their measuredWidth
        // and measuredHeight.
        if (currentSkin && (isNaN(skinMeasuredWidth) || isNaN(skinMeasuredHeight)))
        {
            skinMeasuredWidth = currentSkin.measuredWidth;
            skinMeasuredHeight = currentSkin.measuredHeight;
        }

        if (!isNaN(skinMeasuredWidth))
            w = Math.max(skinMeasuredWidth, w);

        if (!isNaN(skinMeasuredHeight))
            h = Math.max(skinMeasuredHeight, h);

        measuredMinWidth = measuredWidth = w;
        measuredMinHeight = measuredHeight = h;
    }
    
    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        if (emphasizedChanged)
        {
            changeSkins();
            emphasizedChanged = false;
        }

        // Set each skin's size to the layout size of this Button.
        var n:int = skins.length;
        for (var i:int = 0; i < n; i++)
        {
            var skin:IFlexDisplayObject = IFlexDisplayObject(skins[i]);
            skin.setActualSize(unscaledWidth, unscaledHeight);
        }

        // Show the appropriate skin and icon, based on whether this
        // Button is enabled or disabled, whether it is selected
        // or unselected, and how it is currently interacting
        // with the mouse (i.e., the up/over/down phase).
        viewSkin();
        viewIcon();

        /* if (currentIcon && currentIcon is IUIComponent)
            IUIComponent(currentIcon).enabled = enabled; */

        layoutContents(unscaledWidth, unscaledHeight,
                       phase == ButtonPhase.DOWN);

        // If our width changed, reset the label text to get it to fit.
        if (oldUnscaledWidth > unscaledWidth ||
            textField.text != label ||
            labelChanged ||
            styleChangedFlag)
        {
            textField.text = label;
            var truncated:Boolean = textField.truncateToFit();
            if (!toolTipSet)
            {
                if (truncated)
                    super.toolTip = label;
                else
                    super.toolTip = null;
            }

            styleChangedFlag = false;
            labelChanged = false;
        }

        oldUnscaledWidth = unscaledWidth;
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        styleChangedFlag = true;

        super.styleChanged(styleProp);

        // Check for skin/icon changes here.
        // We could only throw out any skins that change,
        // but since dynamic re-skinning is uncommon, we'll take
        // the simpler approach of throwing out all skins.
        if (!styleProp || styleProp == "styleName")
        {
            // All style props have changed, so dump skins and icons.
            changeSkins();
            changeIcons();
            if (initialized)
            {
                viewSkin();
                viewIcon();
            }
        }
        else if (styleProp.toLowerCase().indexOf("skin") != -1)
        {
            changeSkins();
        }
        else if (styleProp.toLowerCase().indexOf("icon") != -1)
        {
            changeIcons();
            invalidateSize();
        }
    }

    /**
     *  @private
     */
    override protected function adjustFocusRect(
                                    object:DisplayObject = null):void
    {
        // If we don't have a skin, show focus around the icon.
        super.adjustFocusRect(!currentSkin ? DisplayObject(currentIcon) : this);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Displays one of the eight possible skins,
     *  creating it if it doesn't already exist.
     */
    mx_internal function viewSkin():void
    {
        // Determine which skin to display, based on whether this
        // Button is enabled or disabled, whether it is
        // selected or unselected, and how it is currently interacting
        // with the mouse (i.e., the up/over/down state).
        var tempSkinName:String;
        var stateName:String;

        if (!enabled)
        {
            tempSkinName = selected ? selectedDisabledSkinName : disabledSkinName;
            stateName = selected ? "selectedDisabled" : "disabled";
        }
        else if (phase == ButtonPhase.UP)
        {
            tempSkinName = selected ? selectedUpSkinName : upSkinName;
            stateName = selected ? "selectedUp" : "up";
        }
        else if (phase == ButtonPhase.OVER)
        {
            tempSkinName = selected ? selectedOverSkinName : overSkinName;
            stateName = selected ? "selectedOver" : "over";
        }
        else if (phase == ButtonPhase.DOWN)
        {
            tempSkinName = selected ? selectedDownSkinName : downSkinName;
            stateName = selected ? "selectedDown" : "down";
        }
            
        viewSkinForPhase(tempSkinName, stateName);
    }

    /**
     *  @private
     *  Displays one of the several possible skins,
     *  depending on the skinName and creating
     *  it if it doesn't already exist.
     */
    mx_internal function viewSkinForPhase(tempSkinName:String, stateName:String):void
    {
        var newSkinClass:Class = Class(getStyle(tempSkinName));
        var newSkin:IFlexDisplayObject;
                
        if (!newSkinClass)
        {
            // Try the default skin
            newSkinClass = Class(getStyle(skinName));
            // If we are using the default skin, then 
            if (defaultSkinUsesStates)
                tempSkinName = skinName;
            
            if (!checkedDefaultSkin && newSkinClass)
            {
                newSkin = IFlexDisplayObject(new newSkinClass());
                // Check if the skin class is a state client or a programmatic skin
                if (!(newSkin is IProgrammaticSkin) && newSkin is IStateClient)
                {
                    defaultSkinUsesStates = true;
                    tempSkinName = skinName; 
                }
                
                if (newSkin)
                {
                    checkedDefaultSkin = true;
                }
            }
        }
        
        // Has this skin already been created?
        newSkin = IFlexDisplayObject(getChildByName(tempSkinName));
        // If not, create it.
        if (!newSkin)
        {           
            if (newSkinClass)
            {
                newSkin = IFlexDisplayObject(new newSkinClass());
                // Set its name so that we can find it in the future
                // using getChildByName().
                newSkin.name = tempSkinName;

                // Make the getStyle() calls in ButtonSkin find the styles
                // for this Button.
                var styleableSkin:ISimpleStyleClient = newSkin as ISimpleStyleClient;
                if (styleableSkin)
                    styleableSkin.styleName = this;

                addChild(DisplayObject(newSkin));

                // Make the skin the proper size for this Button.
                // This will cause to skin to be drawn by drawHaloRect()
                // in ButtonSkin.
                newSkin.setActualSize(unscaledWidth, unscaledHeight);

                // If the skin is programmatic, and we've already been
                // initialized, update it now to avoid flicker.
                if (newSkin is IInvalidating && initialized)
                {
                    IInvalidating(newSkin).validateNow();
                }
                else if (newSkin is IProgrammaticSkin && initialized)
                {
                    IProgrammaticSkin(newSkin).validateDisplayList()
                }

                // Keep track of all skin children that have been created.
                skins.push(newSkin);
            }
        }

        // Hide the old skin.
        if (currentSkin)
            currentSkin.visible = false;

        // Keep track of which skin is current.
        currentSkin = newSkin;

        // Update the state of the skin if it accepts states and it implements the IStateClient interface.
        if (defaultSkinUsesStates && currentSkin is IStateClient)
            IStateClient(currentSkin).currentState = stateName;

        // Show the new skin.
        if (currentSkin)
            currentSkin.visible = true;

        var labelColor:Number;

        if (enabled)
        {
            if (phase == ButtonPhase.OVER)
                labelColor = textField.getStyle("textRollOverColor");
            else if (phase == ButtonPhase.DOWN)
                labelColor = textField.getStyle("textSelectedColor");
            else
                labelColor = textField.getStyle("color");

            textField.setColor(labelColor);
        }
    }

    /**
     *  @private
     *  Gets the currentIconName (string) based on the Button's phase.
     */
    mx_internal function getCurrentIconName():String
    {
        var tempIconName:String;

        if (!enabled)
        {
            tempIconName = selected ?
                           selectedDisabledIconName :
                           disabledIconName;
        }
        else if (phase == ButtonPhase.UP)
        {
            tempIconName = selected ? selectedUpIconName : upIconName;
        }
        else if (phase == ButtonPhase.OVER)
        {
            tempIconName = selected ? selectedOverIconName : overIconName;
        }
        else if (phase == ButtonPhase.DOWN)
        {
            tempIconName = selected ? selectedDownIconName : downIconName;
        }
        
        return tempIconName;
    }


    /**
     *  @private
     *  gets the currentIcon based on the button.phase 
     */
    mx_internal function getCurrentIcon():IFlexDisplayObject
    {
        // Determine which icon will get displayed, based on whether this
        // Button is enabled or disabled, whether it is
        // selected or unselected, and how it is currently interacting
        // with the mouse (i.e., the up/over/down state).

        var tempIconName:String = getCurrentIconName(); 

        if (!tempIconName)
            return null;

        return viewIconForPhase(tempIconName);
    }   

    /**
     *  @private
     *  Displays one of the eight possible icons,
     *  creating it if it doesn't already exist.
     */
    mx_internal function viewIcon():void
    {
        // Determine which icon to display, based on whether this
        // Button is enabled or disabled, whether it is
        // selected or unselected, and how it is currently interacting
        // with the mouse (i.e., the up/over/down state).
        var tempIconName:String = getCurrentIconName();

        viewIconForPhase(tempIconName);
    }

    /**
     *  @private
     *  Displays one of the several possible icons,
     *  depending on the iconName and creating it if it
     *  doesn't already exist.
     */
    mx_internal function viewIconForPhase(tempIconName:String):IFlexDisplayObject
    {
        var newIconClass:Class = Class(getStyle(tempIconName));
        var newIcon:IFlexDisplayObject;
        
        if (!newIconClass)
        {
            newIconClass = Class(getStyle(iconName));
            
            // If we are using the default icon, then set use the default icon name
            if (defaultIconUsesStates)
                tempIconName = iconName;
            
            if (!checkedDefaultIcon && newIconClass)
            {
                newIcon = IFlexDisplayObject(new newIconClass());
                // Check if the icon class is a state client or a programmatic skin
                if (!(newIcon is IProgrammaticSkin) && newIcon is IStateClient)
                {
                    defaultIconUsesStates = true;
                    tempIconName = iconName; 
                }
                
                if (newIcon)
                    checkedDefaultIcon = true;
            }
        }
        
        // Has this icon already been created?
        newIcon = IFlexDisplayObject(getChildByName(tempIconName));
        // If not, create it.
        if (newIcon == null)
        {
            if (newIconClass != null)
            {
                newIcon = IFlexDisplayObject(new newIconClass());

                // Set its name so that we can find it in the future
                // using getChildByName().
                newIcon.name = tempIconName;

                if (newIcon is ISimpleStyleClient)
                    ISimpleStyleClient(newIcon).styleName = this;

                addChild(DisplayObject(newIcon));

                
                // If the skin is programmatic, and we've already been
                // initialized, update it now to avoid flicker.
                var sizeIcon:Boolean = false;
                if (newIcon is IInvalidating)
                {
                    IInvalidating(newIcon).validateNow();
                    sizeIcon = true;
                }
                else if (newIcon is IProgrammaticSkin)
                {
                    IProgrammaticSkin(newIcon).validateDisplayList();
                    sizeIcon = true;
                }
                if (newIcon && newIcon is IUIComponent)
                    IUIComponent(newIcon).enabled = enabled;              

                if (sizeIcon)
                    newIcon.setActualSize(newIcon.measuredWidth, newIcon.measuredHeight); 


                // Keep track of all icon children that have been created.
                icons.push(newIcon);
            }
        }
        
        // Hide the old icon.
        if (currentIcon != null)
            currentIcon.visible = false;

        // Keep track of which icon is current.
        currentIcon = newIcon;
        
        if (defaultIconUsesStates && currentIcon is IStateClient)
        {
            var stateName:String = "";
            
            if (!enabled)
                stateName = selected ? "selectedDisabled" : "disabled";
            else if (phase == ButtonPhase.UP)
                stateName = selected ? "selectedUp" : "up";
            else if (phase == ButtonPhase.OVER)
                stateName = selected ? "selectedOver" : "over";
            else if (phase == ButtonPhase.DOWN)
                stateName = selected ? "selectedDown" : "down";
            
            IStateClient(currentIcon).currentState = stateName;
        }

        // Show the new icon.
        if (currentIcon != null)
            currentIcon.visible = true;
            
        return newIcon;
    }
    
    private function previousVersion_layoutContents(unscaledWidth:Number,
                                        unscaledHeight:Number,
                                        offset:Boolean):void
    {
        var labelWidth:Number = 0;
        var labelHeight:Number = 0;

        var labelX:Number = 0;
        var labelY:Number = 0;

        var iconWidth:Number = 0;
        var iconHeight:Number = 0;

        var iconX:Number = 0;
        var iconY:Number = 0;

        var horizontalGap:Number = 2;
        var verticalGap:Number = 2;

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        var textWidth:Number = 0;
        var textHeight:Number = 0;

        var lineMetrics:TextLineMetrics;

        if (label)
        {
            lineMetrics = measureText(label);
            if (lineMetrics.width > 0)
            {
                textWidth = paddingLeft + paddingRight +
                            getStyle("textIndent") +  lineMetrics.width;
            }
            textHeight = lineMetrics.height;
        }
        else
        {
            lineMetrics = measureText("Wj");
            textHeight = lineMetrics.height;
        }

        var n:Number = offset ? buttonOffset : 0;

        var textAlign:String = getStyle("textAlign");

        var bm:EdgeMetrics = currentSkin &&
                             currentSkin is IRectangularBorder ?
                             IRectangularBorder(currentSkin).borderMetrics :
                             null;

        var viewWidth:Number = unscaledWidth;
        var viewHeight:Number = unscaledHeight - paddingTop - paddingBottom;

        if (bm)
        {
            viewWidth -= bm.left + bm.right;
            viewHeight -= bm.top + bm.bottom;
        }

        if (currentIcon)
        {
            iconWidth = currentIcon.width;
            iconHeight = currentIcon.height;
        }

        if (labelPlacement == ButtonLabelPlacement.LEFT ||
            labelPlacement == ButtonLabelPlacement.RIGHT)
        {
            horizontalGap = getStyle("horizontalGap");

            if (iconWidth == 0 || textWidth == 0)
                horizontalGap = 0;

            if (textWidth > 0)
            {
                textField.width = labelWidth = Math.max(viewWidth - iconWidth - horizontalGap -
                    paddingLeft - paddingRight, 0);
            }
            else
            {
                textField.width = labelWidth = 0;
            }
            textField.height = labelHeight =
                Math.min(viewHeight + 2, textHeight + UITextField.TEXT_HEIGHT_PADDING);

            if (labelPlacement == ButtonLabelPlacement.RIGHT)
            {
                labelX = iconWidth + horizontalGap;

                if (centerContent)
                {
                    if (textAlign == "left")
                    {
                        labelX += paddingLeft;
                    }
                    else if (textAlign == "right")
                    {
                        labelX += (viewWidth - labelWidth -
                                   iconWidth - horizontalGap - paddingLeft);
                    }
                    else // "center" -- default value
                    {
                        var disp:Number = (viewWidth - labelWidth -
                                   iconWidth - horizontalGap) / 2;
                        labelX += Math.max(disp, paddingLeft);
                    }
                }

                iconX = labelX - (iconWidth + horizontalGap);

                if (!centerContent)
                    labelX += paddingLeft;
            }
            else
            {
                labelX = viewWidth - labelWidth - iconWidth - horizontalGap -
                         paddingRight;

                if (centerContent)
                {
                    if (textAlign == "left")
                        labelX = 2;
                    else if (textAlign == "right")
                        labelX -= 1;
                    else if (labelX > 0) // "center" -- default value
                        labelX = labelX / 2;
                }

                iconX  = labelX + labelWidth + horizontalGap;
            }

            iconY  = labelY = 0;

            if (centerContent)
            {
                iconY  = Math.round((viewHeight - iconHeight) / 2) +
                    paddingTop;
                labelY = Math.round((viewHeight - labelHeight) / 2) +
                    paddingTop;
            }
            else
            {
                labelY += Math.max(0, (viewHeight - labelHeight) / 2) +
                    paddingTop;
                iconY += Math.max(0, (viewHeight - iconHeight) / 2 - 1) +
                    paddingTop;
            }
        }
        else
        {
            verticalGap = getStyle("verticalGap");

            if (iconHeight == 0 || textHeight == 0)
                verticalGap = 0;

            if (textWidth > 0)
            {
                textField.width = labelWidth =
                    Math.min(viewWidth, textWidth + UITextField.TEXT_WIDTH_PADDING);
                textField.height = labelHeight =
                    Math.min(viewHeight - iconHeight + 1, textHeight + 5);
            }
            else
            {
                textField.width = labelWidth = 0;
                textField.height = labelHeight = 0;
            }

            labelX = (viewWidth - labelWidth) / 2;

            iconX = (viewWidth - iconWidth) / 2;

            if (labelPlacement == ButtonLabelPlacement.TOP)
            {
                labelY = viewHeight - labelHeight - iconHeight - verticalGap;

                if (centerContent && labelY > 0)
                    labelY = labelY / 2;

                labelY += paddingTop;

                iconY = labelY + labelHeight + verticalGap - 3;
            }
            else
            {
                labelY = iconHeight + verticalGap + paddingTop;

                if (centerContent)
                {
                    labelY += (viewHeight - labelHeight -
                               iconHeight - verticalGap) / 2 + 1;
                }

                iconY = labelY - iconHeight - verticalGap + 3;
            }

        }
        var buffX:Number = n;
        var buffY:Number = n;

        if (bm)
        {
            buffX += bm.left;
            buffY += bm.top;
        }

        textField.x = labelX + buffX;
        textField.y = labelY + buffY;
        
        if (currentIcon)
        {
            iconX += buffX;
            iconY += buffY;

            // dispatch a move on behalf of the icon
            // the focus system uses that to adjust
            // focus rectangles
            var moveEvent:MoveEvent = new MoveEvent(MoveEvent.MOVE);
            moveEvent.oldX = currentIcon.x;
            moveEvent.oldY = currentIcon.y;

            currentIcon.x = Math.round(iconX);
            currentIcon.y = Math.round(iconY);
            currentIcon.dispatchEvent(moveEvent);
        }

        // The skins and icons get created on demand as the user interacts
        // with the Button, and as they are created they become the
        // frontmost child.
        // Here we ensure that the textField is the frontmost child,
        // with the current icon behind it and the current skin behind that.
        // Any other skins and icons are left behind these three,
        // with arbitrary layering.
        if (currentSkin)
            setChildIndex(DisplayObject(currentSkin), numChildren - 1);
        if (currentIcon)
            setChildIndex(DisplayObject(currentIcon), numChildren - 1);
        if (textField)
            setChildIndex(DisplayObject(textField), numChildren - 1);
    }

   /**
     *  @private
     *  Controls the layout of the icon and the label within the button.
     *  The text/icon are aligned based on the textAlign style setting.
     */
    mx_internal function layoutContents(unscaledWidth:Number,
                                        unscaledHeight:Number,
                                        offset:Boolean):void
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            previousVersion_layoutContents(unscaledWidth, unscaledHeight, offset);
            return;
        }
                
        var labelWidth:Number = 0;
        var labelHeight:Number = 0;

        var labelX:Number = 0;
        var labelY:Number = 0;

        var iconWidth:Number = 0;
        var iconHeight:Number = 0;

        var iconX:Number = 0;
        var iconY:Number = 0;

        var horizontalGap:Number = 0;
        var verticalGap:Number = 0;

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        var textWidth:Number = 0;
        var textHeight:Number = 0;

        var lineMetrics:TextLineMetrics;

        if (label)
        {
            lineMetrics = measureText(label);
            textWidth = lineMetrics.width + TEXT_WIDTH_PADDING;
            textHeight = lineMetrics.height + UITextField.TEXT_HEIGHT_PADDING;
        }
        else
        {
            lineMetrics = measureText("Wj");
            textHeight = lineMetrics.height + UITextField.TEXT_HEIGHT_PADDING;
        }

        var n:Number = offset ? buttonOffset : 0;

        var textAlign:String = getStyle("textAlign");

        var viewWidth:Number = unscaledWidth;
        var viewHeight:Number = unscaledHeight;

        var bm:EdgeMetrics = currentSkin &&
                             currentSkin is IBorder && !(currentSkin is IFlexAsset) ?
                             IBorder(currentSkin).borderMetrics :
                             null;

        if (bm)
        {
            viewWidth -= bm.left + bm.right;
            viewHeight -= bm.top + bm.bottom;
        }

        if (currentIcon)
        {
            iconWidth = currentIcon.width;
            iconHeight = currentIcon.height;
        }

        if (labelPlacement == ButtonLabelPlacement.LEFT ||
            labelPlacement == ButtonLabelPlacement.RIGHT)
        {
            horizontalGap = getStyle("horizontalGap");

            if (iconWidth == 0 || textWidth == 0)
                horizontalGap = 0;

            if (textWidth > 0)
            {
                textField.width = labelWidth = 
                    Math.max(Math.min(viewWidth - iconWidth - horizontalGap -
                                      paddingLeft - paddingRight, textWidth), 0);
            }
            else
            {
                textField.width = labelWidth = 0;
            }
            textField.height = labelHeight = Math.min(viewHeight, textHeight);

            if (textAlign == "left")
            {
                labelX += paddingLeft;
            }
            else if (textAlign == "right")
            {
                labelX += (viewWidth - labelWidth - iconWidth - 
                           horizontalGap - paddingRight);
            }
            else // "center" -- default value
            {
                labelX += ((viewWidth - labelWidth - iconWidth - 
                           horizontalGap - paddingLeft - paddingRight) / 2) + paddingLeft;
            }

            if (labelPlacement == ButtonLabelPlacement.RIGHT)
            {
                labelX += iconWidth + horizontalGap;
                iconX = labelX - (iconWidth + horizontalGap);
            }
            else
            {
                iconX  = labelX + labelWidth + horizontalGap; 
            }

            iconY  = ((viewHeight - iconHeight - paddingTop - paddingBottom) / 2) + paddingTop;
            labelY = ((viewHeight - labelHeight - paddingTop - paddingBottom) / 2) + paddingTop;
        }
        else
        {
            verticalGap = getStyle("verticalGap");

            if (iconHeight == 0 || label == "")
                verticalGap = 0;

            if (textWidth > 0)
            {
                textField.width = labelWidth = Math.max(viewWidth - paddingLeft - paddingRight, 0);
                textField.height = labelHeight =
                    Math.min(viewHeight - iconHeight - paddingTop - paddingBottom - verticalGap, textHeight);
            }
            else
            {
                textField.width = labelWidth = 0;
                textField.height = labelHeight = 0;
            }

            labelX = paddingLeft;

            if (textAlign == "left")
            {
                iconX += paddingLeft;
            }
            else if (textAlign == "right")
            {
                iconX += Math.max(viewWidth - iconWidth - paddingRight, paddingLeft);
            }
            else
            {
                iconX += ((viewWidth - iconWidth - paddingLeft - paddingRight) / 2) + paddingLeft;
            }

            if (labelPlacement == ButtonLabelPlacement.TOP)
            {
                labelY += ((viewHeight - labelHeight - iconHeight - 
                            paddingTop - paddingBottom - verticalGap) / 2) + paddingTop;
                iconY += labelY + labelHeight + verticalGap;
            }
            else
            {
                iconY += ((viewHeight - labelHeight - iconHeight - 
                            paddingTop - paddingBottom - verticalGap) / 2) + paddingTop;
                labelY += iconY + iconHeight + verticalGap;
            }

        }
        var buffX:Number = n;
        var buffY:Number = n;

        if (bm)
        {
            buffX += bm.left;
            buffY += bm.top;
        }

        textField.x = Math.round(labelX + buffX);
        textField.y = Math.round(labelY + buffY);

        if (currentIcon)
        {
            iconX += buffX;
            iconY += buffY;

            // dispatch a move on behalf of the icon
            // the focus system uses that to adjust
            // focus rectangles
            var moveEvent:MoveEvent = new MoveEvent(MoveEvent.MOVE);
            moveEvent.oldX = currentIcon.x;
            moveEvent.oldY = currentIcon.y;

            currentIcon.x = Math.round(iconX);
            currentIcon.y = Math.round(iconY);
            currentIcon.dispatchEvent(moveEvent);
        }

        // The skins and icons get created on demand as the user interacts
        // with the Button, and as they are created they become the
        // frontmost child.
        // Here we ensure that the textField is the frontmost child,
        // with the current icon behind it and the current skin behind that.
        // Any other skins and icons are left behind these three,
        // with arbitrary layering.
        if (currentSkin)
            setChildIndex(DisplayObject(currentSkin), numChildren - 1);
        if (currentIcon)
            setChildIndex(DisplayObject(currentIcon), numChildren - 1);
        if (textField)
            setChildIndex(DisplayObject(textField), numChildren - 1);
    }

    /**
     *  @private
     */
    mx_internal function changeSkins():void
    {
        var n:int = skins.length;
        for (var i:int = 0; i < n; i++)
        {
            removeChild(skins[i]);
        }
        skins = [];
        
        skinMeasuredWidth = NaN;
        skinMeasuredHeight = NaN;
        
        checkedDefaultSkin = false;
        defaultSkinUsesStates = false;
        
        if (initialized && FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        {
            viewSkin();
            invalidateSize();
        }
    }

    /**
     *  @private
     */
    mx_internal function changeIcons():void
    {
        var n:int = icons.length;
        for (var i:int = 0; i < n; i++)
        {
            removeChild(icons[i]);
        }
        icons = [];
        
        checkedDefaultIcon = false;
        defaultIconUsesStates = false;
    }

    /**
     *  @private
     */
    mx_internal function buttonPressed():void
    {
        phase = ButtonPhase.DOWN;
        
        dispatchEvent(new FlexEvent(FlexEvent.BUTTON_DOWN));

        if (autoRepeat)
        {
            autoRepeatTimer.delay = getStyle("repeatDelay");
            autoRepeatTimer.addEventListener(
                TimerEvent.TIMER, autoRepeatTimer_timerDelayHandler);
            autoRepeatTimer.start();
        }
    }

    /**
     *  @private
     */
    mx_internal function buttonReleased():void
    {
        // Remove the handlers that were added in mouseDownHandler().
        systemManager.removeEventListener(
            MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);
        systemManager.stage.removeEventListener(
            Event.MOUSE_LEAVE, stage_mouseLeaveHandler);
        
        if (autoRepeatTimer)
        {
            autoRepeatTimer.removeEventListener(
                TimerEvent.TIMER, autoRepeatTimer_timerDelayHandler);
            autoRepeatTimer.removeEventListener(
                TimerEvent.TIMER, autoRepeatTimer_timerHandler);
            autoRepeatTimer.reset();
        }
    }

    /**
     *  @private
     *  Some other components which use a Button as an internal
     *  subcomponent need access to its UITextField, but can't access the
     *  textField var because it is protected and therefore available
     *  only to subclasses.
     */
    mx_internal function getTextField():IUITextField
    {
        return textField;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        super.focusOutHandler(event);

        // Most of the time the system sends a rollout, but there are
        // situations where the mouse is over something else
        // that you don't get one so we force one here.
        if (phase != ButtonPhase.UP)
            phase = ButtonPhase.UP;
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (!enabled)
            return;

        if (event.keyCode == Keyboard.SPACE)
            buttonPressed();
    }

    /**
     *  @private
     */
    override protected function keyUpHandler(event:KeyboardEvent):void
    {
        if (!enabled)
            return;

        if (event.keyCode == Keyboard.SPACE)
        {
            buttonReleased();

            if (phase == ButtonPhase.DOWN)
                dispatchEvent(new MouseEvent(MouseEvent.CLICK));
            phase = ButtonPhase.UP;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /*

    Mouse interaction sequences that Button must handle:

    All start with Button in "up" phase, mouse outside Button,
    and mouse button up.

    Normal click:
        roll over Button -> "over" phase
        mouse down on Button -> "down" phase, dispatch "buttonDown"
        mouse up while over Button -> "over" phase, dispatch "click"
        roll out of Button -> "up" phase

    Click canceled:
        roll over Button -> "over" phase
        mouse down on Button -> "down" phase, dispatch "buttonDown"
        roll out of Button -> "over" phase
        maybe roll over and out of other objects -> dispatch events from them
        maybe roll off the stage, or off and back on
        mouse up while out of Button -> "up" phase
        if mouseup was over another Button, it goes into "over" phase

    Click resumed:
        roll over Button -> "over" phase
        mouse down on Button -> "down" phase, dispatch "buttonDown"
        roll out of Button -> "over" phase
        maybe roll over and out of other objects -> dispatch events from them
        roll over Button -> "down" phase
        maybe roll off the stage, or off and back on
        maybe repeat last four steps
        mouse up while over Button -> "over" phase, dispatch "click"
        roll out of Button -> "up" phase

    Drag over and out
        mouse down while out of Button
        roll over Button -> stay in "up" phase
        roll out of Button -> stay in "up" phase

    Drag over and up
        mouse down while out of Button
        roll over Button -> stay in "up" phase
        mouse up while over Button -> "over" phase
        continue with step 2 of first three sequences above

    */

    /**
     *  The default handler for the <code>MouseEvent.ROLL_OVER</code> event.
     *
     *  @param The event object.
     */
    protected function rollOverHandler(event:MouseEvent):void
    {
        
        // Note that we don't prevent the propagation of rollOver
        // from a disabled Button.
        // Developers may want to detect this low-level event.

        if (phase == ButtonPhase.UP)
        {
            if (event.buttonDown)
                return;

            phase = ButtonPhase.OVER;
            
            // Force a "render" event, which will cause updateDisplayList()
            // to show the appropriate skin for the new phase.
            event.updateAfterEvent();
        }

        else if (phase == ButtonPhase.OVER)
        {
            phase = ButtonPhase.DOWN;
            
            // Force a "render" event, which will cause updateDisplayList()
            // to show the appropriate skin for the new phase.
            event.updateAfterEvent();
            
            // The mouse is back over the Button and the Button is down again,
            // so resume auto-repeating.
            if (autoRepeatTimer)
                autoRepeatTimer.start();
        }
    }

    /**
     *  The default handler for the <code>MouseEvent.ROLL_OUT</code> event.
     *
     *  @param The event object.
     */
    protected function rollOutHandler(event:MouseEvent):void
    {

        // Note that we don't prevent the propagation of rollOut
        // from a disabled Button.
        // Developers may want to detect this low-level event.

        if (phase == ButtonPhase.OVER)
        {
            phase = ButtonPhase.UP;
            
            // Force a "render" event, which will cause updateDisplayList()
            // to show the appropriate skin for the new phase.
            event.updateAfterEvent();
        }

        else if (phase == ButtonPhase.DOWN && !stickyHighlighting)
        {
            phase = ButtonPhase.OVER;
            
            // Force a "render" event, which will cause updateDisplayList()
            // to show the appropriate skin for the new phase.
            event.updateAfterEvent();

            // If the Button no longer looks "down", it shouldn't auto-repeat.
            if (autoRepeatTimer)
                autoRepeatTimer.stop();
        }
    }

    /**
     *  The default handler for the <code>MouseEvent.MOUSE_DOWN</code> event.
     *
     *  @param The event object.
     */
    protected function mouseDownHandler(event:MouseEvent):void
    {
        if (!enabled)
            return;

        // Note that we don't prevent the propagation of mouseDown
        // from a disabled Button.
        // Developers may want to detect this low-level event.

        // In case the user drags out of the Button and then releases
        // the mouse button, we need to get the mouseUp.
        // To accomplish this, we temporarily place a capture-phase
        // mouseUp handler on the SystemManager.
        // We also place a mouseLeave handler on the stage
        // in case the user drags off the stage and releases the mouse.
        // These handlers are removed in buttonReleased().
        systemManager.addEventListener(
            MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);
        systemManager.stage.addEventListener(
            Event.MOUSE_LEAVE, stage_mouseLeaveHandler);

        buttonPressed();

        // Force a "render" event, which will cause updateDisplayList()
        // to show the appropriate skin for the new phase.
        event.updateAfterEvent();
    }

    /**
     *  The default handler for the <code>MouseEvent.MOUSE_UP</code> event.
     *
     *  @param The event object.
     */
    protected function mouseUpHandler(event:MouseEvent):void
    {
        if (!enabled)
            return;

        // Note that we don't prevent the propagation of mouseUp
        // from a disabled Button.
        // Developers may want to detect this low-level event.
        phase = ButtonPhase.OVER;
        buttonReleased();
        
        // Force a "render" event, which will cause updateDisplayList()
        // to show the appropriate skin for the new phase.    
        if (!toggle)
            event.updateAfterEvent(); 
    }

    /**
     *  The default handler for the <code>MouseEvent.CLICK</code> event.
     *
     *  @param The event object.
     */
    protected function clickHandler(event:MouseEvent):void
    {
        if (!enabled)
        {
            // Prevent the propagation of click from a disabled Button.
            // This is conceptually a higher-level event and
            // developers will expect their click handlers not to fire
            // if the Button is disabled.
            event.stopImmediatePropagation();
            return;
        }

        if (toggle)
        {
            setSelected(!selected);
            event.updateAfterEvent(); 
        }
        
          
    }

    /**
     *  @private
     *  This method is called when the user has pressed the Button
     *  and then released the mouse button anywhere.
     *  It's purpose is to get the mouseUp event when the user has
     *  dragged out of the Button before releasing.
     *  However, it gets an inside mouseUp as well;
     *  we have to check for this case becuase mouseHandler()
     *  already deals with it..
     */
    private function systemManager_mouseUpHandler(event:MouseEvent):void
    {
        // If the mouse button was released over the Button,
        // mouseUpHandler() will handle it, so do nothing. 
        if (contains(DisplayObject(event.target)))
            return;

        phase = ButtonPhase.UP;
        buttonReleased();

        // Force a "render" event, which will cause updateDisplayList()
        // to show the appropriate skin for the new phase.
        event.updateAfterEvent();
    }

    /**
     *  @private
     *  This method is called when the user has pressed the Button,
     *  dragged of the stage, and released the mouse button.
     */
    private function stage_mouseLeaveHandler(event:Event):void
    {
        phase = ButtonPhase.UP;
        buttonReleased();
    }

    /**
     *  @private
     */
    private function autoRepeatTimer_timerDelayHandler(event:Event):void
    {
        if (!enabled)
            return;

        dispatchEvent(new FlexEvent(FlexEvent.BUTTON_DOWN));

        if (autoRepeat)
        {
            autoRepeatTimer.reset();
            autoRepeatTimer.removeEventListener(
                TimerEvent.TIMER, autoRepeatTimer_timerDelayHandler);
            autoRepeatTimer.delay = getStyle("repeatInterval");
            autoRepeatTimer.addEventListener(
                TimerEvent.TIMER, autoRepeatTimer_timerHandler);
            autoRepeatTimer.start();
        }
    }

    /**
     *  @private
     */
    private function autoRepeatTimer_timerHandler(event:Event):void
    {
        if (!enabled)
            return;

        dispatchEvent(new FlexEvent(FlexEvent.BUTTON_DOWN));
    }
}

}
