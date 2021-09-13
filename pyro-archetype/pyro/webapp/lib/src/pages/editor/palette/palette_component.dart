import 'package:angular/angular.dart';

import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
import 'package:FlowGraphTool/src/pages/editor/palette/graphs/flowgraph/palette_builder.dart';

import 'package:FlowGraphTool/src/pages/editor/palette/list/list_view.dart';
import 'package:FlowGraphTool/src/pages/editor/palette/list/list_component.dart';
import 'package:FlowGraphTool/src/utils/graph_model_permission_utils.dart';

@Component(
    selector: 'palette',
    templateUrl: 'palette_component.html',
    directives: const [coreDirectives,ListComponent],
    styleUrls: const ['package:FlowGraphTool/src/pages/editor/editor_component.css']
)
class PaletteComponent implements OnInit, OnChanges {

  @Input()
  GraphModel currentGraphModel;
  
  @Input()
  List<PyroGraphModelPermissionVector> permissionVectors;

  List<MapList> map;
  bool canEdit;

  PaletteComponent() {
    permissionVectors = new List();
    map = new List<MapList>();
    canEdit = false;
  }

  @override
  void ngOnInit() {
    buildList();
  }

  void buildList() {
  	if(currentGraphModel!=null)
  	{
  		if(isFlowGraphDiagram(currentGraphModel)) {
  			map = FlowGraphDiagramPaletteBuilder.build(currentGraphModel);
  			//canEdit = GraphModelPermissionUtils.canUpdate("FLOW_GRAPH_DIAGRAM", permissionVectors);
  			canEdit = true;
  		}
    } else {
       map = null;
    }
  }
  
  /// check graph model type
  bool isFlowGraphDiagram(GraphModel graph) {
  	return graph.$type()=='flowgraph.FlowGraphDiagram';
  }

  @override
  ngOnChanges(Map<String, SimpleChange> changes) {
    buildList();
  }
}

