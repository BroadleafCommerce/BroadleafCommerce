////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2002-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.events.Event;
import flash.geom.Rectangle;
import flash.text.StyleSheet;
import flash.text.TextFormat;
import flash.text.TextLineMetrics;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

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
 *  Dispatched when a user clicks a hyperlink in an 
 *  HTML-enabled text field, where the URL begins with <code>"event:"</code>. 
 *  The remainder of the URL after 
 *  <code>"event:"</code> is placed in the text property of the <code>link</code> event object.
 *
 *  <p>When you handle the <code>link</code> event, the hyperlink is not automatically executed; 
 *  you need to execute the hyperlink from within your event handler. 
 *  You typically use the <code>navigateToURL()</code> method to execute the hyperlink.
 *  This allows you to modify the hyperlink, or even prohibit it from occurring, 
 *  in your application. </p>
 *
 *  <p>The Label control must have the <code>selectable</code> property set 
 *  to <code>true</code> to generate the <code>link</code> event.</p>
 *
 *  @eventType flash.events.TextEvent.LINK
 */
[Event(name="link", type="flash.events.TextEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/TextStyles.as"

/**
 *  Number of pixels between the left of the Label and 
 *  the left of the text. 
 *  
 *  @default 0
 */
[Style(name="paddingLeft", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the right of the Label and 
 *  the right of the text. 
 *  
 *  @default 0
 */
[Style(name="paddingRight", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the bottom of the Label and 
 *  the bottom of the text. 
 *  
 *  @default 0 
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the top of the Label and 
 *  the top of the text. 
 *  
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

/**
 *  Color of the Label object's opaque background.
 *  The default value is <code>undefined</code>,
 *  which means that the background is transparent.
 */
//[Style(name="backgroundColor", type="uint", format="Color", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="focusEnabled", kind="property")]
[Exclude(name="focusPane", kind="property")]
[Exclude(name="mouseFocusEnabled", kind="property")]
[Exclude(name="tabEnabled", kind="property")]
[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]
[Exclude(name="themeColor", kind="style")]
[Exclude(name="setFocus", kind="method")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(destination="text")]

[IconFile("Label.png")]

/**
 *  The Label control displays a single line of noneditable text.
 *  Use the Text control to create blocks of multiline
 *  noneditable text.
 *
 *  <p>You can format Label text by using HTML tags,
 *  which are applied after the Label control's CSS styles are applied.
 *  You can also put padding around the four sides of the text.
 *  The text of a Label is nonselectable by default,
 *  but you can make it selectable.</p>
 *
 *  <p>If a Label is sized to be smaller than its text,
 *  you can control whether the text is simply clipped or whether
 *  it is truncated with a localizable string such as "...".
 *  (Note: Plain text can be truncated, but HTML text cannot.)
 *  If the entire text of the Label, either plain or HTML, 
 *  is not completely visible, and you haven't assigned a tooltip
 *  to the Label, an automatic "truncation tip" 
 *  displays the complete plain text when a user holds the mouse over the Label control.</p>
 *
 *  <p>Label controls do not have backgrounds or borders
 *  and cannot take focus.</p>
 *
 *  <p>The Label control has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Width and height large enough for the text</td>
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
 *  <p>The <code>&lt;mx:Label&gt;</code> tag inherits all of the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:Label
 *    <b>Properties</b>
 *    condenseWhite="true|false"
 *    data="null"
 *    htmlText=""
 *    listData="null"
 *    selectable="true|false"
 *    text=""
 *    truncateToFit="true|false"
 *    &nbsp;
 *    <b>Styles</b>
 *    color="0x0B333C"
 *    disabledColor="0xAAB3B3"
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
 *    paddingTop="0"
 *    paddingBottom="0"
 *    textAlign="left|right|center"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    &nbsp;
 *    <b>Events</b>
 *    dataChange="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/LabelExample.mxml
 *
 *  @see mx.controls.Text
 *  @see mx.controls.TextInput
 *  @see mx.controls.TextArea
 *  @see mx.controls.RichTextEditor
 */
public class Label extends UIComponent
                   implements IDataRenderer, IDropInListItemRenderer,
                   IListItemRenderer, IFontContextComponent
                   
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Implementation notes
    //
    //--------------------------------------------------------------------------

    /*
        A Label has a single internal child, a UITextField which displays
        the Label's text or htmlText (whichever was last set).

        See the implementation notes for UITextField to understand
        more about how a Flash TextField works.

        The 'text' and 'htmlText' properties of Label work somewhat
        differently from those of a native TextField.

        Because Flex uses invalidation, setting either 'text' or 'htmlText'
        does very little work; the setter only sets a storage var and some
        flags and dispatches an event to trigger bindings to update.

        In fact, the setters are so fast that calling them in a loop
        does not create a performance problem.
        For example, if you have a Text component (which is a subclass of
        Label intended for displaying multiple lines of text) you can
        write code like myTextComponent.text += moreStuff[i] inside a
        loop. 

        However, the Flex invalidation approach means that the 'text' and
        'htmlText' properties are not coupled as immediately as with
        a TextField.

        If you set the 'text' of a Label, you can immediately get it back,
        but if you immediately get the 'htmlText' it will be null,
        indicating that it is invalid and will be calculated
        the next time the LayoutManager runs.
        Similarly, if you set the 'htmlText' of a Label, you can immediately
        get back exactly what you set, but the 'text' will be null,
        again an indication that it is invalid and will be calculated
        the next time the LayoutManager runs.

        Later, when the LayoutManager runs to re-validate the Label,
        either the 'text' or the 'htmlText' that you set -- whichever one
        was set last -- will be pushed down into the TextField.
        After that happens, the Label's 'text' and 'htmlText' properties
        will be the same as those of the TextFields; the 'text' and the
        'htmlText' will be in sync with each other, but they will no
        longer necessarily be what you set.

        If you need to force the LayoutManager to run immediately,
        you can call validateNow() on the Label.

        Here are some examples of how these interactions work:

        myLabel.htmlText = "This is <b>bold</b>."
        trace(myLabel.htmlText);
            This is <b>bold</b>.
        trace(myLabel.text);
            null
        myLabel.validateNow();
        trace(myLabel.htmlText);
            <TEXTFORMAT LEADING="2">
            <P ALIGN="LEFT">
            <FONT FACE="Verdana" SIZE="10" COLOR="#0B333C"
                  LETTERSPACING="0" KERNING="0">
            This is <B>bold</B>.
            </FONT>
            </P>
            </TEXTFORMAT>
        trace(myLabel.text);
            This is bold.
    */

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Label()
    {
        super();
        
        // this is so the UITextField we contain can be read by a screen-reader
        tabChildren = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Flag that will block default data/listData behavior.
     */
    private var textSet:Boolean;

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
     *  The baselinePosition of a Label is calculated for its textField.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            if (!textField)
                return NaN;
            
            // Ensure that textField.y is accurate.
            validateNow();
    
            var t:String = isHTML ? explicitHTMLText : text; 
    
            if (t == "")
                t = " ";
    
            // Measure the width and height of the text to be displayed.
            // (This uses another TextField that isn't part of the
            // DisplayObject hierarchy.)
            var lineMetrics:TextLineMetrics = isHTML ?
                                              measureHTMLText(t) :
                                              measureText(t);
    
            return textField.y + lineMetrics.ascent;
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

    [Inspectable(category="General")]

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        if (value == enabled)
            return;

        super.enabled = value;
        enabledChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  textField
    //----------------------------------

    /**
     *  The internal UITextField that renders the text of this Label.
     */
    protected var textField:IUITextField;

    //----------------------------------
    //  toolTip
    //----------------------------------

    /**
     *  @private
     */
    private var toolTipSet:Boolean = false;

    /**
     *  @private
     */
    override public function set toolTip(value:String):void
    {
        super.toolTip = value;

        toolTipSet = value != null;
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
     *  Storage for the condenseWhite property.
     */
    private var _condenseWhite:Boolean = false;

    /**
     *  @private
     */
    private var condenseWhiteChanged:Boolean = false;

    [Bindable("condenseWhiteChanged")]
    [Inspectable(category="General", defaultValue="false")]
    
    /**
     *  Specifies whether extra white space (spaces, line breaks,
     *  and so on) should be removed in a Label control with HTML text.
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
        // if this Label is displaying HTML.
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
    mx_internal var htmlTextChanged:Boolean = false;
    
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

    /**
     *  Specifies the text displayed by the Label control, including HTML markup that
     *  expresses the styles of that text. 
     *  When you specify HTML text in this property, you can use the subset of HTML 
     *  tags that is supported by the Flash TextField control.
     * 
     *  <p>When you set this property, the HTML markup is applied
     *  after the CSS styles for the Label instance are applied.
     *  When you get this property, the HTML markup includes
     *  the CSS styles.</p>
     *  
     *  <p>For example, if you set this to be a string such as,
     *  <code>"This is an example of &lt;b&gt;bold&lt;/b&gt; markup"</code>,
     *  the text "This is an example of <b>bold</b> markup" appears
     *  in the Label with whatever CSS styles normally apply.
     *  Also, the word "bold" appears in boldface font because of the
     *  <code>&lt;b&gt;</code> markup.</p>
     *
     *  <p>HTML markup uses characters such as &lt; and &gt;,
     *  which have special meaning in XML (and therefore in MXML). So,  
     *  code such as the following does not compile:</p>
     *  
     *  <pre>
     *  &lt;mx:Label htmlText="This is an example of &lt;b&gt;bold&lt;/b&gt; markup"/&gt;
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
     *  &lt;mx:Label id="myLabel" initialize="myLabel_initialize()"/&gt;
     *  </pre>
     *  
     *  <p>where the <code>myLabel_initialize</code> method is in a script CDATA section:</p>
     *  
     *  <pre>
     *  &lt;mx:Script&gt;
     *  &lt;![CDATA[
     *  private function myLabel_initialize():void {
     *      myLabel.htmlText = "This is an example of &lt;b&gt;bold&lt;/b&gt; markup";
     *  }
     *  ]]&gt;
     *  &lt;/mx:Script&gt;
     *  
     *  </pre>
     *  
     *  <p>This is the simplest approach because the HTML markup
     *  remains easily readable.
     *  Notice that you must assign an <code>id</code> to the label
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
     *  &lt;mx:Label&gt;
     *      &lt;mx:htmlText&gt;&lt;![CDATA[This is an example of &lt;b&gt;bold&lt;/b&gt; markup]]&gt;&lt;/mx:htmlText&gt;
     *  &lt;mx:Label/&gt;
     *  </pre>
     *  
     *  <p>You must write the <code>htmlText</code> property as a child tag
     *  rather than as an attribute on the <code>&lt;mx:Label&gt;</code> tag
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
     *  &lt;mx:Label htmlText="This is an example of &amp;lt;b&amp;gt;bold&amp;lt;/b&amp;gt; markup"/&amp;gt;
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
     *  the value is the characters that the Label actually displays.</p>
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
     *  <code>validateNow()</code> method on the Label.
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

        if (isHTML && value == explicitHTMLText)
            return;

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
    //  listData
    //----------------------------------

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
     *  of the component with the appropriate data from the List control.
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
    //  selectable
    //----------------------------------

    /**
     *  @private
     *  Storage for selectable property.
     */
    private var _selectable:Boolean = false;

    /**
     *  @private
     *  Change flag for selectable property.
     */
    private var selectableChanged:Boolean;

    [Inspectable(category="General", defaultValue="true")]

    /**
     *  Specifies whether the text can be selected. 
     *  Making the text selectable lets you copy text from the control.
     *
     *  <p>When a <code>link</code> event is specified in the Label control, the <code>selectable</code> property must be set 
     *  to <code>true</code> to execute the <code>link</code> event.</p>
     *
     *  @default false;
     */
    public function get selectable():Boolean
    {
        return _selectable;
    }

    /**
     *  @private
     */
    public function set selectable(value:Boolean):void
    {
        if (value == selectable)
            return;

        _selectable = value;
        selectableChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  styleSheet
    //----------------------------------

    /**
     *  @private
     */
    mx_internal function get styleSheet():StyleSheet
    {
        return textField.styleSheet;
    }

    /**
     *  @private
     */
    mx_internal function set styleSheet(value:StyleSheet):void
    {
        textField.styleSheet = value;
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
     *  Although Label/Text do not receive stage focus 
     *  since they are not tabEnabled or 
     *  implement IFocusManagerComponent,
     *  for accessible applications, developers may set their
     *  tabIndex to specify reading order 
     *  of Screen Reader's virtual cursor.
     *  The default value is <code>-1</code>.
     *
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
        _tabIndex = value;

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
    mx_internal var textChanged:Boolean = false;

    [Bindable("valueCommit")]
    [CollapseWhiteSpace]
    [Inspectable(category="General", defaultValue="")]

    /**
     *  Specifies the plain text displayed by this control.
     *  Its appearance is determined by the CSS styles of this Label control.
     *
     *  <p>When you set this property, any characters that might
     *  look like HTML markup in the string have no special meaning
     *  and appear as entered.</p>
     *
     *  <p>To display text formatted by using HTML tags,
     *  use the <code>htmlText</code> property instead.
     *  If you set the <code>htmlText</code> property,
     *  the HTML replaces any text that you set using this property, and the
     *  <code>text</code> property returns a plain-text version of the
     *  HTML text, with all HTML tags stripped out.</p>
     *
     *  <p>To include the special characters left angle  bracket (&lt;),
     *  right angle bracket (&gt;), or ampersand (&amp;) in the text,
     *  wrap the text string in the CDATA tag.
     *  Alternatively, you can use HTML character entities for the
     *  special characters, for example, <code>&amp;lt;</code>.</p>
     *
     *  <p>If the text is wider than the Label control,
     *  the text is truncated and terminated by an ellipsis (...).
     *  The full text displays as a tooltip when
     *  you move the mouse over the Label control.
     *  If you also set a tooltip by using the <code>tooltip</code>
     *  property, the tooltip is displayed rather than the text.</p>
     *
     *  <p>If you try to set this property to <code>null</code>,
     *  it is set, instead, to the empty string.
     *  The <code>text</code> property can temporarily have the value <code>null</code>,
     *  which indicates that the <code>htmlText</code> has been recently set
     *  and the corresponding <code>text</code> value
     *  has not yet been determined.</p>
     *
     *  @default ""
     *  @tiptext Gets or sets the Label content
     *  @helpid 3907
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
        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));

        // commitProperties() will dispatch an "htmlTextChanged" event
        // after the TextField determines the 'htmlText' based on the
        // 'text'; this event will trigger any bindings to 'htmlText'.
    }

    //----------------------------------
    //  textHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the textHeight property.
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
     *  Storage for the textWidth property.
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

    //----------------------------------
    //  truncateToFit
    //----------------------------------

    /**
     *  If this propery is <code>true</code>, and the Label control size is
     *  smaller than its text, the text of the 
     *  Label control is truncated using 
     *  a localizable string, such as <code>"..."</code>.
     *  If this property is <code>false</code>, text that does not fit is clipped.
     * 
     *  @default true
     */
    public var truncateToFit:Boolean = true;
    
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

         if (!textField)
            createTextField(-1);
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
            removeTextField();
            
            condenseWhiteChanged = true;
            enabledChanged = true;
            selectableChanged = true;
            textChanged = true;
        }

        if (!textField)
            createTextField(-1);
        
        if (condenseWhiteChanged)
        {
            textField.condenseWhite = _condenseWhite;
            
            condenseWhiteChanged = false;
        }

        textField.tabIndex = tabIndex;

        if (enabledChanged)
        {
            textField.enabled = enabled;
           
            enabledChanged = false;
        }

        if (selectableChanged)
        {
            textField.selectable = _selectable;
            
            selectableChanged = false;
        }

        if (textChanged || htmlTextChanged)
        {
            // If the 'text' and 'htmlText' properties have both changed,
            // the last one set wins.
            if (isHTML)
                textField.htmlText = explicitHTMLText;
            else
                textField.text = _text;
            
            textFieldChanged(false)
                        
            textChanged = false;
            htmlTextChanged = false;
        }
    }

    /**
     *  @private
     *  Measure min/max/preferred sizes.
     */
    override protected function measure():void
    {
        super.measure();

        var t:String = isHTML ? explicitHTMLText : text; 
        
        t = getMinimumText(t);

        // Determine how large the textField would need to be
        // to display the entire text.
        var textFieldBounds:Rectangle = measureTextFieldBounds(t);

        // Add in the padding.
        measuredMinWidth = measuredWidth = textFieldBounds.width +
            getStyle("paddingLeft") + getStyle("paddingRight");
        measuredMinHeight = measuredHeight = textFieldBounds.height +
            getStyle("paddingTop") + getStyle("paddingBottom");
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
                
        /*
        var g:Graphics = graphics;
        g.clear();
        var backgroundColor:* = getStyle("backgroundColor");
        if (StyleManager.isValidStyleValue(backgroundColor))
        {
            g.beginFill(getStyle("backgroundColor"));
            g.drawRect(0, 0, unscaledWidth, unscaledHeight);
            g.endFill();
        }
        */
                
        // The textField occupies the entire Label bounds minus the padding.

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingBottom:Number = getStyle("paddingBottom");

        
        textField.setActualSize(unscaledWidth - paddingLeft - paddingRight,
                                unscaledHeight - paddingTop - paddingBottom);

        textField.x = paddingLeft;
        textField.y = paddingTop;

        // Now handle truncation if the text doesn't fit.

        var t:String = isHTML ? explicitHTMLText : text; 

        // Determine how large the textField would need to be
        // to display the entire text.
        var textFieldBounds:Rectangle = measureTextFieldBounds(t);
        
        // Plain text gets truncated with a "...".
        // HTML text simply gets clipped, because it is difficult
        // to skip over the markup and truncate only the non-markup.
        // But both plain text and HTML text gets an automatic tooltip
        // if the full text isn't visible.
        if (truncateToFit)
        {
            var truncated:Boolean;
            if (isHTML)
            {
                truncated = textFieldBounds.width > textField.width;
            }
            else
            {
                // Reset the text in case it was previously
                // truncated with a "...".
                textField.text = _text;
                
                // Determine whether the full text needs to be truncated
                // based on the actual size of the TextField.
                // Note that the actual size doesn't change;
                // the text changes to fit within the actual size.
                truncated = textField.truncateToFit();
            }

            // If no explicit tooltip has been set,
            // implicitly set or clear a "truncation tip".
            if (!toolTipSet)
                super.toolTip = truncated ? text : null;
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
            textField.enabled = enabled;
            textField.ignorePadding = true;
            textField.selectable = selectable;
            textField.styleName = this;
            textField.addEventListener("textFieldStyleChange",
                                       textField_textFieldStyleChangeHandler);
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
            textField.removeEventListener("textFieldStyleChange",
                                          textField_textFieldStyleChangeHandler);
            textField.removeEventListener("textInsert",
                                          textField_textModifiedHandler);                                       
            textField.removeEventListener("textReplace",
                                          textField_textModifiedHandler);                                       
            removeChild(DisplayObject(textField));
            textField = null;
        }
    }

    /**
     *  Returns a TextLineMetrics object with information about the text 
     *  position and measurements for a line of text in the control.
     *  The component must be validated to get a correct number.
     *  If you set <code>text</code> and then immediately call
     *  <code>getLineMetrics()</code> you may receive an incorrect value.
     *  You should either wait for the component to validate
     *  or call <code>validateNow()</code>.
     *  This is behavior differs from that of the flash.text.TextField class,
     *  which updates the value immediately.
     * 
     *  @param lineIndex The zero-based index of the line for which to get the metrics. 
     *  For the Label control, which has only a single line, must be 0.
     * 
     *  @return The TextLineMetrics object that contains information about the text.
     *
     *  @see flash.text.TextField
     *  @see flash.text.TextLineMetrics
     */
    public function getLineMetrics(lineIndex:int):TextLineMetrics
    {
        return textField ? textField.getLineMetrics(lineIndex) : null;
    }

    /**
     *  @private
     *  Setting the 'htmlText' of textField changes its 'text',
     *  and vice versa, so afterwards doing so we call this method
     *  to update the storage vars for various properties.
     *  Afterwards, the Label's 'text', 'htmlText', 'textWidth',
     *  and 'textHeight' are all in sync with each other
     *  and are identical to the TextField's.
     */
    private function textFieldChanged(styleChangeOnly:Boolean):void
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

        // If the 'text' property changes, dispatch a valueCommit
        // event, which will trigger bindings to 'text'.
        if (changed1)
            dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
        // If the 'htmlText' property changes, trigger bindings to it.
        if (changed2)
            dispatchEvent(new Event("htmlTextChanged"));

        _textWidth = textField.textWidth;
        _textHeight = textField.textHeight;
    }

    /**
     *  @private
     */
    private function measureTextFieldBounds(s:String):Rectangle
    {
        // Measure the text we need to display.
        var lineMetrics:TextLineMetrics = isHTML ?
                                          measureHTMLText(s) :
                                          measureText(s);
        
        // In order to display this text completely,
        // a TextField must be 4-5 pixels larger.
        return new Rectangle(0, 0,
                             lineMetrics.width + UITextField.TEXT_WIDTH_PADDING,
                             lineMetrics.height + UITextField.TEXT_HEIGHT_PADDING);
    }

    /**
     *  @private
     *  Some other components which use a Label as an internal
     *  subcomponent need access to its UITextField, but can't access the
     *  textField var because it is protected and therefore available
     *  only to subclasses.
     */
    mx_internal function getTextField():IUITextField
    {
        return textField;
    }
    
    /**
     *  @private 
     */
    mx_internal function getMinimumText(t:String):String
    {
         // If the text is null, empty, or a single character,
        // make the measured size big enough to hold
        // a capital and decending character using the current font.
        if (!t || t.length < 2)
            t = "Wj";
            
        return t;   
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function textField_textFieldStyleChangeHandler(event:Event):void
    {
        textFieldChanged(true);
    }

    /**
     *  @private
     */
    private function textField_textModifiedHandler(event:Event):void
    {
        textFieldChanged(false);
    }

}

}
