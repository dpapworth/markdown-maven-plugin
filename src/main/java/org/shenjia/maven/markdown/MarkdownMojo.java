package org.shenjia.maven.markdown;

/*
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * Markdown maven plugin mojo
 * @author json.shen@gmail.com (Json Shen)
 * @goal markdown
 * @phase process-sources
 */
public class MarkdownMojo extends AbstractMojo {
	
	/**
	 * markdown文件根目录
     * @parameter expression="${markdown.directory}"
     * @required
     */
    private File markdownDirectory;
    
	/**
	 * 输出文件根目录
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;
	
	/**
	 * 目标文件头
	 * @parameter expression="${html.header}" default-value="<!DOCTYPE html><head><title>{title}</title></head><body>\n"
	 */
	private String header;
	
	/**
	 * 目标文件尾
	 * @parameter expression="${html.footer}" default-value="</body></html>"
	 */
	private String footer ;
	
	/**
	 * 是否复制markdown文件到输出目录
     * @parameter expression="${copy.markdown}" default-value="true"
     */
    private Boolean copyMarkdown;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		if(!outputDirectory.exists()){
			outputDirectory.mkdirs();
		}
		//递归处理markdown
		processMarkdown(markdownDirectory, new MarkdownProcessor());
	}
	
	private void processMarkdown(File file, MarkdownProcessor processor){
		//markdown文件根目录路径长度
		int srcDirPathLen = markdownDirectory.getPath().length();
		File[] files = file.listFiles(createFileFilter());
		for(File f : files){
			if(f.isDirectory()){
				processMarkdown(f,processor);
			}else{
				BufferedReader in = null;
				BufferedWriter out = null;
				try {
					//输出目录
					File outputDir = outputDirectory;
					String subPath = f.getParentFile().getPath().substring(srcDirPathLen);
					if(subPath.length()>0){
						outputDir = new File(outputDirectory, subPath);
						if(!outputDir.exists()){
							outputDir.mkdirs();
						}
					}
					//输出文件名
					String outputName = f.getName().replaceAll("\\.md", ".html");
					
					//输出流，用于写文件
					out = new BufferedWriter(new FileWriter(new File(outputDir, outputName)));
					//输出流，用于读文件
					in = new BufferedReader(new FileReader(f));
					
					String one = in.readLine();
					//写入目标文件头
					out.write(header.replaceFirst("\\{title}", one));
					
					String two = in.readLine();
					while(one != null){
						if(two == null){
							out.write(processor.markdown(one));
							break;
						}
						if(two.startsWith("====") || two.startsWith("----")){
							out.write(processor.markdown(one+"\n"+two));
						}else{
							out.write(processor.markdown(one));
							out.write(processor.markdown(two));
						}
						one = in.readLine();
						two = in.readLine();
					}
					//写入目标文件尾
					out.write(footer);
				} catch (FileNotFoundException e) {
					getLog().error(e.getMessage(),e);
				} catch (IOException e) {
					getLog().error(e.getMessage(),e);
				} finally {
					try {
						if(in != null)in.close();
						if(out != null)out.close();
					} catch (IOException e) {
						getLog().error(e.getMessage(),e);
					}
				}
			}
		}
	}
	
	/**
	 * 创建一个文件过滤器
	 * @return
	 */
	private FileFilter createFileFilter(){
		return new FileFilter() {
			public boolean accept(File file) {
				if(file.isDirectory()){
					return true;
				}
				if(file.getName().endsWith(".md")){
					return true;
				}
				return false;
			}
		};
	}
	
}
