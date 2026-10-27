<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pom="http://maven.apache.org/POM/4.0.0"
    exclude-result-prefixes="pom">

    <xsl:output method="xml"
                indent="yes"
                encoding="UTF-8"/>

    <!-- Read Maven POM -->
    <xsl:param name="groupId"/>
    <xsl:param name="version"/>
    
    <!-- Keep your existing feature conversion templates -->

	<xsl:output method="xml" indent="yes" encoding="UTF-8" />

	<xsl:template match="/feature">

		<features
			xmlns="http://karaf.apache.org/xmlns/features/v1.4.0"
			name="{@id}.karaf">

			<feature name="{@id}" version="{$version}">

				<xsl:for-each select="plugin">
					<bundle>
						<xsl:if test="@fragment='true'">
							<xsl:attribute name="start">
                                <xsl:text>false</xsl:text>
                            </xsl:attribute>
						</xsl:if>

						<xsl:text>mvn:</xsl:text>
						<xsl:value-of select="$groupId" />
						<xsl:text>/</xsl:text>
						<xsl:value-of select="@id" />
						<xsl:text>/</xsl:text>
						<xsl:value-of select="$version" />
					</bundle>
				</xsl:for-each>

			</feature>
		</features>

	</xsl:template>
</xsl:stylesheet>