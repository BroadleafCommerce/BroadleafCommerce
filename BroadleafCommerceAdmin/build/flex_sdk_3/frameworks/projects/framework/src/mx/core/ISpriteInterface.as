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
 *  The ISprite interface defines the basic set of APIs
 *  for web version of flash.display.Sprite
 */

import flash.display.DisplayObject;
import flash.display.Sprite;
import flash.display.Graphics;
import flash.geom.Rectangle;
import flash.media.SoundTransform;

    /**
     *  @copy flash.display.Sprite#graphics
     */
    function get graphics():Graphics;

    /**
     *  @copy flash.display.Sprite#buttonMode
     */
    function get buttonMode():Boolean;
    function set buttonMode(value:Boolean):void;

    /**
     *  @copy flash.display.Sprite#startDrag()
     */
    function startDrag(lockCenter:Boolean = false, bounds:Rectangle = null):void;

    /**
     *  @copy flash.display.Sprite#stopDrag()
     */
    function stopDrag():void;

    /**
     *  @copy flash.display.Sprite#dropTarget
     */
    function get dropTarget():DisplayObject;

    /**
     *  @copy flash.display.Sprite#hitArea
     */
    function get hitArea():Sprite;
    function set hitArea(value:Sprite):void;


    /**
     *  @copy flash.display.Sprite#useHandCursor
     */
    function get useHandCursor():Boolean;
    function set useHandCursor(value:Boolean):void;


    /**
     *  @copy flash.display.Sprite#soundTransform
     */
    function get soundTransform():SoundTransform;
    function set soundTransform(sndTransform:SoundTransform):void;

