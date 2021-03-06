/**
 * Copyright (c) 2011 Json Shen, http://www.shenjia.org/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.shenjia.maven.markdown;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.mappers.MapperException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.parboiled.common.FileUtils;
import org.pegdown.PegDownProcessor;

/**
 * Markdown maven plugin mojo
 * 
 * @author json.shen@gmail.com (Json Shen)
 * @goal markdown
 * @phase process-sources
 */
public class MarkdownMojo extends AbstractMojo {

    /**
     * markdown文件根目录
     * 
     * @parameter expression="${markdown.sourceDirectory}"
     * @required
     */
    private FileSet fileSet;

    /**
     * 输出文件根目录
     * 
     * @parameter expression="${markdown.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * 目标文件头
     * 
     * @parameter expression="${markdown.htmlHeader}" default-value="<!DOCTYPE html><head><title>{title}</title></head><body>\n"
     */
    private String htmlHeader;

    /**
     * 目标文件尾
     * 
     * @parameter expression="${markdown.htmlFooter}" default-value="</body></html>"
     */
    private String htmlFooter;

    public void execute() throws MojoExecutionException, MojoFailureException {
        PegDownProcessor processor = new PegDownProcessor();
        FileSetManager fileSetManager = new FileSetManager(getLog());
        try {
            Map<String, String> sourceDestinationMap = fileSetManager.mapIncludedFiles(fileSet);
            for (Map.Entry<String, String> sourceDestination : sourceDestinationMap.entrySet()) {
                process(sourceDestination.getKey(), sourceDestination.getValue(), processor);
            }
        } catch (MapperException e) {
            throw new MojoExecutionException("Failed to map source to destination", e);
        }
    }

    protected void process(String inputFilename, String outputFilename, PegDownProcessor processor) throws MojoExecutionException {
        File inputFile = new File(fileSet.getDirectory(), inputFilename);
        File outputFile = new File(outputDirectory, outputFilename);

        // Create parent directories for outputFile
        File parentOutputDirectory = outputFile.getParentFile();
        if (!parentOutputDirectory.isDirectory() && !parentOutputDirectory.mkdirs()) {
            throw new MojoExecutionException("Failed to create directory " + parentOutputDirectory.getAbsolutePath());
        }

        // Convert file
        String markdown = FileUtils.readAllText(inputFile);
        String htmlFragment = processor.markdownToHtml(markdown);
        FileUtils.writeAllText(htmlHeader + htmlFragment + htmlFooter, outputFile);
    }

}
