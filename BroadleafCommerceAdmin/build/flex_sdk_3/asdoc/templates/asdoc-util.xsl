<?xml version="1.0" encoding="utf-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:date="http://exslt.org/dates-and-times" exclude-result-prefixes="exslt date str"
	xmlns:str="http://exslt.org/strings"
	xmlns:exslt="http://exslt.org/common" >

	<!-- <xsl:output method="html" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" encoding="UTF-8" indent="yes"/> -->
	<xsl:output method="html" encoding="UTF-8" indent="yes" />	
	<xsl:variable name="newline">
		<xsl:text>
</xsl:text>
	</xsl:variable>
	<xsl:variable name="markOfTheWeb" select="'&lt;!-- saved from url=(0014)about:internet -->'" />
	<xsl:variable name="docType">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"></xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:if test="$config/options[@standalone='true']">
			<xsl:value-of disable-output-escaping="yes" select="$markOfTheWeb" />
			<xsl:value-of select="$newline" />
		</xsl:if>
	</xsl:variable>	
	<xsl:variable name="frameDocType">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd"></xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:if test="$config/options[@standalone='true']">
			<xsl:value-of  disable-output-escaping="yes" select="$markOfTheWeb" />
			<xsl:value-of select="$newline" />
		</xsl:if>
	</xsl:variable>
	<xsl:param name="configFilename" select="'ASDoc_Config.xml'"/>
	<xsl:variable name="config" select="document($configFilename)/asDocConfig"/>
	<xsl:param name="packageCommentsFilename" select="'packages.xml'"/>
	<xsl:param name="AS1tooltip" select="'This example can be used with ActionScript 1.0'" />
	<xsl:param name="AS2tooltip" select="'This example requires ActionScript 2.0'" />
	<xsl:param name="AS3tooltip" select="'This example requires ActionScript 3.0'" />
	<xsl:param name="showASIcons" select="'false'" />
	<xsl:param name="showInheritanceIcon" select="'true'" />
	<xsl:param name="inheritanceIcon" select="'inherit-arrow.jpg'" />
	<xsl:param name="isEclipse" select="$config/options[@eclipse='true']" />
	<xsl:param name="isLiveDocs" select="$config/options[@livedocs='true']" />
	<xsl:param name="isStandalone" select="$config/options[@standalone='true']" />
	<xsl:param name="liveDocsSearchSite" select="$config/liveDocsSearchSite/." />
	<xsl:param name="showLangVersionWarnings">
		<xsl:if test="$config/warnings/@langversion='true'">
			<xsl:value-of select="'true'"/>
		</xsl:if>
	</xsl:param>
	<xsl:param name="showPlayerVersionWarnings">
		<xsl:if test="$config/warnings/@playerversion='true'">
			<xsl:value-of select="'true'"/>
		</xsl:if>
	</xsl:param>
	<xsl:param name="noLiveDocs">
		<xsl:if test="$config/options[@livedocs='true']">
			<xsl:comment>livedocs:no</xsl:comment>
			<xsl:value-of select="$newline" />
		</xsl:if>
	</xsl:param>
	<xsl:param name="showXrefs" select="$config/xrefs[@show='true']" />
	<xsl:variable name="xrefs">
		<xsl:if test="$showXrefs">
			<xsl:copy-of select="document($config/xrefs/@mapfile)/helpreferences" />
		</xsl:if>
	</xsl:variable>
	<xsl:param name="title-base" select="$config/windowTitle/."/>
	<xsl:param name="page-title-base" select="$config/title/."/>
	<xsl:param name="timestamp">
 		<xsl:value-of select="date:format-date(date:date-time(),'EEE MMM d yyyy, h:mm a z')"/>
<!--		<xsl:if test="string-length(date:hour-in-day())=1">
			<xsl:text>0</xsl:text>
		</xsl:if>
		<xsl:value-of select="date:hour-in-day()"/>
		<xsl:text>:</xsl:text>
		<xsl:if test="string-length(date:minute-in-hour())=1">
			<xsl:text>0</xsl:text>
		</xsl:if>
		<xsl:value-of select="date:minute-in-hour()"/>
		<xsl:text> PDT</xsl:text>-->
	</xsl:param>
	<xsl:param name="copyright">
		<xsl:value-of disable-output-escaping="no" select="$config/footer"/>
		<!-- The timestamp causes problems with xalan, so it has been commented-out for Flex
        <br/>
		<xsl:value-of select="$timestamp" />
		<xsl:text> </xsl:text>          -->
	</xsl:param>
	<xsl:variable name="copyrightComment">
		<xsl:comment>
			<xsl:copy-of select="$copyright"/>
		</xsl:comment>
	</xsl:variable>
	<xsl:variable name="upperCase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
	<xsl:variable name="lowerCase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
	<xsl:variable name="emdash">
		<xsl:text> &#x2014; </xsl:text>
	</xsl:variable>
	<xsl:variable name="asterisk">
		<xsl:text>&#x2A;</xsl:text>
	</xsl:variable>
	<xsl:variable name="nbsp">
		<xsl:text>&#xA0;</xsl:text>
	</xsl:variable>
	<xsl:variable name="degree">
		<xsl:text>&#xB0;</xsl:text>
	</xsl:variable>
	<xsl:variable name="trademark">
		<xsl:text>&#x2122;</xsl:text>
	</xsl:variable>
	<xsl:variable name="registered">
		<xsl:text>&#xAE;</xsl:text>
	</xsl:variable>

	<xsl:template name="getBaseRef">
		<xsl:param name="packageName"/>

		<xsl:variable name="isTopLevel">
			<xsl:call-template name="isTopLevel">
				<xsl:with-param name="packageName" select="$packageName"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$isTopLevel='false'">
			<xsl:variable name="newName" select="substring-after($packageName,'.')"/>
			<xsl:if test="$packageName">
				<xsl:text>../</xsl:text>
				<xsl:call-template name="getBaseRef">
					<xsl:with-param name="packageName" select="$newName"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getStyleLink">
		<xsl:param name="link"/>
		<xsl:param name="packageName"/>

		<xsl:choose>
			<!-- TODO support this? -->
			<xsl:when test="false()">
				<!--(link)">-->
				<xsl:for-each select="$link">
					<xsl:copy-of select="."/>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="baseRef">
					<xsl:call-template name="getBaseRef">
						<xsl:with-param name="packageName" select="$packageName"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:element name="link">
					<xsl:attribute name="rel">stylesheet</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:value-of select="$baseRef"/>style.css</xsl:attribute>
					<xsl:attribute name="type">text/css</xsl:attribute>
					<xsl:attribute name="media">screen</xsl:attribute>
				</xsl:element>
				<xsl:element name="link">
					<xsl:attribute name="rel">stylesheet</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:value-of select="$baseRef"/>print.css</xsl:attribute>
					<xsl:attribute name="type">text/css</xsl:attribute>
					<xsl:attribute name="media">print</xsl:attribute>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getTitleScript">
		<xsl:param name="packageName"/>
		<xsl:param name="title" select="$title-base"/>

		<xsl:variable name="baseRef">
			<xsl:call-template name="getBaseRef">
				<xsl:with-param name="packageName" select="$packageName"/>
			</xsl:call-template>
		</xsl:variable>
		<script language="javascript" type="text/javascript">
			<xsl:attribute name="src">
				<xsl:value-of select="$baseRef"/>
				<xsl:text>asdoc.js</xsl:text>
			</xsl:attribute>
		</script>
		<xsl:if test="$isEclipse">
			<script language="javascript" type="text/javascript">
				<xsl:comment>
					eclipseBuild = true;
				</xsl:comment>
			</script>
		</xsl:if>
		<script language="javascript" type="text/javascript">
			<xsl:attribute name="src">
				<xsl:value-of select="$baseRef"/>
				<xsl:text>cookies.js</xsl:text>
			</xsl:attribute>
		</script>
		<script language="javascript" type="text/javascript">
			<xsl:comment>
				asdocTitle = '<xsl:value-of select="$title" />';
				var baseRef = '<xsl:value-of select="$baseRef" />';
				window.onload = configPage;
			</xsl:comment>
		</script>
	</xsl:template>

	<xsl:template name="getLinks">
		<xsl:param name="packageName" select="''"/>
		<xsl:param name="fileName"/>
		<xsl:param name="fileName2" select="'all-classes.html'" />
		<xsl:param name="showInnerClasses" select="false()"/>
		<xsl:param name="showConstants" select="false()" />
		<xsl:param name="showProperties" select="true()"/>
		<xsl:param name="showConstructors" select="false()" />
		<xsl:param name="showMethods" select="true()"/>
		<xsl:param name="showStyles" select="false()"/>
		<xsl:param name="showEffects" select="false()"/>
		<xsl:param name="showEvents" select="false()"/>
		<xsl:param name="showIncludeExamples" select="false()"/>
		<xsl:param name="showPackages" select="true()"/>
		<xsl:param name="showAllClasses" select="true()"/>
		<xsl:param name="showLanguageElements" select="boolean($config/languageElements[@show='true'])"/>		
		<xsl:param name="showIndex" select="true()"/>
		<xsl:param name="showAppendixes" select="true()" />
		<xsl:param name="showPackageConstants" select="false()"/>
		<xsl:param name="showPackageProperties" select="false()"/>
		<xsl:param name="showPackageFunctions" select="false()"/>
		<xsl:param name="showInterfaces" select="false()"/>
		<xsl:param name="showClasses" select="false()"/>
		<xsl:param name="showPackageUse" select="false()" />
		<xsl:param name="copyNum" select="'1'"/>
		<xsl:param name="additionalLinks"/>
		<xsl:param name="splitIndex" select="$config/options[@splitIndex='true']" />
		<xsl:param name="showMXMLOnly" select="boolean($config/options[@showMXMLOnly='true'])" />
		<xsl:param name="showConventions" select="boolean($config/options[@showConventions!='false'])" />

		<xsl:variable name="baseRef">
			<xsl:call-template name="getBaseRef">
				<xsl:with-param name="packageName" select="$packageName"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="href">
			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="$packageName"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$isTopLevel='false'">
				<xsl:value-of select="translate($packageName,'.','/')"/>
				<xsl:text>/</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$copyNum='1'">
			<div class="pageTop"></div>
			<table width="100%" cellpadding="0" cellspacing="0" id="titleTable" style="display:none">
				<tr>
					<td valign="left" width="64" style="padding-left:5px">
						<img src="{$baseRef}images/mm-icon.jpg" border="0" alt="Macromedia Logo" title="Macromedia Logo"/>
					</td>
					<td align="center" valign="middle">
						<xsl:variable name="fontSize">
							<xsl:if test="string-length($config/title/@size)">
								<xsl:value-of select="$config/title/@size" />
							</xsl:if>
							<xsl:if test="not(string-length($config/title/@size))">
								<xsl:value-of select="24" />
							</xsl:if>
						</xsl:variable>
						<h1 style="font-size:{$fontSize}px"><xsl:value-of select="$title-base"/></h1>
					</td>
				</tr>
				<tr>
					<td colspan="2" height="5px"></td>
				</tr>
			</table>
		</xsl:if>
<!-- 			<xsl:if test="$config/options/@authoring='true' and $copyNum='1'">
				<tr>
					<td width="100%" colspan="2">
						<center>
							<xsl:text>This is a production release of the ActionScript 2.0 Language Reference. For the latest ActionScript Language Reference content, go to: </xsl:text>
							<a href="http://www.macromedia.com/go/maelstrom_AS2LR/{$href}{$fileName}.html" target="mm_temp">http://www.macromedia.com/go/maelstrom_AS2LR</a>.
						</center>
					</td>
				</tr>
				<tr>
					<td width="100%" height="20px">
						<xsl:text> </xsl:text>
					</td>
				</tr>
			</xsl:if> -->
		<xsl:if test="$copyNum=1">
			<xsl:call-template name="getNavLinks">
				<xsl:with-param name="copyNum" select="$copyNum" />
				<xsl:with-param name="baseRef" select="$baseRef" />
				<xsl:with-param name="showPackages" select="$showPackages" />
				<xsl:with-param name="showAllClasses" select="$showAllClasses" />
				<xsl:with-param name="showLanguageElements" select="$showLanguageElements" />
				<xsl:with-param name="showMXMLOnly" select="$showMXMLOnly" />
				<xsl:with-param name="showIndex" select="$showIndex" />
				<xsl:with-param name="splitIndex" select="$splitIndex" />
				<xsl:with-param name="showAppendixes" select="$showAppendixes" />
				<xsl:with-param name="showConventions" select="$showConventions" />
				<xsl:with-param name="href" select="$href" />
				<xsl:with-param name="fileName" select="$fileName" />
				<xsl:with-param name="fileName2" select="$fileName2" />
			</xsl:call-template>
		</xsl:if>
		<div width="100%" class="topLinks" align="right">
			<span>
				<xsl:if test="$showProperties">
					<a href="#propertySummary">Properties</a>
					<xsl:if test="$showPackageProperties or $showConstructors or $showMethods or $showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showPackageProperties">
					<a href="package.html#propertySummary">Properties</a>
					<xsl:if test="$showConstructors or $showMethods or $showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showConstructors">
					<a href="#constructorSummary">Constructor</a>
					<xsl:if test="$showMethods or $showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>					
				<xsl:if test="$showMethods">
					<a href="#methodSummary">Methods</a>
					<xsl:if test="$showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showPackageFunctions">
					<a href="package.html#methodSummary">Functions</a>
					<xsl:if test="$showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showEvents">
					<a href="#eventSummary">Events</a>
					<xsl:if test="$showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>	
				<xsl:if test="$showStyles">
					<a href="#styleSummary">Styles</a>
					<xsl:if test="$showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showEffects">
					<a href="#effectSummary">Effects</a>
					<xsl:if test="$showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showConstants">
					<a href="#constantSummary">Constants</a>
					<xsl:if test="$showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showPackageConstants">
					<a href="package.html#constantSummary">Constants</a>
					<xsl:if test="$showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showInterfaces">
					<a href="package-detail.html#interfaceSummary">Interfaces</a>
					<xsl:if test="$showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showClasses">
					<a href="package-detail.html#classSummary">Classes</a>
					<xsl:if test="$showPackageUse or $showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showPackageUse">
					<a href="package-use.html">Use</a>
					<xsl:if test="$showIncludeExamples or $additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$showIncludeExamples">
					<a href="#includeExamplesSummary">Examples</a>
					<xsl:if test="$additionalLinks">
						<xsl:text>&#xA0;| </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$additionalLinks">
					<xsl:copy-of select="$additionalLinks"/>
				</xsl:if>
			</span>
		</div>
		<xsl:if test="$copyNum=2">
			<xsl:call-template name="getNavLinks">
				<xsl:with-param name="copyNum" select="$copyNum" />
				<xsl:with-param name="baseRef" select="$baseRef" />
				<xsl:with-param name="showPackages" select="$showPackages" />
				<xsl:with-param name="showAllClasses" select="$showAllClasses" />
				<xsl:with-param name="showLanguageElements" select="$showLanguageElements" />
				<xsl:with-param name="showMXMLOnly" select="$showMXMLOnly" />
				<xsl:with-param name="showIndex" select="$showIndex" />
				<xsl:with-param name="splitIndex" select="$splitIndex" />
				<xsl:with-param name="showAppendixes" select="$showAppendixes" />
				<xsl:with-param name="showConventions" select="$showConventions" />
				<xsl:with-param name="href" select="$href" />
				<xsl:with-param name="fileName" select="$fileName" />
				<xsl:with-param name="fileName2" select="$fileName2" />
			</xsl:call-template>
		</xsl:if>
<!-- 			<xsl:if test="$config/options/@authoring='true' and $copyNum='2'">
				<tr>
					<td width="100%" height="20px">
						<xsl:text> </xsl:text>
					</td>
				</tr>
				<tr>
					<td width="100%" colspan="3">
						<center>
							<xsl:text>This is a production release of the ActionScript 2.0 Language Reference. For the latest ActionScript Language Reference content, go to: </xsl:text>
							<a href="http://www.macromedia.com/go/maelstrom_AS2LR/{$href}{$fileName}.html" target="mm_temp">http://www.macromedia.com/go/maelstrom_AS2LR</a>.
						</center>
					</td>
				</tr>
				<tr>
					<td width="100%" height="20px">
						<xsl:text> </xsl:text>
					</td>
				</tr>
				<tr>
					<td width="100%" colspan="3">
						<center>
							<a href="http://www.macromedia.com/go/maelstrom_AS2LR/{$href}{$fileName}.html" target="mm_window">View comments on LiveDocs</a>
						</center>
					</td>
				</tr>
			</xsl:if> -->
	</xsl:template>

	<xsl:template name="setTitle">
			<xsl:value-of disable-output-escaping="yes" select="$page-title-base" />
    </xsl:template>

	<xsl:template name="getLinks2">
		<xsl:param name="subTitle" select="$nbsp" />
		<xsl:param name="packageName" select="''" />
		<xsl:param name="fileName"/>
		<xsl:param name="fileName2" select="'all-classes.html'" />
		<xsl:param name="showConstants" select="false()" />
		<xsl:param name="showProperties" select="true()"/>
		<xsl:param name="showConstructors" select="false()" />
		<xsl:param name="showMethods" select="true()"/>
		<xsl:param name="showStyles" select="false()"/>
		<xsl:param name="showEffects" select="false()"/>
		<xsl:param name="showEvents" select="false()"/>
		<xsl:param name="showIncludeExamples" select="false()"/>
		<xsl:param name="showPackages" select="true()"/>
		<xsl:param name="showAllClasses" select="true()"/>
		<xsl:param name="showLanguageElements" select="boolean($config/languageElements[@show='true'])"/>		
		<xsl:param name="showIndex" select="true()"/>
		<xsl:param name="showAppendixes" select="boolean($config/appendixes[@show='true'])" />
		<xsl:param name="showPackageConstants" select="false()"/>
		<xsl:param name="showPackageProperties" select="false()"/>
		<xsl:param name="showPackageFunctions" select="false()"/>
		<xsl:param name="showInterfaces" select="false()"/>
		<xsl:param name="showClasses" select="false()"/>
		<xsl:param name="showPackageUse" select="false()" />
		<xsl:param name="copyNum" select="'1'"/>
		<xsl:param name="additionalLinks"/>
		<xsl:param name="splitIndex" select="$config/options[@splitIndex='true']" />
		<xsl:param name="showConventions" select="boolean($config/options[@showConventions!='false'])" />

		<xsl:variable name="baseRef">
			<xsl:call-template name="getBaseRef">
				<xsl:with-param name="packageName" select="$packageName"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="href">
			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="$packageName"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$isTopLevel='false'">
				<xsl:value-of select="translate($packageName,'.','/')"/>
				<xsl:text>/</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$copyNum='1'">
			<xsl:if test="not($config/options[@eclipse='true'])">
				<table class="titleTable" cellpadding="0" cellspacing="0" id="titleTable" style="display:none">
					<tr>
						<td class="titleTableTitle" align="left">
							<xsl:value-of disable-output-escaping="yes" select="$page-title-base" />				
						</td>
						<xsl:if test="$isLiveDocs">
							<td class="titleTableSearch" align="center">

								<form class="searchForm" target="adbe_window" method="get" action="/cfusion/search/index.cfm" onsubmit='this.term.value = this.termPrefix.value + "\"" + this.search_text.value + "\"";'>
									<input class="hidden" name="loc" value="en_US" type="hidden" />
									<input class="hidden" name="termPrefix" value="{$liveDocsSearchSite}" type="hidden" />
									<input class="hidden" name="term" value="" type="hidden" />
									<input class="hidden" name="area" value="" type="hidden" />
									<input id="search-livedocs" name="search_text" value="" title="Search LiveDocs" type="text" />
									<xsl:text> </xsl:text>
									<input type="submit" name="action" value="Search" />
								</form>
							</td>
						</xsl:if>
						<td class="titleTableTopNav" align="right">
							<xsl:call-template name="getNavLinks2">
								<xsl:with-param name="copyNum" select="$copyNum" />
								<xsl:with-param name="baseRef" select="$baseRef" />
								<xsl:with-param name="showPackages" select="$showPackages" />
								<xsl:with-param name="showAllClasses" select="$showAllClasses" />
								<xsl:with-param name="showLanguageElements" select="$showLanguageElements" />
								<xsl:with-param name="showIndex" select="$showIndex" />
								<xsl:with-param name="splitIndex" select="$splitIndex" />
								<xsl:with-param name="showAppendixes" select="$showAppendixes" />
								<xsl:with-param name="showConventions" select="$showConventions" />
								<xsl:with-param name="href" select="$href" />
								<xsl:with-param name="fileName" select="$fileName" />
								<xsl:with-param name="fileName2" select="$fileName2" />
							</xsl:call-template>
						</td>
						<td class="titleTableLogo" align="right" rowspan="3">
							<img src="{$baseRef}images/logo.jpg" class="logoImage" title="Adobe Logo" alt="Adobe Logo" />
						</td>
					</tr>
					<tr class="titleTableRow2">
						<td class="titleTableSubTitle" id="subTitle" align="left">
							<xsl:if test="string-length($subTitle)">
								<xsl:value-of select="$subTitle" />
							</xsl:if>
							<xsl:if test="not(string-length($subTitle))">
								<xsl:value-of select="$nbsp" />
							</xsl:if>
						</td>
						<td class="titleTableSubNav" id="subNav" align="right">		
							<xsl:if test="$isLiveDocs">
								<xsl:attribute name="colspan">
									<xsl:text>2</xsl:text>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="$showProperties">
								<a href="#propertySummary">Properties</a>
								<xsl:if test="$showPackageProperties or $showConstructors or $showMethods or $showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showPackageProperties">
								<a href="package.html#propertySummary">Properties</a>
								<xsl:if test="$showConstructors or $showMethods or $showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showConstructors">
								<a href="#constructorSummary">Constructor</a>
								<xsl:if test="$showMethods or $showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>					
							<xsl:if test="$showMethods">
								<a href="#methodSummary">Methods</a>
								<xsl:if test="$showPackageFunctions or $showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showPackageFunctions">
								<a href="package.html#methodSummary">Functions</a>
								<xsl:if test="$showEvents or $showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showEvents">
								<a href="#eventSummary">Events</a>
								<xsl:if test="$showStyles or $showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>	
							<xsl:if test="$showStyles">
								<a href="#styleSummary">Styles</a>
								<xsl:if test="$showEffects or $showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showEffects">
								<a href="#effectSummary">Effects</a>
								<xsl:if test="$showConstants or $showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showConstants">
								<a href="#constantSummary">Constants</a>
								<xsl:if test="$showPackageConstants or $showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showPackageConstants">
								<a href="package.html#constantSummary">Constants</a>
								<xsl:if test="$showInterfaces or $showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showInterfaces">
								<a href="package-detail.html#interfaceSummary">Interfaces</a>
								<xsl:if test="$showClasses or $showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showClasses">
								<a href="package-detail.html#classSummary">Classes</a>
								<xsl:if test="$showPackageUse or $showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showPackageUse">
								<a href="package-use.html">Use</a>
								<xsl:if test="$showIncludeExamples or $additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$showIncludeExamples">
								<a href="#includeExamplesSummary">Examples</a>
								<xsl:if test="$additionalLinks">
									<xsl:text>&#xA0;| </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$additionalLinks">
								<xsl:copy-of select="$additionalLinks"/>
							</xsl:if>			
<!--					  		<a id="constantsLink" href="#constantSummary">Constants</a>
					  		<span id="constantsBar"> | </span>			
					  		<a id="packageConstantsLink" href="package.html#constantSummary">Constants</a>
					  		<span id="packageConstantsBar"> | </span>
							<a id="propertiesLink" href="#propertySummary">Properties</a>
							<span id="propertiesBar"> | </span>
							<a id="packagePropertiesLink" href="package.html#propertySummary">Properties</a>
							<span id="packagePropertiesBar"> | </span>
							<a id="stylesLink" href="#styleSummary">Styles</a>
							<span id="stylesBar"> | </span>
							<a id="effectsLink" href="#effectSummary">Effects</a>
							<span id="effectsBar"> | </span>
							<a id="eventsLink" href="#eventSummary">Events</a>
							<span id="eventsBar"> | </span>
							<a id="constructorLink" href="#constructorSummary">Constructor</a>
							<span id="constructorBar"> | </span>
							<a id="methodsLink" href="#methodSummary">Methods</a>
							<span id="methodsBar"> | </span>
							<a id="packageFunctionsLink" href="package.html#methodSummary">Functions</a>
							<span id="packageFunctionsBar"> | </span>
							<a id="interfacesLink" href="package-detail.html#interfaceSummary">Interfaces</a>
							<span id="interfacesBar"> | </span>
							<a id="classesLink" href="package-detail.html#classSummary">Classes</a>
							<span id="classesBar"> | </span>
							<a id="packageUseLink" href="package-use.html">Use</a>
							<span id="packageUseBar"> | </span>
							<a id="examplesLink" href="#includeExamplesSummary">Examples</a>-->
						</td>
					</tr>
					<tr class="titleTableRow3">
						<td>
							<xsl:attribute name="colspan">
								<xsl:if test="$isLiveDocs">
									<xsl:text>3</xsl:text>
								</xsl:if>
								<xsl:if test="not($isLiveDocs)">
									<xsl:text>2</xsl:text>
								</xsl:if>
							</xsl:attribute>
							<xsl:value-of select="$nbsp" />
						</td>
					</tr>
				</table>
			</xsl:if>
			<script language="javascript" type="text/javascript">
				<xsl:comment>
					<xsl:text>
</xsl:text>
					<xsl:text>if (!isEclipse() || window.name != ECLIPSE_FRAME_NAME) {</xsl:text>
					<xsl:text>titleBar_setSubTitle("</xsl:text><xsl:value-of select="$subTitle" /><xsl:text>"); </xsl:text>
					<xsl:text>titleBar_setSubNav(</xsl:text>
					<xsl:value-of select="$showConstants" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showProperties" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showStyles" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showEffects" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showEvents" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showConstructors" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showMethods" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showIncludeExamples" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showPackageConstants" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showPackageProperties" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showPackageFunctions" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showInterfaces" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showClasses" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$showPackageUse" />
					<xsl:text>);</xsl:text>
					<xsl:text>}</xsl:text>
					<xsl:text>
</xsl:text>
				</xsl:comment>
			</script>
		</xsl:if>

<!--		<xsl:if test="$copyNum=2">
			<div>
				<xsl:call-template name="getNavLinks2">
					<xsl:with-param name="copyNum" select="$copyNum" />
					<xsl:with-param name="baseRef" select="$baseRef" />
					<xsl:with-param name="showPackages" select="$showPackages" />
					<xsl:with-param name="showAllClasses" select="$showAllClasses" />
					<xsl:with-param name="showLanguageElements" select="$showLanguageElements" />
					<xsl:with-param name="showIndex" select="$showIndex" />
					<xsl:with-param name="splitIndex" select="$splitIndex" />
					<xsl:with-param name="showAppendixes" select="$showAppendixes" />
					<xsl:with-param name="showConventions" select="$showConventions" />
					<xsl:with-param name="href" select="$href" />
					<xsl:with-param name="fileName" select="$fileName" />
					<xsl:with-param name="fileName2" select="$fileName2" />
				</xsl:call-template>
			</div>
		</xsl:if>-->
	</xsl:template>

	<xsl:template name="getNavLinks2">
		<xsl:param name="copyNum" />
		<xsl:param name="baseRef" />
		<xsl:param name="showPackages" />
		<xsl:param name="showAllClasses" />
		<xsl:param name="showLanguageElements" />
		<xsl:param name="showIndex" />
		<xsl:param name="splitIndex" />
		<xsl:param name="showAppendixes" />
		<xsl:param name="showConventions" />
		<xsl:param name="href" />
		<xsl:param name="fileName" />
		<xsl:param name="fileName2" />

		<xsl:if test="$showPackages">
			<a href="{$baseRef}package-summary.html" onclick="loadClassListFrame('{$baseRef}all-classes.html')">All&#xA0;Packages</a>
			<!-- <xsl:if test="$showAllClasses or $showIndex or $showLanguageElements"> -->
				<xsl:text>&#xA0;|&#xA0;</xsl:text>
			<!-- </xsl:if> -->
		</xsl:if>
		<xsl:if test="$showAllClasses">
			<a href="{$baseRef}class-summary.html" onclick="loadClassListFrame('{$baseRef}all-classes.html')">All&#xA0;Classes</a>
			<!-- <xsl:if test="$showIndex or $showLanguageElements"> -->
				<xsl:text>&#xA0;|&#xA0;</xsl:text>
			<!-- </xsl:if> -->
		</xsl:if>
		<xsl:if test="$showLanguageElements">
			<a href="{$baseRef}language-elements.html">Language&#xA0;Elements</a>
			<!-- <xsl:if test="$showIndex"> -->
				<xsl:text>&#xA0;| </xsl:text>
			<!-- </xsl:if> -->
		</xsl:if>
		<xsl:if test="$showIndex">
			<xsl:if test="$splitIndex='false'">
				<a href="{$baseRef}all-index.html" onclick="loadClassListFrame('{$baseRef}index-list.html')">Index</a>
			</xsl:if>
			<xsl:if test="$splitIndex!='false' and $config/languageElements/@show='true' and $config/languageElements/@operators='true'">
				<a href="{$baseRef}all-index-Symbols.html" onclick="loadClassListFrame('{$baseRef}index-list.html')">Index</a>
			</xsl:if>
			<xsl:if test="$splitIndex!='false' and ($config/languageElements/@show!='true' or $config/languageElements/@operators!='true')">
				<a href="{$baseRef}all-index-A.html" onclick="loadClassListFrame('{$baseRef}index-list.html')">Index</a>
			</xsl:if>
			<xsl:text>&#xA0;|&#xA0;</xsl:text>
		</xsl:if>
		<xsl:if test="$showAppendixes and $config/appendixes/@show='true'">
			<a href="{$baseRef}appendixes.html">Appendixes</a>
			<xsl:text>&#xA0;|&#xA0;</xsl:text>
		</xsl:if>
		<xsl:if test="$showConventions='true'">
			<a href="{$baseRef}conventions.html">Conventions</a>
			<xsl:text>&#xA0;|&#xA0;</xsl:text>
		</xsl:if>

		<a id="framesLink{$copyNum}" href="{$baseRef}index.html?{$href}{$fileName}.html&amp;amp;{$fileName2}">Frames</a>
		<a id="noFramesLink{$copyNum}" style="display:none" href="" onclick="parent.location=document.location">No&#xA0;Frames</a>
	</xsl:template>

	<xsl:template name="getNavLinks">
		<xsl:param name="copyNum" />
		<xsl:param name="baseRef" />
		<xsl:param name="showPackages" />
		<xsl:param name="showAllClasses" />
		<xsl:param name="showLanguageElements" />
		<xsl:param name="showMXMLOnly" />
		<xsl:param name="showIndex" />
		<xsl:param name="splitIndex" />
		<xsl:param name="showAppendixes" />
		<xsl:param name="showConventions" />
		<xsl:param name="href" />
		<xsl:param name="fileName" />
		<xsl:param name="fileName2" />

		<div width="100%" class="topLinks" align="right" style="padding-bottom:5px">
			<span id="navigationCell{$copyNum}" style="display:none;font-size:14px;font-weight:bold">
<!-- 				<xsl:if test="$showInnerClasses or $showInterfaces or $showClasses or $showConstants or $showPackageConstants or $showProperties or $showPackageProperties or $showConstructors or $showMethods or $showPackageFunctions or $showStyles or $showEffects or $showEvents or $showIncludeExamples or $additionalLinks">
					<xsl:text disable-output-escaping="yes">&amp;nbsp;| </xsl:text>
				</xsl:if> -->
				<xsl:if test="$showPackages">
					<a href="{$baseRef}package-summary.html" onclick="loadClassListFrame('{$baseRef}all-classes.html')">All&#xA0;Packages</a>
					<!-- <xsl:if test="$showAllClasses or $showIndex or $showLanguageElements"> -->
						<xsl:text>&#xA0;| </xsl:text>
					<!-- </xsl:if> -->
				</xsl:if>
				<xsl:if test="$showAllClasses">
					<a href="{$baseRef}class-summary.html" onclick="loadClassListFrame('{$baseRef}all-classes.html')">All&#xA0;Classes</a>
					<!-- <xsl:if test="$showIndex or $showLanguageElements"> -->
						<xsl:text>&#xA0;| </xsl:text>
					<!-- </xsl:if> -->
				</xsl:if>
				<xsl:if test="$showLanguageElements">
					<a href="{$baseRef}language-elements.html">Language&#xA0;Elements</a>
					<!-- <xsl:if test="$showIndex"> -->
						<xsl:text>&#xA0;| </xsl:text>
					<!-- </xsl:if> -->
				</xsl:if>
				<xsl:if test="$showMXMLOnly">
					<a href="{$baseRef}mxml-tag-detail.html" onclick="loadClassListFrame('{$baseRef}mxml-tags.html')">MXML&#xA0;Only&#xA0;Components</a>
					<xsl:text>&#xA0;| </xsl:text>
				</xsl:if>
				<xsl:if test="$showIndex">
					<xsl:if test="$splitIndex='false'">
						<a href="{$baseRef}all-index.html" onclick="loadClassListFrame('{$baseRef}index-list.html')">Index</a>
					</xsl:if>
					<xsl:if test="$splitIndex!='false' and $config/languageElements/@show='true' and $config/languageElements/@operators='true'">
						<a href="{$baseRef}all-index-Symbols.html" onclick="loadClassListFrame('{$baseRef}index-list.html')">Index</a>
					</xsl:if>
					<xsl:if test="$splitIndex!='false' and ($config/languageElements/@show!='true' or $config/languageElements/@operators!='true')">
						<a href="{$baseRef}all-index-A.html" onclick="loadClassListFrame('{$baseRef}index-list.html')">Index</a>
					</xsl:if>
					<xsl:text>&#xA0;| </xsl:text>
				</xsl:if>
				<xsl:if test="$showAppendixes and $config/appendixes/@show='true'">
					<a href="{$baseRef}appendixes.html">Appendixes</a>
					<xsl:text>&#xA0;| </xsl:text>
				</xsl:if>
				<xsl:if test="$showConventions='true'">
					<a href="{$baseRef}conventions.html">Conventions</a>
					<xsl:text>&#xA0;| </xsl:text>
				</xsl:if>

				<a id="framesLink{$copyNum}" href="{$baseRef}index.html?{$href}{$fileName}.html&amp;amp;{$fileName2}">Frames</a>
				<a id="noFramesLink{$copyNum}" style="display:none" href="" onclick="parent.location=document.location">No&#xA0;Frames</a>
			</span>
		</div>
	</xsl:template>

	<xsl:template name="getFeedbackLink">
		<xsl:param name="topic" />
		<xsl:param name="filename" />
		<xsl:param name="filename2" select="''" />

		<xsl:if test="$config/feedback[@show='true']">
			<div class="feedbackLink">
				<center>
					<xsl:if test="$config/feedback[@type='email']">
						<a href="mailto:{$config/feedback/feedbackEmail/address/.}?subject=ASLR Feedback({$timestamp}) : {$topic}">
							<xsl:value-of select="$config/feedback/feedbackEmail/label/." />
						</a>
					</xsl:if>
					<xsl:if test="$config/feedback[@type='livedocs']">
<!-- 			<xsl:variable name="feedbackEmail">
				<xsl:choose>
					<xsl:when test="contains($config/feedbackEmail,'@')">
						<xsl:value-of select="$config/feedbackEmail" />
					</xsl:when>
					<xsl:otherwise>flash_api_writers@macromedia.com</xsl:otherwise>
				</xsl:choose>
			</xsl:variable> -->
<!--					<a href="http://livedocs.macromedia.com/labs/1/flex/langref/{$filename}" target="mm_livedocs"><xsl:text>Submit Feedback on LiveDocs</xsl:text></a> -->
						<a href="javascript:gotoLiveDocs('{$filename}','{$filename2}');">
							<xsl:value-of select="$config/feedback/feedbackLiveDocs/label/." />
						</a>
					</xsl:if>
				</center>
			</div>
<!--			<center>
 				<a href="mailto:{$feedbackEmail}?subject=ASLR Feedback({$config/options/@buildNum}) : {$topic}"> -->
<!-- 				<a href="mailto:{$feedbackEmail}?subject=ASLR Feedback({$timestamp}) : {$topic}">
					<xsl:text>Submit Feedback on </xsl:text>
					<xsl:value-of select="$topic"/>
				</a>
			</center> -->
		</xsl:if>
	</xsl:template>

	<!-- TODO support multiple? -->
	<xsl:template name="version">
		<xsl:if test="$showLangVersionWarnings='true' and not(count(versions/langversion))">
			<xsl:message>WARNING: no langversion for <xsl:if test="../../@name">
				<xsl:value-of select="../../@name"/>
				<xsl:text>.</xsl:text>
			</xsl:if><xsl:value-of select="@name"/></xsl:message>
		</xsl:if>
		<xsl:if test="$showPlayerVersionWarnings='true' and not(count(versions/playerversion))">
			<xsl:message>WARNING: no playerversion for <xsl:if test="../../@name">
				<xsl:value-of select="../../@name"/>
				<xsl:text>.</xsl:text>
			</xsl:if><xsl:value-of select="@name"/></xsl:message>
		</xsl:if>

		<xsl:if test="not($config/options/@showVersions) or $config/options[@showVersions!='false']">
			<xsl:if test="$config/options[@docversion='3']">
				<p></p>
				<table cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td style="white-space:nowrap" valign="top">
							<b>
								<xsl:text disable-output-escaping="yes">Player version:&amp;nbsp;</xsl:text>
							</b>
						</td>
						<td>
							<xsl:text>Flash Player 8.5</xsl:text>
						</td>
					</tr>
				</table>
				<p></p>
			</xsl:if>
			<xsl:if test="not($config/options[@docversion='3'])">
				<xsl:if test="count(versions/langversion[not(starts-with(@version,'1'))]) or count(versions/playerversion)">
					<p/>
					<xsl:if test="count(versions/langversion[not(starts-with(@version,'1'))])">
						<table cellpadding="0" cellspacing="0" border="0">
							<tr>
								<td style="white-space:nowrap" valign="top">
									<b>
									<xsl:text disable-output-escaping="yes">Language version:&amp;nbsp;</xsl:text>
									</b>
								</td>
								<td>
									<xsl:text>ActionScript </xsl:text>
									<xsl:value-of select="translate(versions/langversion/@version,'+','')"/>
									<xsl:if test="substring-before(versions/langversion/@version, '+')">
										<xsl:text> and later</xsl:text>
									</xsl:if>
									<xsl:if test="string-length(normalize-space(versions/langversion))">
										<xsl:value-of select="$emdash"/>
										<xsl:call-template name="deTilda">
											<xsl:with-param name="inText" select="normalize-space(versions/langversion)"/>
										</xsl:call-template>
									</xsl:if>
								</td>
							</tr>
						</table>
					</xsl:if>
					<xsl:if test="count(versions/playerversion)">
						<table cellpadding="0" cellspacing="0" border="0">
							<tr>
								<td style="white-space:nowrap" valign="top">
									<b>
										<xsl:text disable-output-escaping="yes">Player version:&amp;nbsp;</xsl:text>
									</b>
								</td>
								<td>
									<xsl:choose>
										<xsl:when test="versions/playerversion/@name='Flash'">
											<xsl:text>Flash Player </xsl:text>
										</xsl:when>
										<xsl:when test="versions/playerversion/@name='Lite'">
											<xsl:text>Flash Lite </xsl:text>
										</xsl:when>
									</xsl:choose>
				 					<xsl:value-of select="translate(translate(versions/playerversion/@version,'+',''),',','.')"/>
									<xsl:if test="substring-before(versions/playerversion/@version, '+')">
										<xsl:text> and later</xsl:text>
									</xsl:if>
									<xsl:if test="string-length(normalize-space(versions/playerversion))">
										<xsl:value-of select="$emdash"/>
										<xsl:call-template name="deTilda">
											<xsl:with-param name="inText" select="normalize-space(versions/playerversion)"/>
										</xsl:call-template>
									</xsl:if>
								</td>
							</tr>
						</table>
					</xsl:if>
					<p/>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="isTopLevel">
		<xsl:param name="packageName"/>
		<xsl:value-of select="string-length($packageName)=0 or contains($packageName,'$$')"/>
	</xsl:template>

	<xsl:template name="getPackageComments">
		<xsl:param name="name"/>
		<xsl:element name="package">
			<xsl:copy-of select="document($packageCommentsFilename)/packages/package[@name=$name]"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="getFirstSentence">
		<xsl:param name="inText"/>
		<xsl:variable name="text" select="normalize-space($inText)"/>
		<xsl:variable name="periodWithTag">
			<xsl:text disable-output-escaping="yes">.&lt;</xsl:text>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="string-length($text) = 0"/>
			<xsl:when test="substring-before($text,'. ')">
				<xsl:value-of select="substring-before($text,'. ')" disable-output-escaping="yes"/>.</xsl:when>
			<xsl:when test="substring-before($text,$periodWithTag)">
				<xsl:value-of select="substring-before($inText,$periodWithTag)" disable-output-escaping="yes"/>.</xsl:when>
			<xsl:when test="substring-before($text,'.')">
				<xsl:value-of select="substring-before($text,'.')" disable-output-escaping="yes"/>.</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" disable-output-escaping="yes"/>.</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="deTilda">
		<xsl:param name="inText"/>
		<xsl:variable name="text">
			<xsl:call-template name="search-and-replace">
				<xsl:with-param name="search-string" select="'~~'"/>
				<xsl:with-param name="replace-string" select="'*'"/>
				<xsl:with-param name="input">
 					<xsl:call-template name="convertListing">
						<xsl:with-param name="inText" select="$inText"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>		
		<xsl:variable name="text2">
			<xsl:call-template name="search-and-replace">
				<xsl:with-param name="search-string" select="'TAAB'" />
				<xsl:with-param name="replace-string" select="'    '" />
				<xsl:with-param name="input" select="$text" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="$text2" disable-output-escaping="yes"/>
	</xsl:template>

	<xsl:template name="listingIcon">
		<xsl:param name="version" />

		<xsl:variable name="conditionalText">
			<xsl:choose>
				<xsl:when test="number($version)=3">
					<xsl:text>3.gif' alt='</xsl:text>
					<xsl:value-of select="$AS3tooltip" />
					<xsl:text>' title='</xsl:text>
					<xsl:value-of select="$AS3tooltip" />
				</xsl:when>
				<xsl:when test="number($version)=2">
					<xsl:text>2.gif' alt='</xsl:text>
					<xsl:value-of select="$AS2tooltip" />
					<xsl:text>' title='</xsl:text>
					<xsl:value-of select="$AS2tooltip" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>1.gif' alt='</xsl:text>
					<xsl:value-of select="$AS1tooltip" />
					<xsl:text>' title='</xsl:text>
					<xsl:value-of select="$AS1tooltip" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text disable-output-escaping="yes">&lt;img src='</xsl:text>
		<xsl:call-template name="getBaseRef">
			<xsl:with-param name="packageName" select="ancestor-or-self::asClass/@packageName" />
		</xsl:call-template>
		<xsl:text>images/AS</xsl:text>
		<xsl:value-of select="$conditionalText" />
		<xsl:text>' width='96' height='15' style='margin-right:5px' /&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="convertListing">
		<xsl:param name="inText" select="''"/>

		<xsl:if test="not(contains($inText,'&lt;listing'))">
			<xsl:value-of select="$inText"/>
		</xsl:if>
		<xsl:if test="contains($inText,'&lt;listing')">
			<xsl:value-of select="substring-before($inText,'&lt;listing')"/>
			
			<xsl:if test="$showASIcons='true'">
				<xsl:text disable-output-escaping="yes">&lt;div class='listingIcons'&gt;</xsl:text>
				<xsl:choose>
					<xsl:when test="contains(substring-before($inText,'&lt;/listing&gt;'),'version=&quot;3')">
						<xsl:call-template name="listingIcon">
							<xsl:with-param name="version" select="3" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="contains(substring-before($inText,'&lt;/listing&gt;'),'version=&quot;2')">
						<xsl:call-template name="listingIcon">
							<xsl:with-param name="version" select="2" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="listingIcon">
							<xsl:with-param name="version" select="1" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text>
			</xsl:if>

			<xsl:variable name="remainder" select="substring-after(substring-after($inText,'&lt;listing'),'&gt;')"/>
			<xsl:text disable-output-escaping="yes">&lt;div class='listing'&gt;&lt;pre&gt;</xsl:text>
				<!-- <xsl:text disable-output-escaping="yes">&lt;code&gt;</xsl:text> -->
					<xsl:value-of select="substring-before($remainder,'&lt;/listing&gt;')"/>
				<!-- <xsl:text disable-output-escaping="yes">&lt;/code&gt;</xsl:text> -->
			<xsl:text disable-output-escaping="yes">&lt;/pre&gt;&lt;/div&gt;</xsl:text>
			<xsl:call-template name="convertListing">
				<xsl:with-param name="inText" select="substring-after($remainder,'&lt;/listing&gt;')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- 	<xsl:template name="deTilda2">
		<xsl:param name="inText"/>
		<xsl:param name="prevChar"/>

		<xsl:if test="$inText">
			<xsl:if test="starts-with($inText,'~')">
				<xsl:if test="$prevChar='~'">
					<xsl:text>~</xsl:text>
				</xsl:if>
				<xsl:if test="$prevChar and $prevChar!='~'">
					<xsl:text>*</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="not(starts-with($inText,'~'))">
				<xsl:if test="$prevChar
				<xsl:value-of disable-output-escaping="yes" select="substring($inText,'1','1')"/>
			</xsl:if>
			<xsl:call-template name="deTilda2">
				<xsl:with-param name="inText" select="substring($inText,2)"/>
				<xsl:with-param name="prevChar" select="starts-with($inText,'~')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template> -->

	<xsl:template name="getKeywords">
		<xsl:variable name="keywords">
			<!-- TODO use defined keywords after scrub? -->
			<xsl:if test=".//Xkeyword">
				<xsl:for-each select=".//keyword">
					<xsl:value-of select="normalize-space()"/>
					<xsl:text>, </xsl:text>
				</xsl:for-each>
			</xsl:if>
			<xsl:value-of select="@name"/>
			<xsl:if test="string-length(@packageName)">
				<xsl:text>,</xsl:text>
				<xsl:call-template name="convertFullName">
					<xsl:with-param name="fullname" select="@fullname"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="fields/field">
				<xsl:text>,</xsl:text>
				<xsl:for-each select="fields/field">
					<xsl:value-of select="@name"/>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="methods/method">
				<xsl:text>,</xsl:text>
				<xsl:for-each select="methods/method">
					<xsl:value-of select="@name"/>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:variable>

		<meta name="keywords" content="{$keywords}"/>
	</xsl:template>

	<xsl:template name="convertFullName">
		<xsl:param name="fullname"/>
		<xsl:param name="separator">.</xsl:param>
		<xsl:param name="justClass">false</xsl:param>

		<xsl:variable name="trimmed">
			<xsl:call-template name="search-and-replace">
				<xsl:with-param name="input" select="$fullname"/>
				<xsl:with-param name="search-string">:public</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="trimmed2">
			<xsl:call-template name="search-and-replace">
				<xsl:with-param name="input" select="$trimmed"/>
				<xsl:with-param name="search-string">:internal</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="trimmed3" select="translate($trimmed2,':','.')"/>
		<xsl:choose>
			<xsl:when test="$justClass = 'true'">
				<xsl:call-template name="substring-after-last">
					<xsl:with-param name="input" select="translate($trimmed3,'/','.')"/>
					<xsl:with-param name="substr">.</xsl:with-param>
				</xsl:call-template>
				<!-- <xsl:value-of select="str:substring($trimmed3, str:lastIndexOf($trimmed3,'.')+1)"/> -->
			</xsl:when>
			<xsl:when test="contains($trimmed3,'/')">
				<!-- inner class -->
				<xsl:value-of select="translate(substring-before($trimmed3,'/'),'.',$separator)"/>
				<xsl:text>.</xsl:text>
				<xsl:variable name="trimmed4" select="substring-after($trimmed3,'/')"/>
				<xsl:if test="contains($trimmed4,'.')">
					<xsl:variable name="trimmed5">
						<xsl:call-template name="substring-after-last">
							<xsl:with-param name="input" select="$trimmed4"/>
							<xsl:with-param name="substr" select="'.'"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:value-of select="translate($trimmed5,'.',$separator)"/>
				</xsl:if>
				<xsl:if test="not(contains($trimmed4,'.'))">
					<xsl:value-of select="translate($trimmed4,'.',$separator)"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="translate($trimmed3,'.',$separator)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="sees">
		<xsl:param name="labelClass" select="'label'" />
		<xsl:param name="xrefId">
			<xsl:choose>
				<xsl:when test="self::operator">
					<xsl:text>operator#</xsl:text>
				</xsl:when>
				<xsl:when test="self::statement">
					<xsl:text>statement#</xsl:text>
				</xsl:when>
				<xsl:when test="self::specialType">
					<xsl:text>specialType#</xsl:text>
				</xsl:when>
				<xsl:when test="self::statements">
					<xsl:text>statements</xsl:text>
				</xsl:when>
				<xsl:when test="self::operators">
					<xsl:text>operators</xsl:text>
				</xsl:when>
				<xsl:when test="self::specialTypes">
					<xsl:text>special-types</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="ancestor::asPackage/@name='$$Global$$' and not(ancestor-or-self::asClass)">
						<xsl:text>global</xsl:text>					
						<xsl:if test="ancestor::asClass">
							<xsl:text>.</xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="not(ancestor::asPackage/@name='$$Global$$')"> 
						<xsl:value-of select="ancestor::asPackage/@name" />			
						<xsl:if test="ancestor-or-self::asClass">
							<xsl:text>.</xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="ancestor-or-self::asClass">
						<xsl:value-of select="ancestor::asClass/@name" />
					</xsl:if>
					<xsl:choose>
						<xsl:when test="self::constructor">
							<xsl:text>#method:</xsl:text>
						</xsl:when>
						<xsl:when test="self::method">
							<xsl:text>#method:</xsl:text>
						</xsl:when>
						<xsl:when test="self::field">
							<xsl:text>#property:</xsl:text>
						</xsl:when>
						<xsl:when test="self::event">
							<xsl:text>#event:</xsl:text>
						</xsl:when>
						<xsl:when test="self::style">
							<xsl:text>#style:</xsl:text>
						</xsl:when>
						<xsl:when test="self::effect">
							<xsl:text>#effect:</xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="@name" />
		</xsl:param>
		<xsl:param name="packageName">
			<xsl:if test="ancestor-or-self::asPackage/@name!='$$Global$$'">
				<xsl:value-of select="ancestor-or-self::asPackage/@name" />
			</xsl:if>
		</xsl:param>

		<xsl:variable name="numSees" select="count(sees/see[normalize-space(@label) or @href])" />
		<xsl:if test="$numSees or exslt:nodeSet($xrefs)/helpreferences/helpreference[normalize-space(id/.)=$xrefId]">
			<p>
				<span class="{$labelClass}">See also</span>
			</p>
			<div class="seeAlso">
				<xsl:for-each select="sees/see[string-length(@href) or string-length(@label)]">
					<xsl:if test="string-length(@href)">
						<a href="{@href}">
							<xsl:attribute name="target">
								<xsl:if test="starts-with(@href,'http:')">
									<xsl:text>mm_external</xsl:text>
								</xsl:if>
							</xsl:attribute>
							<xsl:if test="normalize-space(@label)">
								<xsl:value-of select="normalize-space(@label)"/>
							</xsl:if>
							<xsl:if test="not(normalize-space(@label))">
								<xsl:value-of select="@href"/>
							</xsl:if>
						</a>
					</xsl:if>
					<xsl:if test="not(string-length(@href)) and string-length(@label) &gt; 0">
						<xsl:value-of select="normalize-space(@label)"/>
					</xsl:if>
					<xsl:if test="position() != last()">
						<br />
					</xsl:if>
				</xsl:for-each>
				<xsl:variable name="baseRef">
					<xsl:call-template name="getBaseRef">
						<xsl:with-param name="packageName" select="$packageName" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:for-each select="exslt:nodeSet($xrefs)/helpreferences/helpreference[normalize-space(id/.)=$xrefId]">
					<xsl:if test="position()=1 and $numSees">
						<br />
					</xsl:if>
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:value-of select="concat($baseRef,$config/xrefs/@baseRef,href/.)" />
						</xsl:attribute>
						<xsl:if test="string-length($config/xrefs/@target)">
							<xsl:attribute name="target">
								<xsl:value-of select="$config/xrefs/@target" />
							</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="title/." />
					</xsl:element>
					<xsl:if test="position() != last()">
						<br />
					</xsl:if>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>
	
<!--	<xsl:template name="sees">
		<xsl:param name="xrefId" />
		<xsl:param name="packageName" />

		<xsl:variable name="numSees" select="count(sees/see[normalize-space(@label) or @href])" />
		<xsl:if test="$numSees or $xrefs/helpreference[normalize-space(id/.)=$xrefId]">
			<p/>
			<b>See also</b>
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td width="20"/>
					<td>
						<xsl:for-each select="sees/see[string-length(@href) or string-length(@label)]">
							<xsl:if test="string-length(@href)">
								<a href="{@href}">
									<xsl:attribute name="target">
										<xsl:if test="starts-with(@href,'http:')">
											<xsl:text>mm_external</xsl:text>
										</xsl:if>
									</xsl:attribute>
									<xsl:if test="normalize-space(@label)">
										<xsl:value-of select="normalize-space(@label)"/>
									</xsl:if>
									<xsl:if test="not(normalize-space(@label))">
										<xsl:value-of select="@href"/>
									</xsl:if>
								</a>
							</xsl:if>
							<xsl:if test="not(string-length(@href)) and string-length(@label) &gt; 0">
								<xsl:value-of select="normalize-space(@label)"/>
							</xsl:if>
							<xsl:if test="position() != last()">
								<xsl:text>, </xsl:text>
							</xsl:if>
						</xsl:for-each>
						<xsl:variable name="baseRef">
							<xsl:call-template name="getBaseRef">
								<xsl:with-param name="packageName" select="$packageName" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:for-each select="$xrefs/helpreference[normalize-space(id/.)=$xrefId]">
							<xsl:if test="position()=1 and $numSees">
								<xsl:text>, </xsl:text>
							</xsl:if>
							<a href="{concat($baseRef,$config/xrefs/@baseRef,href/.)}" target="mm_xref">
								<xsl:value-of select="title/." />
							</a>
							<xsl:if test="position() != last()">
								<xsl:text>, </xsl:text>
							</xsl:if>
						</xsl:for-each>
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>-->

	<xsl:template name="getSimpleClassName">
		<xsl:param name="fullClassName"/>

		<xsl:choose>
			<xsl:when test="contains($fullClassName,':')">
				<xsl:call-template name="substring-after-last">
					<xsl:with-param name="input" select="$fullClassName"/>
					<xsl:with-param name="substr" select="':'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($fullClassName,'.')">
				<xsl:call-template name="substring-after-last">
					<xsl:with-param name="input" select="$fullClassName"/>
					<xsl:with-param name="substr" select="'.'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$fullClassName"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:variable name="deprecatedLabel">
		<b>Deprecated</b>
	</xsl:variable>

	<xsl:template match="deprecated">
		<xsl:param name="showDescription" select="'true'"/>

		<xsl:copy-of select="$deprecatedLabel"/>
<!-- 		<em> -->
			<xsl:if test="string-length(@as-of)">
				<xsl:text> since </xsl:text><xsl:value-of select="normalize-space(@as-of)"/>
			</xsl:if>
			<xsl:if test="$showDescription='true' and string-length(normalize-space())">
				<xsl:value-of select="$emdash"/>
				<xsl:call-template name="deTilda">
					<xsl:with-param name="inText" select="normalize-space()"/>
				</xsl:call-template>
			</xsl:if>
<!-- 		</em> -->
		<xsl:if test="$showDescription!='true' or not(string-length(normalize-space()))">
			<xsl:text>.</xsl:text>
		</xsl:if>
		<br />
	</xsl:template>

	<xsl:template match="item" mode="annotate">
		<xsl:for-each select="annotation">
			<xsl:choose>
				<xsl:when test="@type='text'">
					<div class="annotation">
						<xsl:value-of disable-output-escaping="yes" select="." />
					</div>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="shortDescriptionReview">
		<xsl:if test="(review or customs/review) and $config/options/@showReview='true'">
			<font color="red">Review needed. </font>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getPageTitlePostFix">
		<xsl:if test="string-length($config/pageTitlePostFix/.)">
			<xsl:text> </xsl:text>
			<xsl:value-of select="$config/pageTitlePostFix/." />
		</xsl:if>
	</xsl:template>

	<xsl:template name="addKeywords">
		<xsl:param name="keyword" />
		<xsl:param name="num" select="$config/keywords/@num" />

		<xsl:if test="$config/keywords[@show='true'] and $keyword">
			<div style="display:none">
				<xsl:call-template name="duplicateString">
					<xsl:with-param name="input" select="concat($keyword,' ')" />
					<xsl:with-param name="count" select="$num" />
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="duplicateString">
		<xsl:param name="input" />
		<xsl:param name="count" select="1" />

		<xsl:choose>
			<xsl:when test="not($count) or not($input)" />
			<xsl:when test="$count=1">
				<xsl:value-of select="$input" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$count mod 2">
					<xsl:value-of select="$input" />
				</xsl:if>
				<xsl:call-template name="duplicateString">
					<xsl:with-param name="input" select="concat($input,$input)" />
					<xsl:with-param name="count" select="floor($count div 2)" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

 	<xsl:template name="substring-before-last">
		<xsl:param name="input"/>
		<xsl:param name="substr"/>

		<xsl:if test="$substr and contains($input,$substr)">
			<xsl:variable name="tmp" select="substring-after($input,$substr)"/>
			<xsl:value-of select="substring-before($input,$substr)"/>
			<xsl:if test="contains($tmp,$substr)">
				<xsl:value-of select="$substr"/>
				<xsl:call-template name="substring-before-last">
					<xsl:with-param name="input" select="$tmp"/>
					<xsl:with-param name="substr" select="$substr"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="substring-after-last">
		<xsl:param name="input"/>
		<xsl:param name="substr"/>

		<xsl:variable name="tmp" select="substring-after($input,$substr)"/>
		<xsl:choose>
			<xsl:when test="$substr and contains($tmp,$substr)">
				<xsl:call-template name="substring-after-last">
					<xsl:with-param name="input" select="$tmp"/>
					<xsl:with-param name="substr" select="$substr"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$tmp"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="search-and-replace">
		<xsl:param name="input"/>
		<xsl:param name="search-string"/>
		<xsl:param name="replace-string"/>

		<xsl:choose>
			<xsl:when test="$search-string and contains($input,$search-string)">
				<xsl:value-of select="substring-before($input,$search-string)"/>
				<xsl:value-of select="$replace-string"/>
				<xsl:call-template name="search-and-replace">
					<xsl:with-param name="input" select="substring-after($input,$search-string)"/>
					<xsl:with-param name="search-string" select="$search-string"/>
					<xsl:with-param name="replace-string" select="$replace-string"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$input"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>