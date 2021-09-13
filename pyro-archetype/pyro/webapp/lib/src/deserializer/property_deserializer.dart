import 'package:FlowGraphTool/src/model/core.dart' as core;
import 'flowgraph_property_deserializer.dart';

class PropertyDeserializer
{
  static core.IdentifiableElement deserialize(dynamic jsog, String graphModelType, Map cache)
  {
    //for each graphmodel
    if(graphModelType == 'FlowGraphDiagram' || graphModelType == 'flowgraphdiagram'){
    	return FlowGraphPropertyDeserializer.deserialize(jsog,cache);
    }
    return null;
  }
}
