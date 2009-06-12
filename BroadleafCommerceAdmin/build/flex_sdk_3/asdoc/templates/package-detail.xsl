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
	<xsl:param name="outputPath" select="'../out/'"/>
	<xsl:param name="overviewsFile" select="'../xml/overviews.xml'" />

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
					<xsl:when test="$isTopLevel='true'">package-detail.html</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate(@name,'.','/')"/>/package-detail.html</xsl:otherwise>
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

			<xsl:variable name="title">
				<xsl:if test="$isTopLevel='true'">
					<xsl:value-of select="concat('Top Level - ',$title-base)"/>
				</xsl:if>
				<xsl:if test="$isTopLevel != 'true'">

					<xsl:value-of select="concat(@name,' Package - ',$title-base)"/>
				</xsl:if>
			</xsl:variable>

			<redirect:write select="$packageFile">
				<xsl:copy-of select="$noLiveDocs" />
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
							<xsl:text> Summary</xsl:text>
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
<!-- 						<xsl:if test="$isTopLevel='true'">
							<xsl:call-template name="getFeedbackLink">
								<xsl:with-param name="topic" select="'Top Level'"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="$isTopLevel!='true'">
							<xsl:call-template name="getFeedbackLink">
								<xsl:with-param name="topic" select="@name"/>
							</xsl:call-template>
						</xsl:if> -->
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
							<xsl:with-param name="fileName" select="'package-detail'"/>
							<xsl:with-param name="fileName2" select="$classListFile" />
							<xsl:with-param name="packageName" select="@name"/>
							<xsl:with-param name="showProperties" select="false()"/>
							<xsl:with-param name="showMethods" select="false()"/>
							<xsl:with-param name="showPackageConstants" select="boolean(count(fields/field[@isConst='true']))"/>
							<xsl:with-param name="showPackageProperties" select="boolean(count(fields/field[@isConst='false']))"/>
							<xsl:with-param name="showPackageFunctions" select="boolean(count(methods))"/>
							<xsl:with-param name="showInterfaces" select="boolean(count(classes/asClass[@type='interface']))" />
							<xsl:with-param name="showClasses" select="boolean(count(classes/asClass[@type!='interface']))" />
 							<xsl:with-param name="showPackageUse" select="false()" />
						</xsl:call-template>
						<div class="MainContent">
							<xsl:apply-templates mode="annotate" select="$config/annotate/item[@type='package' and string-length(@name) and str:tokenize(@name,',')[starts-with(current()/@name,.)]]" />
							<br />
							<xsl:if test="not($config/overviews/package)">
								<xsl:variable name="packageComments" select="document($overviewsFile)/overviews/packages/package[@name=current()/@name]"/>
								<xsl:if test="string-length($packageComments/longDescription/.)">
									<p>
										<xsl:call-template name="deTilda">
											<xsl:with-param name="inText" select="$packageComments/longDescription"/>
										</xsl:call-template>
									</p>
									<xsl:for-each select="$packageComments">
										<xsl:call-template name="sees" />
									</xsl:for-each>
								</xsl:if>
							</xsl:if>
							<xsl:if test="$config/overviews/package">
								<xsl:variable name="pname" select="@name" />
								<xsl:for-each select="$config/overviews/package">
									<xsl:variable name="packageOverview" select="document(.)/overviews/packages/package[@name=$pname]" />
									<xsl:if test="$packageOverview/longDescription">
										<p> blah
											<xsl:call-template name="deTilda">
												<xsl:with-param name="inText" select="$packageOverview/longDescription" />
											</xsl:call-template>
										</p>
                                    </xsl:if>
									<xsl:for-each select="$packageOverview">
										<xsl:call-template name="sees">
											<xsl:with-param name="xrefId">
												<xsl:if test="$isTopLevel='true'">
													<xsl:text>global</xsl:text>
												</xsl:if>
												<xsl:if test="not($isTopLevel='true')">
													<xsl:value-of select="$pname" />
												</xsl:if>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:for-each>
								</xsl:for-each>
							</xsl:if>
							<br />
							<hr />
							<xsl:if test="fields/field[@isConst='false']">
								<a name="fieldSummary"></a>
								<div class="summaryTableTitle">
									<xsl:if test="$isTopLevel='true'">
										<xsl:text>Global </xsl:text>
									</xsl:if>
									<xsl:text>Properties</xsl:text>
								</div>
								<table cellpadding="3" cellspacing="0" class="summaryTable">
									<tr>
										<th>
											<xsl:value-of select="$nbsp" />
										</th>
										<th width="30%">
											<xsl:text>Property</xsl:text>
										</th>
										<th width="70%">
											<xsl:text>Description</xsl:text>
										</th>
									</tr>
									<xsl:for-each select="fields/field[@isConst='false']">
										<xsl:sort select="@name" order="ascending"/>

										<tr class="prow{position() mod 2}">
											<td class="summaryTablePaddingCol">
												<xsl:value-of select="$nbsp" />
											</td>
											<td class="summaryTableSecondCol">
												<a href="package.html#{@name}">
													<xsl:value-of select="@name"/>
												</a>
											</td>
											<td class="summaryTableLastCol">
												<xsl:call-template name="shortDescriptionReview" />
												<xsl:if test="string-length(normalize-space(shortDescription/.))">
													<xsl:value-of select="shortDescription" disable-output-escaping="yes"/>
												</xsl:if>
												<xsl:if test="not(string-length(normalize-space(shortDescription/.)))">
													<xsl:value-of select="$nbsp" />
												</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:if>
							<xsl:if test="methods/method">
								<a name="methodSummary"></a>
								<div class="summaryTableTitle">
									<xsl:if test="$isTopLevel='true'">
										<xsl:text>Global </xsl:text>
									</xsl:if>
									<xsl:text>Functions</xsl:text>
								</div>
								<table cellpadding="3" cellspacing="0" class="summaryTable">
									<tr>
										<th>
											<xsl:value-of select="$nbsp" />
										</th>
										<th width="30%">
											<xsl:text>Function</xsl:text>
										</th>
										<th width="70%">
											<xsl:text>Description</xsl:text>
										</th>
									</tr>
									<xsl:for-each select="methods/method">
										<xsl:sort select="@name" order="ascending"/>

										<tr class="prow{position() mod 2}">
											<td class="summaryTablePaddingCol">
												<xsl:value-of select="$nbsp" />
											</td>
											<td class="summaryTableSecondCol">
												<a href="package.html#{@name}()">
													<xsl:value-of select="@name"/>
												</a>
											</td>
											<td class="summaryTableLastCol">
												<xsl:call-template name="shortDescriptionReview" />
												<xsl:if test="string-length(normalize-space(shortDescription/.))">
													<xsl:value-of select="shortDescription" disable-output-escaping="yes"/>
												</xsl:if>
												<xsl:if test="not(string-length(normalize-space(shortDescription/.)))">
													<xsl:value-of select="$nbsp" />
												</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:if>
							<xsl:if test="fields/field[@isConst='true']">
								<a name="constantSummary"></a>
								<div class="summaryTableTitle">
									<xsl:if test="$isTopLevel='true'">
										<xsl:text>Global </xsl:text>
									</xsl:if>
									<xsl:text>Constants</xsl:text>
								</div>
								<table cellpadding="3" cellspacing="0" class="summaryTable">
									<tr>
										<th>
											<xsl:value-of select="$nbsp" />
										</th>
										<th width="30%">
											<xsl:text>Constant</xsl:text>
										</th>
										<th width="70%">
											<xsl:text>Description</xsl:text>
										</th>
									</tr>
									<xsl:for-each select="fields/field[@isConst='true']">
										<xsl:sort select="@name" order="ascending"/>

										<tr class="prow{position() mod 2}">
											<td class="summaryTablePaddingCol">
												<xsl:value-of select="$nbsp" />
											</td>
											<td class="summaryTableSecondCol">
												<a href="package.html#{@name}">
													<xsl:value-of select="@name"/>
												</a>
											</td>
											<td class="summaryTableLastCol">
												<xsl:call-template name="shortDescriptionReview" />
												<xsl:if test="string-length(normalize-space(shortDescription/.))">
													<xsl:value-of select="shortDescription" disable-output-escaping="yes"/>
												</xsl:if>
												<xsl:if test="not(string-length(normalize-space(shortDescription/.)))">
													<xsl:value-of select="$nbsp" />
												</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:if>
							<xsl:if test="classes/asClass[@type='interface']">
								<a name="interfaceSummary"></a>
								<div class="summaryTableTitle">
									<xsl:text>Interfaces</xsl:text>
								</div>
								<table cellpadding="3" cellspacing="0" class="summaryTable">
									<tr>
										<th>
											<xsl:value-of select="$nbsp" />
										</th>
										<th width="30%">
											<xsl:text>Interface</xsl:text>
										</th>
										<th width="70%">
											<xsl:text>Description</xsl:text>
										</th>
									</tr>
									<xsl:for-each select="classes//asClass[@type='interface']">
										<xsl:sort select="@name" order="ascending"/>

										<tr class="prow{position() mod 2}">
											<td class="summaryTablePaddingCol">
												<xsl:value-of select="$nbsp" />
											</td>
											<td class="summaryTableSecondCol">
												<i>
													<a href="{@name}.html">
														<xsl:value-of select="@name"/>
													</a>
												</i>
											</td>
											<td class="summaryTableLastCol">
												<xsl:call-template name="shortDescriptionReview" />
												<xsl:if test="deprecated">
													<xsl:apply-templates select="deprecated"/>
												</xsl:if>
												<xsl:if test="not(deprecated)">
													<xsl:if test="string-length(normalize-space(shortDescription/.))">
														<xsl:value-of select="shortDescription" disable-output-escaping="yes"/>
													</xsl:if>
													<xsl:if test="not(string-length(normalize-space(shortDescription/.)))">
														<xsl:value-of select="$nbsp" />
													</xsl:if>
												</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:if>
							<xsl:if test="classes//asClass[@type!='interface']">
								<a name="classSummary"></a>
								<div class="summaryTableTitle">
									<xsl:text>Classes</xsl:text>
								</div>
								<table cellpadding="3" cellspacing="0" class="summaryTable">
									<tr>
										<th>
											<xsl:value-of select="$nbsp" />
										</th>
										<th width="30%">
											<xsl:text>Class</xsl:text>
										</th>
										<th width="70%">
											<xsl:text>Description</xsl:text>
										</th>
									</tr>
									<xsl:for-each select="classes//asClass[@type!='interface']">
										<xsl:sort select="@name" order="ascending"/>

										<tr class="prow{position() mod 2}">
											<td class="summaryTablePaddingCol">
												<xsl:value-of select="$nbsp" />
											</td>
											<td class="summaryTableSecondCol">
												<a href="{@name}.html">
													<xsl:value-of select="@name"/>
												</a>
											</td>
											<td class="summaryTableLastCol">
												<xsl:call-template name="shortDescriptionReview" />
												<xsl:if test="deprecated">
													<xsl:apply-templates select="deprecated"/>
												</xsl:if>
												<xsl:if test="not(deprecated)">
													<xsl:if test="string-length(normalize-space(shortDescription/.))">
														<xsl:value-of select="shortDescription" disable-output-escaping="yes"/>
													</xsl:if>
													<xsl:if test="not(string-length(normalize-space(shortDescription/.)))">
														<xsl:value-of select="$nbsp" />
													</xsl:if>
												</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:if>
							<p/>
	<!--						<xsl:call-template name="getLinks">
								<xsl:with-param name="fileName" select="'package-detail'"/>
								<xsl:with-param name="fileName2" select="$classListFile" />
								<xsl:with-param name="packageName" select="@name"/>
								<xsl:with-param name="showProperties" select="false()"/>
								<xsl:with-param name="showMethods" select="false()"/>
								<xsl:with-param name="showPackageConstants" select="boolean(count(fields/field[@isConst='true']))"/>
								<xsl:with-param name="showPackageProperties" select="boolean(count(fields/field[@isConst='false']))"/>
								<xsl:with-param name="showPackageFunctions" select="boolean(count(methods))"/>
								<xsl:with-param name="showInterfaces" select="boolean(count(classes/asClass[@type='interface']))" />
								<xsl:with-param name="showClasses" select="boolean(count(classes/asClass[@type!='interface']))" />
	 							<xsl:with-param name="showPackageUse" select="false()" />
								<xsl:with-param name="copyNum" select="'2'"/>
							</xsl:call-template>
							<p/>-->
							<div>
								<p/>
	<!-- 							<xsl:if test="$isTopLevel='true'">
									<xsl:call-template name="getFeedbackLink">
										<xsl:with-param name="topic" select="'Top Level'"/>
									</xsl:call-template>
								</xsl:if>
								<xsl:if test="$isTopLevel!='true'">
									<xsl:call-template name="getFeedbackLink">
										<xsl:with-param name="topic" select="@name"/>
									</xsl:call-template>
								</xsl:if> -->
								<center class="copyright">
									<xsl:copy-of select="$copyright"/>
								</center>
							</div>
						</div>
					</xsl:element>
				</xsl:element>
				<xsl:copy-of select="$copyrightComment"/>
			</redirect:write>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>