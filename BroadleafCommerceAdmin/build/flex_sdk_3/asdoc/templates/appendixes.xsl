<?xml version='1.0'?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dyn="http://exslt.org/dynamic">

	<xsl:import href="asdoc-util.xsl" />
	<xsl:param name="title" select="concat('Appendixes - ',$title-base)"/>
	<xsl:param name="overviewsFile" select="'../xml/overviews.xml'"/>

	<xsl:variable name="overviews" select="document($overviewsFile)/overviews" />

	<xsl:template match="/">
		<xsl:copy-of select="$docType" />
		<xsl:element name="html">
			<head>
				<xsl:call-template name="getStyleLink">
					<xsl:with-param name="link" select="/asdoc/link"/>
				</xsl:call-template>
				<title>
					<xsl:text>Appendixes</xsl:text>
					<xsl:call-template name="getPageTitlePostFix" />
				</title>
			</head>
			<xsl:element name="body">
				<xsl:if test="$isEclipse">
					<xsl:attribute name="class">
						<xsl:text>eclipseBody</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="getTitleScript">
					<xsl:with-param name="title" select="$title"/>
				</xsl:call-template>
<!-- 				<xsl:call-template name="getFeedbackLink">
					<xsl:with-param name="topic" select="'Appendixes'"/>
				</xsl:call-template> -->
				<xsl:call-template name="getLinks2">
					<xsl:with-param name="subTitle" select="'Appendixes'" />
					<xsl:with-param name="fileName" select="'appendixes'"/>
					<xsl:with-param name="showProperties" select="false()"/>
					<xsl:with-param name="showMethods" select="false()"/>
					<xsl:with-param name="showAppendixes" select="false()" />
				</xsl:call-template>
				<div class="MainContent">
					<br/>
					<xsl:if test="string-length($overviews/appendixes/description/.)">
						<p>
							<xsl:value-of disable-output-escaping="yes" select="$overviews/appendixes/description/." />
						</p>
					</xsl:if>
					<xsl:for-each select="$overviews/appendixes">
						<xsl:call-template name="sees">
							<xsl:with-param name="xrefId" select="'appendixes'" />
						</xsl:call-template>
					</xsl:for-each>
					<br />
					<table cellpadding="3" cellspacing="0" class="summaryTable">
						<tr>
							<th>
								<xsl:value-of select="$nbsp" />
							</th>
							<th width="25%">
								<xsl:text>Appendix</xsl:text>
							</th>
							<th width="75%">
								<xsl:text>Description</xsl:text>
							</th>
						</tr>
						<xsl:if test="$config/appendixes[@deprecated='true']">
							<tr class="prow0">
								<td class="summaryTablePaddingCol">
									<xsl:value-of select="$nbsp" />
								</td>
								<td class="summaryTableSecondCol">
									<a href="deprecated.html">Deprecated</a>
								</td>
								<td class="summaryTableLastCol">
									<xsl:value-of select="$overviews/deprecated/shortDescription/." />
								</td>
							</tr>
						</xsl:if>
						<xsl:for-each select="$config/appendixes/appendix">
							<tr class="prow{position() mod 2}">
								<td class="summaryTablePaddingCol">
									<xsl:value-of select="$nbsp" />
								</td>
								<td class="summaryTableSecondCol">
									<xsl:element name="a">
										<xsl:attribute name="href">
											<xsl:value-of select="@href" />
										</xsl:attribute>
										<xsl:if test="string-length(@onclick)">
											<xsl:attribute name="onclick">
												<xsl:value-of select="@onclick" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="@label" />
									</xsl:element><!--
									<a href="{@href}"><xsl:value-of select="@label" /></a>-->
								</td>
								<td class="summaryTableLastCol">
									<xsl:variable name="overview" select="dyn:evaluate(concat('$overviews/',@overview,'/shortDescription/.'))" />
									<xsl:if test="string-length(normalize-space($overview))">
										<xsl:value-of select="$overview" />
									</xsl:if>
									<xsl:if test="not(string-length(normalize-space($overview)))">
										<xsl:value-of select="$nbsp" />
									</xsl:if>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					<p></p>
	<!-- 				<xsl:call-template name="getLinks">
						<xsl:with-param name="fileName" select="'appendixes'"/>
						<xsl:with-param name="showProperties" select="false()"/>
						<xsl:with-param name="showMethods" select="false()"/>
						<xsl:with-param name="copyNum" select="'2'"/>
					</xsl:call-template>
					<p/>
					<xsl:call-template name="getFeedbackLink">
						<xsl:with-param name="topic" select="'Appendixes'"/>
					</xsl:call-template> -->
					<center class="copyright">
						<xsl:copy-of select="$copyright"/>
					</center>
				</div>
			</xsl:element>
		</xsl:element>
		<xsl:copy-of select="$copyrightComment"/>
	</xsl:template>

</xsl:stylesheet>