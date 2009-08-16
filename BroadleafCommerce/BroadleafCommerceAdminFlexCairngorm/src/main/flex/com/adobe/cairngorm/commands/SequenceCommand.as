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
   import com.adobe.cairngorm.control.CairngormEventDispatcher;

   /**
    * The SequenceCommand is provided as a "psuedo-abstract" (since ActionScript
    * has no real concept of abstract classes) base-class that can be extended when
    * you wish to chain commands together for a single user-gesture, or establish
    * some simple form of decision-based workflow.
    * 
    * <p>
    * By extending SequenceCommand, you can specify the event that should be 
    * broadcast to the controller (causing another command execution
    * without a further user-gesture) when the current command has completed
    * execution.
    * </p>
    *
    * <p>
    * For a command implementing the Responder interface, you may choose to
    * sequence a subsequent command on successful completion of the command,
    * in the onResult() handler, or on failure of the command in the onFault()
    * method.
    * </p>
    *
    * <p>
    * For commands that do not implement the Responder interface, you can 
    * simply chain commands by causing the sequenced command to be invoked
    * as the last action in your command's execute() method.
    * </p>
    *
    * <p>
    * <b>Usage</b>
    * </p>
    *
    * <p>
    * In the constructor of a concrete SequenceCommand implementation, you
    * should set nextEvent to the event that is responsible for calling your 
    * subsequent command.
    * </p>
    * 
    * <p>
    * Alternatively, you can override the implicit nextEvent getter, to 
    * programmatically decide at runtime which event should be broadcast
    * next.
    * </p>
    *
    * <p>
    * Invocation of the next command in the sequence is explicitly controlled by
    * the developer, by calling the executeNextCommand() method provided in the
    * SequenceCommand base-class.  This can be called either in the body of the execute() method
    * (for synchronous sequencing) or in the body of an onResult() or onFault()
    * handler (for asynchronous sequencing, that can also support conditional
    * workflow).
    * </p>
    *
    * @see com.adobe.cairngorm.commands.ICommand
    * @see com.adobe.cairngorm.control.CairngormEventDispatcher
    */
   public class SequenceCommand implements ICommand 
   {      
      /**
       * The next event in the sequence.
       */
      public var nextEvent : CairngormEvent;       
       
       /**
       * Constructor, with optional nextEvent.
       */
      public function SequenceCommand( nextEvent : CairngormEvent = null ) : void
      {
         super();
         this.nextEvent = nextEvent;
      }
             
      /** 
       * Abstract implementation of the execute() method.
       *
       * <p>ActionScript does not explicity support abstract methods and abstract classes, so this concrete 
       * implementation of the interface method must be overridden by the developer.</p>
       * 
       */ 
      public function execute( event : CairngormEvent ) : void 
      {
          // abstract, so this method must be provided.   Rather than convolute additional framework classes to enforce
          // abstract classes, we instead delegate responsibility to the developer of a SequenceCommand to ensure that
          // they provide a concrete implementation of this method.
      }
      
      /**
       * Call to execute the next command in the sequence.
       * 
       * <p>Called explicitly by the developer within a concrete SequenceCommand implementation, this method causes the
       * event registered with nextEvent to be broadcast, for the next command in the sequence to be called 
       * without further user-gesture.</p>
       * 
       */ 
      public function executeNextCommand() : void
      {
         var isSequenceCommand : Boolean = ( nextEvent != null );
         if( isSequenceCommand )
            CairngormEventDispatcher.getInstance().dispatchEvent( nextEvent );
      }
   }
}