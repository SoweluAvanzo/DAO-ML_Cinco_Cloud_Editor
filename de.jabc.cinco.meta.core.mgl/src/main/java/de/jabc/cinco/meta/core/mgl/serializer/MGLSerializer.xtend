package de.jabc.cinco.meta.core.mgl.serializer

import mgl.Annotation
import mgl.ComplexAttribute
import mgl.Edge
import mgl.EdgeElementConnection
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalElementContainment
import mgl.Import
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.PrimeParameters
import mgl.PrimitiveAttribute
import mgl.ReferencedEClass
import mgl.ReferencedModelElement
import mgl.ReferencedType
import mgl.Type
import mgl.UserDefinedType
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject

class MGLSerializer {
	
	def dispatch CharSequence serialize(MGLModel it)'''
		«imports.serialize("\n")»
		id «package»
		
		«nodes?.serialize("\n")»
		
		«edges?.serialize("\n")»
		
		«types?.serialize("\n")»
	'''
	
	def dispatch CharSequence serialize(GraphModel gm) '''
	«gm.annotations.serialize("\n")»
	graphModel «gm.name» {
		«serializeIcon(gm.iconPath)»
		«serializeFileExtension(gm.fileExtension)»
		
		«gm.attributes?.serialize("\n")»
	}
	
	'''
	
	//==========NODE Type serializer=========//
	def dispatch CharSequence serialize(Node n) '''
	«n.annotations.serialize("\n")»
	«n.serializeAbstract» «n.nodeType» «n.name» «n.serializeExtends» {
		«n.primeReference?.serialize»
		«n.parameters?.serialize»
		«n.attributes.serialize("\n")»
		«IF !n.incomingEdgeConnections.nullOrEmpty»
			incomingEdges(«n.incomingEdgeConnections.serialize(",")»)
		«ENDIF»
		«IF !n.outgoingEdgeConnections.nullOrEmpty»
			outgoingEdges(«n.outgoingEdgeConnections.serialize(",")»)
		«ENDIF»
		«IF n instanceof NodeContainer»
			«IF !n.containableElements.nullOrEmpty»
			containableElements(«n.containableElements.serialize(",")»)
			«ENDIF»
		«ENDIF»
	}
	'''
	
	//==========EDGE Type serializer=========//
	def dispatch CharSequence serialize(Edge e) '''
	«e.annotations.serialize("\n")»
	«e.serializeAbstract» edge «e.name» «e.serializeExtends» {
		«e.attributes.serialize("\n")»
	}
	'''
	
	//==========UserDefiened Type serializer=========//
	def dispatch serialize(UserDefinedType t) '''
	«t.annotations.serialize("\n")»
	«t.serializeAbstract» type «t.name» «t.serializeExtends» {
		«t.attributes.serialize("\n")»
	}
	'''
	
	//==========ENUM serializer=========//
	def dispatch serialize(Enumeration e) '''
	«e.annotations.serialize("\n")»
	enum «e.name» «e.serializeExtends» {
		«FOR lit: e.literals SEPARATOR " "» "«lit»" «ENDFOR»
	}
	'''
	
	
	def dispatch CharSequence serialize(EList<? extends EObject> objs)
	'''«objs.serialize("")»'''

	def CharSequence serialize(EList<? extends EObject> objs, String separator) {
		objs?.map[serialize].join(separator)
	}
	
		
	def dispatch CharSequence serialize(Annotation it) {
	if (!value.nullOrEmpty)
	'''@«name»(«value.map[v | '''"«v»"'''].join(",")»)'''
	else
	'''@«name»'''
	}
	
	def dispatch CharSequence serialize(Import it) 
	'''«IF stealth»stealth «ENDIF »import "«importURI»" as «name»'''
	
	def dispatch CharSequence serialize(PrimitiveAttribute it) '''
	«annotations.serialize»
	attr «type.getName» as «name»'''
	
	def dispatch CharSequence serialize(ComplexAttribute it) '''
	«annotations.serialize»
	attr «type.getName» as «name»'''
	
	def dispatch CharSequence serialize(ReferencedType it) '''
	«annotations.serialize»
	prime «type» as «name»
	'''
	
	def dispatch type(ReferencedModelElement it)
	'''«IF imprt !== null»«imprt.name»::«ELSE»this::«ENDIF»«type.name»'''
	
	
	def dispatch type(ReferencedEClass it)
	'''«imprt.name».«type.name»'''
	
	def serializeAbstract(ModelElement me) {
		if (me.isIsAbstract) '''abstract'''
	}
	
	def nodeType(Node n) {
		switch (n) {
			NodeContainer:'''container'''
			Node:'''node'''
			default: {
				
			}
		}
	}
	
	def dispatch serialize(PrimeParameters it) '''
	'''
	
	def dispatch serialize(EdgeElementConnection eec) {
	if (!eec.connectingEdges.nullOrEmpty)
	'''{«eec.connectingEdges.map[name].join(",")»}[«eec.lowerBound», «eec.upperBound.toBound»]'''
	else
	'''*[«eec.lowerBound», «eec.upperBound.toBound»]'''
	}
	
	def dispatch serialize(GraphicalElementContainment gec) {
	if (!gec.types.nullOrEmpty)
	'''{«gec.types.map[name].join(",")»}[«gec.lowerBound», «gec.upperBound.toBound»]'''
	else
	'''*[«gec.lowerBound», «gec.upperBound.toBound»]'''
	}
	
	def toBound(int i) {
		if (i == -1) '''*''' else i.toString
	}
	
	def serializePackage(String s)'''«IF !s.empty»package «s»«ENDIF»'''
	def serializeNsUri(String s)'''«IF !s.empty»nsURI "«s»"«ENDIF»'''
	def serializeIcon(String s)'''«IF !s.empty»iconPath "«s»"«ENDIF»'''
	def serializeFileExtension(String s)'''«IF !s.empty»diagramExtension "«s»"«ENDIF»'''
	
	def serializeExtends(Type t) {
		var ext = switch (t) {
			Node: t.extends
			Edge: t.extends
			UserDefinedType: t.extends
			default: null			
		}
		if (ext !== null) '''extends «ext.name»'''
		else ''''''
	}
}
