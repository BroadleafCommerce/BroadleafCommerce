<?xml version="1.0" encoding="utf-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:redirect="http://xml.apache.org/xalan/redirect" 
xmlns:str="http://exslt.org/strings" 
xmlns:exslt="http://exslt.org/common" 
extension-element-prefixes="redirect" 
exclude-result-prefixes="redirect str exslt">

	<xsl:import href="asdoc-util.xsl" />
	<xsl:import href="class-parts.xsl" />

	<xsl:param name="outputPath" select="'../out'"/>
	<xsl:param name="showExamples">true</xsl:param>
	<xsl:param name="showIncludeExamples">true</xsl:param>
	<xsl:param name="showSWFs">
		<xsl:if test="$config/options/@showSWFs='false'">
			<xsl:text>false</xsl:text>
		</xsl:if>
		<xsl:if test="not($config/options/@showSWFs='false')">
			<xsl:text>true</xsl:text>
		</xsl:if>
	</xsl:param>
	<xsl:param name="tabSpaces" select="'    '" />
	<xsl:variable name="tab">
		<xsl:text>	</xsl:text>
	</xsl:variable>

	<xsl:template match="/">
		<xsl:for-each select="//asClass">
			<xsl:sort select="@name" order="ascending" />

			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="@packageName"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="isInnerClass" select="ancestor::asClass"/>
			<xsl:variable name="packagePath" select="translate(@packageName, '.', '/')"/>
			<xsl:variable name="classFile">
				<xsl:value-of select="$outputPath"/>
				<xsl:if test="$isTopLevel='false'">
					<xsl:value-of select="$packagePath"/>
					<xsl:text>/</xsl:text>
				</xsl:if>
				<xsl:value-of select="@name"/>
				<xsl:text>.html</xsl:text>
			</xsl:variable>
			<xsl:variable name="title" select="concat(concat(@name,' - '),$title-base)"/>			
			<xsl:variable name="classDeprecated">
				<xsl:if test="deprecated">
					<xsl:value-of select="'true'"/>
				</xsl:if>
				<xsl:if test="not(deprecated)">
					<xsl:value-of select="'false'"/>
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="baseRef">
				<xsl:call-template name="getBaseRef">
					<xsl:with-param name="packageName" select="@packageName" />
				</xsl:call-template>
			</xsl:variable>

			<redirect:write select="$classFile">
				<xsl:copy-of select="$docType"/>
				<xsl:element name="html">
					<head>
						<xsl:call-template name="getStyleLink">
							<xsl:with-param name="link" select="/asdoc/link"/>
							<xsl:with-param name="packageName" select="@packageName"/>
						</xsl:call-template>
						<xsl:call-template name="getKeywords"/>
						<title>
							<xsl:if test="$isTopLevel='false'">
								<xsl:value-of select="@packageName" />
								<xsl:text>.</xsl:text>
							</xsl:if>
							<xsl:value-of select="@name" />
							<xsl:call-template name="getPageTitlePostFix" />
						</title>
						<xsl:if test="$showIncludeExamples='true' and includeExamples/includeExample/codepart">
							<script src="{$baseRef}AC_OETags.js" type="text/javascript"></script>
						</xsl:if>
					</head>
					<xsl:element name="body">
						<xsl:if test="$isEclipse">
							<xsl:attribute name="class">
								<xsl:text>eclipseBody</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="getTitleScript">
							<xsl:with-param name="title" select="$title"/>
							<xsl:with-param name="packageName" select="@packageName"/>
						</xsl:call-template>
<!-- 						<xsl:call-template name="getFeedbackLink">
							<xsl:with-param name="topic" select="@name"/>
						</xsl:call-template> -->

						<xsl:call-template name="classHeader">
							<xsl:with-param name="classDeprecated" select="$classDeprecated" />
						</xsl:call-template>

						<!--  INNER CLASS SUMMARY  -->
<!--						<xsl:call-template name="innerClassSummary">
							<xsl:with-param name="hasInherited" select="count(asAncestors/asAncestor[string-length(@innerClasses) &gt; 0])"/>
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
						</xsl:call-template>-->
						
						<!-- INHERITED INNER CLASSES -->
<!--						<xsl:for-each select="asAncestors/asAncestor">
							<xsl:call-template name="inherited">
								<xsl:with-param name="lowerType">innerClasses</xsl:with-param>
								<xsl:with-param name="upperType">Inner Classes</xsl:with-param>
								<xsl:with-param name="inheritedItems" select="@innerClasses"/>
								<xsl:with-param name="staticItems" select="@innerClasses" />
								<xsl:with-param name="innerClass" select="true()"/>
							</xsl:call-template>
						</xsl:for-each>-->

						<!--  PUBLIC PROPERTY SUMMARY  -->
						<xsl:call-template name="fieldSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!--  PROTECTED PROPERTY SUMMARY  -->
						<xsl:call-template name="fieldSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="accessLevel" select="'protected'" />
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!--  METHOD SUMMARY -->
						<xsl:call-template name="methodSummary">
							<xsl:with-param name="className" select="@name" />
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!--  PROTECTED METHOD SUMMARY -->
						<xsl:call-template name="methodSummary">
							<xsl:with-param name="className" select="@name" />
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
							<xsl:with-param name="accessLevel" select="'protected'" />
						</xsl:call-template>

						<!--  EVENT SUMMARY  -->
						<xsl:call-template name="eventsGeneratedSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!-- STYLE SUMMARY -->
						<xsl:call-template name="stylesSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!-- EFFECT SUMMARY -->
						<xsl:call-template name="effectsSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!--  PUBLIC CONSTANT SUMMARY  -->
						<xsl:call-template name="fieldSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="isConst" select="'true'" />
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!--  PROTECTED CONSTANT SUMMARY  -->
						<xsl:call-template name="fieldSummary">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="isConst" select="'true'" />
							<xsl:with-param name="accessLevel" select="'protected'" />
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:call-template>

						<!--  CONSTRUCTOR SUMMARY  -->
<!--						<xsl:if test="@type != 'interface' and count(constructors/constructor) &gt; 0">
							<br/>
							<br/>
							<a name="constructorSummary"/>
							<div class="summarySection">
								<table cellspacing="0" cellpadding="3" width="100%" class="withBorder" style="border-bottom:none">
									<tr>
										<td colspan="2" bgcolor="#CCCCCC" class="SummaryTableHeader">
											<font size="+1">
												<b>Constructor summary</b>
											</font>
										</td>
									</tr>
								</table>
								<table cellspacing="0" cellpadding="3" width="100%" class="withBorder" style="border-top:none">
									<xsl:apply-templates select="constructors/constructor" mode="summary">
										<xsl:with-param name="isConstructor" select="true"/>
										<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
									</xsl:apply-templates>
								</table>
							</div>
						</xsl:if>-->

						
					<script language="javascript" type="text/javascript">
						<xsl:comment>
							<xsl:text>
</xsl:text>
							<xsl:text>showHideInherited();</xsl:text>
							<xsl:text>
</xsl:text>
						</xsl:comment>
					</script>
					<div class="MainContent"> 

						<!--  PROPERTY DETAIL -->
						<xsl:apply-templates select="fields" mode="detail">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:apply-templates>

						<!-- CONSTRUCTOR DETAIL -->
						<xsl:if test="@type != 'interface' and count(constructors/constructor) &gt; 0">
							<a name="constructorDetail"/>
							<div class="detailSectionHeader">
								<xsl:text>Constructor detail</xsl:text>
							</div>
							<xsl:variable name="className" select="@name"/>
							<xsl:apply-templates select="constructors/constructor[@name = $className]" mode="detail">
								<xsl:with-param name="isConstructor">true</xsl:with-param>
								<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
								<xsl:with-param name="baseRef" select="$baseRef" />
							</xsl:apply-templates>
						</xsl:if>

						<!-- METHOD DETAIL -->
						<xsl:apply-templates select="methods" mode="detail">
							<xsl:with-param name="className" select="@name"/>
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:apply-templates>		

						<!--  EVENT DETAIL  -->
						<xsl:apply-templates select="eventsGenerated" mode="detail">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
						</xsl:apply-templates>

						<!--  CONSTANT DETAIL -->
						<xsl:apply-templates select="fields" mode="detail">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="isConst" select="'true'" />
							<xsl:with-param name="baseRef" select="$baseRef" />
						</xsl:apply-templates>
						
						<!-- INCLUDE EXAMPLES -->                        
 						<xsl:if test="includeExamples/includeExample/codepart">
							<xsl:call-template name="includeExamples"/>
						</xsl:if>
<!-- 						<xsl:if test="includeExamples/includeExample/codepart">
							<a name="includeExamplesSummary"></a>
							<xsl:apply-templates select="includeExamples/includeExample" />
							<p></p>
						</xsl:if> -->
						<br />
						<br />
						<hr />
						<br />
						<xsl:call-template name="getPageLinks">
							<xsl:with-param name="copyNum" select="'2'"/>
						</xsl:call-template>
						<p></p>
						<xsl:call-template name="getFeedbackLink">
							<xsl:with-param name="topic">
								<xsl:if test="$isTopLevel='false'">
									<xsl:value-of select="@packageName" />
									<xsl:text>.</xsl:text>
								</xsl:if>
								<xsl:value-of select="@name"/>
							</xsl:with-param>
							<xsl:with-param name="filename">
								<xsl:if test="$isTopLevel='false'">
									<xsl:value-of select="$packagePath"/>
									<xsl:text>/</xsl:text>
								</xsl:if>
								<xsl:value-of select="@name"/>
								<xsl:text>.html</xsl:text>
							</xsl:with-param>
							<xsl:with-param name="filename2">
								<xsl:if test="$isTopLevel='true'">class-list.html</xsl:if>
								<xsl:if test="$isTopLevel!='true'">
									<xsl:value-of select="$packagePath" />
									<xsl:text>/class-list.html</xsl:text>
								</xsl:if>
							</xsl:with-param>
						</xsl:call-template>
						<center class="copyright">
							<xsl:copy-of select="$copyright"/>
						</center>
						<xsl:call-template name="addKeywords">
							<xsl:with-param name="keyword" select="@name" />
						</xsl:call-template>
						<xsl:if test="$isTopLevel='false'">
							<xsl:call-template name="addKeywords">
								<xsl:with-param name="keyword">
									<xsl:value-of select="@packageName" />
									<xsl:text>.</xsl:text>
									<xsl:value-of select="@name" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
					</div>
					</xsl:element>
				</xsl:element>
				<xsl:copy-of select="$copyrightComment"/>
			</redirect:write>
		</xsl:for-each>
	</xsl:template>

<!--	<xsl:template name="getAncestors">
		<xsl:for-each select="asAncestors/asAncestor">
			<xsl:variable name="fullname" select="classRef/@fullName" />
<xsl:message>fullname=<xsl:value-of select="$fullname" /></xsl:message>
			<asAncestor>
				<xsl:copy-of select="classRef" />
				<xsl:if test="string-length(concat(@constants,@staticConstants,@properties,@staticProperties))">
					<fields>
						<xsl:for-each select="str:tokenize(@properties,';')">
							<xsl:variable name="elementName" select="." />
<xsl:message>adding <xsl:value-of select="/asdoc/packages/asPackage/classes/asClass[@fullname='Object']/@name" /></xsl:message>
							<xsl:copy-of select="//asClass[@fullname=$fullname]/fields/field[@name=$elementName]" />
						</xsl:for-each>
					</fields>
				</xsl:if>
			</asAncestor>
		</xsl:for-each>
	</xsl:template>-->

	<!-- INNER CLASSES -->
	<xsl:template name="innerClassSummary">
		<xsl:param name="hasInherited" select="false"/>
		<xsl:param name="classDeprecated" select="false"/>

		<xsl:if test="count(classes/asClass) &gt; 0 or boolean($hasInherited)">
			<br/>
			<br/>
			<a name="innerClassSummary"/>
			<table cellspacing="0" cellpadding="3" width="100%" class="withBorder">
				<tr>
					<td colspan="2" bgcolor="#CCCCCC" class="SummaryTableHeader">
						<font size="+1">
							<b>Inner Class summary</b>
						</font>
					</td>
				</tr>
				<xsl:for-each select="classes/asClass">
					<xsl:sort select="@name" order="ascending"/>

					<tr class="row{position() mod 2}">
						<td width="50px" valign="top">
							<code>
								<font size="1" style="font-weight:bold">
									<xsl:if test="@isFinal='true'">
										<xsl:text>final </xsl:text>
									</xsl:if>
									<xsl:if test="@isDynamic='true'">
										<xsl:text>dynamic </xsl:text>
									</xsl:if>
								</font>
							</code>
						</td>
						<td valign="top">
							<code>
								<a href="{@name}.html">
									<b>
										<xsl:value-of select="@name"/>
									</b>
								</a>
							</code>
							<xsl:if test="deprecated">
								<br/>
							</xsl:if>
							<xsl:apply-templates select="deprecated"/>
							<xsl:if test="not(deprecated)">
								<xsl:call-template name="shortDescription">
									<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- FIELDS -->
	<xsl:template name="fieldSummary">
		<xsl:param name="classDeprecated" select="false()"/>
		<xsl:param name="isConst" select="'false'" />
		<xsl:param name="accessLevel" select="'public'" />
		<xsl:param name="baseRef" select="''" />
		<xsl:param name="isGlobal" select="false()" />
		<xsl:param name="showAnchor" select="true()" />

		<xsl:variable name="hasFields" select="count(fields/field[@isConst=$isConst and (@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.)]) &gt; 0" />
		<xsl:variable name="hasInherited" select="count(asAncestors/asAncestor/fields/field[@isConst=$isConst and (@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.)]) &gt; 0" />

		<xsl:if test="$hasFields or $hasInherited">
			<xsl:if test="$showAnchor">
				<xsl:variable name="hasPublic">
					<xsl:if test="$accessLevel='protected'">
						<xsl:value-of select="count(fields/field[@isConst=$isConst and (@accessLevel='public' or @accessLevel=$config/namespaces/namespace[@summaryDisplay='public']/.)]) &gt; 0 or count(asAncestors/asAncestor/fields/field[@isConst=$isConst and (@accessLevel='public' or @accessLevel=$config/namespaces/namespace[@summaryDisplay='public']/.)]) &gt; 0" />
					</xsl:if>
					<xsl:if test="not($accessLevel='protected')">
						<xsl:value-of select="false()" />
					</xsl:if>
				</xsl:variable>
				<xsl:if test="$isConst='true'">
					<xsl:if test="$accessLevel='public'">
						<a name="constantSummary" />
					</xsl:if>
					<xsl:if test="$accessLevel='protected'">
						<xsl:if test="$hasPublic='false'">
							<a name="constantSummary" />
						</xsl:if>
						<a name="protectedConstantSummary" />
					</xsl:if>
				</xsl:if>
				<xsl:if test="$isConst='false'">
					<xsl:if test="$accessLevel='public'">
						<a name="propertySummary" />
					</xsl:if>
					<xsl:if test="$accessLevel='protected'">
						<xsl:if test="$hasPublic='false'">
							<a name="propertySummary" />
						</xsl:if>
						<a name="protectedPropertySummary" />
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<div class="summarySection">
				<div class="summaryTableTitle">
					<xsl:choose>
						<xsl:when test="$isGlobal">
							<xsl:text>Global </xsl:text>
						</xsl:when>
						<xsl:when test="$accessLevel='public'">
							<xsl:text>Public </xsl:text>
						</xsl:when>
						<xsl:when test="$accessLevel='protected'">
							<xsl:text>Protected </xsl:text>
						</xsl:when>
					</xsl:choose>
					<xsl:if test="$isConst='true'">
						<xsl:text>Constants</xsl:text>
					</xsl:if>
					<xsl:if test="$isConst='false'">
						<xsl:text>Properties</xsl:text>
					</xsl:if>
				</div>
				<xsl:if test="$hasInherited">
					<div class="showHideLinks">
						<xsl:if test="$isConst='true' and $accessLevel!='protected'">
							<div id="hideInheritedConstant" class="hideInheritedConstant">
								<a class="showHideLink" href="#constantSummary" onclick="javascript:setInheritedVisible(false,'Constant');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Public Constants</a>
							</div>
							<div id="showInheritedConstant" class="showInheritedConstant">
								<a class="showHideLink" href="#constantSummary" onclick="javascript:setInheritedVisible(true,'Constant');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Public Constants</a>
							</div>
						</xsl:if>
						<xsl:if test="$isConst='true' and $accessLevel='protected'">
							<div id="hideInheritedProtectedConstant" class="hideInheritedProtectedConstant">
								<a class="showHideLink" href="#protectedConstantSummary" onclick="javascript:setInheritedVisible(false,'ProtectedConstant');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Protected Constants</a>
							</div>
							<div id="showInheritedProtectedConstant" class="showInheritedProtectedConstant">
								<a class="showHideLink" href="#protectedConstantSummary" onclick="javascript:setInheritedVisible(true,'ProtectedConstant');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Protected Constants</a>
							</div>
						</xsl:if>
						<xsl:if test="$isConst='false' and $accessLevel!='protected'">
							<div id="hideInheritedProperty" class="hideInheritedProperty">
								<a class="showHideLink" href="#propertySummary" onclick="javascript:setInheritedVisible(false,'Property');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Public Properties</a>
							</div>
							<div id="showInheritedProperty" class="showInheritedProperty">
								<a class="showHideLink" href="#propertySummary" onclick="javascript:setInheritedVisible(true,'Property');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Public Properties</a>
							</div>
						</xsl:if>
						<xsl:if test="$isConst='false' and $accessLevel='protected'">
							<div id="hideInheritedProtectedProperty" class="hideInheritedProtectedProperty">
								<a class="showHideLink" href="#protectedPropertySummary" onclick="javascript:setInheritedVisible(false,'ProtectedProperty');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Protected Properties</a>
							</div>
							<div id="showInheritedProtectedProperty" class="showInheritedProtectedProperty">
								<a class="showHideLink" href="#protectedPropertySummary" onclick="javascript:setInheritedVisible(true,'ProtectedProperty');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Protected Properties</a>
							</div>
						</xsl:if>
					</div>
				</xsl:if>
				<xsl:variable name="tableStyle">
					<xsl:if test="$hasInherited and not($hasFields)">
						<xsl:text>hideInherited</xsl:text>
						<xsl:if test="$accessLevel='protected'">
							<xsl:text>Protected</xsl:text>
						</xsl:if>
						<xsl:if test="$isConst='true'">
							<xsl:text>Constant</xsl:text>
						</xsl:if>
						<xsl:if test="$isConst='false'">
							<xsl:text>Property</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:variable>
				<xsl:variable name="tableId">
					<xsl:text>summaryTable</xsl:text>
					<xsl:if test="$accessLevel='protected'">
						<xsl:text>Protected</xsl:text>
					</xsl:if>
					<xsl:if test="$isConst='true'">
						<xsl:text>Constant</xsl:text>
					</xsl:if>
					<xsl:if test="$isConst='false'">
						<xsl:text>Property</xsl:text>
					</xsl:if>
				</xsl:variable>
				<table cellspacing="0" cellpadding="3" class="summaryTable {$tableStyle}" id="{$tableId}">
					<tr>
						<th>
							<xsl:value-of select="$nbsp" />
						</th>
						<th colspan="2">
							<xsl:if test="$isConst='false'">
								<xsl:text>Property</xsl:text>
							</xsl:if>
							<xsl:if test="$isConst='true'">
								<xsl:text>Constant</xsl:text>
							</xsl:if>
						</th>
                        <xsl:if test="not($config/options/@docversion='2')">
							<th class="summaryTableOwnerCol">
								<xsl:value-of select="concat('Defined',$nbsp,'by')" />
							</th>
                        </xsl:if>
					</tr>
					<xsl:for-each select="fields/field[@isConst=$isConst and (@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.)] | asAncestors/asAncestor/fields/field[@isConst=$isConst and (@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.)]">
						<xsl:sort select="translate(@name,'_','')" order="ascending" data-type="text"/>
	<!--					<tr class="row{position() mod 2}">-->
						<xsl:variable name="rowStyle">
							<xsl:if test="ancestor::asAncestor">
								<xsl:text>hideInherited</xsl:text>
								<xsl:if test="$accessLevel='protected'">
									<xsl:text>Protected</xsl:text>
								</xsl:if>
								<xsl:if test="$isConst='true'">
									<xsl:text>Constant</xsl:text>
								</xsl:if>
								<xsl:if test="$isConst='false'">
									<xsl:text>Property</xsl:text>
								</xsl:if>
							</xsl:if>
						</xsl:variable>
						<tr class="{$rowStyle}">
							<td class="summaryTablePaddingCol">
								<xsl:value-of select="$nbsp" />
							</td>
							<td class="summaryTableInheritanceCol">
								<xsl:if test="ancestor::asAncestor">
									<img src="{$baseRef}images/inheritedSummary.gif" alt="Inherited" title="Inherited" class="inheritedSummaryImage" />
								</xsl:if>
								<xsl:if test="not(ancestor::asAncestor)">
									<xsl:value-of select="$nbsp" />
								</xsl:if>
							</td>
							<td class="summaryTableSignatureCol">
								<xsl:choose>
									<xsl:when test="ancestor::asAncestor">
										<a href="{ancestor::asAncestor/classRef/@relativePath}#{@name}" class="signatureLink">
											<xsl:value-of select="@name" />
										</a>
									</xsl:when>
									<xsl:when test="ancestor::asClass or ancestor::asPackage">
										<a href="#{@name}" class="signatureLink">
											<xsl:value-of select="@name"/>
										</a>
									</xsl:when>
								</xsl:choose>
								<xsl:if test="@type">
									<xsl:text> : </xsl:text>
									<xsl:choose>
										<xsl:when test="classRef">
											<a href="{classRef/@relativePath}">
												<xsl:call-template name="getSimpleClassName">
													<xsl:with-param name="fullClassName" select="@type"/>
												</xsl:call-template>
											</a>
										</xsl:when>
										<xsl:when test="@type='' or @type='*'">
											<xsl:call-template name="getSpecialTypeLink">
												<xsl:with-param name="type" select="'*'" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="getSimpleClassName">
												<xsl:with-param name="fullClassName" select="@type"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>
								<xsl:if test="(string-length(@defaultValue) or @type='String') and @defaultValue!='unknown'">
									<xsl:text> = </xsl:text>
									<xsl:if test="@type='String'">
										<xsl:text>"</xsl:text>
									</xsl:if>
									<xsl:value-of select="@defaultValue" />
									<xsl:if test="@type='String'">
										<xsl:text>"</xsl:text>
									</xsl:if>
								</xsl:if>
								<div class="summaryTableDescription">
									<xsl:apply-templates select="deprecated"/>
									<xsl:if test="not(deprecated)">
										<xsl:if test="@isStatic='true'">
											<xsl:text>[static]</xsl:text>
										</xsl:if>
										<xsl:if test="string-length(@only) and not(@only='read-write')">
											<xsl:text>[</xsl:text>
											<xsl:value-of select="@only"/>
											<xsl:text>-only</xsl:text>
											<xsl:text>]</xsl:text>
										</xsl:if>
										<xsl:call-template name="shortDescription">
											<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
										</xsl:call-template>
									</xsl:if>
								</div>
							</td>
							<xsl:if test="not($config/options/@docversion='2')">
							<td class="summaryTableOwnerCol">
								<xsl:choose>
									<xsl:when test="ancestor::asAncestor">
										<a href="{ancestor::asAncestor/classRef/@relativePath}">
											<xsl:value-of select="ancestor::asAncestor/classRef/@name" />
										</a>
									</xsl:when>
									<xsl:when test="ancestor::asClass">
										<xsl:value-of select="ancestor::asClass/@name" />
									</xsl:when>
									<xsl:when test="ancestor::asPackage">
										<xsl:if test="ancestor::asPackage/@name='$$Global$$'">
											<xsl:value-of select="concat('Top',$nbsp,'Level')" />
										</xsl:if>
										<xsl:if test="ancestor::asPackage/@name!='$$Global$$'">
											<xsl:value-of select="ancestor::asPackage/@name" />
										</xsl:if>
									</xsl:when>
								</xsl:choose>
							</td>
							</xsl:if>
							<!-- AS2 INHERITED PROPERTIES -->
							<xsl:if test="$config/options/@docversion='2'">
								<xsl:for-each select="asAncestors/asAncestor">
								    <xsl:call-template name="inherited">
									    <xsl:with-param name="lowerType">properties</xsl:with-param>
									    <xsl:with-param name="upperType">Properties</xsl:with-param>
									    <xsl:with-param name="inheritedItems" select="@properties" />
									    <xsl:with-param name="staticItems" select="@staticProperties" />
									</xsl:call-template>
8       	                    </xsl:for-each>
            			    </xsl:if>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="fields" mode="detail">
		<xsl:param name="classDeprecated" select="'false'"/>
		<xsl:param name="isConst" select="'false'" />
		<xsl:param name="baseRef" />

		<xsl:if test="count(field[@isConst=$isConst]) &gt; 0">
			<xsl:if test="$isConst='true'">
				<a name="constantDetail" />
			</xsl:if>
			<xsl:if test="not($isConst='true')">
				<a name="propertyDetail" />
			</xsl:if>
<!--			<table cellspacing="0" cellpadding="2" width="100%" class="withBorder">
				<tr>
					<td colspan="2" bgcolor="#CCCCCC" class="SummaryTableHeader">
						<font size="+1">
							<xsl:if test="$isConst='true'">
								<b>Constant detail</b>
							</xsl:if>
							<xsl:if test="not($isConst='true')">
								<b>Property detail</b>
							</xsl:if>
						</font>
					</td>
				</tr>
			</table>-->
			<div class="detailSectionHeader">
				<xsl:if test="$isConst='true'">
					<xsl:text>Constant detail</xsl:text>
				</xsl:if>
				<xsl:if test="not($isConst='true')">
					<xsl:text>Property detail</xsl:text>
				</xsl:if>
			</div>
			<xsl:for-each select="field[@isConst=$isConst]">
				<xsl:sort select="translate(@name,'_','')" order="ascending"/>
				<a name="{@name}"/>
				<table class="detailHeader" cellpadding="0" cellspacing="0">
					<tr>
						<td class="detailHeaderName">
							<xsl:value-of select="@name" />
						</td>
						<td class="detailHeaderType">
							<xsl:if test="@isConst='true'">
								<xsl:text>constant</xsl:text>
							</xsl:if>
							<xsl:if test="@isConst!='true'">
								<xsl:text>property</xsl:text>	
							</xsl:if>
						</td>
						<xsl:if test="position()!=1">
							<td class="detailHeaderRule">
								<xsl:value-of select="$nbsp" />
							</td>
						</xsl:if>
					</tr>
				</table>
<!--				<div class="detailHeader">
					<xsl:value-of select="@name"/>
					<xsl:if test="@isConst='true'">
						<xsl:text> constant</xsl:text>
					</xsl:if>
					<xsl:if test="@isConst!='true'">
						<xsl:text> property</xsl:text>
					</xsl:if>
				</div>-->
				<div class="detailBody">
					<code>
						<xsl:if test="string-length(@only)">
							<xsl:value-of select="@name" />
						</xsl:if>
						<xsl:if test="not(string-length(@only))">
							<xsl:call-template name="getNamespaceLink">
								<xsl:with-param name="accessLevel" select="@accessLevel" />
								<xsl:with-param name="baseRef" select="$baseRef" />
							</xsl:call-template>
							<xsl:text> </xsl:text>
							<xsl:if test="@isStatic='true'">
								<xsl:text>static </xsl:text>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="@isConst='true' and $config/options/@docversion='3'">
									<xsl:text>const </xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>var </xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:value-of select="@name"/>
						</xsl:if>
						<xsl:if test="@type">
							<xsl:text>:</xsl:text>
							<xsl:choose>
								<xsl:when test="classRef">
									<a href="{classRef/@relativePath}">
										<xsl:call-template name="getSimpleClassName">
											<xsl:with-param name="fullClassName" select="@type"/>
										</xsl:call-template>
									</a>
								</xsl:when>
								<xsl:when test="@type='' or @type='*'">
									<xsl:call-template name="getSpecialTypeLink">
										<xsl:with-param name="type" select="'*'" />
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="getSimpleClassName">
										<xsl:with-param name="fullClassName" select="@type"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="(string-length(@defaultValue) or @type='String') and @defaultValue!='unknown'">
							<xsl:text> = </xsl:text>
							<xsl:if test="@type='String'">
								<xsl:text>"</xsl:text>
							</xsl:if>
							<xsl:value-of select="@defaultValue" />
							<xsl:if test="@type='String'">
								<xsl:text>"</xsl:text>
							</xsl:if>
						</xsl:if>
					</code>
					<xsl:if test="string-length(@only)">
						<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;&nbsp;]]>[</xsl:text>
						<xsl:value-of select="@only"/>
						<xsl:if test="not(@only='read-write')">
							<xsl:text>-only</xsl:text>
						</xsl:if>
						<xsl:text>]</xsl:text>
					</xsl:if>

	<!-- 					<xsl:if test="string-length(@only)">
						<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;&nbsp;]]>[</xsl:text>
						<xsl:value-of select="@only"/>
						<xsl:if test="not(@only='read-write')">
							<xsl:text>-only</xsl:text>
						</xsl:if>
						<xsl:text>]</xsl:text>
					</xsl:if>
					<br /> -->
					<xsl:apply-templates select="deprecated"/>
	<!-- 				<xsl:if test="deprecated">
						<br />
					</xsl:if> -->
					<xsl:if test="$classDeprecated='true'">
						<xsl:call-template name="description">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="addParagraphTags" select="true()" />
						</xsl:call-template>
					</xsl:if>
					<xsl:call-template name="version"/>
					<xsl:if test="$classDeprecated!='true'">
						<xsl:call-template name="description">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="addParagraphTags" select="true()" />
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="customs/default">
						<p>
							<xsl:text>The default value is </xsl:text>
							<code>
								<xsl:value-of select="normalize-space(customs/default/.)" />
							</code>
							<xsl:text>.</xsl:text>
						</p>
					</xsl:if>
					<xsl:if test="@isBindable='true'">
						<p>
							<xsl:text>This property can be used as the source for data binding.</xsl:text>
						</p>
					</xsl:if>

					<xsl:if test="string-length(@only)">
						<span class="label">Implementation</span>
						<br />
						<xsl:if test="contains(@only,'read')">
							<code>
								<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;&nbsp;&nbsp;&nbsp;]]></xsl:text>
								<xsl:call-template name="getNamespaceLink">
									<xsl:with-param name="accessLevel" select="@accessLevel" />
									<xsl:with-param name="baseRef" select="$baseRef" />
								</xsl:call-template>
								<xsl:text> </xsl:text>
								<xsl:if test="@isStatic='true'">
									<xsl:text>static </xsl:text>
								</xsl:if>
								<xsl:text>function get </xsl:text>
								<xsl:value-of select="@name" />
								<xsl:text>():</xsl:text>
								<xsl:choose>
									<xsl:when test="classRef">
										<a href="{classRef/@relativePath}">
											<xsl:call-template name="getSimpleClassName">
												<xsl:with-param name="fullClassName" select="@type"/>
											</xsl:call-template>
										</a>
									</xsl:when>
									<xsl:when test="@type='' or @type='*'">
										<xsl:call-template name="getSpecialTypeLink">
											<xsl:with-param name="type" select="'*'" />
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="getSimpleClassName">
											<xsl:with-param name="fullClassName" select="@type"/>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</code>
							<br />
<!--							<xsl:if test="not(contains(@only,'write'))">
								<br />
							</xsl:if>-->
						</xsl:if>
						<xsl:if test="contains(@only,'write')">
<!--							<xsl:if test="contains(@only,'read')">
								<div style="height:0px"></div>
							</xsl:if>-->
							<code>
								<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;&nbsp;&nbsp;&nbsp;]]></xsl:text>
								<xsl:call-template name="getNamespaceLink">
									<xsl:with-param name="accessLevel" select="@accessLevel" />
									<xsl:with-param name="baseRef" select="$baseRef" />
								</xsl:call-template>
								<xsl:text> </xsl:text>
								<xsl:text>function set </xsl:text>
								<xsl:value-of select="@name" />
								<xsl:text>(value:</xsl:text>
								<xsl:choose>
									<xsl:when test="classRef">
										<a href="{classRef/@relativePath}">
											<xsl:call-template name="getSimpleClassName">
												<xsl:with-param name="fullClassName" select="@type"/>
											</xsl:call-template>
										</a>
									</xsl:when>
									<xsl:when test="@type='' or @type='*'">
										<xsl:call-template name="getSpecialTypeLink">
											<xsl:with-param name="type" select="'*'" />
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="getSimpleClassName">
											<xsl:with-param name="fullClassName" select="@type"/>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:text>):</xsl:text>
								<xsl:call-template name="getSpecialTypeLink">
									<xsl:with-param name="type" select="'void'" />
								</xsl:call-template>
							</code>
							<br />
						</xsl:if>
					</xsl:if>

					<xsl:if test="canThrow">
						<br />
						<span class="label">
							<xsl:text>Throws</xsl:text>
						</span>
						<br/>
						<table cellpadding="0" cellspacing="0" border="0">
							<xsl:apply-templates select="canThrow"/>
						</table>
					</xsl:if>

					<xsl:call-template name="sees" />
					<xsl:apply-templates select="example | includeExamples/includeExample[codepart]"/>
				</div>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<!-- STYLES -->
	<xsl:template name="stylesSummary">
		<xsl:param name="classDeprecated" select="'false'" />
		<xsl:param name="baseRef" select="''" />

		<xsl:variable name="hasStyles" select="count(styles/style) &gt; 0" />
		<xsl:variable name="hasInherited" select="count(asAncestors/asAncestor/styles/style) &gt; 0" />
		<xsl:if test="$hasStyles or $hasInherited">
			<a name="styleSummary" />
			<div class="summarySection">
				<div class="summaryTableTitle">
					<xsl:text>Styles</xsl:text>
				</div>
				<xsl:if test="$hasInherited">
					<div class="showHideLinks">
						<div id="hideInheritedStyle" class="hideInheritedStyle">
							<a class="showHideLink" href="#styleSummary" onclick="javascript:setInheritedVisible(false,'Style');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Styles</a>
						</div>
						<div id="showInheritedStyle" class="showInheritedStyle">
							<a class="showHideLink" href="#styleSummary" onclick="javascript:setInheritedVisible(true,'Style');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Styles</a>
						</div>
					</div>
				</xsl:if>
				<xsl:variable name="tableStyle">
					<xsl:if test="$hasInherited and not($hasStyles)">
						<xsl:text>hideInheritedStyle</xsl:text>
					</xsl:if>
				</xsl:variable>
				<table cellspacing="0" cellpadding="3" class="summaryTable {$tableStyle}" id="summaryTableStyle">
					<tr>
						<th>
							<xsl:value-of select="$nbsp" />
						</th>
						<th colspan="2">Style</th>
						<th>Description</th>
						<th class="summaryTableOwnerCol">
							<xsl:value-of select="concat('Defined',$nbsp,'by')" />
						</th>
					</tr>

					<xsl:for-each select="styles/style | asAncestors/asAncestor/styles/style">
						<xsl:sort select="@name" order="ascending" data-type="text"/>

						<xsl:variable name="rowStyle">
							<xsl:if test="ancestor::asAncestor">
								<xsl:text>hideInheritedStyle</xsl:text>
							</xsl:if>
						</xsl:variable>
						<tr class="{$rowStyle}">
							<td class="summaryTablePaddingCol">
								<xsl:if test="not(ancestor::asAncestor)">							
									<a name="style:{@name}" />
								</xsl:if>
								<xsl:value-of select="$nbsp" />
							</td>
							<td class="summaryTableInheritanceCol">
								<xsl:if test="ancestor::asAncestor">
									<img src="{$baseRef}images/inheritedSummary.gif" alt="Inherited" title="Inherited" class="inheritedSummaryImage" />
								</xsl:if>
								<xsl:if test="not(ancestor::asAncestor)">
									<xsl:value-of select="$nbsp" />
								</xsl:if>
							</td>
							<td class="summaryTableSignatureCol">
								<div class="summarySignature">
									<xsl:choose>
										<xsl:when test="ancestor::asAncestor">
											<a href="{ancestor::asAncestor/classRef/@relativePath}#style:{@name}" class="signatureLink">
												<xsl:value-of select="@name" />
											</a>
										</xsl:when>
										<xsl:when test="ancestor::asClass">
											<span class="signatureLink">
												<xsl:value-of select="@name"/>
											</span>
										</xsl:when>
									</xsl:choose>
								</div>
							</td>
							<td class="summaryTableDescription">
								<xsl:if test="string-length(normalize-space(@type)) &gt; 0">
									<span class="label">Type: </span>
										<xsl:if test="string-length(@typeHref)">
											<xsl:variable name="baseRef">
												<xsl:call-template name="getBaseRef">
													<xsl:with-param name="packageName" select="../../@packageName" />
												</xsl:call-template>
												<xsl:if test="contains(@type,'.')">
													<xsl:variable name="package">
														<xsl:call-template name="substring-before-last">
															<xsl:with-param name="input" select="@type" />
															<xsl:with-param name="substr" select="'.'" />
														</xsl:call-template>
													</xsl:variable>
													<xsl:value-of select="translate($package,'.','/')" />
													<xsl:text>/</xsl:text>
												</xsl:if>
											</xsl:variable>
											<a href="{@typeHref}" onclick="loadClassListFrame('{$baseRef}class-list.html')">
												<xsl:value-of select="normalize-space(@type)"/>
											</a>
										</xsl:if>
										<xsl:if test="not(string-length(@typeHref))">
											<xsl:if test="@type='' or @type='*'">
												<xsl:call-template name="getSpecialTypeLink">
													<xsl:with-param name="type" select="'*'" />
												</xsl:call-template>
											</xsl:if>
											<xsl:if test="@type!='' and @type!='*'">
												<xsl:value-of select="normalize-space(@type)" />
											</xsl:if>
										</xsl:if>
									<xsl:if test="string-length(normalize-space(@format)) &gt; 0 or string-length(normalize-space(@inherit)) &gt; 0">
										<xsl:text disable-output-escaping="yes"> <![CDATA[&nbsp;]]> </xsl:text>
									</xsl:if>
								</xsl:if>
								<xsl:if test="string-length(normalize-space(@format)) &gt; 0">
									<span class="label">Format: </span>
									<xsl:value-of select="normalize-space(@format)"/>
									<xsl:if test="string-length(normalize-space(@inherit)) &gt; 0">
										<xsl:text disable-output-escaping="yes"> <![CDATA[&nbsp;]]> </xsl:text>
									</xsl:if>
								</xsl:if>
								<xsl:if test="string-length(normalize-space(@inherit)) &gt; 0">
									<span class="label">CSS Inheritance: </span>
									<xsl:value-of select="normalize-space(@inherit)"/>
								</xsl:if>
								<br/>
								<xsl:if test="not(ancestor::asAncestor)">
									<xsl:call-template name="shortDescriptionReview" />
									<xsl:call-template name="deTilda">
										<xsl:with-param name="inText" select="description/."/>
									</xsl:call-template>
									<xsl:if test="default">
											<xsl:text> The default value is </xsl:text>
											<code>
												<xsl:value-of select="normalize-space(default/.)" />
											</code>
											<xsl:text>.</xsl:text>
									</xsl:if>
								</xsl:if>
								<xsl:if test="ancestor::asAncestor and string-length(shortDescription/.)">
									<xsl:call-template name="deTilda">
										<xsl:with-param name="inText" select="shortDescription" />
									</xsl:call-template>
								</xsl:if>
							</td>
							<td class="summaryTableOwnerCol">
								<xsl:choose>
									<xsl:when test="ancestor::asAncestor">
										<a href="{ancestor::asAncestor/classRef/@relativePath}">
											<xsl:value-of select="ancestor::asAncestor/classRef/@name" />
										</a>
									</xsl:when>
									<xsl:when test="ancestor::asClass">
										<xsl:value-of select="ancestor::asClass/@name" />
									</xsl:when>
								</xsl:choose>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- EFFECTS -->
	<xsl:template name="effectsSummary">
		<xsl:param name="classDeprecated" select="'false'"/>
		<xsl:param name="baseRef" select="''" />

		<xsl:variable name="hasEffects" select="count(effects/effect) &gt; 0" />
		<xsl:variable name="hasInherited" select="count(asAncestors/asAncestor/effects/effect) &gt; 0" />
		<xsl:if test="$hasEffects or $hasInherited">
			<a name="effectSummary" />
			<div class="summarySection">
				<div class="summaryTableTitle">
					<xsl:text>Effects</xsl:text>
				</div>
				<xsl:if test="$hasInherited">
					<div class="showHideLinks">
						<div id="hideInheritedEffect" class="hideInheritedEffect">
							<a class="showHideLink" href="#effectSummary" onclick="javascript:setInheritedVisible(false,'Effect');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Effects</a>
						</div>
						<div id="showInheritedEffect" class="showInheritedEffect">
							<a class="showHideLink" href="#effectSummary" onclick="javascript:setInheritedVisible(true,'Effect');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Effects</a>
						</div>
					</div>
				</xsl:if>
				<xsl:variable name="tableStyle">
					<xsl:if test="$hasInherited and not($hasEffects)">
						<xsl:text>hideInheritedEffect</xsl:text>
					</xsl:if>
				</xsl:variable>
				<table cellspacing="0" cellpadding="3" class="summaryTable {$tableStyle}" id="summaryTableEffect">
					<tr>
						<th>
							<xsl:value-of select="$nbsp" />
						</th>
						<th colspan="2">Effect</th>
						<th>Description</th>
						<th class="summaryTableOwnerCol">
							<xsl:value-of select="concat('Defined',$nbsp,'by')" />
						</th>
					</tr>

					<xsl:for-each select="effects/effect | asAncestors/asAncestor/effects/effect">
						<xsl:sort select="@name" order="ascending" data-type="text"/>

						<xsl:variable name="rowStyle">
							<xsl:if test="ancestor::asAncestor">
								<xsl:text>hideInheritedEffect</xsl:text>
							</xsl:if>
						</xsl:variable>
						<tr class="{$rowStyle}">
							<td class="summaryTablePaddingCol">
								<xsl:if test="not(ancestor::asAncestor)">							
									<a name="effect:{@name}" />
								</xsl:if>
								<xsl:value-of select="$nbsp" />
							</td>
							<td class="summaryTableInheritanceCol">
								<xsl:if test="ancestor::asAncestor">
									<img src="{$baseRef}images/inheritedSummary.gif" alt="Inherited" title="Inherited" class="inheritedSummaryImage" />
								</xsl:if>
								<xsl:if test="not(ancestor::asAncestor)">
									<xsl:value-of select="$nbsp" />
								</xsl:if>
							</td>
							<td class="summaryTableSignatureCol">
								<div class="summarySignature">
									<xsl:choose>
										<xsl:when test="ancestor::asAncestor">
											<a href="{ancestor::asAncestor/classRef/@relativePath}#effect:{@name}" class="signatureLink">
												<xsl:value-of select="@name" />
											</a>
										</xsl:when>
										<xsl:when test="ancestor::asClass">
											<span class="signatureLink">
												<xsl:value-of select="@name"/>
											</span>
										</xsl:when>
									</xsl:choose>
								</div>
							</td>
							<td class="summaryTableDescription">
								<xsl:if test="string-length(@event)">
									<span class="label">Triggering event: </span>
									<xsl:variable name="event" select="@event" />
								
									<xsl:choose>
										<xsl:when test="ancestor::asClass/eventsGenerated/event[@name=$event]">
											<a href="#event:{@event}">
												<xsl:value-of select="@event" />
											</a>
										</xsl:when>
										<xsl:when test="ancestor::asClass/asAncestors/asAncestor/eventsGenerated/event[@name=$event]">
											<a href="{ancestor::asClass/asAncestors/asAncestor[eventsGenerated/event/@name=$event]/classRef/@relativePath}#event:{@event}">
												<xsl:value-of select="@event" />
											</a>
										</xsl:when>
										<xsl:otherwise>
	<!--										<xsl:message>Warning: Did not find effect event. <xsl:value-of select="ancestor::asClass/@name" />.<xsl:value-of select="@event" /></xsl:message>-->
											<xsl:value-of select="@event" />
										</xsl:otherwise>
									</xsl:choose>
									<br />
								</xsl:if>
								<xsl:if test="not(ancestor::asAncestor)">
									<xsl:call-template name="deTilda">
										<xsl:with-param name="inText" select="description/."/>
									</xsl:call-template>
								</xsl:if>
								<xsl:if test="ancestor::asAncestor">
									<xsl:call-template name="shortDescription" />
								</xsl:if>
							</td>
							<td class="summaryTableOwnerCol">
								<xsl:choose>
									<xsl:when test="ancestor::asAncestor">
										<a href="{ancestor::asAncestor/classRef/@relativePath}">
											<xsl:value-of select="ancestor::asAncestor/classRef/@name" />
										</a>
									</xsl:when>
									<xsl:when test="ancestor::asClass">
										<xsl:value-of select="ancestor::asClass/@name" />
									</xsl:when>
								</xsl:choose>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- EVENTS -->
	<xsl:template name="eventsGeneratedSummary">
		<xsl:param name="classDeprecated" select="'false'" />
		<xsl:param name="baseRef" select="''" />

		<xsl:variable name="hasEvents" select="count(eventsGenerated/event) &gt; 0" />
		<xsl:variable name="hasInherited" select="count(asAncestors/asAncestor/eventsGenerated/event) &gt; 0" />
		<xsl:if test="$hasEvents or $hasInherited">
			<a name="eventSummary" />
			<div class="summarySection">
				<div class="summaryTableTitle">
					<xsl:text>Events</xsl:text>
				</div>
				<xsl:if test="$hasInherited">
					<div class="showHideLinks">
						<div id="hideInheritedEvent" class="hideInheritedEvent">
							<a class="showHideLink" href="#eventSummary" onclick="javascript:setInheritedVisible(false,'Event');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Events</a>
						</div>
						<div id="showInheritedEvent" class="showInheritedEvent">
							<a class="showHideLink" href="#eventSummary" onclick="javascript:setInheritedVisible(true,'Event');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Events</a>
						</div>
					</div>
				</xsl:if>
				<xsl:variable name="tableStyle">
					<xsl:if test="$hasInherited and not($hasEvents)">
						<xsl:text>hideInheritedEvent</xsl:text>
					</xsl:if>
				</xsl:variable>
				<table cellspacing="0" cellpadding="3" class="summaryTable {$tableStyle}" id="summaryTableEvent">
					<tr>
						<th>
							<xsl:value-of select="$nbsp" />
						</th>
						<th colspan="2">Event</th>
						<th>Summary</th>
						<th class="summaryTableOwnerCol">
							<xsl:value-of select="concat('Defined',$nbsp,'by')" />
						</th>
					</tr>

					<xsl:for-each select="eventsGenerated/event | asAncestors/asAncestor/eventsGenerated/event">
						<xsl:sort select="@name" order="ascending" data-type="text"/>

						<xsl:variable name="rowStyle">
							<xsl:if test="ancestor::asAncestor">
								<xsl:text>hideInheritedEvent</xsl:text>
							</xsl:if>
						</xsl:variable>
						<tr class="{$rowStyle}">
							<td class="summaryTablePaddingCol">
								<xsl:value-of select="$nbsp" />
							</td>
							<td class="summaryTableInheritanceCol">
								<xsl:if test="ancestor::asAncestor">
									<img src="{$baseRef}images/inheritedSummary.gif" alt="Inherited" title="Inherited" class="inheritedSummaryImage" />
								</xsl:if>
								<xsl:if test="not(ancestor::asAncestor)">
									<xsl:value-of select="$nbsp" />
								</xsl:if>
							</td>
							<td class="summaryTableSignatureCol">
								<div class="summarySignature">
									<xsl:choose>
										<xsl:when test="ancestor::asAncestor">
											<a href="{ancestor::asAncestor/classRef/@relativePath}#event:{@name}" class="signatureLink">
												<xsl:value-of select="@name" />
											</a>
										</xsl:when>
										<xsl:when test="ancestor::asClass">
											<a href="#event:{@name}" class="signatureLink">
												<xsl:value-of select="@name"/>
											</a>
										</xsl:when>
									</xsl:choose>
	<!-- TODO add param classRefs for AS2 -->
									<xsl:if test="$config/options/@docversion='2'">
										<xsl:text> = function(</xsl:text>
										<xsl:call-template name="params" />
										<xsl:text>) {}</xsl:text>
									</xsl:if>
								</div>
							</td>
							<td class="summaryTableDescription summaryTableCol">
								<xsl:if test="$classDeprecated='true'">
									<xsl:copy-of select="$deprecatedLabel" />
									<xsl:text>. </xsl:text>
								</xsl:if>
								<xsl:if test="string-length(normalize-space(shortDescription/.))">
									<xsl:call-template name="deTilda">
										<xsl:with-param name="inText" select="shortDescription" />
									</xsl:call-template>
								</xsl:if>
								<xsl:if test="not(string-length(normalize-space(shortDescription/.)))">
									<xsl:value-of select="$nbsp" />
								</xsl:if>
							</td>
							<td class="summaryTableOwnerCol">
								<xsl:choose>
									<xsl:when test="ancestor::asAncestor">
										<a href="{ancestor::asAncestor/classRef/@relativePath}">
											<xsl:value-of select="ancestor::asAncestor/classRef/@name" />
										</a>
									</xsl:when>
									<xsl:when test="ancestor::asClass">
										<xsl:value-of select="ancestor::asClass/@name" />
									</xsl:when>
								</xsl:choose>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="eventsGenerated" mode="detail">
		<xsl:param name="classDeprecated" select="'false'"/>

		<xsl:if test="count(event) &gt; 0">
			<div class="detailSectionHeader">
				<xsl:text>Event detail</xsl:text>
			</div>
			<xsl:for-each select="event">
				<xsl:sort select="@name" order="ascending"/>

				<a name="event:{@name}"/>
				<table class="detailHeader" cellpadding="0" cellspacing="0">
					<tr>
						<td class="detailHeaderName">
							<xsl:value-of select="@name" />
						</td>
						<td class="detailHeaderType">
							<xsl:value-of select="concat('event',$nbsp)" />
							<xsl:if test="@type='listener'">
								<xsl:text>listener</xsl:text>
							</xsl:if>
							<xsl:if test="@type!='listener'">
								<xsl:text>handler</xsl:text>
							</xsl:if>
						</td>
						<xsl:if test="position()!=1">
							<td class="detailHeaderRule">
								<xsl:value-of select="$nbsp" />
							</td>
						</xsl:if>
					</tr>
				</table>
				<div class="detailBody">
					<xsl:if test="eventObject">
						<span class="label">
							<xsl:text>Event object type: </xsl:text>
						</span>
						<a href="{eventObject/@href}">
							<code>
								<xsl:value-of select="eventObject/@label"/>
							</code>
						</a>
						<br />
						<xsl:if test="eventType">
							<span class="label">
								<xsl:call-template name="substring-after-last">
									<xsl:with-param name="input" select="eventObject/@label" />
									<xsl:with-param name="substr" select="'.'" />
								</xsl:call-template>
								<xsl:text>.type property = </xsl:text>
							</span>
							<a href="{eventType/@href}">
								<code>
									<xsl:value-of select="eventType/@label"/>
								</code>
							</a>
							<br />
						</xsl:if>
						<xsl:if test="not(eventType)">
							<p></p>
						</xsl:if>
					</xsl:if>
					<xsl:if test="$config/options/@docversion='2'">
						<p>
							<code>
									<xsl:text>public </xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text> = function(</xsl:text>
									<xsl:call-template name="params" />
									<xsl:text>) {}</xsl:text>
							</code>
						</p>
					</xsl:if>
					<xsl:if test="$classDeprecated='true'">
						<xsl:call-template name="description">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="addParagraphTags" select="true()" />
						</xsl:call-template>
					</xsl:if>
					<xsl:call-template name="version"/>
					<xsl:if test="$classDeprecated!='true'">
						<xsl:call-template name="description">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
						<xsl:with-param name="addParagraphTags" select="true()" />
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="string-length(eventDescription/.)">
						<xsl:variable name="desc">
							<xsl:if test="contains(eventDescription/.,'&lt;p>')">
								<xsl:value-of select="concat('&lt;p>',substring-before(eventDescription/.,'&lt;p>'),'&lt;/p>&lt;p>',substring-after(eventDescription/.,'&lt;p>'))" />
							</xsl:if>
							<xsl:if test="not(contains(eventDescription/.,'&lt;p>'))">
								<xsl:value-of select="concat('&lt;p>',eventDescription/.,'&lt;/p>')" />
							</xsl:if>
						</xsl:variable>
						<xsl:call-template name="deTilda">
							<xsl:with-param name="inText" select="$desc" />
						</xsl:call-template>
					</xsl:if>
					<xsl:apply-templates select="params"/>
					<xsl:apply-templates select="example | includeExamples/includeExample[codepart]"/>
					<xsl:call-template name="sees" />
				</div>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template name="event">
		<xsl:if test="count(event)">
			<br />
			<span class="label">Events</span>
			<table cellpadding="0" cellspacing="0" border="0">
				<xsl:for-each select="event">
					<tr>
						<td width="20px" />
						<td>
							<code>
								<b>
									<xsl:if test="$config/options/@docversion='2'">
										<a href="#event:{@name}">
											<xsl:value-of select="@name"/>
										</a>
									</xsl:if>
									<xsl:if test="$config/options/@docversion!='2'">
										<xsl:value-of select="@name"/>
									</xsl:if>
								</b>
								<xsl:if test="classRef">:<a href="{classRef/@relativePath}"><xsl:value-of select="classRef/@name"/></a></xsl:if>
							</code>
							<xsl:if test="string-length(description/.)">
								<xsl:value-of select="$emdash"/>
								<xsl:call-template name="description"/>
							</xsl:if>
						</td>
					</tr>
					<xsl:if test="position()!=last()">
						<tr>
							<td class="paramSpacer">
								<xsl:value-of select="$nbsp" />
							</td>
						</tr>
					</xsl:if>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- METHODS -->
	<xsl:template name="methodSummary">
		<xsl:param name="className" />
		<xsl:param name="title" select="'Methods'" />
		<xsl:param name="classDeprecated" select="'false'" />
		<xsl:param name="accessLevel" select="'public'" />
		<xsl:param name="baseRef" select="''" />
		<xsl:param name="isGlobal" select="false()" />
		<xsl:param name="showAnchor" select="true()" />

<!-- TODO - adding non-protected to public -->
		<xsl:variable name="hasMethods" select="count(methods/method[@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.]) &gt; 0 or count(constructors/constructor[@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.]) &gt; 0" />
		<xsl:variable name="hasInherited" select="count(asAncestors/asAncestor/methods/method[@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.]) &gt; 0" />

		<xsl:if test="$hasMethods or $hasInherited">
			<xsl:if test="$showAnchor">
				<xsl:if test="$accessLevel='public'">
					<a name="methodSummary" />
				</xsl:if>
				<xsl:if test="$accessLevel='protected'">
					<xsl:if test="not(count(methods/method[@accessLevel='public' or @accessLevel=$config/namespaces/namespace[@summaryDisplay='public']/.]) &gt; 0 or count(asAncestors/asAncestor/methods/method[@accessLevel='public' or @accessLevel=$config/namespaces/namespace[@summaryDisplay='public']/.]) &gt; 0)">					
						<a name="methodSummary" />
					</xsl:if>
					<a name="protectedMethodSummary" />
				</xsl:if>
			</xsl:if>
			<div class="summarySection">
				<div class="summaryTableTitle">
					<xsl:choose>
						<xsl:when test="$isGlobal">
							<xsl:text>Global </xsl:text>
						</xsl:when>
						<xsl:when test="$accessLevel='public'">
							<xsl:text>Public </xsl:text>
						</xsl:when>
						<xsl:when test="$accessLevel='protected'">
							<xsl:text>Protected </xsl:text>
						</xsl:when>
					</xsl:choose>
					<xsl:value-of select="$title" />
				</div>
				<xsl:if test="$hasInherited">
					<div class="showHideLinks">
						<xsl:if test="$accessLevel!='protected'">
							<div id="hideInheritedMethod" class="hideInheritedMethod">
								<a class="showHideLink" href="#methodSummary" onclick="javascript:setInheritedVisible(false,'Method');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Public Methods</a>
							</div>
							<div id="showInheritedMethod" class="showInheritedMethod">
								<a class="showHideLink" href="#methodSummary" onclick="javascript:setInheritedVisible(true,'Method');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Public Methods</a>
							</div>
						</xsl:if>
						<xsl:if test="$accessLevel='protected'">
							<div id="hideInheritedProtectedMethod" class="hideInheritedProtectedMethod">
								<a class="showHideLink" href="#protectedMethodSummary" onclick="javascript:setInheritedVisible(false,'ProtectedMethod');"><img class="showHideLinkImage" src="{$baseRef}images/expanded.gif" /> Hide Inherited Protected Methods</a>
							</div>
							<div id="showInheritedProtectedMethod" class="showInheritedProtectedMethod">
								<a class="showHideLink" href="#protectedMethodSummary" onclick="javascript:setInheritedVisible(true,'ProtectedMethod');"><img class="showHideLinkImage" src="{$baseRef}images/collapsed.gif" /> Show Inherited Protected Methods</a>
							</div>
						</xsl:if>
					</div>
				</xsl:if>
				<xsl:variable name="tableStyle">
					<xsl:if test="$hasInherited and not($hasMethods)">
						<xsl:text>hideInherited</xsl:text>
						<xsl:if test="$accessLevel='protected'">
							<xsl:text>Protected</xsl:text>
						</xsl:if>
						<xsl:text>Method</xsl:text>
					</xsl:if>
				</xsl:variable>
				<xsl:variable name="tableId">
					<xsl:text>summaryTable</xsl:text>
					<xsl:if test="$accessLevel='protected'">
						<xsl:text>Protected</xsl:text>
					</xsl:if>
					<xsl:text>Method</xsl:text>
				</xsl:variable>
				<table cellspacing="0" cellpadding="3" class="summaryTable {$tableStyle}" id="{$tableId}">
					<tr>
						<th>
							<xsl:value-of select="$nbsp" />
						</th>
						<th colspan="2">
							<xsl:if test="self::asClass">
								<xsl:text>Method</xsl:text>
							</xsl:if>
							<xsl:if test="not(self::asClass)">
								<xsl:text>Function</xsl:text>
							</xsl:if>
						</th>
						<th class="summaryTableOwnerCol">
							<xsl:value-of select="concat('Defined',$nbsp,'by')" />
						</th>
					</tr>

					<xsl:apply-templates select="methods/method[@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.] | constructors/constructor[@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.] | asAncestors/asAncestor/methods/method[@accessLevel=$accessLevel or @accessLevel=$config/namespaces/namespace[@summaryDisplay=$accessLevel]/.]" mode="summary">
						<xsl:sort select="local-name()" />
						<xsl:sort select="@name" order="ascending" />
						<xsl:with-param name="classDeprecated" select="$classDeprecated" />
						<xsl:with-param name="baseRef" select="$baseRef" />
						<xsl:with-param name="accessLevel" select="$accessLevel" />
					</xsl:apply-templates>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="methods" mode="detail">
		<xsl:param name="className" />
		<xsl:param name="title" select="'Method detail'" />
		<xsl:param name="classDeprecated" select="'false'" />
		<xsl:param name="baseRef" />

		<xsl:if test="count(method) &gt; 0">
			<a name="methodDetail"/>
			<div class="detailSectionHeader">
				<xsl:value-of select="$title"/>
			</div>

			<xsl:apply-templates select="method" mode="detail">
				<xsl:sort select="@name" order="ascending"/>
				<xsl:with-param name="classDeprecated" select="$classDeprecated" />
				<xsl:with-param name="isMethod" select="$className!='package'" />
				<xsl:with-param name="className" select="$className" />
				<xsl:with-param name="baseRef" select="$baseRef" />
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template match="method | constructor" mode="summary">
		<xsl:param name="classDeprecated" select="'false'" />
		<xsl:param name="baseRef" select="''" />
		<xsl:param name="accessLevel" select="'public'" />

		<xsl:variable name="rowStyle">
			<xsl:if test="ancestor::asAncestor">
				<xsl:text>hideInherited</xsl:text>
				<xsl:if test="$accessLevel='protected'">
					<xsl:text>Protected</xsl:text>
				</xsl:if>
				<xsl:text>Method</xsl:text>
			</xsl:if>
		</xsl:variable>
		<tr class="{$rowStyle}">
			<td class="summaryTablePaddingCol">
				<xsl:value-of select="$nbsp" />
			</td>
			<td class="summaryTableInheritanceCol">
				<xsl:if test="ancestor::asAncestor">
					<img src="{$baseRef}images/inheritedSummary.gif" alt="Inherited" title="Inherited" class="inheritedSummaryImage" />
				</xsl:if>
				<xsl:if test="not(ancestor::asAncestor)">
					<xsl:value-of select="$nbsp" />
				</xsl:if>
			</td>
			<td class="summaryTableSignatureCol">
				<div class="summarySignature">
					<xsl:choose>
						<xsl:when test="ancestor::asAncestor">
							<a href="{ancestor::asAncestor/classRef/@relativePath}#{@name}()" class="signatureLink">
								<xsl:value-of select="@name" />
							</a>
						</xsl:when>
						<xsl:when test="self::constructor">
							<xsl:if test="position()>1">
								<a href="#{@name}{position()}()" class="signatureLink">
									<xsl:value-of select="@name"/>
								</a>
							</xsl:if>
							<xsl:if test="position()=1">
								<a href="#{@name}()" class="signatureLink">
									<xsl:value-of select="@name" />
								</a>
							</xsl:if>
						</xsl:when>
						<xsl:when test="ancestor::asClass or ancestor::asPackage">
							<a href="#{@name}()" class="signatureLink">
								<xsl:value-of select="@name"/>
							</a>
						</xsl:when>
					</xsl:choose>

					<xsl:if test="(not(@type) or @type='method')">
						<xsl:text>(</xsl:text>
						<xsl:call-template name="params"/>
						<xsl:text>)</xsl:text>
						<xsl:if test="self::method">
							<xsl:text>:</xsl:text>
							<xsl:choose>
								<xsl:when test="@result_type='' or @result_type='*'">
									<xsl:call-template name="getSpecialTypeLink">
										<xsl:with-param name="type" select="'*'" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="@result_type='void'">
									<xsl:call-template name="getSpecialTypeLink">
										<xsl:with-param name="type" select="'void'" />
									</xsl:call-template>
								</xsl:when>
                                <xsl:when test="@result_type='Void' and $config/options/@docversion='2'">
                                     <xsl:value-of select="@result_type" />
                                </xsl:when>
								<xsl:when test="result/classRef">
									<a href="{result/classRef/@relativePath}">
										<xsl:call-template name="getSimpleClassName">
											<xsl:with-param name="fullClassName" select="result/@type"/>
										</xsl:call-template>
									</a>
								</xsl:when>
								<xsl:when test="not(result/classRef) and result/@type">
										<xsl:call-template name="getSimpleClassName">
										<xsl:with-param name="fullClassName" select="result/@type"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="getSimpleClassName">
										<xsl:with-param name="fullClassName" select="@result_type" />
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:if>
				</div>
				<div class="summaryTableDescription">
					<xsl:apply-templates select="deprecated"/>
					<xsl:if test="not(deprecated)">
						<xsl:if test="@isStatic='true'">
							<xsl:text>[static]</xsl:text>
						</xsl:if>
						<xsl:call-template name="shortDescription">
							<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
						</xsl:call-template>
					</xsl:if>
				</div>
			</td>
			<xsl:if test="not($config/options/@docversion='2')">
			<td class="summaryTableOwnerCol">
				<xsl:choose>
					<xsl:when test="ancestor::asAncestor">
						<a href="{ancestor::asAncestor/classRef/@relativePath}">
							<xsl:value-of select="ancestor::asAncestor/classRef/@name" />
						</a>
					</xsl:when>
					<xsl:when test="ancestor::asClass">
						<xsl:value-of select="ancestor::asClass/@name" />
					</xsl:when>
					<xsl:when test="ancestor::asPackage">
						<xsl:if test="ancestor::asPackage/@name='$$Global$$'">
							<xsl:value-of select="concat('Top',$nbsp,'Level')" />
						</xsl:if>
						<xsl:if test="ancestor::asPackage/@name!='$$Global$$'">
							<xsl:value-of select="ancestor::asPackage/@name" />
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</td>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template name="getNamespaceLink">
		<xsl:param name="accessLevel" />
		<xsl:param name="baseRef" />

		<xsl:choose>
			<xsl:when test="$config/languageElements[@show='true' and @statements='true']">		
				<xsl:if test="$accessLevel='public' or $accessLevel='protected'">
					<xsl:value-of select="$accessLevel"/>
				</xsl:if>
				<xsl:if test="not($accessLevel='public' or $accessLevel='protected')">
					<a href="{$baseRef}statements.html#{$accessLevel}">
						<xsl:value-of select="$accessLevel" />
					</a>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$accessLevel" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getSpecialTypeLink">
		<xsl:param name="type" />
		<xsl:param name="baseRef">
			<xsl:call-template name="getBaseRef">
				<xsl:with-param name="packageName">
					<xsl:if test="ancestor::asClass">
						<xsl:value-of select="ancestor::asClass/@packageName" />
					</xsl:if>
					<xsl:if test="not(ancestor::asClass)">
						<xsl:value-of select="ancestor::asPackage/@name" />
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:param>

		<xsl:choose>
			<xsl:when test="$config/languageElements[@show='true' and @specialTypes='true']">
				<a href="{$baseRef}specialTypes.html#{$type}">
					<xsl:value-of select="$type" />
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$type" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="processCodepart">
		<xsl:param name="codepart" />

		<div class='listing'><pre>
			<xsl:call-template name="search-and-replace">
				<xsl:with-param name="input" select="$codepart" />
				<xsl:with-param name="search-string" select="'~~'" />
				<xsl:with-param name="replace-string" select="'*'" />
			</xsl:call-template>
		</pre></div>
	</xsl:template>

	<xsl:template match="codepart">
		<xsl:variable name="deTabbed">
			<xsl:call-template name="search-and-replace">
				<xsl:with-param name="input" select="." />
				<xsl:with-param name="search-string" select="$tab" />
				<xsl:with-param name="replace-string" select="$tabSpaces" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="initialComment" select="starts-with($deTabbed,'/*')" />
		<xsl:if test="$initialComment">
			<xsl:variable name="comment" select="substring-before($deTabbed,'*/')" />
			<xsl:if test="contains($comment,'@exampleText ')">
				<xsl:call-template name="deTilda">
					<xsl:with-param name="inText" select="substring-after(translate($comment,'*',''),'@exampleText ')" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>	
		<xsl:if test="$initialComment">
			<xsl:variable name="rest" select="substring-after($deTabbed,'*/')" />
			<xsl:variable name="finalComment" select="contains($rest,'/*')" />
			<xsl:if test="$finalComment">
				<xsl:call-template name="processCodepart">
					<xsl:with-param name="codepart" select="substring-before($rest,'/*')" />
				</xsl:call-template>
				<xsl:if test="contains($rest,'@exampleText ')">
					<xsl:call-template name="deTilda">
						<xsl:with-param name="inText" select="substring-after(translate(substring-before($rest,'*/'),'*',''),'@exampleText ')" />
					</xsl:call-template>
					<br />
					<br />
				</xsl:if>
			</xsl:if>
			<xsl:if test="not($finalComment)">
				<xsl:call-template name="processCodepart">
					<xsl:with-param name="codepart" select="substring-after($deTabbed,'*/')" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
		<xsl:if test="not($initialComment)">
			<xsl:variable name="finalComment" select="contains($deTabbed,'/*')" />
			<xsl:if test="$finalComment">
				<xsl:call-template name="processCodepart">
					<xsl:with-param name="codepart" select="substring-before($deTabbed,'/*')" />
				</xsl:call-template>
				<xsl:if test="contains($deTabbed,'@exampleText ')">
					<xsl:call-template name="deTilda">
						<xsl:with-param name="inText" select="substring-after(translate(substring-before($deTabbed,'*/'),'*',''),'@exampleText ')" />
					</xsl:call-template>
					<br />
					<br />
				</xsl:if>
			</xsl:if>
			<xsl:if test="not($finalComment)">
				<xsl:call-template name="processCodepart">
					<xsl:with-param name="codepart" select="$deTabbed" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>	
	</xsl:template>
   
	<xsl:template name="includeExamples">        
		<xsl:param name="showIncludeExamples" select="$showIncludeExamples"/>
		<xsl:if test="$showIncludeExamples = 'true'">
			<xsl:if test="includeExamples/includeExample/codepart">
				<a name="includeExamplesSummary" />
				<div class="detailSectionHeader">
					<xsl:text>Examples</xsl:text>
				</div>
			
				<xsl:for-each select="includeExamples/includeExample">
					<xsl:if test="contains(@examplefilename,'.mxml')">
						<div class="exampleHeader">
							<xsl:value-of select="substring-before(@examplefilename,'.mxml')" />
						</div>
					</xsl:if>
					<xsl:if test="contains(@examplefilename,'.as')">
						<br />
					</xsl:if>
					<div class="detailBody">
						<xsl:apply-templates select="codepart" />
			
						<xsl:if test="swfpart/@file and $showSWFs='true'">								
							<xsl:call-template name="getPlugin">
								<xsl:with-param name="pluginId" select="concat('example',position())" />
								<xsl:with-param name="filename" select="swfpart/@file" />
							</xsl:call-template>
						</xsl:if>
					</div>
				</xsl:for-each>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getPlugin">
		<xsl:param name="pluginId" />
		<xsl:param name="filename" />
							
		<script language="javascript" type="text/javascript">
			<xsl:comment>
				AC_FL_RunContent(
					"src", "<xsl:value-of select="substring-before($filename,'.swf')" />",
					"width", "100%",
					"height", "400px",
					"align", "middle",
					"id", "<xsl:value-of select="$pluginId" />",
					"quality", "high",
					"bgcolor", "",
					"name", "<xsl:value-of select="$pluginId" />",
					"flashvars","",
					"allowScriptAccess","sameDomain",
					"type", "application/x-shockwave-flash",
					"pluginspage", "http://www.macromedia.com/go/getflashplayer"
				);
			</xsl:comment>
		</script>


<!--		<object id="{$pluginId}" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab" width="100%" height="400px">
			<param name="movie" value="{$filename}" />
			<param name="quality" value="high" />
			<embed name="{$pluginId}" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" allowScriptAccess="sameDomain" width="100%" height="400px" flashVars="" src="{$filename}" quality="high" play="true" loop="false" align="middle" />
		</object>-->
	</xsl:template>

	<xsl:template match="method" mode="detail">
		<xsl:param name="classDeprecated" select="'false'"/>
		<xsl:param name="isMethod" select="true()" />
		<xsl:param name="className" select="''" />
		<xsl:param name="baseRef" />

		<a name="{@name}()"/>
		<table class="detailHeader" cellpadding="0" cellspacing="0">
			<tr>
				<td class="detailHeaderName">
					<xsl:value-of select="@name" />
<!--				<xsl:value-of select="concat(@name,'()',$nbsp)" />-->
				</td>
				<td class="detailHeaderParens">
					<xsl:text>()</xsl:text>
				</td>
				<td class="detailHeaderType">
					<xsl:if test="$isMethod">
						<xsl:text>method</xsl:text>
					</xsl:if>
					<xsl:if test="not($isMethod)">
						<xsl:text>function</xsl:text>
					</xsl:if>
				</td>
				<xsl:if test="position()!=1">
					<td class="detailHeaderRule">
						<xsl:value-of select="$nbsp" />
					</td>
				</xsl:if>
			</tr>
		</table>
		<div class="detailBody">
			<xsl:if test="(not(@type) or @type='method')">
				<code>
					<xsl:call-template name="getNamespaceLink">
						<xsl:with-param name="accessLevel" select="@accessLevel" />
						<xsl:with-param name="baseRef" select="$baseRef" />
					</xsl:call-template>
					<xsl:text> </xsl:text>
					<xsl:if test="@isFinal='true'">
						<xsl:text>final </xsl:text>
					</xsl:if>
					<xsl:if test="@isStatic='true'">
						<xsl:text>static </xsl:text>
					</xsl:if>
					<xsl:if test="@isOverride='true'">
						<xsl:text>override </xsl:text>
					</xsl:if>
					<xsl:text>function </xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>(</xsl:text>
					<xsl:call-template name="params"/>
					<xsl:text>):</xsl:text>
					<xsl:choose>
						<xsl:when test="result/@type='' or result/@type='*'">
							<xsl:call-template name="getSpecialTypeLink">
								<xsl:with-param name="type" select="'*'" />
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="result/@type='void'">
							<xsl:call-template name="getSpecialTypeLink">
								<xsl:with-param name="type" select="'void'" />
							</xsl:call-template>
						</xsl:when>
                        <xsl:when test="result/@type='Void' and $config/options/@docversion='2'">
                            <xsl:value-of select="@result_type" />
                        </xsl:when>
						<xsl:when test="result/classRef">
							<a href="{result/classRef/@relativePath}">
								<xsl:call-template name="getSimpleClassName">
									<xsl:with-param name="fullClassName" select="result/@type"/>
								</xsl:call-template>
							</a>
						</xsl:when>
						<xsl:when test="not(result/classRef)">
							<xsl:call-template name="getSimpleClassName">
								<xsl:with-param name="fullClassName" select="result/@type"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</code>
			</xsl:if>

			<xsl:apply-templates select="deprecated"/>
	<!-- 		<xsl:if test="deprecated">
				<br />
			</xsl:if> -->
			<xsl:if test="$classDeprecated='true'">
				<xsl:call-template name="description">
					<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
					<xsl:with-param name="addParagraphTags" select="true()" />
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="version"/>
			<xsl:if test="$classDeprecated!='true'">
				<xsl:call-template name="description">
					<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
					<xsl:with-param name="addParagraphTags" select="true()" />
				</xsl:call-template>
			</xsl:if>
	<!-- 		<xsl:if test="$classDeprecated='true'">
				<xsl:call-template name="description">
					<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="version"/>
			<xsl:if test="$classDeprecated!='true'">
				<xsl:call-template name="description">
					<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
				</xsl:call-template>
			</xsl:if> -->
			<xsl:apply-templates select="params"/>
			<xsl:call-template name="result"/>
			<xsl:call-template name="event"/>
			<xsl:if test="canThrow">
				<br />
				<span class="label">Throws</span>
				<table cellpadding="0" cellspacing="0" border="0">
					<xsl:apply-templates select="canThrow"/>
				</table>
			</xsl:if>

			<xsl:call-template name="sees" />
			<xsl:apply-templates select="example | includeExamples/includeExample[codepart]"/>
		</div>
	</xsl:template>

	<!-- CONSTRUCTORS -->
<!--	<xsl:template match="constructor" mode="summary">
		<xsl:param name="isConstructor">false</xsl:param>
		<xsl:param name="classDeprecated" select="'false'"/>

		<tr class="row{position() mod 2}">
			<td width="50px" valign="top" align="right">
				<xsl:value-of select="$nbsp" />
			</td>
			<td valign="top" align="left">
				<code>
					<xsl:if test="position()>1">
						<a href="#{@name}{position()}()">
							<b>
								<xsl:value-of select="@name"/>
							</b>
						</a>
					</xsl:if>
					<xsl:if test="position()=1">
						<a href="#{@name}()">
							<b>
								<xsl:value-of select="@name" />
							</b>
						</a>
					</xsl:if>
					<xsl:if test="(not(@type) or @type='method')">
						<xsl:text>(</xsl:text>
						<xsl:call-template name="params"/>
						<xsl:text>)</xsl:text>
					</xsl:if>
				</code>
				<xsl:call-template name="shortDescription">
					<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>
-->
	<xsl:template match="constructor" mode="detail">
		<xsl:param name="classDeprecated" select="'false'"/>		
		<xsl:param name="baseRef" />
			
		<xsl:if test="position()>1">
			<a name="{@name}{position()}()"></a>
		</xsl:if>
		<xsl:if test="position()=1">
			<a name="{@name}()"></a>
		</xsl:if>
		<table class="detailHeader" cellpadding="0" cellspacing="0">
			<tr>
				<td class="detailHeaderName">
					<xsl:value-of select="@name" />
				</td>
				<td class="detailHeaderParens">
					<xsl:text>()</xsl:text>
				</td>
				<td class="detailHeaderType">
					<xsl:text>constructor</xsl:text>
				</td>
				<xsl:if test="position()!=1">
					<td class="detailHeaderRule">
						<xsl:value-of select="$nbsp" />
					</td>
				</xsl:if>
			</tr>
		</table>
<!--		<div class="detailHeader">
			<xsl:value-of select="@name"/><xsl:text> constructor</xsl:text>
			<xsl:if test="position()>1">
				<a name="{@name}{position()}()"></a>
			</xsl:if>
			<xsl:if test="position()=1">
				<a name="{@name}()"></a>
			</xsl:if>
		</div>-->
		<div class="detailBody">
			<xsl:if test="(not(@type) or @type='method')">
				<code>
					<xsl:call-template name="getNamespaceLink">
						<xsl:with-param name="accessLevel" select="@accessLevel" />
						<xsl:with-param name="baseRef" select="$baseRef" />
					</xsl:call-template>
					<xsl:text> function </xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>(</xsl:text>
					<xsl:call-template name="params"/>
					<xsl:text>)</xsl:text>
				</code>
			</xsl:if>

			<xsl:call-template name="version"/>
			<xsl:call-template name="description">
				<xsl:with-param name="classDeprecated" select="$classDeprecated"/>
							<xsl:with-param name="addParagraphTags" select="true()" />
			</xsl:call-template>
			<xsl:apply-templates select="params"/>
			<xsl:call-template name="event" />

			<xsl:if test="canThrow">
				<br />
				<span class="label">Throws</span>
				<table cellpadding="0" cellspacing="0" border="0">
					<xsl:apply-templates select="canThrow"/>
				</table>
			</xsl:if>

			<xsl:call-template name="sees"/>
			<xsl:apply-templates select="example | includeExamples/includeExample[codepart]"/>
		</div>
	</xsl:template>

	<!-- PARAMS -->
	<xsl:template name="params">
		<xsl:for-each select="params/param">
			<xsl:if test="position()>1">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:if test="$config/options/@docversion='2' and @optional='true'">
				<xsl:text>[</xsl:text>
			</xsl:if>     
			<xsl:if test="@type">
				<xsl:if test="@type = 'restParam'">
					<xsl:variable name="baseRef">
						<xsl:if test="ancestor::asPackage">
							<xsl:call-template name="getBaseRef">
								<xsl:with-param name="packageName" select="ancestor::asPackage/@name" />
							</xsl:call-template>
						</xsl:if>
					</xsl:variable>
					<xsl:if test="$config/languageElements[@show='true' and @statements='true']">
						<a href="{$baseRef}statements.html#..._(rest)_parameter">...</a>
					</xsl:if>
					<xsl:if test="not($config/languageElements[@show='true' and @statements='true'])">
						<xsl:text>...</xsl:text>
					</xsl:if>
					<xsl:text> </xsl:text>
					<xsl:value-of select="@name"/>
				</xsl:if>
				<xsl:if test="@type != 'restParam'">
<!-- 					<b> -->
						<xsl:value-of select="@name"/>
<!-- 					</b> -->
					<xsl:text>:</xsl:text>
					<xsl:choose>
						<xsl:when test="classRef">
							<a href="{classRef/@relativePath}">
								<xsl:call-template name="getSimpleClassName">
									<xsl:with-param name="fullClassName" select="@type"/>
								</xsl:call-template>
							</a>
						</xsl:when>
						<xsl:when test="@type='' or @type='*'">
							<xsl:call-template name="getSpecialTypeLink">
								<xsl:with-param name="type" select="'*'" />
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="not(classRef)">
							<xsl:call-template name="getSimpleClassName">
								<xsl:with-param name="fullClassName" select="@type"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:if>
			</xsl:if>
			<xsl:if test="(string-length(@default) or @type='String') and @default!='unknown'">
				<xsl:text> = </xsl:text>
				<xsl:if test="@type='String' and @default!='null'">
					<xsl:text>"</xsl:text>
				</xsl:if>
				<xsl:value-of select="@default"/>
				<xsl:if test="@type='String' and @default!='null'">
					<xsl:text>"</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="$config/options/@docversion='2' and @optional='true'">
				<xsl:text>]</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="params">
		<span class="label">Parameters</span>
		<table cellpadding="0" cellspacing="0" border="0">
			<xsl:for-each select="param">
				<tr>
					<td width="20px"/>
					<td>
						<code>
							<xsl:if test="@type='restParam'">
								<xsl:variable name="baseRef">
									<xsl:if test="ancestor::asPackage">
										<xsl:call-template name="getBaseRef">
											<xsl:with-param name="packageName" select="ancestor::asPackage/@name" />
										</xsl:call-template>
									</xsl:if>
								</xsl:variable>
								<xsl:if test="$config/languageElements[@show='true' and @statements='true']">
									<a href="{$baseRef}statements.html#..._(rest)_parameter">...</a>
								</xsl:if>
								<xsl:if test="not($config/languageElements[@show='true' and @statements='true'])">
									<xsl:text>...</xsl:text>
								</xsl:if>
								<xsl:text> </xsl:text>
								<span class="label">
									<xsl:value-of select="@name"/>
								</span>
							</xsl:if>
							<xsl:if test="@type!='restParam'">
								<span class="label">
									<xsl:value-of select="@name"/>
								</span>
								<xsl:choose>
									<xsl:when test="classRef">
										<xsl:text>:</xsl:text>
										<a href="{classRef/@relativePath}">
											<xsl:call-template name="getSimpleClassName">
												<xsl:with-param name="fullClassName" select="@type"/>
											</xsl:call-template>
										</a>
									</xsl:when>
									<xsl:when test="@type='' or @type='*'">
										<xsl:text>:</xsl:text>
										<xsl:call-template name="getSpecialTypeLink">
											<xsl:with-param name="type" select="'*'" />
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="not(classRef) and string-length(@type)">										
										<xsl:text>:</xsl:text>
										<xsl:call-template name="getSimpleClassName">
											<xsl:with-param name="fullClassName" select="@type"/>
										</xsl:call-template>
									</xsl:when>
								</xsl:choose>
							</xsl:if>
							<xsl:if test="(string-length(@default) or @type='String') and @default!='unknown'">
								<xsl:text disable-output-escaping="yes">&lt;/code&gt; (default = </xsl:text>
								<xsl:if test="@type='String' and @default!='null'">
									<xsl:text>"</xsl:text>
								</xsl:if>
								<xsl:text disable-output-escaping="yes">&lt;code&gt;</xsl:text>
								<xsl:value-of select="@default" />
								<xsl:text disable-output-escaping="yes">&lt;/code&gt;</xsl:text>
								<xsl:if test="@type='String' and @default!='null'">
									<xsl:text>"</xsl:text>
								</xsl:if>
								<xsl:text>)</xsl:text>
								<xsl:text disable-output-escaping="yes">&lt;code&gt;</xsl:text>
							</xsl:if>
						</code>
						<xsl:if test="@optional='true'">
							<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]>[optional]</xsl:text>
						</xsl:if>
						<xsl:if test="normalize-space(description/.)">
							<xsl:value-of select="$emdash"/>
							<xsl:call-template name="description"/>
							<!-- <xsl:call-template name="deTilda">
								<xsl:with-param name="inText" select="param/text()"/>
							</xsl:call-template> -->
						</xsl:if>
					</td>
				</tr>
				<xsl:if test="position()!=last()">
					<tr>
						<td class="paramSpacer">
							<xsl:value-of select="$nbsp" />
						</td>
					</tr>
				</xsl:if>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!-- RESULT -->
	<xsl:template name="result">
		<xsl:if test="result[@type != 'void'] and not($config/options/@docversion='2' and result/@type='Void')">
			<p/>
			<span class="label">Returns</span>
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td width="20"/>
					<td>
						<code>
							<xsl:choose>
								<xsl:when test="result/@type='' or result/@type='*'">
									<xsl:call-template name="getSpecialTypeLink">
										<xsl:with-param name="type" select="'*'" />
									</xsl:call-template>
								</xsl:when>
                                <xsl:when test="result/@type='Void' and $config/options/@docversion='2'">
                                       <xsl:value-of select="@result_type" />
                                </xsl:when>
								<xsl:when test="result/classRef">
									<a href="{result/classRef/@relativePath}">
										<xsl:call-template name="getSimpleClassName">
											<xsl:with-param name="fullClassName" select="result/@type"/>
										</xsl:call-template>
									</a>
								</xsl:when>
								<xsl:when test="not(result/classRef)">
									<xsl:call-template name="getSimpleClassName">
										<xsl:with-param name="fullClassName" select="result/@type"/>
									</xsl:call-template>
								</xsl:when>
							</xsl:choose>
						</code>
						<xsl:if test="string-length(normalize-space(result/.))">
							<xsl:value-of select="$emdash"/>
							<xsl:call-template name="deTilda">
								<xsl:with-param name="inText" select="result/."/>
							</xsl:call-template>
						</xsl:if>
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- THROWS -->
	<xsl:template match="canThrow">
		<tr>
			<td width="20"/>
			<td>
				<code>
					<xsl:if test="classRef/@relativePath">
						<a href="{classRef/@relativePath}">
							<xsl:value-of select="classRef/@name"/>
						</a>
						<xsl:text> </xsl:text>
					</xsl:if>
					<xsl:if test="not(classRef/@relativePath)">
						<xsl:value-of select="classRef/@name"/>
					</xsl:if>
				</code>
				<xsl:if test="string-length(description/.)">
					<xsl:value-of select="$emdash"/>
				</xsl:if>
				<xsl:call-template name="description"/>
			</td>
		</tr>
		<xsl:if test="position()!=last()">
			<tr>
				<td class="paramSpacer">
					<xsl:value-of select="$nbsp" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- EXAMPLES -->
	<xsl:template match="example | includeExample">
		<xsl:param name="show" select="$showExamples"/>
		<xsl:if test="$show = 'true'">
			<xsl:if test="position()=1">
				<br />
				<span class="label">Example</span>
				<br />
			</xsl:if>
			<xsl:if test="self::example">
				<xsl:call-template name="deTilda">
					<xsl:with-param name="inText">
						<xsl:apply-templates mode="deTab" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="self::includeExample">
				<xsl:variable name="deTabbed">
					<xsl:call-template name="search-and-replace">
						<xsl:with-param name="input" select="codepart/." />
						<xsl:with-param name="search-string" select="$tab" />
						<xsl:with-param name="replace-string" select="$tabSpaces" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="initialComment" select="starts-with($deTabbed,'/*')" />
				<xsl:if test="$initialComment">
					<xsl:variable name="comment" select="substring-before($deTabbed,'*/')" />
					<xsl:if test="contains($comment,'@exampleText ')">
						<xsl:call-template name="deTilda">
							<xsl:with-param name="inText" select="substring-after(translate($comment,'*',''),'@exampleText ')" />
						</xsl:call-template>
					</xsl:if>
				</xsl:if>	
				<xsl:if test="$initialComment">
					<xsl:variable name="rest" select="substring-after($deTabbed,'*/')" />
					<xsl:variable name="finalComment" select="contains($rest,'/*')" />
					<xsl:if test="$finalComment">
						<div class='listing'><pre>
							<xsl:value-of select="substring-before($rest,'/*')" />
						</pre></div>
						<xsl:if test="contains($rest,'@exampleText ')">
							<xsl:call-template name="deTilda">
								<xsl:with-param name="inText" select="substring-after(translate(substring-before($rest,'*/'),'*',''),'@exampleText ')" />
							</xsl:call-template>
							<br />
							<br />
						</xsl:if>
					</xsl:if>
					<xsl:if test="not($finalComment)">
						<div class='listing'><pre>
							<xsl:value-of select="substring-after($deTabbed,'*/')" />
						</pre></div>
					</xsl:if>
				</xsl:if>
				<xsl:if test="not($initialComment)">
					<xsl:variable name="finalComment" select="contains($deTabbed,'/*')" />
					<xsl:if test="$finalComment">
						<div class='listing'><pre>
							<xsl:value-of select="substring-before($deTabbed,'/*')" />
						</pre></div>
						<xsl:if test="contains($deTabbed,'@exampleText ')">
							<xsl:call-template name="deTilda">
								<xsl:with-param name="inText" select="substring-after(translate(substring-before($deTabbed,'*/'),'*',''),'@exampleText ')" />
							</xsl:call-template>
							<br />
							<br />
						</xsl:if>
					</xsl:if>
					<xsl:if test="not($finalComment)">
						<div class='listing'><pre>
							<xsl:value-of select="$deTabbed" />
						</pre></div>
					</xsl:if>
				</xsl:if>	
				<xsl:if test="swfpart/@file and $showSWFs='true'">
					<xsl:variable name="filename" select="swfpart/@file" />
<!-- 					<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" width="100%" height="100%">
						<param name="src" value="{$filename}" />
						<embed pluginspage="http://www.macromedia.com/go/getflashplayer" width="100%" height="100%" flashVars="" src="{$filename}" />
					</object> -->						
<!-- 					<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,5,0,0" width="100%" height="100%">
						<param name="src" value="{$filename}" />
						<embed pluginspage="http://www.macromedia.com/go/getflashplayer" width="100%" height="100%" flashVars="" src="{$filename}" />
					</object> -->
					<xsl:call-template name="getPlugin">
						<xsl:with-param name="filename" select="$filename" />
					</xsl:call-template>
<!-- 					<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" width="100%" height="100%" id="TestExample">
						<param name="allowScriptAccess" value="sameDomain" />
						<param name="movie" value="{$filename}" />
						<param name="quality" value="high" />
						<param name="bgcolor" value="#ffffff" />
						<embed src="{$filename}" quality="high" bgcolor="#ffffff" width="100%" height="100%" name="TestExample" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
					</object> -->
				</xsl:if>
			</xsl:if>
			<p></p>
		</xsl:if>
	</xsl:template>

	<xsl:template match="text()" mode="deTab">
		<xsl:call-template name="search-and-replace">
			<xsl:with-param name="input" select="." />
			<xsl:with-param name="search-string" select="'&#09;'" />
			<xsl:with-param name="replace-string" select="'    '" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="includeExampleLink">
		<xsl:param name="showIncludeExamples" select="$showIncludeExamples"/>
		<xsl:if test="$showIncludeExamples = 'true'">
			<xsl:if test="includeExamples/includeExample/codepart">
			<p>
				<a href="#includeExamplesSummary">View the examples.</a>
			</p>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="inherited">
		<xsl:param name="lowerType" />
		<xsl:param name="upperType" />
		<xsl:param name="prefix" />
		<xsl:param name="postfix" />
		<xsl:param name="inheritedItems" />
		<xsl:param name="staticItems" />

		<xsl:if test="string-length($inheritedItems) &gt; 0">
			<xsl:call-template name="doInherited">
				<xsl:with-param name="lowerType" select="$lowerType" />
				<xsl:with-param name="upperType" select="$upperType" />
				<xsl:with-param name="prefix" select="$prefix" />
				<xsl:with-param name="postfix" select="$postfix" />
				<xsl:with-param name="items" select="$inheritedItems" />
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="string-length($staticItems) &gt; 0">
			<xsl:call-template name="doInherited">
				<xsl:with-param name="lowerType" select="$lowerType" />
				<xsl:with-param name="upperType" select="$upperType" />
				<xsl:with-param name="prefix" select="$prefix" />
				<xsl:with-param name="postfix" select="$postfix" />
				<xsl:with-param name="items" select="$staticItems" />
				<xsl:with-param name="isStatic" select="true()" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="doInherited">
		<xsl:param name="lowerType" />
		<xsl:param name="upperType" />
		<xsl:param name="prefix" />
		<xsl:param name="postfix" />
		<xsl:param name="items" />
		<xsl:param name="innerClass" select="false()" />
		<xsl:param name="isStatic" select="false()" />

		<xsl:variable name="classRef" select="classRef"/>
		<xsl:variable name="bgColor">
			<xsl:if test="not($isStatic)">
				<xsl:text>#EEEEEE</xsl:text>
			</xsl:if>
			<xsl:if test="$isStatic">
				<xsl:text>#EEDDDD</xsl:text>
			</xsl:if>
		</xsl:variable>
		<a name="{$lowerType}InheritedFrom{$classRef/@name}"/>
		<table cellspacing="0" cellpadding="3" class="summaryTable">
        	<tr>
            	<th>
                   <xsl:value-of select="$nbsp" />
                </th>
                <th>
                   <xsl:if test="$isStatic">
                        <xsl:text>Static </xsl:text>
                        <xsl:value-of select="$lowerType" />
                       <xsl:text> defined in class </xsl:text>
                    </xsl:if>
                    <xsl:if test="not($isStatic)">
                        <xsl:value-of select="$upperType"/>
                        <xsl:text> inherited from class </xsl:text>
                    </xsl:if>
                    <a href="{$classRef/@relativePath}">
                       <xsl:value-of select="$classRef/@name"/>
                   </a>
                 </th>
            </tr>
            <tr>
                <td class="summaryTablePaddingCol">
                   <xsl:value-of select="$nbsp" />
                </td>
			</tr>
			<tr>
				<td class="inheritanceList">
					<code>
						<xsl:for-each select="str:tokenize($items,';')">
							<xsl:sort select="." order="ascending" data-type="text"/>

							<xsl:if test="$innerClass">
								<xsl:variable name="href">
									<xsl:if test="contains($classRef/@relativePath,':')">
										<xsl:call-template name="substring-before-last">
											<xsl:with-param name="input" select="$classRef/@relativePath"/>
											<xsl:with-param name="substr" select="':'"/>
										</xsl:call-template>
										<xsl:text>/</xsl:text>
									</xsl:if>
									<xsl:value-of select="."/>
								</xsl:variable>
								<a href="{$href}.html">
									<xsl:value-of select="."/>
								</a>
							</xsl:if>
							<xsl:if test="not($innerClass)">
								<xsl:if test="$prefix">
									<a href="{$classRef/@relativePath}#{$prefix}:{.}{$postfix}">
										<xsl:value-of select="."/>
									</a>
								</xsl:if>
								<xsl:if test="not($prefix)">
									<a href="{$classRef/@relativePath}#{.}{$postfix}">
										<xsl:value-of select="."/>
									</a>
								</xsl:if>
							</xsl:if>
							<xsl:if test="position() != last()">
								<xsl:text>, </xsl:text>
							</xsl:if>
						</xsl:for-each>
					</code>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="description">
		<xsl:param name="classDeprecated" select="'false'" />
		<xsl:param name="addParagraphTags" select="false()" />

		<xsl:if test="$classDeprecated='true'">
			<xsl:copy-of select="$deprecatedLabel"/>
			<xsl:text>.</xsl:text>
<!-- 			<em> -->
				<xsl:text> The </xsl:text>
				<xsl:value-of select="../../@name"/>
				<xsl:text> class is </xsl:text>
				<a href="#deprecated">deprecated</a>
				<xsl:if test="string-length(../../deprecated/@as-of)">
					<xsl:text> since </xsl:text>
					<xsl:value-of select="../../deprecated/@as-of"/>
				</xsl:if>
				<xsl:text>.</xsl:text>
<!-- 			</em> -->
			<br/>
			<br/>
		</xsl:if>
		<xsl:if test="customs/review and $config/options/@showReview='true'">
			<h2><font color="red">Review Needed</font></h2>
		</xsl:if>
		<xsl:if test="description">
			<xsl:variable name="desc">
				<xsl:if test="$addParagraphTags">
					<xsl:if test="not(contains(description/.,'&lt;p>'))">
						<xsl:value-of select="concat('&lt;p>',description/.,'&lt;/p>')" />
					</xsl:if>
					<xsl:if test="contains(description/.,'&lt;p>')">
						<xsl:value-of select="concat('&lt;p>',substring-before(description/.,'&lt;p>'),'&lt;/p>&lt;p>',substring-after(description/.,'&lt;p>'))" />
					</xsl:if>
				</xsl:if>
				<xsl:if test="not($addParagraphTags)">
					<xsl:value-of select="description/." />
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="deTilda">
				<xsl:with-param name="inText" select="$desc" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="shortDescription">
		<xsl:param name="classDeprecated" select="'false'"/>

		<xsl:if test="shortDescription or $classDeprecated='true'">
			<xsl:call-template name="shortDescriptionReview" />
			<xsl:if test="$classDeprecated='true'">
				<xsl:copy-of select="$deprecatedLabel"/>
				<xsl:text>. </xsl:text>
			</xsl:if>
			<xsl:call-template name="deTilda">
				<xsl:with-param name="inText" select="shortDescription"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="hasInnerClasses">
		<xsl:value-of select="count(./*[asClass or asAncestor[classes/asClass]])"/>
	</xsl:template>

	<xsl:template name="hasConstants">
		<xsl:value-of select="count(.//*[field[@isConst='true']])"/>
	</xsl:template>

	<xsl:template name="hasFields">
		<xsl:value-of select="count(.//*[field[not(@isConst='true')]])"/>
	</xsl:template>

	<xsl:template name="hasConstructor">
		<xsl:value-of select="count(./*[constructor])"/>
	</xsl:template>

	<xsl:template name="hasMethods">
		<xsl:value-of select="count(.//*[method])"/>
	</xsl:template>

	<!-- TODO currently the mxmlc compiler does not recognize events defined in interfaces that are
	     not redeclared by the implementor, so we can not consider them when determining the event count -->
	<xsl:template name="hasEvents">
		<xsl:value-of select="count(.//*[event[../../eventsGenerated and not(ancestor::asImplements)]])"/>
	</xsl:template>

	<xsl:template name="hasStyles">
		<xsl:value-of select="count(.//*[style])"/>
	</xsl:template>

	<xsl:template name="hasEffects">
		<xsl:value-of select="count(.//*[effect])"/>
	</xsl:template>

	<xsl:template name="hasIncludeExamples">
		<xsl:param name="showIncludeExamples" select="$showIncludeExamples"/>
		<xsl:if test="$showIncludeExamples = 'true'">
			<xsl:value-of select="count(./*[includeExample/codepart])"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getPageLinks">
		<xsl:param name="copyNum" select="'1'" />
		<xsl:param name="title" select="''" />

		<xsl:variable name="hasInnerClasses">
			<xsl:call-template name="hasInnerClasses"/>
		</xsl:variable>
		<xsl:variable name="hasConstants">
			<xsl:call-template name="hasConstants"/>
		</xsl:variable>
		<xsl:variable name="hasFields">
			<xsl:call-template name="hasFields"/>
		</xsl:variable>
		<xsl:variable name="hasConstructor">
			<xsl:call-template name="hasConstructor"/>
		</xsl:variable>
		<xsl:variable name="hasMethods">
			<xsl:call-template name="hasMethods"/>
		</xsl:variable>
		<xsl:variable name="hasStyles">
			<xsl:call-template name="hasStyles"/>
		</xsl:variable>
		<xsl:variable name="hasEffects">
			<xsl:call-template name="hasEffects"/>
		</xsl:variable>
		<xsl:variable name="hasEvents">
			<xsl:call-template name="hasEvents"/>
		</xsl:variable>
		<xsl:variable name="hasIncludeExamples">
			<xsl:call-template name="hasIncludeExamples"/>
		</xsl:variable>

		<xsl:call-template name="getLinks2">
			<xsl:with-param name="subTitle" select="$title" />
			<xsl:with-param name="packageName" select="@packageName"/>
			<xsl:with-param name="fileName" select="@name"/>
			<xsl:with-param name="fileName2">
				<xsl:if test="string-length(@packageName)">
					<xsl:value-of select="concat(translate(@packageName,'.','/'),'/class-list.html')" />
				</xsl:if>
				<xsl:if test="not(string-length(@packageName))">
					<xsl:value-of select="'class-list.html'" />
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="showInnerClasses" select="boolean(number($hasInnerClasses))"/>
			<xsl:with-param name="showConstants" select="boolean(number($hasConstants))"/>
			<xsl:with-param name="showProperties" select="boolean(number($hasFields))"/>
<!--			<xsl:with-param name="showConstructors" select="boolean(number($hasConstructor))"/>
-->			<xsl:with-param name="showConstructors" select="false()" />
			<xsl:with-param name="showMethods" select="boolean(number($hasMethods)) or boolean(number($hasConstructor))"/>
			<xsl:with-param name="showStyles" select="boolean(number($hasStyles))"/>
			<xsl:with-param name="showEffects" select="boolean(number($hasEffects))"/>
			<xsl:with-param name="showEvents" select="boolean(number($hasEvents))"/>
			<xsl:with-param name="showIncludeExamples" select="boolean(number($hasIncludeExamples))"/>
			<xsl:with-param name="copyNum" select="$copyNum"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
