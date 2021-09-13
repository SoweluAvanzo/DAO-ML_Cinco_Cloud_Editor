
import 'package:FlowGraphTool/src/pages/editor/palette/list/list_view.dart';
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;


class FlowGraphDiagramPaletteBuilder {

  static List<MapList> build(flowgraph.FlowGraphDiagram graph)
  {
    List<MapList> paletteMap = new List();
	paletteMap.add(new MapList('Round Elements',values: [
		new MapListValue('End',instance: new flowgraph.End(), identifier: "flowgraph.End",imgPath:'img/flowgraph/End.png'),
		new MapListValue('Start',instance: new flowgraph.Start(), identifier: "flowgraph.Start",imgPath:'img/flowgraph/Start.png')
	]));
	paletteMap.add(new MapList('Rectangular Elements',values: [
		new MapListValue('Swimlane',instance: new flowgraph.Swimlane(), identifier: "flowgraph.Swimlane",imgPath:'img/flowgraph/Swimlane.png'),
		new MapListValue('Activity',instance: new flowgraph.Activity(), identifier: "flowgraph.Activity",imgPath:'img/flowgraph/Activity.png')
	]));
    return paletteMap;
  }
}

