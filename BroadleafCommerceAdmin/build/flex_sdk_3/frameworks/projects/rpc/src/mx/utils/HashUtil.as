////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.utils
{

[ExcludeClass]

/**
 * @private
 *
 *  Provides several hash implementations, RS, AP, etc.
 */
public class HashUtil
{

   public static function rsHash(value:String):Number
   {
      var a:int = 63689;
      var b:int = 378551;
      var result:Number;

      for (var i:int = 0; i < value.length; i++)
      {
         result = (result * a) + value.charCodeAt(i);
         a = a * b;
      }

      return (result & 0x7FFFFFFF);
   }

   public static function jsHash(value:String):Number
   {
      var result:Number = 1315423911;

      for (var i:int = 0; i < value.length; i++)
      {
         result ^= ((result << 5) + value.charCodeAt(i) + (result >> 2));
      }

      return (result & 0x7FFFFFFF);
   }

   public static function apHash(value:String):Number
   {
      var result:int;

      for (var i:int = 0; i < value.length; i++)
      {

         if ((i & 1) == 0)
         {
            result ^= ((result << 7)^value.charCodeAt(i)^(result >> 3));
         }
         else
         {
            result ^= (~((result << 11)^value.charCodeAt(i)^(result >> 5)));
         }

      }

      return (result & 0x7FFFFFFF);

   }

   public static function dbjHash(value:String):Number
   {
      var result:Number = 5381;

      for (var i:int = 0; i < value.length; i++)
      {
         result = ((result << 5) + result) + value.charCodeAt(i);
      }

      return (result & 0x7FFFFFFF);
   }
}

}