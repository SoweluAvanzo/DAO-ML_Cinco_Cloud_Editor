package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.graphs.graphmodel

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement

class IdentifiableElementPropertyComponent extends Generatable {
	

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameIdentifiableElementPropertyComponent(ModelElement me) '''«me.propertyElementFileDart»'''

	def contentIdentifiableElementPropertyComponent(ModelElement me) {
		val g = me.modelPackage as MGLModel
		val fileAttributes = me.attributesExtended.filter[isPrimitive && isFile].filter[!isHidden]
	'''
		import 'package:angular/angular.dart';
		import 'package:angular_forms/angular_forms.dart';
		import 'dart:async';
		
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart' as core;
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
		«IF !fileAttributes.empty»
			
			import 'package:«gc.projectName.escapeDart»/src/filesupport/fileselect.dart';
			import 'package:«gc.projectName.escapeDart»/src/filesupport/fileuploader.dart';
			import 'package:«gc.projectName.escapeDart»/src/service/base_service.dart';
		«ENDIF»
		
		@Component(
			selector: '«me.name.lowEscapeDart»-property',
			templateUrl: '«me.propertyElementFileHTML»',
			directives: const [coreDirectives,formDirectives«IF !fileAttributes.empty»,FileSelect«ENDIF»]
		)
		class «me.name.fuEscapeDart»PropertyComponent {
		
		  @Input()
		  «me.dartFQN» currentElement;
		  
		  @Input()
		  core.GraphModel currentGraphModel;
		
		  final hasChangedSC = new StreamController();
		  @Output() Stream get hasChanged => hasChangedSC.stream;
		  
			«FOR file:fileAttributes»
				FileUploader uploader_«file.name.escapeDart» = new FileUploader({
					'url': '${BaseService.getUrl()}/files/create'
					//,
					//'authToken': BaseService.getAuthToken()
				},autoUpload:true«IF !file.getFile.value.empty», accept:"«file.getFile.value.join(", ")»"«ENDIF»);
			«ENDFOR»
		  
		  «me.name.fuEscapeDart»PropertyComponent() {
		  	«FOR file:fileAttributes»
		  		uploader_«file.name.escapeDart».newFileStream.listen((fr){
		  			«IF file.isList»
		  				addList«file.name.escapeDart»(fr.downloadPath);
		  			«ELSE»
		  				currentElement.«file.name.escapeDart» = fr.downloadPath;
		  			«ENDIF»
		  			hasChangedSC.add(currentElement);
		  		});
		  	«ENDFOR»
		  }
		 
		  
		  void valueChanged(dynamic e) {
		  	hasChangedSC.add(currentElement);
		  	«IF me instanceof GraphicalModelElement»
		  		currentElement.$isDirty = false;
		  	«ENDIF»
		  }
		  
		  //get for enumeration literals
		  «FOR attr:me.attributesExtended.filter[attributeTypeName.getEnum(g)!==null].filter[!isHidden]»
			«attr.attributeTypeName» parse«attr.name»Enum(String e) {
				switch(e) {
				  «FOR lit:attr.attributeTypeName.getEnum(g).literals»
				  	case "«attr.attributeTypeName.fuEscapeDart».«lit.escapeDart»": return «g.name.lowEscapeDart».«attr.attributeTypeName.fuEscapeDart».«lit.escapeDart»;
				  «ENDFOR»
				}
				return «attr.dartFQN».«attr.attributeTypeName.getEnum(g).literals.get(0).escapeDart»;
			}
		  «ENDFOR»
		  
		  // for each primitive list attribute
		  «FOR attr : me.attributesExtended.filter[isPrimitive].filter[isList].filter[!isHidden]»
		  	void addList«attr.name.escapeDart»(dynamic e) {
		  	  e.preventDefault();
		  	  currentElement.«attr.name.escapeDart».add(«attr.init(g,'''«g.name.lowEscapeJava».''')»);
		  	  hasChangedSC.add(currentElement);
		  	  «IF me instanceof GraphicalModelElement»
		  	  	currentElement.$isDirty = false;
	  		  «ENDIF»
		  	}
		  	void removeList«attr.name.escapeDart»(int index) {
		  	  currentElement.«attr.name.escapeDart».removeAt(index);
		  	  hasChangedSC.add(currentElement);
		  	  «IF me instanceof GraphicalModelElement»
		  	  	currentElement.$isDirty = false;
  	  		  «ENDIF»
		  	}
		  «ENDFOR»
		  
			

			
			«FOR compAttr:me.attributesExtended.filter[isModelElement].filter[!isHidden]»
				List<«compAttr.dartFQN»> get«compAttr.name.escapeDart»Values() => currentGraphModel.allElements().where((n)=>n is «compAttr.dartFQN»).map((n)=>n as «compAttr.dartFQN»).toList();
				
				bool is«compAttr.name.lowEscapeDart»Selected(int id) {
					if(currentElement.«compAttr.name.escapeDart» == null) {
						return false;
					}
					«IF compAttr.isList»
						return currentElement.«compAttr.name.escapeDart».any((e) => e.id == id);
					«ELSE»
						return currentElement.«compAttr.name.escapeDart».id == id;
					«ENDIF»
				}
				
				void selection«compAttr.name.lowEscapeDart»Changed(dynamic e) {
					e.preventDefault();
					int id = int.parse(e.target.selectedOptions[0].value);
					if(id==-1){
						currentElement.«compAttr.name.escapeDart» = null;
					} else {
						currentElement.«compAttr.name.escapeDart» = 
							«IF compAttr.isList»
								get«compAttr.name.escapeDart»Values();
							«ELSE»
								get«compAttr.name.escapeDart»Values().firstWhere((n)=>n.id==id);
							«ENDIF»
					}
					hasChangedSC.add(currentElement);
					«IF me instanceof GraphicalModelElement»
						currentElement.$isDirty = false;
					«ENDIF»
				}
			«ENDFOR»
			
			int trackPrimitiveValue(int index, dynamic e)
			{
				return index;
			}
			«FOR attr : me.attributesExtended.filter[isPrimitive].filter[!isHidden]»
				
				void update«attr.name.escapeDart»(«IF attr.isList»idx,«ENDIF»v) {
					currentElement.«attr.name.escapeDart»«IF attr.list»[idx]«ENDIF» = v;
					«IF me instanceof GraphicalModelElement»
						currentElement.$isDirty = true;
					«ENDIF»
				}
			«ENDFOR»
		}
	'''
	}

	def fileNameIdentifiableElementPropertyComponentTemplate(
		ModelElement me) '''«me.propertyElementFileHTML»'''

	def contentIdentifiableElementPropertyComponentTemplate(ModelElement me) {
		val g = me.modelPackage as MGLModel
		'''
			<form class="form-horizontal" style="padding-right: 5px;" (ngSubmit)="valueChanged(null)">
			«IF me.attributesExtended.filter[isPrimitive].empty»
			No properties to display for «me.name.escapeDart».
			«ENDIF»
			«FOR compAttr:me.attributesExtended.filter[isModelElement].filter[!isHidden]»
				<div class="form-group">
					<label for="«compAttr.name.lowEscapeDart»">«compAttr.name»</label>
					<select (blur)="selection«compAttr.name.lowEscapeDart»Changed($event)" «IF compAttr.readOnly»disabled «ENDIF» id="«compAttr.name.lowEscapeDart»" class="form-control">
					<option [selected]="is«compAttr.name.lowEscapeDart»Selected(-1)" value="-1"></option>
					<option *ngFor="let e of get«compAttr.name.escapeDart»Values()" [value]="e.id.toString()" [selected]="is«compAttr.name.lowEscapeDart»Selected(e.id)">{{e.name}}</option>
					</select>
				</div>
			«ENDFOR»
			«FOR attr : me.attributesExtended.filter[isPrimitive].filter[!isHidden]»
				«IF !attr.isList»
					«IF attr.attributeTypeName.equals("EBoolean")»
						<div class="checkbox">
							<label>
							<input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" (ngModelChange)="update«attr.name.escapeDart»($event)" [ngModel]="currentElement.«attr.name.escapeDart»" type="checkbox"> «attr.name»
							</label>
						</div>
					«ELSEIF attr.attributeTypeName.getEnum(g)!==null»
						<div class="form-group">
							<label for="«attr.name.lowEscapeDart»">«attr.name»</label>
							<select «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" id="«attr.name.lowEscapeDart»" class="form-control" (ngModelChange)="update«attr.name.escapeDart»(parse«attr.name»Enum($event))" [ngModel]="currentElement.«attr.name.escapeDart»">
								«FOR lit:attr.attributeTypeName.getEnum(g).literals»
									<option value="«attr.attributeTypeName.fuEscapeDart».«lit.escapeDart»">«lit»</option>
								«ENDFOR»
							</select>
						</div>
					«ELSE»
						<div class="form-group">
						
						    <label for="«attr.name.lowEscapeDart»">«attr.name»</label>
						    «IF attr.isFile»
						    <div *ngIf="uploader_«attr.name.escapeDart».isUploading" style="display: inline-flex;">
						      <div style="margin-right:10px;" class="dime-file-loader"></div><span style="margin:auto;">Uploading...</span>
						    </div>
						    <div
						    *ngIf="uploader_«attr.name.escapeDart».hasError()"
						    class="alert alert-danger" role="alert">
						    {{uploader_«attr.name.escapeDart».errorMessage()}}
						    </div>
						    <input class="form-control-file" ng2-file-select [uploader]="uploader_«attr.name.escapeDart»" «IF attr.readOnly»disabled«ELSE»[disabled]="uploader_«attr.name.escapeDart».isUploading"«ENDIF» type="file" id="«attr.name.lowEscapeDart»">
						    <a style="color:#fff" [href]="currentElement.«attr.name.escapeDart»">{{currentElement.«attr.name.escapeDart»}}</a>
						    «ELSE»
						    	«IF attr.multiline»
						    		<textarea «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" rows="3" (ngModelChange)="update«attr.name.escapeDart»($event)" [ngModel]="currentElement.«attr.name.escapeDart»" type="«attr.htmlType»" class="form-control" id="«attr.name.lowEscapeDart»">
						    		</textarea>
						    	«ELSE»
						    		<input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" (ngModelChange)="update«attr.name.escapeDart»($event)" [ngModel]="currentElement.«attr.name.escapeDart»" type="«attr.htmlType»" class="form-control" id="«attr.name.lowEscapeDart»">
						    	«ENDIF»
						    «ENDIF»
						</div>
					«ENDIF»
				«ELSE»
					«IF attr.attributeTypeName.getEnum(g)!== null»
						<div class="form-group">
						        <label>«attr.name»</label>
						        «IF !attr.readOnly»
						        <a href (click)="addList«attr.name.escapeDart»($event)">
						            <i class="fas fa-plus"></i>
						         </a>
						         «ENDIF»
						         <div class="input-group" *ngFor="let i of currentElement.«attr.name.escapeDart»; let x = index;trackBy: trackPrimitiveValue" style="margin-bottom: 5px;">
						             <select «IF attr.readOnly»disabled «ENDIF»class="form-control" (blur)="valueChanged($event)"  (ngModelChange)="update«attr.name.escapeDart»(x,parse«attr.name»Enum($event))" [ngModel]="currentElement.«attr.name.escapeDart»[x]">
						                 «FOR lit:attr.attributeTypeName.getEnum(g).literals»
						                 	<option value="«attr.attributeTypeName.fuEscapeDart».«lit.escapeDart»">«lit»</option>
						                 «ENDFOR»
						             </select>
						             «IF !attr.readOnly»
						             <span class="input-group-btn">
						                 <button (click)="removeList«attr.name.escapeDart»(x)" class="btn" type="button">
						                     <i class="fas fa-times"></i>
						                 </button>
						             </span>
						             «ENDIF»
						         </div>
						</div>
					«ELSEIF attr.attributeTypeName.equals("EBoolean")»
						<div class="form-group">
						       <label>«attr.name»</label>
						       «IF !attr.readOnly»
						       <a href (click)="addList«attr.name.escapeDart»($event)">
						           <i class="fas fa-plus"></i>
						       </a>
						       «ENDIF»
						       <div class="input-group" *ngFor="let i of currentElement.«attr.name.escapeDart»; let x = index;trackBy: trackPrimitiveValue" style="margin-bottom: 5px;">
						           <div class="checkbox">
						               <label>
						                   <input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" (ngModelChange)="update«attr.name.escapeDart»(x,$event)" [ngModel]="currentElement.«attr.name.escapeDart»[x]" type="checkbox">
						                   «IF !attr.readOnly»
						                   <button (click)="removeList«attr.name.escapeDart»(x)" class="btn" type="button">
						                       <i class="fas fa-times"></i>
						                   </button>
						                   «ENDIF»
						               </label>
						           </div>
						       </div>
						</div>
					«ELSE»
						<div class="form-group">
						      	<label>«attr.name»</label>
								«IF !attr.readOnly»
									«IF attr.isFile»
										<div *ngIf="uploader_«attr.name.escapeDart».isUploading" style="display: inline-flex;">
										  <div style="margin-right:10px;" class="dime-file-loader"></div><span style="margin:auto;">Uploading...</span>
										</div>
										<div
										    *ngIf="uploader_«attr.name.escapeDart».hasError()"
										    class="alert alert-danger" role="alert">
										    {{uploader_«attr.name.escapeDart».errorMessage()}}
										</div>
										<input class="form-control-file" ng2-file-select «IF !attr.getFile.value.empty»accept="«attr.getFile.value.map['''.«it»'''].join(", ")»" «ENDIF»[uploader]="uploader_«attr.name.escapeDart»" «IF attr.readOnly»disabled«ELSE»[disabled]="uploader_«attr.name.escapeDart».isUploading"«ENDIF» type="file" id="«attr.name.lowEscapeDart»">
									«ELSE»
										<a href (click)="addList«attr.name.escapeDart»($event)" >
										    <i class="fas fa-plus"></i>
										</a>
									«ENDIF»
								«ENDIF»
						      	<div class="input-group" *ngFor="let i of currentElement.«attr.name.escapeDart»; let x = index; trackBy: trackPrimitiveValue" style="margin-bottom: 5px;">
						      		«IF attr.isFile»
						      			<a style="color:#fff" [href]="currentElement.«attr.name.escapeDart»[x]">{{currentElement.«attr.name.escapeDart»[x]}}</a>
						      		«ELSE»
						      			<input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" [(ngModel)]="currentElement.«attr.name.escapeDart»[x]" type="«attr.htmlType»" class="form-control">
						      	    «ENDIF»
						      	    «IF !attr.readOnly»
						      	    	<span class="input-group-btn">
						      	    		<button (click)="removeList«attr.name.escapeDart»(x)" class="btn" type="button">
						      	    			<i class="fas fa-times"></i>
						      	    		</button>
						      	    	</span>
						      	    «ENDIF»
						      	</div>
						</div>
					«ENDIF»
				«ENDIF»
			«ENDFOR»
			</form>
		'''
	}
}
