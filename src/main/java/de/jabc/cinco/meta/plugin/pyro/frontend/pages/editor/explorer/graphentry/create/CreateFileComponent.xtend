
package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.explorer.graphentry.create

import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.Generatable

class CreateFileComponent extends Generatable{
	
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameCreateFileComponent()'''create_file_component.dart'''
	
	def contentCreateFileComponent()
	'''
	import 'dart:async';
	import 'dart:html';
	import 'package:angular/angular.dart';
	import 'package:angular_forms/angular_forms.dart';
	import 'package:ng_bootstrap/ng_bootstrap.dart';
	import 'package:«gc.projectName.escapeDart»/src/filesupport/fileselect.dart';
	import 'package:«gc.projectName.escapeDart»/src/filesupport/fileuploader.dart';
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/graph_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/service/base_service.dart';
	import 'package:«gc.projectName.escapeDart»/src/utils/graph_model_permission_utils.dart';
	
	«FOR m:gc.mglModels»
		import 'package:«gc.projectName.escapeDart»/«m.modelFilePath»' as «m.name.lowEscapeDart»;
	«ENDFOR»
	«FOR m:gc.ecores»
		import 'package:«gc.projectName.escapeDart»/«m.modelFilePath»' as «m.name.lowEscapeDart»;
	«ENDFOR»
	
	@Component(
	    selector: 'create-file',
	    templateUrl: 'create_file_component.html',
	    directives: const [coreDirectives,formDirectives,FileSelect,bsDirectives],
	    exports: const [GraphModelPermissionUtils]
	)
	class CreateFileComponent implements OnInit {
	
	  PyroFolder folder;
	  List<PyroGraphModelPermissionVector> permissionVectors;
	  
	  dynamic _parentEl;
	  ElementRef _el;
	
	  final GraphService _graphService;
	  
	  bool show = false;
	  bool showError = false;
	  
	  StreamController created = null;
	  
	  FileUploader uploader = new FileUploader({
	      'url': '${BaseService.getUrl()}/files/create',
	      'authToken': '${BaseService.getAuthToken()}'
	    }, autoUpload: true);

	
	  CreateFileComponent(this._graphService, this._el) {
	  	permissionVectors = new List();
	  	uploader.newFileStream.listen((fr){
		  _graphService.createbinary(fr,folder).then((g)=>close());
		});
	  }
	  
	  @override
	  void ngOnInit() {
	  	_parentEl = _el.nativeElement.parent;
	  }
	  
	  Stream open(PyroFolder parentFolder, List<PyroGraphModelPermissionVector> pvs) {
	    show = true;
	    folder = parentFolder;
	    permissionVectors = pvs;
	    window.document.querySelector('body').children.add(_el.nativeElement);
	    created = new StreamController();
	    return created.stream;
	  }
	  
	  void close() {
	    show = false;
	    showError = false;
	    folder = null;
	    permissionVectors = new List();
	    _parentEl.children.add(_el.nativeElement);
	  }
	  	
	  void createNewFile(String name,String type,dynamic e)
	  {
	  		showError = false;
	  	  if(e!=null)e.preventDefault();
	      if(name!=null && type != null)
	      {
	      	if(name.isEmpty) {
	      		return;
	      	}
	      	if(folder.files.where((f)=>f.filename==name).isNotEmpty) {
	      		showError = true;
	      		return;
	        }
	        switch(type)
	        {
	        	«FOR g:gc.creatableGraphmodels»
	        		case '«g.name.fuEscapeDart»':{
	        			var g = new «g.dartFQN»();
	        			g.filename = name;
	        			_graphService.create«g.name.escapeDart»(g,folder).then((g){
	        				created.add(folder.files.where((n)=>n.filename == g.filename).first);
	        				close();
	        			});
	        			break;
	        		}
	        	«ENDFOR»
	        }
	
	      }
	
	  }
	  
	  void createNewTextualFile(String name,String extension,dynamic e)
	  {
	  	if(e!=null)e.preventDefault();
	  	if(name!=null && extension != null)
	  	{
	  		if(name.isEmpty) {
	  			return;
	  		}
	  		if(folder.files.where((f)=>f.filename==name).isNotEmpty) {
	  			return;
	  		}
	  		if(!extension.contains("\.")) {
	  			_graphService.createtextual(name,extension,folder).then((g){
	  				created.add(folder.files.where((n)=>n.filename == name).first);
	  				close();
	  			});
	  		}
	  	}
	  }
	  
	  void createNewURLFile(String name,String extension,String url,dynamic e)
	  {
	  	  if(e!=null)e.preventDefault();
	      if(name!=null && extension != null && url != null)
	      {
	      	if(name.isEmpty) {
	      		return;
	      	}
	      	if(folder.files.where((f)=>f.filename==name).isNotEmpty) {
	          return;
	        }
	      	if(!extension.contains("\.")) {
	      		_graphService.createurl(name,extension,url,folder).then((g){
	            	created.add(folder.files
	            		.where((n)=>n.filename == g.filename)
	            		.first);
	            	close();
	            });
	      	}
	      }
	  }
	}

	'''
	
	def fileNameCreateFileComponentTemplate()'''create_file_component.html'''
	
	def contentCreateFileComponentTemplate()
	'''
	<div class="modal show d-block fade in" tabindex="-1" *ngIf="show" role="dialog" aria-labelledby="createEntryLabel">
	    <div class="modal-dialog" role="document">
	        <div class="modal-content">
	            <div class="modal-header">
	            	<h4 class="modal-title" id="createEntryLabel">Create a new File</h4>
	                <button type="button" (click)="close()" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	            </div>
	            <div class="modal-body">
	            
	            	<bs-tabs #tabs>
					    <template bsTab [active]="true" select="model">
					        Model
					    </template>
					    <template bsTab select="binary">
					        Binary
					    </template>
					    <template bsTab select="url">
					        URL
					    </template>
					    <template bsTab select="textual">
					        Textual
					    </template>
					</bs-tabs>
					
					<bs-tab-content [for]="tabs">
					    <template bs-tab-panel name="model">
					    	<p></p>
					        <form (ngSubmit)="createNewFile(graphname.value,fileType.value,null)">
		                    <div *ngIf="showError" class="alert alert-danger">The filename already exists</div>
		                    <div class="form-group">
		                        <label for="projectName">Filename</label>
		                        <input #graphname type="text" class="form-control" id="projectName" placeholder="Name" required>
		                    </div>
		                    <div class="form-group">
		                        <select #fileType class="form-control" required>
			                        «FOR g:gc.creatableGraphmodels.sortBy[displayName]»
			                        	<option value="«g.name.fuEscapeDart»" *ngIf="GraphModelPermissionUtils.canCreate('«g.name.toUnderScoreCase»', permissionVectors)">«g.displayName»</option>
			                        «ENDFOR»
		                        </select>
		                    </div>
		                    <div class="float-right">
		                    	<button (click)="createNewFile(graphname.value,fileType.value,$event)" type="submit" class="btn btn-success">Create</button>
	                			<button type="button" class="btn" data-dismiss="modal" (click)="close()">Close</button>
	                		</div>
	                		</form>
					    </template>
					    
					    <template bs-tab-panel name="binary">
					    	<p></p>
					        <form>
			            		<div *ngIf="uploader.isUploading" style="display: inline-flex;">
							      <div style="margin-right:10px;" class="dime-file-loader"></div><span style="margin:auto;">Uploading...</span>
							    </div>
							    <div
							    *ngIf="uploader.hasError()"
							    class="alert alert-danger" role="alert">
							    {{uploader.errorMessage()}}
							    </div>
							    <div class="float-right">
							    	<input ng2-file-select [uploader]="uploader" [disabled]="uploader.isUploading" type="file" id="binary">
			            			<button type="button" class="btn" data-dismiss="modal" (click)="close()">Close</button>
			            		</div>
			            	</form>
					    </template>
					    
					    <template bs-tab-panel name="url">
					    	<p></p>
					        <form (ngSubmit)="createNewURLFile(urlFilename.value,urlExtension.value,null, $event)">
		                    <div *ngIf="showError" class="alert alert-danger">The filename already exists</div>
		                    <div class="form-group">
		                        <label for="urlFilename">Filename</label>
		                        <input #urlFilename type="text" class="form-control" id="urlFilename" placeholder="Name" required>
		                    </div>
		                    <div class="form-group">
		                        <label for="urlExtension">File extension</label>
		                        <input #urlExtension type="text" class="form-control" id="urlExtension" placeholder="Extension" required>
		                    </div>
		                    <div class="form-group">
		                        <label for="urlURL">URL</label>
		                        <input #urlURL type="text" class="form-control" id="urlURL" placeholder="http://your.url" required>
		                    </div>
		                    <div class="float-right">
		                    	<button (click)="createNewURLFile(urlFilename.value,urlExtension.value,urlURL.value,$event)" type="button" class="btn btn-success">Create</button>
	                			<button type="button" class="btn" data-dismiss="modal" (click)="close()">Close</button>
	                		</div>
	                		</form>
					    </template>
					    
					    <template bs-tab-panel name="textual">
					    	<p></p>
					        <form (ngSubmit)="createNewTextualFile(filename.value, fileExtension.value, $event)">
		                    <div *ngIf="showError" class="alert alert-danger">The filename already exists</div>
		                    <div class="form-group">
		                        <label for="projectName">Filename</label>
		                        <input #filename type="text" class="form-control" id="Filename" placeholder="Name" required>
		                    </div>
		                    <div class="form-group">
		                        <label for="projectName">File extension</label>
		                        <input #fileExtension type="text" class="form-control" id="Fileextension" placeholder="Extension" required>
		                    </div>
		                    <div class="float-right">
		                    	<button (click)="createNewTextualFile(filename.value,fileExtension.value,$event)" type="button" class="btn btn-success">Create</button>
	                			<button type="button" class="btn" data-dismiss="modal" (click)="close()">Close</button>
	                		</div>
	                		</form>
					    </template>
					</bs-tab-content>
		            
	            </div>
	        </div>
	    </div>
	</div>
	'''
}
