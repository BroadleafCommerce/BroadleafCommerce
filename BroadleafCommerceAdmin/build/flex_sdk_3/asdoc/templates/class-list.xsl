<?xml version="1.0" encoding="utf-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:redirect="http://xml.apache.org/xalan/redirect" extension-element-prefixes="redirect" exclude-result-prefixes="redirect">

	<xsl:include href="asdoc-util.xsl"/>
	<xsl:param name="outputPath" select="'../out/'"/>

	<xsl:template match="/">
		<xsl:for-each select="asdoc/packages/asPackage">

			<xsl:variable name="title" select="concat(@name,concat(' - ',$title-base))"/>
			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="@name"/>
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="classListFile">
				<xsl:value-of select="$outputPath"/>
				<xsl:choose>
					<xsl:when test="$isTopLevel='true'">class-list.html</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate(@name,'.','/')"/>/class-list.html</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<redirect:write select="$classListFile">
				<xsl:copy-of select="$noLiveDocs" />
				<xsl:copy-of select="$docType"/>
				<xsl:element name="html">
					<head>
						<title>
							<xsl:value-of select="$title"/>
						</title>
						<base target="classFrame"/>
						<xsl:call-template name="getStyleLink">
							<xsl:with-param name="link" select="/asdoc/link"/>
							<xsl:with-param name="packageName" select="@name"/>
						</xsl:call-template>
					</head>
					<body class="classFrameContent">
						<h3>
							<xsl:choose>
								<xsl:when test="$isTopLevel='true'">
									<a href="package-detail.html" target="classFrame" style="color:black">Top Level</a>
								</xsl:when>
								<xsl:otherwise><a href="package-detail.html" target="classFrame" style="color:black">Package <xsl:value-of select="@name"/></a></xsl:otherwise>
							</xsl:choose>
						</h3>
						<table cellpadding="0" cellspacing="0">
							<xsl:for-each select="fields/field[@isConst='true']">
								<xsl:sort select="@name" order="ascending"/>

								<xsl:if test="position()=1">
									<tr>
										<td>
											<a href="package.html#constantSummary" style="color:black"><b><xsl:text>Constants</xsl:text></b></a>
										</td>
									</tr>
								</xsl:if>
								<tr>
									<td>
										<a href="package.html#{@name}">
											<xsl:value-of select="@name"/>
										</a>
									</td>
								</tr>
								<xsl:if test="position()=last()">
									<tr>
										<td width="10px">
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="fields/field[@isConst='false']">
								<xsl:sort select="@name" order="ascending"/>

								<xsl:if test="position()=1">
									<tr>
										<td>
											<a href="package.html#propertySummary" style="color:black"><b><xsl:text>Properties</xsl:text></b></a>
										</td>
									</tr>
								</xsl:if>
								<tr>
									<td>
										<a href="package.html#{@name}">
											<xsl:value-of select="@name"/>
										</a>
									</td>
								</tr>
								<xsl:if test="position()=last()">
									<tr>
										<td width="10px">
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="methods/method">
								<xsl:sort select="@name" order="ascending"/>

								<xsl:if test="position()=1">
									<tr>
										<td>
											<a href="package.html#methodSummary" style="color:black"><b><xsl:text>Functions</xsl:text></b></a>
										</td>
									</tr>
								</xsl:if>
								<tr>
									<td>
										<a href="package.html#{@name}()">
											<xsl:value-of select="@name"/>
											<xsl:text>()</xsl:text>
										</a>
									</td>
								</tr>
								<xsl:if test="position()=last()">
									<tr>
										<td width="10px">
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="classes//asClass[@type='interface']">
								<xsl:sort select="@name" order="ascending"/>

								<xsl:if test="position()=1">
									<tr>
										<td>
											<a href="package-detail.html#interfaceSummary" style="color:black"><b><xsl:text>Interfaces</xsl:text></b></a>
										</td>
									</tr>
								</xsl:if>
								<tr>
									<td>
										<a href="{@name}.html">
											<i>
												<xsl:value-of select="@name"/>
											</i>
										</a>
									</td>
								</tr>
								<xsl:if test="position()=last()">
									<tr>
										<td width="10px">
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="classes//asClass[@type!='interface']">
								<xsl:sort select="@name" order="ascending"/>

								<xsl:if test="position()=1">
									<tr>
										<td>
											<a href="package-detail.html#classSummary" style="color:black"><b><xsl:text>Classes</xsl:text></b></a>
										</td>
									</tr>
								</xsl:if>
								<tr>
									<td>
										<a href="{@name}.html">
											<xsl:value-of select="@name"/>
										</a>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</body>
				</xsl:element>
				<xsl:copy-of select="$copyrightComment"/>
			</redirect:write>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>