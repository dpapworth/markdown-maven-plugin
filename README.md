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
            <copyMarkdown>false</copyMarkdown>
            <markdownDirectory>src/main/resources/documents</markdownDirectory>
            <outputDirectory>${project.build.directory}/html</outputDirectory>
        </configuration>
    </plugin>

