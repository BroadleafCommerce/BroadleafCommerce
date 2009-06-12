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

package mx.controls
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.ui.Keyboard;
import flash.utils.clearInterval;
import flash.utils.getTimer;
import flash.xml.XMLNode;
import mx.collections.ArrayCollection;
import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.ItemResponder;
import mx.collections.IViewCursor;
import mx.collections.XMLListCollection;
import mx.collections.errors.ItemPendingError;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListRowInfo;
import mx.controls.listClasses.ListBaseSelectionDataPending;
import mx.controls.treeClasses.DefaultDataDescriptor;
import mx.controls.treeClasses.HierarchicalCollectionView;
import mx.controls.treeClasses.HierarchicalViewCursor;
import mx.controls.treeClasses.ITreeDataDescriptor;
import mx.controls.treeClasses.ITreeDataDescriptor2;
import mx.controls.treeClasses.TreeItemRenderer;
import mx.controls.treeClasses.TreeListData;
import mx.core.ClassFactory;
import mx.core.EdgeMetrics;
import mx.core.EventPriority;
import mx.core.FlexSprite;
import mx.core.FlexShape;
import mx.core.IDataRenderer;
import mx.core.IFactory;
import mx.core.IFlexDisplayObject;
import mx.core.IIMESupport;
import mx.core.IInvalidating;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.effects.Tween;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.DragEvent;
import mx.events.FlexEvent;
import mx.events.ListEvent;
import mx.events.ListEventReason;
import mx.events.ScrollEvent;
import mx.events.TreeEvent;
import mx.managers.DragManager;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when a branch is closed or collapsed.
 *
 *  @eventType mx.events.TreeEvent.ITEM_CLOSE
 */
[Event(name="itemClose", type="mx.events.TreeEvent")]

/**
 *  Dispatched when a branch is opened or expanded.
 *
 *  @eventType mx.events.TreeEvent.ITEM_OPEN
 */
[Event(name="itemOpen", type="mx.events.TreeEvent")]

/**
 *  Dispatched when a branch open or close is initiated.
 *
 *  @eventType mx.events.TreeEvent.ITEM_OPENING
 */
[Event(name="itemOpening", type="mx.events.TreeEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/PaddingStyles.as";

/**
 *  Colors for rows in an alternating pattern.
 *  Value can be an Array of two of more colors.
 *  Used only if the <code>backgroundColor</code> property is not specified.
 * 
 *  @default undefined
 */
[Style(name="alternatingItemColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  Array of colors used in the Tree control, in descending order.
 *
 *  @default undefined
 */
[Style(name="depthColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  Specifies the default icon for a leaf item.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>defaultLeafIcon="&#64;Embed(source='c.jpg');"</code>
 *
 *  The default value is the "TreeNodeIcon" symbol in the Assets.swf file.
 */
[Style(name="defaultLeafIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Specifies the icon that is displayed next to a parent item that is open so that its
 *  children are displayed.
 *
 *  The default value is the "TreeDisclosureOpen" symbol in the Assets.swf file.
 */
[Style(name="disclosureOpenIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Specifies the icon that is displayed next to a parent item that is closed so that its
 *  children are not displayed (the subtree is collapsed).
 *
 *  The default value is the "TreeDisclosureClosed" symbol in the Assets.swf file.
 */
[Style(name="disclosureClosedIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Specifies the folder open icon for a branch item of the tree.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>folderOpenIcon="&#64;Embed(source='a.jpg');"</code>
 *
 *  The default value is the "TreeFolderOpen" symbol in the Assets.swf file.
 */
[Style(name="folderOpenIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Specifies the folder closed icon for a branch item of the tree.
 *  In MXML, you can use the following syntax to set this property:
 *  <code>folderClosedIcon="&#64;Embed(source='b.jpg');"</code>
 *
 *  The default value is the "TreeFolderClosed" symbol in the Assets.swf file.
 */
[Style(name="folderClosedIcon", type="Class", format="EmbeddedFile", inherit="no")]

/**
 *  Indentation for each tree level, in pixels.
 *
 *  @default 17
 */
[Style(name="indentation", type="Number", inherit="no")]

/**
 *  Length of an open or close transition, in milliseconds.
 *
 *  @default 250
 */
[Style(name="openDuration", type="Number", format="Time", inherit="no")]

/**
 *  Easing function to control component tweening.
 *
 *  <p>The default value is <code>undefined</code>.</p>
 */
[Style(name="openEasingFunction", type="Function", inherit="no")]

/**
 *  Color of the background when the user rolls over the link.
 *
 *  @default undefined
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Color of the background when the user selects the link.
 *
 *  @default undefined
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

/**
 *  Specifies the disabled color of a list item.
 *
 *  @default 0xDDDDDD
 *
 */
[Style(name="selectionDisabledColor", type="uint", format="Color", inherit="yes")]

/**
 *  Reference to an <code>easingFunction</code> function used for controlling programmatic tweening.
 *
 *  <p>The default value is <code>undefined</code>.</p>
 */
[Style(name="selectionEasingFunction", type="Function", inherit="no")]

/**
 *  Color of the text when the user rolls over a row.
 *
 *  @default 0x2B333C
 */
[Style(name="textRollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Color of the text when the user selects a row.
 *
 *  @default 0x2B333C
 */
[Style(name="textSelectedColor", type="uint", format="Color", inherit="yes")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.TreeAccImpl")]

[DefaultBindingProperty(destination="dataProvider")]

[DefaultProperty("dataProvider")]

[DefaultTriggerEvent("change")]

[IconFile("Tree.png")]

[RequiresDataBinding(true)]

/**
 *  The Tree control lets a user view hierarchical data arranged as an expandable tree.
 *  Each item in a tree can be a leaf or a branch.
 *  A leaf item is an end point in the tree.
 *  A branch item can contain leaf or branch items, or it can be empty.
 * 
 *  <p>By default, a leaf is represented by a text label next to a file icon.
 *  A branch is represented by a text label next to a folder icon, with a
 *  disclosure triangle that a user can open to expose children.</p>
 *  
 *  <p>The Tree class uses an ITreeDataDescriptor or ITreeDataDescriptor2 object to parse and
 *  manipulate the data provider.
 *  The default tree data descriptor, an object of the DefaultDataDescriptor class,
 *  supports XML and Object classes; an Object class data provider must have all children
 *  in <code>children</code> fields.
 *  </p>
 * 
 *  <p>The Tree control has the following default sizing 
 *     characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Wide enough to accommodate the icon, label, and 
 *               expansion triangle, if any, of the widest node in the 
 *               first 7 displayed (uncollapsed) rows, and seven rows 
 *               high, where each row is 20 pixels in height. If a 
 *               scroll bar is required, the width of the scroll bar is 
 *               not included in the width calculations.</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>5000 by 5000.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  <p>
 *  The &lt;mx:Tree&gt; tag inherits all the tag attributes of its superclass, and
 *  adds the following tag attributes:
 *  </p>
 *  <pre>
 *  &lt;mx:Tree
 *    <b>Properties</b>
 *    dataDescriptor="<i>Instance of DefaultDataDescriptor</i>"
 *    dataProvider="null"
 *    dragMoveEnabled="true|false"
 *    firstVisibleItem="<i>First item in the control</i>"
 *    hasRoot="false|true"
 *    itemIcons="null"
 *    maxHorizontalScrollPosition="0"
 *    openItems="null"
 *    showRoot="true|false"
 *    &nbsp;
 *    <b>Styles</b>
 *    alternatingItemColors="undefined"
 *    backgroundDisabledColor="0xDDDDDD"
 *    defaultLeafIcon="<i>'TreeNodeIcon' symbol in Assets.swf</i>"
 *    depthColors="undefined"
 *    disclosureClosedIcon="<i>'TreeDisclosureClosed' symbol in Assets.swf</i>"
 *    disclosureOpenIcon="<i>'TreeDisclosureOpen' symbol in Assets.swf</i>"
 *    folderClosedIcon="<i>'TreeFolderClosed' symbol in Assets.swf</i>"
 *    folderOpenIcon="<i>'TreeFolderOpen' symbol in Assets.swf</i>"
 *    indentation="17"
 *    openDuration="250"
 *    openEasingFunction="undefined"
 *    paddingLeft="2"
 *    paddingRight="0"
 *    rollOverColor="0xAADEFF"
 *    selectionColor="0x7FCDFE"
 *    selectionDisabledColor="0xDDDDDD"
 *    selectionEasingFunction="undefined"
 *    textRollOverColor="0x2B333C"
 *    textSelectedColor="0x2B333C"
 *    &nbsp;
 *    <b>Events</b>
 *    change="<i>No default</i>"
 *    itemClose="<i>No default</i>"
 *    itemOpen="<i>No default</i>"
 *    itemOpening="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.controls.treeClasses.ITreeDataDescriptor
 *  @see mx.controls.treeClasses.ITreeDataDescriptor2
 *  @see mx.controls.treeClasses.DefaultDataDescriptor
 *
 *  @includeExample examples/TreeExample.mxml
 */
public class Tree extends List implements IIMESupport
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
    private var IS_NEW_ROW_STYLE:Object =
    {
        depthColors: true,
        indentation: true,
        disclosureOpenIcon: true,
        disclosureClosedIcon: true,
        folderOpenIcon: true,
        folderClosedIcon: true,
        defaultLeafIcon: true
    };

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by TreeAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Tree()
    {
        super();

        itemRenderer = new ClassFactory(TreeItemRenderer);
        editorXOffset = 12;
        editorWidthOffset = -12;
        
        addEventListener(TreeEvent.ITEM_OPENING, expandItemHandler,
                         false, EventPriority.DEFAULT_HANDLER);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Item is currently in the process of opening
     */
    private var opening:Boolean;

    /**
     *  @private
     *  The tween object that animates rows
     */
    private var tween:Object;

    /**
     *  @private
     */
    private var maskList:Array;

    /**
     *  @private
     */
    private var _userMaxHorizontalScrollPosition:Number = 0;

    /**
     *  @private
     */
    private var eventPending:Object;

    /**
     *  @private
     */
    private var eventAfterTween:Object;

	/**
	 *  @private
	 */
	private var oldLength:int = -1;
 	
	/**
	 *  @private
	 */
	private var expandedItem:Object;

	/**
	 *  @private
	 */
	private var bSelectedItemRemoved:Boolean = false;
   
    /**
     *  @private
     *  Used to slow the scrolling down a bit
     */
    private var minScrollInterval:Number = 50;

    /**
     *  @private
     */
    private var rowNameID:Number = 0;

    /**
     *  @private
     */
    private var _editable:Boolean = false;

    /**
     *  @private
     */
    private var _itemEditor:IFactory = new ClassFactory(TextInput);

    /**
     *  @private
     *  Used to block giving focus to editor on focusIn
     */
    private var dontEdit:Boolean = false;

    /**
     *  @private
     */
    private var lastUserInteraction:Event;

    /**
     *  @private
     *  automation delegate access
     */
    mx_internal var _dropData:Object;

    /**
     *  An object that specifies the icons for the items.
     *  Each entry in the object has a field name that is the item UID
     *  and a value that is an an object with the following format:
     *  <pre>
     *  {iconID: <i>Class</i>, iconID2: <i>Class</i>}
     *  </pre>
     *  The <code>iconID</code> field value is the class of the icon for
     *  a closed or leaf item and the <code>iconID2</code> is the class
     *  of the icon for an open item.
     *
     *  <p>This property is intended to allow initialization of item icons.
     *  Changes to this array after initialization are not detected
     *  automatically.
     *  Use the <code>setItemIcon()</code> method to change icons dynamically.</p>
     *
     *  @see #setItemIcon()
     *  @default undefined
     */
    public var itemIcons:Object;

    /**
     *  @private
     */
    mx_internal var isOpening:Boolean = false;
    
    /**
     *  @private
     *  used by opening tween
     *  rowIndex is the row below the row that was picked
     *  and is the first one that will actually change
     */
    private var rowIndex:int;

    /**
     *  @private
     *  Number of rows that are or will be tweened
     */
    private var rowsTweened:int;

    /**
     *  @private
     */
    private var rowList:Array;
    
    /**
     *  @private
     */
    mx_internal var collectionLength:int;

    /**
     *  A hook for accessibility
     */
    mx_internal var wrappedCollection:ICollectionView;

    /**
     *  @private
     */
    mx_internal var collectionThrowsIPE:Boolean;

    /**
     *  @private
     */
    private var haveItemIndices:Boolean;

    /**
     *  @private
     */
    private var lastTreeSeekPending:TreeSeekPending;

	/**
	 *  @private
	 */
	private var bFinishArrowKeySelection:Boolean = false;
	private var proposedSelectedItem:Object;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  dataProvider
    //----------------------------------
    
    /**
     *  @private
     */
    private var dataProviderChanged:Boolean = false;

    [Bindable("collectionChange")]
    [Inspectable(category="Data", defaultValue="null")]

    /**
     *  An object that contains the data to be displayed.
     *  When you assign a value to this property, the Tree class handles
     *  the source data object as follows:
     *  <p>
     *  <ul><li>A String containing valid XML text is converted to an XMLListCollection.</li>
     *  <li>An XMLNode is converted to an XMLListCollection.</li>
     *  <li>An XMLList is converted to an XMLListCollection.</li>
     *  <li>Any object that implements the ICollectionView interface is cast to
     *  an ICollectionView.</li>
     *  <li>An Array is converted to an ArrayCollection.</li>
     *  <li>Any other type object is wrapped in an Array with the object as its sole
     *  entry.</li></ul>
     *  </p>
     *
     *  @default null
     */
    override public function set dataProvider(value:Object):void
    {
        // in all cases save off the original
        if (_rootModel)
            _rootModel.removeEventListener(
                            CollectionEvent.COLLECTION_CHANGE, 
                            collectionChangeHandler);
                                            
        // handle strings and xml
        if (typeof(value)=="string")
            value = new XML(value);
        else if (value is XMLNode)
            value = new XML(XMLNode(value).toString());
        else if (value is XMLList)
            value = new XMLListCollection(value as XMLList);
        
        if (value is XML)
        {
            _hasRoot = true;
            var xl:XMLList = new XMLList();
            xl += value;
            _rootModel = new XMLListCollection(xl);
        }
        //if already a collection dont make new one
        else if (value is ICollectionView)
        {
            _rootModel = ICollectionView(value);
            if (_rootModel.length == 1)
            	_hasRoot = true;
        }
        else if (value is Array)
        {
            _rootModel = new ArrayCollection(value as Array);
        }
        //all other types get wrapped in an ArrayCollection
        else if (value is Object)
        {
            _hasRoot = true;
            // convert to an array containing this one item
            var tmp:Array = [];
            tmp.push(value);
            _rootModel = new ArrayCollection(tmp);
        }
        else
        {
            _rootModel = new ArrayCollection();
        }
        //flag for processing in commitProps
        dataProviderChanged = true;
        invalidateProperties();
    }

    /**
     *  @private
     */
    override public function get dataProvider():Object
    {
        if (_rootModel)
            return _rootModel;

        return null;
    }

    //----------------------------------
    //  maxHorizontalScrollPosition
    //----------------------------------

    /**
     *  The maximum value for the <code>maxHorizontalScrollPosition</code> property for the Tree control.
     *  Unlike the <code>maxHorizontalScrollPosition</code> property
     *  in the List control, this property is modified by the Tree control as
     *  items open and close and as items in the tree otherwise become
     *  visible or are hidden (for example, by scrolling).
     *
     *  <p>If you set this property to the widest known item in the dataProvider,
     *  the Tree control modifies it so that even if that widest item
     *  is four levels down in the tree, the user can scroll to see it.
     *  As a result, although you read back the same value for the
     *  <code>maxHorizontalScrollPosition</code> property that you set,
     *  it isn't necessarily the actual value used by the Tree control.</p>
     *
     *  @default 0
     *
     */
    override public function get maxHorizontalScrollPosition():Number
    {
        return _userMaxHorizontalScrollPosition > 0 ?
               _userMaxHorizontalScrollPosition :
               super.maxHorizontalScrollPosition;
    }

    /**
     *  @private
     */
    override public function set maxHorizontalScrollPosition(value:Number):void
    {
        _userMaxHorizontalScrollPosition = value;
        value += getIndent();
        super.maxHorizontalScrollPosition = value;
    }

     //----------------------------------
    //  dragMoveEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the dragMoveEnabled property.
     *  For Tree only, this initializes to true. 
     */
    private var _dragMoveEnabled:Boolean = true;

    [Inspectable(defaultValue="true")]

    /**
     *  Indicates that items can be moved instead of just copied
     *  from the Tree control as part of a drag-and-drop operation.
     *
     *  @default true
     */
    override public function get dragMoveEnabled():Boolean
    {
        return _dragMoveEnabled;
    }

    /**
     *  @private
     */
    override public function set dragMoveEnabled(value:Boolean):void
    {
        _dragMoveEnabled = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  firstVisibleItem
    //----------------------------------

    [Bindable("firstVisibleItemChanged")]

    /**
     *  The item that is currently displayed in the top row of the tree.
     *  Based on how the branches have been opened and closed and scrolled,
     *  the top row might hold, for example, the ninth item in the list of
     *  currently viewable items which in turn represents
     *  some great-grandchild of the root.
     *  Setting this property is analogous to setting the verticalScrollPosition of the List control.
     *  If the item isn't currently viewable, for example, because it
     *  is under a nonexpanded item, setting this property has no effect.
     *
     *  <p>NOTE: In Flex 1.0 this property was typed as XMLNode although it really was
     *  either an XMLNode or TreeNode.  In 2.0, it is now the generic type Object and will
     *  return an object of the same type as the data contained in the dataProvider.</p>
     *
     *  <p>The default value is the first item in the Tree control.</p>
     */
    public function get firstVisibleItem():Object
    {
        return listItems[0][0].data;
    }
    
    /**
     *  @private
     */
    public function set firstVisibleItem(value:Object):void
    {
        var pos:int = getItemIndex(value);
        if (pos < 0)
            return;

        verticalScrollPosition = Math.min(maxVerticalScrollPosition, pos);

        dispatchEvent(new Event("firstVisibleItemChanged"));
    }

    //--------------------------------------------------------------------------
    // dataDescriptor
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var _dataDescriptor:ITreeDataDescriptor =
        new DefaultDataDescriptor();

    [Inspectable(category="Data")]

    /**
     *  Tree delegates to the data descriptor for information about the data.
     *  This data is then used to parse and move about the data source.
     *  <p>When you specify this property as an attribute in MXML you must
     *  use a reference to the data descriptor, not the string name of the
     *  descriptor. Use the following format for the property:</p>
     *
     * <pre>&lt;mx:Tree id="tree" dataDescriptor="{new MyCustomTreeDataDescriptor()}"/&gt;></pre>
     *
     *  <p>Alternatively, you can specify the property in MXML as a nested
     *  subtag, as the following example shows:</p>
     *
     * <pre>&lt;mx:Tree&gt;
     * &lt;mx:dataDescriptor&gt;
     * &lt;myCustomTreeDataDescriptor&gt;</pre>
     *
     * <p>The default value is an internal instance of the
     *  DefaultDataDescriptor class.</p>
     *
     */
    public function set dataDescriptor(value:ITreeDataDescriptor):void
    {
        _dataDescriptor = value;
    }

    /**
     *  Returns the current ITreeDataDescriptor.
     *
     *   @default DefaultDataDescriptor
     */
    public function get dataDescriptor():ITreeDataDescriptor
    {
        return ITreeDataDescriptor(_dataDescriptor);
    }

    //--------------------------------------------------------------------------
    // showRoot
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Storage variable for showRoot flag.
     */
    mx_internal var _showRoot:Boolean = true;

    /**
     *  @private
     *  Storage variable for changes to showRoot.
     */
    mx_internal var showRootChanged:Boolean = false;
    
    /** 
    *  @private
    *  Flag to indicate if the model has a root
    */
    mx_internal var _hasRoot:Boolean = false;

    /**
     *  @private
     *  Storage variable for the original dataProvider
     */
    mx_internal var _rootModel:ICollectionView;

    [Inspectable(category="Data", enumeration="true,false", defaultValue="false")]

    /**
     *  Sets the visibility of the root item.
     *
     *  If the dataProvider data has a root node, and this is set to 
     *  <code>false</code>, the Tree control does not display the root item. 
     *  Only the decendants of the root item are displayed.  
     * 
     *  This flag has no effect on non-rooted dataProviders, such as List and Array. 
     *
     *  @default true
     *  @see #hasRoot
     */
    public function get showRoot():Boolean
    {
        return _showRoot;
    }

    /**
     *  @private
     */
    public function set showRoot(value:Boolean):void
    {
        if (_showRoot != value)
        {
            _showRoot = value;
            showRootChanged = true;
            invalidateProperties();
        }
    }

    /**
     *  Indicates that the current dataProvider has a root item; for example, 
     *  a single top node in a hierarchical structure. XML and Object 
     *  are examples of types that have a root. Lists and arrays do not.
     * 
     *  @see #showRoot
     */
    public function get hasRoot():Boolean
    {
        return _hasRoot;
    }
    
    //--------------------------------------------------------------------------
    // openItems
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Used to hold a list of items that are opened or set opened.
     */
    private var _openItems:Object = {};
    
    /**
     *  @private 
     */
    private var openItemsChanged:Boolean = false;
    
    /**
     *  The items that have been opened or set opened.
     * 
     *  @default null
     */
    public function get openItems():Object
    {
        var openItemsArray:Array = [];
        for each(var item:* in _openItems) 
        {
            openItemsArray.push(item);
        }
        return openItemsArray;
    }
    
    /**
     *  @private 
     */
    public function set openItems(value:Object):void
    {
        if (value != null)
        {
        	for (var uid:String in _openItems)
        		delete _openItems[uid];
        		
            for each (var item:* in value)
            {
                _openItems[itemToUID(item)] = item;
            }
            openItemsChanged = true;
            invalidateProperties();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (Tree.createAccessibilityImplementation != null)
            Tree.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        if (showRootChanged)
        {
            if (!_hasRoot)
                showRootChanged = false;            
        }
        
        if (dataProviderChanged || showRootChanged || openItemsChanged)
        {
            var tmpCollection:ICollectionView;
            //reset flags 
            
            dataProviderChanged = false;
            showRootChanged = false;
            
            //we always reset the open and selected items on a dataprovider assignment
            if (!openItemsChanged)
                _openItems = {};
        
            // are we swallowing the root?
            if (_rootModel && !_showRoot && _hasRoot)
            {
                var rootItem:* = _rootModel.createCursor().current;
                if (rootItem != null &&
                    _dataDescriptor.isBranch(rootItem, _rootModel) &&
                    _dataDescriptor.hasChildren(rootItem, _rootModel))
                {
                    // then get rootItem children
                    tmpCollection = getChildren(rootItem, _rootModel);
                }
            }

            // at this point _rootModel may be null so we dont need to continue
            if (_rootModel)
            {
                //wrap userdata in a TreeCollection and pass that collection to the List
                super.dataProvider = wrappedCollection = (_dataDescriptor is ITreeDataDescriptor2) ?
															ITreeDataDescriptor2(_dataDescriptor).getHierarchicalCollectionAdaptor(
																   tmpCollection != null ? tmpCollection : _rootModel,
																   itemToUID,
																   _openItems) :
															new HierarchicalCollectionView(
																  tmpCollection != null ? tmpCollection : _rootModel,
																 _dataDescriptor,
																   itemToUID,
																   _openItems);


                // not really a default handler, but we need to be later than the wrapper
                wrappedCollection.addEventListener(CollectionEvent.COLLECTION_CHANGE,
                                          collectionChangeHandler,
                                          false,
                                          EventPriority.DEFAULT_HANDLER, true);
            }
            else
            {
                super.dataProvider = null;
            }
        }
        
        super.commitProperties();
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // Kill any animation before resizing;
        // tween is null if there is no Tween underway.
        if (tween)
            tween.endTween();

        super.updateDisplayList(unscaledWidth, unscaledHeight);

        //update collection length
        if (collection)
            collectionLength = collection.length;
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        if (styleProp == null ||
            styleProp == "styleName" ||
            IS_NEW_ROW_STYLE[styleProp])
        {
            itemsSizeChanged = true;
            invalidateDisplayList();
        }

        super.styleChanged(styleProp);
    }

    /**
     *  @private
     *  Position indicator bar that shows where an item will be placed in the list.
     */
    override public function showDropFeedback(event:DragEvent):void
    {
        super.showDropFeedback(event);
        // Adjust for indent
        var vm:EdgeMetrics = viewMetrics;
        var offset:int = 0;
        updateDropData(event);
        var indent:int = 0;
        var depth:int;
        if (_dropData.parent)
        {
            offset = getItemIndex(iterator.current);
            depth = getItemDepth(_dropData.parent, Math.abs(offset - getItemIndex(_dropData.parent)));
            indent = (depth + 1) * getStyle("indentation");
        }
        else 
        {
            indent = getStyle("indentation");
        }
        if (indent < 0)
            indent = 0;
        //position drop indicator
        dropIndicator.width = listContent.width - indent;
        dropIndicator.x = indent + vm.left + 2;
        if (_dropData.emptyFolder)
        {
            dropIndicator.y += _dropData.rowHeight / 2;
        }
    }

    /**
     *  @private
     */
    override public function calculateDropIndex(event:DragEvent = null):int
    {
        if (event)
            updateDropData(event);

        return _dropData.rowIndex;
    }

    /**
     *  @private
     */
    override protected function addDragData(ds:Object):void    // actually a DragSource
    {
        ds.addHandler(collapseSelectedItems, "treeItems");
    }

    /**
     *  @private
     *
     *  see ListBase.as
     */
    override mx_internal function addClipMask(layoutChanged:Boolean):void
    {
        var vm:EdgeMetrics = viewMetrics;

        if (horizontalScrollBar)
            vm.bottom -= horizontalScrollBar.minHeight;

        if (verticalScrollBar)
            vm.right -= verticalScrollBar.minWidth;

        listContent.scrollRect = new Rectangle(
            0, 0,
            unscaledWidth - vm.left - vm.right,
            listContent.heightExcludingOffsets);
    }

    /**
     *  @private
     *
     *  Undo the effects of the addClipMask function (above)
     */
    override mx_internal function removeClipMask():void
    {
    }

	/**
     *  Creates a new TreeListData instance and populates the fields based on
     *  the input data provider item. 
     *  
     *  @param data The data provider item used to populate the ListData.
     *  @param uid The UID for the item.
     *  @param rowNum The index of the item in the data provider.
     *  
     *  @return A newly constructed ListData object.
     */
    override protected function makeListData(data:Object, uid:String, 
    	rowNum:int):BaseListData
    {
        var treeListData:TreeListData = new TreeListData(itemToLabel(data), uid, this, rowNum);
        initListData(data, treeListData);
        return treeListData;
    }
    
    /**
     *  @private
     */
    override public function itemToIcon(item:Object):Class
    {
        if (item == null)
        {
            return null;
        }

        var icon:*;
        var open:Boolean = isItemOpen(item);
        var branch:Boolean = isBranch(item);
        var uid:String = itemToUID(item);

        //first lets check the component
        var iconClass:Class =
                itemIcons && itemIcons[uid] ?
                itemIcons[uid][open ? "iconID2" : "iconID"] :
                null;

        if (iconClass)
        {
            return iconClass;
        }
        else if (iconFunction != null)
        {
            return iconFunction(item)
        }
        else if (branch)
        {
            return getStyle(open ? "folderOpenIcon" : "folderClosedIcon");
        }
        else
        //let's check the item itself
        {
            if (item is XML)
            {
                try
                {
                    if (item[iconField].length() != 0)
                       icon = String(item[iconField]);
                }
                catch(e:Error)
                {
                }
            }
            else if (item is Object)
            {
                try
                {
                    if (iconField && item[iconField])
                        icon = item[iconField];
                    else if (item.icon)
                        icon = item.icon;
                }
                catch(e:Error)
                {
                }
            }
        }

        //set default leaf icon if nothing else was found
        if (icon == null)
            icon = getStyle("defaultLeafIcon");

        //convert to the correct type and class
        if (icon is Class)
        {
            return icon;
        }
        else if (icon is String)
        {
            iconClass = Class(systemManager.getDefinitionByName(String(icon)));
            if (iconClass)
                return iconClass;

            return document[icon];
        }
        else
        {
            return Class(icon);
        }

    }

    /**
     *  @private
     */
    override protected function drawRowBackgrounds():void
    {
        var rowBGs:Sprite = Sprite(listContent.getChildByName("rowBGs"));
        if (!rowBGs)
        {
            rowBGs = new FlexSprite();
            rowBGs.name = "rowBGs";
            rowBGs.mouseEnabled = false;
            listContent.addChildAt(rowBGs, 0);
        }

        var color:Object;
        var colors:Array;
        var depthColors:Boolean = false;

        colors = getStyle("depthColors");
        if (colors)
        {
            depthColors = true;
        }
        else
        {
            colors = getStyle("alternatingItemColors");
        }
        color = getStyle("backgroundColor");
        if (!colors || colors.length == 0)
		{
			while (rowBGs.numChildren > n)
			{
				rowBGs.removeChildAt(rowBGs.numChildren - 1);
			}
            return;
		}

        StyleManager.getColorNames(colors);

        var curRow:int = 0;
        var actualRow:int = verticalScrollPosition;
        var i:int = 0;
        var n:int = listItems.length;

        while (curRow < n)
        {
            if (depthColors)
            {
                try
				{
                    if (listItems[curRow][0])
                    {
                        var d:int = getItemDepth(listItems[curRow][0].data, curRow);
                        var rowColor:uint = colors[d-1] ? colors[d - 1] : uint(color);
                        drawRowBackground(rowBGs, i++, rowInfo[curRow].y, rowInfo[curRow].height, rowColor, curRow);
                    }
                    else
                    {
                        drawRowBackground(rowBGs, i++, rowInfo[curRow].y, rowInfo[curRow].height, uint(color), curRow);
                    }
                }
                catch(e:Error)
                {
                    //trace("[Tree] caught exception in drawRowBackground");
                }
            }
            else
            {
                drawRowBackground(rowBGs, i++, rowInfo[curRow].y, rowInfo[curRow].height, colors[actualRow % colors.length], actualRow);
            }
            curRow++;
			actualRow++;
        }
        while (rowBGs.numChildren > n)
        {
            rowBGs.removeChildAt(rowBGs.numChildren - 1);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Sets the associated icon for the item.  Calling this method overrides the
     *  <code>iconField</code> and <code>iconFunction</code> properties for
     *  this item if it is a leaf item. Branch items don't use the
     *  <code>iconField</code> and <code>iconFunction</code> properties.
     *  They use the <code>folderOpenIcon</code> and <code>folderClosedIcon</code> properties.
     *
     *  @param item Item to affect.
     *  @param iconID Linkage ID for the closed (or leaf) icon.
     *  @param iconID2 Linkage ID for the open icon.
     *
     */
    public function setItemIcon(item:Object, iconID:Class, iconID2:Class):void
    {
        if (!itemIcons)
            itemIcons = {};

        if (!iconID2)
            iconID2 = iconID;

        itemIcons[itemToUID(item)] = { iconID: iconID, iconID2: iconID2 };

        itemsSizeChanged = true;
        invalidateDisplayList();
    }

    /**
    *   @private
     *  Returns <code>true</code> if the specified item is a branch item. The Tree 
     *  control delegates to the IDataDescriptor to determine if an item is a branch.
     *  @param item Item to inspect.
     *  @return True if a branch, false if not.
     *
     */
    private function isBranch(item:Object):Boolean
    {
        if (item != null)
            return _dataDescriptor.isBranch(item, iterator.view);

        return false;
    }
    
   /**
    * @private
    * wraps calls to the descriptor 
    * mx_internal for automation delegate access
    */
    mx_internal function getChildren(item:Object, view:Object):ICollectionView
    {
        //get the collection of children
        var children:ICollectionView = _dataDescriptor.getChildren(item, view);
        return children;
    }

    /**
    *  Determines the number of parents from root to the specified item.
    *  Method starts with the Cursor.current item and will seek forward
    *  to a specific offset, returning the cursor to its original position.
    *
    *  @private
    */
    mx_internal function getItemDepth(item:Object, offset:int):int
    {
        //first test for a match (most cases)
        if (!collection)
            return 0;

        if (!iterator)
            listContent.iterator = collection.createCursor();

        if (iterator.current == item)
            return getCurrentCursorDepth();

        //otherwise seek to offset and get the depth
        var bookmark:CursorBookmark = iterator.bookmark;
        iterator.seek(bookmark, offset);
        var depth:int = getCurrentCursorDepth();
        //put the cursor back
        iterator.seek(bookmark, 0);
        return depth;
    }

    /**
     *  @private
     *  Utility method to get the depth of the current item from the cursor.
     */
    private function getCurrentCursorDepth():int  //private
    {
		if (_dataDescriptor is ITreeDataDescriptor2)
			return ITreeDataDescriptor2(_dataDescriptor).getNodeDepth(iterator.current, iterator, _rootModel);

		return HierarchicalViewCursor(iterator).currentDepth;
    }

    /**
     *  @private
     *  Gets the number of visible items from a starting item.
     */
    private function getVisibleChildrenCount(item:Object):int
    {
        var count:int = 0;
        
        if (item == null)
            return count;
        
        var uid:String = itemToUID(item);
        var children:Object;
        if (_openItems[uid] && 
            _dataDescriptor.isBranch(item, iterator.view) &&
            _dataDescriptor.hasChildren(item, iterator.view))
        {
            children = getChildren(item, iterator.view);
        }
        if (children != null)
        {
	    	var cursor:IViewCursor = children.createCursor();
    		while (!cursor.afterLast)
    		{
                count++;
                uid = itemToUID(cursor.current);
                if (_openItems[uid])
                    count += getVisibleChildrenCount(cursor.current);
    			cursor.moveNext();
    		}
        }
        return count;
    }

    /**
     *  Returns <code>true</code> if the specified item branch is open (is showing its children).
     *  @param item Item to inspect.
     *  @return True if open, false if not.
     * 
     */
    public function isItemOpen(item:Object):Boolean
    {
        var uid:String = itemToUID(item);
        return _openItems[uid] != null;
    }

    /**
     *  @private
     */
    private function makeMask():DisplayObject
    {
        var tmpMask:Shape = new FlexShape();
        tmpMask.name = "mask";

        var g:Graphics = tmpMask.graphics;
        g.beginFill(0xFFFFFF);
        g.moveTo(0,0);
        g.lineTo(0,10);
        g.lineTo(10,10);
        g.lineTo(10,0);
        g.lineTo(0,0);
        g.endFill();

        listContent.addChild(tmpMask);
        return tmpMask;
    }

    /**
     *  Opens or closes a branch item.
     *  When a branch item opens, it restores the open and closed states
     *  of its child branches if they were already opened.
     * 
     *  If you set <code>dataProvider</code> and then immediately call
     *  <code>expandItem()</code> you may not see the correct behavior. 
     *  You should either wait for the component to validate
     *  or call <code>validateNow()</code>.
     *
     *  @param item Item to affect.
     *
     *  @param open Specify <code>true</code> to open, <code>false</code> to close.
     *
     *  @param animate Specify <code>true</code> to animate the transition. (Note:
     *  If a branch has over 20 children, it does not animate the first time it opens,
     *  for performance reasons.)
     *
     *  @param dispatchEvent Controls whether the tree fires an <code>open</code> event
     *                       after the open animation is complete.
     *
     *  @param cause The event, if any, that initiated the item open action.
     *
     */
    public function expandItem(item:Object, open:Boolean,
                              animate:Boolean = false,
                              dispatchEvent:Boolean = false,    
                              cause:Event = null):void
    {
        //if the iterator is null, that indicates we have not been 
        //validated yet, so we will not continue. 
        if (iterator == null)
            return;
        
        if (cause) 
            lastUserInteraction = cause;
            
		expandedItem = item;

        listContent.allowItemSizeChangeNotification = false;
        
        var i:int;
        var bSelected:Boolean = false;
        var bHighlight:Boolean = false;
        var bCaret:Boolean = false;
        var newRowIndex:int;
        var rowData:BaseListData;

        var tmpMask:DisplayObject;

        var uid:String = itemToUID(item);
        // if this can't be opened, or shouldn't be, don't!
        if (!isBranch(item) || (isItemOpen(item)==open) || isOpening)
            return;

        if (itemEditorInstance)
            endEdit(ListEventReason.OTHER);

        //we'll use the last recorded length not necessarily the current one
        oldLength = collectionLength;

        var bookmark:CursorBookmark = iterator.bookmark;
        
        // sent to update the length in the collection     
        var event:CollectionEvent = new CollectionEvent(
                                        CollectionEvent.COLLECTION_CHANGE,
                                        false, 
                                        true,
                                        CollectionEventKind.mx_internal::EXPAND);
        event.items = [item];
        
        // update the list of _openItems
        if (open)
        {
            _openItems[uid] = item;
            collection.dispatchEvent(event);
            rowsTweened = Math.abs(oldLength - collection.length);
        }
        else
        {
            delete _openItems[uid];
            collection.dispatchEvent(event);
            //how many rows to move? 
            rowsTweened = Math.abs(oldLength - collection.length);  
        }
        
        // will it affect the displayList?
        if (isItemVisible(item))
        {
            // is the item on screen?
            if (visibleData[uid])
            {
                //find the rowindex of the row after the open thats opening/closing
                var n:int = listItems.length;
                for (rowIndex = 0; rowIndex < n; rowIndex++)
                {
                    if (rowInfo[rowIndex].uid == uid)
                    {
                        rowIndex++;
                        // rowIndex is set to the row after the one opening/closing
                        // because that is the first row to change
                        break;
                    }
                }
            }
        }
        //if we're opening or closing a node that is not visible,
        //we still need to dispatch the correct collectionChange events
        //so that scroll position and selection properties update correctly.
        else
        {
	    	var eventArr:Array = open ? 
	    		buildUpCollectionEvents(true) : buildUpCollectionEvents(false);
			for (i = 0; i < eventArr.length; i++)
			{
				collection.dispatchEvent(eventArr[i]);
			}
	        return;
        }
    
        var rC:int = listItems.length;
        var tmpRowInfo:Object;
        var row:Array;
        // we will cap this with as many rows as can be displayed later
        var rowsToMove:int = rowsTweened;
        var dur:Number = getStyle("openDuration");
        if (animate && rowIndex < rC && rowsToMove > 0 && rowsToMove < 20 && dur != 0)
        {
            // Kill any previous animation. tween is undefined if there is no Tween underway.
            if (tween)
                tween.endTween();

            var renderer:IListItemRenderer = listItems[rowIndex - 1][0];
            if (renderer is IDropInListItemRenderer)
            {
                var di:IDropInListItemRenderer = IDropInListItemRenderer(renderer);
                var treeListData:TreeListData = TreeListData(di.listData);
                treeListData = TreeListData(makeListData(treeListData.item,
                    treeListData.uid, treeListData.rowIndex));
                di.listData = treeListData;
                renderer.data = renderer.data;  // this forces eval of new listData
            }

            // animate the opening
            opening = open;
            isOpening = true;

            maskList = [];
            rowList = [];

            var xx:Number = getStyle("paddingLeft") - horizontalScrollPosition;
            var ww:Number = renderer.width;
            var yy:Number = 0;
            var hh:Number;

            // don't tween anymore than the amount of space we have
            var delta:int = rowIndex;
            var maxDist:Number = 0;
            if (open)
            {
                newRowIndex = rowIndex;
                // don't tween anymore than the amount of space we have
                maxDist = listContent.height - rowInfo[rowIndex].y;
                iterator.seek(CursorBookmark.CURRENT, delta);
                var data:Object;

                // create the rows now so we know how much to move
                for (i = 0; i < rowsToMove && yy < maxDist; i++)
                {
                    data = iterator.current;

                    if (freeItemRenderers.length)
                    {
                        renderer = freeItemRenderers.pop();
                    }
                    else
                    {
                        renderer = createItemRenderer(data);
                        renderer.owner = this;
                        renderer.styleName = listContent;
                        listContent.addChild(DisplayObject(renderer));
                    }
                    uid = itemToUID(data);
                    rowData = makeListData(data, uid, rowIndex + i);
                    rowMap[renderer.name] = rowData;
                    if (renderer is IDropInListItemRenderer)
                        IDropInListItemRenderer(renderer).listData = data ? rowData : null;
                    renderer.data = data;
                    renderer.enabled = enabled;
                    if (data)
                    {
                        visibleData[uid] = renderer;
                        renderer.visible = true;
                    }
                    else
                    {
                        renderer.visible = false;
                    }
                    renderer.explicitWidth = ww;
                    
                    //from list
                    if ((renderer is IInvalidating) && 
                        (wordWrapChanged || 
                        variableRowHeight))
                        IInvalidating(renderer).invalidateSize();

					UIComponentGlobals.layoutManager.validateClient(renderer, true);
                        
                    hh = Math.ceil(variableRowHeight ?
                             renderer.getExplicitOrMeasuredHeight() +
                             cachedPaddingTop + cachedPaddingBottom :
                             rowHeight);
                    var rh:Number = renderer.getExplicitOrMeasuredHeight();
                    renderer.setActualSize(ww, variableRowHeight ? rh : rowHeight - cachedPaddingTop - cachedPaddingBottom);
                    renderer.move(xx, yy + cachedPaddingTop);
                    bSelected = selectedData[uid] != null;
                    bHighlight = highlightUID == uid;
                    bCaret = caretUID == uid;
                    tmpRowInfo = new ListRowInfo(yy, hh, uid, data);
                    if (data)
                        drawItem(renderer, bSelected, bHighlight, bCaret);
                    yy += hh;

                    rowInfo.splice(rowIndex + i, 0, tmpRowInfo);
                    row = [];
                    row.push(renderer);
                    listItems.splice(rowIndex + i, 0, row);

					// due to issues in HCV with paging
					// don't go looking for something unless you really
					// need it
					if (i < rowsToMove - 1)
					{
						try
						{
							iterator.moveNext();
						}
						catch(e:ItemPendingError)
						{
							rowsToMove = i + 1;
							break;
						}
					}
                }
                rowsTweened = i;
                // position the new rows;
                var referenceRowInfo:ListRowInfo = rowInfo[rowIndex + rowsTweened];
                for (i = 0; i < rowsTweened; i++)
                {
                    renderer = listItems[rowIndex + i][0];
                    renderer.move(renderer.x, renderer.y - (yy - referenceRowInfo.y));
                    rowInfo[rowIndex + i].y -= yy - referenceRowInfo.y;
                    tmpMask = makeMask();
                    tmpMask.x = xx;
                    tmpMask.y = referenceRowInfo.y;
                    tmpMask.width = ww;
                    tmpMask.height = yy;
                    listItems[rowIndex + i][0].mask = tmpMask;
                }
            }
            else // closing up rows
            {
                var more:Boolean = true;
                var valid:Boolean = true;
                var startY:Number = yy = rowInfo[listItems.length - 1].y + rowInfo[listItems.length - 1].height;

                // figure out how much space was consumed by the rows that are going away
                for (i = rowIndex; i < rowIndex + rowsToMove && i < rC; i++)
                {
                    maxDist += rowInfo[i].height;
                    // retain a reference to the rows going away
                    rowList.push({item: listItems[i][0]});
                    tmpMask = makeMask();
                    tmpMask.x = xx;
                    tmpMask.y = listItems[rowIndex][0].y;
                    tmpMask.width = ww;
                    tmpMask.height = maxDist;
                    listItems[i][0].mask = tmpMask;
                }
                rowsToMove = i - rowIndex;
                // remove the rows going away
                rowInfo.splice(rowIndex, rowsToMove);
                listItems.splice(rowIndex, rowsToMove);

                iterator.seek(CursorBookmark.CURRENT, listItems.length);
                more = (iterator != null && !iterator.afterLast && iteratorValid);

                maxDist += yy;
                // create the rows now so we know how much to move
                for (i = 0; i < rowsToMove && yy < maxDist; i++)
                {
                    //reset item specific values
                    uid = null;
                    data = null;
                    renderer = null;

                    valid = more;
                    data = more ? iterator.current : null;

                    if (valid)
                    {
                        if (freeItemRenderers.length)
                        {
                            renderer = freeItemRenderers.pop();
                        }
                        else
                        {
                            renderer = createItemRenderer(data);
                            renderer.owner = this;
                            renderer.styleName = listContent;
                            listContent.addChild(DisplayObject(renderer));
                        }
                        uid = itemToUID(data);
                        rowData = makeListData(data, uid, rC - rowsToMove + i);
                        rowMap[renderer.name] = rowData;
                        if (renderer is IDropInListItemRenderer)
                            IDropInListItemRenderer(renderer).listData = data ? rowData : null;
                        renderer.data = data;
                        renderer.enabled = enabled;
                        if (data)
                        {
                            visibleData[uid] = renderer;
                            renderer.visible = true;
                        }
                        else
                            renderer.visible = false;
                        
                        renderer.explicitWidth = ww;
                        
                        //from list
                        if ((renderer is IInvalidating) && 
                            (wordWrapChanged || 
                            variableRowHeight))
                            IInvalidating(renderer).invalidateSize();

						UIComponentGlobals.layoutManager.validateClient(renderer, true);
                        
                        hh = Math.ceil(variableRowHeight ?
                             renderer.getExplicitOrMeasuredHeight() +
                             cachedPaddingTop + cachedPaddingBottom :
                             rowHeight);
                        rh = renderer.getExplicitOrMeasuredHeight();
                        renderer.setActualSize(ww, variableRowHeight ? rh : rowHeight - cachedPaddingTop - cachedPaddingBottom);
                        renderer.move(xx, yy + cachedPaddingTop);
                    }
                    else
                    {
                        // if we've run out of data, we dont make renderers
                        // and we inherit the previous row's height or rowHeight
                        // if it is the first row.
                        // EXCEPT when variable row height is on since the row 
                        // above us might be bigger then we are.  So we'll get
                        // this row out of the rowList and check it. 
                        if (!variableRowHeight)
                        {
                            hh = rowIndex + i > 0 ? rowInfo[rowIndex + i - 1].height : rowHeight;
                        }
                        else 
                        {
                            if (rowList[i]) 
                            {
                                hh = Math.ceil(rowList[i].item.getExplicitOrMeasuredHeight() +
                                        cachedPaddingTop + cachedPaddingBottom);
                            }
                            else 
                            {
                                //default
                                hh = rowHeight;
                            }
                        }
                    }
                    bSelected = selectedData[uid] != null;
                    bHighlight = highlightUID == uid;
                    bCaret = caretUID == uid;
                    tmpRowInfo = new ListRowInfo(yy, hh, uid, data);
                    rowInfo.push(tmpRowInfo);
                    if (data)
                    {
                        drawItem(renderer, bSelected, bHighlight, bCaret);
                    }
                    yy += hh;

                    if (valid) 
                    {
                        row = [];
                        row.push(renderer); 
                        listItems.push(row);
                    }
                    else
                    {
                        listItems.push([]);
                    }

                    if (more)
					{
						try
						{
							more = iterator.moveNext();
						}
						catch (e:ItemPendingError)
						{
							more = false;
						}
					}
                }

                //make indicator masks
                var maskY:Number = rowList[0].item.y - getStyle("paddingTop");
                var maskX:Number = rowList[0].item.x - getStyle("paddingLeft");
                for (i = 0; i < rowList.length; i++)
                {
                    var indicator:Object = selectionIndicators[itemToUID(rowList[i].item.data)];
                    if (indicator)
                    {
                        tmpMask = makeMask();
                        tmpMask.x = maskX;
                        tmpMask.y = maskY; 
                        tmpMask.width = rowList[i].item.width + 
                                        getStyle("paddingLeft") + 
                                        getStyle("paddingRight");  
                        tmpMask.height = rowList[i].item.y + 
                                         rowList[i].item.height +
                                         getStyle("paddingTop") + 
                                         getStyle("paddingBottom") - 
                                         maskY;
                        selectionIndicators[itemToUID(rowList[i].item.data)].mask = tmpMask;
                    }
                }
            }
            // restore the iterator
            iterator.seek(bookmark, 0);

            rC = rowList.length;
            for (i = 0; i < rC; i++)
            {
                rowList[i].itemOldY = rowList[i].item.y;
            }
            rC = listItems.length;
            for (i = rowIndex; i < rC; i++)
            {
                if (listItems[i].length)
                {
                    rowInfo[i].itemOldY = listItems[i][0].y;
                }
                rowInfo[i].oldY = rowInfo[i].y;
            }
            // slow down the tween if there's lots of rows to tween
            dur = dur * Math.max(rowsToMove / 5, 1);

            if (dispatchEvent)
                eventAfterTween = item;
                
            tween = new Tween(this, 0, (open) ? yy : startY - yy, dur, 5);
            var oE:Function = getStyle("openEasingFunction") as Function;
            if (oE != null)
                tween.easingFunction = oE;

            // Block all layout, responses from web service, and other background
            // processing until the tween finishes executing.
            UIComponent.suspendBackgroundProcessing();
            // force drawing in case there's new rows
			UIComponentGlobals.layoutManager.validateNow();
        }
        else
        {
            // not to be animated
            if (dispatchEvent)
            {
                dispatchTreeEvent(open ? TreeEvent.ITEM_OPEN : TreeEvent.ITEM_CLOSE,
                                  item,
                                  visibleData[itemToUID(item)],
                                  lastUserInteraction);
                lastUserInteraction = null;
            }
            itemsSizeChanged = true;
            invalidateDisplayList();
        }

        // If we're wordwrapping, no need to adjust maxHorizontalScrollPosition.
        // Also check if _userMaxHorizontalScrollPosition is greater than 0.
        if (!wordWrap && initialized)
        {
            super.maxHorizontalScrollPosition =
                _userMaxHorizontalScrollPosition > 0 ?
                _userMaxHorizontalScrollPosition + getIndent() :
                super.maxHorizontalScrollPosition;
        }
        //restore ItemSizeChangeNotification flag
        listContent.allowItemSizeChangeNotification = variableRowHeight;
    }

    /**
     *  @private
     */
     mx_internal function onTweenUpdate(value:Object):void
    {
        var renderer:IFlexDisplayObject;
        var n:int;
        var i:int;
        var deltaY:Number;
        var lastY:Number;

        n = listItems.length;
        var s:Sprite;
        for (i = rowIndex; i < n; i++)
        {
            //move items that are staying
            if (listItems[i].length)
            {
                renderer = IFlexDisplayObject(listItems[i][0]);
                lastY = renderer.y;
                renderer.move(renderer.x, rowInfo[i].itemOldY + value);
                deltaY = renderer.y - lastY;
            }
            //move selection graphics of the items that are staying visible
            s = selectionIndicators[rowInfo[i].uid];

            rowInfo[i].y += deltaY;

            if (s)
            {
                s.y += deltaY;
            }
        }
        //move the items that are going away.
        n = rowList.length;
        for (i = 0; i < n; i++)
        {
            s = null;
            renderer = IFlexDisplayObject(rowList[i].item);
            if (rowMap[renderer.name] != null)
            {
                s = selectionIndicators[BaseListData(rowMap[renderer.name]).uid];
            }
            lastY = renderer.y;
            renderer.move(renderer.x, rowList[i].itemOldY + value);
            deltaY = renderer.y - lastY;
            //move selection graphic for items that are going away
            if (s)
            {
                s.y += deltaY;
            }
        }
    }

    /**
     *  @private
     */
    mx_internal function onTweenEnd(value:Object):void
    {
        UIComponent.resumeBackgroundProcessing();

        onTweenUpdate(value);

        var i:int;
        var renderer:*;
        var dilir:IDropInListItemRenderer;
        var rC:int = listItems.length;
        var itemUID:*;
        var indicator:Object;

        isOpening = false;

		//dispatch collectionChange ADD or REMOVE events that correlate 
        //to the nodes that were expanded or collapsed
        if (collection)
        {
        	var eventArr:Array = opening ? 
	        		buildUpCollectionEvents(true) : buildUpCollectionEvents(false);
			for (i = 0; i < eventArr.length; i++)
			{
				collection.dispatchEvent(eventArr[i]);
			}
        }

        if (opening)
        {
            var firstDeletedRow:int = -1;
             for (i = rowIndex; i < rC; i++)
            {
                if (listItems[i].length)
                {
                    renderer = listItems[i][0];
                    var mask:DisplayObject = renderer.mask;
                    if (mask)
                    {
                        listContent.removeChild(mask);
                        renderer.mask = null;
                    }
                    rowMap[renderer.name].rowIndex = i;
                    if (renderer is IDropInListItemRenderer)
                    {
                        dilir = IDropInListItemRenderer(renderer);
                        if (dilir.listData)
                        {
                            dilir.listData.rowIndex = i;
                            dilir.listData = dilir.listData; // call the setter
                        }
                    }
                    if (renderer.y > listContent.height)
                    {
                        addToFreeItemRenderers(renderer);
                        itemUID = itemToUID(renderer.data);
                        if (selectionIndicators[itemUID])
                        {
                            //remove indicators mask
                            indicator = selectionIndicators[itemUID];
                            if (indicator)
                            {
                                mask = indicator.mask;
                                if (mask)
                                {
                                    listContent.removeChild(mask);
                                    indicator.mask = null;
                                }
                            }
                            removeIndicators(itemUID);
                        }
                        delete rowMap[renderer.name];
                        if (firstDeletedRow < 0)
                            firstDeletedRow = i;
                    }
                }
                else
                {
                    if (rowInfo[i].y >= listContent.height)
                    {
                        if (firstDeletedRow < 0)
                            firstDeletedRow = i;
                    }
                }
            }
            if (firstDeletedRow >= 0)
            {
                rowInfo.splice(firstDeletedRow);
                listItems.splice(firstDeletedRow);
            }
        }
        else //closing
        {
            for (i = 0; i < rowList.length; i++)
            {
                mask = rowList[i].item.mask;
                if (mask)
                {
                    listContent.removeChild(mask);
                    rowList[i].item.mask = null;
                }
                addToFreeItemRenderers(rowList[i].item);
                //kill graphic and graphic mask if necessary
                itemUID = itemToUID(rowList[i].item.data);
                if (selectionIndicators[itemUID])
                {
                    //remove indicators mask
                    indicator = selectionIndicators[itemUID];
                    if (indicator)
                    {
                        mask = indicator.mask;
                        if (mask)
                        {
                            listContent.removeChild(mask);
                            indicator.mask = null;
                        }
                    }
                    removeIndicators(itemUID);
                }
                delete rowMap[rowList[i].item.name];
            }
            for (i = rowIndex; i < rC; i++)
            {
                if (listItems[i].length)
                {
                    renderer = listItems[i][0];
                    rowMap[renderer.name].rowIndex = i;
                    if (renderer is IDropInListItemRenderer)
                    {
                        dilir = IDropInListItemRenderer(renderer);
                        if (dilir.listData)
                        {
                            dilir.listData.rowIndex = i;
                            dilir.listData = dilir.listData; // call the setter
                        }
                    }
                }
            }
        }
        
        //should we dispatch a tree event?
        if (eventAfterTween)
        {
            dispatchTreeEvent((isItemOpen(eventAfterTween)
                               ? TreeEvent.ITEM_OPEN
                               : TreeEvent.ITEM_CLOSE),
                              eventAfterTween,
                              visibleData[itemToUID(eventAfterTween)],
                              lastUserInteraction);
            lastUserInteraction = null;
            eventAfterTween = false;
        }
        //invalidate
        itemsSizeChanged = true;
        invalidateDisplayList();
        // Get rid of the tween, so this onTweenEnd doesn't get called more than once.
        tween = null;
    }

	/**
	 *  @private
	 * 
	 *  Helper function that builds up the collectionChange ADD or 
	 *  REMOVE events that correlate to the nodes that were expanded 
	 *  or collapsed. 
	 */
	private function buildUpCollectionEvents(open:Boolean):Array
    {
    	var ce:CollectionEvent;
    	var i:int;
    	var item:Object;
    	var parentArray:Array;
    	var rowsAdded:Array = [];
    	var rowsRemoved:Array = [];
    	var retVal:Array = [];
    	
    	var itemIndex:int = getItemIndex(expandedItem);
    	
    	if (open)
    	{
    		var children:ICollectionView = getChildren(expandedItem, iterator.view);
    		if (!children)
    			return [];
	    	var cursor:IViewCursor = children.createCursor();
    		var push:Boolean = true;
    		while (!cursor.afterLast)
    		{
				rowsAdded.push(cursor.current);
    			cursor.moveNext();
    		}
    	}
    	else 
    	{
    		var stack:Array = [];
    		var j:int = 0;
    		stack = getOpenChildrenStack(expandedItem, stack);
    		while (j < stack.length)
    		{
    			for (i = 0; i < selectedItems.length; i++)
    			{
    				if (selectedItems[i] == stack[j])
    				{
    					bSelectedItemRemoved = true;
    				}
    			}
    			rowsRemoved.push(stack[j]);
    			j++;
    		}
    	}
    	if (rowsAdded.length > 0)
    	{
    		ce = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
    		ce.kind = CollectionEventKind.ADD;
    		ce.location = itemIndex + 1;
    		ce.items = rowsAdded;
    		retVal.push(ce);
    	}
    	if (rowsRemoved.length > 0)
    	{
    		ce = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
    		ce.kind = CollectionEventKind.REMOVE;
    		ce.location = itemIndex + 1;
    		ce.items = rowsRemoved;
  			retVal.push(ce);
    	}
    	return retVal;
    }

    /**
     *  @private
     *  Go through the open items and figure out which is deepest.
     */
    private function getIndent():Number
    {
        var depth:Number = 0;
        for (var p:String in _openItems)
        {
            // add one since its children are actually indented
            depth = Math.max(getParentStack(_openItems[p]).length + 1, depth);
        }
        return depth * getStyle("indentation");
    }

    /**
    *   Checks to see if item is visible in the list
     *  @private
     */
    override public function isItemVisible(item:Object):Boolean
    {
        //first check visible data
        if (visibleData[itemToUID(item)])
            return true;

        //then check parent items
        var parentItem:Object = getParentItem(item);
        if (parentItem)
        {
            var uid:String = itemToUID(parentItem);
            if (visibleData[uid] && _openItems[uid])
            {
                return true;
            }
        }
        return false;
    }

    /**
    *  @private
    */
    public function getItemIndex(item:Object):int
    {
        var cursor:IViewCursor = collection.createCursor();
        var i:int = 0;
        do
        {
            if (cursor.current === item)
                break;
            i++;
        }
        while (cursor.moveNext());
		// set back to 0 in case a change event comes along
		// and causes the cursor to hit an unexpected IPE
		cursor.seek(CursorBookmark.FIRST, 0);
        return i;
    }

	/**
    *  @private
    */
    private function getIndexItem(index:int):Object
    {
        var cursor:IViewCursor = collection.createCursor();
        var i:int = index;
        while (cursor.moveNext())
        {
        	if (i == 0)
        		return cursor.current;
        	i--;
        }
        return null;
    }

    /**
     *  Opens or closes all the tree items below the specified item.
     * 
     *  If you set <code>dataProvider</code> and then immediately call
     *  <code>expandChildrenOf()</code> you may not see the correct behavior. 
     *  You should either wait for the component to validate
     *  or call the <code>validateNow()</code> method.
     *  
     *  @param item The starting item.
     *
     *  @param open Toggles an open or close operation. 
     *  Specify <code>true</code> to open the items, and <code>false</code> to close them.
     */
    public function expandChildrenOf(item:Object, open:Boolean):void
    {
        //if the iterator is null, that indicates we have not been 
        //validated yet, so we will not continue. 
        if (iterator == null)
            return;
        
        // if it is not a branch item there's nothing to do
        if (isBranch(item))
        {
            dispatchTreeEvent(TreeEvent.ITEM_OPENING,
                                    item,   //item
                                    null,   //renderer
                                    null,   //trigger
                                    open,   //opening
                                    false,  //animate
                                    true);  //dispatch

            var childItems:ICollectionView;
            if (item != null &&
                _dataDescriptor.isBranch(item, iterator.view) &&
                _dataDescriptor.hasChildren(item, iterator.view)) 
            {
                childItems = getChildren(item, iterator.view);
            }
            if (childItems)
            {
		    	var cursor:IViewCursor = childItems.createCursor();
    			while (!cursor.afterLast)
    			{
                    if (isBranch(cursor.current))
                        expandChildrenOf(cursor.current, open);
    				cursor.moveNext();
    			}
            }
        }
    }

    /**
     *  Returns the known parent of a child item. This method returns a value
     *  only if the item was or is currently visible. Top level items have a 
     *  parent with the value <code>null</code>. 
     * 
     *  @param The item for which to get the parent.
     * 
     *  @return The parent of the item.
     */
    public function getParentItem(item:Object):*
    {
        if (item == null)
            return null;
        if (item && collection)
		{
			if (_dataDescriptor is ITreeDataDescriptor2)
				return ITreeDataDescriptor2(_dataDescriptor).getParent(item, wrappedCollection, _rootModel);
			return HierarchicalCollectionView(collection).getParentItem(item);
		}
        return null;
    }
    
    /**
     *  @private
     *  Returns the stack of parents from a child item. 
     */
    private function getParentStack(item:Object):Array
    {
        var stack:Array = [];
        if (item == null)
            return stack;
        
        var parent:* = getParentItem(item);
        while (parent)
        {
            stack.push(parent);
            parent = getParentItem(parent);
        }
        return stack;       
    }

	/**
     *  @private
     *  Returns a stack of all open descendants of an item. 
     */
    private function getOpenChildrenStack(item:Object, stack:Array):Array
    {
        var curr:Object;
        if (item == null)
            return stack;
        
        var children:ICollectionView = getChildren(item, iterator.view);
        if (!children)
        	return [];
       	var cursor:IViewCursor = children.createCursor();
        while (!cursor.afterLast)
        {
        	curr = cursor.current;
        	stack.push(curr);
	        if (isBranch(curr) && isItemOpen(curr))
	        {
	        	getOpenChildrenStack(curr, stack);
	        }
        	cursor.moveNext();
        }
        return stack;       
    }

    /**
     *  @private
     *  Finds the index distance between a parent and child
     */
    private function getChildIndexInParent(parent:Object, child:Object):int
    {
        var index:int = 0;
        if (!parent)
        {
            var cursor:IViewCursor = ICollectionView(iterator.view).createCursor();
            while (!cursor.afterLast)
            {
                if (child === cursor.current)
                    break;
                index++;
                cursor.moveNext();
            }
        }
        else
        {
            if (parent != null && 
                _dataDescriptor.isBranch(parent, iterator.view) &&
                _dataDescriptor.hasChildren(parent, iterator.view))
            {
                var children:ICollectionView = getChildren(parent, iterator.view);
                if (children.contains(child))
                {
			    	cursor = children.createCursor();
					while (!cursor.afterLast)
    				{
                        if (child === cursor.current)
							break;
    					cursor.moveNext();
						index++;
    				}

                }
                else 
                {
                    //throw new Error("Parent item does not contain specified child: " + itemToUID(child));
                }
            }
        }
        return index;
    }

	/**
     *  @private
     *  Collapses those items in the selected items array that have
     *  parent nodes already selected. 
     */
	private function collapseSelectedItems():Array
	{	
		var collection:ArrayCollection = new ArrayCollection(selectedItems);
		
		for (var i:int = 0; i < selectedItems.length; i++)
		{
			var item:Object = selectedItems[i];
			var stack:Array = getParentStack(item);
			for (var j:int = 0; j < stack.length; j++)
			{
				if (collection.contains(stack[j]))
				{
					//item's parent is included in the selected item set
					var index:int = collection.getItemIndex(item);
					var removed:Object = collection.removeItemAt(index);
					break;
				}
			}
		}
		return collection.source;
	}

    /**
     *  @private
     */
    private function updateDropData(event:DragEvent):void
    {
        var rowCount:int = rowInfo.length;
        var rowNum:int = 0;
        var yy:int = rowInfo[rowNum].height;
        var pt:Point = globalToLocal(new Point(event.stageX, event.stageY));
		while (rowInfo[rowNum] && pt.y >= yy)
		{
		    if (rowNum != rowInfo.length-1)
		    {
		    	rowNum++;
		    	yy += rowInfo[rowNum].height;
		    }
		    else
		    {
		    	// now we're past all rows.  adding a pixel or two should be enough.
		    	// at this point yOffset doesn't really matter b/c we're past all elements
		    	// but might as well try to keep it somewhat correct
		    	yy += rowInfo[rowNum].height;
		    	rowNum++;
		    }
		}
        
        var lastRowY:Number = rowNum < rowInfo.length ? rowInfo[rowNum].y : (rowInfo[rowNum-1].y + rowInfo[rowNum-1].height);
        var yOffset:Number = pt.y - lastRowY;
        var rowHeight:Number = rowNum < rowInfo.length ? rowInfo[rowNum].height : rowInfo[rowNum-1].height;

        rowNum += verticalScrollPosition;

        var parent:Object;
        var index:int;
        var emptyFolder:Boolean = false;
        var numItems:int = collection ? collection.length : 0;

        var topItem:Object = (rowNum > _verticalScrollPosition && rowNum <= numItems) ? 
        					 listItems[rowNum - _verticalScrollPosition - 1][0].data : null;
        var bottomItem:Object = (rowNum - verticalScrollPosition < rowInfo.length && rowNum < numItems) ? 
        						listItems[rowNum - _verticalScrollPosition][0].data  : null;

        var topParent:Object = collection ? getParentItem(topItem) : null;
        var bottomParent:Object = collection ? getParentItem(bottomItem) : null;

        // check their relationship
        if (yOffset > rowHeight * .5 && 
            isItemOpen(bottomItem) &&
            _dataDescriptor.isBranch(bottomItem, iterator.view) &&
            (!_dataDescriptor.hasChildren(bottomItem, iterator.view) ||
			  _dataDescriptor.getChildren(bottomItem, iterator.view).length == 0))
        {
            // we'll get here if we're dropping into an empty folder.
            // we have to be in the lower 50% of the row, otherwise
            // we're "between" rows.
            parent = bottomItem;
            index = 0;
            emptyFolder = true;
        }
        else if (!topItem && !rowNum == rowCount)
        {
            parent = collection ? getParentItem(bottomItem) : null;
            index =  bottomItem ? getChildIndexInParent(parent, bottomItem) : 0;
            rowNum = 0;
        }
        else if (bottomItem && bottomParent == topItem)
        {
            // we're dropping in the first item of a folder, that's an easy one
            parent = topItem;
            index = 0;
        }
        else if (topItem && bottomItem && topParent == bottomParent)
        {
            parent = collection ? getParentItem(topItem) : null;
            index = iterator ? getChildIndexInParent(parent, bottomItem) : 0;
        }
        else
        {
            //we're dropping at the end of a folder.  Pay attention to the position.
            if (topItem && (yOffset < (rowHeight * .5)))
            {
                // ok, we're on the top half of the bottomItem.
                parent = topParent;
                index = getChildIndexInParent(parent, topItem) + 1; // insert after
            }
            else if (!bottomItem)
            {
                parent = null;
                if ((rowNum - verticalScrollPosition) == 0)
                    index = 0;
                else if (collection)
                    index = collection.length;
                else index = 0;
            }
            else
            {
                parent = bottomParent;
                index = getChildIndexInParent(parent, bottomItem);
            }
        }
        _dropData = { parent: parent, index: index, localX: event.localX, localY: event.localY, 
                        emptyFolder: emptyFolder, rowHeight: rowHeight, rowIndex: rowNum };
    }

    /**
     *  Initializes a TreeListData object that is used by the tree item renderer.
     * 
     *  @param item The item to be rendered.
     *  @param treeListData The TreeListDataItem to use in rendering the item.
     */
    protected function initListData(item:Object, treeListData:TreeListData):void
    {
        if (item == null)
            return;

        var open:Boolean = isItemOpen(item);
        var branch:Boolean = isBranch(item);
        var uid:String = itemToUID(item);

        // this is hidden by non-branches but kept so we know how wide it is so things align
        treeListData.disclosureIcon = getStyle(open ? "disclosureOpenIcon" :
                                                      "disclosureClosedIcon");
        treeListData.open = open;
        treeListData.hasChildren = branch;
        treeListData.depth = getItemDepth(item, treeListData.rowIndex);
        treeListData.indent = (treeListData.depth - 1) * getStyle("indentation");
        treeListData.item = item;
        treeListData.icon = itemToIcon(item);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function layoutEditor(x:int, y:int, w:int, h:int):void
    {
        var indent:int = rowMap[editedItemRenderer.name].indent;
        itemEditorInstance.move(x + indent, y);
        itemEditorInstance.setActualSize(w - indent, h);
    }

    /**
     *  @private
     */
    override protected function scrollHandler(event:Event):void
    {
        if (isOpening)
            return;

        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
            super.scrollHandler(event);
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
    	var evt:ListEvent;
    	var pt:Point;
    	
        if (isOpening)
        {
            event.stopImmediatePropagation();
            return;
        }

        if (itemEditorInstance)
            return;

        // Keyboard handling is consistent with Windows Explorer.
        var item:Object = selectedItem;
        if (event.ctrlKey)
        {
            // Ctrl keys always get sent to the List.
            super.keyDownHandler(event);
        }
        else if (event.keyCode == Keyboard.SPACE)
        {
            // if user has moved the caret cursor from the selected item
            // move the cursor back to selected item
            if (caretIndex != selectedIndex)
            {
                // erase the caret
                var renderer:IListItemRenderer = indexToItemRenderer(caretIndex);
                if (renderer)
                    drawItem(renderer);
                caretIndex = selectedIndex;
            }

            // Spacebar toggles the current open/closed status. No effect for leaf items.
            if (isBranch(item))
            {
                var o:Boolean = !isItemOpen(item);
                dispatchTreeEvent(TreeEvent.ITEM_OPENING,
                                    item,   //item
                                    null,   //renderer
                                    event,  //trigger
                                    o,      //opening
                                    true,   //animate
                                    true);  //dispatch
            }
            event.stopImmediatePropagation();
        }
        else if (event.keyCode == Keyboard.LEFT)
        {
            // Left Arrow closes an open item.
            // Otherwise, selects the parent item if there is one.
            if (isItemOpen(item))
            {
                dispatchTreeEvent(TreeEvent.ITEM_OPENING,
                                    item,   //item
                                    null,   //renderer
                                    event,  //trigger
                                    false,  //opening
                                    true,   //animate
                                    true)   //dispatch
            }
            else
            {
            	var parentItem:Object = getParentItem(item);
            	if (parentItem)
            	{
					proposedSelectedItem = parentItem;
					finishArrowKeySelection();
	            }
            }
            event.stopImmediatePropagation();
        }
        else if (event.keyCode == Keyboard.RIGHT)
        {
            // Right Arrow has no effect on leaf items. Closed branch items are opened. 
            //Opened branch items select the first child.
            if (isBranch(item)) 
            {
                if (isItemOpen(item))
                {
                	if (item)
                	{
                		var children:ICollectionView = getChildren(item, iterator.view);
                		if (children)
                		{
                			var cursor:IViewCursor  = children.createCursor();
							if (cursor.current)
                				proposedSelectedItem = cursor.current;
                		}
						else
							proposedSelectedItem = null;
                	}
                	else 
                		selectedItem = proposedSelectedItem = null;
					
					finishArrowKeySelection();
                }
                else
                {
                    dispatchTreeEvent(TreeEvent.ITEM_OPENING,
                                        item,   //item
                                        null,   //renderer
                                        event,  //trigger
                                        true,   //opening
                                        true,   //animate
                                        true);  //dispatch
                }
            }
            event.stopImmediatePropagation();
        }
        else if (event.keyCode == Keyboard.NUMPAD_MULTIPLY)
        {
            expandChildrenOf(item, !isItemOpen(item));
        }
        else if (event.keyCode == Keyboard.NUMPAD_ADD)
        {
            if (isBranch(item))
            {
                if (!isItemOpen(item))
                {
                    dispatchTreeEvent(TreeEvent.ITEM_OPENING,
                                        item,   //item
                                        null,   //renderer
                                        event,  //trigger
                                        true,   //opening
                                        true,   //animate
                                        true);   //dispatch
                }
            }
        }
        else if (event.keyCode == Keyboard.NUMPAD_SUBTRACT)
        {
            if (isItemOpen(item))
            {
                dispatchTreeEvent(TreeEvent.ITEM_OPENING,
                                    item, //item
                                    null,   //renderer
                                    event,  //trigger
                                    false,  //opening
                                    true,   //animate
                                    true);   //dispatch
            }
        }
        else
        {
            // Nothing that we know or care about. Send it off to the List.
            super.keyDownHandler(event);
        }
    }

    /**
     *  @private
     *  finish up left/right arrow key handling
     */
    private function finishArrowKeySelection():void
	{
		bFinishArrowKeySelection = false;

		if (proposedSelectedItem)
			selectedItem = proposedSelectedItem;

		// now test to see if it worked, if it didn't we probably
		// got an IPE
		if (selectedItem === proposedSelectedItem || !proposedSelectedItem)
		{
			var evt:ListEvent;
			var pt:Point;
			evt = new ListEvent(ListEvent.CHANGE);
			evt.itemRenderer = indexToItemRenderer(selectedIndex);
			pt = itemRendererToIndices(evt.itemRenderer);
			if (pt)
			{
				evt.rowIndex = pt.y;
				evt.columnIndex = pt.x;
			}
			dispatchEvent(evt);
			var dI:int = getItemIndex(selectedItem);
			if (dI != caretIndex)
			{
				caretIndex = selectedIndex;
			}
			if (dI < _verticalScrollPosition)
			{
				verticalScrollPosition = dI;
			}
		}
		else
		{
			bFinishArrowKeySelection = true;
		}
	}


    /**
     *  @private
     *  Blocks mouse events on items that are tweening away and are invalid for input
     */
    override protected function mouseOverHandler(event:MouseEvent):void
    {
        if (!tween)
            super.mouseOverHandler(event);
    }

    /**
     *  @private
     *  Blocks mouse events on items that are tweening away and are invalid for input
     */
    override protected function mouseOutHandler(event:MouseEvent):void
    {
        if (!tween)
            super.mouseOutHandler(event);
    }

    /**
     *  @private
     *  Blocks mouse events on items that are tweening away and are invalid for input
     */
    override protected function mouseClickHandler(event:MouseEvent):void
    {
        if (!tween)
            super.mouseClickHandler(event);
    }

    /**
     *  @private
     *  Blocks mouse events on items that are tweening away and are invalid for input
     */
    override protected function mouseDoubleClickHandler(event:MouseEvent):void
    {
        if (!tween)
            super.mouseDoubleClickHandler(event);
    }

    /**
     *  @private
     *  Blocks mouse events on items that are tweening away and are invalid for input
     */
    override protected function mouseDownHandler(event:MouseEvent):void
    {
        if (!tween)
            super.mouseDownHandler(event);
    }

    /**
     *  @private
     *  Blocks mouse events on items that are tweening away and are invalid for input
     */
    override protected function mouseUpHandler(event:MouseEvent):void
    {
        if (!tween)
            super.mouseUpHandler(event);
    }

    /**
     *  @private
     *  Blocks mouse wheel handling while tween is running
     */
    override protected function mouseWheelHandler(event:MouseEvent):void
    {
    	if (!tween)
        	super.mouseWheelHandler(event);
    }

    /**
     *  @private
     */
    override protected function dragEnterHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;

		lastDragEvent = event;
		haveItemIndices = false;

		try 
		{
			if (iteratorValid && event.dragSource.hasFormat("treeItems"))
			{
				//if (collectionThrowsIPE)
					//checkItemIndices(event);

				DragManager.acceptDragDrop(this);
				DragManager.showFeedback(event.ctrlKey ?
										 DragManager.COPY :
										 DragManager.MOVE);
				showDropFeedback(event);
				return;
			}
		}
        catch(e:ItemPendingError)
        {
			if (!lastTreeSeekPending)
			{
				lastTreeSeekPending = new TreeSeekPending(event, dragEnterHandler)
				e.addResponder(new ItemResponder(seekPendingDuringDragResultHandler, seekPendingDuringDragFailureHandler,
                                            lastTreeSeekPending));
			}
        }
		catch(e1:Error)
		{
		}
        hideDropFeedback(event);
        DragManager.showFeedback(DragManager.NONE);
    }

    /**
     *  @private
     */
    override protected function dragOverHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;

		lastDragEvent = event;

		try
		{
			if (iteratorValid && event.dragSource.hasFormat("treeItems"))
			{
				if (collectionThrowsIPE)
					checkItemIndices(event);

				DragManager.showFeedback(event.ctrlKey ?
										 DragManager.COPY :
										 DragManager.MOVE);
				showDropFeedback(event);
				return;
			}
		}
        catch(e:ItemPendingError)
        {
			if (!lastTreeSeekPending)
			{
				lastTreeSeekPending = new TreeSeekPending(event, dragOverHandler)
				e.addResponder(new ItemResponder(seekPendingDuringDragResultHandler, seekPendingDuringDragFailureHandler,
                                            lastTreeSeekPending));
			}
        }
		catch(e1:Error)
		{
		}
        hideDropFeedback(event);
        DragManager.showFeedback(DragManager.NONE);
    }

    /**
	 *  @private
     *  The default failure handler when a seek fails due to a page fault.
     */
    private function seekPendingDuringDragFailureHandler(data:Object,
                                                 info:TreeSeekPending):void
    {
    }

    /**
	 *  @private
     *  The default result handler when a seek fails due to a page fault.
     *  This method re-attempts setting the drag feedback
     */
    private function seekPendingDuringDragResultHandler(data:Object,
                                                info:TreeSeekPending):void
    {
		lastTreeSeekPending = null;

		if (lastDragEvent)
			info.retryFunction(info.event);
    }

    /**
	 *  @private
     */
    private function checkItemIndices(event:DragEvent):void
    {
		if (haveItemIndices)
			return;

		// if we're moving to ourselves, we need to make sure we have
		// everything paged below before we allow a drop
		if ((event.action == DragManager.MOVE || event.action == DragManager.NONE) && dragMoveEnabled)
		{
			if (event.dragInitiator == this)
			{
	        	var items:Array = event.dragSource.dataForFormat("treeItems") as Array;
				var n:int = items.length;
				for (var i:int = 0; i < n; i++) 
				{
					var parent:Object = getParentItem(items[i]);
					getChildIndexInParent(parent, items[i]);
				}
				haveItemIndices = true;
			}
		}
	}

    /**
     *  Handles <code>DragEvent.DRAG_DROP events</code>.  This method  hides
     *  the drop feedback by calling the <code>hideDropFeedback()</code> method.
     *
     *  <p>If the action is a <code>COPY</code>, 
     *  then this method makes a deep copy of the object 
     *  by calling the <code>ObjectUtil.copy()</code> method, 
     *  and replaces the copy's <code>uid</code> property (if present) 
     *  with a new value by calling the <code>UIDUtil.createUID()</code> method.</p>
     * 
     *  @param event The DragEvent object.
     *
     *  @see mx.utils.ObjectUtil
     *  @see mx.utils.UIDUtil
     */
    override protected function dragDropHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;

        hideDropFeedback(event);

		if (event.dragSource.hasFormat("treeItems"))
		{
        	var items:Array = event.dragSource.dataForFormat("treeItems") as Array;
        	var i:int;
        	var n:int;
            	
			// if we're moving to ourselves, we need to treat it specially and check for "parent" 
			// problems where we could recurse forever.
			if (event.action == DragManager.MOVE && dragMoveEnabled)
			{
				if (event.dragInitiator == this)
				{
					// If we're dropping onto ourselves or a child of a descendant then dont actually drop
					
					calculateDropIndex(event);
									
					// If we did start this drag op then we need to remove first
					var index:int;
					var parent:*;
					var parentItem:*;
					var dropIndex:int = _dropData.index;
                
					//get ancestors of the drop target item
					var dropParentStack:Array = getParentStack(_dropData.parent);
					dropParentStack.unshift(_dropData.parent);
                
					n = items.length;
					for (i = 0; i < n; i++) 
					{ 
						parent = getParentItem(items[i]);
						index = getChildIndexInParent(parent, items[i]);
						//check ancestors of the dropTarget if the item matches, we're invalid
                    
						for each (parentItem in dropParentStack)
						{ 
							//we dont want to drop into one of our own sets of children
							if (items[i] === parentItem)
								return;
						}
                    
						//we remove before we add due to the behavior 
						//of structures with parent pointers like e4x
						removeChildItem(parent, items[i], index);
                    
						//is the removed item before the drop location?
						// then we need to shift the dropIndex accordingly
						if (parent == _dropData.parent && index < _dropData.index)
                        	dropIndex--;
                    
						addChildItem(_dropData.parent, items[i], dropIndex);
					}
                
					return;
				}
			}
        
			// If not dropping onto ourselves, then add the 
			// items here if it's a copy operation.
			// If it's a move operation (and not on ourselves), then they 
			// are added in dragCompleteHandler and are removed from 
			// the source's dragCompleteHandler.  We do both in dragCompleteHandler
			// because in order to be re-parented, they must be removed from their
			// original source FIRST.  This means our code isn't coupled fantastically 
			// as dragCompleteHandler must get the destination tree and 
			// cast it to a Tree.
        
			if (event.action == DragManager.COPY)
			{
				if (!dataProvider) {
					// Create an empty collection to drop items into.
					dataProvider = [];
					validateNow();
				}
				
				n = items.length;
				for (i = 0; i < n; i++) 
				{ 
	            	var item:Object = copyItemWithUID(items[i]);
	            	
					addChildItem(_dropData.parent, 
	                   			 item, 
	                       		 _dropData.index);
				}
	    	}
		}
		lastDragEvent = null;
    }

    /**
     *  Handles <code>DragEvent.DRAG_COMPLETE</code> events.  This method
     *  removes the item from the data provider.
     *
     *  @param event The DragEvent object.
     */
    override protected function dragCompleteHandler(event:DragEvent):void
    {
        isPressed = false;

        if (event.isDefaultPrevented())
            return;

		resetDragScrolling()

		try
		{
			if (event.dragSource.hasFormat("treeItems"))
			{
				// if we've moved the elements, then remove them here
				if (event.action == DragManager.MOVE && dragMoveEnabled)
				{
            		// if we moved onto ourselves, we already handled this in
            		// dragDropHandler
					if (event.relatedObject != this)
					{
						var items:Array = event.dragSource.dataForFormat("treeItems") as Array;
						var parent:*;
						var index:int;
						var i:int;
						var n:int;
                
						//do the remove
						n = items.length;
						for (i = 0; i < n; i++)
						{
							parent = getParentItem(items[i]);
							index = getChildIndexInParent(parent, items[i]);
							removeChildItem(parent, items[i], index);
						}
                
						// then add it to the target control (copy operations are
						// handled in the target's dragDropHandler), but the MOVE
						// operations need to be handled here (see comment in 
						// dragDropHandler about this)
						if (event.relatedObject is Tree)
						{
							var targetTree:Tree = Tree(event.relatedObject);
							if (!targetTree.dataProvider) {
								// Create an empty collection to drop items into.
								targetTree.dataProvider = [];
								targetTree.validateNow();
							}
							
							n = items.length;
							for (i = 0; i < n; i++) 
                    		{ 
			            		var item:Object = items[i];
			            		
								targetTree.addChildItem(targetTree._dropData.parent, 
			                   	         				item, 
			                       	     				targetTree._dropData.index);
							}
						}
					}
					clearSelected(false);
				}
			}
		}
        catch(e:ItemPendingError)
        {
            e.addResponder(new ItemResponder(seekPendingDuringDragResultHandler, seekPendingDuringDragFailureHandler,
                                            new TreeSeekPending(event, dragCompleteHandler)));
        }
		lastDragEvent = null;
    }

    /**
     *  @private
     *  Delegates to the Descriptor to add a child to a parent
     */
    mx_internal function addChildItem(parent:Object, child:Object, index:Number):Boolean
    {
        return _dataDescriptor.addChildAt(parent, child, index, iterator.view);
    }

    /**
     *  @private
     *  Delegates to the Descriptor to remove a child from a parent
     */
    mx_internal function removeChildItem(parent:Object, child:Object, index:Number):Boolean
    {
        return _dataDescriptor.removeChildAt(parent, child, index, iterator.view);
    }

    /**
     *  @private
     */
    mx_internal function dispatchTreeEvent(type:String,
                                           item:Object,
                                           renderer:IListItemRenderer,
                                           trigger:Event = null,
                                           opening:Boolean = true, 
                                           animate:Boolean = true,
                                           dispatch:Boolean = true):void
    {
        var event:TreeEvent;
        
        // Create expanding event.
        if (type == TreeEvent.ITEM_OPENING)
        {
            event = new TreeEvent(TreeEvent.ITEM_OPENING,
                                  false, true);
            event.opening = opening;
            event.animate = animate;
            event.dispatchEvent = dispatch;
        }
        
        // Create all other events.
        if (!event) 
            event = new TreeEvent(type);
        
        // Common values.
        event.item = item;
        event.itemRenderer = renderer;
        event.triggerEvent = trigger;
        
        // Send it off.
        dispatchEvent(event);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Handler for CollectionEvents dispatched from the root dataProvider as the data changes.
     */
    override protected function collectionChangeHandler(event:Event):void
    {
        //if the iterator is null that indicates we havent been validated yet so we'll bail. 
        if (iterator == null)
            return;
            
        var node:Object;
        var parent:Object;
            
        if (event is CollectionEvent)
        {
            var ce:CollectionEvent = CollectionEvent(event);
            
            if (ce.kind == CollectionEventKind.mx_internal::EXPAND)
            {
                //we ignore expand in list/tree
                event.stopPropagation();
            }
            if (ce.kind == CollectionEventKind.UPDATE)
            {
                //this prevents listbase from invalidating the displaylist too early. 
                event.stopPropagation();
                //we only want to update the displaylist if an updated item was visible
                //but dont have a sufficient test for that yet
                itemsSizeChanged = true;
                invalidateDisplayList();
            }
            else
            {
                super.collectionChangeHandler(event);
            }
        }
    }
    
    /**
     *  @private
     */
	override protected function adjustAfterRemove(items:Array, location:int, emitEvent:Boolean):Boolean
    {
    	var indicesLength:int = selectedItems.length;
    	var requiresValueCommit:Boolean = emitEvent;
        var length:int = items.length;
        
        if (_selectedIndex > location)
        {
            _selectedIndex -= length;
            requiresValueCommit = true;
        }
        
        if (bSelectedItemRemoved && indicesLength < 1)
        {
        	_selectedIndex = getItemIndex(expandedItem);
        	requiresValueCommit = true;
        	bSelectionChanged = true;
            bSelectedIndexChanged = true;
            invalidateDisplayList();
        }
                
        return requiresValueCommit;
    }
    
    /**
     *
     */
    mx_internal function expandItemHandler(event:TreeEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        if (event.type == TreeEvent.ITEM_OPENING)
        {
            expandItem(event.item, event.opening, event.animate, event.dispatchEvent, event.triggerEvent);
        }
    }

    /**
     *
     */
    override mx_internal function selectionDataPendingResultHandler(
                                    data:Object,
                                    info:ListBaseSelectionDataPending):void
    {
		super.selectionDataPendingResultHandler(data, info);
		if (bFinishArrowKeySelection && selectedItem === proposedSelectedItem)
			finishArrowKeySelection();
	}
}

}

import mx.events.DragEvent;

class TreeSeekPending
{
	public function TreeSeekPending(event:DragEvent, retryFunction:Function)
	{
		this.event = event;
		this.retryFunction = retryFunction;
	}

	public var event:DragEvent;

	public var retryFunction:Function;

}
