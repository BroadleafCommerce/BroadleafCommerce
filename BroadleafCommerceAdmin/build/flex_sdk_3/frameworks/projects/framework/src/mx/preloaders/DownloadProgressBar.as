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

package mx.preloaders
{

import flash.display.DisplayObject;
import flash.display.GradientType;
import flash.display.Graphics;
import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.ProgressEvent;
import flash.events.TimerEvent;
import flash.geom.Matrix;
import flash.geom.Rectangle;
import flash.net.URLRequest;
import flash.system.ApplicationDomain;
import flash.system.LoaderContext;
import flash.text.TextField;
import flash.text.TextFormat;
import flash.utils.Timer;
import flash.utils.getDefinitionByName;
import flash.utils.getTimer;
import mx.events.FlexEvent;
import mx.events.RSLEvent;
import mx.graphics.RectangularDropShadow;
import mx.graphics.RoundedRectangle;

/**
 *  The DownloadProgressBar class displays download progress.
 *  It is used by the Preloader control to provide user feedback
 *  while the application is downloading and loading. 
 *
 *  <p>The download progress bar displays information about 
 *  two different phases of the application: 
 *  the download phase and the initialization phase. </p>
 *
 *  <p>In the <code>&lt;mx:Application&gt;</code> tag, use the 
 *  the <code>preloader</code> property to specify the name of your subclass.</p>
 *
 *  <p>You can implement a custom download progress bar component 
 *  by creating a subclass of the DownloadProgressBar class. 
 *  Do not implement a download progress bar as an MXML component 
 *  because it loads too slowly.</p>
 *
 *  @see mx.core.Application
 *  @see mx.preloaders.IPreloaderDisplay
 *  @see mx.preloaders.Preloader
 */
public class DownloadProgressBar extends Sprite implements IPreloaderDisplay
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
	public function DownloadProgressBar() 
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  The minimum number of milliseconds
	 *  that the display should appear visible.
	 *  If the downloading and initialization of the application
	 *  takes less time than this value, then Flex pauses for this amount
	 *  of time before dispatching the <code>complete</code> event.
	 *
	 *  @default 0
	 */
	protected var MINIMUM_DISPLAY_TIME:uint = 0;
	
	/**
	 *  The percentage of the progress bar that the downloading phase
	 *  fills when the SWF file is fully downloaded.
	 *  The rest of the progress bar is filled during the initializing phase.
	 *  This should be a value from 0 to 100. 
	 *
	 *  @default 60
	 */
	protected var DOWNLOAD_PERCENTAGE:uint = 60;

	/**
	 *  @private
	 */
	private var _showProgressBar:Boolean = true;
		
	/**
	 *  @private
	 *  Cached Rectangle returned by the labelRect getter.
	 */
	private var _labelRect:Rectangle = labelRect;

	/**
	 *  @private
	 *  Cached Rectangle returned by the percentRect getter.
	 */
	private var _percentRect:Rectangle = percentRect;

	/**
	 *  @private
	 *  Cached RoundedRectangle returned by the borderRect getter.
	 */
	private var _borderRect:RoundedRectangle = borderRect;

	/**
	 *  @private
	 *  Cached RoundedRectangle returned by the barFrameRect getter.
	 */
	private var _barFrameRect:RoundedRectangle = barFrameRect;

	/**
	 *  @private
	 *  Cached RoundedRectangle returned by the barRect getter.
	 */
	private var _barRect:RoundedRectangle = barRect; 

	/**
	 *  @private
	 */
	private var _xOffset:Number = 20;
	
	/**
	 *  @private
	 */
	private var _yOffset:Number = 20;
	
	/**
	 *  @private
	 */
	private var	_maximum:Number = 0;
	
	/**
	 *  @private
	 */
	private var	_value:Number = 0;
	
	/**
	 *  @private
	 */
	private var _barSprite:Sprite;

	/**
	 *  @private
	 */
	private var _barFrameSprite:Sprite;
	
	/**
	 *  @private
	 */
	private var _labelObj:TextField;

	/**
	 *  @private
	 */
	private var _percentObj:TextField;

	/**
	 *  @private
	 */
	private var _startTime:int;

	/**
	 *  @private
	 */
	private var _displayTime:int;
		
	/**
	 *  @private
	 */
	private var _startedLoading:Boolean = false;

	/**
	 *  @private
	 */
	private var _startedInit:Boolean = false;

	/**
	 *  @private
	 */
	private var _showingDisplay:Boolean = false;
	
	/**
	 *  @private
	 */
	private var _displayStartCount:uint = 0; 

	/**
	 *  @private
	 */
	private var _initProgressCount:uint = 0;

	/**
	 *  @private
	 */
	private var _initProgressTotal:uint = 12;

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  visible
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the visible property.
	 */
	private var _visible:Boolean = false;

	/**
	 *  Specifies whether the download progress bar is visible.
	 *
	 *  <p>When the Preloader control determines that the progress bar should be displayed, 
	 *  it sets this value to <code>true</code>. When the Preloader control determines that
	 *  the progress bar should be hidden, it sets the value to <code>false</code>.</p>
	 *
	 *  <p>A subclass of the DownloadProgressBar class should never modify this property. 
	 *  Instead, you can override the setter method to recognize when 
	 *  the Preloader control modifies it, and perform any necessary actions. </p>
	 *
	 *  @default false 
	 */
	override public function get visible():Boolean
	{
		return _visible;
	}

	/**
	 *  @private
	 */
	override public function set visible(value:Boolean):void
	{
		if (!_visible && value) 
			show();
		
		else if (_visible && !value ) 
			hide();
		
		_visible = value;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties: IPreloaderDisplay
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  backgroundAlpha
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the backgroundAlpha property.
	 */
	private var _backgroundAlpha:Number = 1;

	/**
     *  Alpha level of the SWF file or image defined by 
     *  the <code>backgroundImage</code> property, or the color defined by 
	 *  the <code>backgroundColor</code> property. 
	 *  Valid values range from 0 to 1.0.	 
	 *
	 *  <p>You can specify either a <code>backgroundColor</code> 
	 *  or a <code>backgroundImage</code>, but not both.</p>
	 *
	 *  @default 1.0
	 *
	 */
	public function get backgroundAlpha():Number
	{
		if (!isNaN(_backgroundAlpha))
			return _backgroundAlpha;
		else
			return 1;
	}
	
	/**
	 *  @private
	 */
	public function set backgroundAlpha(value:Number):void
	{
		_backgroundAlpha = value;
	}
	
	//----------------------------------
	//  backgroundColor
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the backgroundColor property.
	 */
	private var _backgroundColor:uint;

	/**
     *  Background color of a download progress bar.
     *  You can have either a <code>backgroundColor</code> or a
     *  <code>backgroundImage</code>, but not both.
	 */	
	public function get backgroundColor():uint
	{
		return _backgroundColor;
	}

	/**
	 *  @private
	 */
	public function set backgroundColor(value:uint):void
	{
		_backgroundColor = value;
	}
	
	//----------------------------------
	//  backgroundImage
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the backgroundImage property.
	 */
	private var _backgroundImage:Object;

	/**
	 *  The background image of the application,
	 *  which is passed in by the preloader.
	 *  You can specify either a <code>backgroundColor</code> 
	 *  or a <code>backgroundImage</code>, but not both.
	 *
	 *  <p>A value of null means "not set". 
	 *  If this style and the <code>backgroundColor</code> style are undefined, 
	 *  the component has a transparent background.</p>
	 *
	 *  <p>The preloader does not display embedded images. 
	 *  You can only use images loaded at runtime.</p>
	 *
	 *  @default null
	 */
	public function get backgroundImage():Object
	{
		return _backgroundImage;
	}
	
	/**
	 *  @private
	 */
	public function set backgroundImage(value:Object):void
	{
		_backgroundImage = value;
	}
	
	//----------------------------------
	//  backgroundSize
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the backgroundSize property.
	 */
	private var _backgroundSize:String = "";

	/**
     *  Scales the image specified by <code>backgroundImage</code>
     *  to different percentage sizes.
     *  A value of <code>"100%"</code> stretches the image
     *  to fit the entire component.
     *  To specify a percentage value, you must include the percent sign (%).
     *  A value of <code>"auto"</code>, maintains
     *  the original size of the image.
	 *
	 *  @default "auto"
	 */
	public function get backgroundSize():String
	{
		return _backgroundSize;
	}
	
	/**
	 *  @private
	 */
	public function set backgroundSize(value:String):void
	{
		_backgroundSize = value;
	}
	
	//----------------------------------
	//  preloader
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the preloader property.
	 */
	private var _preloader:Sprite; 
	 
	/**
	 *  The Preloader class passes in a reference to itself to the display class
	 *  so that it can listen for events from the preloader.
	 */
	public function set preloader(value:Sprite):void
	{
		_preloader = value;
	
		value.addEventListener(ProgressEvent.PROGRESS, progressHandler);	
		value.addEventListener(Event.COMPLETE, completeHandler);
		
		value.addEventListener(RSLEvent.RSL_PROGRESS, rslProgressHandler);
		value.addEventListener(RSLEvent.RSL_COMPLETE, rslCompleteHandler);
		value.addEventListener(RSLEvent.RSL_ERROR, rslErrorHandler);
		
		value.addEventListener(FlexEvent.INIT_PROGRESS, initProgressHandler);
		value.addEventListener(FlexEvent.INIT_COMPLETE, initCompleteHandler);
	}

	//----------------------------------
	//  stageHeight
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the stageHeight property.
	 */
	private var _stageHeight:Number = 375;

	/**
	 *  The height of the stage,
	 *  which is passed in by the Preloader class.
	 */
	public function get stageHeight():Number 
	{
		return _stageHeight;
	}

	/**
	 *  @private
	 */
	public function set stageHeight(value:Number):void 
	{
		_stageHeight = value;
	}
		
	//----------------------------------
	//  stageWidth
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the stageHeight property.
	 */
	private var _stageWidth:Number = 500;

	/**
	 *  The width of the stage,
	 *  which is passed in by the Preloader class.
	 */
	public function get stageWidth():Number 
	{
		return _stageWidth;
	}
	
	/**
	 *  @private
	 */
	public function set stageWidth(value:Number):void 
	{
		_stageWidth = value;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  barFrameRect
	//----------------------------------

	/**
	 *  The dimensions of the progress bar border.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get barFrameRect():RoundedRectangle
	{
		return new RoundedRectangle(14, 40, 154, 4);
	}
	
	//----------------------------------
	//  barRect
	//----------------------------------

	/**
	 *  The dimensions of the progress bar.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get barRect():RoundedRectangle
	{
		return new RoundedRectangle(14, 39, 154, 6, 0);
	}
	
	//----------------------------------
	//  borderRect
	//----------------------------------

	/**
	 *  The dimensions of the border of the display.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get borderRect():RoundedRectangle
	{
		return new RoundedRectangle(0, 0, 182, 60, 4);
	}
	
	//----------------------------------
	//  downloadingLabel
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the downloadingLabel property.
     */
	private var _downloadingLabel:String = "Loading";
	
	/**
	 *  The string to display as the label while in the downloading phase.
	 *
	 *  @default "Loading"
	 */
	protected function get downloadingLabel():String
	{
		return _downloadingLabel;	
	}

    /**
	 *  @private
     */
	protected function set downloadingLabel(value:String):void
	{
		_downloadingLabel = value;
	}
	
	//----------------------------------
  	//  initializingLabel
  	//----------------------------------
  
    /**
  	 *  @private
  	 *  Storage for the initializingLabel property.
     */
  	private static var _initializingLabel:String = "Initializing";
  
  	/**
  	 *  The string to display as the label while in the initializing phase.
	 *
	 *  @default "Initializing"
  	 */
  	public static function get initializingLabel():String
  	{
  		return _initializingLabel;	
  	}
  
    /**
  	 *  @private
     */
  	public static function set initializingLabel(value:String):void
  	{
  		_initializingLabel = value;
  	}	
	
	//----------------------------------
	//  label
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the label property.
	 */
	private var	_label:String = "";

	/**
	 *  Text to display when the progress bar is active.
	 *  The Preloader class sets this value
	 *  before displaying the progress bar.
	 *  Implementing this property in a subclass is optional.
	 *
	 *  @default ""
	 */	
	protected function get label():String
	{
		return _label;
	}

	/**
	 *  @private
	 */
	protected function set label(value:String):void
	{
		if (!(value is Function))
			_label = value;

		draw();
	}
	
	//----------------------------------
	//  labelFormat
	//----------------------------------

	/**
	 *  The TextFormat object of the TextField component of the label.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get labelFormat():TextFormat
	{
		var tf:TextFormat = new TextFormat();
		tf.color = 0x333333;
		tf.font = "Verdana";
		tf.size = 10;
		return tf;
	}

	//----------------------------------
	//  labelRect
	//----------------------------------

	/**
	 *  The dimensions of the TextField component for the label. 
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get labelRect():Rectangle
	{
		return new Rectangle(14, 17, 100, 16);
	}
	
	//----------------------------------
	//  percentFormat
	//----------------------------------

	/**
	 *  The TextFormat of the TextField component for displaying the percent.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get percentFormat():TextFormat
	{
		var tf:TextFormat = new TextFormat();
		tf.align = "right";
		tf.color  = 0x000000;
		tf.font = "Verdana";
		tf.size = 10;
		return tf;
	}
	
	//----------------------------------
	//  percentRect
	//----------------------------------

	/**
	 *  The dimensions of the TextField component for displaying the percent.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get percentRect():Rectangle
	{
		return new Rectangle(108, 4, 34, 16);
	}
	
	//----------------------------------
	//  showLabel
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the showLabel property.
	 */
	private var _showLabel:Boolean = true;

	/**
	 *  Controls whether to display the label, <code>true</code>, 
	 *  or not, <code>false</code>.
	 *
	 *  @default true
	 */	
	protected function get showLabel():Boolean
	{
		return _showLabel;
	}
		
	/**
	 *  @private
	 */	
	protected function set showLabel(value:Boolean):void
	{
		_showLabel = value;
		
		draw();
	}
	
	//----------------------------------
	//  showPercentage
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the showPercentage property.
	 */
	private var _showPercentage:Boolean = false;

	/**
	 *  Controls whether to display the percentage, <code>true</code>, 
	 *  or not, <code>false</code>.
	 *
	 *  @default true
	 */	
	protected function get showPercentage():Boolean
	{
		return _showPercentage;
	}

	/**
	 *  @private
	 */	
	protected function set showPercentage(value:Boolean):void
	{
		_showPercentage = value;
		
		draw();
	}
		
	//--------------------------------------------------------------------------
	//
	//  Methods:IPreloaderDisplay
	//
	//--------------------------------------------------------------------------

	/**
	 *  Called by the Preloader after the download progress bar
	 *  has been added as a child of the Preloader. 
	 *  This should be the starting point for configuring your download progress bar. 
	 */
	public function initialize():void
	{
		_startTime = getTimer();
		
		center(stageWidth, stageHeight);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Centers the download progress bar based on the passed in dimensions.
	 *
	 *  @param width The width of the area in which to center the download progress bar.
	 *
	 *  @param height The height of the area in which to center the download progress bar.
	 */
	protected function center(width:Number, height:Number):void
	{
		_xOffset = Math.floor((width - _borderRect.width) / 2);
		_yOffset = Math.floor((height - _borderRect.height) / 2);
	}
	
	/**
	 *  @private
	 *  Updates the display.
	 */
	private function draw():void
	{
		var percentage:Number;

		if (_startedLoading)
		{
			if (!_startedInit)
			{
				// 0 to MaxDL Percentage
				percentage = Math.round(getPercentLoaded(_value, _maximum) *
										DOWNLOAD_PERCENTAGE / 100);
			}
			else
			{
				// MaxDL percentage to 100
				percentage = Math.round((getPercentLoaded(_value, _maximum) *
										(100 - DOWNLOAD_PERCENTAGE) / 100) +
										DOWNLOAD_PERCENTAGE);
			}
		}
		else
		{
			percentage = getPercentLoaded(_value, _maximum);
		}
				
		if (_labelObj)
			_labelObj.text = _label;
		
		if (_percentObj)
		{
			if (!_showPercentage) 
			{
				_percentObj.visible = false;
				_percentObj.text = "";
			}
			else 
			{
				_percentObj.text = String(percentage) + "%";
			}
		}
		
		if (_barSprite && _barFrameSprite)
		{
			if (!_showProgressBar)	
			{
				_barSprite.visible = false;
				_barFrameSprite.visible = false;
			}
			else 
			{
				drawProgressBar(percentage);	
			}
		}
	}
	
	/**
	 *  Creates the subcomponents of the display.
	 */
	protected function createChildren():void
	{
		var g:Graphics = graphics;

		var labelObj:TextField;
		var percentObj:TextField;
		
		// Draw the background first
		// Same value as StyleManager.NOT_A_COLOR. However, we don't want to bring in StyleManager at this point. 
		if (backgroundColor != 0xFFFFFFFF)
		{
			g.beginFill(backgroundColor, backgroundAlpha);
			g.drawRect(0, 0, stageWidth, stageHeight);
		}
				
		if (backgroundImage != null)
			loadBackgroundImage(backgroundImage);
		
		_barFrameSprite = new Sprite();
		_barSprite = new Sprite();
		
		addChild(_barFrameSprite);	
		addChild(_barSprite);

		// Draw the outside border and fill.
		g.beginFill(0xCCCCCC, 0.4);	
		g.drawRoundRect(calcX(_borderRect.x),
				 		calcY(_borderRect.y),
				 		_borderRect.width,
				 		_borderRect.height,
				 		_borderRect.cornerRadius * 2,
				 		_borderRect.cornerRadius * 2);
		g.drawRoundRect(calcX(_borderRect.x + 1),
					    calcY(_borderRect.y + 1),
					 	_borderRect.width - 2,
						_borderRect.height - 2,
					 	_borderRect.cornerRadius - 1 * 2,
					 	_borderRect.cornerRadius - 1 * 2);
		g.endFill();				

		g.beginFill(0xCCCCCC,0.4);
		g.drawRoundRect(calcX(_borderRect.x + 1),
					 	calcY(_borderRect.y + 1),
					 	_borderRect.width - 2,
					 	_borderRect.height - 2,
					 	_borderRect.cornerRadius - 1 * 2,
					 	_borderRect.cornerRadius - 1 * 2);
		g.endFill();
		
		var frame_g:Graphics = _barFrameSprite.graphics;
		
		// Draw the bar frame border and fill
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(_barFrameRect.width, _barFrameRect.height,
								 Math.PI / 2, calcX(_barFrameRect.x), calcY(_barFrameRect.y));
		
		frame_g.beginGradientFill(GradientType.LINEAR, 
								  [ 0x5C6266, 0xB5B8BA ],
								  [ 1.0, 1.0 ],
								  [ 0, 0xFF ],
								  matrix);
		frame_g.drawRoundRect(calcX(_barFrameRect.x),
							  calcY(_barFrameRect.y),
							  _barFrameRect.width,
							  _barFrameRect.height,
							  _barFrameRect.cornerRadius * 2,
							  _barFrameRect.cornerRadius * 2); 
		frame_g.drawRoundRect(calcX(_barFrameRect.x + 1),
							  calcY(_barFrameRect.y + 1),
							  _barFrameRect.width - 2,
							  _barFrameRect.height - 2,
							  _barFrameRect.cornerRadius * 2,
							  _barFrameRect.cornerRadius * 2);					  
		frame_g.endFill();						  

		// Attach the label TextField.
		_labelObj = new TextField();
		_labelObj.x = calcX(_labelRect.x);
		_labelObj.y = calcY(_labelRect.y);
		_labelObj.width = _labelRect.width;
		_labelObj.height = _labelRect.height;			
		_labelObj.selectable = false;
		_labelObj.defaultTextFormat = labelFormat;
		addChild(_labelObj);
		
		// Attach the percentage TextField.
		_percentObj = new TextField();
		_percentObj.x = calcX(_percentRect.x);
		_percentObj.y = calcY(_percentRect.y);
		_percentObj.width = _percentRect.width;
		_percentObj.height = _percentRect.height;			
		_percentObj.selectable = false;
		_percentObj.defaultTextFormat = percentFormat;
		addChild(_percentObj);
		
		// Create dropshadow
		var ds:RectangularDropShadow = new RectangularDropShadow();
		ds.color = 0x000000;
		ds.angle = 90;
		ds.alpha = .6;
		ds.distance = 2;
		ds.tlRadius = ds.trRadius = ds.blRadius = ds.brRadius = _borderRect.cornerRadius;
		ds.drawShadow(g, 
					  calcX(_borderRect.x),
				 	  calcY(_borderRect.y),
					  _borderRect.width,
					  _borderRect.height);
							  
		// Draw the top line		
		g.lineStyle(1,0xFFFFFF, .3);
		g.moveTo(calcX(_borderRect.x) + _borderRect.cornerRadius, calcY(_borderRect.y));
		g.lineTo(calcX(_borderRect.x) - _borderRect.cornerRadius + _borderRect.width, calcY(_borderRect.y));				  
	}
	
	/**
	 *  @private
	 *  Draws the progress bar.
	 */
	private function drawProgressBar(percentage:Number):void
	{
		var g:Graphics = _barSprite.graphics;
		g.clear();
		
		var colors:Array = [ 0xFFFFFF, 0xFFFFFF ];
		var ratios:Array = [ 0, 0xFF ];
		var matrix:Matrix = new Matrix();
		
		// Middle
		var barWidth:Number = _barRect.width * percentage / 100;
		var barWidthSplit:Number = barWidth / 2;
		var barHeight:Number = _barRect.height-4;
		var barX:Number = calcX(_barRect.x);
		var barY:Number = calcY(_barRect.y) + 2;
		var barY2:Number;
		
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .39, .85],
							ratios, matrix);
			
		g.drawRect(barX, barY, barWidthSplit, barHeight);		
						
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX + barWidthSplit, barY);				
		g.beginGradientFill(GradientType.LINEAR, colors, [.85, 1.0],
							ratios, matrix);				
		g.drawRect(barX + barWidthSplit, barY, barWidthSplit, barHeight);						

		// Outer highlight
		barWidthSplit = barWidth / 3;
		barHeight = _barRect.height;
		barY = calcY(_barRect.y);
		barY2 = barY + barHeight - 1;
		
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .05, .15],
							ratios, matrix);
			
		g.drawRect(barX, barY, barWidthSplit, 1);
		g.drawRect(barX, barY2, barWidthSplit, 1);
			
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX + barWidthSplit, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .15, .25],
							ratios, matrix);
			
		g.drawRect(barX + barWidthSplit, barY, barWidthSplit, 1);
		g.drawRect(barX + barWidthSplit, barY2, barWidthSplit, 1);
							
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX + barWidthSplit * 2, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .25, .1],
							ratios, matrix);
			
		g.drawRect(barX + barWidthSplit * 2, barY, barWidthSplit, 1);
		g.drawRect(barX + barWidthSplit * 2, barY2, barWidthSplit, 1);					
		
		// Inner highlight
		barWidthSplit = barWidth / 3;
		barHeight = _barRect.height;
		barY = calcY(_barRect.y) + 1;
		barY2 = calcY(_barRect.y) + barHeight - 2;
		
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .15, .30],
							ratios, matrix);
			
		g.drawRect(barX, barY, barWidthSplit, 1);
		g.drawRect(barX, barY2, barWidthSplit, 1);
			
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX + barWidthSplit, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .30, .40],
							ratios, matrix);
			
		g.drawRect(barX + barWidthSplit, barY, barWidthSplit, 1);
		g.drawRect(barX + barWidthSplit, barY2, barWidthSplit, 1);
							
		matrix.createGradientBox(barWidthSplit, barHeight,
								 0, barX + barWidthSplit * 2, barY);
		g.beginGradientFill(GradientType.LINEAR, colors, [ .40, .25],
							ratios, matrix);
			
		g.drawRect(barX + barWidthSplit * 2, barY, barWidthSplit, 1);
		g.drawRect(barX + barWidthSplit * 2, barY2, barWidthSplit, 1);	
	}

	/**
	 *  Updates the display of the download progress bar
	 *  with the current download information. 
	 *  A typical implementation divides the loaded value by the total value
	 *  and displays a percentage.
	 *  If you do not implement this method, you should create
	 *  a progress bar that displays an animation to indicate to the user
	 *  that a download is occurring.
	 *
	 *  <p>The <code>setProgress()</code> method is only called
     *  if the application is being downloaded from a remote server
     *  and the application is not in the browser cache.</p>
     *
	 *  @param completed Number of bytes of the application SWF file
	 *  that have been downloaded.
	 *
	 *  @param total Size of the application SWF file in bytes.
	 */
	protected function setProgress(completed:Number, total:Number):void
	{
		if (!isNaN(completed) && 
		   !isNaN(total) &&
		   completed >= 0 && 
		   total > 0)
		{
			_value = Number(completed);
			_maximum = Number(total);
			draw();
		}	
	}	
	
	/**
	 *  Returns the percentage value of the application loaded. 
     *
	 *  @param loaded Number of bytes of the application SWF file
	 *  that have been downloaded.
	 *
	 *  @param total Size of the application SWF file in bytes.
	 *
	 *  @return The percentage value of the loaded application.
	 */
	protected function getPercentLoaded(loaded:Number, total:Number):Number
	{
		var perc:Number;
		
		if (loaded == 0 || total == 0 || isNaN(total) || isNaN(loaded))
			return 0;
		else 
		 	perc = 100 * loaded/total;

		if (isNaN(perc) || perc <= 0)
			return 0;
		else if (perc > 99)
			return 99;
		else
			return Math.round(perc);
	}
	
	/**
	 *  @private
	 *  Make the display class visible.
	 */
	private function show():void
	{
		_showingDisplay = true;
		calcScale();
		draw();
		_displayTime = getTimer(); // Time when the display is shown.
	}
	
	/**
	 *  @private
	 */
	private function hide():void
	{
	}
	
	/**
	 *  @private
	 */
	private function calcX(base:Number):Number
	{
		return base + _xOffset;
	}
	
	/**
	 *  @private
	 */
	private function calcY(base:Number):Number
	{
		return base + _yOffset;
	}
	
	/**
	 *  @private
	 *  Figure out the scale for the display class based on the stage size.
	 *  Then creates the children subcomponents.
	 */
	private function calcScale():void
	{
		if (stageWidth < 160 || stageHeight < 120)
		{
			scaleX = 1.0;
			scaleY = 1.0;
		}
		else if (stageWidth < 240 || stageHeight < 150)
		{
			// Scale to appropriate size
			createChildren();
			var scale:Number = Math.min(stageWidth / 240.0,
										stageHeight / 150.0);
			scaleX = scale;
			scaleY = scale;
		}
		else
		{
			createChildren();
		}
	}
	
	/**
	 *  Defines the algorithm for determining whether to show
	 *  the download progress bar while in the download phase.
	 *
	 *  @param elapsedTime number of milliseconds that have elapsed
	 *  since the start of the download phase.
	 *
	 *  @param event The ProgressEvent object that contains
	 *  the <code>bytesLoaded</code> and <code>bytesTotal</code> properties.
	 *
	 *  @return If the return value is <code>true</code>, then show the 
	 *  download progress bar.
	 *  The default behavior is to show the download progress bar 
	 *  if more than 700 milliseconds have elapsed
	 *  and if Flex has downloaded less than half of the bytes of the SWF file.
	 */
	protected function showDisplayForDownloading(elapsedTime:int,
											  event:ProgressEvent):Boolean
	{
		return elapsedTime > 700 &&
			event.bytesLoaded < event.bytesTotal / 2;
	}
	
	/**
	 *  Defines the algorithm for determining whether to show the download progress bar
	 *  while in the initialization phase, assuming that the display
	 *  is not currently visible.
	 *
	 *  @param elapsedTime number of milliseconds that have elapsed
	 *  since the start of the download phase.
	 *
	 *  @param count number of times that the <code>initProgress</code> event
	 *  has been received from the application.
	 *
	 *  @return If <code>true</code>, then show the download progress bar.
	 */
	protected function showDisplayForInit(elapsedTime:int, count:int):Boolean
	{
		return elapsedTime > 300 && count == 2;
	}
	
	/**
	 *  @private
	 */
	private function loadBackgroundImage(classOrString:Object):void
	{
		var cls:Class;
		
		// The "as" operator checks to see if classOrString
		// can be coerced to a Class
		if (classOrString && classOrString as Class)
		{
			// Load background image given a class pointer
			cls = Class(classOrString);
			initBackgroundImage(new cls());
		}
		else if (classOrString && classOrString is String)
		{
			try
			{
				cls = Class(getDefinitionByName(String(classOrString)));
			}
			catch(e:Error)
			{
				// ignore
			}

			if (cls)
			{
				var newStyleObj:DisplayObject = new cls();
				initBackgroundImage(newStyleObj);
			}
			else
			{
				// Loading the image is slightly different
				// than in Loader.loadContent()... is this on purpose?

				// Load background image from external URL
				var loader:Loader = new Loader();
				loader.contentLoaderInfo.addEventListener(
					Event.COMPLETE, loader_completeHandler);
				loader.contentLoaderInfo.addEventListener(
					IOErrorEvent.IO_ERROR, loader_ioErrorHandler);	
				var loaderContext:LoaderContext = new LoaderContext();
				loaderContext.applicationDomain = new ApplicationDomain(ApplicationDomain.currentDomain);
				loader.load(new URLRequest(String(classOrString)), loaderContext);		
			}
		}
	}
	
	/**
	 *  @private
	 */
	private function initBackgroundImage(image:DisplayObject):void
	{
		addChildAt(image,0);
		
		var backgroundImageWidth:Number = image.width;
		var backgroundImageHeight:Number = image.height;
		
		// Scale according to backgroundSize
		var percentage:Number = calcBackgroundSize();
		if (isNaN(percentage))
		{
			var sX:Number = 1.0;
			var sY:Number = 1.0;
		}
		else
		{
			var scale:Number = percentage * 0.01;
			sX = scale * stageWidth / backgroundImageWidth;
			sY = scale * stageHeight / backgroundImageHeight;
		}
		
		image.scaleX = sX;
		image.scaleY = sY;

		// Center everything.
		// Use a scrollRect to position and clip the image.
		var offsetX:Number =
			Math.round(0.5 * (stageWidth - backgroundImageWidth * sX));
		var offsetY:Number =
			Math.round(0.5 * (stageHeight - backgroundImageHeight * sY));

		image.x = offsetX;
		image.y = offsetY;

		// Adjust alpha to match backgroundAlpha
		if (!isNaN(backgroundAlpha))
			image.alpha = backgroundAlpha;
	}
	
	/**
	 *  @private
	 */
	private function calcBackgroundSize():Number
	{	
		var percentage:Number =	NaN;
		
		if (backgroundSize)
		{
			var index:int = backgroundSize.indexOf("%");
			if (index != -1)
				percentage = Number(backgroundSize.substr(0, index));
		}
		
		return percentage;
	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Event listener for the <code>ProgressEvent.PROGRESS</code> event. 
	 *  This implementation updates the progress bar
	 *  with the percentage of bytes downloaded.
	 *
	 *  @param event The event object.
	 */
	protected function progressHandler(event:ProgressEvent):void
	{
		var loaded:uint = event.bytesLoaded;
		var total:uint = event.bytesTotal;

		var elapsedTime:int = getTimer() - _startTime;
		
		// Only show the Loading phase if it will appear for awhile.
		if (_showingDisplay || showDisplayForDownloading(elapsedTime, event))		
		{
			if (!_startedLoading)
			{
				show();
				label = downloadingLabel;		
				_startedLoading = true;
			}

			setProgress(event.bytesLoaded, event.bytesTotal);
		}
	}
	
	/**
	 *  Event listener for the <code>Event.COMPLETE</code> event. 
	 *  The default implementation does nothing.
	 *
	 *  @param event The event object.
	 */
	protected function completeHandler(event:Event):void
	{
	}
	
	/**
	 *  Event listener for the <code>RSLEvent.RSL_PROGRESS</code> event. 
	 *  The default implementation does nothing.
	 *
	 *  @param event The event object.
	 */
	protected function rslProgressHandler(event:RSLEvent):void
	{
	}
	
	/**
	 *  Event listener for the <code>RSLEvent.RSL_COMPLETE</code> event. 
	 *
	 *  @param event The event object.
	 */
	protected function rslCompleteHandler(event:RSLEvent):void
	{
		label = "Loaded library " + event.rslIndex + " of " + event.rslTotal;
	}
	
	/**
	 *  Event listener for the <code>RSLEvent.RSL_ERROR</code> event. 
	 *  This event listner handles any errors detected when downloading an RSL.
	 *
	 *  @param event The event object.
	 */
	protected function rslErrorHandler(event:RSLEvent):void
	{
		_preloader.removeEventListener(ProgressEvent.PROGRESS,
									   progressHandler);	
		
		_preloader.removeEventListener(Event.COMPLETE,
									   completeHandler);
		
		_preloader.removeEventListener(RSLEvent.RSL_PROGRESS,
									   rslProgressHandler);

		_preloader.removeEventListener(RSLEvent.RSL_COMPLETE,
									   rslCompleteHandler);

		_preloader.removeEventListener(RSLEvent.RSL_ERROR,
									   rslErrorHandler);
		
		_preloader.removeEventListener(FlexEvent.INIT_PROGRESS,
									   initProgressHandler);

		_preloader.removeEventListener(FlexEvent.INIT_COMPLETE,
									   initCompleteHandler);
	
		if (!_showingDisplay)
		{
			show();
			_showingDisplay = true;
		}

		label = "RSL Error " + (event.rslIndex + 1) + " of " + event.rslTotal;
		
		var errorField:ErrorField = new ErrorField(this.parent);
		errorField.show(event.errorText);
	}
	
	/**
	 *  @private
	 *  Helper function that dispatches the Complete event to the preloader.
	 *
	 *  @param event The event object.
	 */
	private function timerHandler(event:Event = null):void
	{
		dispatchEvent(new Event(Event.COMPLETE)); 
	}
	
	/**
	 *  Event listener for the <code>FlexEvent.INIT_PROGRESS</code> event. 
	 *  This implementation updates the progress bar
	 *  each time the event is dispatched, and changes the text of the label. 
	 *
	 *  @param event The event object.
	 */
	protected function initProgressHandler(event:Event):void
	{
		var elapsedTime:int = getTimer() - _startTime;
		_initProgressCount++;
		
		if (!_showingDisplay &&
			showDisplayForInit(elapsedTime, _initProgressCount))
		{
			_displayStartCount = _initProgressCount;
			show();
		}
		else if (_showingDisplay)
		{
			if (!_startedInit)
			{
				// First init progress event.
				_startedInit = true;
				label = initializingLabel;
			}

			var loaded:Number = 100 * _initProgressCount /
								(_initProgressTotal - _displayStartCount);

			setProgress(loaded, 100);
		}
	}
	
	/**
	 *  @private
	 */
	private function initCompleteHandler(event:Event):void
	{
		var elapsedTime:int = getTimer() - _displayTime;
		
		if (_showingDisplay && elapsedTime < MINIMUM_DISPLAY_TIME)
		{
			var timer:Timer = new Timer(MINIMUM_DISPLAY_TIME - elapsedTime, 1);
			timer.addEventListener(TimerEvent.TIMER, timerHandler);
			timer.start();
		}
		else
		{
			timerHandler();
		}
	}

	/**
	 *  @private
	 */
	private function loader_completeHandler(event:Event):void
	{
		var target:DisplayObject = DisplayObject(LoaderInfo(event.target).loader);
		
		initBackgroundImage(target);
	}
	
	private function loader_ioErrorHandler(event:IOErrorEvent):void
	{
		// Swallow the error
	}
	
}

}

import flash.display.DisplayObject;
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFormat;
import flash.system.Capabilities;
import flash.text.TextFieldAutoSize;
import flash.display.DisplayObjectContainer;
import flash.display.Stage;


	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
/**
 * @private
 * 
 * Area to display error messages to help debug startup problems.
 * 
 */
class ErrorField extends Sprite
{
    private var parentContainer:DisplayObjectContainer;
    private const MIN_WIDTH_INCHES:int = 2;                    // min width of error message in inches
    private const MAX_WIDTH_INCHES:int = 6;                    // max width of error message in inches
    private const TEXT_MARGIN_PX:int = 10;
    
    
 	//----------------------------------
	//  labelFormat
	//----------------------------------

	/**
	 *  The TextFormat object of the TextField component of the label.
	 *  This is a read-only property which you must override
	 *  if you need to change it.
	 */
	protected function get labelFormat():TextFormat
	{
		var tf:TextFormat = new TextFormat();
		tf.color = 0x000000;
		tf.font = "Verdana";
		tf.size = 10;
		return tf;
	}

   
   /**
   * @private
   * 
   * @param - parent - parent of the error field.
   */ 
    public function ErrorField(parent:DisplayObjectContainer)
    {
    	super();
        this.parentContainer = parent;
    }
    
    
    /**
    * Create and show the error message.
    * 
    * @param errorText - text for error message.
    */
    public function show(errorText:String):void
    {
    	if (errorText == null || errorText.length == 0)
    		return;
    		
        var stage:Stage = parentContainer.stage;
        
        // create the text field for the message and 
        // add it to the parent.
        var textField:TextField = new TextField();
        
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.multiline = true;
        textField.wordWrap = true;
	    textField.background = true;
     	textField.defaultTextFormat = labelFormat;
        textField.text = errorText;

        textField.width = Math.max(MIN_WIDTH_INCHES * Capabilities.screenDPI, stage.stageWidth - (TEXT_MARGIN_PX * 2));
        textField.width = Math.min(MAX_WIDTH_INCHES * Capabilities.screenDPI, textField.width);
        textField.y = Math.max(0, stage.stageHeight - TEXT_MARGIN_PX - textField.height);
        
        // center field horizontally
        textField.x = (stage.stageWidth - textField.width) / 2;
        
        parentContainer.addChild(this);
        this.addChild(textField);
                
    }
}
