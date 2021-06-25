package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.explorer.graphentry

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.ModelElement
import mgl.ReferencedModelElement

class FileEntryComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameFileEntryComponent()'''file_entry_component.dart'''
	
	def contentFileEntryComponent()'''
	import 'package:angular/angular.dart';
	import 'package:angular_forms/angular_forms.dart';
	import 'dart:async';
	import 'dart:html';
	import 'package:dnd/dnd.dart';
	import 'dart:collection' as collection;
	
	import '../../../../model/core.dart';
	import '../../../../service/graph_service.dart';
	import '../../../../service/base_service.dart';
	import '../../../../service/context_menu_service.dart';
	import '../../../../pages/shared/context_menu/context_menu.dart';
	import '../../../../utils/graph_model_permission_utils.dart';
	import '../delete/delete_component.dart';
	import '../share/share_component.dart';
	«FOR g:gc.mglModels»
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
	«ENDFOR»
	
	@Component(
	    selector: 'file-entry',
	    templateUrl: 'file_entry_component.html',
	    directives: const [coreDirectives, formDirectives, DeleteComponent,ShareComponent],
	    exports: const [GraphModelPermissionUtils]
	)
	class FileEntryComponent implements OnInit {
	
	  final openFileSC = new StreamController();
	  @Output() Stream get openFile => openFileSC.stream;
	
	  final deleteSC = new StreamController();
	  @Output() Stream get delete => deleteSC.stream;
	
	  final hasChangedSC = new StreamController();
	  @Output() Stream get hasChanged => hasChangedSC.stream;
	
	  @Input()
	  PyroFile file;
	  
	  @Input()
	  PyroProject project;
	  
	  @Input()
	  List<PyroGraphModelPermissionVector> permissionVectors;
	  
	  @Input('currentFile')
	  PyroFile currentOpenFile;
	  
	  @ViewChild('fileEl')
	  ElementRef fileEl;
	  
	  @ViewChild('deleteFileModal')
	  DeleteComponent deleteFileModal;
	  
	  @ViewChild('shareFileModal')
	  ShareComponent shareGraphModel;
	
	  bool editMode = false;
	
	  final GraphService graphService;
	  
	  final ContextMenuService _contextMenuService;
	  
	  bool open = false;
	  

	  Map<String,List<IdentifiableElement>> primeRefs = new Map();
	
	  FileEntryComponent(this.graphService, this._contextMenuService) {
	  	permissionVectors = new List();
	  }
	  
	  @override
	  void ngOnInit() {
	    Draggable draggable = new Draggable(fileEl.nativeElement, 
	   	  avatarHandler: new AvatarHandler.clone(),
	      draggingClass: 'dragging'
	    );
	  }
	
	void editFile(dynamic e) {
	  if (e != null) e.preventDefault();
	  editMode = true;
	}

	void share(GraphModel g) {
		graphService.updateShareGraphModel(g,!g.isPublic).then((g){
			hasChangedSC.add(null);
		});
	}
	
	  void save(dynamic e)
	  {
	    e?.preventDefault();
	    if(file is GraphModel) {
		    graphService.updateGraphModel(file).then((g){
		      editMode = false;
		      hasChangedSC.add(e);
		    });
	    } else {
		    graphService.updateFile(file).then((g){
		      editMode = false;
		      hasChangedSC.add(e);
		    });
	    }
	
	  }
	  
	  Map<String,List<IdentifiableElement>> getPrimeGroups(Map<String, List<IdentifiableElement>> map) {
	     	return new collection.SplayTreeMap.from(map,(a,b) => a.compareTo(b));
	  }
	    
	  List<IdentifiableElement> getPrime(List<IdentifiableElement> e) {
	   	e.sort((a,b)=>getPrimeRefName(a).compareTo(getPrimeRefName(b)));
	    return e;
	  }
	  
	  trackPrimeGroups(int index, dynamic item) {
	   	return item is MapEntry<String,List<IdentifiableElement>> ? item.key : item;
	  }
	    
	  trackPrime(int index, dynamic item) {
	  	return item is IdentifiableElement ? item.id : item;
	  }
	  
	  showContextMenu(dynamic e) {
	      e.preventDefault();
	      
	      ContextMenu menu = ContextMenu(e.client.x, e.client.y, List.of([
	        ContextMenuItem('fa-edit', 'Edit', canEdit(file), (){
	          editFile(null);
	        }),
	        ContextMenuItem('fa-trash', 'Delete', canDelete(file), (){
	          deleteFileModal.open('File', file.filename+getExtension());
	        })
	      ]));
	      
	      if (isBinaryOrUrl()) {
	        menu.entries.add(ContextMenuSeparator());
	        menu.entries.add(ContextMenuItem('fa-external-link-alt', 'Open', true, (){
	          window.open(getURL(), '_blank');
	        }));
	      }
	      
	      if (isGraphModel()) {
	      	menu.entries.add(ContextMenuSeparator());
	        menu.entries.add(ContextMenuItem('fa-share', 'Publish', true, (){
	          shareGraphModel.open(file as GraphModel);
	        }));
	      }
	      
	      _contextMenuService.show(menu);
	    }
	
	  String getExtension(){
	    if(file==null || file.extension == null){
	      return "";
	    }
	    return ".${file.extension}";
	  }
	  
	  bool canContainPrime() {
	  	if(file==null||currentOpenFile==null){
	      return false;
	    }
	    //check if current open file can contaiun prime nodes
	    «FOR g:gc.graphMopdels.filter[!primeRefs.empty] SEPARATOR " else "
	    »if(currentOpenFile is «g.dartFQN»){
	    	//check if elements of current file entry can be referenced 
	    	«FOR refG:g.primeReferencedGraphModels SEPARATOR " else "
	    	»if(file is «refG.dartFQN»){
	    		return true;
	    	}«
	    	ENDFOR»
	    }«
    	ENDFOR»
	    return false;
	  }
	  
	  bool isBinaryOrUrl() => file is PyroBinaryFile || file is PyroURLFile;
	  
	  String getURL() => "${BaseService.getUrl()}/pyrofile/read/projectresource/${project.id.toString()}/${project.fullPath(file)}${file.getFullName()}";
	  bool isGraphModel() => file is GraphModel;
	  
	  
	  String getPrimeRefName(PyroElement elem) {
		if(elem == null||currentOpenFile==null){
			return "null";
		}
		//check if current open file can contaiun prime nodes
		«FOR g:gc.graphMopdels.filter[!primeRefs.empty] SEPARATOR " else "
		»if(currentOpenFile is «g.dartFQN») {
			//check if elements of current file entry can be referenced 
			«FOR refG:g.primeReferencedGraphModels»
				if(file is «refG.dartFQN»)
				{
					«FOR pr:g.getPrimeReferencingElements(refG)»
						// nodes of «(pr as ReferencedModelElement).element.typeName»
						«{
							val subTypes = (pr as ReferencedModelElement).referencedElement.resolveSubTypesAndType
							'''
								«FOR subType:subTypes»
									if(elem.$type() == "«subType.typeName»")
									{
										«IF (pr as ReferencedModelElement).referencedElementAttributeName === null»
											return "«(subType as ModelElement).displayName» (" + elem.id.toString() + ")";
										«ELSE»
											return (elem as «subType.dartFQN»).«(pr as ReferencedModelElement).referencedElementAttributeName»;
										«ENDIF»
									}
      							«ENDFOR»
      						'''
						}»
					«ENDFOR»
				}
			«ENDFOR»
		}«
		ENDFOR»
		return elem.id.toString();
	  }
	  
	  String getFolderClass()
	  {
	      if(open){
	        return "fas fa-chevron-down";
	      }
	      return "fas fa-chevron-right";
	  }
	  
	  bool canRead(PyroFile file) {
		«FOR g:gc.graphMopdels»
			if (file is «g.dartFQN») {
				«IF g.isReadable»
					return GraphModelPermissionUtils.canRead("«g.name.toUnderScoreCase»", permissionVectors);
				«ELSE»
					return false;
				«ENDIF»
			}
		«ENDFOR»
		return true;
	  }
	  
	  bool canEdit(PyroFile file) {
		«FOR g:gc.graphMopdels»
		  if (file is «g.dartFQN») {
		  	«IF g.isEditable»
		  		return GraphModelPermissionUtils.canUpdate("«g.name.toUnderScoreCase»", permissionVectors);
		  	«ELSE»
		  		return false;
		  	«ENDIF»
		  }
		«ENDFOR»
		return true;
	  }
	  	  
	  bool canDelete(PyroFile file) {
		«FOR g:gc.graphMopdels»
		  if (file is «g.dartFQN») {
		  	«IF g.isDeletable»
		  		return GraphModelPermissionUtils.canDelete("«g.name.toUnderScoreCase»", permissionVectors);
		  	«ELSE»
		  		return false;
		  	«ENDIF»
		  }
		«ENDFOR»
		return true;
	  }
	  
	  void primeRefAdd(String id,IdentifiableElement e) {
	  	if(!primeRefs.containsKey(id)) {
	  		primeRefs[id] = new List();
	  	}
	  	primeRefs[id].add(e);
	  }
	  
	  void openPrimeRefs(dynamic e)
	  {
	      e.preventDefault();
	      if(file==null||currentOpenFile==null) {
	      	return;
	      }
	      open = !open;
	      if(open==true) {
	      	//fetch primerefs for current graphmodel

	      	primeRefs = new Map();
	      	«FOR g:gc.graphMopdels.filter[!primeRefs.empty]»
	      	if(currentOpenFile is «g.dartFQN»)
	      	{
	      		//check if elements of current file entry can be referenced 
	      		«FOR refG:g.primeReferencedGraphModels»
	      		if(file is «refG.dartFQN») {
	      			graphService.loadCommandGraph«refG.name.fuEscapeDart»(file,new List()).then((n){
	      				var list = n.currentGraphModel.allElements();
	      				list.forEach((elem){
	      					«FOR pr:g.getPrimeReferencingElements(refG).toSet»
	      					«{
	      						val subTypes = (pr as ReferencedModelElement).referencedElement.resolveSubTypesAndType
	      						'''
	      							«FOR subType:subTypes»
	      							if(elem.$type() == "«subType.typeName»") {
	      								primeRefAdd("«(pr as ReferencedModelElement).element.displayName»",elem);
	      							}
	      							«ENDFOR»
	      						'''
	      					}»
	      					«ENDFOR»
	      				});
	      			});
	      		}
	      		«ENDFOR»
	      	}
  		    «ENDFOR»
	      }
	  }
	  
	  bool hasIcon() {
	  	if(file==null){
	      return false;
	    }
		«FOR g:gc.graphMopdels.filter[!iconPath.nullOrEmpty]»
		    if(file is «g.dartFQN»){
		      return true;
		    }
		«ENDFOR»
	    return false;
	  }
	  
	  String getIcon() {
	  	 if(file==null){
	  	      return "";
	  	  }
	  	  «FOR g:gc.graphMopdels.filter[!iconPath.nullOrEmpty]»
	  	    if(file is «g.dartFQN»){
	  	      return "«g.iconPath(true)»";
	  	    }
	  	  «ENDFOR»
	  	  return "";
	  }
	
	  void selectFile(PyroFile file,dynamic e)
	  {
	    e.preventDefault();
	    openFileSC.add(file);
	  }
	}
	'''
	
	def ModelElement element(ReferencedModelElement element){
		element.eContainer as ModelElement
	}
}
