markdown-maven-plugin
=====================

Plugin for converting Markdown files into HTML fragments.

Usage
-----

    <plugin>
        <groupId>org.shenjia.maven</groupId>
        <artifactId>markdown-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <executions>
            <execution>
                <phase>generate-sources</phase>
                <goals>
                    <goal>markdown</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <htmlHeader><![CDATA[<!DOCTYPE html><head><title>{title}</title></head><body>]]></htmlHeader>
            <htmlFooter><![CDATA[</body></html>]]></htmlFooter>
            <sourceDirectory>src/main/resources/documents</sourceDirectory>
            <outputDirectory>${project.build.directory}/html</outputDirectory>
        </configuration>
    </plugin>

