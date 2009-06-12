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

package mx.utils
{
    import flash.utils.ByteArray;
    
    /**
     * Implementation of SHA-256 hash algorithm as described in
     * Federal Information Processing Standards Publication 180-2
     * at http://csrc.nist.gov/publications/fips/fips180-2/fips180-2.pdf
     */
    public class SHA256
    {     
        /**
        *  Identifies this hash is of type "SHA-256".
        */
        public static const TYPE_ID:String = "SHA-256";
        
        private static var k:Array = 
                        [0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
                         0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
                         0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                         0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
                         0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                         0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                         0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
                         0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2];
                 
        
        /**
        * Computes the digest of a message using the SHA-256 hash algorithm.
        * 
        * @param byteArray - the message, may not be null.
        * 
        * return String - 64 character hexidecimal representation of the digest.
        * 
        */   
        public static function computeDigest(byteArray:ByteArray):String
        {
            // Preprocessing
            // 1. Pad the message
            var paddingLength:int = byteArray.length % 64;

            paddingLength = 64 - paddingLength;
            
            if (paddingLength < (1 + 8))
            {
                paddingLength += 64;   //  need to pad a partial block plus a full block
            }
            
            var messagePadding:Array = new Array(paddingLength);
            var n:int = (byteArray.length + paddingLength) / 64;        // number of message blocks
            
            var messageLengthBits:uint = byteArray.length * 8;
            messagePadding[0] = 128;
            
            // put message size in last 32 bits of the message padding
            var i:int;
            
            for (i = 1; i < paddingLength - 8; i++)
            {
                messagePadding[i] = 0;
            }

            var lastIndex:int = messagePadding.length - 1;        // last index of messagePadding
            for (i = 0; i < 4; i++)
            {
                messagePadding[lastIndex - i] = (messageLengthBits >> (i << 3)) & 0xff;
            }    
            

            // 2. Set initial hash H(0)
            var h0:int = 0x6a09e667;
            var h1:int = 0xbb67ae85;
            var h2:int = 0x3c6ef372;
            var h3:int = 0xa54ff53a;
            var h4:int = 0x510e527f;
            var h5:int = 0x9b05688c;
            var h6:int = 0x1f83d9ab;
            var h7:int = 0x5be0cd19;

            var a:int;
            var b:int;
            var c:int;
            var d:int;
            var e:int;
            var f:int;
            var g:int;
            var h:int;        
            
            // Hash computation
            // for all message blocks
            var m:ByteArray = new ByteArray();    // message block; 16 32-bit words or 64 bytes
            var w:Array = new Array(64);    // message schedule, 64 32-bit words
            var paddingStart:uint = 0;           // index to start padding message
            var paddingSize:uint = 0;            // amount of padding to copy to message
            var j:uint;
            var t1:int;                    // temporary storage in hash loop
            var t2:int;                    // temporary storage in hash loop
            var t:uint;
            var msgIndex:uint;
            var wt2:int;                   // w[t - 2]
            var wt15:int;                  // w[t -15]
            
            //var messageSchTime:int = 0;
            //var hashTime:int = 0;
            //var startTime:int;
            //var endTime:int;
            
            for (i = 0; i < n; i++)
            {
                // get the next message block of 512 bits or 64 bytes.
                getMessageBlock(byteArray, m);

                // append pass to end of last message block
                if (i == (n - 2) && messagePadding.length > 64)
                {
                    // pad end of message before last block
                    paddingStart = 64 - (messagePadding.length % 64);
                    paddingSize = 64 - paddingStart;
                    for (j = 0; j < paddingSize; j++)
                    {
                        m[j + paddingStart] = messagePadding[j];
                    }
                    
                }
                else if (i == n - 1)
                {
                    var prevPaddingSize:int = paddingSize;
                    if (messagePadding.length > 64)
                    {
                        paddingStart = 0;
                        paddingSize = 64;
                    }
                    else
					{
                        paddingStart = 64 - messagePadding.length;
                        paddingSize = messagePadding.length;
                    }
                    
                    for (j = 0; j < paddingSize; j++)
                    {
                        m[j + paddingStart] = messagePadding[j + prevPaddingSize];
                    }
                }
             
                // prepare the message schedule, w
                //startTime= getTimer();    
                for (t = 0; t < 64; t++)
                {
                    if (t < 16)
                    {
                        msgIndex = t << 2;
                        w[t] = int((m[msgIndex] << 24) | 
                               (m[msgIndex + 1] << 16) | 
                               (m[msgIndex + 2] << 8) | 
                                m[msgIndex + 3]);
                    }
                    else 
                    {
                       // inline functions to boost performance. keep orginal code for reference.
                       // w[t] = divisor1(w[t - 2]) + uint(w[t - 7]) + divisor0(w[t - 15]) + uint(w[t - 16]);
                        wt2 = w[t -2];
                        wt15 = w[t-15];   
                        w[t] = int(int((((wt2 >>> 17) | (wt2 << 15)) ^ ((wt2 >>> 19) | (wt2 << 13)) ^ (wt2 >>> 10))) + // divisor1(w[t - 2])
                               int(w[t - 7]) + 
                               int((((wt15 >>> 7) | (wt15 << 25)) ^ ((wt15 >>> 18) | (wt15 << 14)) ^ (wt15 >>> 3))) + // divisor0(w[t - 15])
                               int(w[t - 16]));
                    }
                }
                
                //endTime= getTimer();    
                //messageSchTime += endTime - startTime;
                
                //startTime= getTimer();    

                a = h0;
                b = h1;
                c = h2;
                d = h3;
                e = h4;
                f = h5;
                g = h6;
                h = h7;
                
                for (t = 0; t < 64; t++)
                {
                    // inline functions to boost performance. keep orginal code for reference.
                    //t1 = h + sum1(e) + Ch(e, f, g) + uint(k[t]) + uint(w[t]);
                    //t2 = sum0(a) + Maj(a, b, c);
                    t1 = h + 
                        int((((e >>> 6) | (e << 26)) ^ ((e >>> 11) | (e << 21)) ^ ((e >>> 25) | (e << 7)))) + //  sum1(e)
                        int(((e & f) ^ (~e & g))) + // Ch(e, f, g)
                        int(k[t]) + 
                        int(w[t]);
                    t2 = int((((a >>> 2) | (a << 30)) ^ ((a >>> 13) | (a << 19)) ^ ((a >>> 22) | (a << 10)))) + // sum0(a)
                         int(((a & b) ^ (a & c) ^ (b & c))); // Maj(a, b, c)
                        
                    h = g;
                    g = f;
                    f = e;
                    e = d + t1;
                    d = c;
                    c = b;
                    b = a;
                    a = t1 + t2;    

                    //trace("t = " + t + " a = " + uint(a).toString(16) + " b = " + uint(b).toString(16) + 
                    //      " c = " + uint(c).toString(16) + " d = " + uint(d).toString(16) + "\n");
                    //trace("t = " + t + " e = " + uint(e).toString(16) + " f = " + uint(f).toString(16) + 
                    //      " g = " + uint(g).toString(16) + " h = " + uint(h).toString(16) + "\n");

                }
                
                h0 += a;
                h1 += b;
                h2 += c;
                h3 += d;
                h4 += e;
                h5 += f;
                h6 += g;
                h7 += h;
              
                //endTime= getTimer();    
                //hashTime += endTime - startTime;

            }
            
            //trace("messageSchTime = " + messageSchTime);
            //trace("hashTime = " + hashTime);
            
            // final digest is h1 | h2 | h3 | h4 | h5 | h6 | h7
            // convert H(i) variables to hex strings and concatinate
            return toHex(h0) + toHex(h1) +
                   toHex(h2) + toHex(h3) +
                   toHex(h4) + toHex(h5) +
                   toHex(h6) + toHex(h7);
        }

        
        /**
        * get the next n bytes of the message from the byteArray and move it to the message block.
        *
        * @param byteArray - message
        * @param m - message block (output)
        */
        private static function getMessageBlock(byteArray:ByteArray, m:ByteArray):void
        {
            byteArray.readBytes(m, 0, Math.min(byteArray.bytesAvailable, 64));
            
//            for (var i:int; i < length && (i + startingIndex) < byteArray.length; i++)
//            {
//                m[i] = byteArray[i + startingIndex];
//            }
        }
        
        private static function toHex(n:uint):String
        {
            var s:String = n.toString(16);
           
            if (s.length < 8)
            {
                // add leading zeros
                var zeros:String = "0";
                var count:int = 8 - s.length;
                for (var i:int = 1; i < count; i++)
                {
                    zeros = zeros.concat("0");
                }
                
                return zeros + s;
            }
            
            return s;
        }
        
        // The below functions are defined in Federal Information
        // Processing Standards Publication 180-2
        // at 
        // http://csrc.nist.gov/publications/fips/fips180-2/fips180-2.pdf
        
/*      The functions have been inlined to boost performance. They are 
        kept here for reference.
        
        private static function Ch(x:uint, y:uint, z:uint):uint
        {
            return (x & y) ^ (~x & z);
        }
        
        private static function  Maj(x:uint, y:uint, z:uint):uint
        {
            return (x & y) ^ (x & z) ^ (y & z);
        }
        
        private static function sum0(x:uint):uint
        {
            return ((x >>> 2) | (x << 30)) ^ ((x >>> 13) | (x << 19)) ^ ((x >>> 22) | (x << 10));
            //return rotr(2, x) ^ rotr(13, x) ^ rotr(22, x);
        }
        
        private static function sum1(x:uint):uint
        {
            return ((x >>> 6) | (x << 26)) ^ ((x >>> 11) | (x << 21)) ^ ((x >>> 25) | (x << 7));
//            return rotr(6, x) ^ rotr(11, x) ^ rotr(25, x);
        }
        
        private static function divisor0(x:uint):uint
        {
             return ((x >>> 7) | (x << 25)) ^ ((x >>> 18) | (x << 14)) ^ (x >>> 3);
//             return rotr(7, x) ^ rotr(18, x) ^ shr(3, x);
        }

        private static function divisor1(x:uint):uint
        {
             return ((x >>> 17) | (x << 15)) ^ ((x >>> 19) | (x << 13)) ^ (x >>> 10);
//             return rotr(17, x) ^ rotr(19, x) ^ shr(10, x);
        }
        
        private static function rotr(n:uint, x:uint):uint
        {
            return (x >>> n) | (x << 32 - n);
        }
        
        private static function shr(n:uint, x:uint):uint
        {
            return x >>> n;
        }
*/
    }
}