<?xml version="1.0" encoding="utf-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="asdoc-util.xsl" />
	<xsl:output method="html" indent="yes" encoding="ASCII" />

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="html">
		<xsl:copy-of select="$noLiveDocs" />
		<xsl:if test=".//frameset">
			<xsl:copy-of select="$frameDocType" />
		</xsl:if>
		<xsl:if test="not(.//frameset)">
			<xsl:copy-of select="$docType" />
		</xsl:if>


		<xsl:element name="html">
			<xsl:apply-templates />
			<xsl:copy-of select="$copyrightComment" />
			<xsl:value-of select="$newline" />
		</xsl:element>
	</xsl:template>

<!--	<xsl:template match="head">
		<xsl:element name="head">
			<xsl:if test="$config/options[@standalone='true']">
				<xsl:value-of select="$newline" />
				<xsl:copy-of select="$markOfTheWeb" />
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>-->

	<xsl:template match="titleHere">
            <xsl:call-template name="setTitle"/>
    </xsl:template>

	<xsl:template match="script">
		<xsl:element name="script">
			<xsl:apply-templates select="@* | node()" />
			<xsl:value-of select="$newline" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of disable-output-escaping="no" select="." />
	</xsl:template>

	<xsl:template match="comment()">
		<xsl:comment>
			<xsl:value-of disable-output-escaping="yes" select="." />
		</xsl:comment>
	</xsl:template>

</xsl:stylesheet>