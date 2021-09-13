import 'dart:async';

import 'package:angular_router/angular_router.dart';
import 'package:FlowGraphTool/src/filesupport/fileuploader.dart';
import '../model/core.dart';
import '../model/message.dart';
import '../model/command.dart';
import 'package:FlowGraphTool/src/pages/editor/canvas/canvas_component.dart';
import 'base_service.dart';
import 'package:FlowGraphTool/src/model/externallibrary.dart' as externallibrary;
import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
import 'package:FlowGraphTool/src/pages/editor/canvas/graphs/flowgraph/flowgraphdiagram_command_graph.dart' as flowgraphdiagramCG;

import 'dart:html' as html;
import 'dart:convert';

class GraphService extends BaseService {
	
	 CanvasComponent canvasComponent;
	
	 GraphService(Router router) : super(router);
	 
	 Map<int,StreamController> graphModelUpdate = new Map();
	 
	 Stream register(int id) {
	     graphModelUpdate[id] = new StreamController();
	     return graphModelUpdate[id].stream;
	 }
	 
	 void update(int id) {
	     if(graphModelUpdate.containsKey(id)) {
	       graphModelUpdate[id].add({});
	     } else {
	       print("NO UPDATE");
	     }
	 }
	 
	 Future<Message> sendMessage(Message m,String graphModelType,int graphModelId) async{
	    print("[SEND] message ${m}");
	    return html.HttpRequest.request("${getBaseUrl()}/${graphModelType}/message/${graphModelId.toString()}/private",
	    	sendData:m.toJSON(),
	    	method: "POST",
	    	requestHeaders: requestHeaders,
	    	withCredentials: true
	    ).then((response){
	      var p = Message.fromJSON(response.responseText);
	      print("[PYRO] send command ${p.messageType}");
	      return p;
	    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	 }
	 
	Future<Map> jumpToPrime(String graphModelType,String elementType,int graphModelId,int elementId) async{
      print("[SEND] jump to prime message");
      return html.HttpRequest.request("${getBaseUrl()}/${graphModelType}/jumpto/${graphModelId.toString()}/${elementId.toString()}/private",
	      	method: "GET",
	      	requestHeaders: requestHeaders,
	 		withCredentials: true
		).then((response){
	      Map m = jsonDecode(response.responseText);
	      return m;
	    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}

	
  Future<dynamic> removeGraph(GraphModel graph) async {
    return html.HttpRequest.request("${getBaseUrl()}/${graph.$lower_type()}/remove/${graph.id}/private",
    	method: "GET",
    	requestHeaders: requestHeaders,
    	withCredentials: true
    ).then((response){
      print("[PYRO] tried to remove modelFile ${graph.filename}");
	}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
  }

  Future<dynamic> generateGraph(GraphModel graph, String generatorId) async {
    return html.HttpRequest.request(
        "${getBaseUrl()}/${graph.$lower_type()}/generate/${graph.id}/${generatorId}/private",
        method: "GET",
        requestHeaders: requestHeaders,
        withCredentials: true);
  }
  
  Future<GraphModel> updateGraphModel(GraphModel graph) async {
    return html.HttpRequest.request("${getBaseUrl()}/graph/update/graphmodel/private",
    	sendData:jsonEncode(graph.toJSOG(new Map())),
    	method: "POST",
    	requestHeaders: requestHeaders,
    	withCredentials: true
    ).then((response){
      print("[PYRO] update graphmodel ${graph.filename}");
      return graph;
    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
  }
  
	
	Future<dynamic> loadCommandGraph(GraphModel graph,List<HighlightCommand> highlightings) async{
		if(graph == null) throw new Error();
		if(graph.$lower_type() == "flowgraphdiagram") {
			return loadCommandGraphFlowGraphDiagram(graph, highlightings);
		}
		      throw new Error();
	}
	
	/**
	 * METHODS FOR flowgraph.FlowGraphDiagram
	 */
	
	Future<Message> executeGraphmodelButtonFlowGraphDiagram(int id,flowgraph.FlowGraphDiagram graph,String key,List<HighlightCommand> highlightings) async {
		var data = {
			'fqn':null,
			'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
			'runtimeType':'info.scce.pyro.core.command.types.Action'
			 };
		return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/${graph.id}/button/${key}/${id}/trigger/private",
			sendData:jsonEncode(data),
			method: "POST",
			requestHeaders: requestHeaders,
			withCredentials: true
		).then((response){
		   return Message.fromJSON(response.responseText);
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	
	Future<Message> triggerPostSelectForFlowGraphDiagram(int id,flowgraph.FlowGraphDiagram graph,String fqn,List<HighlightCommand> highlightings) async {
	  var data = {
	  	'fqn':fqn,
	  	'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
	  	'runtimeType':'info.scce.pyro.core.command.types.Action'
	  };
	  return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/${graph.id}/psaction/${id}/trigger/private",
	  	  	  	sendData:jsonEncode(data),
	  	  	  	method: "POST",
	  	  	  	requestHeaders: requestHeaders,
	  	withCredentials: true
	  ).then((response){
	  	return Message.fromJSON(response.responseText);
	  }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
					  
	Future<flowgraph.FlowGraphDiagram> createFlowGraphDiagram(flowgraph.FlowGraphDiagram graph) async {
	    var data = {
	        'filename':graph.filename
	    };
	    return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/create/private",
		    	sendData:jsonEncode(data),
		    	method: "POST",
		  requestHeaders: requestHeaders,
		  withCredentials: true
		).then((response){
		       var newGraph = flowgraph.FlowGraphDiagram.fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
		       print("[PYRO] created FlowGraphDiagram ${graph.filename}");
		       graph.id=newGraph.id;
		       graph.merge(newGraph);
		       return graph;
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	
	Future<Map<String,String>> fetchCustomActionsForFlowGraphDiagram(int id,flowgraph.FlowGraphDiagram graph) async {
	    return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/customaction/${graph.id}/${id}/fetch/private",
	    	method: "GET",
	    	requestHeaders: requestHeaders,
			withCredentials: true
		).then((response){
	        Map<String, dynamic> map = jsonDecode(response.responseText);
	        Map<String, String> res = new Map();
	        map.forEach((k,v){res[k] = v.toString();});
	        print("[PYRO] fetched custom action for FlowGraphDiagram ${graph.filename}");
	        return res;
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	
	Future<Message> triggerCustomActionsForFlowGraphDiagram(int id,flowgraph.FlowGraphDiagram graph,String fqn,List<HighlightCommand> highlightings) async {
	  	var data = {
	  		'fqn':fqn,
	  		'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
	  		'runtimeType':'info.scce.pyro.core.command.types.Action'
	  		};
	  	return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/customaction/${graph.id}/${id}/trigger/private",
	  		sendData:jsonEncode(data),
	  		method: "POST",
	  		requestHeaders: requestHeaders,
		withCredentials: true
		).then((response){
			  	   return Message.fromJSON(response.responseText);
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	
	Future<Message> triggerDoubleClickActionsForFlowGraphDiagram(int id,flowgraph.FlowGraphDiagram graph,List<HighlightCommand> highlightings) async {
		var data = {
			'fqn':null,
			'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
			'runtimeType':'info.scce.pyro.core.command.types.Action'
		};
		return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/dbaction/${graph.id}/${id}/trigger/private",
			sendData:jsonEncode(data),
			method: "POST",
			requestHeaders: requestHeaders,
			withCredentials: true
		).then((response){
			return Message.fromJSON(response.responseText);
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	
	Future<flowgraphdiagramCG.FlowGraphDiagramCommandGraph> loadCommandGraphFlowGraphDiagram(flowgraph.FlowGraphDiagram graph,List<HighlightCommand> highlightings) async{
		return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/read/${graph.id}/private",
		  	method: "GET",
		  	requestHeaders: requestHeaders,
		withCredentials: true
		).then((response){
			var newGraph = flowgraph.FlowGraphDiagram.fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
			print("[PYRO] load flowgraphdiagram ${newGraph.filename}");
			graph.merge(newGraph);
			var cg = new flowgraphdiagramCG.FlowGraphDiagramCommandGraph(graph,highlightings);
			return cg;
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	Future<flowgraph.FlowGraphDiagram> loadGraphFlowGraphDiagram(int id) async{
		return html.HttpRequest.request("${getBaseUrl()}/flowgraphdiagram/read/${id}/private",
		  	method: "GET",
		  	requestHeaders: requestHeaders,
		withCredentials: true
		).then((response){
			print("[PYRO] load flowgraphdiagram ${id}");
			return flowgraph.FlowGraphDiagram.fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
		}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
	
	Future<externallibrary.ExternalLibrary> createexternalLibrary(externallibrary.ExternalLibrary ecore) async {
	    var data = {
	        'filename':ecore.filename
	    };
	    return html.HttpRequest.request("${getBaseUrl()}/externallibrary/create/private",
	    	sendData:jsonEncode(data),
	    	method: "POST",
	    	requestHeaders: requestHeaders,
	withCredentials: true
	).then((response){
	       var newEcore = externallibrary.ExternalLibrary.fromJSOG(jsonDecode(response.responseText),new Map());
	       print("[PYRO] created ExternalLibrary ${ecore.filename}");
	       ecore.id=newEcore.id;
	       return newEcore;
	}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
	}
}
