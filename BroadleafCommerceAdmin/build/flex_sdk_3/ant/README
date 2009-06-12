################################################################################
#                                                                              #
# README - flexTasks                                                           #
#                                                                              #
################################################################################

Contents
  I.    Installation
  II.   Using flexTasks
    A.  mxmlc

###################


  I.    Installation

Place flexTasks.jar into the lib directory of your Ant installation.
Alternatively, you can specify the location of the JAR file as an
argument to Ant, as the follow example shows:

  ant -lib <some-path>/flexTasks.jar .

In addition to this, you must include the following line in any build
file that uses the mxmlc or asc tasks:

  <taskdef resource="flexTasks.tasks" /> .

The build tools that you want to use (such as mxmlc or asc) must be in
your PATH. Unlike Ant, these tasks do not search your current
directory for executables unless the current directory appears in your
PATH (either explicitly or by including ".").

#########

  II.   Using flexTasks

#####

    A.  mxmlc

FlexBuild exposes all of the command-line options of mxmlc through the
attributes and nested elements of an <mxmlc> task. The full name and
abbreviated name of a command line option can be used interchangably
when the option is implemented as an attribute.

The following examples are both acceptable ways to pass the compiler.as3 option 
to mxmlc:

  <mxmlc file="foo.mxml" compiler.as3="true" />

and

  <mxmlc file="foo.mxml" as3="true" />

All boolean options are implemented as attributes of the <mxmlc> element.

All options that take a single argument are also implemented as attributes of
the <mxmlc> element. The descriptions of these types of options vary in the 
mxmlc documentation. If an option is documented as taking a <string>,
<filename>, <int>, or some sort of path element, and that option is non-
repeatable, then this option is set by setting an attribute in the <mxmlc>
element.

Options that are repeatable, or take more than one argument (such as 
default-size), are implemented as nested elements with attributes corresponding
to the names given to arguments in the mxmlc documentation. For example, if you
want to pass the option -default-size 800 600 to mxmlc, use the following
syntax:

<mxmlc file="foo.mxmlc">
    <default-size width="800" height="600" />
</mxmlc>

Do not include multiple nested elements corresponding to a non-repeatable option.

The following two nested elements can contain nested elements: 
 - <fonts>
 - <metadata>

These elements encapsulate all options starting with "compiler.fonts" and "metadata", 
repectively. The same rules that apply to other options apply to nested elements.
The following example includes contributors names and a description of the application:

<mxmlc file="foo.mxmlc">
    <metadata description="foo app">
        <contributor name="Joe" />
        <contributor name="Mike" />
    </metadata>
</mxmlc>.

There are some exceptions to the rules states above: 

- The compiler.fonts.languages.language-range option is set by
adding a <language-range> nested element to <fonts>, rather than a 
<languages.language-range> element.

- The following options (repeatable options that take a path-element) are 
implemented as FileSets:

-compiler.external-library-path
-compiler.include-libraries
-compiler.library-path
-compiler.theme

The following example shows the usage for external-library-path:

<compiler.external-library-path dir="${lib.dir}">
    <include name="**/*.swc" />
    <exclude name="not-this-one.swc" />
<compiler.external-library-path>

<compiler.theme file="${src.dir}/foo.mxml" />

To use these options, append the files that the FileSet chooses to the compiler
defaults and set the append attribute of the FileSet to true.

- The file-spec option is not supported. Instead, use the file attribute of
the mxmlc task.


################################################################################
#                                                                              #
# End of README                                                                #
#                                                                              #
################################################################################
