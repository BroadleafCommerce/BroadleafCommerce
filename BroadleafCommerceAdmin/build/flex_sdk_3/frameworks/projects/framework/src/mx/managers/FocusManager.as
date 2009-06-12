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

package mx.managers
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.InteractiveObject;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.system.Capabilities;
import flash.text.TextField;
import flash.ui.Keyboard;
import mx.core.Application;
import mx.core.FlexSprite;
import mx.core.IButton;
import mx.core.IChildList;
import mx.core.IRawChildrenContainer;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.events.FlexEvent;

use namespace mx_internal;

/**
 *  The FocusManager class manages the focus on components in response to mouse
 *  activity or keyboard activity (Tab key).  There can be several FocusManager
 *  instances in an application.  Each FocusManager instance 
 *  is responsible for a set of components that comprise a "tab loop".  If you
 *  hit Tab enough times, focus traverses through a set of components and
 *  eventually get back to the first component that had focus.  That is a "tab loop"
 *  and a FocusManager instance manages that loop.  If there are popup windows
 *  with their own set of components in a "tab loop" those popup windows will have
 *  their own FocusManager instances.  The main application always has a
 *  FocusManager instance.
 *
 *  <p>The FocusManager manages focus from the "component level".
 *  In Flex, a UITextField in a component is the only way to allow keyboard entry
 *  of text. To the Flash Player or AIR, that UITextField has focus. However, from the 
 *  FocusManager's perspective the component that parents the UITextField has focus.
 *  Thus there is a distinction between component-level focus and player-level focus.
 *  Application developers generally only have to deal with component-level focus while
 *  component developers must understand player-level focus.</p>
 *
 *  <p>All components that can be managed by the FocusManager must implement
 *  mx.managers.IFocusManagerComponent, whereas objects managed by player-level focus do not.</p>  
 *
 *  <p>The FocusManager also managers the concept of a defaultButton, which is
 *  the Button on a form that dispatches a click event when the Enter key is pressed
 *  depending on where focus is at that time.</p>
 */
public class FocusManager implements IFocusManager
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  <p>A FocusManager manages the focus within the children of an IFocusManagerContainer.
     *  It installs itself in the IFocusManagerContainer during execution
     *  of the constructor.</p>
     *
     *  @param container An IFocusManagerContainer that hosts the FocusManager.
     *
     *  @param popup If <code>true</code>, indicates that the container
     *  is a popup component and not the main application.
     */
    public function FocusManager(container:IFocusManagerContainer, popup:Boolean = false)
    {
        super();

        browserMode = Capabilities.playerType == "ActiveX" && !popup;

        container.focusManager = this; // this property name is reserved in the parent

        // trace("FocusManager constructor " + container + ".focusManager");
        
        _form = container;
        
        focusableObjects = [];

        focusPane = new FlexSprite();
        focusPane.name = "focusPane";

        addFocusables(DisplayObject(container));
        
        container.addEventListener(Event.ADDED, addedHandler);
        container.addEventListener(Event.REMOVED, removedHandler);
        container.addEventListener(FlexEvent.SHOW, showHandler);
        container.addEventListener(FlexEvent.HIDE, hideHandler);
        
        //special case application and window
        if (container.systemManager is SystemManager)
        {
            // special case application.  It shouldn't need to be made
            // active and because we defer appCreationComplete, this 
            // would steal focus back from any popups created during
            // instantiation
            if (container != SystemManager(container.systemManager).application)
                container.addEventListener(FlexEvent.CREATION_COMPLETE,
                                       creationCompleteHandler);
        }
        
        // Make sure the SystemManager is running so it can tell us about
        // mouse clicks and stage size changes.
        container.systemManager.addFocusManager(container);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    private var LARGE_TAB_INDEX:int = 99999;

    private var calculateCandidates:Boolean = true;

    /**
     *  @private
     *  the object that last had focus
     */
    private var lastFocus:IFocusManagerComponent;

    /**
     *  @private
     *  We track whether we've been last activated or saw a TAB
     *  This is used in browser tab management
     */
    private var lastAction:String;

    /**
     *  @private
     *  Tab management changes based on whether were in a browser or not
     *  This value is also affected by whether you are a modal dialog or not
     */
    public var browserMode:Boolean;

    /**
     *  @private
     *  Tab management changes based on whether were in a browser or not
     *  If non-null, this is the object that will
     *  lose focus to the browser
     */
    private var browserFocusComponent:InteractiveObject;

    /**
     *  @private
     *  Total set of all objects that can receive focus
     *  but might be disabled or invisible.
     */
    private var focusableObjects:Array;
    
    /**
     *  @private
     *  Filtered set of objects that can receive focus right now.
     */
    private var focusableCandidates:Array;

    /**
     *  @private
     */
    private var activated:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  showFocusIndicator
    //----------------------------------

    /**
     *  @private
     *  Storage for the showFocusIndicator property.
     */
    private var _showFocusIndicator:Boolean = false;
    
    /**
     *  @inheritDoc
     */
    public function get showFocusIndicator():Boolean
    {
        return _showFocusIndicator;
    }
    
    /**
     *  @private
     */
    public function set showFocusIndicator(value:Boolean):void
    {
        _showFocusIndicator = value;
    }

    //----------------------------------
    //  defaultButton
    //----------------------------------

    /**
     *  @private
     *  The current default button.
     */
    private var defButton:IButton;

    /**
     *  @private
     */
    private var _defaultButton:IButton;

    /**
     *  @inheritDoc
     */
    public function get defaultButton():IButton
    {
		return _defaultButton;
    }

    /**
     *  @private
     *  We don't type the value as Button for dependency reasons
     */
    public function set defaultButton(value:IButton):void
    {
		var button:IButton = value ? IButton(value) : null;

        if (button != _defaultButton)
        {
            if (_defaultButton)
                _defaultButton.emphasized = false;
            
            if (defButton)  
                defButton.emphasized = false;
            
            _defaultButton = button;
            defButton = button;
            
            if (button)
                button.emphasized = true;
        }
    }

    //----------------------------------
    //  defaultButtonEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the defaultButtonEnabled property.
     */
    private var _defaultButtonEnabled:Boolean = true;

    /**
     *  @inheritDoc
     */
    public function get defaultButtonEnabled():Boolean
    {
        return _defaultButtonEnabled;
    }
    
    /**
     *  @private
     */
    public function set defaultButtonEnabled(value:Boolean):void
    {
        _defaultButtonEnabled = value;
    }
    
    //----------------------------------
    //  focusPane
    //----------------------------------

    /**
     *  @private
     *  Storage for the focusPane property.
     */
    private var _focusPane:Sprite;

    /**
     *  @inheritDoc
     */
    public function get focusPane():Sprite
    {
        return _focusPane;
    }

    /**
     *  @private
     */
    public function set focusPane(value:Sprite):void
    {
        _focusPane = value;
    }

    //----------------------------------
    //  form
    //----------------------------------

    /**
     *  @private
     *  Storage for the form property.
     */
    private var _form:IFocusManagerContainer;
    
    /**
     *  @private
     *  The form is the property where we store the IFocusManagerContainer
     *  that hosts this FocusManager.
     */
    mx_internal function get form():IFocusManagerContainer
    {
        return _form;
    }
    
    /**
     *  @private
     */
    mx_internal function set form (value:IFocusManagerContainer):void
    {
        _form = value;
    }

    //----------------------------------
    //  nextTabIndex
    //----------------------------------

    /**
     *  @inheritDoc
     */
    public function get nextTabIndex():int
    {
        return getMaxTabIndex() + 1;
    }

    /**
     *  Gets the highest tab index currently used in this Focus Manager's form or subform.
     *
     *  @return Highest tab index currently used.
     */
    private function getMaxTabIndex():int
    {
        var z:Number = 0;

        var n:int = focusableObjects.length;
        for (var i:int = 0; i < n; i++)
        {
            var t:Number = focusableObjects[i].tabIndex;
            if (!isNaN(t))
                z = Math.max(z, t);
        }
        
        return z;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    public function getFocus():IFocusManagerComponent
    {
        var o:InteractiveObject = form.systemManager.stage.focus;
        return findFocusManagerComponent(o);
    }

    /**
     *  @inheritDoc
     */
    public function setFocus(o:IFocusManagerComponent):void
    {
        // trace("FM " + this + " setting focus to " + o);
        
        o.setFocus();
        
        // trace("FM set focus");
    }

    /**
     *  @private
     */
    private function focusInHandler(event:FocusEvent):void
    {
        var target:InteractiveObject = InteractiveObject(event.target);
        // trace("FM " + this + " focusInHandler " + target);
        if (isParent(DisplayObjectContainer(form), target))
        {
            // trace("FM " + this + " setting last focus " + target);
            lastFocus = findFocusManagerComponent(InteractiveObject(target));

			// handle default button here
			// we can't check for Button because of cross-versioning so
			// for now we just check for an emphasized property
			if (lastFocus is IButton)
			{
				var x:IButton = lastFocus as IButton;
				// if we have marked some other button as a default button
				if (defButton)
				{
					// change it to be this button
					defButton.emphasized = false;
					defButton = x;
					x.emphasized = true;
				}
			}
			else
			{
				// restore the default button to be the original one
				if (defButton && defButton != _defaultButton)
				{
					defButton.emphasized = false;
					defButton = _defaultButton;
					_defaultButton.emphasized = true;
				}
			}
		}
    }

    /**
     *  @private  Useful for debugging
     */
    private function focusOutHandler(event:FocusEvent):void
    {
        var target:InteractiveObject = InteractiveObject(event.target);
        // trace("FM " + this + " focusOutHandler " + target);
    }

    /**
     *  @private
     *  restore focus to whoever had it last
     */
    private function activateHandler(event:Event):void
    {
        var target:InteractiveObject = InteractiveObject(event.target);
        // trace("FM " + this + " activateHandler ", lastFocus);
        if (lastFocus && !browserMode)
            lastFocus.setFocus();
        lastAction = "ACTIVATE";

    }

    /**
     *  @private  Useful for debugging
     */
    private function deactivateHandler(event:Event):void
    {
        var target:InteractiveObject = InteractiveObject(event.target);
        // trace("FM " + this + " deactivateHandler ", lastFocus);
    }

    /**
     *  @inheritDoc
     */
    public function showFocus():void
    {
        if (!showFocusIndicator)
        {
            showFocusIndicator = true;
            if (lastFocus)
                lastFocus.drawFocus(true);
        }
    }

    /**
     *  @inheritDoc
     */
    public function hideFocus():void
    {
        // trace("FOcusManger " + this + " Hide Focus");
        if (showFocusIndicator)
        {
            showFocusIndicator = false;
            if (lastFocus)
                lastFocus.drawFocus(false);
        }
        // trace("END FOcusManger Hide Focus");
    }
    
    /**
     *  The SystemManager activates and deactivates a FocusManager
     *  if more than one IFocusManagerContainer is visible at the same time.
     *  If the mouse is clicked in an IFocusManagerContainer with a deactivated
     *  FocusManager, the SystemManager will call 
     *  the <code>activate()</code> method on that FocusManager.
     *  The FocusManager that was activated will have its <code>deactivate()</code> method
     *  called prior to the activation of another FocusManager.
     *
     *  <p>The FocusManager adds event handlers that allow it to monitor
     *  focus related keyboard and mouse activity.</p>
     */
    public function activate():void
    {
        // we can get a double activation if we're popping up and becoming visible
        // like the second time a menu appears
        if (activated)
            return;

        // trace("FocusManager activating " + this);

        // listen for focus changes, use weak references for the stage
        form.systemManager.stage.addEventListener(FocusEvent.MOUSE_FOCUS_CHANGE, mouseFocusChangeHandler, false, 0, true);
        form.systemManager.stage.addEventListener(FocusEvent.KEY_FOCUS_CHANGE, keyFocusChangeHandler, false, 0, true);
        form.addEventListener(FocusEvent.FOCUS_IN, focusInHandler, true);
        form.addEventListener(FocusEvent.FOCUS_OUT, focusOutHandler, true);
        form.systemManager.stage.addEventListener(Event.ACTIVATE, activateHandler, false, 0, true);
        form.systemManager.stage.addEventListener(Event.DEACTIVATE, deactivateHandler, false, 0, true);
        form.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler); 
        // listen for default button in Capture phase. Some components like TextInput 
        // and Accordion stop the Enter key from propagating in the Bubble phase. 
        form.addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler, true);

        activated = true;

        // Restore focus to the last control that had it if there was one.
        if (lastFocus)
            setFocus(lastFocus);
    }

    /**
     *  The SystemManager activates and deactivates a FocusManager
     *  if more than one IFocusManagerContainer is visible at the same time.
     *  If the mouse is clicked in an IFocusManagerContainer with a deactivated
     *  FocusManager, the SystemManager will call 
     *  the <code>activate()</code> method on that FocusManager.
     *  The FocusManager that was activated will have its <code>deactivate()</code> method
     *  called prior to the activation of another FocusManager.
     *
     *  <p>The FocusManager removes event handlers that allow it to monitor
     *  focus related keyboard and mouse activity.</p>
     */
    public function deactivate():void
    {
        // trace("FocusManager deactivating " + this);
        // listen for focus changes
        form.systemManager.stage.removeEventListener(FocusEvent.MOUSE_FOCUS_CHANGE, mouseFocusChangeHandler);
        form.systemManager.stage.removeEventListener(FocusEvent.KEY_FOCUS_CHANGE, keyFocusChangeHandler);
        form.removeEventListener(FocusEvent.FOCUS_IN, focusInHandler, true);
        form.removeEventListener(FocusEvent.FOCUS_OUT, focusOutHandler, true);
        form.systemManager.stage.removeEventListener(Event.ACTIVATE, activateHandler);
        form.systemManager.stage.removeEventListener(Event.DEACTIVATE, deactivateHandler);
        form.removeEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler); 
        // stop listening for default button in Capture phase
        form.removeEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler, true);

        activated = false;

    }

    /**
     *  @inheritDoc
     */
    public function findFocusManagerComponent(
                            o:InteractiveObject):IFocusManagerComponent
    {
        while (o)
        {
            if (o is IFocusManagerComponent && IFocusManagerComponent(o).focusEnabled)
                return IFocusManagerComponent(o);
            
            o = o.parent;
        }

        // tab was set somewhere else
        return null;
    }

    /**
     *  @private
     *  Returns true if p is a parent of o.
     */
    private function isParent(p:DisplayObjectContainer, o:DisplayObject):Boolean
    {
        if (p is IRawChildrenContainer)
            return IRawChildrenContainer(p).rawChildren.contains(o);
        
        return p.contains(o);
    }
    
    private function isEnabledAndVisible(o:DisplayObject):Boolean
    {
        var formParent:DisplayObjectContainer = DisplayObject(form).parent;
        
        while (o != formParent)
        {
            if (o is IUIComponent)
                if (!IUIComponent(o).enabled)
                    return false;
            if (!o.visible) 
                return false;
            o = o.parent;
        }
        return true;
    }

    /**
     *  @private
     */
    private function sortByTabIndex(a:IFocusManagerComponent, b:IFocusManagerComponent):int
    {
        var aa:int = a.tabIndex;
        var bb:int = b.tabIndex;

        if (aa == -1)
            aa = int.MAX_VALUE;
        if (bb == -1)
            bb = int.MAX_VALUE;

        return (aa > bb ? 1 :
                aa < bb ? -1 : sortByDepth(a, b));
    }

    /**
     *  @private
     */
    private function sortFocusableObjectsTabIndex():void
    {
        // trace("FocusableObjectsTabIndex");
        
        focusableCandidates = [];
        
        var n:int = focusableObjects.length;
        for (var i:int = 0; i < n; i++)
        {
            var c:IFocusManagerComponent = focusableObjects[i];
            if (c.tabIndex && !isNaN(Number(c.tabIndex)))
            {
                // if we get here, it is a candidate
                focusableCandidates.push(c);
            }
        }
        
        focusableCandidates.sort(sortByTabIndex);
    }

    /**
     *  @private
     */
    private function sortByDepth(aa:IFocusManagerComponent, bb:IFocusManagerComponent):Number
    {
        var val1:String = "";
        var val2:String = "";
        var index:int;
        var tmp:String;
        var tmp2:String;
        var zeros:String = "0000";

        var a:DisplayObject = DisplayObject(aa);
        var b:DisplayObject = DisplayObject(bb);

        while (a != DisplayObject(form) && a.parent)
        {
            index = getChildIndex(a.parent, a);
            tmp = index.toString(16);
            if (tmp.length < 4)
            {
                tmp2 = zeros.substring(0, 4 - tmp.length) + tmp;
            }
            val1 = tmp2 + val1;
            a = a.parent;
        }
        while (b != DisplayObject(form) && b.parent)
        {
            index = getChildIndex(b.parent, b);
            tmp = index.toString(16);
            if (tmp.length < 4)
            {
                tmp2 = zeros.substring(0, 4 - tmp.length) + tmp;
            }
            val2 = tmp2 + val2;
            b = b.parent;
        }

        return val1 > val2 ? 1 : val1 < val2 ? -1 : 0;
    }

    private function getChildIndex(parent:DisplayObjectContainer, child:DisplayObject):int
    {
        try 
        {
            return parent.getChildIndex(child);
        }
        catch(e:Error)
        {
            if (parent is IRawChildrenContainer)
                return IRawChildrenContainer(parent).rawChildren.getChildIndex(child);
            throw e;
        }
        throw new Error("FocusManager.getChildIndex failed");   // shouldn't ever get here
    }

    /**
     *  @private
     *  Calculate what focusableObjects are valid tab candidates.
     */
    private function sortFocusableObjects():void
    {
        // trace("FocusableObjects " + focusableObjects.length.toString());
        focusableCandidates = [];
        
        var n:int = focusableObjects.length;
        for (var i:int = 0; i < n; i++)
        {
            var c:InteractiveObject = focusableObjects[i];
            // trace("  " + c);
            if (c.tabIndex && !isNaN(Number(c.tabIndex)) && c.tabIndex > 0)
            {
                sortFocusableObjectsTabIndex();
                return;
            }
            focusableCandidates.push(c);
        }
        
        focusableCandidates.sort(sortByDepth);
    }

    /**
     *  Call this method to make the system
     *  think the Enter key was pressed and the defaultButton was clicked
     */
    mx_internal function sendDefaultButtonEvent():void
    {
        // trace("FocusManager.sendDefaultButtonEvent " + defButton);
        defButton.dispatchEvent(new MouseEvent("click"));
    }

    /**
     *  @private
     *  Do a tree walk and add all children you can find.
     */
    private function addFocusables(o:DisplayObject, skipTopLevel:Boolean = false):void
    {
        // trace(">>addFocusables " + o);
        if (o is IFocusManagerComponent && !skipTopLevel)
        {
            var focusable:IFocusManagerComponent = IFocusManagerComponent(o);
            if (focusable.focusEnabled)
            {
                if (focusable.tabEnabled && isTabVisible(o))
                {
                    focusableObjects.push(o);
                    calculateCandidates = true;
                    // trace("FM added " + o);
                }
                o.addEventListener("tabEnabledChange", tabEnabledChangeHandler);
                o.addEventListener("tabIndexChange", tabIndexChangeHandler);
            }
        }

        if (o is DisplayObjectContainer)
        {
            var doc:DisplayObjectContainer = DisplayObjectContainer(o);
            // Even if they aren't focusable now,
            // listen in case they become later.
            o.addEventListener("tabChildrenChange", tabChildrenChangeHandler);

            if (doc.tabChildren)
            {
                var i:int;
                if (o is IRawChildrenContainer)
                {
                    // trace("using view rawChildren");
                    var rawChildren:IChildList = IRawChildrenContainer(o).rawChildren;
                    // recursively visit and add children of components
                    // we don't do this for containers because we get individual
                    // adds for the individual children
                    for (i = 0; i < rawChildren.numChildren; i++)
                    {
                        try
                        {
                            addFocusables(rawChildren.getChildAt(i));
                        }
                        catch(error:SecurityError)
                        {
                            // Ignore this child if we can't access it
                        }
                    }

                }
                else
                {
                    // trace("using container's children");
                    // recursively visit and add children of components
                    // we don't do this for containers because we get individual
                    // adds for the individual children
                    for (i = 0; i < doc.numChildren; i++)
                    {
                        try
                        {
                            addFocusables(doc.getChildAt(i));
                        }
                        catch(error:SecurityError)
                        {
                            // Ignore this child if we can't access it
                        }
                    }
                }
            }
        }
        // trace("<<addFocusables " + o);
    }

    /**
     *  @private
     *  is it really tabbable?
     */
    private function isTabVisible(o:DisplayObject):Boolean
    {
        var s:DisplayObject = DisplayObject(form.systemManager);
        if (!s) return false;

        var p:DisplayObjectContainer = o.parent;
        while (p && p != s)
        {
            if (!p.tabChildren)
                return false;
            p = p.parent;
        }
        return true;
    }

    private function isValidFocusCandidate(o:DisplayObject, g:String):Boolean
    {
        if (!isEnabledAndVisible(o))
            return false;

        if (o is IFocusManagerGroup)
        {
            // reject if it is in the same tabgroup
            var tg:IFocusManagerGroup = IFocusManagerGroup(o);
            if (g == tg.groupName) return false;
        }
        return true;
    }
    
    private function getIndexOfFocusedObject(o:DisplayObject):int
    {
        if (!o)
            return -1;

        var n:int = focusableCandidates.length;
        // trace(" focusableCandidates " + n);
        var i:int = 0;
        for (i = 0; i < n; i++)
        {
            // trace(" comparing " + focusableCandidates[i]);
            if (focusableCandidates[i] == o)
                return i;
        }

        // no match?  try again with a slower match for certain
        // cases like DG editors
        for (i = 0; i < n; i++)
        {
            var iui:IUIComponent = focusableCandidates[i] as IUIComponent;
            if (iui && iui.owns(o))
                return i;
        }

        return -1;
    }


    private function getIndexOfNextObject(i:int, shiftKey:Boolean, bSearchAll:Boolean, groupName:String):int
    {
        var n:int = focusableCandidates.length;
        var start:int = i;

        while (true)
        {
            if (shiftKey)
                i--;
            else
                i++;
            if (bSearchAll)
            {
                if (shiftKey && i < 0)
                    break;
                if (!shiftKey && i == n)
                    break;
            }
            else
            {
                i = (i + n) % n;
                // came around and found the original
                if (start == i)
                    break;
            }
            // trace("testing " + focusableCandidates[i]);
            if (isValidFocusCandidate(focusableCandidates[i], groupName))
            {
                // trace(" stopped at " + i);
                var o:DisplayObject = DisplayObject(findFocusManagerComponent(focusableCandidates[i]));     
                if (o is IFocusManagerGroup)
                {
                    // look around to see if there's a selected member in the tabgroup
                    // otherwise use the first one we found.
                    var tg1:IFocusManagerGroup = IFocusManagerGroup(o);
                    for (var j:int = 0; j < focusableCandidates.length; j++)
                    {
                        var obj:DisplayObject = focusableCandidates[j];
                        if (obj is IFocusManagerGroup)
                        {
                            var tg2:IFocusManagerGroup = IFocusManagerGroup(obj);
                            if (tg2.groupName == tg1.groupName && tg2.selected)
                            {
                                // if objects of same group have different tab index
                                // skip you aren't selected.
                                if (InteractiveObject(obj).tabIndex != InteractiveObject(o).tabIndex && !tg1.selected)
                                    return getIndexOfNextObject(i, shiftKey, bSearchAll, groupName);

                                i = j;
                                break;
                            }
                        }
                    }

                }
                return i;
            }
        }
        return i;
    }

    /**
     *  @private
     */
    private function setFocusToNextObject(event:FocusEvent):void
    {
        if (focusableObjects.length == 0)
            return;

		var o:IFocusManagerComponent = getNextFocusManagerComponent(event.shiftKey);
		// trace("winner = ", o);
		
		if (o)
		{
			if (o is IFocusManagerComplexComponent)
				IFocusManagerComplexComponent(o).assignFocus(event.shiftKey ? "bottom" : "top");
			else
				setFocus(o);
		}
	}

    /**
     *  @inheritDoc
     */
    public function getNextFocusManagerComponent(
                            backward:Boolean = false):IFocusManagerComponent
    {
        if (focusableObjects.length == 0)
            return null;

        // I think we'll have time to do this here instead of at creation time
        // this makes and orders the focusableCandidates array
        if (calculateCandidates)
        {
            sortFocusableObjects();
            calculateCandidates = false;
        }

        // get the object that has the focus
        var o:DisplayObject = form.systemManager.stage.focus;
        // trace("focus was at " + o);
        // trace("focusableObjects " + focusableObjects.length);
        o = DisplayObject(findFocusManagerComponent(InteractiveObject(o)));

        var g:String = "";
        if (o is IFocusManagerGroup)
        {
            var tg:IFocusManagerGroup = IFocusManagerGroup(o);
            g = tg.groupName;
        }
        var i:int = getIndexOfFocusedObject(o);

        // trace(" starting at " + i);
        var bSearchAll:Boolean = false;
        var start:int = i;
        if (i == -1) // we didn't find it
        {
            if (backward)
                i = focusableCandidates.length;
            bSearchAll = true;
            // trace("search all " + i);
        }

        var j:int = getIndexOfNextObject(i, backward, bSearchAll, g);

        return findFocusManagerComponent(focusableCandidates[j]);
    }

    /**
     *  @private
     */
    private function getTopLevelFocusTarget(o:InteractiveObject):InteractiveObject
    {
        while (o != InteractiveObject(form))
        {
            if (o is IFocusManagerComponent &&
                IFocusManagerComponent(o).focusEnabled &&
                IFocusManagerComponent(o).mouseFocusEnabled &&
                (o is IUIComponent ? IUIComponent(o).enabled : true))
                return o;

            o = o.parent;

            if (o == null)
                break;
        }

        return null;
    }

    /**
     *  Returns a String representation of the component hosting the FocusManager object, 
     *  with the String <code>".focusManager"</code> appended to the end of the String.
     *
     *  @return Returns a String representation of the component hosting the FocusManager object, 
     *  with the String <code>".focusManager"</code> appended to the end of the String.
     */
    public function toString():String
    {
        return Object(form).toString() + ".focusManager";
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Listen for children being added
     *  and see if they are focus candidates.
     */
    private function addedHandler(event:Event):void
    {
        var target:DisplayObject = DisplayObject(event.target);
        
        // trace("FM got added for " + target);
        
        // if it is truly parented, add it, otherwise it will get added when the top of the tree
        // gets parented
        if (target.stage)
        {
            // trace("adding focusables");
            addFocusables(DisplayObject(event.target));
        }
    }

    /**
     *  @private
     *  Listen for children being removed.
     */
    private function removedHandler(event:Event):void
    {
        var i:int;
        var o:DisplayObject = DisplayObject(event.target);
 
        // trace("FM got added for " + event.target);

        if (o is IFocusManagerComponent)
        {
            for (i = 0; i < focusableObjects.length; i++)
            {
                if (o == focusableObjects[i])
                {
                    if (o == lastFocus)
                    {
                        lastFocus.drawFocus(false);
                        lastFocus = null;
                    }
                    // trace("FM removed " + o);
                    o.removeEventListener("tabEnabledChange", tabEnabledChangeHandler);
                    o.removeEventListener("tabIndexChange", tabIndexChangeHandler);
                    focusableObjects.splice(i, 1);
                    calculateCandidates = true;                 
                    break;
                }
            }
        }
        removeFocusables(o, false);
    }

    /**
     *  @private
     */
    private function removeFocusables(o:DisplayObject, dontRemoveTabChildrenHandler:Boolean):void
    {
        var i:int;
        if (o is DisplayObjectContainer)
        {
            if (!dontRemoveTabChildrenHandler)
                o.removeEventListener("tabChildrenChange", tabChildrenChangeHandler);

            for (i = 0; i < focusableObjects.length; i++)
            {
                if (isParent(DisplayObjectContainer(o), focusableObjects[i]))
                {
                    if (focusableObjects[i] == lastFocus)
                    {
                        lastFocus.drawFocus(false);
                        lastFocus = null;
                    }
                    // trace("FM removed " + focusableObjects[i]);
                    focusableObjects[i].removeEventListener(
                        "tabEnabledChange", tabEnabledChangeHandler);
                    focusableObjects[i].removeEventListener(
                        "tabIndexChange", tabIndexChangeHandler);
                    focusableObjects.splice(i, 1);
                    i = i - 1;  // because increment would skip one
                    calculateCandidates = true;                 
                }
            }
        }
    }

    /**
     *  @private
     */
    private function showHandler(event:Event):void
    {
        form.systemManager.activate(form);
    }

    /**
     *  @private
     */
    private function hideHandler(event:Event):void
    {
        form.systemManager.deactivate(form);
    }

    /**
     *  @private
     */
    private function creationCompleteHandler(event:FlexEvent):void
    {
        if (DisplayObject(form).visible && !activated)
            form.systemManager.activate(form);
    }

    /**
     *  @private
     *  Add or remove if tabbing properties change.
     */
    private function tabIndexChangeHandler(event:Event):void
    {
        calculateCandidates = true;
    }

    /**
     *  @private
     *  Add or remove if tabbing properties change.
     */
    private function tabEnabledChangeHandler(event:Event):void
    {
        calculateCandidates = true;

        var o:InteractiveObject = InteractiveObject(event.target);
        var n:int = focusableObjects.length;
        for (var i:int = 0; i < n; i++)
        {
            if (focusableObjects[i] == o)
                break;
        }
        if (o.tabEnabled)
        {
            if (i == n && isTabVisible(o))
            {
                // trace("FM tpc added " + o);
                // add it if were not already
                focusableObjects.push(o);
            }
        }
        else
        {
            // remove it
            if (i < n)
            {
                // trace("FM tpc removed " + o);
                focusableObjects.splice(i, 1);
            }
        }
    }

    /**
     *  @private
     *  Add or remove if tabbing properties change.
     */
    private function tabChildrenChangeHandler(event:Event):void
    {
        if (event.target != event.currentTarget)
            return;

        calculateCandidates = true;

        var o:DisplayObjectContainer = DisplayObjectContainer(event.target);
        if (o.tabChildren)
        {
            addFocusables(o, true);
        }
        else
        {
            removeFocusables(o, true);
        }
    }

    /**
     *  @private
     *  This gets called when mouse clicks on a focusable object.
     *  We block player behavior
     */
    private function mouseFocusChangeHandler(event:FocusEvent):void
    {
        if (event.relatedObject is TextField)
        {
            var tf:TextField = event.relatedObject as TextField;
            if (tf.type == "input" || tf.selectable)
            {
                return; // pass it on
            }
        }

        event.preventDefault();
    }

    /**
     *  @private
     *  This gets called when the tab key is hit.
     */
    private function keyFocusChangeHandler(event:FocusEvent):void
    {
        showFocusIndicator = true;

        if (event.keyCode == Keyboard.TAB && !event.isDefaultPrevented())
        {
            if (browserFocusComponent)
            {
                if (browserFocusComponent.tabIndex == LARGE_TAB_INDEX)
                    browserFocusComponent.tabIndex = -1;
                browserFocusComponent = null;
                return;
            }

            // trace("tabHandled by " + this);
            setFocusToNextObject(event);

            event.preventDefault();
        }
    }

    /**
     *  @private
     *  Watch for Enter key.
     */
    private function keyDownHandler(event:KeyboardEvent):void
    {
        // trace("onKeyDown handled by " + this);
        var sm:SystemManager = form.systemManager as SystemManager;
        if (sm)
            sm.idleCounter = 0;
    /*  else
        {
            var wsm:WindowedSystemManager = WindowedSystemManager(form.systemManager);
            wsm.idleCounter = 0;
        }
        */
        if (event.keyCode == Keyboard.TAB)
        {
            lastAction = "KEY";

            // I think we'll have time to do this here instead of at creation time
            // this makes and orders the focusableCandidates array
            if (calculateCandidates)
            {
                sortFocusableObjects();
                calculateCandidates = false;
            }
        }

        if (browserMode)
        {
            if (event.keyCode == Keyboard.TAB && focusableCandidates.length > 0)
            {
                // get the object that has the focus
                var o:DisplayObject = form.systemManager.stage.focus;
                // trace("focus was at " + o);
                // trace("focusableObjects " + focusableObjects.length);
                o = DisplayObject(findFocusManagerComponent(InteractiveObject(o)));
                var g:String = "";
                if (o is IFocusManagerGroup)
                {
                    var tg:IFocusManagerGroup = IFocusManagerGroup(o);
                    g = tg.groupName;
                }

                var i:int = getIndexOfFocusedObject(o);
                var j:int = getIndexOfNextObject(i, event.shiftKey, false, g);
                if (event.shiftKey)
                {
                    if (j >= i)
                    {
                        // we wrapped so let browser have it
                        browserFocusComponent = form.systemManager.stage.focus;
                        if (browserFocusComponent.tabIndex == -1)
                            browserFocusComponent.tabIndex = 0;
                    }
                }
                else
                {
                    if (j <= i)
                    {
                        // we wrapped so let browser have it
                        browserFocusComponent = form.systemManager.stage.focus;
                        if (browserFocusComponent.tabIndex == -1)
                            browserFocusComponent.tabIndex = LARGE_TAB_INDEX;
                    }
                }
            }
        }

        if (defaultButtonEnabled &&
            event.keyCode == Keyboard.ENTER &&
            defaultButton && defButton.enabled)
            //sendDefaultButtonEvent();
            defButton.callLater(sendDefaultButtonEvent);
    }

    /**
     *  @private
     *  This gets called when the focus changes due to a mouse click.
     *
     *  Note: If the focus is changing to a TextField, we don't call
     *  setFocus() on it because the player handles it;
     *  calling setFocus() on a TextField which has scrollable text
     *  causes the text to autoscroll to the end, making the
     *  mouse click set the insertion point in the wrong place.
     */
    private function mouseDownHandler(event:MouseEvent):void
    {
        // trace("FocusManager mouseDownHandler by " + this);
        // trace("FocusManager mouseDownHandler target " + event.target);
        
        if (event.isDefaultPrevented())
            return;

        var o:DisplayObject = getTopLevelFocusTarget(
            InteractiveObject(event.target));

        if (!o)
            return;

        showFocusIndicator = false;
        
        // trace("FocusManager mouseDownHandler on " + o);
        
        // Make sure the containing component gets notified.
        // As the note above says, we don't set focus to a TextField ever
        // because the player already did and took care of where
        // the insertion point is, and we also don't call setfocus
        // on a component that last the last focused object unless
        // the last action was just to activate the player and didn't
        // involve tabbing or clicking on a component
        if ((o != lastFocus || lastAction == "ACTIVATE") && !(o is TextField))
            setFocus(IFocusManagerComponent(o));

        lastAction = "MOUSEDOWN";
    }
}

}
