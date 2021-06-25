package de.jabc.cinco.meta.core.utils

import java.util.Properties
import java.nio.file.Paths
import java.io.FileInputStream
import java.util.List

class CincoProperties extends Properties {
	
	// name of the Cinco config folder to be expected in the home of the user
	public static val CONFIG_FOLDER = ".cinco";
	
	// name of the properties file to be expected in the root of a project
	public static val PROPERTIES_FILE_NAME = "cinco.properties";
	
	// Pairs: Property key -> Default value
	static val MAX_THREADS = "maxThreads" -> 0; // default: no limit
	static val DELETE_FOLDERS = "deleteFolders" -> ""; // default: none
	static val VM_ARGS = "vmArgs" -> ""; // default: no args
	
	// Convinient System information
	static val OS_NAME = System.getProperty("os.name")
	
	// singleton pattern
	private new() {}
	static CincoProperties INSTANCE
	static def newInstance() { INSTANCE = new CincoProperties => [load] }
	static def getInstance() { INSTANCE ?: newInstance }
	
	/*
	 * Convenient Getters
	 */
	static def getMaxThreads() {
		MAX_THREADS.intValue
	}
	
	static def getDeleteFolders() {
		DELETE_FOLDERS.strValues
	}
	
	static def getVmArgs() {
		VM_ARGS.strValue
	}
	
	static def getOSName(){
		OS_NAME
	}
	
	static def isWindows(){
		OSName.toLowerCase.contains("windows")
	}
	
	/**
	 * Load Cinco properties from user home
	 */
	def load() {
		val home = System.getProperty("user.home")
		if (home !== null) try {
			val file = Paths.get(home).resolve(CONFIG_FOLDER).resolve(PROPERTIES_FILE_NAME).toFile
			if (file.exists) {
				load(new FileInputStream(file))
			}
		} catch(Exception e) {
			e.printStackTrace
		}
	}
	
	/**
	 * Load properties from properties file in the specified project.
	 */
	def load(String rootPath) {
		println("Loading Cinco Properties...")
		val file = IWorkspaceContext.getLocalInstance().getFile(PROPERTIES_FILE_NAME)
		if (file.exists) { 
			super.load(new FileInputStream(file))
		}
		println("Cinco Properties: " + this)
	}
	
	private static def String getStrValue(Pair<String,String> p) {
		instance.getProperty(p.key) ?: p.value
	}
	
	private static def List<String> getStrValues(Pair<String,String> p) {
		(instance.getProperty(p.key) ?: p.value).splitValues.toList
	}
	
	private static def Integer getIntValue(Pair<String,Integer> p) {
		val value = instance.getProperty(p.key)
		if (value !== null) 
			Integer.parseInt(value)
		else p.value
	}
	
	private static def splitValues(String listOfValues) {
		listOfValues?.split(",")?.map[trim]?.filter[!nullOrEmpty] ?: #[]
	}
}