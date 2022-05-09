package de.jabc.cinco.meta.plugin.pyro.backend.connector

import java.util.LinkedList
import java.util.List
import java.util.concurrent.Callable
import de.jabc.cinco.meta.plugin.pyro.util.Escaper
import org.eclipse.emf.ecore.EObject

class Model  {
	public static val escaper = new Escaper
 	public static val dbType = '''PanacheEntity'''
 	public static val dbTypeFqn = '''io.quarkus.hibernate.orm.panache.«dbType»'''
	
	String fqn = ""
	String name = ""
	String entityName = ""
	public EObject type = null
	List<String> literals = new LinkedList;
	List<String> joinTables = new java.util.LinkedList<String>();
	
	StringBuilder attributes = new StringBuilder
	StringBuilder functions = new StringBuilder
	
	new(String fqn, String name, EObject element) {
		this.fqn = fqn
		this.name = name
		this.type = element
		// create entityName for the use-case: "e.g. several packages have different elements with same name"
		this.entityName = escaper.lowEscapeJava(fqn+"_"+name).replaceAll("\\.", "_")
	}
	
	def getName() {
		return this.name
	}
	
	def getPath() {
		return this.fqn.replaceAll("\\.","/")
	}
	
	def singlePrimitiveAttribute(String name,String type) {
		attributes.append('''
			
			public «type» «name»;
		''')
	}
	
	def multiPrimitiveAttribute(String name,String type) {
		attributes.append('''
			
			@javax.persistence.ElementCollection
			public java.util.Collection<«type»> «name» = new java.util.ArrayList<>();
		''')
	}
	
	def singleAttribute(String name, String type, boolean aggregation, boolean joinColumn) {
		attributes.append('''
			
			«IF aggregation»
				@javax.persistence.OneToOne(cascade=javax.persistence.CascadeType.ALL, fetch=javax.persistence.FetchType.LAZY)
			«ELSE»
				@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL, fetch=javax.persistence.FetchType.LAZY)
				@javax.persistence.JoinColumn(
					nullable=true«IF joinColumn»,«/* Column-name is: name of the attribute, name of the DB-class and "id" */»
					name = "«name»_«type.substring(type.lastIndexOf('.')+1)»_id"
				«ENDIF»
				)
			«ENDIF»
			public «type» «name»;
		''')
	}
	
	def singleEnumAttribute(String name, CharSequence type) {
		attributes.append('''
			
			@javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
			public «type» «name»;
		''')
	}
	
	def multiAttributeJoinTable(String name, String type, String joinColumn, String inverseJoinColumn) {
		// create entityName for the use-case: "e.g. several packages have different elements with same name"
		val joinTableName = name.createJoinTableName
		attributes.append('''
			
			«IF joinTableName !== null»
				@javax.persistence.JoinTable(
					name = "«joinTableName»",
					joinColumns = { @javax.persistence.JoinColumn(name = "«joinColumn»") },
					inverseJoinColumns = { @javax.persistence.JoinColumn(name = "«inverseJoinColumn»") }
				)
			«ENDIF»
			@javax.persistence.OneToMany(fetch=javax.persistence.FetchType.LAZY)
			public java.util.Collection<«type»> «name» = new java.util.ArrayList<>();
		''')
	}
	
	def multiAttribute(String name, String type, String mapped) {
		attributes.append('''
			
			@javax.persistence.OneToMany«IF !mapped.nullOrEmpty»(mappedBy="«mapped»", fetch=javax.persistence.FetchType.LAZY)«ENDIF»
			public java.util.Collection<«type»> «name» = new java.util.ArrayList<>();
		''')
	}
	
	def multiEnumAttribute(String name, CharSequence type) {
		attributes.append('''
			
			@javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
			@javax.persistence.ElementCollection
			public java.util.Collection<«type»> «name» = new java.util.ArrayList<>();
		''')
	}
	
	def enumLiteral(String value) {
		literals.add(value)
	}
	
	def content() {
		if(literals.empty) {
			return classContent
		}
		enumContent
	}
	
	def fileNameDataConnector()'''«name»DB.java'''
	
	def classContent() {
		'''
			package «fqn»;
			
			import javax.persistence.Entity;
			import «dbTypeFqn»;
			
			@Entity(name="«entityName»")
			public class «name»DB extends «dbType» {
				«attributes.toString»
				«IF functions.length>0»
					«functions.toString»
				«ENDIF»
			}
		'''
	}
	
	def enumContent() {
		'''
			package «fqn»;
			
			public enum «name»DB {
				
				«literals.join(",")»
			}
		'''
	}
	
	/**
	 * Getter/Setter
	 */
	 def createDelete(Callable<CharSequence> content) {
	 	createFunction('''void''', '''delete''', null, content, null, true)
	 }
	
	/**
	 * Getter/Setter
	 */
	def createGetter(CharSequence returnType, CharSequence attributeName, Callable<CharSequence> content) {
		createFunction(returnType, '''get«attributeName»''', null, content)
	}
	
	def createSetter(CharSequence attributeName, CharSequence parameter, Callable<CharSequence> content) {
		createFunction(null, '''set«attributeName»''', parameter, content)
	}
	
	/**
	 * Collection operations
	 */
	def createCollectionClear(CharSequence collectionAttributenName, CharSequence parameter, Callable<CharSequence> content) {
		createFunction(null, '''clear«collectionAttributenName»''', parameter, content)
	}
	
	def createCollectionAdd(CharSequence collectionAttributenName, CharSequence parameter, Callable<CharSequence> content) {
		createFunction(null, '''add«collectionAttributenName»''', parameter, content)
	}
	
	def createCollectionAddAll(CharSequence collectionAttributenName, CharSequence parameter, Callable<CharSequence> content) {
		createFunction(null, '''addAll«collectionAttributenName»''', parameter, content)
	}
	
	def createCollectionRemove(CharSequence collectionAttributenName, CharSequence parameter, Callable<CharSequence> content) {
		createFunction('''boolean''', '''remove«collectionAttributenName»''', parameter, content)
	}
	
	def createCollectionContains(CharSequence collectionAttributenName, CharSequence parameter, Callable<CharSequence> content) {
		createFunction('''boolean''', '''contains«collectionAttributenName»''', parameter, content)
	}
	
	def createCollectionIsEmpty(CharSequence collectionAttributenName, Callable<CharSequence> content) {
		createFunction('''boolean''', '''isEmpty«collectionAttributenName»''', null, content)
	}
	
	def createCollectionSize(CharSequence collectionAttributenName, Callable<CharSequence> content) {
		createFunction('''int''', '''size«collectionAttributenName»''', null, content)
	}
	
	def createFunction(CharSequence returnType, CharSequence functionName, CharSequence parameters, Callable<CharSequence> content) {
		createFunction(returnType, functionName, parameters, content, null, false)
	}
	
	def createFunction(CharSequence returnType, CharSequence functionName, CharSequence parameters, Callable<CharSequence> content, boolean overrideSuper) {
		createFunction(returnType, functionName, parameters, content, null, overrideSuper)
	}
	
	def createFunction(CharSequence returnType, CharSequence functionName, CharSequence parameters, Callable<CharSequence> content, CharSequence javaDoc, boolean overrideSuper) {
		functions.append(
			'''	
				
				«IF javaDoc !== null»«javaDoc»«ENDIF»
				«IF overrideSuper»@Override«ENDIF»
				public «IF returnType !== null»«returnType»«ELSE»void«ENDIF» «functionName»(«IF parameters !== null»«parameters»«ENDIF») {
					«content.call»
				}
			'''
		)	
	}
	
	def createJoinTableName(String name) {
		val index = joinTables.length;
		joinTables.add(name);
		escaper.lowEscapeJava(entityName+"_joinTable_"+index).replaceAll("\\.", "_");
	}
}

