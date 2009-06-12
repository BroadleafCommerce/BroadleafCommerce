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

import flash.accessibility.AccessibilityProperties;
import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.TextEvent;
import flash.system.IME;
import flash.system.IMEConversionMode;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFieldType;
import flash.text.TextFormat;
import flash.text.TextLineMetrics;
import flash.ui.Keyboard;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IIMESupport;
import mx.core.IInvalidating;
import mx.core.IRectangularBorder;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when text in the TextInput control changes
 *  through user input.
 *  This event does not occur if you use data binding or 
 *  ActionScript code to change the text.
 *
 *  <p>Even though the default value of the <code>Event.bubbles</code> property 
 *  is <code>true</code>, this control dispatches the event with 
 *  the <code>Event.bubbles</code> property set to <code>false</code>.</p>
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

/**
 *  Dispatched when the user presses the Enter key.
 *
 *  @eventType mx.events.FlexEvent.ENTER
 */
[Event(name="enter", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the user types, deletes, or pastes text into the control.
 *
 *  <p>Even though the default value of the <code>TextEvent.bubbles</code> property 
 *  is <code>true</code>, this control dispatches the event with 
 *  the <code>TextEvent.bubbles</code> property set to <code>false</code>.</p>
 *
 *  @eventType flash.events.TextEvent.TEXT_INPUT
 */
[Event(name="textInput", type="flash.events.TextEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/BorderStyles.as"
include "../styles/metadata/FocusStyles.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Number of pixels between the component's bottom border
 *  and the bottom edge of its content area.
 *
 *  @default 0
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the component's top border
 *  and the top edge of its content area.
 *  
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DataBindingInfo("editEvents", "&quot;focusIn;focusOut&quot;")]

[DefaultBindingProperty(source="text", destination="text")]

[DefaultTriggerEvent("change")]

[IconFile("TextInput.png")]

[ResourceBundle("controls")]
    
/**
 *  The TextInput control is a single-line text field
 *  that is optionally editable.
 *  All text in this control must use the same styling
 *  unless it is HTML text.
 *  The TextInput control supports the HTML rendering
 *  capabilities of Flash Player and AIR.
 *
 *  <p>TextInput controls do not include a label, although you
 *  can add one by using a Label control or by nesting the
 *  TextInput control in a FormItem control in a Form container.
 *  When used in a FormItem control, a TextInput control
 *  indicates whether a value is required.
 *  TextInput controls have a number of states, including filled,
 *  selected, disabled, and error.
 *  TextInput controls support formatting, validation, and keyboard
 *  equivalents; they also dispatch change and enter events.</p>
 *
 *  <p>If you disable a TextInput control, it displays its contents
 *  in the color specified by the <code>disabledColor</code>
 *  style.
 *  To disallow editing the text, you set the <code>editable</code>
 *  property to <code>false</code>.
 *  To conceal the input text by displaying asterisks instead of the
 *  characters entered, you set the <code>displayAsPassword</code> property
 *  to <code>true</code>.</p>
 * 
 *  <p>The TextInput control is used as a subcomponent in several other controls,
 *  such as the RichTextEditor, NumericStepper, and ComboBox controls. As a result,
 *  if you assign style properties to a TextInput control by using a CSS type selector, 
 *  Flex applies those styles to the TextInput when it appears in the other controls 
 *  unless you explicitly override them.</p>
 *
 *  <p>The TextInput control has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>The size of the text with a default minimum size of 22 pixels high and 160 pixels wide</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>10000 by 10000 pixels</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:TextInput&gt;</code> tag inherits the attributes
 *  of its superclass and adds the following attributes:</p>
 *
 *  <pre>
 *  &lt;mx:TextInput
 *    <b>Properties</b>
 *    condenseWhite="false|true"
 *    data="undefined"
 *    displayAsPassword="false|true"
 *    editable="true|false"
 *    horizontalScrollPosition="0"
 *    htmlText=""
 *    imeMode="null"
 *    length="0"
 *    listData="null"
 *    maxChars="0"
 *    restrict="null"
 *    selectionBeginIndex="0"
 *    selectionEndIndex="0"
 *    text=""
 *    textHeight="0"
 *    textWidth="0"
 *    &nbsp;
 *    <b>Styles</b>
 *    backgroundAlpha="1.0"
 *    backgroundColor="undefined"
 *    backgroundImage="undefined"
 *    backgroundSize="auto"
 *    borderColor="0xAAB3B3"
 *    borderSides="left top right bottom"
 *    borderSkin="mx.skins.halo.HaloBorder"
 *    borderStyle="inset"
 *    borderThickness="1"
 *    color="0x0B333C"
 *    cornerRadius="0"
 *    disabledColor="0xAAB3B3"
 *    dropShadowColor="0x000000"
 *    dropShadowEnabled="false"
 *    focusAlpha="0.5"
 *    focusRoundedCorners"tl tr bl br"
 *    fontAntiAliasType="advanced|normal"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel|none|subpixel"
 *    fontSharpness="0"
 *    fontSize="10"
 *    fontStyle="normal|italic"
 *    fontThickness="0"
 *    fontWeight="normal|bold"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    shadowDirection="center"
 *    shadowDistance="2"
 *    textAlign="left|right|center"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    &nbsp;
 *    <b>Events</b>
 *    change="<i>No default</i>"
 *    dataChange="<i>No default</i>"
 *    enter="<i>No default</i>"
 *    textInput="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/TextInputExample.mxml
 *
 *  @see mx.controls.Label
 *  @see mx.controls.Text
 *  @see mx.controls.TextArea
 *  @see mx.controls.RichTextEditor
 *  @see mx.controls.textClasses.TextRange
 *
 *  @helpid 3188
 *  @tiptext TextInput is a single-line, editable text field.
 */
public class TextInput extends UIComponent
                       implements IDataRenderer, IDropInListItemRenderer,
                       IFocusManagerComponent, IIMESupport, IListItemRenderer,
                       IFontContextComponent

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
    public function TextInput()
    {
        super();

        // InteractiveObject variables.
        tabChildren = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  The internal subcontrol that draws the border and background.
     */
    mx_internal var border:IFlexDisplayObject;

    /**
     *  @private
     *  Flag that will block default data/listData behavior.
     */
    private var textSet:Boolean;

    /**
     *  @private
     */    
    private var selectionChanged:Boolean = false;

    /**
     *  @private
     *  If true, pass calls to drawFocus() up to the parent.
     *  This is used when a TextInput is part of a composite control
     *  like NumericStepper or ComboBox;
     */
    mx_internal var parentDrawsFocus:Boolean = false;

    /**
     *  @private
     *  Previous imeMode.
     */
    private var prevMode:String = null;

    /**
     *  @private
     */    
    private var errorCaught:Boolean = false;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  accessibilityProperties
    //----------------------------------

    /**
     *  @private
     *  Storage for the accessibilityProperties property.
     */
    private var _accessibilityProperties:AccessibilityProperties;

    /**
     *  @private
     */
    private var accessibilityPropertiesChanged:Boolean = false;

    /**
     *  @private
     *  Storage for the accessibilityProperties property.
     */
    override public function get accessibilityProperties():
                                            AccessibilityProperties
    {
        return _accessibilityProperties;
    }

    /**
     *  @private
     *  Accessibility data.
     *
     *  @tiptext
     *  @helpid 3199
     */
    override public function set accessibilityProperties(
                                        value:AccessibilityProperties):void
    {
        if (value == _accessibilityProperties)
            return;

        _accessibilityProperties = value;
        accessibilityPropertiesChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of a TextInput is calculated for its textField.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            var t:String = text;
            if (t == "")
                t = " ";
    
            return (border && border is IRectangularBorder ?
                    IRectangularBorder(border).borderMetrics.top :
                    0)  + measureText(t).ascent;
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
     *  Disable TextField when we're disabled.
     */
    override public function set enabled(value:Boolean):void
    {
        if (value == enabled)
            return;

        super.enabled = value;
        enabledChanged = true;

        invalidateProperties();
        
        if (border && border is IInvalidating)
            IInvalidating(border).invalidateDisplayList();
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
    //  tabIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the tabIndex property.
     */
    private var _tabIndex:int = -1;

    /**
     *  @private
     */
    private var tabIndexChanged:Boolean = false;

    /**
     *  @private
     *  Tab order in which the control receives the focus when navigating
     *  with the Tab key.
     *
     *  @default -1
     *  @tiptext tabIndex of the component
     *  @helpid 3198
     */
    override public function get tabIndex():int
    {
        return _tabIndex;
    }

    /**
     *  @private
     */
    override public function set tabIndex(value:int):void
    {
        if (value == _tabIndex)
            return;

        _tabIndex = value;
        tabIndexChanged = true;

        invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  condenseWhite
    //----------------------------------

    /**
     *  @private
     */
    private var _condenseWhite:Boolean = false;

    /**
     *  @private
     */
    private var condenseWhiteChanged:Boolean = false;

    [Bindable("condenseWhiteChanged")]
    [Inspectable(category="General", defaultValue="")]
    
    /**
     *  Specifies whether extra white space (spaces, line breaks,
     *  and so on) should be removed in a TextInput control with HTML text.
     *
     *  <p>The <code>condenseWhite</code> property affects only text set with
     *  the <code>htmlText</code> property, not the <code>text</code> property.
     *  If you set text with the <code>text</code> property,
     *  <code>condenseWhite</code> is ignored.</p>
     *
     *  <p>If you set the <code>condenseWhite</code> property to <code>true</code>,
     *  you must use standard HTML commands, such as <code>&lt;br&gt;</code>
     *  and <code>&lt;p&gt;</code>, to place line breaks in the text field.</p>
     *
     *  @default false;
     */
    public function get condenseWhite():Boolean
    {
        return _condenseWhite;
    }

    /**
     *  @private
     */
    public function set condenseWhite(value:Boolean):void
    {
        if (value == _condenseWhite)
            return;

        _condenseWhite = value;
        condenseWhiteChanged = true;

        // Changing the condenseWhite property needs to trigger
        // the same response as changing the htmlText property
        // if this TextArea is displaying HTML.
        if (isHTML)
            htmlTextChanged = true;         

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("condenseWhiteChanged"));
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
     *  Lets you pass a value to the component
     *  when you use it in an item renderer or item editor.
     *  You typically use data binding to bind a field of the <code>data</code>
     *  property to a property of this component.
     *
     *  <p>When you use the control as a drop-in item renderer or drop-in
     *  item editor, Flex automatically writes the current value of the item
     *  to the <code>text</code> property of this control.</p>
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
        var newText:*;

        _data = value;

        if (_listData)
        {
            newText = _listData.label;
        }
        else if (_data != null)
        {
            if (_data is String)
                newText = String(_data);
            else
                newText = _data.toString();
        }

        if (newText !== undefined && !textSet)
        {
            text = newText;
            textSet = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  displayAsPassword
    //----------------------------------

    /**
     *  @private
     *  Storage for the displayAsPassword property.
     */
    private var _displayAsPassword:Boolean = false;

    /**
     *  @private
     */
    private var displayAsPasswordChanged:Boolean = false;

    [Bindable("displayAsPasswordChanged")]
    [Inspectable(category="General", defaultValue="false")]

    /**
     *  Indicates whether this control is used for entering passwords.
     *  If <code>true</code>, the field does not display entered text,
     *  instead, each text character entered into the control
     *  appears as the  character "&#42;".
     *
     *  @default false
     *  @tiptext Specifies whether to display '*'
     *  instead of the actual characters
     *  @helpid 3197
     */
    public function get displayAsPassword():Boolean
    {
        return _displayAsPassword;
    }

    /**
     *  @private
     */
    public function set displayAsPassword(value:Boolean):void
    {
        if (value == _displayAsPassword)
            return;

        _displayAsPassword = value;
        displayAsPasswordChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("displayAsPasswordChanged"));
    }

    //----------------------------------
    //  editable
    //----------------------------------

    /**
     *  @private
     *  Storage for the editable property.
     */
    private var _editable:Boolean = true;

    /**
     *  @private
     */
    private var editableChanged:Boolean = false;

    [Bindable("editableChanged")]
    [Inspectable(category="General", defaultValue="true")]

    /**
     *  Indicates whether the user is allowed to edit the text in this control.
     *  If <code>true</code>, the user can edit the text.
     *
     *  @default true
     * 
     *  @tiptext Specifies whether the component is editable or not
     *  @helpid 3196
     */
    public function get editable():Boolean
    {
        return _editable;
    }

    /**
     *  @private
     */
    public function set editable(value:Boolean):void
    {
        if (value == _editable)
            return;

        _editable = value;
        editableChanged = true;

        invalidateProperties();

        dispatchEvent(new Event("editableChanged"));
    }

    //----------------------------------
    //  horizontalScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Used to store the init time value if any.
     */
    private var _horizontalScrollPosition:Number = 0;

    /**
     *  @private
     */
    private var horizontalScrollPositionChanged:Boolean = false;

    [Bindable("horizontalScrollPositionChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  Pixel position in the content area of the leftmost pixel
     *  that is currently displayed. 
     *  (The content area includes all contents of a control, not just 
     *  the portion that is currently displayed.)
     *  This property is always set to 0, and ignores changes,
     *  if <code>wordWrap</code> is set to <code>true</code>.
     * 
     *  @default 0

     *  @tiptext The pixel position of the left-most character
     *  that is currently displayed
     *  @helpid 3194
     */
    public function get horizontalScrollPosition():Number
    {
        return _horizontalScrollPosition;
    }

    /**
     *  @private
     */
    public function set horizontalScrollPosition(value:Number):void
    {
        if (value == _horizontalScrollPosition)
            return;

        _horizontalScrollPosition = value;
        horizontalScrollPositionChanged = true;

        invalidateProperties();

        dispatchEvent(new Event("horizontalScrollPositionChanged"));
    }

    //----------------------------------
    //  htmlText
    //----------------------------------

    /**
     *  @private
     *  Storage for the htmlText property.
     *  In addition to being set in the htmlText setter,
     *  it is automatically updated at two other times.
     *  1. When the 'text' or 'htmlText' is pushed down into
     *  the textField in commitProperties(), this causes
     *  the textField to update its own 'htmlText'.
     *  Therefore in commitProperties() we reset this storage var
     *  to be in sync with the textField.
     *  2. When the TextFormat of the textField changes
     *  because a CSS style has changed (see validateNow()
     *  in UITextField), the textField also updates its own 'htmlText'.
     *  Therefore in textField_textFieldStyleChangeHandler()
     */
    private var _htmlText:String = "";

    /**
     *  @private
     */
    private var htmlTextChanged:Boolean = false;

    /**
     *  @private
     *  The last value of htmlText that was set.
     *  We have to keep track of this because when you set the htmlText
     *  of a TextField and read it back, you don't get what you set.
     *  In general it will have additional HTML markup corresponding
     *  to the defaultTextFormat set from the CSS styles.
     *  If this var is null, it means that 'text' rather than 'htmlText'
     *  was last set.
     */
    private var explicitHTMLText:String = null; 

    [Bindable("htmlTextChanged")]
    [CollapseWhiteSpace]
    [Inspectable(category="General", defaultValue="")]
    [NonCommittingChangeEvent("change")]

   /**
     *  Specifies the text displayed by the TextInput control, including HTML markup that
     *  expresses the styles of that text.
     *  When you specify HTML text in this property, you can use the subset of HTML 
     *  tags that is supported by the Flash TextField control.
     * 
     *  <p> When you set this property, the HTML markup is applied
     *  after the CSS styles for the TextInput instance are applied.
     *  When you get this property, the HTML markup includes
     *  the CSS styles.</p>
     *  
     *  <p>For example, if you set this to be a string such as,
     *  <code>"This is an example of &lt;b&gt;bold&lt;/b&gt; markup"</code>,
     *  the text "This is an example of <b>bold</b> markup" appears
     *  in the TextInput with whatever CSS styles normally apply.
     *  Also, the word "bold" appears in boldface font because of the
     *  <code>&lt;b&gt;</code> markup.</p>
     *
     *  <p>HTML markup uses characters such as &lt; and &gt;,
     *  which have special meaning in XML (and therefore in MXML). So,  
     *  code such as the following does not compile:</p>
     *  
     *  <pre>
     *  &lt;mx:TextInput htmlText="This is an example of &lt;b&gt;bold&lt;/b&gt; markup"/&gt;
     *  </pre>
     *  
     *  <p>There are three ways around this problem.</p>
     *  
     *  <ul>
     *  
     *  <li>
     *  
     *  <p>Set the <code>htmlText</code> property in an ActionScript method called as 
     *  an <code>initialize</code> handler:</p>
     *  
     *  <pre>
     *  &lt;mx:TextInput id="myTextInput" initialize="myTextInput_initialize()"/&gt;
     *  </pre>
     *  
     *  <p>where the <code>myTextInput_initialize</code> method is in a script CDATA section:</p>
     *  
     *  <pre>
     *  &lt;mx:Script&gt;
     *  &lt;![CDATA[
     *  private function myTextInput_initialize():void {
     *      myTextInput.htmlText = "This is an example of &lt;b&gt;bold&lt;/b&gt; markup";
     *  }
     *  ]]&gt;
     *  &lt;/mx:Script&gt;
     *  
     *  </pre>
     *  
     *  <p>This is the simplest approach because the HTML markup
     *  remains easily readable.
     *  Notice that you must assign an <code>id</code> to the TextInput
     *  so you can refer to it in the <code>initialize</code>
     *  handler.</p>
     *  
     *  </li>
     *  
     *  <li>
     *  
     *  <p>Specify the <code>htmlText</code> property by using a child tag
     *  with a CDATA section. A CDATA section in XML contains character data
     *  where characters like &lt; and &gt; aren't given a special meaning.</p>
     *  
     *  <pre>
     *  &lt;mx:TextInput&gt;
     *      &lt;mx:htmlText&gt;&lt;![CDATA[This is an example of &lt;b&gt;bold&lt;/b&gt; markup]]&gt;&lt;/mx:htmlText&gt;
     *  &lt;mx:TextInput/&gt;
     *  </pre>
     *  
     *  <p>You must write the <code>htmlText</code> property as a child tag
     *  rather than as an attribute on the <code>&lt;mx:TextInput&gt;</code> tag
     *  because XML doesn't allow CDATA for the value of an attribute.
     *  Notice that the markup is readable, but the CDATA section makes 
     *  this approach more complicated.</p>
     *  
     *  </li>
     *  
     *  <li>
     *  
     *  <p>Use an <code>hmtlText</code> attribute where any occurences
     *  of the HTML markup characters &lt; and &gt; in the attribute value
     *  are written instead as the XML "entities" <code>&amp;lt;</code>
     *  and <code>&amp;gt;</code>:</p>
     *  
     *  <pre>
     *  &lt;mx:TextInput htmlText="This is an example of &amp;lt;b&amp;gt;bold&amp;lt;/b&amp;gt; markup"/&amp;gt;
     *  </pre>
     *  
     *  Adobe does not recommend this approach because the HTML markup becomes
     *  nearly impossible to read.
     *  
     *  </li>
     *  
     *  </ul>
     *  
     *  <p>If the <code>condenseWhite</code> property is <code>true</code> 
     *  when you set the <code>htmlText</code> property, multiple
     *  white-space characters are condensed, as in HTML-based browsers;
     *  for example, three consecutive spaces are displayed
     *  as a single space.
     *  The default value for <code>condenseWhite</code> is
     *  <code>false</code>, so you must set <code>condenseWhite</code>
     *  to <code>true</code> to collapse the white space.</p>
     *  
     *  <p>If you read back the <code>htmlText</code> property quickly
     *  after setting it, you get the same string that you set.
     *  However, after the LayoutManager runs, the value changes
     *  to include additional markup that includes the CSS styles.</p>
     *  
     *  <p>Setting the <code>htmlText</code> property affects the <code>text</code>
     *  property in several ways. 
     *  If you read the <code>text</code> property quickly after setting
     *  the <code>htmlText</code> property, you get <code>null</code>,
     *  which indicates that the <code>text</code> corresponding to the new
     *  <code>htmlText</code> has not yet been determined.
     *  However, after the LayoutManager runs, the <code>text</code> property 
     *  value changes to the <code>htmlText</code> string with all the 
     *  HTML markup removed; that is,
     *  the value is the characters that the TextInput actually displays.</p>
     *   
     *  <p>Conversely, if you set the <code>text</code> property,
     *  any previously set <code>htmlText</code> is irrelevant.
     *  If you read the <code>htmlText</code> property quickly after setting
     *  the <code>text</code> property, you get <code>null</code>,
     *  which indicates that the <code>htmlText</code> that corresponds to the new
     *  <code>text</code> has not yet been determined.
     *  However, after the LayoutManager runs, the <code>htmlText</code> property 
     *  value changes to the new text plus the HTML markup for the CSS styles.</p>
     *
     *  <p>To make the LayoutManager run immediately, you can call the
     *  <code>validateNow()</code> method on the TextInput.
     *  For example, you could set some <code>htmlText</code>,
     *  call the <code>validateNow()</code> method, and immediately
     *  obtain the corresponding <code>text</code> that doesn't have
     *  the HTML markup.</p>
     *  
     *  <p>If you set both <code>text</code> and <code>htmlText</code> properties 
     *  in ActionScript, whichever is set last takes effect.
     *  Do not set both in MXML, because MXML does not guarantee that
     *  the properties of an instance get set in any particular order.</p>
     *  
     *  <p>Setting either <code>text</code> or <code>htmlText</code> property
     *  inside a loop is a fast operation, because the underlying TextField
     *  that actually renders the text is not updated until
     *  the LayoutManager runs.</p>
     *
     *  <p>If you try to set this property to <code>null</code>,
     *  it is set, instead, to the empty string.
     *  If the property temporarily has the value <code>null</code>,
     *  it indicates that the <code>text</code> has been recently set
     *  and the corresponding <code>htmlText</code>
     *  has not yet been determined.</p>
     *  
     *  @default ""
     * 
     *  @see flash.text.TextField#htmlText
     */
    public function get htmlText():String
    {
        return _htmlText;
    }

    /**
     *  @private
     */
    public function set htmlText(value:String):void
    {
        textSet = true;

        // The htmlText property can't be set to null,
        // only to the empty string, because if you set the htmlText
        // of a TextField to null it throws an RTE.
        // If the getter returns null, it means that 'text' was just set
        // and the value of 'htmlText' isn't yet known, because the 'text'
        // hasn't been committed into the textField and the 'htmlText'
        // hasn't yet been read back out of the textField.
        if (!value)
            value = "";

        _htmlText = value;
        htmlTextChanged = true;

        // The text property is unknown until commitProperties(),
        // when we push the htmlText into the TextField and it
        // calculates the text.
        // But you can call validateNow() to make this happen right away.
        _text = null;
        
        explicitHTMLText = value;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();

        // Trigger bindings to htmlText.
        dispatchEvent(new Event("htmlTextChanged"));

        // commitProperties() will dispatch a "valueCommit" event
        // after the TextField determines the 'text' based on the
        // 'htmlText'; this event will trigger any bindings to 'text'.
    }

    //----------------------------------
    //  imeMode
    //----------------------------------

    /**
     *  @private
     */
    private var _imeMode:String = null;

    /**
     *  Specifies the IME (input method editor) mode.
     *  The IME enables users to enter text in Chinese, Japanese, and Korean.
     *  Flex sets the specified IME mode when the control gets the focus,
     *  and sets it back to the previous value when the control loses the focus.
     *
     *  <p>The flash.system.IMEConversionMode class defines constants for the
     *  valid values for this property.
     *  You can also specify <code>null</code> to specify no IME.</p>
     *
     *  @default null
     * 
     *  @see flash.system.IMEConversionMode
     */
    public function get imeMode():String
    {
        return _imeMode;
    }

    /**
     *  @private
     */
    public function set imeMode(value:String):void
    {
        _imeMode = value;
        // We don't call IME.conversionMode here. We call it
        // only on focusIn. Thus fringe cases like setting
        // imeMode dynamically without moving focus, through
        // keyboard events, wouldn't change the mode. Also
        // getting imeMode asynch. from the server which gets
        // delayed and set later after focusIn is not handled
        // as having the text partly in one script and partly
        // in another is not desirable.
    }

    //----------------------------------
    //  isHTML
    //----------------------------------

    /**
     *  @private
     */
    private function get isHTML():Boolean
    {
        return explicitHTMLText != null;
    }

    //----------------------------------
    //  length
    //----------------------------------

    /**
     *  The number of characters of text displayed in the TextArea.
     *
     *  @default 0
     *  @tiptext The number of characters in the TextInput.
     *  @helpid 3192
     */
    public function get length():int
    {
        return text != null ? text.length : -1;
    }

    //----------------------------------
    //  listData
    //----------------------------------

    private var _listData:BaseListData;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  When a component is used as a drop-in item renderer or drop-in
     *  item editor, Flex initializes the <code>listData</code> property
     *  of the component with the appropriate data from the list control.
     *  The component can then use the <code>listData</code> property
     *  to initialize the <code>data</code> property of the drop-in
     *  item renderer or drop-in item editor.
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
    //  maxChars
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxChars property.
     */
    private var _maxChars:int = 0;

    /**
     *  @private
     */
    private var maxCharsChanged:Boolean = false;

    [Bindable("maxCharsChanged")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Maximum number of characters that users can enter in the text field.
     *  This property does not limit the length of text specified by the
     *  setting the control's <code>text</code> or <code>htmlText</code> property.
     * 
     *  <p>The default value is 0, which is a special case
     *  meaning an unlimited number.</p>
     *
     *  @tiptext The maximum number of characters
     *  that the TextInput can contain
     *  @helpid 3191
     */
    public function get maxChars():int
    {
        return _maxChars;
    }

    /**
     *  @private
     */
    public function set maxChars(value:int):void
    {
        if (value == _maxChars)
            return;

        _maxChars = value;
        maxCharsChanged = true;

        invalidateProperties();

        dispatchEvent(new Event("maxCharsChanged"));
    }

    //----------------------------------
    //  maxHorizontalScrollPosition
    //----------------------------------

    /**
     *  @private 
     *  Maximum value of <code>horizontalScrollPosition</code>.
     * 
     *  <p>The default value is 0, which means that horizontal scrolling is not 
     *  required.</p>
     *
     *  <p>The value of the <code>maxHorizontalScrollPosition</code> property is
     *  computed from the data and size of component, and must not be set by
     *  the application code.</p>
     */
    public function get maxHorizontalScrollPosition():Number
    {
        return textField ? textField.maxScrollH : 0;
    }

    //----------------------------------
    //  restrict
    //----------------------------------

    /**
     *  @private
     *  Storage for the restrict property.
     */
    private var _restrict:String;

    /**
     *  @private
     */
    private var restrictChanged:Boolean = false;

    [Bindable("restrictChanged")]
    [Inspectable(category="General")]

    /**
     *  Indicates the set of characters that a user can enter into the control. 
     *  If the value of the <code>restrict</code> property is <code>null</code>, 
     *  you can enter any character. If the value of the <code>restrict</code> 
     *  property is an empty string, you cannot enter any character.
     *  This property only restricts user interaction; a script
     *  can put any text into the text field. If the value of
     *  the <code>restrict</code> property is a string of characters,
     *  you may enter only characters in that string into the
     *  text field.
     *
     *  <p>Flex scans the string from left to right. You can specify a range by 
     *  using the hyphen (-) character.
     *  If the string begins with a caret (^) character, all characters are 
     *  initially accepted and succeeding characters in the string are excluded 
     *  from the set of accepted characters. If the string does not begin with a 
     *  caret (^) character, no characters are initially accepted and succeeding 
     *  characters in the string are included in the set of accepted characters.</p>
     * 
     *  <p>Because some characters have a special meaning when used
     *  in the <code>restrict</code> property, you must use
     *  backslash characters to specify the literal characters -, &#094;, and \.
     *  When you use the <code>restrict</code> property as an attribute
     *  in an MXML tag, use single backslashes, as in the following 
     *  example: \&#094;\-\\.
     *  When you set the <code>restrict</code> In and ActionScript expression,
     *  use double backslashes, as in the following example: \\&#094;\\-\\\.</p>
     *
     *  @default null
     *  @see flash.text.TextField#restrict
     *  @tiptext The set of characters that may be entered
     *  into the TextInput.
     *  @helpid 3193
     */
    public function get restrict():String
    {
        return _restrict;
    }

    /**
     *  @private
     */
    public function set restrict(value:String):void
    {
        if (value == _restrict)
            return;
        
        _restrict = value;
        restrictChanged = true;

        invalidateProperties();

        dispatchEvent(new Event("restrictChanged"));
    }

    //----------------------------------
    //  selectable
    //----------------------------------    

    /**
     *  @private
     *  Used to make TextInput function correctly in the components that use it
     *  as a subcomponent. ComboBox, at this point. 
     */
    private var _selectable:Boolean = true;
    
    /**
     *  @private
     */
    private var selectableChanged:Boolean = false;
    
    /**
     *  @private
     */ 
    mx_internal function get selectable():Boolean
    {
        return _selectable;
    }
    
    /**
     *  @private
     */
    mx_internal function set selectable(value:Boolean):void
    {
        if (_selectable == value)
            return;
        _selectable = value;
        selectableChanged = true;
        invalidateProperties();
    }
    //----------------------------------
    //  selectionBeginIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectionBeginIndex property.
     */
    private var _selectionBeginIndex:int = 0;

    [Inspectable(defaultValue="0")]

    /**
     *  The zero-based character index value of the first character
     *  in the current selection.
     *  For example, the first character is 0, the second character is 1,
     *  and so on.
     *  When the control gets the focus, the selection is visible if the 
     *  <code>selectionBeginIndex</code> and <code>selectionEndIndex</code>
     *  properties are both set.
     *
     *  @default 0
     * 
     *  @tiptext The zero-based index value of the first character
     *  in the selection.
     */
    public function get selectionBeginIndex():int
    {
        return textField ?
               textField.selectionBeginIndex :
               _selectionBeginIndex;
    }

    /**
     *  @private
     */
    public function set selectionBeginIndex(value:int):void
    {
        _selectionBeginIndex = value;
        selectionChanged = true;

        invalidateProperties(); 
    }

    //----------------------------------
    //  selectionEndIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectionEndIndex property.
     */
    private var _selectionEndIndex:int = 0;

    [Inspectable(defaultValue="0")]

    /**
     *  The zero-based index of the position <i>after</i> the last character
     *  in the current selection (equivalent to the one-based index of the last
     *  character).
     *  If the last character in the selection, for example, is the fifth
     *  character, this property has the value 5.
     *  When the control gets the focus, the selection is visible if the 
     *  <code>selectionBeginIndex</code> and <code>selectionEndIndex</code>
     *  properties are both set.
     *
     *  @default 0
     *
     *  @tiptext The zero-based index value of the last character
     *  in the selection.
     */
    public function get selectionEndIndex():int
    {
        return textField ?
               textField.selectionEndIndex :
               _selectionEndIndex;
    }

    /**
     *  @private
     */
    public function set selectionEndIndex(value:int):void
    {
        _selectionEndIndex = value;
        selectionChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  text
    //----------------------------------

    /**
     *  @private
     *  Storage for the text property.
     *  In addition to being set in the 'text' setter,
     *  it is automatically updated at another time:
     *  When the 'text' or 'htmlText' is pushed down into
     *  the textField in commitProperties(), this causes
     *  the textField to update its own 'text'.
     *  Therefore in commitProperties() we reset this storage var
     *  to be in sync with the textField.
     */
    private var _text:String = "";

    /**
     *  @private
     */
    private var textChanged:Boolean = false;

    [Bindable("textChanged")]
    [CollapseWhiteSpace]
    [Inspectable(category="General", defaultValue="")]
    [NonCommittingChangeEvent("change")]

    /**
     *  Plain text that appears in the control.
     *  Its appearance is determined by the CSS styles of this Label control.
     *  
     *  <p>Any HTML tags in the text string are ignored,
     *  and appear as entered in the string. 
     *  To display text formatted using HTML tags,
     *  use the <code>htmlText</code> property instead.
     *  If you set the <code>htmlText</code> property,
     *  the HTML replaces any text you had set using this propety, and the
     *  <code>text</code> property returns a plain-text version of the
     *  HTML text, with all HTML tags stripped out. For more information
     *  see the <code>htmlText</code> property.</p>
     *
     *  <p>To include the special characters left angle  bracket (&lt;),
     *  right angle bracket (&gt;), or ampersand (&amp;) in the text,
     *  wrap the text string in the CDATA tag.
     *  Alternatively, you can use HTML character entities for the
     *  special characters, for example, <code>&amp;lt;</code>.</p>
     *
     *  <p>If you try to set this property to <code>null</code>,
     *  it is set, instead, to the empty string.
     *  The <code>text</code> property can temporarily have the value <code>null</code>,
     *  which indicates that the <code>htmlText</code> has been recently set
     *  and the corresponding <code>text</code> value
     *  has not yet been determined.</p>
     *
     *  @default ""
     *  @tiptext Gets or sets the TextInput content
     *  @helpid 3190
     */
    public function get text():String
    {
        return _text;
    }

    /**
     *  @private
     */
    public function set text(value:String):void
    {
        textSet = true;

        // The text property can't be set to null, only to the empty string.
        // If the getter returns null, it means that 'htmlText' was just set
        // and the value of 'text' isn't yet known, because the 'htmlText'
        // hasn't been committed into the textField and the 'text'
        // hasn't yet been read back out of the textField.
        if (!value)
            value = "";

        if (!isHTML && value == _text)
            return;
        
        _text = value;
        textChanged = true;

        // The htmlText property is unknown until commitProperties(),
        // when we push the text into the TextField and it
        // calculates the htmlText.
        // But you can call validateNow() to make this happen right away.
        _htmlText = null;
        
        explicitHTMLText = null;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();

        // Trigger bindings to 'text'.
        dispatchEvent(new Event("textChanged"));

        // commitProperties() will dispatch an "htmlTextChanged" event
        // after the TextField determines the 'htmlText' based on the
        // 'text'; this event will trigger any bindings to 'htmlText'.

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //----------------------------------
    //  textField
    //----------------------------------

    /**
     *  The internal UITextField that renders the text of this TextInput.
     */
    protected var textField:IUITextField;

    //----------------------------------
    //  textHeight
    //----------------------------------

    /**
     *  @private
     */
    private var _textHeight:Number;
    
    /**
     *  The height of the text.
     *
     *  <p>The value of the <code>textHeight</code> property is correct only
     *  after the component has been validated.
     *  If you set <code>text</code> and then immediately ask for the
     *  <code>textHeight</code>, you might receive an incorrect value.
     *  You should wait for the component to validate
     *  or call the <code>validateNow()</code> method before you get the value.
     *  This behavior differs from that of the flash.text.TextField control,
     *  which updates the value immediately.</p>
     *
     *  @see flash.text.TextField
     */
    public function get textHeight():Number
    {
        return _textHeight;
    }

    //----------------------------------
    //  textWidth
    //----------------------------------

    /**
     *  @private
     */
    private var _textWidth:Number;
    
    /**
     *  The width of the text.
     *
     *  <p>The value of the <code>textWidth</code> property is correct only
     *  after the component has been validated.
     *  If you set <code>text</code> and then immediately ask for the
     *  <code>textWidth</code>, you might receive an incorrect value.
     *  You should wait for the component to validate
     *  or call the <code>validateNow()</code> method before you get the value.
     *  This behavior differs from that of the flash.text.TextField control,
     *  which updates the value immediately.</p>
     *
     *  @see flash.text.TextField
     */
    public function get textWidth():Number
    {
        return _textWidth;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Create child objects.
     */
    override protected function createChildren():void
    {
        super.createChildren();

        createBorder();

        createTextField(-1);
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();


        if (hasFontContextChanged() && textField != null)
        {
            var childIndex:int = getChildIndex(DisplayObject(textField));
            removeTextField();
            createTextField(childIndex);
            
            accessibilityPropertiesChanged = true;
            condenseWhiteChanged = true;
            displayAsPasswordChanged = true;
            enabledChanged = true;
            maxCharsChanged = true;
            restrictChanged = true;
            tabIndexChanged = true;
            textChanged = true;
            selectionChanged = true;
            horizontalScrollPositionChanged = true;
        }
        
        if (accessibilityPropertiesChanged)
        {
            textField.accessibilityProperties = _accessibilityProperties;

            accessibilityPropertiesChanged = false;
        }

        if (condenseWhiteChanged)
        {
            textField.condenseWhite = _condenseWhite;
            
            condenseWhiteChanged = false;
        }

        if (displayAsPasswordChanged)
        {
            textField.displayAsPassword = _displayAsPassword;

            displayAsPasswordChanged = false;
        }

        if (enabledChanged || editableChanged)
        {
            textField.type = enabled && _editable ?
                             TextFieldType.INPUT :
                             TextFieldType.DYNAMIC;

            if (enabledChanged)
            {
                if (textField.enabled != enabled)
                    textField.enabled = enabled;

                enabledChanged = false;
            }
            selectableChanged = true;
            editableChanged = false;
        }
        
        if (selectableChanged)
        {
            if (_editable)
                textField.selectable = enabled;
            else
                textField.selectable = enabled && _selectable;
            selectableChanged = false;
        }

        if (maxCharsChanged)
        {
            textField.maxChars = _maxChars;

            maxCharsChanged = false;
        }

        if (restrictChanged)
        {
            textField.restrict = _restrict;

            restrictChanged = false;
        }

        if (tabIndexChanged)
        {
            textField.tabIndex = _tabIndex;

            tabIndexChanged = false;
        }

        if (textChanged || htmlTextChanged)
        {
            // If the 'text' and 'htmlText' properties have both changed,
            // the last one set wins.
            if (isHTML)
                textField.htmlText = explicitHTMLText;
            else
                textField.text = _text;
            
            textFieldChanged(false, true);
            
            textChanged = false;
            htmlTextChanged = false;
        }

        if (selectionChanged)
        {
            textField.setSelection(_selectionBeginIndex, _selectionEndIndex);

            selectionChanged = false;
        }

        if (horizontalScrollPositionChanged)
        {
            textField.scrollH = _horizontalScrollPosition;

            horizontalScrollPositionChanged = false;
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var bm:EdgeMetrics = border && border is IRectangularBorder ?
                             IRectangularBorder(border).borderMetrics :
                             EdgeMetrics.EMPTY;

        var w:Number;
        var h:Number;

        // Start with a width of 160. This may change.
        measuredWidth = DEFAULT_MEASURED_WIDTH;
        
        if (maxChars)
        {
            // Use the width of "W" and multiply by the maxChars
            measuredWidth = Math.min(measuredWidth,
                measureText("W").width * maxChars + bm.left + bm.right + 8);
        }
        
        if (!text || text == "")
        {
            w = DEFAULT_MEASURED_MIN_WIDTH;
            h = measureText(" ").height +
                bm.top + bm.bottom + UITextField.TEXT_HEIGHT_PADDING;
            if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)  
                h += getStyle("paddingTop") + getStyle("paddingBottom");
        }
        else
        {
            var lineMetrics:TextLineMetrics;
            lineMetrics = measureText(text);

            w = lineMetrics.width + bm.left + bm.right + 8; 
            h = lineMetrics.height + bm.top + bm.bottom + UITextField.TEXT_HEIGHT_PADDING; 
                            
            if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
            {
                w += getStyle("paddingLeft") + getStyle("paddingRight");
                h += getStyle("paddingTop") + getStyle("paddingBottom");
            }
        }

        measuredWidth = Math.max(w, measuredWidth);
        measuredHeight = Math.max(h, DEFAULT_MEASURED_HEIGHT);
        
        measuredMinWidth = DEFAULT_MEASURED_MIN_WIDTH;
        measuredMinHeight = DEFAULT_MEASURED_MIN_HEIGHT;
    }

    /**
     *  @private
     *  Stretch the border and fit the TextField inside it.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var bm:EdgeMetrics;

        if (border)
        {
            border.setActualSize(unscaledWidth, unscaledHeight);
            bm = border is IRectangularBorder ?
                    IRectangularBorder(border).borderMetrics : EdgeMetrics.EMPTY;
        }
        else
        {
            bm = EdgeMetrics.EMPTY;
        }
        
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");
        var widthPad:Number = bm.left + bm.right;
        var heightPad:Number = bm.top + bm.bottom + 1;
        
        textField.x = bm.left;
        textField.y = bm.top;

        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        {
            textField.x += paddingLeft;
            textField.y += paddingTop;
            widthPad += paddingLeft + paddingRight; 
            heightPad += paddingTop + paddingBottom;
        }
        
        textField.width = Math.max(0, unscaledWidth - widthPad);
        textField.height = Math.max(0, unscaledHeight - heightPad);
    }

    /**
     *  @private
     *  Focus should always be on the internal TextField.
     */
    override public function setFocus():void
    {
        textField.setFocus();
    }

    /**
     *  @private
     */
    override protected function isOurFocus(target:DisplayObject):Boolean
    {
        return target == textField || super.isOurFocus(target);
    }

    /**
     *  @private
     *  Forward the drawFocus to the parent, if requested
     */
    override public function drawFocus(isFocused:Boolean):void
    {
        if (parentDrawsFocus)
        {
            IFocusManagerComponent(parent).drawFocus(isFocused);
            return;
        }

        super.drawFocus(isFocused);
    }
    
    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var allStyles:Boolean = (styleProp == null || styleProp == "styleName");

        super.styleChanged(styleProp);
        
        // Replace the borderSkin
        if (allStyles || styleProp == "borderSkin")
        {
            if (border)
            {
                removeChild(DisplayObject(border));
                border = null;
                createBorder();
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Creates the text field child and adds it as a child of this component.
     * 
     *  @param childIndex The index of where to add the child.
     *  If -1, the text field is appended to the end of the list.
     */
    mx_internal function createTextField(childIndex:int):void
    {
        if (!textField)
        {
            textField = IUITextField(createInFontContext(UITextField));

            textField.autoSize = TextFieldAutoSize.NONE;
            textField.enabled = enabled;
            textField.ignorePadding = false;
            textField.multiline = false;
            textField.tabEnabled = true;
            textField.wordWrap = false;
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                textField.styleName = this;

            textField.addEventListener(Event.CHANGE, textField_changeHandler);
            textField.addEventListener(TextEvent.TEXT_INPUT,
                                       textField_textInputHandler);
            textField.addEventListener(Event.SCROLL, textField_scrollHandler);
            textField.addEventListener("textFieldStyleChange",
                                       textField_textFieldStyleChangeHandler);
            textField.addEventListener("textFormatChange",
                                       textField_textFormatChangeHandler);
            textField.addEventListener("textInsert",
                                       textField_textModifiedHandler);                                       
            textField.addEventListener("textReplace",
                                       textField_textModifiedHandler);                                       

            if (childIndex == -1)
                addChild(DisplayObject(textField));
            else
                addChildAt(DisplayObject(textField), childIndex);
        }
    }

    /**
     *  @private
     *  Removes the text field from this component.
     */
    mx_internal function removeTextField():void
    {
        if (textField)
        {
            textField.removeEventListener(Event.CHANGE, textField_changeHandler);
            textField.removeEventListener(TextEvent.TEXT_INPUT,
                                          textField_textInputHandler);
            textField.removeEventListener(Event.SCROLL, textField_scrollHandler);
            textField.removeEventListener("textFieldStyleChange",
                                          textField_textFieldStyleChangeHandler);
            textField.removeEventListener("textFormatChange",
                                          textField_textFormatChangeHandler);
            textField.removeEventListener("textInsert",
                                          textField_textModifiedHandler);                                       
            textField.removeEventListener("textReplace",
                                          textField_textModifiedHandler);                                       

            removeChild(DisplayObject(textField));
            textField = null;
        }
    }
    
    /**
     *  Creates the border for this component.
     *  Normally the border is determined by the
     *  <code>borderStyle</code> and <code>borderSkin</code> styles.  
     *  It must set the border property to the instance
     *  of the border.
     */
    protected function createBorder():void
    {
        if (!border)
        {
            var borderClass:Class = getStyle("borderSkin");

            if (borderClass != null)
            {
                border = new borderClass();
    
                if (border is ISimpleStyleClient)
                    ISimpleStyleClient(border).styleName = this;
    
                // Add the border behind all the children.
                addChildAt(DisplayObject(border), 0);
    
                invalidateDisplayList();
            }
        }
    }

    /**
     *  Returns a TextLineMetrics object with information about the text 
     *  position and measurements for a line of text in the control.
     *  The component must be validated to get a correct number.
     *  If you set the <code>text</code> or <code>htmlText</code> property
     *  and then immediately call
     *  <code>getLineMetrics()</code> you may receive an incorrect value.
     *  You should either wait for the component to validate
     *  or call <code>validateNow()</code>.
     *  This is behavior differs from that of the flash.text.TextField class,
     *  which updates the value immediately.
     * 
     *  @param lineIndex The zero-based index of the line for which to get the metrics. 
     *
     *  @return The object that contains information about the text position
     *  and measurements for the specified line of text in the control.
     * 
     *  @see flash.text.TextField
     *  @see flash.text.TextLineMetrics
     */
    public function getLineMetrics(lineIndex:int):TextLineMetrics
    {
        return textField ? textField.getLineMetrics(lineIndex) : null;
    }

    /**
     *  Selects the text in the range specified by the parameters.
     *  If the control is not in focus, the selection highlight will not show 
     *  until the control gains focus. Also, if the focus is gained by clicking 
     *  on the control, any previous selection would be lost.
     *  If the two parameter values are the same,
     *  the new selection is an insertion point.
     *
     *  @param beginIndex The zero-based index of the first character in the
     *  selection; that is, the first character is 0, the second character
     *  is 1, and so on.
     *
     *  @param endIndex The zero-based index of the position <i>after</i>
     *  the last character in the selection (equivalent to the one-based
     *  index of the last character).
     *  If the parameter is 5, the last character in the selection, for
     *  example, is the fifth character.
     *  When the TextInput control gets the focus, the selection is visible 
     *  if the <code>selectionBeginIndex</code> and <code>selectionEndIndex</code>
     *  properties are both set.
     *
     *  @tiptext Sets a new text selection.
     */
    public function setSelection(beginIndex:int, endIndex:int):void
    {
        _selectionBeginIndex = beginIndex;
        _selectionEndIndex = endIndex;
        selectionChanged = true;

        invalidateProperties();
    }

    /**
     *  @private
     *  Setting the 'htmlText' of textField changes its 'text',
     *  and vice versa, so afterwards doing so we call this method
     *  to update the storage vars for various properties.
     *  Afterwards, the TextInput's 'text', 'htmlText', 'textWidth',
     *  and 'textHeight' are all in sync with each other
     *  and are identical to the TextField's.
     */
    private function textFieldChanged(styleChangeOnly:Boolean,
                                      dispatchValueCommitEvent:Boolean):void
    {
        var changed1:Boolean;
        var changed2:Boolean;

        if (!styleChangeOnly)
        {
            changed1 = _text != textField.text;
            _text = textField.text;
        }
        
        changed2 = _htmlText != textField.htmlText;
        _htmlText = textField.htmlText;

        // If the 'text' property changes, trigger bindings to it
        // and conditionally dispatch a 'valueCommit' event.
        if (changed1)
        {
            dispatchEvent(new Event("textChanged"));
            
            if (dispatchValueCommitEvent)
                dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
        }
        // If the 'htmlText' property changes, trigger bindings to it.
        if (changed2)
            dispatchEvent(new Event("htmlTextChanged"));

        _textWidth = textField.textWidth;
        _textHeight = textField.textHeight;
    }

    /**
     *  @private
     *  Some other components which use a TextInput as an internal
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
     *  Gets called by internal field so we draw a focus rect around us.
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        if (event.target == this)
            systemManager.stage.focus = TextField(textField);

        var fm:IFocusManager = focusManager;

        if (editable && fm)
        {
            fm.showFocusIndicator = true;
            if (textField.selectable &&
                _selectionBeginIndex == _selectionEndIndex)
            {
                textField.setSelection(0, textField.length);
            }
        }

        super.focusInHandler(event);
            
        if (_imeMode != null && _editable)
        {
            IME.enabled = true;
            prevMode = IME.conversionMode;
            // When IME.conversionMode is unknown it cannot be
            // set to anything other than unknown(English)
            try
            {
                if (!errorCaught &&
                    IME.conversionMode != IMEConversionMode.UNKNOWN)
                {
                    IME.conversionMode = _imeMode;
                }
                errorCaught = false;
            }
            catch(e:Error)
            {
                // Once an error is thrown, focusIn is called 
                // again after the Alert is closed, throw error 
                // only the first time.
                errorCaught = true;
                var message:String = resourceManager.getString(
                    "controls", "unsupportedMode", [ _imeMode ]);
                throw new Error(message);
            }
        }
    }

    /**
     *  @private
     *  Gets called by internal field so we remove focus rect.
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        super.focusOutHandler(event);

        if (_imeMode != null && _editable)
        {
            // When IME.conversionMode is unknown it cannot be
            // set to anything other than unknown(English)
            // and when known it cannot be set to unknown
            if (IME.conversionMode != IMEConversionMode.UNKNOWN 
                && prevMode != IMEConversionMode.UNKNOWN)
                IME.conversionMode = prevMode;
            IME.enabled = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        switch (event.keyCode)
        {
            case Keyboard.ENTER:
            {
                dispatchEvent(new FlexEvent(FlexEvent.ENTER));
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
     */
    private function textField_changeHandler(event:Event):void
    {
        textFieldChanged(false, false);

        // Kill any programmatic change we might be looking at.
        textChanged = false;
        htmlTextChanged = false;

        // Stop propagation of the original event
        // and dispatch a new one that doesn't bubble.
        event.stopImmediatePropagation();
        dispatchEvent(new Event(Event.CHANGE));
    }

    /**
     *  @private
     */
    private function textField_textInputHandler(event:TextEvent):void
    {
        event.stopImmediatePropagation();

        // Dispatch a cancelable version of this event.
        var newEvent:TextEvent =
            new TextEvent(TextEvent.TEXT_INPUT, false, true);
        newEvent.text = event.text;
        dispatchEvent(newEvent);

        // If any handler has called preventDefault(),
        // then stop the TextField from accepting the text.
        if (newEvent.isDefaultPrevented())
            event.preventDefault();
    }

    /**
     *  @private
     */
    private function textField_scrollHandler(event:Event):void
    {
        _horizontalScrollPosition = textField.scrollH;
    }

    /**
     *  @private
     */
    private function textField_textFieldStyleChangeHandler(event:Event):void
    {
        textFieldChanged(true, false);
    }

    /**
     *  @private
     */
    private function textField_textFormatChangeHandler(event:Event):void
    {
        textFieldChanged(true, false);
    }
    
    /**
     *  @private
     */
    private function textField_textModifiedHandler(event:Event):void
    {
        textFieldChanged(false, true);
    }
}

}
