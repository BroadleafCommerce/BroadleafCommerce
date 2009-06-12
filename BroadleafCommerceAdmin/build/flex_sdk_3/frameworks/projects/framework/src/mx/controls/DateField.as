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

import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.ui.Keyboard;

import mx.controls.dataGridClasses.DataGridListData;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListData;
import mx.core.ClassFactory;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFactory;
import mx.core.mx_internal;
import mx.core.UIComponentGlobals;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.DateChooserEvent;
import mx.events.DropdownEvent;
import mx.events.FlexEvent;
import mx.events.FlexMouseEvent;
import mx.managers.IFocusManagerComponent;
import mx.managers.PopUpManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when a date is selected or changed,
 *  and the DateChooser control closes.
 *
 *  @eventType mx.events.CalendarLayoutChangeEvent.CHANGE
 *  @helpid 3613
 */
[Event(name="change", type="mx.events.CalendarLayoutChangeEvent")]

/**
 *  Dispatched when a date is selected or the user clicks
 *  outside the drop-down list.
 *
 *  @eventType mx.events.DropdownEvent.CLOSE
 *  @helpid 3615
 */
[Event(name="close", type="mx.events.DropdownEvent")]

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
 *  Dispatched when a user selects the field to open the drop-down list.
 *
 *  @eventType mx.events.DropdownEvent.OPEN
 *  @helpid 3614
 */
[Event(name="open", type="mx.events.DropdownEvent")]

/**
 *  Dispatched when the month changes due to user interaction.
 *
 *  @eventType mx.events.DateChooserEvent.SCROLL
 *  @helpid 3616
 */
[Event(name="scroll", type="mx.events.DateChooserEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/FocusStyles.as"
include "../styles/metadata/IconColorStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Color of the border.
 *  The following controls support this style: Button, CheckBox,
 *  ComboBox, MenuBar,
 *  NumericStepper, ProgressBar, RadioButton, ScrollBar, Slider, and any
 *  components that support the <code>borderStyle</code> style.
 *  The default value depends on the component class;
 *  if not overriden for the class, the default value is <code>0xB7BABC</code>.
 */
[Style(name="borderColor", type="uint", format="Color", inherit="no")]

/**
 *  The bounding box thickness of the DateChooser control.
 *  The default value is 1.
 */
[Style(name="borderThickness", type="Number", format="Length", inherit="no")]

/**
 *  Radius of component corners.
 *  The following components support this style: Alert, Button, ComboBox,  
 *  LinkButton, MenuBar, NumericStepper, Panel, ScrollBar, Tab, TitleWindow, 
 *  and any component
 *  that supports a <code>borderStyle</code> property set to <code>"solid"</code>.
 *  The default value depends on the component class;
 *  if not overriden for the class, the default value is <code>0</code>.
 */
[Style(name="cornerRadius", type="Number", format="Length", inherit="no", deprecatedReplacement="dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Name of the CSS Style declaration to use for the styles for the
 *  DateChooser control's drop-down list.
 *  By default, the DateChooser control uses the DateField control's
 *  inheritable styles. 
 *  
 *  <p>You can use this class selector to set the values of all the style properties 
 *  of the DateChooser class, including <code>cornerRadius</code>,
 *  <code>fillAlphas</code>, <code>fillColors</code>, <code>headerColors</code>, <code>headerStyleName</code>, 
 *  <code>highlightAlphas</code>, <code>todayStyleName</code>, and <code>weekdayStyleName</code>.</p>
 */
[Style(name="dateChooserStyleName", type="String", inherit="no")]

/**
 *  Alphas used for the background fill of controls. Use [1, 1] to make the control background
 *  opaque.
 *  
 *  @default [ 0.6, 0.4 ]
 */
[Style(name="fillAlphas", type="Array", arrayType="Number", inherit="no", deprecatedReplacement="nextMonthStyleFilters, prevMonthStyleFilters, dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Colors used to tint the background of the control.
 *  Pass the same color for both values for a flat-looking control.
 *  
 *  @default [ 0xFFFFFF, 0xCCCCCC ]
 */
[Style(name="fillColors", type="Array", arrayType="uint", format="Color", inherit="no", deprecatedReplacement="nextMonthStyleFilters, prevMonthStyleFilters, dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Colors of the band at the top of the DateChooser control.
 *  The default value is <code>[ 0xE6EEEE, 0xFFFFFF ]</code>.
 */
[Style(name="headerColors", type="Array", arrayType="uint", format="Color", inherit="yes", deprecatedReplacement="dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Name of the style sheet definition to configure the text (month name and year)
 *  and appearance of the header area of the control.
 */
[Style(name="headerStyleName", type="String", inherit="no", deprecatedReplacement="dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Alpha transparencies used for the highlight fill of controls.
 *  The first value specifies the transparency of the top of the highlight and the second value specifies the transparency 
 *  of the bottom of the highlight. The highlight covers the top half of the skin.
 *  
 *  @default [ 0.3, 0.0 ]
 */
[Style(name="highlightAlphas", type="Array", arrayType="Number", inherit="no", deprecatedReplacement="dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Color of the highlight area of the date when the user holds the
 *  mouse pointer over a date in the DateChooser control.
 *  @default 0xE3FFD6
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Color of the highlight area of the currently selected date
 *  in the DateChooser control.
 *  @default 0xCDFFC1
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

/**
 *  Name of the class to use as the default skin for the background and border. 
 *  For the DateField class, there is no default value.
 */
[Style(name="skin", type="Class", inherit="no", states=" up, over, down, disabled")]


/**
 *  Color of the highlight of today's date in the DateChooser control.
 *  The default value is <code>0x2B333</code>.
 */
[Style(name="todayColor", type="uint", format="Color", inherit="yes")]

/**
 *  Name of the style sheet definition to configure the appearance of the current day's
 *  numeric text, which is highlighted
 *  in the control when the <code>showToday</code> property is <code>true</code>.
 *  Specify a <code>color</code> style property to change the font color.
 *  If omitted, the current day text inherits
 *  the text styles of the control.
 */
[Style(name="todayStyleName", type="String", inherit="no", deprecatedReplacement="dateChooserStyleName", deprecatedSince="3.0")]

/**
 *  Name of the style sheet definition to configure the weekday names of
 *  the control. If omitted, the weekday names inherit the text
 *  styles of the control.
 */
[Style(name="weekDayStyleName", type="String", inherit="no", deprecatedReplacement="dateChooserStyleName", deprecatedSince="3.0")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="selectedIndex", kind="property")]
[Exclude(name="selectedItem", kind="property")]
[Exclude(name="borderThickness", kind="style")]
[Exclude(name="editableUpSkin", kind="style")]
[Exclude(name="editableOverSkin", kind="style")]
[Exclude(name="editableDownSkin", kind="style")]
[Exclude(name="editableDisabledSkin", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.DateFieldAccImpl")]

[DefaultBindingProperty(source="selectedDate", destination="selectedDate")]

[DefaultTriggerEvent("change")]

[IconFile("DateField.png")]

[RequiresDataBinding(true)]

[ResourceBundle("controls")]
[ResourceBundle("SharedResources")]

/**
 *  The DateField control is a text field that shows the date
 *  with a calendar icon on its right side.
 *  When the user clicks anywhere inside the bounding box
 *  of the control, a DateChooser control pops up
 *  and shows the dates in the month of the current date.
 *  If no date is selected, the text field is blank
 *  and the month of the current date is displayed
 *  in the DateChooser control.
 *
 *  <p>When the DateChooser control is open, the user can scroll
 *  through months and years, and select a date.
 *  When a date is selected, the DateChooser control closes,
 *  and the text field shows the selected date.</p>
 *
 *  <p>The user can also type the date in the text field if the <code>editable</code>
 *  property of the DateField control is set to <code>true</code>.</p>
 *
 *  <p>The DateField has the same default characteristics (shown below) as the DateChooser for its expanded date chooser.</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>A size large enough to hold the calendar, and wide enough to display the day names</td>
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
*  <p>The DateField has the following default characteristics for the collapsed control:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>A size large enough to hold the formatted date and the calendar icon</td>
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
 *  <p>The <code>&lt;mx:DateField&gt</code> tag inherits all of the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:DateField
 *    <strong>Properties</strong>
 *    dayNames="["S", "M", "T", "W", "T", "F", "S"]"
 *    disabledDays="<i>No default</i>"
 *    disabledRanges="<i>No default</i>"
 *    displayedMonth="<i>Current month</i>"
 *    displayedYear="<i>Current year</i>"
 *    dropdownFactory="<i>ClassFactory that creates an mx.controls.DateChooser</i>"
 *    firstDayOfWeek="0"
 *    formatString="MM/DD/YYYY"
 *    labelFunction="<i>Internal formatter</i>"
 *    maxYear="2100"
 *    minYear="1900"
 *    monthNames="["January", "February", "March", "April", "May",
 *    "June", "July", "August", "September", "October", "November",
 *    "December"]"
 *    monthSymbol=""
 *    parseFunction="<i>Internal parser</i>"
 *    selectableRange="<i>No default</i>"
 *    selectedDate="<i>No default</i>"
 *    showToday="true|false"
 *    yearNavigationEnabled="false|true"
 *    yearSymbol=""
 *  
 *   <strong>Styles</strong>
 *    borderColor="0xAAB3B3"
 *    color="0x0xB333C"
 *    dateChooserStyleName="dateFieldPopup"
 *    disabledColor="0xAAB3B3"
 *    disabledIconColor="0x999999"
 *    focusAlpha="0.5"
 *    focusRoundedCorners="tl tr bl br"
 *    fontAntiAliasType="advanced"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel"
 *    fontSharpness="0"
 *    fontSize="10"
 *    fontStyle="normal|italic"
 *    fontThickness="0"
 *    fontWeight="normal|bold"
 *    iconColor="0x111111"
 *    leading="2"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    rollOverColor="0xE3FFD6"
 *    selectionColor="0xB7F39B"
 *    textAlign="left|right|center"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    todayColor="0x2B333C"
 * 
 *    <strong>Events</strong>
 *    change="<i>No default</i>"
 *    close="<i>No default</i>"
 *    dataChange="<i>No default</i>"
 *    open="<i>No default</i>"
 *    scroll="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.controls.DateChooser
 *  @includeExample examples/DateFieldExample.mxml
 *
 *  @helpid 3617
 */
public class DateField extends ComboBase
                       implements IDataRenderer, IDropInListItemRenderer,
                       IFocusManagerComponent, IListItemRenderer
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by DateFieldAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Parses a String object that contains a date, and returns a Date
     *  object corresponding to the String.
     *  The <code>inputFormat</code> argument contains the pattern
     *  in which the <code>valueString</code> String is formatted.
     *  It can contain <code>"M"</code>,<code>"D"</code>,<code>"Y"</code>,
     *  and delimiter and punctuation characters.
     *
     *  <p>The function does not check for the validity of the Date object.
     *  If the value of the date, month, or year is NaN, this method returns null.</p>
     * 
     *  <p>For example:
     *  <pre>var dob:Date = DateField.stringToDate("06/30/2005", "MM/DD/YYYY");</pre>        
     *  </p>
     *
     *  @param valueString Date value to format.
     *
     *  @param inputFormat String defining the date format.
     *
     *  @return The formatted date as a Date object.
     *
     */
    public static function stringToDate(valueString:String, inputFormat:String):Date
    {
        var mask:String
        var temp:String;
        var dateString:String = "";
        var monthString:String = "";
        var yearString:String = "";
        var j:int = 0;

        var n:int = inputFormat.length;
        for (var i:int = 0; i < n; i++,j++)
        {
            temp = "" + valueString.charAt(j);
            mask = "" + inputFormat.charAt(i);

            if (mask == "M")
            {
                if (isNaN(Number(temp)) || temp == " ")
                    j--;
                else
                    monthString += temp;
            }
            else if (mask == "D")
            {
                if (isNaN(Number(temp)) || temp == " ")
                    j--;
                else
                    dateString += temp;
            }
            else if (mask == "Y")
            {
                yearString += temp;
            }
            else if (!isNaN(Number(temp)) && temp != " ")
            {
                return null;
            }
        }

        temp = "" + valueString.charAt(inputFormat.length - i + j);
        if (!(temp == "") && (temp != " "))
            return null;

        var monthNum:Number = Number(monthString);
        var dayNum:Number = Number(dateString);
        var yearNum:Number = Number(yearString);

        if (isNaN(yearNum) || isNaN(monthNum) || isNaN(dayNum))
            return null;

        if (yearString.length == 2 && yearNum < 70)
            yearNum+=2000;

        var newDate:Date = new Date(yearNum, monthNum - 1, dayNum);

        if (dayNum != newDate.getDate() || (monthNum - 1) != newDate.getMonth())
            return null;

        return newDate;
    }

    /**
     *  Formats a Date into a String according to the <code>outputFormat</code> argument.
     *  The <code>outputFormat</code> argument contains a pattern in which
     *  the <code>value</code> String is formatted.
     *  It can contain <code>"M"</code>,<code>"D"</code>,<code>"Y"</code>,
     *  and delimiter and punctuation characters.
     *
     *  @param value Date value to format.
     *
     *  @param outputFormat String defining the date format.
     *
     *  @return The formatted date as a String.
     *
     *  @example <pre>var todaysDate:String = DateField.dateToString(new Date(), "MM/DD/YYYY");</pre>
     */
    public static function dateToString(value:Date, outputFormat:String):String
    {
        if (!value)
            return "";

        var date:String = String(value.getDate());
        if (date.length < 2)
            date = "0" + date;

        var month:String = String(value.getMonth() + 1);
        if (month.length < 2)
            month = "0" + month;

        var year:String = String(value.getFullYear());

        var output:String = "";
        var mask:String;

        // outputFormat will be null if there are no resources.
        var n:int = outputFormat != null ? outputFormat.length : 0;
        for (var i:int = 0; i < n; i++)
        {
            mask = outputFormat.charAt(i);

            if (mask == "M")
            {
                output += month;
                i++;
            }
            else if (mask == "D")
            {
                output += date;
                i++;
            }
            else if (mask == "Y")
            {
                if (outputFormat.charAt(i+2) == "Y")
                {
                    output += year;
                    i += 3;
                }
                else
                {
                    output += year.substring(2,4);
                    i++;
                }
            }
            else
            {
                output += mask;
            }
        }

        return output;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function DateField()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var creatingDropdown:Boolean = false;

    /**
     *  @private
     */
    mx_internal var showingDropdown:Boolean = false;

    /**
     *  @private
     */
    private var inKeyDown:Boolean = false;

    /**
     *  @private
     */
    private var isPressed:Boolean;

    /**
     *  @private
     */
    private var openPos:Number = 0;

    /**
     *  @private
     */
    private var lastSelectedDate:Date;

    /**
     *  @private
     */
    private var updateDateFiller:Boolean = false;

    /**
     *  @private
     */
    private var addedToPopupManager:Boolean = false;

    /**
     *  @private
     */
    private var isMouseOver:Boolean = false;

    /**
     *  @private
     */ 
    private var yearChangedWithKeys:Boolean = false;
    
    /**
     *  @private
     *  Flag that will block default data/listData behavior
     */
    private var selectedDateSet:Boolean;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the enabled property.
     */
    private var _enabled:Boolean = true;

    /**
     *  @private
     */
    private var enabledChanged:Boolean = false;

    [Bindable("enabledChanged")]
    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function get enabled():Boolean
    {
        return _enabled;
    }

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        if (value == _enabled)
            return;

        _enabled = value;
        super.enabled = value;
        enabledChanged = true;

        invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property
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
     *  <p>When you use the control as a drop-in item renderer or drop-in
     *  item editor, Flex automatically writes the current value of the item
     *  to the <code>selectedDate</code> property of this control.</p>
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
        var newDate:Date;

        _data = value;

        if (_listData && _listData is DataGridListData)
            newDate = _data[DataGridListData(_listData).dataField];
        else if (_listData is ListData && ListData(_listData).labelField in _data)
            newDate = _data[ListData(_listData).labelField];
        else if (_data is String)
            newDate = new Date(Date.parse(data as String));
        else
            newDate = _data as Date;

        if (!selectedDateSet)
        {
            selectedDate = newDate;
            selectedDateSet = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  dayNames
    //----------------------------------

    /**
     *  @private
     *  Storage for the dayNames property.
     */
    private var _dayNames:Array;

    /**
     *  @private
     */
    private var dayNamesChanged:Boolean = false;

    /**
     *  @private
     */
    private var dayNamesOverride:Array;
    
    [Bindable("dayNamesChanged")]
    [Inspectable(arrayType="String", defaultValue="null")]

    /**
     *  Weekday names for DateChooser control.
     *  Setting this property changes the day labels
     *  of the DateChooser control.
     *  Sunday is the first day (at index 0).
     *  The rest of the week names follow in the normal order.
     *  
     *  @default [ "S", "M", "T", "W", "T", "F", "S" ]
     *  @helpid 3626
     */
    public function get dayNames():Array
    {
        return _dayNames;
    }

    /**
     *  @private
     */
    public function set dayNames(value:Array):void
    {
        dayNamesOverride = value;

        _dayNames = value != null ?
                    value :
                    resourceManager.getStringArray(
                        "controls", "dayNamesShortest");
                        
        // _dayNames will be null if there are no resources.
        _dayNames = _dayNames ? _dayNames.slice(0) : null;

        dayNamesChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  disabledDays
    //----------------------------------

    /**
     *  @private
     *  Storage for the disabledDays property.
     */
    private var _disabledDays:Array = [];

    /**
     *  @private
     */
    private var disabledDaysChanged:Boolean = false;

    [Bindable("disabledDaysChanged")]
    [Inspectable(arrayType="int")]

    /**
     *  Days to disable in a week.
     *  All the dates in a month, for the specified day, are disabled.
     *  This property immediately changes the user interface
     *  of the DateChooser control.
     *  The elements of this Array can have values from 0 (Sunday)
     *  to 6 (Saturday).
     *  For example, a value of <code>[0, 6]</code> disables
     *  Sunday and Saturday.
     *
     *  @default []
     *  @helpid 3627
     */
    public function get disabledDays():Array
    {
        return _disabledDays;
    }

    /**
     *  @private
     */
    public function set disabledDays(value:Array):void
    {
        _disabledDays = value;
        disabledDaysChanged = true;
        updateDateFiller = true;

        invalidateProperties();
    }

    //----------------------------------
    //  disabledRanges
    //----------------------------------

    /**
     *  @private
     *  Storage for the disabledRanges property.
     */
    private var _disabledRanges:Array = [];

    /**
     *  @private
     */
    private var disabledRangesChanged:Boolean = false;

    [Bindable("disabledRangesChanged")]
    [Inspectable(arrayType="Object")]

    /**
     *  Disables single and multiple days.
     *
     *  <p>This property accepts an Array of objects as a parameter.
     *  Each object in this Array is a Date object that specifies a
     *  single day to disable; or an object containing one or both
     *  of the <code>rangeStart</code> and <code>rangeEnd</code> properties,
     *  each of whose values is a Date object.
     *  The value of these properties describes the boundaries
     *  of the date range.
     *  If either is omitted, the range is considered
     *  unbounded in that direction.
     *  If you specify only <code>rangeStart</code>,
     *  all the dates after the specified date are disabled,
     *  including the <code>rangeStart</code> date.
     *  If you specify only <code>rangeEnd</code>,
     *  all the dates before the specified date are disabled,
     *  including the <code>rangeEnd</code> date.
     *  To disable a single day, use a single Date object that specifies a date
     *  in the Array. Time values are zeroed out from the Date object if
     *  they are present.</p>
     *
     *  <p>The following example, disables the following dates: January 11
     *  2006, the range January 23 - February 10 2006, and March 1 2006
     *  and all following dates.</p>
     *
     *  <pre>disabledRanges="{[new Date(2006,0,11), {rangeStart:
     *  new Date(2006,0,23), rangeEnd: new Date(2006,1,10)},
     *  {rangeStart: new Date(2006,2,1)}]}"</pre>
     *
     *  <p>Setting this property immediately changes the appearance of the
     *  DateChooser control, if the disabled dates are included in the
     *  <code>displayedMonth</code> and <code>displayedYear</code>
     *  properties.</p>
     *
     *  @default []
     *  @helpid 3629
     */
    public function get disabledRanges():Array
    {
        return _disabledRanges;
    }

    /**
     *  @private
     */
    public function set disabledRanges(value:Array):void
    {
        _disabledRanges = scrubTimeValues(value);
        disabledRangesChanged = true;
        updateDateFiller = true;

        invalidateProperties();
    }

    //----------------------------------
    //  displayedMonth
    //----------------------------------

    /**
     *  @private
     *  Storage for the displayedMonth property.
     */
    private var _displayedMonth:int = (new Date()).getMonth();

    /**
     *  @private
     */
    private var displayedMonthChanged:Boolean = false;

    [Bindable("displayedMonthChanged")]
    [Inspectable(category="General")]

    /**
     *  Used with the <code>displayedYear</code> property,
     *  the <code>displayedMonth</code> property
     *  specifies the month displayed in the DateChooser control.
     *  Month numbers are zero-based, so January is 0 and December is 11.
     *  Setting this property immediately changes the appearance
     *  of the DateChooser control.
     *  The default value is the month number of today's date.
     *
     *  <p>The default value is the current month.</p>
     *
     *  @helpid 3624
     */
    public function get displayedMonth():int
    {
        if (dropdown && dropdown.displayedMonth != _displayedMonth)
            return dropdown.displayedMonth;
        else
            return _displayedMonth;
    }

    /**
     *  @private
     */
    public function set displayedMonth(value:int):void
    {
        _displayedMonth = value;
        displayedMonthChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  displayedYear
    //----------------------------------

    /**
     *  @private
     *  Storage for the displayedYear property.
     */
    private var _displayedYear:int = (new Date()).getFullYear();

    /**
     *  @private
     */
    private var displayedYearChanged:Boolean = false;

    [Bindable("displayedYearChanged")]
    [Inspectable(category="General")]

    /**
     *  Used with the <code>displayedMonth</code> property,
     *  the <code>displayedYear</code> property determines
     *  which year is displayed in the DateChooser control.
     *  Setting this property immediately changes the appearance
     *  of the DateChooser control.
     *  
     *  <p>The default value is the current year.</p>
     *
     *  @helpid 3625
     */
    public function get displayedYear():int
    {
        if (dropdown && dropdown.displayedYear != _displayedYear)
            return dropdown.displayedYear;
        else
            return _displayedYear;
    }

    /**
     *  @private
     */
    public function set displayedYear(value:int):void
    {
        _displayedYear = value;
        displayedYearChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  dropdown
    //----------------------------------

    /**
     *  @private
     *  Storage for the dropdown property.
     */
    private var _dropdown:DateChooser;

    /**
     *  Contains a reference to the DateChooser control
     *  contained by the DateField control.  The class used 
     *  can be set with <code>dropdownFactory</code> as long as 
     *  it extends <code>DateChooser</code>.
     */
    public function get dropdown():DateChooser
    {
        return _dropdown;
    }

    //----------------------------------
    //  dropdownFactory
    //----------------------------------

    /**
     *  @private
     *  Storage for the dropdownFactory property.
     */
    private var _dropdownFactory:IFactory = new ClassFactory(DateChooser);

    [Bindable("dropdownFactoryChanged")]

    /**
     *  The IFactory that creates a DateChooser-derived instance to use
     *  as the date-chooser
     *  The default value is an IFactory for DateChooser
     *
     */
    public function get dropdownFactory():IFactory
    {
        return _dropdownFactory;
    }

    /**
     *  @private
     */
    public function set dropdownFactory(value:IFactory):void
    {
        _dropdownFactory = value;

        dispatchEvent(new Event("dropdownFactoryChanged"));
    }

    //----------------------------------
    //  firstDayOfWeek
    //----------------------------------

    /**
     *  @private
     *  Storage for the firstDayOfWeek property.
     */
    private var _firstDayOfWeek:Object

    /**
     *  @private
     */
    private var firstDayOfWeekChanged:Boolean = false;

    [Bindable("firstDayOfWeekChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  @private
     */
    private var firstDayOfWeekOverride:Object;
    
    /**
     *  Day of the week (0-6, where 0 is the first element
     *  of the dayNames Array) to display in the first column
     *  of the  DateChooser control.
     *  Setting this property changes the order of the day columns.
     *
     *  @default 0 (Sunday)
     *  @helpid 3623
     */
    public function get firstDayOfWeek():Object
    {
        return _firstDayOfWeek;
    }

    /**
     *  @private
     */
    public function set firstDayOfWeek(value:Object):void
    {
        firstDayOfWeekOverride = value;

        _firstDayOfWeek = value != null ?
                          int(value) :
                          resourceManager.getInt(
                              "controls", "firstDayOfWeek");

        firstDayOfWeekChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  formatString
    //----------------------------------

    /**
     *  @private
     *  Storage for the formatString property.
     */
    private var _formatString:String = null;

    [Bindable("formatStringChanged")]
    [Inspectable(defaultValue="null")]

    /**
     *  @private
     */
    private var formatStringOverride:String;
    
    /**
     *  The format of the displayed date in the text field.
     *  This property can contain any combination of <code>"MM"</code>, 
     *  <code>"DD"</code>, <code>"YY"</code>, <code>"YYYY"</code>,
     *  delimiter, and punctuation characters.
     * 
     *  @default "MM/DD/YYYY"
     */
    public function get formatString():String
    {
        return _formatString;
    }

    /**
     *  @private
     */
    public function set formatString(value:String):void
    {
        formatStringOverride = value;

        _formatString = value != null ?
                        value :
                        resourceManager.getString(
                            "SharedResources", "dateFormat");

        updateDateFiller = true;

        invalidateProperties();
        invalidateSize();
        
        dispatchEvent(new Event("formatStringChanged"));
    }

    //----------------------------------
    //  labelFunction
    //----------------------------------

    /**
     *  @private
     *  Storage for the labelFunction property.
     */
    private var _labelFunction:Function;

    [Bindable("labelFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  Function used to format the date displayed
     *  in the text field of the DateField control.
     *  If no function is specified, the default format is used.
     *  
     *  <p>The function takes a Date object as an argument,
     *  and returns a String in the format to be displayed, 
     *  as the following example shows:</p>
     *  <pre>
     *  public function formatDate(currentDate:Date):String {
     *      ...
     *      return dateString;
     *  }</pre>
     *
     *  <p>If you allow the user to enter a date in the text field
     *  of the DateField control, and you define a formatting function using 
     *  the <code>labelFunction</code> property, you should specify a 
     *  function to the <code>parseFunction</code> property that converts 
     *  the input text string to a Date object for use by the DateField control, 
     *  or set the <code>parseFunction</code> property to null.</p>
     *
     *  @default null
     *  @see mx.controls.DateField#parseFunction
     *  @helpid 3618
     */
    public function get labelFunction():Function
    {
        return _labelFunction;
    }

    /**
     *  @private
     */
    public function set labelFunction(value:Function):void
    {
        _labelFunction = value;
        updateDateFiller = true;

        invalidateProperties();

        dispatchEvent(new Event("labelFunctionChanged"));
    }

    //----------------------------------
    //  listData
    //----------------------------------

    /**
     *  @private
     *  Storage for the listData property
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
    //  maxYear
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxYear property.
     */
    private var _maxYear:int = 2100;

    /**
     *  @private
     */
    private var maxYearChanged:Boolean = false;

    /**
     *  The last year selectable in the control.
     *  @default 2100
     *
     *  @helpid
     */
    public function get maxYear():int
    {
        if (dropdown)
            return dropdown.maxYear;
        else
            return _maxYear;
    }

    /**
     *  @private
     */
    public function set maxYear(value:int):void
    {
        if (_maxYear == value)
            return;

        _maxYear = value;
        maxYearChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  minYear
    //----------------------------------

    /**
     *  @private
     *  Storage for the minYear property.
     */
    private var _minYear:int = 1900;

    /**
     *  @private
     */
    private var minYearChanged:Boolean = false;

    /**
     *  The first year selectable in the control.
     *  @default 1900
     *
     *  @helpid
     */
    public function get minYear():int
    {
        if (dropdown)
            return dropdown.minYear;
        else
            return _minYear;
    }

    /**
     *  @private
     */
    public function set minYear(value:int):void
    {
        if (_displayedYear == value)
            return;

        _minYear = value;
        minYearChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  monthNames
    //----------------------------------

    /**
     *  @private
     *  Storage for the monthNames property.
     */
    private var _monthNames:Array;

    /**
     *  @private
     */
    private var monthNamesChanged:Boolean = false;

    /**
     *  @private
     */
    private var monthNamesOverride:Array;
    
    [Bindable("monthNamesChanged")]
    [Inspectable(category="Other", arrayType="String", defaultValue="null")]

    /**
     *  Names of the months displayed at the top of the control.
     *  The <code>monthSymbol</code> property is appended to the end of 
     *  the value specified by the <code>monthNames</code> property, 
     *  which is useful in languages such as Japanese.
     *
     *  @default [ "January", "February", "March", "April", "May", "June", 
     *  "July", "August", "September", "October", "November", "December" ]
     */
    public function get monthNames():Array
    {
        return _monthNames;
    }

    /**
     *  @private
     */
    public function set monthNames(value:Array):void
    {
        monthNamesOverride = value;

        _monthNames = value != null ?
                      value :
                      resourceManager.getStringArray(
                          "SharedResources", "monthNames");
                         
        // _monthNames will be null if there are no resources.
        _monthNames = _monthNames ? _monthNames.slice(0) : null;

        monthNamesChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  monthSymbol
    //----------------------------------

    /**
     *  @private
     *  Storage for the monthSymbol property.
     */
    private var _monthSymbol:String;

    /**
     *  @private
     */
    private var monthSymbolChanged:Boolean = false;

    /**
     *  @private
     */
    private var monthSymbolOverride:String;
    
    [Bindable("monthSymbolChanged")]
    [Inspectable(defaultValue="")]

    /**
     *  This property is appended to the end of the value specified 
     *  by the <code>monthNames</code> property to define the names 
     *  of the months displayed at the top of the control.
     *  Some languages, such as Japanese, use an extra 
     *  symbol after the month name. 
     *
     *  @default ""
     */
    public function get monthSymbol():String
    {
        return _monthSymbol;
    }

    /**
     *  @private
     */
    public function set monthSymbol(value:String):void
    {
        monthSymbolOverride = value;

        _monthSymbol = value != null ?
                       value :
                       resourceManager.getString(
                           "SharedResources", "monthSymbol");

        monthSymbolChanged = true;

        invalidateProperties();
    }
      
    //----------------------------------
    //  parseFunction
    //----------------------------------

    /**
     *  @private
     *  Storage for the parseFunction property.
     */
    private var _parseFunction:Function = DateField.stringToDate;

    [Bindable("parseFunctionChanged")]

    /**
     *  Function used to parse the date entered as text
     *  in the text field area of the DateField control and return a 
     *  Date object to the control.
     *  If no function is specified, Flex uses
     *  the default function.
     *  If you set the <code>parseFunction</code> property, it should 
     *  typically perform the reverse of the function specified to 
     *  the <code>labelFunction</code> property.
     *  
     *  <p>The function takes two arguments 
     *  and returns a Date object to the DateField control, 
     *  as the following example shows:</p>
     *  <pre>
     *  public function parseDate(valueString:String, inputFormat:String):Date {
     *      ...
     *      return newDate
     *  }</pre>
     * 
     *  <p>Where the <code>valueString</code> argument contains the text 
     *  string entered by the user in the text field, and the <code>inputFormat</code> 
     *  argument contains the format of the string. For example, if you 
     *  only allow the user to enter a text sting using two characters for 
     *  month, day, and year, then pass "MM/DD/YY" to 
     *  the <code>inputFormat</code> argument. </p>
     *
     *  @see mx.controls.DateField#labelFunction
     * 
     *  @helpid
     */
    public function get parseFunction():Function
    {
        return _parseFunction;
    }

    /**
     *  @private
     */
    public function set parseFunction(value:Function):void
    {
        _parseFunction = value;

        dispatchEvent(new Event("parseFunctionChanged"));
    }

    //----------------------------------
    //  selectableRange
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectableRange property.
     */
    private var _selectableRange:Object = null;

    /**
     *  @private
     */
    private var selectableRangeChanged:Boolean = false;

    [Bindable("selectableRangeChanged")]
    [Inspectable(arrayType="Date")]

    /**
     *  Range of dates between which dates are selectable.
     *  For example, a date between 04-12-2006 and 04-12-2007
     *  is selectable, but dates out of this range are disabled.
     *
     *  <p>This property accepts an Object as a parameter.
     *  The Object contains two properties, <code>rangeStart</code>
     *  and <code>rangeEnd</code>, of type Date.
     *  If you specify only <code>rangeStart</code>,
     *  all the dates after the specified date are enabled.
     *  If you only specify <code>rangeEnd</code>,
     *  all the dates before the specified date are enabled.
     *  To enable only a single day in a DateChooser control,
     *  you can pass a Date object directly. Time values are 
     *  zeroed out from the Date object if they are present.</p>
     *
     *  <p>The following example enables only the range
     *  January 1, 2006 through June 30, 2006. Months before January
     *  and after June do not appear in the DateChooser.</p>
     *
     *  <pre>selectableRange="{{rangeStart : new Date(2006,0,1),
     *  rangeEnd : new Date(2006,5,30)}}"</pre>
     *
     *  @default null
     *  @helpid 3628
     */
    public function get selectableRange():Object
    {
        return _selectableRange;
    }

    /**
     *  @private
     */
    public function set selectableRange(value:Object):void
    {
        _selectableRange = scrubTimeValue(value);
        selectableRangeChanged = true;
        updateDateFiller = true;

        invalidateProperties();
    }

    //----------------------------------
    //  selectedDate
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectedDate property.
     */
    private var _selectedDate:Date = null;

    /**
     *  @private
     */
    private var selectedDateChanged:Boolean = false;

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General")]

    /**
     *  Date as selected in the DateChooser control.
     *  Accepts a Date object as a parameter. If the incoming Date 
     *  object has any time values, they are zeroed out.
     *
     *  <p>Selecting the currently selected date in the control deselects it, 
     *  sets the <code>selectedDate</code> property to <code>null</code>, 
     *  and then dispatches the <code>change</code> event.</p>
     *
     *  @default null
     *  @helpid 3630
     */
    public function get selectedDate():Date
    {
        return _selectedDate;
    }

    /**
     *  @private
     */
    public function set selectedDate(value:Date):void
    {
        selectedDateSet = true;
        _selectedDate = scrubTimeValue(value) as Date;
        updateDateFiller = true;
        selectedDateChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  showToday
    //----------------------------------

    /**
     *  @private
     *  Storage for the showToday property.
     */
    private var _showToday:Boolean = true;

    /**
     *  @private
     */
    private var showTodayChanged:Boolean = false;

    [Bindable("showTodayChanged")]
    [Inspectable(category="General", defaultValue="true")]

    /**
     *  If <code>true</code>, specifies that today is highlighted
     *  in the DateChooser control.
     *  Setting this property immediately changes the appearance
     *  of the DateChooser control.
     *
     *  @default true
     *  @helpid 3622
     */
    public function get showToday():Boolean
    {
        return _showToday;
    }

    /**
     *  @private
     */
    public function set showToday(value:Boolean):void
    {
        _showToday = value;
        showTodayChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  yearNavigationEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the yearNavigationEnabled property.
     */
    private var _yearNavigationEnabled:Boolean = false;

    /**
     *  @private
     */
    private var yearNavigationEnabledChanged:Boolean = false;

    [Bindable("yearNavigationEnabledChanged")]
    [Inspectable(defaultValue="false")]

    /**
     *  Enables year navigation. When <code>true</code>
     *  an up and down button appear to the right
     *  of the displayed year. You can use these buttons
     *  to change the current year.
     *  These button appear to the left of the year in locales where year comes 
     *  before the month in the date format.
     *
     *  @default false
     */
    public function get yearNavigationEnabled():Boolean
    {
        return _yearNavigationEnabled;
    }

    /**
     *  @private
     */
    public function set yearNavigationEnabled(value:Boolean):void
    {
        _yearNavigationEnabled = value;
        yearNavigationEnabledChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  yearSymbol
    //----------------------------------

    /**
     *  @private
     *  Storage for the yearSymbol property.
     */
    private var _yearSymbol:String;

    /**
     *  @private
     */
    private var yearSymbolChanged:Boolean = false;

    /**
     *  @private
     */
    private var yearSymbolOverride:String;
    
    [Bindable("yearSymbolChanged")]
    [Inspectable(defaultValue="")]

    /**
     *  This property is appended to the end of the year 
     *  displayed at the top of the control.
     *  Some languages, such as Japanese, 
     *  add a symbol after the year. 
     *
     *  @default ""
     */
    public function get yearSymbol():String
    {
        return _yearSymbol;
    }

    /**
     *  @private
     */
    public function set yearSymbol(value:String):void
    {
        yearSymbolOverride = value;

        _yearSymbol = value != null ?
                      value :
                      resourceManager.getString(
                          "controls", "yearSymbol");

        yearSymbolChanged = true;

        invalidateProperties();
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
        if (DateField.createAccessibilityImplementation != null)
            DateField.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     *  Create subobjects in the component.
     */
    override protected function createChildren():void
    {
        super.createChildren();

        createDropdown();

        downArrowButton.setStyle("paddingLeft", 0);
        downArrowButton.setStyle("paddingRight", 0);
        textInput.editable = false;
        textInput.addEventListener(TextEvent.TEXT_INPUT, textInput_textInputHandler);
        // hide the border, we use the text input's border
        border.visible = false;

    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        if (enabledChanged)
        {
            enabledChanged = false;
            dispatchEvent(new Event("enabledChanged"));
        }

        if (dayNamesChanged)
        {
            dayNamesChanged = false;
            // _dayNames will be null if there are no resources.
            dropdown.dayNames = _dayNames ? _dayNames.slice(0) : null;
            dispatchEvent(new Event("dayNamesChanged"));
        }

        if (disabledDaysChanged)
        {
            disabledDaysChanged = false;
            dropdown.disabledDays = _disabledDays.slice(0);
            dispatchEvent(new Event("disabledDaysChanged"));
        }

        if (disabledRangesChanged)
        {
            disabledRangesChanged = false;
            dropdown.disabledRanges = _disabledRanges.slice(0);
            dispatchEvent(new Event("disabledRangesChanged"));
        }

        if (displayedMonthChanged)
        {
            displayedMonthChanged = false;
            dropdown.displayedMonth = _displayedMonth;
            dispatchEvent(new Event("displayedMonthChanged"));
        }

        if (displayedYearChanged)
        {
            displayedYearChanged = false;
            dropdown.displayedYear = _displayedYear;
            dispatchEvent(new Event("displayedYearChanged"));
        }

        if (firstDayOfWeekChanged)
        {
            firstDayOfWeekChanged = false;
            dropdown.firstDayOfWeek = _firstDayOfWeek;
            dispatchEvent(new Event("firstDayOfWeekChanged"));
        }

        if (minYearChanged)
        {
            minYearChanged = false;
            dropdown.minYear = _minYear;
        }

        if (maxYearChanged)
        {
            maxYearChanged = false;
            dropdown.maxYear = _maxYear;
        }

        if (monthNamesChanged)
        {
            monthNamesChanged = false;
            // _monthNames will be null if there are no resources.
            dropdown.monthNames = _monthNames ? _monthNames.slice(0) : null;
            dispatchEvent(new Event("monthNamesChanged"));
        }

        if (selectableRangeChanged)
        {
            selectableRangeChanged = false;
            dropdown.selectableRange = _selectableRange is Array ? _selectableRange.slice(0) : _selectableRange;
            dispatchEvent(new Event("selectableRangeChanged"));
        }

        if (selectedDateChanged)
        {
            selectedDateChanged = false;
            dropdown.selectedDate = _selectedDate;
            dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
        }

        if (showTodayChanged)
        {
            showTodayChanged = false;
            dropdown.showToday = _showToday;
            dispatchEvent(new Event("showTodayChanged"));
        }

        if (updateDateFiller)
        {
            updateDateFiller = false;
            dateFiller(_selectedDate);
        }

        if (yearNavigationEnabledChanged)
        {
            yearNavigationEnabledChanged = false;
            dropdown.yearNavigationEnabled = _yearNavigationEnabled;
            dispatchEvent(new Event("yearNavigationEnabledChanged"));
        }

        if (yearSymbolChanged)
        {
            yearSymbolChanged = false;
            dropdown.yearSymbol = _yearSymbol;
            dispatchEvent(new Event("yearSymbolChanged"));
        }
        
        if (monthSymbolChanged)
        {
            monthSymbolChanged = false;
            dropdown.monthSymbol = _monthSymbol;
            dispatchEvent(new Event("monthSymbolChanged"));
        }

        super.commitProperties();
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        // skip base class, we do our own calculation here
        // super.measure();

        var buttonWidth:Number = downArrowButton.getExplicitOrMeasuredWidth();
        var buttonHeight:Number = downArrowButton.getExplicitOrMeasuredHeight();

        var bigDate:Date = new Date(2004, 12, 31);
        var txt:String = (_labelFunction != null) ? _labelFunction(bigDate) : 
                            dateToString(bigDate, formatString);

        measuredMinWidth = measuredWidth = measureText(txt).width + 8 + 2 + buttonWidth;
        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
            measuredMinWidth = measuredWidth += getStyle("paddingLeft") + getStyle("paddingRight");
        measuredMinHeight = measuredHeight = textInput.getExplicitOrMeasuredHeight();
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var w:Number = unscaledWidth;
        var h:Number = unscaledHeight;

        var arrowWidth:Number = downArrowButton.getExplicitOrMeasuredWidth();
        var arrowHeight:Number = downArrowButton.getExplicitOrMeasuredHeight();

        downArrowButton.setActualSize(arrowWidth, arrowHeight);
        downArrowButton.move(w - arrowWidth, Math.round((h - arrowHeight) / 2));

        textInput.setActualSize(w - arrowWidth - 2, h);
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);
   
        if (dropdown)
            dropdown.styleChanged(styleProp);
   
        if (styleProp == null ||
            styleProp == "styleName" ||
            styleProp == "dateChooserStyleName")
        {
            if (dropdown)
            {
                var dateChooserStyleName:String = getStyle(
                            "dateChooserStyleName");
   
                if (dateChooserStyleName)
                {
                    var styleDecl:CSSStyleDeclaration =
                    StyleManager.getStyleDeclaration("." + dateChooserStyleName);
                
                    if (styleDecl)
                    {
                        _dropdown.styleDeclaration = styleDecl;
                        _dropdown.regenerateStyleCache(true);
                    }
                }
            } 
        }
    }

    /**
     *  @private
     */
    override public function notifyStyleChangeInChildren(
                                styleProp:String, recursive:Boolean):void
    {
        super.notifyStyleChangeInChildren(styleProp, recursive);

        if (dropdown)
            dropdown.notifyStyleChangeInChildren(styleProp, recursive);
    }

    /**
     *  @private
     */
    override public function regenerateStyleCache(recursive:Boolean):void
    {
        super.regenerateStyleCache(recursive);

        if (dropdown)
            dropdown.regenerateStyleCache(recursive);
    }

    /**
     *  @private
     */
    override protected function resourcesChanged():void
    {
        super.resourcesChanged();

        dayNames = dayNamesOverride;
        firstDayOfWeek = firstDayOfWeekOverride;
        formatString = formatStringOverride;
        monthNames = monthNamesOverride;
        monthSymbol = monthSymbolOverride;
        yearSymbol = yearSymbolOverride;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Opens the DateChooser control.
     *
     *  @helpid 3620
     */
    public function open():void
    {
        displayDropdown(true);
    }

    /**
     *  Closes the DateChooser control.
     *
     *  @helpid 3621
     */
    public function close():void
    {
        displayDropdown(false);
    }

    /**
     *  @private
     */
    private function displayDropdown(show:Boolean, triggerEvent:Event = null):void
    {
        if (!_enabled)
            return;

        if (show == showingDropdown)
            return;

        if (!addedToPopupManager)
        {
            addedToPopupManager = true;
            PopUpManager.addPopUp(_dropdown, this, false);
        }
        else
            PopUpManager.bringToFront(_dropdown);

        // Subclasses may extend to do pre-processing
        // before the dropdown is displayed
        // or override to implement special display behavior.
        //var point = {};
        // point x will exactly appear on the icon.
        // Leaving 1 pixel for the border to appear.
        var point:Point = new Point(unscaledWidth - downArrowButton.width,0);
        point = localToGlobal(point);
        if (show)
        {
            if (_parseFunction != null)
                _selectedDate = _parseFunction(text, formatString);
            lastSelectedDate = _selectedDate;
            selectedDate_changeHandler(triggerEvent);

            var dd:DateChooser = dropdown;

            if (_dropdown.selectedDate)
            {
                _dropdown.displayedMonth = _dropdown.selectedDate.getMonth();
                _dropdown.displayedYear = _dropdown.selectedDate.getFullYear();
            }
            point = dd.parent.globalToLocal(point);
            dd.visible = show;
            dd.scaleX = scaleX;
            dd.scaleY = scaleY;

            var xVal:Number = point.x;
            var yVal:Number = point.y;

            //handling of dropdown position
            // A. Bottom Left Placment
            // B. Bottom Right Placement
            // C. Top Right Placement
            var screen:Rectangle = systemManager.screen;

            if (screen.width > dd.getExplicitOrMeasuredWidth() + point.x &&
                screen.height < dd.getExplicitOrMeasuredHeight() + point.y)
            {
                xVal = point.x
                yVal = point.y - dd.getExplicitOrMeasuredHeight();
                openPos = 1;
            }
            else if (screen.width < dd.getExplicitOrMeasuredWidth() + point.x &&
                     screen.height < dd.getExplicitOrMeasuredHeight() + point.y)
            {
                xVal = point.x - dd.getExplicitOrMeasuredWidth() + downArrowButton.width;
                yVal = point.y - dd.getExplicitOrMeasuredHeight();
                openPos = 2;
            }
            else if (screen.width < dd.getExplicitOrMeasuredWidth() + point.x &&
                     screen.height > dd.getExplicitOrMeasuredHeight() + point.y)
            {
                xVal = point.x - dd.getExplicitOrMeasuredWidth() + downArrowButton.width;
                yVal = point.y + unscaledHeight;
                openPos = 3;
            }
            else
                // Why do we need to disable downArrowButton when its hidden?
                //downArrowButton.enabled = false;
                openPos = 0;

            UIComponentGlobals.layoutManager.validateClient(dd, true);
            dd.move(xVal, yVal);
            Object(dd).setActualSize(dd.getExplicitOrMeasuredWidth(),dd.getExplicitOrMeasuredHeight());

        }
        else
        {
            _dropdown.visible = false;
        }

        showingDropdown = show;

        var event:DropdownEvent =
            new DropdownEvent(show ? DropdownEvent.OPEN : DropdownEvent.CLOSE);
        event.triggerEvent = triggerEvent;
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function createDropdown():void
    {
        if (creatingDropdown)
            return;

        creatingDropdown = true;

        _dropdown = dropdownFactory.newInstance();
        _dropdown.focusEnabled = false;
        _dropdown.owner = this;
        _dropdown.moduleFactory = moduleFactory;
        var todaysDate:Date = new Date();
        _dropdown.displayedMonth = todaysDate.getMonth();
        _dropdown.displayedYear = todaysDate.getFullYear();

        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            _dropdown.styleName = this;
        else
            _dropdown.styleName = new StyleProxy(this, {}); 
        
        var dateChooserStyleName:Object = getStyle("dateChooserStyleName");
        if (dateChooserStyleName)
        {
            var styleDecl:CSSStyleDeclaration =
            StyleManager.getStyleDeclaration("." + dateChooserStyleName);

            if (styleDecl)
                _dropdown.styleDeclaration = styleDecl;
        }

        _dropdown.visible = false;

        _dropdown.addEventListener(CalendarLayoutChangeEvent.CHANGE,
                                   dropdown_changeHandler);
        _dropdown.addEventListener(DateChooserEvent.SCROLL,
                                   dropdown_scrollHandler);
        _dropdown.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE,
                                   dropdown_mouseDownOutsideHandler);
        _dropdown.addEventListener(FlexMouseEvent.MOUSE_WHEEL_OUTSIDE,
                                   dropdown_mouseDownOutsideHandler);
        
        creatingDropdown = false;
    }

    /**
     *  @private
     *  This is the default date format that is displayed
     *  if labelFunction is not defined.
     */
    private function dateFiller(value:Date):void
    {
        if (_labelFunction != null)
            textInput.text = labelFunction(value);
        else
            textInput.text = dateToString(value, formatString);
    }
    
    /**
     *  @private
     *  This method scrubs out time values from incoming date objects
     */ 
     private function scrubTimeValue(value:Object):Object
     {
        if (value is Date)
        {
            return new Date(value.getFullYear(), value.getMonth(), value.getDate());
        }
        else if (value is Object) 
        {
            var range:Object = {};
            if (value.rangeStart)
            {
                range.rangeStart = new Date(value.rangeStart.getFullYear(), 
                                            value.rangeStart.getMonth(), 
                                            value.rangeStart.getDate());
            }
            
            if (value.rangeEnd)
            {
                range.rangeEnd = new Date(value.rangeEnd.getFullYear(), 
                                          value.rangeEnd.getMonth(), 
                                          value.rangeEnd.getDate());
            }
            return range;
        }
        return null;
     }
     
     /**
     *  @private
     *  This method scrubs out time values from incoming date objects
     */ 
     private function scrubTimeValues(values:Array):Array
     {
         var dates:Array = [];
         for (var i:int = 0; i < values.length; i++)
         {
            dates[i] = scrubTimeValue(values[i]);
         }
         return dates;
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
        if (showingDropdown && event != null)
            displayDropdown(false);

        super.focusOutHandler(event);

        if (_parseFunction != null)
            _selectedDate = _parseFunction(text, formatString);
        
        selectedDate_changeHandler(event);
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (event.ctrlKey && event.keyCode == Keyboard.DOWN)
        {
            displayDropdown(true, event);
            event.stopPropagation();
        }

        else if (event.ctrlKey && event.keyCode == Keyboard.UP)
        {
            if (showingDropdown)
                selectedDate = lastSelectedDate;
            displayDropdown(false, event);
            event.stopPropagation();
        }

        else if (event.keyCode == Keyboard.ESCAPE)
        {
            if (showingDropdown)
                selectedDate = lastSelectedDate;
            displayDropdown(false, event);
            event.stopPropagation();
        }

        else if (event.keyCode == Keyboard.ENTER)
        {
            if (showingDropdown)
            {
                _selectedDate = _dropdown.selectedDate;
                displayDropdown(false, event);
                event.stopPropagation();
            }
            else if (editable)
            {
                if (_parseFunction != null)
                    _selectedDate = _parseFunction(text, formatString);
            }
            selectedDate_changeHandler(event);
        }

        else if (event.keyCode == Keyboard.UP ||
                 event.keyCode == Keyboard.DOWN ||
                 event.keyCode == Keyboard.LEFT ||
                 event.keyCode == Keyboard.RIGHT ||
                 event.keyCode == Keyboard.PAGE_UP ||
                 event.keyCode == Keyboard.PAGE_DOWN ||
                 event.keyCode == 189 || // - or _ key used to step down year
                 event.keyCode == 187 || // + or = key used to step up year
                 event.keyCode == Keyboard.HOME ||
                 event.keyCode == Keyboard.END)
        {
            if (showingDropdown)
            {
                if (yearNavigationEnabled &&
                    (event.keyCode == 189 || event.keyCode == 187)) 
                    yearChangedWithKeys = true;
                inKeyDown = true;
                // Redispatch the event to the DateChooser
                // and let its keyDownHandler() handle it.
                dropdown.dispatchEvent(event);
                inKeyDown = false;              
                // Prevent keys from moving scrollBars.
                event.stopPropagation();
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: ComboBase
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function downArrowButton_buttonDownHandler(
                                    event:FlexEvent):void
    {
        // The down arrow should always toggle the visibility of the dropdown.
        callLater(displayDropdown, [ !showingDropdown, event ]);

        // We hide the down arrow with the dropdown so the down arrow
        // never gets a release, so it is in the wrong state.
        // Force the state to be released:
        downArrowButton.phase = "up";
    }
    
    /**
     *  @private
     */
    override protected function textInput_changeHandler(event:Event):void
    {
        super.textInput_changeHandler(event);
        
        var inputDate:Date = _parseFunction(text, formatString);
        if (inputDate)
            _selectedDate = inputDate;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function dropdown_changeHandler(
                        event:CalendarLayoutChangeEvent):void
    {
        _selectedDate = dropdown.selectedDate;

        // If this was generated by the dropdown as a result of a keystroke,
        // it is likely a Page-Up or Page-Down, or Arrow-Up or Arrow-Down.
        // If the selection changes due to a keystroke,
        // we leave the dropdown displayed.
        // If it changes as a result of a mouse selection,
        // we close the dropdown.
        if (!inKeyDown)
            displayDropdown(false);

        if (_selectedDate)
            dateFiller(_selectedDate);
        else
            textInput.text = "";
        
        var e:CalendarLayoutChangeEvent = new 
            CalendarLayoutChangeEvent(CalendarLayoutChangeEvent.CHANGE);        
        e.newDate = event.newDate;
        e.triggerEvent = event.triggerEvent;
        dispatchEvent(e);                   
    }

    /**
     *  @private
     */
    private function dropdown_scrollHandler(event:DateChooserEvent):void
    {
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function dropdown_mouseDownOutsideHandler(event:MouseEvent):void
    {
        if (! hitTestPoint(event.stageX, event.stageY, true))
            displayDropdown(false, event);
    }

    /**
     *  @private
     *  Handling change in selectedDate due to user interaction.
     */
    private function selectedDate_changeHandler(triggerEvent:Event):void
    {
        if (!dropdown.selectedDate && !_selectedDate)
            return;

        if (_selectedDate)
            dateFiller(_selectedDate);

        if (dropdown.selectedDate && _selectedDate &&
            dropdown.selectedDate.getFullYear() == _selectedDate.getFullYear() &&
            dropdown.selectedDate.getMonth() == _selectedDate.getMonth() &&
            dropdown.selectedDate.getDate() == _selectedDate.getDate())
            return;

        dropdown.selectedDate = _selectedDate;

        var changeEvent:CalendarLayoutChangeEvent =
            new CalendarLayoutChangeEvent(CalendarLayoutChangeEvent.CHANGE);
        changeEvent.newDate = _selectedDate;
        changeEvent.triggerEvent = triggerEvent;
        dispatchEvent(changeEvent);
    }

    /**
     *  @private
     */ 
    private function textInput_textInputHandler(event:TextEvent):void
    {
            if (yearChangedWithKeys)
            {
                event.preventDefault();
                yearChangedWithKeys = false;
            }
    }

    /**
     *  @private
     */
    mx_internal function isShowingDropdown():Boolean
    {
        return showingDropdown;
    }

    
}

}
