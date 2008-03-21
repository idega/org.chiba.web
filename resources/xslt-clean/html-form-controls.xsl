<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xforms="http://www.w3.org/2002/xforms"
                xmlns:chiba="http://chiba.sourceforge.net/xforms"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                exclude-result-prefixes="chiba xforms xlink xsl">
    <!-- Copyright 2001-2007 ChibaXForms GmbH -->

    <xsl:variable name="data-prefix" select="'d_'"/>
    <xsl:variable name="trigger-prefix" select="'t_'"/>
    <xsl:variable name="remove-upload-prefix" select="'ru_'"/>

    <!-- change this to your ShowAttachmentServlet -->
    <xsl:variable name="show-attachment-action" select="'http://localhost:8080/chiba-1.0.0/ShowAttachmentServlet'"/>

    <!-- This stylesheet contains a collection of templates which map XForms controls to HTML controls. -->
    <xsl:output method="html" version="4.01" indent="yes"/>


    <!-- ######################################################################################################## -->
    <!-- This stylesheet serves as a 'library' for HTML form controls. It contains only named templates and may   -->
    <!-- be re-used in different layout-stylesheets to create the naked controls.                                 -->
    <!-- ######################################################################################################## -->

    <!-- build input control -->
    <xsl:template name="input">
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="incremental" select="@incremental"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>

        <!-- this is only an interims solution until Schema type and base type handling has been clarified -->
        <xsl:variable name="type">
            <xsl:call-template name="getType"/>
        </xsl:variable>

        <!--<xsl:value-of select="$type"/>-->
        <xsl:choose>
            <!-- input bound to 'date' or 'dateTime' type -->
            <!--<xsl:when test="chiba:data[@chiba:type='date' or @chiba:type='dateTime']">-->
            <xsl:when test="($type='date' or $type='dateTime' or $type='time') and $scripted='true'">
               <script type="text/javascript">
                    dojo.require("chiba.widget.DropdownDatePicker");
                </script>
				<xsl:variable name="controlType">

					<xsl:choose>
						<xsl:when test="$type='dateTime' and @appearance='chiba:time'">chiba:DropdownDatePicker</xsl:when>
						<xsl:otherwise>chiba:DropdownDatePicker</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<input id="{concat($id,'-value')}" type="text" name="{$name}" value="" readonly="" class="value">
                    <xsl:if test="string-length($navindex) != 0">
                        <xsl:attribute name="tabindex">
                            <xsl:value-of select="$navindex"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$scripted='true'">
                        <!--<xsl:attribute name="dojoType">chiba:DropdownDatePicker</xsl:attribute>-->
                        <xsl:attribute name="dojoType">
                            <xsl:value-of select="$controlType"/>
                        </xsl:attribute>
                        <xsl:attribute name="datatype">
                            <xsl:value-of select="$type"/>
                        </xsl:attribute>
                        <xsl:attribute name="xfreadonly">
                            <xsl:value-of select="chiba:data/@chiba:readonly"/>
                        </xsl:attribute>
                        <xsl:attribute name="xfincremental">
                            <xsl:value-of select="$incremental"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="chiba:data/text()"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="xforms:hint"/>
                </input>
            </xsl:when>
            <xsl:when test="$type='boolean'">
                <xsl:choose>
                    <xsl:when test="$scripted='true'">
                        <script type="text/javascript">
                            dojo.require("chiba.widget.Boolean");
                        </script>
                        <input id="{$id}-value" type="checkbox" name="{$name}" class="value">
                            <xsl:if test="string-length($navindex) != 0">
                                <xsl:attribute name="tabindex">
                                    <xsl:value-of select="$navindex"/>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:if test="chiba:data/@chiba:readonly='true'">
                                <xsl:attribute name="disabled">disabled</xsl:attribute>
                            </xsl:if>
                            <xsl:choose>
                                <xsl:when test="chiba:data/text()='true'">
                                    <xsl:attribute name="checked">true</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="value">false</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:if test="$scripted='true'">
                                <xsl:attribute name="dojoType">chiba:Boolean</xsl:attribute>
                                <xsl:attribute name="xfreadonly">
                                    <xsl:value-of select="chiba:data/@chiba:readonly"/>
                                </xsl:attribute>
                                <xsl:attribute name="xfincremental">
                                    <xsl:value-of select="$incremental"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:apply-templates select="xforms:hint"/>
                        </input>
                    </xsl:when>
                    <xsl:otherwise>
                        <input id="{$id}-value" type="checkbox" name="{$name}" class="value">
                            <xsl:if test="string-length($navindex) != 0">
                                <xsl:attribute name="tabindex">
                                    <xsl:value-of select="$navindex"/>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:if test="chiba:data/@chiba:readonly='true'">
                                <xsl:attribute name="disabled">disabled</xsl:attribute>
                            </xsl:if>
                            <xsl:choose>
                                <xsl:when test="chiba:data/text()='true'">
                                    <xsl:attribute name="checked">true</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="value">true</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:apply-templates select="xforms:hint"/>
                        </input>
                        <!-- create hidden parameter for deselection -->
                        <xsl:if test="chiba:data='true' and $scripted='true'">
                            <input type="hidden" name="{$name}" value="false"/>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="input">
                    <xsl:if test="string-length($navindex) != 0">
                        <xsl:attribute name="tabindex">
                            <xsl:value-of select="$navindex"/>
                        </xsl:attribute>
                    </xsl:if>                    
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="concat($data-prefix,$id)"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">text</xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="chiba:data/text()"/>
                    </xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="$scripted='true'">
                        <xsl:choose>
                            <xsl:when test="$incremental='true'">
                                <xsl:attribute name="onkeyup">setXFormsValue(this);</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="onkeyup">return keepAlive(this);</xsl:attribute>
                                <xsl:attribute name="onchange">setXFormsValue(this);</xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:attribute name="onkeydown">DWRUtil.onReturn(event, submitFunction);</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="xforms:hint"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="getType">
        <xsl:choose>
            <xsl:when test="contains(chiba:data/@chiba:type,':')">
                <xsl:value-of select="substring-after(chiba:data/@chiba:type,':')"/>
            </xsl:when>
            <xsl:when test="chiba:data/@chiba:type">
                <xsl:value-of select="chiba:data/@chiba:type"/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

    <!-- build output -->
    <xsl:template name="output">

        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:choose>
            <xsl:when test="contains(@mediatype,'image/')">
                <xsl:element name="img">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="src">
                        <xsl:value-of select="chiba:data/text()"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                        <xsl:value-of select="xforms:label"/>
                    </xsl:attribute>
                    <xsl:apply-templates select="xforms:hint"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="contains(@mediatype,'text/html')">
                <span id="{concat($id,'-value')}" class="mediatype-text-html">
                    <xsl:value-of select="chiba:data/text()" disable-output-escaping="yes"/>
                </span>
            </xsl:when>
            <xsl:when test="chiba:data[@chiba:type='anyURI'] and (not(@mediatype))">
                <!-- SIDOC/CNAF : sidoc-infra-204, implementation de l'approche Dojo -->
                <xsl:if test="$scripted='true'">
                    <script type="text/javascript">
                        dojo.require("chiba.widget.Link");
                    </script>
                </xsl:if>
                <xsl:element name="a">
                   <xsl:if test="string-length($navindex) != 0">
                     <xsl:attribute name="tabindex"><xsl:value-of select="$navindex"/></xsl:attribute>
                   </xsl:if>

                    <!-- SIDOC/CNAF : sidoc-infra-204, implementation de l'approche Dojo -->
                    <xsl:attribute name="dojoType">XFLink</xsl:attribute>
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of select="chiba:data/text()"/>
                    </xsl:attribute>
                    <xsl:apply-templates select="xforms:hint"/>
                    <xsl:value-of select="chiba:data/text()"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="xforms:hint"/>
                <span id="{concat($id,'-value')}">
                    <xsl:value-of select="chiba:data/text()"/>
                </span>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- build range -->
    <!--
         todo: add input/output control at the side of slider in scripted mode
         todo: support different appearances ?
         todo: support incremental ?
    -->
    <xsl:template name="range">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="start" select="@start"/>
        <xsl:variable name="end" select="@end"/>
        <xsl:variable name="step" select="@step"/>
        <xsl:variable name="showInput">
            <xsl:choose>
                <xsl:when test="@appearance='full'">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="value" select="chiba:data/text()"/>

        <div>
            <xsl:choose>
                <xsl:when test="$scripted='true'">
                    <input type="hidden" id="{$id}-value" value="$value"/>
                    <!--<table border="0" cellpadding="0" cellspacing="1" id="{$id}-value" class="range-widget">-->
                    <table border="0" cellpadding="0" cellspacing="1" class="range-widget">
                        <tr class="rangesteps" bgcolor="silver">
                            <xsl:call-template name="drawRangeScripted">
                                <xsl:with-param name="rangeId" select="$id"/>
                                <xsl:with-param name="value" select="$value"/>
                                <xsl:with-param name="current" select="$start"/>
                                <xsl:with-param name="step" select="$step"/>
                                <xsl:with-param name="end" select="$end"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="select">
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat($id,'-value')"/>
                        </xsl:attribute>
                        <xsl:attribute name="size">1</xsl:attribute>
                        <xsl:attribute name="class">value</xsl:attribute>
                        <xsl:if test="chiba:data/@chiba:readonly='true'">
                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                        </xsl:if>
                        <xsl:apply-templates select="xforms:hint"/>
                        <xsl:call-template name="drawRangeBasic">
                            <xsl:with-param name="rangeId" select="$id"/>
                            <xsl:with-param name="value" select="$value"/>
                            <xsl:with-param name="current" select="$start"/>
                            <xsl:with-param name="step" select="$step"/>
                            <xsl:with-param name="end" select="$end"/>
                        </xsl:call-template>
                    </xsl:element>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <!-- *** graphical representation of range as slider component *** -->
    <xsl:template name="drawRangeScripted">
        <xsl:param name="rangeId"/>
        <xsl:param name="value"/>
        <xsl:param name="current"/>
        <xsl:param name="step"/>
        <xsl:param name="end"/>

        <xsl:if test="$current &lt;= $end">
            <xsl:variable name="classes">
                <xsl:choose>
                    <xsl:when test="$value = $current">step rangevalue</xsl:when>
                    <xsl:otherwise>step</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:element name="td">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat($rangeId,$current)"/>
                </xsl:attribute>
                <xsl:attribute name="class">
                    <xsl:value-of select="$classes"/>
                </xsl:attribute>
                <!-- todo: change to use 'this' instead of 'rangeId' -->
                <a href="javascript:setRange('{$rangeId}',{$current});">
                    <img alt="" src="../resources/images/trans.gif" height="25" width="6" title="{$current}"/>
                </a>
            </xsl:element>

        </xsl:if>

        <xsl:variable name="newStep" select="$current + $step"/>
        <xsl:if test="$newStep &lt;= $end">
            <xsl:call-template name="drawRangeScripted">
                <xsl:with-param name="rangeId" select="$rangeId"/>
                <xsl:with-param name="value" select="$value"/>
                <xsl:with-param name="current" select="$newStep"/>
                <xsl:with-param name="step" select="$step"/>
                <xsl:with-param name="end" select="$end"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- *** fallback template for representing range as a combobox in non-scripted mode *** -->
    <xsl:template name="drawRangeBasic">
        <xsl:param name="rangeId"/>
        <xsl:param name="value"/>
        <xsl:param name="current"/>
        <xsl:param name="step"/>
        <xsl:param name="end"/>

        <xsl:if test="$current &lt;= $end">
            <option id="{$rangeId}-value" value="{$current}" title="{xforms:hint}" class="selector-item">
                <xsl:if test="$value = $current">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="$current"/>
            </option>
        </xsl:if>

        <xsl:variable name="newStep" select="$current + $step"/>
        <xsl:if test="$newStep &lt;= $end">
            <xsl:call-template name="drawRangeBasic">
                <xsl:with-param name="rangeId" select="$rangeId"/>
                <xsl:with-param name="value" select="$value"/>
                <xsl:with-param name="current" select="$newStep"/>
                <xsl:with-param name="step" select="$step"/>
                <xsl:with-param name="end" select="$end"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- build secret control -->
    <xsl:template name="secret">
        <xsl:param name="maxlength"/>
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="incremental" select="@incremental"/>

        <xsl:element name="input">
            <xsl:if test="string-length($navindex) != 0">
                <xsl:attribute name="tabindex">
                    <xsl:value-of select="$navindex"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="id">
                <xsl:value-of select="concat($id,'-value')"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="concat($data-prefix,$id)"/>
            </xsl:attribute>
            <xsl:attribute name="class">value</xsl:attribute>
            <xsl:attribute name="type">password</xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="chiba:data/text()"/>
            </xsl:attribute>
            <xsl:if test="$maxlength">
                <xsl:attribute name="maxlength">
                    <xsl:value-of select="$maxlength"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="chiba:data/@chiba:readonly='true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:if test="$scripted='true'">
                <xsl:choose>
                    <xsl:when test="$incremental='true'">
                        <xsl:attribute name="onkeyup">setXFormsValue(this);</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="onkeyup">return keepAlive(this);</xsl:attribute>
                        <xsl:attribute name="onchange">setXFormsValue(this);</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:attribute name="onkeydown">DWRUtil.onReturn(event, submitFunction);</xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="xforms:hint"/>
        </xsl:element>
    </xsl:template>


    <xsl:template name="select1">
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="parent" select="."/>
        <xsl:variable name="incremental" select="@incremental"/>
        <xsl:variable name="handler">
            <xsl:choose>
                <xsl:when test="$incremental='false'">onblur</xsl:when>
                <xsl:otherwise>onchange</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="@appearance='compact'">
                <!-- create data structure for dropdown -->
                <xsl:element name="select">
                    <xsl:if test="string-length($navindex) != 0">
                        <xsl:attribute name="tabindex">
                            <xsl:value-of select="$navindex"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:attribute name="style">display: none;</xsl:attribute>
                    <xsl:attribute name="size">5</xsl:attribute>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">setXFormsValue(this);</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="xforms:hint"/>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </xsl:element>
                <!-- handle itemset prototype -->
                <xsl:if test="$scripted='true' and not(ancestor::xforms:repeat)">
                    <xsl:for-each select="xforms:itemset/chiba:data/xforms:item">
                        <xsl:call-template name="build-item-prototype">
                            <xsl:with-param name="item-id" select="@id"/>
                            <xsl:with-param name="itemset-id" select="../../@id"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
                <!-- create hidden parameter for deselection -->
                <input type="hidden" name="{$name}" value=""/>
                
                <!-- create the dropdown that is shown to the user: the CLONE -->
                <xsl:variable name="original_id" select="concat($id, '-value')"/>
                <xsl:variable name="clone_id" select="concat('clone-', $original_id)"/>               
                <xsl:element name="select">                	
                    <xsl:attribute name="id"><xsl:value-of select="$clone_id"/></xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                     <xsl:attribute name="size">5</xsl:attribute>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">updateSelectionOfOriginal('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');</xsl:attribute>
                    </xsl:if>
                </xsl:element>
                <script type="text/javascript">
                	initializeClone('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');
                </script>
            </xsl:when>
            <xsl:when test="@appearance='full'">
                <xsl:call-template name="build-radiobuttons">
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="parent" select="$parent"/>
                    <xsl:with-param name="navindex" select="$navindex"/>
                </xsl:call-template>
                <!-- handle itemset prototype -->
                <xsl:if test="$scripted='true' and not(ancestor::xforms:repeat)">
                    <xsl:for-each select="xforms:itemset/chiba:data/xforms:item">
                        <xsl:call-template name="build-radiobutton-prototype">
                            <xsl:with-param name="item-id" select="@id"/>
                            <xsl:with-param name="itemset-id" select="../../@id"/>
                            <xsl:with-param name="name" select="$name"/>
                            <xsl:with-param name="parent" select="$parent"/>
                            <xsl:with-param name="navindex" select="$navindex"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
                <!-- create hidden parameter for identification and deselection -->
                <input type="hidden" id="{$id}-value" name="{$name}" value=""/>
            </xsl:when>
            <xsl:otherwise>
                <!-- create data structure for selection list -->
                <xsl:element name="select">
                    <xsl:if test="string-length($navindex) != 0">
                        <xsl:attribute name="tabindex">
                            <xsl:value-of select="$navindex"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:attribute name="style">display: none;</xsl:attribute>
                    <xsl:attribute name="size">1</xsl:attribute>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">setXFormsValue(this);</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="xforms:hint"/>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </xsl:element>
                <!-- handle itemset prototype -->
                <xsl:if test="$scripted='true' and not(ancestor::xforms:repeat)">
                    <xsl:for-each select="xforms:itemset/chiba:data/xforms:item">
                        <xsl:call-template name="build-item-prototype">
                            <xsl:with-param name="item-id" select="@id"/>
                            <xsl:with-param name="itemset-id" select="../../@id"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
                <!-- create hidden parameter for deselection -->
                <input type="hidden" name="{$name}" value=""/>
                
                <!-- create the  selectionlist that is shown to the user: the CLONE -->
                <xsl:variable name="original_id" select="concat($id, '-value')"/>
                <xsl:variable name="clone_id" select="concat('clone-', $original_id)"/>               
                <xsl:element name="select">                	
                    <xsl:attribute name="id"><xsl:value-of select="$clone_id"/></xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:attribute name="size">1</xsl:attribute>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">updateSelectionOfOriginal('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');</xsl:attribute>
                    </xsl:if>
                </xsl:element>
                <script type="text/javascript">
                	initializeClone('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');
                </script>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="select">
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="parent" select="."/>
        <xsl:variable name="incremental" select="@incremental"/>
        <xsl:variable name="handler">
            <xsl:choose>
                <xsl:when test="$incremental='false'">onblur</xsl:when>
                <xsl:otherwise>onchange</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:choose>
            <!-- create data structure for list -->
            <xsl:when test="@appearance='compact'">
                <xsl:element name="select">
                    <xsl:if test="string-length($navindex) != 0">
                        <xsl:attribute name="tabindex">
                            <xsl:value-of select="$navindex"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:attribute name="style">display: none;</xsl:attribute>
                    <xsl:attribute name="multiple">multiple</xsl:attribute>
                    <xsl:attribute name="size">5</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">setXFormsValue(this);</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="xforms:hint"/>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </xsl:element>
                <!-- handle itemset prototype -->
                <xsl:if test="$scripted='true' and not(ancestor::xforms:repeat)">
                    <xsl:for-each select="xforms:itemset/chiba:data/xforms:item">
                        <xsl:call-template name="build-item-prototype">
                            <xsl:with-param name="item-id" select="@id"/>
                            <xsl:with-param name="itemset-id" select="../../@id"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
                <!-- create hidden parameter for deselection -->
                <input type="hidden" name="{$name}" value=""/>
                
                <!-- create the  selectionlist that is shown to the user: the CLONE -->
                <xsl:variable name="original_id" select="concat($id,'-value')"/>
                <xsl:variable name="clone_id" select="concat('clone-',$original_id)"/>               
                <xsl:element name="select">                	
                    <xsl:attribute name="id"><xsl:value-of select="$clone_id"/></xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:attribute name="size">5</xsl:attribute>
                    <xsl:attribute name="multiple">multiple</xsl:attribute>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">updateSelectionOfOriginal('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');</xsl:attribute>
                    </xsl:if>
                </xsl:element>
                <script type="text/javascript">
                	initializeClone('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');
                </script>
            </xsl:when>
            <xsl:when test="@appearance='full'">
                <xsl:call-template name="build-checkboxes">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="parent" select="$parent"/>
                    <xsl:with-param name="navindex" select="$navindex"/> 
                </xsl:call-template>
                <!-- handle itemset prototype -->
                <xsl:if test="$scripted='true' and not(ancestor::xforms:repeat)">
                    <xsl:for-each select="xforms:itemset/chiba:data/xforms:item">
                        <xsl:call-template name="build-checkbox-prototype">
                            <xsl:with-param name="item-id" select="@id"/>
                            <xsl:with-param name="itemset-id" select="../../@id"/>
                            <xsl:with-param name="name" select="$name"/>
                            <xsl:with-param name="parent" select="$parent"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
                <!-- create hidden parameter for identification and deselection -->
                <input type="hidden" id="{$id}-value" name="{$name}" value=""/>
            </xsl:when>
            <xsl:otherwise>
                <!-- create data structure for list -->
                <xsl:element name="select">
                    <xsl:if test="string-length($navindex) != 0">
                        <xsl:attribute name="tabindex">
                            <xsl:value-of select="$navindex"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat($id,'-value')"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:attribute name="style">display: none;</xsl:attribute>
                    <xsl:attribute name="multiple">multiple</xsl:attribute>
                    <xsl:attribute name="size">3</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">setXFormsValue(this);</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="xforms:hint"/>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </xsl:element>
                <!-- handle itemset prototype -->
                <xsl:if test="$scripted='true' and not(ancestor::xforms:repeat)">
                    <xsl:for-each select="xforms:itemset/chiba:data/xforms:item">
                        <xsl:call-template name="build-item-prototype">
                            <xsl:with-param name="item-id" select="@id"/>
                            <xsl:with-param name="itemset-id" select="../../@id"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
                <!-- create hidden parameter for deselection -->
                <input type="hidden" name="{$name}" value=""/>
                
                <!-- create the  selectionlist that is shown to the user: the CLONE -->
                <xsl:variable name="original_id" select="concat($id,'-value')"/>
                <xsl:variable name="clone_id" select="concat('clone-',$original_id)"/>
                <xsl:element name="select">
                    <xsl:attribute name="id"><xsl:value-of select="$clone_id"/></xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$name"/>
                    </xsl:attribute> 
                    <xsl:attribute name="size">3</xsl:attribute>
                    <xsl:attribute name="multiple">multiple</xsl:attribute>
                    <xsl:attribute name="class">value</xsl:attribute>
                    <xsl:if test="chiba:data/@chiba:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$scripted='true'">
                        <xsl:attribute name="{$handler}">updateSelectionOfOriginal('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');</xsl:attribute>
                    </xsl:if>
                </xsl:element>
                <script type="text/javascript">
                        initializeClone('<xsl:value-of select="$original_id"/>', '<xsl:value-of select="$clone_id"/>');
                </script>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- build textarea control -->
    <xsl:template name="textarea">
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="incremental" select="@incremental"/>

        <xsl:variable name="html-mediatype-class">
            <xsl:choose>
                <xsl:when test="@mediatype='text/html' and $scripted='true'"><xsl:text> </xsl:text>mediatype-text-html</xsl:when><xsl:otherwise/>
            </xsl:choose>
        </xsl:variable>


        <xsl:element name="textarea">
            <xsl:if test="string-length($navindex) != 0">
                <xsl:attribute name="tabindex">
                    <xsl:value-of select="$navindex"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:attribute name="id">
                <xsl:value-of select="concat($id,'-value')"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="concat($data-prefix,$id)"/>
            </xsl:attribute>
            <xsl:if test="chiba:data/@chiba:readonly='true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:attribute name="rows">5</xsl:attribute>
            <xsl:attribute name="cols">30</xsl:attribute>
            <xsl:attribute name="class">
                <xsl:value-of select="concat('value',$html-mediatype-class)"/>
            </xsl:attribute>
            <xsl:if test="$scripted='true'">
                <xsl:choose>
                    <xsl:when test="$incremental='true'">
                        <xsl:attribute name="onkeyup">setXFormsValue(this);</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="onkeyup">return keepAlive(this);</xsl:attribute>
                        <xsl:attribute name="onchange">setXFormsValue(this);</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:apply-templates select="xforms:hint"/>
            <xsl:value-of select="chiba:data/text()"/>
        </xsl:element>
    </xsl:template>

    <!-- build submit -->
    <!-- todo: align with trigger template -->
    <xsl:template name="submit">
        <xsl:param name="classes"/>
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>

        <span id="{$id}" class="{$classes}">
            <xsl:element name="input">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:attribute name="id">
                    <xsl:value-of select="concat($id,'-value')"/>
                </xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$scripted='true'">
                        <xsl:attribute name="type">button</xsl:attribute>
                        <xsl:attribute name="onclick">activate(this);</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="type">submit</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:attribute name="name">
                    <xsl:value-of select="concat($trigger-prefix,$id)"/>
                </xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="xforms:label"/>
                </xsl:attribute>
                <xsl:if test="chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <!--            <xsl:if test="chiba:data/@chiba:enabled='false'">-->
                <!--                <xsl:attribute name="disabled">true</xsl:attribute>-->
                <!--            </xsl:if>-->
                <xsl:attribute name="class">value</xsl:attribute>
                <xsl:apply-templates select="xforms:hint"/>
            </xsl:element>
        </span>
    </xsl:template>

    <!-- build trigger -->
    <xsl:template name="trigger">
        <xsl:param name="classes"/>
        <xsl:variable name="navindex" select="@navindex" /> 
        <xsl:variable name="id" select="@id"/>

        <xsl:choose>
            <!-- minimal appearance only supported in scripted mode -->
            <xsl:when test="@appearance='minimal' and $scripted='true'">
                <span id="{$id}" class="{$classes}">
                    <xsl:element name="a">
                        <xsl:if test="string-length($navindex) != 0">
                            <xsl:attribute name="tabindex">
                                <xsl:value-of select="$navindex"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="class">value</xsl:attribute>
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat($id,'-value')"/>
                        </xsl:attribute>
                        <xsl:attribute name="href">#</xsl:attribute>
                        <xsl:if test="not(chiba:data/@chiba:readonly='true')">
                            <xsl:attribute name="onclick">return activate(this);</xsl:attribute>
                        </xsl:if>
                        <xsl:apply-templates select="xforms:hint"/>
                        <xsl:apply-templates select="xforms:label"/>
                    </xsl:element>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <span id="{$id}" class="{$classes}">
                    <xsl:element name="input">
                        <xsl:if test="string-length($navindex) != 0">
                            <xsl:attribute name="tabindex">
                                <xsl:value-of select="$navindex"/>
                            </xsl:attribute>
                        </xsl:if>                        
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat($id,'-value')"/>
                        </xsl:attribute>
                        <xsl:attribute name="name">
                            <xsl:value-of select="concat($trigger-prefix,$id)"/>
                        </xsl:attribute>
                        <xsl:attribute name="type">
                            <xsl:choose>
                                <xsl:when test="$scripted='true'">
                                    <xsl:value-of select="'button'"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="'submit'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="xforms:label"/>
                        </xsl:attribute>
                        <xsl:attribute name="class">value</xsl:attribute>
                        <xsl:if test="chiba:data/@chiba:readonly='true'">
                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                        </xsl:if>
                        <xsl:if test="@accesskey">
                            <xsl:attribute name="accesskey">
                                <xsl:value-of select="@accesskey"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="normalize-space(xforms:hint)"/>- KEY: [ALT]+ <xsl:value-of select="@accesskey"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:if test="$scripted='true'">
                            <xsl:attribute name="onclick">activate(this);</xsl:attribute>

                        </xsl:if>
                        <xsl:apply-templates select="xforms:hint"/>
                        <!--
                                                <xsl:if test="contains(@xforms:src,'.gif') or contains(@xforms:src,'.jpg') or contains(@xforms:src,'.png')">
                                                    <img alt="{xforms:label}" src="{@xforms:src}" id="{@id}-label"/>
                                                </xsl:if>
                        -->
                    </xsl:element>
<!--
                    <xsl:if test="$repeat-id and $scripted = 'true'">
                        <script type="text/javascript">
                            dojo.event.connect("before",dojo.byId("<xsl:value-of select="concat($id,'-value')"/>"),"onclick","setRepeatIndex");
                        </script>
                    </xsl:if>
-->

                </span>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <!-- build upload control -->
    <xsl:template name="upload">
        <!-- the stylesheet using this template has to take care, that form enctype is set to 'multipart/form-data' -->
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="navindex" select="@navindex" />
        <xsl:element name="input">
            <xsl:if test="string-length($navindex) != 0">
                <xsl:attribute name="tabindex">
                    <xsl:value-of select="$navindex"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="id">
                <xsl:value-of select="concat($id,'-value')"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="concat($data-prefix,$id)"/>
            </xsl:attribute>
            <xsl:attribute name="type">file</xsl:attribute>
            <xsl:attribute name="value"/>
            <xsl:if test="chiba:data/@chiba:readonly='true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">value</xsl:attribute>
			<!-- Content types accepted, from mediatype xforms:upload attribute
            to accept input attribute -->
            <!--
                        <xsl:attribute name="accept">
                            <xsl:value-of select="translate(normalize-space(@xforms:mediatype),' ',',')"/>
                        </xsl:attribute>
            -->
            <xsl:if test="$scripted='true'">
                <!--<xsl:attribute name="onchange">submitFile(this);</xsl:attribute>-->
                <xsl:attribute name="dojoType">chiba:Upload</xsl:attribute>
                <xsl:attribute name="xfreadonly">
                    <xsl:value-of select="chiba:data/@chiba:readonly"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="xforms:hint"/>
        </xsl:element>

        <xsl:if test="$scripted='true'">
            <script type="text/javascript">
                dojo.require("chiba.widget.Upload");
            </script>

            <iframe id="UploadTarget" name="UploadTarget" src="" style="width:0px;height:0px;border:0"/>
            <div class="progressbar" style="display:none;" id="{$id}-progress">
                <div class="border">
                    <div id="{$id}-progress-bg" class="background"/>
                </div>
            </div>
        </xsl:if>
        <xsl:if test="xforms:filename">
            <input type="hidden" id="{xforms:filename/@id}" value="{xforms:filename/chiba:data}"/>
        </xsl:if>
        <xsl:if test="@chiba:destination">
            <!-- create hidden parameter for destination -->
            <input type="hidden" id="{$id}-destination" value="{@chiba:destination}"/>
        </xsl:if>
    </xsl:template>


    <!-- ######################################################################################################## -->
    <!-- ########################################## HELPER TEMPLATES FOR SELECT, SELECT1 ######################## -->
    <!-- ######################################################################################################## -->

    <xsl:template name="build-items">
        <xsl:param name="parent"/>

		<!-- add an empty item, because otherwise deselection is not possible -->
		<option value="">
			<xsl:if test="string-length($parent/chiba:data/text()) = 0">
				<xsl:attribute name="selected">selected</xsl:attribute>
			</xsl:if>
		</option>
		<xsl:for-each select="$parent/xforms:itemset|$parent/xforms:item|$parent/xforms:choices">
			<xsl:call-template name="build-items-list"/>
		</xsl:for-each>
    </xsl:template>
    
    <xsl:template name="build-items-list">
    	<xsl:choose>
    		<xsl:when test="local-name(.) = 'choices'">
    			<xsl:call-template name="build-items-choices"/>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'itemset'">
    			<xsl:call-template name="build-items-itemset"/>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'item'">
    			<xsl:call-template name="build-items-item"/>
    		</xsl:when>
    	</xsl:choose>
    </xsl:template>
    
	<xsl:template name="build-items-choices">
		<xsl:for-each select="xforms:itemset|xforms:item|xforms:choices">
			<xsl:call-template name="build-items-list"/>
		</xsl:for-each>
	</xsl:template>

    <xsl:template name="build-items-itemset">
		<optgroup id="{@id}">
			<xsl:for-each select="xforms:item">
				<xsl:call-template name="build-items-item"/>
			</xsl:for-each>
		</optgroup>
	</xsl:template>
	
	<xsl:template name="build-items-item">
		<option id="{@id}-value" value="{xforms:value}" title="{xforms:hint}" class="selector-item">
			<xsl:if test="@selected='true'">
				<xsl:attribute name="selected">selected</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="xforms:label" />
		</option>
	</xsl:template>
	
    <xsl:template name="build-item-prototype">
        <xsl:param name="item-id"/>
        <xsl:param name="itemset-id"/>

        <select id="{$itemset-id}-prototype" class="selector-prototype">
            <option id="{$item-id}-value" class="selector-prototype">
	           	<xsl:choose>
    	       		<xsl:when test="xforms:copy">
	    	   			<xsl:attribute name="value" select="xforms:copy/@id"/>
	              		<xsl:attribute name="title" select="xforms:copy/@id"/>
    	          	</xsl:when>
        	      	<xsl:otherwise>
            	   		<xsl:attribute name="value" select="xforms:value"/>
              			<xsl:attribute name="title" select="xforms:hint"/>
                	</xsl:otherwise>
				</xsl:choose>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="xforms:label"/>
            </option>
        </select>
    </xsl:template>

    <xsl:template name="build-checkboxes">
        <xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="navindex"/> 
        <!-- handle items, choices and itemsets -->
        <xsl:for-each select="$parent/xforms:item|$parent/xforms:choices|$parent/xforms:itemset">
        	<xsl:call-template name="build-checkboxes-list">
        		<xsl:with-param name="name" select="$name"/>
        		<xsl:with-param name="parent" select="$parent"/>
        	</xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="build-checkboxes-list">
    	<xsl:param name="name"/>
        <xsl:param name="parent"/>
    	<xsl:choose>
    		<xsl:when test="local-name(.) = 'choices'">
    			<xsl:call-template name="build-checkboxes-choices">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'itemset'">
    			<xsl:call-template name="build-checkboxes-itemset">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'item'">
    			<xsl:call-template name="build-checkboxes-item">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    	</xsl:choose>
    </xsl:template>

	<xsl:template name="build-checkboxes-choices">
		<xsl:param name="name"/>
        <xsl:param name="parent"/>
		<xsl:for-each select="xforms:itemset|xforms:item|xforms:choices">
			<xsl:call-template name="build-checkboxes-list">
				<xsl:with-param name="name" select="$name"/>
           		<xsl:with-param name="parent" select="$parent"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

    <xsl:template name="build-checkboxes-itemset">
    	<xsl:param name="name"/>
        <xsl:param name="parent"/>
		<span id="{@id}">
			<xsl:for-each select="xforms:item">
				<xsl:call-template name="build-checkboxes-item">
	           		<xsl:with-param name="name" select="$name"/>
	           		<xsl:with-param name="parent" select="$parent"/>
				</xsl:call-template>
			</xsl:for-each>
		</span>
	</xsl:template>
	
	<xsl:template name="build-checkboxes-item">
    	<xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="navindex"/>         
        <span id="{@id}" class="selector-item">
            <input id="{@id}-value" class="value" type="checkbox" name="{$name}">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:choose>
        			<xsl:when test="xforms:copy">
           				<xsl:attribute name="value" select="xforms:copy/@id"/>
	            	</xsl:when>
    	        	<xsl:otherwise>
	    	    		<xsl:attribute name="value" select="xforms:value"/>
    	    		</xsl:otherwise>
        	    </xsl:choose>
                <xsl:choose>
                    <xsl:when test="xforms:hint">
                        <xsl:apply-templates select="xforms:hint"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="$parent/xforms:hint"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:if test="$scripted='true'">
                    <xsl:attribute name="onclick">setXFormsValue(this);</xsl:attribute>
                    <xsl:attribute name="onkeydown">DWRUtil.onReturn(event, submitFunction);</xsl:attribute>
                </xsl:if>
            </input>
            <span id="{@id}-label" class="label">
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xforms:label"/>
            </span>
        </span>
	</xsl:template>
	
    <xsl:template name="build-checkbox-prototype">
        <xsl:param name="item-id"/>
        <xsl:param name="itemset-id"/>
        <xsl:param name="name"/>
        <xsl:param name="parent"/>

        <span id="{$itemset-id}-prototype" class="selector-prototype">
            <input id="{$item-id}-value" class="value" type="checkbox" name="{$name}">
                <xsl:choose>
	       			<xsl:when test="xforms:copy">
		   				<xsl:attribute name="value"><xsl:value-of select="xforms:copy/@id"/></xsl:attribute>
	              	</xsl:when>
    	        	<xsl:otherwise>
      	 	    		<xsl:attribute name="value"><xsl:value-of select="xforms:value"/></xsl:attribute>
            		</xsl:otherwise>
           	    </xsl:choose>
                <xsl:attribute name="title">
                    <xsl:choose>
                        <xsl:when test="xforms:hint">
                            <xsl:value-of select="xforms:hint"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$parent/xforms:hint"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:if test="$scripted='true'">
                    <xsl:attribute name="onclick">setXFormsValue(this);</xsl:attribute>
                    <xsl:attribute name="onkeydown">DWRUtil.onReturn(event, submitFunction);</xsl:attribute>
                </xsl:if>
            </input>
            <span id="{@item-id}-label" class="label">
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xforms:label"/>
            </span>
        </span>
    </xsl:template>

    <!-- overwrite/change this template, if you don't like the way labels are rendered for checkboxes -->
    <xsl:template name="build-radiobuttons">
        <xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="id"/>
        <xsl:param name="navindex"/> 
        <!-- handle items, choices and itemsets -->
        <xsl:for-each select="$parent/xforms:item|$parent/xforms:choices|$parent/xforms:itemset">
        	<xsl:call-template name="build-radiobuttons-list">
        		<xsl:with-param name="name" select="$name"/>
        		<xsl:with-param name="parent" select="$parent"/>
        	</xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="build-radiobuttons-list">
    	<xsl:param name="name"/>
    	<xsl:param name="parent"/>
        
        <xsl:choose>
    		<xsl:when test="local-name(.) = 'choices'">
    			<xsl:call-template name="build-radiobuttons-choices">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'itemset'">
    			<xsl:call-template name="build-radiobuttons-itemset">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'item'">
    			<xsl:call-template name="build-radiobuttons-item">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    	</xsl:choose>
    </xsl:template>

	<xsl:template name="build-radiobuttons-choices">
		<xsl:param name="name"/>
		<xsl:param name="parent"/>
		<xsl:for-each select="xforms:itemset|xforms:item|xforms:choices">
			<xsl:call-template name="build-radiobuttons-list">
				<xsl:with-param name="name" select="$name"/>
           		<xsl:with-param name="parent" select="$parent"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

    <xsl:template name="build-radiobuttons-itemset">
    	<xsl:param name="name"/>
    	<xsl:param name="parent"/>
		<span id="{@id}">
			<xsl:for-each select="xforms:item">
				<xsl:call-template name="build-radiobuttons-item">
	           		<xsl:with-param name="name" select="$name"/>
	           		<xsl:with-param name="parent" select="$parent"/>
				</xsl:call-template>
			</xsl:for-each>
		</span>
	</xsl:template>
	
	<xsl:template name="build-radiobuttons-item">
    	<xsl:param name="name"/>
    	<xsl:param name="parent"/>
        <xsl:param name="navindex"/>         
        <span id="{@id}" class="selector-item">
            <input id="{@id}-value" class="value" type="radio" name="{$name}">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:choose>
        			<xsl:when test="xforms:copy">
           				<xsl:attribute name="value" select="xforms:copy/@id"/>
	            	</xsl:when>
    	        	<xsl:otherwise>
	    	    		<xsl:attribute name="value" select="xforms:value"/>
    	    		</xsl:otherwise>
        	    </xsl:choose>
                <xsl:choose>
                    <xsl:when test="xforms:hint">
                        <xsl:apply-templates select="xforms:hint"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="$parent/xforms:hint"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:if test="$scripted='true'">
                    <xsl:attribute name="onclick">setXFormsValue(this);</xsl:attribute>
                    <xsl:attribute name="onkeydown">DWRUtil.onReturn(event, submitFunction);</xsl:attribute>
                </xsl:if>
            </input>
            <span id="{@id}-label" class="label">
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xforms:label"/>
            </span>
        </span>
	</xsl:template>
	
    <xsl:template name="build-radiobutton-prototype">
        <xsl:param name="item-id"/>
        <xsl:param name="itemset-id"/>
        <xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="navindex"/>
        <span id="{$itemset-id}-prototype" class="selector-prototype">
            <input id="{$item-id}-value" class="value" type="radio" name="{$name}">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                
                <xsl:choose>
					<xsl:when test="xforms:copy">
   						<xsl:attribute name="value" select="xforms:copy/@id"/>
	            	</xsl:when>
    	        	<xsl:otherwise>
	    	    		<xsl:attribute name="value" select="xforms:value"/>
    	    		</xsl:otherwise>
        	    </xsl:choose>
                <xsl:choose>
                    <xsl:when test="xforms:hint">
                        <xsl:apply-templates select="xforms:hint"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="$parent/xforms:hint"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:if test="$scripted='true'">
                    <xsl:attribute name="onclick">setXFormsValue(this);</xsl:attribute>
                    <xsl:attribute name="onkeydown">DWRUtil.onReturn(event, submitFunction);</xsl:attribute>
                </xsl:if>
            </input>
            <span id="{$item-id}-label" class="label">
                <xsl:if test="$parent/chiba:data/@chiba:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xforms:label"/>
            </span>
        </span>
    </xsl:template>


</xsl:stylesheet>
