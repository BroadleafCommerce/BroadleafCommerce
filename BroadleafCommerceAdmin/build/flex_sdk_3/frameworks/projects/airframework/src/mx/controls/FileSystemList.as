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
import mx.controls.fileSystemClasses.FileSystemControlHelper;
import mx.controls.List;
import mx.core.mx_internal;
import mx.core.ScrollPolicy;
import mx.events.ListEvent;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the directory displayed by this control
 *  has changed for any reason.
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

[IconFile("FileSystemList.png")]

[ResourceBundle("aircontrols")]

/**
 *  The FileSystemList control lets you display the contents of a
 *  single file system directory, in a list format.
 *
 *  <p>To change the displayed data, rather than using the <code>dataProvider</code> property,
 *  you set the <code>directory</code> property.
 *  The control then automatically populates the <code>dataProvider</code>
 *  property by enumerating the contents of that directory.
 *  You should not set the <code>dataProvider</code> yourself.</p>
 *
 *  <p>You set the <code>directory</code> property to a File instance,
 *  as the following example shows: </p>
 *  <pre>
 *    &lt;mx:FileSystemList directory="{File.desktopDirectory}"/&gt;</pre>
 *
 *  <p>You can set the <code>enumerationMode</code> property to specify
 *  whether to show files, subdirectories, or both.
 *  There are three ways to show both: directories first,
 *  files first, or intermixed.</p>
 *
 *  <p>You can set the <code>extensions</code> property
 *  to filter the displayed items so that only files
 *  with the specified extensions appear.
 *  The <code>showHidden</code> determines whether the control
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
 *  are not displayed in a FileSystemList, even though they are
 *  accessible in AIR via the <code>icon</code> property of a File.</p>
 * 
 *  @mxml
 *
 *  <p>The <code>&lt;mx:FileSystemList&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:FileSystemList
 *    <strong>Properties</strong>
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
public class FileSystemList extends List
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  A constant that can be used as a value for the <code>directory</code> property,
	 *  representing a pseudo-top level directory named "Computer". This pseudo-directory
     *  contains the root directories
     *  (such as C:\ and D:\ on Windows or / on Macintosh).
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
    public function FileSystemList()
    {
        super();

        helper = new FileSystemControlHelper(this, false);

        doubleClickEnabled = true;
        horizontalScrollPolicy = ScrollPolicy.AUTO;
        iconFunction = helper.fileIconFunction;
        labelFunction = helper.fileLabelFunction;
        showDataTips = true;

        addEventListener(ListEvent.ITEM_DOUBLE_CLICK, itemDoubleClickHandler);

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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Back"
     *      enabled="{fileSystemViewer.canNavigateBack}"
     *      dataProvider="{fileSystemViewer.backHistory}"
     *      click="fileSystemViewer.navigateBack();"
     *      itemClick="fileSystemViewer.navigateBack(event.index);"/&gt;</pre>
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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  displaying the root directories
     *  such as C:\ and D:\ on Microsoft Windows.
     *  (This is the case in which the <code>directory</code>
     *  property is <code>COMPUTER</code>.)
     *
     *  <p>The following example shows how to use this property
     *  along with the Button control:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
    //  directory
    //----------------------------------

    [Bindable("directoryChanged")]

    /**
     *  The directory whose contents this control displays.
     *
     *  <p>If you set this property to a File object representing
     *  an existing directory, the <code>dataProvider</code>
     *  immediately becomes <code>null</code>.
     *  Later, when this control is revalidated by the LayoutManager,
     *  it enumerates that directory's contents
     *  and populates the <code>dataProvider</code> property
     *  with an ArrayCollection of the resulting File objects
     *  for the directory's files and subdirectories.</p>
     *
     *  <p>Setting this to a File which does not represent
     *  an existing directory is an error.
     *  Setting this to <code>COMPUTER</code> synchronously displays
     *  the root directories, such as C: and D: on Windows.</p>
     *
     *  <p>Setting this property deselects any previously selected items
     *  and causes the control to reset its scroll position
     *  to the upper-left corner.</p>
     *
     *  @default COMPUTER
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
     *  A String specifying whether this control displays
     *  only files, only subdirectories, or both.
     *  In the case that both are displayed,
     *  it also specifies whether the subdirectories are displayed
     *  before, after, or mixed in with the files.
     *  The possible values are specified
     *  by the FileSystemEnumerationMode class.
     *
     *  <p>This property affects which subdirectories
     *  and files are displayed in the control,
     *  and the order in which they appear.
     *  However, it does not affect which File instances
     *  are in the <code>dataProvider</code> property; it works
     *  by changing the behavior of the filter function
     *  that is applied to the <code>dataProvider</code>.
     *  Setting it does not cause the current <code>directory</code>
     *  to be re-enumerated.</p>
     *
     *  <p>Setting this property deselects any selected items
     *  and causes the control to reset its scroll position
     *  to the upper-left corner.</p>
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
     *  An Array of extensions specifying which files
     *  can be displayed in this control.
     *  If this property is set, for example,
     *  to <code>[ ".htm", ".html" ]</code>,
     *  then only files with these extensions can be displayed.
     *
     *  <p>Extensions are considered case-insensitive
     *  and the preceding dot is optional.
     *  For example, specifying <code>".HTML"</code>
     *  is equivalent to specifying <code>"html"</code>.</p>
     *
     *  <p>Directories are not subject to extension filtering,
     *  even if they have names containing dots.</p>
     *
     *  <p>The <code>enumerationMode</code> and <code>filterFunction</code>
     *  properties are also involved in determining which files
     *  appear in the control.
     *  For example, if this property is <code>[ ".htm", ".html" ]</code>,
     *  an .html file is not displayed if the
     *  <code>enumerationMode</code> property is
     *  <code>FileSystemEnumerationMode.DIRECTORIES_ONLY</code>,
     *  or if the <code>filterFunction</code> returns <code>false</code>
     *  for the file.</p>
     *
     *  <p>If this property is <code>null</code>, no extension filtering
     *  occurs, and all files are displayed.
     *  If this property is an empty Array, all extensions are filtered out
     *  and no files with extensions are displayed.</p>
     *
     *  <p>This property affects which files are displayed in the control.
     *  However, it does not affect which File instances
     *  are in the <code>dataProvider</code> property; it works
     *  by changing the behavior of the filter function
     *  that is applied to the <code>dataProvider</code>.
     *  Setting it does not cause the current <code>directory</code>
     *  to be re-enumerated.</p>
     *
     *  <p>Setting this property deselects any selected items
     *  and causes the control to reset its scroll position
     *  to the upper-left corner.</p>
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
     *  A callback Function that you can use to perform additional filtering,
     *  after the <code>enumerationMode</code> and <code>extensions</code>
     *  properties have been applied, to determine which files
     *  and subdirectories are displayed and which are hidden.
     *
     *  <p>This function must have the following signature:</p>
     *
     *  <pre>function myFilterFunction(file:File):Boolean</pre>
     *
     *  This function should return <code>true</code> to show the specified
     *  file or subdirectory and <code>false</code> to hide it.
     *
     *  <p>To ensure that every enumerated file and subdirectory is passed
     *  to this function, the <code>enumerationMode</code> property must
     *  not be <code>FileSystemEnumerationMode.FILES_ONLY</code> or
     *  <code>FileSystemEnumerationMode.DIRECTORIES_ONLY</code>,
     *  and the <code>extensions</code> property must be <code>null</code>.
     *  Otherwise, these properties cause pre-filtering to occur
     *  before this filter function is called.</p>
     *
     *  <p>This property affects which subdirectories
     *  and files are displayed in the control.
     *  However, it does not affect which File instances
     *  are in the <code>dataProvider</code> property; it works
     *  by changing the behavior of the filter function
     *  that is applied to the <code>dataProvider</code>.
     *  Setting it does not cause the current <code>directory</code>
     *  to be re-enumerated.</p>
     *
     *  <p>Setting this property deselects any selected items
     *  and causes the control to reset its scroll position
     *  to the upper-left corner.</p>
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
     *  <p>This Array may contain the special <code>COMPUTER</code> File object,
     *  which represents the non-existent directory whose contents
     *  are root directories such as C:\ and D:\ on Windows.</p>
     *
     *  <p>The following example shows how to use this property
     *  along with the FileSystemHistoryButton control
     *  to implement a forward button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
     *  &lt;mx:FileSystemHistoryButton label="Forward"
     *      enabled="{fileSystemViewer.canNavigateForward}"
     *      dataProvider="{fileSystemViewer.forwardHistory}"
     *      click="fileSystemViewer.navigateForward();"
     *      itemClick="fileSystemViewer.navigateForward(event.index);"/&gt;</pre>
     *
     *  @default []
	 *
	 *  @see #canNavigateForward
	 *  @see #navigateForward()
	 *  @see mx.controls.FileSystemHistoryButton
     */
    public function get forwardHistory():Array
    {
        return helper.forwardHistory;
    }

    //----------------------------------
    //  nameCompareFunction
    //----------------------------------

    /**
     *  A callback Function that you can use to change how file and subdirectory
     *  names are compared in order to produce the sort order.
     *
     *  <p>This function must have the following signature:</p>
     *
     *  <pre>function myNameCompareFunction(name1:String, name2:String):int</pre>
     *
     *  <p>It returns <code>-1</code> if <code>name1</code> should sort before
     *  <code>name2</code>, <code>1</code> if <code>name1</code> should
     *  sort after <code>name2</code>, and <code>0</code> if the names
     *  are the same.</p>
     *
     *  <p>If you do not set this property, the control performs
     *  a case-insensitive, locale-dependent comparison of the two names,
     *  by first calling the String method <code>toLocaleLowerCase()</code>
     *  on each name and then comparing them with the <code>&lt;</code>
     *  and <code>&gt;</code> operators.</p>
     *
     *  <p>If you have set <code>enumerationMode</code> to either
     *  <code>FileSystemEnumerationMode.FILES_FIRST</code> or
     *  <code>FileSystemEnumerationMode.DIRECTORIES_FIRST</code>,
     *  then this method is used to compare names only within
     *  the separate groups of files and directories.</p>
     *
     *  <p>This property affects the order in which
     *  subdirectories and files are displayed in the control.
     *  However, it does not affect which File instances
     *  are in the <code>dataProvider</code> property; it works
     *  by changing the behavior of the sort
     *  that is applied to the <code>dataProvider</code>.
     *  Setting it does not cause the current <code>directory</code>
     *  to be re-enumerated.</p>
     *
     *  <p>Setting this property deselects any selected items
     *  and causes the control to reset its scroll position
     *  to the upper-left corner.</p>
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
     *  The <code>nativePath</code> of the File item
     *  representing the selected subdirectory or file,
     *  or <code>null</code> if no item is selected.
     *
     *  <p>If multiple items are selected, getting this property
     *  gives the first path in the <code>selectedPaths</code> array.</p>
     *
     *  <p>Setting this property affects the <code>selectedPaths</code>,
     *  <code>selectedItem</code>, <code>selectedItems</code>,
     *  <code>selectedIndex</code>, and <code>selectedIndices</code>
     *  properties.</p>
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
     *  An Array of <code>nativePath</code> Strings for the File items
     *  representing the selected subdirectories and files.
     *  This Array is empty if no items are selected.
     *
     *  <p>Setting this property affects the <code>selectedPaths</code>,
     *  <code>selectedItem</code>, <code>selectedItems</code>,
     *  <code>selectedIndex</code>, and <code>selectedIndices</code>
     *  properties.</p>
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
     *  A flag that specifies whether extensions in file names are shown.
     *  Set this property to <code>true</code> to show file extensions
     *  and to <code>false</code> to hide them.
     *  Extensions in directory names are always shown.
     *
     *  <p>Setting this property does not affect which items
     *  are displayed, or the order in which they appear.
     *  It also does not affect which items are selected,
     *  or the scroll position.</p>
     *
     *  @default true
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
     *  A flag that specifies whether files and directories
     *  that the operating system considers hidden are displayed.
     *  Set this property to <code>true</code> to show hidden files
     *  and directories and to <code>false</code> to hide them.
     *
     *  <p>Setting this property deselects any selected items
     *  and causes the control to reset its scroll position
     *  to the upper-left corner.</p>
     *
     *  @default false
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
     *  A flag that specifies that icons are displayed
     *  before the file name.
     *  Set this property to <code>true</code> to show icons
     *  and to <code>false</code> to hide them.
     *
     *  <p>You can set the <code>directoryIcon</code>
     *  and <code>fileIcon</code> styles to change the default icons.
     *  This control cannot display the actual icon that the operating system
     *  displays for a file, because Apollo M2 does not support that feature.</p>
     *
     *  <p>Setting this property does not affect which items
     *  are displayed, or the order in which they appear.
     *  It also does not affect which items are selected,
     *  or the scroll position.</p>
     *
     *  @default true
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

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

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

        var text:String = resourceManager.getString(
            "aircontrols", "fileSystemList_measuredText");
        measuredWidth = measureText(text).width;
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
    override protected function itemToUID(data:Object):String
    {
        return helper.itemToUID(data);
    }

    /**
     *  @private
     */
    override public function itemToDataTip(data:Object):String
    {
        return helper.fileLabelFunction(File(data));
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the index of the File item with the specified
     *  native file system path.
     *
     *  <p>Items which are present in the control's collection
     *  but not displayed because of filtering are not searched.</p>
     *
     *  @param file A String specifying the <code>nativePath</code>
     *  of a File item.
     *
     *  @return A zero-based index, or <code>-1</code>
     *  if no File was found with the specified path.
     *
     *  @see flash.filesystem.File#nativePath
     */
    public function findIndex(nativePath:String):int
    {
        return helper.findIndex(nativePath);
    }

    /**
     *  Searches the File instances currently displayed in this control
     *  and returns the one with the specified <code>nativePath</code>property.
     *
     *  <p>File instances which are present in the control's collection
     *  but not displayed because of filtering are not searched.</p>
     *
     *  @param file A String specifying the <code>nativePath</code>
     *  of a File item.
     *
     *  @return A File instance if one was found with the specified
     *  <code>nativePath</code>, or <code>null</code> if none was found.
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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  <p>The following example shows how to use this method
     *  along with the Button control to create an up button:</p>
     *
     *  <pre>
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  &lt;mx:FileSystemList id="fileSystemViewer" directory="{File.desktopDirectory}"/&gt;
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
     *  Changes this control to display the contents of the specified
     *  directory.
     *
     *  @param file A file object representing a file or directory.
     */
    public function navigateTo(directory:File):void
    {
        helper.navigateTo(directory);
    }

    /**
     *  Re-enumerates the current directory being displayed by this control.
     *
     *  <p>Because AIR does not support file system notifications,
     *  this control does not automatically refresh if a file or
     *  subdirectory is created, deleted, moved, or renamed;
     *  in other words, it can display an out-of-date view of the file system.
     *  However, you can call <code>refresh()</code> to re-enumerate
     *  the current <code>directory</code> so that it is up-to-date.
     *  You could, for example, choose to do this when you have
     *  performed a file operation that you know causes the control's
     *  view to become stale, or when the user deactivates
     *  and reactivates your application.</p>
     *
     *  <p>This method preserves any selected items (if they still exist)
     *  and the scroll position (if the first visible item still exists).</p>
     */
    public function refresh():void
    {
        helper.refresh();
    }

    /**
     *  Clears this control so that it displays no items.
     *
     *  <p>This method sets the <code>dataProvider</code> to <code>null</code>
     *  but leaves the <code>directory</code> property unchanged.
     *  You can call <code>refresh</code> to populate this control again.</p>
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
