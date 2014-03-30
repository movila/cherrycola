<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
	<html>
	<head>
		<title>Food Report</title>
	</head>
	<body>
		<xsl:apply-templates/>
	</body>
	</html>
</xsl:template>

<xsl:template match="order">
	<h1>Food Report</h1>
	<h2>ID: <xsl:value-of select="@id"/></h2>
	<h2>Submitted Time: <xsl:value-of select="@submitted"/></h2>
	<xsl:apply-templates select="customer"/>
	<xsl:apply-templates select="items"/>
	<h3>Sub Total: $<xsl:value-of select="total"/></h3>
	<h3>HST:       $<xsl:value-of select="HST"/></h3>
	<h3>Shipping:  $<xsl:value-of select="shipping"/></h3>
	<h3>Total:     $<xsl:value-of select="grandTotal"/></h3>
</xsl:template>

<xsl:template match="customer">
	<h3>Customer ID: <xsl:value-of select="@account"/></h3>
	<h3>Customer Name: <xsl:value-of select="./name"/></h3>
</xsl:template>

<xsl:template match="items">
	<h3>Items</h3>
	<ol>
		<xsl:apply-templates/>
	</ol>
</xsl:template>

<xsl:template match="item">
	<li>
		<ul>
			<li>Name:     <xsl:value-of select="./name"/></li>
			<li>Price:    $<xsl:value-of select="./price"/></li>
			<li>Quantity: <xsl:value-of select="./quantity"/></li>
			<li>Extended: $<xsl:value-of select="./extended"/></li>
		</ul>
	</li>
</xsl:template>

</xsl:stylesheet>
