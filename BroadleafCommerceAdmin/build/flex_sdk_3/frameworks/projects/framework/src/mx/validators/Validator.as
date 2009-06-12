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

package mx.validators
{

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import mx.binding.BindingManager;
import mx.core.IMXMLObject;
import mx.events.FlexEvent;
import mx.events.ValidationResultEvent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when validation succeeds.
 *
 *  @eventType mx.events.ValidationResultEvent.VALID 
 */
[Event(name="valid", type="mx.events.ValidationResultEvent")]

/** 
 *  Dispatched when validation fails.
 *
 *  @eventType mx.events.ValidationResultEvent.INVALID 
 */
[Event(name="invalid", type="mx.events.ValidationResultEvent")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[ResourceBundle("core")]
[ResourceBundle("validators")]

/**
 *  The Validator class is the base class for all Flex validators. 
 *  This class implements the ability for a validator to make a field
 *  required, which means that the user must enter a value in the field
 *  or the validation fails.
 * 
 *  @mxml
 *
 *  <p>The Validator class defines the following tag attributes, 
 *  which all of its subclasses inherit:</p>
 *
 *  <pre>
 *  &lt;mx:Validator 
 *    enabled="true|false" 
 *    listener="<i>Value of the source property</i>" 
 *    property="<i>No default</i>" 
 *    required="true|false" 
 *    requiredFieldError="This field is required." 
 *    source="<i>No default</i>" 
 *    trigger="<i>Value of the source property</i>" 
 *    triggerEvent="valueCommit" 
 *  /&gt;
 *  </pre>
 *
 *  @see mx.events.ValidationResultEvent
 *  @see mx.validators.ValidationResult
 *  @see mx.validators.RegExpValidationResult
 *
 *  @includeExample examples/SimpleValidatorExample.mxml
 */
public class Validator extends EventDispatcher implements IMXMLObject
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  A string containing the upper- and lower-case letters
     *  of the Roman alphabet  ("A" through "Z" and "a" through "z").
     */
    protected static const ROMAN_LETTERS:String =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     *  A String containing the decimal digits 0 through 9.    
     */ 
    protected static const DECIMAL_DIGITS:String = "0123456789";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Invokes all the validators in the <code>validators</code> Array.
     *  Returns an Array containing one ValidationResultEvent object 
     *  for each validator that failed.
     *  Returns an empty Array if all validators succeed. 
     *
     *  @param validators An Array containing the Validator objects to execute. 
     *
     *  @return Array of ValidationResultEvent objects, where the Array
     *  contains one ValidationResultEvent object for each validator
     *  that failed. 
     *  The Array is empty if all validators succeed.
     */
    public static function validateAll(validators:Array):Array
    {   
        var result:Array = [];

        var n:int = validators.length;
        for (var i:int = 0; i < n; i++)
        {
            var v:Validator = Validator(validators[i]);
            
            if (v.enabled)
            {
	            var resultEvent:ValidationResultEvent = v.validate();
	            
	            if (resultEvent.type != ValidationResultEvent.VALID)
	                result.push(resultEvent);
	        }
        }   
        
        return result;
    }
    
    /**
     *  @private
     */
    private static function findObjectFromString(doc:Object,
                                                 value:String):Object
    {
        var obj:Object = doc;
        var parts:Array = value.split(".");

        var n:int = parts.length;
        for (var i:int = 0; i < n; i++)
        {
            try
            {
                obj = obj[parts[i]];
                
                // There's no guarantee that the objects have
                // already been created when this function fires;
                // for example, in the deferred instantiation case,
                // this function fires before the object for validation
                // has been created.
                if (obj == null)
                {
                    //return true;
                }
            }
            catch(error:Error)
            {
                if ((error is TypeError) &&
                    (error.message.indexOf("null has no properties") != -1))
                {
                    var resourceManager:IResourceManager =
                        ResourceManager.getInstance();
                    var message:String = resourceManager.getString(
                        "validators", "fieldNotFound", [ value ]);
                    throw new Error(message);
                }
                else
                {                    
                    throw error;
                }
            }
        }
        
        return obj;
    }
    
    /**
     *  @private
     */
    private static function trimString(str:String):String
    {
        var startIndex:int = 0;
        while (str.indexOf(' ', startIndex) == startIndex)
        {
            ++startIndex;
        }

        var endIndex:int = str.length - 1;
        while (str.lastIndexOf(' ', endIndex) == endIndex)
        {
            --endIndex;
        }

        return endIndex >= startIndex ?
               str.slice(startIndex, endIndex + 1) :
               "";
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Validator()
    {
        super();

        // Register as a weak listener for "change" events from ResourceManager.
        // If Validators registered as a strong listener,
        // they wouldn't get garbage collected.
        resourceManager.addEventListener(
            Event.CHANGE, resourceManager_changeHandler, false, 0, true);

        resourcesChanged();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var document:Object;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  actualTrigger
    //----------------------------------

    /**
     *  Contains the trigger object, if any,
     *  or the source object. Used to determine the listener object
     *  for the <code>triggerEvent</code>. 
     */
    protected function get actualTrigger():IEventDispatcher
    {
        if (_trigger)
            return _trigger;
        
        else if (_source)
            return _source as IEventDispatcher;
            
        return null;            
    }
    
    //----------------------------------
    //  actualListeners
    //----------------------------------

    /**
     *  Contains an Array of listener objects, if any,  
     *  or the source object. Used to determine which object
     *  to notify about the validation result.
     */
    protected function get actualListeners():Array
    {
        var result:Array = [];
    
        if (_listener)
            result.push(_listener);
        
        else if (_source)
            result.push(_source);
            
        return result;
    }
    
    //----------------------------------
    //  enabled
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the enabled property.
     */
    private var _enabled:Boolean = true;
    
    [Inspectable(category="General", defaultValue="true")]

    /** 
     *  Setting this value to <code>false</code> will stop the validator
     *  from performing validation. 
     *  When a validator is disabled, it dispatch no events, 
     *  and the <code>validate()</code> method returns null.
     *
     *  @default true
     */
    public function get enabled():Boolean
    {
        return _enabled;
    }

    /**
     *  @private
     */
    public function set enabled(value:Boolean):void
    {
        _enabled = value;
    }
    
    //----------------------------------
    //  listener
    //----------------------------------

    /**
     *  @private
     *  Storage for the listener property.
     */
    private var _listener:Object;
    
    [Inspectable(category="General")]

    /**
     *  Specifies the validation listener.
     *
     *  <p>If you do not specify a listener,
     *  Flex uses the value of the <code>source</code> property. 
     *  After Flex determines the source component,
     *  it changes the border color of the component,
     *  displays an error message for a failure,
     *  or hides any existing error message for a successful validation.</p>
     *
     */
     
    /* This behavior has been removed.
     *  <p>If Flex does not find an appropriate listener, 
     *  validation errors propagate to the Application object, causing Flex 
     *  to display an Alert box containing the validation error message.</p>
     *
     *  <p>Specifying <code>this</code> causes the validation error
     *  to propagate to the Application object, 
     *  and displays an Alert box containing the validation error message.</p>
     */
    public function get listener():Object
    {
        return _listener;
    }

    /**
     *  @private
     */
    public function set listener(value:Object):void
    {
        removeListenerHandler();    
        _listener = value;
        addListenerHandler();   
    }
    
    //----------------------------------
    //  property
    //----------------------------------    
    
    /**
     *  @private
     *  Storage for the property property.
     */
    private var _property:String;
    
    [Inspectable(category="General")]

    /**
     *  A String specifying the name of the property
     *  of the <code>source</code> object that contains 
     *  the value to validate.
     *  The property is optional, but if you specify <code>source</code>,
     *  you should set a value for this property as well.
     *  
     *  @default null
     */
    public function get property():String
    {
        return _property;
    }

    /**
     *  @private
     */
    public function set property(value:String):void
    {
        _property = value;
    }
    
    //----------------------------------
    //  required
    //----------------------------------

    [Inspectable(category="General", defaultValue="true")]
    
    /**
     *  If <code>true</code>, specifies that a missing or empty 
     *  value causes a validation error. 
     *  
     *  @default true
     */
    public var required:Boolean = true;
    
    //----------------------------------
    //  resourceManager
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the resourceManager property.
     */
    private var _resourceManager:IResourceManager = ResourceManager.getInstance();
    
	/**
	 *  @private
	 *  This metadata suppresses a trace() in PropertyWatcher:
	 *  "warning: unable to bind to property 'resourceManager' ..."
	 */
	[Bindable("unused")]
	
    /**
     *  @copy mx.core.UIComponent#resourceManager
     */
    protected function get resourceManager():IResourceManager
    {
        return _resourceManager;
    }

    //----------------------------------
    //  source
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the source property.
     */
    private var _source:Object;
    
    [Inspectable(category="General")]

    /**
     *  Specifies the object containing the property to validate. 
     *  Set this to an instance of a component or a data model. 
     *  You use data binding syntax in MXML to specify the value.
     *  This property supports dot-delimited Strings
     *  for specifying nested properties. 
     *
     *  If you specify a value to the <code>source</code> property,
     *  then you should specify a value to the <code>property</code>
     *  property as well. 
     *  The <code>source</code> property is optional.
     *  
     *  @default null
     */
    public function get source():Object
    {
        return _source;
    }
    
    /**
     *  @private
     */
    public function set source(value:Object):void
    {
        if (_source == value)
            return;
        
        if (value is String)
        {
            var message:String = resourceManager.getString(
                "validators", "SAttribute", [ value ]);
            throw new Error(message);
        }
        
        // Remove the listener from the old source.
        removeTriggerHandler();
        removeListenerHandler();
        
        _source = value;
                
        // Listen for the trigger event on the new source.
        addTriggerHandler();    
        addListenerHandler();   
    }

    //----------------------------------
    //  subFields
    //----------------------------------

    /**
     *  An Array of Strings containing the names for the properties contained 
     *  in the <code>value</code> Object passed to the <code>validate()</code> method. 
     *  For example, CreditCardValidator sets this property to 
     *  <code>[ "cardNumber", "cardType" ]</code>. 
     *  This value means that the <code>value</code> Object 
     *  passed to the <code>validate()</code> method 
     *  should contain a <code>cardNumber</code> and a <code>cardType</code> property. 
     *
     *  <p>Subclasses of the Validator class that 
     *  validate multiple data fields (like CreditCardValidator and DateValidator)
     *  should assign this property in their constructor. </p>
     */
    protected var subFields:Array = [];

    //----------------------------------
    //  trigger
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the trigger property.
     */
    private var _trigger:IEventDispatcher;
    
    [Inspectable(category="General")]

    /**
     *  Specifies the component generating the event that triggers the validator. 
     *  If omitted, by default Flex uses the value of the <code>source</code> property.
     *  When the <code>trigger</code> dispatches a <code>triggerEvent</code>,
     *  validation executes. 
     */
    public function get trigger():IEventDispatcher
    {
        return _trigger;
    }
    
    /**
     *  @private
     */
    public function set trigger(value:IEventDispatcher):void
    {
        removeTriggerHandler();
        _trigger = value;
        addTriggerHandler();    
    }
    
    //----------------------------------
    //  triggerEvent
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the triggerEvent property.
     */
    private var _triggerEvent:String = FlexEvent.VALUE_COMMIT;
    
    [Inspectable(category="General")]

    /**
     *  Specifies the event that triggers the validation. 
     *  If omitted, Flex uses the <code>valueCommit</code> event. 
     *  Flex dispatches the <code>valueCommit</code> event
     *  when a user completes data entry into a control.
     *  Usually this is when the user removes focus from the component, 
     *  or when a property value is changed programmatically.
     *  If you want a validator to ignore all events,
     *  set <code>triggerEvent</code> to the empty string ("").
     */
    public function get triggerEvent():String
    {
        return _triggerEvent;
    }
    
    /**
     *  @private
     */
    public function set triggerEvent(value:String):void
    {
        if (_triggerEvent == value)
            return;
            
        removeTriggerHandler();
        _triggerEvent = value;
        addTriggerHandler();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Errors
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  requiredFieldError
    //----------------------------------

    /**
     *  @private
     *  Storage for the requiredFieldError property.
     */
    private var _requiredFieldError:String;
    
    /**
     *  @private
     */
    private var requiredFieldErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]
    
    /**
     *  Error message when a value is missing and the 
     *  <code>required</code> property is <code>true</code>. 
     *  
     *  @default "This field is required."
     */
    public function get requiredFieldError():String
    {
        return _requiredFieldError;
    }

    /**
     *  @private
     */
    public function set requiredFieldError(value:String):void
    {
        requiredFieldErrorOverride = value;

        _requiredFieldError = value != null ?
                              value :
                              resourceManager.getString(
                                  "validators", "requiredFieldError");
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: IMXMLObject
    //
    //--------------------------------------------------------------------------

     /**
      *  Called automatically by the MXML compiler when the Validator
      *  is created using an MXML tag.  
      *
      *  @param document The MXML document containing this Validator.
      *
      *  @param id Ignored.
      */
     public function initialized(document:Object, id:String):void
     {
        this.document = document;
     }
     
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  This method is called when a Validator is constructed,
     *  and again whenever the ResourceManager dispatches
     *  a <code>"change"</code> Event to indicate
     *  that the localized resources have changed in some way.
     * 
     *  <p>This event will be dispatched when you set the ResourceManager's
     *  <code>localeChain</code> property, when a resource module
     *  has finished loading, and when you call the ResourceManager's
     *  <code>update()</code> method.</p>
     *
     *  <p>Subclasses should override this method and, after calling
     *  <code>super.resourcesChanged()</code>, do whatever is appropriate
     *  in response to having new resource values.</p>
     */
    protected function resourcesChanged():void
    {
        requiredFieldError = requiredFieldErrorOverride;
    }

    /**
     *  @private
     */
    private function addTriggerHandler():void
    {
        if (actualTrigger)
            actualTrigger.addEventListener(_triggerEvent, triggerHandler);
    }
    
    /**
     *  @private
     */
    private function removeTriggerHandler():void
    {
        if (actualTrigger)  
            actualTrigger.removeEventListener(_triggerEvent, triggerHandler);
    }
    
    /**
     *  Sets up all of the listeners for the 
     *  <code>valid</code> and <code>invalid</code>
     *  events dispatched from the validator. Subclasses of the Validator class 
     *  should first call the <code>removeListenerHandler()</code> method, 
     *  and then the <code>addListenerHandler()</code> method if 
     *  the value of one of their listeners or sources changes. 
     *  The CreditCardValidator and DateValidator classes use this function internally. 
     */
    protected function addListenerHandler():void
    {
        var actualListener:Object;
        var listeners:Array = actualListeners;
    
        var n:int = listeners.length;
        for (var i:int = 0; i < n; i++)
        {
            actualListener = listeners[i];
            if (actualListener is IValidatorListener)
            {
                addEventListener(ValidationResultEvent.VALID,
                                 IValidatorListener(actualListener).validationResultHandler); 

                addEventListener(ValidationResultEvent.INVALID,
                                 IValidatorListener(actualListener).validationResultHandler); 
            }
        }
    }
    
    /**
     *  Disconnects all of the listeners for the 
     *  <code>valid</code> and <code>invalid</code>
     *  events dispatched from the validator. Subclasses should first call the
     *  <code>removeListenerHandler()</code> method and then the 
     *  <code>addListenerHandler</code> method if 
     *  the value of one of their listeners or sources changes. 
     *  The CreditCardValidator and DateValidator classes use this function internally. 
     */
    protected function removeListenerHandler():void
    {
        var actualListener:Object;
        var listeners:Array = actualListeners;
    
        var n:int = listeners.length;
        for (var i:int = 0; i < n; i++)
        {
            actualListener = listeners[i];
            if (actualListener is IValidatorListener)
            {
                removeEventListener(ValidationResultEvent.VALID,
                                    IValidatorListener(actualListener).validationResultHandler); 
                                    
                removeEventListener(ValidationResultEvent.INVALID,
                                    IValidatorListener(actualListener).validationResultHandler); 
            }
        }
    }

    /**
     *  Returns <code>true</code> if <code>value</code> is not null. 
     * 
     *  @param value The value to test.
     *
     *  @return <code>true</code> if <code>value</code> is not null.
     */
    protected function isRealValue(value:Object):Boolean
    {
        return (value != null); 
    }

    /**
     *  Performs validation and optionally notifies
     *  the listeners of the result. 
     *
     *  @param value Optional value to validate.
     *  If null, then the validator uses the <code>source</code> and
     *  <code>property</code> properties to determine the value.
     *  If you specify this argument, you should also set the
     *  <code>listener</code> property to specify the target component
     *  for any validation error messages.
     *
     *  @param suppressEvents If <code>false</code>, then after validation,
     *  the validator will notify the listener of the result.
     *
     *  @return A ValidationResultEvent object
     *  containing the results of the validation. 
     *  For a successful validation, the
     *  <code>ValidationResultEvent.results</code> Array property is empty. 
     *  For a validation failure, the
     *  <code>ValidationResultEvent.results</code> Array property contains
     *  one ValidationResult object for each field checked by the validator, 
     *  both for fields that failed the validation and for fields that passed. 
     *  Examine the <code>ValidationResult.isError</code>
     *  property to determine if the field passed or failed the validation. 
     *
     *  @see mx.events.ValidationResultEvent
     *  @see mx.validators.ValidationResult
     */
    public function validate(
                        value:Object = null,
                        suppressEvents:Boolean = false):ValidationResultEvent
    {   
        if (value == null)
            value = getValueFromSource();   
        
        // if required flag is true and there is no value
        // we need to generate a required field error
        if (isRealValue(value) || required)
        {
            return processValidation(value, suppressEvents);
        }
        else
        {
            // Just return valid
            return new ValidationResultEvent(ValidationResultEvent.VALID); 
        }
    }
    
    /**
     *  Returns the Object to validate. Subclasses, such as the 
     *  CreditCardValidator and DateValidator classes, 
     *  override this method because they need
     *  to access the values from multiple subfields. 
     *
     *  @return The Object to validate.
     */
    protected function getValueFromSource():Object
    {
        var message:String;

        if (_source && _property)
        {
            return _source[_property];
        }

        else if (!_source && _property)
        {
            message = resourceManager.getString(
                "validators", "SAttributeMissing");
            throw new Error(message);
        }

        else if (_source && !_property)
        {
            message = resourceManager.getString(
                "validators", "PAttributeMissing");
            throw new Error(message);
        }
        
        return null;
    }

    /** 
     *  @private 
     *  Main internally used function to handle validation process.
     */ 
    private function processValidation(
                        value:Object,
                        suppressEvents:Boolean):ValidationResultEvent
    {   
        var resultEvent:ValidationResultEvent;
        
        if (_enabled)
        {
            var errorResults:Array = doValidation(value);

            resultEvent = handleResults(errorResults);
        }           
        else
        {
            suppressEvents = true; // Don't send any events
        }
        
        if (!suppressEvents)
        {
            dispatchEvent(resultEvent);
        }

        return resultEvent;
    }

    /**
     *  Executes the validation logic of this validator, 
     *  including validating that a missing or empty value
     *  causes a validation error as defined by
     *  the value of the <code>required</code> property.
     *
     *  <p>If you create a subclass of a validator class,
     *  you must override this method. </p>
     *
     *  @param value Value to validate.
     *
     *  @return For an invalid result, an Array of ValidationResult objects,
     *  with one ValidationResult object for each field examined
     *  by the validator that failed validation.
     *
     *  @see mx.validators.ValidationResult
     */
    protected function doValidation(value:Object):Array
    {
        var results:Array = [];
        
        var result:ValidationResult = validateRequired(value);
        if (result)
            results.push(result);
            
        return results;
    }
            
    /**
     *  @private 
     *  Determines if an object is valid based on its
     *  <code>required</code> property.
     *  This is a convenience method for calling a validator from within a 
     *  custom validation function. 
     */
    private function validateRequired(value:Object):ValidationResult
    {
        if (required)
        {
            var val:String = (value != null) ? String(value) : "";

            val = trimString(val);

            // If the string is empty and required is set to true
            // then throw a requiredFieldError.
            if (val.length == 0)
            {
                return new ValidationResult(true, "", "requiredField",
                                            requiredFieldError);                 
            }
        }
        
        return null;
    }

    /**
     *  Returns a ValidationResultEvent from the Array of error results. 
     *  Internally, this function takes the results from the 
     *  <code>doValidation()</code> method and puts it into a ValidationResultEvent object. 
     *  Subclasses, such as the RegExpValidator class, 
     *  should override this function if they output a subclass
     *  of ValidationResultEvent objects, such as the RegExpValidationResult objects, and 
     *  needs to populate the object with additional information. You never
     *  call this function directly, and you should rarely override it. 
     *
     *  @param errorResults Array of ValidationResult objects.
     * 
     *  @return The ValidationResultEvent returned by the <code>validate()</code> method. 
     */
    protected function handleResults(errorResults:Array):ValidationResultEvent
    {
        var resultEvent:ValidationResultEvent;
        
        if (errorResults.length > 0)
        {
            resultEvent =
                new ValidationResultEvent(ValidationResultEvent.INVALID);
            resultEvent.results = errorResults;
            
            if (subFields.length > 0)
            {
                var errorFields:Object = {};
                var subField:String;
                
                // Now we need to send valid results
                // for every subfield that didn't fail.
                var n:int;
                var i:int;
                
                n = errorResults.length;
                for (i = 0; i < n; i++)
                {
                    subField = errorResults[i].subField;
                    if (subField)
                    {
                        errorFields[subField] = true;
                    }
                }
                
                n = subFields.length;
                for (i = 0; i < n; i++)
                {
                    if (!errorFields[subFields[i]])
                    {
                        errorResults.push(new ValidationResult(false,subFields[i]));
                    }
                }
            }
        }
        else
        {
            resultEvent = new ValidationResultEvent(ValidationResultEvent.VALID);
        }
        
        return resultEvent;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    private function triggerHandler(event:Event):void
    {
        validate();
    }

    /**
     *  @private
     */
    private function resourceManager_changeHandler(event:Event):void
    {
        resourcesChanged();
    }
}   

}
