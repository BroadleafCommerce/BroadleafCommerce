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

package flex.ant.types;

import flex.ant.config.ConfigBoolean;
import flex.ant.config.ConfigString;
import flex.ant.config.ConfigVariable;
import flex.ant.config.NestedAttributeElement;
import flex.ant.config.OptionSpec;
import flex.ant.config.OptionSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.types.Commandline;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public final class Fonts implements OptionSource, DynamicConfigurator
{
    /*
     * Use this defintion of lrSpec if you want to allow users to set the
     * compiler.fonts.languages.language-range by using a nested element named
     * languages.language-range:
     *
     * private static OptionSpec lrSpec = new OptionSpec("compiler.fonts.languages.language-range", "languages.language-range");
     *
     * Note that using this will no longer allow users to set the option by
     * using a language-range nested element.
     */
    private static OptionSpec lrSpec = new OptionSpec("compiler.fonts.languages", "language-range");
    private static OptionSpec maSpec = new OptionSpec("compiler.fonts", "managers");

    private final ConfigVariable[] attribs;

    private final ArrayList nestedAttribs;

    public Fonts()
    {
        attribs = new ConfigVariable[] {
            new ConfigBoolean(new OptionSpec("compiler.fonts", "flash-type")),
            new ConfigBoolean(new OptionSpec("compiler.fonts", "advanced-anti-aliasing")),
            new ConfigString(new OptionSpec("compiler.fonts", "local-fonts-snapshot")),
            new ConfigString(new OptionSpec("compiler.fonts", "max-cached-fonts")),
            new ConfigString(new OptionSpec("compiler.fonts", "max-glyphs-per-face"))
        };

        nestedAttribs = new ArrayList();
    }

    /*=======================================================================*
     *  Attributes                                                           *
     *=======================================================================*/

    public void setDynamicAttribute(String name, String value)
    {
        ConfigVariable var = null;

        for (int i = 0; i < attribs.length && var == null; i++) {
            if (attribs[i].matches(name))
                var = attribs[i];
        }

        if (var != null)
            var.set(value);
        else
            throw new BuildException("The <font> type doesn't support the \""
                                     + name + "\" attribute.");
    }

    /*=======================================================================*
     *  Nested Elements                                                      *
     *=======================================================================*/

    public Object createDynamicElement(String name)
    {
        if (lrSpec.matches(name)) {
            NestedAttributeElement e = new NestedAttributeElement(new String[] { "lang", "range" }, lrSpec);
            nestedAttribs.add(e);
            return e;
        }
        else {
            throw new BuildException("Invalid element: " + name);
        }
    }

    public NestedAttributeElement createManager()
    {
        NestedAttributeElement e = new NestedAttributeElement("class", maSpec);
        nestedAttribs.add(e);
        return e;
    }

    /*=======================================================================*
     *  OptionSource interface                                               *
     *=======================================================================*/

    public void addToCommandline(Commandline cmdl)
    {
        for (int i = 0; i < attribs.length; i++)
            attribs[i].addToCommandline(cmdl);

        Iterator it = nestedAttribs.iterator();

        while (it.hasNext())
            ((OptionSource) it.next()).addToCommandline(cmdl);
    }

} //End of Fonts
