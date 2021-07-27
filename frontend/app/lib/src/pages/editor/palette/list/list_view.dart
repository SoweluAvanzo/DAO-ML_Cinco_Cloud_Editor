	
import '../../../../model/core.dart';

class MapList {
  String name;
  List<MapListValue> values;
  bool open = true;

  MapList(String this.name,{List<MapListValue> this.values})
  {

  }
}

class MapListValue {
  String name;
  String identifier;
  String imgPath;
  Node instance;

  MapListValue(String this.name,{Node this.instance, String this.identifier,String this.imgPath})
  {

  }
}

