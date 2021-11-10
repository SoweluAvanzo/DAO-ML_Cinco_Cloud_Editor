package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.graphs.graphmodel

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.ComplexAttribute
import mgl.GraphModel
import mgl.ModelElement
import mgl.UserDefinedType

class GraphmodelTree extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameGraphmodelTree(GraphModel g) '''«g.treeFile»'''

	def elementProperties(ModelElement gme, GraphModel g) '''
		class «gme.name.fuEscapeDart»TreeNode extends TreeNode {
			String name;
			
			«gme.name.fuEscapeDart»TreeNode(IdentifiableElement root, «gme.dartFQN» element, {String this.name,TreeNode parent})  : super(root)
			{
				if(name==null){
					name = "«gme.name.fuEscapeDart»";
				}
				if(parent!=null){
					super.parent = parent;
				}
				delegate = element;
				«{
					val attributeExt = gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement].filter[!hidden].toList
					'''
						«FOR attr : attributeExt»
							«IF attr.isList»
								//complex list attributes of "«attr.name»"
								children.add(new «gme.name.fuEscapeJava»«attr.name.fuEscapeDart»TreeListNode(root,element.«attr.name.escapeDart»,name: "«attr.name.escapeDart»",parent: this));
							«ELSE»
								«{
									val subTypes = (attr as ComplexAttribute).getType().resolveSubTypesAndType
									'''
										«IF !subTypes.empty»
											//complex attributes of "«attr.name»"
											if(element.«attr.name.escapeDart» != null) {
												// resolving of type
												«FOR subType:subTypes SEPARATOR " else "
												»if(element.«attr.name.escapeDart».$type() == "«subType.typeName»") {
													children.add(new «subType.name.fuEscapeDart»TreeNode(root,element.«attr.name.escapeDart»,name: "«attr.name.escapeDart»",parent: this));
												}«
												ENDFOR»
											}
										«ENDIF»
									'''
								}»
							«ENDIF»
					    «ENDFOR»
					'''
				}»
			}
			«FOR attr : gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement]»
				
				bool canRemove«attr.name.escapeDart»() {
					«IF attr.readOnly»
						return false;
					«ELSE»
						«IF attr.isList»
							return delegate.«attr.name.escapeDart».length > «attr.lowerBound»;
						«ELSE»
							return true;
						«ENDIF»
					«ENDIF»
				}
			«ENDFOR»
			
			@override
			TreeNode createChildren(String child) {
				// for all complex not list attributes
				«FOR attr : gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement].filter[!isList] SEPARATOR " else "
				»«val subTypes = (attr as ComplexAttribute).getType().resolveSubTypesAndType»«FOR subType:subTypes SEPARATOR " else "
				»if(child == "«attr.name.escapeDart» as «subType.name.fuEscapeDart»") {
					print("«gme.name.fuEscapeDart» create children ${child}");
					//create pyro element
					var element = new «subType.dartFQN»();
					//create tree node «val treeNodeClass = '''«subType.name.fuEscapeDart»TreeNode'''»
					«treeNodeClass» node = new «treeNodeClass»(root,element,name:"«attr.name.escapeDart»",parent: this);
					// update business model;
					this.delegate.«attr.name.escapeDart» = element;
					//add to tree
					children.add(node);
					return node;
				}«
				ENDFOR»«
				ENDFOR»
				return null;
			}
			
			@override
			List<String> getPossibleChildren() {
				List<String> possibleElements = new List<String>();
				//for all complex not list attributes
				//check upper bound for single value
				«FOR attr : gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement].filter[!hidden].filter[!list]»
					«{
						val subTypes = (attr as ComplexAttribute).getType.resolveSubTypesAndType;
						'''
							«IF !subTypes.empty»
								// possible types for "«attr.name.escapeDart»"
								if(delegate.«attr.name.escapeDart»==null) {
									«FOR subType:subTypes»
										possibleElements.add("«attr.name.escapeDart» as «subType.name.fuEscapeDart»");
									«ENDFOR»
								}
							«ENDIF»
						'''
					}»
				«ENDFOR»
				return possibleElements;
			}
		  
			@override
			bool isChildRemovable(TreeNode node)
			{
				switch(node.name){
					«FOR attr : gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement].filter[isList]»
						case '«attr.name.escapeDart»':return false;
				    «ENDFOR»
				}
				return true;
			}
			
			@override
			bool isSelectable() => true;
			
			@override
			bool isRemovable() {
				return canRemove();
			}
			
			@override
			void removeAttribute(String name) {
				//for each complex not list attribute
				«val attributesExt_removeAttribute = gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement].filter[!list].toList»
				«FOR attr : attributesExt_removeAttribute SEPARATOR " else "
				»if(name == '«attr.name.escapeDart»') {
						delegate.«attr.name.escapeDart» = null;
				}«
				ENDFOR»
			}
		}
		«FOR attr:gme.attributesExtended.filter[!isPrimitive].filter[!isModelElement].filter[list]»
			«val subTypes = (attr as ComplexAttribute).getType().resolveSubTypesAndType»
			class «gme.name.fuEscapeJava»«attr.name.fuEscapeDart»TreeListNode extends TreeNode {
				String name;
							
				«gme.name.fuEscapeJava»«attr.name.fuEscapeDart»TreeListNode(
					IdentifiableElement root,
					List<«attr.dartFQN»> elements,
					{String this.name,TreeNode parent}
				) : super(root)
				{
				    if(name==null){
				      name = "«attr.name.fuEscapeDart»";
				    }
				    if(parent!=null){
				      super.parent = parent;
				    }
				    delegate = elements;
				    int i = 0;
					elements.forEach((n){
						«FOR subType:(attr as ComplexAttribute).getType.resolveSubTypesAndType SEPARATOR " else "
						»if(n.$type() == "«subType.typeName»") {
							children.add(new «subType.name.fuEscapeDart»TreeNode(root,n,name: "${i}",parent: this));
						}«
						ENDFOR»
						i++;
					});
				}
				
				@override
				bool isSelectable() => false;
				
				@override
				bool isRemovable() {
				    return false;
				}
				
				@override
				bool canRemove() {
					return false;
				}
				
				@override
				TreeNode createChildren(String child) {
					// for all complex not list attributes
					«FOR subType:subTypes SEPARATOR " else "
					»if(child == "«attr.name.escapeDart» as «subType.name.fuEscapeDart»") {
						print("«gme.name.fuEscapeDart» create children ${child}");
						
						//create pyro element
						var element = new «subType.dartFQN»();
						
						//create tree node «val treeNodeClass = '''«subType.name.fuEscapeDart»TreeNode'''»
						«treeNodeClass» node = new «treeNodeClass»(root,element,name:"${children.length}",parent: this);
						
						// update business model;
						this.delegate.add(element);
						
						//add to tree
						children.add(node);
						
						return node;
					}«
					ENDFOR»
					return null;
				}
				
				@override
				List<String> getPossibleChildren() {
					List<String> possibleElements = new List<String>();
					
					//for all complex not list attributes
					//check upper bound for single value
					«IF !subTypes.empty»
						// possible containment types for "«attr.name.escapeDart»"
						if(«IF attr.upperBound<=-1»true«ELSE»delegate.length < «attr.upperBound»«ENDIF»){
							«FOR subType:subTypes»
								possibleElements.add("«attr.name.escapeDart» as «subType.name.fuEscapeDart»");
							«ENDFOR»
						}
					«ENDIF»
					
					return possibleElements;
				}
				
				@override
				bool isChildRemovable(TreeNode node) {
					«IF attr.lowerBound>0»
						return (this.delegate.length > «attr.lowerBound»);
					«ELSE»
						return true;
					«ENDIF»
				}
				
				@override
				void removeAttribute(String name) {
					try{
					  var idx = int.parse(name);
					  delegate.removeAt(idx);
					} catch(e) {
					}
				}
			}
		«ENDFOR»
	'''

	def contentGraphmodelTree(GraphModel g) '''
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.modelPackage.name.lowEscapeDart»;
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
		import 'package:«gc.projectName.escapeDart»/src/model/tree_view.dart';
		
		class «g.name.fuEscapeDart»TreeBuilder
		{
			Tree getTree(IdentifiableElement element)
			{
				Tree tree = new Tree();
				//for every complex attribute
				//for every type
				if(element!=null) {
					//instanceofs
					if(element.$type() == "«g.typeName»"){
						tree.root = new «g.name.fuEscapeDart»TreeNode(element,element);
					}
					«val elements = g.elements.toList»
					«FOR elem : elements.filter[!isAbstract]»
						«IF elem.isIsAbstract»
							//«FOR subType:elem.name.subTypes(g).filter[!elements.contains(it)]»
							//	if(element.$type() == '«subType.typeName»'){
							//		tree.root = new «subType.name.fuEscapeDart»TreeNode(element,element);
							//	}
							//«ENDFOR»
						«ELSE»
							if(element.$type() == '«elem.typeName»'){
								tree.root = new «elem.name.fuEscapeDart»TreeNode(element,element);
							}
						«ENDIF»
					«ENDFOR»
				}
				return tree;
			}
		}
		
		/// node, edge, container, graphmodel type
		«g.elementProperties(g)»
		
		«g.elements.filter[!it.isIsAbstract].map[elementProperties(g)].join("\n")»
	'''
}
