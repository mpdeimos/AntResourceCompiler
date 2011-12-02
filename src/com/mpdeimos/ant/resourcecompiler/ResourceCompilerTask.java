package com.mpdeimos.ant.resourcecompiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * The resource compiler main class.
 * 
 * @author mpdeimos
 * 
 */
public class ResourceCompilerTask extends Task {
	private String path;
	private static String DELIMITER = "//@@CUT@@//";

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void execute() throws BuildException {
		try {
			File out = new File(path + "/resources/R.java");
			
			File strings = new File(path + "/string/string.properties");
			File[] drawables = new File(path + "/drawable").listFiles(new FilenameFilter() {
				public boolean accept(File path, String name) {
					return name.endsWith(".png");
				}
			});
			
			// check if files were modified since last run
			if (fileExists(out))
			{
				long lastCompile = out.lastModified();
				
				BufferedReader br = new BufferedReader(new FileReader(out));
				
				Collection<String> drawbleNames = new HashSet<String>();
				boolean inDrawableEnum = false;
				while(true)
				{
					String line = br.readLine();
					
					if (line == null)
						break;
					
					if (line.startsWith("\tpublic static enum drawable"))
					{
						inDrawableEnum = true;
						continue;
					}
					
					if (!inDrawableEnum)
						continue;
					
					if (line.startsWith("\t}"))
					{
						break;
					}
					
					if (line.startsWith("\t\t/** "))
					{
						drawbleNames.add(line.substring(6, line.length()-3));
					}
				}
				
				int fileCount = 0;
				if (fileExists(strings) && strings.lastModified() > lastCompile)
				{
					fileCount++;
				}
				for (File drawable : drawables)
				{
					if (fileExists(drawable) && drawable.lastModified() > lastCompile)
					{
						fileCount++;
					}
					if (drawbleNames.contains(drawable.getName()))
					{
						drawbleNames.remove(drawable.getName());
					}
					else
					{
						fileCount++;
					}
				}
				
				fileCount += drawbleNames.size();
				
				if (fileCount == 0)
				{
					this.log("Nothing to do. Bye.");
					return;
				}
				
				this.log(String.format("%d files modified.", fileCount));
			}
			
			InputStream rProtoIS = this.getClass().getResourceAsStream("R.java.prototype");
			StringPair rProto = splitFile(rProtoIS);
			
			InputStream rStringIS = this.getClass().getResourceAsStream("string.prototype");
			StringPair rString = splitFile(rStringIS);
			
			InputStream rDrawableIS = this.getClass().getResourceAsStream("drawable.prototype");
			StringPair rDrawable = splitFile(rDrawableIS);
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(strings));

			Set<Object> keySet = properties.keySet();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			
			bw.write(rProto.pre);
			
			// writing string stuff
			bw.write(rString.pre);
			
			for (Object o : keySet)
			{
				bw.write(String.format("\t\t/** Value: %s */\n", properties.get(o.toString())));
				bw.write("\t\t"+o.toString().toUpperCase()+",\n");
			}
			
			bw.write("\t\t;\n");
			
			bw.write(rString.post);
			
			// writing drawable stuff
			bw.write(rDrawable.pre);
			
			for (File d: drawables)
			{
				String s = d.getName();
				bw.write(String.format("\t\t/** %s */\n", s));
				bw.write("\t\t"+s.substring(0, s.length()-4).toUpperCase()+",\n");
			}
			
			bw.write("\t\t;\n");
			
			bw.write(rDrawable.post);
			
			bw.write(rProto.post);
			
			bw.flush();
			bw.close();

		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	private boolean fileExists(File out)
	{
		return out != null && out.exists();
	}
	
	private static StringPair splitFile(InputStream is)
	{
		StringPair r = new StringPair();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = br.readLine()) != null)
			{
				if (DELIMITER.equals(s))
				{
					r.pre = sb.toString();
					sb = new StringBuilder();
					continue;
				}
				sb.append(s+"\n");
			}
			r.post = sb.toString();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		return r;
	}
	
	private static class StringPair
	{
		String pre;
		String post;
	}
}
