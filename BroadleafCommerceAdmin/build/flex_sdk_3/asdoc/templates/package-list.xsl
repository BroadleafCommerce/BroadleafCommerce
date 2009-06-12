<?xml version="1.0" encoding="utf-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="asdoc-util.xsl"/>

	<xsl:template match="/">
		<xsl:copy-of select="$noLiveDocs" />
		<xsl:copy-of select="$docType" />
		<xsl:element name="html">
			<head>
				<title>Package List - <xsl:value-of select="$title-base"/></title>
				<base target="classFrame"/>
				<xsl:call-template name="getStyleLink">
					<xsl:with-param name="link" select="asdoc/link"/>
				</xsl:call-template>
				<script language="javascript" src="asdoc.js" type="text/javascript" />
			</head>
			<body class="classFrameContent">
				<h3><a href="package-summary.html" onclick="javascript:loadClassListFrame('all-classes.html');" style="color:black">Packages</a></h3>
				<table cellpadding="0" cellspacing="0">
					<xsl:if test="asdoc/packages/asPackage[contains(@name,'$$')]/classes/asClass">
						<tr>
							<td>
								<a href="package-detail.html" onclick="javascript:loadClassListFrame('class-list.html');">Top Level</a>
							</td>
						</tr>
					</xsl:if>
					<xsl:for-each select="asdoc/packages/asPackage[classes/asClass or methods/method or fields/field]">
						<xsl:sort select="@name"/>

						<xsl:variable name="isTopLevel">
							<xsl:call-template name="isTopLevel">
								<xsl:with-param name="packageName" select="@name"/>
							</xsl:call-template>
						</xsl:variable>
						<tr>
							<td>
								<xsl:if test="$isTopLevel='false'">
									<xsl:variable name="packagePath" select="translate(@name,'.','/')"/>
									<a href="{$packagePath}/package-detail.html" onclick="javascript:loadClassListFrame('{$packagePath}/class-list.html');">
										<xsl:value-of select="@name"/>
									</a>
									<xsl:if test="position() != last()">
										<br/>
									</xsl:if>
								</xsl:if>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<xsl:if test="$config/languageElements[@show='true']">
					<h3><a href="language-elements.html" style="color:black">Language Elements</a></h3>
					<table cellpadding="0" cellspacing="0">
						<xsl:if test="$config/languageElements[@directives='true']">
							<tr>
								<td>
									<a href="directives.html">Compiler Directives</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$config/languageElements[@constants='true']">
							<tr>
								<td>
									<xsl:if test="$config/options[@docversion='3']">
										<a href="package.html#constantSummary">Global Constants</a>
									</xsl:if>
									<xsl:if test="not($config/options[@docversion='3'])">
										<a href="constants.html">Global Constants</a>
									</xsl:if>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$config/languageElements[@functions='true']">
							<tr>
								<td>
									<xsl:if test="$config/options[@docversion='3']">
										<a href="package.html#methodSummary">Global Functions</a>
									</xsl:if>
									<xsl:if test="not($config/options[@docversion='3'])">
										<a href="global_functions.html">Global Functions</a>
									</xsl:if>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$config/languageElements[@properties='true']">
							<tr>
								<td>
									<a href="global_props.html">Global Properties</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$config/languageElements[@operators='true']">
							<tr>
								<td>
									<a href="operators.html">Operators</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$config/languageElements[@statements='true']">
							<tr>
								<td>
									<a href="statements.html">Statements, Keywords &amp; Directives</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$config/languageElements[@specialTypes='true']">
							<tr>
								<td>
									<a href="specialTypes.html">Special Types</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:for-each select="$config/languageElements/element">
							<tr>
								<td>
									<a href="{@href}" onclick="{@onclick}">
										<xsl:value-of select="@label" />
									</a>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:if>
<!-- 					<xsl:if test="$config/options[@showDeprecated='true']"> -->
				<xsl:if test="$config/appendixes[@show='true']">
					<h3><a href="appendixes.html" style="color:black">Appendixes</a></h3>
					<table cellpadding="0" cellspacing="0">
						<xsl:if test="$config/appendixes[@deprecated='true']">
							<tr>
								<td>
									<a href="deprecated.html">Deprecated</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:for-each select="$config/appendixes/appendix">
							<tr>
								<td>
									<a href="{@href}" onclick="{@onclick}">
										<xsl:value-of select="@label" />
									</a>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:if>
			</body>
		</xsl:element>
		<xsl:copy-of select="$copyrightComment"/>
	</xsl:template>
</xsl:stylesheet>