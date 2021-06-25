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
		import 'package:«gc.projectName.escapeDart»/src/pages/main/routes.dart';
		import 'package:«gc.projectName.escapeDart»/src/pages/editor/canvas/canvas_component.dart';
		import 'base_service.dart';
		«FOR g : gc.ecores»
			import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
		«ENDFOR»
		«FOR g : gc.mglModels»
			import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
		«ENDFOR»
		«FOR g : gc.graphMopdels»
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
		
		  	Future<PyroProject> loadProjectStructure(PyroProject project) async {
			    return html.HttpRequest.request("${getBaseUrl()}/project/structure/${project.id}/private",
			    	method: "GET",
			    	requestHeaders: requestHeaders,
			  		withCredentials: true
				).then((response){
				     var p = PyroProject.fromJSON(response.responseText);
				     print("[PYRO] load project ${p.name}");
				     return p;
				   }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
			 
			 Future<dynamic> moveFolder(int folderId, int targetId) {
			 	return html.HttpRequest.request("${getBaseUrl()}/graph/move/folder/${folderId}/${targetId}/private",
			 		method: "POST",
			 		requestHeaders: requestHeaders,
			 		withCredentials: true
			 	).then((response){
			 	   print("[PYRO] folder moved");
			 	   return null;
			 	 }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			 }
			 
			 Future<dynamic> moveFile(int fileId, int targetId) {
			 	return html.HttpRequest.request("${getBaseUrl()}/pyrofile/move/${fileId}/${targetId}/private",
			 		method: "POST",
			 		requestHeaders: requestHeaders,
			 		withCredentials: true
			 	).then((response){
			 	     print("[PYRO] file moved");
			 	     return null;
			 	 }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			 }
			 
			 Future<PyroProject> loadProjectStructureById(id) async {
			     return html.HttpRequest.request("${getBaseUrl()}/project/structure/${id}/private",
			     	method: "GET",
			     	requestHeaders: requestHeaders,
			     	withCredentials: true
			     ).then((response){
			       var p = PyroProject.fromJSON(response.responseText);
			       print("[PYRO] load project ${p.name}");
			       return p;
			     }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			   }
		
		  Future<PyroFolder> createFolder(PyroFolder folder,dynamic parent) async {
		    var data = {
		      'parentId':parent.id,
		      'name':folder.name
		    };
		    return html.HttpRequest.request("${getBaseUrl()}/graph/create/folder/private",
		    	sendData:jsonEncode(data),
		    	method: "POST",
		    	requestHeaders: requestHeaders,
		    	withCredentials: true
		    ).then((response){
		      var newFolder = PyroFolder.fromJSON(response.responseText);
		      if(parent.innerFolders.where((f)=>f.id==newFolder.id).isEmpty) {
		         	parent.innerFolders.add(newFolder);
		      }
		      print("[PYRO] new folder ${folder.name} in folder ${parent.name}");
		      return newFolder;
		    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
		  }
		
		  Future<PyroFolder> updateFolder(PyroFolder folder) async {
		    var data = {
		      'id':folder.id,
		      'name':folder.name
		    };
		    return html.HttpRequest.request("${getBaseUrl()}/graph/update/folder/private",
		    	sendData:jsonEncode(data),
		    	method: "POST",
		    	requestHeaders: requestHeaders,
		    	withCredentials: true
		    ).then((response){
		      print("[PYRO] update folder ${folder.name}");
		      return folder;
		    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
		  }
		
		  Future<dynamic> removeFolder(PyroFolder folder, PyroFolder parent) async {
		    return html.HttpRequest.request("${getBaseUrl()}/graph/remove/folder/${folder.id}/${parent.id}/private",
		    	method: "GET",
		    	requestHeaders: requestHeaders,
		    	withCredentials: true
		    ).then((response){
		      print("[PYRO] removed folder ${folder.name} (if permitted)");
		      // parent.innerFolders.remove(folder);
			}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			 }
			 
			 Future<dynamic> removeFile(PyroFile file, PyroFolder parent) async {
			     return html.HttpRequest.request("${getBaseUrl()}/pyrofile/remove/${file.id}/${parent.id}/private",
			     	method: "GET",
			     	requestHeaders: requestHeaders,
			     	withCredentials: true
			     ).then((response){
			       print("[PYRO] tried to remove file ${file.filename}");
			     	parent.files.remove(file);
			  }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			 }
			 
			 Future<PyroFile> updateFile(PyroFile file) async {
			       var data = {
			         'id':file.id,
			         'filename':file.filename,
			       };
			       return html.HttpRequest.request("${getBaseUrl()}/pyrofile/update/file/private",
			        sendData:jsonEncode(data),
			        method: "POST",
			        requestHeaders: requestHeaders,
			        withCredentials: true
			       ).then((response){
			         print("[PYRO] update file ${file.filename}");
			         return file;
			       }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			   }
			 
			 Future<GraphModel> updateShareGraphModel(GraphModel g,bool newStatus) async {
			var data = {
			  'id':g.id,
			  'isPublic':newStatus,
			};
			return html.HttpRequest.request("${getBaseUrl()}/graph/update/graphmodel/shared/private",
				sendData:jsonEncode(data),
				method: "POST",
				requestHeaders: requestHeaders,
				withCredentials: true
			).then((response){
			  var responseMessage = jsonDecode(response.response);
			     var isPublic = responseMessage['isPublic'];
			     g.isPublic = isPublic;
			  print("[PYRO] update share status file ${g.filename}");
			  return g;
			}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			 }
		
		  Future<dynamic> removeGraph(GraphModel graph,PyroFolder parent) async {
		    return html.HttpRequest.request("${getBaseUrl()}/${graph.$lower_type()}/remove/${graph.id}/${parent.id}/private",
		    	method: "GET",
		    	requestHeaders: requestHeaders,
		    	withCredentials: true
		    ).then((response){
		      print("[PYRO] tried to remove modelFile ${graph.filename}");
		      parent.files.remove(graph);
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
		  
			Future<PyroBinaryFile> createbinary(FileReference fr,PyroFolder parent) async {
			    var data = {
			        'parentId':parent.id,
			        'file':fr.toJSOG(new Map())
			    };
			    return html.HttpRequest.request("${getBaseUrl()}/pyrofile/create/binary/private",
			    	sendData:jsonEncode(data),
			    	method: "POST",
			    	requestHeaders: requestHeaders,
			    	withCredentials: true
			    ).then((response){
			        var newFile = PyroBinaryFile.fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
			        print("[PYRO] created PyroBinaryFile ${newFile.filename}");
			     parent.files.add(newFile);
			        return newFile;
			    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
			
			Future<PyroBinaryFile> createblob(String name,String content,PyroFolder parent) async {
			    var data = {
			        'parentId':parent.id,
			        'file':content,
			        'name':name
			    };
			    return html.HttpRequest.request("${getBaseUrl()}/pyrofile/create/blob/private",
			    	sendData:jsonEncode(data),
			    	method: "POST",
			    	requestHeaders: requestHeaders,
			    	withCredentials: true
			    ).then((response){
			        var newFile = PyroBinaryFile.fromJSOG(jsonDecode(response.responseText),new Map<String, dynamic>());
			        print("[PYRO] created PyroBinaryFile ${newFile.filename}");
			     parent.files.add(newFile);
			        return newFile;
			    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
			
			Future<PyroTextualFile> createtextual(String filename,String extension,PyroFolder parent) async {
			    var data = {
			        'parentId':parent.id,
			        'filename':filename,
			        'extension':extension
			    };
			    return html.HttpRequest.request("${getBaseUrl()}/pyrofile/create/textual/private",
			    	sendData:jsonEncode(data),
			    	method: "POST",
			    	requestHeaders: requestHeaders,
			    	withCredentials: true
			    ).then((response){
			        var newFile = PyroTextualFile.fromJSOG(jsonDecode(response.responseText),new Map());
			        print("[PYRO] created PyroTextualFile ${newFile.filename}");
			     parent.files.add(newFile);
			        return newFile;
			    }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
		
			Future<PyroURLFile> createurl(String filename,String extension,String url,PyroFolder parent) async {
		      var data = {
		        'parentId':parent.id,
		        'filename':filename,
		        'extension':extension,
		        'url':url
		      };
		      return html.HttpRequest.request("${getBaseUrl()}/pyrofile/create/url/private",
		      	sendData:jsonEncode(data),
		      	method: "POST",
		      	requestHeaders: requestHeaders,
		      	withCredentials: true
		      ).then((response){
		        var newFile = PyroURLFile.fromJSOG(jsonDecode(response.responseText),new Map());
		        print("[PYRO] created PyroURLFile ${newFile.filename}");
				    parent.files.add(newFile);
				       return newFile;
				     }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
			}
			
			Future<dynamic> loadCommandGraph(GraphModel graph,List<HighlightCommand> highlightings) async{
				if(graph == null) throw new Error();
				«FOR g : gc.graphMopdels SEPARATOR " else "»if(graph.$lower_type() == "«g.name.lowEscapeDart»") {
					return loadCommandGraph«g.name.fuEscapeDart»(graph, highlightings);
				}«ENDFOR»
				      throw new Error();
			}
			«FOR m : gc.mglModels»
					«{
						val styles = CincoUtil.getStyles(m)
						'''
							«FOR g:m.graphModels»
								
								/**
								 * METHODS FOR «g.dartFQN»
								 */
								
								«IF g.hasAppearanceProvider(styles)»
									
									Future<String> appearances«g.name.fuEscapeDart»(«g.dartFQN» graph) async{
										return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/appearance/${graph.id}/private",
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
									return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/${graph.id}/button/${key}/${id}/trigger/private",
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
								  return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/${graph.id}/psaction/${id}/trigger/private",
								  	  	  	sendData:jsonEncode(data),
								  	  	  	method: "POST",
								  	  	  	requestHeaders: requestHeaders,
								  	withCredentials: true
								  ).then((response){
								  	return Message.fromJSON(response.responseText);
								  }).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
												  
								Future<«g.dartFQN»> create«g.name.escapeDart»(«g.dartFQN» graph, PyroFolder parent) async {
								    var data = {
								        'parentId':parent.id,
								        'filename':graph.filename
								    };
								    parent.files.add(graph);
								    return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/create/private",
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
								    return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/customaction/${graph.id}/${id}/fetch/private",
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
								  	return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/customaction/${graph.id}/${id}/trigger/private",
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
									return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/dbaction/${graph.id}/${id}/trigger/private",
										sendData:jsonEncode(data),
										method: "POST",
										requestHeaders: requestHeaders,
										withCredentials: true
									).then((response){
										return Message.fromJSON(response.responseText);
									}).catchError(super.handleProgressEvent,test: (e) => e is html.ProgressEvent);
								}
								
								Future<«g.name.lowEscapeDart»CG.«g.name.fuEscapeDart»CommandGraph> loadCommandGraph«g.name.fuEscapeDart»(«g.dartFQN» graph,List<HighlightCommand> highlightings) async{
									return html.HttpRequest.request("${getBaseUrl()}/«g.name.lowEscapeDart»/read/${graph.id}/private",
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
							«ENDFOR»
						'''
					}»
			«ENDFOR»
			«FOR g : gc.ecores SEPARATOR "\n"»
				
				Future<«g.dartFQN»> create«g.name.escapeDart»(«g.dartFQN» ecore,PyroFolder parent) async {
				    var data = {
				        'parentId':parent.id,
				        'filename':ecore.filename
				    };
				    parent.files.add(ecore);
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
