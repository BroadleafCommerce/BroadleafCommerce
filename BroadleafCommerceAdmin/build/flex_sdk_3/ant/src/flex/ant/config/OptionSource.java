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

package flex.ant.config;

import org.apache.tools.ant.types.Commandline;

/**
 * Interface to describe an object that can represent a commandline argument.
 */
public interface OptionSource 
{
    /**
     * Adds the object's commandline representation to <code>cmdl</code>.
     */
    public void addToCommandline(Commandline cmdl);
}
