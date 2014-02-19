<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">
    <xsl:output method="text" indent="no"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="module">
        <xsl:text>
eXgit
=====

</xsl:text>
        <xsl:value-of select="description"/>
        <xsl:text>

functions
---------
</xsl:text>
        <xsl:apply-templates select="function"/>
    </xsl:template>
    
    <xsl:template match="function">
        <xsl:text>
```xquery
</xsl:text>
        <xsl:value-of select="@name"/>
        
        <xsl:text>(</xsl:text>
        
        <xsl:for-each select="argument">
            <xsl:text>$</xsl:text>
            <xsl:value-of select="@var"/>
            <xsl:text> as </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:apply-templates select="@cardinality"/>
            <xsl:if test="position() != count(../argument)">
                <xsl:text>, </xsl:text>
            </xsl:if>
        </xsl:for-each>
        
        <xsl:text>) as </xsl:text>
        <xsl:value-of select="returns/@type"/>
        <xsl:apply-templates select="returns/@cardinality"/>
        <xsl:text>
```

</xsl:text>
        
        <xsl:for-each select="argument">
            <xsl:text>$</xsl:text>
            <xsl:value-of select="@var"/>
            <xsl:text> as </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:apply-templates select="@cardinality"/>
            <xsl:text> - </xsl:text>
            <xsl:value-of select="text()"/>
            <xsl:text>
</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="@cardinality">
        <xsl:choose>
            <xsl:when test=". = 'exactly one'"></xsl:when>
            <xsl:when test=". = 'one or more'"><xsl:text>+</xsl:text></xsl:when>
            <xsl:when test=". = 'zero or more'"><xsl:text>*</xsl:text></xsl:when>
            <xsl:when test=". = 'zero or one'"><xsl:text>?</xsl:text></xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
