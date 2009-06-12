<?xml version="1.0" encoding="UTF-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="asdoc-util.xsl"/>
	<xsl:variable name="title" select="concat('All Classes - ',$title-base)"/>

	<xsl:template match="/">
		<xsl:copy-of select="$noLiveDocs" />
		<xsl:copy-of select="$docType" />
		<xsl:element name="html">
			<head>
				<title>
					<xsl:value-of select="$title"/>
				</title>
				<base target="classFrame"/>
				<xsl:call-template name="getStyleLink">
					<xsl:with-param name="link" select="asdoc/link"/>
				</xsl:call-template>
			</head>
			<body class="classFrameContent">
				<h3>
					<a href="class-summary.html" target="classFrame" style="color:black">All Classes</a>
				</h3>
				<table cellpadding="0" cellspacing="0">
					<xsl:for-each select="//asClass">
						<xsl:sort select="@name" order="ascending"/>

						<xsl:variable name="classPath" select="translate(@packageName,'.','/')"/>
						<tr>
							<td>
								<xsl:choose>
									<xsl:when test="$classPath">
										<a href="{$classPath}/{@name}.html">
											<xsl:if test="@type='interface'">
												<i>
													<xsl:value-of select="@name"/>
												</i>
											</xsl:if>
											<xsl:if test="@type != 'interface'">
												<xsl:value-of select="@name"/>
											</xsl:if>
										</a>
									</xsl:when>
									<xsl:otherwise>
										<a href="{@name}.html">
											<xsl:if test="@type='interface'">
												<i>
													<xsl:value-of select="@name"/>
												</i>
											</xsl:if>
											<xsl:if test="@type != 'interface'">
												<xsl:value-of select="@name"/>
											</xsl:if>
										</a>
									</xsl:otherwise>
								</xsl:choose>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</xsl:element>
		<xsl:copy-of select="$copyrightComment"/>
	</xsl:template>
</xsl:stylesheet>