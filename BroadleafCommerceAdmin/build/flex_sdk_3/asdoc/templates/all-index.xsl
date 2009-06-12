<?xml version="1.0" encoding="UTF-8"?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:exslt="http://exslt.org/common" 
	xmlns:str="http://exslt.org/strings"
	xmlns:redirect="http://xml.apache.org/xalan/redirect" extension-element-prefixes="redirect"  exclude-result-prefixes="exslt str redirect">

	<xsl:include href="asdoc-util.xsl"/>
	<xsl:param name="basedir" select="'../xml/'"/>
	<xsl:param name="directivesFile" select="'directives.xml'"/>
	<xsl:param name="globalFuncFile" select="'global_functions.xml'"/>
	<xsl:param name="globalPropsFile" select="'global_props.xml'"/>
	<xsl:param name="constantsFile" select="'constants.xml'"/>
	<xsl:param name="operatorsFile" select="'operators.xml'"/>
	<xsl:param name="statementsFile" select="'statements.xml'"/>
	<xsl:param name="specialTypesFile" select="'specialTypes.xml'" />
	<xsl:param name="unsupportedFile" select="'unsupported.xml'" />
	<xsl:param name="fscommandFile" select="'fscommand.xml'" />
	<xsl:param name="splitIndex" select="$config/options[@splitIndex='true']" />
	<xsl:param name="outputPath" select="'../out/'"/>
	<xsl:param name="symbolsName" select="'Symbols'" />
	<xsl:param name="overviewsFile" select="'../xml/overviews.xml'" />

	<xsl:variable name="directives">
		<xsl:if test="$config/languageElements[@show='true' and @directives='true']">
			<xsl:copy-of select="document(concat($basedir,$directivesFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="globalFuncs">
		<xsl:if test="$config/languageElements[@show='true' and @functions='true'] and $config/options[@docversion!='3']">
			<xsl:copy-of select="document(concat($basedir,$globalFuncFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="globalProps">
		<xsl:if test="$config/languageElements[@show='true' and @properties='true']">
			<xsl:copy-of select="document(concat($basedir,$globalPropsFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="constants">
		<xsl:if test="$config/languageElements[@show='true' and @constants='true'] and $config/options[@docversion!='3']">
			<xsl:copy-of select="document(concat($basedir,$constantsFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="operators">
		<xsl:if test="$config/languageElements[@show='true' and @operators='true']">
			<xsl:copy-of select="document(concat($basedir,$operatorsFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="statements">
		<xsl:if test="$config/languageElements[@show='true' and @statements='true']">
			<xsl:copy-of select="document(concat($basedir,$statementsFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="specialTypes">
		<xsl:if test="$config/languageElements[@show='true' and @specialTypes='true']">
			<xsl:copy-of select="document(concat($basedir,$specialTypesFile))/asdoc"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="unsupported">
		<xsl:if test="$config/index[@showUnsupported='true']">
			<xsl:copy-of select="document(concat($basedir,$unsupportedFile))/asdoc" />
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="fscommand">
		<xsl:if test="$config/index[@showFscommand='true']">
			<xsl:copy-of select="document(concat($basedir,$fscommandFile))/asdoc" />
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="matches" select="//*[((self::method or self::field or self::constructor or self::style or self::effect) and not(ancestor::asAncestor)) or self::asPackage or self::asClass or (self::event and (not(parent::method) and not(parent::constructor) and not(parent::eventsDefined) and not(ancestor::asAncestor)))] | exslt:nodeSet($directives)/asdoc/object/methods/method | exslt:nodeSet($globalFuncs)/asdoc/object/methods/method | exslt:nodeSet($globalProps)/asdoc/object/fields/field | exslt:nodeSet($constants)/asdoc/object/fields/field | exslt:nodeSet($operators)/asdoc/operators/operator | exslt:nodeSet($statements)/asdoc/statements/statement | exslt:nodeSet($specialTypes)/asdoc/specialTypes/specialType | exslt:nodeSet($unsupported)/asdoc/unsupported//*[@name] | exslt:nodeSet($fscommand)/asdoc/fscommand | $config/index/entry"/>
						
	<xsl:variable name="symbols">
		<xsl:text disable-output-escaping="yes">+,:!?/.^~*=%|&amp;&lt;>()[]{}"</xsl:text>
	</xsl:variable>
	<xsl:variable name="letters">
		<xsl:if test="$config/languageElements[@show='true' and (@operators='true' or @specialTypes='true')]">
			<xsl:value-of select="$symbolsName" />
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:text>A B C D E F G H I J K L M N O P Q R S T U V W X Y Z</xsl:text>
	</xsl:variable>
	<xsl:variable name="letterSet" select="str:tokenize($letters)" />

	<xsl:template match="/">
		<xsl:if test="$splitIndex='false'">
			<xsl:apply-templates select="asdoc" />
		</xsl:if>
		<xsl:if test="$splitIndex!='false'">
			<xsl:variable name="context" select="/" />
			<xsl:for-each select="$letterSet">
				<xsl:variable name="fileName" select="concat('all-index-',.)" />
				<redirect:write select="concat($outputPath,$fileName,'.html')">
					<xsl:apply-templates select="$context/asdoc">
 						<xsl:with-param name="displayLetters" select="str:tokenize(.)" />
						<xsl:with-param name="fileName" select="$fileName" />
						<xsl:with-param name="letter" select="." />
					</xsl:apply-templates>
				</redirect:write>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template match="asdoc">
		<xsl:param name="displayLetters" select="$letterSet" />
		<xsl:param name="fileName" select="'all-index'" />
		<xsl:param name="letter" />

		<xsl:copy-of select="$noLiveDocs" />
		<xsl:copy-of select="$docType" />
		<xsl:element name="html">
			<head>
				<title>
					<xsl:if test="$splitIndex and $letter">
						<xsl:value-of select="$letter" />
					</xsl:if>
					<xsl:if test="not($splitIndex)">
						<xsl:text>All</xsl:text>
					</xsl:if>
					<xsl:text> Index</xsl:text>
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
<!-- 				<xsl:call-template name="getFeedbackLink">
					<xsl:with-param name="topic" select="'Index'"/>
				</xsl:call-template> -->
				<xsl:call-template name="getTitleScript">
					<xsl:with-param name="title">
						<xsl:if test="$splitIndex">
							<xsl:value-of select="concat($letter,' Index - ',$title-base)"/>
						</xsl:if>
						<xsl:if test="not($splitIndex)">
							<xsl:value-of select="concat('All Index - ',$title-base)"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="getLinks2">
					<xsl:with-param name="subTitle">
						<xsl:if test="$splitIndex">
							<xsl:value-of select="concat($letter,$nbsp,'Index')" />
						</xsl:if>
						<xsl:if test="not($splitIndex)">
							<xsl:value-of select="concat('All',$nbsp,'Index')" />
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="fileName" select="$fileName"/>
					<xsl:with-param name="fileName2" select="'index-list.html'" />
					<xsl:with-param name="showProperties" select="false()"/>
					<xsl:with-param name="showMethods" select="false()"/>
					<xsl:with-param name="showIndex" select="false()"/>
				</xsl:call-template>
				<div class="MainContent">
					<br />
					<table border="0" cellspacing="0" cellpadding="0">
						<xsl:for-each select="$displayLetters">
							<tr>
								<td colspan="2">
									<a name="{.}"/>
									<xsl:variable name="currLetter" select="."/>
									<xsl:for-each select="$letterSet">
										<xsl:if test="$currLetter=.">
											<xsl:if test="$currLetter=$symbolsName">
												<font color="black" size="6px" style="bold">
													<xsl:value-of select="."/>
												</font>
											</xsl:if>
											<xsl:if test="$currLetter!=$symbolsName">
												<font color="black" size="10px" style="bold">
													<xsl:value-of select="."/>
												</font>
											</xsl:if>
										</xsl:if>
										<xsl:if test="$currLetter!=.">
											<xsl:if test="$splitIndex='false'">
												<a href="#{.}" onclick="javascript:loadClassListFrame('index-list.html');">
													<xsl:value-of select="."/>
												</a>
											</xsl:if>
											<xsl:if test="$splitIndex!='false'">
												<a href="all-index-{.}.html" onclick="javascript:loadClassListFrame('index-list.html');">
													<xsl:value-of select="." />
												</a>
											</xsl:if>
										</xsl:if>
										<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;&nbsp;]]></xsl:text>
									</xsl:for-each>
								</td>
							</tr>
							<xsl:variable name="firstUpper" select="."/>
							<xsl:variable name="firstLower" select="translate($firstUpper,$upperCase,$lowerCase)"/>
							<xsl:variable name="checkingSymbol" select=".=$symbolsName and $config/languageElements[@show='true' and (@operators='true' or @specialTypes='true')]"/>

							<xsl:for-each select="$matches">
								<xsl:sort select="concat(translate(self::asPackage[@name='$$Global$$']/@name,'Global$','Top Le'),translate(@symbol,$symbols,''),translate(@name,'#_.( ',''))" data-type="text" />
								<xsl:sort select="../../@packageName"/>
								<xsl:sort select="../../@name"/>

								<xsl:variable name="isSymbol" select="string-length(@symbol) > 0 and not(contains($letters,translate(substring(@symbol,1,1),$lowerCase,$upperCase)))"/>
								<xsl:variable name="isSpecialSymbol" select="self::specialType and not(contains($letters,translate(substring(@name,1,1),$lowerCase,$upperCase)))" />
								<xsl:variable name="isRestParam" select="starts-with(@name,'...')" />
								<xsl:variable name="sortableName">
									<xsl:choose>
										<!-- special case for -Infinity -->
										<xsl:when test="@name='-Infinity'">
											<xsl:value-of select="substring(@name,2)"/>
										</xsl:when>
										<xsl:when test="@name='$$Global$$'">
											<xsl:value-of select="'Top Level'" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:if test="$isSymbol">
												<xsl:value-of select="@symbol"/>
											</xsl:if>
											<xsl:if test="not($isSymbol)">
												<xsl:if test="string-length(@symbol) > 0">
													<xsl:value-of select="@symbol"/>
												</xsl:if>
												<xsl:if test="not(string-length(@symbol) > 0)">
													<xsl:value-of select="translate(@name,'#_.( ','')"/>
												</xsl:if>
											</xsl:if>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>

								<xsl:variable name="symbolMatch" select="$checkingSymbol and ($isSymbol or $isSpecialSymbol or $isRestParam)"/>
								<xsl:if test="$symbolMatch or starts-with($sortableName,$firstLower) or starts-with($sortableName,$firstUpper)">
									<tr>
										<td class="idxrow" colspan="2">
											<xsl:choose>
												<!-- unsupported must come first, otherwise they show up in their original sections -->
												<xsl:when test="ancestor::unsupported">
	<!-- 												<xsl:if test="string-length(@package)">
														<xsl:value-of select="@package" />
														<xsl:text>.</xsl:text>
													</xsl:if>
													<xsl:if test="string-length(@class)">
														<xsl:value-of select="@class" />
														<xsl:text>.</xsl:text>
													</xsl:if> -->
													<a href="unsupported.html">
														<xsl:value-of select="@name" />
														<xsl:if test="self::method">
															<xsl:text>()</xsl:text>
														</xsl:if>
													</a>
													<xsl:value-of select="$emdash"/>
													<xsl:text>Unsupported </xsl:text>
													<xsl:choose>
														<xsl:when test="self::globalFunction">
															<a href="global_functions.html">
																<xsl:value-of select="../@label" />
															</a>
														</xsl:when>
														<xsl:when test="self::fscommand">
															<a href="global_functions.html#fscommand()">
																<xsl:value-of select="../@label" />
															</a>
														</xsl:when>
														<xsl:otherwise>
															<xsl:if test="self::eventHandler and not(string-length(@class))">
																<xsl:text> global </xsl:text>
															</xsl:if>
															<xsl:value-of select="parent::node()/@label" />
														</xsl:otherwise>
													</xsl:choose>
													<xsl:if test="string-length(@package)">
														<xsl:text> in </xsl:text>
														<xsl:if test="string-length(@class)">
															<xsl:text>class </xsl:text>
															<a href="">
																<xsl:value-of select="@package" />
															</a>
														</xsl:if>
														<xsl:if test="not(string-length(@class))">
															<xsl:text>package </xsl:text>
															<a href="{concat(translate(@package,'.','/'),'/package-detail.html')}">
																<xsl:value-of select="@package" />
															</a>
														</xsl:if>
													</xsl:if>
													<xsl:if test="not(string-length(@package))">
														<xsl:choose>
															<xsl:when test="self::class">
																<xsl:text> in </xsl:text>
																<a href="package-detail.html" onclick="javascript:loadClassListFrame('class-list.html');">Top Level</a>
															</xsl:when>
															<xsl:otherwise>
																<xsl:if test="string-length(@class)">
																	<xsl:text> in class </xsl:text>
																	<a href="{@class}.html" onclick="javascript:loadClassListFrame('class-list.html');">
																		<xsl:value-of select="@class" />
																	</a>
																</xsl:if>
															</xsl:otherwise>
														</xsl:choose>
													</xsl:if>
												</xsl:when>
												<xsl:when test="self::fscommand">
													<a href="{concat('fscommand/',@name,'.html')}" onclick="javascript:loadClassListFrame('fscommand-list.html');">
														<xsl:value-of select="@name" />
													</a>
													<xsl:value-of select="$emdash" />
													<xsl:text>Command for </xsl:text>
													<a href="global_functions.html#fscommand2()">
														<xsl:text>fscommand2</xsl:text>
													</a>
													<xsl:text> global function</xsl:text>
												</xsl:when>
												<xsl:when test="(self::method and (not(@type) or (@type!='handler'))) or self::constructor">
													<xsl:variable name="isTopLevel">
														<xsl:call-template name="isTopLevel">
															<xsl:with-param name="packageName">
																<xsl:if test="ancestor::asClass">
																	<xsl:value-of select="../../@packageName"/>
																</xsl:if>
																<xsl:if test="not(ancestor::asClass)">
																	<xsl:value-of select="../../@name"/>
																</xsl:if>
															</xsl:with-param>
														</xsl:call-template>
													</xsl:variable>
													<xsl:variable name="classPath">
														<!-- AS2 lang elements -->
	 													<xsl:if test="$isTopLevel='true' or ../../@type='list'">
															<xsl:text>.</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:if test="ancestor::asClass">
																<xsl:value-of select="translate(../../@packageName,'.','/')"/>
															</xsl:if>
															<xsl:if test="not(ancestor::asClass)">
																<xsl:value-of select="translate(../../@name,'.','/')"/>
															</xsl:if>
														</xsl:if>
													</xsl:variable>
													<xsl:choose>
														<!-- AS2 lang elements -->
														<xsl:when test="../../@type='list'">
															<a href="{../../@href}#{@name}()">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:when>
														<xsl:when test="ancestor::asClass">
															<a href="{$classPath}/{../../@name}.html#{@name}()" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:when>
														<xsl:otherwise>
															<a href="{$classPath}/package.html#{@name}()" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:if test="not(@type) or @type!='directive'">
														<xsl:variable name="params">
															<xsl:call-template name="getParamList">
																<xsl:with-param name="params" select="params"/>
															</xsl:call-template>
														</xsl:variable>
														<xsl:text>(</xsl:text>
														<xsl:copy-of select="$params"/>
														<xsl:text>)</xsl:text>
													</xsl:if>
													<xsl:value-of select="$emdash"/>
													<xsl:if test="self::method">
														<xsl:call-template name="getMethodDesc">
															<xsl:with-param name="classPath" select="$classPath"/>
														</xsl:call-template>
													</xsl:if>
													<xsl:if test="self::constructor">
														<xsl:call-template name="getConstructorDesc">
															<xsl:with-param name="classPath" select="$classPath"/>
														</xsl:call-template>
													</xsl:if>
												</xsl:when>
												<xsl:when test="self::field">
													<xsl:variable name="isTopLevel">
														<xsl:call-template name="isTopLevel">
															<xsl:with-param name="packageName">
																<xsl:if test="ancestor::asClass">
																	<xsl:value-of select="../../@packageName"/>
																</xsl:if>
																<xsl:if test="not(ancestor::asClass)">
																	<xsl:value-of select="../../@name"/>
																</xsl:if>
															</xsl:with-param>
														</xsl:call-template>
													</xsl:variable>
													<xsl:variable name="classPath">
														<!-- AS2 lang elements -->
	 													<xsl:if test="$isTopLevel='true' or ../../@type='list'">
															<xsl:text>.</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:if test="ancestor::asClass">
																<xsl:value-of select="translate(../../@packageName,'.','/')"/>
															</xsl:if>
															<xsl:if test="not(ancestor::asClass)">
																<xsl:value-of select="translate(../../@name,'.','/')"/>
															</xsl:if>
														</xsl:if>
													</xsl:variable>
													<xsl:choose>
														<!-- AS2 lang elements -->
														<xsl:when test="../../@type='list'">
															<a href="{../../@href}#{@name}">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:when>
														<xsl:when test="ancestor::asClass">
															<a href="{$classPath}/{../../@name}.html#{@name}" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:when>
														<xsl:otherwise>
															<a href="{$classPath}/package.html#{@name}" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:value-of select="$emdash"/>
													<xsl:call-template name="getPropertyDesc">
														<xsl:with-param name="classPath" select="$classPath"/>
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="self::style or self::effect">
													<xsl:variable name="isTopLevel">
														<xsl:call-template name="isTopLevel">
															<xsl:with-param name="packageName">
																<xsl:if test="ancestor::asClass">
																	<xsl:value-of select="../../@packageName"/>
																</xsl:if>
																<xsl:if test="not(ancestor::asClass)">
																	<xsl:value-of select="../../@name"/>
																</xsl:if>
															</xsl:with-param>
														</xsl:call-template>
													</xsl:variable>
													<xsl:variable name="classPath">
														<!-- AS2 lang elements -->
	 													<xsl:if test="$isTopLevel='true' or ../../@type='list'">
															<xsl:text>.</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:if test="ancestor::asClass">
																<xsl:value-of select="translate(../../@packageName,'.','/')"/>
															</xsl:if>
															<xsl:if test="not(ancestor::asClass)">
																<xsl:value-of select="translate(../../@name,'.','/')"/>
															</xsl:if>
														</xsl:if>
													</xsl:variable>
													<xsl:if test="self::style">
														<a href="{$classPath}/{../../@name}.html#style:{@name}" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
															<xsl:value-of select="@name" />
														</a>
														<xsl:value-of select="$emdash" />
														<xsl:text>Style</xsl:text>
													</xsl:if>
													<xsl:if test="self::effect">
														<a href="{$classPath}/{../../@name}.html#effect:{@name}" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
															<xsl:value-of select="@name" />
														</a>
														<xsl:value-of select="$emdash" />
														<xsl:text>Effect</xsl:text>
													</xsl:if>
													<xsl:text> in class </xsl:text>
													<xsl:call-template name="getClassRef">
														<xsl:with-param name="classPath" select="$classPath"/>
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="self::asPackage">
													<xsl:variable name="isTopLevel">
														<xsl:call-template name="isTopLevel">
															<xsl:with-param name="packageName" select="@name" />
														</xsl:call-template>
													</xsl:variable>
													<xsl:variable name="packagePath">
	 													<xsl:if test="$isTopLevel='true'">
															<xsl:text>.</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:value-of select="translate(@name,'.','/')" />
														</xsl:if>
													</xsl:variable>
													<a href="{$packagePath}/package-detail.html" onclick="javascript:loadClassListFrame('{$packagePath}/class-list.html');">
														<xsl:if test="$isTopLevel='true'">
															<xsl:text>Top Level</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:value-of select="@name" />
														</xsl:if>
													</a>
													<xsl:value-of select="$emdash" />
													<xsl:text>Package</xsl:text>
	<!--												<xsl:call-template name="getPackageDesc">
														<xsl:with-param name="isTopLevel" select="$isTopLevel" />
													</xsl:call-template>-->
												</xsl:when>
												<xsl:when test="self::asClass">
													<xsl:variable name="isTopLevel">
														<xsl:call-template name="isTopLevel">
															<xsl:with-param name="packageName" select="@packageName" />
														</xsl:call-template>
													</xsl:variable>
													<xsl:variable name="classPath">
	 													<xsl:if test="$isTopLevel='true'">
															<xsl:text>.</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:value-of select="translate(@packageName,'.','/')" />
														</xsl:if>
													</xsl:variable>
													<a href="{$classPath}/{@name}.html" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
														<xsl:if test="@type='interface'">
															<i><xsl:value-of select="@name"/></i>
														</xsl:if>
														<xsl:if test="@type!='interface'">
															<xsl:value-of select="@name"/>
														</xsl:if>
													</a>
													<xsl:value-of select="$emdash"/>
													<xsl:call-template name="getClassDesc">
														<xsl:with-param name="packageName" select="@packageName"/>
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="self::event or (self::method and @type='handler')">
													<xsl:variable name="isTopLevel">
														<xsl:call-template name="isTopLevel">
															<xsl:with-param name="packageName">
																<xsl:if test="ancestor::asClass">
																	<xsl:value-of select="../../@packageName"/>
																</xsl:if>
																<xsl:if test="not(ancestor::asClass)">
																	<xsl:value-of select="../../@name"/>
																</xsl:if>
															</xsl:with-param>
														</xsl:call-template>
													</xsl:variable>
													<xsl:variable name="classPath">
														<!-- AS2 lang elements -->
	 													<xsl:if test="$isTopLevel='true' or ../../@type='list'">
															<xsl:text>.</xsl:text>
														</xsl:if>
														<xsl:if test="$isTopLevel='false'">
															<xsl:if test="ancestor::asClass">
																<xsl:value-of select="translate(../../@packageName,'.','/')"/>
															</xsl:if>
															<xsl:if test="not(ancestor::asClass)">
																<xsl:value-of select="translate(../../@name,'.','/')"/>
															</xsl:if>
														</xsl:if>
													</xsl:variable>
													<xsl:choose>
														<!-- AS2 lang elements -->
														<xsl:when test="../../@type='list'">
															<a href="{../../@href}#event:{@name}">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:when>
														<xsl:when test="ancestor::asClass">
															<a href="{$classPath}/{../../@name}.html#event:{@name}" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:when>
														<xsl:otherwise>
															<a href="{$classPath}/package-detail.html#event:{@name}" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
																<xsl:value-of select="@name"/>
															</a>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:if test="@type='handler'">
														<xsl:variable name="params">
															<xsl:call-template name="getParamList">
																<xsl:with-param name="params" select="params"/>
															</xsl:call-template>
														</xsl:variable>
														<xsl:text>(</xsl:text>
														<xsl:copy-of select="$params"/>
														<xsl:text>)</xsl:text>
													</xsl:if>
													<xsl:value-of select="$emdash"/>
													<xsl:call-template name="getEventDesc">
														<xsl:with-param name="classPath" select="$classPath"/>
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="self::operator or self::statement">
													<xsl:variable name="suffix">
														<xsl:if test="self::operator and deprecated">
															<xsl:value-of select="'_deprecated'"/>
														</xsl:if>
														<xsl:if test="self::statment or not(deprecated)">
															<xsl:value-of select="''"/>
														</xsl:if>
													</xsl:variable>
													<xsl:variable name="href">
														<xsl:if test="$config/options/@docversion='2'">
															<xsl:value-of select="../@href" />
														</xsl:if>
														<xsl:if test="not($config/options/@docversion='2')">
															<xsl:value-of select="local-name()" />
															<xsl:text>s.html</xsl:text>
														</xsl:if>
													</xsl:variable>
													<a href="{$href}#{concat(translate(@name,' ','_'),$suffix)}">
														<xsl:if test="string-length(@symbol)">
															<xsl:value-of select="@symbol"/>
															<xsl:text> (</xsl:text>
														</xsl:if>
														<xsl:value-of select="@name"/>
														<xsl:if test="string-length(@symbol)">
															<xsl:text>)</xsl:text>
														</xsl:if>
													</a>
													<xsl:value-of select="$emdash"/>
													<xsl:if test="self::operator">
														<xsl:text>Operator</xsl:text>
													</xsl:if>
													<xsl:if test="self::statement">
														<xsl:text>Statement</xsl:text>
													</xsl:if>
												</xsl:when>
												<xsl:when test="self::specialType">
													<a href="specialTypes.html#{@name}">
														<xsl:value-of select="@name" />
													</a>
													<xsl:value-of select="$emdash" />
													<xsl:text>Special Type</xsl:text>
												</xsl:when>
												<xsl:when test="self::entry">
													<a href="{@href}" onclick="loadClassListFrame('mxml-tags.html');">
														<xsl:value-of select="@name" />
													</a>
													<xsl:value-of select="$emdash"/>
													<a href="mxml-tag-detail.html" onclick="loadClassListFrame('mxml-tags.html');">
														<xsl:text>MXML Only Component</xsl:text>
													</a>
												</xsl:when>
											</xsl:choose>
										</td>
									</tr>										
									<tr>
										<td width="20"/>
										<td>
											<xsl:choose>
												<xsl:when test="deprecated">
													<xsl:apply-templates select="deprecated"/>
												</xsl:when>
												<xsl:when test="(self::field or self::method or self::constructor or self::event) and ../../deprecated">
													<xsl:copy-of select="$deprecatedLabel"/>
													<em>
														<xsl:text>. The </xsl:text>
														<xsl:value-of select="../../@name"/>
														<xsl:text> class is deprecated</xsl:text>
														<xsl:if test="string-length(../../deprecated/@as-of)"> 
															<xsl:text> as of </xsl:text>
															<xsl:value-of select="../../deprecated/@as-of"/>
														</xsl:if>
														<xsl:text>.</xsl:text>
													</em>
												</xsl:when>
												<xsl:when test="self::entry">
													<xsl:call-template name="deTilda">
														<xsl:with-param name="inText" select="node()" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="self::asPackage">
													<xsl:call-template name="getPackageComment">
														<xsl:with-param name="packageName" select="@name" />
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<!-- AS2 lang elements -->
													<xsl:if test="string-length(shortDescription/.) or string-length(short-description)">
														<xsl:call-template name="deTilda">
															<xsl:with-param name="inText" select="shortDescription/. | short-description/."/>
														</xsl:call-template>
													</xsl:if>
												</xsl:otherwise>
											</xsl:choose>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
							<tr>
								<td colspan="2" style="padding-bottom:20px"/>
							</tr>
						<xsl:if test="$splitIndex!='false'">
							<tr>
								<td colspan="2">
									<xsl:variable name="currLetter" select="."/>
									<xsl:for-each select="$letterSet">
										<xsl:if test="$currLetter=.">
											<xsl:if test="$currLetter=$symbolsName">
												<font color="black" size="6px" style="bold">
													<xsl:value-of select="."/>
												</font>
											</xsl:if>
											<xsl:if test="$currLetter!=$symbolsName">
												<font color="black" size="10px" style="bold">
													<xsl:value-of select="."/>
												</font>
											</xsl:if>
										</xsl:if>
										<xsl:if test="$currLetter!=.">
											<xsl:if test="$splitIndex='false'">
												<a href="#{.}" onclick="javascript:loadClassListFrame('index-list.html');">
													<xsl:value-of select="."/>
												</a>
											</xsl:if>
											<xsl:if test="$splitIndex!='false'">
												<a href="all-index-{.}.html" onclick="javascript:loadClassListFrame('index-list.html');">
													<xsl:value-of select="." />
												</a>
											</xsl:if>
										</xsl:if>
										<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;&nbsp;]]></xsl:text>
									</xsl:for-each>
								</td>
							</tr>
						</xsl:if>
						</xsl:for-each>
					</table>
					<p/>
<!--				<xsl:call-template name="getLinks">
						<xsl:with-param name="fileName" select="$fileName"/>
						<xsl:with-param name="fileName2" select="'index-list.html'" />
						<xsl:with-param name="showProperties" select="false()"/>
						<xsl:with-param name="showMethods" select="false()"/>
						<xsl:with-param name="showIndex" select="false()"/>
						<xsl:with-param name="copyNum" select="'2'"/>
					</xsl:call-template>
					<p/>-->
					<xsl:call-template name="getFeedbackLink">
						<xsl:with-param name="topic">					
							<xsl:if test="$splitIndex">
								<xsl:value-of select="concat($letter,' Index')"/>
							</xsl:if>
							<xsl:if test="not($splitIndex)">
								<xsl:value-of select="'Index'"/>
							</xsl:if>
						</xsl:with-param>
						<xsl:with-param name="filename" select="$fileName" />
						<xsl:with-param name="filename2" select="'index-list.html'" />
					</xsl:call-template>
					<center class="copyright">
						<xsl:copy-of select="$copyright"/>
					</center>
				</div>
			</xsl:element>
		</xsl:element>
		<xsl:copy-of select="$copyrightComment"/>
	</xsl:template>

	<xsl:template name="getClassRef">
		<xsl:param name="classPath"/>

		<xsl:choose>
			<xsl:when test="string-length($classPath) > 1">
				<xsl:value-of select="../../@packageName"/>
				<xsl:text>.</xsl:text>
				<a href="{$classPath}/{../../@name}.html" onclick="javascript:loadClassListFrame('{$classPath}/class-list.html');">
					<xsl:value-of select="../../@name"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<a href="{../../@name}.html" onclick="javascript:loadClassListFrame('class-list.html');">
					<xsl:value-of select="../../@name"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getMethodDesc">
		<xsl:param name="classPath"/>

		<!-- AS2 lang elements -->
		<xsl:if test="../../@type and ../../@type!='list'">
			<!-- TODO handle more variations (override,final?) -->
			<xsl:if test="@isStatic = 'true'">Static method in</xsl:if>
			<xsl:if test="@isStatic != 'true'">Method in</xsl:if>
			<xsl:text> </xsl:text>
			<xsl:value-of select="../../@type"/>
			<xsl:text> </xsl:text>
			<xsl:call-template name="getClassRef">
				<xsl:with-param name="classPath" select="$classPath"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not(../../@type)">
			<xsl:if test="@isStatic = 'true'">Package static function in </xsl:if>
			<xsl:if test="@isStatic != 'true'">Package function in </xsl:if>
			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="ancestor::asPackage/@name"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$isTopLevel='false'">
				<a href="{$classPath}/package.html" onclick="loadClassListFrame('{$classPath}/class-list.html')">
					<xsl:value-of select="ancestor::asPackage/@name" />
				</a>
			</xsl:if>
			<xsl:if test="$isTopLevel!='false'">
				<a href="package.html" onclick="loadClassListFrame('class-list.html')">Top Level</a>
			</xsl:if>
		</xsl:if>
		<!-- AS2 lang elements -->
		<xsl:if test="../../@type='list'">
			<xsl:if test="@type='directive'">
				<xsl:text>Compiler Directive</xsl:text>
			</xsl:if>
			<xsl:if test="@type!='directive'">
				<xsl:text>Global function</xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getConstructorDesc">
		<xsl:param name="classPath"/>

		<xsl:text>Constructor in class</xsl:text>
		<xsl:text> </xsl:text>
		<xsl:call-template name="getClassRef">
			<xsl:with-param name="classPath" select="$classPath"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="getPropertyDesc">
		<xsl:param name="classPath"/>

		<!-- AS2 lang elements -->
		<xsl:if test="../../@type and ../../@type!='list'">
			<xsl:if test="@isStatic = 'true'">
				<xsl:if test="@isConst = 'true'">Constant static property in</xsl:if>
				<xsl:if test="@isConst != 'true'">Static property in</xsl:if>
			</xsl:if>
			<xsl:if test="@isStatic != 'true'">
				<xsl:if test="@isConst = 'true'">Constant property in</xsl:if>
				<xsl:if test="@isConst != 'true'">Property in</xsl:if>
			</xsl:if>
			<xsl:text> </xsl:text>
			<xsl:value-of select="../../@type"/>
			<xsl:text> </xsl:text>
			<xsl:call-template name="getClassRef">
				<xsl:with-param name="classPath" select="$classPath"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not(../../@type)">
			<xsl:if test="@isStatic = 'true'">
				<xsl:if test="@isConst = 'true'">Package constant static property in </xsl:if>
				<xsl:if test="@isConst != 'true'">Package static property in </xsl:if>
			</xsl:if>
			<xsl:if test="@isStatic != 'true'">
				<xsl:if test="@isConst = 'true'">Package constant property in </xsl:if>
				<xsl:if test="@isConst != 'true'">Package property in </xsl:if>
			</xsl:if>
			<xsl:variable name="isTopLevel">
				<xsl:call-template name="isTopLevel">
					<xsl:with-param name="packageName" select="ancestor::asPackage/@name"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$isTopLevel='false'">
				<a href="{$classPath}/package.html" onclick="loadClassListFrame('{$classPath}/class-list.html')">
					<xsl:value-of select="ancestor::asPackage/@name" />
				</a>
			</xsl:if>
			<xsl:if test="$isTopLevel!='false'">
				<a href="package.html" onclick="loadClassListFrame('class-list.html')">Top Level</a>
			</xsl:if>
		</xsl:if>
		<!-- AS2 lang elements -->
		<xsl:if test="../../@type='list'">
			<xsl:if test="../../@name='Constants'">
				<xsl:text>Constant property</xsl:text>
			</xsl:if>
			<xsl:if test="../../@name!='Constants'">
				<xsl:text>Global property</xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getEventDesc">
		<xsl:param name="classPath"/>

		<!-- AS2 lang elements -->
		<xsl:if test="../../@type!='list'">
			<xsl:choose>
				<xsl:when test="@type = 'handler'">
					<xsl:text>Event handler in</xsl:text>
				</xsl:when>
				<xsl:when test="@type != 'handler'">
					<xsl:text>Event listener in</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>Event in</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> </xsl:text>
			<xsl:value-of select="../../@type"/>
			<xsl:text> </xsl:text>
			<xsl:call-template name="getClassRef">
				<xsl:with-param name="classPath" select="$classPath"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not(../../@type) or ../../@type='list'">
			<xsl:if test="@type = 'handler'">
				<xsl:text>Global event handler</xsl:text>
			</xsl:if>
			<xsl:if test="@type != 'handler'">
				<xsl:text>Global event listener</xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getPackageComment">
		<xsl:param name="packageName" />
		
		<xsl:if test="not($config/overviews/package)">
			<xsl:variable name="packageComments" select="document($overviewsFile)/overviews/packages/package[@name=$packageName]"/>
			<xsl:if test="string-length($packageComments/shortDescription/.)">
				<xsl:call-template name="deTilda">
					<xsl:with-param name="inText" select="$packageComments/shortDescription"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$config/overviews/package">
			<xsl:for-each select="$config/overviews/package">
				<xsl:variable name="packageOverview" select="document(.)/overviews/packages/package[@name=$packageName]" />									
				<xsl:if test="string-length($packageOverview/shortDescription/.)">
					<xsl:call-template name="deTilda">
						<xsl:with-param name="inText" select="$packageOverview/shortDescription" />
					</xsl:call-template>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getClassDesc">
		<xsl:param name="packageName"/>

		<xsl:if test="string-length($packageName)=0">

			<xsl:if test="@isFinal = 'true'">
				<xsl:if test="@isDynamic = 'true'">Final dynamic class</xsl:if>
				<xsl:if test="not(@isDynamic) or @isDynamic != 'true'">Final class</xsl:if>
			</xsl:if>
			<xsl:if test="@isFinal != 'true'">
				<xsl:if test="@isDynamic = 'true'">Dynamic class</xsl:if>
				<xsl:if test="not(@isDynamic) or @isDynamic != 'true'">
					<xsl:if test="@type='interface'">Interface</xsl:if>
					<xsl:if test="@type!='interface'">Class</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:text> in </xsl:text>
			<a href="package-detail.html" onclick="javascript:loadClassListFrame('class-list.html');">Top Level</a>
		</xsl:if>
		<xsl:if test="string-length($packageName)">
			<xsl:if test="@isFinal = 'true'">
				<xsl:if test="@isDynamic = 'true'">Final dynamic class</xsl:if>
				<xsl:if test="not(@isDynamic) or @isDynamic != 'true'">Final class</xsl:if>
			</xsl:if>
			<xsl:if test="@isFinal != 'true'">
				<xsl:if test="@isDynamic = 'true'">Dynamic class</xsl:if>
				<xsl:if test="not(@isDynamic) or @isDynamic != 'true'">
					<xsl:if test="@type='interface'">Interface</xsl:if>
					<xsl:if test="@type!='interface'">Class</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:text> in package </xsl:text>
			<a href="{translate($packageName,'.','/')}/package-detail.html" onclick="javascript:loadClassListFrame('{translate($packageName,'.','/')}/class-list.html');">
				<xsl:value-of select="$packageName"/>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getParamList">
		<xsl:param name="params"/>

		<xsl:for-each select="$params/param">
			<xsl:if test="position()>1">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:if test="$config/options/@docversion='2' and @optional='true'">
				<xsl:text>[</xsl:text>
			</xsl:if>
			<xsl:if test="not(@type = 'restParam')">
				<xsl:variable name="href">
					<!-- AS2 lang elements -->
					<xsl:if test="../../../../@type='list'">
						<xsl:value-of select="@type"/>
					</xsl:if>
					<xsl:if test="../../../../@type!='list'">
						<xsl:call-template name="convertFullName">
							<xsl:with-param name="fullname" select="classRef/@fullName"/>
							<xsl:with-param name="separator">/</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="@name" />
				<xsl:if test="string-length($href)">
					<xsl:text>:</xsl:text>
					<xsl:if test="../../../../@type='list'">
						<a href="{$href}.html">
							<xsl:value-of select="@type" />
						</a>
					</xsl:if>
					<xsl:if test="../../../../@type!='list'">
						<a href="{$href}.html">
							<xsl:attribute name="onclick">
								<xsl:text>javascript:loadClassListFrame('</xsl:text>
								<xsl:call-template name="substring-before-last">
									<xsl:with-param name="input" select="$href" />
									<xsl:with-param name="substr" select="'/'" />
								</xsl:call-template>
								<xsl:text>./class-list.html');</xsl:text>
							</xsl:attribute>
							<xsl:value-of select="@type" />
						</a>
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:if test="@type = 'restParam'">
				<xsl:text>... rest</xsl:text>
			</xsl:if>
			<xsl:if test="$config/options/@docversion='2' and @optional='true'">
				<xsl:text>]</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>