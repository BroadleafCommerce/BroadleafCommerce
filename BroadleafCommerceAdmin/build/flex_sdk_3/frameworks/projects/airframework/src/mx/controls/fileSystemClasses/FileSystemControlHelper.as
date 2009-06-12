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

package mx.controls.fileSystemClasses
{

import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.filesystem.File;
import flash.system.Capabilities;
import flash.ui.Keyboard;
import mx.collections.ArrayCollection;
import mx.controls.FileSystemEnumerationMode;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.core.mx_internal;
import mx.events.FileEvent;
import mx.events.FlexEvent;
import mx.events.ListEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.DirectoryEnumeration;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class FileSystemControlHelper
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class initialization
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    public static var COMPUTER:File;
     
    /**
     *  @private
     */
    private static function initClass():void
    {
		if (Capabilities.os.substring(0, 3) == "Win")
   	 	 	COMPUTER = new File("root$:\\Computer");
      	else // Mac or Unix
      		COMPUTER = new File("/Computer");
    }
     
    initClass();
   	  
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
	private static function fileSystemIsCaseInsensitive():Boolean
	{
		var os:String = Capabilities.os.substring(0, 3);
		return os == "Win" || os == "Mac";
	}

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function FileSystemControlHelper(owner:Object, hierarchical:Boolean)
	{
		super();
		
		this.owner = owner;
		this.hierarchical = hierarchical;
		
		owner.addEventListener(FlexEvent.UPDATE_COMPLETE,
							   updateCompleteHandler);
	}
	
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  A reference to the FileSystemList, FileSystemDataGrid,
	 *  FileSystemTree, or FileSystemComboBox using this object.
	 */
	mx_internal var owner:Object;
	
	/**
	 *  @private
	 *  A flag indicating whether the dataProvider of the owner
	 *  is hierarchical or flat.
	 *  In other words, this flag is true if the owner
	 *  is a FileSystemTree and false otherwise.
	 */
	mx_internal var hierarchical:Boolean;
	
	/**
	 *  @private
	 */
	mx_internal var resourceManager:IResourceManager =
						ResourceManager.getInstance();
	
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  backHistory
    //----------------------------------

	/**
	 *  @private
	 */
	public function get backHistory():Array
	{
		return historyIndex > 0 ?
			   history.slice(0, historyIndex).reverse() :
			   [];
	}

    //----------------------------------
	//  canNavigateBack
    //----------------------------------

	/**
	 *  @private
	 */
	public function get canNavigateBack():Boolean
	{
		return historyIndex > 0;
	}
	
    //----------------------------------
	//  canNavigateDown
    //----------------------------------

	/**
	 *  @private
	 */
	public function get canNavigateDown():Boolean
	{
		var selectedFile:File = File(owner.selectedItem);
		return selectedFile && selectedFile.isDirectory;
	}
	
    //----------------------------------
	//  canNavigateForward
    //----------------------------------

	/**
	 *  @private
	 */
	public function get canNavigateForward():Boolean
	{
		return historyIndex < history.length - 1;
	}
	
    //----------------------------------
	//  canNavigateUp
    //----------------------------------

	/**
	 *  @private
	 */
	public function get canNavigateUp():Boolean
	{
		return !isComputer(directory);
	}
	
    //----------------------------------
	//  directory
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the directory property.
	 */
	private var _directory:File;
	
	/**
	 *  @private
	 */
	private var directoryChanged:Boolean = false;
	
	/**
	 *  @private
	 */
	public function get directory():File
	{
		return _directory;
	}
	
	/**
	 *  @private
	 */
	public function set directory(value:File):void
	{
		if (!value ||
			(!isComputer(value) && 
			 (!value.exists || !value.isDirectory)))
		{
			throw(new Error("No such directory: " + value.nativePath));
		}

		resetHistory(value);
		
		setDirectory(value);
	}
	
    //----------------------------------
	//  directoryEnumeration
    //----------------------------------

	/**
	 *  @private
	 */
	mx_internal var directoryEnumeration:DirectoryEnumeration =
						new DirectoryEnumeration();
	
    //----------------------------------
	//  enumerationMode
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the enumerationMode property.
	 */
	private var _enumerationMode:String =
					FileSystemEnumerationMode.DIRECTORIES_FIRST;
	
	/**
	 *  @private
	 */
	private var enumerationModeChanged:Boolean = false;

	/**
	 *  @private
	 */
	public function get enumerationMode():String
	{
		return _enumerationMode;
	}
	
	/**
	 *  @private
	 */
	public function set enumerationMode(value:String):void
	{
		_enumerationMode = value;
		enumerationModeChanged = true;
		
		owner.invalidateProperties();
	}

    //----------------------------------
	//  extensions
    //----------------------------------
    
	/**
	 *  @private
	 *  Storage for the extensions property.
	 */
   	private var _extensions:Array /* of String */;
	
	/**
	 *  @private
	 */
	private var extensionsChanged:Boolean = false;
	
	/**
	 *  @private
	 */
	public function get extensions():Array /* of String */
	{
		return _extensions;
	}
	
	/**
	 *  @private
	 */
	public function set extensions(value:Array /* of String */):void
	{
		_extensions = value;
		extensionsChanged = true;
		
		owner.invalidateProperties();
	}
	
    //----------------------------------
	//  filterFunction
    //----------------------------------
    
	/**
	 *  @private
	 *  Storage for the filterFunction property.
	 */
   	private var _filterFunction:Function;
   	
   	/**
   	 *  @private
   	 */
   	private var filterFunctionChanged:Boolean = false;
	
	/**
	 *  @private
	 */
	public function get filterFunction():Function
	{
		return _filterFunction;
	}
	
	/**
	 *  @private
	 */
	public function set filterFunction(value:Function):void
	{
		_filterFunction = value;
		filterFunctionChanged = true;
		
		owner.invalidateProperties();
	}
	
    //----------------------------------
	//  forwardHistory
    //----------------------------------

	[Bindable("historyChanged")]
	
	/**
	 *  @private
	 */
	public function get forwardHistory():Array
	{
		return historyIndex < history.length - 1 ?
			   history.slice(historyIndex + 1) :
			   [];
	}

    //----------------------------------
	//  history
    //----------------------------------

	/**
	 *  @private
	 */
	public var history:Array;
	
    //----------------------------------
	//  historyIndex
    //----------------------------------

	/**
	 *  @private
	 */
	public var historyIndex:int;
	
    //----------------------------------
	//  nativePathToIndexMap
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the nativePathToIndexMap property.
	 */
	private var _nativePathToIndexMap:Object;
	
	/**
	 *  @private
	 *  Maps nativePath (String) -> index (int).
	 *  This map is used to implement findIndex() as a simple lookup,
	 *  so that multiple finds are fast.
	 *  It is freed whenever an operation changes which items
	 *  are displayed in the control, or their order,
	 *  and rebuilt tne next time it or <code>items</code> is accessed.
	 */
	mx_internal function get nativePathToIndexMap():Object
	{
		if (!_nativePathToIndexMap)
			rebuildEnumerationInfo();
		
		return _nativePathToIndexMap;
	}

    //----------------------------------
	//  itemArray
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the itemArray property.
	 */
	private var _itemArray:Array /* of File */;
	
	/**
	 *  @private
	 *  An array of all the File items displayed in the control,
	 *  in the order in which they appear.
	 *  This array is used together with <code>nativePathToIndexMap</code>
	 *  to implement findItem() as a simple lookup,
	 *  so that multiple finds are fast.
	 *  It is freed whenever an operation changes which items
	 *  are displayed in the control, or their order,
	 *  and rebuilt tne next time it
	 *  or <code>nativePathToIndexMap</code> is accessed.
	 */
	mx_internal function get itemArray():Array /* of File */
	{
		if (!_itemArray)
			rebuildEnumerationInfo();
			
		return _itemArray;
	}

    //----------------------------------
	//  nameCompareFunction
    //----------------------------------
    
	/**
	 *  @private
	 *  Storage for the nameCompareFunction property.
	 */
   	private var _nameCompareFunction:Function;
   	
   	/**
   	 *  @private
   	 */
   	private var nameCompareFunctionChanged:Boolean = false;
	
	/**
	 *  @private
	 */
	public function get nameCompareFunction():Function
	{
		return _nameCompareFunction;
	}
	
	/**
	 *  @private
	 */
	public function set nameCompareFunction(value:Function):void
	{
		_nameCompareFunction = value;
		nameCompareFunctionChanged = true;
		
		owner.invalidateProperties();
	}
	
    //----------------------------------
    //  openPaths
    //----------------------------------

	/**
	 *  @private
	 */
	private var pendingOpenPaths:Array /* of String */;

    /**
     *  An Array of <code>nativePath</code> Strings for the File items
	 *  representing the open subdirectories.
     *  This Array is empty if no subdirectories are open.
     * 
     *  @default []
     */
    public function get openPaths():Array /* of String */
    {
        return pendingOpenPaths ?
        	   pendingOpenPaths :
        	   getOpenPaths();
    }
    
    /**
     *  @private
     */
    public function set openPaths(value:Array /* of String */):void
    {
    	pendingOpenPaths = value;
		
		owner.invalidateProperties();
    }

    //----------------------------------
    //  selectedPath
    //----------------------------------

    /**
     *  @private
     */
    public function get selectedPath():String
    {
        return selectedPaths[0];
    }
    
    /**
     *  @private
     */
    public function set selectedPath(value:String):void
    {
        selectedPaths = [ value ];
    }

    //----------------------------------
    //  selectedPaths
    //----------------------------------

	/**
	 *  @private
	 */
	private var pendingSelectedPaths:Array /* of String */;

    /**
     *  @private
     */
    public function get selectedPaths():Array /* of String */
    {
        return pendingSelectedPaths ?
        	   pendingSelectedPaths :
        	   getSelectedPaths();
   }
    
    /**
     *  @private
     */
    public function set selectedPaths(value:Array /* of String */):void
    {
    	pendingSelectedPaths = value;
		
		owner.invalidateProperties();
     }

    //----------------------------------
	//  showExtensions
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the showExtensions property.
	 */
	private var _showExtensions:Boolean = true;
	
	/**
	 *  @private
	 */
	public function get showExtensions():Boolean
	{
		return _showExtensions;
	}
	
	/**
	 *  @private
	 */
	public function set showExtensions(value:Boolean):void
	{
		_showExtensions = value;
		
		owner.invalidateList();
	}

    //----------------------------------
	//  showHidden
    //----------------------------------
    
	/**
	 *  @private
	 *  Storage for the showHidden property.
	 */
 	private var _showHidden:Boolean = false;
 	
	/**
	 *  @private
	 */
 	private var showHiddenChanged:Boolean = false;
	
	/**
	 *  @private
	 */
	public function get showHidden():Boolean
	{
		return _showHidden;
	}
	
	/**
	 *  @private
	 */
	public function set showHidden(value:Boolean):void
	{
		_showHidden = value;
		showHiddenChanged = true;
		
		owner.invalidateProperties();
	}

    //----------------------------------
	//  showIcons
    //----------------------------------
    
	/**
	 *  @private
	 *  Storage for the showIcons property.
	 */
 	private var _showIcons:Boolean = true;
	
	/**
	 *  @private
	 */
	public function get showIcons():Boolean
	{
		return _showIcons;
	}
	
	/**
	 *  @private
	 */
	public function set showIcons(value:Boolean):void
	{
		_showIcons = value;
		
		owner.invalidateList();
	}

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	public function commitProperties():void
	{
		if (enumerationModeChanged ||
			extensionsChanged ||
			filterFunctionChanged ||
			nameCompareFunctionChanged ||
			showHiddenChanged)
		{
			directoryEnumeration.enumerationMode = enumerationMode;
			directoryEnumeration.extensions = extensions;
			directoryEnumeration.filterFunction = filterFunction;
			directoryEnumeration.nameCompareFunction = nameCompareFunction;
			directoryEnumeration.showHidden = showHidden;
			directoryEnumeration.refresh();
			
			// For a List or DataGrid, refreshing its collection
			// (which is what directoryEnumeration.refresh() does)
			// is enough to make the control update properly
			// with the newly filtered/sorted collection.
			// But a Tree doesn't properly handle having its
			// collection refreshed; for example, if the new
			// filter reduces the number of items, the Tree
			// can display blank renderers.
			// So instead we simply reset the dataProvider.
			owner.dataProvider = directoryEnumeration.collection;
			
			itemsChanged();

			extensionsChanged = false;
			enumerationModeChanged = false;
			filterFunctionChanged = false;
			nameCompareFunctionChanged = false;
			showHiddenChanged = false;
		}
		
		if (directoryChanged)
		{
			fill();
			
			var event:FileEvent = new FileEvent(FileEvent.DIRECTORY_CHANGE);
			event.file = directory;
			owner.dispatchEvent(event);

			directoryChanged = false;
		}
	}
	
	/**
	 *  Fills the list by enumerating the current directory
	 *  and setting the dataProvider.
	 */
	mx_internal function fill():void
	{
		setDataProvider(isComputer(directory) ?
						getRootDirectories() :
						directory.getDirectoryListing());
	}
	
	/**
	 *  @private
	 */
	public function styleChanged(styleProp:String):void
	{
		if (styleProp == "fileIcon" || styleProp == "directoryIcon")
			owner.invalidateList();
	}
	
	/**
	 *  @private
	 */
	mx_internal function setDirectory(value:File):void
	{
		_directory = value;
		directoryChanged = true;
		
		// Clear the now-stale contents of the list.
		// The list will repopulate after the new directory
		// is enumerated.
		owner.dataProvider = null;
		
		if (hierarchical)
			owner.dataDescriptor.reset();
		
		owner.invalidateProperties();

		// Trigger databindings.
		owner.dispatchEvent(new Event("directoryChanged"));
	}
	
	/**
	 *  @private
	 */
	mx_internal function setDataProvider(value:Array):void
	{
		directoryEnumeration.enumerationMode = enumerationMode;
		directoryEnumeration.extensions = extensions;
		directoryEnumeration.filterFunction = filterFunction;
		directoryEnumeration.nameCompareFunction = nameCompareFunction;
		directoryEnumeration.showHidden = showHidden;
		
		directoryEnumeration.source = value;
		
		owner.dataProvider = directoryEnumeration.collection;
		
		itemsChanged();
	}
	
	/**
	 *  @private
	 */
	public function itemToUID(data:Object):String
	{
		return data ? File(data).nativePath : "null";
	}
	
	/**
	 *  @private
	 */
	public function isComputer(f:File):Boolean
	{
		 if (Capabilities.os.substr(0, 3) =="Win") 
		 	return f.nativePath.substring(0, 6) == "root$:";
		 return f.nativePath == "/Computer";
	}
	
	/**
	 *  @private
	 */
	private function getRootDirectories():Array
	{
		var a:Array = [];
		
		for each (var f:File in File.getRootDirectories())
		{
			if (f.isDirectory)
				a.push(f);
		}
		
		return a;
	}
	
 	/**
	 *  @private
	 */
	public function fileIconFunction(item:File):Class
	{
		if (!showIcons)
			return null;
		
		return owner.getStyle(item.isDirectory ? "directoryIcon" : "fileIcon");
	}
	
	/**
	 *  @private
	 */
	public function fileLabelFunction(item:File,
									  column:DataGridColumn = null):String
	{
		if (isComputer(item))
		{
			return resourceManager.getString(
				"aircontrols", "computer");
		}
		
		var label:String = item.name;
		
		// The name of the / directory on Mac is the empty string.
		// In this case, display the nativePath, which will be "/".
		if (label == "")
			label = item.nativePath;
		
		if (!item.isDirectory && !showExtensions)
		{
			var index:int = label.lastIndexOf(".");
			if (index != -1)
				label = label.substring(0, index);
		}
		
		return label;
	}
	
	/**
	 *  @private
	 */
	public function findIndex(nativePath:String):int
	{
		if (!nativePath)
			return -1;
			
		if (fileSystemIsCaseInsensitive())
			nativePath = nativePath.toLowerCase();

		var value:* = nativePathToIndexMap[nativePath];
		return value === undefined ? -1 : int(value);
	}

	/**
	 *  @private
	 */
	public function findItem(nativePath:String):File
	{
		var index:int = findIndex(nativePath);
		if (index == -1)
			return null;
			
		return itemArray[index];
	}
	
	/**
	 *  @private
	 *  This method is called whenever something happens
	 *  that affects which items are displayed by the
	 *  control, or the order in which they are displayed.
	 */
	mx_internal function itemsChanged():void
	{
		// These two data structures are now invalid, so free them.
		// They will be rebuilt the next time they are needed.
		_itemArray = null;
		_nativePathToIndexMap = null;
	}

	/**
	 *  @private
	 */
	private function rebuildEnumerationInfo():void
	{
		_itemArray = [];
		_nativePathToIndexMap = {};
		
		enumerateItems(addItemToEnumerationInfo);
	}
	
	/**
	 *  @private
	 */
	private function addItemToEnumerationInfo(index:int, item:File):void
	{
		var nativePath:String = item.nativePath;
		
		if (fileSystemIsCaseInsensitive())
			nativePath = nativePath.toLowerCase();
		
		_itemArray.push(item);
		_nativePathToIndexMap[nativePath] = index;
	}
	
	/**
	 *  @private
	 */
	private function enumerateItems(itemCallback:Function):int
	{
		return enumerate(ArrayCollection(owner.dataProvider),
						 0, itemCallback);
	}
	
	/**
	 *  @private
	 */
	private function enumerate(items:ArrayCollection, index:int,
							   itemCallback:Function):int
	{
		var n:int = items.length;
		for (var i:int = 0; i < n; i++)
		{
			var item:File = File(items.getItemAt(i));
			itemCallback(index, item);
			index++;
			
			if (hierarchical && item.isDirectory && owner.isItemOpen(item))
			{
				var childItems:ArrayCollection =
					owner.dataDescriptor.getChildren(item);
					
				index = enumerate(childItems, index, itemCallback);
			}
		}
		return index;
	}

	/**
	 *  @private
	 */
	public function navigateDown():void
	{
		if (canNavigateDown)
			navigateTo(File(owner.selectedItem));
	}

	/**
	 *  @private
	 */
	public function navigateUp():void
	{
		if (canNavigateUp)
			navigateTo(directory.parent ? directory.parent : COMPUTER);
	}
	
	/**
	 *  @private
	 */
	public function navigateBack(index:int = 0):void
	{
		if (canNavigateBack)
			navigateBy(-1 - index);
	}

	/**
	 *  @private
	 */
	public function navigateForward(index:int = 0):void
	{
		if (canNavigateForward)
			navigateBy(1 + index)
	}
	
	/**
	 *  @private
	 */
	private function navigateBy(n:int):void
	{
		historyIndex += n;
		
		if (historyIndex < 0)
			historyIndex = 0;
		else if (historyIndex > history.length - 1)
			historyIndex = history.length - 1;
		
		setDirectory(history[historyIndex]);
		
		owner.dispatchEvent(new Event("historyChanged"));
	}

	/**
	 *  @private
	 */
	public function navigateTo(directory:File):void
	{
		setDirectory(directory);		
		
		pushHistory(directory);
	}

	/**
	 *  @private
	 */
	public function refresh():void
	{
		var openPaths:Array /* of String */
		var selectedPaths:Array /* of String */;
		var firstVisiblePath:String;
		var oldHorizontalScrollPosition:int;

		if (hierarchical)
			openPaths = getOpenPaths();
		selectedPaths = getSelectedPaths();
		firstVisiblePath = getFirstVisiblePath();
		oldHorizontalScrollPosition = owner.horizontalScrollPosition;
			
		fill();
		
		// Tree must be revalidated after its dataProvider
		// changes for expandItem() to work.
		if (hierarchical)
			owner.validateNow();
			
		if (hierarchical)
			setOpenPaths(openPaths);
		setSelectedPaths(selectedPaths);
		if (setFirstVisiblePath(firstVisiblePath))
			owner.horizontalScrollPosition = oldHorizontalScrollPosition;
	}
	
	/**
	 *  @private
	 */
	private function getOpenPaths():Array /* of String */
	{
		var openPaths:Array /* of String */ = [];
		var n:int = owner.openItems.length;
		for (var i:int = 0; i < n; i++)
		{
			openPaths.push(File(owner.openItems[i]).nativePath);
		}
		return openPaths;
	}
	
	/**
	 *  @private
	 *  Returns an Array of nativePath Strings for the selected items.
	 *  This method is called by refresh() before repopulating the control.
	 */
	private function getSelectedPaths():Array /* of String */
	{
		var selectedPaths:Array /* of String */ = [];
		var n:int = owner.selectedItems.length;
		for (var i:int = 0; i < n; i++)
		{
			selectedPaths.push(File(owner.selectedItems[i]).nativePath);
		}
		return selectedPaths;
	}
	
	/**
	 *  @private
	 *  Returns the nativePath of the first visible item.
	 *  This method is called by refresh() before repopulating the control.
	 */
	private function getFirstVisiblePath():String
	{
		if (owner.dataProvider == null || owner.dataProvider.length == 0)
			return null;
			
		var index:int = owner.verticalScrollPosition;
		var item:File = itemArray[index];
		return item ? item.nativePath : null;
	}
	
	/**
	 *  @private
	 */
	private function setOpenPaths(openPaths:Array /* of String */):void
	{
		var n:int = openPaths.length;
		for (var i:int = 0; i < n; i++)
		{
			owner.openSubdirectory(openPaths[i]);
		}
	}
	
	/**
	 *  @private
	 *  Selects items whose nativePaths are in the specified Array.
	 *  This method is called by refresh() after repopulating the control.
	 */
	private function setSelectedPaths(selectedPaths:Array /* of String */):void
	{
		var indices:Array /* of int */ = [];
		
		var n:int = selectedPaths.length;
		for (var i:int = 0; i < n; i++)
		{
			var path:String = selectedPaths[i];
			var index:int = findIndex(path);
			if (index != -1)
				indices.push(index);
		}
		
		owner.selectedIndices = indices;
	}
	
	/**
	 *  @private
	 *  Scrolls the list to the item with the specified nativePath.
	 *  This method is by refresh() after repopulating the control.
	 */
	private function setFirstVisiblePath(path:String):Boolean
	{
		if (path == null)
			return false;
			
		var index:int = findIndex(path);
		if (index == -1)
			return false;
		
		owner.verticalScrollPosition = index;
		return true;
	}
	
	/**
	 *  @private
	 */
	public function clear():void
	{
		owner.dataProvider = null;
		
		itemsChanged();
	}
	
	/**
	 *  @private
	 */
	public function resetHistory(directory:File):void
	{
		history = [ directory ];
		historyIndex = 0;
		
		owner.dispatchEvent(new Event("historyChanged"));
	}
	
	/**
	 *  @private
	 */
	private function pushHistory(directory:File):void
	{
		historyIndex++;
		history.splice(historyIndex);
		history.push(directory);
		
		owner.dispatchEvent(new Event("historyChanged"));
	}
	
	/**
	 *  @private
	 *  Returns an Array of File objects
	 *  representing the path to the specified directory.
	 *  The first File represents a root directory.
	 *  The last File represents the specified file's parent directory.
	 */
	public function getParentChain(file:File):Array
	{
		if (!file)
			return [];
			
		var a:Array = [];
		
		for (var f:File = file; f != null; f = f.parent)
		{
			a.unshift(f);
		}
				
		return a;
	}
	
	/**
	 *  @private
	 *  Dispatches a cancelable "directoryChanging" event
	 *  and returns true if it wasn't canceled.
	 */
	mx_internal function dispatchDirectoryChangingEvent(newDirectory:File):Boolean
	{
		var event:FileEvent =
			new FileEvent(FileEvent.DIRECTORY_CHANGING, false, true);
		event.file = newDirectory;
		owner.dispatchEvent(event);
		
		return !event.isDefaultPrevented();
	}
	
	/**
	 *  @private
	 *  Dispatches a "fileChoose" event.
	 */
	mx_internal function dispatchFileChooseEvent(file:File):void
	{
		var event:FileEvent = new FileEvent(FileEvent.FILE_CHOOSE);
		event.file = file;
		owner.dispatchEvent(event);
	}

 	/**
	 *  @private
	 */
	private function getBackDirectory():File
	{
		return historyIndex == 0 ?
			   null :
			   history[historyIndex - 1];
	}

	/**
	 *  @private
	 */
	private function getForwardDirectory():File
	{
		return historyIndex == history.length - 1 ?
			   null :
			   history[historyIndex + 1];
	}

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function updateCompleteHandler(event:FlexEvent):void
	{
		if (pendingOpenPaths != null)
		{
			setOpenPaths(pendingOpenPaths);
			
			pendingOpenPaths = null;
		}
		
		if (pendingSelectedPaths != null)
		{
			setSelectedPaths(pendingSelectedPaths);
			
			pendingSelectedPaths = null;
		}
	}
	
	/**
	 *  @private
	 */
   	public function itemDoubleClickHandler(event:ListEvent):void
	{
		var selectedFile:File = File(owner.selectedItem);
		
		if (selectedFile.isDirectory)
		{
			if (dispatchDirectoryChangingEvent(selectedFile))
				navigateDown();
		}
		else
		{
			dispatchFileChooseEvent(selectedFile);
		}	
	}
	
	/**
	 *  @private
	 */
	public function handleKeyDown(event:KeyboardEvent):Boolean
	{
		switch (event.keyCode)
		{
			case Keyboard.ENTER:
			{
				var selectedFile:File = File(owner.selectedItem);
		
				if (canNavigateDown &&
					dispatchDirectoryChangingEvent(selectedFile))
				{
					navigateDown();
				}
				else
				{
					dispatchFileChooseEvent(selectedFile);
				}
				return true;
			}
			
			case Keyboard.BACKSPACE:
			{
				if (canNavigateUp &&
					dispatchDirectoryChangingEvent(directory.parent))
				{
					navigateUp();
				}
				return true;
			}
		}
	
		return false;
	}
}

}
