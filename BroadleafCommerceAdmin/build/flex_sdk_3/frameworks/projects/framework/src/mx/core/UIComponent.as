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

package mx.core
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.GradientType;
import flash.display.Graphics;
import flash.display.InteractiveObject;
import flash.display.Loader;
import flash.display.Sprite;
import flash.display.Stage;
import flash.events.Event;
import flash.events.EventPhase;
import flash.events.FocusEvent;
import flash.events.IEventDispatcher;
import flash.events.KeyboardEvent;
import flash.geom.Matrix;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.system.ApplicationDomain;
import flash.text.TextLineMetrics;
import flash.utils.getQualifiedClassName;
import flash.utils.getQualifiedSuperclassName;

import mx.automation.IAutomationObject;
import mx.binding.BindingManager;
import mx.controls.IFlexContextMenu;
import mx.effects.EffectManager;
import mx.effects.IEffect;
import mx.effects.IEffectInstance;
import mx.events.ChildExistenceChangedEvent;
import mx.events.DragEvent;
import mx.events.DynamicEvent;
import mx.events.EffectEvent;
import mx.events.FlexEvent;
import mx.events.MoveEvent;
import mx.events.PropertyChangeEvent;
import mx.events.ResizeEvent;
import mx.events.StateChangeEvent;
import mx.events.ToolTipEvent;
import mx.events.ValidationResultEvent;
import mx.graphics.RoundedRectangle;
import mx.managers.CursorManager;
import mx.managers.ICursorManager;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.managers.IFocusManagerContainer;
import mx.managers.ILayoutManagerClient;
import mx.managers.ISystemManager;
import mx.managers.IToolTipManagerClient;
import mx.managers.SystemManager;
import mx.managers.SystemManagerGlobals;
import mx.managers.ToolTipManager;
import mx.modules.ModuleManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.states.State;
import mx.states.Transition;
import mx.styles.CSSStyleDeclaration;
import mx.styles.ISimpleStyleClient;
import mx.styles.IStyleClient;
import mx.styles.StyleManager;
import mx.styles.StyleProtoChain;
import mx.utils.ColorUtil;
import mx.utils.GraphicsUtil;
import mx.utils.StringUtil;
import mx.validators.IValidatorListener;
import mx.validators.ValidationResult;

use namespace mx_internal;

//--------------------------------------
//  Lifecycle events
//--------------------------------------

/**
 *  Dispatched when the component is added to a container as a content child
 *  by using the <code>addChild()</code> or <code>addChildAt()</code> method. 
 *  If the component is added to the container as a noncontent child by 
 *  using the <code>rawChildren.addChild()</code> or 
 *  <code>rawChildren.addChildAt()</code> method, the event is not dispatched.
 * 
 *  @eventType mx.events.FlexEvent.ADD
 */
[Event(name="add", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the component has finished its construction,
 *  property processing, measuring, layout, and drawing.
 *
 *  <p>At this point, depending on its <code>visible</code> property,
 *  the component may not be visible even though it has been drawn.</p>
 *
 *  @eventType mx.events.FlexEvent.CREATION_COMPLETE
 */
[Event(name="creationComplete", type="mx.events.FlexEvent")]

/**
 *  Dispatched when an object has had its <code>commitProperties()</code>, 
 *  <code>measure()</code>, and
 *  <code>updateDisplayList()</code> methods called (if needed).
 *
 *  <p>This is the last opportunity to alter the component before it is
 *  displayed. All properties have been committed and the component has
 *  been measured and layed out.</p>
 *
 *  @eventType mx.events.FlexEvent.UPDATE_COMPLETE
 */
[Event(name="updateComplete", type="mx.events.FlexEvent")]

/**
 *  Dispatched when an object's state changes from visible to invisible.
 *
 *  @eventType mx.events.FlexEvent.HIDE
 */
[Event(name="hide", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the component has finished its construction
 *  and has all initialization properties set.
 *
 *  <p>After the initialization phase, properties are processed, the component
 *  is measured, laid out, and drawn, after which the
 *  <code>creationComplete</code> event is dispatched.</p>
 *
 *  @eventType mx.events.FlexEvent.INITIALIZE
 */
[Event(name="initialize", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the object has moved.
 *
 *  <p>You can move the component by setting the <code>x</code>
 *  or <code>y</code> properties, by calling the <code>move()</code>
 *  method, by setting one 
 *  of the following properties either on the component or on other
 *  components such that the LayoutManager needs to change the
 *  <code>x</code> or <code>y</code> properties of the component:</p>
 *
 *  <ul>
 *    <li><code>minWidth</code></li>
 *    <li><code>minHeight</code></li>
 *    <li><code>maxWidth</code></li>
 *    <li><code>maxHeight</code></li>
 *    <li><code>explicitWidth</code></li>
 *    <li><code>explicitHeight</code></li>
 *  </ul>
 *
 *  <p>When you call the <code>move()</code> method, the <code>move</code>
 *  event is dispatched before the method returns.
 *  In all other situations, the <code>move</code> event is not dispatched
 *  until after the property changes.</p>
 *
 *  @eventType mx.events.MoveEvent.MOVE
 */
[Event(name="move", type="mx.events.MoveEvent")]

/**
 *  Dispatched at the beginning of the component initialization sequence. 
 *  The component is in a very raw state when this event is dispatched. 
 *  Many components, such as the Button control, create internal child 
 *  components to implement functionality; for example, the Button control 
 *  creates an internal UITextField component to represent its label text. 
 *  When Flex dispatches the <code>preinitialize</code> event, 
 *  the children, including the internal children, of a component 
 *  have not yet been created.
 *
 *  @eventType mx.events.FlexEvent.PREINITIALIZE
 */
[Event(name="preinitialize", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the component is removed from a container as a content child
 *  by using the <code>removeChild()</code> or <code>removeChildAt()</code> method. 
 *  If the component is removed from the container as a noncontent child by 
 *  using the <code>rawChildren.removeChild()</code> or 
 *  <code>rawChildren.removeChildAt()</code> method, the event is not dispatched.
 *
 *  @eventType mx.events.FlexEvent.REMOVE
 */
[Event(name="remove", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the component is resized.
 *
 *  <p>You can resize the component by setting the <code>width</code> or
 *  <code>height</code> property, by calling the <code>setActualSize()</code>
 *  method, or by setting one of
 *  the following properties either on the component or on other components
 *  such that the LayoutManager needs to change the <code>width</code> or
 *  <code>height</code> properties of the component:</p>
 *
 *  <ul>
 *    <li><code>minWidth</code></li>
 *    <li><code>minHeight</code></li>
 *    <li><code>maxWidth</code></li>
 *    <li><code>maxHeight</code></li>
 *    <li><code>explicitWidth</code></li>
 *    <li><code>explicitHeight</code></li>
 *  </ul>
 *
 *  <p>The <code>resize</code> event is not 
 *  dispatched until after the property changes.</p>
 *
 *  @eventType mx.events.ResizeEvent.RESIZE
 */
[Event(name="resize", type="mx.events.ResizeEvent")]

/**
 *  Dispatched when an object's state changes from invisible to visible.
 *
 *  @eventType mx.events.FlexEvent.SHOW
 */
[Event(name="show", type="mx.events.FlexEvent")]

//--------------------------------------
//  Mouse events
//--------------------------------------

/**
 *  Dispatched from a component opened using the PopUpManager 
 *  when the user clicks outside it.
 *
 *  @eventType mx.events.FlexMouseEvent.MOUSE_DOWN_OUTSIDE
 */
[Event(name="mouseDownOutside", type="mx.events.FlexMouseEvent")]

/**
 *  Dispatched from a component opened using the PopUpManager 
 *  when the user scrolls the mouse wheel outside it.
 *
 *  @eventType mx.events.FlexMouseEvent.MOUSE_WHEEL_OUTSIDE
 */
[Event(name="mouseWheelOutside", type="mx.events.FlexMouseEvent")]

//--------------------------------------
//  Validation events
//--------------------------------------

/**
 *  Dispatched when values are changed programmatically
 *  or by user interaction.
 *
 *  <p>Because a programmatic change triggers this event, make sure
 *  that any <code>valueCommit</code> event handler does not change
 *  a value that causes another <code>valueCommit</code> event.
 *  For example, do not change a control's <code>dataProvider</code>
 *  property in a <code>valueCommit</code> event handler. </p>
 *
 *  @eventType mx.events.FlexEvent.VALUE_COMMIT
 */
[Event(name="valueCommit", type="mx.events.FlexEvent")]

/**
 *  Dispatched when a component is monitored by a Validator
 *  and the validation failed.
 *
 *  @eventType mx.events.FlexEvent.INVALID
 */
[Event(name="invalid", type="mx.events.FlexEvent")]

/**
 *  Dispatched when a component is monitored by a Validator
 *  and the validation succeeded.
 *
 *  @eventType mx.events.FlexEvent.VALID
 */
[Event(name="valid", type="mx.events.FlexEvent")]

//--------------------------------------
//  Drag-and-drop events
//--------------------------------------

/**
 *  Dispatched by a component when the user moves the mouse over the component
 *  during a drag operation. 
 *  In an application running in Flash Player, 
 *  the event is dispatched many times when you move the mouse over any component.
 *  In an application running in AIR, the event is dispatched only once.
 *
 *  <p>In order to be a valid drop target, you must define a handler
 *  for this event.
 *  In the handler, you can change the appearance of the drop target
 *  to provide visual feedback to the user that the component can accept
 *  the drag.
 *  For example, you could draw a border around the drop target,
 *  or give focus to the drop target.</p>
 *
 *  <p>If you want to accept the drag, you must call the 
 *  <code>DragManager.acceptDragDrop()</code> method. If you don't
 *  call <code>acceptDragDrop()</code>, you will not get any of the
 *  other drag events.</p>
 *
 *  <p>In Flash Player, the value of the <code>action</code> property is always
 *  <code>DragManager.MOVE</code>, even if you are doing a copy. 
 *  This is because the <code>dragEnter</code> event occurs before 
 *  the control recognizes that the Control key is pressed to signal a copy.
 *  The <code>action</code> property of the event object for the 
 *  <code>dragOver</code> event does contain a value that signifies the type of 
 *  drag operation. You may change the type of drag action by calling the
 *  <code>DragManager.showFeedback()</code> method.</p>
 *
 *  <p>In AIR, the default value of the <code>action</code> property is 
 *  <code>DragManager.COPY</code>.</p> 
 *
 *  <p>Because of the way data to a Tree control is structured, 
 *  the Tree control handles drag and drop differently from the other list-based controls. 
 *  For the Tree control, the event handler for the <code>dragDrop</code> event 
 *  only performs an action when you move or copy data in the same Tree control, 
 *  or copy data to another Tree control. 
 *  If you drag data from one Tree control and drop it onto another Tree control 
 *  to move the data, the event handler for the <code>dragComplete</code> event 
 *  actually performs the work to add the data to the destination Tree control, 
 *  rather than the event handler for the dragDrop event, 
 *  and also removes the data from the source Tree control. 
 *  This is necessary because to reparent the data being moved, 
 *  Flex must remove it first from the source Tree control.</p>
 *
 *  @see mx.managers.DragManager
 *
 *  @eventType mx.events.DragEvent.DRAG_ENTER
 */
[Event(name="dragEnter", type="mx.events.DragEvent")]

/**
 *  Dispatched by a component when the user moves the mouse while over the component
 *  during a drag operation. 
 *  In Flash Player, the event is dispatched 
 *  when you drag an item over a valid drop target.
 *  In AIR, the event is dispatched when you drag an item over 
 *  any component, even if the component is not a valid drop target.
 *
 *  <p>In the handler, you can change the appearance of the drop target
 *  to provide visual feedback to the user that the component can accept
 *  the drag.
 *  For example, you could draw a border around the drop target,
 *  or give focus to the drop target.</p>
 *
 *  <p>You should handle this event to perform additional logic
 *  before allowing the drop, such as dropping data to various locations
 *  in the drop target, reading keyboard input to determine if the
 *  drag-and-drop action is a move or copy of the drag data, or providing
 *  different types of visual feedback based on the type of drag-and-drop
 *  action.</p>
 *
 *  <p>You may also change the type of drag action by changing the
 *  <code>DragManager.showFeedback()</code> method.
 *  The default value of the <code>action</code> property is
 *  <code>DragManager.MOVE</code>.</p>
 *
 *  @see mx.managers.DragManager
 *
 *  @eventType mx.events.DragEvent.DRAG_OVER
 */
[Event(name="dragOver", type="mx.events.DragEvent")]

/**
 *  Dispatched by the component when the user drags outside the component,
 *  but does not drop the data onto the target.
 *
 *  <p>You use this event to restore the drop target to its normal appearance
 *  if you modified its appearance as part of handling the
 *  <code>dragEnter</code> or <code>dragOver</code> event.</p>
 *
 *  @eventType mx.events.DragEvent.DRAG_EXIT
 */
[Event(name="dragExit", type="mx.events.DragEvent")]

/**
 *  Dispatched by the drop target when the user releases the mouse over it.
 *
 *  <p>You use this event handler to add the drag data to the drop target.</p>
 *
 *  <p>If you call <code>Event.preventDefault()</code> in the event handler 
 *  for the <code>dragDrop</code> event for 
 *  a Tree control when dragging data from one Tree control to another, 
 *  it prevents the drop.</p>
 *
 *  @eventType mx.events.DragEvent.DRAG_DROP
 */
[Event(name="dragDrop", type="mx.events.DragEvent")]

/**
 *  Dispatched by the drag initiator (the component that is the source
 *  of the data being dragged) when the drag operation completes,
 *  either when you drop the dragged data onto a drop target or when you end
 *  the drag-and-drop operation without performing a drop.
 *
 *  <p>You can use this event to perform any final cleanup
 *  of the drag-and-drop operation.
 *  For example, if you drag a List control item from one list to another,
 *  you can delete the List control item from the source if you no longer
 *  need it.</p>
 *
 *  <p>If you call <code>Event.preventDefault()</code> in the event handler 
 *  for the <code>dragComplete</code> event for 
 *  a Tree control when dragging data from one Tree control to another, 
 *  it prevents the drop.</p>
 *
 *  @eventType mx.events.DragEvent.DRAG_COMPLETE
 */
[Event(name="dragComplete", type="mx.events.DragEvent")]

/**
 *  Dispatched by the drag initiator when starting a drag operation.
 *  This event is used internally by the list-based controls; 
 *  you do not handle it when implementing drag and drop. 
 *  If you want to control the start of a drag-and-drop operation, 
 *  use the <code>mouseDown</code> or <code>mouseMove</code> event.
 * 
 *  @eventType mx.events.DragEvent.DRAG_START
 */
[Event(name="dragStart", type="mx.events.DragEvent")]

//--------------------------------------
//  Effect events
//--------------------------------------

/**
 *  Dispatched just before an effect starts.
 *
 *  <p>The effect does not start changing any visuals
 *  until after this event is fired.</p>
 *
 *  @eventType mx.events.EffectEvent.EFFECT_START
 */
[Event(name="effectStart", type="mx.events.EffectEvent")]

/**
 *  Dispatched after an effect ends.
 *
 *  <p>The effect will have made the last set of visual changes
 *  before this event is fired, but those changes will not have
 *  been rendered on the screen.
 *  Thus, you might have to use the <code>callLater()</code> method
 *  to delay any other changes that you want to make until after the 
 *  changes have been rendered onscreen.</p>
 *
 *  @eventType mx.events.EffectEvent.EFFECT_END
 */
[Event(name="effectEnd", type="mx.events.EffectEvent")]


//--------------------------------------
//  State events
//--------------------------------------

/**
 *  Dispatched after the <code>currentState</code> property changes,
 *  but before the view state changes.
 *
 *  @eventType mx.events.StateChangeEvent.CURRENT_STATE_CHANGING
 */
[Event(name="currentStateChanging", type="mx.events.StateChangeEvent")]

/**
 *  Dispatched after the view state has changed.
 *
 *  @eventType mx.events.StateChangeEvent.CURRENT_STATE_CHANGE
 */
[Event(name="currentStateChange", type="mx.events.StateChangeEvent")]

/**
 *  Dispatched after the component has returned to the root view state.
 *
 *  @eventType mx.events.FlexEvent.ENTER_STATE
 */
[Event(name="enterState", type="mx.events.FlexEvent")]

/**
 *  Dispatched before the component exits from the root view state.
 *
 *  @eventType mx.events.FlexEvent.EXIT_STATE
 */
[Event(name="exitState", type="mx.events.FlexEvent")]

//--------------------------------------
//  Tooltip events
//--------------------------------------

/**
 *  Dispatched by the component when it is time to create a ToolTip.
 *
 *  <p>If you create your own IToolTip object and place a reference
 *  to it in the <code>toolTip</code> property of the event object
 *  that is passed to your <code>toolTipCreate</code> handler,
 *  the ToolTipManager displays your custom ToolTip.
 *  Otherwise, the ToolTipManager creates an instance of
 *  <code>ToolTipManager.toolTipClass</code> to display.</p>
 *
 *  <p>The sequence of ToolTip events is <code>toolTipStart</code>,
 *  <code>toolTipCreate</code>, <code>toolTipShow</code>,
 *  <code>toolTipShown</code>, <code>toolTipHide</code>,
 *  and <code>toolTipEnd</code>.</p>
 *
 *  @eventType mx.events.ToolTipEvent.TOOL_TIP_CREATE
 */
[Event(name="toolTipCreate", type="mx.events.ToolTipEvent")]

/**
 *  Dispatched by the component when its ToolTip has been hidden
 *  and will be discarded soon.
 *
 *  <p>If you specify an effect using the 
 *  <code>ToolTipManager.hideEffect</code> property, 
 *  this event is dispatched after the effect stops playing.</p>
 *
 *  <p>The sequence of ToolTip events is <code>toolTipStart</code>,
 *  <code>toolTipCreate</code>, <code>toolTipShow</code>,
 *  <code>toolTipShown</code>, <code>toolTipHide</code>,
 *  and <code>toolTipEnd</code>.</p>
 *
 *  @eventType mx.events.ToolTipEvent.TOOL_TIP_END
 */
[Event(name="toolTipEnd", type="mx.events.ToolTipEvent")]

/**
 *  Dispatched by the component when its ToolTip is about to be hidden.
 *
 *  <p>If you specify an effect using the 
 *  <code>ToolTipManager.hideEffect</code> property, 
 *  this event is dispatched before the effect starts playing.</p>
 *
 *  <p>The sequence of ToolTip events is <code>toolTipStart</code>,
 *  <code>toolTipCreate</code>, <code>toolTipShow</code>,
 *  <code>toolTipShown</code>, <code>toolTipHide</code>,
 *  and <code>toolTipEnd</code>.</p>
 *
 *  @eventType mx.events.ToolTipEvent.TOOL_TIP_HIDE
 */
[Event(name="toolTipHide", type="mx.events.ToolTipEvent")]

/**
 *  Dispatched by the component when its ToolTip is about to be shown.
 *
 *  <p>If you specify an effect using the 
 *  <code>ToolTipManager.showEffect</code> property, 
 *  this event is dispatched before the effect starts playing.
 *  You can use this event to modify the ToolTip before it appears.</p>
 *
 *  <p>The sequence of ToolTip events is <code>toolTipStart</code>,
 *  <code>toolTipCreate</code>, <code>toolTipShow</code>,
 *  <code>toolTipShown</code>, <code>toolTipHide</code>,
 *  and <code>toolTipEnd</code>.</p>
 *
 *  @eventType mx.events.ToolTipEvent.TOOL_TIP_SHOW
 */
[Event(name="toolTipShow", type="mx.events.ToolTipEvent")]

/**
 *  Dispatched by the component when its ToolTip has been shown.
 *
 *  <p>If you specify an effect using the 
 *  <code>ToolTipManager.showEffect</code> property, 
 *  this event is dispatched after the effect stops playing.</p>
 *
 *  <p>The sequence of ToolTip events is <code>toolTipStart</code>,
 *  <code>toolTipCreate</code>, <code>toolTipShow</code>,
 *  <code>toolTipShown</code>, <code>toolTipHide</code>,
 *  and <code>toolTipEnd</code>.</p>
 *
 *  @eventType mx.events.ToolTipEvent.TOOL_TIP_SHOWN
 */
[Event(name="toolTipShown", type="mx.events.ToolTipEvent")]

/**
 *  Dispatched by a component whose <code>toolTip</code> property is set,
 *  as soon as the user moves the mouse over it.
 *
 *  <p>The sequence of ToolTip events is <code>toolTipStart</code>,
 *  <code>toolTipCreate</code>, <code>toolTipShow</code>,
 *  <code>toolTipShown</code>, <code>toolTipHide</code>,
 *  and <code>toolTipEnd</code>.</p>
 *
 *  @eventType mx.events.ToolTipEvent.TOOL_TIP_START
 */
[Event(name="toolTipStart", type="mx.events.ToolTipEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/AnchorStyles.as";

/**
 *  Color of the component highlight when validation fails. 
 *  Flex also sets the <code>borderColor</code> style of the component to this 
 *  <code>errorColor</code> on a validation failure.
 *
 *  @default 0xFF0000
 */
[Style(name="errorColor", type="uint", format="Color", inherit="yes")]

/**
 *  Blend mode used by the focus rectangle.
 *  For more information, see the <code>blendMode</code> property 
 *  of the flash.display.DisplayObject class.
 *
 *  @default "normal"
 */
[Style(name="focusBlendMode", type="String", inherit="no")]

/**
 *  Skin used to draw the focus rectangle.
 *
 *  @default mx.skins.halo.HaloFocusRect
 */
[Style(name="focusSkin", type="Class", inherit="no")]

/**
 *  Thickness, in pixels, of the focus rectangle outline.
 *
 *  @default 2
 */
[Style(name="focusThickness", type="Number", format="Length", inherit="no")]

/**
 *  Theme color of a component. This property controls the appearance of highlights,
 *  appearance when a component is selected, and other similar visual cues, but it
 *  does not have any effect on the regular borders or background colors of the component.
 *  The preferred values are <code>haloGreen</code>, <code>haloBlue</code>,
 *  <code>haloOrange</code>, and <code>haloSilver</code>, although any valid color
 *  value can be used.
 *
 *  <p>The default values of the <code>rollOverColor</code> and
 *  <code>selectionColor</code> styles are based on the
 *  <code>themeColor</code> value.</p>
 *
 *  @default "haloBlue"
 */
[Style(name="themeColor", type="uint", format="Color", inherit="yes")]

//--------------------------------------
//  Effects
//--------------------------------------

/**
 *  Played when the component is created.
 */
[Effect(name="creationCompleteEffect", event="creationComplete")]

/**
 *  Played when the component is moved.
 */
[Effect(name="moveEffect", event="move")]

/**
 *  Played when the component is resized.
 */
[Effect(name="resizeEffect", event="resize")]

/**
 *  Played when the component becomes visible.
 */
[Effect(name="showEffect", event="show")]

/**
 *  Played when the component becomes invisible.
 */
[Effect(name="hideEffect", event="hide")]

/**
 *  Played when the user presses the mouse button while over the component.
 */
[Effect(name="mouseDownEffect", event="mouseDown")]

/**
 *  Played when the user releases the mouse button while over the component.
 */
[Effect(name="mouseUpEffect", event="mouseUp")]

/**
 *  Played when the user rolls the mouse over the component.
 */
[Effect(name="rollOverEffect", event="rollOver")]

/**
 *  Played when the user rolls the mouse so it is no longer over the component.
 */
[Effect(name="rollOutEffect", event="rollOut")]

/**
 *  Played when the component gains keyboard focus.
 */
[Effect(name="focusInEffect", event="focusIn")]

/**
 *  Played when the component loses keyboard focus.
 */
[Effect(name="focusOutEffect", event="focusOut")]

/**
 *  Played when the component is added as a child to a Container.
 */
[Effect(name="addedEffect", event="added")]

/**
 *  Played when the component is removed from a Container.
 */
[Effect(name="removedEffect", event="removed")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.UIComponentAccImpl")]

[ResourceBundle("core")]

// skins resources aren't found because CSS visited by the compiler
[ResourceBundle("skins")]

/**
 *  The UIComponent class is the base class for all visual components,
 *  both interactive and noninteractive.
 *
 *  <p>An interactive component can participate in tabbing and other kinds of
 *  keyboard focus manipulation, accept low-level events like keyboard and
 *  mouse input, and be disabled so that it does not receive keyboard and
 *  mouse input.
 *  This is in contrast to noninteractive components, like Label and
 *  ProgressBar, which simply display contents and are not manipulated by
 *  the user.</p>
 *  <p>The UIComponent class is not used as an MXML tag, but is used as a base
 *  class for other classes.</p>
 *
 *  @mxml
 *
 *  <p>All user interface components in Flex extend the UIComponent class.
 *  Flex components inherit the following properties from the UIComponent
 *  class:</p>
 *
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *   <b>Properties </b>
 *    automationName="null"
 *    cachePolicy="auto|on|off"
 *    currentState="null"
 *    doubleClickEnabled="false|true"
 *    enabled="true|false"
 *    explicitHeight="NaN"
 *    explicitMaxHeight="NaN"
 *    explicitMaxWidth="NaN"
 *    explicitMinHeight="NaN"
 *    explicitMinWidth="NaN"
 *    explicitWidth="NaN"
 *    focusEnabled="true|false"
 *    height="0"
 *    id=""
 *    includeInLayout="true|false"
 *    maxHeight="10000"
 *    maxWidth="10000"
 *    measuredHeight=
 *    measuredMinHeight=
 *    measuredMinWidth=
 *    measuredWidth=
 *    minHeight="0"
 *    minWidth="0"
 *    mouseFocusEnabled="true|false"
 *    percentHeight="NaN"
 *    percentWidth="NaN"
 *    scaleX="1.0"
 *    scaleY="1.0"
 *    states="null"
 *    styleName="undefined"
 *    toolTip="null"
 *    transitions=""
 *    validationSubField
 *    width="0"
 *    x="0"
 *    y="0"
 *  
 *  <b>Styles</b>
 *    bottom="undefined"
 *    errorColor="0xFF0000"
 *    focusBlendMode="normal"
 *    focusSkin="HaloFocusRect""
 *    focusThickness="2"
 *    horizontalCenter="undefined"
 *    left="undefined"
 *    right="undefined"
 *    themeColor="haloGreen"
 *    top="undefined"
 *    verticalCenter="undefined"
 *  
 *  <b>Effects</b>
 *    addedEffect="<i>No default</i>"
 *    creationCompleteEffect="<i>No default</i>"
  *   focusInEffect="<i>No default</i>"
 *    focusOutEffect="<i>No default</i>"
 *    hideEffect="<i>No default</i>"
 *    mouseDownEffect="<i>No default</i>"
 *    mouseUpEffect="<i>No default</i>"
 *    moveEffect="<i>No default</i>"
 *    removedEffect="<i>No default</i>"
 *    resizeEffect="<i>No default</i>"
 *    rollOutEffect="<i>No default</i>"
 *    rollOverEffect="<i>No default</i>"    
 *    showEffect="<i>No default</i>"
 *  
 *  <b>Events</b>
 *    add="<i>No default</i>"
 *    creationComplete="<i>No default</i>"
 *    currentStateChange="<i>No default</i>"
 *    currentStateChanging="<i>No default</i>"
 *    dragComplete="<i>No default</i>"
 *    dragDrop="<i>No default</i>"
 *    dragEnter="<i>No default</i>"
 *    dragExit="<i>No default</i>"
 *    dragOver="<i>No default</i>"
 *    effectEnd="<i>No default</i>"
 *    effectStart="<i>No default</i>"
 *    enterState="<i>No default</i>"
 *    exitState="<i>No default</i>"
 *    hide="<i>No default</i>"
 *    initialize="<i>No default</i>"
 *    invalid="<i>No default</i>"
 *    mouseDownOutside="<i>No default</i>"
 *    mouseWheelOutside="<i>No default</i>"
 *    move="<i>No default</i>"
 *    preinitialize="<i>No default</i>"
 *    record="<i>No default</i>"
 *    remove="<i>No default</i>"
 *    resize="<i>No default</i>"
 *    show="<i>No default</i>"
 *    toolTipCreate="<i>No default</i>"
 *    toolTipEnd="<i>No default</i>"
 *    toolTipHide="<i>No default</i>"
 *    toolTipShow="<i>No default</i>"
 *    toolTipShown="<i>No default</i>"
 *    toolTipStart="<i>No default</i>"
 *    updateComplete="<i>No default</i>"
 *    valid="<i>No default</i>"
 *    valueCommit="<i>No default</i>"
 *  &gt;
 *  </pre>
 *
 *  @see mx.core.UIComponent
 */
public class UIComponent extends FlexSprite
       implements IAutomationObject, IChildList,
                         IDeferredInstantiationUIComponent, IFlexDisplayObject,
                         IFlexModule,
                         IInvalidating, ILayoutManagerClient,
                         IPropertyChangeNotifier, IRepeaterClient,
                         ISimpleStyleClient, IStyleClient,
                         IToolTipManagerClient, IUIComponent,
                         IValidatorListener, IStateClient,
                         IConstraintClient
{
    include "../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The default value for the <code>measuredWidth</code> property.
     *  Most components calculate a measuredWidth but some are flow-based and
     *  have to pick a number that looks reasonable.
     *
     *  @default 160
     */
    public static const DEFAULT_MEASURED_WIDTH:Number = 160;

    /**
     *  The default value for the <code>measuredMinWidth</code> property.
     *  Most components calculate a measuredMinWidth but some are flow-based and
     *  have to pick a number that looks reasonable.
     *
     *  @default 40
     */
    public static const DEFAULT_MEASURED_MIN_WIDTH:Number = 40;

    /**
     *  The default value for the <code>measuredHeight</code> property.
     *  Most components calculate a measuredHeight but some are flow-based and
     *  have to pick a number that looks reasonable.
     *
     *  @default 22
     */
    public static const DEFAULT_MEASURED_HEIGHT:Number = 22;

    /**
     *  The default value for the <code>measuredMinHeight</code> property.
     *  Most components calculate a measuredMinHeight but some are flow-based and
     *  have to pick a number that looks reasonable.
     *
     *  @default 22
     */
    public static const DEFAULT_MEASURED_MIN_HEIGHT:Number = 22;

    /**
     *  The default value for the <code>maxWidth</code> property.
     *
     *  @default 10000
     */
    public static const DEFAULT_MAX_WIDTH:Number = 10000;

    /**
     *  The default value for the <code>maxHeight</code> property.
     *
     *  @default 10000
     */
    public static const DEFAULT_MAX_HEIGHT:Number = 10000;

    /**
     *  @private
     *  The inheritingStyles and nonInheritingStyles properties
     *  are initialized to this empty Object.
     *  This allows the getStyle() and getStyle()
     *  methods to simply access inheritingStyles[] and nonInheritingStyles[]
     *  without needing to first check whether those objects exist.
     *  If they were simply initialized to {}, we couldn't determine
     *  whether the style chain has already been built or not.
     */
    mx_internal static var STYLE_UNINITIALIZED:Object = {};

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by UIComponentAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  embeddedFontRegistry
    //----------------------------------

    /**
     *  @private
     *  Storage for the _embeddedFontRegistry property.
     *  Note: This gets initialized on first access,
     *  not when this class is initialized, in order to ensure
     *  that the Singleton registry has already been initialized.
     */
    private static var _embeddedFontRegistry:IEmbeddedFontRegistry;

    /**
     *  @private
     *  A reference to the embedded font registry.
     *  Single registry in the system.
     *  Used to look up the moduleFactory of a font.
     */
    private static function get embeddedFontRegistry():IEmbeddedFontRegistry
    {
        if (!_embeddedFontRegistry)
        {
            _embeddedFontRegistry = IEmbeddedFontRegistry(
                Singleton.getInstance("mx.core::IEmbeddedFontRegistry"));
        }

        return _embeddedFontRegistry;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Blocks the background processing of methods
     *  queued by <code>callLater()</code>,
     *  until <code>resumeBackgroundProcessing()</code> is called.
     *
     *  <p>These methods can be useful when you have time-critical code
     *  which needs to execute without interruption.
     *  For example, when you set the <code>suspendBackgroundProcessing</code>
     *  property of an Effect to <code>true</code>,
     *  <code>suspendBackgroundProcessing()</code> is automatically called
     *  when it starts playing, and <code>resumeBackgroundProcessing</code>
     *  is called when it stops, in order to ensure that the animation
     *  is smooth.</p>
     *
     *  <p>Since the LayoutManager uses <code>callLater()</code>,
     *  this means that <code>commitProperties()</code>,
     *  <code>measure()</code>, and <code>updateDisplayList()</code>
     *  will not get called in between calls to
     *  <code>suspendBackgroundProcessing()</code> and
     *  <code>resumeBackgroundProcessing()</code>.</p>
     *
     *  <p>It is safe for both an outer method and an inner method
     *  (i.e., one that the outer methods calls) to call
     *  <code>suspendBackgroundProcessing()</code>
     *  and <code>resumeBackgroundProcessing()</code>, because these
     *  methods actually increment and decrement a counter
     *  which determines whether background processing occurs.</p>
     */
    public static function suspendBackgroundProcessing():void
    {
        UIComponentGlobals.callLaterSuspendCount++;
    }

    /**
     *  Resumes the background processing of methods
     *  queued by <code>callLater()</code>, after a call to
     *  <code>suspendBackgroundProcessing()</code>.
     *
     *  <p>Refer to the description of
     *  <code>suspendBackgroundProcessing()</code> for more information.</p>
     */
    public static function resumeBackgroundProcessing():void
    {
        if (UIComponentGlobals.callLaterSuspendCount > 0)
        {
            UIComponentGlobals.callLaterSuspendCount--;

            // Once the suspend count gets back to 0, we need to
            // force a render event to happen
            if (UIComponentGlobals.callLaterSuspendCount == 0)
            {
                var sm:ISystemManager = SystemManagerGlobals.topLevelSystemManagers[0];
                if (sm && sm.stage)
                    sm.stage.invalidate();
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function UIComponent()
    {
        super();

        // Override  variables in superclasses.
        focusRect = false; // We do our own focus drawing.
        tabEnabled = (this is IFocusManagerComponent);
            // We are tab enabled by default if IFocusManagerComponent
        tabChildren = false;

        // Whether the component can accept user interaction.
        // The default is true. If you set enabled to false for a container,
        // Flex dims the color of the container and of all of its children,
        // and blocks user input  to the container and to all of its children.
        // We set enabled to true here because some components keep their
        // own _enabled flag and may not initialize it to true.
        enabled = true;

        // Make the component invisible until the initialization sequence
        // is complete.
        // It will be set visible when the 'initialized' flag is set.
        $visible = false;

        addEventListener(Event.ADDED, addedHandler);
        addEventListener(Event.REMOVED, removedHandler);

        // Register for focus and keyboard events.
        if (this is IFocusManagerComponent)
        {
            addEventListener(FocusEvent.FOCUS_IN, focusInHandler);
            addEventListener(FocusEvent.FOCUS_OUT, focusOutHandler);
            addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);
            addEventListener(KeyboardEvent.KEY_UP, keyUpHandler);
        }

        resourcesChanged();

        // Register as a weak listener for "change" events from ResourceManager.
        // If UIComponents registered as a strong listener,
        // they wouldn't get garbage collected.
        resourceManager.addEventListener(
            Event.CHANGE, resourceManager_changeHandler, false, 0, true);

        _width = super.width;
        _height = super.height;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  There is a bug (139381) where we occasionally get callLaterDispatcher()
     *  even though we didn't expect it.
     *  That causes us to do a removeEventListener() twice,
     *  which messes up some internal thing in the player so that
     *  the next addEventListener() doesn't actually get us the render event.
     */
    private var listeningForRender:Boolean = false;

    /**
     *  @private
     *  List of methods used by callLater().
     */
    private var methodQueue:Array /* of MethodQueueElement */ = [];

    /**
     *  @private
     *  Whether or not we "own" the focus graphic
     */
    private var hasFocusRect:Boolean = false;


    //--------------------------------------------------------------------------
    //
    //  Variables: Creation
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  initialized
    //----------------------------------

    /**
     *  @private
     *  Storage for the initialized property.
     */
    private var _initialized:Boolean = false;

    [Inspectable(environment="none")]

    /**
     *  A flag that determines if an object has been through all three phases
     *  of layout: commitment, measurement, and layout (provided that any were required).
     */
    public function get initialized():Boolean
    {
        return _initialized;
    }

    /**
     *  @private
     */
    public function set initialized(value:Boolean):void
    {
        _initialized = value;
        
        if (value)
        {
            setVisible(_visible, true);

            dispatchEvent(new FlexEvent(FlexEvent.CREATION_COMPLETE));
        }
    }

    //----------------------------------
    //  processedDescriptors
    //----------------------------------

    /**
     *  @private
     *  Storage for the processedDescriptors property.
     */
    private var _processedDescriptors:Boolean = false;

    [Inspectable(environment="none")]

    /**
     *  Set to <code>true</code> after immediate or deferred child creation, 
     *  depending on which one happens. For a Container object, it is set 
     *  to <code>true</code> at the end of 
     *  the <code>createComponentsFromDescriptors()</code> method, 
     *  meaning after the Container object creates its children from its child descriptors. 
     *  
     *  <p>For example, if an Accordion container uses deferred instantiation, 
     *  the <code>processedDescriptors</code> property for the second pane of 
     *  the Accordion container does not become <code>true</code> until after 
     *  the user navigates to that pane and the pane creates its children. 
     *  But, if the Accordion had set the <code>creationPolicy</code> property 
     *  to <code>"all"</code>, the <code>processedDescriptors</code> property 
     *  for its second pane is set to <code>true</code> during application startup.</p>
     *  
     *  <p>For classes that are not containers, which do not have descriptors, 
     *  it is set to <code>true</code> after the <code>createChildren()</code> 
     *  method creates any internal component children.</p>
     */
    public function get processedDescriptors():Boolean
    {
        return _processedDescriptors;
    }

    /**
     *  @private
     */
    public function set processedDescriptors(value:Boolean):void
    {
        _processedDescriptors = value;
        
        if (value)
            dispatchEvent(new FlexEvent(FlexEvent.INITIALIZE));
    }

    //----------------------------------
    //  updateCompletePendingFlag
    //----------------------------------

    /**
     *  @private
     *  Storage for the updateCompletePendingFlag property.
     */
    private var _updateCompletePendingFlag:Boolean = false;

    [Inspectable(environment="none")]

    /**
     *  A flag that determines if an object has been through all three phases
     *  of layout validation (provided that any were required).
     */
    public function get updateCompletePendingFlag():Boolean
    {
        return _updateCompletePendingFlag;
    }

    /**
     *  @private
     */
    public function set updateCompletePendingFlag(value:Boolean):void
    {
        _updateCompletePendingFlag = value;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables: Invalidation
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Whether this component needs to have its
     *  commitProperties() method called.
     */
    mx_internal var invalidatePropertiesFlag:Boolean = false;

    /**
     *  @private
     *  Whether this component needs to have its
     *  measure() method called.
     */
    mx_internal var invalidateSizeFlag:Boolean = false;

    /**
     *  @private
     *  Whether this component needs to be have its
     *  updateDisplayList() method called.
     */
    mx_internal var invalidateDisplayListFlag:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Variables: Measurement
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Holds the last recorded value of the x property.
     *  Used in dispatching a MoveEvent.
     */
    private var oldX:Number = 0;

    /**
     *  @private
     *  Holds the last recorded value of the y property.
     *  Used in dispatching a MoveEvent.
     */
    private var oldY:Number = 0;

    /**
     *  @private
     *  Holds the last recorded value of the width property.
     *  Used in dispatching a ResizeEvent.
     */
    private var oldWidth:Number = 0;

    /**
     *  @private
     *  Holds the last recorded value of the height property.
     *  Used in dispatching a ResizeEvent.
     */
    private var oldHeight:Number = 0;

    /**
     *  @private
     *  Holds the last recorded value of the minWidth property.
     */
    private var oldMinWidth:Number;

    /**
     *  @private
     *  Holds the last recorded value of the minHeight property.
     */
    private var oldMinHeight:Number;

    /**
     *  @private
     *  Holds the last recorded value of the explicitWidth property.
     */
    private var oldExplicitWidth:Number;

    /**
     *  @private
     *  Holds the last recorded value of the explicitHeight property.
     */
    private var oldExplicitHeight:Number;

    /**
     *  @private
     *  Holds the last recorded value of the scaleX property.
     */
    private var oldScaleX:Number = 1.0;

    /**
     *  @private
     *  Holds the last recorded value of the scaleY property.
     */
    private var oldScaleY:Number = 1.0;

    /**
     *  @private
     * True if createInFontContext has been called.
     */
    private var hasFontContextBeenSaved:Boolean = false;
     
    /**
     *  @private
     * Holds the last recorded value of the module factory used to create the font.
     */
    private var oldEmbeddedFontContext:IFlexModuleFactory = null;
     
    /**
     * @private
     * 
     * Cache last value of embedded font.
     */
    private var cachedEmbeddedFont:EmbeddedFont = null;
         
    //--------------------------------------------------------------------------
    //
    //  Variables: Styles
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var cachedTextFormat:UITextFormat;

    //--------------------------------------------------------------------------
    //
    //  Variables: Effects
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Sprite used to display an overlay.
     */
    mx_internal var overlay:UIComponent;

    /**
     *  @private
     *  Color used for overlay.
     */
    mx_internal var overlayColor:uint;

    /**
     *  @private
     *  Counter to keep track of the number of current users
     *  of the overlay.
     */
    mx_internal var overlayReferenceCount:int = 0;

    //--------------------------------------------------------------------------
    //
    //  Variables: Validation
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var saveBorderColor:Boolean = true;

    /**
     *  @private
     */
    mx_internal var origBorderColor:Number;

    //--------------------------------------------------------------------------
    //
    //  Variables: Other
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Storage for automatically-created RadioButtonGroups.
     *  If a RadioButton's groupName isn't the id of a RadioButtonGroup tag,
     *  we automatically create a RadioButtonGroup and store it here as
     *  document.automaticRadioButtonGroups[groupName] = theRadioButtonGroup;
     */
    mx_internal var automaticRadioButtonGroups:Object;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  owner
    //----------------------------------

    /**
     *  @private
     */
    private var _owner:DisplayObjectContainer;

    /**
     *  The owner of this UIComponent. By default, it is the parent of this UIComponent.
     *  However, if this UIComponent object is a child component that is 
     *  popped up by its parent, such as the dropdown list of a ComboBox control, 
     *  the owner is the component that popped up this UIComponent object. 
     *
     *  <p>This property is not managed by Flex, but by each component. 
     *  Therefore, if you use the <code>PopUpManger.createPopUp()</code> or 
     *  <code>PopUpManger.addPopUp()</code> method to pop up a child component, 
     *  you should set the <code>owner</code> property of the child component 
     *  to the component that popped it up.</p>
     * 
     *  <p>The default value is the value of the <code>parent</code> property.</p>
     */
    public function get owner():DisplayObjectContainer
    {
        return _owner ? _owner : parent;
    }

    public function set owner(value:DisplayObjectContainer):void
    {
        _owner = value;
    }

    //----------------------------------
    //  parent
    //----------------------------------

    /**
     *  @private
     *  Reference to this component's virtual parent container.
     *  "Virtual" means that this parent may not be the same
     *  as the one that the Player returns as the 'parent'
     *  property of a DisplayObject.
     *  For example, if a Container has created a contentPane
     *  to improve scrolling performance,
     *  then its "children" are really its grandchildren
     *  and their "parent" is actually their grandparent,
     *  because we don't want developers to be concerned with
     *  whether a contentPane exists or not.
     */
    mx_internal var _parent:DisplayObjectContainer;

    /**
     *  The parent container or component for this component.
     *  Only UIComponent objects should have a parent property.
     *  Non-UIComponent objects should use another property to reference
     *  the object to which they belong.
     *  By convention, non-UIComponent objects use an <code>owner</code>
     *  property to reference the object to which they belong.
     */
    override public function get parent():DisplayObjectContainer
    {
        // Flash PlaceObject tags can have super.parent set
        // before we get to setting the _parent property.
        return _parent ? _parent : super.parent;
    }

    //----------------------------------
    //  x
    //----------------------------------

    [Bindable("xChanged")]
    [Inspectable(category="General")]

    /**
     *  Number that specifies the component's horizontal position,
     *  in pixels, within its parent container.
     *
     *  <p>Setting this property directly or calling <code>move()</code>
     *  will have no effect -- or only a temporary effect -- if the
     *  component is parented by a layout container such as HBox, Grid,
     *  or Form, because the layout calculations of those containers
     *  set the <code>x</code> position to the results of the calculation.
     *  However, the <code>x</code> property must almost always be set
     *  when the parent is a Canvas or other absolute-positioning
     *  container because the default value is 0.</p>
     *
     *  @default 0
     */
    override public function get x():Number
    {
        return super.x;
    }

    /**
     *  @private
     */
    override public function set x(value:Number):void
    {
        if (super.x == value)
            return;

        super.x = value;

        invalidateProperties();

        dispatchEvent(new Event("xChanged"));
    }

    //----------------------------------
    //  y
    //----------------------------------

    [Bindable("yChanged")]
    [Inspectable(category="General")]

    /**
     *  Number that specifies the component's vertical position,
     *  in pixels, within its parent container.
     *
     *  <p>Setting this property directly or calling <code>move()</code>
     *  will have no effect -- or only a temporary effect -- if the
     *  component is parented by a layout container such as HBox, Grid,
     *  or Form, because the layout calculations of those containers
     *  set the <code>x</code> position to the results of the calculation.
     *  However, the <code>x</code> property must almost always be set
     *  when the parent is a Canvas or other absolute-positioning
     *  container because the default value is 0.</p>
     *
     *  @default 0
     */
    override public function get y():Number
    {
        return super.y;
    }

    /**
     *  @private
     */
    override public function set y(value:Number):void
    {
        if (super.y == value)
            return;

        super.y = value;

        invalidateProperties();

        dispatchEvent(new Event("yChanged"));
    }

    //----------------------------------
    //  width
    //----------------------------------

    /**
     *  @private
     *  Storage for the width property.
     */
    mx_internal var _width:Number;

    [Bindable("widthChanged")]
    [Inspectable(category="General")]
    [PercentProxy("percentWidth")]

    /**
     *  Number that specifies the width of the component, in pixels,
     *  in the parent's coordinates.
     *  The default value is 0, but this property will contain the actual component 
     *  width after Flex completes sizing the components in your application.
     *
     *  <p>Note: You can specify a percentage value in the MXML
     *  <code>width</code> attribute, such as <code>width="100%"</code>,
     *  but you cannot use a percentage value in the <code>width</code>
     *  property in ActionScript.
     *  Use the <code>percentWidth</code> property instead.</p>
     *
     *  <p>Setting this property causes a <code>resize</code> event to
     *  be dispatched.
     *  See the <code>resize</code> event for details on when
     *  this event is dispatched.
     *  If the component's <code>scaleX</code> property is not 1.0,
     *  the width of the component from its internal coordinates
     *  will not match.
     *  Thus a 100 pixel wide component with a <code>scaleX</code>
     *  of 2 will take 100 pixels in the parent, but will
     *  internally think it is 50 pixels wide.</p>
     */
    override public function get width():Number
    {
        return _width;
    }

    /**
     *  @private
     */
    override public function set width(value:Number):void
    {
        if (explicitWidth != value)
        {
            explicitWidth = value;

            // We invalidate size because locking in width
            // may change the measured height in flow-based components.
            invalidateSize();
        }

        if (_width != value)
        {
            invalidateProperties();
            invalidateDisplayList();

            var p:IInvalidating = parent as IInvalidating;
            if (p && includeInLayout)
            {
                p.invalidateSize();
                p.invalidateDisplayList();
            }

            _width = value;

            dispatchEvent(new Event("widthChanged"));
        }
    }

    //----------------------------------
    //  height
    //----------------------------------

    /**
     *  @private
     *  Storage for the height property.
     */
    mx_internal var _height:Number;

    [Bindable("heightChanged")]
    [Inspectable(category="General")]
    [PercentProxy("percentHeight")]

    /**
     *  Number that specifies the height of the component, in pixels,
     *  in the parent's coordinates.
     *  The default value is 0, but this property will contain the actual component 
     *  height after Flex completes sizing the components in your application.
     *
     *  <p>Note: You can specify a percentage value in the MXML
     *  <code>height</code> attribute, such as <code>height="100%"</code>,
     *  but you cannot use a percentage value for the <code>height</code>
     *  property in ActionScript;
     *  use the <code>percentHeight</code> property instead.</p>
     *
     *  <p>Setting this property causes a <code>resize</code> event to be dispatched.
     *  See the <code>resize</code> event for details on when
     *  this event is dispatched.
     *  If the component's <code>scaleY</code> property is not 100,
     *  the height of the component from its internal coordinates
     *  will not match.
     *  Thus a 100 pixel high component with a <code>scaleY</code>
     *  of 200 will take 100 pixels in the parent, but will
     *  internally think it is 50 pixels high.</p>
     */
    override public function get height():Number
    {
        return _height;
    }

    /**
     *  @private
     */
    override public function set height(value:Number):void
    {
        if (explicitHeight != value)
        {
            explicitHeight = value;

            // We invalidate size because locking in width
            // may change the measured height in flow-based components.
            invalidateSize();
        }

        if (_height != value)
        {
            invalidateProperties();
            invalidateDisplayList();

            var p:IInvalidating = parent as IInvalidating;
            if (p && includeInLayout)
            {
                p.invalidateSize();
                p.invalidateDisplayList();
            }

            _height = value;

            dispatchEvent(new Event("heightChanged"));
        }
    }

    //----------------------------------
    //  scaleX
    //----------------------------------

    /**
     *  @private
     *  Storage for the scaleX property.
     */
    private var _scaleX:Number = 1.0;

    [Bindable("scaleXChanged")]
    [Inspectable(category="Size", defaultValue="1.0")]

    /**
     *  Number that specifies the horizontal scaling factor.
     *
     *  <p>The default value is 1.0, which means that the object
     *  is not scaled.
     *  A <code>scaleX</code> of 2.0 means the object has been
     *  magnified by a factor of 2, and a <code>scaleX</code> of 0.5
     *  means the object has been reduced by a factor of 2.</p>
     *
     *  <p>A value of 0.0 is an invalid value. 
     *  Rather than setting it to 0.0, set it to a small value, or set 
     *  the <code>visible</code> property to <code>false</code> to hide the component.</p>
     *
     *  @default 1.0
     */
    override public function get scaleX():Number
    {
        return _scaleX;
    }

    /**
     *  @private
     */
    override public function set scaleX(value:Number):void
    {
        if (_scaleX == value)
            return;

        _scaleX = value;

        invalidateProperties();
        invalidateSize();

        dispatchEvent(new Event("scaleXChanged"));
    }

    //----------------------------------
    //  scaleY
    //----------------------------------

    /**
     *  @private
     *  Storage for the scaleY property.
     */
    private var _scaleY:Number = 1.0;

    [Bindable("scaleYChanged")]
    [Inspectable(category="Size", defaultValue="1.0")]

    /**
     *  Number that specifies the vertical scaling factor.
     *
     *  <p>The default value is 1.0, which means that the object
     *  is not scaled.
     *  A <code>scaleY</code> of 2.0 means the object has been
     *  magnified by a factor of 2, and a <code>scaleY</code> of 0.5
     *  means the object has been reduced by a factor of 2.</p>
     *
     *  <p>A value of 0.0 is an invalid value. 
     *  Rather than setting it to 0.0, set it to a small value, or set 
     *  the <code>visible</code> property to <code>false</code> to hide the component.</p>
     *
     *  @default 1.0
     */
    override public function get scaleY():Number
    {
        return _scaleY;
    }

    /**
     *  @private
     */
    override public function set scaleY(value:Number):void
    {
        if (_scaleY == value)
            return;

        _scaleY = value;

        invalidateProperties();
        invalidateSize();

        dispatchEvent(new Event("scaleYChanged"));
    }

    //----------------------------------
    //  visible
    //----------------------------------

    /**
     *  @private
     *  Storage for the visible property.
     */
    private var _visible:Boolean = true;

    [Bindable("hide")]
    [Bindable("show")]
    [Inspectable(category="General", defaultValue="true")]

    /**
     *  Controls the visibility of this UIComponent. If <code>true</code>, 
     *  the object is visible.
     *
     *  <p>When setting to <code>true</code>, the object will dispatch
     *  a <code>show</code> event.
     *  When setting to <code>false</code>, the object will dispatch
     *  a <code>hide</code> event.
     *  In either case the children of the object will not emit a
     *  <code>show</code> or <code>hide</code> event unless the object
     *  has specifically written an implementation to do so.</p>
     *
     *  @default true
     */
    override public function get visible():Boolean
    {
        return _visible;
    }

    /**
     *  @private
     */
    override public function set visible(value:Boolean):void
    {
        setVisible(value);
    }

    /**
     *  Called when the <code>visible</code> property changes.
     *  You should set the <code>visible</code> property to show or hide
     *  a component instead of calling this method directly.
     *
     *  @param value The new value of the <code>visible</code> property. 
     *  Specify <code>true</code> to show the component, and <code>false</code> to hide it. 
     *
     *  @param noEvent If <code>true</code>, do not dispatch an event. 
     *  If <code>false</code>, dispatch a <code>show</code> event when 
     *  the component becomes visible, and a <code>hide</code> event when 
     *  the component becomes invisible.
     */
    public function setVisible(value:Boolean,
                               noEvent:Boolean = false):void 
    {
        _visible = value;

        if (!initialized)
            return;

        if ($visible == value)
            return;

        $visible = value;

        if (!noEvent)
        {
            dispatchEvent(new FlexEvent(value ?
                                        FlexEvent.SHOW :
                                        FlexEvent.HIDE));
        }
    }

    //----------------------------------
    //  alpha
    //----------------------------------

    [Bindable("alphaChanged")]
    [Inspectable(defaultValue="1.0", category="General", verbose="1")]

    /**
     *  @private
     */
    override public function set alpha(value:Number):void
    {
        super.alpha = value;

        dispatchEvent(new Event("alphaChanged"));
    }

    //----------------------------------
    //  doubleClickEnabled
    //----------------------------------

    [Inspectable(enumeration="true,false", defaultValue="true")]

    /**
     *  Specifies whether the UIComponent object receives <code>doubleClick</code> events. 
     *  The default value is <code>false</code>, which means that the UIComponent object 
     *  does not receive <code>doubleClick</code> events. 
     *
     *  <p>The <code>mouseEnabled</code> property must also be set to <code>true</code>, 
     *  its default value, for the object to receive <code>doubleClick</code> events.</p>
     *
     *  @default false
     */
    override public function get doubleClickEnabled():Boolean
    {
        return super.doubleClickEnabled;
    }

    /**
     *  @private
     *  Propagate to children.
     */
    override public function set doubleClickEnabled(value:Boolean):void
    {
        super.doubleClickEnabled = value;

        var childList:IChildList;

        if (this is IRawChildrenContainer)
            childList = IRawChildrenContainer(this).rawChildren;
        else
            childList = IChildList(this);

        for (var i:int = 0; i < childList.numChildren; i++)
        {
            var child:InteractiveObject = childList.getChildAt(i) as InteractiveObject;
            if (child)
                child.doubleClickEnabled = value;
        }
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     */
    private var _enabled:Boolean = false;

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]
    [Bindable("enabledChanged")] 

    /**
     *  @copy mx.core.IUIComponent#enabled
     */
    public function get enabled():Boolean
    {
        return _enabled;
    }

    /**
     *  @private
     */
    public function set enabled(value:Boolean):void
    {
        _enabled = value;

        // Need to flush the cached TextFormat
        // so it recalcs with the disabled color,
        cachedTextFormat = null;

        invalidateDisplayList();
        
        dispatchEvent(new Event("enabledChanged"));
    }

    //----------------------------------
    //  cacheAsBitmap
    //----------------------------------

    /**
     *  @private
     */
    override public function set cacheAsBitmap(value:Boolean):void
    {
        super.cacheAsBitmap = value;

        // If cacheAsBitmap is set directly,
        // reset the value of cacheAsBitmapCount.
        cacheAsBitmapCount = value ? 1 : 0;
    }

    //----------------------------------
    //  filters
    //----------------------------------

    /**
     *  @private
     *  Storage for the filters property.
     */
    private var _filters:Array;
    
    /**
     *  @private
     */
    override public function get filters():Array
    {
        return _filters ? _filters : super.filters;
    }
    
    /**
     *  @private
     */
    override public function set filters(value:Array):void
    {
        var n:int;
        var i:int;
        var e:IEventDispatcher;

        if (_filters)
        {
            n = _filters.length;
            for (i = 0; i < n; i++)
            {
                e = _filters[i] as IEventDispatcher;
                if (e)
                    e.removeEventListener("change", filterChangeHandler);
            }
        }
        
        _filters = value;
        
        if (_filters)
        {
            n = _filters.length;
            for (i = 0; i < n; i++)
            {
                e = _filters[i] as IEventDispatcher;
                if (e)
                    e.addEventListener("change", filterChangeHandler);
            }
        }

        super.filters = _filters;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Display
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  $parent
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the 'parent' property, which can be useful since components
     *  can override 'parent' and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $parent():DisplayObjectContainer
    {
        return super.parent;
    }

    //----------------------------------
    //  $x
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the 'x' property, which can be useful since components
     *  can override 'x' and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $x():Number
    {
        return super.x;
    }

    /**
     *  @private
     */
    mx_internal final function set $x(value:Number):void
    {
        super.x = value;
    }

    //----------------------------------
    //  $y
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the 'y' property, which can be useful since components
     *  can override 'y' and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $y():Number
    {
        return super.y;
    }

    /**
     *  @private
     */
    mx_internal final function set $y(value:Number):void
    {
        super.y = value;
    }

    //----------------------------------
    //  $width
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the 'width' property, which can be useful since components
     *  can override 'width' and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $width():Number
    {
        return super.width;
    }

    /**
     *  @private
     */
    mx_internal final function set $width(value:Number):void
    {
        super.width = value;
    }

    //----------------------------------
    //  $height
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the 'height' property, which can be useful since components
     *  can override 'height' and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $height():Number
    {
        return super.height;
    }

    /**
     *  @private
     */
    mx_internal final function set $height(value:Number):void
    {
        super.height = value;
    }

    //----------------------------------
    //  $visible
    //----------------------------------

    /**
     *  @private
     *  This property allows access to the Player's native implementation
     *  of the 'visible' property, which can be useful since components
     *  can override 'visible' and thereby hide the native implementation.
     *  Note that this "base property" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function get $visible():Boolean
    {
        return super.visible;
    }

    /**
     *  @private
     */
    mx_internal final function set $visible(value:Boolean):void
    {
        super.visible = value;
    }

    //----------------------------------
    //  contentMouseX
    //----------------------------------

    /**
     *  Returns the <i>x</i> position of the mouse, in the content coordinate system.
     *  Content coordinates specify a pixel position relative to the upper left 
     *  corner of the component's content, and include all of the component's 
     *  content area, including any regions that are currently clipped and must 
     *  be accessed by scrolling the component.
     */
    public function get contentMouseX():Number
    {
        return mouseX;
    }
    
    //----------------------------------
    //  contentMouseY
    //----------------------------------

    /**
     *  Returns the <i>y</i> position of the mouse, in the content coordinate system.
     *  Content coordinates specify a pixel position relative to the upper left 
     *  corner of the component's content, and include all of the component's 
     *  content area, including any regions that are currently clipped and must 
     *  be accessed by scrolling the component.
     */
    public function get contentMouseY():Number
    {
        return mouseY;
    }

    //----------------------------------
    //  tweeningProperties
    //----------------------------------

    /**
     *  @private
     */
    private var _tweeningProperties:Array;

    [Inspectable(environment="none")]

    /**
     *  Array of properties that are currently being tweened on this object.
     *
     *  <p>Used to alert the EffectManager that certain properties of this object
     *  are being tweened, so that the EffectManger doesn't attempt to animate
     *  the same properties.</p>
     */
    public function get tweeningProperties():Array
    {
        return _tweeningProperties;
    }

    /**
     *  @private
     */
    public function set tweeningProperties(value:Array):void
    {
        _tweeningProperties = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Manager access
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  cursorManager
    //----------------------------------

    /**
     *  Gets the CursorManager that controls the cursor for this component
     *  and its peers.
     *  Each top-level window has its own instance of a CursorManager;
     *  To make sure you're talking to the right one, use this method.
     */
    public function get cursorManager():ICursorManager
    {
        var o:DisplayObject = parent;

        while (o)
        {
            if (o is IUIComponent && "cursorManager" in o)
            {
                var cm:ICursorManager = o["cursorManager"];
                return cm;
            }

            o = o.parent;
        }

        return CursorManager.getInstance();
    }

    //----------------------------------
    //  focusManager
    //----------------------------------

    /**
     *  @private
     *  Storage for the focusManager property.
     */
    private var _focusManager:IFocusManager;

    [Inspectable(environment="none")]

    /**
     *  Gets the FocusManager that controls focus for this component
     *  and its peers.
     *  Each popup has its own focus loop and therefore its own instance
     *  of a FocusManager.
     *  To make sure you're talking to the right one, use this method.
     */
    public function get focusManager():IFocusManager
    {
        if (_focusManager)
            return _focusManager;

        var o:DisplayObject = parent;

        while (o)
        {
            if (o is IFocusManagerContainer)
                return IFocusManagerContainer(o).focusManager;

            o = o.parent;
        }

        return null;
    }

    /**
     *  @private
     *  IFocusManagerContainers have this property assigned by the framework
     */
    public function set focusManager(value:IFocusManager):void
    {
        _focusManager = value;
    }

    //----------------------------------
    //  resourceManager
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the resourceManager property.
     */
    private var _resourceManager:IResourceManager = ResourceManager.getInstance();
    
    /**
     *  @private
     *  This metadata suppresses a trace() in PropertyWatcher:
     *  "warning: unable to bind to property 'resourceManager' ..."
     */
    [Bindable("unused")]
    
    /**
     *  A reference to the object which manages
     *  all of the application's localized resources.
     *  This is a singleton instance which implements
     *  the IResourceManager interface.
     */
    protected function get resourceManager():IResourceManager
    {
        return _resourceManager;
    }

    //----------------------------------
    //  systemManager
    //----------------------------------

    /**
     *  @private
     *  Storage for the systemManager property.
     *  Set by the SystemManager so that each UIComponent
     *  has a references to its SystemManager
     */
    private var _systemManager:ISystemManager;
    
    /**
     *  @private
     *  if component has been reparented, we need to potentially 
     *  reassign systemManager, cause we could be in a new Window.
     */
    private var _systemManagerDirty:Boolean = false;

    [Inspectable(environment="none")]

    /**
     *  Returns the SystemManager object used by this component.
     */
    public function get systemManager():ISystemManager
    {
        if (!_systemManager || _systemManagerDirty)
        {
            var r:DisplayObject = root;
            if (r && !(r is Stage))
            {
                // If this object is attached to the display list, then
                // the root property holds its SystemManager.
                _systemManager = (r as ISystemManager);
            }
          else if (r)
            {
                // if the root is the Stage, then we are in a second AIR window
                _systemManager = Stage(r).getChildAt(0) as ISystemManager;
            }
            else
            {
                // If this object isn't attached to the display list, then
                // we need to walk up the parent chain ourselves.
                var o:DisplayObjectContainer = parent;
                while (o)
                {
                    var ui:IUIComponent = o as IUIComponent;
                    if (ui)
                    {
                        _systemManager = ui.systemManager;
                        break;
                    }
                    o = o.parent;
                }
            }
            _systemManagerDirty = false;
        }

        return _systemManager;
    }

    /**
     *  @private
     */
    public function set systemManager(value:ISystemManager):void
    {
        _systemManager = value;
        _systemManagerDirty = false;
    }

    /**
     *  @private
     *  Returns the current system manager, <code>systemManager</code>,
     *  unless it is null. 
     *  If the current system manager is null,
     *  then search to find the correct system manager.
     * 
     *  @return A system manager, will not be null.
     */
    mx_internal function getNonNullSystemManager():ISystemManager
    {
        var sm:ISystemManager = systemManager;
        
        if (!sm) 
            sm = ISystemManager(SystemManager.getSWFRoot(this));
        
        if (!sm) 
            return SystemManagerGlobals.topLevelSystemManagers[0];

        return sm;      
    }

    //----------------------------------
    //  nestLevel
    //----------------------------------

    /**
     *  @private
     *  Storage for the nestLevel property.
     */
    private var _nestLevel:int = 0;

    [Inspectable(environment="none")]

    /**
     *  Depth of this object in the containment hierarchy.
     *  This number is used by the measurement and layout code.
     *  The value is 0 if this component is not on the DisplayList.
     */
    public function get nestLevel():int
    {
        return _nestLevel;
    }

    /**
     *  @private
     */
    public function set nestLevel(value:int):void
    {
        // If my parent hasn't been attached to the display list, then its nestLevel
        // will be zero.  If it tries to set my nestLevel to 1, ignore it.  We'll
        // update nest levels again after the parent is added to the display list.
        //
        // Also punt if the new value for nestLevel is the same as my current value.
        if (value > 1 && _nestLevel != value)
        {
            _nestLevel = value;

            updateCallbacks();

            var childList:IChildList = (this is IRawChildrenContainer) ?
                IRawChildrenContainer(this).rawChildren : IChildList(this);
            var n:int = childList.numChildren;
            for (var i:int = 0; i < n; i++)
            {
                var ui:ILayoutManagerClient  = childList.getChildAt(i) as ILayoutManagerClient;
                if (ui)
                {
                    ui.nestLevel = value + 1;
                }
                else
                {
                    var textField:IUITextField = childList.getChildAt(i) as IUITextField;
                    
                    if (textField)
                        textField.nestLevel = value + 1;
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: MXML
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  descriptor
    //----------------------------------

    /**
     *  @private
     *  Storage for the descriptor property.
     *  This variable is initialized in the construct() method
     *  using the _descriptor in the initObj, which is set in
     *  createComponentFromDescriptor().
     *  If this UIComponent was not created by createComponentFromDescriptor(),
     *  its 'descriptor' property is null.
     */
    mx_internal var _descriptor:UIComponentDescriptor;

    [Inspectable(environment="none")]

    /**
     *  Reference to the UIComponentDescriptor, if any, that was used
     *  by the <code>createComponentFromDescriptor()</code> method to create this
     *  UIComponent instance. If this UIComponent instance 
     *  was not created from a descriptor, this property is null.
     *
     *  @see mx.core.UIComponentDescriptor
     */
    public function get descriptor():UIComponentDescriptor
    {
        return _descriptor;
    }

    /**
     *  @private
     */
    public function set descriptor(value:UIComponentDescriptor):void
    {
        _descriptor = value;
    }

    //----------------------------------
    //  document
    //----------------------------------

    /**
     *  @private
     *  Storage for the document property.
     *  This variable is initialized in the init() method.
     *  A document object (i.e., an Object at the top of the hierarchy
     *  of a Flex application, MXML component, or AS component) has an
     *  autogenerated override of initalize() which sets its _document to
     *  'this', so that its 'document' property is a reference to itself.
     *  Other UIComponents set their _document to their parent's _document,
     *  so that their 'document' property refers to the document object
     *  that they are inside.
     */
    mx_internal var _document:Object;

    [Inspectable(environment="none")]

    /**
     *  A reference to the document object associated with this UIComponent.
     *  A document object is an Object at the top of the hierarchy of a
     *  Flex application, MXML component, or AS component.
     */
    public function get document():Object
    {
        return _document;
    }

    /**
     *  A reference to the document object associated with this UIComponent.
     *  A document object is an Object at the top of the hierarchy of a
     *  Flex application, MXML component, or AS component.
     */
    public function set document(value:Object):void
    {
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IUIComponent = getChildAt(i) as IUIComponent;
            if (!child)
                continue;

            if (child.document == _document ||
                child.document == ApplicationGlobals.application)
            {
                child.document = value;
            }
        }
        
        _document = value;
    }

    //----------------------------------
    //  documentDescriptor
    //----------------------------------

    /**
     *  @private
     *  Storage for the documentDescriptor property.
     *  A document object (i.e., a UIComponent at the top of the
     *  hierarchy of a Flex application, MXML component,
     *  or AS component) has an autogenerated override of init()
     *  which sets its _documentDescriptor to the descriptor
     *  at the top of the autogenerated descriptor tree for that
     *  document. For other UIComponents, _documentDescriptor is
     *  never defined.
     */
    mx_internal var _documentDescriptor:UIComponentDescriptor;

    /**
     *  @private
     *  For a document object, which is an instance of a UIComponent
     *  at the top of the hierarchy of a Flex application, MXML
     *  component, or ActionScript component, the
     *  <code>documentDescriptor</code> property is a reference
     *  to the UIComponentDescriptor at the top of the autogenerated
     *  descriptor tree for that document, which describes the
     *  set of children and their attributes for that document.
     *  For other UIComponents, it is <code>null</code>.
     */
    mx_internal function get documentDescriptor():UIComponentDescriptor
    {
        return _documentDescriptor;
    }

    //----------------------------------
    //  id
    //----------------------------------

    /**
     *  @private
     */
    private var _id:String;

    /**
     *  ID of the component. This value becomes the instance name of the object
     *  and should not contain any white space or special characters. Each component
     *  throughout an application should have a unique id.
     *
     *  <p>If your application is going to be tested by third party tools, give each component
     *  a meaningful id. Testing tools use ids to represent the control in their scripts and
     *  having a meaningful name can make scripts more readable. For example, set the
     *  value of a button to submit_button rather than b1 or button1.</p>
     */
    public function get id():String
    {
        return _id;
    }

    /**
     *  @private
     */
    public function set id(value:String):void
    {
        _id = value;
    }

    //----------------------------------
    //  isDocument
    //----------------------------------

    /**
     *  Determines whether this UIComponent instance is a document object,
     *  that is, whether it is at the top of the hierarchy of a Flex
     *  application, MXML component, or ActionScript component.
     */
    public function get isDocument():Boolean
    {
        return document == this;
    }

    //----------------------------------
    //  parentApplication
    //----------------------------------

    [Bindable("initialize")]

    /**
     *  A reference to the Application object that contains this UIComponent
     *  instance.
     *  This Application object might exist in a SWFLoader control in another
     *  Application, and so on, creating a chain of Application objects that
     *  can be walked using parentApplication.
     *  The <code>parentApplication</code> property of an Application is never itself;
     *  it is either the Application into which it was loaded or null
     *  (for the top-level Application).
     *  Walking the application chain using the <code>parentApplication</code>
     *  property is similar to walking the document chain using the
     *  <code>parentDocument</code> property.
     *  You can access the top-level application using the
     *  <code>application</code> property of the Application class.
     */
    /*
     *  Note:
     *  There are two reasons that 'parentApplication' is typed as Object
     *  rather than as Application. The first is that typing it as Application
     *  would make UIComponent dependent on Application, slowing down compile
     *  times not only for SWCs for also for MXML and AS components. The
     *  second is that authors would not be able to access properties and
     *  methods in the <Script> of their <Application> without casting it
     *  to their application's subclass, as in
     *     MyApplication(paentApplication).myAppMethod().
     *  Therefore we decided to dispense with strict typing for
     *  'parentApplication'.
     */
    public function get parentApplication():Object
    {
        // Look for the SystemManager's document,
        // which should be the Application.
        var o:Object = systemManager.document;
        
        // If this UIComponent is its own root, then it is an Application.
        // We want to return its parent Application, or null
        // (if it has no parent because it is the top-level Application).
        // The hierarchy in this situation looks something like this:
        //
        //  SystemManager
        //      Application
        //          SomeContainer
        //              Loader
        //                  Loaded App's SystemManager
        //                      Application
        //                          ThisComponent
        if (o == this)
        {
            var p:UIComponent = o.systemManager.parent as UIComponent;
            o = p ? p.systemManager.document : null;
        }

        return o;
    }

    //----------------------------------
    //  parentDocument
    //----------------------------------

    [Bindable("initialize")]

    /**
     *  A reference to the parent document object for this UIComponent.
     *  A document object is a UIComponent at the top of the hierarchy
     *  of a Flex application, MXML component, or AS component.
     *  For the Application object, the <code>parentDocument</code> 
     *  property is null.
     *  This property  is useful in MXML scripts to go up a level
     *  in the chain of document objects.
     *  It can be used to walk this chain using
     *  <code>parentDocument.parentDocument</code>, and so on.
     *  It is typed as Object so that authors can access properties
     *  and methods on ancestor document objects without casting.
     */
    public function get parentDocument():Object
    {
        if (document == this)
        {
            var p:IUIComponent = parent as IUIComponent;
            if (p)
                return p.document;

            var sm:ISystemManager = parent as ISystemManager;
            if (sm)
                return sm.document;

            return null;            
        }
        else
        {
            return document;
        }
    }

    //----------------------------------
    //  screen
    //----------------------------------

    /**
     *  Returns an object that contains the size and position of the base
     *  drawing surface for this object.
     */
    public function get screen():Rectangle
    {
        var sm:ISystemManager = systemManager;
        return sm ? sm.screen : null;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Modules
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  moduleFactory
    //----------------------------------

    /**
     *  @private
     *  Storage for the moduleFactory property.
     */
    private var _moduleFactory:IFlexModuleFactory;
    
    [Inspectable(environment="none")]
    
    /**
     *  The moduleFactory that is used to create TextFields in the correct SWF context. This is necessary so that
     *  embedded fonts will work.
     */
    public function get moduleFactory():IFlexModuleFactory
    {
        return _moduleFactory;
    }

    /**
     *  @private
     */
    public function set moduleFactory(factory:IFlexModuleFactory):void
    {
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:UIComponent = getChildAt(i) as UIComponent;
            if (!child)
                continue;

            if (child.moduleFactory == null || child.moduleFactory == _moduleFactory)
            {
                child.moduleFactory = factory;
            }
        }
        
        _moduleFactory = factory;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Styles
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  inheritingStyles
    //----------------------------------

    /**
     *  @private
     *  Storage for the inheritingStyles property.
     */
    private var _inheritingStyles:Object = UIComponent.STYLE_UNINITIALIZED;
    
    [Inspectable(environment="none")]

    /**
     *  The beginning of this component's chain of inheriting styles.
     *  The <code>getStyle()</code> method simply accesses
     *  <code>inheritingStyles[styleName]</code> to search the entire
     *  prototype-linked chain.
     *  This object is set up by <code>initProtoChain()</code>.
     *  Developers typically never need to access this property directly.
     */
    public function get inheritingStyles():Object
    {
        return _inheritingStyles;
    }
    
    /**
     *  @private
     */
    public function set inheritingStyles(value:Object):void
    {
        _inheritingStyles = value;
    }

    //----------------------------------
    //  nonInheritingStyles
    //----------------------------------

    /**
     *  @private
     *  Storage for the nonInheritingStyles property.
     */
    private var _nonInheritingStyles:Object = UIComponent.STYLE_UNINITIALIZED;

    [Inspectable(environment="none")]

    /**
     *  The beginning of this component's chain of non-inheriting styles.
     *  The <code>getStyle()</code> method simply accesses
     *  <code>nonInheritingStyles[styleName]</code> to search the entire
     *  prototype-linked chain.
     *  This object is set up by <code>initProtoChain()</code>.
     *  Developers typically never need to access this property directly.
     */
    public function get nonInheritingStyles():Object
    {
        return _nonInheritingStyles;
    }

    /**
     *  @private
     */
    public function set nonInheritingStyles(value:Object):void
    {
        _nonInheritingStyles = value;
    }

     //----------------------------------
    //  styleDeclaration
    //----------------------------------

    /**
     *  @private
     *  Storage for the styleDeclaration property.
     */
    private var _styleDeclaration:CSSStyleDeclaration;

    [Inspectable(environment="none")]

    /**
     *  Storage for the inline inheriting styles on this object.
     *  This CSSStyleDeclaration is created the first time that 
     *  the <code>setStyle()</code> method
     *  is called on this component to set an inheriting style.
     *  Developers typically never need to access this property directly.
     */
    public function get styleDeclaration():CSSStyleDeclaration
    {
        return _styleDeclaration;
    }

    /**
     *  @private
     */
    public function set styleDeclaration(value:CSSStyleDeclaration):void
    {
        _styleDeclaration = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Bitmap caching
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  cachePolicy
    //----------------------------------

    /**
     *  @private
     *  Storage for cachePolicy property.
     */
    private var _cachePolicy:String = UIComponentCachePolicy.AUTO;

    [Inspectable(enumeration="on,off,auto", defaultValue="auto")]

    /**
     *  Specifies the bitmap caching policy for this object.
     *  Possible values in MXML are <code>"on"</code>,
     *  <code>"off"</code> and
     *  <code>"auto"</code> (default).
     * 
     *  <p>Possible values in ActionScript are <code>UIComponentCachePolicy.ON</code>,
     *  <code>UIComponentCachePolicy.OFF</code> and
     *  <code>UIComponentCachePolicy.AUTO</code> (default).</p>
     *
     *  <p><ul>
     *    <li>A value of <code>UIComponentCachePolicy.ON</code> means that 
     *      the object is always cached as a bitmap.</li>
     *    <li>A value of <code>UIComponentCachePolicy.OFF</code> means that 
     *      the object is never cached as a bitmap.</li>
     *    <li>A value of <code>UIComponentCachePolicy.AUTO</code> means that 
     *      the framework uses heuristics to decide whether the object should 
     *      be cached as a bitmap.</li>
     *  </ul></p>
     *
     *  @default UIComponentCachePolicy.AUTO
     */
    public function get cachePolicy():String
    {
        return _cachePolicy;
    }

    /**
     *  @private
     */
    public function set cachePolicy(value:String):void
    {
        if (_cachePolicy != value)
        {
            _cachePolicy = value;

            if (value == UIComponentCachePolicy.OFF)
                cacheAsBitmap = false;
            else if (value == UIComponentCachePolicy.ON)
                cacheAsBitmap = true;
            else
                cacheAsBitmap = (cacheAsBitmapCount > 0);
        }
    }

    //----------------------------------
    //  cacheHeuristic
    //----------------------------------

    /**
     *  @private
     *  Counter used by the cacheHeuristic property.
     */
    private var cacheAsBitmapCount:int = 0;

    [Inspectable(environment="none")]

    /**
     *  Used by Flex to suggest bitmap caching for the object.
     *  If <code>cachePolicy</code> is <code>UIComponentCachePolicy.AUTO</code>, 
     *  then <code>cacheHeuristic</code>
     *  is used to control the object's <code>cacheAsBitmap</code> property.
     */
    public function set cacheHeuristic(value:Boolean):void
    {
        if (_cachePolicy == UIComponentCachePolicy.AUTO)
        {
            if (value)
                cacheAsBitmapCount++;
            else if (cacheAsBitmapCount != 0)
                cacheAsBitmapCount--;

            super.cacheAsBitmap = (cacheAsBitmapCount != 0);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Focus management
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  focusPane
    //----------------------------------

    /**
     *  @private
     *  Storage for the focusPane property.
     */
    private var _focusPane:Sprite;

    [Inspectable(environment="none")]

    /**
     *  The focus pane associated with this object.
     *  An object has a focus pane when one of its children has focus.
     */
    public function get focusPane():Sprite
    {
        return _focusPane;
    }

    /**
     *  @private
     */
    public function set focusPane(value:Sprite):void
    {
        if (value)
        {
            addChild(value);

            value.x = 0;
            value.y = 0;
            value.scrollRect = null;

            _focusPane = value;
        }
        else
        {
            removeChild(_focusPane);
			
			_focusPane.mask = null;
            _focusPane = null;
        }
    }

    //----------------------------------
    //  focusEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the focusEnabled property.
     */
    private var _focusEnabled:Boolean = true;

    [Inspectable(defaultValue="true")]

    /**
     *  Indicates whether the component can receive focus when tabbed to.
     *  You can set <code>focusEnabled</code> to <code>false</code> 
     *  when a UIComponent is used as a subcomponent of another component 
     *  so that the outer component becomes the focusable entity.
     *  If this property is <code>false</code>, focus will be transferred to
     *  the first parent that has <code>focusEnable</code> 
     *  set to <code>true</code>.
     *  
     *  @default true
     */
    public function get focusEnabled():Boolean
    {
        return _focusEnabled;
    }

    /**
     *  @private
     */
    public function set focusEnabled(value:Boolean):void
    {
        _focusEnabled =  value;
    }

    //----------------------------------
    //  mouseFocusEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the mouseFocusEnabled property.
     */
    private var _mouseFocusEnabled:Boolean = true;

    [Inspectable(defaultValue="true")]

    /**
     *  Whether you can receive focus when clicked on.
     *  If <code>false</code>, focus will be transferred to
     *  the first parent that is <code>mouseFocusEnable</code> 
     *  set to <code>true</code>.
     *  For example, you can set this property to <code>false</code> 
     *  on a Button control so that you can use the Tab key to move focus 
     *  to the control, but not have the control get focus when you click on it.
     *
     *  @default true
     */
    public function get mouseFocusEnabled():Boolean
    {
        return _mouseFocusEnabled;
    }

    /**
     *  @private
     */
    public function set mouseFocusEnabled(value:Boolean):void
    {
        _mouseFocusEnabled =  value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Measurement
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  measuredMinWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the measuredMinWidth property.
     */
    private var _measuredMinWidth:Number = 0;

    [Inspectable(environment="none")]

    /**
     *  The default minimum width of the component, in pixels.
     *  This value is set by the <code>measure()</code> method.
     */
    public function get measuredMinWidth():Number
    {
        return _measuredMinWidth;
    }

    /**
     *  @private
     */
    public function set measuredMinWidth(value:Number):void
    {
        _measuredMinWidth = value;
    }

    //----------------------------------
    //  measuredMinHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the measuredMinHeight property.
     */
    private var _measuredMinHeight:Number = 0;

    [Inspectable(environment="none")]

    /**
     *  The default minimum height of the component, in pixels.
     *  This value is set by the <code>measure()</code> method.
     */
    public function get measuredMinHeight():Number
    {
        return _measuredMinHeight;
    }

    /**
     *  @private
     */
    public function set measuredMinHeight(value:Number):void
    {
        _measuredMinHeight = value;
    }

    //----------------------------------
    //  measuredWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the measuredWidth property.
     */
    private var _measuredWidth:Number = 0;
    
    [Inspectable(environment="none")]

    /**
     *  The default width of the component, in pixels.
     *  This value is set by the <code>measure()</code> method.
     */
    public function get measuredWidth():Number
    {
        return _measuredWidth;
    }

    /**
     *  @private
     */
    public function set measuredWidth(value:Number):void
    {
        _measuredWidth = value;
    }

    //----------------------------------
    //  measuredHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the measuredHeight property.
     */
    private var _measuredHeight:Number = 0;

    [Inspectable(environment="none")]

    /**
     *  The default height of the component, in pixels.
     *  This value is set by the <code>measure()</code> method.
     */
    public function get measuredHeight():Number
    {
        return _measuredHeight;
    }

    /**
     *  @private
     */
    public function set measuredHeight(value:Number):void
    {
        _measuredHeight = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Layout
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  percentWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the percentWidth property.
     */
    private var _percentWidth:Number;

    [Bindable("resize")]
    [Inspectable(environment="none")]

    /**
     *  Number that specifies the width of a component as a percentage
     *  of its parent's size. Allowed values are 0-100. The default value is NaN.
     *  Setting the <code>width</code> or <code>explicitWidth</code> properties 
     *  resets this property to NaN.
     *
     *  <p>This property returns a numeric value only if the property was
     *  previously set; it does not reflect the exact size of the component
     *  in percent.</p>
     *
     *  <p>This property is always set to NaN for the UITextField control.</p>
     */
    public function get percentWidth():Number
    {
        return _percentWidth;
    }

    /**
     *  @private
     */
    public function set percentWidth(value:Number):void
    {
        if (_percentWidth == value)
            return;

        if (!isNaN(value))
            _explicitWidth = NaN;

        _percentWidth = value;

        var p:IInvalidating = parent as IInvalidating;
        if (p)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }
    }

    //----------------------------------
    //  percentHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the percentHeight property.
     */
    private var _percentHeight:Number;

    [Bindable("resize")]
    [Inspectable(environment="none")]

    /**
     *  Number that specifies the height of a component as a percentage
     *  of its parent's size. Allowed values are 0-100. The default value is NaN.
     *  Setting the <code>height</code> or <code>explicitHeight</code> properties 
     *  resets this property to NaN.
     *
     *  <p>This property returns a numeric value only if the property was
     *  previously set; it does not reflect the exact size of the component
     *  in percent.</p>
     *
     *  <p>This property is always set to NaN for the UITextField control.</p>
     */
    public function get percentHeight():Number
    {
        return _percentHeight;
    }

    /**
     *  @private
     */
    public function set percentHeight(value:Number):void
    {
        if (_percentHeight == value)
            return;

        if (!isNaN(value))
            _explicitHeight = NaN;

        _percentHeight = value;

        var p:IInvalidating = parent as IInvalidating;
        if (p)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }
    }

    //----------------------------------
    //  minWidth
    //----------------------------------

    [Bindable("explicitMinWidthChanged")]
    [Inspectable(category="Size", defaultValue="0")]

    /**
     *  The minimum recommended width of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels. The default value depends on
     *  the component's implementation.
     *
     *  <p>If the application developer sets the value of minWidth,
     *  the new value is stored in explicitMinWidth. The default value of minWidth
     *  does not change. As a result, at layout time, if
     *  minWidth was explicitly set by the application developer, then the value of
     *  explicitMinWidth is used for the component's minimum recommended width.
     *  If minWidth is not set explicitly by the application developer, then the value of
     *  measuredMinWidth is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>minWidth</code> with respect to its parent
     *  is affected by the <code>scaleX</code> property.</p>
     */
    public function get minWidth():Number
    {
        if (!isNaN(explicitMinWidth))
            return explicitMinWidth;

        return measuredMinWidth;
    }

    /**
     *  @private
     */
    public function set minWidth(value:Number):void
    {
        if (explicitMinWidth == value)
            return;

        explicitMinWidth = value;
    }

    //----------------------------------
    //  minHeight
    //----------------------------------

    [Bindable("explicitMinHeightChanged")]
    [Inspectable(category="Size", defaultValue="0")]

    /**
     *  The minimum recommended height of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels. The default value depends on
     *  the component's implementation.
     *
     *  <p>If the application developer sets the value of minHeight,
     *  the new value is stored in explicitMinHeight. The default value of minHeight
     *  does not change. As a result, at layout time, if
     *  minHeight was explicitly set by the application developer, then the value of
     *  explicitMinHeight is used for the component's minimum recommended height.
     *  If minHeight is not set explicitly by the application developer, then the value of
     *  measuredMinHeight is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>minHeight</code> with respect to its parent
     *  is affected by the <code>scaleY</code> property.</p>
     */
    public function get minHeight():Number
    {
        if (!isNaN(explicitMinHeight))
            return explicitMinHeight;

        return measuredMinHeight;
    }

    /**
     *  @private
     */
    public function set minHeight(value:Number):void
    {
        if (explicitMinHeight == value)
            return;

        explicitMinHeight = value;
    }

    //----------------------------------
    //  maxWidth
    //----------------------------------

    [Bindable("explicitMaxWidthChanged")]
    [Inspectable(category="Size", defaultValue="10000")]

    /**
     *  The maximum recommended width of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels. The default value of this property is
     *  set by the component developer.
     *
     *  <p>The component developer uses this property to set an upper limit on the
     *  width of the component.</p>
     *
     *  <p>If the application developer overrides the default value of maxWidth,
     *  the new value is stored in explicitMaxWidth. The default value of maxWidth
     *  does not change. As a result, at layout time, if
     *  maxWidth was explicitly set by the application developer, then the value of
     *  explicitMaxWidth is used for the component's maximum recommended width.
     *  If maxWidth is not set explicitly by the user, then the default value is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>maxWidth</code> with respect to its parent
     *  is affected by the <code>scaleX</code> property.
     *  Some components have no theoretical limit to their width.
     *  In those cases their <code>maxWidth</code> will be set to
     *  <code>UIComponent.DEFAULT_MAX_WIDTH</code>.</p>
     *
     *  @default 10000
     */
    public function get maxWidth():Number
    {
        return !isNaN(explicitMaxWidth) ?
               explicitMaxWidth :
               DEFAULT_MAX_WIDTH;
    }

    /**
     *  @private
     */
    public function set maxWidth(value:Number):void
    {
        if (explicitMaxWidth == value)
            return;

        explicitMaxWidth = value;
    }

    //----------------------------------
    //  maxHeight
    //----------------------------------

    [Bindable("explicitMaxHeightChanged")]
    [Inspectable(category="Size", defaultValue="10000")]

    /**
     *  The maximum recommended height of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels. The default value of this property is
     *  set by the component developer.
     *
     *  <p>The component developer uses this property to set an upper limit on the
     *  height of the component.</p>
     *
     *  <p>If the application developer overrides the default value of maxHeight,
     *  the new value is stored in explicitMaxHeight. The default value of maxHeight
     *  does not change. As a result, at layout time, if
     *  maxHeight was explicitly set by the application developer, then the value of
     *  explicitMaxHeight is used for the component's maximum recommended height.
     *  If maxHeight is not set explicitly by the user, then the default value is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>maxHeight</code> with respect to its parent
     *  is affected by the <code>scaleY</code> property.
     *  Some components have no theoretical limit to their height.
     *  In those cases their <code>maxHeight</code> will be set to
     *  <code>UIComponent.DEFAULT_MAX_HEIGHT</code>.</p>
     *
     *  @default 10000
     */
    public function get maxHeight():Number
    {
        return !isNaN(explicitMaxHeight) ?
               explicitMaxHeight :
               DEFAULT_MAX_HEIGHT;
    }

    /**
     *  @private
     */
    public function set maxHeight(value:Number):void
    {
        if (explicitMaxHeight == value)
            return;

        explicitMaxHeight = value;
    }

    //----------------------------------
    //  explicitMinWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the minWidth property.
     */
    mx_internal var _explicitMinWidth:Number;

    [Bindable("explicitMinWidthChanged")]
    [Inspectable(environment="none")]

    /**
     *  The minimum recommended width of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels.
     *
     *  <p>Application developers typically do not set the explicitMinWidth property. Instead, they
     *  set the value of the minWidth property, which sets the explicitMinWidth property. The
     *  value of minWidth does not change.</p>
     *
     *  <p>At layout time, if minWidth was explicitly set by the application developer, then
     *  the value of explicitMinWidth is used. Otherwise, the value of measuredMinWidth
     *  is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>minWidth</code> with respect to its parent
     *  is affected by the <code>scaleX</code> property.</p>
     *
     *  @default NaN
     */
    public function get explicitMinWidth():Number
    {
        return _explicitMinWidth;
    }

    /**
     *  @private
     */
    public function set explicitMinWidth(value:Number):void
    {
        if (_explicitMinWidth == value)
            return;

        _explicitMinWidth = value;

        // We invalidate size because locking in width
        // may change the measured height in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }

        dispatchEvent(new Event("explicitMinWidthChanged"));
    }

    //----------------------------------
    //  minHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the minHeight property.
     */
    mx_internal var _explicitMinHeight:Number;

    [Bindable("explictMinHeightChanged")]
    [Inspectable(environment="none")]

    /**
     *  The minimum recommended height of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels.
     *
     *  <p>Application developers typically do not set the explicitMinHeight property. Instead, they
     *  set the value of the minHeight property, which sets the explicitMinHeight property. The
     *  value of minHeight does not change.</p>
     *
     *  <p>At layout time, if minHeight was explicitly set by the application developer, then
     *  the value of explicitMinHeight is used. Otherwise, the value of measuredMinHeight
     *  is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>minHeight</code> with respect to its parent
     *  is affected by the <code>scaleY</code> property.</p>
     *
     *  @default NaN
     */
    public function get explicitMinHeight():Number
    {
        return _explicitMinHeight;
    }

    /**
     *  @private
     */
    public function set explicitMinHeight(value:Number):void
    {
        if (_explicitMinHeight == value)
            return;

        _explicitMinHeight = value;

        // We invalidate size because locking in height
        // may change the measured width in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }

        dispatchEvent(new Event("explicitMinHeightChanged"));
    }

    //----------------------------------
    //  explicitMaxWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxWidth property.
     */
    mx_internal var _explicitMaxWidth:Number;

    [Bindable("explicitMaxWidthChanged")]
    [Inspectable(environment="none")]

    /**
     *  The maximum recommended width of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels.
     *
     *  <p>Application developers typically do not set the explicitMaxWidth property. Instead, they
     *  set the value of the maxWidth property, which sets the explicitMaxWidth property. The
     *  value of maxWidth does not change.</p>
     *
     *  <p>At layout time, if maxWidth was explicitly set by the application developer, then
     *  the value of explicitMaxWidth is used. Otherwise, the default value for maxWidth
     *  is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>maxWidth</code> with respect to its parent
     *  is affected by the <code>scaleX</code> property.
     *  Some components have no theoretical limit to their width.
     *  In those cases their <code>maxWidth</code> will be set to
     *  <code>UIComponent.DEFAULT_MAX_WIDTH</code>.</p>
     *
     *  @default NaN
     */
    public function get explicitMaxWidth():Number
    {
        return _explicitMaxWidth;
    }

    /**
     *  @private
     */
    public function set explicitMaxWidth(value:Number):void
    {
        if (_explicitMaxWidth == value)
            return;

        _explicitMaxWidth = value;

        // Se invalidate size because locking in width
        // may change the measured height in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }

        dispatchEvent(new Event("explicitMaxWidthChanged"));
    }

    //----------------------------------
    //  explicitMaxHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxHeight property.
     */
    mx_internal var _explicitMaxHeight:Number;

    [Bindable("explicitMaxHeightChanged")]
    [Inspectable(environment="none")]

    /**
     *  The maximum recommended height of the component to be considered
     *  by the parent during layout. This value is in the
     *  component's coordinates, in pixels.
     *
     *  <p>Application developers typically do not set the explicitMaxHeight property. Instead, they
     *  set the value of the maxHeight property, which sets the explicitMaxHeight property. The
     *  value of maxHeight does not change.</p>
     *
     *  <p>At layout time, if maxHeight was explicitly set by the application developer, then
     *  the value of explicitMaxHeight is used. Otherwise, the default value for maxHeight
     *  is used.</p>
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>maxHeight</code> with respect to its parent
     *  is affected by the <code>scaleY</code> property.
     *  Some components have no theoretical limit to their height.
     *  In those cases their <code>maxHeight</code> will be set to
     *  <code>UIComponent.DEFAULT_MAX_HEIGHT</code>.</p>
     *
     *  @default NaN
     */
    public function get explicitMaxHeight():Number
    {
        return _explicitMaxHeight;
    }

    /**
     *  @private
     */
    public function set explicitMaxHeight(value:Number):void
    {
        if (_explicitMaxHeight == value)
            return;

        _explicitMaxHeight = value;

        // Se invalidate size because locking in height
        // may change the measured width in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }

        dispatchEvent(new Event("explicitMaxHeightChanged"));
    }

    //----------------------------------
    //  explicitWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the explicitWidth property.
     */
    private var _explicitWidth:Number;

    [Bindable("explicitWidthChanged")]
    [Inspectable(environment="none")]

    /**
     *  Number that specifies the explicit width of the component,
     *  in pixels, in the component's coordinates.
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>explicitWidth</code> with respect to its parent
     *  is affected by the <code>scaleX</code> property.</p>
     *  <p>Setting the <code>width</code> property also sets this property to
     *  the specified width value.</p>
     */
    public function get explicitWidth():Number
    {
        return _explicitWidth;
    }

    /**
     *  @private
     */
    public function set explicitWidth(value:Number):void
    {
        if (_explicitWidth == value)
            return;

        // width can be pixel or percent not both
        if (!isNaN(value))
            _percentWidth = NaN;

        _explicitWidth = value;

        // We invalidate size because locking in width
        // may change the measured height in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p && includeInLayout)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }

        dispatchEvent(new Event("explicitWidthChanged"));
    }

    //----------------------------------
    //  explicitHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the explicitHeight property.
     */
    private var _explicitHeight:Number;

    [Bindable("explicitHeightChanged")]
    [Inspectable(environment="none")]

    /**
     *  Number that specifies the explicit height of the component,
     *  in pixels, in the component's coordinates.
     *
     *  <p>This value is used by the container in calculating
     *  the size and position of the component.
     *  It is not used by the component itself in determining
     *  its default size.
     *  Thus this property may not have any effect if parented by
     *  Container, or containers that don't factor in
     *  this property.
     *  Because the value is in component coordinates,
     *  the true <code>explicitHeight</code> with respect to its parent
     *  is affected by the <code>scaleY</code> property.</p>
     *  <p>Setting the <code>height</code> property also sets this property to
     *  the specified height value.</p>
     */
    public function get explicitHeight():Number
    {
        return _explicitHeight;
    }

    /**
     *  @private
     */
    public function set explicitHeight(value:Number):void
    {
        if (_explicitHeight == value)
            return;

        // height can be pixel or percent, not both
        if (!isNaN(value))
            _percentHeight = NaN;

        _explicitHeight = value;

        // We invalidate size because locking in height
        // may change the measured width in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p && includeInLayout)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }

        dispatchEvent(new Event("explicitHeightChanged"));
    }

    //----------------------------------
    //  includeInLayout
    //----------------------------------

    /**
     *  @private
     *  Storage for the includeInLayout property.
     */
    private var _includeInLayout:Boolean = true;

    [Bindable("includeInLayoutChanged")]
    [Inspectable(category="General", defaultValue="true")]

    /**
     *  Specifies whether this component is included in the layout of the
     *  parent container.
     *  If <code>true</code>, the object is included in its parent container's
     *  layout.  If <code>false</code>, the object is positioned by its parent
     *  container as per its layout rules, but it is ignored for the purpose of
     *  computing the position of the next child.
     *
     *  @default true
     */
    public function get includeInLayout():Boolean
    {
        return _includeInLayout;
    }
    
    /**
     *  @private
     */
    public function set includeInLayout(value:Boolean):void
    {
        if (_includeInLayout != value)
        {
            _includeInLayout = value;

            var p:IInvalidating = parent as IInvalidating;
            if (p)
            {
                p.invalidateSize();
                p.invalidateDisplayList();
            }

            dispatchEvent(new Event("includeInLayoutChanged"));
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Repeater
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  instanceIndex
    //----------------------------------

    /**
     *  The index of a repeated component. 
     *  If the component is not within a Repeater, the value is -1.
     */
    public function get instanceIndex():int
    {
        // For efficiency, _instanceIndices starts out null rather than [].
        return _instanceIndices ?
               _instanceIndices[_instanceIndices.length - 1] :
               -1;
    }

    //----------------------------------
    //  instanceIndices
    //----------------------------------

    /**
     *  @private
     *  Storage for the instanceIndices and index properties.
     */
    private var _instanceIndices:Array /* of int */;

    [Inspectable(environment="none")]

    /**
     *  An Array containing the indices required to reference
     *  this UIComponent object from its parent document.
     *  The Array is empty unless this UIComponent object is within one or more Repeaters.
     *  The first element corresponds to the outermost Repeater.
     *  For example, if the id is "b" and instanceIndices is [2,4],
     *  you would reference it on the parent document as b[2][4].
     */
    public function get instanceIndices():Array
    {
        // For efficiency, _instanceIndices starts out undefined rather than [].
        return _instanceIndices ? _instanceIndices.slice(0) : null;
    }

    /**
     *  @private
     */
    public function set instanceIndices(value:Array):void
    {
        _instanceIndices = value;
    }

    //----------------------------------
    //  repeater
    //----------------------------------

    /**
     *  A reference to the Repeater object
     *  in the parent document that produced this UIComponent.
     *  Use this property, rather than the <code>repeaters</code> property, 
     *  when the UIComponent is created by a single Repeater object. 
     *  Use the <code>repeaters</code> property when this UIComponent is created 
     *  by nested Repeater objects. 
     *
     *  <p>The property is set to <code>null</code> when this UIComponent 
     *  is not created by a Repeater.</p>
     */
    public function get repeater():IRepeater
    {
        // For efficiency, _repeaters starts out undefined rather than [].
        return _repeaters ? _repeaters[_repeaters.length - 1] : null;
    }

    //----------------------------------
    //  repeaters
    //----------------------------------

    /**
     *  @private
     *  Storage for the repeaters and repeater properties.
     */
    private var _repeaters:Array /* of Repeater */;

    [Inspectable(environment="none")]

    /**
     *  An Array containing references to the Repeater objects
     *  in the parent document that produced this UIComponent.
     *  The Array is empty unless this UIComponent is within
     *  one or more Repeaters.
     *  The first element corresponds to the outermost Repeater object.
     */
    public function get repeaters():Array
    {
        // For efficiency, _repeaters starts out undefined rather than [].
        return _repeaters ? _repeaters.slice(0) : [];
    }

    /**
     *  @private
     */
    public function set repeaters(value:Array):void
    {
        _repeaters = value;
    }

    //----------------------------------
    //  repeaterIndex
    //----------------------------------

    /**
     *  The index of the item in the data provider
     *  of the Repeater that produced this UIComponent.
     *  Use this property, rather than the <code>repeaterIndices</code> property, 
     *  when the UIComponent is created by a single Repeater object. 
     *  Use the <code>repeaterIndices</code> property when this UIComponent is created 
     *  by nested Repeater objects. 
     *
     *  <p>This property is set to -1 when this UIComponent is 
     *  not created by a Repeater.</p>
     */
    public function get repeaterIndex():int
    {
        // For efficiency, _repeaterIndices starts out null rather than [].
        return _repeaterIndices ?
               _repeaterIndices[_repeaterIndices.length - 1] :
               -1;
    }

    //----------------------------------
    //  repeaterIndices
    //----------------------------------

    /**
     *  @private
     *  Storage for the repeaterIndices and repeaterIndex properties.
     */
    private var _repeaterIndices:Array /* of int */;

    [Inspectable(environment="none")]

    /**
     *  An Array containing the indices of the items in the data provider
     *  of the Repeaters in the parent document that produced this UIComponent.
     *  The Array is empty unless this UIComponent is within one or more Repeaters.
     * 
     *  <p>The first element in the Array corresponds to the outermost Repeater.
     *  For example, if <code>repeaterIndices</code> is [2,4] it means that the
     *  outer repeater used item <code>dataProvider[2]</code> and the inner repeater
     *  used item <code>dataProvider[4]</code>.</p>
     * 
     *  <p>Note that this property differs from the <code>instanceIndices</code> property
     *  if the <code>startingIndex</code> property of any of the Repeaters is not 0.
     *  For example, even if a Repeater starts at <code>dataProvider[4]</code>,
     *  the document reference of the first repeated object is b[0], not b[4].</p>
     */
    public function get repeaterIndices():Array
    {
        // For efficiency, _repeaterIndices starts out null rather than [].
        return _repeaterIndices ? _repeaterIndices.slice() : [];
    }

    /**
     *  @private
     */
    public function set repeaterIndices(value:Array):void
    {
        _repeaterIndices = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: States
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  currentState
    //----------------------------------

    /**
     *  @private
     *  Storage for the currentState property.
     */
    private var _currentState:String;

    /**
     *  @private
     *  Pending current state name.
     */
    private var requestedCurrentState:String;

    /**
     *  @private
     *  Flag to play state transition
     */
    private var playStateTransition:Boolean = true;

    /**
     *  @private
     *  Flag that is set when the currentState has changed and needs to be
     *  committed.
     *  This property name needs the initial underscore to avoid collisions
     *  with the "currentStateChange" event attribute.
     */
    private var _currentStateChanged:Boolean;

    [Bindable("currentStateChange")]

    /**
     *  The current view state of the component.
     *  Set to <code>""</code> or <code>null</code> to reset
     *  the component back to its base state.
     *
     *  <p>When you use this property to set a component's state,
     *  Flex applies any transition you have defined.
     *  You can also use the <code>setCurrentState()</code> method to set the
     *  current state; this method can optionally change states without
     *  applying a transition.</p>
     *
     *  @see #setCurrentState()
     */
    public function get currentState():String
    {
        return _currentStateChanged ? requestedCurrentState : _currentState;
    }

    /**
     *  @private
     */
    public function set currentState(value:String):void
    {
        setCurrentState(value, true);
    }

    //----------------------------------
    //  states
    //----------------------------------

    [Inspectable(arrayType="mx.states.State")]
    [ArrayElementType("mx.states.State")]

    /**
     *  The view states that are defined for this component.
     *  You can specify the <code>states</code> property only on the root
     *  of the application or on the root tag of an MXML component.
     *  The compiler generates an error if you specify it on any other control.
     */
    public var states:Array /* of State */ = [];

    //----------------------------------
    //  transitions
    //----------------------------------

    /**
     *  @private
     *  Transition effect currently playing.
     */
    private var _currentTransitionEffect:IEffect;

    [Inspectable(arrayType="mx.states.Transition")]
    [ArrayElementType("mx.states.Transition")]

    /**
     *  An Array of Transition objects, where each Transition object defines a
     *  set of effects to play when a view state change occurs.
     *
     *  @see mx.states.Transition
     */
    public var transitions:Array /* of Transition */ = [];

    //--------------------------------------------------------------------------
    //
    //  Properties: Other
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  The y-coordinate of the baseline
     *  of the first line of text of the component.
     * 
     *  <p>This property is used to implement
     *  the <code>baseline</code> constraint style.
     *  It is also used to align the label of a FormItem
     *  with the controls in the FormItem.</p>
     * 
     *  <p>Each component should override this property.</p>
     */
    public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return NaN;

        if (!validateBaselinePosition())
            return NaN;
        
        // Unless the height is very small, the baselinePosition
        // of a generic UIComponent is calculated as if there was
        // a UITextField using the component's styles
        // whose top coincides with the component's top.
        // If the height is small, the baselinePosition is calculated
        // as if there were text within whose ascent the component
        // is vertically centered.
        // At the crossover height, these two calculations
        // produce the same result.
        
        var lineMetrics:TextLineMetrics = measureText("Wj");
        
        if (height < 2 + lineMetrics.ascent + 2)
            return int(height + (lineMetrics.ascent - height) / 2);
        
        return 2 + lineMetrics.ascent;
    }

    //----------------------------------
    //  className
    //----------------------------------

    /**
     *  The name of this instance's class, such as <code>"Button"</code>.
     *
     *  <p>This string does not include the package name.
     *  If you need the package name as well, call the
     *  <code>getQualifiedClassName()</code> method in the flash.utils package.
     *  It will return a string such as <code>"mx.controls::Button"</code>.</p>
     */
    public function get className():String
    {
        var name:String = getQualifiedClassName(this);
        
        // If there is a package name, strip it off.
        var index:int = name.indexOf("::");
        if (index != -1)
            name = name.substr(index + 2);
                
        return name;
    }
    
    //----------------------------------
    //  effectsStarted
    //----------------------------------
    
    /**
     *  The list of effects that are currently playing on the component,
     *  as an Array of EffectInstance instances.
     */
    public function get activeEffects():Array
    {
        return _effectsStarted;
    } 
    
    //----------------------------------
    //  flexContextMenu
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the flexContextMenu property.
     */
    private var _flexContextMenu:IFlexContextMenu;

    /**
     *  The context menu for this UIComponent. 
     *
     *  @default null
     */
    public function get flexContextMenu():IFlexContextMenu
    {
        return _flexContextMenu;
    }

    /**
     *  @private
     */
    public function set flexContextMenu(value:IFlexContextMenu):void
    {
        if (_flexContextMenu)
            _flexContextMenu.unsetContextMenu(this);
        
        _flexContextMenu = value;
        
        if (value != null)
            _flexContextMenu.setContextMenu(this);
    }

    //----------------------------------
    //  styleName
    //----------------------------------

    /**
     *  @private
     *  Storage for the styleName property.
     */
    private var _styleName:Object /* String, CSSStyleDeclaration, or UIComponent */;

    [Inspectable(category="General")]

    /**
     *  The class style used by this component. This can be a String, CSSStyleDeclaration
     *  or an IStyleClient. 
     *
     *  <p>If this is a String, it is the name of a class declaration
     *  in an <code>mx:Style</code> tag or CSS file. You do not include the period in 
     *  the <code>styleName</code>. For example, if you have a class style named <code>".bigText"</code>,
     *  set the <code>styleName</code> property to <code>"bigText"</code> (no period).</p>
     *
     *  <p>If this is an IStyleClient (typically a UIComponent), all styles in the
     *  <code>styleName</code> object are used by this component.</p>
     *
     *  @default null
     */
    public function get styleName():Object /* String, CSSStyleDeclaration, or UIComponent */
    {
        return _styleName;
    }

    /**
     *  @private
     */
    public function set styleName(value:Object /* String, CSSStyleDeclaration, or UIComponent */):void
    {
        if (_styleName === value)
            return;

        _styleName = value;

        // If inheritingStyles is undefined, then this object is being
        // initialized and we haven't yet generated the proto chain.
        // To avoid redundant work, don't bother to create
        // the proto chain here.
        if (inheritingStyles == UIComponent.STYLE_UNINITIALIZED)
            return;

        regenerateStyleCache(true);

        initThemeColor();

        styleChanged("styleName");

        notifyStyleChangeInChildren("styleName", true);
    }

    //----------------------------------
    //  toolTip
    //----------------------------------

    /**
     *  @private
     *  Storage for the toolTip property.
     */
    mx_internal var _toolTip:String;

    [Bindable("toolTipChanged")]
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Text to display in the ToolTip.
     *
     *  @default null
     */
    public function get toolTip():String
    {
        return _toolTip;
    }

    /**
     *  @private
     */
    public function set toolTip(value:String):void
    {
        var oldValue:String = _toolTip;
        _toolTip = value;
        
        ToolTipManager.registerToolTip(this, oldValue, value);

        dispatchEvent(new Event("toolTipChanged"));
    }

    //----------------------------------
    //  uid
    //----------------------------------

    /**
     *  @private
     */
    private var _uid:String;

    /**
     *  A unique identifier for the object. 
     *  Flex data-driven controls, including all controls that are 
     *  subclasses of List class, use a UID to track data provider items. 
     *
     *  <p>Flex can automatically create and manage UIDs. 
     *  However, there are circumstances when you must supply your own 
     *  <code>uid</code> property by implementing the IUID interface, 
     *  or when supplying your own <code>uid</code> property improves processing efficiency. 
     *  UIDs do not need to be universally unique for most uses in Flex. 
     *  One exception is for messages sent by data services.</p>
     *
     *  @see IUID
     *  @see mx.utils.UIDUtil
     */
    public function get uid():String
    {
        if (!_uid)
            _uid = toString();

        return _uid;
    }

    /**
     *  @private
     */
    public function set uid(uid:String):void
    {
        this._uid = uid;
    }

    //----------------------------------
    //  indexedID
    //----------------------------------

    /**
     *  @private
     *  Utility getter used by uid. It returns an indexed id string
     *  such as "foo[1][2]" if this object is a repeated object,
     *  or a nonindexed id string like "bar" if it isn't.
     */
    private function get indexedID():String
    {
        var s:String = id;
        var indices:Array /* of int */ = instanceIndices;
        if (indices)
            s += "[" + indices.join("][") + "]";
        return s;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Popups
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  isPopUp
    //----------------------------------

    /**
     *  @private
     */
    private var _isPopUp:Boolean;

    [Inspectable(environment="none")]

    /**
     *  Set to <code>true</code> by the PopUpManager to indicate
     *  that component has been popped up.
     */
    public function get isPopUp():Boolean
    {
        return _isPopUp;
    }
    public function set isPopUp(value:Boolean):void
    {
        _isPopUp = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties: Required to support automated testing
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  automationDelegate
    //----------------------------------

    /**
     *  @private
     */
    private var _automationDelegate:IAutomationObject;

    /**
     *  The delegate object that handles the automation-related functionality.
     */
    public function get automationDelegate():Object
    {
        return _automationDelegate;
    }

    /**
     *  @private
     */
    public function set automationDelegate(value:Object):void
    {
        _automationDelegate = value as IAutomationObject;
    }

    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  @private
     *  Storage for the <code>automationName</code> property.
     */
    private var _automationName:String = null;

    /**
     *  @inheritDoc
     */
    public function get automationName():String
    {
        if (_automationName)
            return _automationName; 
        if (automationDelegate)
           return automationDelegate.automationName;
        
        return "";
    }

    /**
     *  @private
     */
    public function set automationName(value:String):void
    {
        _automationName = value;
    }

    /**
     *  @copy mx.automation.IAutomationObject#automationValue
     */
    public function get automationValue():Array
    {
        if (automationDelegate)
           return automationDelegate.automationValue;
        
        return [];
    }

    //----------------------------------
    //  showInAutomationHierarchy
    //----------------------------------

    /**
     *  @private
     *  Storage for the <code>showInAutomationHierarchy</code> property.
     */
    private var _showInAutomationHierarchy:Boolean = true;
    
    /**
     *  @inheritDoc
     */
    public function get showInAutomationHierarchy():Boolean
    {
        return _showInAutomationHierarchy;
    }
    
    /**
     *  @private
     */
    public function set showInAutomationHierarchy(value:Boolean):void
    {
        _showInAutomationHierarchy = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties Validation
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  errorString
    //----------------------------------

    /**
     *  @private
     *  Storage for errorString property.
     */
    mx_internal var _errorString:String = "";
    
    /**
     *  @private
     *  Flag set when error string changes.
     */
    private var errorStringChanged:Boolean = false;

    [Bindable("errorStringChanged")]

    /**
     *  The text that will be displayed by a component's error tip when a
     *  component is monitored by a Validator and validation fails.
     *
     *  <p>You can use the <code>errorString</code> property to show a
     *  validation error for a component, without actually using a validator class.
     *  When you write a String value to the <code>errorString</code> property,
     *  Flex draws a red border around the component to indicate the validation error,
     *  and the String appears in a tooltip as the validation error message when you move
     *  the mouse over the component, just as if a validator detected a validation error.</p>
     *
     *  <p>To clear the validation error, write an empty String, "",
     *  to the <code>errorString</code> property.</p>
     *
     *  <p>Note that writing a value to the <code>errorString</code> property
     *  does not trigger the valid or invalid events; it only changes the border
     *  color and displays the validation error message.</p>
     */
    public function get errorString():String
    {
        return _errorString;
    }

    /**
     *  @private
     */
    public function set errorString(value:String):void
    {
        var oldValue:String = _errorString;
        _errorString = value;

        ToolTipManager.registerErrorString(this, oldValue, value);

        errorStringChanged = true;
        invalidateProperties();
        dispatchEvent(new Event("errorStringChanged"));
    }
    
    /**
     *  @private
     *  Set the appropriate borderColor based on errorString.
     *  If we have an errorString, use errorColor. If we don't
     *  have an errorString, restore the original borderColor.
     */
    private function setBorderColorForErrorString():void
    {
        if (!_errorString || _errorString.length == 0)
        {
            if (!isNaN(origBorderColor))
            {
                setStyle("borderColor", origBorderColor);
                saveBorderColor = true;
            }
        }
        else
        {
            // Remember the original border color
            if (saveBorderColor)
            {
                saveBorderColor = false;
                origBorderColor = getStyle("borderColor");
            }

            setStyle("borderColor", getStyle("errorColor"));
        }

        styleChanged("themeColor");

        var focusManager:IFocusManager = focusManager;
        var focusObj:DisplayObject = focusManager ?
                                     DisplayObject(focusManager.getFocus()) :
                                     null;
        if (focusManager && focusManager.showFocusIndicator &&
            focusObj == this)
        {
            drawFocus(true);
        }

    }

    //----------------------------------
    //  validationSubField
    //----------------------------------

    /**
     *  @private
     *  Storage for the validationSubField property.
     */
    private var _validationSubField:String;

    /**
     *  Used by a validator to associate a subfield with this component.
     */
    public function get validationSubField():String
    {
        return _validationSubField;
    }

    /**
     *  @private
     */
    public function set validationSubField(value:String):void
    {
        _validationSubField = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function addChild(child:DisplayObject):DisplayObject
    {
        var formerParent:DisplayObjectContainer = child.parent;
        if (formerParent && !(formerParent is Loader))
            formerParent.removeChild(child);
            
        // If there is an overlay, place the child underneath it.
        var index:int = overlayReferenceCount && child != overlay ?
                        Math.max(0, super.numChildren - 1) :
                        super.numChildren;
        
        // Do anything that needs to be done before the child is added.
        // When adding a child to UIComponent, this will set the child's
        // virtual parent, its nestLevel, its document, etc.
        // When adding a child to a Container, the override will also
        // invalidate the container, adjust its content/chrome partitions,
        // etc.
        addingChild(child);

        // Call a low-level player method in DisplayObjectContainer which
        // actually attaches the child to this component.
        // The player dispatches an "added" event from the child just after
        // it is attached, so all "added" handlers execute during this call.
        // UIComponent registers an addedHandler() in its constructor,
        // which makes it runs before any other "added" handlers except
        // capture-phase ones; it sets up the child's styles.
        $addChildAt(child, index);

        // Do anything that needs to be done after the child is added
        // and after all "added" handlers have executed.
        // This is where 
        childAdded(child);
        
        return child;
    }

    /**
     *  @private
     */
    override public function addChildAt(child:DisplayObject,
                                        index:int):DisplayObject
    {   
        var formerParent:DisplayObjectContainer = child.parent;
        if (formerParent && !(formerParent is Loader))
            formerParent.removeChild(child);
            
        // If there is an overlay, place the child underneath it.
        if (overlayReferenceCount && child != overlay)
             index = Math.min(index, Math.max(0, super.numChildren - 1));

        addingChild(child);

        $addChildAt(child, index);

        childAdded(child);

        return child;
    }

    /**
     *  @private
     */
    override public function removeChild(child:DisplayObject):DisplayObject
    {
        removingChild(child);

        $removeChild(child);

        childRemoved(child);

        return child;
    }

    /**
     *  @private
     */
    override public function removeChildAt(index:int):DisplayObject
    {
        var child:DisplayObject = getChildAt(index);
        
        removingChild(child);

        $removeChild(child);

        childRemoved(child);

        return child;
    }

    /**
     *  @private
     */
    override public function setChildIndex(child:DisplayObject,
                                           newIndex:int):void
    {
        // Place the child underneath the overlay.
        if (overlayReferenceCount && child != overlay)
            newIndex = Math.min(newIndex, Math.max(0, super.numChildren - 2));

        super.setChildIndex(child, newIndex);
    }

    /**
     *  @private
     */
    override public function stopDrag():void
    {
        super.stopDrag();

        invalidateProperties();

        dispatchEvent(new Event("xChanged"));
        dispatchEvent(new Event("yChanged"));
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Access to overridden methods of base classes
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of addChild(), which can be useful since components
     *  can override addChild() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $addChild(child:DisplayObject):DisplayObject
    {
        return super.addChild(child);
    }

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of addChildAt(), which can be useful since components
     *  can override addChildAt() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $addChildAt(child:DisplayObject,
                                           index:int):DisplayObject
    {
        return super.addChildAt(child, index);
    }

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of removeChild(), which can be useful since components
     *  can override removeChild() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $removeChild(child:DisplayObject):DisplayObject
    {
        return super.removeChild(child);
    }

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of removeChildAt(), which can be useful since components
     *  can override removeChildAt() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $removeChildAt(index:int):DisplayObject
    {
        return super.removeChildAt(index);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Initialization
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal function updateCallbacks():void
    {
        if (invalidateDisplayListFlag)
            UIComponentGlobals.layoutManager.invalidateDisplayList(this);

        if (invalidateSizeFlag)
            UIComponentGlobals.layoutManager.invalidateSize(this);

        if (invalidatePropertiesFlag)
            UIComponentGlobals.layoutManager.invalidateProperties(this);

        // systemManager getter tries to set the internal _systemManager varaible 
        // if it is null. Hence a call to the getter is necessary.
        if (systemManager)
        {
            if (methodQueue.length > 0 && !listeningForRender)
            {
                _systemManager.stage.addEventListener(Event.RENDER, callLaterDispatcher);
                _systemManager.stage.addEventListener(Event.ENTER_FRAME, callLaterDispatcher);
                listeningForRender = true;
            }

            // Force a "render" event to happen soon
            _systemManager.stage.invalidate();
        }
    }

    /**
     *  Called by Flex when a UIComponent object is added to or removed from a parent.
     *  Developers typically never need to call this method.
     *
     *  @param p The parent of this UIComponent object.
     */
    public function parentChanged(p:DisplayObjectContainer):void
    {
        if (!p)
        {
            _parent = null;
            _nestLevel = 0;
        }
        else if (p is IStyleClient)
        {
            _parent = p;
        }
        else if (p is ISystemManager)
        {
            _parent = p;
        }
        else
        {
            _parent = p.parent;
        }
    }

    /**
     *  @private
     */
    mx_internal function addingChild(child:DisplayObject):void
    {
        // If the document property isn't already set on the child,
        // set it to be the same as this component's document.
        // The document setter will recursively set it on any
        // descendants of the child that exist.
        if (child is IUIComponent &&
            !IUIComponent(child).document)
        {
            IUIComponent(child).document = document ?
                                           document :
                                           ApplicationGlobals.application;
        }
        
        // Propagate moduleFactory to the child, but don't overwrite an existing moduleFactory.
        if (child is UIComponent && UIComponent(child).moduleFactory == null)
        {
            if (moduleFactory != null)
                UIComponent(child).moduleFactory = moduleFactory;
            
            else if (document is IFlexModule && document.moduleFactory != null)
                UIComponent(child).moduleFactory = document.moduleFactory;
            
            else if (parent is UIComponent && UIComponent(parent).moduleFactory != null)
                UIComponent(child).moduleFactory = UIComponent(parent).moduleFactory;               
        }

        // Set the font context in non-UIComponent children.
        // UIComponent children use moduleFactory.
        if (child is IFontContextComponent && !child is UIComponent &&
            IFontContextComponent(child).fontContext == null)
            IFontContextComponent(child).fontContext = moduleFactory;
        
        if (child is IUIComponent)
            IUIComponent(child).parentChanged(this);
            
        // Set the nestLevel of the child to be one greater
        // than the nestLevel of this component.
        // The nestLevel setter will recursively set it on any
        // descendants of the child that exist.
        if (child is ILayoutManagerClient)
            ILayoutManagerClient(child).nestLevel = nestLevel + 1;
        else if (child is IUITextField)
            IUITextField(child).nestLevel = nestLevel + 1;

        if (child is InteractiveObject)
            if (doubleClickEnabled)
                InteractiveObject(child).doubleClickEnabled = true;
        
        // Sets up the inheritingStyles and nonInheritingStyles objects
        // and their proto chains so that getStyle() works.
        // If this object already has some children,
        // then reinitialize the children's proto chains.
        if (child is IStyleClient)
            IStyleClient(child).regenerateStyleCache(true);
        else if (child is IUITextField && IUITextField(child).inheritingStyles)
            StyleProtoChain.initTextField(IUITextField(child));

        if (child is ISimpleStyleClient)
            ISimpleStyleClient(child).styleChanged(null);

        if (child is IStyleClient)
            IStyleClient(child).notifyStyleChangeInChildren(null, true);

        if (child is UIComponent)
            UIComponent(child).initThemeColor();

        // Inform the component that it's style properties
        // have been fully initialized. Most components won't care,
        // but some need to react to even this early change.
        if (child is UIComponent)
            UIComponent(child).stylesInitialized();
    }

    /**
     *  @private
     */
    mx_internal function childAdded(child:DisplayObject):void
    {
        if (child is UIComponent)
        {
            if (!UIComponent(child).initialized)
                UIComponent(child).initialize();
        }
        else if (child is IUIComponent)
        {
            IUIComponent(child).initialize();
        }
    }

    /**
     *  @private
     */
    mx_internal function removingChild(child:DisplayObject):void
    {
    }

    /**
     *  @private
     */
    mx_internal function childRemoved(child:DisplayObject):void
    {
        if (child is IUIComponent)
        {
            // only reset document if the child isn't 
            // a document itself
            if (IUIComponent(child).document != child)
                IUIComponent(child).document = null;
            IUIComponent(child).parentChanged(null);
        }
    }

    /**
     *  Initializes the internal structure of this component.
     *
     *  <p>Initializing a UIComponent is the fourth step in the creation
     *  of a visual component instance, and happens automatically
     *  the first time that the instance is added to a parent.
     *  Therefore, you do not generally need to call
     *  <code>initialize()</code>; the Flex framework calls it for you
     *  from UIComponent's override of the <code>addChild()</code>
     *  and <code>addChildAt()</code> methods.</p>
     *
     *  <p>The first step in the creation of a visual component instance
     *  is construction, with the <code>new</code> operator:</p>
     *
     *  <pre>
     *  var okButton:Button = new Button();</pre>
     *
     *  <p>After construction, the new Button instance is a solitary
     *  DisplayObject; it does not yet have a UITextField as a child
     *  to display its label, and it doesn't have a parent.</p>
     *
     *  <p>The second step is configuring the newly-constructed instance
     *  with the appropriate properties, styles, and event handlers:</p>
     *
     *  <pre>
     *  okButton.label = "OK";
     *  okButton.setStyle("cornerRadius", 0);
     *  okButton.addEventListener(MouseEvent.CLICK, clickHandler);</pre>
     *
     *  <p>The third step is adding the instance to a parent:</p>
     *
     *  <pre>
     *  someContainer.addChild(okButton);</pre>
     *
     *  <p>A side effect of calling <code>addChild()</code>
     *  or <code>addChildAt()</code>, when adding a component to a parent
     *  for the first time, is that <code>initialize</code> gets
     *  automatically called.</p>
     *
     *  <p>This method first dispatches a <code>preinitialize</code> event,
     *  giving developers using this component a chance to affect it
     *  before its internal structure has been created.
     *  Next it calls the <code>createChildren()</code> method
     *  to create the component's internal structure; for a Button,
     *  this method creates and adds the UITextField for the label.
     *  Then it dispatches an <code>initialize</code> event,
     *  giving developers a chance to affect the component
     *  after its internal structure has been created.</p>
     *
     *  <p>Note that it is the act of attaching a component to a parent
     *  for the first time that triggers the creation of its internal structure.
     *  If its internal structure includes other UIComponents, then this is a
     *  recursive process in which the tree of DisplayObjects grows by one leaf
     *  node at a time.</p> 
     *
     *  <p>If you are writing a component, you should not need
     *  to override this method.</p>
     */
    public function initialize():void
    {
        if (initialized)
            return;
        
        // The "preinitialize" event gets dispatched after everything about this
        // DisplayObject has been initialized, and it has been attached to
        // its parent, but before any of its children have been created.
        // This allows a "preinitialize" event handler to set properties which
        // affect child creation.
        // Note that this implies that "preinitialize" handlers are called
        // top-down; i.e., parents before children.
        dispatchEvent(new FlexEvent(FlexEvent.PREINITIALIZE));

        // Create child objects.
        createChildren();
        childrenCreated();

        // Create and initialize the accessibility implementation.
        // for this component. For some components accessible object is attached
        // to child component so it should be called after createChildren
        initializeAccessibility();
        
        // This should always be the last thing that initialize() calls.
        initializationComplete();
    }

    /**
     *  Finalizes the initialization of this component.
     *
     *  <p>This method is the last code that executes when you add a component
     *  to a parent for the first time using <code>addChild()</code>
     *  or <code>addChildAt()</code>.
     *  It handles some housekeeping related to dispatching
     *  the <code>initialize</code> event.
     *  If you are writing a component, you should not need
     *  to override this method.</p>
     */
    protected function initializationComplete():void
    {
        processedDescriptors = true;
    }

    /**
     *  Initializes this component's accessibility code.
     *
     *  <p>This method is called by the <code>initialize()</code> method to hook in the
     *  component's accessibility code, which resides in a separate class
     *  in the mx.accessibility package.
     *  Each subclass that supports accessibility must override this method
     *  because the hook-in process uses a different static variable
     *  in each subclass.</p>
     */
    protected function initializeAccessibility():void
    {
        if (UIComponent.createAccessibilityImplementation != null)
            UIComponent.createAccessibilityImplementation(this);
    }

    /**
     *  Initializes various properties which keep track of repeated instances
     *  of this component.
     *
     *  <p>An MXML <code>&lt;mx:Repeater/&gt;</code> tag can cause repeated instances
     *  of a component to be created, one instance for each item in the
     *  Repeater's data provider.
     *  The <code>instanceIndices</code>, <code>repeaters</code>,
     *  and <code>repeaterIndices</code> properties of UIComponent
     *  keep track of which instance came from which data item
     *  and which Repeater.</p>
     *
     *  <p>This method is an internal method which is automatically called
     *  by the Flex framework.
     *  You should not have to call it or override it.</p>
     *
     *  @param parent The parent object containing the Repeater that created
     *  this component.
     */
    public function initializeRepeaterArrays(parent:IRepeaterClient):void
    {
        if (parent && parent.instanceIndices &&
            !_instanceIndices && !parent.isDocument)
        {
            _instanceIndices = parent.instanceIndices;
            _repeaters = parent.repeaters;
            _repeaterIndices = parent.repeaterIndices;
        }
    }

    /**
     *  Create child objects of the component.
     *  This is an advanced method that you might override
     *  when creating a subclass of UIComponent.
     *
     *  <p>A component that creates other components or objects within it is called a composite component.
     *  For example, the Flex ComboBox control is actually made up of a TextInput control
     *  to define the text area of the ComboBox, and a Button control to define the ComboBox arrow.
     *  Components implement the <code>createChildren()</code> method to create child
     *  objects (such as other components) within the component.</p>
     *
     *  <p>From within an override of the <code>createChildren()</code> method,
     *  you call the <code>addChild()</code> method to add each child object. </p>
     *
     *  <p>You do not call this method directly. Flex calls the
     *  <code>createChildren()</code> method in response to the call to
     *  the <code>addChild()</code> method to add the component to its parent. </p>
     */
    protected function createChildren():void
    {
    }

    /**
     *  Performs any final processing after child objects are created.
     *  This is an advanced method that you might override
     *  when creating a subclass of UIComponent.
     */
    protected function childrenCreated():void
    {
        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();
    }
    
   
    //--------------------------------------------------------------------------
    //
    //  Methods: Invalidation
    //
    //--------------------------------------------------------------------------

    /**
     *  Marks a component so that its <code>commitProperties()</code>
     *  method gets called during a later screen update.
     *
     *  <p>Invalidation is a useful mechanism for eliminating duplicate
     *  work by delaying processing of changes to a component until a
     *  later screen update.
     *  For example, if you want to change the text color and size,
     *  it would be wasteful to update the color immediately after you
     *  change it and then update the size when it gets set.
     *  It is more efficient to change both properties and then render
     *  the text with its new size and color once.</p>
     *
     *  <p>Invalidation methods rarely get called.
     *  In general, setting a property on a component automatically
     *  calls the appropriate invalidation method.</p>
     */
    public function invalidateProperties():void
    {
        if (!invalidatePropertiesFlag)
        {
            invalidatePropertiesFlag = true;

            if (parent && UIComponentGlobals.layoutManager)
                UIComponentGlobals.layoutManager.invalidateProperties(this);
        }
    }

    /**
     *  Marks a component so that its <code>measure()</code>
     *  method gets called during a later screen update.
     *
     *  <p>Invalidation is a useful mechanism for eliminating duplicate
     *  work by delaying processing of changes to a component until a
     *  later screen update.
     *  For example, if you want to change the text and font size,
     *  it would be wasteful to update the text immediately after you
     *  change it and then update the size when it gets set.
     *  It is more efficient to change both properties and then render
     *  the text with its new size once.</p>
     *
     *  <p>Invalidation methods rarely get called.
     *  In general, setting a property on a component automatically
     *  calls the appropriate invalidation method.</p>
     */
    public function invalidateSize():void
    {
        if (!invalidateSizeFlag)
        {
            invalidateSizeFlag = true;

            if (parent && UIComponentGlobals.layoutManager)
                UIComponentGlobals.layoutManager.invalidateSize(this);
        }
    }

    /**
     *  Marks a component so that its <code>updateDisplayList()</code>
     *  method gets called during a later screen update.
     *
     *  <p>Invalidation is a useful mechanism for eliminating duplicate
     *  work by delaying processing of changes to a component until a
     *  later screen update.
     *  For example, if you want to change the width and height,
     *  it would be wasteful to update the component immediately after you
     *  change the width and then update again with the new height.
     *  It is more efficient to change both properties and then render
     *  the component with its new size once.</p>
     *
     *  <p>Invalidation methods rarely get called.
     *  In general, setting a property on a component automatically
     *  calls the appropriate invalidation method.</p>
     */
    public function invalidateDisplayList():void
    {
        if (!invalidateDisplayListFlag)
        {
            invalidateDisplayListFlag = true;

            if (parent && UIComponentGlobals.layoutManager)
                UIComponentGlobals.layoutManager.invalidateDisplayList(this);
        }
    }

    /**
     *  Flex calls the <code>stylesInitialized()</code> method when
     *  the styles for a component are first initialized.
     *
     *  <p>This is an advanced method that you might override
     *  when creating a subclass of UIComponent. Flex guarantees that
     *  your component's styles will be fully initialized before
     *  the first time your component's <code>measure</code> and
     *  <code>updateDisplayList</code> methods are called.  For most
     *  components, that is sufficient. But if you need early access to
     *  your style values, you can override the stylesInitialized() function
     *  to access style properties as soon as they are initialized the first time.</p>
     */
    public function stylesInitialized():void
    {
    }

    /**
     *  Detects changes to style properties. When any style property is set,
     *  Flex calls the <code>styleChanged()</code> method,
     *  passing to it the name of the style being set.
     *
     *  <p>This is an advanced method that you might override
     *  when creating a subclass of UIComponent. When you create a custom component,
     *  you can override the <code>styleChanged()</code> method
     *  to check the style name passed to it, and handle the change accordingly.
     *  This lets you override the default behavior of an existing style,
     *  or add your own custom style properties.</p>
     *
     *  <p>If you handle the style property, your override of
     *  the <code>styleChanged()</code> method should call the
     *  <code>invalidateDisplayList()</code> method to cause Flex to execute
     *  the component's <code>updateDisplayList()</code> method at the next screen update.</p>
     *
     *  @param styleProp The name of the style property, or null if all styles for this
     *  component have changed.
     */
    public function styleChanged(styleProp:String):void
    {

        // If font changed, then invalidateProperties so
        // we can re-create the text field in commitProperties
        if (this is IFontContextComponent && hasFontContextChanged())
            invalidateProperties();
        
        // Check to see if this is one of the style properties
        // that is known to affect layout.
        if (!styleProp ||
            styleProp == "styleName" ||
            StyleManager.isSizeInvalidatingStyle(styleProp))
        {
            // This style property change may affect the layout of this
            // object. Signal the LayoutManager to re-measure the object.
            invalidateSize();
        }

        if (!styleProp || 
            styleProp == "styleName" ||
            styleProp == "themeColor")
        {
            initThemeColor();
        }
        
        invalidateDisplayList();

        if (parent is IInvalidating)
        {
            if (StyleManager.isParentSizeInvalidatingStyle(styleProp))
                IInvalidating(parent).invalidateSize();

            if (StyleManager.isParentDisplayListInvalidatingStyle(styleProp))
                IInvalidating(parent).invalidateDisplayList();
        }
    }

    /**
     *  Validate and update the properties and layout of this object
     *  and redraw it, if necessary.
     *
     *  Processing properties that require substantial computation are normally
     *  not processed until the script finishes executing.
     *  For example setting the <code>width</code> property is delayed, because it may
     *  require recalculating the widths of the objects children or its parent.
     *  Delaying the processing prevents it from being repeated
     *  multiple times if the script sets the <code>width</code> property more than once.
     *  This method lets you manually override this behavior.
     */
    public function validateNow():void
    {
        UIComponentGlobals.layoutManager.validateClient(this);
    }
    
    /**
     *  @private
     *  This method is called at the beginning of each getter
     *  for the baselinePosition property.
     *  If it returns false, the getter should return NaN
     *  because the baselinePosition can't be computed.
     *  If it returns true, the getter can do computations
     *  like textField.y + textField.baselinePosition
     *  because these properties will be valid.
     */
    mx_internal function validateBaselinePosition():Boolean
    {
        // If this component isn't parented,
        // then it doesn't know its text styles
        // and we can't compute a baselinePosition.
        if (!parent)
            return false;
            
        // If this component hasn't been sized yet, assign it
        // an actual size that's based on its explicit or measured size.
        if (width == 0 && height == 0)
        {
            validateNow();
            
            var w:Number = getExplicitOrMeasuredWidth();
            var h:Number = getExplicitOrMeasuredHeight();
            
            setActualSize(w, h);
        }
        
        // Ensure that this component's internal TextFields
        // are properly laid out, so that we can use
        // their locations to compute a baselinePosition.
        validateNow();
        
        return true;
    }

    /**
     *  Queues a function to be called later.
     *
     *  <p>Before each update of the screen, Flash Player or AIR calls
     *  the set of functions that are scheduled for the update.
     *  Sometimes, a function should be called in the next update
     *  to allow the rest of the code scheduled for the current
     *  update to be executed.
     *  Some features, like effects, can cause queued functions to be
     *  delayed until the feature completes.</p>
     *
     *  @param method Reference to a method to be executed later.
     *
     *  @param args Array of Objects that represent the arguments to pass to the method.
     *  
     */
    public function callLater(method:Function,
                              args:Array /* of Object */ = null):void
    {
        // trace(">>calllater " + this)
        // Push the method and the arguments onto the method queue.
        methodQueue.push(new MethodQueueElement(method, args));

        // Register to get the next "render" event
        // just before the next rasterization.
        var sm:ISystemManager = systemManager;
        if (sm && sm.stage)
        {
            if (!listeningForRender)
            {
                // trace("  added");
                sm.stage.addEventListener(Event.RENDER, callLaterDispatcher);
                sm.stage.addEventListener(Event.ENTER_FRAME, callLaterDispatcher);
                listeningForRender = true;
            }

            // Force a "render" event to happen soon
            sm.stage.invalidate();
        }
        // trace("<<calllater " + this)
    }

    /**
     *  @private
     *  Cancels all queued functions.
     */
    mx_internal function cancelAllCallLaters():void
    {
        var sm:ISystemManager = systemManager;
        if (sm && sm.stage)
        {
            if (listeningForRender)
            {
                sm.stage.removeEventListener(Event.RENDER, callLaterDispatcher);
                sm.stage.removeEventListener(Event.ENTER_FRAME, callLaterDispatcher);
                listeningForRender = false;
            }
        }

        // Empty the method queue.
        methodQueue.splice(0);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Commitment
    //
    //--------------------------------------------------------------------------

    /**
     *  Used by layout logic to validate the properties of a component
     *  by calling the <code>commitProperties()</code> method.  
     *  In general, subclassers should
     *  override the <code>commitProperties()</code> method and not this method.
     */
    public function validateProperties():void
    {
        if (invalidatePropertiesFlag)
        {
            commitProperties();

            invalidatePropertiesFlag = false;
        }
    }

    /**
     *  Processes the properties set on the component.
     *  This is an advanced method that you might override
     *  when creating a subclass of UIComponent.
     *
     *  <p>You do not call this method directly.
     *  Flex calls the <code>commitProperties()</code> method when you
     *  use the <code>addChild()</code> method to add a component to a container,
     *  or when you call the <code>invalidateProperties()</code> method of the component.
     *  Calls to the <code>commitProperties()</code> method occur before calls to the
     *  <code>measure()</code> method. This lets you set property values that might
     *  be used by the <code>measure()</code> method.</p>
     *
     *  <p>Some components have properties that affect the number or kinds
     *  of child objects that they need to create, or have properties that
     *  interact with each other, such as the <code>horizontalScrollPolicy</code>
     *  and <code>horizontalScrollPosition</code> properties.
     *  It is often best at startup time to process all of these
     *  properties at one time to avoid duplicating work.</p>
     */
    protected function commitProperties():void
    {
        if (_scaleX != oldScaleX)
        {
            var scalingFactorX:Number = Math.abs(_scaleX / oldScaleX);
            if (!isNaN(explicitMinWidth))
                explicitMinWidth *= scalingFactorX;
            if (!isNaN(explicitWidth))
                explicitWidth *= scalingFactorX;
            if (!isNaN(explicitMaxWidth))
                explicitMaxWidth *= scalingFactorX;

            _width *= scalingFactorX;

            super.scaleX = oldScaleX = _scaleX;
        }

        if (_scaleY != oldScaleY)
        {
            var scalingFactorY:Number = Math.abs(_scaleY / oldScaleY);
            if (!isNaN(explicitMinHeight))
                explicitMinHeight *= scalingFactorY;
            if (!isNaN(explicitHeight))
                explicitHeight *= scalingFactorY;
            if (!isNaN(explicitMaxHeight))
                explicitMaxHeight *= scalingFactorY;

            _height *= scalingFactorY;

            super.scaleY = oldScaleY = _scaleY;
        }

        if (x != oldX || y != oldY)
            dispatchMoveEvent();

        if (width != oldWidth || height != oldHeight)
            dispatchResizeEvent();
        
        if (errorStringChanged)
        {
            errorStringChanged = false;
            setBorderColorForErrorString();
        }
       
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Measurement
    //
    //--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    public function validateSize(recursive:Boolean = false):void
    {
        if (recursive)
        {
            for (var i:int = 0; i < numChildren; i++)
            {
                var child:DisplayObject = getChildAt(i);
                if (child is ILayoutManagerClient )
                    (child as ILayoutManagerClient ).validateSize(true);
            }
        }

        if (invalidateSizeFlag)
        {
            var sizeChanging:Boolean = measureSizes();

            if (sizeChanging && includeInLayout)
            {
                invalidateDisplayList();

                var p:IInvalidating = parent as IInvalidating;
                if (p)
                {
                    p.invalidateSize();
                    p.invalidateDisplayList();
                }
            }
        }
    }

    /**
     *  @private
     */
    private function measureSizes():Boolean
    {
        var changed:Boolean = false;

        if (!invalidateSizeFlag)
            return changed;

        var scalingFactor:Number;
        var newValue:Number;

        // We can skip the measure function if the object's width and height
        // have been explicitly specified (e.g.: the object's MXML tag has
        // attributes like width="50" and height="100").
        //
        // If an object's width and height have been explicitly specified,
        // then the explicitWidth and explicitHeight properties contain
        // Numbers (as opposed to NaN)
        if (isNaN(explicitWidth) ||
            isNaN(explicitHeight))
        {
            var xScale:Number = Math.abs(scaleX);
            var yScale:Number = Math.abs(scaleY);

            if (xScale != 1.0)
            {
                _measuredMinWidth /= xScale;
                _measuredWidth /= xScale;
            }

            if (yScale != 1.0)
            {
                _measuredMinHeight /= yScale;
                _measuredHeight /= yScale;
            }

            measure();

            invalidateSizeFlag = false;

            if (!isNaN(explicitMinWidth) && measuredWidth < explicitMinWidth)
                measuredWidth = explicitMinWidth;

            if (!isNaN(explicitMaxWidth) && measuredWidth > explicitMaxWidth)
                measuredWidth = explicitMaxWidth;

            if (!isNaN(explicitMinHeight) && measuredHeight < explicitMinHeight)
                measuredHeight = explicitMinHeight;

            if (!isNaN(explicitMaxHeight) && measuredHeight > explicitMaxHeight)
                measuredHeight = explicitMaxHeight;

            if (xScale != 1.0)
            {
                _measuredMinWidth *= xScale;
                _measuredWidth *= xScale;
            }

            if (yScale != 1.0)
            {
                _measuredMinHeight *= yScale;
                _measuredHeight *= yScale;
            }
        }
        else
        {
            invalidateSizeFlag = false;
            _measuredMinWidth = 0;
            _measuredMinHeight = 0;
        }

        adjustSizesForScaleChanges();

        if (isNaN(oldMinWidth))
        {
            // This branch does the same thing as the else branch,
            // but it is optimized for the first time that
            // measureSizes() is called on this object.
            oldMinWidth = !isNaN(explicitMinWidth) ?
                          explicitMinWidth :
                          measuredMinWidth;

            oldMinHeight = !isNaN(explicitMinHeight) ?
                           explicitMinHeight :
                           measuredMinHeight;

            oldExplicitWidth = !isNaN(explicitWidth) ?
                                explicitWidth :
                                measuredWidth;

            oldExplicitHeight = !isNaN(explicitHeight) ?
                                 explicitHeight :
                                 measuredHeight;

            changed = true;
        }
        else
        {
            newValue = !isNaN(explicitMinWidth) ?
                        explicitMinWidth :
                        measuredMinWidth;
            if (newValue != oldMinWidth)
            {
                oldMinWidth = newValue;
                changed = true;
            }

            newValue = !isNaN(explicitMinHeight) ?
                       explicitMinHeight :
                       measuredMinHeight;
            if (newValue != oldMinHeight)
            {
                oldMinHeight = newValue;
                changed = true;
            }

            newValue = !isNaN(explicitWidth) ?
                       explicitWidth :
                       measuredWidth;
            if (newValue != oldExplicitWidth)
            {
                oldExplicitWidth = newValue;
                changed = true;
            }

            newValue = !isNaN(explicitHeight) ?
                       explicitHeight :
                       measuredHeight;
            if (newValue != oldExplicitHeight)
            {
                oldExplicitHeight = newValue;
                changed = true;
            }

        }

        return changed;
    }

    /**
     *  Calculates the default size, and optionally the default minimum size,
     *  of the component. This is an advanced method that you might override when
     *  creating a subclass of UIComponent.
     *
     *  <p>You do not call this method directly. Flex calls the
     *  <code>measure()</code> method when the component is added to a container
     *  using the <code>addChild()</code> method, and when the component's
     *  <code>invalidateSize()</code> method is called. </p>
     *
     *  <p>When you set a specific height and width of a component, 
     *  Flex does not call the <code>measure()</code> method, 
     *  even if you explicitly call the <code>invalidateSize()</code> method. 
     *  That is, Flex only calls the <code>measure()</code> method if 
     *  the <code>explicitWidth</code> property or the <code>explicitHeight</code> 
     *  property of the component is NaN. </p>
     * 
     *  <p>In your override of this method, you must set the
     *  <code>measuredWidth</code> and <code>measuredHeight</code> properties
     *  to define the default size.
     *  You may optionally set the <code>measuredMinWidth</code> and
     *  <code>measuredMinHeight</code> properties to define the default
     *  minimum size.</p>
     *
     *  <p>Most components calculate these values based on the content they are
     *  displaying, and from the properties that affect content display.
     *  A few components simply have hard-coded default values. </p>
     *
     *  <p>The conceptual point of <code>measure()</code> is for the component to provide
     *  its own natural or intrinsic size as a default. Therefore, the
     *  <code>measuredWidth</code> and <code>measuredHeight</code> properties
     *  should be determined by factors such as:</p>
     *  <ul>
     *     <li>The amount of text the component needs to display.</li>
     *     <li>The styles, such as <code>fontSize</code>, for that text.</li>
     *     <li>The size of a JPEG image that the component displays.</li>
     *     <li>The measured or explicit sizes of the component's children.</li>
     *     <li>Any borders, margins, and gaps.</li>
     *  </ul>
     *
     *  <p>In some cases, there is no intrinsic way to determine default values.
     *  For example, a simple GreenCircle component might simply set
     *  measuredWidth = 100 and measuredHeight = 100 in its <code>measure()</code> method to
     *  provide a reasonable default size. In other cases, such as a TextArea,
     *  an appropriate computation (such as finding the right width and height
     *  that would just display all the text and have the aspect ratio of a Golden Rectangle)
     *  might be too time-consuming to be worthwhile.</p>
     *
     *  <p>The default implementation of <code>measure()</code>
     *  sets <code>measuredWidth</code>, <code>measuredHeight</code>,
     *  <code>measuredMinWidth</code>, and <code>measuredMinHeight</code>
     *  to <code>0</code>.</p>
     */
    protected function measure():void
    {
        measuredMinWidth = 0;
        measuredMinHeight = 0;
        measuredWidth = 0;
        measuredHeight = 0;
    }

    /**
     *  @private
     */
    mx_internal function adjustSizesForScaleChanges():void
    {
        var xScale:Number = scaleX;
        var yScale:Number = scaleY;

        var scalingFactor:Number;

        if (xScale != oldScaleX)
        {
            scalingFactor = Math.abs(xScale / oldScaleX);

            if (explicitMinWidth)
                explicitMinWidth *= scalingFactor;

            if (!isNaN(explicitWidth))
                explicitWidth *= scalingFactor;

            if (explicitMaxWidth)
                explicitMaxWidth *= scalingFactor;

            oldScaleX = xScale;
        }

        if (yScale != oldScaleY)
        {
            scalingFactor = Math.abs(yScale / oldScaleY);

            if (explicitMinHeight)
                explicitMinHeight *= scalingFactor;

            if (explicitHeight)
                explicitHeight *= scalingFactor;

            if (explicitMaxHeight)
                explicitMaxHeight *= scalingFactor;

            oldScaleY = yScale;
        }
    }

    /**
     *  A convenience method for determining whether to use the
     *  explicit or measured width
     *
     *  @return A Number which is explicitWidth if defined
     *  or measuredWidth if not.
     */
    public function getExplicitOrMeasuredWidth():Number
    {
        return !isNaN(explicitWidth) ? explicitWidth : measuredWidth;
    }

    /**
     *  A convenience method for determining whether to use the
     *  explicit or measured height
     *
     *  @return A Number which is explicitHeight if defined
     *  or measuredHeight if not.
     */
    public function getExplicitOrMeasuredHeight():Number
    {
        return !isNaN(explicitHeight) ? explicitHeight : measuredHeight;
    }

    /**
     *  A convenience method for determining the unscaled width
     *  of the component
     *  All of a component's drawing and child layout should be done
     *  within a bounding rectangle of this width, which is also passed
     *  as an argument to <code>updateDisplayList()</code>.
     *
     *  @return A Number which is unscaled width of the component.
     */
    protected function get unscaledWidth():Number
    {
        return width / Math.abs(scaleX);
    }

    /**
     *  A convenience method for setting the unscaledWidth of a 
     *  component. 
     * 
     *  Setting this sets the width of the component as desired
     *  before any transformation is applied. 
     */
    mx_internal function setUnscaledWidth(value:Number):void
    {
        var scaledValue:Number = value * Math.abs(oldScaleX);
        if (_explicitWidth == scaledValue)
            return;

        // width can be pixel or percent not both
        if (!isNaN(scaledValue))
            _percentWidth = NaN;
            
        _explicitWidth = scaledValue;

        // We invalidate size because locking in width
        // may change the measured height in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p && includeInLayout)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }
    }

    /**
     *  A convenience method for determining the unscaled height
     *  of the component.
     *  All of a component's drawing and child layout should be done
     *  within a bounding rectangle of this height, which is also passed
     *  as an argument to <code>updateDisplayList()</code>.
     *
     *  @return A Number which is unscaled height of the component.
     */
    protected function get unscaledHeight():Number
    {
        return height / Math.abs(scaleY);
    }

    /**
     *  A convenience method for setting the unscaledHeight of a 
     *  component. 
     * 
     *  Setting this sets the height of the component as desired
     *  before any transformation is applied. 
     */
    mx_internal function setUnscaledHeight(value:Number):void
    {
        var scaledValue:Number = value * Math.abs(oldScaleY);
        if (_explicitHeight == scaledValue)
            return;

        // height can be pixel or percent, not both
        if (!isNaN(scaledValue))
            _percentHeight = NaN;
            
        _explicitHeight = scaledValue;

        // We invalidate size because locking in height
        // may change the measured width in flow-based components.
        invalidateSize();

        var p:IInvalidating = parent as IInvalidating;
        if (p && includeInLayout)
        {
            p.invalidateSize();
            p.invalidateDisplayList();
        }
    }

    /**
     *  Measures the specified text, assuming that it is displayed
     *  in a single-line UITextField using a UITextFormat
     *  determined by the styles of this UIComponent.
     *
     *  @param text A String specifying the text to measure.
     *
     *  @return A TextLineMetrics object containing the text measurements.
     */
    public function measureText(text:String):TextLineMetrics
    {
        return determineTextFormatFromStyles().measureText(text);
    }

    /**
     *  Measures the specified HTML text, which may contain HTML tags such
     *  as <code>&lt;font&gt;</code> and <code>&lt;b&gt;</code>, 
     *  assuming that it is displayed
     *  in a single-line UITextField using a UITextFormat
     *  determined by the styles of this UIComponent.
     *
     *  @param text A String specifying the HTML text to measure.
     *
     *  @return A TextLineMetrics object containing the text measurements.
     */
    public function measureHTMLText(htmlText:String):TextLineMetrics
    {
        return determineTextFormatFromStyles().measureHTMLText(htmlText);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Drawing and Child Layout
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var lastUnscaledWidth:Number;
    /**
     *  @private
     */
    private var lastUnscaledHeight:Number;

    /**
     *  @inheritDoc
     */
    public function validateDisplayList():void
    {
        if (invalidateDisplayListFlag)
        {
            // Check if our parent is the top level system manager
            var sm:ISystemManager = parent as ISystemManager;
            if (sm)
            {
                if (sm == systemManager.topLevelSystemManager &&
                    sm.document != this)
                {
                    // Size ourself to the new measured width/height
                    setActualSize(getExplicitOrMeasuredWidth(),
                                  getExplicitOrMeasuredHeight());
                }
            }

            var unscaledWidth:Number = scaleX == 0 ? 0 : width / scaleX;
            var unscaledHeight:Number = scaleY == 0 ? 0 : height / scaleY;
            // Use some hysteresis to prevent roundoff errors from
            // causing problems as we scale. This isn't a full solution,
            // but it helps.
            if (Math.abs(unscaledWidth - lastUnscaledWidth) < .00001)
                unscaledWidth = lastUnscaledWidth;
            if (Math.abs(unscaledHeight - lastUnscaledHeight) < .00001)
                unscaledHeight = lastUnscaledHeight;
            updateDisplayList(unscaledWidth,unscaledHeight);
            lastUnscaledWidth = unscaledWidth;
            lastUnscaledHeight = unscaledHeight;
            
            invalidateDisplayListFlag = false;
        }
    }

    /**
     *  Draws the object and/or sizes and positions its children.
     *  This is an advanced method that you might override
     *  when creating a subclass of UIComponent.
     *
     *  <p>You do not call this method directly. Flex calls the
     *  <code>updateDisplayList()</code> method when the component is added to a container
     *  using the <code>addChild()</code> method, and when the component's
     *  <code>invalidateDisplayList()</code> method is called. </p>
     *
     *  <p>If the component has no children, this method
     *  is where you would do programmatic drawing
     *  using methods on the component's Graphics object
     *  such as <code>graphics.drawRect()</code>.</p>
     *
     *  <p>If the component has children, this method is where
     *  you would call the <code>move()</code> and <code>setActualSize()</code>
     *  methods on its children.</p>
     *
     *  <p>Components may do programmatic drawing even if
     *  they have children. In doing either, you should use the
     *  component's <code>unscaledWidth</code> and <code>unscaledHeight</code>
     *  as its bounds.</p>
     *
     *  <p>It is important to use <code>unscaledWidth</code> and
     *  <code>unscaledHeight</code> instead of the <code>width</code>
     *  and <code>height</code> properties.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     */
    protected function updateDisplayList(unscaledWidth:Number,
                                        unscaledHeight:Number):void
    {
    }

    /**
     *  Returns a layout constraint value, which is the same as 
     *  getting the constraint style for this component.
     *
     *  @param constraintName The name of the constraint style, which
     *  can be any of the following: left, right, top, bottom,
     *  verticalCenter, horizontalCenter, baseline
     *
     *  @return Returns the layout constraint value, which can be
     *  specified in either of two forms. It can be specified as a
     *  numeric string, for example, "10" or it can be specified as
     *  identifier:numeric string. For identifier:numeric string,
     *  identifier is the <code>id</code> of a ConstraintRow or
     *  ConstraintColumn. For example, a value of "cc1:10" specifies a
     *  value of 10 for the ConstraintColumn that has the
     *  <code>id</code> "cc1."
     */
    public function getConstraintValue(constraintName:String):*
    {
        return getStyle(constraintName);
    }

    /**
     *  Sets a layout constraint value, which is the same as 
     *  setting the constraint style for this component.
     *
     *  @param constraintName The name of the constraint style, which
     *  can be any of the following: left, right, top, bottom,
     *  verticalCenter, horizontalCenter, baseline
     *  
     *  @value The value of the constraint can be specified in either
     *  of two forms. It can be specified as a numeric string, for
     *  example, "10" or it can be specified as identifier:numeric
     *  string. For identifier:numeric string, identifier is the
     *  <code>id</code> of a ConstraintRow or ConstraintColumn. For
     *  example, a value of "cc1:10" specifies a value of 10 for the
     *  ConstraintColumn that has the <code>id</code> "cc1."
     * 
     */
    public function setConstraintValue(constraintName:String, value:*):void
    {
        setStyle(constraintName, value);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods: Drawing
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns a box Matrix which can be passed to the 
     *  <code>drawRoundRect()</code> method
     *  as the <code>rot</code> parameter when drawing a horizontal gradient.
     *
     *  <p>For performance reasons, the Matrix is stored in a static variable
     *  which is reused by all calls to <code>horizontalGradientMatrix()</code>
     *  and <code>verticalGradientMatrix()</code>.
     *  Therefore, you should pass the resulting Matrix
     *  to <code>drawRoundRect()</code> before calling
     *  <code>horizontalGradientMatrix()</code>
     *  or <code>verticalGradientMatrix()</code> again.</p>
     *
     *  @param x The left coordinate of the gradient, in pixels.
     *
     *  @param y The top coordinate of the gradient, in pixels.
     *
     *  @param width The width of the gradient, in pixels.
     *
     *  @param height The height of the gradient, in pixels.
     *
     *  @return The Matrix for the horizontal gradient.
     */
    public function horizontalGradientMatrix(x:Number, y:Number,
                                             width:Number,
                                             height:Number):Matrix
    {
        UIComponentGlobals.tempMatrix.createGradientBox(width, height, 0, x, y);
        return UIComponentGlobals.tempMatrix;
    }

    /**
     *  Returns a box Matrix which can be passed to <code>drawRoundRect()</code>
     *  as the <code>rot</code> parameter when drawing a vertical gradient.
     *
     *  <p>For performance reasons, the Matrix is stored in a static variable
     *  which is reused by all calls to <code>horizontalGradientMatrix()</code>
     *  and <code>verticalGradientMatrix()</code>.
     *  Therefore, you should pass the resulting Matrix
     *  to <code>drawRoundRect()</code> before calling
     *  <code>horizontalGradientMatrix()</code>
     *  or <code>verticalGradientMatrix()</code> again.</p>
     *
     *  @param x The left coordinate of the gradient, in pixels.
     *
     *  @param y The top coordinate of the gradient, in pixels.
     *
     *  @param width The width of the gradient, in pixels.
     *
     *  @param height The height of the gradient, in pixels.
     *
     *  @return The Matrix for the vertical gradient.
     */
    public function verticalGradientMatrix(x:Number, y:Number,
                                           width:Number,
                                           height:Number):Matrix
    {
        UIComponentGlobals.tempMatrix.createGradientBox(width, height, Math.PI / 2, x, y);
        return UIComponentGlobals.tempMatrix;
    }

    /**
     *  @copy mx.skins.ProgrammaticSkin#drawRoundRect()
     */
    public function drawRoundRect(x:Number, y:Number, w:Number, h:Number,
                                  r:Object = null, c:Object = null,
                                  alpha:Object = null, rot:Object = null,
                                  gradient:String = null, ratios:Array = null,
                                  hole:Object = null):void
    {
        var g:Graphics = graphics;

        // Quick exit if w or h is zero. This happens when scaling a component
        // to a very small value, which then gets rounded to 0.
        if (!w || !h)
            return;

        // If color is an object then allow for complex fills.
        if (c !== null)
        {
            if (c is Array)
            {
                var alphas:Array;

                if (alpha is Array)
                    alphas = alpha as Array;
                else
                    alphas = [ alpha, alpha ];

                if (!ratios)
                    ratios = [ 0, 0xFF ];

                var matrix:Matrix = null;

                if (rot)
                {
                    if (rot is Matrix)
                    {
                        matrix = Matrix(rot);
                    }
                    else
                    {
                        matrix = new Matrix();

                        if (rot is Number)
                        {
                            matrix.createGradientBox(
                                w, h, Number(rot) * Math.PI / 180, x, y);
                        }
                        else
                        {
                            matrix.createGradientBox(
                                rot.w, rot.h, rot.r, rot.x, rot.y);
                        }
                    }
                }

                if (gradient == GradientType.RADIAL)
                {
                    g.beginGradientFill(GradientType.RADIAL,
                                        c as Array, alphas, ratios, matrix);
                }
                else
                {
                    g.beginGradientFill(GradientType.LINEAR,
                                        c as Array, alphas, ratios, matrix);
                }
            }
            else
            {
                g.beginFill(Number(c), Number(alpha));
            }
        }

        var ellipseSize:Number;

        // Stroke the rectangle.
        if (!r)
        {
            g.drawRect(x, y, w, h);
        }
        else if (r is Number)
        {
            ellipseSize = Number(r) * 2;
            g.drawRoundRect(x, y, w, h, ellipseSize, ellipseSize);
        }
        else
        {
            GraphicsUtil.drawRoundRectComplex(g, x, y, w, h, r.tl, r.tr, r.bl, r.br);
        }

        // Carve a rectangular hole out of the middle of the rounded rect.
        if (hole)
        {
            var holeR:Object = hole.r;
            if (holeR is Number)
            {
                ellipseSize = Number(holeR) * 2;
                g.drawRoundRect(hole.x, hole.y, hole.w, hole.h, 
                                ellipseSize, ellipseSize);
            }
            else
            {
                GraphicsUtil.drawRoundRectComplex(g, hole.x, hole.y, hole.w, hole.h,
                                       holeR.tl, holeR.tr, holeR.bl, holeR.br);
            }
        }

        if (c !== null)
            g.endFill();
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Moving and sizing
    //
    //--------------------------------------------------------------------------

     /**
      *  Moves the component to a specified position within its parent.
      *  Calling this method is exactly the same as
      *  setting the component's <code>x</code> and <code>y</code> properties.
      *
      *  <p>If you are overriding the <code>updateDisplayList()</code> method 
      *  in a custom component, you should call the <code>move()</code> method 
      *  rather than setting the <code>x</code> and <code>y</code> properties. 
      *  The difference is that the <code>move()</code> method changes the location 
      *  of the component and then dispatches a <code>move</code> event when you 
      *  call the method, while setting the <code>x</code> and <code>y</code> 
      *  properties changes the location of the component and dispatches 
      *  the event on the next screen refresh.</p>
      *
      *  @param x Left position of the component within its parent.
      *
      *  @param y Top position of the component within its parent.
      */
    public function move(x:Number, y:Number):void
    {
        var changed:Boolean = false;

        if (x != super.x)
        {
            super.x = x;
            dispatchEvent(new Event("xChanged"));
            changed = true;
        }

        if (y != super.y)
        {
            super.y = y;
            dispatchEvent(new Event("yChanged"));
            changed = true;
        }

        if (changed)
            dispatchMoveEvent();
    }

    /**
     *  Sizes the object.
     *  Unlike directly setting the <code>width</code> and <code>height</code>
     *  properties, calling the <code>setActualSize()</code> method
     *  does not set the <code>explictWidth</code> and
     *  <code>explicitHeight</code> properties, so a future layout
     *  calculation may result in the object returning to its previous size.
     *  This method is used primarily by component developers implementing
     *  the <code>updateDisplayList()</code> method, by Effects,
     *  and by the LayoutManager.
     *
     *  @param w Width of the object.
     *
     *  @param h Height of the object.
     */
    public function setActualSize(w:Number, h:Number):void
    {
        var changed:Boolean = false;

        if (_width != w)
        {
            _width = w;
            dispatchEvent(new Event("widthChanged"));
            changed = true;
        }

        if (_height != h)
        {
            _height = h;
            dispatchEvent(new Event("heightChanged"));
            changed = true;
        }

        if (changed)
        {
            invalidateDisplayList();
            dispatchResizeEvent();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Content coordinate transformations
    //
    //--------------------------------------------------------------------------

    /**
     *  Converts a <code>Point</code> object from content coordinates to global coordinates.
     *  Content coordinates specify a pixel position relative to the upper left corner
     *  of the component's content, and include all of the component's content area, 
     *  including any regions that are currently clipped and must be 
     *  accessed by scrolling the component.
     *  You use the content coordinate system to set and get the positions of children 
     *  of a container that uses absolute positioning. 
     *  Global coordinates specify a pixel position relative to the upper-left corner 
     *  of the stage, that is, the outermost edge of the application.
     *  
     *  @param point A Point object that 
     *  specifies the <i>x</i> and <i>y</i> coordinates in the content coordinate system 
     *  as properties.
     *  
     *  @return A Point object with coordinates relative to the Stage. 
     * 
     *  @see #globalToContent()
     */
    public function contentToGlobal(point:Point):Point
    {
        return localToGlobal(point);
    }
    
    /**
     *  Converts a <code>Point</code> object from global to content coordinates.
     *  Global coordinates specify a pixel position relative to the upper-left corner 
     *  of the stage, that is, the outermost edge of the application.
     *  Content coordinates specify a pixel position relative to the upper left corner
     *  of the component's content, and include all of the component's content area, 
     *  including any regions that are currently clipped and must be 
     *  accessed by scrolling the component.
     *  You use the content coordinate system to set and get the positions of children 
     *  of a container that uses absolute positioning. 
     *  
     *  @param point A Point object that 
     *  specifies the <i>x</i> and <i>y</i> coordinates in the global (Stage) 
     *  coordinate system as properties.
     *  
     *  @return Point A Point object with coordinates relative to the component.
     * 
     *  @see #contentToGlobal()
     */
    public function globalToContent(point:Point):Point
    {
        return globalToLocal(point);
    }

    /**
     *  Converts a <code>Point</code> object from content to local coordinates.
     *  Content coordinates specify a pixel position relative to the upper left corner
     *  of the component's content, and include all of the component's content area, 
     *  including any regions that are currently clipped and must be 
     *  accessed by scrolling the component.
     *  You use the content coordinate system to set and get the positions of children 
     *  of a container that uses absolute positioning. 
     *  Local coordinates specify a pixel position relative to the 
     *  upper left corner of the component.
     *  
     *  @param point A Point object that specifies the <i>x</i> and <i>y</i> 
     *  coordinates in the content coordinate system as properties.
     *  
     *  @return Point A Point object with coordinates relative to the 
     *  local coordinate system. 
     * 
     *  @see #contentToGlobal()
     */
    public function contentToLocal(point:Point):Point
    {
        return point;
    }
    
    /**
     *  Converts a <code>Point</code> object from local to content coordinates.
     *  Local coordinates specify a pixel position relative to the 
     *  upper left corner of the component.
     *  Content coordinates specify a pixel position relative to the upper left corner
     *  of the component's content, and include all of the component's content area, 
     *  including any regions that are currently clipped and must be 
     *  accessed by scrolling the component.
     *  You use the content coordinate system to set and get the positions of children 
     *  of a container that uses absolute positioning. 
     *  
     *  @param point A Point object that specifies the <i>x</i> and <i>y</i> 
     *  coordinates in the local coordinate system as properties.
     *  
     *  @return Point A Point object with coordinates relative to the 
     *  content coordinate system. 
     * 
     *  @see #contentToLocal()
     */
    public function localToContent(point:Point):Point
    {
        return point;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Focus
    //
    //--------------------------------------------------------------------------

    /**
     *  Gets the object that currently has focus.
     *  It might not be this object.
     *  Note that this method does not necessarily return the component
     *  that has focus.
     *  It may return the internal subcomponent of the component
     *  that has focus.
     *  To get the component that has focus, use the
     *  <code>focusManager.focus</code> property.
     *
     *  @return Object that has focus.
     */
    public function getFocus():InteractiveObject
    {
        var sm:ISystemManager = systemManager;
        if (!sm)
            return null;

        if (UIComponentGlobals.nextFocusObject)
            return UIComponentGlobals.nextFocusObject;

        return sm.stage.focus;
    }

    /**
     *  Sets the focus to this component.
     *  The component may in turn pass focus to a subcomponent.
     *
     *  <p><b>Note:</b> Only the TextInput and TextArea controls show a highlight
     *  when this method sets the focus.
     *  All controls show a highlight when the user tabs to the control.</p>
     */
    public function setFocus():void
    {
        var sm:ISystemManager = systemManager;
        if (sm && sm.stage)
        {
            if (UIComponentGlobals.callLaterDispatcherCount == 0)
            {
                sm.stage.focus = this;
                UIComponentGlobals.nextFocusObject = null;
            }
            else
            {
                UIComponentGlobals.nextFocusObject = this;
                sm.stage.addEventListener(Event.ENTER_FRAME, setFocusLater);
            }
        }
        else
        {
            UIComponentGlobals.nextFocusObject = this;
            callLater(setFocusLater);
        }
    }

    /**
     *  @private
     *  Returns the focus object
     */
    mx_internal function getFocusObject():DisplayObject
    {
        var fm:IFocusManager = focusManager;
        
        if (!fm || !fm.focusPane)
            return null;
        
        return fm.focusPane.numChildren == 0 ?
               null :
               fm.focusPane.getChildAt(0);
    }

    /**
     *  Shows or hides the focus indicator around this component.
     *
     *  <p>UIComponent implements this by creating an instance of the class
     *  specified by the <code>focusSkin</code> style and positioning it
     *  appropriately.</p>
     *  
     *  @param isFocused Determines if the focus indicator should be displayed. Set to
     *  <code>true</code> to display the focus indicator. Set to <code>false</code> to hide it.
     */
    public function drawFocus(isFocused:Boolean):void
    {
        // Gets called by removeChild() after un-parented.
        if (!parent)
            return;

        var focusObj:DisplayObject = getFocusObject();
        var focusPane:Sprite = focusManager ? focusManager.focusPane : null;

        if (isFocused && !preventDrawFocus) //&& !isEffectStarted
        {
            var focusOwner:DisplayObjectContainer = focusPane.parent;

            if (focusOwner != parent)
            {
                if (focusOwner)
                {
                    if (focusOwner is ISystemManager)
                        ISystemManager(focusOwner).focusPane = null;
                    else
                        IUIComponent(focusOwner).focusPane = null;
                }
                if (parent is ISystemManager)
                    ISystemManager(parent).focusPane = focusPane;
                else
                    IUIComponent(parent).focusPane = focusPane;
            }

            var focusClass:Class = getStyle("focusSkin");

            if (focusObj && !(focusObj is focusClass))
            {
                focusPane.removeChild(focusObj);
                focusObj = null;
            }

            if (!focusObj)
            {
                focusObj = new focusClass();

                focusObj.name = "focus";

                focusPane.addChild(focusObj);
            }

            if (focusObj is ILayoutManagerClient )
                ILayoutManagerClient (focusObj).nestLevel = nestLevel;

            if (focusObj is ISimpleStyleClient)
                ISimpleStyleClient(focusObj).styleName = this;

            addEventListener(MoveEvent.MOVE, focusObj_moveHandler, true);
            addEventListener(MoveEvent.MOVE, focusObj_moveHandler);
            addEventListener(ResizeEvent.RESIZE, focusObj_resizeHandler, true);
            addEventListener(ResizeEvent.RESIZE, focusObj_resizeHandler);
            addEventListener(Event.REMOVED, focusObj_removedHandler, true);

            focusObj.visible = true;
            hasFocusRect = true;

            adjustFocusRect();
        }
        else if (hasFocusRect)
        {
            hasFocusRect = false;

            if (focusObj)
                focusObj.visible = false;

            removeEventListener(MoveEvent.MOVE, focusObj_moveHandler);
            removeEventListener(MoveEvent.MOVE, focusObj_moveHandler, true);
            removeEventListener(ResizeEvent.RESIZE, focusObj_resizeHandler, true);
            removeEventListener(ResizeEvent.RESIZE, focusObj_resizeHandler);
            removeEventListener(Event.REMOVED, focusObj_removedHandler, true);
        }
    }

    /**
     *  Adjust the focus rectangle.
     *
     *  @param The component whose focus rectangle to modify. 
     *  If omitted, the default value is this UIComponent object.
     */
    protected function adjustFocusRect(obj:DisplayObject = null):void
    {
        if (!obj)
            obj = this;
        
        // Something inside the lisder has a width and height of NaN
        if (isNaN(obj.width) || isNaN(obj.height))
            return;

        var fm:IFocusManager = focusManager;
        if (!fm)
            return; // we've been unparented so ignore

        var focusObj:IFlexDisplayObject = IFlexDisplayObject(getFocusObject());
        if (focusObj)
        {
            var rectCol:Number;
            if (errorString && errorString != "")
                rectCol = getStyle("errorColor");
            else
                rectCol = getStyle("themeColor");

            var thickness:Number = getStyle("focusThickness");

            if (focusObj is IStyleClient)
            {
                IStyleClient(focusObj).setStyle("focusColor", rectCol);
            }
            //if (getStyle("focusColor") != rectCol)
            //  setStyle("focusColor", rectCol);

            focusObj.setActualSize(obj.width + 2 * thickness,
                             obj.height + 2 * thickness);

            var pt:Point;

            if (rotation)
            {
                var rotRad:Number = rotation * Math.PI / 180;
                pt = new Point(obj.x - thickness * (Math.cos(rotRad) - Math.sin(rotRad)),
                               obj.y - thickness * (Math.cos(rotRad) + Math.sin(rotRad)));
                DisplayObject(focusObj).rotation = rotation;
            }
            else
            {
                pt = new Point(obj.x - thickness, obj.y - thickness);
            }

            if (obj.parent == this)
            {
                // This adjustment only works if obj is a direct child of this.
                pt.x += x;
                pt.y += y;
            }
            pt = parent.localToGlobal(pt);
            pt = parent.globalToLocal(pt);
            focusObj.move(pt.x, pt.y);

            if (focusObj is IInvalidating)
                IInvalidating(focusObj).validateNow();

            else if (focusObj is IProgrammaticSkin)
                IProgrammaticSkin(focusObj).validateNow();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Events
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function dispatchMoveEvent():void
    {

        var moveEvent:MoveEvent = new MoveEvent(MoveEvent.MOVE);
        moveEvent.oldX = oldX;
        moveEvent.oldY = oldY;
        dispatchEvent(moveEvent);

        oldX = x;
        oldY = y;
    }

    /**
     *  @private
     */
    private function dispatchResizeEvent():void
    {
        var resizeEvent:ResizeEvent = new ResizeEvent(ResizeEvent.RESIZE);
        resizeEvent.oldWidth = oldWidth;
        resizeEvent.oldHeight = oldHeight;
        dispatchEvent(resizeEvent);

        oldWidth = width;
        oldHeight = height;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: States
    //
    //--------------------------------------------------------------------------

    /**
     *  Set the current state. 
     *  
     *  @param stateName The name of the new view state.
     *  
     *  @param playTransition If <code>true</code>, play 
     *  the appropriate transition when the view state changes.
     *
     *  @see #currentState
     */
    public function setCurrentState(stateName:String,
                                    playTransition:Boolean = true):void
    {
        // Only change if the requested state is different. Since the root
        // state can be either null or "", we need to add additional check
        // to make sure we're not going from null to "" or vice-versa.
        if (stateName != currentState &&
            !(isBaseState(stateName) && isBaseState(currentState)))
        {
            requestedCurrentState = stateName;
            playStateTransition = playTransition;

            if (initialized)
            {
                commitCurrentState();
            }
            else
            {
                _currentStateChanged = true;
                
                // We need to wait until we're fully initialized before commiting
                // the current state. Otherwise children may not be created
                // (if we're inside a deferred instantiation container), or
                // bindings may not be fired yet.
                addEventListener(FlexEvent.CREATION_COMPLETE, 
                                 creationCompleteHandler);
            }
        }
    }

    /**
     *  @private
     *  Returns true if the passed in state name is the 'base' state, which
     *  is currently defined as null or ""
     */
    private function isBaseState(stateName:String):Boolean
    {
        return !stateName || stateName == "";
    }

    /**
     *  @private
     *  Commit a pending current state change.
     */
    private function commitCurrentState():void
    {
        var transition:IEffect =
            playStateTransition ?
            getTransition(_currentState, requestedCurrentState) :
            null;
        var commonBaseState:String = findCommonBaseState(_currentState, requestedCurrentState);
        var event:StateChangeEvent;
        var oldState:String = _currentState ? _currentState : "";
        var destination:State = getState(requestedCurrentState);

        // Stop any transition that may still be playing
        if (_currentTransitionEffect)
            _currentTransitionEffect.end();

        // Initialize the state we are going to.
        initializeState(requestedCurrentState);

        // Capture transition start values
        if (transition)
            transition.captureStartValues();

        // Dispatch currentStateChanging event
        event = new StateChangeEvent(StateChangeEvent.CURRENT_STATE_CHANGING);
        event.oldState = oldState;
        event.newState = requestedCurrentState ? requestedCurrentState : "";
        dispatchEvent(event);

        // If we're leaving the base state, send an exitState event
        if (isBaseState(_currentState))
            dispatchEvent(new FlexEvent(FlexEvent.EXIT_STATE));

        // Remove the existing state
        removeState(_currentState, commonBaseState);
        _currentState = requestedCurrentState;

        // If we're going back to the base state, dispatch an
        // enter state event, otherwise apply the state.
        if (isBaseState(currentState))
            dispatchEvent(new FlexEvent(FlexEvent.ENTER_STATE));
        else
            applyState(_currentState, commonBaseState);

        // Dispatch currentStateChange
        event = new StateChangeEvent(StateChangeEvent.CURRENT_STATE_CHANGE);
        event.oldState = oldState;
        event.newState = _currentState ? _currentState : "";
        dispatchEvent(event);

        if (transition)
        {
            // Force a validation before playing the transition effect
            UIComponentGlobals.layoutManager.validateNow();
            _currentTransitionEffect = transition;
            transition.addEventListener(EffectEvent.EFFECT_END, transition_effectEndHandler);
            transition.play();
        }
    }

    /**
     *  @private
     */
    private function transition_effectEndHandler(event:EffectEvent):void
    {
        _currentTransitionEffect = null;
    }

    /**
     *  @private
     *  Returns the state with the specified name, or null if it doesn't exist.
     *  If multiple states have the same name the first one will be returned.
     */
    private function getState(stateName:String):State
    {
        if (!states || isBaseState(stateName))
            return null;

        // Do a simple linear search for now. This can
        // be optimized later if needed.
        for (var i:int = 0; i < states.length; i++)
        {
            if (states[i].name == stateName)
                return states[i];
        }

        var message:String = resourceManager.getString(
            "core", "stateUndefined", [ stateName ]);
        throw new ArgumentError(message);

        return null;
    }

    /**
     *  @private
     *  Find the deepest common state between two states. For example:
     *
     *  State A
     *  State B basedOn A
     *  State C basedOn A
     *
     *  findCommonBaseState(B, C) returns A
     *
     *  If there are no common base states, the root state ("") is returned.
     */
    private function findCommonBaseState(state1:String, state2:String):String
    {
        var firstState:State = getState(state1);
        var secondState:State = getState(state2);

        // Quick exit if either state is the base state
        if (!firstState || !secondState)
            return "";

        // Quick exit if both states are not based on other states
        if (isBaseState(firstState.basedOn) && isBaseState(secondState.basedOn))
            return "";

        // Get the base states for each state and walk from the top
        // down until we find the deepest common base state.
        var firstBaseStates:Array = getBaseStates(firstState);
        var secondBaseStates:Array = getBaseStates(secondState);
        var commonBase:String = "";
        
        while (firstBaseStates[firstBaseStates.length - 1] ==
               secondBaseStates[secondBaseStates.length - 1])
        {
            commonBase = firstBaseStates.pop();
            secondBaseStates.pop();

            if (!firstBaseStates.length || !secondBaseStates.length)
                break;
        }

        // Finally, check to see if one of the states is directly based on the other.
        if (firstBaseStates.length && 
            firstBaseStates[firstBaseStates.length - 1] == secondState.name)
        {
            commonBase = secondState.name;
        }
        else if (secondBaseStates.length && 
                 secondBaseStates[secondBaseStates.length - 1] == firstState.name)
        {
            commonBase = firstState.name;
        }
        
        return commonBase;
    }

    /**
     *  @private
     *  Returns the base states for a given state.
     *  This Array is in high-to-low order - the first entry
     *  is the immediate basedOn state, the last entry is the topmost
     *  basedOn state.
     */
    private function getBaseStates(state:State):Array
    {
        var baseStates:Array = [];
        
        // Push each basedOn name
        while (state && state.basedOn)
        {
            baseStates.push(state.basedOn);
            state = getState(state.basedOn);
        }

        return baseStates;
    }

    /**
     *  @private
     *  Remove the overrides applied by a state, and any
     *  states it is based on.
     */
    private function removeState(stateName:String, lastState:String):void
    {
        var state:State = getState(stateName);

        if (stateName == lastState)
            return;
            
        // Remove existing state overrides.
        // This must be done in reverse order
        if (state)
        {
            // Dispatch the "exitState" event
            state.dispatchExitState();

            var overrides:Array = state.overrides;

            for (var i:int = overrides.length; i; i--)
                overrides[i-1].remove(this);

            // Remove any basedOn deltas last
            if (state.basedOn != lastState)
                removeState(state.basedOn, lastState);
        }
    }

    /**
     *  @private
     *  Apply the overrides from a state, and any states it
     *  is based on.
     */
    private function applyState(stateName:String, lastState:String):void
    {
        var state:State = getState(stateName);

        if (stateName == lastState)
            return;
            
        if (state)
        {
            // Apply "basedOn" overrides first
            if (state.basedOn != lastState)
                applyState(state.basedOn, lastState);

            // Apply new state overrides
            var overrides:Array = state.overrides;

            for (var i:int = 0; i < overrides.length; i++)
                overrides[i].apply(this);

            // Dispatch the "enterState" event
            state.dispatchEnterState();
        }
    }

    /**
     *  @private
     *  Initialize the state, and any states it is based on
     */
    private function initializeState(stateName:String):void
    {
        var state:State = getState(stateName);
        
        while (state)
        {
            state.initialize();
            state = getState(state.basedOn);
        }
    }
    
    /**
     *  @private
     *  Find the appropriate transition to play between two states.
     */
    private function getTransition(oldState:String, newState:String):IEffect
    {
        var result:IEffect = null;      // Current candidate
        var priority:int = 0;           // Priority     fromState   toState
                                        //    1             *           *
                                        //    2           match         *
                                        //    3             *         match
                                        //    4           match       match

        if (!transitions)
            return null;

        if (!oldState)
            oldState = "";

        if (!newState)
            newState = "";

        for (var i:int = 0; i < transitions.length; i++)
        {
            var t:Transition = transitions[i];

            if (t.fromState == "*" && t.toState == "*" && priority < 1)
            {
                result = t.effect;
                priority = 1;
            }
            else if (t.fromState == oldState && t.toState == "*" && priority < 2)
            {
                result = t.effect;
                priority = 2;
            }
            else if (t.fromState == "*" && t.toState == newState && priority < 3)
            {
                result = t.effect;
                priority = 3;
            }
            else if (t.fromState == oldState && t.toState == newState && priority < 4)
            {
                result = t.effect;
                priority = 4;

                // Can't get any higher than this, let's go.
                break;
            }
        }

        return result;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Styling
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Sets up the inheritingStyles and nonInheritingStyles objects
     *  and their proto chains so that getStyle() can work.
     */
    //  Note that initProtoChain is 99% copied into DataGridItemRenderer
    mx_internal function initProtoChain():void
    {
        //trace("initProtoChain", name);

        var classSelector:CSSStyleDeclaration;

        if (styleName)
        {
            if (styleName is CSSStyleDeclaration)
            {
                // Get the style sheet referenced by the styleName property
                classSelector = CSSStyleDeclaration(styleName);
            }
            else if (styleName is IFlexDisplayObject || styleName is IStyleClient)
            {
                // If the styleName property is a UIComponent, then there's a
                // special search path for that case.
                StyleProtoChain.initProtoChainForUIComponentStyleName(this);
                return;
            }
            else if (styleName is String)
            {
                // Get the style sheet referenced by the styleName property
                classSelector =
                    StyleManager.getStyleDeclaration("." + styleName);
            }
        }

        // To build the proto chain, we start at the end and work forward.
        // Referring to the list at the top of this function, we'll start by
        // getting the tail of the proto chain, which is:
        //  - for non-inheriting styles, the global style sheet
        //  - for inheriting styles, my parent's style object
        var nonInheritChain:Object = StyleManager.stylesRoot;
        
        if (nonInheritChain && nonInheritChain.effects)
            registerEffects(nonInheritChain.effects);
        
        var p:IStyleClient = parent as IStyleClient;
        if (p)
        {
            var inheritChain:Object = p.inheritingStyles;
            if (inheritChain == UIComponent.STYLE_UNINITIALIZED)
                inheritChain = nonInheritChain;
        }
        else
        {
            // Pop ups inheriting chain starts at Application instead of global.
            // This allows popups to grab styles like themeColor that are
            // set on Application.
            if (isPopUp)
            {
                if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0 && _owner && _owner is IStyleClient)
                    inheritChain = IStyleClient(_owner).inheritingStyles;
                else
                    inheritChain = ApplicationGlobals.application.inheritingStyles;
            }
            else
                inheritChain = StyleManager.stylesRoot;
        }

        // Working backwards up the list, the next element in the
        // search path is the type selector
        var typeSelectors:Array = getClassStyleDeclarations();
        var n:int = typeSelectors.length;
        for (var i:int = 0; i < n; i++)
        {
            var typeSelector:CSSStyleDeclaration = typeSelectors[i];
            inheritChain =
                typeSelector.addStyleToProtoChain(inheritChain, this);

            nonInheritChain =
                typeSelector.addStyleToProtoChain(nonInheritChain, this);
            
            if (typeSelector.effects)
                registerEffects(typeSelector.effects);
        }

        // Next is the class selector
        if (classSelector)
        {
            inheritChain =
                classSelector.addStyleToProtoChain(inheritChain, this);

            nonInheritChain =
                classSelector.addStyleToProtoChain(nonInheritChain, this);
            
            if (classSelector.effects)
                registerEffects(classSelector.effects);
        }

        // Finally, we'll add the in-line styles
        // to the head of the proto chain.
        inheritingStyles =
            _styleDeclaration ?
            _styleDeclaration.addStyleToProtoChain(inheritChain, this) :
            inheritChain;

        nonInheritingStyles =
            _styleDeclaration ?
            _styleDeclaration.addStyleToProtoChain(nonInheritChain, this) :
            nonInheritChain;
    }

    /**
     *  Finds the type selectors for this UIComponent instance.
     *  The algorithm walks up the superclass chain.
     *  For example, suppose that class MyButton extends Button.
     *  A MyButton instance will first look for a MyButton type selector
     *  then, it will look for a Button type selector.
     *  then, it will look for a UIComponent type selector.
     *  (The superclass chain is considered to stop at UIComponent, not Object.)
     *
     *  @return An Array of type selectors for this UIComponent instance.
     */
    public function getClassStyleDeclarations():Array
    {
        var myApplicationDomain:ApplicationDomain;

        var factory:IFlexModuleFactory = ModuleManager.getAssociatedFactory(this);
        if (factory != null)
        {
            myApplicationDomain = ApplicationDomain(factory.info()["currentDomain"]);
        }
        else
        {
            var myRoot:DisplayObject = SystemManager.getSWFRoot(this);
            if (!myRoot)
                return [];
            myApplicationDomain = myRoot.loaderInfo.applicationDomain;
        }

        var className:String = getQualifiedClassName(this)
        className = className.replace("::", ".");
        var cache:Array;
        cache = StyleManager.typeSelectorCache[className];
        if (cache)
            return cache;
        
        // trace("getClassStyleDeclaration", className);
        var decls:Array = [];
        var classNames:Array = [];
        var caches:Array = [];
        var declcache:Array = [];

        while (className != null &&
               className != "mx.core.UIComponent" &&
               className != "mx.core.UITextField")
        {
            var s:CSSStyleDeclaration;
            cache = StyleManager.typeSelectorCache[className];
            if (cache)
            {
                decls = decls.concat(cache);
                break;
            }

            s = StyleManager.getStyleDeclaration(className);
            
            if (s)
            {
                decls.unshift(s);
                // we found one so the next set define the selectors for this
                // found class and its ancestors.  Save the old list and start
                // a new list
                classNames.push(className);
                caches.push(classNames);
                declcache.push(decls);
                decls = [];
                classNames = [];
                // trace("   ", className);
                // break;
            }
            else
                classNames.push(className);

            try
            {
                className = getQualifiedSuperclassName(myApplicationDomain.getDefinition(className));
                className = className.replace("::", ".");
            }
            catch(e:ReferenceError)
            {
                className = null;
            }
        }

        caches.push(classNames);
        declcache.push(decls);
        decls = [];
        while (caches.length)
        {
            classNames = caches.pop();
            decls = decls.concat(declcache.pop());
            while (classNames.length)
            {
                StyleManager.typeSelectorCache[classNames.pop()] = decls;
            }
        }

        return decls;
    }


    /**
     *  Builds or rebuilds the CSS style cache for this component
     *  and, if the <code>recursive</code> parameter is <code>true</code>,
     *  for all descendants of this component as well.
     *
     *  <p>The Flex framework calls this method in the following
     *  situations:</p>
     *
     *  <ul>
     *    <li>When you add a UIComponent to a parent using the
     *    <code>addChild()</code> or <code>addChildAt()</code> methods.</li>
     *    <li>When you change the <code>styleName</code> property
     *    of a UIComponent.</li>
     *    <li>When you set a style in a CSS selector using the
     *    <code>setStyle()</code> method of CSSStyleDeclaration.</li>
     *  </ul>
     *
     *  <p>Building the style cache is a computation-intensive operation,
     *  so you should avoid changing <code>styleName</code> or
     *  setting selector styles unnecessarily.</p>
     *
     *  <p>This method is not called when you set an instance style
     *  by calling the <code>setStyle()</code> method of UIComponent.
     *  Setting an instance style is a relatively fast operation
     *  compared with setting a selector style.</p>
     *
     *  <p>You should not need to call or override this method.</p>
     *
     *  @param recursive Recursivly regenerates the style cache for 
     *  all children of this component.
     */
    public function regenerateStyleCache(recursive:Boolean):void
    {
        // Regenerate the proto chain for this object
        initProtoChain();

        var childList:IChildList =
            this is IRawChildrenContainer ?
            IRawChildrenContainer(this).rawChildren :
            IChildList(this);
        
        // Recursively call this method on each child.
        var n:int = childList.numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:DisplayObject = childList.getChildAt(i);

            if (child is IStyleClient)
            {
                // Does this object already have a proto chain? 
                // If not, there's no need to regenerate a new one.
                if (IStyleClient(child).inheritingStyles !=
                    UIComponent.STYLE_UNINITIALIZED)
                {
                    IStyleClient(child).regenerateStyleCache(recursive);
                }
            }
            else if (child is IUITextField)
            {
                // Does this object already have a proto chain? 
                // If not, there's no need to regenerate a new one.
                if (IUITextField(child).inheritingStyles)
                    StyleProtoChain.initTextField(IUITextField(child));
            }
        }
    }

    /**
     *  Gets a style property that has been set anywhere in this
     *  component's style lookup chain.
     *
     *  <p>This same method is used to get any kind of style property,
     *  so the value returned may be a Boolean, String, Number, int,
     *  uint (for an RGB color), Class (for a skin), or any kind of object.
     *  Therefore the return type is simply specified as ~~.</p>
     *
     *  <p>If you are getting a particular style property, you will
     *  know its type and will often want to store the result in a
     *  variable of that type.
     *  No casting from ~~ to that type is necessary.</p>
     *
     *  <p>
     *  <code>
     *  var backgroundColor:uint = getStyle("backgroundColor");
     *  </code>
     *  </p>
     *
     *  <p>If the style property has not been set anywhere in the
     *  style lookup chain, the value returned by <code>getStyle()</code>
     *  will be <code>undefined</code>.
     *  Note that <code>undefined</code> is a special value that is
     *  not the same as <code>false</code>, <code>""</code>,
     *  <code>NaN</code>, <code>0</code>, or <code>null</code>.
     *  No valid style value is ever <code>undefined</code>.
     *  You can use the static method
     *  <code>StyleManager.isValidStyleValue()</code>
     *  to test whether the value was set.</p>
     *
     *  @param styleProp Name of the style property.
     *
     *  @return Style value.
     */
    public function getStyle(styleProp:String):*
    {
        return StyleManager.inheritingStyles[styleProp] ?
               _inheritingStyles[styleProp] :
               _nonInheritingStyles[styleProp];
    }

    /**
     *  Sets a style property on this component instance.
     *
     *  <p>This may override a style that was set globally.</p>
     *
     *  <p>Calling the <code>setStyle()</code> method can result in decreased performance.
     *  Use it only when necessary.</p>
     *
     *  @param styleProp Name of the style property.
     *
     *  @param newValue New value for the style.
     */
    public function setStyle(styleProp:String, newValue:*):void
    {
        if (styleProp == "styleName")
        {
            // Let the setter handle this one, see UIComponent.
            styleName = newValue;

            // Short circuit, because styleName isn't really a style.
            return;
        }

        if (EffectManager.getEventForEffectTrigger(styleProp) != "")
            EffectManager.setStyle(styleProp,this);

        /*
        if (StyleManager.isEffectStyle(styleProp))
            EffectManager.setStyle(styleProp,this);
        */

        // If this UIComponent didn't previously have any inline styles,
        // then regenerate the UIComponent's proto chain (and the proto chains
        // of this UIComponent's descendants).
        var isInheritingStyle:Boolean =
            StyleManager.isInheritingStyle(styleProp);
        var isProtoChainInitialized:Boolean =
            inheritingStyles != UIComponent.STYLE_UNINITIALIZED;
        var valueChanged:Boolean = getStyle(styleProp) != newValue;
        
        if (!_styleDeclaration)
        {
            _styleDeclaration = new CSSStyleDeclaration();
           
            _styleDeclaration.mx_internal::setStyle(styleProp, newValue);

            // If inheritingStyles is undefined, then this object is being
            // initialized and we haven't yet generated the proto chain.  To
            // avoid redundant work, don't bother to create the proto chain here.
            if (isProtoChainInitialized)
                regenerateStyleCache(isInheritingStyle);
        }
        else
        {
            _styleDeclaration.mx_internal::setStyle(styleProp, newValue);
        }

        if (isProtoChainInitialized && valueChanged)
        {
            styleChanged(styleProp);
            notifyStyleChangeInChildren(styleProp, isInheritingStyle);
        }
    }

    /**
     *  Deletes a style property from this component instance.
     *
     *  <p>This does not necessarily cause the <code>getStyle()</code> method 
     *  to return <code>undefined</code>.</p>
     *
     *  @param styleProp The name of the style property.
     */
    public function clearStyle(styleProp:String):void
    {
        setStyle(styleProp, undefined);
    }

    /**
     *  Propagates style changes to the children.
     *  You typically never need to call this method.
     *
     *  @param styleProp String specifying the name of the style property.
     *
     *  @param recursive Recursivly notify all children of this component.
     */
    public function notifyStyleChangeInChildren(
                        styleProp:String, recursive:Boolean):void
    {
        cachedTextFormat = null;

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:ISimpleStyleClient = getChildAt(i) as ISimpleStyleClient;
            if (child)
            {
                child.styleChanged(styleProp);

                // Always recursively call this function because of my
                // descendants might have a styleName property that points
                // to this object.  The recursive flag is respected in
                // Container.notifyStyleChangeInChildren.
                if (child is IStyleClient)
                    IStyleClient(child).notifyStyleChangeInChildren(styleProp, recursive);
            }
        }
    }

    /**
     *  @private
     *  If this object has a themeColor style, which is not inherited,
     *  then set it inline.
     */
    mx_internal function initThemeColor():Boolean
    {
        var styleName:Object /* String or UIComponent */ = _styleName;

        var tc:Object;  // Can be number or string
        var rc:Number;
        var sc:Number;

        // First look for locally-declared styles
        if (_styleDeclaration)
        {
            tc = _styleDeclaration.getStyle("themeColor");
            rc = _styleDeclaration.getStyle("rollOverColor");
            sc = _styleDeclaration.getStyle("selectionColor");
        }

        // Next look for class selectors
        if ((tc === null || !StyleManager.isValidStyleValue(tc)) && 
            (styleName && !(styleName is ISimpleStyleClient)))
        {
            var classSelector:Object =
                styleName is String ?
                StyleManager.getStyleDeclaration("." + styleName) :
                styleName;

            if (classSelector)
            {
                tc = classSelector.getStyle("themeColor");
                rc = classSelector.getStyle("rollOverColor");
                sc = classSelector.getStyle("selectionColor");
            }
        }

        // Finally look for type selectors
        if (tc === null || !StyleManager.isValidStyleValue(tc))
        {
            var typeSelectors:Array = getClassStyleDeclarations();
            
            for (var i:int = 0; i < typeSelectors.length; i++)
            {
                var typeSelector:CSSStyleDeclaration = typeSelectors[i];

                if (typeSelector)
                {
                    tc = typeSelector.getStyle("themeColor");
                    rc = typeSelector.getStyle("rollOverColor");
                    sc = typeSelector.getStyle("selectionColor");
                }
                
                if (tc !== null && StyleManager.isValidStyleValue(tc))
                    break;
            }
        }
        
        // If we have a themeColor but no rollOverColor or selectionColor, call
        // setThemeColor here which will calculate rollOver/selectionColor based
        // on the themeColor.
        if (tc !== null && StyleManager.isValidStyleValue(tc) && isNaN(rc) && isNaN(sc))
        {
            setThemeColor(tc);
            return true;
        }
        
        return (tc !== null && StyleManager.isValidStyleValue(tc)) && !isNaN(rc) && !isNaN(sc);
    }

    /**
     *  @private
     *  Calculate and set new roll over and selection colors based on theme color.
     */
    mx_internal function setThemeColor(value:Object /* Number or String */):void
    {
        var newValue:Number;

        if (newValue is String)
            newValue = parseInt(String(value));
        else
            newValue = Number(value);

        if (isNaN(newValue))
            newValue = StyleManager.getColorName(value);

        var newValueS:Number = ColorUtil.adjustBrightness2(newValue, 50);

        var newValueR:Number = ColorUtil.adjustBrightness2(newValue, 70);

        setStyle("selectionColor", newValueS);
        setStyle("rollOverColor", newValueR);
    }

    /**
     *  Returns a UITextFormat object corresponding to the text styles
     *  for this UIComponent.
     *
     *  @return UITextFormat object corresponding to the text styles
     *  for this UIComponent.
     */
    public function determineTextFormatFromStyles():UITextFormat
    {
        var textFormat:UITextFormat = cachedTextFormat;

        if (!textFormat)
        {
            var font:String =
                StringUtil.trimArrayElements(_inheritingStyles.fontFamily, ",");
            textFormat = new UITextFormat(getNonNullSystemManager(), font);
            textFormat.moduleFactory = moduleFactory;
            
            textFormat.align = _inheritingStyles.textAlign;
            textFormat.bold = _inheritingStyles.fontWeight == "bold";
            textFormat.color = enabled ?
                               _inheritingStyles.color :
                               _inheritingStyles.disabledColor;
            textFormat.font = font;
            textFormat.indent = _inheritingStyles.textIndent;
            textFormat.italic = _inheritingStyles.fontStyle == "italic";
            textFormat.kerning = _inheritingStyles.kerning;
            textFormat.leading = _nonInheritingStyles.leading;
            textFormat.leftMargin = _nonInheritingStyles.paddingLeft;
            textFormat.letterSpacing = _inheritingStyles.letterSpacing;
            textFormat.rightMargin = _nonInheritingStyles.paddingRight;
            textFormat.size = _inheritingStyles.fontSize;
            textFormat.underline =
                _nonInheritingStyles.textDecoration == "underline";

            textFormat.antiAliasType = _inheritingStyles.fontAntiAliasType;
            textFormat.gridFitType = _inheritingStyles.fontGridFitType;
            textFormat.sharpness = _inheritingStyles.fontSharpness;
            textFormat.thickness = _inheritingStyles.fontThickness;

            cachedTextFormat = textFormat;
        }

        return textFormat;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Binding
    //
    //--------------------------------------------------------------------------

    /**
     *  Executes the data bindings into this UIComponent object.
     *
     *  Workaround for MXML container/bindings problem (177074):
     *  override Container.executeBindings() to prefer descriptor.document over parentDocument in the
     *  call to BindingManager.executeBindings().
     *
     *  This should always provide the correct behavior for instances created by descriptor, and will
     *  provide the original behavior for procedurally-created instances. (The bug may or may not appear
     *  in the latter case.)
     *
     *  A more complete fix, guaranteeing correct behavior in both non-DI and reparented-component
     *  scenarios, is anticipated for updater 1.
     *
     *  @param recurse Recursively execute bindings for children of this component.
     */
    public function executeBindings(recurse:Boolean = false):void
    {
        var bindingsHost:Object = descriptor && descriptor.document ? descriptor.document : parentDocument;
        BindingManager.executeBindings(bindingsHost, id, this);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Effects
    //
    //--------------------------------------------------------------------------

    /**
     *  For each effect event, registers the EffectManager
     *  as one of the event listeners.
     *  You typically never need to call this method.
     *
     *  @param effects The names of the effect events.
     */
    public function registerEffects(effects:Array /* of String */):void
    {
        var n:int = effects.length;
        for (var i:int = 0; i < n; i++)
        {
            // Ask the EffectManager for the event associated with this effectTrigger
            var event:String = EffectManager.getEventForEffectTrigger(effects[i]);

            if (event != null && event != "")
            {
                addEventListener(event, EffectManager.eventHandler,
                                 false, EventPriority.EFFECT);
            }
        }
    }

    /**
     *  @private
     * 
     *  Adds an overlay object that's always on top of our children.
     *  Calls createOverlay(), which returns the overlay object.
     *  Currently used by the Dissolve and Resize effects.
     *
     *  Returns the overlay object.
     */
    mx_internal function addOverlay(color:uint,
                               targetArea:RoundedRectangle = null):void
    {
        if (!overlay)
        {
            overlayColor = color;
            overlay = new UIComponent();
            overlay.name = "overlay";
            // Have to set visibility immediately
            // to make sure we avoid flicker
            overlay.$visible = true;
            
            fillOverlay(overlay, color, targetArea);
            
            attachOverlay();
                    
            if (!targetArea)
                addEventListener(ResizeEvent.RESIZE, overlay_resizeHandler);

            overlay.x = 0;
            overlay.y = 0;

            invalidateDisplayList();

            overlayReferenceCount = 1;
        }
        else
        {
            overlayReferenceCount++;
        }

        dispatchEvent(new ChildExistenceChangedEvent(ChildExistenceChangedEvent.OVERLAY_CREATED, true, false, overlay));
    }
    
    /**
     *  This is an internal method used by the Flex framework
     *  to support the Dissolve effect.
     *  You should not need to call it or override it.
     */
    protected function attachOverlay():void
    {
        addChild(overlay);
    }

    /**
     *  @private
     *  Fill an overlay object which is always the topmost child.
     *  Used by the Dissolve effect.
     *  Never call this function directly.
     *  It is called internally by addOverlay().
     *
     *  The overlay object is filled with a solid rectangle that has the
     *  same width and height as the component.
     */
    mx_internal function fillOverlay(overlay:UIComponent, color:uint,
                                   targetArea:RoundedRectangle = null):void
    {
        if (!targetArea)
            targetArea = new RoundedRectangle(0, 0, unscaledWidth, unscaledHeight, 0);

        var g:Graphics = overlay.graphics;
        g.clear();
        g.beginFill(color);     
        
        g.drawRoundRect(targetArea.x, targetArea.y,
                        targetArea.width, targetArea.height,
                        targetArea.cornerRadius * 2,
                        targetArea.cornerRadius * 2);
        g.endFill();
    }

    /**
     *  @private  
     *  Removes the overlay object added by addOverlay().
     */
    mx_internal function removeOverlay():void
    {
        if (overlayReferenceCount > 0 && --overlayReferenceCount == 0 && overlay)
        {
            removeEventListener("resize", overlay_resizeHandler);

            if (super.getChildByName("overlay"))
                $removeChild(overlay);

            overlay = null;
        }
    }
    /**
     *  @private
     *  Resize the overlay when the components size changes
     *
     */
    private function overlay_resizeHandler(event:Event):void
    {
        fillOverlay(overlay, overlayColor, null);
    }
    
    /**
     *  @private
     */
    mx_internal var _effectsStarted:Array = [];
    
    /**
     *  @private
     */
    mx_internal var _affectedProperties:Object = {};
    
    /**
     *  Contains <code>true</code> if an effect is currently playing on the component.
     */
    private var _isEffectStarted:Boolean = false;
    mx_internal function get isEffectStarted():Boolean
    {
        return _isEffectStarted;
    }
    mx_internal function set isEffectStarted(value:Boolean):void
    {
        _isEffectStarted = value;
    }
    
    private var preventDrawFocus:Boolean = false;

    /**
     *  Called by the effect instance when it starts playing on the component.
     *  You can use this method to perform a modification to the component as part
     *  of an effect. You can use the <code>effectFinished()</code> method
     *  to restore the modification when the effect ends.
     *
     *  @param effectInst The effect instance object playing on the component.
     */
    public function effectStarted(effectInst:IEffectInstance):void
    {
        // Check that the instance isn't already in our list
        _effectsStarted.push(effectInst);

        var aProps:Array = effectInst.effect.getAffectedProperties();
        for (var j:int = 0; j < aProps.length; j++)
        {
            var propName:String = aProps[j];
            if (_affectedProperties[propName] == undefined)
            {
                _affectedProperties[propName] = [];
            }

            _affectedProperties[propName].push(effectInst);
        }

        isEffectStarted = true;
        // Hide the focus ring if the target already has one drawn
        if (effectInst.hideFocusRing)
        {
            preventDrawFocus = true;
            drawFocus(false);
        }
    }


    private var _endingEffectInstances:Array = [];

    /**
     *  Called by the effect instance when it stops playing on the component.
     *  You can use this method to restore a modification to the component made
     *  by the <code>effectStarted()</code> method when the effect started,
     *  or perform some other action when the effect ends.
     *
     *  @param effectInst The effect instance object playing on the component.
     */
    public function effectFinished(effectInst:IEffectInstance):void
    {   
        _endingEffectInstances.push(effectInst);
        invalidateProperties();

        // weak reference
        UIComponentGlobals.layoutManager.addEventListener(
            FlexEvent.UPDATE_COMPLETE, updateCompleteHandler, false, 0, true);
    }
    
    /**
     *  Ends all currently playing effects on the component.
     */
    public function endEffectsStarted():void
    {
        var len:int = _effectsStarted.length;
        for (var i:int = 0; i < len; i++)
        {
            _effectsStarted[i].end();
        }
    }

    /**
     *  @private
     */
    private function updateCompleteHandler(event:FlexEvent):void
    {
        UIComponentGlobals.layoutManager.removeEventListener(
            FlexEvent.UPDATE_COMPLETE, updateCompleteHandler);
        processEffectFinished(_endingEffectInstances);
        _endingEffectInstances = [];
    }

    /**
     *  @private
     */
    private function processEffectFinished(effectInsts:Array):void
    {
        // Find the instance in our list.
        for (var i:int = _effectsStarted.length - 1; i >= 0; i--)
        {
            for (var j:int = 0; j < effectInsts.length; j++)
            {
                var effectInst:IEffectInstance = effectInsts[j];
                if (effectInst == _effectsStarted[i])
                {
                    // Remove the effect from our array.
                    var removedInst:IEffectInstance = _effectsStarted[i];
                    _effectsStarted.splice(i, 1);
    
                    // Remove the affected properties from our internal object
                    var aProps:Array = removedInst.effect.getAffectedProperties();
                    for (var k:int = 0; k < aProps.length; k++)
                    {
                        var propName:String = aProps[k];
                        if (_affectedProperties[propName] != undefined)
                        {
                            for (var l:int = 0; l < _affectedProperties[propName].length; l++)
                            {
                                if (_affectedProperties[propName][l] == effectInst)
                                {
                                    _affectedProperties[propName].splice(l, 1);
                                    break;
                                }
                            }

                            if (_affectedProperties[propName].length == 0)
                                delete _affectedProperties[propName];
                        }
                    }
                    break;
                }
            }
        }

        isEffectStarted = _effectsStarted.length > 0 ? true : false;
        if (effectInst && effectInst.hideFocusRing)
        {
            preventDrawFocus = false;
        }
    }

    /**
     *  @private
     */
    mx_internal function getEffectsForProperty(propertyName:String):Array
    {
        return _affectedProperties[propertyName] != undefined ?
               _affectedProperties[propertyName] :
               [];
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Repeater
    //
    //--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    public function createReferenceOnParentDocument(
                        parentDocument:IFlexDisplayObject):void
    {
        if (id && id != "")
        {
            var indices:Array = _instanceIndices;
            if (!indices)
            {
                parentDocument[id] = this;
            }
            else
            {
                var r:Object = parentDocument[id];

                if (! (r is Array))
                {
                    r = parentDocument[id] = [];
                }

                var n:int = indices.length;
                for (var i:int = 0; i < n - 1; i++)
                {
                    var s:Object = r[indices[i]];

                    if (!(s is Array))
                        s = r[indices[i]] = [];

                    r = s;
                }

                r[indices[n - 1]] = this;
                var event:PropertyChangeEvent =
                    PropertyChangeEvent.createUpdateEvent(parentDocument,
                                                          id,
                                                          parentDocument[id],
                                                          parentDocument[id]);
                parentDocument.dispatchEvent(event);
            }
        }
    }

    /**
     *  @inheritDoc
     */
    public function deleteReferenceOnParentDocument(
                                parentDocument:IFlexDisplayObject):void
    {
        if (id && id != "")
        {
            var indices:Array = _instanceIndices;
            if (!indices)
            {
                parentDocument[id] = null;
            }
            else
            {
                var r:Object = parentDocument[id];

                if (!r)
                    return;

                var stack:Array = [];
                stack.push(r);

                var n:int = indices.length;
                for (var i:int = 0; i < n - 1; i++)
                {
                    var s:Object = r[indices[i]];

                    if (!s)
                        return;

                    r = s;
                    stack.push(r);
                }

                r.splice(indices[n - 1], 1);

                for (var j:int = stack.length - 1; j > 0; j--)
                {
                    if (stack[j].length == 0)
                        stack[j - 1].splice(indices[j], 1);
                }

                if ((stack.length > 0) && (stack[0].length == 0))
                {
                    parentDocument[id] = null;
                }
                else
                {
                    var event:PropertyChangeEvent =
                        PropertyChangeEvent.createUpdateEvent(parentDocument,
                                                              id,
                                                              parentDocument[id],
                                                              parentDocument[id]);
                    parentDocument.dispatchEvent(event);
                }
            }
        }
    }

    /**
     *  Returns the item in the <code>dataProvider</code> that was used
     *  by the specified Repeater to produce this Repeater, or
     *  <code>null</code> if this Repeater isn't repeated.
     *  The argument <code>whichRepeater</code> is 0 for the outermost
     *  Repeater, 1 for the next inner Repeater, and so on.
     *  If <code>whichRepeater</code> is not specified,
     *  the innermost Repeater is used.
     *
     *  @param whichRepeater Number of the Repeater,
     *  counting from the outermost one, starting at 0.
     *
     *  @return The requested repeater item.
     */
    public function getRepeaterItem(whichRepeater:int = -1):Object
    {
        var repeaterArray:Array = repeaters;

        // If whichRepeater isn't specified, assume the
        // innermost Repeater. This lets authors simply write
        //   myRepeater.getRepeaterItem()
        if (whichRepeater == -1)
            whichRepeater = repeaterArray.length - 1;

        return repeaterArray[whichRepeater].
               getItemAt(repeaterIndices[whichRepeater]);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Resources
    //
    //--------------------------------------------------------------------------

    /**
     *  This method is called when a UIComponent is constructed,
     *  and again whenever the ResourceManager dispatches
     *  a <code>"change"</code> Event to indicate
     *  that the localized resources have changed in some way.
     * 
     *  <p>This event will be dispatched when you set the ResourceManager's
     *  <code>localeChain</code> property, when a resource module
     *  has finished loading, and when you call the ResourceManager's
     *  <code>update()</code> method.</p>
     *
     *  <p>Subclasses should override this method and, after calling
     *  <code>super.resourcesChanged()</code>, do whatever is appropriate
     *  in response to having new resource values.</p>
     */
    protected function resourcesChanged():void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Methods: Printing
    //
    //--------------------------------------------------------------------------

    /**
     *  Prepares an IFlexDisplayObject for printing. 
     *  For the UIComponent class, the method performs no action. 
     *  Flex containers override the method to prepare for printing; 
     *  for example, by removing scroll bars from the printed output.
     *
     *  <p>This method is normally not used by application developers. </p>
     *
     *  @param target The component to be printed.
     *  It may be the current component or one of its children.
     *
     *  @return Object containing the properties of the current component 
     *  required by the <code>finishPrint()</code> method 
     *  to restore it to its previous state.
     *
     *  @see mx.printing.FlexPrintJob
     */
    public function prepareToPrint(target:IFlexDisplayObject):Object
    {
        return null;
    }

    /**
     *  Called after printing is complete. 
     *  For the UIComponent class, the method performs no action. 
     *  Flex containers override the method to restore the container after printing.
     *
     *  <p>This method is normally not used by application developers. </p>
     *
     *  @param obj Contains the properties of the component that 
     *  restore it to its state before printing.
     *
     *  @param target The component that just finished printing.
     *  It may be the current component or one of its children.
     *
     *  @see mx.printing.FlexPrintJob
     */
    public function finishPrint(obj:Object, target:IFlexDisplayObject):void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers: Invalidation
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Callback that then calls queued functions.
     */
    private function callLaterDispatcher(event:Event):void
    {
        // trace(">>calllaterdispatcher " + this);
        UIComponentGlobals.callLaterDispatcherCount++;

        // At run-time, callLaterDispatcher2() is called
        // without a surrounding try-catch.
        if (!UIComponentGlobals.catchCallLaterExceptions)
        {
            callLaterDispatcher2(event);
        }

        // At design-time, callLaterDispatcher2() is called
        // with a surrounding try-catch.
        else
        {
            try
            {
                callLaterDispatcher2(event);
            }
            catch(e:Error)
            {
                var callLaterErrorEvent:DynamicEvent = new DynamicEvent("callLaterError");
                callLaterErrorEvent.error = e;
                systemManager.dispatchEvent(callLaterErrorEvent);
            }
        }
        // trace("<<calllaterdispatcher");
        UIComponentGlobals.callLaterDispatcherCount--;
    }

    /**
     *  @private
     *  Callback that then calls queued functions.
     */
    private function callLaterDispatcher2(event:Event):void
    {
        if (UIComponentGlobals.callLaterSuspendCount > 0)
            return;

        // trace("  >>calllaterdispatcher2");
        var sm:ISystemManager = systemManager;
        if (sm && sm.stage && listeningForRender)
        {
            // trace("  removed");
            sm.stage.removeEventListener(Event.RENDER, callLaterDispatcher);
            sm.stage.removeEventListener(Event.ENTER_FRAME, callLaterDispatcher);
            listeningForRender = false;
        }

        // Move the method queue off to the side, so that subsequent
        // calls to callLater get added to a new queue that'll get handled
        // next time.
        var queue:Array = methodQueue;
        methodQueue = [];
        
        // Call each method currently in the method queue.
        // These methods can call callLater(), causing additional
        // methods to be queued, but these will get called the next
        // time around.
        var n:int = queue.length;
        //  trace("  queue length " + n);
        for (var i:int = 0; i < n; i++)
        {
            var mqe:MethodQueueElement = MethodQueueElement(queue[i]);

            mqe.method.apply(null, mqe.args);
        }

        // trace("  <<calllaterdispatcher2 " + this);
    }

    /**
     *  @private
     *  Event handler called when creation is complete and we have a pending
     *  current state change. We commit the current state change here instead
     *  of inside commitProperties since the state may have bindings to children
     *  that have not been created yet if we are inside a deferred instantiation
     *  container.
     */
    private function creationCompleteHandler(event:FlexEvent):void
    {
        if (_currentStateChanged)
        {
            _currentStateChanged = false;
            commitCurrentState();
            
            // Need to call validateNow() to avoid screen flicker. This handler
            // is called immediately before the component is displayed, and 
            // changing states takes a frame to commit the changes, which 
            // results in the base state flashing quickly on the screen before 
            // the desired state is entered.
            validateNow();
        }
        
        removeEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers: Keyboard
    //
    //--------------------------------------------------------------------------

    /**
     *  The event handler called for a <code>keyDown</code> event.
     *  If you override this method, make sure to call the base class version.
     *
     *  @param event The event object. 
     */
    protected function keyDownHandler(event:KeyboardEvent):void
    {
        // You must override this function if your component accepts focus
    }

    /**
     *  The event handler called for a <code>keyUp</code> event.
     *  If you override this method, make sure to call the base class version.
     *
     *  @param event The event object. 
     */
    protected function keyUpHandler(event:KeyboardEvent):void
    {
        // You must override this function if your component accepts focus
    }

    /**
     *  Typically overridden by components containing UITextField objects,
     *  where the UITextField object gets focus.
     *
     *  @param target A UIComponent object containing a UITextField object 
     *  that can receive focus.
     *
     *  @return Returns <code>true</code> if the UITextField object has focus.
     */
    protected function isOurFocus(target:DisplayObject):Boolean
    {
        return target == this;
    }

    /**
     *  The event handler called when a UIComponent object gets focus.
     *  If you override this method, make sure to call the base class version.
     *
     *  @param event The event object. 
     */
    protected function focusInHandler(event:FocusEvent):void
    {
        if (isOurFocus(DisplayObject(event.target)))
        {
            var fm:IFocusManager = focusManager;
            if (fm && fm.showFocusIndicator)
                drawFocus(true);
                
            ContainerGlobals.checkFocus(event.relatedObject, this);    
        }
    }

    /**
     *  The event handler called when a UIComponent object loses focus.
     *  If you override this method, make sure to call the base class version.
     *
     *  @param event The event object. 
     */
    protected function focusOutHandler(event:FocusEvent):void
    {
        // We don't need to remove our event listeners here because we
        // won't receive keyboard events.
        if (isOurFocus(DisplayObject(event.target)))
            drawFocus(false);
    }

    /**
     *  @private
     *  The player dispatches an "added" event when the addChild()
     *  or addChildAt() method of DisplayObjectContainer is called,
     *  but handling this event at that time can be dangerous
     *  so we prevent the event dispatched then from propagating;
     *  we'll dispatch another "added" event later when it is safe.
     *  The reason the timing of this player event is dangerous
     *  is that the Flex framework overrides addChild() and addChildAt(),
     *  to perform important additional work after calling the super
     *  method, such as setting _parent to skip over the contentPane
     *  of a Container. So if an "added" handler executes too early,
     *  the child is in an inconsistent state. (For example, its
     *  toString() can be wrong because the contentPane is wrongly
     *  included when traversing the parent chain.) Our overrides
     *  delay dispatching the "added" event until the end of the
     *  override, as opposed to in the middle when super.addChild()
     *  is called.
     *  Note: This event handler is registered by the UIComponent
     *  constructor, which means it is registered before any
     *  other handlers for an "added" event.
     *  Therefore it can prevent all others from being called.
     */
    private function addedHandler(event:Event):void
    {
        //reset systemManager in case we've been reparented to a new Window.
        //systemManager will be set on get systemManager()
        if (event.eventPhase != EventPhase.AT_TARGET)
            return;

        if (parent is IContainer && IContainer(parent).creatingContentPane)
        {
            event.stopImmediatePropagation();
            return;
        }
    }

    /**
     *  @private
     *  See the comments for addedHandler() above.
     */
    private function removedHandler(event:Event):void
    {
        if (event.eventPhase != EventPhase.AT_TARGET)
            return;

        if (parent is IContainer && IContainer(parent).creatingContentPane)
        {
            event.stopImmediatePropagation();
            return;
        }
        _systemManagerDirty = true;
    }

    /**
     *  @private
     *  There is a bug (139390) where setting focus from within callLaterDispatcher
     *  screws up the ActiveX player.  We defer focus until enterframe.
     */
    private function setFocusLater(event:Event = null):void
    {
        var sm:ISystemManager = systemManager;
        if (sm && sm.stage)
        {
            sm.stage.removeEventListener(Event.ENTER_FRAME, setFocusLater);
            if (UIComponentGlobals.nextFocusObject)
                sm.stage.focus = UIComponentGlobals.nextFocusObject;
            UIComponentGlobals.nextFocusObject = null;
        }
    }

    /**
     *  @private
     *  Called when this component moves. Adjust the focus rect.
     */
    private function focusObj_scrollHandler(event:Event):void
    {
        adjustFocusRect();
    }

    /**
     *  @private
     *  Called when this component moves. Adjust the focus rect.
     */
    private function focusObj_moveHandler(event:MoveEvent):void
    {
        adjustFocusRect();
    }

    /**
     *  @private
     *  Called when this component resizes. Adjust the focus rect.
     */
    private function focusObj_resizeHandler(event:ResizeEvent):void
    {
        adjustFocusRect();
    }

    /**
     *  @private
     *  Called when this component is unloaded. Hide the focus rect.
     */
    private function focusObj_removedHandler(event:Event):void
    {
        // ignore if we captured on a child
        if (event.target != this)
            return;

        var focusObject:DisplayObject = getFocusObject();

        if (focusObject)
            focusObject.visible = false;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers: Validation
    //
    //--------------------------------------------------------------------------

    /**
     *  Handles both the <code>valid</code> and <code>invalid</code> events from a
     *  validator assigned to this component.
     *
     *  <p>You typically handle the <code>valid</code> and <code>invalid</code> events
     *  dispatched by a validator by assigning event listeners to the validators.
     *  If you want to handle validation events directly in the component that is being validated,
     *  you can override this method to handle the <code>valid</code>
     *  and <code>invalid</code> events. You typically call
     *  <code>super.validationResultHandler(event)</code> in your override.</p>
     *
     *  @param event The event object for the validation.
     *
     *  @see mx.events.ValidationResultEvent
     */
    public function validationResultHandler(event:ValidationResultEvent):void
    {
        // If we are valid, then clear the error string
        if (event.type == ValidationResultEvent.VALID)
        {
            if (errorString != "")
            {
                errorString = "";
                dispatchEvent(new FlexEvent(FlexEvent.VALID));
            }
        }
        else // If we get an invalid event
        {
            var msg:String;
            var result:ValidationResult;

            // We are associated with a subfield
            if (validationSubField != null && validationSubField != "" && event.results)
            {
                for (var i:int = 0; i < event.results.length; i++)
                {
                    result = event.results[i];
                    // Find the result that is meant for us
                    if (result.subField == validationSubField)
                    {
                        if (result.isError)
                        {
                            msg = result.errorMessage;
                        }
                        else
                        {
                            if (errorString != "")
                            {
                                errorString = "";
                                dispatchEvent(new FlexEvent(FlexEvent.VALID));
                            }
                        }
                        break;
                    }
                }
            }
            else if (event.results && event.results.length > 0)
            {
                msg = event.results[0].errorMessage;
            }

            if (msg && errorString != msg)
            {
                errorString = msg;
                dispatchEvent(new FlexEvent(FlexEvent.INVALID));
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers: Resources
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function resourceManager_changeHandler(event:Event):void
    {
        resourcesChanged();
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers: Filters
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function filterChangeHandler(event:Event):void
    {
        super.filters = _filters;
    }

    //--------------------------------------------------------------------------
    //
    //  IUIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns <code>true</code> if the chain of <code>owner</code> properties 
     *  points from <code>child</code> to this UIComponent.
     *
     *  @param child A UIComponent.
     *
     *  @return <code>true</code> if the child is parented or owned by this UIComponent.
     */
    public function owns(child:DisplayObject):Boolean
    {
        var childList:IChildList =
            this is IRawChildrenContainer ?
            IRawChildrenContainer(this).rawChildren :
            IChildList(this);

        if (childList.contains(child))
            return true;

        while (child && child != this)
        {
            // do a parent walk
            if (child is IUIComponent)
                child = IUIComponent(child).owner;
            else
                child = child.parent;
        }
        return child == this;
    }

    /**
     * @private
     * 
     * Get the embedded font for a set of font attributes.
     */ 
    mx_internal function getEmbeddedFont(fontName:String, bold:Boolean, italic:Boolean):EmbeddedFont
    {
        // Check if we can reuse a cached value.
        if (cachedEmbeddedFont)
        {
            if (cachedEmbeddedFont.fontName == fontName &&
                cachedEmbeddedFont.fontStyle == EmbeddedFontRegistry.getFontStyle(bold, italic))
            {
                return cachedEmbeddedFont;
            }   
        }
        
        cachedEmbeddedFont = new EmbeddedFont(fontName, bold, italic);      
        
        return cachedEmbeddedFont;
    }
    
    /**
     *  @private
     *  Finds a module factory that can create a TextField
     *  that can display the given font.
     *  This is important for embedded fonts, not for system fonts.
     * 
     *  @param fontName The name of the fontFamily.
     *
     *  @param bold A flag which true if the font weight is bold,
     *  and false otherwise.
     *
     *  @param italic A flag which is true if the font style is italic,
     *  and false otherwise.
     * 
     *  @return The IFlexModuleFactory that represents the context
     *  where an object wanting to  use the font should be created.
     */
    mx_internal function getFontContext(fontName:String, bold:Boolean,
                                        italic:Boolean):IFlexModuleFactory
    {
        return embeddedFontRegistry.getAssociatedModuleFactory(
               getEmbeddedFont(fontName, bold, italic), moduleFactory);     
    }
   
    /**
     *  Creates a new object using a context
     *  based on the embedded font being used.
     *
     *  <p>This method is used to solve a problem
     *  with access to fonts embedded  in an application SWF
     *  when the framework is loaded as an RSL
     *  (the RSL has its own SWF context).
     *  Embedded fonts may only be accessed from the SWF file context
     *  in which they were created.
     *  By using the context of the application SWF,
     *  the RSL can create objects in the application SWF context
     *  that will have access to the application's  embedded fonts.</p>
     * 
     *  <p>Call this method only after the font styles
     *  for this object are set.</p>
     * 
     *  @param class The class to create.
     *
     *  @return The instance of the class created in the context
     *  of the SWF owning the embedded font.
     *  If this object is not using an embedded font,
     *  the class is created in the context of this object.
     */
    protected function createInFontContext(classObj:Class):Object
    {
        hasFontContextBeenSaved = true;     
        var fontName:String = StringUtil.trimArrayElements(getStyle("fontFamily"), ",");
        var fontWeight:String = getStyle("fontWeight");
        var fontStyle:String = getStyle("fontStyle");
        var bold:Boolean = (fontWeight == "bold");
        var italic:Boolean = (fontStyle == "italic");

        // save for hasFontContextChanged()
        oldEmbeddedFontContext = getFontContext(fontName, bold, italic);
        
        // not in font registry so create in this font context.
        var obj:Object = createInModuleContext(oldEmbeddedFontContext ?
                                               oldEmbeddedFontContext : moduleFactory,
                                               getQualifiedClassName(classObj)); 
        if (obj == null)
            obj = new classObj;
        
        return obj;
    }
    
    
    /**
     *  Creates the object using a given moduleFactory.
     *  If the moduleFactory is null or the object
     *  cannot be created using the module factory,
     *  then fall back to creating the object using a systemManager.
     * 
     *  @param moduleFactory The moduleFactory to create the class in;
     *  may be null.
     *
     *  @param className The name of the class to create.
     *
     *  @return The object created in the context of the moduleFactory.
     */
    protected function createInModuleContext(moduleFactory:IFlexModuleFactory,
                                             className:String):Object
    {
        var newObject:Object = null;
        
        if (moduleFactory)
            newObject = moduleFactory.create(className);            
        
        return newObject;
    }
    
    /**
     *  @private
     * 
     *  Tests if the current font context has changed 
     *  since that last time createInFontContext() was called.
     */
    public function hasFontContextChanged():Boolean
    {
        
        // If the font has not been set yet, then return false;
        // the font has not changed.
        if (!hasFontContextBeenSaved)
            return false;

        // Check if the module factory has changed.
        var fontName:String =
            StringUtil.trimArrayElements(getStyle("fontFamily"), ",");
        var fontWeight:String = getStyle("fontWeight");
        var fontStyle:String = getStyle("fontStyle");
        var bold:Boolean = fontWeight == "bold";
        var italic:Boolean = fontStyle == "italic";
        var embeddedFont:EmbeddedFont =
            getEmbeddedFont(fontName, bold, italic);
        var fontContext:IFlexModuleFactory =
            embeddedFontRegistry.getAssociatedModuleFactory(
                embeddedFont, moduleFactory);
        return fontContext != oldEmbeddedFontContext;
    }
    
    /**
     *  @inheritDoc
     */
    public function createAutomationIDPart(child:IAutomationObject):Object
    {
        if (automationDelegate)
            return automationDelegate.createAutomationIDPart(child);
        return null;
    }

    /**
     *  @inheritDoc
     */
    public function resolveAutomationIDPart(criteria:Object):Array
    {
        if (automationDelegate)
            return automationDelegate.resolveAutomationIDPart(criteria);
        return [];
    }
    
    /**
     *  @inheritDoc
     */
    public function getAutomationChildAt(index:int):IAutomationObject
    {
        if (automationDelegate)
            return automationDelegate.getAutomationChildAt(index);
        return null;
    }
    
    /**
     *  @inheritDoc
     */
    public function get numAutomationChildren():int
    {
        if (automationDelegate)
            return automationDelegate.numAutomationChildren;
        return 0;
    }
    
    /**
     *  @inheritDoc
     */
    public function get automationTabularData():Object
    {
        if (automationDelegate)
            return automationDelegate.automationTabularData;
        return null;
    }
    
    /**
     *  @inheritDoc
     */
    public function replayAutomatableEvent(event:Event):Boolean
    {
        if (automationDelegate)
            return automationDelegate.replayAutomatableEvent(event);
        return false;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Diagnostics
    //
    //--------------------------------------------------------------------------

    mx_internal static var dispatchEventHook:Function;

    /**
     *  Dispatches an event into the event flow. 
     *  The event target is the EventDispatcher object upon which 
     *  the <code>dispatchEvent()</code> method is called.
     *
     *  @param event The Event object that is dispatched into the event flow. 
     *  If the event is being redispatched, a clone of the event is created automatically. 
     *  After an event is dispatched, its <code>target</code> property cannot be changed, 
     *  so you must create a new copy of the event for redispatching to work.
     *
     *  @return A value of <code>true</code> if the event was successfully dispatched. 
     *  A value of <code>false</code> indicates failure or that 
     *  the <code>preventDefault()</code> method was called on the event.
     */
    override public function dispatchEvent(event:Event):Boolean
    {
        if (dispatchEventHook != null)
            dispatchEventHook(event, this);

        return super.dispatchEvent(event);
    }

    private static var fakeMouseX:QName = new QName(mx_internal, "_mouseX");
    private static var fakeMouseY:QName = new QName(mx_internal, "_mouseY");

    /**
     *  @private
     */
    override public function get mouseX():Number
    {
        if (!root || root is Stage || root[fakeMouseX] === undefined)
            return super.mouseX;
        return globalToLocal(new Point(root[fakeMouseX], 0)).x;
    }

    /**
     *  @private
     */
    override public function get mouseY():Number
    {
        if (!root || root is Stage || root[fakeMouseY] === undefined)
            return super.mouseY;
        return globalToLocal(new Point(0, root[fakeMouseY])).y;
    }

}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: MethodQueueElement
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 *  An element of the methodQueue array.
 */
class MethodQueueElement
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function MethodQueueElement(method:Function,
                                       args:Array /* of Object */ = null)
    {
        super();

        this.method = method;
        this.args = args;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  method
    //----------------------------------

    /**
     *  A reference to the method to be called.
     */
    public var method:Function;

    //----------------------------------
    //  args
    //----------------------------------

    /**
     *  The arguments to be passed to the method.
     */
    public var args:Array /* of Object */;
}
