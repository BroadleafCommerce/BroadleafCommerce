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
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.text.TextLineMetrics;
import flash.ui.Keyboard;
import flash.utils.getQualifiedClassName;
import flash.utils.describeType;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.DateChooserEvent;
import mx.events.DateChooserEventDetail;
import mx.managers.IFocusManagerComponent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.skins.halo.DateChooserIndicator;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/GapStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Name of the skin for the <code>rollOverIndicator</code>.
 *  It can be customized to some other shape other than rectangular.
 *  If you want to change just the color,
 *  use the <code>rollOverColor</code> instead.
 *  The default value is the DateChooserRollOverIndicator class.
 */
[Style(name="rollOverIndicatorSkin", type="Class", inherit="no")]

/**
 *  Name of the skin for <code>selectionIndicator</code>.
 *  It can customized to some other shape other than rectangular.
 *  If one just needs to change color,
 *  use the <code>selectionColor</code> instead.
 *  The default value is the DateChooserSelectionIndicator class.
 */
[Style(name="selectionIndicatorSkin", type="Class", inherit="no")]

/**
 *  Name of the skin for <code>todayIndicator</code> style property. It can be customized
 *  to some other shape other than rectangular. If you
 *  wnat to change just the color, use the <code>todayColor</code> style property instead.
 *  The default value is the DateChooserTodayIndicator class.
 */
[Style(name="todayIndicatorSkin", type="Class", inherit="no")]

/**
 *  Name of the style sheet definition to configure the appearence of the current day's
 *  numeric text, which is highlighted
 *  in the control when the <code>showToday</code> property is <code>true</code>.
 *  Specify a "color" style to change the font color.
 *  If omitted, the current day text inherits
 *  the text styles of the control.
 */
[Style(name="todayStyleName", type="String", inherit="no")]

/**
 *  Name of the style sheet definition to configure the weekday names of
 *  the control. If omitted, the weekday names inherit the text
 *  styles of the control.
 */
[Style(name="weekDayStyleName", type="String", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[ExcludeClass]

[ResourceBundle("controls")]
	
/**
 *  @private
 *  The CalendarLayout class handles the layout of the date grid in a month.
 *  CalendarLayout can be extended to develop DateControls with
 *  single month display control or side-by-side month displays.
 *
 *  @see mx.styles.StyleManager
 */
public class CalendarLayout extends UIComponent
							implements IFocusManagerComponent, IFontContextComponent
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
    public function CalendarLayout()
    {
        super();

        addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
        addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var todayRow:int = -1;

    /**
     *  @private
     */
	private var todayColumn:int = -1;

    /**
     *  @private
     */
	private var enabledDaysInMonth:Array = [];

    /**
     *  @private
     */
    private var disabledRangeMode:Array;

    /**
     *  @private
     */
	private var cellHeight:Number = 14;

    /**
     *  @private
     */
	private var cellWidth:Number = 14;

    /**
     *  @private
     */
	private var yOffset:Number = -1;

    /**
     *  @private
     */
    mx_internal var dayBlocksArray:Array = [];

    /**
     *  @private
     */
	private var disabledArrays:Array = []; // An Array of Arrays

    /**
     *  @private
     */
	private var todaysLabelReference:IUITextField = null;

    /**
     *  @private
     */
	private var selectedMonthYearChanged:Boolean = false;

    /**
     *  @private
     */
	private var todayIndicator:IFlexDisplayObject;

    /**
     *  @private
     */
	private var selectionIndicator:Array = [];

    /**
     *  @private
     */
	private var rollOverIndicator:IFlexDisplayObject;

    /**
     *  @private
     */
	private var selectedRangeCount:int = 0;

    /**
     *  @private
     */
	private var lastSelectedDate:Date;

    /**
     *  @private
     */
	private var rangeStartDate:Date = null;

    /**
     *  @private
     */
    mx_internal var selRangeMode:int = 1;

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
	 *  Storage for the displayedYear property.
     */
	private var _enabled:Boolean = true;

    /**
     *  @private
     */
	private var enabledChanged:Boolean = false;

    [Inspectable(category="General", defaultValue="true")]

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
        super.enabled = value;

        _enabled = value;
        enabledChanged = true;

        invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  allowDisjointSelection
    //----------------------------------

    /**
     *  @private
	 *  Storage for the allowDisjointSelection property.
     */
	private var _allowDisjointSelection:Boolean = true;

    [Inspectable(category="General", defaultValue="true")]

    /**
	 *  @private
	 */
    public function get allowDisjointSelection():Boolean
    {
        return _allowDisjointSelection;
    }

    /**
     *  @private
     */
    public function set allowDisjointSelection(value:Boolean):void
    {
        _allowDisjointSelection = value;
    }

    //----------------------------------
    //  allowMultipleSelection
    //----------------------------------

    /**
     *  @private
	 *  Storage for the allowMultipleSelection property.
     */
	private var _allowMultipleSelection:Boolean = false;

    [Inspectable(category="General", defaultValue="false")]

    /**
	 *  @private
	 */
    public function get allowMultipleSelection():Boolean
    {
        return _allowMultipleSelection;
    }

    /**
     *  @private
     */
    public function set allowMultipleSelection(value:Boolean):void
    {
        _allowMultipleSelection = value;
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
	
    [Inspectable(category="Other", arrayType="String", defaultValue="null")]

    /**
	 *  @private
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
        invalidateSize();
        invalidateDisplayList();
    }

    //----------------------------------
    //  disabledDays
    //----------------------------------

    /**
     *  @private
	 *  Storage for the disabledDays property.
     */
	private var _disabledDays:Array = [];

	[Inspectable(category="General", arrayType="int")]

    /**
	 *  @private
	 */
    public function get disabledDays():Array
    {
        var result:Array = [];
        
		for (var i:int = 0, k:int = 0; i < _disabledDays.length; i++)
        {
            if (_disabledDays[i] >= 0 && _disabledDays[i] <= 6)
            {
                result[k] = _disabledDays[i];
                k++;
            }
        }

        return result;
    }

    /**
     *  @private
     */
    public function set disabledDays(value:Array):void
    {
        _disabledDays = value.slice(0);
        selectedMonthYearChanged = true;
        
		invalidateProperties();
		
		// This removes the disabled ranges/days from the selected ranges.
		var selRange:Array = selectedRanges;
    }

    //----------------------------------
    //  disabledRanges
    //----------------------------------

    /**
     *  @private
	 *  Storage for the disabledRanges property.
     */
	private var _disabledRanges:Array = [];

	[Inspectable(category="General", arrayType="Object")]

    /**
	 *  @private
	 */
    public function get disabledRanges():Array
    {
        return _disabledRanges.slice(0);
    }

    /**
     *  @private
     */
    public function set disabledRanges(value:Array):void
    {
        _disabledRanges = value.slice(0);
        disabledRangeMode = [];

        for (var i:int = 0; i < _disabledRanges.length; i++)
        {
            if (_disabledRanges[i] is Date)
            {
                disabledRangeMode[i] = 4;
                _disabledRanges[i] = new Date(value[i].getFullYear(),
											  value[i].getMonth(),
											  value[i].getDate());
            }
            else if (_disabledRanges[i] is Object)
            {
                _disabledRanges[i] = {};
                _disabledRanges[i] = value[i];

                if (!_disabledRanges[i].rangeStart &&
					_disabledRanges[i].rangeEnd)
                {
                    disabledRangeMode[i] = 3;
                }
                else if (_disabledRanges[i].rangeStart &&
						!_disabledRanges[i].rangeEnd)
                {
                    disabledRangeMode[i] = 2;
                }
                else if (_disabledRanges[i].rangeStart &&
						 _disabledRanges[i].rangeEnd)
                {
                    disabledRangeMode[i] = 1;
                }
            }
        }

        selectedMonthYearChanged = true;

        invalidateProperties();

        // To remove the disabled ranges/days from the selected ranges.
        var selRange:Array=selectedRanges;
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
	 *  Holds the proposed value of displayedMonth until it can be verified in commitProperties
	 */
	private var _proposedDisplayedMonth:int = -1;

    [Inspectable(category="General", defaultValue="0")]

    /**
	 *  @private
	 */
    public function get displayedMonth():int
    {
        return _proposedDisplayedMonth == -1 ? _displayedMonth : _proposedDisplayedMonth;
    }

    /**
     *  @private
     */
    public function set displayedMonth(value:int):void
    {
        if (value < 0 || value > 11)
            return;

        if (value == _displayedMonth)
            return;

		_proposedDisplayedMonth = value;
		selectedMonthYearChanged = true;
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
	 *  Holds the proposed value of displayedYear until it can be verified in commitProperties
	 */
	private var _proposedDisplayedYear:int = -1;

    [Inspectable(category="General", defaultValue="2006")]

    /**
	 *  @private
	 */
    public function get displayedYear():int
    {
        return _proposedDisplayedYear == -1 ? _displayedYear : _proposedDisplayedYear;
    }

    /**
     *  @private
     */
    public function set displayedYear(value:int):void
    {
        if (value <= 0)
            return;

        if (value == _displayedYear)
            return;
            
        _proposedDisplayedYear = value;
        selectedMonthYearChanged = true;
        invalidateProperties();
    }

    //----------------------------------
    //  firstDayOfWeek
    //----------------------------------

    /**
     *  @private
	 *  Storage for the firstDayOfWeek property.
     */
	private var _firstDayOfWeek:int = 0; // Sunday

    [Inspectable(category="General", defaultValue="0")]

    /**
	 *  @private
	 */
    public function get firstDayOfWeek():int
    {
        return _firstDayOfWeek;
    }

    /**
     *  @private
     */
    public function set firstDayOfWeek(value:int):void
    {
        if (value < 0 || value > 6)
            return;

        if (value == _firstDayOfWeek)
            return;

        _firstDayOfWeek = value;
        dayNamesChanged = true;
        selectedMonthYearChanged = true;
        
		invalidateProperties();
    }

    //----------------------------------
    //  fontContext
    //----------------------------------
    
    /**
     *  @private
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
    //  selectableRange
    //----------------------------------

    /**
     *  @private
	 *  Storage for the selectableRange property.
     */
	private var _selectableRange:Object = null;

	[Inspectable(category="General")]

    /**
	 *  @private
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
        if (!value)
        {
            _selectableRange = null;
            selectedMonthYearChanged = true;
            invalidateProperties();
            return;
        }

        var todaysDate:Date = new Date();
        var todaysMonth:int = todaysDate.getMonth();
        var todaysYear:int = todaysDate.getFullYear();
        var callMonth:int;
        var callYear:int;
        
		if (value is Date)
        {
            selRangeMode = 4;
            
			_selectableRange = new Date(value.getFullYear(),
										value.getMonth(),
										value.getDate());
            
			callMonth  = value.getMonth();
            callYear = value.getFullYear();
        }
        else if (value is Object)
        {
            _selectableRange = {};

            if (!value.rangeStart && value.rangeEnd)
            {
                selRangeMode = 3;
                _selectableRange.rangeEnd = value.rangeEnd;
                
				if (todaysYear <= _selectableRange.rangeEnd.getFullYear())
                {
                    if (todaysMonth >= _selectableRange.rangeEnd.getMonth())
                    {
                        callMonth = _selectableRange.rangeEnd.getMonth();
                        callYear = _selectableRange.rangeEnd.getFullYear();
                    }
                    else if (todaysMonth <=
							 _selectableRange.rangeEnd.getMonth())
                    {
                        callMonth = todaysMonth;
                        callYear = todaysYear;
                    }
                }
                else if (todaysYear > _selectableRange.rangeEnd.getFullYear())
                {
                    callMonth = _selectableRange.rangeEnd.getMonth();
                    callYear = _selectableRange.rangeEnd.getFullYear();
                }

            }
            else if (!value.rangeEnd && value.rangeStart)
            {
                selRangeMode = 2;
                _selectableRange.rangeStart = value.rangeStart;
                
				if (todaysYear >= _selectableRange.rangeStart.getFullYear())
                {
                    if (todaysMonth <= _selectableRange.rangeStart.getMonth())
                    {
                        callMonth = _selectableRange.rangeStart.getMonth();
                        callYear = _selectableRange.rangeStart.getFullYear();
                    }
                    else if (todaysMonth >=
							 _selectableRange.rangeStart.getMonth())
                    {
                        callMonth = todaysMonth;
                        callYear = todaysYear;
                    }
                }
                else if (todaysYear < _selectableRange.rangeStart.getFullYear())
                {
                    callMonth = _selectableRange.rangeStart.getMonth();
                    callYear = _selectableRange.rangeStart.getFullYear();
                }
            }
            else if (value.rangeStart && value.rangeEnd)
            {
                selRangeMode = 1;
                _selectableRange.rangeStart = value.rangeStart;
                _selectableRange.rangeEnd = value.rangeEnd;
                
				if (todaysDate >= _selectableRange.rangeStart &&
					todaysDate <= _selectableRange.rangeEnd)
                {
                    callMonth = todaysMonth;
                    callYear = todaysYear;
                }
                else if (todaysDate < _selectableRange.rangeStart)
                {
                    callMonth = _selectableRange.rangeStart.getMonth();
                    callYear = _selectableRange. rangeStart.getFullYear();
                }
                else if (todaysDate > _selectableRange.rangeEnd)
                {
                    callMonth = _selectableRange.rangeEnd.getMonth();
                    callYear = _selectableRange.rangeEnd.getFullYear();
                }
            }
        }

        _displayedMonth = callMonth;
        _displayedYear = callYear;

        selectedMonthYearChanged = true;
        invalidateProperties();
        
		// This removes the non-selectable ranges from the selected ranges.
        var selRange:Array = selectedRanges;
    }

    //----------------------------------
    //  selectedRanges
    //----------------------------------

    /**
     *  @private
	 *  Storage for the selectableRange property.
     */
	private var _selectedRanges:Array = [];

	[Inspectable(category="General", arrayType="Object")]

    /**
	 *  @private
	 */
    public function get selectedRanges():Array
    {
        if (_selectableRange)
        {
            switch (selRangeMode)
            {
                case 1:
				{
                	removeRangeFromSelection(null, _selectableRange.rangeStart);
                	removeRangeFromSelection(_selectableRange.rangeEnd, null);
                	break;
				}

                case 2:
                case 3:
				{
                	removeRangeFromSelection(_selectableRange.rangeEnd,
											 _selectableRange.rangeStart);
                    break;
				}

                case 4:
				{
                	removeRangeFromSelection(null, _selectableRange as Date);
                	removeRangeFromSelection(_selectableRange as Date, null);
                    break;
				}
            }
        }

        var i:int;
		
		for (i = 0; i < _disabledRanges.length; i++)
        {
            switch (disabledRangeMode[i])
            {
                case 1:
                case 2:
                case 3:
				{
                	removeRangeFromSelection(_disabledRanges[i].rangeStart,
											 _disabledRanges[i].rangeEnd);
					break;
				}

				case 4:
				{
					removeRangeFromSelection(_disabledRanges[i],
											 _disabledRanges[i]);
					break;
				}
            }
        }

        if (_disabledDays.length > 0 && selectedRangeCount)
        {
			var minDate:Date = _selectedRanges[0].rangeStart;
			var maxDate:Date = _selectedRanges[0].rangeEnd;

			for (i = 1; i < selectedRangeCount; i++)
   			{
				if (minDate > _selectedRanges[i].rangeStart)
				    minDate = _selectedRanges[i].rangeStart;

				if (maxDate < _selectedRanges[i].rangeEnd)
				    maxDate = _selectedRanges[i].rangeEnd;
			}

            for (i = 0; i < _disabledDays.length; i++)
            {
				var tempDate:Date = minDate;

				var dayOffset:int = _disabledDays[i] - tempDate.getDay();

				if (dayOffset < 0)
				    dayOffset += 7;

				tempDate = incrementDate(tempDate,dayOffset)

				while (tempDate < maxDate)
				{
					removeRangeFromSelection(tempDate,tempDate);
					tempDate = incrementDate(tempDate,7)
    			}
            }
        }

		_selectedRanges.length = selectedRangeCount;
        return _selectedRanges;
    }

    /**
     *  @private
     */
    public function set selectedRanges(value:Array):void
    {
        _selectedRanges = value;

        selectedRangeCount = _selectedRanges.length;

        setSelectedIndicators();
    }

    //----------------------------------
    //  showToday
    //----------------------------------

    /**
     *  @private
	 *  Storage for the showToday property.
     */
	private var _showToday:Boolean = true;

    [Inspectable(category="General", defaultValue="true")]

    /**
	 *  @private
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
        if (_showToday != value)
            _showToday = value;

        selectedMonthYearChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  selectedDate
    //----------------------------------

    [Inspectable(category="General", defaultValue="null")]

    /**
	 *  @private
     */
    public function get selectedDate():Date
    {
		return selectedRangeCount ? _selectedRanges[0].rangeStart : null;
    }

    /**
     *  @private
     */
    public function set selectedDate(value:Date):void
    {
 		selectedRangeCount = 0;

        if (value && !checkDateIsDisabled(value))
        {
	 		addToSelected(value);
            
			_displayedMonth = value.getMonth();
            _displayedYear = value.getFullYear();
  		    selectedMonthYearChanged = true;
        	
			invalidateProperties();
        }
        else
		{
	 		setSelectedIndicators();
		}
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /*

    A note about DayBlocks:

    dayBlock0..dayBlock6 are skins for the 7 grid columns.
    They are dynamically created instances of the "DayBlock" symbol.
    Three additional properties are set when each dayBlock is created
    in createChildren():

        columnIndex:int (0-6)
            keeps track of where this dayBlock is in the grid

        selectedArray:Array
            7 elements, one for each row
            selectedArray[rowIndex] is true
                if that row in the dayBlock is selected

        disabledArray:Array
            7 elements, one for each row
            disabledArray[rowIndex] is true
                if that row in the dayBlock is disabled

    Additional properties are created dynamically to hold the skins
    for selected and disabled rows in the dayBlock:

        selectedSkin0..selectedSkin6

        disabledSkin0..disabledSkin6

    */

    /**
	 *  @private
	 *  Everything that gets initially created now exists.
	 *  Some things, such as the selection skins and disabled skins,
	 *  are created later as they are needed.
	 *  Set the "S", "M", etc. labels in the first row.
	 *  
	 *  Set the day-number labels ("1".."31") in the other rows.
	 *  This method also displays the selection skins,
	 *  the disabled skins, and the "today" indicator.
	 */
    override protected function createChildren():void
    {
		super.createChildren();

        var labelPosition:Number = 0;

		createDayLabels(-1);
		createTodayIndicator(0);
		
		if (!rollOverIndicator)
		{
			var rollOverIndicatorClass:Class = getStyle("rollOverIndicatorSkin");
			if (!rollOverIndicatorClass)
				rollOverIndicatorClass = DateChooserIndicator;
            rollOverIndicator = IFlexDisplayObject(new rollOverIndicatorClass());
			// too lazy to make an interface for this.
			if (isDateChooserIndicator(rollOverIndicator))
				Object(rollOverIndicator).indicatorColor = "rollOverColor";
			if (rollOverIndicator is ISimpleStyleClient)
				ISimpleStyleClient(rollOverIndicator).styleName = this;
			addChildAt(DisplayObject(rollOverIndicator), 0);
			rollOverIndicator.visible = false;
		}

        // Wait for all of the properties to be set before calling setSelectedMonthAndYear
        dayNamesChanged = true;
        selectedMonthYearChanged = true;
    }

 	/**
	 *  @private
	 */
    override protected function commitProperties():void
    {
		super.commitProperties();

		if (hasFontContextChanged() && todayIndicator != null)
        {
       		// Re-create the children so we can display
			// the embedded font from the new font context.
       		removeSelectionIndicators();
       		
       		var childIndex:int = getChildIndex(DisplayObject(todayIndicator));
       		removeTodayIndicator();
       		createTodayIndicator(childIndex);

   			childIndex = getChildIndex(dayBlocksArray[0][0]);
   			removeDayLabels();
   			createDayLabels(childIndex);
       		
       		enabledChanged = true;
       		dayNamesChanged = true;
       		selectedMonthYearChanged = true;
        }

        if (enabledChanged)
        {
            enabledChanged = false;

            for (var o:int = 0; o < 7; o++)
            {
                for (var r:int = 0; r < 7; r++)
                {
                    dayBlocksArray[o][r].enabled = _enabled;
                    disabledArrays[o][r] = _enabled;
                }
            }

            if (!_enabled)
            {

                if (todayIndicator)
                    todayIndicator.alpha = 0.3;

                // Remove the mouse event listeners
                removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
                removeEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
                removeEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
				removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
            }
            else
            {
                if (todayIndicator)
                    todayIndicator.alpha = 1.0;

                selectedMonthYearChanged = true;

                // Restore the mouse event listeners
                addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
                addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
                addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
				addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
            }
        }

        if (dayNamesChanged)
        {
            dayNamesChanged = false;

            drawDayNames();
        }

        if (selectedMonthYearChanged)
        {
            selectedMonthYearChanged = false;

			var proposedDate:Date = 
				new Date(_proposedDisplayedYear == -1 ? _displayedYear : _proposedDisplayedYear,
						 _proposedDisplayedMonth == -1 ? _displayedMonth : _proposedDisplayedMonth);
						 
			if (isDateInRange(proposedDate, _selectableRange, selRangeMode, true))
			{			 
	            setSelectedMonthAndYear();
	  		}	
            
            _proposedDisplayedYear = -1;
            _proposedDisplayedMonth = -1;
        }
    }

    /**
	 *  @private
	 */
	override protected function measure():void
    {
		super.measure();

        var verticalGap:Number = getStyle("verticalGap");
        var horizontalGap:Number = getStyle("horizontalGap");

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingBottom:Number = getStyle("paddingBottom");
        var paddingTop:Number = getStyle("paddingTop");

        var lineMetrics:TextLineMetrics;

        cellWidth = 0;
        cellHeight = 0;

        for (var dayOfWeek:int = 0; dayOfWeek < 7; dayOfWeek++)
        {
			// dayNames will be null if there are no resources.
            var dayName:String = dayNames ? dayNames[dayOfWeek] : "";
            lineMetrics = measureText(dayName);
            if (lineMetrics.width > cellWidth)
                cellWidth = lineMetrics.width;
            if (lineMetrics.height > cellHeight)
				cellHeight = lineMetrics.height;
        }

        lineMetrics = measureText("30");

        if (lineMetrics.width > cellWidth)
            cellWidth = lineMetrics.width;

        if (lineMetrics.height > cellHeight)
            cellHeight = lineMetrics.height;

		measuredWidth = paddingLeft + horizontalGap * 6 +
						cellWidth * 7 + paddingRight;
        measuredHeight = verticalGap * 6 + cellHeight * 7 +
						 paddingBottom + paddingTop;
        measuredMinWidth = cellWidth * 7;
        measuredMinHeight = cellHeight * 7;
    }

    /**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

		//var verticalGap:Number = getStyle("verticalGap");
        //var horizontalGap:Number = getStyle("horizontalGap");

		var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingBottom:Number = getStyle("paddingBottom");
		var paddingTop:Number = getStyle("paddingTop");

        var blockX:Number = paddingLeft;

        // Bug 134794, clip height/width so that RTE's are not thrown
        cellWidth = Math.max((unscaledWidth - (paddingLeft + paddingRight))/7, 4);
        cellHeight = Math.max((unscaledHeight - paddingBottom)/7, 4);

        var labelPosition:Number = paddingTop;

        rollOverIndicator.setActualSize(cellWidth, cellHeight);
        todayIndicator.setActualSize(cellWidth, cellHeight);

        for (var columnIndex:int = 0; columnIndex < 7; columnIndex++)
        {
            // Remember the height of the cells if not set by user.
            // Create the 7 labels within each DayBlock.
            // The first row in each column displays a day name string, such as "Sun".
            // The other six rows displays day numbers in the range 1-31.

            for (var rowIndex:int = 0; rowIndex < 7; rowIndex++)
            {
                var label:IUITextField = dayBlocksArray[columnIndex][rowIndex];
                if (rowIndex == 0)
                    labelPosition = paddingTop;
                else
                    labelPosition += cellHeight;

                label.setActualSize(cellWidth, cellHeight);
                label.move(blockX, labelPosition);

				if (selectionIndicator[columnIndex][rowIndex])
				{
	     			selectionIndicator[columnIndex][rowIndex].setActualSize(cellWidth, cellHeight);
    	 			selectionIndicator[columnIndex][rowIndex].move(blockX, labelPosition + yOffset);
    	 		}
               //label.width = cellWidth;
                //label.height = cellHeight;
                //label.x = blockX;
                //label.y = labelPosition;
            }

            blockX += cellWidth;
        }

        drawDayNames();
        setSelectedMonthAndYear();
    }

    /**
	 *  @private
	 */
    override public function styleChanged(styleProp:String):void
    {
		var allStyles:Boolean = !styleProp || styleProp == "styleName";

        if (allStyles || styleProp == "todayStyleName")
        {
            selectedMonthYearChanged = true;
            invalidateProperties();
        }

        if (allStyles || styleProp == "weekDayStyleName")
        {
            var weekDayStyleName:Object = getStyle("weekDayStyleName");
            if (!weekDayStyleName)
                weekDayStyleName = this;

            if (dayBlocksArray)
			{
				for (var i:int = 0; i < 7; i++)
				{
					// Set the styleName on the top row of day name labels
					if (dayBlocksArray[i] && dayBlocksArray[i][0])
						dayBlocksArray[i][0].styleName = weekDayStyleName;
				}
			}
        }

        super.styleChanged(styleProp);
    }

    /**
	 *  @private
     */
	override protected function resourcesChanged():void
	{
		super.resourcesChanged();

		dayNames = dayNamesOverride;
	}

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Creates the day labels and adds them as children of this component.
     * 
     *  @param childIndex The index of where to add the children.
	 *  If -1, the text fields are appended to the end of the list.
     */
    mx_internal function createDayLabels(childIndex:int):void
    {
        var weekDayStyleName:Object = getStyle("weekDayStyleName");

	    // Remember the height of the cells if not set by user.
        // Create the 7 labels within each DayBlock.
        // The first row in each column displays a day name string,
		// such as "Sun".
        // The other six rows displays day numbers in the range 1-31.

        // Calendar days
        for (var columnIndex:int = 0; columnIndex < 7; columnIndex++)
        {
	        dayBlocksArray[columnIndex] = [];
    	    selectionIndicator[columnIndex] = [];

		    for (var rowIndex:int = 0; rowIndex < 7; rowIndex++)
	        {
	            var label:IUITextField =
				dayBlocksArray[columnIndex][rowIndex] =
					IUITextField(createInFontContext(UITextField));
	            
				label.selectable = false;
	            label.ignorePadding = true;
	
				if (childIndex == -1)
		            addChild(DisplayObject(label));			
				else
					addChildAt(DisplayObject(label), childIndex++);
	
	            if (rowIndex == 0)
				{
	                label.styleName = weekDayStyleName ?
									  weekDayStyleName :
									  this;
				}
	            else
				{
	                label.styleName = this;
				}
	        }

            disabledArrays[columnIndex] = new Array(7);
        }
    }

    /**
     *  @private
     *  Removes the day labels from this component.
     */
    mx_internal function removeDayLabels():void
    {
       for (var columnIndex:int = 0; columnIndex < 7; columnIndex++)
        {
		    for (var rowIndex:int = 0; rowIndex < 7; rowIndex++)
	        {
				removeChild(dayBlocksArray[columnIndex][rowIndex]);
				dayBlocksArray[columnIndex][rowIndex] = null;
    	    }
        }
    }
    
    /**
     *  @private
     *  @param childIndex The index of where to add the child.
     */
    mx_internal function createTodayIndicator(childIndex:int):void
    {
    	if (!todayIndicator)
    	{
			var todayIndicatorClass:Class = getStyle("todayIndicatorSkin");
			if (!todayIndicatorClass)
				todayIndicatorClass = DateChooserIndicator;
            todayIndicator = IFlexDisplayObject(new todayIndicatorClass());
			
			if (isDateChooserIndicator(todayIndicator))
			{
				Object(todayIndicator).indicatorColor =
					"todayColor";
			}
			if (todayIndicator is ISimpleStyleClient)
				ISimpleStyleClient(todayIndicator).styleName = this;
			
			addChildAt(DisplayObject(todayIndicator), childIndex);
			
			todayIndicator.visible = false;
    	}
    }

    /**
     *  @private
     */
    mx_internal function removeTodayIndicator():void
    {
		if (todayIndicator)
		{
			removeChild(DisplayObject(todayIndicator));
			todayIndicator = null;
		}    	
    }

    /**
	 *  @private
	 */
    mx_internal function drawDayNames():void
    {
        for (var columnIndex:int = 0; columnIndex < 7; columnIndex++)
        {
            var dayOfWeek:int = (columnIndex + firstDayOfWeek) % 7;
        	// dayNames will be null if there are no resources.
            var dayName:String = dayNames ? dayNames[dayOfWeek] : "";
            dayBlocksArray[columnIndex][0].text = dayName;
        }
    }

    /**
	 *  @private
	 */
    mx_internal function setSelectedMonthAndYear(monthVal:int = -1, yearVal:int = -1):void
    {
        // This lengthy method updates the UI to display a specified month
        // and year. In particular, it updates the day numbers (1-31) in the grid.

        // It does NOT update the day names ("Sun", "Mon", etc.),
        // since these do not change when the month and year change.
        //
        // It also takes care of displaying days that are disabled or selected.
        // Instances of the skins cal_monthDayDisabled (for disabled days) and
        // cal_monthDaySelected (for selected days) get created as they
        // are required, to minimize initialization time.

        var dayNumber:int; // 1 - 31
        var columnIndex:int; // 0 - 6
        var rowIndex:int; // 1 - 6
        var i:int;
        var displayTodayInCurrentMonth:Boolean = false;
        var displayDate:Date = null;


        // When another method needs to redraw the UI without changing the
        // currently selected month and year (because the firstDayOfWeek
        // property changed, for example) it calls setSelectedMonthAndYear()
        // with no arguments. Therefore we need to handle undefined arguments.
        var newMonth:int = monthVal == -1 ? displayedMonth : monthVal;
        var newYear:int = yearVal == -1 ? displayedYear : yearVal;

        // Determine where in the grid the 1st of the month should appear,
        // how many days are in the month, and today's date.

        enabledDaysInMonth = [];
        var offset:int = getOffsetOfMonth(newYear, newMonth);
        var daysInMonth:int = getNumberOfDaysInMonth(newYear, newMonth);

        // Determine whether this month contains today.
        var today:Date = new Date();
        var currentMonthContainsToday:Boolean = (today.getFullYear() == newYear && today.getMonth() == newMonth);

        // Set up the days (if any) in row 1 that come from the previous month.
        var previousMonth:int = Math.max(newMonth - 1,0);
        var previousMonthDate:Date = new Date(newYear, previousMonth, 1);
        dayNumber = getNumberOfDaysInMonth(previousMonthDate.getFullYear(), previousMonthDate.getMonth());
        rowIndex = 1;

        for (columnIndex = 0; columnIndex < offset; columnIndex++)
        {
            dayBlocksArray[columnIndex][rowIndex].text = "";

            // Disable the day.
            disabledArrays[columnIndex][rowIndex] = true;

            removeSelectionIndicator(columnIndex,rowIndex);
        }

        // Set up the days of the new month.
        for (dayNumber = 1; dayNumber <= daysInMonth; dayNumber++)
        {
            var cellDate:Date = new Date(newYear, newMonth, dayNumber);
            i = offset + dayNumber - 1;
            columnIndex = i % 7;
            rowIndex = 1 + Math.floor(i / 7);

            var todayLabel:IUITextField = dayBlocksArray[columnIndex][rowIndex];//this["dayBlock" + columnIndex+"label" + rowIndex];
            todayLabel.text = dayNumber.toString();
            // Enable the day.

            if (_enabled)
            {
                disabledArrays[columnIndex][rowIndex] = false;
                todayLabel.enabled = true;
            }

            if (!todayLabel.styleName)
                todayLabel.styleName = this;

            // Some of these days may be selected.
            // One of these days may be today's date.
            if (currentMonthContainsToday && (cellDate.getDate() == today.getDate()) && _showToday)
            {
                todayRow = rowIndex;
                todayColumn = columnIndex;
                displayTodayInCurrentMonth = true;

                todayIndicator.visible = _showToday;
                todayLabel.styleName = getStyle("todayStyleName");

                todayIndicator.move(todayLabel.x, todayLabel.y + yOffset); // Don't trigger layout
                todaysLabelReference = todayLabel;

            }
            else
            {
                if (!displayTodayInCurrentMonth)
                {
                    if (todaysLabelReference)
                    {
                        todaysLabelReference.styleName = this;
                        //todaysLabelReference.styleName = this;
                    }
                    todayIndicator.visible = false;
                }
            }

            var cellString:String;

            // Selectable Range
            // set up the selectable Range: type: Object/ Date Object
            // Object Attrib: rangeStart[Date Object], rangeEnd[Date Object]
            // 1 :: if both attribs are defined
            // 2 :: If only rangeStart is defined: All dates after the specified date are enabled
            // 3 :: if only rangeEnd is defined: All dates before ths specified date are enabled
            // 4 :: If selectable Range param is a Date Object, then only that day has to be defined

            if (_selectableRange)
            {
            	if (!isDateInRange(cellDate, _selectableRange, selRangeMode))
            	{
            		todayLabel.enabled = false;
                    disabledArrays[columnIndex][rowIndex] = true;
            	}
            }

            // Disabled Ranges
            // set up the disabledRanges: type: Array
            // Array can contain an Object || Date Object.
            // Attrib for Object: rangeStart[Date Object], rangeEnd[Date Object]
            // "start"::All dates after the specified date are disabled, including the startDate
            // "end"::All dates before ths specified date are disabled, including the end Date
            // "date"::Only that day has to be disabled
            // "normal"::range is disabled including the start and end date
            if (_disabledRanges.length>0)
            {
                for (var dRanges:int = 0; dRanges < _disabledRanges.length; dRanges++)
                {
                	if (isDateInRange(cellDate, _disabledRanges[dRanges], disabledRangeMode[dRanges]))
                	{
                		todayLabel.enabled = false;
                        disabledArrays[columnIndex][rowIndex] = true;
                	}
                }
            }

            var valToPush:Object = {};
            if (todayLabel.enabled)
            {
                valToPush.name = todayLabel.name;
                valToPush.text = todayLabel.text;
                valToPush.x = todayLabel.x;
                valToPush.y = todayLabel.y;
            }
            enabledDaysInMonth.push(valToPush);
        }

        // Set up the days (if any) at the end of the grid
        // that come from the following month.
        dayNumber = 1;
        for (i = offset + daysInMonth; i < 42; i++)
        {
            columnIndex = i % 7;
            rowIndex = 1 + Math.floor(i / 7);

            dayBlocksArray[columnIndex][rowIndex].text = "";


            // Disable the day.
            disabledArrays[columnIndex][rowIndex] = true;
            removeSelectionIndicator(columnIndex,rowIndex);
        }

        if (_disabledDays.length>0)
        {
            for (i = 0; i < _disabledDays.length; i++)
            {
                if (_disabledDays[i] >= 0 && _disabledDays[i] <= 6 && _disabledDays[i] != -1)
                {
                    columnIndex = ((7 + _disabledDays[i] - _firstDayOfWeek) % 7);

                    for (rowIndex = 1; rowIndex < 7; rowIndex++)
                    {
                        // Disable the day.
                        disabledArrays[columnIndex][rowIndex] = true;
                        var tempCalcDate:Number = Number(dayBlocksArray[columnIndex][rowIndex].text);
                        var tempOffset:Number;
                        if (!isNaN(tempCalcDate))
                        {
                            tempOffset = offset + tempCalcDate % 7;
                            enabledDaysInMonth[tempCalcDate-1] = null;
                        }
                        dayBlocksArray[columnIndex][rowIndex].enabled = false;
                    }
                }
            }
        }

        _displayedMonth = newMonth;
        _displayedYear = newYear;
        displayDate = new Date(newYear, newMonth, 1);
        todayIndicator.alpha = (todaysLabelReference) ? ((todaysLabelReference.enabled == false) ? 0.3 : 1.0) : 1.0;
		setSelectedIndicators();
        invalidateDisplayList();
    }

    /**
	 *  @private
	 *  Called from setSelectedMonthAndYear() to get an Offset of the starting day of the month
	 */
    mx_internal function getOffsetOfMonth(year:int, month:int):int
    {
        // Determine whether the 1st of the month is a Sunday, Monday, etc.
        // and then determine which column of the grid where it appears.
        var first:Date = new Date(year, month, 1);
        var offset:int = first.getDay() - _firstDayOfWeek;
        return offset < 0 ? offset + 7 : offset;
    }

    /**
	 *  @private
	 */
    mx_internal function getNumberOfDaysInMonth(year:int, month:int):int
    {
        // "Thirty days hath September..."

        var n:int;

        if (month == 1) // Feb
        {
            if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) // leap year
                n = 29;
            else
                n = 28;
        }

        else if (month == 3 || month == 5 || month == 8 || month == 10)
            n = 30;

        else
            n = 31;

        return n;
    }

    /**
	 *  @private
	 */
    mx_internal function stepDate(deltaY:int, deltaM:int, triggerEvent:Event = null):void
    {
        var oldYear:int = displayedYear;
        var oldMonth:int = displayedMonth;

        var newYear:int = oldYear + deltaY;
        var newMonth:int = oldMonth + deltaM;

        while (newMonth < 0)
        {
            newYear--;
            newMonth += 12;
        }

        while (newMonth > 11)
        {
            newYear++;
            newMonth -= 12;
        }

        _displayedMonth = newMonth;
        _displayedYear = newYear;
        selectedMonthYearChanged = true;
        invalidateProperties();

        var event:DateChooserEvent = new DateChooserEvent(DateChooserEvent.SCROLL);
        event.triggerEvent = triggerEvent;
        if (newYear > oldYear)
            event.detail = DateChooserEventDetail.NEXT_YEAR;
        else if (newYear < oldYear)
            event.detail = DateChooserEventDetail.PREVIOUS_YEAR;
        else if (newMonth > oldMonth)
            event.detail = DateChooserEventDetail.NEXT_MONTH;
        else if (newMonth < oldMonth)
            event.detail = DateChooserEventDetail.PREVIOUS_MONTH;
    	dispatchEvent(event);
    }

    /**
	 *  @private
	 */
    mx_internal function dispatchChangeEvent(triggerEvent:Event = null):void
    {
        var change:CalendarLayoutChangeEvent =
			new CalendarLayoutChangeEvent(CalendarLayoutChangeEvent.CHANGE);
        change.newDate = lastSelectedDate;
        change.triggerEvent = triggerEvent;
        dispatchEvent(change);
    }

	/**
	 *  @private
	 * 
	 *  Returns true if the date is within the dates specified by the dateRange object. 
	 */ 
	mx_internal function isDateInRange(value:Date, dateRange:Object, rangeMode:int, ignoreDay:Boolean = false):Boolean
	{
		var result:Boolean = true;
		
		if (dateRange)
        {
        	if (ignoreDay)
        	{
        		var dateRangeCopy:Object = {};
        		if (dateRange.rangeStart)
        		{
        			var startDate:Date = dateRange.rangeStart;
        			dateRangeCopy.rangeStart = new Date(startDate.fullYear, startDate.month, 1);
        		}
        		if (dateRange.rangeEnd)
        		{
        			var endDate:Date = dateRange.rangeEnd;
        			dateRangeCopy.rangeEnd = new Date(endDate.fullYear, endDate.month, getNumberOfDaysInMonth(endDate.fullYear, endDate.month));
        		}
        		dateRange = dateRangeCopy;
        	}
        	
            switch (rangeMode)
            {
                case 1:
                {
                    if (value < dateRange.rangeStart ||
						value > dateRange.rangeEnd)
                    {
                        result = false;
                    }
                    break;
                }

                case 2:
                {
                    if (value < dateRange.rangeStart)
                        result = false;
                    break;
                }

                case 3:
                {
                    if (value > dateRange.rangeEnd)
                        result = false;
                    break;
                }

                case 4:
                {
                    if (value > dateRange || value < dateRange)
                        result = false;
                    break;
                }

                default:
                {
                    break;
                }
            }
        }
        
		return result;
	}


    /**
	 *  @private
	 *
	 *  Checking for valid dates, months and Years before setting them through API
 	 *  Returns true is date is disabled. null date is considered enabled,
	 *  as one can set date to null.
	 */
    mx_internal function checkDateIsDisabled(value:Date):Boolean
    {
        if (!value)
            return false;

        var selectedDateIsDisabled:Boolean = false;

        if (_selectableRange)
        {
        	if (!isDateInRange(value, _selectableRange, selRangeMode))
        	{
        		selectedDateIsDisabled = true;
        	}
        }

        if (_disabledRanges.length > 0)
        {
            for (var dRanges:int = 0; dRanges < _disabledRanges.length; dRanges++)
            {
            	if (isDateInRange(value, _disabledRanges[dRanges], disabledRangeMode[dRanges]))
            	{
            		selectedDateIsDisabled = true;
            	}
            }
        }

        if (_disabledDays.length > 0)
        {
            for (var i:int = 0; i < _disabledDays.length; i++)
            {
                if (value.getDay() == _disabledDays[i])
                    selectedDateIsDisabled = true;
            }
        }

        return selectedDateIsDisabled;
    }

    /**
     *  @private
     *  Adds the newDate to the list of selected dates.
     *  If range is true, a range of dates starting from the previous selection is selected.
	 */
    mx_internal function addToSelected(newDate:Date, range:Boolean = false):void
    {

		if (!selectedRangeCount)
			rangeStartDate = null;

		lastSelectedDate = newDate;

		if (range == false)
		{
			_selectedRanges[selectedRangeCount] = {};
			_selectedRanges[selectedRangeCount].rangeStart =
				new Date(newDate);
			_selectedRanges[selectedRangeCount].rangeEnd =
				_selectedRanges[selectedRangeCount].rangeStart;
			selectedRangeCount++;
		}
		else
		{
		    if (selectedRangeCount == 0)
		    {
				_selectedRanges[0] = {};
				_selectedRanges[0].rangeStart = new Date(newDate);
			}
			else
			{
	  			selectedRangeCount = 1;

				if (!rangeStartDate)
   				    rangeStartDate = _selectedRanges[0].rangeStart;

   				_selectedRanges[0].rangeStart = new Date(rangeStartDate);

				if (newDate < _selectedRanges[0].rangeStart)
				{
					_selectedRanges[0].rangeEnd = _selectedRanges[0].rangeStart;
					_selectedRanges[0].rangeStart = new Date(newDate);
					return;
				}
			}

			_selectedRanges[0].rangeEnd = new Date(newDate);
		}
	}

    /**
     *  @private
     *  Increments/decrements a date by 'No. of days'
	 *  specified by amount and returns the new date.
     */
	mx_internal function incrementDate(value:Date, amount:int = 1):Date
	{
		var newDate:Date = new Date(value);
		var time:Number = newDate.getTime();
		newDate.setTime(time + amount * 86400000);
		return newDate;
	}

    /**
     *  @private
	 *  Returns true if newDate is selected.
     */
	mx_internal function isSelected(newDate:Date):Boolean
	{
		for (var i:int = 0; i < selectedRangeCount; i++)
		{
			if (newDate >= _selectedRanges[i].rangeStart &&
				newDate <=_selectedRanges[i].rangeEnd)
			{
			    return true;
			}
		}

		return false;
	}

    /**
     *  @private
	 *  Removes the range of dates specified by startDate and endDate
	 *  from the selected dates.
     */
	mx_internal function removeRangeFromSelection(startDate:Date, endDate:Date):void
    {
		for (var n:int = 0; n < selectedRangeCount; n++)
		{
			var s1:int;

			if (!startDate || startDate <= _selectedRanges[n].rangeStart)
			    s1 = 1;
			else if (startDate <= _selectedRanges[n].rangeEnd)
			    s1 = 2;
			else if (startDate > _selectedRanges[n].rangeEnd)
			    s1 = 3;

			if (endDate < _selectedRanges[n].rangeStart)
			    s1 *= 5;
			else if (endDate < _selectedRanges[n].rangeEnd)
			    s1 *= 7;
			else if (!endDate || endDate >= _selectedRanges[n].rangeEnd)
			    s1 *= 11;

			switch (s1)
			{
				case 5:
				case 33:
				    break;

				case 14:
				{
					var temp:Date = _selectedRanges[n].rangeEnd;

					_selectedRanges[n].rangeEnd = incrementDate(startDate,-1);

					_selectedRanges[selectedRangeCount] = {};
					_selectedRanges[selectedRangeCount].rangeStart = incrementDate(endDate);
					_selectedRanges[selectedRangeCount].rangeEnd = temp;
					selectedRangeCount += 1;
					break;
				}

				case 7:
				{
					_selectedRanges[n].rangeStart = incrementDate(endDate);
					break;
				}

				case 22:
				{
					_selectedRanges[n].rangeEnd = incrementDate(startDate,-1);
					break;
				}

				case 11:
				{
					_selectedRanges[n] = _selectedRanges[selectedRangeCount-1];
					_selectedRanges[selectedRangeCount-1] = null;
					selectedRangeCount -= 1;
					break;
				}
			}
		}
	}

    /**
     *  @private
	 *  Updates the visible property of all the selected indicators.
	 *  Called when a date range has been selected or deselected.
     */
	mx_internal function setSelectedIndicators():void
	{
        var offset:int = getOffsetOfMonth(displayedYear, displayedMonth);
        var daysInMonth:int = getNumberOfDaysInMonth(displayedYear, displayedMonth);

        var columnIndex:int; // 0 - 6
        var rowIndex:int; // 1 - 6
        var i:int;
        for (var dayNumber:int = 1; dayNumber <= daysInMonth; dayNumber++)
        {
            var cellDate:Date = new Date(displayedYear, displayedMonth, dayNumber);
            i = offset + dayNumber - 1;
            columnIndex = i % 7;
            rowIndex = 1 + Math.floor(i / 7);

           	if (isSelected(cellDate) && disabledArrays[columnIndex][rowIndex] == false)
				addSelectionIndicator(columnIndex,rowIndex);
			else
				removeSelectionIndicator(columnIndex,rowIndex);
		}

		var today:Date = new Date();
        if (isSelected(today))
        	todayIndicator.alpha = 1.0;
	}

    /**
     *  @private
     */
    mx_internal function addSelectionIndicator(columnIndex:int, rowIndex:int):void
    {
        if (!selectionIndicator[columnIndex][rowIndex])
        {

			var selectionIndicatorClass:Class =
					getStyle("selectionIndicatorSkin");
			if (!selectionIndicatorClass)
				selectionIndicatorClass = DateChooserIndicator;
            selectionIndicator[columnIndex][rowIndex] =
				IFlexDisplayObject(new selectionIndicatorClass());
			
			if (isDateChooserIndicator(selectionIndicator[columnIndex][rowIndex]))
				Object(selectionIndicator[columnIndex][rowIndex]).indicatorColor =
					"selectionColor";
			if (selectionIndicator[columnIndex][rowIndex] is ISimpleStyleClient)
				ISimpleStyleClient(selectionIndicator[columnIndex][rowIndex]).styleName = this;
			
			addChildAt(DisplayObject(selectionIndicator[columnIndex][rowIndex]), 0);

			var selCell:IUITextField = dayBlocksArray[columnIndex][rowIndex];
            selectionIndicator[columnIndex][rowIndex].move(selCell.x, selCell.y + yOffset);
			selectionIndicator[columnIndex][rowIndex].setActualSize(cellWidth, cellHeight);
	    }
        selectionIndicator[columnIndex][rowIndex].visible = true;
    }

    /**
     *  @private
     */
	mx_internal function removeSelectionIndicator(columnIndex:int,
												  rowIndex:int):void
	{
		if (selectionIndicator[columnIndex][rowIndex])
		{
			removeChild(selectionIndicator[columnIndex][rowIndex]);
	   		selectionIndicator[columnIndex][rowIndex] = null;
	   	}
	}

    /**
     *  @private
     * 
     *  Removes the selection indicators from this component.
     */
    mx_internal function removeSelectionIndicators():void
    {
       for (var columnIndex:int = 0; columnIndex < 7; columnIndex++)
        {
		    for (var rowIndex:int = 0; rowIndex < 7; rowIndex++)
	        {
	        	removeSelectionIndicator(columnIndex, rowIndex);
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
        /*
             PageUp: Previous Month
             PageDown: Next Month
        */

        var selChanged:Boolean = false;

        var date:int = lastSelectedDate ? lastSelectedDate.getDate() : 1;

        /*
        calculate Days to move will take the following values:

        1: Left
        2: Right
        3: Up
        4: Down
        5. Home
        6. End

        */

   		var daysInMonth:int = getNumberOfDaysInMonth(displayedYear, displayedMonth);

		for (var i:uint = 0; i < 31; i++)
		{
	        if (event.keyCode == Keyboard.LEFT)
	        {
				if (date > 1)
				{
				    date--;
					selChanged = true;
				}
				else
					return;
	        }
	
	        else if (event.keyCode == Keyboard.RIGHT)
	        {
	    		if (date < daysInMonth)
	    		{
	    		    date++;
					selChanged = true;
				}
				else
					return;
	        }
	
	        else if (event.keyCode == Keyboard.UP)
	        {
	            if (date > 7)
	            {
				    date -= 7;
					selChanged = true;
				}
				else
					return;
	        }
	
	        else if (event.keyCode == Keyboard.DOWN)
	        {
	    		if (date + 7 <= daysInMonth)
	    		{
	    		    date += 7;
					selChanged = true;
				}
				else
					return;
			}
	
	        else if (event.keyCode == Keyboard.HOME)
	        {
	        	if (i == 0)
	            	date = 1;
	            else
	            	date++;
	 			selChanged = true;
	        }
	
	        else if (event.keyCode == Keyboard.END)
	        {
	        	if (i == 0)
	            	date = daysInMonth;
	            else
	            	date--;
	 			selChanged = true;
	        }
	
	        else if (lastSelectedDate && event.shiftKey &&
	        		 (event.keyCode == Keyboard.PAGE_UP ||
					  event.keyCode == Keyboard.PAGE_DOWN))
			{
	        	selChanged = true;
			}
			
	        else if (lastSelectedDate &&
	        		 (event.keyCode == 189 ||
					  event.keyCode == 187)) // for year - and +
			{
	        	selChanged = true;
			}
	
	        if (event.keyCode >= Keyboard.PAGE_UP &&
				event.keyCode <= Keyboard.DOWN)
			{
	        	event.stopPropagation();
			}
	
			if (selChanged)
	        {
		        var newDate:Date = new Date(displayedYear, displayedMonth, date);
	
				if (checkDateIsDisabled(newDate) && !event.shiftKey)
					continue;
	
	    		if (!(event.shiftKey && _allowMultipleSelection))
	    			selectedRangeCount = 0;
	
	            addToSelected(newDate, event.shiftKey && _allowMultipleSelection);
	
				setSelectedIndicators();
	
				dispatchChangeEvent(event);
				return;
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
	private function mouseOverHandler(event:MouseEvent):void
    {
        if (event.relatedObject && event.relatedObject.parent != this)
            addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
        else
            event.stopImmediatePropagation();
    }

    /**
	 *  @private
	 */
    private function mouseOutHandler(event:MouseEvent):void
    {
        if (event.relatedObject && event.relatedObject.parent != this)
        {

            removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
            rollOverIndicator.visible = false;


            // If todayColumn and todayRow exist and today is not disabled
            if (todayColumn != -1 && todayRow != -1 && !disabledArrays[todayColumn][todayRow])
            {
            	var today:Date = new Date();
                if (!isSelected(today))
                    todayIndicator.alpha = 1.0;
            }
        }
        else
        {
            event.stopImmediatePropagation();
        }
    }

    /**
	 *  @private
	 */
    private function mouseMoveHandler(event:MouseEvent):void
    {
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingTop:Number = getStyle("paddingTop");

		var firstColX:Number = dayBlocksArray[0][0].x;
        var lastColX:Number = dayBlocksArray[6][0].x;
        var firstRowY:Number = dayBlocksArray[6][0].y + cellHeight;

        var mousePoint:Point = new Point(event.stageX, event.stageY);
        mousePoint = globalToLocal(mousePoint);
        var mouseY:Number = mousePoint.y;
        var mouseX:Number = mousePoint.x;

        if (mouseX < firstColX ||
            mouseX > lastColX + cellWidth ||
            mouseY < firstRowY)
            return;

        var rowIndex:int = Math.floor((mouseY-paddingTop) / cellHeight);
        var colIndex:int = Math.floor((mouseX-paddingLeft) / cellWidth);

        colIndex = Math.min(colIndex, 6);
        rowIndex = Math.min(rowIndex, 6);

        var selCell:IUITextField = dayBlocksArray[colIndex][rowIndex];

        // If it is disabled, we're done.
        if (disabledArrays[colIndex][rowIndex] || rowIndex == 0)
            return;

        if (mouseY >= selCell.y &&
            mouseY <= selCell.y + cellHeight &&
            mouseX >= selCell.x &&
            mouseX <= selCell.x + cellWidth)
        {
            rollOverIndicator.move(selCell.x, selCell.y + yOffset); // Don't trigger layout
            rollOverIndicator.visible = true;

            // Don't show rollover if we're over the selected date
            if (selectionIndicator[colIndex][rowIndex])
            {
                rollOverIndicator.visible = false;
            }

            // Set alpha only if today is not disabled
            if (todayColumn != -1 && todayRow != -1 && !disabledArrays[todayColumn][todayRow])
            {
            	var today:Date = new Date();
                if (rollOverIndicator.x == todayIndicator.x &&
                    rollOverIndicator.y == todayIndicator.y)
                {
                    todayIndicator.alpha = 0.6;
                }
                else if (!isSelected(today))
                {
                    todayIndicator.alpha = 1.0;
                }
            }
        }
    }

    /**
	 *  @private
	 */
    private function mouseUpHandler(event:MouseEvent):void
    {
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingTop:Number = getStyle("paddingTop");

        var firstColX:Number = dayBlocksArray[0][0].x;
        var lastColX:Number = dayBlocksArray[6][0].x;
        var firstRowY:Number = dayBlocksArray[6][0].y + cellHeight;

        var mousePoint:Point = new Point(event.stageX, event.stageY);
        mousePoint = globalToLocal(mousePoint);
        var mouseY:Number = mousePoint.y;
        var mouseX:Number = mousePoint.x;

        if (mouseX < firstColX &&
            mouseX >= lastColX + cellWidth ||
            mouseY < firstRowY)
            return;

        var rowIndex:int = Math.floor((mouseY-paddingTop) / cellHeight);

        if (rowIndex <= 0)
            return;
		rowIndex = Math.min(rowIndex, 6);

        var colIndex:int = Math.floor((mouseX-paddingLeft) / cellWidth);
        var selCell:IUITextField = dayBlocksArray[colIndex][rowIndex];//this["dayBlock"+colIndex+"label"+rowIndex];
        // If it is disabled, we're done.
        if (disabledArrays[colIndex][rowIndex])
            return;

        if (mouseY >= selCell.y &&
            mouseY <= selCell.y + cellHeight &&
            mouseX >= selCell.x &&
            mouseX <= selCell.x + cellWidth)
        {
			var newDate:Date = new Date(displayedYear, displayedMonth, int(selCell.text));

			if (event.shiftKey && _allowMultipleSelection)
			{
				addToSelected(newDate,true);
    		    setSelectedIndicators();
			}
			else
   			{
				var alreadySelected:Boolean = selectionIndicator[colIndex][rowIndex] ? true : false;

				if (event.ctrlKey && _allowMultipleSelection && _allowDisjointSelection)
    			{
					if (alreadySelected)
					{
                        removeSelectionIndicator(colIndex,rowIndex);
          	    		removeRangeFromSelection(newDate,newDate);
					}
					else
					{
    					addSelectionIndicator(colIndex,rowIndex);
						addToSelected(newDate);
					}
    			}
    			else
    			{
    				rangeStartDate = null;

					if (alreadySelected)
					{
						if (selectedRangeCount > 1 || (selectedRangeCount == 1 && _selectedRanges[0].rangeStart != _selectedRanges[0].rangeEnd))
						{
							selectedRangeCount = 0;
                           	addSelectionIndicator(colIndex,rowIndex);
							addToSelected(newDate);
						    setSelectedIndicators();
						}
						else if (event.ctrlKey)
						{
    	                    removeSelectionIndicator(colIndex,rowIndex);
        	  	    		removeRangeFromSelection(newDate,newDate);
						}
					}
					else
					{
							selectedRangeCount = 0;
                           	addSelectionIndicator(colIndex,rowIndex);
							addToSelected(newDate);
        					setSelectedIndicators();
 					}
				}
			}

			dispatchChangeEvent(event);

            if (todayColumn != -1 && todayRow != -1 && !disabledArrays[todayColumn][todayRow]) // Set alpha only if today is not disabled
            {
                var todaysDate:Date = new Date();
                todayIndicator.alpha  = isSelected(todaysDate) ? 0.6 : 1.0;
            }

            // Hide the rollover indicator if it is the selected cell
            if (selectionIndicator[colIndex][rowIndex])
                rollOverIndicator.visible = false;
		}
    }

   	/**
	 *  We don't use 'is' to prevent dependency issues
	 */
	static private var dcis:Object = {};

	static private function isDateChooserIndicator(parent:Object):Boolean
	{
		var s:String = getQualifiedClassName(parent);
		if (dcis[s] == 1)
			return true;

		if (dcis[s] == 0)
			return false;

		if (s == "mx.skins.halo::DateChooserIndicator")
		{
			dcis[s] == 1;
			return true;
		}

		var x:XML = describeType(parent);
		var xmllist:XMLList = x.extendsClass.(@type == "mx.skins.halo::DateChooserIndicator");
		if (xmllist.length() == 0)
		{
			dcis[s] = 0;
			return false;
		}
		
		dcis[s] = 1;
		return true;
	}
}

}
