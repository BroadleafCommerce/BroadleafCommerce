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

package mx.controls
{

import flash.events.KeyboardEvent;
import flash.filesystem.File;

import mx.controls.dataGridClasses.DataGridColumn;
import mx.controls.fileSystemClasses.FileSystemControlHelper;
import mx.core.ClassFactory;
import mx.core.IUITextField;
import mx.core.ScrollPolicy;
import mx.core.mx_internal;
import mx.events.ListEvent;
import mx.formatters.DateFormatter;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the selected directory displayed by this control
 *  changes for any reason.
 *
 *  @eventType mx.events.FileEvent.DIRECTORY_CHANGE
 */
[Event(name="directoryChange", type="mx.events.FileEvent")]

/**
 *  Dispatched when the user tries to change
 *  the directory displayed by this control.
 *
 *  <p>The user can try to change the directory
 *  by double-clicking a subdirectory,
 *  by pressing Enter or Ctrl-Down when a subdirectory is selected,
 *  by pressing Ctrl-Up when the control isn't displaying
 *  the COMPUTER directory,
 *  by pressing Ctrl-Left when there is a previous directory
 *  in the history list to navigate back to,
 *  or by pressing Ctrl-Right when there is a next directory
 *  in the history list to navigate forward to.</p>
 *
 *  <p>This event is cancelable.
 *  If you call <code>event.preventDefault()</code>,
 *  the directory is not changed.</p>
 *
 *  <p>After the <code>directory</code> property has changed
 *  and the <code>dataProvider</code> contains File instances
 *  for the items in the new directory,
 *  the <code>directoryChange</code> event is dispatched.</p>
 *
 *  @eventType mx.events.FileEvent.DIRECTORY_OPENING
 */
[Event(name="directoryChanging", type="mx.events.FileEvent")]

/**
 *  Dispatched when the user chooses a file by double-clicking it
 *  or by selecting it and pressing Enter.
 *
 *  @eventType mx.events.FileEvent.FILE_CHOOSE
 */
[Event(name="fileChoose", type="mx.events.FileEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Specifies the icon that indicates a directory.
 *  The default icon is located in the Assets.swf file.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>directoryIcon="&#64;Embed(source='directoryIcon.jpg');"</code>
 *
 *  @default TreeNodeIcon
 */
[Style(name="directoryIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Specifies the icon that indicates a file.
 *  The default icon is located in the Assets.swf file.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>fileIcon="&#64;Embed(source='fileIcon.jpg');"</code>
 *
 *  @default TreeNodeIcon
 */
[Style(name="fileIcon", type="Class", format="EmbeddedFile", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("FileSystemDataGrid.png")]

[ResourceBundle("aircontrols")]

/**
 *  The FileSystemDataGrid control lets you display the contents of a
 *  single file system directory in a data grid format.
 *
 *  <p>The information displayed for each item consists of its name
 *  (with optional generic icon), type, size, creation date,
 *  and modification date.
 *  To do this, FileSystemDataGrid automatically creates five columns
 *  (DataGridColumn instances) -- <code>nameColumn</code>, <code>typeColumn</code>,
 *  <code>sizeColumn</code>, <code>creationDateColumn</code>,
 *  and <code>modificationDateColumn</code> -- and sets
 *  the <code>columns</code> property to an array of these five instances.
 *  Each column instance is automatically configured to have an
 *  appropriate <code>labelFunction</code>,
 *  <code>sortCompareFunction</code>, etc.
 *  If you don't want all five columns, or if you want to change the
 *  order, reset the <code>columns</code> property.
 *  If you want to customize a column, such as by changing its
 *  <code>labelFunction</code>, simply reassign that property
 *  on the appropriate column object.</p>
 *
 *  <p>To change the displayed data, rather than using the <code>dataProvider</code> property,
 *  you set the <code>directory</code> property.
 *  The control then automatically populates the <code>dataProvider</code>
 *  property by enumerating the contents of that directory.
 *  You should not set the <code>dataProvider</code> yourself.</p>
 *
 *  <p>You set the <code>directory</code> property to a File instance,
 *  as the following example shows:</p>
 *  <pre>&lt;mx:FileSystemDataGrid directory="{File.desktopDirectory}"/&gt;</pre>
 *
 *  <p>You can set the <code>enumerationMode</code> property to specify
 *  whether to show files, subdirectories, or both.
 *  There are three ways to show both: directories first,
 *  files first, or intermixed.</p>
 *
 *  <p>You can set the <code>extensions</code> property
 *  to filter the displayed items so that only files
 *  with the specified extensions appear.
 *  The <code>showHidden</code> property determines whether the control
 *  displays files and subdirectories that the operating system
 *  normally hides.
 *  You can specify an additional <code>filterFunction</code>
 *  to perform custom filtering, and a <code>nameCompareFunction</code>
 *  to perform custom sorting.</p>
 *
 *  <p>Because AIR does not support file system notifications,
 *  this control does not automatically refresh if a file or
 *  subdirectory is created, deleted, moved, or renamed;
 *  in other words, it can display an out-of-date view of the file system.
 *  However, you can call <code>refresh()</code> to re-enumerate
 *  the current <code>directory</code>.
 *  You could, for example, choose to do this when you have
 *  performed a file operation that you know causes the control's
 *  view to become out-of-date, or when the user deactivates
 *  and reactivates your application.</p>
 *
 *  <p>You can use the <code>showIcons</code> property
 *  to show or hide icons, and the <code>showExtensions</code>
 *  property to show or hide file extensions.</p>
 *
 *  <p>The control provides two methods, <code>findItem()</code>
 *  and <code>findIndex()</code>, which you can use to search the
 *  displayed files and subdirectories to find the one
 *  with a specified <code>nativePath</code>.</p>
 *
 *  <p>Two properties, <code>selectedPath</code>
 *  and <code>selectedPaths</code>, work similarly
 *  to <code>selectedItem</code> and <code>selectedItems</code>
 *  or <code>selectedIndex</code> and <code>selectedIndices</code>,
 *  but let you specify the selection via <code>nativePath</code>
 *  strings.
 *  These are very useful if you need to display a directory
 *  with particular items preselected, since in this case
 *  you don't yet have the File items that the control will create
 *  when it enumerates the directory, and you don't know what
 *  their indices will be.</p>
 *
 *  <p>The control allows the user to navigate to other directories
 *  using the mouse or keyboard.
 *  The user can try to change the directory
 *  by double-clicking a subdirectory,
 *  by pressing Enter or Ctrl-Down when a subdirectory is selected,
 *  by pressing Ctrl-Up when the control isn't displaying
 *  the COMPUTER directory, by pressing Ctrl-Left when there is
 *  a "previous" directory to navigate back to, or by pressing Ctrl-Right
 *  when there is a "next" directory to navigate forward to.
 *  If the user attempts to change the directory being displayed,
 *  the control dispatches a cancelable <code>directoryChanging</code> event.
 *  If you don't cancel this event by calling
 *  <code>event.preventDefault()</code>, the control displays the
 *  contents of the new directory and the <code>directory</code>
 *  property changes.
 *  Whenever the <code>directory</code> property changes, for any reason,
 *  the controls dispatches a <code>directoryChange</code> event
 *  to let you know.</p>
 *
 *  <p>In order to support "Up" and "Down" controls, the FileSystemList
 *  has <code>canNavigateUp</code> and <code>canNavigateDown</code>
 *  properties and <code>navigateUp()</code> and <code>navigateDown()</code>
 *  methods. There is also a <code>navigateTo()</code> for navigating
 *  to an arbitrary directory.</p>
 *
 *  <p>The control keeps track of the directories to which the user
 *  has navigated, in order to make it easy for you to support
 *  "Back" and "Forward" controls.
 *  For more information, see the <code>backHistory</code>,
 *  <code>forwardHistory</code>, <code>canNavigateBack</code>,
 *  and <code>canNavigateForward</code> properties, and the
 *  <code>navigateBack()</code> and <code>navigateForward()</code> methods.</p>
 *
 *  <p>Note: The icons displayed for each item are generic file and directory
 *  icons which you can set using the <code>fileIcon</code>
 *  and <code>directoryIcon</code> styles.
 *  Flex's list-based controls currently support displaying
 *  only embedded icons, not icons read at runtime.
 *  Therefore the actual file system icons displayed in the operating system
 *  are not displayed in a FileSystemDataGrid, even though they are
 *  accessible in AIR via the <code>icon</code> property of a File.</p>
 * 
 *  @mxml
 *
 *  <p>The <code>&lt;mx:FileSystemDataGrid&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:FileSystemDataGrid
 *    <strong>Properties</strong>
 *    dateFormatString=""
 *    directory="<i>null</i>"
 *    enumerationMode="directoriesFirst"
 *    extensions="<i>null</i>"
 *    filterFunction="<i>null</i>"
 *    nameCompareFunction="<i>null</i>"
 *    selectedPath="<i>null</i>"
 *    selectedPaths="<i>null</i>"
 *    showExtensions="true"
 *    showHidden="false"
 *    showIcons="true"
 * 
 *    <strong>Styles</strong>
 *    directoryIcon="<i>TreeNodeIcon</i>"
 *    fileIcon="<i>TreeNodeIcon</i>"
 * 
 *    <strong>Events</strong>
 *    directoryChange="<i>No default</i>"
 *    directoryChanging="<i>No default</i>"
 *    fileChoose="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @see flash.filesystem.File
 * 
 *  @playerversion AIR 1.1
 */
public class FileSystemDataGrid extends DataGrid
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @copy mx.controls.FileSystemList#COMPUTER
     */
    public static const COMPUTER:File = FileSystemControlHelper.COMPUTER;

   //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function FileSystemDataGrid()
    {
        super();

        helper = new FileSystemControlHelper(this, false);

        doubleClickEnabled = true;
        horizontalScrollPolicy = ScrollPolicy.AUTO;
        iconFunction = helper.fileIconFunction;

        addEventListener(ListEvent.ITEM_DOUBLE_CLICK, itemDoubleClickHandler);

        dateFormatter.formatString = dateFormatString;

        // Set the initial dataProvider by enumerating the root directories.
        directory = COMPUTER;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  An undocumented class that implements functionality
     *  shared by various file system components.
     */
    mx_internal var helper:FileSystemControlHelper;

    /**
     *  The DateFormatter object used to format the dates
     *  in the Created and Modified columns.
     */
    mx_internal var dateFormatter:DateFormatter = new DateFormatter();

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  backHistory
    //----------------------------------

    [Bindable("historyChanged")]

    /**
     *  An Array of File objects representing directories
     *  to which the user can navigate backward.
     *
     *  <p>The first item in this Array is the next directory backward
     *  in the history list.
     *  The last item is the directory furthest backward
     *  in the history list.</p>
     *
     *  <p>This Array may contain a <code>null</code> item, which represents
     *  the non-existent directory whose contents are root directories
     *  such as C:\ and D:\ on Microsoft Windows.</p>
     *
     *  <p>The following example shows how to use this property
     *  along with the FileSystemHistoryButton control
     *  to implement a back button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Back"
     *     enabled="{fileSystemViewer.canNavigateBack}"
     *     dataProvider="{fileSystemViewer.backHistory}"
     *     click="fileSystemViewer.navigateBack();"
     *     itemClick="fileSystemViewer.navigateBack(event.index);"/&gt;</pre>
     *
     *  @default []
	 *
	 *  @see #canNavigateBack
	 *  @see #navigateBack()
	 *  @see mx.controls.FileSystemHistoryButton
     */
    public function get backHistory():Array
    {
        return helper.backHistory;
    }

    //----------------------------------
    //  canNavigateBack
    //----------------------------------

    [Bindable("historyChanged")]

    /**
     *  A flag which is <code>true</code> if there is at least one directory
     *  in the history list to which the user can navigate backward.
     *
     *  <p>The following example shows how to use this property
     *  along with the FileSystemHistoryButton control
     *  to implement a back button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Back"
     *      enabled="{fileSystemViewer.canNavigateBack}"
     *      dataProvider="{fileSystemViewer.backHistory}"
     *      click="fileSystemViewer.navigateBack();"
     *      itemClick="fileSystemViewer.navigateBack(event.index);"/&gt;</pre>
     *
     *  @default false
	 *
	 *  @see #backHistory
	 *  @see #navigateBack()
     */
    public function get canNavigateBack():Boolean
    {
        return helper.canNavigateBack;
    }

    //----------------------------------
    //  canNavigateDown
    //----------------------------------

    [Bindable("change")]
    [Bindable("directoryChanged")]

    /**
     *  A flag which is <code>true</code> if the user can navigate down
     *  into a selected directory.
     *  This flag is <code>false</code> when there is no selected item
     *  or when the selected item is a file rather than a directory.
     *
     *  <p>The following example shows how to use this property
     *  along with the Button control:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:Button label="Open"
     *      enabled="{fileSystemViewer.canNavigateDown}"
     *      click="fileSystemViewer.navigateDown();"/&gt;</pre>
     *
     *  @default false
	 *
	 *  @see #navigateDown()
     */
    public function get canNavigateDown():Boolean
    {
        return helper.canNavigateDown;
    }

    //----------------------------------
    //  canNavigateForward
    //----------------------------------

    [Bindable("historyChanged")]

    /**
     *  A flag which is <code>true</code> if there is at least one directory
     *  in the history list to which the user can navigate forward.
     *
     *  <p>The following example shows how to use this property
     *  along with the FileSystemHistoryButton control
     *  to implement a forward button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Forward"
     *      enabled="{fileSystemViewer.canNavigateForward}"
     *      dataProvider="{fileSystemViewer.forwardHistory}"
     *      click="fileSystemViewer.navigateForward();"
     *      itemClick="fileSystemViewer.navigateForward(event.index);"/&gt;</pre>
     *
     *  @default false
	 *
	 *  @see #forwardHistory
	 *  @see #navigateForward()
     */
    public function get canNavigateForward():Boolean
    {
        return helper.canNavigateForward;
    }

    //----------------------------------
    //  canNavigateUp
    //----------------------------------

    [Bindable("directoryChanged")]

    /**
     *  A flag which is <code>true</code> if the user can navigate up
     *  to a parent directory.
     *  This flag is only <code>false</code> when this control is
     *  displaying the root directories such as C:\ and D:\ on Microsoft Windows.
     *  (This is the case in which the <code>directory</code>
     *  property is <code>COMPUTER</code>.)
     *
     *  <p>The following example shows how to use this property
     *  along with the Button control:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:Button label="Up"
     *      enabled="{fileSystemViewer.canNavigateUp}"
     *      click="fileSystemViewer.navigateUp();"/&gt;</pre>
     *
     *  @default false
	 *
	 *  @see #navigateUp()
     */
    public function get canNavigateUp():Boolean
    {
        return helper.canNavigateUp;
    }

    //----------------------------------
    //  creationDateColumn
    //----------------------------------

    /**
     *  The DataGridColumn representing the Created column.
     *  The FileSystemDataGrid control automatically creates this column.
     *
     *  <p>You can set properties such as
     *  <code>creationDateColumn.width</code> to customize this column.
     *  To remove this column entirely, or to change the column order,
     *  set the <code>columns</code> property to an array such as
     *  <code>[ nameColumn, modificationDateColumn, sizeColumn ]</code>.</p>
     */
    public var creationDateColumn:DataGridColumn;

    //----------------------------------
    //  dateFormatString
    //----------------------------------

    /**
     *  @private
     *  Storage for the dateFormatString property.
     */
    private var _dateFormatString:String;

    /**
	 *  @private
	 */
	private var dateFormatStringOverride:String;
	
    /**
     *  A String that determines how dates in the Created and Modified
     *  columns are formatted.
     *  Setting this property sets the <code>formatString</code>
     *  of an internal DateFormatter that this control creates.
     *
     *  @see mx.formatters.DateFormatter#formatString
     */
    public function get dateFormatString():String
    {
        return _dateFormatString;
    }

    /**
     *  @private
     */
    public function set dateFormatString(value:String):void
    {
		dateFormatStringOverride = value;

		_dateFormatString = value != null ?
					   		value :
					   		resourceManager.getString(
								"aircontrols",
								"fileSystemDataGrid_dateFormatString");

        dateFormatter.formatString = _dateFormatString != null ?
        							 _dateFormatString :
        							 "";

        invalidateList();
    }

    //----------------------------------
    //  directory
    //----------------------------------

    [Bindable("directoryChanged")]

    /**
     *  @copy mx.controls.FileSystemList#directory
     */
    public function get directory():File
    {
        return helper.directory;
    }

    /**
     *  @private
     */
    public function set directory(value:File):void
    {
        helper.directory = value;
    }

    //----------------------------------
    //  enumerationMode
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#enumerationMode
     *
     *  @default FileSystemEnumerationMode.DIRECTORIES_FIRST
     *
     *  @see mx.controls.FileSystemEnumerationMode
     */
    public function get enumerationMode():String
    {
        return helper.enumerationMode;
    }

    /**
     *  @private
     */
    public function set enumerationMode(value:String):void
    {
        helper.enumerationMode = value;
    }

    //----------------------------------
    //  extensions
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#extensions
     *
     *  @default null
     */
    public function get extensions():Array /* of String */
    {
        return helper.extensions;
    }

    /**
     *  @private
     */
    public function set extensions(value:Array /* of String */):void
    {
        helper.extensions = value;
    }

    //----------------------------------
    //  filterFunction
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#filterFunction
     *
     *  @default null
     */
    public function get filterFunction():Function
    {
        return helper.filterFunction;
    }

    /**
     *  @private
     */
    public function set filterFunction(value:Function):void
    {
        helper.filterFunction = value;
    }

    //----------------------------------
    //  forwardHistory
    //----------------------------------

    [Bindable("historyChanged")]

    /**
     *  An Array of File objects representing directories
     *  to which the user can navigate forward.
     *
     *  <p>The first item in this Array is the next directory forward
     *  in the history list.
     *  The last item is the directory furthest forward
     *  in the history list.</p>
     *
     *  <p>This Array may contain a <code>null</code> item, which represents
     *  the non-existent directory whose contents are root directories
     *  such as C:\ and D:\ on Windows.</p>
     *
     *  <p>The following example shows how to use this property
     *  along with the FileSystemHistoryButton control to implement a forward button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Forward"
     *      enabled="{fileSystemViewer.canNavigateForward}"
     *      dataProvider="{fileSystemViewer.forwardHistory}"
     *      click="fileSystemViewer.navigateForward();"
     *      itemClick="fileSystemViewer.navigateForward(event.index);"/&gt;</pre>
     *
     *  @default []
	 *
	 * @see mx.controls.FileSystemHistoryButton
     */
    public function get forwardHistory():Array
    {
        return helper.forwardHistory;
    }

    //----------------------------------
    //  modificationDateColumn
    //----------------------------------

    /**
     *  The DataGridColumn representing the Modified column.
     *  The FileSystemDataGrid control automatically creates this column.
     *
     *  <p>You can set properties such as
     *  <code>modificationDateColumn.width</code> to customize this column.
     *  To remove this column entirely, or to change the column order,
     *  set the <code>columns</code> property to an array such as
     *  <code>[ nameColumn, modificationDateColumn, sizeColumn ]</code>.</p>
     */
    public var modificationDateColumn:DataGridColumn;

    //----------------------------------
    //  nameColumn
    //----------------------------------

    /**
     *  The DataGridColumn representing the Name column.
     *  The FileSystemDataGrid control automatically creates this column.
     *
     *  <p>You can set properties such as <code>nameColumn.width</code>
     *  to customize this column.
     *  To remove this column entirely, or to change the column order,
     *  set the <code>columns</code> property to an array such as
     *  <code>[ nameColumn, modificationDateColumn, sizeColumn ]</code>.</p>
     */
    public var nameColumn:DataGridColumn;

    //----------------------------------
    //  nameCompareFunction
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#nameCompareFunction
     *
     *  @default null
     */
    public function get nameCompareFunction():Function
    {
        return helper.nameCompareFunction;
    }

    /**
     *  @private
     */
    public function set nameCompareFunction(value:Function):void
    {
        helper.nameCompareFunction = value;
    }

    //----------------------------------
    //  selectedPath
    //----------------------------------

    [Bindable("change")]
    [Bindable("directoryChanged")]
	
    /**
     *  @copy mx.controls.FileSystemList#selectedPath
     *
     *  @default null
     *
     *  @see mx.controls.listClasses.ListBase#selectedIndex
     *  @see mx.controls.listClasses.ListBase#selectedItem
     */
    public function get selectedPath():String
    {
        return helper.selectedPath;
    }

    /**
     *  @private
     */
    public function set selectedPath(value:String):void
    {
        helper.selectedPath = value;
    }

    //----------------------------------
    //  selectedPaths
    //----------------------------------

    [Bindable("change")]
    [Bindable("directoryChanged")]

    /**
     *  @copy mx.controls.FileSystemList#selectedPaths
     *
     *  @default []
     *
     *  @see mx.controls.listClasses.ListBase#selectedIndex
     *  @see mx.controls.listClasses.ListBase#selectedItem
     */
    public function get selectedPaths():Array /* of String */
    {
        return helper.selectedPaths;
    }

    /**
     *  @private
     */
    public function set selectedPaths(value:Array /* of String */):void
    {
        helper.selectedPaths = value;
    }

    //----------------------------------
    //  showExtensions
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#showExtensions
     */
    public function get showExtensions():Boolean
    {
        return helper.showExtensions;
    }

    /**
     *  @private
     */
    public function set showExtensions(value:Boolean):void
    {
        helper.showExtensions = value;
    }

    //----------------------------------
    //  showHidden
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#showHidden
     */
    public function get showHidden():Boolean
    {
        return helper.showHidden;
    }

    /**
     *  @private
     */
    public function set showHidden(value:Boolean):void
    {
        helper.showHidden = value;
    }

    //----------------------------------
    //  showIcons
    //----------------------------------

    /**
     *  @copy mx.controls.FileSystemList#showIcons
     */
    public function get showIcons():Boolean
    {
        return helper.showIcons;
    }

    /**
     *  @private
     */
    public function set showIcons(value:Boolean):void
    {
        helper.showIcons = value;
    }

    //----------------------------------
    //  sizeColumn
    //----------------------------------

    /**
     *  The DataGridColumn representing the Size column.
     *  The FileSystemDataGrid control automatically creates this column.
     *
     *  <p>You can set properties such as <code>sizeColumn.width</code>
     *  to customize this column.
     *  To remove this column entirely, or to change the column order,
     *  set the <code>columns</code> property to an array such as
     *  <code>[ nameColumn, modificationDateColumn, sizeColumn ]</code>.</p>
     */
    public var sizeColumn:DataGridColumn;

    //----------------------------------
    //  sizeDisplayMode
    //----------------------------------

    /**
     *  @private
     *  Storage for the sizeDisplayMode property.
     */
    private var _sizeDisplayMode:String = FileSystemSizeDisplayMode.KILOBYTES;

    /**
     *  A String specifying whether the Size column displays file sizes
     *  in bytes or rounded up to the nearest kilobyte,
     *  where a kilobyte is 1024 bytes.
     *  The possible values are specified
     *  by the FileSystemSizeDisplayMode class.
     *
     *  @see mx.controls.FileSystemSizeDisplayMode
     */
    public function get sizeDisplayMode():String
    {
        return _sizeDisplayMode;
    }

    /**
     *  @private
     */
    public function set sizeDisplayMode(value:String):void
    {
        _sizeDisplayMode = value;

        invalidateList();
    }

    //----------------------------------
    //  typeColumn
    //----------------------------------

    /**
     *  The DataGridColumn representing the Type column.
     *  The FileSystemDataGrid control automatically creates this column.
     *
     *  <p>You can set properties such as <code>typeColumn.width</code>
     *  to customize this column.
     *  To remove this column entirely, or to change the column order,
     *  set the <code>columns</code> property to an array such as
     *  <code>[ nameColumn, modificationDateColumn, sizeColumn ]</code>.</p>
     */
    public var typeColumn:DataGridColumn;

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function childrenCreated():void
    {
        super.childrenCreated();

        var measuredText:String;

        nameColumn = new CustomDataGridColumn();
        nameColumn.headerText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_nameColumnHeader");
        nameColumn.itemRenderer = new ClassFactory(NameColumnRenderer);
        nameColumn.labelFunction = helper.fileLabelFunction;
        nameColumn.sortCompareFunction = nameSortCompareFunction;
        measuredText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_nameColumnMeasuredText");
        nameColumn.width = determineWidthToDisplay(measuredText);

        typeColumn = new CustomDataGridColumn();
        typeColumn.headerText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_typeColumnHeader");
        typeColumn.labelFunction = typeLabelFunction;
        typeColumn.sortCompareFunction = typeSortCompareFunction;
        measuredText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_typeColumnMeasuredText");
        typeColumn.width = determineWidthToDisplay(measuredText);

        sizeColumn = new CustomDataGridColumn();
        sizeColumn.headerText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_sizeColumnHeader");
        sizeColumn.labelFunction = sizeLabelFunction;
        sizeColumn.public::setStyle("textAlign", "right");
        sizeColumn.sortCompareFunction = sizeSortCompareFunction;
        measuredText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_sizeColumnMeasuredText");
        sizeColumn.width = determineWidthToDisplay(measuredText);

        creationDateColumn = new CustomDataGridColumn();
        creationDateColumn.headerText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_creationDateColumnHeader");
        creationDateColumn.labelFunction = creationDateLabelFunction;
        measuredText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_creationDateColumnMeasuredText");
        creationDateColumn.width = determineWidthToDisplay(measuredText);

        modificationDateColumn = new CustomDataGridColumn();
        modificationDateColumn.headerText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_modificationDateColumnHeader");
        modificationDateColumn.labelFunction = modificationDateLabelFunction;
        measuredText = resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_modificationDateColumnMeasuredText");
        modificationDateColumn.width = determineWidthToDisplay(measuredText);

        // If DataGridColumns haven't already been defined,
        // use the five ones we just created.
        if (!columns || columns.length == 0)
        {
            columns = [ nameColumn, typeColumn, sizeColumn,
                        creationDateColumn, modificationDateColumn ];
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        // Call this before the supermethod
        // because it sets the dataProvider superproperty.
        helper.commitProperties();

        super.commitProperties();
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        measuredWidth += 16;
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);

        helper.styleChanged(styleProp);
    }

    /**
     *  @private
     */
    override protected function resourcesChanged():void
    {
        super.resourcesChanged();

        var measuredText:String;

        if (nameColumn)
        {
 	        nameColumn.headerText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_nameColumnHeader");
	        measuredText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_nameColumnMeasuredText");
	        nameColumn.width = determineWidthToDisplay(measuredText);
        }

        if (typeColumn)
        {
	        typeColumn.headerText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_typeColumnHeader");
	        measuredText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_typeColumnMeasuredText");
	        typeColumn.width = determineWidthToDisplay(measuredText);
        }

        if (sizeColumn)
        {
	        sizeColumn.headerText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_sizeColumnHeader");
	        measuredText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_sizeColumnMeasuredText");
	        sizeColumn.width = determineWidthToDisplay(measuredText);
        }

        if (creationDateColumn)
        {
	        creationDateColumn.headerText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_creationDateColumnHeader");
	        measuredText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_creationDateColumnMeasuredText");
	        creationDateColumn.width = determineWidthToDisplay(measuredText);
        }

        if (modificationDateColumn)
        {
	        modificationDateColumn.headerText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_modificationDateColumnHeader");
	        measuredText = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_modificationDateColumnMeasuredText");
	        modificationDateColumn.width = determineWidthToDisplay(measuredText);
        }

		dateFormatString = dateFormatStringOverride;

		invalidateList();
		invalidateSize();
    }

    /**
     *  @private
     */
    override protected function itemToUID(data:Object):String
    {
    	return helper.itemToUID(data);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @copy mx.controls.FileSystemList#findIndex()
     */
    public function findIndex(nativePath:String):int
    {
        return helper.findIndex(nativePath);
    }

    /**
     *  @copy mx.controls.FileSystemList#findItem()
     */
    public function findItem(nativePath:String):File
    {
        return helper.findItem(nativePath);
    }

    /**
     *  Changes this control to display the contents
     *  of the selected subdirectory.
     *
     *  <p>If a subdirectory is not selected, this method does nothing.</p>
     *
     *  <p>When this method returns, the <code>directory</code> property
     *  contains the File instance for the new directory.
     *  The <code>dataProvider</code> property is temporarily
     *  <code>null</code> until the new directory has been enumerated.
     *  After the enumeration, the <code>dataProvider</code> property
     *  contains an ArrayCollection of File instances
     *  for the new directory's contents.</p>
     *
     *  <p>The following example shows how to use this method
     *  along with the Button control to create an open button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:Button label="Open"
     *      enabled="{fileSystemViewer.canNavigateDown}"
     *      click="fileSystemViewer.navigateDown();"/&gt;</pre>
	 *
	 *  @see #canNavigateDown
     */
    public function navigateDown():void
    {
        helper.navigateDown();
    }

    /**
     *  Changes this control to display the contents of the next directory
     *  up in the hierarchy.
     *
     *  <p>If this control is currently displaying root directories
     *  (such as C: and D: on Microsoft Windows), this method does nothing.</p>
     *
     *  <p>When this method returns, the <code>directory</code> property
     *  contains the File instance for the new directory.
     *  The <code>dataProvider</code> property is temporarily
     *  <code>null</code> until the new directory has been enumerated.
     *  After the enumeration, the <code>dataProvider</code> property
     *  contains an ArrayCollection of File instances
     *  for the new directory's contents.</p>
     *
     *  <p>The following example shows how to use this property
     *  along with the Button control to create an up button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:Button label="Up"
     *      enabled="{fileSystemViewer.canNavigateUp}"
     *      click="fileSystemViewer.navigateUp();"/&gt;</pre>
	 *
	 *  @see #canNavigateUp
     */
    public function navigateUp():void
    {
        helper.navigateUp();
    }

    /**
     *  Changes this control to display the contents of a previously-visited
     *  directory in the <code>backHistory</code> array.
     *
     *  <p>If the <code>backHistory</code> array is empty, or if you specify
     *  an index that is not in that array, then this method does nothing.</p>
     *
     *  <p>When this method returns, the <code>directory</code> property
     *  contains the File instance for the new directory.
     *  The <code>dataProvider</code> property is temporarily
     *  <code>null</code> until the new directory has been enumerated.
     *  After the enumeration, the <code>dataProvider</code> property
     *  contains an ArrayCollection of File instances
     *  for the new directory's contents.</p>
     *
     *  <p>The history list is left unchanged. However, the current index
     *  into it changes, which affects the <code>backHistory</code>
     *  and <code>forwardHistory</code> properties.
     *  They have new values as soon as this method returns.</p>
     *
     *  <p>The following example shows how to use this method
     *  along with the FileSystemHistoryButton control to create a back button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Back"
     *      enabled="{fileSystemViewer.canNavigateBack}"
     *      dataProvider="{fileSystemViewer.backHistory}"
     *      click="fileSystemViewer.navigateBack();"
     *      itemClick="fileSystemViewer.navigateBack(event.index);"/&gt;</pre>
     *
     *  @param index The index in the <code>backHistory</code> array
     *  to navigate to.
     *  The default is 0, indicating the directory that is "closest back".
	 *
	 *  @see #backHistory
	 *  @see #canNavigateBack
     */
    public function navigateBack(index:int = 0):void
    {
        helper.navigateBack(index);
    }

    /**
     *  Changes this control to display the contents of a previously-visited
     *  directory in the <code>forwardHistory</code> array.
     *
     *  <p>If the <code>forwardHistory</code> array is empty, or if you specify
     *  an index that is not in that array, then this method does nothing.</p>
     *
     *  <p>When this method returns, the <code>directory</code> property
     *  contains the File instance for the new directory.
     *  The <code>dataProvider</code> property is temporarily
     *  <code>null</code> until the new directory has been enumerated.
     *  After the enumeration, the <code>dataProvider</code> property
     *  contains an ArrayCollection of File instances
     *  for the new directory's contents.</p>
     *
     *  <p>The history list is left unchanged. However, the current index
     *  into it changes, which affects the <code>backHistory</code>
     *  and <code>forwardHistory</code> properties.
     *  They have new values as soon as this method returns.</p>
     *
     *  <p>The following example shows how to use this method
     *  along with the FileSystemHistoryButton control to create a forward button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemDataGrid id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Forward"
     *      enabled="{fileSystemViewer.canNavigateForward}"
     *      dataProvider="{fileSystemViewer.forwardHistory}"
     *      click="fileSystemViewer.navigateForward();"
     *      itemClick="fileSystemViewer.navigateForward(event.index);"/&gt;</pre>
     *
     *  @param index The index in the <code>forwardHistory</code> array
     *  to navigate to.
     *  The default is 0, indicating the directory that is "closest forward".
	 *
	 *  @see #canNavigateForward
	 *  @see #forwardHistory
     */
    public function navigateForward(index:int = 0):void
    {
        helper.navigateForward(index);
    }

    /**
     *  @copy mx.controls.FileSystemList#navigateTo()
     */
    public function navigateTo(directory:File):void
    {
        helper.navigateTo(directory);
    }

    /**
     *  @copy mx.controls.FileSystemList#refresh()
     */
    public function refresh():void
    {
        helper.refresh();
    }

    /**
     *  @copy mx.controls.FileSystemList#clear()
     */
    public function clear():void
    {
        helper.clear();
    }

    /**
     *  @private
     */
    public function resetHistory(dir:File):void
    {
        helper.resetHistory(dir);
    }

    /**
     *  @private
     */
    private function nameSortCompareFunction(item1:File, item2:File):int
    {
         return helper.directoryEnumeration.fileCompareFunction(item1, item2);
    }

    /**
     *  @private
     */
    private function typeLabelFunction(item:File,
                                       column:DataGridColumn = null):String
    {
        // If item is a directory, return "Folder".
        if (item.isDirectory)
        {
            return resourceManager.getString(
            	"aircontrols", "fileSystemDataGrid_typeFolder");
        }

        // If item is a file without an extension, return "File".
        var extension:String = item.extension;
        if (!extension || extension == "")
        {
            return resourceManager.getString(
            	"aircontrols", "fileSystemDataGrid_typeFileWithoutExtension");
        }

        // If item is a file with an extension, return a String like "TXT File".
        return resourceManager.getString(
        	"aircontrols", "fileSystemDataGrid_typeFileWithExtension",
        	[ extension.toUpperCase() ]);
    }

    /**
     *  @private
     */
    private function typeSortCompareFunction(item1:File, item2:File):int
    {
        var typeLabel1:String = typeLabelFunction(item1, null).toUpperCase();
        var typeLabel2:String = typeLabelFunction(item2, null).toUpperCase();

        if (typeLabel1 < typeLabel2)
            return -1;
        if (typeLabel1 > typeLabel2)
            return 1;
        return 0;
    }

    /**
     *  @private
     */
    private function sizeLabelFunction(item:File,
                                       column:DataGridColumn = null):String
    {
        var label:String;

        if (!item.exists) // item may have been deleted
            return "";

        try
        {
	        if (item.isDirectory)
	        {
	            label = resourceManager.getString(
	            	"aircontrols", "fileSystemDataGrid_sizeFolder");
	        }
	        else if (sizeDisplayMode == FileSystemSizeDisplayMode.KILOBYTES)
	        {
	            var kb:int = Math.ceil(item.size / 1024);
	            label = resourceManager.getString(
	            	"aircontrols", "fileSystemDataGrid_sizeKilobytes",
					[ kb ]);
	        }
	        else if (sizeDisplayMode == FileSystemSizeDisplayMode.BYTES)
	        {
	            label = resourceManager.getString(
	            	"aircontrols", "fileSystemDataGrid_sizeBytes",
					[ item.size ]);
	        }
        }
        catch(e:Error)
        {
        	// item.size throws a File I/O Error for some files,
        	// such as /etc/master.passwd and /etc/sudoers on a Mac
        	
	        label = resourceManager.getString(
	        	"aircontrols", "fileSystemDataGrid_sizeFolder");
        }

        return label;
    }

    /**
     *  @private
     */
    private function sizeSortCompareFunction(item1:File, item2:File):int
    {
        var size1:Number;
        try
        {
        	size1 = item1.isDirectory ? 0 : item1.size;
        }
        catch(e1:Error)
        {
        	// item.size throws a File I/O Error for some files,
        	// such as /etc/master.passwd and /etc/sudoers on a Mac
        	
        	size1 = 0;
        }

        var size2:Number;
        try
        {
        	size2 = item2.isDirectory ? 0 : item2.size;
        }
        catch(e2:Error)
        {
        	size2 = 0;
        }

        if (size1 < size2)
            return -1;
        if (size1 > size2)
            return 1;
        return 0;
    }

    /**
     *  @private
     */
    private function creationDateLabelFunction(
                                item:File, column:DataGridColumn = null):String
    {
        if (!item.exists) // item may have been deleted
            return "";

        return dateFormatter.format(item.creationDate);
    }

    /**
     *  @private
     */
    private function modificationDateLabelFunction(
                                item:File, column:DataGridColumn = null):String
    {
        if (!item.exists) // item may have been deleted
            return "";

        return dateFormatter.format(item.modificationDate);
    }

    /**
     *  @private
     */
    private function determineWidthToDisplay(s:String):Number
    {
        return measureText(s).width + 13;
    }

   //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (helper.handleKeyDown(event))
            return;

        super.keyDownHandler(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    protected function itemDoubleClickHandler(event:ListEvent):void
    {
        helper.itemDoubleClickHandler(event);
    }
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper classes
//
////////////////////////////////////////////////////////////////////////////////

import flash.display.DisplayObject;
import flash.filesystem.File;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.controls.dataGridClasses.DataGridListData;
import mx.controls.listClasses.BaseListData;
import mx.controls.FileSystemDataGrid;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBase;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.events.FlexEvent;
import mx.core.IUITextField;

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: CustomDataGridColumn
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class CustomDataGridColumn extends DataGridColumn
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function CustomDataGridColumn(columnName:String = null)
    {
        super(columnName);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function itemToDataTip(data:Object):String
    {
        return labelFunction(data);
    }
}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: NameColumnRenderer
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 *  This helper class implements the renderer for the Name column,
 *  which displays an icon and a name for a File.
 *  We need a custom renderer because DataGridItemRenderer
 *  doesn't support an icon.
 */
class NameColumnRenderer extends UIComponent
    implements IDataRenderer, IDropInListItemRenderer, IListItemRenderer
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function NameColumnRenderer()
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
    private var listOwner:ListBase;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties: UIComponent
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
	 *  The baselinePosition of a NameColumnRenderer is calculated
	 *  for its label.
     */
    override public function get baselinePosition():Number
    {
		if (!mx_internal::validateBaselinePosition())
			return NaN;
    	
        return label.y + label.baselinePosition;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property.
     */
    private var _data:Object;

    [Bindable("dataChange")]

    /**
     *  The implementation of the <code>data</code> property
     *  as defined by the IDataRenderer interface.
     *  When set, it stores the value and invalidates the component
     *  to trigger a relayout of the component.
     *
     *  @see mx.core.IDataRenderer
     */
    public function get data():Object
    {
        return _data;
    }

    /**
     *  @private
     */
    public function set data(value:Object):void
    {
        _data = value;

        invalidateProperties();

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  icon
    //----------------------------------

    /**
     *  The internal IFlexDisplayObject that displays the icon in this renderer.
     */
    protected var icon:IFlexDisplayObject;

    //----------------------------------
    //  label
    //----------------------------------

    /**
     *  The internal IUITextField that displays the text in this renderer.
     */
    protected var label:IUITextField;

    //----------------------------------
    //  listData
    //----------------------------------

    /**
     *  @private
     *  Storage for the listData property.
     */
    private var _listData:DataGridListData;

    [Bindable("dataChange")]

    /**
     *  The implementation of the <code>listData</code> property
     *  as defined by the IDropInListItemRenderer interface.
     *
     *  @see mx.controls.listClasses.IDropInListItemRenderer
     */
    public function get listData():BaseListData
    {
        return _listData;
    }

    /**
     *  @private
     */
    public function set listData(value:BaseListData):void
    {
        _listData = DataGridListData(value);

        invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!label)
        {
            label = IUITextField(createInFontContext(UITextField));
            label.styleName = this;
            addChild(DisplayObject(label));
        }
    }

    /**
     *  @private
     *  Apply the data and listData.
     *  Create an instance of the icon if specified,
     *  and set the text into the text field.
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (icon)
        {
            removeChild(DisplayObject(icon));
            icon = null;
        }

        if (_data != null)
        {
            listOwner = ListBase(_listData.owner);

            if (FileSystemDataGrid(listOwner).showIcons)
            {
                var iconClass:Class =
                    listOwner.getStyle(File(_data).isDirectory ?
                                       "directoryIcon" :
                                       "fileIcon");
                icon = new iconClass();
                addChild(DisplayObject(icon));
            }

            label.text = _listData.label ? _listData.label : " ";
            label.multiline = listOwner.variableRowHeight;
            label.wordWrap = listOwner.wordWrap;

            if (listOwner.showDataTips)
            {
                if (label.textWidth > label.width ||
                    listOwner.dataTipFunction != null)
                {
                    toolTip = listOwner.itemToDataTip(_data);
                }
                else
                {
                    toolTip = null;
                }
            }
            else
            {
                toolTip = null;
            }
        }
        else
        {
            label.text = " ";
            toolTip = null;
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var w:Number = 0;

        if (icon)
            w = icon.measuredWidth;

        // Guarantee that label width isn't zero
        // because it messes up ability to measure.
        if (label.width < 4 || label.height < 4)
        {
            label.width = 4;
            label.height = 16;
        }

        if (isNaN(explicitWidth))
        {
            w += label.getExplicitOrMeasuredWidth();
            measuredWidth = w;
            measuredHeight = label.getExplicitOrMeasuredHeight();
        }
        else
        {
            measuredWidth = explicitWidth;
            label.setActualSize(Math.max(explicitWidth - w, 4), label.height);
            measuredHeight = label.getExplicitOrMeasuredHeight();
            if (icon && icon.measuredHeight > measuredHeight)
                measuredHeight = icon.measuredHeight;
        }
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var startX:Number = 0;

        if (icon)
        {
            icon.x = startX;
            startX = icon.x + icon.measuredWidth;
            icon.setActualSize(icon.measuredWidth, icon.measuredHeight);
        }

        label.x = startX;
        label.setActualSize(unscaledWidth - startX, measuredHeight);

        var verticalAlign:String = getStyle("verticalAlign");
        if (verticalAlign == "top")
        {
            label.y = 0;
            if (icon)
                icon.y = 0;
        }
        else if (verticalAlign == "bottom")
        {
            label.y = unscaledHeight - label.height + 2; // 2 for gutter
            if (icon)
                icon.y = unscaledHeight - icon.height;
        }
        else
        {
            label.y = (unscaledHeight - label.height) / 2;
            if (icon)
                icon.y = (unscaledHeight - icon.height) / 2;
        }

        var labelColor:Number;

        if (data && parent)
        {
            if (!enabled)
                labelColor = getStyle("disabledColor");
            else if (listOwner.isItemHighlighted(listData.uid))
                labelColor = getStyle("textRollOverColor");
            else if (listOwner.isItemSelected(listData.uid))
                labelColor = getStyle("textSelectedColor");
            else
                labelColor = getStyle("color");

            label.setColor(labelColor);
        }
    }
}
