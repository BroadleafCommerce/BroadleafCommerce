/*

Copyright (c) 2006. Adobe Systems Incorporated.
All rights reserved.

Includes "Cairngorm 3" extensions developed by Marcin Hagmajer, mhagmajer@gmail.com.
Find out more at: http://students.mimuw.edu.pl/~mhagmajer/cairngorm3/.
The note below applies to the modifications as well.

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
    
package com.adobe.cairngorm.control
{   
   import com.adobe.cairngorm.CairngormError;
   import com.adobe.cairngorm.CairngormMessageCodes;
   import com.adobe.cairngorm.commands.ICommand;
   import com.adobe.cairngorm.responder.CairngormResponder;
   
   import flash.events.Event;
   import flash.events.EventDispatcher;
   import flash.utils.Dictionary;

/**
 *  Event processing executed an asynchronous process.
 *
 *  @eventType com.adobe.cairngorm.control.FrontControllerEvent.ASYNCHRONOUS
 */
[Event(name="asynchronous", type="com.adobe.cairngorm.control.FrontControllerEvent")]

/**
 *  Event processing has completed.
 *
 *  @eventType com.adobe.cairngorm.control.FrontControllerEvent.COMPLETE
 */
[Event(name="complete", type="com.adobe.cairngorm.control.FrontControllerEvent")]

/**
 * A base class for an application specific front controller,
 * that is able to dispatch control following particular user gestures to appropriate
 * command classes.
 *
 * <p>
 * The Front Controller is the centralised request handling class in a
 * Cairngorm application.  Throughout the application architecture are
 * scattered a number of CairngormEventDispatcher.getInstance().dispatchEvent( event )
 * method calls, that signal to the listening controller that a user gesture
 * has occured.
 * </p>
 *
 * <p>
 * The role of the Front Controller is to first register all the different
 * events that it is capable of handling against worker classes, called
 * command classes.  On hearing an application event, the Front Controller
 * will look up its table of registered events, find the appropriate
 * command for handling of the event, before dispatching control to the
 * command by calling its execute() method.
 * </p>
 *
 * <p>
 * Commands are added to the front controller with a weak reference,
 * meaning that when the command is garbage collected, the reference in
 * the controller is also garbage collected.
 * </p>
 * 
 * <p>
 * The Front Controller is a base-class that  listen for events 
 * dispatched by CairngormEventDispatcher.  In a 
 * Cairngorm application, the developer should create a class that
 * extends the FrontController, and in the constructor of their
 * application specific controller, they should make numerous calls to
 * addCommand() to register all the expected events with application
 * specific command classes.
 * </p>
 *
 * <p>
 * Consider a LoginController, that is the main controller for a Login
 * application that has 2 user gestures - Login and Logout.  The application
 * will have 2 buttons, "Login" and "Logout" and in the click handler for
 * each button, one of the following methods is executed:
 * </p>
 *
 * <pre>
 * public function doLogin() : void
 * {
 *    var event : LoginEvent = new LoginEvent( username.text, password.text );
 *    CairngormEventDispatcher.getInstance.dispatchEvent( event );
 * }
 * 
 * public function doLogout() : void
 * {
 *    var event : LogoutEvent = new LogoutEvent();
 *    CairngormEventDispatcher.getInstance.dispatchEvent( event );
 * }
 * </pre>
 * 
 * <p>
 * We would create LoginController as follows:
 * </p>
 *
 * <pre>
 * class LoginController extends com.adobe.cairngorm.control.FrontController
 * {
 *    public function LoginController()
 *    {
 *       initialiseCommands();
 *    }
 * 
 *    public function initialiseCommands() : void
 *    {
 *       addCommand( LoginEvent.EVENT_LOGIN, LoginCommand );
 *       addCommand( LogoutEvent.EVENT_LOGOUT, LogoutCommand );
 *    }
 *   
 * }
 * </pre>
 *
 * <p>
 * In our concrete implementation of a FrontController, LoginController, we
 * register the 2 events that are expected for broadcast - login and logout -
 * using the addCommand() method of the parent FrontController class, to
 * assign a command class to each event.
 * </p>
 *
 * <p>
 * Adding a new use-case to a Cairngorm application is as simple as
 * registering the event against a command in the application Front Controller,
 * and then creating the concrete command class.
 * </p>
 * 
 * <p>
 * The concrete implementation of the FrontController, LoginController,
 * should be created once and once only (as we only want a single controller
 * in our application architecture).  Typically, in our main application, we
 * would declare our FrontController child class as a tag, which should be placed
 * above any tags which have a dependency on the FrontController
 * </p>
 *
 * <pre>
 * &lt;mx:Application  xmlns:control="com.domain.project.control.LoginController"   ... &gt;
 *
 *   &lt;control:LoginController id="controller" /&gt;
 *
 *  ...
 * 
 * </pre>
 *
 * @see com.adobe.cairngorm.commands.ICommand
 */
public class FrontController extends EventDispatcher
{
	/**
	 * Dictionary of event name to command class mappings
	 */ 
	protected var commands : Dictionary = new Dictionary();
	
    [Inspectable(category="General", defaultValue="true")]
	/**
	 * Indicates wheter errors throwed by the commands are throwed on by the controller.
	 * 
	 * This only takes effect if you run your application in the debug mode.
	 * Otherwise the all uncatched errors are ignored by the Adobe Flash Player. 
	 * 
	 * @default true
	 */
	public var throwErrors:Boolean = true;

	/**
	 * Registers a ICommand class with the Front Controller, against an event name
	 * and listens for events with that name.
	 *
	 * <p>When an event is broadcast that matches commandName,
	 * the ICommand class referred to by commandRef receives control of the
	 * application, by having its execute() method invoked.</p>
	 *
	 * @param commandName The name of the event that will be broadcast by the
	 * when a particular user gesture occurs, eg "login"
	 *
	 * @param commandRef An ICommand Class reference upon which execute()
	 * can be called when the Front Controller hears an event broadcast with
	 * commandName. Typically, this argument is passed as "LoginCommand" 
	 * or similar.
	 * 
	 * @param useWeakReference A Boolean indicating whether the controller
	 * should added as a weak reference to the CairngormEventDispatcher,
	 * meaning it will eligibile for garbage collection if it is unloaded from 
	 * the main application.
	 */     
    public function addCommand( commandName : String, commandRef : Class, useWeakReference : Boolean = true ) : void
    {
        if( commands[ commandName ] != null )
            throw new CairngormError( CairngormMessageCodes.COMMAND_ALREADY_REGISTERED, commandName );
  
        commands[ commandName ] = commandRef;
        CairngormEventDispatcher.getInstance().addEventListener( commandName, executeCommand, false, 0, useWeakReference );
    }

	/**
	 * Deregisters an ICommand class with the given event name from the Front Controller 
	 *
	 * @param commandName The name of the event that will be broadcast by the
	 * when a particular user gesture occurs, eg "login"
	 *
	 */     
    public function removeCommand( commandName : String ) : void
    {
        if( commands[ commandName ] === null)
            throw new CairngormError( CairngormMessageCodes.COMMAND_NOT_REGISTERED, commandName);  
    
        CairngormEventDispatcher.getInstance().removeEventListener( commandName, executeCommand );
        commands[ commandName ] = null;
        delete commands[ commandName ]; 
    }

	/**
	 * Executes the command.
	 * 
	 * If the command is not responder, basic CairngormResponder is created.
	 * Dispatches generic ASYNCHRONOUS event and responder specific event.type + "Asynchronous"
	 * event if the responder is still finish after the execute() call.
	 * Any error throwed in execute() call is catched and passed back to the responder's fault
	 * handler.
	 */  
    protected function executeCommand( event : CairngormEvent ) : void
    {
        var commandToInitialise : Class = getCommand( event.type );
        var commandToExecute : ICommand = new commandToInitialise();
        var isResponder:Boolean = commandToExecute is CairngormResponder;
        var responder:CairngormResponder;

		responder = isResponder ? CairngormResponder(commandToExecute) : new CairngormResponder;

        responder.event = event;
        responder.addEventListener(Event.COMPLETE, responderComplete);
		
        try {
            commandToExecute.execute(event);
            
            if (!isResponder) {
            	// assume that everything went ok
            	responder.result(null);
            } else if (responder.active) {
        		responder.asynchronous = true;
        		
        		dispatchEvent(new FrontControllerEvent(
        			FrontControllerEvent.ASYNCHRONOUS, responder));
        		
        		if (responder.event) {
	        		dispatchEvent(new FrontControllerEvent(
	        			responder.event.type + "Asynchronous", responder));
	        	}
        	}
        }
        catch(error:*) {
        	responder.fault(error);
        	
        	if (throwErrors)
        		throw error;
        }
    }
    
    /**
    * Handles responder COMPLETE event.
    * 
    * Dispatches generic COMPLETE event, responder specific event.type + "Complete" event
    * and calls dispatch specific function.
    */ 
    protected function responderComplete(event:Event):void {
    	var responder:CairngormResponder = CairngormResponder(event.currentTarget);
    	
    	responder.removeEventListener(Event.COMPLETE, responderComplete);    	
    	
    	dispatchEvent(new FrontControllerEvent(
			FrontControllerEvent.COMPLETE, responder));

		if (responder.event) {
	    	dispatchEvent(new FrontControllerEvent(
				responder.event.type + "Complete", responder));
	
			if (responder.event.completeHandlerFunction != null) {
				responder.event.completeHandlerFunction(responder);
			}
		}
    }
    
	/**
	 * Returns the command class registered with the command name. 
	 */
    protected function getCommand( commandName : String ) : Class
    {
        var command : Class = commands[ commandName ];
    
        if ( command == null )
            throw new CairngormError( CairngormMessageCodes.COMMAND_NOT_FOUND, commandName );
  
        return command;
    }
}   
}
    
 
 