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

import flash.events.Event;
import flash.filesystem.File;
import flash.text.TextLineMetrics;
import mx.collections.CursorBookmark;
import mx.controls.ComboBox;
import mx.controls.fileSystemClasses.FileSystemControlHelper;
import mx.core.ClassFactory;
import mx.core.mx_internal;
import mx.events.FileEvent;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

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

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Specifies the icon that indicates
 *  the root directories of the computer.
 *  There is no default icon.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>computerIcon="&#64;Embed(source='computerIcon.jpg');"</code>
 *
 *  @default null
 */
[Style(name="computerIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Specifies the icon that indicates a directory.
 *  The default icon is located in the Assets.swf file.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>directoryIcon="&#64;Embed(source='directoryIcon.jpg');"</code>
 *
 *  @default TreeNodeIcon
 */
[Style(name="directoryIcon", type="Class", format="EmbeddedFile", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("FileSystemComboBox.png")]

[ResourceBundle("aircontrols")]

/**
 *  The FileSystemComboBox control defines a combo box control for
 *  navigating up the chain of ancestor directories from a specified
 *  directory in a file system.
 *  You often use this control with the FileSystemList and FileSystemTree
 *  controls to change the current directory displayed by those controls.
 *
 *  <p>Unlike the standard ComboBox control, to populate the FileSystemComboBox control's
 *  <code>dataProvider</code> property,
 *  you set the <code>directory</code> property.
 *  This control then automatically sets the <code>dataProvider</code>
 *  property to an ArrayCollection of File objects
 *  that includes all the ancestor directories of the specified directory,
 *  starting with the <code>COMPUTER</code> File
 *  and ending with the specified directory.</p>
 *
 *  <p>When you select an entry in the dropdown list,
 *  this control dispatches a <code>change</code> event.
 *  After the event is dispatched data provider, and consequently the dropdown list,
 *  contain the selected directory's ancestors.</p>
 * 
 *  @mxml
 *
 *  <p>The <code>&lt;mx:FileSystemComboBox&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:FileSystemComboBox
 *    <strong>Properties</strong>
 *    directory="<i>null</i>"
 *    indent="8"
 *    showIcons="true"
 * 
 *    <strong>Styles</strong>
 *    computerIcon="<i>null</i>"
 *    directoryIcon="<i>TreeNodeIcon</i>"
 * 
 *    <strong>Events</strong>
 *    directoryChange="<i>No default</i>"
 *  /&gt;
 *  </pre>
 * 
 *  @see flash.filesystem.File
 *  @see mx.controls.FileSystemList
 *  @see mx.controls.FileSystemTree
 * 
 *  @playerversion AIR 1.1
 */
public class FileSystemComboBox extends ComboBox
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
    public function FileSystemComboBox()
    {
        super();

        helper = new FileSystemControlHelper(this, false);

        itemRenderer = new ClassFactory(FileSystemComboBoxRenderer);
        labelFunction = helper.fileLabelFunction;
        rowCount = 10;

        addEventListener(Event.CHANGE, changeHandler);

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

    [Bindable("directoryChanged")]

    /**
     *  A File object representing the directory
     *  whose ancestors are to be displayed in this control.
     *  The control displays each ancestor directory
     *  as a separate entry in the dropdown list.
     *
     *  @default null
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
        _directory = value;
        directoryChanged = true;

        invalidateProperties();

		dispatchEvent(new Event("directoryChanged"));
    }

    //----------------------------------
    //  indent
    //----------------------------------

    /**
     *  @private
     *  Storage for the indent property.
     */
    private var _indent:int = 8;

    /**
     *  The number of pixels to indent each entry in the dropdown list.
     *
     *  @default 8
     */
    public function get indent():int
    {
        return _indent;
    }

    /**
     *  @private
     */
    public function set indent(value:int):void
    {
        _indent = value;
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
     *  A flag that determines whether icons are displayed
     *  before the directory names in the dropdown list.
     *
     *  @default true
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
        super.commitProperties();

        if (directoryChanged)
        {
            dataProvider = getParentChain(_directory);
            selectedItem = _directory;

			var event:FileEvent = new FileEvent(FileEvent.DIRECTORY_CHANGE);
			event.file = _directory;
			dispatchEvent(event);

            directoryChanged = false;
        }
    }

    /**
     *  @private
     */
    override protected function resourcesChanged():void
    {
        super.resourcesChanged();

        // The name of the COMPUTER pseudo-directory is localizable.
        // It appears at the top of the dropdown,
        // and may also be displayed as the selected item.
		invalidateSize();
        invalidateDisplayList();
		selectedIndex = selectedIndex;
    }

    /**
     *  @private
     */
    override protected function calculatePreferredSizeFromData(count:int):Object
	{
        var maxW:Number = 0;
        var maxH:Number = 0;

        var bookmark:CursorBookmark = iterator ? iterator.bookmark : null;

        iterator.seek(CursorBookmark.FIRST, 0);

        var more:Boolean = iterator != null;

        var lineMetrics:TextLineMetrics;

        for (var i:int = 0; i < count; i++)
        {
            var data:Object;
            if (more)
                data = iterator ? iterator.current : null;
            else
                data = null;

            var txt:String = itemToLabel(data);

            lineMetrics = measureText(txt);

			lineMetrics.width += i * indent;

            maxW = Math.max(maxW, lineMetrics.width);
            maxH = Math.max(maxH, lineMetrics.height);

            if (iterator)
                iterator.moveNext();
        }

        if (prompt)
        {
            lineMetrics = measureText(prompt);

            maxW = Math.max(maxW, lineMetrics.width);
            maxH = Math.max(maxH, lineMetrics.height);
        }

        maxW += getStyle("paddingLeft") + getStyle("paddingRight");

        if (iterator)
            iterator.seek(bookmark, 0);

        return { width: maxW, height: maxH };
	}

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Returns an Array of File objects
     *  representing the path to the specified directory.
     *  The first File represents a root directory.
     *  The last File represents the specified file's parent directory.
     */
    private function getParentChain(file:File):Array
    {
        if (helper.isComputer(file))
            return [ file ];

        var a:Array = [];

        for (var f:File = file; f != null; f = f.parent)
        {
            a.unshift(f);
        }

        a.unshift(COMPUTER);

        return a;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  When the user chooses a directory along the path,
     *  change this control to display the path to that directory.
     */
    private function changeHandler(event:Event):void
    {
        directory = File(selectedItem);
    }
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: FileSystemComboBoxRenderer
//
////////////////////////////////////////////////////////////////////////////////

import flash.display.DisplayObject;
import flash.filesystem.File;
import mx.controls.FileSystemComboBox;
import mx.controls.fileSystemClasses.FileSystemControlHelper;
import mx.controls.listClasses.ListItemRenderer;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  @private
 */
class FileSystemComboBoxRenderer extends ListItemRenderer
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function FileSystemComboBoxRenderer()
    {
        super();
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
        super.commitProperties();

        if (!listData || !data)
            return;

        var comboBox:FileSystemComboBox =
            FileSystemComboBox(listData.owner.owner);
        if (!comboBox.showIcons)
            return;

        var iconClass:Class = comboBox.getStyle(
        	comboBox.helper.isComputer(File(data)) ?
            "computerIcon" :
            "directoryIcon");

		if (iconClass)
        {
            icon = new iconClass();
            addChild(DisplayObject(icon));
        }
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        if (!listData || !data)
            return;

        var comboBox:FileSystemComboBox =
            FileSystemComboBox(listData.owner.owner);
        if (comboBox.indent == 0)
            return;

        var delta:Number = comboBox.indent * getNestLevel(File(data));
        if (icon)
            icon.x += delta;
        label.x += delta;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function getNestLevel(item:File):int
    {
        if (!item || !item.exists)
            return 0;

        var nestLevel:int = 0;
        for (var f:File = item; f != null; f = f.parent)
        {
            nestLevel++;
        }
        return nestLevel;
    }
}
