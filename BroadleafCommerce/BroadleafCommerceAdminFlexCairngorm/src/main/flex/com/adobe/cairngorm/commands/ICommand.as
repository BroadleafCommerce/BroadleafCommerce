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
package com.adobe.cairngorm.commands
{ 
   import com.adobe.cairngorm.control.CairngormEvent;

   /**
    * 
    * The ICommand interface enforces the contract between the Front
    * Controller and concrete command classes in your application.
    * 
    * <p>In a Cairngorm application, the application specific Front Controller
    * will listen for events of interest, dispatching control to appropriate
    * command classes according to the type of the event broadcast.</p>
    *
    * <p>
    * When an event is broadcasted by the Front Controller, it will
    * lookup its list of registered commands, to find the command capable
    * of carrying out the appropriate work in response to the user gesture
    * that has caused the event.
    * </p>
    *
    * <p>
    * When the event that an command is registered against is broadcast,
    * the Front Controller class will invoke the command by calling its
    * execute() method, which can be considered the entry point to a
    * command.
    * </p>
    *
    * @see com.adobe.cairngorm.control.FrontController
    * @see com.adobe.cairngorm.control.CairngormEventDispatcher
    */ 
   public interface ICommand
   {
      /** Called by the Front Controller to execute the command.
       * 
       * <p>The single entry point into an ICommand, the 
       * execute() method is called by the Front Controller when a 
       * user-gesture indicates that the user wishes to perform a 
       * task for which a particularconcrete command class has been 
       * provided.</p>
       *
       * @param event When the Front Controller receives notification
       * of a user gesture, the Event that it receives contains both the
       * type of the event (indicating which command should handle the
       * work) but also any data associated with the event.
       *
       * <p>
       * For instance, if a "login" event has been broadcasted,
       * to which the controller has registered the LoginCommand,
       * the event may also contain some associated data, such as
       * the number of prior attempts at login have been made
       * already. In this case, the event.type would be set to
       * "login" while other properties define in the custom login 
       * event object would contain (by way of example)
       * an attribute such as attemptedLogins. 
       * </p>
       *
       * <p>
       * By careful use of custom event objects, the same
       * concrete command class is capable of responding in slightly
       * different ways to similar user gesture requests.
       * </p>
       */
      function execute( event : CairngormEvent ) : void;
   }
}