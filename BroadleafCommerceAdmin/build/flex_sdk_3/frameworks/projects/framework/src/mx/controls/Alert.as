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

import flash.display.Sprite;
import flash.events.Event;
import mx.containers.Panel;
import mx.controls.alertClasses.AlertForm;
import mx.core.Application;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.managers.ISystemManager;
import mx.managers.PopUpManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Name of the CSS style declaration that specifies 
 *  styles for the Alert buttons. 
 * 
 *  @default "alertButtonStyle"
 */
[Style(name="buttonStyleName", type="String", inherit="no")]

/**
 *  Name of the CSS style declaration that specifies
 *  styles for the Alert message text. 
 *
 *  <p>You only set this style by using a type selector, which sets the style 
 *  for all Alert controls in your application.  
 *  If you set it on a specific instance of the Alert control, it can cause the control to 
 *  size itself incorrectly.</p>
 * 
 *  @default undefined
 */
[Style(name="messageStyleName", type="String", inherit="no")]

/**
 *  Name of the CSS style declaration that specifies styles
 *  for the Alert title text. 
 *
 *  <p>You only set this style by using a type selector, which sets the style 
 *  for all Alert controls in your application.  
 *  If you set it on a specific instance of the Alert control, it can cause the control to 
 *  size itself incorrectly.</p>
 * 
 *  @default "windowStyles" 
 */
[Style(name="titleStyleName", type="String", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.AlertAccImpl")]

[RequiresDataBinding(true)]

[ResourceBundle("controls")]
    
/**
 *  The Alert control is a pop-up dialog box that can contain a message,
 *  a title, buttons (any combination of OK, Cancel, Yes, and No) and an icon. 
 *  The Alert control is modal, which means it will retain focus until the user closes it.
 *
 *  <p>Import the mx.controls.Alert class into your application, 
 *  and then call the static <code>show()</code> method in ActionScript to display
 *  an Alert control. You cannot create an Alert control in MXML.</p>
 *
 *  <p>The Alert control closes when you select a button in the control, 
 *  or press the Escape key.</p>
 *
 *  @includeExample examples/SimpleAlert.mxml
 *
 *  @see mx.managers.SystemManager
 *  @see mx.managers.PopUpManager
 */
public class Alert extends Panel
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Value that enables a Yes button on the Alert control when passed
     *  as the <code>flags</code> parameter of the <code>show()</code> method.
     *  You can use the | operator to combine this bitflag
     *  with the <code>OK</code>, <code>CANCEL</code>,
     *  <code>NO</code>, and <code>NONMODAL</code> flags.
     */
    public static const YES:uint = 0x0001;
    
    /**
     *  Value that enables a No button on the Alert control when passed
     *  as the <code>flags</code> parameter of the <code>show()</code> method.
     *  You can use the | operator to combine this bitflag
     *  with the <code>OK</code>, <code>CANCEL</code>,
     *  <code>YES</code>, and <code>NONMODAL</code> flags.
     */
    public static const NO:uint = 0x0002;
    
    /**
     *  Value that enables an OK button on the Alert control when passed
     *  as the <code>flags</code> parameter of the <code>show()</code> method.
     *  You can use the | operator to combine this bitflag
     *  with the <code>CANCEL</code>, <code>YES</code>,
     *  <code>NO</code>, and <code>NONMODAL</code> flags.
     */
    public static const OK:uint = 0x0004;
    
    /**
     *  Value that enables a Cancel button on the Alert control when passed
     *  as the <code>flags</code> parameter of the <code>show()</code> method.
     *  You can use the | operator to combine this bitflag
     *  with the <code>OK</code>, <code>YES</code>,
     *  <code>NO</code>, and <code>NONMODAL</code> flags.
     */
    public static const CANCEL:uint= 0x0008;

    /**
     *  Value that makes an Alert nonmodal when passed as the
     *  <code>flags</code> parameter of the <code>show()</code> method.
     *  You can use the | operator to combine this bitflag
     *  with the <code>OK</code>, <code>CANCEL</code>,
     *  <code>YES</code>, and <code>NO</code> flags.
     */
    public static const NONMODAL:uint = 0x8000;

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Placeholder for mixin by AlertAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Storage for the resourceManager getter.
	 *  This gets initialized on first access,
	 *  not at static initialization time, in order to ensure
	 *  that the Singleton registry has already been initialized.
	 */
	private static var _resourceManager:IResourceManager;
	
	/**
	 *  @private
     *  A reference to the object which manages
     *  all of the application's localized resources.
     *  This is a singleton instance which implements
     *  the IResourceManager interface.
	 */
	private static function get resourceManager():IResourceManager
	{
		if (!_resourceManager)
			_resourceManager = ResourceManager.getInstance();

		return _resourceManager;
	}
	
	/**
	 *  @private
	 */
	private static var initialized:Boolean = false;
	
    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  buttonHeight
    //----------------------------------

    [Inspectable(category="Size")]

    /**
     *  Height of each Alert button, in pixels.
     *  All buttons must be the same height.
     *
     *  @default 22
     */
    public static var buttonHeight:Number = 22;
    
    //----------------------------------
    //  buttonWidth
    //----------------------------------

    [Inspectable(category="Size")]

    /**
     *  Width of each Alert button, in pixels.
     *  All buttons must be the same width.
     *
     *  @default 60
     */
    public static var buttonWidth:Number = FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 ? 60 : 65;
    
    //----------------------------------
    //  cancelLabel
    //----------------------------------
    
    /**
	 *  @private
	 *  Storage for the cancelLabel property.
	 */
	private static var _cancelLabel:String;
	
    /**
	 *  @private
	 */
	private static var cancelLabelOverride:String;

    [Inspectable(category="General")]

    /**
     *  The label for the Cancel button.
     *
     *  <p>If you use a different label, you may need to adjust the 
     *  <code>buttonWidth</code> property to fully display it.</p>
     *
     *  The English resource bundle sets this property to "CANCEL". 
     */
	public static function get cancelLabel():String
	{
		initialize();
		
		return _cancelLabel;
	}

	/**
	 *  @private
	 */
	public static function set cancelLabel(value:String):void
	{
		cancelLabelOverride = value;

		_cancelLabel = value != null ?
					   value :
					   resourceManager.getString(
					       "controls", "cancelLabel");
	}
    
    //----------------------------------
    //  noLabel
    //----------------------------------
    
    /**
	 *  @private
	 *  Storage for the noLabel property.
	 */
	private static var _noLabel:String;
	
    /**
	 *  @private
	 */
	private static var noLabelOverride:String;

    [Inspectable(category="General")]

    /**
     *  The label for the No button.
     *
     *  <p>If you use a different label, you may need to adjust the 
     *  <code>buttonWidth</code> property to fully display it.</p>
     *
     *  The English resource bundle sets this property to "NO". 
     */
	public static function get noLabel():String
	{
		initialize();
		
		return _noLabel;
	}

	/**
	 *  @private
	 */
	public static function set noLabel(value:String):void
	{
		noLabelOverride = value;

		_noLabel = value != null ?
				   value :
				   resourceManager.getString(
				      "controls", "noLabel");
	}

    //----------------------------------
    //  okLabel
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the okLabel property.
	 */
	private static var _okLabel:String;
	
    /**
	 *  @private
	 */
	private static var okLabelOverride:String;

    [Inspectable(category="General")]

    /**
     *  The label for the OK button.
     *
     *  <p>If you use a different label, you may need to adjust the 
     *  <code>buttonWidth</code> property to fully display the label.</p>
     *
     *  The English resource bundle sets this property to "OK". 
     */
	public static function get okLabel():String
	{
		initialize();
		
		return _okLabel;
	}

	/**
	 *  @private
	 */
	public static function set okLabel(value:String):void
	{
		okLabelOverride = value;

		_okLabel = value != null ?
				   value :
				   resourceManager.getString(
				       "controls", "okLabel");
	}

    //----------------------------------
    //  yesLabel
    //----------------------------------
    
    /**
	 *  @private
	 *  Storage for the yesLabel property.
	 */
	private static var _yesLabel:String;
	
    /**
	 *  @private
	 */
	private static var yesLabelOverride:String;

    [Inspectable(category="General")]

    /**
     *  The label for the Yes button.
     *
     *  <p>If you use a different label, you may need to adjust the 
     *  <code>buttonWidth</code> property to fully display the label.</p>
     *
     *  The English resource bundle sets this property to "YES". 
     */
	public static function get yesLabel():String
	{
		initialize();
		
		return _yesLabel;
	}

	/**
	 *  @private
	 */
	public static function set yesLabel(value:String):void
	{
		yesLabelOverride = value;

		_yesLabel = value != null ?
					value :
					resourceManager.getString(
						"controls", "yesLabel");
	}

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Static method that pops up the Alert control. The Alert control 
     *  closes when you select a button in the control, or press the Escape key.
     * 
     *  @param text Text string that appears in the Alert control. 
     *  This text is centered in the alert dialog box.
     *
     *  @param title Text string that appears in the title bar. 
     *  This text is left justified.
     *
     *  @param flags Which buttons to place in the Alert control.
     *  Valid values are <code>Alert.OK</code>, <code>Alert.CANCEL</code>,
     *  <code>Alert.YES</code>, and <code>Alert.NO</code>.
     *  The default value is <code>Alert.OK</code>.
     *  Use the bitwise OR operator to display more than one button. 
     *  For example, passing <code>(Alert.YES | Alert.NO)</code>
     *  displays Yes and No buttons.
     *  Regardless of the order that you specify buttons,
     *  they always appear in the following order from left to right:
     *  OK, Yes, No, Cancel.
     *
     *  @param parent Object upon which the Alert control centers itself.
     *
     *  @param closeHandler Event handler that is called when any button
     *  on the Alert control is pressed.
     *  The event object passed to this handler is an instance of CloseEvent;
     *  the <code>detail</code> property of this object contains the value
     *  <code>Alert.OK</code>, <code>Alert.CANCEL</code>,
     *  <code>Alert.YES</code>, or <code>Alert.NO</code>.
     *
     *  @param iconClass Class of the icon that is placed to the left
     *  of the text in the Alert control.
     *
     *  @param defaultButtonFlag A bitflag that specifies the default button.
     *  You can specify one and only one of
     *  <code>Alert.OK</code>, <code>Alert.CANCEL</code>,
     *  <code>Alert.YES</code>, or <code>Alert.NO</code>.
     *  The default value is <code>Alert.OK</code>.
     *  Pressing the Enter key triggers the default button
     *  just as if you clicked it. Pressing Escape triggers the Cancel
     *  or No button just as if you selected it.
     *
     *  @return A reference to the Alert control. 
     *
     *  @see mx.events.CloseEvent
     */
    public static function show(text:String = "", title:String = "",
                                flags:uint = 0x4 /* Alert.OK */, 
                                parent:Sprite = null, 
                                closeHandler:Function = null, 
                                iconClass:Class = null, 
                                defaultButtonFlag:uint = 0x4 /* Alert.OK */):Alert
    {
        var modal:Boolean = (flags & Alert.NONMODAL) ? false : true;

        if (!parent)
            parent = Sprite(Application.application);   
        
        var alert:Alert = new Alert();

        if (flags & Alert.OK||
            flags & Alert.CANCEL ||
            flags & Alert.YES ||
            flags & Alert.NO)
        {
            alert.buttonFlags = flags;
        }
        
        if (defaultButtonFlag == Alert.OK ||
            defaultButtonFlag == Alert.CANCEL ||
            defaultButtonFlag == Alert.YES ||
            defaultButtonFlag == Alert.NO)
        {
            alert.defaultButtonFlag = defaultButtonFlag;
        }
        
        alert.text = text;
        alert.title = title;
        alert.iconClass = iconClass;
            
        if (closeHandler != null)
            alert.addEventListener(CloseEvent.CLOSE, closeHandler);

		// Setting a module factory allows the correct embedded font to be found.
        if (parent is UIComponent)
        	alert.moduleFactory = UIComponent(parent).moduleFactory;
        	
        PopUpManager.addPopUp(alert, parent, modal);

        alert.setActualSize(alert.getExplicitOrMeasuredWidth(),
                            alert.getExplicitOrMeasuredHeight());
        
        return alert;
    }

	/**
	 *  @private    
     */
	private static function initialize():void
	{
		if (!initialized)
		{
			// Register as a weak listener for "change" events
			// from ResourceManager.
			resourceManager.addEventListener(
				Event.CHANGE, static_resourceManager_changeHandler,
				false, 0, true);

			static_resourcesChanged();

			initialized = true;
		}
	}

    /**
     *  @private    
     */
    private static function static_resourcesChanged():void
    {
		cancelLabel = cancelLabelOverride;
        noLabel = noLabelOverride;
		okLabel = okLabelOverride;
        yesLabel = yesLabelOverride;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Class event handlers
    //
    //--------------------------------------------------------------------------

    /**
	 *  @private
	 */
	private static function static_resourceManager_changeHandler(
									event:Event):void
	{
		static_resourcesChanged();
	}

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Alert()
    {
        super();

        // Panel properties.
        title = "";
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */ 
    private var init:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  alertForm
    //----------------------------------
    
    /**
     *  @private
     *  The internal AlertForm object that contains the text, icon, and buttons
     *  of the Alert control.
     */
    mx_internal var alertForm:AlertForm;

    //----------------------------------
    //  buttonFlags
    //----------------------------------

    /**
     *  A bitmask that contains <code>Alert.OK</code>, <code>Alert.CANCEL</code>, 
     *  <code>Alert.YES</code>, and/or <code>Alert.NO</code> indicating
	 *  the buttons available in the Alert control.
     *
     *  @default Alert.OK
     */
    public var buttonFlags:uint = OK;
    
    //----------------------------------
    //  defaultButtonFlag
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  A bitflag that contains either <code>Alert.OK</code>, 
     *  <code>Alert.CANCEL</code>, <code>Alert.YES</code>, 
     *  or <code>Alert.NO</code> to specify the default button.
     *
     *  @default Alert.OK
     */
    public var defaultButtonFlag:uint = OK;
    
    //----------------------------------
    //  iconClass
    //----------------------------------

    [Inspectable(category="Other")]

    /**
     *  The class of the icon to display.
     *  You typically embed an asset, such as a JPEG or GIF file,
     *  and then use the variable associated with the embedded asset 
     *  to specify the value of this property.
     *
     *  @default null
     */
    public var iconClass:Class;
    
    //----------------------------------
    //  text
    //----------------------------------

    [Inspectable(category="General")]
    
    /**
     *  The text to display in this alert dialog box.
     *
     *  @default ""
     */
    public var text:String = "";
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (Alert.createAccessibilityImplementation != null)
            Alert.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        var messageStyleName:String = getStyle("messageStyleName");
        if (messageStyleName)
            styleName = messageStyleName;

        if (!alertForm)
        {   
            alertForm = new AlertForm();
            alertForm.styleName = this;
            addChild(alertForm);
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {   
        super.measure();
        
        var m:EdgeMetrics = viewMetrics;
        
        // The width is determined by the title or the AlertForm,
        // whichever is wider.
        measuredWidth = 
            Math.max(measuredWidth, alertForm.getExplicitOrMeasuredWidth() +
            m.left + m.right);
        
        measuredHeight = alertForm.getExplicitOrMeasuredHeight() +
                         m.top + m.bottom;
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        
        // Position the AlertForm inside the "client area" of the Panel
        var vm:EdgeMetrics = viewMetrics;
        alertForm.setActualSize(unscaledWidth - vm.left - vm.right -
                                getStyle("paddingLeft") -
                                getStyle("paddingRight"),
                                unscaledHeight - vm.top - vm.bottom -
                                getStyle("paddingTop") -
                                getStyle("paddingBottom"));

        // Choose an (x,y) position that centers me in my parent.       
        if (!init)
        {
            var x:Number;
            var y:Number;
            if (parent == systemManager)
            {
                x = (screen.width - measuredWidth) / 2;
                y = (screen.height - measuredHeight) / 2;
            }
            else
            {
                x = (parent.width - measuredWidth) / 2;
                y = (parent.height - measuredHeight) / 2;
            }

            // Set my position, because my parent won't do it for me.
            move(Math.round(x), Math.round(y));
            init = true;
        }
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);
        
        if (styleProp == "messageStyleName")
        {
            var messageStyleName:String =
                getStyle("messageStyleName");

            styleName = messageStyleName;
        }
        
        if (alertForm)
            alertForm.styleChanged(styleProp);
    }

    /**
     *  @private
     */
	override protected function resourcesChanged():void
	{
		super.resourcesChanged();

		static_resourcesChanged();
	}
}

}
