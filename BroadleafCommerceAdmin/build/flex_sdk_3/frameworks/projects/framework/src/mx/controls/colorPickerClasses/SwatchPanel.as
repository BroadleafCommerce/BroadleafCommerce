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

package mx.controls.colorPickerClasses
{

import flash.events.Event;
import flash.events.EventPhase;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Rectangle;
import flash.ui.Keyboard;
import mx.collections.ArrayList;
import mx.collections.IList;
import mx.controls.ColorPicker;
import mx.controls.TextInput;
import mx.core.FlexVersion;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.ColorPickerEvent;
import mx.managers.IFocusManagerContainer;
import mx.skins.halo.SwatchPanelSkin;
import mx.skins.halo.SwatchSkin;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the selected color changes.
 *
 *  @eventType flash.events.Event.CHANGE
 */
[Event(name="change", type="flash.events.Event")]

/**
 *  Dispatched when the user presses the Enter key.
 *
 *  @eventType mx.events.FlexEvent.ENTER
 */
[Event(name="enter", type="flash.events.Event")]

/**
 *  Dispatched when the mouse rolls over a color.
 *
 *  @eventType mx.events.ColorPickerEvent.ITEM_ROLL_OVER
 */
[Event(name="itemRollOver", type="mx.events.ColorPickerEvent")]

/**
 *  Dispatched when the mouse rolls out of a color.
 *
 *  @eventType mx.events.ColorPickerEvent.ITEM_ROLL_OUT
 */
[Event(name="itemRollOut", type="mx.events.ColorPickerEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../../styles/metadata/GapStyles.as"
include "../../styles/metadata/PaddingStyles.as"

/**
 *  Background color of the component.
 *  You can either have a <code>backgroundColor</code> or a
 *  <code>backgroundImage</code>, but not both.
 *  Note that some components, like a Button, do not have a background
 *  because they are completely filled with the button face or other graphics.
 *  The DataGrid control also ignores this style.
 *  The default value is <code>undefined</code>. If both this style and the
 *  backgroundImage style are undefined, the control has a transparent background.
 */
[Style(name="backgroundColor", type="uint", format="Color", inherit="no")]

/**
 *  Black section of a three-dimensional border, or the color section
 *  of a two-dimensional border.
 *  The following components support this style: Button, CheckBox,
 *  ComboBox, MenuBar,
 *  NumericStepper, ProgressBar, RadioButton, ScrollBar, Slider, and all
 *  components that support the <code>borderStyle</code> style.
 *  The default value depends on the component class;
 *  if not overriden for the class, it is <code>0xAAB3B3</code>.
 */
[Style(name="borderColor", type="uint", format="Color", inherit="no")]

/**
 *  Number of columns in the swatch grid.
 *  The default value is 20.
 */
[Style(name="columnCount", type="int", inherit="no")]

/**
 *  Color of the control border highlight.
 *  The default value is <code>0xC4CCCC</code> (medium gray) .
 */
[Style(name="highlightColor", type="uint", format="Color", inherit="yes")]

/**
 *  Color for the left and right inside edges of a component's skin.
 *  The default value is <code>0xD5DDDD</code>.
 */
[Style(name="shadowCapColor", type="uint", format="Color", inherit="yes")]

/**
 *  Bottom inside color of a button's skin.
 *  A section of the three-dimensional border.
 *  The default value is <code>0xEEEEEE</code> (light gray).
 */
[Style(name="shadowColor", type="uint", format="Color", inherit="yes")]

/**
 *  Height of the larger preview swatch that appears above the swatch grid on
 *  the top left of the SwatchPanel object.
 *  The default value is 22.
 */
[Style(name="previewHeight", type="Number", format="Length", inherit="no")]

/**
 *  Width of the larger preview swatch.
 *  The default value is 45.
 */
[Style(name="previewWidth", type="Number", format="Length", inherit="no")]

/**
 *  Size of the swatchBorder outlines.
 *  The default value is 1.
 */
[Style(name="swatchBorderSize", type="Number", format="Length", inherit="no")]

/**
 *  Color of the swatch borders.
 *  The default value is <code>0x000000</code>.
 */
[Style(name="swatchBorderColor", type="uint", format="Color", inherit="no")]

/**
 *  Size of the single border around the grid of swatches.
 *  The default value is 0.
 */
[Style(name="swatchGridBorderSize", type="Number", format="Length", inherit="no")]

/**
 *  Color of the background rectangle behind the swatch grid.
 *  The default value is <code>0x000000</code>.
 */
[Style(name="swatchGridBackgroundColor", type="uint", format="Color", inherit="no")]

/**
 *  Height of each swatch.
 *  The default value is 12.
 */
[Style(name="swatchHeight", type="Number", format="Length", inherit="no")]

/**
 *  Color of the highlight that appears around the swatch when the user
 *  rolls over a swatch.
 *  The default value is <code>0xFFFFFF</code>.
 */
[Style(name="swatchHighlightColor", type="uint", format="Color", inherit="no")]

/**
 *  Size of the highlight that appears around the swatch when the user
 *  rolls over a swatch.
 *  The default value is 1.
 */
[Style(name="swatchHighlightSize", type="Number", format="Length", inherit="no")]

/**
 *  Width of each swatch.
 *  The default value is 12.
 */
[Style(name="swatchWidth", type="Number", format="Length", inherit="no")]

/**
 *  @REVIEW 
 *  Name of the style sheet definition to configure the text input control.
 *  The default value is "swatchPanelTextField"
 */
[Style(name="textFieldStyleName", type="String", inherit="no")]

/**
 *  Width of the hexadecimal text box that appears above the swatch grid.
 *  The default value is 72.
 */
[Style(name="textFieldWidth", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[ExcludeClass]

/**
 *  @private
 */
public class SwatchPanel extends UIComponent implements IFocusManagerContainer
{
    include "../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function SwatchPanel() 
    {
        super();
        
        // Register for events.
        addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);
        addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);        
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */    
    mx_internal var textInput:TextInput;

    /**
     *  @private
     */    
    private var border:SwatchPanelSkin;

    /**
     *  @private
     */    
    private var preview:SwatchSkin;

    /**
     *  @private
     */    
    private var swatches:SwatchSkin;

    /**
     *  @private
     */    
    private var highlight:SwatchSkin;

    /**
     *  @private
     *  Used by ColorPicker
     */   
    mx_internal var isOverGrid:Boolean = false;

    /**
     *  @private
     *  Used by ColorPicker
     */   
    mx_internal var isOpening:Boolean = false;

    /**
     *  @private
     *  Used by ColorPicker
     */   
    mx_internal var focusedIndex:int = -1;

    /**
     *  @private
     *  Used by ColorPicker
     */   
    mx_internal var tweenUp:Boolean = false;
    
    /**
     *  @private
     */    
    private var initializing:Boolean = true;
 
    /**
     *  @private
     */    
    private var indexFlag:Boolean = false;
 
    /**
     *  @private
     */    
    private var lastIndex:int = -1;
 
    /**
     *  @private
     */    
    private var grid:Rectangle;
 
    /**
     *  @private
     */    
    private var rows:int;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var horizontalGap:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var verticalGap:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var columnCount:int;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var paddingLeft:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var paddingRight:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var paddingTop:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var paddingBottom:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var textFieldWidth:Number;
     
    /**
     *  @private
	 *  Cached style.
     */        
    private var previewWidth:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var previewHeight:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var swatchWidth:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var swatchHeight:Number;
    
    /**
     *  @private
	 *  Cached style.
     */        
    private var swatchGridBorderSize:Number;
    
    /**
     *  @private
     */        
	private var cellOffset:Number = 1;
 
    /**
     *  @private
     */        
    private var itemOffset:Number = 3;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  height
    //----------------------------------
    
    /**
     *  @private
     *  We set our size internally based on style values.
	 *  Setting height has no effect on the panel.
	 *  Override to return the preferred width and height of our contents.
     */    
    override public function get height():Number
    {
        return getExplicitOrMeasuredHeight();
    }

    /**
     *  @private
     */
    override public function set height(value:Number):void 
    {
        // do nothing...
    }
    
    //----------------------------------
    //  width
    //----------------------------------
    
    /**
     *  @private
     *  We set our size internally based on style values.
	 *  Setting width has no effect on the panel.
	 *  Override to return the preferred width and height of our contents.
     */    
    override public function get width():Number
    {
        return getExplicitOrMeasuredWidth();
    }

    /**
     *  @private
     */
    override public function set width(value:Number):void 
    {
        // do nothing...
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  colorField
    //----------------------------------
    
    /**
	 *  Storage for the colorField property.
	 */
    private var _colorField:String = "color";

    /**
     *  @private
     */    
    public function get colorField():String
    {
        return _colorField;
    }

    /**
     *  @private
     */
    public function set colorField(value:String):void
    {
        _colorField = value;
    }

    //----------------------------------
    //  dataProvider
    //----------------------------------
    
    /**
	 *  Storage for the dataProvider property.
	 */
	private var _dataProvider:IList;

    /**
     *  @private
     */    
    public function get dataProvider():Object
    {
        return _dataProvider;
    }

    /**
     *  @private
     */
    public function set dataProvider(value:Object):void
    {
        if (value is IList)
        {
	        _dataProvider = IList(value);        
        }
        else if (value is Array)
		{
			var tmpDP:IList = new ArrayList(value as Array);
			value = tmpDP;
		}
		else
		{
	        _dataProvider = null;			
        }        

        if (!initializing)
        {
            // Adjust if dataProvider is empty
            if (length == 0 || isNaN(length))
            {
                highlight.visible = false;
                _selectedIndex = -1;
            }
            
			// Redraw using new dataProvider
            refresh();
        }
    }

    //----------------------------------
    //  editable
    //----------------------------------    
    
    /**
	 *  Storage for the editable property.
	 */
	private var _editable:Boolean = true;

    /**
     *  @private
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
        _editable = value;
        
		if (!initializing)
            textInput.editable = value;
    }

    //----------------------------------
    //  labelField
    //----------------------------------
    
    /**
	 *  Storage for the labelField property.
	 */
    private var _labelField:String = "label";

    /**
     *  @private
     */    
    public function get labelField():String
    {
        return _labelField;
    }

    /**
     *  @private
     */
    public function set labelField(value:String):void
    {
        _labelField = value;
    }

    //----------------------------------
    //  length
    //----------------------------------
    
    /**
     *  @private
     */    
    public function get length():int
    {
        return _dataProvider ? _dataProvider.length : 0;
    }

    //----------------------------------
    //  selectedColor
    //----------------------------------
    
    /**
	 *  Storage for the selectedColor property.
	 */
    private var _selectedColor:uint = 0x000000;

    /**
     *  @private
     */    
    public function get selectedColor():uint
    {
        return _selectedColor;
    }

    /**
     *  @private
     */
    public function set selectedColor(value:uint):void
    {
        // Set index unless it set us
        if (!indexFlag)
        {
            var SI:int = findColorByName(value);
            if (SI != -1)
            {
                focusedIndex = findColorByName(value);
                _selectedIndex = focusedIndex;
            }
            else
			{
                selectedIndex = -1;
			}
        }
        else
        {
            indexFlag = false;
        }
        
		if (value != selectedColor || !isOverGrid || isOpening)
        {
            _selectedColor = value;
            updateColor(value);

            if (isOverGrid || isOpening)
                setFocusOnSwatch(selectedIndex);
            if (isOpening)
                isOpening = false;
        }
    }

    //----------------------------------
    //  selectedIndex
    //----------------------------------
    
    /**
	 *  Storage for the selectedIndex property.
	 */
    private var _selectedIndex:int = 0;

    /**
     *  @private
     */    
    public function get selectedIndex():int
    {
        return _selectedIndex;
    }

    /**
     *  @private
     */
    public function set selectedIndex(value:int):void
    {
        if (value != selectedIndex && !initializing)
        {
            focusedIndex = value;
            _selectedIndex = focusedIndex;
            
			if (value >= 0)
            {
                indexFlag = true;
                selectedColor = getColor(value);
            }
        }
    }

    //----------------------------------
    //  selectedItem
    //----------------------------------
    
    /**
     *  @private
     */    
    public function get selectedItem():Object
    {
        return dataProvider ? dataProvider.getItemAt(selectedIndex) : null;
    }

    /**
     *  @private
     */
    public function set selectedItem(value:Object):void
    {
        if (value != selectedItem)
        {
            var color:Number;
			if (typeof(value) == "object")
                color = Number(value[colorField]);
            else if (typeof(value) == "number")
                color = Number(value);
            
			selectedIndex = findColorByName(color);
        }
    }
    
    //----------------------------------
    //  showTextField
    //----------------------------------
    
    /**
	 *  Storage for the showTextField property.
	 */
    private var _showTextField:Boolean = true;

    /**
     *  @private
     */        
    public function get showTextField():Boolean
    {
        return _showTextField;
    }

    /**
     *  @private
     */
    public function set showTextField(value:Boolean):void
    {
        _showTextField = value;

        if (!initializing)
            textInput.visible = value;
    }

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
        
        // Create the panel background
        if (!border)
		{
			border = new SwatchPanelSkin();
			
			border.styleName = this;
			border.name = "swatchPanelBorder";
			
			addChild(border);  
		}      
                    
        // Create the preview swatch
        if (!preview)
		{
			preview = new SwatchSkin();
			
			preview.styleName = this;
			preview.color = selectedColor;
			preview.name = "swatchPreview";
			
			preview.setStyle("swatchBorderSize", 0);
			
			addChild(preview);        
		}    

        // Create the hex text field  
        if (!textInput)
		{			
			textInput = new TextInput();    
			
			if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
			{
				textInput.styleName = this;
				textInput.setStyle("borderCapColor", 0x919999);
				textInput.setStyle("buttonColor", 0x6F7777);
				textInput.setStyle("highlightColor", 0xC4CCCC);
				textInput.setStyle("shadowColor", 0xc);
				textInput.setStyle("shadowCapColor", 0xD5DDDD);
				textInput.setStyle("borderStyle", "inset");
				textInput.setStyle("backgroundColor", 0xFFFFFF);
				textInput.setStyle("borderColor", 0xD5DDDD);
			}
			else
			{
				textInput.styleName = getStyle("textFieldStyleName");
			}
			
			textInput.editable = _editable;
			textInput.maxChars = 6;
			textInput.name = "inset";
			textInput.text = rgbToHex(selectedColor);
			textInput.restrict = "#xa-fA-F0-9";   
			
			textInput.addEventListener(Event.CHANGE, textInput_changeHandler);
			textInput.addEventListener(KeyboardEvent.KEY_DOWN, textInput_keyDownHandler);
			
			addChild(textInput);        
		}
        
        // Create the swatches grid
        if (!swatches)
		{
			swatches = new SwatchSkin();
			
			swatches.styleName = this;
			swatches.colorField = colorField;
			swatches.name = "swatchGrid";
						
			swatches.addEventListener(MouseEvent.CLICK, swatches_clickHandler);
			
			addChild(swatches);        
		}
    
        // Create the swatch highlight for grid rollovers
        if (!highlight)
		{
			highlight = new SwatchSkin();
			
			highlight.styleName = this;
			highlight.visible = false;
			highlight.name = "swatchHighlight";
						
			addChild(highlight);        
		}

        refresh();

        initializing = false;
    }

    /**
     *  @private
     *  Change
     */    
    override protected function measure():void
    {
		super.measure();
        
        swatches.updateGrid(IList(dataProvider));

        // Make sure we're at least 100 pixels wide
        
		measuredWidth = Math.max(
			paddingLeft + paddingRight + swatches.width, 100);
        
		measuredHeight = Math.max(
			paddingTop + previewHeight + itemOffset +
			paddingBottom + swatches.height, 100);
    }

    /**
     *  @private
     */    
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        // Layout preview position.
        preview.updateSkin(selectedColor);
        preview.move(paddingLeft, paddingTop);

        // Layout hex text field position.
        textInput.setActualSize(textFieldWidth, previewHeight);
        textInput.move(paddingLeft + previewWidth + itemOffset, paddingTop);

        // Layout grid position.
        swatches.updateGrid(IList(dataProvider));
        swatches.move(paddingLeft, paddingTop + previewHeight + itemOffset);

        // Layout highlight skin.
		// Highlight doesn't require a color, hence we pass 0.
        highlight.updateSkin(0);

        // Layout panel skin.
        border.setActualSize(unscaledWidth, unscaledHeight);
        
        // Define area surrounding the swatches.
        if (!grid) 
            grid = new Rectangle();
        grid.left = swatches.x + swatchGridBorderSize;
        grid.top = swatches.y + swatchGridBorderSize;
        grid.right = swatches.x + swatchGridBorderSize +
					 (swatchWidth - 1) * columnCount + 1 +
					 horizontalGap * (columnCount - 1);
        grid.bottom = swatches.y + swatchGridBorderSize +
					  (swatchHeight - 1) * rows + 1 +
					  verticalGap * (rows - 1);
    }


    /**
     *  @private
     */    
    override public function styleChanged(styleProp:String):void
    {
        if (!initializing)
            refresh();
    }
    
    /**
     *  @private
     */    
    override public function drawFocus(isFocused:Boolean):void
    {  
        // do nothing...
    }
   
    /**
     *  @private
     */    
    override public function setFocus():void
    {
        // Our text field controls focus
        if (showTextField && editable)
        {
            textInput.setFocus();
            //ensure text field has the correct color value
	        textInput.text = rgbToHex(selectedColor);
        }
    }
        
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */    
    private function updateStyleCache():void
    {
        horizontalGap = getStyle("horizontalGap");
        verticalGap = getStyle("verticalGap");
        columnCount = getStyle("columnCount");
        paddingLeft = getStyle("paddingLeft");
        paddingRight = getStyle("paddingRight");
        paddingTop = getStyle("paddingTop");
        paddingBottom = getStyle("paddingBottom");
        textFieldWidth = getStyle("textFieldWidth");
        previewWidth = getStyle("previewWidth");
        previewHeight = getStyle("previewHeight");
        swatchWidth = getStyle("swatchWidth");
        swatchHeight = getStyle("swatchHeight");
        swatchGridBorderSize = getStyle("swatchGridBorderSize");

        // Adjust if columnCount is greater than # of swatches
        if (columnCount > length)
            columnCount = length;

        // Rows based on columnCount and list length
        rows = Math.ceil(length / columnCount);
    }

    /**
     *  @private
     */    
    private function refresh():void
    {
        updateStyleCache();
        updateDisplayList(unscaledWidth, unscaledHeight);
    }
 
    /**
     *  @private
	 *  Update color values in preview
     */    
    private function updateColor(color:uint):void
    {
        if (initializing || isNaN(color))
            return;

        // Update the preview swatch
        preview.updateSkin(color);

        // Set hex field
        if (isOverGrid)
        {            
            var label:String = null;
            
			if (focusedIndex >= 0 && 
                typeof(dataProvider.getItemAt(focusedIndex)) == "object")
            {
                label = dataProvider.getItemAt(focusedIndex)[labelField];
            }

            textInput.text = label != null && label.length != 0 ?
							 label :
                             rgbToHex(color);
        }
    }

    /**
     *  @private
	 *  Convert RGB offset to Hex.
     */    
    private function rgbToHex(color:uint):String
    {
        // Find hex number in the RGB offset
        var colorInHex:String = color.toString(16);
        var c:String = "00000" + colorInHex;
        var e:int = c.length;
        c = c.substring(e - 6, e);
        return c.toUpperCase();
    }

    /**
     *  @private
     */    
    private function findColorByName(name:Number):int
    {
        if (name == getColor(selectedIndex))
            return selectedIndex;

        for (var i:int = 0; i < length; i++)
		{
            if (name == getColor(i))
                return i;
		}

        return -1;
    }
    
    /**
     *  @private
     */    
    private function getColor(index:int):uint
    {
		if (!dataProvider || dataProvider.length < 1 ||
			index < 0 || index >= length)
		{
			return StyleManager.NOT_A_COLOR;
		}
        
		return uint(typeof(dataProvider.getItemAt(index)) == "object" ?
        	   		dataProvider.getItemAt(index)[colorField] : 
					dataProvider.getItemAt(index));
    }

    /**
     *  @private
     */    
    private function setFocusOnSwatch(index:int):void
    {
        if (index < 0 || index > length - 1)
        {
            highlight.visible = false;
            return;
        }
        
		// Swatch highlight activated by mouse move or key events
        var row:Number = Math.floor(index / columnCount);
        var column:Number = index - (row * columnCount);
        
		var xPos:Number = swatchWidth * column + horizontalGap * column -
						  cellOffset * column + paddingLeft +
						  swatchGridBorderSize;
        var yPos:Number = swatchHeight * row + verticalGap * row -
						  cellOffset * row + paddingTop + previewHeight +
						  itemOffset + swatchGridBorderSize;
        
		highlight.move(xPos, yPos);
        highlight.visible = true;
        
		isOverGrid = true;
        
		updateColor(getColor(index));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */    
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
		// Ignore events that bubbling from the owner ColorPicker.
		// through the textInput's keyDownHandler
		if (event.eventPhase != EventPhase.AT_TARGET || !enabled)
			return;
			
        if (focusedIndex == -1 || isNaN(focusedIndex))
            focusedIndex = 0;

        var currentRow:int = Math.floor(focusedIndex / columnCount);

        switch (event.keyCode)
        {
            case Keyboard.UP:
            {
                // Move up in column / jump to bottom of next column at end.
                focusedIndex = focusedIndex - columnCount < 0 ?
							   (rows - 1) * columnCount + focusedIndex + 1 :
							   focusedIndex - columnCount;
                isOverGrid = true;
                break;
            }

            case Keyboard.DOWN:
            {
                // Move down in column / jump to top of last column at end.
                focusedIndex = focusedIndex + columnCount > length ?
							   (focusedIndex - 1) - (rows - 1) * columnCount :
							   focusedIndex + columnCount;
                isOverGrid = true;
                break;
            }

            case Keyboard.LEFT:
            {
                // Move left in row / jump to right of last row at end.
                focusedIndex = focusedIndex < 1 ?
							   length - 1 :
							   focusedIndex - 1;
                isOverGrid = true;
                break;
            }

            case Keyboard.RIGHT:
            {
                // Move right in row / jump to left of next row at end.
                focusedIndex = focusedIndex >= length - 1 ?
							   0 :
							   focusedIndex + 1;
                isOverGrid = true;
                break;
            }

            case Keyboard.PAGE_UP:
            {
                // Move to first swatch in column.
                focusedIndex = focusedIndex - currentRow * columnCount;
                isOverGrid = true;
                break;
            }

            case Keyboard.PAGE_DOWN:
            {
                // Move to last swatch in column.
                focusedIndex = focusedIndex + (rows - 1) * columnCount -
							   currentRow * columnCount;
                isOverGrid = true;
                break;
            }

            case Keyboard.HOME:
            {
                // Move to first swatch in row.
                focusedIndex = focusedIndex -
							   (focusedIndex - currentRow * columnCount);
                isOverGrid = true;
                break;
            }

            case Keyboard.END:
            {
                // Move to last swatch in row.
                focusedIndex = focusedIndex +
							   (currentRow * columnCount - focusedIndex) +
							   (columnCount - 1);
                isOverGrid = true;
                break;
            }
        }

        // Draw focus on new swatch.
        if (focusedIndex < length && isOverGrid)
        {
            setFocusOnSwatch(focusedIndex);
			dispatchEvent(new Event("change")); 
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
    private function mouseMoveHandler(event:MouseEvent):void
    {    
        if (ColorPicker(owner).isDown && enabled)
        {
            var colorPickerEvent:ColorPickerEvent;
                    
            // Only assess movements that occur over the swatch grid.
            if (mouseX > grid.left && mouseX < grid.right &&
                mouseY > grid.top && mouseY < grid.bottom)
            {
                // Calculate location
                var column:Number = Math.floor(
					(Math.floor(mouseX) - (grid.left + verticalGap)) /
					(swatchWidth + horizontalGap - cellOffset));
				var row:Number = Math.floor(
					(Math.floor(mouseY) - grid.top) /
					((swatchHeight + verticalGap) - cellOffset));
                var index:Number = row * columnCount + column;

                // Adjust for edges
                if (column == -1)
					index++;
                else if (column > (columnCount - 1))
				    index--;
                else if (row > (rows - 1))
					index -= columnCount;
                else if (index < 0)
					index += columnCount;

                // Set state
                if ((lastIndex != index || highlight.visible == false) &&
					index < length)
                {
                    if (lastIndex != -1 && lastIndex != index)
                    {
                        // Dispatch a ColorPickerEvent with type "itemRollOut".
						colorPickerEvent = new ColorPickerEvent(
                            ColorPickerEvent.ITEM_ROLL_OUT);
                        colorPickerEvent.index = lastIndex;
						colorPickerEvent.color = getColor(lastIndex);
                        dispatchEvent(colorPickerEvent);
                    }

                    focusedIndex = index;
                    lastIndex = focusedIndex;
                    setFocusOnSwatch(focusedIndex);
                    
                    // Dispatch a ColorPickerEvent with type "itemRollOver".
					colorPickerEvent = new ColorPickerEvent(
                        ColorPickerEvent.ITEM_ROLL_OVER);
                    colorPickerEvent.index =  focusedIndex;
					colorPickerEvent.color = getColor(focusedIndex);
                    dispatchEvent(colorPickerEvent); 
                }
            }
            else
            {
                if (highlight.visible == true && isOverGrid && lastIndex != -1)
                {
                    highlight.visible = false;

                    // Dispatch a ColorPickerEvent with type "itemRollOut".
                    colorPickerEvent = new ColorPickerEvent(
                        ColorPickerEvent.ITEM_ROLL_OUT);
                    colorPickerEvent.index = lastIndex;
					colorPickerEvent.color = getColor(lastIndex);
                    dispatchEvent(colorPickerEvent); 
                }

                isOverGrid = false;
            }
        }
    }
    
    /**
     *  @private
     */    
    private function swatches_clickHandler(event:MouseEvent):void
    {
		if (!enabled)
			return;
	
        if (mouseX > grid.left && mouseX < grid.right &&
            mouseY > grid.top && mouseY < grid.bottom)
        {
            selectedIndex = focusedIndex;
            
			if (ColorPicker(owner).selectedIndex != selectedIndex)
            {
                ColorPicker(owner).selectedIndex = selectedIndex;
                
				var cpEvent:ColorPickerEvent = 
                    new ColorPickerEvent(ColorPickerEvent.CHANGE);
                cpEvent.index = selectedIndex;
                cpEvent.color = getColor(selectedIndex);
                ColorPicker(owner).dispatchEvent(cpEvent);
            }

            ColorPicker(owner).close(); // owner = ColorPicker
        }
    }
        
    /**
     *  @private
     */    
    private function textInput_keyDownHandler(event:KeyboardEvent):void
    {
        // Redispatch the event from the ColorPicker
		// and let its keyDownHandler() handle it.
        ColorPicker(owner).dispatchEvent(event);
    }

    /**
     *  @private
     */    
    private function textInput_changeHandler(event:Event):void
    {
        // Handle events from hex TextField.
        var color:String = TextInput(event.target).text;
        if (color.charAt(0) == "#")
        {
            textInput.maxChars = 7;
            color = "0x"+color.substring(1);
        }
        else if (color.substring(0,2) == "0x")
        {
            textInput.maxChars = 8;
        }
        else
        {
            textInput.maxChars = 6;
            color = "0x"+color;
        }
        
		highlight.visible = false;
        isOverGrid = false;
		selectedColor = Number(color);
        
		dispatchEvent(new Event("change"));   
    }
}

}
