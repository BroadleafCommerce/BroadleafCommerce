////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.printing
{

import flash.display.DisplayObject;
import flash.display.Loader;
import flash.display.Sprite;
import flash.display.Stage;
import flash.geom.Rectangle;
import flash.printing.PrintJob;
import flash.printing.PrintJobOptions;
import mx.core.Application;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The FlexPrintJob class is a wrapper for the flash.printing.PrintJob class.
 *  It supports automatically slicing and paginating the output on multilple pages,
 *  and scaling the grid contents to fit the printer's page size.
 *
 *  @includeExample examples/FormPrintHeader.mxml -noswf
 *  @includeExample examples/FormPrintFooter.mxml -noswf
 *  @includeExample examples/FormPrintView.mxml -noswf
 *  @includeExample examples/PrintDataGridExample.mxml
 *
 */
public class FlexPrintJob
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
    public function FlexPrintJob()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var printJob:PrintJob = new PrintJob();

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  pageHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the pageHeight property.
     */
    private var _pageHeight:Number = 0;

    /**
     *  The height  of the printable area on the printer page; 
     *  it does not include any user-set margins. 
     *  It is set after start() method returns.
     */
    public function get pageHeight():Number
    {
        return _pageHeight;
    }

    //----------------------------------
    //  pageWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the pageWidth property.
     */
    private var _pageWidth:Number = 0;
    
    /**
     *  The width of the printable area on the printer page;
     *  it does not include any user-set margins.
     *  This property is set after <code>start()</code> method returns.
     */
    public function get pageWidth():Number
    {
        return _pageWidth;
    }

    //----------------------------------
    //  printAsBitmap
    //----------------------------------

    /**
     *  @private
     *  Storage for the printAsBitmap property.
     */
    private var _printAsBitmap:Boolean = true;

    /**
     *  Specifies whether to print the job content as a bitmap (<code>true</code>)
     *  or in vector format (<code>false</code>). 
     *  Printing as a bitmap supports output that includes a bitmap image with 
     *  alpha transparency or color effects. 
     *  If the content does not include any bitmap images with
     *  alpha transparency or color effects, you can print in higher quality
     *  vector format by setting the <code>printAsBitmap</code> property to 
     *  <code>false</code>.
     * 
     *  @default true
     */
    public function get printAsBitmap():Boolean
    {
        return _printAsBitmap;
    }

    /**
     *  @private
     */
    public function set printAsBitmap(value:Boolean):void
    {
        _printAsBitmap = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Initializes the PrintJob object.
     *  Displays the operating system printer dialog to the user.
     *  Flex sets the <code>pageWidth</code> and <code>pageHeight</code>
     *  properties after this call returns.
     *
     *  @return <code>true</code> if the user clicks OK
     *  when the print dialog box appears, or <code>false</code> if the user
     *  clicks Cancel or if an error occurs.
     */
    public function start():Boolean
    {
        var ok:Boolean = printJob.start();
        
        if (ok)
        {
            _pageWidth = printJob.pageWidth;
            _pageHeight = printJob.pageHeight;
        }
        
        return ok;
    }

    /**
     *  Adds a UIComponent object to the list of objects being printed.
     *  Call this method after the <code>start()</code> method returns.
     *  Each call to this method starts a new page, so you should format 
     *  your objects in page-sized chunks. 
     *  You can use the PrintDataGrid class to span a data grid across
     *  multiple pages.
     * 
     *  @see PrintDataGrid
     *  @see FlexPrintJobScaleType
     *
     *  @param obj The Object to be printed.
     * 
     *  @param scaleType The scaling technique to use to control how the 
     *  object fits on one or more printed pages. 
     *  Must be one of the constant values defined in the FlexPrintJobScaleType
     *  class.
     */
    public function addObject(obj:IUIComponent,
                              scaleType:String = "matchWidth"):void
    {
        var objWidth:Number;
        var objHeight:Number;
        
        var objPercWidth:Number;
        var objPercHeight:Number;

        var n:int;
        var i:int;
        var j:int;

        var child:IFlexDisplayObject;
        var childPercentSizes:Object = {};

        var appExplicitWidth:Number;
        var appExplicitHeight:Number;

        if (obj is Application)
        {
            // The following loop is required only for scenario where 
            // application may have a few children with percent
            // width or height.
            n = Application(obj).numChildren
            for (i = 0; i < n; i++)
            {
                child = IFlexDisplayObject(Application(obj).getChildAt(i));
                
                if (child is UIComponent &&
                    (!isNaN(UIComponent(child).percentWidth) ||
                     !isNaN(UIComponent(child).percentHeight)))
                {
                    childPercentSizes[child.name] = {};
                    
                    if (!isNaN(UIComponent(child).percentWidth) &&
                        isNaN(UIComponent(child).explicitWidth))
                    {
                        childPercentSizes[child.name].percentWidth =
                            UIComponent(child).percentWidth;
                        UIComponent(child).percentWidth = NaN;
                        UIComponent(child).explicitWidth =
                            UIComponent(child).width;
                    }

                    if (!isNaN(UIComponent(child).percentHeight) &&
                        isNaN(UIComponent(child).explicitHeight))
                    {
                        childPercentSizes[child.name].percentHeight =
                            UIComponent(child).percentHeight;
                        UIComponent(child).percentHeight = NaN;
                        UIComponent(child).explicitHeight =
                            UIComponent(child).height;
                    }
                }
            }

            if (!isNaN(UIComponent(obj).explicitWidth) 
                && !isNaN(UIComponent(obj).explicitHeight))
            {
                appExplicitWidth = UIComponent(obj).explicitWidth;
                appExplicitHeight = UIComponent(obj).explicitHeight;
                
                UIComponent(obj).explicitWidth = NaN;
                UIComponent(obj).explicitHeight = NaN;

                UIComponent(obj).measuredWidth = appExplicitWidth;
                UIComponent(obj).measuredHeight = appExplicitHeight;
            }

            if (isNaN(obj.percentWidth) && isNaN(obj.percentHeight))
                UIComponent(obj).invalidateSizeFlag = false;

            UIComponent(obj).validateSize();

            objWidth = obj.measuredWidth;
            objHeight = obj.measuredHeight;
        }
        else
        {
            // Lock if the content is percent width or height.
            if (!isNaN(obj.percentWidth) && isNaN(obj.explicitWidth))
            {
                objPercWidth = obj.percentWidth;
                obj.percentWidth = NaN;
                obj.explicitWidth = obj.width;
            }
            
            if (!isNaN(obj.percentHeight) && isNaN(obj.explicitHeight))
            {
                objPercHeight = obj.percentHeight;
                obj.percentHeight = NaN;
                obj.explicitHeight = obj.height;
            }
            
            objWidth = obj.getExplicitOrMeasuredWidth();
            objHeight = obj.getExplicitOrMeasuredHeight();
        }

        var widthRatio:Number = _pageWidth/objWidth;
        var heightRatio:Number = _pageHeight/objHeight;

        var ratio:Number = 1;

        if (scaleType == FlexPrintJobScaleType.SHOW_ALL)
        {
            // Smaller of the two ratios for showAll.
            ratio = (widthRatio < heightRatio) ? widthRatio : heightRatio;
        }
        else if (scaleType == FlexPrintJobScaleType.FILL_PAGE)
        {
            // Bigger of the two ratios for fillPage.
            ratio = (widthRatio > heightRatio) ? widthRatio : heightRatio;
        }
        else if (scaleType == FlexPrintJobScaleType.NONE)
        {
        }
        else if (scaleType == FlexPrintJobScaleType.MATCH_HEIGHT)
        {
            ratio = heightRatio;
        }
        else
        {
            ratio = widthRatio;
        }

        // Scale it to the required value.
        obj.scaleX *= ratio;
        obj.scaleY *= ratio;
        
        UIComponentGlobals.layoutManager.usePhasedInstantiation = false;
        UIComponentGlobals.layoutManager.validateNow(); 

        var arrPrintData:Array = prepareToPrintObject(obj);

        if (obj is Application)
        {
            objWidth *= ratio;
            objHeight *= ratio;
        }
        else
        {
            objWidth = obj.getExplicitOrMeasuredWidth();
            objHeight = obj.getExplicitOrMeasuredHeight();
        }

        // Find the number of pages required in vertical and horizontal.
        var hPages:int = Math.ceil(objWidth / _pageWidth);
        var vPages:int = Math.ceil(objHeight / _pageHeight);

        // when sent to addPage, scaling is to be ignored.
        var incrX:Number = _pageWidth / ratio;
        var incrY:Number = _pageHeight / ratio;

        var lastPageWidth:Number = (objWidth % _pageWidth) / ratio;
        var lastPageHeight:Number = (objHeight % _pageHeight) / ratio;

        for (j = 0; j < vPages; j++)
        {
            for (i = 0; i < hPages; i++)
            {
                var r:Rectangle =
                    new Rectangle(i * incrX, j * incrY, incrX, incrY);

                // For last pages send only the remaining amount
                // so that rest of the paper is printed white
                // else it prints that in gray.
                if (i == hPages - 1 && lastPageWidth != 0)
                    r.width = lastPageWidth;

                if (j == vPages - 1 && lastPageHeight != 0)
                    r.height = lastPageHeight;

                // The final edge may have got fractioned as
                // contents may not be complete multiple of pageWidth/Height.
                // This may result in a blank area at the end of page.
                // Tthis rounding off ensures no small blank area in the end 
                // but results in some part of next page getting reprinted
                // this page but it does not result in loss of any information.
                r.width = Math.ceil(r.width);
                r.height = Math.ceil(r.height);

                var printJobOptions:PrintJobOptions = new PrintJobOptions();
                printJobOptions.printAsBitmap = _printAsBitmap;

                printJob.addPage(Sprite(obj), r, printJobOptions);
            }
        }

        finishPrintObject(obj, arrPrintData);

        // Scale it back.
        obj.scaleX /= ratio;
        obj.scaleY /= ratio;

        if (obj is Application)
        {
            if (!isNaN(appExplicitWidth)) //&& !isNaN(appExplicitHeight))
            {
                UIComponent(obj).setActualSize(appExplicitWidth,appExplicitHeight);
                //UIComponent(obj).explicitWidth = appExplicitWidth;
                //UIComponent(obj).explicitHeight = appExplicitHeight;

                appExplicitWidth = NaN;
                appExplicitHeight = NaN;

                UIComponent(obj).measuredWidth = 0;
                UIComponent(obj).measuredHeight = 0;
            }

            // The following loop is required only for scenario
            // where application may have a few children
            // with percent width or height.
            n = Application(obj).numChildren
            for (i = 0; i < n; i++)
            {
                child = IFlexDisplayObject(Application(obj).getChildAt(i));
                if (child is UIComponent && childPercentSizes[child.name])
                {
                    var childPercentSize:Object = childPercentSizes[child.name];
                    if (childPercentSize &&
                        !isNaN(childPercentSize.percentWidth))
                    {
                        UIComponent(child).percentWidth =
                            childPercentSize.percentWidth;
                        UIComponent(child).explicitWidth = NaN;
                    }

                    if (childPercentSize &&
                        !isNaN(childPercentSize.percentHeight))
                    {
                        UIComponent(child).percentHeight =
                            childPercentSize.percentHeight;
                        UIComponent(child).explicitHeight = NaN;
                    }
                }
            }
            UIComponent(obj).invalidateSizeFlag = false;
            UIComponent(obj).validateSize();
        }
        else
        {
            // Unlock if the content was percent width or height.
            if (!isNaN(objPercWidth))
            {
                obj.percentWidth = objPercWidth;
                obj.explicitWidth = NaN;
            }

            if (!isNaN(objPercHeight))
            {
                obj.percentHeight = objPercHeight;
                obj.explicitHeight = NaN;
            }
        }

        UIComponentGlobals.layoutManager.usePhasedInstantiation = false;
        UIComponentGlobals.layoutManager.validateNow(); 
    }

    /**
     *  Sends the added objects to the printer to start printing.
     *  Call this method after you have used the <code>addObject()</code>
     *  method to add the print pages.
     */
    public function send():void
    {
        printJob.send();
    }

    /**
     *  @private
     *  Prepare the target and its parents to print.
     *  If the content is inside a Container with scrollBars,
     *  it still gets printed all right. 
     */
    private function prepareToPrintObject(target:IUIComponent):Array
    {
        var arrPrintData:Array = [];

        var obj:DisplayObject 
            = (target is DisplayObject) ? DisplayObject(target) : null;
        var index:Number = 0;
        
        while (obj)
        {
            if (obj is UIComponent)
                arrPrintData[index++] 
                    = UIComponent(obj).prepareToPrint(UIComponent(target));
            else if (obj is DisplayObject && !(obj is Stage))
            {
                arrPrintData[index++] = DisplayObject(obj).mask;
                DisplayObject(obj).mask = null;
            }

            obj = (obj.parent is DisplayObject) ?
                  DisplayObject(obj.parent) :
                  null;
        }
        
        return arrPrintData;
    }

    /**
     *  @private
     *  Reverts the target and its parents back from Print state, 
     */
    private function finishPrintObject(target:IUIComponent,
                                       arrPrintData:Array):void
    {
        var obj:DisplayObject 
            = (target is DisplayObject) ? DisplayObject(target) : null;
        var index:Number = 0;
        while (obj)
        {
            if (obj is UIComponent)
                UIComponent(obj).finishPrint(arrPrintData[index++],
                                                UIComponent(target));
            else if (obj is DisplayObject && !(obj is Stage))
            {
                DisplayObject(obj).mask = arrPrintData[index++];
            }

            obj = (obj.parent is DisplayObject) ?
                  DisplayObject(obj.parent) :
                  null;
        }
    }
}

}
