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
   import com.adobe.cairngorm.CairngormError;
   import com.adobe.cairngorm.CairngormMessageCodes;
   
   import flash.utils.Dictionary;

   /**
    * <p><strong>Deprecated as of Cairngorm 2.1.</strong></p>
    * 
    * The ViewLocator is a singleton class, that is used to retreive ViewHelper
    * classes that can manipulate (get/set/switch) the user interface of a
    * Cairngorm RIA.
    *
    * <p>Model-View-Controller (MVC) best practices specify that command classes 
    * should interact with the view using the model (see the ModelLocator class), 
    * but in some instance, command classes may require the assistance of 
    * ViewHelpers to get/set the user interface associated with the work they 
    * are designed to perform.  ViewHelper classes contain methods that allow 
    * them to interrogate (get) individual elements of the view, such as the 
    * values of form fields, and to update the user interface, such as setting the 
    * data provider on DataGrids or Listcomponents.  Additionally, a command may 
    * choose to toggle the user interface from one view to another - for instance, 
    * to take the user to a checkout screen.</p>
    *
    * <p>
    * The ViewLocator class is used to allow commands to instantly retrieve
    * the appropriate ViewHelper.  A command need only know the canonical
    * name of a ViewHelper and the ViewLocator will return an instance of
    * the appropriate ViewHelper class.  In this way, command classes can
    * manipulate the View irrespective of its implementation.
    * </p>
    *
    * @see com.adobe.cairngorm.model.ModelLocator
    * @see com.adobe.cairngorm.view.ViewHelper
    * @see com.adobe.cairngorm.commands.ICommand
    */
   public class ViewLocator
   {
      private static var viewLocator : ViewLocator;
      private var viewHelpers : Dictionary;      
      
      /**
       * Singleton access to the ViewLocator is assured through the static getInstance()
       * method, which is used to retrieve the only ViewLocator instance in a Cairngorm
       * application.
       *
       * <p>Wherever there is a need to retreive the ViewLocator instance, it is achieved
       * using the following code:</p>
       *
       * <pre>
       * var viewLocator:ViewLocator = ViewLocator.getInstance();
       * </pre>
       */
      public static function getInstance() : ViewLocator
      {
         if ( viewLocator == null )
            viewLocator = new ViewLocator();
   
         return viewLocator;
      }

      /**
       * The ViewLocator constructor should only be created
       * through the static singleton getInstance() method.  ViewLocator
       * maintains a hash map of ViewHelpers, keyed on viewName with a
       * particular view as the value stored in the hash map.
       */
      public function ViewLocator()
      {
         if ( ViewLocator.viewLocator != null )
         {
            throw new CairngormError(
               CairngormMessageCodes.SINGLETON_EXCEPTION, "ViewLocator" );
         }
         
         viewHelpers = new Dictionary();      
      }

      /**
       * Registers a viewHelper under a canonical viewName.
       *
       * <p>In order that the application developer need not know
       * the implementation of the view, a ViewHelper capable of manipulating
       * a given view is registered under a simple canonical name.</p>
       * <p>
       * For example, a LoginViewHelper may allow the manipulation of a
       * Login window, that may start life as a PopUpWindow, but later be
       * changed to a screen in a ViewStack.  By registering the LoginViewHelper
       * with the viewName "login", then any code that fetches the ViewHelper
       * by it's name "login", and then calls methods on the ViewHelper, is
       * completely insulated from any changes in the implementation of the
       * view, and the implementation of the ViewHelper.
       * </p>
       * <p>
       * If a view is already registered with the canonical name, and Error
       * is thrown.
       * </p>
       * @param viewName A simple canonical name for the view that the ViewHelper
       * will manipulate, eg "login"
       * @param viewHelper An instance of a ViewHelper
       */
      public function register( viewName : String, viewHelper : ViewHelper ) : void
      {
         if ( registrationExistsFor( viewName ) )
         {
            throw new CairngormError(
               CairngormMessageCodes.VIEW_ALREADY_REGISTERED, viewName );
         }
   
         viewHelpers[ viewName ] = viewHelper;
      }
      
      /**
       * Unregisters a viewHelper using its canonical name.
       *
       * @param viewName The canonical name for the view to be removed
       */
      public function unregister( viewName : String ) : void
      {
         if ( !registrationExistsFor( viewName ) )
         {
            throw new CairngormError(
               CairngormMessageCodes.VIEW_NOT_FOUND, viewName );
         }
         
         delete viewHelpers[ viewName ];
      }
      
      /**
       * Retrieves the ViewHelper instance that has previously been registered
       * with viewName.
       *
       * @param viewName The name of the view for which we wish to retrieve a
       * ViewHelper, eg "login"
       * @returns The ViewHelper instance that is required to manipulate the
       * view registered with viewName
       */
      public function getViewHelper( viewName : String ) : ViewHelper
      {
         if ( !registrationExistsFor( viewName ) )
         {
            throw new CairngormError(
               CairngormMessageCodes.VIEW_NOT_FOUND, viewName );
         }
         
         return viewHelpers[ viewName ];
      }
      
      /**
       * Returns whether a view has been registered with a canonical name.
       *
       * <p>If two views are registered with the same canonical name,
       * the second entry will overwrite the first. This method can be used to check
       * whether a view has already been registered with a canonical name.</p>
       *
       * @param The canonical name for the view that the ViewHelper will check, eg
       * "login"
       * @return A Boolean that indicates if a view is already registered with that
       * view name
       */
      public function registrationExistsFor( viewName : String ) : Boolean
      {
         return viewHelpers[ viewName ] != undefined;
      }
   }
}