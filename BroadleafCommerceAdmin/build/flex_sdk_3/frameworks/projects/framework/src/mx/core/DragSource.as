////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  The DragSource class contains the data being dragged. The data can be in
 *  multiple formats, depending on the type of control that initiated the drag.
 *
 *  <p>Each format of data is identified with a string. The 
 *  <code>hasFormat()</code> method is used to determine if the object has
 *  data in that format. The <code>dataForFormat()</code> method is used
 *  to retrieve the data in the specified format.</p>
 *
 *  <p>Data can be added directly using the <code>addData()</code> method,
 *  or indirectly using the <code>addHandler()</code> method. The 
 *  <code>addHandler()</code> method registers a callback that will be called
 *  if the data is requested. This is useful for adding data in a non-native 
 *  format that may require large computations or conversions. For example, 
 *  if you have raw sound data you can add an MP3 format handler. The MP3 
 *  conversion will only be done if the MP3 data is requested.</p>
 *  
 */
public class DragSource
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
	public function DragSource()
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
	private var dataHolder:Object = {};	

	/**
	 *  @private
	 */
	private var formatHandlers:Object = {};

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  formats
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the formats property.
	 */
	private var _formats:Array /* of String */ = [];

	/**
	 *  Contains the formats of the drag data, as an Array of Strings.
	 *  Set this property using the <code>addData()</code>
	 *  or <code>addHandler()</code> methods.
	 *  The default value depends on data added to the DragSource object.
	 *
	 */
	public function get formats():Array /* of String */
	{
		return _formats;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Adds data and a corresponding format String to the drag source.
	 *  This method does not return a value.
	 * 
	 *  @param data Object that specifies the drag data.
	 *  This can be any object, such as a String, a DataProvider, and so on.
	 *
	 *  @param format String that specifies a label that describes
	 *  the format for this data.
	 */
	public function addData(data:Object, format:String):void
	{
		_formats.push(format);
		
		dataHolder[format] = data;
	}
	
	/**
	 *  Adds a handler that is called when data
	 *  for the specified format is requested. 
	 *  This is useful when dragging large amounts of data.
	 *  The handler is only called if the data is requested.
	 *  This method does not return a value.
	 *
	 *  @param handler Function that specifies the handler
	 *  called to request the data.
	 *  This function must return the data in the specified format.
	 *
	 *  @param format String that specifies the format for this data.
	 */
	public function addHandler(handler:Function,
							   format:String):void
	{
		_formats.push(format);

		formatHandlers[format] = handler;
	}
	
	/**
	 *  Retrieves the data for the specified format.
	 *  If the data was added with the <code>addData()</code> method,
	 *  it is returned directly.
	 *  If the data was added with the <code>addHandler()</code> method,
	 *  the handler function is called to return the data.
	 *
	 *  @param format String that specifies a label that describes
	 *  the format for the data to return. This string can be a custom value
	 *  if you are creating a custom drop target with the <code>addData()</code> method. 
	 *  <p>List-based controls have predefined values 
	 *  for the <code>format</code> parameter. If the control that initiated the
	 *  drag operation is a Tree, then the format is "treeItems" and the items
	 *  implement the ITreeDataProvider interface. For all other List-based
	 *  controls that have built-in drag and drop support, the format is "items" and the items
	 *  implement the IDataProvider interface.</p>
	 *
	 *  @return An Object
	 *  containing the data in the requested format.
	 *  If you drag multiple items, the returned value is an Array. 
	 *  For a List-based control, the returned value is always an Array, 
	 *  even if it contains a single item.
	 */
	public function dataForFormat(format:String):Object
	{
		var data:Object = dataHolder[format];
		if (data)
			return data;
		
		if (formatHandlers[format])
			return formatHandlers[format]();
		
		return null;
	}
	
	/**
	 *  Returns <code>true</code> if the data source contains
	 *  the requested format; otherwise, it returns <code>false</code>.
	 *
	 *  @param format String that specifies a label that describes the format 
	 *  for the data. 
	 *
	 *  @return <code>true</code> if the data source contains
	 *  the requested format.
	 */
	public function hasFormat(format:String):Boolean
	{
		var n:int = _formats.length;
		for (var i:int = 0; i < n; i++)
		{
			if (_formats[i] == format)
				return true;
		}
		
		return false;
	}
}

}
