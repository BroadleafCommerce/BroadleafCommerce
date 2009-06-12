////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  Modality of components launched by the PopUp Manager is simulated by
 *  creating a large translucent overlay underneath the component.
 *  Because of the way translucent objects are rendered, you may notice a slight
 *  dimming of the objects under the overlay.
 *  The effective transparency can be set by changing the
 *  <code>modalTransparency</code> value from 0.0 (fully transparent)
 *  to 1.0 (fully opaque).
 *  You can also set the color of the overlay by changing the 
 *  <code>modalTransparencyColor</code> style.
 *
 *  @default 0.5
 */
[Style(name="modalTransparency", type="Number", inherit="yes")]

/**
 *  The blur applied to the application while a modal window is open.
 *  A Blur effect softens the details of an image. 
 *  
 *  @see flash.filters.BlurFilter
 *
 *  @default 3
 */
[Style(name="modalTransparencyBlur", type="Number", inherit="yes")]

/**
 *  Color of the modal overlay layer. This style is used in conjunction
 *  with the <code>modalTransparency</code> style to determine the colorization 
 *  applied to the application when a modal window is open.
 *
 *  @default #DDDDDD
 */
[Style(name="modalTransparencyColor", type="uint", format="Color", inherit="yes")]

/**
 *  Duration, in milliseconds, of the modal transparency effect that
 *  plays when a modal window opens or closes.
 *
 *  @default 100
 */
[Style(name="modalTransparencyDuration", type="Number", format="Time", inherit="yes")]
