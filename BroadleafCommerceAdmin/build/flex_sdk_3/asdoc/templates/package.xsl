<?xml version="1.0" encoding="utf-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:str="http://exslt.org/strings" xmlns:redirect="http://xml.apache.org/xalan/redirect" extension-element-prefixes="redirect" exclude-result-prefixes="redirect str">

	<xsl:include href="asdoc-util.xsl"/>
	<xsl:include href="class-files.xsl"/>
	<xsl:param name="outputPath" select="'../out/'"/>
	<xsl:param name="packageOverviewFile" select="'../xml/overviews.xml'" />

	<xsl:template match="/">
		<xsl:for-each select="asdoc/packages/asPackage">
			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="@name"/>
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="packageFile">
				<xsl:value-of select="$outputPath"/>
				<xsl:choose>
					<xsl:when test="$isTopLevel='true'">package.html</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate(@name,'.','/')"/>/package.html</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="classListFile">
				<xsl:choose>
					<xsl:when test="$isTopLevel='true'">class-list.html</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate(@name,'.','/')"/>/class-list.html</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="packageName">
				<xsl:choose>
					<xsl:when test="$isTopLevel='true'">Top Level</xsl:when>
					<xsl:otherwise>
						<xsl:text>Package </xsl:text>
						<xsl:value-of select="@name"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="xrefPackageName">
				<xsl:choose>
					<xsl:when test="$isTopLevel='true'">global</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="title">
				<xsl:if test="$isTopLevel='true'">
					<xsl:value-of select="concat('Top Level Constants and Functions - ',$title-base)"/>
				</xsl:if>
				<xsl:if test="not($isTopLevel='true')">
					<xsl:value-of select="concat(@name,' Package - ',$title-base)"/>
				</xsl:if>
			</xsl:variable>

			<xsl:variable name="hasConstants" select="count(fields/field[@isConst='true']) &gt; 0"/>
			<xsl:variable name="hasFields" select="count(fields/field[@isConst='false']) &gt; 0"/>
			<xsl:variable name="hasFunctions" select="count(methods/method) &gt; 0"/>

			<xsl:if test="$hasConstants or $hasFields or $hasFunctions">
				<!-- TODO move this to asdoc-util -->
				<xsl:variable name="pname" select="@name" />
				<redirect:write select="$packageFile">
					<xsl:copy-of select="$docType" />
					<xsl:element name="html">
						<head>
							<xsl:call-template name="getStyleLink">
								<xsl:with-param name="link" select="/asdoc/link"/>
								<xsl:with-param name="packageName" select="@name"/>
							</xsl:call-template>
							<title>
								<xsl:if test="$isTopLevel='true'">
									<xsl:text>Top Level Constants and Functions</xsl:text>
								</xsl:if>
								<xsl:if test="$isTopLevel='false'">
									<xsl:value-of select="@name" />
								</xsl:if>
								<xsl:text> Details</xsl:text>
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
								<xsl:with-param name="packageName" select="@name"/>
							</xsl:call-template>
							<xsl:call-template name="getLinks2">
								<xsl:with-param name="subTitle">
									<xsl:choose>
										<xsl:when test="$isTopLevel='true'">
											<xsl:value-of select="concat('Top',$nbsp,'Level')" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="concat('Package',$nbsp,@name)" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="fileName" select="'package'"/>
								<xsl:with-param name="fileName2" select="$classListFile" />
								<xsl:with-param name="packageName" select="@name"/>
								<xsl:with-param name="showProperties" select="false()"/>
								<xsl:with-param name="showMethods" select="false()"/>
								<xsl:with-param name="showPackageConstants" select="boolean(number($hasConstants))" />
								<xsl:with-param name="showPackageProperties" select="boolean(number($hasFields))" />
								<xsl:with-param name="showPackageFunctions" select="boolean(number($hasFunctions))" />
							</xsl:call-template>
							<div class="MainContent">
								<xsl:apply-templates mode="annotate" select="$config/annotate/item[@type='package' and string-length(@name) and str:tokenize(@name,',')[starts-with($pname,.)]]" />
								<br />
								<xsl:if test="$hasFields">
									<a name="propertySummary" />
									<xsl:if test="not($config/overviews/package)">
										<xsl:variable name="packageComments" select="document($packageOverviewFile)/overviews/packages/package[@name=$pname]" />
										<xsl:call-template name="deTilda">
											<xsl:with-param name="inText" select="$packageComments/propertiesDescription"/>
										</xsl:call-template>
										<xsl:for-each select="$packageComments/propertiesDescription">
											<xsl:call-template name="sees">
												<xsl:with-param name="xrefId" select="concat($xrefPackageName,'#propertySummary')" />
											</xsl:call-template>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="$config/overviews/package">
										<xsl:for-each select="$config/overviews/package">
											<xsl:variable name="packageOverview" select="document(.)/overviews/packages/package[@name=$pname]" />									
											<xsl:if test="$packageOverview/propertiesDescription">
												<xsl:call-template name="deTilda">
													<xsl:with-param name="inText" select="$packageOverview/propertiesDescription" />
												</xsl:call-template>
												<xsl:for-each select="$packageOverview/propertiesDescription">
													<xsl:call-template name="sees">
														<xsl:with-param name="xrefId" select="concat($xrefPackageName,'#propertySummary')" />
													</xsl:call-template>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
									<xsl:call-template name="fieldSummary">
										<xsl:with-param name="isGlobal" select="$isTopLevel='true'" />
										<xsl:with-param name="showAnchor" select="false()" />
									</xsl:call-template>
									<xsl:if test="boolean(number($hasFunctions)) or boolean(number($hasConstants))">
										<br />
										<br />
									</xsl:if>
								</xsl:if>

								<xsl:if test="$hasFunctions">
									<a name="methodSummary" />
									<xsl:variable name="packageComments" select="document($packageOverviewFile)/overviews/packages/package[@name=$pname]" />
									<xsl:if test="not($config/overviews/package)">
										<xsl:call-template name="deTilda">
											<xsl:with-param name="inText" select="$packageComments/functionsDescription"/>
										</xsl:call-template>
										<xsl:for-each select="$packageComments/functionsDescription">
											<xsl:call-template name="sees">
												<xsl:with-param name="xrefId" select="concat($xrefPackageName,'#methodSummary')" />
											</xsl:call-template>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="$config/overviews/package">
										<xsl:for-each select="$config/overviews/package">
											<xsl:variable name="packageOverview" select="document(.)/overviews/packages/package[@name=$pname]" />									
											<xsl:if test="$packageOverview/functionsDescription">
												<xsl:call-template name="deTilda">
													<xsl:with-param name="inText" select="$packageOverview/functionsDescription" />
												</xsl:call-template>
												<xsl:for-each select="$packageOverview/functionsDescription">
													<xsl:call-template name="sees">
														<xsl:with-param name="xrefId" select="concat($xrefPackageName,'#methodSummary')" />
													</xsl:call-template>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
									<xsl:call-template name="methodSummary">
										<xsl:with-param name="className" select="'package'" />
										<xsl:with-param name="title" select="'Functions'" />
										<xsl:with-param name="isGlobal" select="$isTopLevel='true'" />
										<xsl:with-param name="showAnchor" select="false()" />
									</xsl:call-template>
									<xsl:if test="boolean(number($hasConstants))">
										<br />
										<br />
									</xsl:if>
								</xsl:if>

								<xsl:if test="$hasConstants">
									<a name="constantSummary" />
									<xsl:if test="not($config/overviews/package)">
										<xsl:variable name="packageComments" select="document($packageOverviewFile)/overviews/packages/package[@name=$pname]" />
										<xsl:call-template name="deTilda">
											<xsl:with-param name="inText" select="$packageComments/constantsDescription"/>
										</xsl:call-template>
										<xsl:for-each select="$packageComments/constantsDescription">
											<xsl:call-template name="sees">
												<xsl:with-param name="xrefId" select="concat($xrefPackageName,'#constantSummary')" />
											</xsl:call-template>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="$config/overviews/package">
										<xsl:for-each select="$config/overviews/package">
											<xsl:variable name="packageOverview" select="document(.)/overviews/packages/package[@name=$pname]" />									
											<xsl:if test="$packageOverview/constantsDescription">
												<xsl:call-template name="deTilda">
													<xsl:with-param name="inText" select="$packageOverview/constantsDescription" />
												</xsl:call-template>
												<xsl:for-each select="$packageOverview/constantsDescription">
													<xsl:call-template name="sees">
														<xsl:with-param name="xrefId" select="concat($xrefPackageName,'#constantSummary')" />
													</xsl:call-template>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
									<xsl:call-template name="fieldSummary">
										<xsl:with-param name="isConst" select="'true'" />
										<xsl:with-param name="isGlobal" select="$isTopLevel='true'" />
										<xsl:with-param name="showAnchor" select="false()" />
									</xsl:call-template>
								</xsl:if>
						
								<xsl:apply-templates select="fields" mode="detail"/>
								<xsl:apply-templates select="methods" mode="detail">
									<xsl:with-param name="className" select="'package'"/>
									<xsl:with-param name="title" select="'Function detail'"/>
								</xsl:apply-templates>
								<xsl:apply-templates select="fields" mode="detail">
									<xsl:with-param name="isConst" select="'true'" />
								</xsl:apply-templates>
								<p/>
	<!--							<xsl:call-template name="getLinks">
									<xsl:with-param name="fileName" select="'package'"/>
									<xsl:with-param name="fileName2" select="$classListFile" />
									<xsl:with-param name="packageName" select="@name"/>
									<xsl:with-param name="showProperties" select="false()"/>
									<xsl:with-param name="showMethods" select="false()"/>
									<xsl:with-param name="showPackageConstants" select="boolean(number($hasConstants))" />
									<xsl:with-param name="showPackageProperties" select="boolean(number($hasFields))" />
									<xsl:with-param name="showPackageFunctions" select="boolean(number($hasFunctions))" />
									<xsl:with-param name="showInterfaces" select="boolean(count(classes/asClass[@type='interface']))" />
									<xsl:with-param name="showClasses" select="boolean(count(classes/asClass[@type!='interface']))" />
									<xsl:with-param name="copyNum" select="'2'" />
								</xsl:call-template>
								<p/>-->
								<xsl:call-template name="getFeedbackLink">
									<xsl:with-param name="topic" select="$packageName" />
									<xsl:with-param name="filename">
										<xsl:if test="$isTopLevel='true'">package.html</xsl:if>
										<xsl:if test="$isTopLevel!='true'">										
											<xsl:value-of select="translate(@name,'.','/')"/>
											<xsl:text>/package.html</xsl:text>
										</xsl:if>
									</xsl:with-param>
									<xsl:with-param name="filename2">
										<xsl:if test="$isTopLevel='true'">class-list.html</xsl:if>
										<xsl:if test="$isTopLevel!='true'">										
											<xsl:value-of select="translate(@name,'.','/')"/>
											<xsl:text>/class-list.html</xsl:text>
										</xsl:if>
									</xsl:with-param>
								</xsl:call-template>
								<center class="copyright">
									<xsl:copy-of select="$copyright"/>
								</center>
								<xsl:call-template name="addKeywords">
									<xsl:with-param name="keyword">
										<xsl:if test="$isTopLevel='true'">
											<xsl:value-of select="'Top Level'" />
										</xsl:if>
										<xsl:if test="$isTopLevel!='true'">
											<xsl:value-of select="@name" />
										</xsl:if>
									</xsl:with-param>
								</xsl:call-template>
							</div>
						</xsl:element>
					</xsl:element>
				</redirect:write>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>