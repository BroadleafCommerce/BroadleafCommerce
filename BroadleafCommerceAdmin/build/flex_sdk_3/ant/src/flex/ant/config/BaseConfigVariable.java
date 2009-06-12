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
 * Provides a base class for <code>ConfigVariable</code> and
 * <code>RepeatableConfigVariable</code>. This abstract class encapsulates all
 * of the functionality that any ConfigVariable must have that does not
 * involve "setting" it.
 *
 * Consumers of this class must implement the <code>addToCommandline</code>
 * method.
 */
public abstract class BaseConfigVariable implements OptionSource
{
    /**
     * The <code>OptionSpec</code> describing the names that this <code>ConfigVariable</code> should match.
     */
    protected final OptionSpec spec;

    /**
     * Create a Configuration Variable with the specified <code>OptionSpec</code>.
     */
    protected BaseConfigVariable(OptionSpec spec)
    {
        this.spec = spec;
    }

    /**
     * Adds arguments to the end of <code>cmdl</code> corresponding to the state of this variable.
     *
     * @param cmld The Commandline object to which arguments correspond to this option should be added
     */
    public abstract void addToCommandline(Commandline cmdl);

    /**
     * @return the OptionSpec associated with this instance.
     */
    public OptionSpec getSpec()
    {
        return spec;
    }

    /**
     * Returns the result of calling matches() on <code>spec</code> with <code>option</code> as the argument.
     *
     * @return true of <code>option</code> matches <code>spec</code>, and false otherwise.
     */
    public boolean matches(String option)
    {
        return spec.matches(option);
    }
}
