package de.jabc.cinco.meta.plugin.pyro.util

import java.util.LinkedList
import org.apache.commons.lang3.RandomStringUtils

class Escaper {
	def escapeJavaDart(String s) {
		s.escapeJava.escapeDart
	}
	
	def escapeDart(String s){
		var s1 = s.replaceAll(" ","_")
		s1 = s1.replaceAll("-","_")
		val iOf = dartKeywords.toList.indexOf(s1)
		if(iOf>-1){
			return '''attr_«s1»'''
		}
		return s1;
	}
	
	def toCamelCase(String s) {
		if (s === null || s.isEmpty()) {
        	return s;
	    }
	 
	    val converted = new StringBuilder();
	 
	    var upperInfront = false;
	    for (char ch : s.toCharArray()) {
	        if (upperInfront && Character.isUpperCase(ch)) {
	        	converted.append(Character.toLowerCase(ch))
	        } else if (Character.isUpperCase(ch)) {
	            converted.append(ch)
	            upperInfront = true
	        } else {
	           converted.append(ch);
	           upperInfront = false
	        }
	    }
	 
	    return converted.toString();
	}
	
	def replaceEscapeDart(String s){
		var newS = new String(s)
	    val parts = newS.split("[^a-zA-Z0-9_]");
	    for(String p : new LinkedList(parts)) {
	      for(String n: getDartKeywords().filter[n|p.equals(n)] ){
				newS = s.replaceAll(n,n.escapeDart)
			}
	    }
		return newS;
	}
	
	def toUnderScoreCase(String s) {
		val regex = "(.)([A-Z][a-z])";
        val replacement = "$1_$2";
        return s.replaceAll(regex, replacement).toUpperCase();
	}
	
	def escapeJava(String s){
		var s1 = s.replaceAll(" ","_")
		s1 = s1.replaceAll("-","_")
		val iOf = javaKeywords.toList.indexOf(s1)
		if(iOf>-1){
			return '''attr_«s1»'''
		}
		return s1;
	}
	
	def fuEscapeDart(String s){
		return s.toFirstUpper.escapeDart;
	}
	
	def fuEscapeJava(String s){
		return s.toFirstUpper.escapeJava;
	}
	
	def lowEscapeDart(String s){
		return s.toLowerCase.escapeDart;
	}
	
	def lowEscapeJava(String s){
		return s.toLowerCase.escapeJava;
	}
	
	
	def getDartKeywords()
	{
		#[ 	
			"abstract" ,	"deferred", 	"if",	"super",
			"as", 	"do",	"implements", 	"switch",
			"assert",	"dynamic", 	"import", 	"sync" ,
			"async", 	"else",	"in",	"this",	"enum",	"is",	"throw",
			"await", 	"export", 	"library", 	"true",
			"break",	"external", 	"new",	"try",
			"case",	"extends",	"null",	"typedef", 
			"catch",	"factory",	"operator", 	"var",
			"class",	"false",	"part",	"void",
			"const",	"final",	"rethrow",	"while",
			"continue",	"finally",	"return",	"with",
			"covariant", 	"for",	"set",	"yield", 
			"default",	"get", 	"static" , "library"
		]
		+
		#[
			"int", "double", "num", "bool", "String"
		]
		+
		#[
			"id"
		]
	}
	
	def getJavaKeywords() {
		#[
			
			"abstract",	"continue",	"for",	"new",	"switch",
			"assert",	"default",	"goto",	"package",	"synchronized",
			"boolean",	"do",	"if",	"private",	"this",
			"break",	"double",	"implements",	"protected",	"throw",
			"byte",	"else",	"import",	"public",	"throws",
			"case",	"enum",	"instanceof",	"return",	"transient",
			"catch",	"extends",	"int",	"short",	"try",
			"char",	"final",	"interface",	"static",	"void",
			"class",	"finally",	"long",	"strictfp",	"volatile",
			"const",	"float",	"native",	"super",	"while"
		]
		+
		#[
			"id"
		]
	}
	
	def static randomString(int length) {
		val useLetters = true;
    	val useNumbers = false;
		return RandomStringUtils.random(length, useLetters, useNumbers)
	}
	
	def trimQuotes(String s) {
		s.replace('"','')
	}
	
	def escapespecialCharacters(String s){
		var s1 = s;
		if(!s.isNullOrEmpty){
			if(s.contains("\"")){
				s1=s.replace("\"", '\\"'.toString);		
			}
		}
		return s1;
		// the list of special expressions is to be extended
	}

	def escapeJavaSpecialCharacters(String s){
		// the list of special expressions is to be extended
		var specialChars = #["\""];
		var s1 = s;
		for (String c : specialChars){
			if(!s.isNullOrEmpty){
				if(s.contains(c)){
					s1=s.replace(c, "\\"+c);            
				}
			}            
		}
		return s1;
	}

	def escapeDartSpecialCharacters(String s){
		// the list of special expressions is to be extended
		var specialChars = #["\"", "$", "!","@","#", "%", "^","&", "*", "(", ")", "?"];
		var s1 = s;
		for (String c : specialChars){
			if(!s.isNullOrEmpty){
				if(s.contains(c)){
					s1=s.replace(c, "\\"+c);
				}
			}
			return s1;
		}    
    }
}

