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

package mx.controls.videoClasses 
{

import flash.events.Event;
import mx.controls.VideoDisplay;
import mx.core.mx_internal;
import mx.events.MetadataEvent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("controls")]
    
/**
 *  The CuePointManager class lets you use ActionScript code to 
 *  manage the cue points associated with the VideoDisplay control.  
 *
 *  @see mx.controls.VideoDisplay
 */
public class CuePointManager 
{
    include "../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */ 
    private var _owner:VideoPlayer;

    private var _metadataLoaded:Boolean;
    private var _disabledCuePoints:Array;
    private var _disabledCuePointsByNameOnly:Object;
    private var _cuePointIndex:uint;
    private var _cuePointTolerance:Number;
    private var _linearSearchTolerance:Number;

    private static var DEFAULT_LINEAR_SEARCH_TOLERANCE:Number = 50;

    private var cuePoints:Array;

    /**
     *  @private
     *  Reference to VideoDisplay object associated with this CuePointManager
     *  instance.
     */
    mx_internal var videoDisplay:VideoDisplay;

    /**
     *  @private
     *  Used for accessing localized Error messages.
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //
    // public APIs
    //

    /**
     *  Constructor.
     *  
     *  @param owner The VideoPlayer instance that is the parent of this CuePointManager.
     *  @param id This parameter is ignored; it is provided only for backwards compatibility.
     */
    public function CuePointManager(owner:VideoPlayer, id:uint = 0) 
    {
        // take in id just to be back-wards compatible
        super();

        _owner = owner;
        reset();
        _cuePointTolerance = _owner.playheadUpdateInterval / 2000;
        _linearSearchTolerance = DEFAULT_LINEAR_SEARCH_TOLERANCE;
    }

    /**
     *  @private
     *  Reset cue point lists
     */
    private function reset():void 
    {
        _metadataLoaded = false;
        cuePoints = null;
        _disabledCuePoints = null;
        _cuePointIndex = 0;
    }

    /**
     *  @private
     *  read only, has metadata been loaded
     */
    private function get metadataLoaded():Boolean
    {
        return _metadataLoaded;
    }

    /**
     *  @private
     *  <p>Set by FLVPlayback to update _cuePointTolerance</p>
     *  Should be exposed in VideoDisplay/ here in Flex 2.0.
     */
    private function set playheadUpdateInterval(aTime:Number):void
    { 
        _cuePointTolerance = aTime / 2000;
    }

    /**
     *  Adds a cue point.
     *
     *  <p>You can add multiple cue points with the same
     *  name and time.  When you call the <code>removeCuePoint()</code> method 
     *  with the name and time,  it removes the first matching cue point. 
     *  To remove all matching cue points, you have to make additional calls to
     *  the <code>removeCuePoint()</code> method.</p>
     *
     *  @param cuePoint The Object describes the cue
     *  point.  It must contain the properties <code>name:String</code> 
     *  and <code>time:Number</code> (in seconds).  
     *  If the Object does not conform to these
     *  conventions, it throws a <code>VideoError</code> error.
     *
     *  @return A copy of the cue point Object added. The copy has the
     *  following additional properties:
     *
     *  <ul>
     *    <li><code>array</code> - the Array of all cue points. Treat
     *    this Array as read only because adding, removing or editing objects
     *    within it can cause cue points to malfunction.</li>
     * 
     *    <li><code>index</code> - the index into the Array for the
     *    returned cue point.</li>
     *  </ul>
     * 
     *  @throws mx.controls.videoClasses.VideoError If the arguments are invalid.
     */
    public function addCuePoint(cuePoint:Object):Object
    {
        var message:String;
        
        // make sense of param
        var copy:Object = deepCopyObject(cuePoint);

        // sanity check
        var timeUndefined:Boolean = (isNaN(copy.time) || copy.time < 0);
        if (timeUndefined)
        {
            message = resourceManager.getString(
                "controls", "wrongTime");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }
        if (!copy.name)
        {
            message = resourceManager.getString(
                "controls", "wrongName");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }

        // add cue point to cue point array
        var index:Number;
        copy.type = "actionscript";
        if (cuePoints == null || cuePoints.length < 1)
        {
            index = 0;
            cuePoints = [];
            cuePoints.push(copy);
        }
        else
        {
            index = getCuePointIndex(cuePoints, true, copy.time, null, 0, 0);
            index = (cuePoints[index].time > copy.time) ? 0 : index + 1;
            cuePoints.splice(index, 0, copy);
        }
        
        // adjust _cuePointIndex
        var now:Number = _owner.playheadTime;
        if (now > 0)
        {
            if (_cuePointIndex == index)
            {
                if (now > cuePoints[index].time)
                {
                    _cuePointIndex++;
                }
            } 
            else if (_cuePointIndex > index)
            {
                _cuePointIndex++;
            }
        }
        else
        {
            _cuePointIndex = 0;
        }

        // return the cue point
        var returnObject:Object = deepCopyObject(cuePoints[index]);
        returnObject.array = cuePoints;
        returnObject.index = index;
        videoDisplay.dispatchEvent(new Event("cuePointsChanged"));
        return returnObject;
    }

    /**
     *  Removes a cue point from the currently
     *  loaded FLV file.  Only the <code>name</code> and <code>time</code> 
     *  properties are used from the <code>cuePoint</code> argument to 
     *  determine the cue point to be removed.
     *
     *  <p>If multiple cue points match the search criteria, only
     *  one will be removed.  To remove all cue points, call this function
     *  repeatedly in a loop with the same arguments until it returns
     *  <code>null</code>.</p>
     *
     *  @param cuePoint The Object must contain at least one of
     *  <code>name:String</code> and <code>time:Number</code> properties, and
     *  removes the cue point that matches the specified properties.
     *
     *  @return An object representing the cue point removed. If there was no
     *  matching cue point, then it returns <code>null</code>.
     */
    public function removeCuePoint(cuePoint:Object):Object 
    {
        // bail if no cue points
        if (cuePoints == null || cuePoints.length < 1)
            return null;

        // remove cue point from cue point array
        var index:Number = getCuePointIndex(cuePoints, false, cuePoint.time, cuePoint.name, 0, 0);
        if (index < 0) 
            return null;

        cuePoint = cuePoints[index];
        cuePoints.splice(index, 1);
        
        // adjust _cuePointIndex
        if (_owner.playheadTime > 0)
        {
            if (_cuePointIndex > index)
            {
                _cuePointIndex--;
            }
        }
        else
        {
            _cuePointIndex = 0;
        }

        videoDisplay.dispatchEvent(new Event("cuePointsChanged"));
        // return the cue point
        return cuePoint;
    }

    /**
     *  @private    
     *  removes enabled cue points from _disabledCuePoints
     */
    private function removeCuePoints(cuePointArray:Array, cuePoint:Object):Number 
    {
        var matchIndex:Number;
        var matchCuePoint:Object;
        var numChanged:Number = 0;
        for (matchIndex = getCuePointIndex(cuePointArray, true, -1, cuePoint.name, 0, 0); matchIndex >= 0;
             matchIndex = getNextCuePointIndexWithName(matchCuePoint.name, cuePointArray, matchIndex)) 
        {
            // remove match
            matchCuePoint = cuePointArray[matchIndex];
            cuePointArray.splice(matchIndex, 1);
            matchIndex--;
            numChanged++;
        }
        return numChanged;
    }

    /**
     *  @private    
     *  inserts cue points into array
     */
    private function insertCuePoint(insertIndex:Number, cuePointArray:Array, cuePoint:Object):Array 
    {
        if (insertIndex < 0)
        {
            cuePointArray = [];
            cuePointArray.push(cuePoint);
        }
        else
        {
            // find insertion point
            if (cuePointArray[insertIndex].time > cuePoint.time)
            {
                insertIndex = 0;
            }
            else
            {
                insertIndex++;
            }
            // insert into sorted cuePointArray
            cuePointArray.splice(insertIndex, 0, cuePoint);
        }
        return cuePointArray;
    }

    //
    // package internal methods, called by FLVPlayback
    //

    /**
     *  @private
     *  <p>Called by FLVPlayback on "playheadUpdate" event
     *  to throw "cuePoint" events when appropriate.</p>
     */
    mx_internal function dispatchCuePoints():void
    {
        var now:Number = _owner.playheadTime;
        if (_owner.stateResponsive && cuePoints != null)
        {   
            while (_cuePointIndex < cuePoints.length &&
                    cuePoints[_cuePointIndex].time <= now + _cuePointTolerance)
            {
                var metadataEvent:MetadataEvent =
                    new MetadataEvent(MetadataEvent.CUE_POINT);
                metadataEvent.info = deepCopyObject(cuePoints[_cuePointIndex++]);
                _owner.dispatchEvent(metadataEvent);
            }
        }
    }

    /**
     *  @private
     *  When our place in the stream is changed, this is called
     *  to reset our index into actionscript cue point array.
     *  Another method is used when cue points are added
     *  are removed.
     */
    mx_internal function resetCuePointIndex(time:Number):void 
    {
        if (time <= 0 || cuePoints == null)
        {
            _cuePointIndex = 0;
            return;
        }
        var index:Number = getCuePointIndex(cuePoints, true, time, null, 0, 0);
        _cuePointIndex = (cuePoints[index].time < time) ? index + 1 : index;
    }

    /**
     *  @private
     *  Search for a cue point in an array sorted by time.  See
     *  closeIsOK parameter for search rules.
     *
     *  @param cuePointArray array to search
     *  @param closeIsOK If true, the behavior differs depending on the
     *  parameters passed in:
     * 
     *  <ul>
     *
     *  <li>If name is null or undefined, then if the specific time is
     *  not found then the closest time earlier than that is returned.
     *  If there is no cue point earlier than time, the first cue point
     *  is returned.</li>
     *
     *  <li>If time is null, undefined or less than 0 then the first
     *  cue point with the given name is returned.</li>
     *
     *  <li>If time and name are both defined then the closest cue
     *  point, then if the specific time and name is not found then the
     *  closest time earlier than that with that name is returned.  If
     *  there is no cue point with that name and with an earlier time,
     *  then the first cue point with that name is returned.  If there
     *  is no cue point with that name, null is returned.</li>
     * 
     *  <li>If time is null, undefined or less than 0 and name is null
     *  or undefined, a VideoError is thrown.</li>
     * 
     *  </ul>
     *
     *  <p>If closeIsOK is false the behavior is:</p>
     *
     *  <ul>
     *
     *  <li>If name is null or undefined and there is a cue point with
     *  exactly that time, it is returned.  Otherwise null is
     *  returned.</li>
     *
     *  <li>If time is null, undefined or less than 0 then the first
     *  cue point with the given name is returned.</li>
     *
     *  <li>If time and name are both defined and there is a cue point
     *  with exactly that time and name, it is returned.  Otherwise null
     *  is returned.</li>
     *
     *  <li>If time is null, undefined or less than 0 and name is null
     *  or undefined, a VideoError is thrown.</li>
     * 
     *  </ul>
     *  @param time search criteria
     *  @param name search criteria
     *  @param start index of first item to be searched, used for
     *  recursive implementation, defaults to 0 if undefined
     *  @param len length of array to search, used for recursive
     *  implementation, defaults to cuePointArray.length if undefined
     *  @returns index for cue point in given array or -1 if no match found
     *  @throws VideoError if time and/or name parameters are bad
     *  @see #cuePointCompare()
     */
    private function getCuePointIndex(cuePointArray:Array, closeIsOK:Boolean,
                                              time:Number, name:String,
                                              start:Number, len:Number):Number 
    {
        // sanity checks        
        if (cuePointArray == null || cuePointArray.length < 1)
            return -1;
        
        var timeUndefined:Boolean = (isNaN(time) || time < 0);
        if (timeUndefined && !name) 
        {
            var message:String = resourceManager.getString(
                "controls", "wrongTimeName");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }

        if (len == 0) len = cuePointArray.length;

        // name is passed in and time is undefined or closeIsOK is
        // true, search for first name starting at either start
        // parameter index or index at or after passed in time, respectively
        if (name && (closeIsOK || timeUndefined)) 
        {
            var firstIndex:Number;
            var index:Number;
            if (timeUndefined)
                firstIndex = start;
            else 
                firstIndex = getCuePointIndex(cuePointArray, closeIsOK, time, null, 0, 0);
            for (index = firstIndex; index >= start; index--) 
                if (cuePointArray[index].name == name) 
                    break;
            if (index >= start) 
                return index;
            for (index = firstIndex + 1; index < len; index++)
                if (cuePointArray[index].name == name) 
                    break;
            if (index < len) 
                return index;
            return -1;
        }

        var result:Number;

        // iteratively check if short length
        if (len <= _linearSearchTolerance) 
        {
            var max:Number = start + len;
            for (var i:uint = start; i < max; i++) 
            {
                result = cuePointCompare(time, name, cuePointArray[i]);
                if (result == 0)
                    return i;
                if (result < 0) break;
            }
            if (closeIsOK) 
            {
                if (i > 0) 
                    return i - 1;
                return 0;
            }
            return -1;
        }

        // split list and recurse
        var halfLen:Number = Math.floor(len / 2);
        var checkIndex:Number = start + halfLen;
        result = cuePointCompare(time, name, cuePointArray[checkIndex]);
        if (result < 0) 
            return getCuePointIndex(cuePointArray, closeIsOK, time, name,
                                     start, halfLen);
        if (result > 0) 
            return getCuePointIndex(cuePointArray, closeIsOK, time, name,
                                     checkIndex + 1, halfLen - 1 + (len % 2));
        return checkIndex;
    }   

    /**
     *  @private
     *  <p>Given a name, array and index, returns the next cue point in
     *  that array after given index with the same name.  Returns null
     *  if no cue point after that one with that name.  Throws
     *  VideoError if argument is invalid.</p>
     *
     *  @returns index for cue point in given array or -1 if no match found
     */
    private function getNextCuePointIndexWithName(name:String, array:Array, index:Number):Number 
    {
        var message:String;
        
        // sanity checks
        if (!name)
        {
            message = resourceManager.getString(
                "controls", "wrongName");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }
        if (!array)
        {
            message = resourceManager.getString(
                "controls", "undefinedArray");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }
        if (isNaN(index) || index < -1 || index >= array.length)
        {
            message = resourceManager.getString(
                "controls", "wrongIndex");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }

        // find it
        var i:int;
        for (i = index + 1; i < array.length; i++)
            if (array[i].name == name) 
                break;
        if (i < array.length) 
            return i;
        return -1;
    }

    /**
     *  @private
     *  Takes two cue point Objects and returns -1 if first sorts
     *  before second, 1 if second sorts before first and 0 if they are
     *  equal.  First compares times with millisecond precision.  If
     *  they match, compares name if name parameter is not null or undefined.
     */
    private static function cuePointCompare(time:Number, name:String, cuePoint:Object):Number 
    {
        var compTime1:Number = Math.round(time * 1000);
        var compTime2:Number = Math.round(cuePoint.time * 1000);
        if (compTime1 < compTime2) return -1;
        if (compTime1 > compTime2) return 1;
        if (name != null) 
        {
            if (name == cuePoint.name) return 0;
            if (name < cuePoint.name) return -1;
            return 1;
        }
        return 0;
    }

    /**
     *  @private
     *
     *  <p>Search for a cue point in the given array at the given time
     *  and/or with given name.</p>
     *
     *  @param closeIsOK If true, the behavior differs depending on the
     *  parameters passed in:
     * 
     *  <ul>
     *
     *  <li>If name is null or undefined, then if the specific time is
     *  not found then the closest time earlier than that is returned.
     *  If there is no cue point earlier than time, the first cue point
     *  is returned.</li>
     *
     *  <li>If time is null, undefined or less than 0 then the first
     *  cue point with the given name is returned.</li>
     *
     *  <li>If time and name are both defined then the closest cue
     *  point, then if the specific time and name is not found then the
     *  closest time earlier than that with that name is returned.  If
     *  there is no cue point with that name and with an earlier time,
     *  then the first cue point with that name is returned.  If there
     *  is no cue point with that name, null is returned.</li>
     * 
     *  <li>If time is null, undefined or less than 0 and name is null
     *  or undefined, a VideoError is thrown.</li>
     * 
     *  </ul>
     *
     *  <p>If closeIsOK is false the behavior is:</p>
     *
     *  <ul>
     *
     *  <li>If name is null or undefined and there is a cue point with
     *  exactly that time, it is returned.  Otherwise null is
     *  returned.</li>
     *
     *  <li>If time is null, undefined or less than 0 then the first
     *  cue point with the given name is returned.</li>
     *
     *  <li>If time and name are both defined and there is a cue point
     *  with exactly that time and name, it is returned.  Otherwise null
     *  is returned.</li>
     *
     *  <li>If time is null, undefined or less than 0 and name is null
     *  or undefined, a VideoError is thrown.</li>
     *  
     *  </ul>
     *  @param timeOrCuePoint If String, then name for search.  If
     *  Number, then time for search.  If Object, then cuepoint object
     *  containing time and/or name parameters for search.
     *  @returns <code>null</code> if no match was found, otherwise
     *  copy of cuePoint object with additional properties:
     *
     *  <ul>
     *  
     *  <li><code>array</code> - the array that was searched. Treat
     *  this array as read only as adding, removing or editing objects
     *  within it can cause cue points to malfunction.</li>
     *
     *  <li><code>index</code> - the index into the array for the
     *  returned cuepoint.</li>
     *
     *  </ul>
     *  @see #getCuePointIndex()
     */
    private function getCuePoint(cuePointArray:Array, closeIsOK:Boolean,
                          timeNameOrCuePoint:Object = null):Object
    {
        var cuePoint:Object;
        switch (typeof(timeNameOrCuePoint)) 
        {
        case "string":
            cuePoint = {name:timeNameOrCuePoint};
            break;
        case "number":
            cuePoint = {time:timeNameOrCuePoint};
            break;
        case "object":
            cuePoint = timeNameOrCuePoint;
            break;
        } // switch
        var index:Number = getCuePointIndex(cuePointArray, closeIsOK, cuePoint.time, cuePoint.name, 0, 0);
        if (index < 0) return null;
        cuePoint = deepCopyObject(cuePointArray[index]);
        cuePoint.array = cuePointArray;
        cuePoint.index = index;
        return cuePoint;
    }

    /**
     *  @private
     *  <p>Given a cue point object returned from getCuePoint (needs
     *  the index and array properties added to those cue points),
     *  returns the next cue point in that array after that one with
     *  the same name.  Returns null if no cue point after that one
     *  with that name.  Throws VideoError if argument is invalid.</p>
     *
     *  @returns <code>null</code> if no match was found, otherwise
     *  copy of cuePoint object with additional properties:
     *
     *  <ul>
     *  
     *  <li><code>array</code> - the array that was searched.  Treat
     *  this array as read only as adding, removing or editing objects
     *  within it can cause cue points to malfunction.</li>
     *
     *  <li><code>index</code> - the index into the array for the
     *  returned cuepoint.</li>
     *
     *  </ul>
     */
    private function getNextCuePointWithName(cuePoint:Object):Object 
    {
        var message:String;

        // sanity checks
        if (!cuePoint)
        {
            message = resourceManager.getString(
                "controls", "undefinedParameter");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }
        if (isNaN(cuePoint.time) || cuePoint.time < 0)
        {
            message = resourceManager.getString(
                "controls", "wrongTime");
            throw new VideoError(VideoError.ILLEGAL_CUE_POINT, message);
        }

        // get index
        var index:Number = getNextCuePointIndexWithName(cuePoint.name, cuePoint.array, cuePoint.index);
        if (index < 0) 
            return null;

        // return copy
        var returnCuePoint:Object = deepCopyObject(cuePoint.array[index]);
        returnCuePoint.array = cuePoint.array;
        returnCuePoint.index = index;
        return returnCuePoint;
    }

    /**
     *  Search for a cue point with specified name.
     *
     *  @param name The name of the cue point.
     *  
     *  @return <code>null</code> if no match was found, or 
     *  a copy of the matching cue point Object with additional properties:
     *
     *  <ul>
     *    <li><code>array</code> - the Array of cue points searched. Treat
     *    this array as read only because adding, removing or editing objects
     *    within it can cause cue points to malfunction.</li>
     *
     *    <li><code>index</code> - the index into the Array for the
     *    returned cue point.</li>
     *  </ul>
     */
    public function getCuePointByName(name:String):Object
    {
        return getCuePoint(cuePoints, false, name);
    }
    
    /**
     *  Returns an Array of all cue points.
     *
     *  @return An Array of cue point objects. 
     *  Each cue point object describes the cue
     *  point, and contains the properties <code>name:String</code> 
     *  and <code>time:Number</code> (in seconds).  
     */
    public function getCuePoints():Array 
    {
        return cuePoints;
    }
    
    /**
     *  Removes all cue points.
     */
    public function removeAllCuePoints():void
    {
        cuePoints = null;
        videoDisplay.dispatchEvent(new Event("cuePointsChanged"));
    }
    
    /**
     * Set the array of cue points.
     *
     * <p>You can add multiple cue points with the same
     * name and time.  When you call the <code>removeCuePoint()</code> method
     * with this name, only the first one is removed.</p>
     *
     *  @param cuePointArray An Array of cue point objects. 
     *  Each cue point object describes the cue
     *  point. It must contain the properties <code>name:String</code> 
     *  and <code>time:Number</code> (in seconds).  
     */
    public function setCuePoints(cuePointArray:Array):void
    {
        // sanity checks
        if (cuePointArray == null)
            return;
        for (var index:uint = 0; index < cuePointArray.length; index++)
        {
            addCuePoint(cuePointArray[index]);
        }
    }
    
    /**
     *  @private
     *  Used to make copies of cue point objects.
     */
    private static function deepCopyObject(obj:Object, recurseLevel:Number = 0):Object 
    {
        if (obj == null || typeof(obj) != "object") return obj;
        if (isNaN(recurseLevel)) recurseLevel = 0;
        var newObj:Object = {};
        for (var i:Object in obj)
        {
            if (recurseLevel == 0 && (i == "array" || i == "index"))
            {
                // skip it
            }
            else if (typeof(obj[i]) == "object")
            {
                newObj[i] = deepCopyObject(obj[i], recurseLevel+1);
            }
            else
            {
                newObj[i] = obj[i];
            }
        }
        return newObj;
    }

} // class mx.controls.videoClasses.CuePointManager

}
