package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.tree

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class TreeComponet extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameTreeComponent()'''tree_component.dart'''
	
	def contentTreeComponent()
	'''
	import 'package:angular/angular.dart';
	import 'dart:async';
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/model/tree_view.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/properties/tree/node/tree_node_component.dart';
	«FOR m:gc.mglModels»
		import 'package:«gc.projectName.escapeDart»/«m.modelFilePath»' as «m.name.lowEscapeDart»;
	«ENDFOR»
	«FOR g:gc.discreteGraphModels»
		import 'package:«gc.projectName.escapeDart»/«g.treeFilePath»' as «g.name.lowEscapeDart»TB;
	«ENDFOR»
	
	@Component(
	    selector: 'tree',
	    templateUrl: 'tree_component.html',
	    directives: const [coreDirectives,TreeNodeComponent]
	)
	class TreeComponent implements OnInit, OnChanges {

		final hasChangedSC = new StreamController();
		@Output() Stream get hasChanged => hasChangedSC.stream;
		
		final hasRemovedSC = new StreamController();
		@Output() Stream get hasRemoved => hasRemovedSC.stream;
		
		final hasSelectedSC = new StreamController();
		@Output() Stream get hasSelected => hasSelectedSC.stream;
		
		@Input()
		IdentifiableElement currentElement;
		@Input()
		GraphModel currentGraphModel;
		@Input()
		bool isModal = false;
		
		Tree currentTree;
		
		final GraphService _graphService;
		
		TreeComponent(this._graphService){}
		
		@override
		void ngOnInit()
		{
			init();
		}
		
		@override
		ngOnChanges(Map<String, SimpleChange> changes) {
			init();
		}

		void init() {
			if(currentGraphModel!=null&&currentElement!=null);
			{
			  buildTree();
			}
		}
		
		void buildTree() {
			var newTree = null;
			«FOR g:gc.discreteGraphModels SEPARATOR " else "
			»if(currentGraphModel.$type() == "«g.typeName»") {
				newTree = new «g.name.lowEscapeDart»TB.«g.name.fuEscapeDart»TreeBuilder().getTree(currentElement);
			}«
			ENDFOR»
			if(currentTree == null || newTree == null || !currentTree.root.equals(newTree.root)) {
				currentTree = newTree;
			} else {
				currentTree.root.merge(newTree.root, new Map(), new Set());
			}
		}
		
		void hasNew(TreeNode node)
		{
			hasChangedSC.add(node);
		}
		
		void hasDeleted(TreeNode node)
		{
			print(node.delegate);
			var delegate = node.delegate;
			node.delegate = null;
			hasRemovedSC.add(delegate);
		}
		
		void rebuild() {
			init();
		}
	}
	'''
}
