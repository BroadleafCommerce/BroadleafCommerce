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
	<xsl:param name="localTitle" select="'All Classes'" />
	<xsl:variable name="title" select="concat($localTitle,' - ',$title-base)"/>
	<xsl:param name="overviewsFile" select="'../xml/overviews.xml'"/>
	<xsl:param name="filter" select="'*'" />
	<xsl:param name="outfile" select="'class-summary'" />

	<xsl:variable name="useFilter">
		<xsl:if test="contains($filter,'*')">
			<xsl:value-of select="substring-before($filter,'*')" />
		</xsl:if>
		<xsl:if test="not(contains($filter,'*'))">
			<xsl:value-of select="$filter" />
		</xsl:if>
	</xsl:variable>

	<xsl:template match="/">
		<xsl:copy-of select="$noLiveDocs" />
		<xsl:copy-of select="$docType" />
		<xsl:element name="html">
			<head>
				<title>
					<xsl:value-of select="$localTitle" />
					<xsl:call-template name="getPageTitlePostFix" />
				</title>
				<xsl:call-template name="getStyleLink">
					<xsl:with-param name="link" select="asdoc/link"/>
				</xsl:call-template>
			</head>
			<xsl:element name="body">
				<xsl:if test="$isEclipse">
					<xsl:attribute name="class">
						<xsl:text>eclipseBody</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="getTitleScript">
					<xsl:with-param name="title" select="$title" />
				</xsl:call-template>
				<xsl:call-template name="getLinks2">
					<xsl:with-param name="subTitle">
						<xsl:call-template name="search-and-replace">
							<xsl:with-param name="input" select="$localTitle" />
							<xsl:with-param name="search-string" select="' '" />
							<xsl:with-param name="replace-string" select="$nbsp" />
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="fileName" select="$outfile"/>
					<xsl:with-param name="showProperties" select="false()"/>
					<xsl:with-param name="showMethods" select="false()"/>
					<xsl:with-param name="showAllClasses" select="false()"/>
				</xsl:call-template>
				<div class="MainContent">
					<br />
					<xsl:variable name="overviews" select="document($overviewsFile)/overviews"/>
					<xsl:if test="string-length($overviews/all-classes/description/.)">
						<p>
							<xsl:value-of disable-output-escaping="yes" select="$overviews/all-classes/description/." />
						</p>
					</xsl:if>
					<xsl:for-each select="$overviews/all-classes">
						<xsl:call-template name="sees">
							<xsl:with-param name="xrefId" select="'all-classes'" />
						</xsl:call-template>
					</xsl:for-each>
					<br />
					<table cellpadding="3" cellspacing="0" class="summaryTable">
						<tr>
							<th>
								<xsl:value-of select="$nbsp" />
							</th>
							<th width="20%">
								<xsl:text>Class</xsl:text>
							</th>
							<th width="20%">
								<xsl:text>Package</xsl:text>
							</th>
							<th width="60%">
								<xsl:text>Description</xsl:text>
							</th>
						</tr>
						<xsl:for-each select="//asClass[starts-with(@packageName,$useFilter) or ($useFilter='flash.' and @packageName='')]">
							<xsl:sort select="@name" order="ascending" data-type="text"/>

							<xsl:variable name="classPath" select="translate(@packageName,'.','/')"/>
							<tr class="prow{position() mod 2}">
								<td class="summaryTablePaddingCol">
									<xsl:value-of select="$nbsp" />
								</td>
								<td class="summaryTableSecondCol">
									<xsl:choose>
										<xsl:when test="$classPath">
											<a href="{$classPath}/{@name}.html">
												<xsl:if test="@type='interface'">
													<i><xsl:value-of select="@name"/></i>
												</xsl:if>
												<xsl:if test="@type!='interface'">
													<xsl:value-of select="@name"/>
												</xsl:if>
											</a>
											<br/>
										</xsl:when>
										<xsl:otherwise>
											<a href="{@name}.html">
												<xsl:if test="@type='interface'">
													<i><xsl:value-of select="@name"/></i>
												</xsl:if>
												<xsl:if test="@type !='interface'">
													<xsl:value-of select="@name"/>
												</xsl:if>
											</a>
											<br/>
										</xsl:otherwise>
									</xsl:choose>
								</td>
								<td class="summaryTableCol">
									<xsl:if test="$classPath">
										<a href="{$classPath}/package-detail.html" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
											<xsl:value-of select="@packageName"/>
										</a>
									</xsl:if>
									<xsl:if test="not($classPath)">
										<a href="package-detail.html" onclick="javascript:loadClassListFrame('class-list.html');">Top Level</a>
									</xsl:if>
								</td>
								<td class="summaryTableLastCol">
									<xsl:if test="deprecated">
										<xsl:apply-templates select="deprecated"/>
									</xsl:if>
									<xsl:if test="not(deprecated)">
										<xsl:if test="string-length(normalize-space(shortDescription/.)) &gt; 0">
											<xsl:value-of select="shortDescription" disable-output-escaping="yes"/>
										</xsl:if>
										<xsl:if test="not(string-length(normalize-space(shortDescription/.)) &gt; 0)">
											<xsl:value-of select="$nbsp" />
										</xsl:if>
									</xsl:if>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					<p/>
<!--				<xsl:call-template name="getLinks">
						<xsl:with-param name="fileName" select="$outfile"/>
						<xsl:with-param name="showProperties" select="false()"/>
						<xsl:with-param name="showMethods" select="false()"/>
						<xsl:with-param name="showAllClasses" select="false()"/>
						<xsl:with-param name="copyNum" select="'2'"/>
					</xsl:call-template>
					<p/>-->
					<center class="copyright">
						<xsl:copy-of select="$copyright"/>
					</center>
				</div>
			</xsl:element>
		</xsl:element>
		<xsl:copy-of select="$copyrightComment"/>
	</xsl:template>
</xsl:stylesheet>