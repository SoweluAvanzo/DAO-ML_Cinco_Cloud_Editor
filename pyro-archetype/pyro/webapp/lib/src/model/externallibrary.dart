import 'core.dart' as core;
import 'dispatcher.dart';
import './externallibrary.dart' as externallibrary;

//Ecore package class

class ExternalLibrary extends core.PyroModelFile implements core.PyroElement {

	List<externallibrary.ExternalActivityLibrary> externalactivitylibrarys = new List();

	List<externallibrary.ExternalActivity> externalactivitys = new List();
	
	List<core.PyroElement> getAll() {
		List<core.PyroElement> elements = new List();
		elements.addAll(externalactivitylibrarys);
		elements.addAll(externalactivitys);
		return elements;
	}
	
	static ExternalLibrary fromJSOG(dynamic jsog, Map cache) {
		var entity = new externallibrary.ExternalLibrary();
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
			if(jsog.containsKey('externalactivity')) {
				for(var entry in jsog['externalactivity']) {
					if(entry != null && entry.containsKey("__type")) {
						if(entry["__type"] == "externallibrary.ExternalActivity") {
							entity.externalactivitys.add(externallibrary.ExternalActivity.fromJSOG(entry,cache));
						}
					}
				}
			}
			if(jsog.containsKey('externalactivitylibrary')) {
				for(var entry in jsog['externalactivitylibrary']) {
					if(entry != null && entry.containsKey("__type")) {
						if(entry["__type"] == "externallibrary.ExternalActivityLibrary") {
							entity.externalactivitylibrarys.add(externallibrary.ExternalActivityLibrary.fromJSOG(entry,cache));
						}
					}
				}
			}
		}
		return entity;
	}
	
	@override
	String $type() => "externallibrary.ExternalLibrary";
	
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

//Ecore classes of externalLibrary package
class ExternalActivityLibrary extends core.PyroElement {
	
	//attributes
	List<ExternalActivity> activities = new List();
	
	ExternalActivityLibrary({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		// default constructor
		if (jsog == null) {
			this.id = -1;
		activities =
		new List();
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			// properties
			if (jsog.containsKey("activities")) {
				this.activities = new List();
				for(var jsogObj in jsog["activities"]) {
				if (jsogObj != null) {
					ExternalActivity valueactivities;
					String jsogId;
					if (jsogObj.containsKey('@ref')) {
						jsogId = jsogObj['@ref'];
					} else {
						jsogId = jsogObj['@id'];
					}
					if (cache.containsKey(jsogId)) {
						valueactivities = cache[jsogId];
					} else {
						if (jsogObj != null) {
							if (jsogObj['__type'] == "externallibrary.ExternalActivity") {
								valueactivities =
									new externallibrary.ExternalActivity(cache: cache, jsog: jsogObj);
							}
							
							
						}
					}
					this.activities.add(valueactivities);
				}
			}
			}
		}
	}
	
	@override
	Map toJSOG(Map cache) {
		Map map = new Map();
		if(cache.containsKey("externallibrary.ExternalActivityLibrary:${id}")){
			map['@ref']=cache["externallibrary.ExternalActivityLibrary:${id}"]['@id'];
			return map;
		}
		else{
			cache["externallibrary.ExternalActivityLibrary:${id}"]=(cache.length+1).toString();
			map['@id']=cache["externallibrary.ExternalActivityLibrary:${id}"];
			
			map['runtimeType']="info.scce.pyro.externallibrary.rest.ExternalActivityLibrary";
			map['id']=id;
			cache["externallibrary.ExternalActivityLibrary:${id}"]=map;
			map['activities']=activities==null?null:
			activities.map((n)=>n.toJSOG(cache)).toList();
		}
		return map;
	}
	
	@override
	String $type()=>"externallibrary.ExternalActivityLibrary";
	
	static ExternalActivityLibrary fromJSOG(dynamic jsog,Map cache) => new externallibrary.ExternalActivityLibrary(jsog: jsog,cache: cache);
	
	@override
	void merge(core.PyroElement ie,{bool structureOnly:false,Map cache}) {
		var elem = ie as ExternalActivityLibrary;
		if(cache==null){
			cache = new Map();
		}
		cache["externallibrary.ExternalActivityLibrary:${id}"]=this;
		activities = elem.activities;
	}
	
	@override
	ExternalActivityLibrary propertyCopy({bool root:true}) {
		var elem = new externallibrary.ExternalActivityLibrary();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		if(this.activities!=null) {
			elem.activities = this.activities
			.map((n) => n.propertyCopy(root:false) as ExternalActivity).toList();
		}
		return elem;
	}
}

class ExternalActivity extends core.PyroElement {
	
	//attributes
	String name;
	String description;
	
	ExternalActivity({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		// default constructor
		if (jsog == null) {
			this.id = -1;
		name =
		"";
		description =
		"";
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			// properties
			if (jsog.containsKey("description")) {
				var jsogObj = jsog["description"];
				if (jsogObj != null) {
					String valuedescription;
					if (jsogObj != null) {
						valuedescription = jsogObj.toString() as String;
					}
					this.description = valuedescription;
				}
			}
			if (jsog.containsKey("name")) {
				var jsogObj = jsog["name"];
				if (jsogObj != null) {
					String valuename;
					if (jsogObj != null) {
						valuename = jsogObj.toString() as String;
					}
					this.name = valuename;
				}
			}
		}
	}
	
	@override
	Map toJSOG(Map cache) {
		Map map = new Map();
		if(cache.containsKey("externallibrary.ExternalActivity:${id}")){
			map['@ref']=cache["externallibrary.ExternalActivity:${id}"]['@id'];
			return map;
		}
		else{
			cache["externallibrary.ExternalActivity:${id}"]=(cache.length+1).toString();
			map['@id']=cache["externallibrary.ExternalActivity:${id}"];
			
			map['runtimeType']="info.scce.pyro.externallibrary.rest.ExternalActivity";
			map['id']=id;
			cache["externallibrary.ExternalActivity:${id}"]=map;
			map['name']=name==null?null:
			name;
			map['description']=description==null?null:
			description;
		}
		return map;
	}
	
	@override
	String $type()=>"externallibrary.ExternalActivity";
	
	static ExternalActivity fromJSOG(dynamic jsog,Map cache) => new externallibrary.ExternalActivity(jsog: jsog,cache: cache);
	
	@override
	void merge(core.PyroElement ie,{bool structureOnly:false,Map cache}) {
		var elem = ie as ExternalActivity;
		if(cache==null){
			cache = new Map();
		}
		cache["externallibrary.ExternalActivity:${id}"]=this;
		name = elem.name;
		description = elem.description;
	}
	
	@override
	ExternalActivity propertyCopy({bool root:true}) {
		var elem = new externallibrary.ExternalActivity();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		elem.name = this.name;
		elem.description = this.description;
		return elem;
	}
}

//Enumerations
