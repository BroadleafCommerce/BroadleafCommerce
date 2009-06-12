////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  The ITextField interface defines the basic set of APIs
 *  for flash.display.TextField
 */

    /**
     *  @copy flash.text.TextField#alwaysShowSelection
     */
    function get alwaysShowSelection():Boolean;
    function set alwaysShowSelection(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#antiAliasType
     */
    function get antiAliasType():String;
    function set antiAliasType(antiAliasType:String):void;

    /**
     *  @copy flash.text.TextField#autoSize
     */
    function get autoSize():String;
    function set autoSize(value:String):void;

    /**
     *  @copy flash.text.TextField#background
     */
    function get background():Boolean;
    function set background(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#backgroundColor
     */
    function get backgroundColor():uint;
    function set backgroundColor(value:uint):void;

    /**
     *  @copy flash.text.TextField#border
     */
    function get border():Boolean;
    function set border(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#borderColor
     */
    function get borderColor():uint;
    function set borderColor(value:uint):void;

    /**
     *  @copy flash.text.TextField#bottomScrollV
     */
    function get bottomScrollV():int;

    /**
     *  @copy flash.text.TextField#caretIndex
     */
    function get caretIndex():int;

    /**
     *  @copy flash.text.TextField#condenseWhite
     */
    function get condenseWhite():Boolean;
    function set condenseWhite(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#defaultTextFormat
     */
    function get defaultTextFormat():TextFormat;
    function set defaultTextFormat(format:TextFormat):void;

    /**
     *  @copy flash.text.TextField#embedFonts
     */
    function get embedFonts():Boolean;
    function set embedFonts(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#gridFitType
     */
    function get gridFitType():String;
    function set gridFitType(gridFitType:String):void;

    /**
     *  @copy flash.text.TextField#htmlText
     */
    function get htmlText():String;
    function set htmlText(value:String):void;

    /**
     *  @copy flash.text.TextField#length
     */
    function get length():int;

    /**
     *  @copy flash.text.TextField#maxChars
     */
    function get maxChars():int;
    function set maxChars(value:int):void;

    /**
     *  @copy flash.text.TextField#maxScrollH
     */
    function get maxScrollH():int;

    /**
     *  @copy flash.text.TextField#maxScrollV
     */
    function get maxScrollV():int;

    /**
     *  @copy flash.text.TextField#mouseWheelEnabled
     */
    function get mouseWheelEnabled():Boolean;
    function set mouseWheelEnabled(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#multiline
     */
    function get multiline():Boolean;
    function set multiline(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#numLines
     */
    function get numLines():int;

    /**
     *  @copy flash.text.TextField#displayAsPassword
     */
    function get displayAsPassword():Boolean;
    function set displayAsPassword(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#restrict
     */
    function get restrict():String;
    function set restrict(value:String):void;

    /**
     *  @copy flash.text.TextField#scrollH
     */
    function get scrollH():int;
    function set scrollH(value:int):void;

    /**
     *  @copy flash.text.TextField#scrollV
     */
    function get scrollV():int;
    function set scrollV(value:int):void;

    /**
     *  @copy flash.text.TextField#selectable
     */
    function get selectable():Boolean;
    function set selectable(value:Boolean):void;

    /**
     *  @copy flash.text.TextField#selectionBeginIndex
     */
    function get selectionBeginIndex():int;

    /**
     *  @copy flash.text.TextField#selectionEndIndex
     */
    function get selectionEndIndex():int;

    /**
     *  @copy flash.text.TextField#sharpness
     */
    function get sharpness():Number;
    function set sharpness(value:Number):void;

    /**
     *  @copy flash.text.TextField#styleSheet
     */
    function get styleSheet():StyleSheet;
    function set styleSheet(value:StyleSheet):void;

    /**
     *  @copy flash.text.TextField#text
     */
    function get text():String;
    function set text(value:String):void;

    /**
     *  @copy flash.text.TextField#textColor
     */
    function get textColor():uint;
    function set textColor(value:uint):void;

    /**
     *  @copy flash.text.TextField#textHeight
     */
    function get textHeight():Number;

    /**
     *  @copy flash.text.TextField#textWidth
     */
    function get textWidth():Number;

    /**
     *  @copy flash.text.TextField#thickness
     */
    function get thickness():Number;
    function set thickness(value:Number):void;

    /**
     *  @copy flash.text.TextField#type
     */
    function get type():String;
    function set type(value:String):void;

    /**
     *  @copy flash.text.TextField#wordWrap
     */
    function get wordWrap():Boolean;
    function set wordWrap(value:Boolean):void;  
    

    /**
     *  @copy flash.text.TextField#appendText()
     */
    function appendText(newText:String):void;

    /**
     *  @copy flash.text.TextField#getCharBoundaries()
     */
    function getCharBoundaries(charIndex:int):Rectangle;

    /**
     *  @copy flash.text.TextField#getCharIndexAtPoint()
     */
    function getCharIndexAtPoint(x:Number, y:Number):int;

    /**
     *  @copy flash.text.TextField#getFirstCharInParagraph()
     */
    function getFirstCharInParagraph(charIndex:int):int;

    /**
     *  @copy flash.text.TextField#getLineIndexAtPoint()
     */
    function getLineIndexAtPoint(x:Number, y:Number):int;

    /**
     *  @copy flash.text.TextField#getLineIndexOfChar()
     */
    function getLineIndexOfChar(charIndex:int):int;

    /**
     *  @copy flash.text.TextField#getLineLength()
     */
    function getLineLength(lineIndex:int):int;

    /**
     *  @copy flash.text.TextField#getLineMetrics()
     */
    function getLineMetrics(lineIndex:int):TextLineMetrics;

    /**
     *  @copy flash.text.TextField#getLineOffset()
     */
    function getLineOffset(lineIndex:int):int;

    /**
     *  @copy flash.text.TextField#getLineText()
     */
    function getLineText(lineIndex:int):String;

    /**
     *  @copy flash.text.TextField#getParagraphLength()
     */
    function getParagraphLength(charIndex:int):int;

    /**
     *  @copy flash.text.TextField#getTextFormat()
     */
    function getTextFormat(beginIndex:int=-1, endIndex:int=-1):TextFormat;

    /**
     *  @copy flash.text.TextField#replaceSelectedText()
     */
    function replaceSelectedText(value:String):void;

    /**
     *  @copy flash.text.TextField#replaceText()
     */
    function replaceText(beginIndex:int, endIndex:int, newText:String):void;

    /**
     *  @copy flash.text.TextField#setSelection()
     */
    function setSelection(beginIndex:int, endIndex:int):void;

    /**
     *  @copy flash.text.TextField#setTextFormat()
     */
    function setTextFormat(format:TextFormat,
                        beginIndex:int=-1,
                        endIndex:int=-1):void;

    /**
     *  @copy flash.text.TextField#getImageReference()
     */
    function getImageReference(id:String):DisplayObject;
    
    /**
     *  @copy flash.text.TextField#useRichTextClipboard
     */
    function set useRichTextClipboard(value:Boolean):void;
    function get useRichTextClipboard():Boolean;

