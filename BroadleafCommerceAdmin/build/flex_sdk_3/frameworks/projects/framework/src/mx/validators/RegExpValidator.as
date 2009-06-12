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

package mx.validators
{

import mx.events.ValidationResultEvent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;

[ResourceBundle("validators")]

/** 
 *  The RegExpValidator class lets you use a regular expression
 *  to validate a field. 
 *  You pass a regular expression to the validator using the
 *  <code>expression</code> property, and additional flags
 *  to control the regular expression pattern matching 
 *  using the <code>flags</code> property. 
 *
 *  <p>The validation is successful if the validator can find a match
 *  of the regular expression in the field to validate.
 *  A validation error occurs when the validator finds no match.</p>
 *
 *  <p>The RegExpValidator class dispatches the <code>valid</code>
 *  and <code>invalid</code> events.
 *  For an <code>invalid</code> event, the event object is an instance
 *  of the ValidationResultEvent class, and it contains an Array
 *  of ValidationResult objects.</p>
 *
 *  <p>However, for a <code>valid</code> event, the ValidationResultEvent
 *  object contains an Array of RegExpValidationResult objects.
 *  The RegExpValidationResult class is a child class of the
 *  ValidationResult class, and contains additional properties 
 *  used with regular expressions, including the following:</p>
 *  <ul>
 *    <li><code>matchedIndex</code> An integer that contains the starting 
 *      index in the input String of the match.</li>
 *    <li><code>matchedString</code> A String that contains the substring 
 *      of the input String that matches the regular expression.</li>
 *    <li><code>matchedSubStrings</code> An Array of Strings that contains 
 *      parenthesized substring matches, if any. If no substring matches are found, 
 *      this Array is of length 0.  Use matchedSubStrings[0] to access the 
 *      first substring match.</li>
 *  </ul>
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:RegExpValidator&gt;</code> tag 
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:RegExpValidator
 *    expression="<i>No default</i>" 
 *    flags="<i>No default</i>" 
 *    noExpressionError="The expression is missing." 
 *    noMatchError="The field is invalid." 
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/RegExValidatorExample.mxml
 *  
 *  @see mx.validators.RegExpValidationResult
 *  @see mx.validators.ValidationResult
 *  @see RegExp
 */
public class RegExpValidator extends Validator
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /** 
     *  Constructor
     */     
    public function RegExpValidator()
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
    private var regExp:RegExp;
    
    /** 
     *  @private
     */     
    private var foundMatch:Boolean = false;
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  expression
    //----------------------------------

    /** 
     *  @private
     *  Storage for the expression property.
     */     
    private var _expression:String;
    
    [Inspectable(category="General")]

    /**
     *  The regular expression to use for validation. 
     */
    public function get expression():String
    {
        return _expression;
    }
        
    /** 
     *  @private
     */     
    public function set expression(value:String):void
    {
        if (_expression != value)
        {
            _expression = value;

            createRegExp();
        }
    }
    
    //----------------------------------
    //  flags
    //----------------------------------
    
    /** 
     *  @private
     *  Storage for the flags property.
     */     
    private var _flags:String;
    
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  The regular expression flags to use when matching.
     */
    public function get flags():String
    {
        return _flags;
    }
    
    /** 
     *  @private
     */     
    public function set flags(value:String):void
    {
        if (_flags != value)
        {
            _flags = value;

            createRegExp();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Errors
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  noExpressionError
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the noExpressionError property.
	 */
	private var _noExpressionError:String;
	
    /**
	 *  @private
	 */
	private var noExpressionErrorOverride:String;
	
    [Inspectable(category="Errors", defaultValue="null")]

    /** 
     *  Error message when there is no regular expression specifed. 
     *  The default value is "The expression is missing."
     */
	public function get noExpressionError():String
	{
		return _noExpressionError;
	}

	/**
	 *  @private
	 */
	public function set noExpressionError(value:String):void
	{
		noExpressionErrorOverride = value;

		_noExpressionError = value != null ?
							 value :
							 resourceManager.getString(
							     "validators", "noExpressionError");
	}

    //----------------------------------
    //  noMatchError
    //----------------------------------
        
    /**
	 *  @private
	 *  Storage for the noMatchError property.
	 */
	private var _noMatchError:String;
	
    /**
	 *  @private
	 */
	private var noMatchErrorOverride:String;
	
    [Inspectable(category="Errors", defaultValue="null")]

    /** 
     *  Error message when there are no matches to the regular expression. 
     *  The default value is "The field is invalid."
     */
	public function get noMatchError():String
	{
		return _noMatchError;
	}

	/**
	 *  @private
	 */
	public function set noMatchError(value:String):void
	{
		noMatchErrorOverride = value;

		_noMatchError = value != null ?
						value :
						resourceManager.getString(
							"validators", "noMatchError");
	}
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private    
     */
    override protected function resourcesChanged():void
    {
		super.resourcesChanged();

        noExpressionError = noExpressionErrorOverride;  
        noMatchError = noMatchErrorOverride;
    }

    /**
     *  Override of the base class <code>doValidation()</code> method
     *  to validate a regular expression.
     *
     *  <p>You do not call this method directly;
     *  Flex calls it as part of performing a validation.
     *  If you create a custom Validator class, you must implement this method. </p>
     *
     *  @param value Object to validate.
     *
     *  @return For an invalid result, an Array of ValidationResult objects,
     *  with one ValidationResult object for each field examined by the validator. 
     */
    override protected function doValidation(value:Object):Array
    {
        var results:Array = super.doValidation(value);
        
        // Return if there are errors
        // or if the required property is set to <code>false</code> and length is 0.
        var val:String = value ? String(value) : "";
        if (results.length > 0 || ((val.length == 0) && !required))
            return results;

        return validateRegExpression(value);
    }
    
    /**
     *  @private
     */
    override protected function handleResults(
                                    errorResults:Array):ValidationResultEvent
    {
        var result:ValidationResultEvent;
        
        if (foundMatch)
        {
            result = new ValidationResultEvent(ValidationResultEvent.VALID);
            result.results = errorResults;
        }
        else
        {
            result = super.handleResults(errorResults);
        }
        
        foundMatch = false;    
        
        return result;  
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function createRegExp():void
    {
        regExp = new RegExp(_expression,_flags);
    }
    
    /**
     *  @private 
     *  Performs validation on the validator
     */     
    private function validateRegExpression(value:Object):Array
    {
        var results:Array = [];
        foundMatch = false;
    
        if (regExp && _expression != "")
        {
            var result:Object = regExp.exec(String(value));
            if (regExp.global)
            {
                while (result != null) 
                {
                    results.push(new RegExpValidationResult(
                        false, null, "", "", result[0],
                        result.index, result.slice(1)));
                    result = regExp.exec(String(value));
                    foundMatch = true;
                }
            }
            else if (result != null)
            {
                results.push(new RegExpValidationResult(
                    false, null, "", "", result[0],
                    result.index, result.slice(1)));                
                foundMatch = true;
            }   
            
            if (results.length == 0)
            {
                results.push(new ValidationResult(
                    true, null, "noMatch",
                    noMatchError));
            }
        }
        else
        {
            results.push(new ValidationResult(
                true, null, "noExpression",
                noExpressionError));
        }
        
        return results;
    }
}

}
