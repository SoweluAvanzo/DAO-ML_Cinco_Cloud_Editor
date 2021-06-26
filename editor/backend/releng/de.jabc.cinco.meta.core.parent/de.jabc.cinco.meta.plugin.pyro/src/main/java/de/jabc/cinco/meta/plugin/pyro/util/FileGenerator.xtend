package de.jabc.cinco.meta.plugin.pyro.util

import java.io.File
import org.apache.commons.io.FileUtils

class FileGenerator {
	
	protected String basePath
	
	new(String base){
		basePath = base
	}
	
	/**
	 * Helper method to create a file with the given content on the given path and filename.
	 * @param path
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	def generateFile(String path,CharSequence fileName,CharSequence content){
		val File f = new File(basePath+"/"+path.toLowerCase+"/"+fileName.toString.toLowerCase);
		f.getParentFile().mkdirs(); 
		f.createNewFile();
		
		FileUtils.writeStringToFile(f,content.toString);
	}
	
	/**
	 * Helper method to create a file with the given content on the given path and filename.
	 * @param path
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	def generateJavaFile(String path,CharSequence fileName,CharSequence content){
		val File f = new File(basePath+"/"+path.toLowerCase+"/"+fileName.toString.toFirstUpper);
		f.getParentFile().mkdirs(); 
		f.createNewFile();
		
		FileUtils.writeStringToFile(f,content.toString);
	}
	
	/**
	 * Helper method to create a file with the given content on the given path and filename.
	 * @param path
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	def clearDirectory(String path){
		val File f = new File(basePath+"/"+path.toLowerCase);
		if(f.directory){
			for(File fi: f.listFiles) {
	            if(!fi.isDirectory()) {
	                fi.delete();
	            }
            }
		}
		
	}
	
	
	
}
