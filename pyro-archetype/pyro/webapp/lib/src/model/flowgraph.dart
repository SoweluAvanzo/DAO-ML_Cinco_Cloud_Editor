import 'core.dart' as core;
import 'dispatcher.dart';
import 'dart:js' as js;

import './flowgraph.dart' as flowgraph;
import '../../src/pages/editor/canvas/graphs/flowgraph/flowgraphdiagram_command_graph.dart';
//prime referenced ecore externalLibrary
import 'externallibrary.dart' as externallibrary;

// GraphModels, Nodes, Edges, Container and UserDefinedTypes
class End extends core.Node
{
	
	End({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			width = 36;
			height = 36;
			this.incoming = new List();
			this.outgoing = new List();
			this.x = 0;
			this.y = 0;
			// properties
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						  this.incoming.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
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
						  this.outgoing.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			
			// properties
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.End:${id}")){
			map['@ref']=cache["flowgraph.End:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.End:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.End:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.End";
			map['__type'] = "flowgraph.End";
			map['id'] = id;
			cache["flowgraph.End:${id}"] = map;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.End";
	
	static End fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.End(jsog: jsog,cache: cache);
	}
	
	@override
	End propertyCopy({bool root:true}) {
		var elem = new flowgraph.End();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		return elem;
	}
	
	@override
	void merge(covariant End elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"End";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as FlowGraphDiagram;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class Swimlane extends core.Container
{
	String actor = "";
	
	Swimlane({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			this.modelElements = new List();
			width = 400;
			height = 100;
			this.incoming = new List();
			this.outgoing = new List();
			this.x = 0;
			this.y = 0;
			// properties
			actor =
			"";
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						  this.incoming.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
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
						  this.outgoing.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			this.modelElements = new List();
			if (jsog.containsKey("a_modelElements")) {
				if(jsog["a_modelElements"]!=null){
					for(var v in jsog["a_modelElements"]){
						if(v.containsKey("@ref")){
							this.modelElements.add(cache[v['@ref']]);
						} else {
						  this.modelElements.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			
			// properties
			if (jsog.containsKey("c_actor")) {
				dynamic jsogObj = jsog["c_actor"];
				if (jsogObj != null) {
					String valueactor;
					if (jsogObj != null) {
						valueactor = jsogObj.toString();
					}
					this.actor = valueactor;
				}
			}
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.Swimlane:${id}")){
			map['@ref']=cache["flowgraph.Swimlane:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.Swimlane:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.Swimlane:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.Swimlane";
			map['__type'] = "flowgraph.Swimlane";
			map['id'] = id;
			cache["flowgraph.Swimlane:${id}"] = map;
			map['actor']=actor==null?null:
				actor;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.Swimlane";
	
	static Swimlane fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.Swimlane(jsog: jsog,cache: cache);
	}
	
	@override
	Swimlane propertyCopy({bool root:true}) {
		var elem = new flowgraph.Swimlane();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
	
		
		
		return elem;
	}
	
	@override
	void merge(covariant Swimlane elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
			modelElements.addAll(elem.modelElements.where((e)=>modelElements.where((m)=>m.id==e.id).isEmpty).toList());
		}
		
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
		actor = elem.actor;
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"Swimlane";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as FlowGraphDiagram;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		list.addAll(modelElements.expand((n) => n.allElements()));
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class SubFlowGraph extends core.Node
{
	flowgraph.FlowGraphDiagram subFlowGraph;
	
	SubFlowGraph({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			width = 96;
			height = 32;
			this.incoming = new List();
			this.outgoing = new List();
			this.x = 0;
			this.y = 0;
			// properties
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						  this.incoming.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
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
						  this.outgoing.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			if(jsog.containsKey("b_subFlowGraph")){
				if(jsog["b_subFlowGraph"]!=null){
					if(jsog["b_subFlowGraph"].containsKey("@ref")){
						this.subFlowGraph = cache[jsog["b_subFlowGraph"]["@ref"]];
					} else {
						this.subFlowGraph =
						GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog["b_subFlowGraph"]);
					}
				}
			}
			
			// properties
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.SubFlowGraph:${id}")){
			map['@ref']=cache["flowgraph.SubFlowGraph:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.SubFlowGraph:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.SubFlowGraph:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.SubFlowGraph";
			map['__type'] = "flowgraph.SubFlowGraph";
			map['id'] = id;
			cache["flowgraph.SubFlowGraph:${id}"] = map;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.SubFlowGraph";
	
	static SubFlowGraph fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.SubFlowGraph(jsog: jsog,cache: cache);
	}
	
	@override
	SubFlowGraph propertyCopy({bool root:true}) {
		var elem = new flowgraph.SubFlowGraph();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		return elem;
	}
	
	@override
	void merge(covariant SubFlowGraph elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
		
		if(!structureOnly){
			if(elem != null && elem.subFlowGraph == null) {
				subFlowGraph = elem.subFlowGraph;
			} else if(subFlowGraph != null){
				if(cache.containsKey("${subFlowGraph.id}")){
					subFlowGraph = cache["${subFlowGraph.id}"];
				} else {
					subFlowGraph.merge(elem.subFlowGraph, cache: cache, structureOnly: structureOnly);
				}
			} else {
				subFlowGraph = elem.subFlowGraph;
			}
		}
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"SubFlowGraph";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as FlowGraphDiagram;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class Transition extends core.Edge
{
	
	Transition({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			this.bendingPoints = new List();
			// properties
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						this.source = GraphModelDispatcher.dispatchFlowGraphModelElement(cache,j);
					}
				}
			}
			if(jsog.containsKey('a_targetElement')){
				if(jsog['a_targetElement']!=null){
					Map<String,dynamic> j = jsog['a_targetElement'];
					if(j.containsKey('@ref')){
						this.target = cache[j['@ref']];
					} else {
						this.target = GraphModelDispatcher.dispatchFlowGraphModelElement(cache,j);
					}
				}
			}
			
			// properties
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.Transition:${id}")){
			map['@ref']=cache["flowgraph.Transition:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.Transition:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.Transition:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.Transition";
			map['__type'] = "flowgraph.Transition";
			map['id'] = id;
			cache["flowgraph.Transition:${id}"] = map;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.Transition";
	
	static Transition fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.Transition(jsog: jsog,cache: cache);
	}
	
	@override
	Transition propertyCopy({bool root:true}) {
		var elem = new flowgraph.Transition();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		return elem;
	}
	
	@override
	void merge(covariant Transition elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"Transition";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as core.ModelElementContainer;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class Start extends core.Node
{
	
	Start({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			width = 36;
			height = 36;
			this.incoming = new List();
			this.outgoing = new List();
			this.x = 0;
			this.y = 0;
			// properties
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						  this.incoming.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
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
						  this.outgoing.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			
			// properties
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.Start:${id}")){
			map['@ref']=cache["flowgraph.Start:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.Start:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.Start:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.Start";
			map['__type'] = "flowgraph.Start";
			map['id'] = id;
			cache["flowgraph.Start:${id}"] = map;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.Start";
	
	static Start fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.Start(jsog: jsog,cache: cache);
	}
	
	@override
	Start propertyCopy({bool root:true}) {
		var elem = new flowgraph.Start();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		return elem;
	}
	
	@override
	void merge(covariant Start elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"Start";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as FlowGraphDiagram;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class Activity extends core.Node
{
	String name = "";
	String description = "";
	
	Activity({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			width = 96;
			height = 32;
			this.incoming = new List();
			this.outgoing = new List();
			this.x = 0;
			this.y = 0;
			// properties
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
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						  this.incoming.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
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
						  this.outgoing.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			
			// properties
			if (jsog.containsKey("c_description")) {
				dynamic jsogObj = jsog["c_description"];
				if (jsogObj != null) {
					String valuedescription;
					if (jsogObj != null) {
						valuedescription = jsogObj.toString();
					}
					this.description = valuedescription;
				}
			}
			if (jsog.containsKey("c_name")) {
				dynamic jsogObj = jsog["c_name"];
				if (jsogObj != null) {
					String valuename;
					if (jsogObj != null) {
						valuename = jsogObj.toString();
					}
					this.name = valuename;
				}
			}
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.Activity:${id}")){
			map['@ref']=cache["flowgraph.Activity:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.Activity:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.Activity:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.Activity";
			map['__type'] = "flowgraph.Activity";
			map['id'] = id;
			cache["flowgraph.Activity:${id}"] = map;
			map['name']=name==null?null:
				name;
			map['description']=description==null?null:
				description;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.Activity";
	
	static Activity fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.Activity(jsog: jsog,cache: cache);
	}
	
	@override
	Activity propertyCopy({bool root:true}) {
		var elem = new flowgraph.Activity();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
	
		
		
	
		
		
		return elem;
	}
	
	@override
	void merge(covariant Activity elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
		name = elem.name;
		description = elem.description;
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"Activity";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as FlowGraphDiagram;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class ExternalActivity extends core.Node
{
	externallibrary.ExternalActivity activity;
	
	ExternalActivity({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			width = 96;
			height = 32;
			this.incoming = new List();
			this.outgoing = new List();
			this.x = 0;
			this.y = 0;
			// properties
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						  this.incoming.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
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
						  this.outgoing.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			if(jsog.containsKey("b_activity")){
				if(jsog["b_activity"]!=null){
					if(jsog["b_activity"].containsKey("@ref")){
						this.activity = cache[jsog["b_activity"]["@ref"]];
					} else {
						this.activity =
						GraphModelDispatcher.dispatchEcoreElement(cache,jsog["b_activity"]);
					}
				}
			}
			
			// properties
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.ExternalActivity:${id}")){
			map['@ref']=cache["flowgraph.ExternalActivity:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.ExternalActivity:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.ExternalActivity:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.ExternalActivity";
			map['__type'] = "flowgraph.ExternalActivity";
			map['id'] = id;
			cache["flowgraph.ExternalActivity:${id}"] = map;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.ExternalActivity";
	
	static ExternalActivity fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.ExternalActivity(jsog: jsog,cache: cache);
	}
	
	@override
	ExternalActivity propertyCopy({bool root:true}) {
		var elem = new flowgraph.ExternalActivity();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
		return elem;
	}
	
	@override
	void merge(covariant ExternalActivity elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
		
		if(!structureOnly){
			if(elem != null && elem.activity == null) {
				activity = elem.activity;
			} else if(activity != null){
				if(cache.containsKey("${activity.id}")){
					activity = cache["${activity.id}"];
				} else {
					activity.merge(elem.activity, cache: cache, structureOnly: structureOnly);
				}
			} else {
				activity = elem.activity;
			}
		}
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"ExternalActivity";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as core.ModelElementContainer;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class LabeledTransition extends core.Edge
{
	String label = "";
	
	LabeledTransition({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			this.bendingPoints = new List();
			// properties
			label =
			"";
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			if(jsog.containsKey('a_container')){
				if(jsog['a_container']!=null){
					if(jsog['a_container'].containsKey('@ref')){
						this.container = cache[jsog['a_container']['@ref']];
					} else {
						this.container = GraphModelDispatcher.dispatchFlowGraphModelElementContainer(cache,jsog['a_container']);
					}
				}
			}
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
						this.source = GraphModelDispatcher.dispatchFlowGraphModelElement(cache,j);
					}
				}
			}
			if(jsog.containsKey('a_targetElement')){
				if(jsog['a_targetElement']!=null){
					Map<String,dynamic> j = jsog['a_targetElement'];
					if(j.containsKey('@ref')){
						this.target = cache[j['@ref']];
					} else {
						this.target = GraphModelDispatcher.dispatchFlowGraphModelElement(cache,j);
					}
				}
			}
			
			// properties
			if (jsog.containsKey("c_label")) {
				dynamic jsogObj = jsog["c_label"];
				if (jsogObj != null) {
					String valuelabel;
					if (jsogObj != null) {
						valuelabel = jsogObj.toString();
					}
					this.label = valuelabel;
				}
			}
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.LabeledTransition:${id}")){
			map['@ref']=cache["flowgraph.LabeledTransition:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.LabeledTransition:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.LabeledTransition:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.LabeledTransition";
			map['__type'] = "flowgraph.LabeledTransition";
			map['id'] = id;
			cache["flowgraph.LabeledTransition:${id}"] = map;
			map['label']=label==null?null:
				label;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.LabeledTransition";
	
	static LabeledTransition fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.LabeledTransition(jsog: jsog,cache: cache);
	}
	
	@override
	LabeledTransition propertyCopy({bool root:true}) {
		var elem = new flowgraph.LabeledTransition();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
	
		
		
		return elem;
	}
	
	@override
	void merge(covariant LabeledTransition elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
		
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
		label = elem.label;
	}
	
	@override
	js.JsArray styleArgs() {
		try {
	    	return new js.JsArray.from([]);
	    } catch(e) {
	    	return new js.JsArray.from([]);
	    }
	}
	
	
	@override
	String $displayName() =>"LabeledTransition";
	
	@override
	String $information() {
		return null;
	}
	
	@override
	String $label() {
		return null;
	}
	
	@override
	core.ModelElementContainer get container => super.container as core.ModelElementContainer;
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => container.getRootElememt();
}

class FlowGraphDiagram extends core.GraphModel
{
	FlowGraphDiagramCommandGraph commandGraph;
	int width;
	int height;
	double scale;
	String router;
	String connector;
	bool isPublic;
	String modelName = "";
	
	FlowGraphDiagram({Map cache, dynamic jsog}) {
		if (cache == null) {
			cache = new Map();
		}
		
		// default constructor
		if (jsog == null) {
			this.id = -1;
			this.modelElements = new List();
			this.width = 1000;
			this.height = 600;
			this.scale = 1.0;
			this.router = null;
			this.connector = 'normal';
			this.filename = '';
			this.isPublic = false;
			this.extension = 'flowgraph';
			// properties
			modelName =
			"";
		}
		// from jsog
		else {
			String jsogId = jsog['@id'];
			cache[jsogId] = this;
			
			this.id = jsog['id'];
			this.width = jsog['a_width'];
			this.height = jsog['a_height'];
			this.scale = jsog['a_scale'];
			this.router = jsog['a_router'];
			this.connector = jsog['a_connector'];
			this.filename = jsog['filename'];
			this.extension = jsog['extension'];
			this.isPublic = jsog['isPublic'];
			this.modelElements = new List();
			if (jsog.containsKey("a_modelElements")) {
				if(jsog["a_modelElements"]!=null){
					for(var v in jsog["a_modelElements"]){
						if(v.containsKey("@ref")){
							this.modelElements.add(cache[v['@ref']]);
						} else {
						  this.modelElements.add(GraphModelDispatcher.dispatchFlowGraphModelElement(cache,v));
						}
					}
				}
			}
			
			// properties
			if (jsog.containsKey("c_modelName")) {
				dynamic jsogObj = jsog["c_modelName"];
				if (jsogObj != null) {
					String valuemodelName;
					if (jsogObj != null) {
						valuemodelName = jsogObj.toString();
					}
					this.modelName = valuemodelName;
				}
			}
		}
		
	}
	
	Map toJSOG(Map cache) {
		Map map = new Map();
		
		if(cache.containsKey("flowgraph.FlowGraphDiagram:${id}")){
			map['@ref']=cache["flowgraph.FlowGraphDiagram:${id}"]['@id'];
			return map;
		}
		else {
			cache["flowgraph.FlowGraphDiagram:${id}"]=(cache.length+1).toString();
			map['@id'] = cache["flowgraph.FlowGraphDiagram:${id}"];
			
			map['runtimeType'] = "info.scce.pyro.flowgraph.rest.FlowGraphDiagram";
			map['__type'] = "flowgraph.FlowGraphDiagram";
			map['id'] = id;
			map['width'] = this.width;
			map['height'] = this.height;
			map['scale'] = this.scale;
			map['router'] = this.router;
			map['connector'] = this.connector;
			map['filename'] = this.filename;
			map['extension'] = this.extension;
			cache["flowgraph.FlowGraphDiagram:${id}"] = map;
			map['modelName']=modelName==null?null:
				modelName;
		}
		return map;
	}
	
	@override
	String $type()=>"flowgraph.FlowGraphDiagram";
	
	@override
	String $lower_type()=>"flowgraphdiagram";
	
	static FlowGraphDiagram fromJSOG(dynamic jsog, Map cache)
	{
		return new flowgraph.FlowGraphDiagram(jsog: jsog,cache: cache);
	}
	
	@override
	FlowGraphDiagram propertyCopy({bool root:true}) {
		var elem = new flowgraph.FlowGraphDiagram();
		elem.id = this.id;
		if(!root) {
			return elem;
		}
	
		
		
		return elem;
	}
	
	@override
	void merge(covariant FlowGraphDiagram elem,{bool structureOnly:false,covariant Map<String,core.PyroElement> cache}) {
		if(cache==null){
			cache = new Map();
		}
		cache["${elem.id}"]=this;
		this.id = elem.id;
			
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
			modelElements.addAll(elem.modelElements.where((e)=>modelElements.where((m)=>m.id==e.id).isEmpty).toList());
		}
		modelName = elem.modelName;
	}
	
	
	@override
	String $displayName() =>"FlowGraphDiagram";
	
	@override
	List<core.IdentifiableElement> allElements()
	{
		List<core.IdentifiableElement> list = new List();
		list.add(this);
		list.addAll(modelElements.expand((n) => n.allElements()));
		return list;
	}
	
	@override
	core.GraphModel getRootElememt() => this;
}

//Enumerations
