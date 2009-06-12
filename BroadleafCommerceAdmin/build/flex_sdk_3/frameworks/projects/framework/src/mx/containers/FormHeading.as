////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers
{

import flash.events.Event;
import flash.text.TextFieldAutoSize;
import mx.controls.Label;
import mx.core.EdgeMetrics;
import mx.core.UIComponent;

include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Number of pixels between the label area and the heading text.
 *
 *  @default 14
 */
[Style(name="indicatorGap", type="Number", format="Length", inherit="yes")]

/**
 *  Width of the form labels.
 *  The default value is the length of the longest label in the form.
 *  For FormHeading, the <code>labelWidth</code>
 *  is space to the left of the heading text.
 */
[Style(name="labelWidth", type="Number", format="Length", inherit="yes")]

/**
 *  Number of pixels above the heading text.
 *
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

[IconFile("FormHeading.png")]

/**
 *  The FormHeading container is used to display a heading
 *  for a group of controls inside a Form container.
 *  The left side of the heading is aligned
 *  with the left side of the controls inside the form.
 *  You can include multiple FormHeading containers within a single Form
 *  container.
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:FormHeading&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:FormHeading
 *    <strong>Properties</strong>
 *    label=""
 *  
 *    <strong>Styles</strong>
 *    color="0x0B333C"
 *    disabledColor="0xAAB3B3"
 *    fontAntiAliasType="advanced|normal"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel|subpixel|none"
 *    fontSharpness="0"
 *    fontSize="12"
 *    fontStyle="normal|italic"
 *    fontThickness="0"
 *    fontWeight="normal|bold"
 *    indicatorGap="14"
 *    labelWidth="<i>Calculated</i>"
 *    leading="2"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    paddingTop="0"
 *    textAlign="<i>Calculated</i>"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.containers.Form
 *  @see mx.containers.FormItem
 * 
 *  @includeExample examples/FormExample.mxml
 */
public class FormHeading extends UIComponent
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
	public function FormHeading()
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
	private var labelObj:Label;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

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
	 *  Form heading text.
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

		invalidateProperties();

		dispatchEvent(new Event("labelChanged"));
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

		createLabel();
	}

	/**
	 *  @private
	 */
	override protected function measure():void
	{
		super.measure();

		var preferredWidth:Number = 0;
		var preferredHeight:Number = getStyle("paddingTop");

		if (labelObj)
		{
			if (isNaN(labelObj.measuredWidth))
				labelObj.validateSize();

			preferredWidth = labelObj.measuredWidth;
			preferredHeight += labelObj.measuredHeight;
		}

		preferredWidth += getLabelWidth() + getStyle("indicatorGap");

		measuredMinWidth = preferredWidth;
		measuredMinHeight = preferredHeight;
		measuredWidth = preferredWidth;
		measuredHeight = preferredHeight;
	}

	/**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);

		if (labelObj)
		{
			var indicatorGap:Number = getStyle("indicatorGap");
			var paddingTop:Number = getStyle("paddingTop");
			var labelWidth:Number = width;
			
			labelObj.move(getLabelWidth() + indicatorGap, paddingTop);
			
			if (parent && parent is Form)
			{
				var vm:EdgeMetrics = Form(parent).viewMetricsAndPadding;
				
				labelWidth = parent.width - 
					(getLabelWidth() + indicatorGap + vm.left + vm.right);
			}
			
			labelObj.setActualSize(labelWidth, height);
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
	private function createLabel():void
	{
		// See if we need to create our labelObj.
		if (_label.length > 0)
		{
			if (!labelObj)
			{
				labelObj = new Label();
				labelObj.styleName = this;
				addChild(labelObj);
			}

			if (labelObj.text != _label)
			{
				labelObj.text = _label;

				labelObj.validateSize();

				invalidateSize();
				invalidateDisplayList();
			}
		}

		// See if we need to destroy our labelObj.
		if (_label.length == 0 && labelObj)
		{
			removeChild(labelObj);
			labelObj = null;

			invalidateSize();
			invalidateDisplayList();
		}
	}

	/**
	 *  @private
	 */
	private function getLabelWidth():Number
	{
		var labelWidth:Number = getStyle("labelWidth");

		// labelWidth of 0 is the same as NaN
		if (labelWidth == 0)
		{
			labelWidth = NaN;
		}

		if (isNaN(labelWidth) && parent is Form)
			labelWidth = Form(parent).calculateLabelWidth();

		if (isNaN(labelWidth))
			labelWidth = 0;

		return labelWidth;
	}
}

}
