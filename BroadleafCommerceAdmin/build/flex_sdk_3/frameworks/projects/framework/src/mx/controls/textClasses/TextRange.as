////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.textClasses
{

import flash.text.TextField;
import flash.text.TextFormat;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.styles.StyleManager;
import mx.utils.StringUtil;

use namespace mx_internal;

/**
 *  The TextRange class provides properties that select and format a range of
 *  text in the Label, Text, TextArea, TextEditor, and RichTextEditor controls.
 *
 *  @see mx.controls.Label
 *  @see mx.controls.RichTextEditor
 *  @see mx.controls.Text
 *  @see mx.controls.TextArea
 *  @see mx.controls.TextInput
 *  @see flash.text.TextFormatAlign
 */
public class TextRange
{
	include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var htmlTextField:TextField;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
	
	/**
	 *  Create a new TextRange Object that represents a subset of the contents
	 *  of a text control, including the formatting information.
	 *
	 *  @param owner The control that contains the text. The control must have
	 *  a <code>textField</code> property, or, as is the case of the
	 *  RichTextEditor control, a <code>textArea</code> property.
	 *
	 *  @param modifiesSelection Whether to select the text in the range.
	 *  If you set this parameter to <code>true</code> and do not specify a
	 *  begin or end index that corresponds to text in the control, Flex
	 *  uses the begin or end index of the current text selection.
	 *  If this parameter is <code>true</code>, you omit the
	 *  <code>beginIndex</code> and <code>endIndex</code>
	 *  parameters, and there is no selection, the TextRange object is empty.
	 *
	 *  @param beginIndex Zero-based index of the first character in the range.
	 *  If the <code>modifiesSelection</code> parameter is <code>false</code>
	 *  and you omit this parameter or specify a negative value,
	 *  the range starts with the first text character.
	 *
	 *  @param endIndex Zero-based index of the position <i>after</i> the
	 *  last character in the range.
	 *  If the <code>modifiesSelection</code> parameter is <code>false</code>
	 *  and you omit this parameter, specify a negative value, or specify
	 *  a value past the end of the text, the range ends with the last
	 *  text character.
	 */
	public function TextRange(owner:UIComponent,
							  modifiesSelection:Boolean = false,
							  beginIndex:int = -1, endIndex:int = -1)
	{
		super();

		_owner = owner;

		try
		{
			textField = _owner["textArea"].mx_internal::getTextField();
		}
		catch(e:Error)
		{
			textField = this["_owner"].mx_internal::getTextField();
		}

		_modifiesSelection = modifiesSelection;

		if (!_modifiesSelection)
		{
			if (beginIndex < 0)
				beginIndex = 0;

			if (beginIndex > textField.length)
				beginIndex = textField.length;

			if (endIndex < 0 || endIndex > textField.length)
				endIndex = textField.length;

			_beginIndex = beginIndex;
			_endIndex = endIndex;
		}
		else
		{
			if (beginIndex < 0 || beginIndex > textField.length)
				beginIndex = textField.selectionBeginIndex;

			if (endIndex < 0 || endIndex > textField.length)
				endIndex = textField.selectionEndIndex;

			textField.selectable = true;
			
			if (beginIndex != textField.selectionBeginIndex ||
				endIndex != textField.selectionEndIndex)
			{
				textField.setSelection(beginIndex, endIndex);
			}
		}
	}

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var textField:TextField;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  beginIndex
    //----------------------------------

	/**
	 *  Storage for the beginIndex property.
	 */
	private var _beginIndex:int;

	/**
	 *  Zero-based index in the control's text field of the first
	 *  character in the range.
	 *  If the fifth character in the text is the first character in the
	 *  range, this property has a value of 4.
	 */
	public function get beginIndex():int
	{
		if (_modifiesSelection)
			return textField.selectionBeginIndex;
		else
			return _beginIndex;
	}

	/**
	 *  @private
	 */
	public function set beginIndex(value:int):void
	{
		if (_modifiesSelection)
			textField.setSelection(value, textField.selectionEndIndex);
		else
			_beginIndex = value;
	}

    //----------------------------------
	//  bullet
    //----------------------------------

	/**
	 *  Whether the text in the range is in a bulleted list.
	 *  If only part of the range is in a bulleted list,
	 *  this value is <code>false</code>.
	 */
	public function get bullet():Boolean
	{
		return getTextFormat().bullet;
	}

	/**
	 *  @private
	 */
	public function set bullet(value:Boolean):void
	{
		var tf:TextFormat = getTextFormat();
		tf.bullet = value;
		setTextFormat(tf);
	}

    //----------------------------------
	//  color
    //----------------------------------

	/**
	 *  Color of the text in the range.
	 *  You can set this value using any valid color identifier.
	 *  The property returns the value as a numeric value.
	 *  If the range has multiple colors, this value is <code>null</code>.
	 */
	public function get color():Object
	{
		return getTextFormat().color;
	}

	/**
	 *  @private
	 */
	public function set color(value:Object):void
	{
		var tf:TextFormat = getTextFormat();
		var colorNumber:uint = StyleManager.getColorName(value);
		if (colorNumber != StyleManager.NOT_A_COLOR)
			value = colorNumber;
		else
			value = 0;
		tf.color = value;
		setTextFormat(tf);
	}

    //----------------------------------
	//  endIndex
    //----------------------------------

	/**
	 *  Storage for the beginIndex property.
	 */
	private var _endIndex:int;

	/**
	 *  Zero-based index in the control's text field of the point
	 *  immediately after the last character in the range; equivalent to
	 *  the One-based index of the last character. 
	 *  If the fifth character in the text is the last character in the
	 *  range, this property has a value of 5.
	 */
	public function get endIndex():int
	{
		if (_modifiesSelection)
			return textField.selectionEndIndex;
		else
			return _endIndex;
	}

	/**
	 *  @private
	 */
	public function set endIndex(value:int):void
	{
		if (_modifiesSelection)
			textField.setSelection(textField.selectionBeginIndex, value);
		else
			_endIndex = value;
	}

    //----------------------------------
	//  fontFamily
    //----------------------------------

	/**
	 *  Name of the font for text in the range.
	 *  If the range has multiple fonts, this value is <code>null</code>.
	 */
	public function get fontFamily():String
	{
		return getTextFormat().font;
	}

	/**
	 *  @private
	 */
	public function set fontFamily(value:String):void
	{
		var tf:TextFormat = getTextFormat();
		tf.font = StringUtil.trimArrayElements(value,",");
		setTextFormat(tf);
	}

    //----------------------------------
	//  fontSize
    //----------------------------------

	/**
	 *  Point size of the text in the range.
	 *  If the range has multiple sizes, this value is 0.
	 */
	public function get fontSize():int
	{
		return int(getTextFormat().size);
	}

	/**
	 *  @private
	 */
	public function set fontSize(value:int):void
	{
		var tf:TextFormat = getTextFormat();
		tf.size = value;
		setTextFormat(tf);
	}

    //----------------------------------
	//  fontStyle
    //----------------------------------

	/**
	 *  Style of the font in the range, as "italic"
	 *  or "normal". Setting the property to any other string results
	 *  in normal style.
	 *  If the range has multiple styles, this value is <code>null</code>.
	 */
	public function get fontStyle():String
	{
		return getTextFormat().italic ? "italic" : "normal";
	}

	/**
	 *  @private
	 */
	public function set fontStyle(value:String):void
	{
		var tf:TextFormat = getTextFormat();
		tf.italic = (value == "italic") ? true : false;
		setTextFormat(tf);
	}

    //----------------------------------
	//  fontWeight
    //----------------------------------

	/**
	 *  Weight of the font in the range, as "bold"
	 *  or "normal". Setting the property to any other string results
	 *  in normal weight.
	 *  If the range has multiple weights, this value is <code>null</code>.
	 */
	public function get fontWeight():String
	{
		return getTextFormat().bold ? "bold" : "normal";
	}

	/**
	 *  @private
	 */
	public function set fontWeight(value:String):void
	{
		var tf:TextFormat = getTextFormat();
		tf.bold = (value == "bold") ? true : false;
		setTextFormat(tf);
	}

    //----------------------------------
	//  htmlText
    //----------------------------------

	/**
	 *  Contents of the range in the form of HTML text.
	 *  This property returns all HTML markup for the range, including
	 *  markup for formatting that is applied by Flex, not just
	 *  HTML that you specify in using an <code>htmlText</code> property.
	 *  This property is, therefore, a full HTML representation of the
	 *  text as it appears in the control.
	 */
	public function get htmlText():String
	{
		if (beginIndex == endIndex)
			return "";
		
		if (!htmlTextField)
		{
			htmlTextField = new TextField();
			htmlTextField.multiline = true;
		}

		htmlTextField.defaultTextFormat = textField.defaultTextFormat;
		htmlTextField.htmlText = "";

		htmlTextField.insertXMLText(
			0, 0, textField.getXMLText(beginIndex, endIndex))

		return htmlTextField.htmlText;
	}

	/**
	 *  @private
	 */
	public function set htmlText(value:String):void
	{
		
		if (!htmlTextField)
		{
			htmlTextField = new TextField();
			htmlTextField.multiline = true;
		}

		htmlTextField.defaultTextFormat = textField.defaultTextFormat;
		htmlTextField.htmlText = value;
		var length:int = htmlTextField.length;

		var oldBeginIndex:int = beginIndex;
		textField.insertXMLText(
			beginIndex, endIndex, htmlTextField.getXMLText());
		// workaround for what seems to be a player bug (#207147)
		// where a zero length selection is shifted
		// (if no bug, this code should never execute)
		if ((oldBeginIndex == 0) && (beginIndex > 0))
			beginIndex = 0;
		
		endIndex = beginIndex + length;
	}

    //----------------------------------
	//  kerning
    //----------------------------------

	/**
	 *  A Boolean value that indicates whether kerning
	 *  is enabled (<code>true</code>) or disabled (<code>false</code>).
	 *  Kerning adjusts the pixels between certain character pairs
	 *  to improve readability, and should be used only when necessary,
	 *  such as with headings in large fonts.
	 *  Kerning is supported for embedded fonts only. 
	 *  Certain fonts, such as Verdana, and monospaced fonts,
	 *  such as Courier New, do not support kerning.
	 *
	 *  @default false
	 */
	public function get kerning():Boolean
	{
		return Boolean(getTextFormat().kerning);
	}

	/**
	 *  @private
	 */
	public function set kerning(value:Boolean):void
	{
		var tf:TextFormat = getTextFormat();
		tf.kerning = value;
		setTextFormat(tf);
	}

    //----------------------------------
	//  letterSpacing
    //----------------------------------

	/**
	 *  The number of additional pixels to appear between each character.
	 *  A positive value increases the character spacing
	 *  beyond the normal spacing, while a negative value decreases it.
	 * 
	 *  @default 0
	 */
	public function get letterSpacing():Number
	{
		return Number(getTextFormat().letterSpacing);
	}

	/**
	 *  @private
	 */
	public function set letterSpacing(value:Number):void
	{
		var tf:TextFormat = getTextFormat();
		tf.letterSpacing = value;
		setTextFormat(tf);
	}

    //----------------------------------
	//  modifiesSelection
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the modifiesSelection property.
	 */
	private var _modifiesSelection:Boolean;

	/**
	 *  Whether the TextRange modifies the currenly selected text.
	 *  Set by the constructor.
	 */
	public function get modifiesSelection():Boolean
	{
		return _modifiesSelection;
	}

    //----------------------------------
	//  owner
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the owner property.
	 */
	private var _owner:UIComponent;

	/**
	 *  The control that contains the text.
	 *  The owner control must have a <code>textField</code> property,
	 *  or, as is the case of the RichTextEditor control,
	 *  a <code>textArea</code> property.
	 *  The owner of the text in a RichTextEditor control is the
	 *  RichTextEditor control, not its TextArea subcontrol.
	 *  Initially set by the constructor.
	 */
	public function get owner():UIComponent
	{
		return _owner;
	}

	/**
	 *  @private
	 */
	public function set owner(value:UIComponent):void
	{
		_owner = value;

		try
		{
			textField = _owner["textArea"]["textField"];
		}
		catch(e:Error)
		{
			textField = _owner["textField"];
		}
	}

    //----------------------------------
	//  text
    //----------------------------------

	/**
	 *  Plain-text contents of the range.
	 */
	public function get text():String
	{
		return textField.text.substring(beginIndex, endIndex);
	}

	/**
	 *  @private
	 */
	public function set text(value:String):void
	{
		var oldBeginIndex:int = beginIndex;
		textField.replaceText(beginIndex,endIndex,value);
		// workaround for what seems to be a player bug (#207147)
		// where a zero length selection is shifted
		// (if no bug, this code should never execute)
		if ((oldBeginIndex == 0) && (beginIndex > 0))
			beginIndex = 0;
		endIndex = beginIndex + value.length;
	}

    //----------------------------------
	//  textAlign
    //----------------------------------

	/**
	 *  Alignment of the text in the range.
	 *  The flash.text.TextFormatAlign constants specify the valid values.
	 *  Setting this property to any other value has no effect.
	 *  If the range has multiple alignments, this value is <code>null</code>.
	 * 
	 *  @see flash.text.TextFormatAlign
	 */
	public function get textAlign():String
	{
		return getTextFormat().align;
	}

	/**
	 *  @private
	 */
	public function set textAlign(value:String):void
	{
		var tf:TextFormat = getTextFormat();
		tf.align = value;
		setTextFormat(tf);
	}

    //----------------------------------
	//  textDecoration
    //----------------------------------

	/**
	 *  Decoration of the font in the range, as "underline"
	 *  or "normal". Setting the property to any other string results
	 *  in normal text.
	 *  If the range has multiple decoration settings, this value is
	 *  <code>null</code>.
	 */
	public function get textDecoration():String
	{
		return getTextFormat().underline ? "underline" : "normal";
	}

	/**
	 *  @private
	 */
	public function set textDecoration(value:String):void
	{
		var tf:TextFormat = getTextFormat();
		tf.underline = (value == "underline") ? true : false;
		setTextFormat(tf);
	}

    //----------------------------------
	//  url
    //----------------------------------

	/**
	 *  URL for a hypertext link in the range.
	 *  If the range does not include a link, the value
	 *  is the empty string.
	 *  If the range includes multiple links, the value
	 *  is <code>null</code>.
	 */
	public function get url():String
	{
		return getTextFormat().url;
	}

	/**
	 *  @private
	 */
	public function set url(value:String):void
	{
		var tf:TextFormat = getTextFormat();

		if (value != "")
		{
			tf.url = value;
			tf.target = "_blank";
		}
		else if (tf.url != "")
		{
			tf.url = "";
			tf.target = "";
		}

		setTextFormat(tf);
	}

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function getTextFormat():TextFormat
	{
		var tf:TextFormat;
		if (_modifiesSelection && beginIndex == endIndex)
			tf = textField.defaultTextFormat;
		else
			tf = textField.getTextFormat(beginIndex, endIndex);
		return tf;
	}

	/**
	 *  @private
	 */
	private function setTextFormat(tf:TextFormat):void
	{
		if (_modifiesSelection && beginIndex == endIndex)
			textField.defaultTextFormat = tf;
		else
			textField.setTextFormat(tf,beginIndex, endIndex);
	}
}

}