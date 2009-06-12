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

package mx.core
{

/**
 *  The IBorder interface defines the interface that all classes 
 *  used for border skins should implement.
 *
 *  <p>It is not an error if the border skin does not implement IBorder. 
 *  In this case, however, the container using the skin cannot determine 
 *  the border metrics of the border. 
 *  Therefore, the container places content starting at its top-left edge 
 *  (adjusted for padding, if any). 
 *  For the HaloBorder class, the <code>borderThickness</code> style 
 *  usually determines the value of the <code>borderMetrics</code> style. 
 *  For graphical skin classes, Flex examines the <code>scale9Grid</code>
 *  property to determine the value of the <code>borderMetrics</code> style.</p>
 */
public interface IBorder
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  borderMetrics
    //----------------------------------

    /**
     *  Returns an EdgeMetrics object for the border that has four properties:
     *  <code>left</code>, <code>top</code>, <code>right</code>,
     *  and <code>bottom</code>.
     *  The value of each property is equal to the thickness of one side
     *  of the border, in pixels.
     */
    function get borderMetrics():EdgeMetrics;

}

}
