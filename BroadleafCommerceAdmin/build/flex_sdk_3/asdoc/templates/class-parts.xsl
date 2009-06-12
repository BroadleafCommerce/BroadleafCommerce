<?xml version='1.0'?>

<!--

	ADOBE SYSTEMS INCORPORATED
	Copyright 2006-2007 Adobe Systems Incorporated
	All Rights Reserved.

	NOTICE: Adobe permits you to use, modify, and distribute this file
	in accordance with the terms of the license agreement accompanying it.

-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:str="http://exslt.org/strings"
exclude-result-prefixes="str">

	<xsl:import href="asdoc-util.xsl"/>

	<xsl:template name="classInheritance">
		<xsl:param name="baseRef" />

		<xsl:variable name="iconRef">
			<xsl:value-of select="$baseRef" />
			<xsl:text>images/inherit-arrow.gif</xsl:text>
		</xsl:variable>

		<xsl:value-of select="@name" />
		<xsl:text> </xsl:text>
		<img src="{$iconRef}" title="Inheritance" alt="Inheritance" class="inheritArrow" />
		<xsl:text> </xsl:text>

		<xsl:for-each select="asAncestors/asAncestor">
            <xsl:if test="(classRef/@relativePath) != 'none'">
 			    <a href="{translate(classRef/@relativePath,':','/')}">
			    <xsl:value-of select="classRef/@name"/> 
				</a>
            </xsl:if>
            <xsl:if test="(classRef/@relativePath) = 'none'">
			    <xsl:value-of select="classRef/@name"/> 
            </xsl:if>
			<xsl:if test="position() != last()">
				<xsl:text> </xsl:text>
				<img src="{$iconRef}" title="Inheritance" alt="Inheritance" class="inheritArrow" />
				<xsl:text> </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="defaultProperty">
		<p>
			<span class="classHeaderTableLabel">
				<xsl:text>Default MXML Property</xsl:text>
			</span>
			<code>
				<xsl:value-of select="@name" />
			</code>
		</p>
	</xsl:template>

	<xsl:template name="classHeader">
		<xsl:param name="classDeprecated" />

		<xsl:variable name="baseRef">
			<xsl:call-template name="getBaseRef">
				<xsl:with-param name="packageName" select="@packageName" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="getPageLinks">
			<xsl:with-param name="title">
				<xsl:if test="@type='interface'">
					<xsl:value-of select="concat('Interface',$nbsp,@name)" />
				</xsl:if>
				<xsl:if test="@type!='interface'">
					<xsl:value-of select="concat('Class',$nbsp,@name)" />
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>

		<div class="MainContent">
			<xsl:variable name="name" select="@name" />
			<xsl:variable name="packageName" select="@packageName" />
			<xsl:apply-templates mode="annotate" select="$config/annotate/item[@type='class' and ((@name=$name and (not(string-length(@packageName)) or @packageName=$packageName)) or (not(string-length(@name)) and string-length(@packageName) and str:tokenize(@packageName,',')[starts-with($packageName,.)]))]" />
	
			<table class="classHeaderTable" cellpadding="0" cellspacing="0">
				<tr>
					<td class="classHeaderTableLabel">Package</td>
					<td>
						<a href="package-detail.html" onclick="javascript:loadClassListFrame('class-list.html')">
							<xsl:if test="string-length(@packageName) &gt; 0">
								<xsl:value-of select="@packageName"/>
							</xsl:if>
							<xsl:if test="string-length(@packageName) = 0">
								<xsl:text>Top Level</xsl:text>
							</xsl:if>
						</a>
					</td>
				</tr>
				<tr>
					<xsl:if test="@type='class'">
						<td class="classHeaderTableLabel">Class</td>
					</xsl:if>
					<xsl:if test="@type='interface'">
						<td class="classHeaderTableLabel">Interface</td>
					</xsl:if>
					<td class="classSignature">
						<xsl:value-of select="@accessLevel"/>
						<xsl:if test="@isFinal='true'">
							<xsl:text> final </xsl:text>
						</xsl:if>
						<xsl:if test="@isDynamic='true'">
							<xsl:text> dynamic </xsl:text>
						</xsl:if>
						<xsl:text> </xsl:text>
						<xsl:value-of select="@type" />
						<xsl:text> </xsl:text>
						<xsl:value-of select="@name"/>
						<xsl:if test="@type='interface' and asAncestors/asAncestor">
							<xsl:text> extends </xsl:text>
							<xsl:for-each select="asAncestors/asAncestor">
								<a href="{classRef/@relativePath}">
									<xsl:value-of select="classRef/@name" />
								</a>
								<xsl:if test="position()!=last()">
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</td>
				</tr>
				<xsl:if test="@type!='interface' and asAncestors/asAncestor">
					<tr>
						<td class="classHeaderTableLabel">Inheritance</td>
						<td class="inheritanceList">
							<xsl:call-template name="classInheritance">
								<xsl:with-param name="baseRef" select="$baseRef" />
							</xsl:call-template>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="asImplements/asAncestor">
					<tr>
						<td class="classHeaderTableLabel">Implements</td>
						<td>
							<xsl:for-each select="asImplements/asAncestor">
								<xsl:sort select="classRef/@name"/>

                                <xsl:if test="(classRef/@relativePath) != 'none'">
						 			<a href="{translate(classRef/@relativePath,':','/')}">
										<xsl:value-of select="classRef/@name"/>
									</a>
                                </xsl:if>
                                <xsl:if test="(classRef/@relativePath) = 'none'">
									<xsl:value-of select="classRef/@name"/>
                                </xsl:if>
								<xsl:if test="position() != last()">
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="asDecendants/classRef">
					<tr>
						<td class="classHeaderTableLabel">
							<xsl:if test="@type='interface'">
								<xsl:text>Subinterfaces</xsl:text>
							</xsl:if>
							<xsl:if test="@type!='interface'">
								<xsl:text>Subclasses</xsl:text>
							</xsl:if>
						</td>
						<td>
							<xsl:for-each select="asDecendants/classRef">
								<xsl:sort select="@name"/>

								<a href="{translate(@relativePath,':','/')}">
									<xsl:value-of select="@name"/>
								</a>
								<xsl:if test="position() != last()">
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="implementers/classRef">
					<tr>
						<td class="classHeaderTableLabel">Implementors</td>
						<td>
							<xsl:for-each select="implementers/classRef">
								<xsl:sort select="@name"/>

								<a href="{translate(@relativePath,':','/')}">
									<xsl:value-of select="@name"/>
								</a>
								<xsl:if test="position() != last()">
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
			</table>
			<xsl:if test="$classDeprecated='true'">
				<xsl:apply-templates select="deprecated"/>
				<br/>
			</xsl:if>
			<xsl:call-template name="version"/>
			<p/>
				<xsl:call-template name="description"/>
			<p/>

			<xsl:if test="customs/mxml">
				<a name="mxmlSyntaxSummary"></a>
				<span class="classHeaderTableLabel">
					<xsl:text>MXML Syntax</xsl:text>
				</span>
				<span id="showMxmlLink" style="display:none">
					<a href="#mxmlSyntaxSummary" onclick="toggleMXMLOnly();">
						<img src="{$baseRef}images/collapsed.gif" title="collapsed" alt="collapsed" class="collapsedImage" />
						<xsl:text> Show MXML Syntax</xsl:text>
					</a>
					<br />
				</span>
				<span id="hideMxmlLink">
					<a href="#mxmlSyntaxSummary" onclick="toggleMXMLOnly();">
						<img src="{$baseRef}images/expanded.gif" title="expanded" alt="expanded" class="expandedImage" />
						<xsl:text> Hide MXML Syntax</xsl:text>
					</a>
				</span>
				<div id="mxmlSyntax" class="mxmlSyntax">
					<xsl:value-of disable-output-escaping="yes" select="customs/mxml/." />
				</div>
				<script language="javascript" type="text/javascript">
					<xsl:comment>
						<xsl:text>
</xsl:text>
						<xsl:text>setMXMLOnly();</xsl:text>
						<xsl:text>
</xsl:text>
					</xsl:comment>
				</script>
			</xsl:if>

			<xsl:apply-templates select="defaultProperty" />

			<xsl:apply-templates select="example"/>
			<xsl:call-template name="includeExampleLink"/>
			<xsl:call-template name="sees">
				<xsl:with-param name="labelClass" select="'classHeaderTableLabel'" />
			</xsl:call-template>
			<br />
			<hr />
		</div>
	</xsl:template>

</xsl:stylesheet>