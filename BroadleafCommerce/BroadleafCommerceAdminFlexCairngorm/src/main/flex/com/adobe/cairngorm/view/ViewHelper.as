/*

Copyright (c) 2006. Adobe Systems Incorporated.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
  * Neither the name of Adobe Systems Incorporated nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

@ignore
*/

package com.adobe.cairngorm.view
{
   import flash.events.Event;
   import mx.core.IMXMLObject;

   /**
    * <p><strong>Deprecated as of Cairngorm 2.1.</strong></p>
    * 
    * Used to isolate command classes from the implementation details of a view.
    * 
    * <p>Model-View-Controller (MVC) best practices specify that command classes 
    * should interact with the view using the model (see the ModelLocator class), 
    * but, in some instances, command classes may require to both interrogate and 
    * update the view directly.  Prior to performing any business logic, the command 
    * class may require to fetch values that have been set on the view; following 
    * completion of any business logic, the final task may be for a 
    * command class to update the View (user interface) with any results returned, 
    * or perhaps to switch the View entirely (to a different screen).</p>
    *
    * <p>
    * By encapsulating all the logic necessary for interrogating and
    * updating a particular View into a single helper class, we remove
    * the need for the command classes to have any knowledge about the
    * implementation of the View.  The ViewHelper class decouples our
    * presentation from the control of the application.
    * </p>
    *
    * <p>
    * A ViewHelper belongs to a particular View in the application; when
    * a ViewHelper is created, its id is used to register
    * against a particular View component (such as a particular
    * tab in a TabNavigator, or a particular screen in a ViewStack).  The
    * developer then uses the ViewLocator to locate the particular
    * ViewHelper for interrogation or update of a particular View.
    * </p>
    * 
    * @see com.adobe.cairngorm.model.ModelLocator
    * @see com.adobe.cairngorm.view.ViewLocator
    */

   public class ViewHelper implements IMXMLObject
   {      
      public function ViewHelper() {
         super();
      }
   	
      /**
      * The view referred to by this view helper
      */
      protected var view : Object;
      
      /**
      * The id of the view 
      */
      protected var id : String;      
      
      /**
       * On initialization, the view is initialized with the <code>ViewLocator</code>
       * with the <code>ViewLocator</code>, using its <code>id</code>.
       * On Event.REMOVED and Event.ADDED events of a view, 
       * the view is registered or unregistered from the <code>ViewLocator</code>.
       * <p>The <code>initialized</code> method is called by the Flex component
       * framework after a component has been initialized, so long as the
       * component implements <code>mx.core.IMXMLObject</code>.</p>
       */
      public function initialized( document : Object, id : String ) : void
      {
         this.view = document;
         this.id = id;
         
         view.addEventListener( Event.ADDED, registerView );
         view.addEventListener( Event.REMOVED, unregisterView );
      }
      
      /**
       * Registers the view from the ViewLocator when added to the display list.
       * @see com.adobe.cairngorm.view.ViewLocator
       */
      private function registerView( event : Event ) : void
      {
         if ( event.target == view )
           {
              ViewLocator.getInstance().register( id, this );
           }
      }
      
      /**
       * Unregisters the view from the ViewLocator when taken off the display list.
       * @see com.adobe.cairngorm.view.ViewLocator
       */
      private function unregisterView( event : Event ) : void
      {
         if( event.target == view )
         {
            ViewLocator.getInstance().unregister( id );
         }
      }
   }
}

