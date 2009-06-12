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

package mx.automation.delegates
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.FocusEvent;
import flash.events.IEventDispatcher;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.text.TextFieldType;
import flash.ui.Keyboard;

import mx.automation.Automation;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationEvent;
import mx.automation.events.TextSelectionEvent;
import mx.core.Application;
import mx.core.EventPriority;
import mx.core.IUITextField;
import mx.core.mx_internal;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.managers.IFocusManagerContainer;
import mx.managers.ISystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("automation")]

/** 
 * Utility class that facilitates replay of text input and selection.
 */
public class TextFieldAutomationHelper 
{
    include "../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Constructors
    //
    //--------------------------------------------------------------------------

    /**
     * Constructor.
     *  
     * @param owner The UIComponent that is using the TextField. For example, if a 
     * TextArea is using the TextField, then the TextArea is the owner.
     *  
     * @param replayer The IAutomationObject of the component.
     *  
     * @param textField The TextField object inside the component.
     *  
     */
    public function TextFieldAutomationHelper(owner:IEventDispatcher,
                                                replayer:IAutomationObject,
                                              textField:IUITextField)
    {
        super();

        this.owner = owner;
        this.replayer = replayer;
        this.textField = textField;
        this.owner.addEventListener(FocusEvent.FOCUS_IN, 
                                    focusInHandler, 
                                    false, 
                                    EventPriority.DEFAULT-100, true);
        this.textField.addEventListener(MouseEvent.MOUSE_DOWN, 
                                        mouseDownHandler, false, EventPriority.DEFAULT, true);

        captureSelection();
        oldSelection = currentSelection;
        hasSelectionChanged = false;
        
        if(recording)
            checkInitialFocus();
        else
            Automation.automationManager.addEventListener(AutomationEvent.BEGIN_RECORD, beginRecordingHandler);
        
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */ 
    private var stringBuffer:String;

    /**
     *  @private
     */ 
    private var owner:IEventDispatcher;
    
    /**
     *  @private
     */ 
    private var replayer:IAutomationObject;

    /**
     *  @private
     */ 
    private var textField:IUITextField;

    /**
     *  @private
     */ 
    private var currentSelection:Array = null;
    
    /**
     * @private
     */
    private var oldSelection:Array = null;

    /**
     *  @private
     */ 
    private var hasSelectionChanged:Boolean = false;

    /**
     *  @private
     */ 
    private var isWatchingFocus:Boolean = false;
    
    /**
     *  @private
     */ 
    private var isInInsertMode:Boolean = false;

    /**
     *  @private
     *  Used for accessing localized Error messages.
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  recording
    //----------------------------------

    /**
     *  @private
     */ 
    private function get recording():Boolean
    {
        return Automation.automationManager &&
               (Automation.automationManager as IAutomationManager).recording;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */ 
    private function flushCharacterBuffer():void
    {
        if (stringBuffer)
        {
            flushSelection();
            var e:TextEvent = new TextEvent(TextEvent.TEXT_INPUT);
            e.text = stringBuffer.toString();
            stringBuffer = null;
            recordAutomatableEvent(e);
        }
    }
    
    /**
     *  @private
     */ 
    private function captureSelection():void
    {
       // var oldSelection:Array = selection;
        
        if (textField.selectionBeginIndex != textField.selectionEndIndex)
        {
            currentSelection = [ textField.selectionBeginIndex, textField.selectionEndIndex ];
//                        trace("captured selection [" + currentSelection[0] + ", " + currentSelection[1] + 
//                                "] using selection");
        }
        else
        {
            currentSelection = [ textField.caretIndex, textField.caretIndex ];
//                        trace("captured selection [" + currentSelection[0] + ", " + currentSelection[1] + 
//                              "] using caret");
        }
        
        
        hasSelectionChanged =  oldSelection == null || oldSelection[0] != currentSelection[0] || oldSelection[1] != currentSelection[1];
    }

    /**
     *  @private
     */ 
    private function flushSelection():void
    {
        if (!hasSelectionChanged)
            return;
        
        //        trace("flushing selection [" + (selection ? selection[0] + "," + selection[1] : null) + "]");
        if (currentSelection && currentSelection[0] >= 0 && currentSelection[1] >= 0)
        {
            var e:TextSelectionEvent = new TextSelectionEvent();
            e.beginIndex = currentSelection[0];
            e.endIndex = currentSelection[1];
            recordAutomatableEvent(e);
            oldSelection = currentSelection;
            currentSelection = null;
        }
    }

    /**
     *  @private
     */ 
    private function get hasSelection():Boolean
    {
        return (textField.selectionBeginIndex != textField.selectionEndIndex);
    }

    /**
    *  @private
    */
    protected function checkInitialFocus():void
    {
        //check whether we have already focus so that we can prepare for user input
        var o:DisplayObject = DisplayObject(textField) ;

        while (o)
        {
            if (o is IFocusManagerContainer)
                break ; 

            o = o.parent;
        }

        if (o)
        {
            var focusManager:IFocusManager = IFocusManagerContainer(o).focusManager;        
            var focusObj:DisplayObject = focusManager ?
                                         DisplayObject(focusManager.getFocus()) :
                                         null;
            if (focusObj == owner)
                focusInHandler(null);
        }
    }

    /**
     *  Records the user interaction with the text control.
     *  
     *  @param interaction The event to record.
     * 
     *  @param cacheable Contains <code>true</code> if this is a cacheable event, and <code>false</code> if not.
     */ 
    public function recordAutomatableEvent(interaction:Event, 
                                           cacheable:Boolean = false):void
    {
        var am:IAutomationManager = Automation.automationManager;
        am.recordAutomatableEvent(replayer, interaction, cacheable);
    }
    
    /**
     *  Replays TypeTextEvents and TypeEvents. TypeTextEvents are replayed by
     *  calling replaceText on the underlying text field. TypeEvents are replayed
     *  depending on the character typed.  Both also dispatch the origin keystrokes.
     *  This is necessary to mimic the original behavior, in case any components are
     *  listening to keystroke events (for example, DataGrid listens to itemRenderer events,
     *  or if a custom component is trying to do key masking).  Ideally, the code would just
     *  dispatch the original keystrokes, but the Flash Player TextField ignores
     *  the events we are sending it.
     *
     * @param event Event to replay.
     * 
     * @return If true, replay the event.
     */
    public function replayAutomatableEvent(event:Event):Boolean
    {
        var changeEvent:Event = new Event(Event.CHANGE);
        var sm:ISystemManager = Application.application.systemManager;
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
         
        if (event is MouseEvent &&
            event.type == MouseEvent.CLICK)
            return help.replayClick(owner, event as MouseEvent);
        else if (event is TextSelectionEvent)
        {
            // need to set focus in order for the uitextfield to behave correctly
            IFocusManagerComponent(owner).setFocus();
            var selectionEvent:TextSelectionEvent = 
                TextSelectionEvent(event);
            textField.setSelection(selectionEvent.beginIndex, 
                                   selectionEvent.endIndex);
            // replay a click so that if anyone is listening for selection
            // changes they would get the signal.
            // RichTextEditor is one of the clients!
            help.replayClick(owner);
            return true;
        }
        else if (event is TextEvent)
        {
            // need to set focus in order for the uitextfield to behave correctly
            IFocusManagerComponent(owner).setFocus();
            var textEvent:TextEvent = TextEvent(event);
            var text:String = textEvent.text;
            for (var i:uint = 0; i < textEvent.text.length; i++)
            {
                var ke:KeyboardEvent = new KeyboardEvent(KeyboardEvent.KEY_DOWN);
                ke.charCode = text.charCodeAt(i);
                ke.keyCode = text.charCodeAt(i);
                textField.dispatchEvent(ke);
                var pos:int ;
                if(!isInInsertMode)
                {
                    textField.replaceSelectedText(text.charAt(i));
                    pos = (textField.selectionBeginIndex != textField.selectionEndIndex
                                   ? textField.selectionBeginIndex + 1
                                   : textField.caretIndex);
                    textField.setSelection(pos, pos);
                }
                else
                {
                    if(textField.selectionBeginIndex == textField.selectionEndIndex)
                        textField.replaceText(textField.caretIndex, textField.caretIndex+1, text.charAt(i));
                    else
                        textField.replaceText(textField.selectionBeginIndex, textField.selectionEndIndex+1, text.charAt(i));
                    
                    pos = (textField.selectionBeginIndex != textField.selectionEndIndex
                                   ? textField.selectionBeginIndex + 1
                                   : textField.caretIndex+1);
                    textField.setSelection(pos, pos);
                }

                var te:TextEvent = new TextEvent(TextEvent.TEXT_INPUT);
                te.text = String(text.charCodeAt(i));
                textField.dispatchEvent(te);

                ke = new KeyboardEvent(KeyboardEvent.KEY_UP);
                ke.charCode = text.charCodeAt(i);
                ke.keyCode = text.charCodeAt(i);
                textField.dispatchEvent(ke);

                textField.dispatchEvent(changeEvent);
            }

            return true;
        }
        else if (event is KeyboardEvent)
        {
            var kbEvent:KeyboardEvent = KeyboardEvent(event);
            var keyCode:int = kbEvent.keyCode;
            switch (keyCode)
            {
            case Keyboard.HOME:
                break;
            case Keyboard.END:
                break;
            
            case Keyboard.ENTER:
                // replace the selected text with newline
                if (textField.multiline)
                    textField.replaceSelectedText("\n");
                break;

            case Keyboard.BACKSPACE:
                // if selection, erase it. else erase before cursor
                if (hasSelection)
                {
                    textField.replaceText(textField.selectionBeginIndex,
                                            textField.selectionEndIndex,
                                            "");
                    var x:uint = (textField.selectionBeginIndex != textField.selectionEndIndex 
                                  ? textField.selectionBeginIndex
                                  : textField.caretIndex);
                    textField.setSelection(x, x);
                }
                else
                {
                    var index:uint = textField.caretIndex - 1;
                    textField.replaceText(index, textField.caretIndex, "");
                    textField.setSelection(index, index);
                }
                break;

            case Keyboard.DELETE:
                // if selection, erase it. else erase after cursor
                if (hasSelection)
                {
                    textField.replaceText(textField.selectionBeginIndex,
                                            textField.selectionEndIndex, "");
                    textField.setSelection(textField.selectionBeginIndex, 
                                            textField.selectionBeginIndex);
                }
                else
                    textField.replaceText(textField.caretIndex, 
                                    textField.caretIndex + 1, "");
                break;
            case Keyboard.INSERT:
                isInInsertMode = !isInInsertMode;
                break;
            case Keyboard.ESCAPE:
                break;

            default:
            {
                var message:String = resourceManager.getString(
                    "automation", "notReplayable", [keyCode]);
                throw new Error(message);
            }
            }
            ke = new KeyboardEvent(KeyboardEvent.KEY_DOWN);
            ke.charCode = keyCode;
            ke.keyCode = keyCode;
            ke.ctrlKey = kbEvent.ctrlKey;
            ke.shiftKey = kbEvent.shiftKey;
            ke.altKey = kbEvent.altKey;

            textField.dispatchEvent(ke);

            ke = new KeyboardEvent(KeyboardEvent.KEY_UP);
            ke.charCode = keyCode;
            ke.keyCode = keyCode;
            ke.ctrlKey = kbEvent.ctrlKey;
            ke.shiftKey = kbEvent.shiftKey;
            ke.altKey = kbEvent.altKey;

            textField.dispatchEvent(ke);

            textField.dispatchEvent(changeEvent);
            return true;
        }
        return false;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function focusInHandler(event:FocusEvent):void
    {
        if (!recording)
            return;

        if (textField.type == TextFieldType.INPUT && !isWatchingFocus)
        {
            isWatchingFocus = true;
            
            //Add the focus change listeners as low priority
            //so that any code that may prevent default (prevent
            //the focus change) gets a chance to execute before
            //getting to us.  We only want to process the event
            //if the focus really is going to change.
            textField.addEventListener(FocusEvent.KEY_FOCUS_CHANGE,
                                       focusOutHandler,
                                       false,
                                       EventPriority.DEFAULT-1000, true);
            //Use FOCUS_OUT instead of MOUSE_FOCUS_CHANGE never
            //really gets fired because the player doesn't initiate
            //mouse focus changes (except when a text field gets
            //focus).  Our mouseDownOutside handler should take
            //care of flushing events before a new item gets focus
            //and we may not even need this event handler
            textField.addEventListener(FocusEvent.FOCUS_OUT,
                                       focusOutHandler,
                                       false, EventPriority.DEFAULT, true);

            //In case someone clicks elsewhere but we don't loose the focus
            //we need to flush, i.e. they click a button that generates a click
            //we need to beat them and record our events first
            var sm:ISystemManager = Application.application.systemManager;
            sm.addEventListener(MouseEvent.MOUSE_DOWN,
                                mouseDownOutsideHandler,
                                true, EventPriority.DEFAULT, true);
            
            //If the user activates a different app, or tries to end recording
            //we need to flush.  If they use the keyboard all is good, but if
            //they use a mouse, our flush of events steals the focus back
            //which is annoying, so flush when the mouse leaves the app area
            Application.application.stage.addEventListener(Event.DEACTIVATE,
                                                           stageEventHandler,
                                                           false,
                                                           EventPriority.DEFAULT+1, true);
            Application.application.stage.addEventListener(Event.MOUSE_LEAVE,
                                                           stageEventHandler,
                                                           false,
                                                           EventPriority.DEFAULT+1, true);

            Application.application.stage.addEventListener(MouseEvent.MOUSE_DOWN,
                                                           stageEventHandler,
                                                           true,
                                                           EventPriority.DEFAULT+1, true);

            textField.addEventListener(TextEvent.TEXT_INPUT, 
                                       textInputHandler, 
                                       false, 
                                       EventPriority.DEFAULT+100, true);
            textField.addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler, false, EventPriority.DEFAULT, true);
            textField.addEventListener(KeyboardEvent.KEY_UP, keyUpHandler, false, EventPriority.DEFAULT, true);
            //need to cache selection so it is not recorded unless it changes
           captureSelection();
           oldSelection = currentSelection;
           hasSelectionChanged = false;
        }
    }
    
    /**
     *  @private
     */
    private function stageEventHandler(event:Event):void
    {
        //Don't call focusOutHandler, that would remove our event listeners
        //which would be bad because a deactive and mouse leave doesn't mean
        //the framework thinks we lost focus, framework should call focus out
        //if it does intend to remove focus during a deactive
        flushSelection();
        flushCharacterBuffer();
    }

    /**
     *  @private
     */
    private function mouseDownOutsideHandler(event:MouseEvent):void
    {
        //trace("mouseDownOutsideHandler " + event.type + " target " + event.target);
        if (event.target != textField)
        {
            //Don't call focusOutHandler, that would remove our event listeners
            //which would be bad because it's possible for someone to click outside 
            //of the textfield but not have the focus change.  Just flush the 
            //event buffers in case that mouse down outside causes an event to be recorded
            flushSelection();
            flushCharacterBuffer();
        }
    }

    /**
     *  @private
     */
    private function focusOutHandler(event:Event):void
    {
        //trace("focusOutHandler " + event.type);
        if (isWatchingFocus && !event.isDefaultPrevented())
        {
            isWatchingFocus = false;
    
            if (textField)
            {
                textField.removeEventListener(FocusEvent.KEY_FOCUS_CHANGE,
                                              focusOutHandler,
                                              false);
                textField.removeEventListener(FocusEvent.FOCUS_OUT,
                                              focusOutHandler,
                                              false);
            }
            var sm:ISystemManager = Application.application.systemManager;
            sm.removeEventListener(MouseEvent.MOUSE_DOWN,
                                   mouseDownOutsideHandler,
                                   true);
            Application.application.stage.removeEventListener(Event.DEACTIVATE,
                                                            stageEventHandler,
                                                            false);
            Application.application.stage.removeEventListener(Event.MOUSE_LEAVE,
                                                            stageEventHandler,
                                                            false);
            Application.application.stage.removeEventListener(MouseEvent.MOUSE_DOWN,
                                                            stageEventHandler,
                                                            true);
                                                            
            textField.removeEventListener(TextEvent.TEXT_INPUT, textInputHandler);
            textField.removeEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);
            textField.removeEventListener(KeyboardEvent.KEY_UP, keyUpHandler);
            
            flushSelection();
            flushCharacterBuffer();

        }
    }

    /**
     *  @private
     */
    private function mouseDownHandler(event:MouseEvent):void
    {
        //        trace("mouseDownHandler " + event.type);
        if (!recording)
            return;
        switch (textField.type)
        {
            case TextFieldType.DYNAMIC:
            {
                textField.addEventListener(MouseEvent.CLICK, 
                                           mouseClickHandler, false, EventPriority.DEFAULT, true);
                break;
            }
           case TextFieldType.INPUT:
           {
               textField.stage.addEventListener(MouseEvent.MOUSE_UP, 
                                                mouseUpHandler, false, EventPriority.DEFAULT, true);
               textField.addEventListener(MouseEvent.DOUBLE_CLICK,
                                           mouseDoubleClickHandler, false, EventPriority.DEFAULT, true);
               break;
           }
           default:
               throw new Error();
        }
    }

    /**
     *  @private
     */
    private function mouseClickHandler(event:MouseEvent):void
    {
        //        trace("mouseDownHandler " + event.type);
        if (!recording)
            return;
        textField.removeEventListener(MouseEvent.CLICK, 
                                      mouseClickHandler);

        recordAutomatableEvent(event);
    }
    
    private function mouseDoubleClickHandler(event:MouseEvent):void
    {
        if (!recording)
            return;
        captureSelection();
    }
    

    /**
     *  @private
     */
    private function mouseUpHandler(event:MouseEvent):void
    {
        //        trace("mouseUpHandler " + event.type);
        if (!recording)
            return;
        textField.stage.removeEventListener(MouseEvent.MOUSE_UP, 
                                            mouseUpHandler);

        flushCharacterBuffer();
        captureSelection();
        hasSelectionChanged = true;
    }

    /**
     *  @private
     */
    private function keyDownHandler(event:KeyboardEvent):void
    {
        if (!recording)
            return;
        
        //arrow and navigation keys should dispatch whatever was last typed
        //backspace, delete, and enter are dispatched
        //        trace("keyDownHandler " + event.keyCode);
        switch (event.keyCode)
        {
            case Keyboard.CONTROL:
                flushCharacterBuffer();
                break;
            case Keyboard.SHIFT:
                break;

            case Keyboard.DOWN:
            case Keyboard.END:
            case Keyboard.HOME:
            case Keyboard.LEFT:
            case Keyboard.PAGE_DOWN:
            case Keyboard.PAGE_UP:
            case Keyboard.RIGHT:
            case Keyboard.UP:
            {
                flushCharacterBuffer();
                break;
            }

            case Keyboard.INSERT:
            case Keyboard.BACKSPACE:
            case Keyboard.DELETE:
            case Keyboard.ENTER:
            {
                flushSelection();
                flushCharacterBuffer();
                recordAutomatableEvent(event);
                oldSelection = null;
                break;
            }
            
            case Keyboard.ESCAPE:
            {
                recordAutomatableEvent(event);
                break;
            }

            default:
            {
                break;
            }
        }
    }
    
    /**
     *  @private
     */
    private function keyUpHandler(event:KeyboardEvent):void
    {

        if (!recording)
            return;

        //arrow and navigation keys should dispatch whatever was last typed
        //backspace, delete, and enter are dispatched
        //        trace("keyUpHandler " + event.keyCode);
        switch (event.keyCode)
        {
            case Keyboard.TAB:
            {
                //flushCharacterBuffer();
                break;
            }

            case Keyboard.SHIFT:
            {
                break;
            }

            case Keyboard.DOWN:
            case Keyboard.END:
            case Keyboard.HOME:
            case Keyboard.LEFT:
            case Keyboard.PAGE_DOWN:
            case Keyboard.PAGE_UP:
            case Keyboard.RIGHT:
            case Keyboard.UP:
            {
                captureSelection();
                break;
            }

            case Keyboard.BACKSPACE:
            case Keyboard.DELETE:
            case Keyboard.ENTER:
            {
                break;
            }
            
            case Keyboard.CONTROL:
                captureSelection();
                break;

            default:
            {
                if (event.ctrlKey)
                {
                    flushSelection();
                    flushCharacterBuffer();
                }
                break;
            }
        }
    }

    /**
     *  @private
     */
    private function textInputHandler(event:TextEvent):void
    {
        if (!recording)
            return;

        // The \n will be caught by the ENTER capture
        if (event.text == "\n") 
            return;

        if (!stringBuffer)
        {
            flushSelection();
            stringBuffer = "";
        }
        
        // TextField allows a script to enter more text to be inserted than maxChars.
        // Hence we have to prevent the recording of more characters than maxChars. 
        // Without this check playback will add more characters than maxChars leading to errors.
        if(textField.maxChars == 0 || textField.length < textField.maxChars)
        {   
            stringBuffer += event.text;
            oldSelection = null;
        }
            
    }
    
    /**
     *  @private
     */
    private function beginRecordingHandler(event:Event):void
    {
        checkInitialFocus();
    }
}

}
