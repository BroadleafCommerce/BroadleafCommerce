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

package mx.formatters
{

import mx.core.mx_internal;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;

[ResourceBundle("SharedResources")]

/**
 *  The DateFormatter class uses a format String to return a formatted date and time String
 *  from an input String or a Date object.
 *  You can create many variations easily, including international formats.
 *
 *  <p>If an error occurs, an empty String is returned and a String describing 
 *  the error is saved to the <code>error</code> property. The <code>error</code> 
 *  property can have one of the following values:</p>
 *
 *  <ul>
 *    <li><code>"Invalid value"</code> means a value that is not a Date object or a 
 *    is not a recognized String representation of a date is
 *    passed to the <code>format()</code> method. (An empty argument is allowed.)</li>
 *    <li> <code>"Invalid format"</code> means either the <code>formatString</code> 
 *    property is set to empty (""), or there is less than one pattern letter 
 *    in the <code>formatString</code> property.</li>
 *  </ul>
 *
 *  <p>The <code>parseDateString()</code> method uses the mx.formatters.DateBase class
 *  to define the localized string information required to convert 
 *  a date that is formatted as a String into a Date object.</p>
 *  
 *  @mxml
 *  
 *  <p>You use the <code>&lt;mx:DateFormatter&gt;</code> tag
 *  to render date and time Strings from a Date object.</p>
 *
 *  <p>The <code>&lt;mx:DateFormatter&gt;</code> tag
 *  inherits all of the tag attributes  of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:DateFormatter
 *    formatString="Y|M|D|A|E|H|J|K|L|N|S"
 *   /> 
 *  </pre>
 *  
 *  @includeExample examples/DateFormatterExample.mxml
 *  
 *  @see mx.formatters.DateBase
 */
public class DateFormatter extends Formatter
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private    
     */
    private static const VALID_PATTERN_CHARS:String = "Y,M,D,A,E,H,J,K,L,N,S";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Converts a date that is formatted as a String into a Date object.
     *  Month and day names must match the names in mx.formatters.DateBase.
     *  
     *  @see mx.formatters.DateBase
     * 
     *  @param str Date that is formatted as a String. 
     *
     *  @return Date object.
     */
    protected static function parseDateString (str:String):Date
    {
        if (!str || str == "")
            return null;

        var year:int = -1;
        var mon:int = -1;
        var day:int = -1;
        var hour:int = -1;
        var min:int = -1;
        var sec:int = -1;
        
        var letter:String = "";
        var marker:Object = 0;
        
        var count:int = 0;
        var len:int = str.length;
        
        // Strip out the Timezone. It is not used by the DateFormatter
        var timezoneRegEx:RegExp = /(GMT|UTC)-\d\d\d\d/ig;
        
        str = str.replace(timezoneRegEx, "");
        
        while (count < len)
        {
            letter = str.charAt(count);
            count++;

            // If the letter is a blank space or a comma,
            // continue to the next character
            if (letter <= " " || letter == ",")
                continue;

            // If the letter is a key punctuation character,
            // cache it for the next time around.
            if (letter == "/" || letter == ":" ||
                letter == "+" || letter == "-")
            {
                marker = letter;
                continue;
            }

            // Scan for groups of numbers and letters
            // and match them to Date parameters
            if ("a" <= letter && letter <= "z" ||
                "A" <= letter && letter <= "Z")
            {
                // Scan for groups of letters
                var word:String = letter;
                while (count < len) 
                {
                    letter = str.charAt(count);
                    if (!("a" <= letter && letter <= "z" ||
                          "A" <= letter && letter <= "Z"))
                    {
                        break;
                    }
                    word += letter;
                    count++;
                }

                // Allow for an exact match
                // or a match to the first 3 letters as a prefix.
                var n:int = DateBase.mx_internal::defaultStringKey.length;
                for (var i:int = 0; i < n; i++)
                {
                    var s:String = String(DateBase.mx_internal::defaultStringKey[i]);
                    if (s.toLowerCase() == word.toLowerCase() ||
                        s.toLowerCase().substr(0,3) == word.toLowerCase())
                    {
                        if (i == 13) 
                        {
                            // pm
                            if (hour > 12 || hour < 1)
                                break; // error
                            else if (hour < 12)
                                hour += 12;
                        } 
                        else if (i == 12) 
                        {
                            // am
                            if (hour > 12 || hour < 1)
                                break; // error
                            else if (hour == 12)
                                hour = 0;

                        } 
                        else if (i < 12) 
                        {
                            // month
                            if (mon < 0)
                                mon = i;
                            else
                                break; // error
                        }
                        break;
                    }
                }
                marker = 0;
            }
            
            else if ("0" <= letter && letter <= "9")
            {
                // Scan for groups of numbers
                var numbers:String = letter;
                while ("0" <= (letter = str.charAt(count)) &&
                       letter <= "9" &&
                       count < len)
                {
                    numbers += letter;
                    count++;
                }
                var num:int = int(numbers);

                // If num is a number greater than 70, assign num to year.
                if (num >= 70)
                {
                    if (year != -1)
                    {
                        break; // error
                    }
                    else if (letter <= " " || letter == "," || letter == "." ||
                             letter == "/" || letter == "-" || count >= len)
                    {
                        year = num;
                    }
                    else
                    {
                        break; //error
                    }
                }

                // If the current letter is a slash or a dash,
                // assign num to month or day.
                else if (letter == "/" || letter == "-" || letter == ".")
                {
                    if (mon < 0)
                        mon = (num - 1);
                    else if (day < 0)
                        day = num;
                    else
                        break; //error
                }

                // If the current letter is a colon,
                // assign num to hour or minute.
                else if (letter == ":")
                {
                    if (hour < 0)
                        hour = num;
                    else if (min < 0)
                        min = num;
                    else
                        break; //error
                }

                // If hours are defined and minutes are not,
                // assign num to minutes.
                else if (hour >= 0 && min < 0)
                {
                    min = num;
                }

                // If minutes are defined and seconds are not,
                // assign num to seconds.
                else if (min >= 0 && sec < 0)
                {
                    sec = num;
                }

                // If day is not defined, assign num to day.
                else if (day < 0)
                {
                    day = num;
                }

                // If month and day are defined and year is not,
                // assign num to year.
                else if (year < 0 && mon >= 0 && day >= 0)
                {
                    year = 2000 + num;
                }

                // Otherwise, break the loop
                else
                {
                    break;  //error
                }
                
                marker = 0
            }
        }

        if (year < 0 || mon < 0 || mon > 11 || day < 1 || day > 31)
            return null; // error - needs to be a date

        // Time is set to 0 if null.
        if (sec < 0)
            sec = 0;
        if (min < 0)
            min = 0;
        if (hour < 0)
            hour = 0;

        // create a date object and check the validity of the input date
        // by comparing the result with input values.
        var newDate:Date = new Date(year, mon, day, hour, min, sec);
        if (day != newDate.getDate() || mon != newDate.getMonth())
            return null;

        return newDate;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function DateFormatter()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  formatString
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the formatString property.
     */
    private var _formatString:String;
    
    /**
     *  @private
     */
    private var formatStringOverride:String;

    [Inspectable(category="General", defaultValue="null")]

    /**
     *  The mask pattern.
     *  
     *  <p>You compose a pattern String using specific uppercase letters,
     *  for example: YYYY/MM.</p>
     *
     *  <p>The DateFormatter pattern String can contain other text
     *  in addition to pattern letters.
     *  To form a valid pattern String, you only need one pattern letter.</p>
     *      
     *  <p>The following table describes the valid pattern letters:</p>
     *
     *  <table class="innertable">
     *    <tr><th>Pattern letter</th><th>Description</th></tr>
     *    <tr>
     *      <td>Y</td>
     *      <td> Year. If the number of pattern letters is two, the year is 
     *        truncated to two digits; otherwise, it appears as four digits. 
     *        The year can be zero-padded, as the third example shows in the 
     *        following set of examples: 
     *        <ul>
     *          <li>YY = 05</li>
     *          <li>YYYY = 2005</li>
     *          <li>YYYYY = 02005</li>
     *        </ul></td>
     *    </tr>
     *    <tr>
     *      <td>M</td>
     *      <td> Month in year. The format depends on the following criteria:
     *        <ul>
     *          <li>If the number of pattern letters is one, the format is 
     *            interpreted as numeric in one or two digits. </li>
     *          <li>If the number of pattern letters is two, the format 
     *            is interpreted as numeric in two digits.</li>
     *          <li>If the number of pattern letters is three, 
     *            the format is interpreted as short text.</li>
     *          <li>If the number of pattern letters is four, the format 
     *           is interpreted as full text. </li>
     *        </ul>
     *          Examples:
     *        <ul>
     *          <li>M = 7</li>
     *          <li>MM= 07</li>
     *          <li>MMM=Jul</li>
     *          <li>MMMM= July</li>
     *        </ul></td>
     *    </tr>
     *    <tr>
     *      <td>D</td>
     *      <td>Day in month. While a single-letter pattern string for day is valid, 
     *        you typically use a two-letter pattern string.
     * 
     *        <p>Examples:</p>
     *        <ul>
     *          <li>D=4</li>
     *          <li>DD=04</li>
     *          <li>DD=10</li>
     *        </ul></td>
     *    </tr>
     *    <tr>
     *      <td>E</td>
     *      <td>Day in week. The format depends on the following criteria:
     *        <ul>
     *          <li>If the number of pattern letters is one, the format is 
     *            interpreted as numeric in one or two digits.</li>
     *          <li>If the number of pattern letters is two, the format is interpreted 
     *           as numeric in two digits.</li>
     *          <li>If the number of pattern letters is three, the format is interpreted 
     *            as short text. </li>
     *          <li>If the number of pattern letters is four, the format is interpreted 
     *           as full text. </li>
     *        </ul>
     *          Examples:
     *        <ul>
     *          <li>E = 1</li>
     *          <li>EE = 01</li>
     *          <li>EEE = Mon</li>
     *          <li>EEEE = Monday</li>
     *        </ul></td>
     *    </tr>
     *    <tr>
     *      <td>A</td>
     *      <td> am/pm indicator.</td>
     *    </tr>
     *    <tr>
     *      <td>J</td>
     *      <td>Hour in day (0-23).</td>
     *    </tr>
     *    <tr>
     *      <td>H</td>
     *      <td>Hour in day (1-24).</td>
     *    </tr>
     *    <tr>
     *      <td>K</td>
     *      <td>Hour in am/pm (0-11).</td>
     *    </tr>
     *    <tr>
     *      <td>L</td>
     *      <td>Hour in am/pm (1-12).</td>
     *    </tr>
     *    <tr>
     *      <td>N</td>
     *      <td>Minute in hour.
     * 
     *        <p>Examples:</p>
     *        <ul>
     *          <li>N = 3</li>
     *          <li>NN = 03</li>
     *        </ul></td>
     *    </tr>
     *    <tr>
     *      <td>S</td>
     *      <td>Second in minute. 
     * 
     *        <p>Example:</p>
     *        <ul>
     *          <li>SS = 30</li>
     *        </ul></td>
     *    </tr>
     *    <tr>
     *      <td>Other text</td>
     *      <td>You can add other text into the pattern string to further 
     *        format the string. You can use punctuation, numbers, 
     *        and all lowercase letters. You should avoid uppercase letters 
     *        because they may be interpreted as pattern letters.
     * 
     *        <p>Example:</p>
     *        <ul>
     *          <li>EEEE, MMM. D, YYYY at L:NN A = Tuesday, Sept. 8, 2005 at 1:26 PM</li>
     *        </ul></td>
     *    </tr>
     *  </table>
     *
     *  @default "MM/DD/YYYY"
     */
    public function get formatString():String
    {
        return _formatString;
    }

    /**
     *  @private
     */
    public function set formatString(value:String):void
    {
        formatStringOverride = value;

        _formatString = value != null ?
                        value :
                        resourceManager.getString(
                            "SharedResources", "dateFormat");
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

        formatString = formatStringOverride;
    }
    
    /**
     *  Generates a date-formatted String from either a date-formatted String or a Date object. 
     *  The <code>formatString</code> property
     *  determines the format of the output String.
     *  If <code>value</code> cannot be formatted, return an empty String 
     *  and write a description of the error to the <code>error</code> property.
     *
     *  @param value Date to format. This can be a Date object,
     *  or a date-formatted String such as "Thursday, April 22, 2004".
     *
     *  @return Formatted String. Empty if an error occurs. A description 
     *  of the error condition is written to the <code>error</code> property.
     */
    override public function format(value:Object):String
    {       
        // Reset any previous errors.
        if (error)
            error = null;

        // If value is null, or empty String just return "" 
        // but treat it as an error for consistency.
        // Users will ignore it anyway.
        if (!value || value == "")
        {
            error = defaultInvalidValueError;
            return "";
        }

        // -- value --

        if (value is String)
        {
            value = DateFormatter.parseDateString(String(value));
            if (!value)
            {
                error = defaultInvalidValueError;
                return "";
            }
        }
        else if (!(value is Date))
        {
            error = defaultInvalidValueError;
            return "";
        }

        // -- format --

        var letter:String;
        var nTokens:int = 0;
        var tokens:String = "";
        
        var n:int = formatString.length;
        for (var i:int = 0; i < n; i++)
        {
            letter = formatString.charAt(i);
            if (VALID_PATTERN_CHARS.indexOf(letter) != -1 && letter != ",")
            {
                nTokens++;
                if (tokens.indexOf(letter) == -1)
                {
                    tokens += letter;
                }
                else
                {
                    if (letter != formatString.charAt(Math.max(i - 1, 0)))
                    {
                        error = defaultInvalidFormatError;
                        return "";
                    }
                }
            }
        }

        if (nTokens < 1)
        {
            error = defaultInvalidFormatError;
            return "";
        }

        var dataFormatter:StringFormatter = new StringFormatter(
            formatString, VALID_PATTERN_CHARS,
            DateBase.mx_internal::extractTokenDate);

        return dataFormatter.formatValue(value);
    }
}

}
