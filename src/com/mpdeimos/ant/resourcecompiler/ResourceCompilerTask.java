package com.mpdeimos.ant.resourcecompiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
			InputStream rProtoIS = this.getClass().getResourceAsStream("R.java.prototype");
			StringPair rProto = splitFile(rProtoIS);
			
			InputStream rStringIS = this.getClass().getResourceAsStream("string.prototype");
			StringPair rString = splitFile(rStringIS);
			
			InputStream rDrawableIS = this.getClass().getResourceAsStream("drawable.prototype");
			StringPair rDrawable = splitFile(rDrawableIS);
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(path
					+ "/string/string.properties"));

			Set<Object> keySet = properties.keySet();
			
			String[] list = new File(path + "/drawable").list();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					path + "/resources/R.java"));
			
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
			
			for (String s : list)
			{
				if (s.endsWith(".png"))
				{
					bw.write(String.format("\t\t/** %s */\n", s));
					bw.write("\t\t"+s.substring(0, s.length()-4).toUpperCase()+",\n");
				}
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
