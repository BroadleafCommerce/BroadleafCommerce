////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Graphics;
import flash.display.InteractiveObject;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;

import mx.automation.IAutomationObject;
import mx.core.ApplicationGlobals;
import mx.core.FlexSprite;
import mx.core.IChildList;
import mx.core.IFlexDisplayObject;
import mx.core.IInvalidating;
import mx.core.IUIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.effects.Blur;
import mx.effects.IEffect;
import mx.effects.Fade;
import mx.events.EffectEvent;
import mx.events.FlexEvent;
import mx.events.FlexMouseEvent;
import mx.styles.IStyleClient;
import flash.display.Stage;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 *  The PopUpManager singleton class creates new top-level windows and
 *  places or removes those windows from the layer on top of all other
 *  visible windows.  See the SystemManager for a description of the layering.
 *  It is used for popup dialogs, menus, and dropdowns in the ComboBox control 
 *  and in similar components.
 * 
 *  <p>The PopUpManager also provides modality, so that windows below the popup
 *  cannot receive mouse events, and also provides an event if the user clicks
 *  the mouse outside the window so the developer can choose to dismiss
 *  the window or warn the user.</p>
 * 
 *  @see PopUpManagerChildList
 */
public class PopUpManagerImpl implements IPopUpManager
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var instance:IPopUpManager;
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public static function getInstance():IPopUpManager
    {
        if (!instance)
            instance = new PopUpManagerImpl();

        return instance;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function PopUpManagerImpl()
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
     *  The class used to create the shield that makes a window appear modal.
     */
    mx_internal var modalWindowClass:Class;

    /**
     *  @private
     *  An array of information about currently active popups
     */
    private var popupInfo:Array;


    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates a top-level window and places it above other windows in the
     *  z-order.
     *  It is good practice to call the <code>removePopUp()</code> method 
     *  to remove popups created by using the <code>createPopUp()</code> method.
     *
     *  If the class implements IFocusManagerContainer, the window will have its
     *  own FocusManager so that, if the user uses the TAB key to navigate between
     *  controls, only the controls in the window will be accessed.
     *
     *  <p><b>Example</b></p> 
     *
     *  <pre>pop = mx.managers.PopUpManager.createPopUp(pnl, TitleWindow, false); </pre>
     *  
     *  <p>Creates a popup window based on the TitleWindow class, using <code>pnl</code> as the MovieClip 
     *  for determining where to place the popup. It is defined to be a non-modal window 
     *  meaning that other windows can receive mouse events</p>
     *
     *  @param parent DisplayObject to be used for determining which SystemManager's layers
     *  to use and optionally the reference point for centering the new
     *  top level window.  It may not be the actual parent of the popup as all popups
     *  are parented by the SystemManager.
     * 
     *  @param className Class of object that is to be created for the popup.
     *  The class must implement IFlexDisplayObject.
     *
     *  @param modal If <code>true</code>, the window is modal which means that
     *  the user will not be able to interact with other popups until the window
     *  is removed.
     *
     *  @param childList The child list in which to add the popup.
     *  One of <code>PopUpManagerChildList.APPLICATION</code>, 
     *  <code>PopUpManagerChildList.POPUP</code>, 
     *  or <code>PopUpManagerChildList.PARENT</code> (default).
     *
     *  @return Reference to new top-level window.
     *
     *  @see PopUpManagerChildList
     */
    public function createPopUp(parent:DisplayObject,
                                className:Class,
                                modal:Boolean = false,
                                childList:String = null):IFlexDisplayObject
    {   
        const window:IUIComponent = new className();
        addPopUp(window, parent, modal, childList);
        return window;
    }
    
    /**
     *  Pops up a top-level window.
     *  It is good practice to call <code>removePopUp()</code> to remove popups
     *  created by using the <code>createPopUp()</code> method.
     *  If the class implements IFocusManagerContainer, the window will have its
     *  own FocusManager so that, if the user uses the TAB key to navigate between
     *  controls, only the controls in the window will be accessed.
     *
     *  <p><b>Example</b></p> 
     *
     *  <pre>var tw = new TitleWindow();
     *    tw.title = "My Title";
     *    mx.managers.PopUpManager.addPopUp(tw, pnl, false);</pre>
     *
     *  <p>Creates a popup window using the <code>tw</code> instance of the 
     *  TitleWindow class and <code>pnl</code> as the Sprite for determining
     *  where to place the popup.
     *  It is defined to be a non-modal window.</p>
     *  
     *  @param window The IFlexDisplayObject to be popped up.
     *
     *  @param parent DisplayObject to be used for determining which SystemManager's layers
     *  to use and optionally  the reference point for centering the new
     *  top level window.  It may not be the actual parent of the popup as all popups
     *  are parented by the SystemManager.
     *
     *  @param modal If <code>true</code>, the window is modal which means that
     *  the user will not be able to interact with other popups until the window
     *  is removed.
     *
     *  @param childList The child list in which to add the pop-up.
     *  One of <code>PopUpManagerChildList.APPLICATION</code>, 
     *  <code>PopUpManagerChildList.POPUP</code>, 
     *  or <code>PopUpManagerChildList.PARENT</code> (default).
     *
     *  @see PopUpManagerChildList
     */
    public function addPopUp(window:IFlexDisplayObject,
                             parent:DisplayObject,
                             modal:Boolean = false,
                             childList:String = null):void
    {
        // trace("POPUP: window is " + window);
        // All popups go on the local root.
        // trace("POPUP: root is " + parent.root);
        // trace("POPUP: initial parent is " + parent);
        
        const visibleFlag:Boolean = window.visible;
        
        var localRoot:DisplayObjectContainer = DisplayObjectContainer(parent.root);

        var sm:ISystemManager;
        var children:IChildList;
        var topMost:Boolean;

        // If the parent isn't rooted yet,
        // Or the root is the stage (which is the case in a second AIR window)
        // use the global system manager instance.
        if ((!localRoot || localRoot is Stage) && parent is IUIComponent)
            localRoot = DisplayObjectContainer(IUIComponent(parent).systemManager);
        if (localRoot is ISystemManager)
        {
            sm = ISystemManager(localRoot);
            if (!sm.isTopLevel())
                sm = sm.topLevelSystemManager;
        }
        else
        {
            //trace("error: popup root was not SystemManager");
            return; // and maybe a nice error message
        }
        
        if (window is IUIComponent)
            IUIComponent(window).isPopUp = true;
        
        if (!childList || childList == PopUpManagerChildList.PARENT)
            topMost = sm.popUpChildren.contains(parent);
        else
            topMost = (childList == PopUpManagerChildList.POPUP);
        
        children = topMost ? sm.popUpChildren : sm;
        children.addChild(DisplayObject(window));
        window.visible = false;
        
        if (!popupInfo)
            popupInfo = [];

        const o:PopUpData = new PopUpData();
        o.owner = DisplayObject(window);
        o.topMost = topMost;
        popupInfo.push(o);

        if (window is IFocusManagerContainer)
        {
            if (IFocusManagerContainer(window).focusManager)
                sm.addFocusManager(IFocusManagerContainer(window));
            else
                // Popups get their own focus loop
                IFocusManagerContainer(window).focusManager =
                    new FocusManager(IFocusManagerContainer(window), true);
        }

        // force into automation hierarchy
        if (window is IAutomationObject)
            IAutomationObject(window).showInAutomationHierarchy = true;

        if (window is ILayoutManagerClient )
            UIComponentGlobals.layoutManager.validateClient(ILayoutManagerClient (window), true);
        
        o.parent = parent;
        
        if (window is IUIComponent)
        {
            IUIComponent(window).setActualSize(
                IUIComponent(window).getExplicitOrMeasuredWidth(),
                IUIComponent(window).getExplicitOrMeasuredHeight());
        }

        if (modal)
        {
            // create a modal window shield which blocks input and sets up mouseDownOutside logic
            this.createModalWindow(parent, o, children, visibleFlag);
        }
        else
        {
            o._mouseDownOutsideHandler  = nonmodalMouseDownOutsideHandler;
            o._mouseWheelOutsideHandler = nonmodalMouseWheelOutsideHandler;
            
            sm.addEventListener(MouseEvent.MOUSE_DOWN,  o.mouseDownOutsideHandler);
            sm.addEventListener(MouseEvent.MOUSE_WHEEL, o.mouseWheelOutsideHandler, true);
            
            window.visible = visibleFlag;
        }
        
        // Listen for unload so we know to kill the window (and the modalWindow if modal)
        // this handles _all_ cleanup
        window.addEventListener(Event.REMOVED, popupRemovedHandler);
            
        if (window is IFocusManagerContainer && visibleFlag)
            sm.activate(IFocusManagerContainer(window));

        // trace("END POPUP: addPopUp" + parent);
    }

    /**
     *  Centers a popup window over whatever window was used in the call 
     *  to the <code>createPopUp()</code> or <code>addPopUp()</code> method.
     *
     *  <p>Note that the position of the popup window may not
     *  change immediately after this call since Flex may wait to measure and layout the
     *  popup window before centering it.</p>
     *
     *  @param The IFlexDisplayObject representing the popup.
     */
    public function centerPopUp(popUp:IFlexDisplayObject):void
    {
        if (popUp is IInvalidating)
            IInvalidating(popUp).validateNow();

        const o:PopUpData = findPopupInfoByOwner(popUp);
        if (o && o.parent)
        {
            var pt:Point = new Point(0, 0);
            pt = o.parent.localToGlobal(pt);
            pt = popUp.parent.globalToLocal(pt);
            popUp.move(Math.round((o.parent.width - popUp.width) / 2) + pt.x,
                       Math.round((o.parent.height - popUp.height) / 2) + pt.y);
        }
    }

    /**
     *  Removes a popup window popped up by 
     *  the <code>createPopUp()</code> or <code>addPopUp()</code> method.
     *  
     *  @param window The IFlexDisplayObject representing the popup window.
     */
    public function removePopUp(popUp:IFlexDisplayObject):void
    {
        // all we want to do here is verify that this popup is one of ours
        // and remove it from the display list; the REMOVED handler will do the rest
        // (this is so that we never leak memory, popups will self-manage even if
        //  removePopUp is not called).
        if (popUp && popUp.parent)
        {
            const o:PopUpData = findPopupInfoByOwner(popUp);
            if (o)
            {
                var sm:ISystemManager = popUp.parent as ISystemManager;
                if (!sm)
				{
					var iui:IUIComponent = popUp as IUIComponent;
					// cross-versioning error sometimes returns wrong parent
					if (iui)
						sm = iui.systemManager;
					else
						return;
				}

                if (o.topMost)
                    sm.popUpChildren.removeChild(DisplayObject(popUp));
                else
                    sm.removeChild(DisplayObject(popUp));
            }
        }
    }
    
    /**
     *  Makes sure a popup window is higher than other objects in its child list
     *  The SystemManager does this automatically if the popup is a top level window
     *  and is moused on, 
     *  but otherwise you have to take care of this yourself.
     *
     *  @param The IFlexDisplayObject representing the popup.
     */
    public function bringToFront(popUp:IFlexDisplayObject):void
    {
        if (popUp && popUp.parent)
        {
            const o:PopUpData = findPopupInfoByOwner(popUp);
            if (o)
            {
                const sm:ISystemManager = ISystemManager(popUp.parent);
                if (o.topMost)
                    sm.popUpChildren.setChildIndex(DisplayObject(popUp), sm.popUpChildren.numChildren - 1);
                else
                    sm.setChildIndex(DisplayObject(popUp), sm.numChildren - 1);
            }
        }
    }
    
    /**
     *  @private
     *  Create the modal window.
     */
    private function createModalWindow(parentReference:DisplayObject,
                                       o:PopUpData,
                                       childrenList:IChildList,
                                       visibleFlag:Boolean):void
    {
        const popup:IFlexDisplayObject = IFlexDisplayObject(o.owner);

        const popupStyleClient:IStyleClient = popup as IStyleClient;
        var duration:Number = 0;
        
        // Create a modalWindow the size of the stage
        // that eats all mouse clicks.
        var modalWindow:Sprite;
        if (modalWindowClass)
        {
            modalWindow = new modalWindowClass();
        }
        else
        {
            modalWindow = new FlexSprite();
            modalWindow.name = "modalWindow";
        }
    
        const sm:ISystemManager = IUIComponent(parentReference).systemManager;
        sm.numModalWindows++;
    
        // Add it to the collection just below the popup
        childrenList.addChildAt(modalWindow,
            childrenList.getChildIndex(DisplayObject(popup)));

        // force into the automation hierarchy
        if (popup is IAutomationObject)
            IAutomationObject(popup).showInAutomationHierarchy = true;
        
        // set alpha of the popup and get it out of the focus loop
        if (popupStyleClient)
            modalWindow.alpha = popupStyleClient.getStyle("modalTransparency");
		else
			modalWindow.alpha = 0;
			
        modalWindow.tabEnabled = false;
        
        const s:Rectangle = sm.screen;
        const g:Graphics = modalWindow.graphics;
        
        var c:Number = 0xFFFFFF;
        if (popupStyleClient)
            c = popupStyleClient.getStyle("modalTransparencyColor");
            
        g.clear();
        g.beginFill(c, 100);
        g.drawRect(s.x, s.y, s.width, s.height);
        g.endFill();

        o.modalWindow = modalWindow;
        
        // a modal mousedownoutside handler just dispatches the event
        o._mouseDownOutsideHandler  = dispatchMouseDownOutsideEvent;
        o._mouseWheelOutsideHandler = dispatchMouseWheelOutsideEvent;
        
        // the following handlers all get removed in REMOVED on the popup
        
        // Because it listens to the modal window
        modalWindow.addEventListener(MouseEvent.MOUSE_DOWN,  o.mouseDownOutsideHandler);
        modalWindow.addEventListener(MouseEvent.MOUSE_WHEEL, o.mouseWheelOutsideHandler, true);
        
        // Set the resize handler so the modal can stay the size of the screen
        sm.addEventListener(Event.RESIZE, o.resizeHandler);

        // Listen for show so we know to show the modal window
        popup.addEventListener(FlexEvent.SHOW, popupShowHandler);
        
        // Listen for hide so we know to hide the modal window
        popup.addEventListener(FlexEvent.HIDE, popupHideHandler);
        
        if (visibleFlag)
            showModalWindow(o);
        else
            popup.visible = visibleFlag;
        
    }

    /**
     *  @private
     *  Set by PopUpManager on modal windows so they show when the parent shows
     */
    private function popupShowHandler(event:FlexEvent):void
    {
        const o:PopUpData = findPopupInfoByOwner(event.target);
        if (o)
            showModalWindow(o);
    }

    /**
     *  @private
     *  Set by PopUpManager on modal windows so they hide when the parent hide
     */
    private function popupHideHandler(event:FlexEvent):void
    {
        const o:PopUpData = findPopupInfoByOwner(event.target);
        if (o)
            hideModalWindow(o);
    }

    /**
     *  @private
     */
    private function endEffects(o:PopUpData):void
    {
        if (o.fade)
        {
            o.fade.end();
            o.fade = null;
        }
        
        if (o.blur)
        {
            o.blur.end();
            o.blur = null;
        }
    }
    
    /**
     *  @private
     *  Show the modal transparency blocker, playing effects if needed.
     */
    private function showModalWindow(o:PopUpData):void
    {
        const popUpStyleClient:IStyleClient = o.owner as IStyleClient;
        var duration:Number = 0;
        
        if (popUpStyleClient)
            duration = popUpStyleClient.getStyle("modalTransparencyDuration");
        
        // End any effects that are currently playing for this popup.
        endEffects(o);
        
        if (duration)
        {
            // Fade effect on the modal transparency blocker
            const fade:Fade = new Fade(o.modalWindow);

            fade.alphaFrom = 0;
            fade.alphaTo = popUpStyleClient.getStyle("modalTransparency");
            fade.duration = duration;
            fade.addEventListener(EffectEvent.EFFECT_END, fadeInEffectEndHandler);

            o.modalWindow.alpha = 0;
            o.modalWindow.visible = true;
            o.fade = fade;
            IUIComponent(o.owner).setVisible(false, true);
            
            fade.play();
            
            // Blur effect on the application
            const blurAmount:Number = popUpStyleClient.getStyle("modalTransparencyBlur");
            
            if (blurAmount)
            {
                // Ensure we blur the appropriate top level document.
                var parentApp:Object = ("parentApplication" in o.owner) ? 
                    (Object(o.owner).parentApplication) : null;
                     
                const blur:Blur = new Blur(parentApp ? parentApp : ApplicationGlobals.application);
                blur.blurXFrom = blur.blurYFrom = 0;
                blur.blurXTo = blur.blurYTo = blurAmount;
                blur.duration = duration;
                blur.addEventListener(EffectEvent.EFFECT_END, effectEndHandler);
                o.blur = blur;
                
                blur.play();
            }
        }
        else
        {
            IUIComponent(o.owner).setVisible(true, true);
            o.modalWindow.visible = true;
        }
    }
    
    /**
     *  @private
     *  Hide the modal transparency blocker, playing effects if needed.
     */
    private function hideModalWindow(o:PopUpData, destroy:Boolean = false):void
    {
        const popUpStyleClient:IStyleClient = o.owner as IStyleClient;

        var duration:Number = 0;
        if (popUpStyleClient)
            duration = popUpStyleClient.getStyle("modalTransparencyDuration");
        
        // end any effects that are current playing for this popup
        endEffects(o);
        
        if (duration)
        {
            // Fade effect on the modal transparency blocker
            const fade:Fade = new Fade(o.modalWindow);

            fade.alphaFrom = o.modalWindow.alpha;
            fade.alphaTo = 0;
            fade.duration = duration;
            fade.addEventListener(EffectEvent.EFFECT_END, 
                destroy ? fadeOutDestroyEffectEndHandler : fadeOutCloseEffectEndHandler);

            o.modalWindow.visible = true;
            o.fade = fade;
            fade.play();
            
            // Blur effect on the application
            const blurAmount:Number = popUpStyleClient.getStyle("modalTransparencyBlur");
            
            if (blurAmount)
            {
                // Ensure we blur the appropriate top level document.
                var parentApp:Object = ("parentApplication" in o.owner) ? 
                    (Object(o.owner).parentApplication) : null;
                    
                const blur:Blur = new Blur(parentApp ? parentApp : ApplicationGlobals.application);
                blur.blurXFrom = blur.blurYFrom = blurAmount;
                blur.blurXTo = blur.blurYTo = 0;
                blur.duration = duration;
                blur.addEventListener(EffectEvent.EFFECT_END, effectEndHandler);
                o.blur = blur;
                
                blur.play();
            }
        }
        else
        {
            o.modalWindow.visible = false;
        }
    }
    
    /**
     *  @private
     *  Returns the PopUpData (or null) for a given popupInfo.owner
     */
    private function findPopupInfoByOwner(owner:Object):PopUpData
    {
        const n:int = popupInfo.length;
        for (var i:int = 0; i < n; i++)
        {
            var o:PopUpData = popupInfo[i];
            if (o.owner == owner)
                return o;
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Set by PopUpManager on modal windows to monitor when the parent window gets killed.
     *  PopUps self-manage their memory -- when they are removed using removePopUp OR
     *  manually removed with removeChild, they will clean themselves up when they leave the
     *  display list (including all references to PopUpManager).
     */
    private function popupRemovedHandler(event:Event):void
    {
        const n:int = popupInfo.length;
        for (var i:int = 0; i < n; i++)
        {
            var o:PopUpData               = popupInfo[i],
                popUp:DisplayObject       = o.owner;
                  
            if (popUp == event.target)
            {
                var popUpParent:DisplayObject = o.parent,
                    modalWindow:DisplayObject = o.modalWindow,
                    sm:ISystemManager         = (popUpParent is ISystemManager)
                                                    ? ISystemManager(popUpParent)
                                                    : IUIComponent(popUpParent).systemManager;
                
				if (!sm.isTopLevel())
					sm = sm.topLevelSystemManager;

                if (popUp is IUIComponent)
                    IUIComponent(popUp).isPopUp = false;
                
                if (popUp is IFocusManagerContainer)
                    sm.removeFocusManager(IFocusManagerContainer(popUp));
                
                popUp.removeEventListener(Event.REMOVED,  popupRemovedHandler);
                
                // modal
                if (modalWindow)
                {
                    // clean up all handlers
                    modalWindow.removeEventListener(MouseEvent.MOUSE_DOWN,  o.mouseDownOutsideHandler);
                    modalWindow.removeEventListener(MouseEvent.MOUSE_WHEEL, o.mouseWheelOutsideHandler, true);
                    
                    sm.removeEventListener(Event.RESIZE, o.resizeHandler);
                    
                    popUp.removeEventListener(FlexEvent.SHOW, popupShowHandler);
                    popUp.removeEventListener(FlexEvent.HIDE, popupHideHandler);
                    
                    hideModalWindow(o, true);
                    sm.numModalWindows--;
                }
                
                // non-modal
                else
                {
                    sm.removeEventListener(MouseEvent.MOUSE_DOWN,  o.mouseDownOutsideHandler);
                    sm.removeEventListener(MouseEvent.MOUSE_WHEEL, o.mouseWheelOutsideHandler, true);
                }
                
                popupInfo.splice(i, 1);
                break;
            }
        }
    }
    
    /**
     *  @private
     *  Show the modal window after the fade effect finishes
     */
    private function fadeInEffectEndHandler(event:EffectEvent):void
    {
        effectEndHandler(event);
        
        const n:int = popupInfo.length;
        for (var i:int = 0; i < n; i++)
        {
            var o:PopUpData = popupInfo[i];
            if (o.modalWindow == event.effectInstance.target)
            {
                IUIComponent(o.owner).setVisible(true, true);
                break;
            }
        }
    }
    
    /**
     *  @private
     *  Remove the modal window after the fade effect finishes
     */
    private function fadeOutDestroyEffectEndHandler(event:EffectEvent):void
    {
        effectEndHandler(event);
        const obj:DisplayObject = DisplayObject(event.effectInstance.target);
        if (obj.parent is ISystemManager)
        {
            const sm:ISystemManager = ISystemManager(obj.parent)
            if (sm.popUpChildren.contains(obj))
                sm.popUpChildren.removeChild(obj);
            else
                sm.removeChild(obj);
        }
        else
		{
			if (obj.parent)	// Mustella can already take you off stage
				obj.parent.removeChild(obj);
		}
    }
    
    /**
     *  @private
     *  Remove the modal window after the fade effect finishes
     */
    private function fadeOutCloseEffectEndHandler(event:EffectEvent):void
    {
        effectEndHandler(event);
        DisplayObject(event.effectInstance.target).visible = false;
    }
    
    /**
     *  @private
     */
    private function effectEndHandler(event:EffectEvent):void
    {
        const n:int = popupInfo.length;
        for (var i:int = 0; i < n; i++)
        {
            var o:PopUpData = popupInfo[i];
            var e:IEffect = event.effectInstance.effect;
            
            if (e == o.fade)
                o.fade = null;
            else if (e == o.blur)
                o.blur = null;
        }
    }
    
    /**
     *  @private
     *  If not modal, use this kind of mouseDownOutside logic
     */
    private static function nonmodalMouseDownOutsideHandler(owner:DisplayObject, evt:MouseEvent):void
    {
        // shapeFlag is false here for performance reasons
        if (owner.hitTestPoint(evt.stageX, evt.stageY, true))
		{
		}
        else
		{
			if (owner is IUIComponent)
				if (IUIComponent(owner).owns(DisplayObject(evt.target)))
					return;

            dispatchMouseDownOutsideEvent(owner, evt);
		}
    }
    
    /**
     *  @private
     *  If not modal, use this kind of mouseWheelOutside logic
     */
    private static function nonmodalMouseWheelOutsideHandler(owner:DisplayObject, evt:MouseEvent):void
    {
        // shapeFlag is false here for performance reasons
        if (owner.hitTestPoint(evt.stageX, evt.stageY, true))
        {
		}
        else
		{
			if (owner is IUIComponent)
				if (IUIComponent(owner).owns(DisplayObject(evt.target)))
					return;

            dispatchMouseWheelOutsideEvent(owner, evt);
		}
    }
    
    /**
     *  @private
     *  This mouseWheelOutside handler just dispatches the event.
     */
    private static function dispatchMouseWheelOutsideEvent(owner:DisplayObject, evt:MouseEvent):void
    {
        const event:MouseEvent = new FlexMouseEvent(FlexMouseEvent.MOUSE_WHEEL_OUTSIDE);
        const pt:Point = owner.globalToLocal(new Point(evt.stageX, evt.stageY));
        event.localX = pt.x;
        event.localY = pt.y;
        event.buttonDown = evt.buttonDown;
        event.shiftKey = evt.shiftKey;
        event.altKey = evt.altKey;
        event.ctrlKey = evt.ctrlKey;
        event.delta = evt.delta;
        event.relatedObject = InteractiveObject(evt.target);
        owner.dispatchEvent(event);
    }
    
    /**
     *  @private
     *  This mouseDownOutside handler just dispatches the event.
     */
    private static function dispatchMouseDownOutsideEvent(owner:DisplayObject, evt:MouseEvent):void
    {
        const event:MouseEvent = new FlexMouseEvent(FlexMouseEvent.MOUSE_DOWN_OUTSIDE);
        const pt:Point = owner.globalToLocal(new Point(evt.stageX, evt.stageY));
        event.localX = pt.x;
        event.localY = pt.y;
        event.buttonDown = evt.buttonDown;
        event.shiftKey = evt.shiftKey;
        event.altKey = evt.altKey;
        event.ctrlKey = evt.ctrlKey;
        event.delta = evt.delta;
        event.relatedObject = InteractiveObject(evt.target);
        owner.dispatchEvent(event);
    }
    
}

}

import flash.display.DisplayObject;
import flash.geom.Rectangle;
import flash.events.Event;
import flash.events.MouseEvent;
import mx.core.IUIComponent;
import mx.effects.Effect;
import mx.managers.ISystemManager;
import flash.display.Stage;

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: PopUpData
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class PopUpData
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function PopUpData()
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
    public var owner:DisplayObject;

    /**
     *  @private
     */
    public var parent:DisplayObject;

    /**
     *  @private
     */
    public var topMost:Boolean;

    /**
     *  @private
     */
    public var modalWindow:DisplayObject;

    /**
     *  @private
     */
    public var _mouseDownOutsideHandler:Function;

    /**
     *  @private
     */
    public var _mouseWheelOutsideHandler:Function;

    /**
     *  @private
     */
    public var fade:Effect;

    /**
     *  @private
     */
    public var blur:Effect;
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function mouseDownOutsideHandler(event:MouseEvent):void
    {
        _mouseDownOutsideHandler(owner, event);
    }

    /**
     *  @private
     */
    public function mouseWheelOutsideHandler(event:MouseEvent):void
    {
        _mouseWheelOutsideHandler(owner, event);
    }

    /**
     *  @private
     *  Set by PopUpManager on modal windows to make sure they cover the whole screen
     */
    public function resizeHandler(event:Event):void
    {
        var s:Rectangle = ISystemManager(event.target).screen;  
        
        if (modalWindow && owner.stage == DisplayObject(event.target).stage)
        {
            modalWindow.width = s.width;
            modalWindow.height = s.height;
            modalWindow.x = s.x;
            modalWindow.y = s.y;
        }
    }
}
