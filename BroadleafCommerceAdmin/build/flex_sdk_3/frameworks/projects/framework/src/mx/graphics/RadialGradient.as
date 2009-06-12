////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.graphics
{

import flash.display.GradientType;
import flash.display.Graphics;
import flash.geom.Matrix;
import flash.geom.Rectangle;

/**
 *  The RadialGradient class lets you specify a gradual color transition 
 *  in the fill color.
 *  A radial gradient defines a fill pattern
 *  that radiates out from the center of a graphical element. 
 *  You add a series of GradientEntry objects
 *  to the RadialGradient object's <code>entries</code> Array
 *  to define the colors that make up the gradient fill.
 *  
 *  <p>In MXML, you define a RadialGradient by adding a series
 *  of GradientEntry objects, as the following example shows:
 *  <pre>
 *  &lt;mx:fill&gt;
 *      &lt;mx:RadialGradient&gt;
 *          &lt;mx:entries&gt;
 *              &lt;mx:GradientEntry color="0xC5C551" ratio="0.00" alpha="0.5"/&gt;
 *              &lt;mx:GradientEntry color="0xFEFE24" ratio="0.33" alpha="0.5"/&gt;
 *              &lt;mx:GradientEntry color="0xECEC21" ratio="0.66" alpha="0.5"/&gt;
 *          &lt;/mx:entries&gt;
 *      &lt;/mx:RadialGradient&gt;
 *  &lt;/mx:fill&gt;
 *  </pre>
 *  </p>
 *  
 *  <p>You can also define a RadialGradient as a fill for any graphic element
 *  in ActionScript, as the following example shows:
 *  <pre>
 *  
 *  &lt;?xml version="1.0"?&gt;
 *  &lt;mx:Application xmlns:mx="http://www.adobe.com/2006/mxml" creationComplete="init()"&gt;
 *      &lt;mx:Script&gt;
 *      import flash.display.Graphics;
 *      import flash.geom.Rectangle;
 *      import mx.graphics.GradientEntry;
 *      import mx.graphics.RadialGradient;
 *  
 *      private function init():void
 *      {
 *          var w:Number = 200;
 *          var h:Number = 200;
 *  
 *          var s:Sprite = new Sprite();
 *          // Add the new Sprite to the display list.
 *          rawChildren.addChild(s);    
 *  
 *          var g:Graphics = s.graphics;
 *          g.lineStyle(1, 0x33CCFF, 1.0);
 *  
 *          var fill:RadialGradient = new RadialGradient();
 *          
 *          var g1:GradientEntry = new GradientEntry(0xFFCC66, 0.00, 0.5);
 *          var g2:GradientEntry = new GradientEntry(0x000000, 0.33, 0.5);
 *          var g3:GradientEntry = new GradientEntry(0x99FF33, 0.66, 0.5);
 *          
 *          fill.entries = [ g1, g2, g3 ];
 *  
 *          // Set focal point to upper left corner.
 *          fill.angle = 45;
 *          fill.focalPointRatio = -0.8;
 *  
 *          // Draw a box and fill it with the RadialGradient.
 *          g.moveTo(0, 0);
 *          fill.begin(g,new Rectangle(0, 0, w, h));
 *          g.lineTo(w, 0);
 *          g.lineTo(w, h);
 *          g.lineTo(0, h);
 *          g.lineTo(0, 0);      
 *          fill.end(g);
 *      }
 *      &lt;/mx:Script&gt;
 *  &lt;/mx:Application&gt;
 *  </pre>  
 *  </p>  
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:RadialGradient&gt;</code> tag
 *  inherits all the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:RadialGradient
 *    <b>Properties</b>
 *    angle="0"
 *    focalPointRatio="0"
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.graphics.GradientEntry
 *  @see mx.graphics.LinearGradient  
 *  @see mx.graphics.IFill
 */
public class RadialGradient extends GradientBase implements IFill
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function RadialGradient()
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
    private var matrix:Matrix = new Matrix();
        
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  angle
    //----------------------------------

    /**
     *  @private
     *  Storage for the angle property.
     */
    private var _rotation:Number = 0.0;

    [Bindable("propertyChange")]
    [Inspectable(category="General")]
    
    /**
     *  Sets the location of the start of the radial fill.
     *
     *  <p>Use the <code>focalPointRatio</code> property in conjunction
     *  with this property to adjust the location and distance
     *  from the center point of the bounding Rectangle to the focal point.</p>
     * 
     *  <p>Valid values are from 0.0 to 360.0.</p>
     *  
     *  @default 0
     */
    public function get angle():Number
    {
        return _rotation / Math.PI * 180;
    }

    /**
     *  @private 
     */
    public function set angle(value:Number):void
    {
        var oldValue:Number = _rotation;
        _rotation = value / 180 * Math.PI;
        
        mx_internal::dispatchGradientChangedEvent("angle", oldValue, _rotation);
    }

    //----------------------------------
    //  focalPointRatio
    //----------------------------------

    /**
     *  @private
     *  Storage for the focalPointRatio property.
     */
    private var _focalPointRatio:Number = 0.0;
    
    [Bindable("propertyChange")]
    [Inspectable(category="General")]
    
    /**
     *  Sets the location of the start of the radial fill.
     *
     *  <p>Valid values are from <code>-1.0</code> to <code>1.0</code>.
     *  A value of <code>-1.0</code> sets the focal point
     *  (or, start of the gradient fill)
     *  on the left of the bounding Rectangle.
     *  A value of <code>1.0</code> sets the focal point
     *  on the right of the bounding Rectangle.
     *  
     *  <p>If you use this property in conjunction
     *  with the <code>angle</code> property, 
     *  this value specifies the degree of distance
     *  from the center that the focal point occurs. 
     *  For example, with an angle of 45
     *  and <code>focalPointRatio</code> of 0.25,
     *  the focal point is slightly lower and to the right of center.
     *  If you set <code>focalPointRatio</code> to <code>0</code>,
     *  the focal point is in the middle of the bounding Rectangle.</p>
     *  If you set <code>focalPointRatio</code> to <code>1</code>,
     *  the focal point is all the way to the bottom right corner
     *  of the bounding Rectangle.</p>
     *
     *  @default 0.0
     */
    public function get focalPointRatio():Number
    {
        return _focalPointRatio;
    }
    
    /**
     *  @private
     */
    public function set focalPointRatio(value:Number):void
    {
        var oldValue:Number = _focalPointRatio;
        if (value != oldValue)
        {
            _focalPointRatio = value;
            
            mx_internal::dispatchGradientChangedEvent("focalPointRatio",
                                                      oldValue, value);
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    public function begin(target:Graphics, rc:Rectangle):void
    {
        matrix.createGradientBox(rc.width, rc.height, _rotation,
                                 rc.left, rc.top);
            
        target.beginGradientFill(GradientType.RADIAL, mx_internal::colors,
                                 mx_internal::alphas, mx_internal::ratios,
                                 matrix, null, null, focalPointRatio);      
    }

    /**
     *  @inheritDoc
     */
    public function end(target:Graphics):void
    {
        target.endFill();
    }
    
}

}
