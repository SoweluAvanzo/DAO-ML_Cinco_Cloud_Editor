import 'package:angular/angular.dart';
import 'dart:async';

import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/model/tree_view.dart';
import 'package:FlowGraphTool/src/service/graph_service.dart';
import 'package:FlowGraphTool/src/pages/editor/properties/tree/node/tree_node_component.dart';
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
import 'package:FlowGraphTool/src/pages/editor/properties/graphs/flowgraph/flowgraphdiagram_tree.dart' as flowgraphdiagramTB;


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
		if(currentGraphModel is flowgraph.FlowGraphDiagram){
			currentTree = new flowgraphdiagramTB.FlowGraphDiagramTreeBuilder().getTree(currentElement);
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
}
