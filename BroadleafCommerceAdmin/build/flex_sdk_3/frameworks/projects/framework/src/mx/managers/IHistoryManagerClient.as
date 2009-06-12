////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

/**
 *  Interface that must be implemented by objects
 *  registered with the History Manager. The methods in this interface are
 *  called by the HistoryManager when saving and loading the history state
 *  of the application.
 *
 *  <p>This interface is implemented by the Flex navigator containers 
 *  TabNavigator, Accordion, and ViewStack. It must be implemented by any other
 *  component that is registered with the HistoryManager.</p> 
 *
 *  @see mx.managers.HistoryManager
 */
public interface IHistoryManagerClient
{
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Saves the state of this object. 
	 *  The object contains name:value pairs for each property
	 *  to be saved with the state. 
	 *
	 *  <p>The History Manager collects the state information from all components
	 *  and encodes the information in a URL format. Most browsers have a length
	 *  limitation on URLs, so the state information returned should be as minimal
	 *  as possible.</p>
	 *
	 *  @example The following code saves the selected index from a List, and
	 *  a search string.
	 *  <pre>
	 *  public function saveState():Object
	 *  {
	 *  	var state:Object = {};
	 *
	 *  	state.selectedIndex = myList.selectedIndex;
	 *  	state.searchString = mySearchInput.text;
	 *
	 *  	return state;
	 *	}
	 *	</pre>
	 *
	 *  @return The state of this object.
	 */
	function saveState():Object;
	
	/**
	 *  Loads the state of this object.
	 *  
	 *  @param state State of this object to load.
	 *  This will be null when loading the initial state of the application.
	 *
	 *  @example The following code loads the selected index and search string
	 *  from the saved state.
	 *  <pre>
	 *  public function loadState(state:Object):void
	 *  {
	 *  	// First, check to see if state is null. When the app is reset
	 *  	// back to its initial state, loadState() is passed null.
	 *  	if (state == null)
	 *  	{
	 *			myList.selectedIndex = -1;
	 *  		mySearchInput.text = "";
	 *  	}
	 *  	else
	 *  	{
	 *  		myList.selectedIndex = state.selectedIndex;
	 *  		mySearchInput.text = state.searchString;
	 *  	}
	 *  }
	 *  </pre>
	 */
	function loadState(state:Object):void;
	
	/**
	 *  Converts this object to a unique string. 
	 *  Implemented by UIComponent.
	 *
	 *  @return The unique identifier for this object.
	 */
	function toString():String;
}

}
