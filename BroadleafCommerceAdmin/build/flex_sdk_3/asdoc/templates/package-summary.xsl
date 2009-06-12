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
	<xsl:param name="localTitle" select="'All Packages'" />
	<xsl:variable name="title" select="concat($localTitle,' - ',$title-base)"/>
	<xsl:param name="overviewsFile" select="'../xml/overviews.xml'"/>
	<xsl:param name="filter" select="'*'" />
	<xsl:param name="outfile" select="'package-summary'" />
	<xsl:param name="overviewsFile" select="'../xml/overviews.xml'" />

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
					<xsl:with-param name="link" select="asdoc/link" />
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
					<xsl:with-param name="showPackages" select="false()"/>
				</xsl:call-template>	
				<div class="MainContent">			
					<br />
					<xsl:variable name="overviews" select="document($overviewsFile)/overviews"/>
					<xsl:if test="string-length($overviews/all-packages/description/.)">
						<p>
							<xsl:value-of disable-output-escaping="yes" select="$overviews/all-packages/description/." />
						</p>
					</xsl:if>
					<xsl:for-each select="$overviews/all-packages">
						<xsl:call-template name="sees">
							<xsl:with-param name="xrefId" select="'all-packages'" />
						</xsl:call-template>
					</xsl:for-each>
					<br />
					<table cellpadding="3" cellspacing="0" class="summaryTable">
						<tr>
							<th>
								<xsl:value-of select="$nbsp" />
							</th>
							<th width="30%">
								<xsl:text>Package</xsl:text>
							</th>
							<th width="70%">
								<xsl:text>Description</xsl:text>
							</th>
						</tr>
						<xsl:for-each select="asdoc/packages/asPackage[starts-with(@name,$useFilter) or ($useFilter='flash.' and @name='$$Global$$')]">
							<xsl:sort select="@name" order="ascending"/>

							<xsl:variable name="isTopLevel">
								<xsl:call-template name="isTopLevel">
									<xsl:with-param name="packageName" select="@name"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:variable name="packageFile">
								<xsl:if test="$isTopLevel='false'">
									<xsl:value-of select="translate(@name,'.','/')"/>
									<xsl:text>/</xsl:text>
								</xsl:if>
								<xsl:text>package-detail.html</xsl:text>
							</xsl:variable>
							<xsl:variable name="classListFile">
								<xsl:if test="$isTopLevel='false'">
									<xsl:value-of select="translate(@name,'.','/')"/>
									<xsl:text>/</xsl:text>
								</xsl:if>
								<xsl:text>class-list.html</xsl:text>
							</xsl:variable>
							<xsl:if test="classes/asClass or methods/method or fields/field">
								<tr class="prow{position() mod 2}">
									<td class="summaryTablePaddingCol">
										<xsl:value-of select="$nbsp" />
									</td>
									<td class="summaryTableSecondCol">
										<a href="{$packageFile}" onclick="javascript:loadClassListFrame('{$classListFile}');">
											<xsl:if test="$isTopLevel='true'">
												<xsl:text>Top Level</xsl:text>
											</xsl:if>
											<xsl:if test="$isTopLevel='false'">
												<xsl:value-of select="@name"/>
											</xsl:if>
										</a>
									</td>
									<td class="summaryTableLastCol">
										<xsl:if test="not($config/overviews/package)">
											<xsl:variable name="overview" select="normalize-space(document($overviewsFile)/overviews/packages/package[@name=current()/@name]/shortDescription/.)" />
											<xsl:if test="string-length($overview)">
												<xsl:call-template name="deTilda">
													<xsl:with-param name="inText" select="$overview" />
												</xsl:call-template>
											</xsl:if>
											<xsl:if test="not(string-length($overview))">
												<xsl:value-of select="$nbsp" />
											</xsl:if>
										</xsl:if>
										<xsl:if test="$config/overviews/package">
											<xsl:variable name="pname" select="@name" />
											<xsl:for-each select="$config/overviews/package">
												<xsl:variable name="packageOverview" select="normalize-space(document(.)/overviews/packages/package[@name=$pname]/shortDescription/.)" />									
												<xsl:if test="string-length($packageOverview)">
													<xsl:call-template name="deTilda">
														<xsl:with-param name="inText" select="$packageOverview" />
													</xsl:call-template>
												</xsl:if>
											</xsl:for-each>
											<xsl:value-of select="$nbsp" />
										</xsl:if>
									</td>
								</tr>
							</xsl:if>
						</xsl:for-each>
					</table>
					<p/>
<!--					<xsl:call-template name="getLinks">
						<xsl:with-param name="fileName" select="$outfile"/>
						<xsl:with-param name="showProperties" select="false()"/>
						<xsl:with-param name="showMethods" select="false()"/>
						<xsl:with-param name="showPackages" select="false()"/>
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