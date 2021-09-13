import 'dart:convert';
import 'dart:js' as js;
import 'dart:html';
import 'dispatcher.dart';

import '../filesupport/fileuploader.dart';

abstract class PyroElement {
  int id;

  Map toJSOG(Map cache);

  String $type();

  void merge(PyroElement ie,{bool structureOnly:false,Map cache});

  PyroElement({Map jsog,Map cache});
}

abstract class IdentifiableElement implements PyroElement{
  int id;
  bool $isDirty;

  IdentifiableElement propertyCopy();
  
  String $displayName();
}

abstract class ModelElement implements IdentifiableElement{
  int id;
  ModelElementContainer container;

  List<IdentifiableElement> allElements()
  {
    List<IdentifiableElement> list = new List();
    list.add(this);
    return list;
  }
  
  js.JsArray styleArgs();
  
  GraphModel getRootElememt() => container.getRootElememt();
  
  String $information();
  
  String $label();
  
  
}

abstract class ModelElementContainer implements IdentifiableElement{
  int id;
  List<ModelElement> modelElements;

  List<IdentifiableElement> allElements()
  {
      List<IdentifiableElement> list = new List();
      list.add(this);
      list.addAll(modelElements.expand((n) => n.allElements()));
      return list;
  }
  
  GraphModel getRootElememt();
}

abstract class Node implements ModelElement {
  int id;
  bool $isDirty;
  ModelElementContainer container;
  List<Edge> incoming;
  List<Edge> outgoing;
  int x;
  int y;
  int width;
  int height;
  int angle;

  @override
  GraphModel getRootElememt() => container.getRootElememt();
}

abstract class Container implements Node, ModelElementContainer {
  int id;
  bool $isDirty;
  ModelElementContainer container;
  List<Edge> incoming;
  List<Edge> outgoing;
  List<ModelElement> modelElements;
  int x;
  int y;
  int width;
  int height;
  int angle;

  @override
  GraphModel getRootElememt() => container.getRootElememt();
}

abstract class Edge implements ModelElement {
  int id;
  bool $isDirty;
  ModelElementContainer container;
  Node source;
  Node target;
  List<BendingPoint> bendingPoints;

  @override
  GraphModel getRootElememt() => container.getRootElememt();
}

abstract class GraphModel extends PyroModelFile
	implements ModelElementContainer {
  int id;
  bool $isDirty;
  int width;
  int height;
  bool isPublic = false;
  double scale;
  String router;
  String connector;
  List<ModelElement> modelElements;
  GlobalGraphModelSettings globalGraphModelSettings;

  void mergeStructure(PyroFile ie)
  {
  	var gm = ie as GraphModel;
    filename = gm.filename;
    width = gm.width;
    height = gm.height;
    scale = gm.scale;
    router = gm.router;
    connector = gm.connector;
    isPublic = gm.isPublic;
  }

  @override
  GraphModel getRootElememt() => this;
  
  String $lower_type();
  
  String $displayName();
}

class BendingPoint implements PyroElement{
  int id;
  int x;
  int y;
  BendingPoint({Map cache,dynamic jsog})
  {
    if(jsog!=null)
    {
      id = jsog["id"];
      x = jsog["x"];
      y = jsog["y"];

    }
    else{
      id=-1;
      x=0;
      y=0;
    }
  }
  @override
  Map toJSOG(Map cache)
  {
    Map map = new Map();
    if(cache.containsKey("core.BendingPoint:${id}")) {
      map['@ref']=cache["core.BendingPoint:${id}"];
      return map;
    }
    
    cache["core.BendingPoint:${id}"]=(cache.length+1).toString();
    map['@id']=cache["core.BendingPoint:${id}"];
    
    map['runtimeType']="info.scce.pyro.core.graphmodel.BendingPoint";
    map['id'] = id;
    map['x'] = x;
    map['y'] = y;
    return map;
  }


  BendingPoint fromJSOG(jsog, {Map cache}) {
    return new BendingPoint(cache: cache,jsog: jsog);
  }
  @override
  String $type() {
    return "core.BendingPoint";
  }
  @override
  void merge(PyroElement ie, {bool structureOnly: false, Map cache}) {
    // TODO: implement merge
  }
}

class LocalGraphModelSettings {
  int id;
  String router;
  String connector;

  List<PyroFile> openedFiles;
  
  LocalGraphModelSettings({Map cache,dynamic jsog})
  {
    openedFiles = new List<GraphModel>();
  	if(jsog!=null){
  		id = jsog["id"];
      router = jsog["router"];
      connector = jsog["connector"];
  		for(var g in jsog["openedFiles"]){
  			if(g.containsKey("@ref")){
  				openedFiles.add(cache[g["@ref"]]);
  			} else {
  		openedFiles.add( GraphModelDispatcher.dispatch(cache,g));
  	}
  		}
  	}
  	else{
  		id = -1;
  		   router = null;
  		   connector = "normal";
  		   openedFiles = new List<PyroFile>();
  	}
  }
  
  	static LocalGraphModelSettings fromJSOG(dynamic jsog){
  		return new LocalGraphModelSettings(cache:new Map(),jsog:jsog);
  	}
  	
  	Map toJSOG(Map cache){
  		Map jsog = new Map();
  		if(cache.containsKey("core.LocalGraphModelSettings:${id}")){
  	jsog["@ref"]=cache["core.LocalGraphModelSettings:${id}"];
  } else {
  	cache["core.LocalGraphModelSettings:${id}"]=(cache.length+1).toString();
  	jsog["@id"]=cache["core.LocalGraphModelSettings:${id}"];
  	jsog["id"]=id;
    jsog["connector"]=connector;
    jsog["router"]=router;
  	jsog["openedFiles"]=openedFiles.map((n)=>n.toJSOG(cache));
  }
  return jsog;
  	}
}

class GlobalGraphModelSettings {
  int id;
  GlobalGraphModelSettings({Map cache,dynamic jsog})
  {
    if(jsog!=null){
    	id = jsog["id"];
    } else {
    	id = -1;
    }
  }
  Map toJSOG(Map cache){
  	Map jsog = new Map();
  if(cache.containsKey("core.GlobalGraphModelSettings:${id}")){
  	jsog["@ref"]=cache["core.GlobalGraphModelSettings:${id}"];
  		} else {
  			cache["core.GlobalGraphModelSettings:${id}"]=(cache.length+1).toString();
  jsog["@id"]=cache["core.GlobalGraphModelSettings:${id}"];
  jsog["id"]=id;
  		}
  		return jsog;
	}
}

class PyroGraphModelType {
	static final String FLOW_GRAPH_DIAGRAM = "FLOW_GRAPH_DIAGRAM";
}		

class PyroCrudOperation {
  static final String CREATE = "CREATE";
  static final String READ = "READ";
  static final String UPDATE = "UPDATE";
  static final String DELETE = "DELETE";
}

class PyroGraphModelPermissionVector {
  int id;
  
  PyroUser user;
  String graphModelType;
  List<String> permissions;
  
  PyroGraphModelPermissionVector({Map cache,dynamic jsog}) {
    permissions = new List<String>();
    
    if(jsog != null) {
  	  cache[jsog["@id"]]=this;
      id = jsog["id"];
        	  
      
      if(jsog["user"].containsKey("@ref")){              
        user = cache[jsog["user"]["@ref"]];
      } else {
      	user = new PyroUser(cache:cache, jsog:jsog["user"]);
      }
      
      for(var value in jsog["permissions"]){  
	  	if (value == PyroCrudOperation.CREATE) {
	  	  permissions.add(PyroCrudOperation.CREATE);
	  	} else if (value == PyroCrudOperation.READ) {
	  	  permissions.add(PyroCrudOperation.READ);
	  	} else if (value == PyroCrudOperation.UPDATE) {
	  	  permissions.add(PyroCrudOperation.UPDATE);
	  	} else if (value == PyroCrudOperation.DELETE) {
	  	  permissions.add(PyroCrudOperation.DELETE);
	  	}
	  }
	  
	  if(jsog["graphModelType"] == PyroGraphModelType.FLOW_GRAPH_DIAGRAM) {
	    graphModelType = PyroGraphModelType.FLOW_GRAPH_DIAGRAM;
	  }
  	} else {
  	  id=-1;
      permissions = new List<String>();
  	}
  }
  
  static PyroGraphModelPermissionVector fromJSON(String s) {
    return fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroGraphModelPermissionVector fromJSOG({Map cache, dynamic jsog}) {
    return new PyroGraphModelPermissionVector(cache: cache, jsog: jsog);
  }

  String toJSON() {
    return jsonEncode(toJSOG(new Map()));
  }
  
  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if(cache.containsKey("core.PyroGraphModelPermissionVector:${id}")){
  		jsog["@ref"]=cache["core.PyroGraphModelPermissionVector:${id}"];
    } else {
    	cache["core.PyroGraphModelPermissionVector:${id}"]=(cache.length+1).toString();
    	jsog["@id"]=cache["core.PyroGraphModelPermissionVector:${id}"];
    	jsog["id"]=id;
  		jsog["user"]=user.toJSOG(cache);
  		jsog["permissions"]=permissions;
  		jsog["graphModelType"]=graphModelType;
  		jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroGraphModelPermissionVector";
    }
    return jsog;
  }
}



class PyroEditorGrid {
	int id;
	
	int userId;

	List<PyroEditorGridItem> items;
	List<PyroEditorWidget> availableWidgets;
	
	PyroEditorGrid({Map cache,dynamic jsog}) {
	  items = new List();
	  availableWidgets = new List();
		
	  if(jsog != null) {
		cache[jsog["@id"]]=this;
		id = jsog["id"];
		
		for(var value in jsog["availableWidgets"]){
		  if(value.containsKey("@ref")){
		    availableWidgets.add(cache[value["@ref"]]);
		  } else {
		    availableWidgets.add(new PyroEditorWidget(cache:cache,jsog:value));
		  }
		}
		
		for(var value in jsog["items"]){
		  if(value.containsKey("@ref")){
		    items.add(cache[value["@ref"]]);
		  } else {
		    items.add(new PyroEditorGridItem(cache:cache,jsog:value));
		  }
		}
	  	userId = jsog['userId'];
	  } else {
		id=-1;
	  }
	}
	
	static PyroEditorGrid fromJSON(String s) {
 	  return fromJSOG(cache: new Map(), jsog: jsonDecode(s));
	}
				
	static PyroEditorGrid fromJSOG({Map cache, dynamic jsog}) {
	  return new PyroEditorGrid(cache: cache, jsog: jsog);
	}
	
	String toJSON() {
	  return jsonEncode(toJSOG(new Map()));
	}
	
	Map toJSOG(Map cache) {
      Map jsog = new Map();
      if(cache.containsKey("core.PyroEditorGrid:${id}")){
  		jsog["@ref"]=cache["core.PyroEditorGrid:${id}"];
      } else {
    	cache["core.PyroEditorGrid:${id}"]=(cache.length+1).toString();
    	jsog["@id"]=cache["core.PyroEditorGrid:${id}"];
    	jsog["id"]=id;
  		jsog["userId"]=userId;
  		jsog["items"]=items.map((i) => i.toJSOG(cache)).toList();
  		jsog["availableWidgets"]=availableWidgets.map((i) => i.toJSOG(cache)).toList();
  		jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroEditorGrid";
      }
      return jsog;
    }
}

class PyroEditorWidget {
	int id;
	
	PyroEditorGrid grid;
	PyroEditorGridItem area;
	String tab;
	String key;
	int position;
	
	PyroEditorWidget({Map cache,dynamic jsog}) {
	  if(jsog != null) {
		cache[jsog["@id"]]=this;
		id = jsog["id"];
			
		tab = jsog["tab"];
		key = jsog["key"];
		position = jsog["position"];
			
		if (jsog["area"] != null) {
		  if(jsog["area"].containsKey("@ref")){
		    area = cache[jsog["area"]["@ref"]];
		  } else {
		    area = new PyroEditorGridItem(cache:cache, jsog:jsog["area"]);
		  }
		}
		
		if(jsog["grid"].containsKey("@ref")){
		  grid = cache[jsog["grid"]["@ref"]];
		} else {
		  grid = new PyroEditorGrid(cache:cache, jsog:jsog["grid"]);
		}
	  } else {
		id=-1;
	  }
	}
	
	static PyroEditorWidget fromJSON(String s) {
	  return fromJSOG(cache: new Map(), jsog: jsonDecode(s));
	}
	
	static PyroEditorWidget fromJSOG({Map cache, dynamic jsog}) {
	  return new PyroEditorWidget(cache: cache, jsog: jsog);
	}
	
	String toJSON() {
	  return jsonEncode(toJSOG(new Map()));
	}
	
	Map toJSOG(Map cache) {
      Map jsog = new Map();
      if(cache.containsKey("core.PyroEditorWidget:${id}")){
  		jsog["@ref"]=cache["core.PyroEditorWidget:${id}"];
      } else {
    	cache["core.PyroEditorWidget:${id}"]=(cache.length+1).toString();
    	jsog["@id"]=cache["core.PyroEditorWidget:${id}"];
    	jsog["id"]=id;
  		jsog["tab"]=tab;
  		jsog["key"]=key;
  		jsog["position"]=position;
  		jsog["area"]=area != null ? area.toJSOG(cache) : null;
  		jsog["grid"]=grid.toJSOG(cache);
  		jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroEditorWidget";
      }
      return jsog;
    }
}

class PyroEditorGridItem {
	int id;
	int x;
	int y;
	int width;
	int height;
	List<PyroEditorWidget> widgets;
	
	PyroEditorGridItem({Map cache,dynamic jsog}) {
	  widgets = new List();
      if(jsog != null) {
		cache[jsog["@id"]]=this;
		id = jsog["id"];
		
		x = jsog["x"];
		y = jsog["y"];
		width = jsog["width"];
		height = jsog["height"];
		
		for(var value in jsog["widgets"]){
		  if(value.containsKey("@ref")){
		    widgets.add(cache[value["@ref"]]);
		  } else {
		    widgets.add(new PyroEditorWidget(cache:cache,jsog:value));
		  }
		}
	  } else {
		id=-1;
	  }
	}
	
	List<PyroEditorWidget> get visibleWidgets => widgets.where((w) => w.area != null).toList();
	
	static PyroEditorGridItem fromJSON(String s) {
	  return fromJSOG(cache: new Map(), jsog: jsonDecode(s));
	}
	
	static PyroEditorGridItem fromJSOG({Map cache, dynamic jsog}) {
	  return new PyroEditorGridItem(cache: cache, jsog: jsog);
	}
	
	String toJSON() {
	  return jsonEncode(toJSOG(new Map()));
	}
	
	Map toJSOG(Map cache) {
      Map jsog = new Map();
      if(cache.containsKey("core.PyroEditorGridItem:${id}")){
  		jsog["@ref"]=cache["core.PyroEditorGridItem:${id}"];
      } else {
    	cache["core.PyroEditorGridItem:${id}"]=(cache.length+1).toString();
    	jsog["@id"]=cache["core.PyroEditorGridItem:${id}"];
    	jsog["id"]=id;
  		jsog["x"]=x;
  		jsog["y"]=y;
  		jsog["width"]=width;
  		jsog["height"]=height;
  		jsog["widgets"]=widgets.map((w) => w.toJSOG(cache)).toList();
  		jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroEditorGridItem";
      }
      return jsog;
    }
}





class PyroUser {
  int id;
  String username;
  String email;
  String emailHash;
  String profilePicture;
  
  
  var knownUsers;

  PyroUser({Map cache,dynamic jsog})
  {
    
    if(jsog!=null)
    {
      cache[jsog["@id"]]=this;
      id = jsog["id"];
      username = jsog["username"];
      email = jsog["email"];
      emailHash = jsog["emailHash"];
      
      if (jsog.containsKey("profilePicture") && jsog["profilePicture"] != null) {
	  	profilePicture = jsog["profilePicture"];
	  }
    }
    else{
      id=-1;
    }
  }

  static PyroUser fromJSON(String s)
  {
    return fromJSOG(new Map(),jsonDecode(s));
  }

  static PyroUser fromJSOG(Map cache,dynamic jsog)
  {
    return new PyroUser(cache: cache,jsog: jsog);
  }

  String toJSON()
  {
    return jsonEncode(toJSOG(new Map()));
  }

  Map toJSOG(Map cache)
  {
    Map jsog = new Map();
    if(cache.containsKey("core.PyroUser:${id}")){
  		jsog["@ref"]=cache["core.PyroUser:${id}"];
    } else {
    	cache["core.PyroUser:${id}"]=(cache.length+1).toString();
    	jsog["@id"]=cache["core.PyroUser:${id}"];
    	jsog["id"]=id;
  		jsog["username"]=username;
  		jsog["email"]=email;
  		jsog["emailHash"]=emailHash;
  		if (profilePicture != null) {
			jsog["profilePicture"]=profilePicture;
		}
  		jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroUser";
    }
    return jsog;
  }
}


class PyroStyle {
  int id;
  
  String navBgColor;
  String navTextColor;
  String bodyBgColor;
  String bodyTextColor;
  String primaryBgColor;
  String primaryTextColor;
  FileReference logo;
  
  PyroStyle({Map cache, dynamic jsog}) {
    if (jsog != null) {
  	  cache[jsog["@id"]]=this;
  	  id = jsog["id"];
  	  
  	  navBgColor = jsog["navBgColor"];
  	  navTextColor = jsog["navTextColor"];
  	  bodyBgColor = jsog["bodyBgColor"];
  	  bodyTextColor = jsog["bodyTextColor"];
  	  primaryBgColor = jsog["primaryBgColor"];
  	  primaryTextColor = jsog["primaryTextColor"];
  	  
  	  if (jsog.containsKey("logo") && jsog["logo"] != null) {
  	  	logo = new FileReference(jsog:jsog["logo"]);
  	  }
  	} else {
  	  id = -2;
  	}
  }
  
  static PyroStyle fromJSON(String s) {
    return PyroStyle.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroStyle fromJSOG({Map cache, dynamic jsog}) {
    return new PyroStyle(cache: cache, jsog: jsog);
  }
  
  Map toJSOG(Map cache) {
    Map jsog = new Map();
	if(cache.containsKey("core.PyroStyle:${id}")) {
	  jsog["@ref"]=cache["core.PyroStyle:${id}"];
	  return jsog;
	}
	cache["core.PyroStyle:${id}"] = (cache.length+1).toString();
	jsog['@id']=cache["core.PyroStyle:${id}"];
	
	jsog['id']=id;
	jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroStyle";
	jsog['navBgColor']=navBgColor;
	jsog['navTextColor']=navTextColor;
	jsog['bodyBgColor']=bodyBgColor;
	jsog['bodyTextColor']=bodyTextColor;
	jsog['primaryBgColor']=primaryBgColor;
	jsog['primaryTextColor']=primaryTextColor;
	if (logo != null) {
		jsog['logo']=logo.toJSOG(cache);
	}
	return jsog;
  }
}



class PyroSettings {
  int id;
  PyroStyle style;
  
  PyroSettings({Map cache, dynamic jsog}) {
  	if (jsog != null) {
  	  cache[jsog["@id"]]=this;
  	  id = jsog["id"];
  	    	  
  	  if (jsog.containsKey("style")) {
  	  	style = new PyroStyle(cache:cache, jsog:jsog["style"]);
  	  }
  	} else {
  	  id = -1;
	  style = new PyroStyle();
  	}
  }
  
  static PyroSettings fromJSON(String s) {
    return PyroSettings.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroSettings fromJSOG({Map cache, dynamic jsog}) {
    return new PyroSettings(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
	Map jsog = new Map();
	if(cache.containsKey("core.PyroSettings:${id}")){
		jsog["@ref"]=cache["core.PyroSettings:${id}"];
	} else {
		cache["core.PyroSettings:${id}"]=(cache.length+1).toString();
		jsog['@id']=cache["core.PyroSettings:${id}"];
		
		jsog['id']=id;
		jsog['style']=style.toJSOG(cache);
		jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroSettings";
	}
	return jsog;
  }
}


abstract class PyroFile {
  int id;
  String filename;
  String extension;
  
  Map toJSOG(Map cache);

  void mergeStructure(PyroFile pf);

  String $type() => "core.PyroFile";
  
  String getFullName() {
     return filename + (extension == null ? "" : "." + extension);
  }
}

abstract class PyroModelFile extends PyroFile {
 
}

