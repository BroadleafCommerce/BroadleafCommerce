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

/**
 *  Color of the border.
 *  The following controls support this style: Button, CheckBox,
 *  ComboBox, MenuBar,
 *  NumericStepper, ProgressBar, RadioButton, ScrollBar, Slider, and any
 *  components that support the <code>borderStyle</code> style.
 *  The default value depends on the component class;
 *  if not overriden for the class, the default value is <code>0xB7BABC</code>.
 */
[Style(name="borderColor", type="uint", format="Color", inherit="no")]

/**
 *  Radius of component corners.
 *  The following components support this style: Alert, Button, ComboBox,  
 *  LinkButton, MenuBar, NumericStepper, Panel, ScrollBar, Tab, TitleWindow, 
 *  and any component
 *  that supports a <code>borderStyle</code> property set to <code>"solid"</code>.
 *  The default value depends on the component class;
 *  if not overriden for the class, the default value is <code>0</code>.
 */
[Style(name="cornerRadius", type="Number", format="Length", inherit="no")]

/**
 *  Alphas used for the background fill of controls. Use [1, 1] to make the control background
 *  opaque.
 *  
 *  @default [ 0.6, 0.4 ]
 */
[Style(name="fillAlphas", type="Array", arrayType="Number", inherit="no")]

/**
 *  Colors used to tint the background of the control.
 *  Pass the same color for both values for a flat-looking control.
 *  
 *  @default [ 0xFFFFFF, 0xCCCCCC ]
 */
[Style(name="fillColors", type="Array", arrayType="uint", format="Color", inherit="no")]

/**
 *  Alpha transparencies used for the highlight fill of controls.
 *  The first value specifies the transparency of the top of the highlight and the second value specifies the transparency 
 *  of the bottom of the highlight. The highlight covers the top half of the skin.
 *  
 *  @default [ 0.3, 0.0 ]
 */
[Style(name="highlightAlphas", type="Array", arrayType="Number", inherit="no")]
