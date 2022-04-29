package de.jabc.cinco.meta.plugin.pyro.frontend.service

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.core.utils.CincoUtil

class GraphService extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameGraphServcie() '''graph_service.dart'''

	def contentGraphService() '''
		import 'dart:async';
		
		import 'package:angular_router/angular_router.dart';
		import 'package:«gc.projectName.escapeDart»/src/filesupport/fileuploader.dart';
		import '../model/core.dart';
		import '../model/message.dart';
		import '../model/command.dart';
		import 'package:«gc.projectName.escapeDart»/src/pages/editor/canvas/canvas_component.dart';
		import 'base_service.dart';
		«FOR g : gc.ecores»
			import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
		«ENDFOR»
		«FOR g : gc.mglModels»
			import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
		«ENDFOR»
		«FOR g : gc.concreteGraphModels»
			import 'package:«gc.projectName.escapeDart»/«g.commandGraphPath»' as «g.name.lowEscapeDart»CG;
		«ENDFOR»
		
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
				var graphModelEndpoint = getGraphModelEndpoint(graphModelType);
				print("[SEND] message ${m}");
				return html.HttpRequest.request("${getBaseUrl()}/${graphModelEndpoint}/message/${graphModelId.toString()}/private",
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
			 var graphModelEndpoint = getGraphModelEndpoint(graphModelType);
			 print("[SEND] jump to prime message");
			 return html.HttpRequest.request("${getBaseUrl()}/${graphModelEndpoint}/jumpto/${graphModelId.toString()}/${elementId.toString()}/private",
			      	method: "GET",
			      	requestHeaders: requestHeaders,
			 		withCredentials: true
			 ).then((response){
			      Map m = jsonDecode(response.responseText);
			      return m;
			 }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
		
			
		  Future<dynamic> removeGraph(GraphModel graph) async {
		  	var graphModelEndpoint = getGraphModelEndpoint(graph.$type());
		    return html.HttpRequest.request("${getBaseUrl()}/${graphModelEndpoint}/remove/${graph.id}/private",
		    	method: "GET",
		    	requestHeaders: requestHeaders,
		    	withCredentials: true
		    ).then((response){
		      print("[PYRO] tried to remove modelFile ${graph.filename}");
			}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
		  }
		
		  Future<dynamic> generateGraph(GraphModel graph, String generatorId) async {
		  	var graphModelEndpoint = getGraphModelEndpoint(graph.$type());
		    return html.HttpRequest.request(
		        "${getBaseUrl()}/${graphModelEndpoint}/generate/${graph.id}/${generatorId}/private",
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
				«FOR g : gc.concreteGraphModels SEPARATOR " else "»if(graph.$type() == "«g.typeName»") {
					return loadCommandGraph«g.name.fuEscapeDart»(graph, highlightings);
				}«ENDFOR»
				      throw new Error();
			}
			
			/**
			 * load the model by either the type or the fileExtension.
			 * Occurence of graphModels is sorted by typeName (packageName + ModelName).
			 */
			Future<dynamic> loadGraphModel(String typeOrExtension, int modelId) {
				«FOR g : gc.concreteGraphModels SEPARATOR " else "
				»if(
					typeOrExtension == "«g.fileExtension»"
					|| typeOrExtension == "«g.typeName»"
				) {
					return loadGraph«g.name.fuEscapeDart»(modelId);
				}«
				ENDFOR»
		    	throw new Error();
			}
			
			Future<Message> loadAppearance(GraphModel g) async {
				if(g == null)
					return null;
				var id = g.id;
				var type = this.getGraphModelEndpoint(g.$type());
				return html.HttpRequest.request("${getBaseUrl()}/${type}/appearance/${id}/private",
					method: "GET",
					requestHeaders: requestHeaders,
					withCredentials: true
				).then((response){
					var p = Message.fromJSON(response.responseText);
					print("[PYRO] load appearance for ${id}");
					return p;
				}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
			
			/**
			 * checks if the given type or fileExtension is related to a GraphModel.
			 * Occurence of graphModels is sorted by typeName (packageName + ModelName).
			 */
			bool isGraphModel(String typeOrExtension) {
				«FOR g : gc.concreteGraphModels SEPARATOR " else "
				»if(
					typeOrExtension == "«g.fileExtension»"
					|| typeOrExtension == "«g.typeName»"
				) {
					return true;
				}«
				ENDFOR»
		    	return false;
			}
			«FOR m : gc.mglModels»
					«{
						val styles = CincoUtil.getStyles(m)
						'''
							«FOR g:m.concreteGraphModels»
								
								/**
								 * METHODS FOR «g.dartFQN»
								 */
								
								«IF g.hasAppearanceProvider(styles)»
									Future<String> appearances«g.name.fuEscapeDart»(«g.dartFQN» graph) async{
										return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/appearance/${graph.id}/private",
											method: "GET",
											requestHeaders: requestHeaders,
											withCredentials: true
										).then((response){
											print("[PYRO] load «g.name.lowEscapeDart» appearance proiveders ${graph.filename}");
											return response.responseText;
										}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
									}
									
								«ENDIF»
								
								Future<Message> executeGraphmodelButton«g.name.escapeDart»(int id,«g.dartFQN» graph,String key,List<HighlightCommand> highlightings) async {
									var data = {
										'fqn':null,
										'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
										'runtimeType':'info.scce.pyro.core.command.types.Action'
										 };
									return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/${graph.id}/button/${key}/${id}/trigger/private",
										sendData:jsonEncode(data),
										method: "POST",
										requestHeaders: requestHeaders,
										withCredentials: true
									).then((response){
									   return Message.fromJSON(response.responseText);
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								
								Future<Message> triggerPostSelectFor«g.name.escapeDart»(int id,«g.dartFQN» graph,String fqn,List<HighlightCommand> highlightings) async {
								  var data = {
								  	'fqn':fqn,
								  	'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
								  	'runtimeType':'info.scce.pyro.core.command.types.Action'
								  };
								  return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/${graph.id}/psaction/${id}/trigger/private",
								  	  	  	sendData:jsonEncode(data),
								  	  	  	method: "POST",
								  	  	  	requestHeaders: requestHeaders,
								  	withCredentials: true
								  ).then((response){
								  	return Message.fromJSON(response.responseText);
								  }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
												  
								Future<«g.dartFQN»> create«g.name.escapeDart»(«g.dartFQN» graph) async {
								    var data = {
								        'filename':graph.filename
								    };
								    return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/create/private",
									    	sendData:jsonEncode(data),
									    	method: "POST",
									  requestHeaders: requestHeaders,
									  withCredentials: true
									).then((response){
									       var newGraph = «g.dartFQN».fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
									       print("[PYRO] created «g.name.fuEscapeDart» ${graph.filename}");
									       graph.id=newGraph.id;
									       graph.merge(newGraph);
									       return graph;
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								
								Future<Map<String,String>> fetchCustomActionsFor«g.name.escapeDart»(int id,«g.dartFQN» graph) async {
								    return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/customaction/${graph.id}/${id}/fetch/private",
								    	method: "GET",
								    	requestHeaders: requestHeaders,
										withCredentials: true
									).then((response){
								        Map<String, dynamic> map = jsonDecode(response.responseText);
								        Map<String, String> res = new Map();
								        map.forEach((k,v){res[k] = v.toString();});
								        print("[PYRO] fetched custom action for «g.name.fuEscapeDart» ${graph.filename}");
								        return res;
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								
								Future<Message> triggerCustomActionsFor«g.name.escapeDart»(int id,«g.dartFQN» graph,String fqn,List<HighlightCommand> highlightings) async {
								  	var data = {
								  		'fqn':fqn,
								  		'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
								  		'runtimeType':'info.scce.pyro.core.command.types.Action'
								  		};
								  	return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/customaction/${graph.id}/${id}/trigger/private",
								  		sendData:jsonEncode(data),
								  		method: "POST",
								  		requestHeaders: requestHeaders,
									withCredentials: true
									).then((response){
										  	   return Message.fromJSON(response.responseText);
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								
								Future<Message> triggerDoubleClickActionsFor«g.name.escapeDart»(int id,«g.dartFQN» graph,List<HighlightCommand> highlightings) async {
									var data = {
										'fqn':null,
										'highlightings':highlightings.map((n)=>n.toJSOG()).toList(),
										'runtimeType':'info.scce.pyro.core.command.types.Action'
									};
									return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/dbaction/${graph.id}/${id}/trigger/private",
										sendData:jsonEncode(data),
										method: "POST",
										requestHeaders: requestHeaders,
										withCredentials: true
									).then((response){
										return Message.fromJSON(response.responseText);
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								
								Future<«g.name.lowEscapeDart»CG.«g.name.fuEscapeDart»CommandGraph> loadCommandGraph«g.name.fuEscapeDart»(«g.dartFQN» graph,List<HighlightCommand> highlightings) async{
									return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/read/${graph.id}/private",
									  	method: "GET",
									  	requestHeaders: requestHeaders,
									withCredentials: true
									).then((response){
										var newGraph = «g.dartFQN».fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
										print("[PYRO] load «g.name.lowEscapeDart» ${newGraph.filename}");
										graph.merge(newGraph);
										var cg = new «g.name.lowEscapeDart»CG.«g.name.fuEscapeDart»CommandGraph(graph,highlightings);
										return cg;
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								Future<«g.dartFQN»> loadGraph«g.name.fuEscapeDart»(int id) async{
									return html.HttpRequest.request("${getBaseUrl()}/«g.restEndpoint»/read/${id}/private",
									  	method: "GET",
									  	requestHeaders: requestHeaders,
									withCredentials: true
									).then((response){
										print("[PYRO] load «g.name.lowEscapeDart» ${id}");
										return «g.dartFQN».fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
							«ENDFOR»«/* TODO: These methods shouldn't be generated on the name ob the GraphModel. It should be derived from the FQN */»
						'''
					}»
			«ENDFOR»
			«FOR g : gc.ecores SEPARATOR "\n"»
				
				Future<«g.dartFQN»> create«g.name.escapeDart»(«g.dartFQN» ecore) async {
				    var data = {
				        'filename':ecore.filename
				    };
				    return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/create/private",
				    	sendData:jsonEncode(data),
				    	method: "POST",
				    	requestHeaders: requestHeaders,
				withCredentials: true
				).then((response){
				       var newEcore = «g.dartFQN».fromJSOG(jsonDecode(response.responseText),new Map());
				       print("[PYRO] created «g.name.fuEscapeDart» ${ecore.filename}");
				       ecore.id=newEcore.id;
				       return newEcore;
				}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
				}
			«ENDFOR»
		}
	'''

}
