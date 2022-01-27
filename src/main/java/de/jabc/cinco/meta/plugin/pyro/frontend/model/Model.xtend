package de.jabc.cinco.meta.plugin.pyro.frontend.model

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.util.Collections
import mgl.ContainingElement 
import mgl.Edge
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.UserDefinedType
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import style.NodeStyle
import style.Styles

class Model extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameDispatcher() '''dispatcher.dart'''
	
	def contentDispatcher() '''
		import 'core.dart' as core;
		
		«FOR g:gc.mglModels»
			import '«g.modelFile»' as «g.dartImplPackage»;
		«ENDFOR»
		«FOR p:gc.ecores»
			import '«p.modelFile»' as «p.dartImplPackage»;
		«ENDFOR»
		
		class GraphModelDispatcher {
			
			static core.PyroElement dispatchElement(Map<String, dynamic> jsog,Map cache) {
			  	var type = jsog["runtimeType"];
				«FOR e:gc.mglModels.map[elements].flatten.toSet.filter[!isAbstract] SEPARATOR " else "
				»if(type == '«e.restFQN»'){
						return «e.dartImplClass».fromJSOG(jsog,cache);
				}«
				ENDFOR»
				return dispatchEcoreElement(cache,jsog);
			}
			
			static core.PyroElement dispatchEcoreElement(Map cache,dynamic jsog) {
			  	var type = jsog["__type"];
				«FOR e:gc.ecores.map[elements].flatten.toSet.filter[!abstract] SEPARATOR " else "
				»if(type == '«e.typeName»'){
						return «e.dartImplClass».fromJSOG(jsog,cache);
				}«
				ENDFOR»
				throw new Exception("Unkown element ${type}");
			}
		  	
			static core.PyroModelFile dispatchEcorePackage(Map cache,dynamic jsog) {
			  	var type = jsog["__type"];
		  		«FOR g:gc.ecores SEPARATOR " else "
		  		»if(type == '«g.name.fuEscapeDart»' || type == '«g.typeName»'){
		  			return «g.dartImplClass».fromJSOG(jsog,cache);
		  		}«
		  		ENDFOR»
				throw new Exception("Unkown dispatching type ${type}");
			}
			
			static core.PyroModelFile dispatch(Map cache,dynamic jsog){
			  	var type = jsog["__type"];
				«FOR g:gc.concreteGraphModels.map[resolveSubTypesAndType].flatten.toSet SEPARATOR " else "
				»if(type == '«g.typeName»'){
				  return «g.dartImplClass».fromJSOG(jsog,cache);
				}«
				ENDFOR»
			    return dispatchEcorePackage(cache,jsog);
			}
			«FOR g:gc.mglModels»
				
				static core.ModelElementContainer dispatch«g.name.escapeDart»ModelElementContainer(
				  	Map cache,dynamic jsog
				  ){
				  	var type = jsog["__type"];
				  	«FOR e:g.elementsAndGraphmodels.filter(ContainingElement).map[resolveSubTypesAndType].flatten.toSet SEPARATOR " else "
				  	»if(type == '«e.typeName»'){
				  		return «e.dartImplClass».fromJSOG(jsog,cache);
				  	}«
				  	ENDFOR»
				  	throw new Exception("Unkown modelelement type ${type}");
				}
				
				static core.ModelElement dispatch«g.name.escapeDart»ModelElement(Map cache,dynamic jsog){
				  	var type = jsog["__type"];
					«FOR e:g.elementsAndTypes.filter(GraphicalModelElement).map[resolveSubTypesAndType].flatten.toSet SEPARATOR " else "
					»if(type == '«e.typeName»'){
						return «e.dartImplClass».fromJSOG(jsog,cache);
					}«
					ENDFOR»
					throw new Exception("Unkown modelelement type ${type}");
				}
		    «ENDFOR»
		}
	'''

	def fileNameGraphModel(MGLModel g) '''«g.modelFile»'''
	
	def fileNameEcore(EPackage g) '''«g.modelFile»'''

	def pyroElementConstr(ModelElement element, MGLModel g,Styles styles) '''
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			«IF element instanceof Edge»
				this.bendingPoints = new List();
			«ENDIF»
			«IF element instanceof ContainingElement»
				this.modelElements = new List();
			«ENDIF»
			«IF element instanceof Node»
				«{
					val nodeStyle = styling(element,styles) as NodeStyle
					val size = nodeStyle.mainShape.size
					'''
					«IF size!==null»
						width = «size.width»;
						height = «size.height»;
					«ENDIF»
					'''
				}»
				this.incoming = new List();
				this.outgoing = new List();
				this.x = 0;
				this.y = 0;
			«ENDIF»
			«IF element instanceof GraphModel»
				this.width = 10000;
				this.height = 10000;
				this.scale = 1.0;
				this.router = null;
				this.connector = 'normal';
				this.filename = '';
				this.isPublic = false;
				this.extension = '«element.fileExtension»';
			«ENDIF»
			// properties
			«FOR attr : element.attributesExtended»
				«attr.name.escapeDart» =
				«IF attr.list»
					new List();
				«ELSE»
					«attr.init(g,"")»;
				«ENDIF»
			«ENDFOR»
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			«IF element instanceof GraphicalModelElement»
				if(jsog.containsKey('a_container')){
					if(jsog['a_container']!=null){
						if(jsog['a_container'].containsKey('@ref')){
							this.container = cache[jsog['a_container']['@ref']];
						} else {
							this.container = GraphModelDispatcher.dispatch«g.name.escapeDart»ModelElementContainer(cache,jsog['a_container']);
						}
					}
				}
			«ENDIF»
			«IF element instanceof Edge»
				this.bendingPoints = new List();
				if(jsog.containsKey('a_bendingPoints')){
					if(jsog['a_bendingPoints']!=null){
						this.bendingPoints = jsog['a_bendingPoints'].map((n)=>new core.BendingPoint(jsog:n)).toList().cast<core.BendingPoint>();
					}
				}
				if(jsog.containsKey('a_sourceElement')){
					if(jsog['a_sourceElement']!=null){
						Map<String, dynamic> j = jsog['a_sourceElement'];
						if(j.containsKey('@ref')){
							this.source = cache[j['@ref']];
						} else {
							this.source = GraphModelDispatcher.dispatch«g.name.escapeDart»ModelElement(cache,j);
						}
					}
				}
				if(jsog.containsKey('a_targetElement')){
					if(jsog['a_targetElement']!=null){
						Map<String,dynamic> j = jsog['a_targetElement'];
						if(j.containsKey('@ref')){
							this.target = cache[j['@ref']];
						} else {
							this.target = GraphModelDispatcher.dispatch«g.name.escapeDart»ModelElement(cache,j);
						}
					}
				}
			«ENDIF»
			«IF element instanceof GraphModel»
				this.width = jsog['a_width'];
				this.height = jsog['a_height'];
				this.scale = jsog['a_scale'];
				this.router = jsog['a_router'];
				this.connector = jsog['a_connector'];
				this.filename = jsog['filename'];
				this.extension = jsog['extension'];
				this.isPublic = jsog['isPublic'];
			«ENDIF»
			«IF element instanceof Node»
				width = jsog["a_width"];
				height = jsog["a_height"];
				x = jsog["a_x"];
				y = jsog["a_y"];
				angle = jsog["a_angle"];
				
				this.incoming = new List();
				if (jsog.containsKey("a_incoming")) {
					if(jsog["a_incoming"]!=null){
						for(var v in jsog["a_incoming"]){
							if(v.containsKey("@ref")){
								this.incoming.add(cache[v['@ref']]);
							} else {
							  this.incoming.add(GraphModelDispatcher.dispatch«g.name.escapeDart»ModelElement(cache,v));
							}
						}
					}
				}
				this.outgoing = new List();
				if (jsog.containsKey("a_outgoing")) {
					if(jsog["a_outgoing"]!=null) {
						for(var v in jsog["a_outgoing"]){
							if(v.containsKey("@ref")){
								this.outgoing.add(cache[v['@ref']]);
							} else {
							  this.outgoing.add(GraphModelDispatcher.dispatch«g.name.escapeDart»ModelElement(cache,v));
							}
						}
					}
				}
				«IF element.isPrime»
					«{
						val primeName = element.primeReference.name.escapeJavaDart
						val primeKey = '''b_«primeName»'''
						'''
							if(jsog.containsKey("«primeKey»")){
								if(jsog["«primeKey»"]!=null){
									if(jsog["«primeKey»"].containsKey("@ref")){
										this.«primeName» = cache[jsog["«primeKey»"]["@ref"]];
									} else {
										«{
											var primeRef = element.primeReference.type
											var primePack = primeRef.modelPackage
											var primePackName = primePack === null ? null : primePack.name.escapeDart
											'''
												this.«primeName» =
												«IF primeRef instanceof ContainingElement»
													GraphModelDispatcher.dispatch«primePackName»ModelElementContainer(cache,jsog["«primeKey»"]);
												«ELSEIF primePack instanceof EPackage»
													GraphModelDispatcher.dispatchEcoreElement(cache,jsog["«primeKey»"]);
												«ELSE»
													GraphModelDispatcher.dispatch«primePackName»ModelElement(cache,jsog["«primeKey»"]);
												«ENDIF»
											'''
										}»
									}
								}
							}
						'''
					}»
				«ENDIF»
		  	«ENDIF»  
			«IF element instanceof ContainingElement»
				this.modelElements = new List();
				if (jsog.containsKey("a_modelElements")) {
					if(jsog["a_modelElements"]!=null){
						for(var v in jsog["a_modelElements"]){
							if(v.containsKey("@ref")){
								this.addElement(cache[v['@ref']]);
							} else {
							  this.addElement(GraphModelDispatcher.dispatch«g.name.escapeDart»ModelElement(cache,v));
							}
						}
					}
				}
			«ENDIF»
			
			// properties
			«FOR attr : element.attributesExtended.sortBy[name]»
				«IF attr.list»
					this.«attr.name.escapeDart» = new List();
				«ENDIF»
				if (jsog.containsKey("c_«attr.name.escapeJava»")) {
					«IF attr.list»
						for(dynamic jsogObj in jsog["c_«attr.name.escapeJava»"]) {
					«ELSE»
						dynamic jsogObj = jsog["c_«attr.name.escapeJava»"];
					«ENDIF»
					if (jsogObj != null) {
						«IF attr.isPrimitive»
							«attr.primitiveDartType(g)» value«attr.name.escapeDart»;
							if (jsogObj != null) {
								«IF attr.attributeTypeName.getEnum(g)!==null»
									value«attr.name.escapeDart» = «attr.attributeTypeName.fuEscapeDart»Parser.fromJSOG(jsogObj);
								«ELSE»
									value«attr.name.escapeDart» = jsogObj«attr.deserialize(g)»;
								«ENDIF»
							}
							«IF attr.list»
								this.«attr.name.escapeDart».add(value«attr.name.escapeDart»);
							«ELSE»
								this.«attr.name.escapeDart» = value«attr.name.escapeDart»;
							«ENDIF»
						«ELSE»
							«attr.complexDartType» value«attr.name.escapeDart»;
							String jsogId;
							if (jsogObj.containsKey('@ref')) {
								jsogId = jsogObj['@ref'];
							} else {
								jsogId = jsogObj['@id'];
							}
							if (cache.containsKey(jsogId)) {
								value«attr.name.escapeDart» = cache[jsogId];
							} else {
								if (jsogObj != null) {
									«val subTypes = attr.attributeTypeName.subTypesAndType(g).toList»
									«FOR subType : subTypes»										
										«IF subType instanceof ModelElement && (subType as ModelElement).isIsAbstract»
											«FOR subSubType : subType.name.subTypes(g).filter[!subTypes.contains(it)]»
												if (jsogObj['__type'] == "«subSubType.typeName»") {
													value«attr.name.escapeDart» =
													new «subSubType.dartFQN»(cache: cache, jsog: jsogObj);
												}
											«ENDFOR»
										«ELSE»
											if (jsogObj['__type'] == "«subType.typeName»") {
												value«attr.name.escapeDart» =
												new «subType.dartFQN»(cache: cache, jsog: jsogObj);
											}
										«ENDIF»
									«ENDFOR»
								}
							}
							«IF attr.list»
								this.«attr.name.escapeDart».add(value«attr.name.escapeDart»);
							«ELSE»
								this.«attr.name.escapeDart» = value«attr.name.escapeDart»;
							«ENDIF»
						«ENDIF»
					}
				}
				«IF attr.list»}«ENDIF»
			«ENDFOR»
		}
		
	'''

	def pyroElementFromJSOG(ModelElement element, MGLModel g) '''
		Map toJSOG(Map cache) {
			Map map = new Map();
			
			if(cache.containsKey("«element.typeName»:${id}")){
				map['@ref']=cache["«element.typeName»:${id}"]['@id'];
				return map;
			}
			else {
				cache["«element.typeName»:${id}"]=(cache.length+1).toString();
				map['@id'] = cache["«element.typeName»:${id}"];
				
				map['runtimeType'] = "info.scce.pyro.«g.name.lowEscapeJava».rest.«element.name.fuEscapeJava»";
				map['__type'] = "«element.typeName»";
				map['id'] = id;
				«IF element instanceof GraphModel»
					map['width'] = this.width;
					map['height'] = this.height;
					map['scale'] = this.scale;
					map['router'] = this.router;
					map['connector'] = this.connector;
					map['filename'] = this.filename;
					map['extension'] = this.extension;
				«ENDIF»
				cache["«element.typeName»:${id}"] = map;
				«FOR attr : element.attributesExtended»
					map['«attr.name.escapeJava»']=«attr.name.escapeDart»==null?null:
						«IF attr.isList»
							«attr.name.escapeDart».map((n)=>n != null ? «attr.serialize(g,'''n''')» : null).toList();
						«ELSE»
							«attr.serialize(g,'''«attr.name.escapeDart»''')»;
						«ENDIF»
				«ENDFOR»
			}
			return map;
		}
		
		static «element.name» fromJSOG(dynamic jsog, Map cache)
		{
			return new «element.dartFQN»(jsog: jsog,cache: cache);
		}
		
		@override
		«element.name» propertyCopy({bool root:true}) {
			var elem = new «element.dartFQN»();
			elem.id = this.id;
			if(!root) {
				return elem;
			}
			«FOR attr:element.attributesExtended»
				«IF !attr.isPrimitive()»if(this.«attr.name.escapeDart»!=null) {«ENDIF»«IF attr.list»
					elem.«attr.name.escapeDart» = this.«attr.name.escapeDart».map((n){
						«FOR sub:attr.attributeTypeName.subTypesAndType(g).filter(ModelElement).filter[!isAbstract] SEPARATOR " else "»
							if(n is «sub.dartFQN») {
								return (n as «sub.dartFQN»).propertyCopy(root:false);
							}
						«ENDFOR»
						return null;
					}).cast<«IF attr.isPrimitive»«attr.primitiveDartType(g)»«ELSE»«attr.complexDartType»«ENDIF»>();
				«ELSE»
					«FOR sub:attr.attributeTypeName.subTypesAndType(g).filter(ModelElement).filter[!isAbstract] SEPARATOR " else "»
						if(this.«attr.name.escapeDart» is «sub.dartFQN») {
							elem.«attr.name.escapeDart» = (this.«attr.name.escapeDart» as «sub.dartFQN»).propertyCopy(root:false);
						}
					«ENDFOR»
				«ENDIF»«IF !attr.isPrimitive»}«ENDIF»
			«ENDFOR»
			return elem;
		}
		
		@override
		void merge(covariant «element.name» elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
			if(cache==null){
				cache = new Map();
			}
			cache["${elem.id}"]=this;
			this.id = elem.id;
			«IF element instanceof GraphModel»
				
			if(!structureOnly) {
				filename = elem.filename;
				extension = elem.extension;
				scale = elem.scale;
				connector = elem.connector;
				router = elem.router;
				height = elem.height;
				width = elem.width;
				isPublic = elem.isPublic;
			}
			«ENDIF»
			«IF element instanceof ContainingElement»
				
				if(!structureOnly) {
					//remove missing
					modelElements.removeWhere((m)=>elem.modelElements.where((e)=>m.id==e.id).isEmpty);
					//merge known
					for(var m in modelElements){
						if(!cache.containsKey("${m.id}")){
							m.merge(elem.modelElements.firstWhere((e)=>m.id==e.id),cache:cache);
						}
					}
					//add new
					addAllElements(elem.modelElements.where((e)=>modelElements.where((m)=>m.id==e.id).isEmpty).toList());
				}
			«ENDIF»
			«IF element instanceof GraphicalModelElement»
				
				if(!structureOnly){
					if(container != null){
						if(cache.containsKey("${container.id}")){
							container = cache["${container.id}"];
						} else {
							container.merge(elem.container, cache: cache, structureOnly: structureOnly);
						}
					} else {
						container = elem.container;
					}
				}
			«ENDIF»
			«IF element instanceof Node»
				
				if(!structureOnly) {
					height = elem.height;
					width = elem.width;
					x = elem.x;
					y = elem.y;
					angle = elem.angle;
					//remove missing
					incoming.removeWhere((m)=>elem.incoming.where((e)=>m.id==e.id).isEmpty);
					//merge known
					for(var m in incoming){
						if(!cache.containsKey("${m.id}")){
							m.merge(elem.incoming.firstWhere((e)=>m.id==e.id),cache:cache);
						}
					}
					//add new
					incoming.addAll(elem.incoming.where((e)=>incoming.where((m)=>m.id==e.id).isEmpty).toList());
					//remove missing
					outgoing.removeWhere((m)=>elem.outgoing.where((e)=>m.id==e.id).isEmpty);
					//merge known
					for(var m in outgoing){
						if(!cache.containsKey("${m.id}")){
							m.merge(elem.outgoing.firstWhere((e)=>m.id==e.id), cache:cache);
						}
					}
					//add new
					outgoing.addAll(elem.outgoing.where((e)=>outgoing.where((m)=>m.id==e.id).isEmpty).toList());
				}
				«IF element.prime»
					«{
						val primeElementName = element.primeReference.name.escapeJavaDart
						'''
							if(!structureOnly){
								if(elem != null && elem.«primeElementName» == null) {
									«primeElementName» = elem.«primeElementName»;
								} else if(«primeElementName» != null){
									if(cache.containsKey("${«primeElementName».id}")){
										«primeElementName» = cache["${«primeElementName».id}"];
									} else {
										«primeElementName».merge(elem.«primeElementName», cache: cache, structureOnly: structureOnly);
									}
								} else {
									«primeElementName» = elem.«primeElementName»;
								}
							}
						'''
					}»
				«ENDIF»
			«ENDIF»
			«IF element instanceof Edge»
				if(!structureOnly) {
					bendingPoints = elem.bendingPoints;
					if(target!=null){
						if(!cache.containsKey("${target.id}")){
							target.merge(elem.target, cache:cache);
						}
					} else {
						target = elem.target;
					}
					if(source != null){
						if(!cache.containsKey("${source.id}")){
							source.merge(elem.source,cache:cache);
						}
					} else {
						source = elem.source;
					}
				}
			«ENDIF»
			«FOR attr : element.attributesExtended»
				«IF !attr.list && g.elementsAndTypes.map[name].exists[equals(attr.attributeTypeName)]»
					if(«attr.name.escapeDart»!=null && elem.«attr.name.escapeDart»!=null){
						if(!cache.containsKey("${«attr.name.escapeDart».id}")){
							«attr.name.escapeDart».merge(elem.«attr.name.escapeDart»,cache:cache,structureOnly:structureOnly);
						} else{
							«attr.name.escapeDart» = cache["${«attr.name.escapeDart».id}"];
						}
					} else {
						«attr.name.escapeDart» = elem.«attr.name.escapeDart»;
					}
				«ELSEIF !attr.list && g.elementsAndTypes.map[name].exists[equals(attr.attributeTypeName)]»
					//remove missing
					«attr.name.escapeDart».removeWhere((m)=>elem.«attr.name.escapeDart».where((e)=>m.id==e.id).isEmpty);
					//merge known
					for(var m in «attr.name.escapeDart»){
						if(!cache.containsKey(m.id)){
							m.merge(elem.«attr.name.escapeDart».firstWhere((e)=>m.id==e.id),cache:cache);
						}
					}
					//add new
					elem.«attr.name.escapeDart».where((e)=>«attr.name.escapeDart».where((m)=>m.id==e.id).isEmpty).forEach((e)=>«attr.name.escapeDart».add(e));
				«ELSEIF attr.list && g.elementsAndTypes.map[name].exists[equals(attr.attributeTypeName)]»
					// adjust new length
					if(elem.«attr.name.escapeDart».length < «attr.name.escapeDart».length) {
						«attr.name.escapeDart».removeRange(elem.«attr.name.escapeDart».length, «attr.name.escapeDart».length);
					}
					elem.«attr.name.escapeDart».asMap().forEach((idx,b){
						if(«attr.name.escapeDart».length>idx && «attr.name.escapeDart»[idx] != null && «attr.name.escapeDart»[idx].id==-1) {
							// update id of pre-existing element (one with id=-1)
							«attr.name.escapeDart»[idx].id = b.id;
							«attr.name.escapeDart»[idx].merge(b,cache:cache,structureOnly:structureOnly);
						} else {
							if(idx>=«attr.name.escapeDart».length) {
								// append element
								«attr.name.escapeDart».add(b);
							} else {
								// change/update element
								«attr.name.escapeDart»[idx] = b;								
							}
						}
					});
					«attr.name.escapeDart».forEach((a) {
						if(a!=null) {
							a.merge(
								elem.«attr.name.escapeDart».where((b)=>b != null && b.id==a.id).first,
								structureOnly:false,
								cache:cache
							);
						}
					});
				«ELSE»
					«attr.name.escapeDart» = elem.«attr.name.escapeDart»;
				«ENDIF»
			«ENDFOR»
		}
		«IF (element instanceof Node || element instanceof Edge) && !element.isIsAbstract»
			
			@override
			js.JsArray styleArgs() {
				try {
			    	return new js.JsArray.from([«element.styleArgs.map[n|'''"«n»"'''].join(",")»]);
			    } catch(e) {
			    	return new js.JsArray.from([«element.styleArgs.map[n|'''"invalid"'''].join(",")»]);
			    }
			}
		«ENDIF»
	'''
	
	def getStyleArgs(ModelElement element) {
		if (element instanceof Node ) {
			var values = element.styleParameters
			if (!values.nullOrEmpty) {
			return values.map[replaceEscapeDart]; 			}
		}else if (element instanceof Edge ) {
			var values = element.styleParameters
			if (!values.nullOrEmpty) {
			return values.map[replaceEscapeDart]; 			}
		}

		return Collections.EMPTY_LIST
	}
	
	def getStyleArgs(Edge element){
		val values = element.styleParameters
		if(!values.nullOrEmpty){
			return values.subList(1,values.size).map[replaceEscapeDart];
		}
		return Collections.EMPTY_LIST
	}

	def pyroElementAttributeDeclaration(ModelElement element, MGLModel g) '''
		«IF element instanceof GraphModel»
			CommandGraph commandGraph;
					 	
			int width;
			int height;
			double scale;
			String router;
			String connector;
			bool isPublic;
		«ENDIF»
	    «IF element instanceof Node»
	    	«IF element.prime»
		    	«{
		    		val refElem = element.primeReference.type
		    		val primeElementName = element.primeReference.name.escapeJavaDart
		    		'''«refElem.dartFQN» «primeElementName»;'''
		    	}»
	    	«ENDIF»
	    «ENDIF»
		«FOR attr : element.attributesExtended.filter[isPrimitive]»
			«IF attr.list»List<«ENDIF»«attr.primitiveDartType(g)»«IF attr.list»>«ENDIF» «attr.name.escapeDart» = «IF attr.list»[]«ELSE»«attr.primitiveDefaultDart»«ENDIF»;
		«ENDFOR»
		«FOR attr : element.attributesExtended.filter[!isPrimitive]»
			«IF attr.list»List<«ENDIF»«attr.complexDartType»«IF attr.list»>«ENDIF» «attr.name.escapeDart»;
		«ENDFOR»
	'''

	def pyroElementClass(ModelElement element,Styles styles) {
	     	val g = element.modelPackage as MGLModel
	'''
		«IF element.isIsAbstract»abstract «ENDIF»class «element.name.fuEscapeDart» extends «element.extending("core.")»«IF element.hasToExtendContainer» implements core.Container«ENDIF»
		{
			@override
			String $displayName() =>"«element.displayName»";
			«element.pyroElementAttributeDeclaration(g)»
			«IF !element.isIsAbstract»
				@override
				String $type()=>"«element.typeName»";
				«IF element instanceof GraphModel»
					
					@override
					String $extension()=>"«element.fileExtension»";
					
					
					@override
					String $lower_type()=>"«element.lowerType»";
				«ENDIF»
				
				«element.name.fuEscapeDart»({Map cache, dynamic jsog}) {
					«element.pyroElementConstr(g,styles)»
				}
				
				«element.pyroElementFromJSOG(g)»
			«ENDIF»
			«IF element instanceof GraphicalModelElement»
				
				@override
				String $information() {
					«IF element.information»
						return "${«element.informationAttribute.name.escapeDart»}";
					«ELSE»
						return null;
					«ENDIF»
				}
				
				@override
				String $label() {
					«IF element instanceof Node && (element as Node).directlyEditable»
						return "${«(element as Node).directlyEditableAttribute.name.escapeDart»}";
					«ELSE»
						return null;
					«ENDIF»
				}
				
				@override
				core.ModelElementContainer get container => super.container as «element.getBestContainerSuperTypeNameDart»;
			«ENDIF»
			
			@override
			List<core.IdentifiableElement> allElements()
			{
				Set<core.IdentifiableElement> elementSet = new Set();
				elementSet.add(this);
				«IF element.canContain»
					
					// add contained elements
					elementSet.addAll(modelElements.expand((n) => n.allElements()));
				«ENDIF»
				«{
					val userDefinedTypes = element.attributesExtended.filter(mgl.ComplexAttribute).filter[it.type instanceof UserDefinedType];
					'''
					«IF !userDefinedTypes.empty»
						// add all contained userDefinedTypes
						«FOR attr : userDefinedTypes»
							«{
								val attributeName = attr.name.escapeDart
								'''
									«IF attr.list»
										if(«attributeName» != null && !«attributeName».isEmpty) {
											elementSet.addAll(«attributeName».expand((n) => n.allElements()));
										}
									«ELSE»
										if(«attributeName» != null) {
											elementSet.add(«attributeName»);
											elementSet.addAll(«attributeName».allElements());
										}
									«ENDIF»
								'''
							}»
						«ENDFOR»
					«ENDIF»
					'''
				}»
				
			    List<core.IdentifiableElement> list = new List();
			    list.addAll(elementSet);
				list.sort((a,b) => a.id.compareTo(b.id));
				return list;
			}
			«IF !(element instanceof UserDefinedType)»
				«IF element.hasToExtendContainer»
					
					List<core.ModelElement> modelElements;
				«ENDIF»
				
				@override
				«IF element instanceof GraphModel»
					core.GraphModel getRootElememt() => this;
				«ELSE»
					core.GraphModel getRootElememt() => container.getRootElememt();
				«ENDIF»
			«ENDIF»
		}
	'''}
	
	def pyroElementClass(EClass element, EPackage g) {
		
		val ownAttributes = element.eContents.filter(EAttribute)
		val ownReferences = element.eContents.filter(EReference)
		val allAttributes = (element.attributesExtended + element.referencesExtended).sortBy[name]
		'''
		«IF element.abstract»abstract «ENDIF»class «element.name.fuEscapeDart» extends «element.extending("core.")» {
			
			//attributes
			«FOR attr : ownAttributes»
				«IF attr.list»List<«ENDIF»«attr.primitiveDartType(g)»«IF attr.list»>«ENDIF» «attr.name.escapeDart»«IF attr.list» = new List()«ENDIF»;
			«ENDFOR»
			«FOR attr : ownReferences»
				«IF attr.list»List<«ENDIF»«attr.complexDartType»«IF attr.list»>«ENDIF» «attr.name.escapeDart»«IF attr.list» = new List()«ENDIF»;
			«ENDFOR»
			
			«IF !element.abstract»
				«element.name.fuEscapeDart»({Map cache, dynamic jsog}) {
					if (cache == null) {
						cache = new Map();
					}
					// default constructor
					if (jsog == null) {
						this.id = -1;
						«FOR attr : allAttributes»
							«attr.name.escapeDart» = «IF attr.list
								»new List()«
								ELSE
								»«attr.init»«
								ENDIF»;
						«ENDFOR»
					}
					// from jsog
					else {
						String jsogId = jsog['@id'];
						cache[jsogId] = this;
						
						this.id = jsog['id'];
						// properties
						«FOR attr : allAttributes»
							if (jsog.containsKey("«attr.name.escapeDart»")) {
								«IF attr.list»
									this.«attr.name.escapeDart» = new List();
									if(jsog["«attr.name.escapeDart»"] != null) {
										for(var jsogObj in jsog["«attr.name.escapeDart»"]) {
											«attr.handleAttribute(g)»
										}
									}
								«ELSE»
									var jsogObj = jsog["«attr.name.escapeDart»"];
									«attr.handleAttribute(g)»
								«ENDIF»
							}
						«ENDFOR»
					}
				}
				
				@override
				Map toJSOG(Map cache) {
					Map map = new Map();
					if(cache.containsKey("«element.typeName»:${id}")){
						map['@ref']=cache["«element.typeName»:${id}"]['@id'];
						return map;
					}
					else{
						cache["«element.typeName»:${id}"]=(cache.length+1).toString();
						map['@id']=cache["«element.typeName»:${id}"];
						
						map['runtimeType']="«element.restFQN»";
						map['id']=id;
						cache["«element.typeName»:${id}"]=map;
						«FOR attr : allAttributes»
							map['«attr.name.escapeDart»']=«attr.name.escapeDart»==null?null:
							«IF attr.isList»
								«attr.name.escapeDart».map((n)=>«attr.serialize(g,'''n''')»).toList();
							«ELSE»
								«attr.serialize(g,'''«attr.name.escapeDart»''')»;
							«ENDIF»
						«ENDFOR»
					}
					return map;
				}
				
				@override
				String $type()=>"«element.typeName»";
				
				static «element.name» fromJSOG(dynamic jsog,Map cache) => new «element.dartFQN»(jsog: jsog,cache: cache);
				
				@override
				void merge(core.PyroElement ie,{bool structureOnly:false,Map cache}) {
					var elem = ie as «element.name»;
					if(cache==null){
						cache = new Map();
					}
					cache["«element.typeName»:${id}"]=this;
					«FOR attr : allAttributes»
						«attr.name.escapeDart» = elem.«attr.name.escapeDart»;
					«ENDFOR»
				}
				
				@override
				«element.name» propertyCopy({bool root:true}) {
					var elem = new «element.dartFQN»();
					elem.id = this.id;
					if(!root) {
						return elem;
					}
					«FOR attr:ownAttributes»
						elem.«attr.name.escapeDart» = this.«attr.name.escapeDart»;
					«ENDFOR»
					«FOR attr : ownReferences»
						if(this.«attr.name.escapeDart»!=null) {
							elem.«attr.name.escapeDart» = this.«attr.name.escapeDart»
							«IF attr.list»
								.map((n) => n.propertyCopy(root:false) as «attr.complexDartType»).toList();
							«ELSE»
								.propertyCopy(root:false) as «attr.complexDartType»;
							«ENDIF»
						}
					«ENDFOR»
					return elem;
				}
			«ELSE»
				Map toJSOG(Map cache);
			«ENDIF»
		}
		'''
	}
	
	def contentGraphmodel(MGLModel m,Styles styles) {
		val ecoreReferencedModels = gc.ecores // TODO: needs to be more precise
		val primeReferencedModels = m.primeReferencedElements.map[modelPackage].filter(MGLModel).toSet.filter[it !== m]
		'''
		import 'core.dart' as core;
		import 'dispatcher.dart';
		import 'dart:js' as js;
		
		import './«m.modelFile»' as «m.name.lowEscapeDart»;
		import 'command_graph.dart';
		«FOR gm:m.concreteGraphModels»
			import 'package:«gc.projectName.escapeDart»/«gm.commandGraphPath»';
		«ENDFOR»
		«FOR pr:ecoreReferencedModels»
			//prime referenced ecore «pr.name»
			import '«pr.modelFile»' as «pr.name.lowEscapeDart»;
		«ENDFOR»
		«FOR pr:primeReferencedModels»
			//prime referenced package «pr.name»
			import '«pr.modelFile»' as «pr.name.lowEscapeDart»;
		«ENDFOR»
		
		// GraphModels, Nodes, Edges, Container and UserDefinedTypes
		«m.elementsAndTypes.map[pyroElementClass(styles)].join("\n")»
		
		//Enumerations
		«FOR enu :m.enumerations»
			enum «enu.name.fuEscapeDart» {
				«FOR lit:enu.literals SEPARATOR ","»«lit.escapeDart»«ENDFOR»
			}
			
			class «enu.name.fuEscapeDart»Parser {
				static «enu.name.fuEscapeDart» fromJSOG(dynamic s){
					switch(s['literal']) {
						«FOR lit:enu.literals»
							case '«lit.toUnderScoreCase.escapeJava»':return «enu.name.fuEscapeDart».«lit.escapeDart»;
						«ENDFOR»
					}
					return «enu.name.fuEscapeDart».«enu.literals.get(0).escapeDart»;
				}
			
				static Map toJSOG(«enu.name.fuEscapeDart» e) {
					Map map = new Map();
					switch(e) {
						«FOR lit:enu.literals»
							case «enu.name.fuEscapeDart».«lit.escapeDart»:map['literal']='«lit.toUnderScoreCase.escapeJava»';break;
						«ENDFOR»
					}
					return map;
				}
			}
		«ENDFOR»
		'''
	} 

	def contentEcore(EPackage g) '''
		import 'core.dart' as core;
		import 'dispatcher.dart';
		import './«g.modelFile»' as «g.modelPackage.name.lowEscapeDart»;
		
		//Ecore package class
		
		class «g.name.fuEscapeDart» extends core.PyroModelFile implements core.PyroElement {
			«FOR c:g.EClassifiers.filter(EClass)»

				List<«c.dartFQN»> «c.name.lowEscapeDart»s = new List();
			«ENDFOR»
			
			List<core.PyroElement> getAll() {
				List<core.PyroElement> elements = new List();
				«FOR c:g.EClassifiers.filter(EClass)»
					elements.addAll(«c.name.lowEscapeDart»s);
				«ENDFOR»
				return elements;
			}
			
			static «g.name.fuEscapeDart» fromJSOG(dynamic jsog, Map cache) {
				var entity = new «g.dartFQN»();
				if (cache == null) {
					cache = new Map();
				}
				// default constructor
				if (jsog == null) {
					entity.id = -1;
					entity.filename = "";
					entity.extension = "ecore";
				}
				// from jsog
				else {
					String jsogId = jsog['@id'];
					cache[jsogId] = entity;
					entity.id = jsog['id'];
					entity.filename = jsog['filename'];
					entity.extension = jsog['extension'];
					// properties
					«FOR c:g.EClassifiers.filter(EClass).sortBy[name]»
						«{
							val subTypes = c.resolveSubTypesAndType.filter(EClass);
							'''
								if(jsog.containsKey('«c.name.lowEscapeJava»')) {
									for(var entry in jsog['«c.name.lowEscapeJava»']) {
										if(entry != null && entry.containsKey("__type")) {
											«FOR subType:subTypes SEPARATOR " else "
											»if(entry["__type"] == "«subType.typeName»") {
												entity.«c.name.lowEscapeDart»s.add(«subType.dartFQN».fromJSOG(entry,cache));
											}«
											ENDFOR»
										}
									}
								}
							'''
						}»
					«ENDFOR»
				}
				return entity;
			}
			
			@override
			String $type() => "«g.typeName»";
			
			@override
			void merge(core.PyroElement ie, {bool structureOnly: false, Map cache}) { }
			
			@override
			void mergeStructure(core.PyroFile pf) {
			    this.filename = pf.filename;
				this.extension = pf.extension;
			}
			
			@override
			Map toJSOG(Map cache) {
				return null;
			}
		}
		
		//Ecore classes of «g.name» package
		«g.EClassifiers.filter(EClass).map[pyroElementClass(g)].join("\n")»
		
		//Enumerations
		«FOR enu : g.EClassifiers.filter(EEnum)»
			enum «enu.name.fuEscapeDart» {
				«FOR lit:enu.ELiterals SEPARATOR ","»«lit.name.escapeDart»«ENDFOR»
			}
			
			class «enu.name.fuEscapeDart»Parser {
				
				static «enu.name.fuEscapeDart» fromJSOG(dynamic s) {
					switch(s['literal']) {
						«FOR lit:enu.ELiterals»
							case '«lit.name.escapeJava»':return «enu.name.fuEscapeDart».«lit.name.escapeDart»;
						«ENDFOR»
					}
					return «enu.name.fuEscapeDart».«enu.ELiterals.get(0).name.escapeDart»;
				}
			
				static Map toJSOG(«enu.name.fuEscapeDart» e) {
					Map map = new Map();
					switch(e) {
						«FOR lit:enu.ELiterals»
							case «enu.name.fuEscapeDart».«lit.name.escapeDart»:map['literal']='«lit.name.escapeJava»';break;
						«ENDFOR»
					}
					return map;
				}
			}
		«ENDFOR»
	'''
	
	def handleAttribute(EStructuralFeature attr, EPackage g) {
		'''
			if (jsogObj != null) {
				«IF attr.isPrimitive»
					«attr.primitiveDartType(g)» value«attr.name.escapeDart»;
					if (jsogObj != null) {
						«IF attr.EType.name.getEnum(g)!==null»
							value«attr.name.escapeDart» = «attr.EType.name.fuEscapeDart»Parser.fromJSOG(jsogObj as Map<String, dynamic>);
						«ELSE»
							value«attr.name.escapeDart» = jsogObj«attr.deserialize(g)» as «attr.primitiveDartType(g)»;
						«ENDIF»
					}
					«IF attr.list»
						this.«attr.name.escapeDart».add(value«attr.name.escapeDart»);
					«ELSE»
						this.«attr.name.escapeDart» = value«attr.name.escapeDart»;
					«ENDIF»
				«ELSE»
					«attr.complexDartType» value«attr.name.escapeDart»;
					String jsogId;
					if (jsogObj.containsKey('@ref')) {
						jsogId = jsogObj['@ref'];
					} else {
						jsogId = jsogObj['@id'];
					}
					if (cache.containsKey(jsogId)) {
						value«attr.name.escapeDart» = cache[jsogId];
					} else {
						if (jsogObj != null) {
							«{
								val subTypes = attr.EType.resolveSubTypesAndType
								'''
									«FOR subType : subTypes SEPARATOR " else "
									»if (jsogObj['__type'] == "«subType.typeName»") {
										value«attr.name.escapeDart» =
											new «subType.dartFQN»(cache: cache, jsog: jsogObj);
									}«
									ENDFOR»
								'''
							}»
							«»
							
						}
					}
					«IF attr.list»
						this.«attr.name.escapeDart».add(value«attr.name.escapeDart»);
					«ELSE»
						this.«attr.name.escapeDart» = value«attr.name.escapeDart»;
					«ENDIF»
				«ENDIF»
			}
		'''
	}
}
