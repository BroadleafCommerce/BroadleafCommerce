////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////


/**
 *  The color for the icon in a skin. 
 *  For example, this style is used by the CheckBoxIcon skin class 
 *  to draw the check mark for a CheckBox control, 
 *  by the ComboBoxSkin class to draw the down arrow of the ComboBox control, 
 *  and by the DateChooserMonthArrowSkin skin class to draw the month arrow 
 *  for the DateChooser control. 
 * 
 *  The default value depends on the component class;
 *  if it is not overridden by the class, the default value is <code>0x111111</code>.
 */
[Style(name="iconColor", type="uint", format="Color", inherit="yes")]

/**
 *  The color for the icon in a disabled skin. 
 *  For example, this style is used by the CheckBoxIcon skin class 
 *  to draw the check mark for a disabled CheckBox control, 
 *  by the ComboBoxSkin class to draw the down arrow of a disabled ComboBox control, 
 *  and by the DateChooserMonthArrowSkin skin class to draw the month arrow 
 *  for a disabled DateChooser control. 
 * 
 *  The default value depends on the component class;
 *  if it is not overridden by the class, the default value is <code>0x999999</code>.
 */
[Style(name="disabledIconColor", type="uint", format="Color", inherit="yes")]