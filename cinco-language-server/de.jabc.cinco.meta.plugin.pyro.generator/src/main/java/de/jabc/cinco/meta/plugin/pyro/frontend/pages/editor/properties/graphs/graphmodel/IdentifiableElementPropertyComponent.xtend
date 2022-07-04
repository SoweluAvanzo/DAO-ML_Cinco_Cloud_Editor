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
		import 'dart:js' as js;
		
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
			styleUrls: const ['../../../editor_component.css'],
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
				}, autoUpload:true«IF !file.getFile.value.empty», accept:"«file.getFile.value.join(", ")»"«ENDIF»);
			«ENDFOR»
			
			«me.name.fuEscapeDart»PropertyComponent() {
			  	«FOR file:fileAttributes»
			  		uploader_«file.name.escapeDart».newFileStream.listen((fr){
			  			«IF file.isList»
			  				currentElement.attrFiles.add(fr.path);
			  			«ELSE»
			  				currentElement.«file.name.escapeDart» = fr.path;
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
			«IF !fileAttributes.empty»
				
				String getDownloadPath(path) {
					return FileReference.toDownloadPath(path);
				}
				
				bool connectedToTheia() {
					return js.context.callMethod('connectedToTheia');
				}
				
				String getTheiaFileReferenceName(String filePath) {
					// TODO: currently a bit dirty. Should receive workspace basePath from Backend
					const pathSeparator = [ '\\', '/', '\\\\'];
					for(var ps in pathSeparator) {
					  if(filePath.contains('editor'+ps+'workspace'+ps)) {
					    return filePath.split('editor'+ps+'workspace'+ps)[1];
					  }
					}
					return filePath;
				}
				
				void parseFile(dynamic event, Function foo) {
					var file = js.JsObject.fromBrowserObject(event)['dataTransfer'];
					if(file != null) {
						foo(file);
					}
				}
				
				void openFilePicker(String textFieldId, bool multi, dynamic fileTypes, callback) {
					js.context.callMethod('openFilePicker', [fileTypes, multi, callback]);
				}
				
				«FOR attr: fileAttributes»
					«{
						val textFieldId = '''textfield_«me.typeNameUnderScore»_«attr.name.escapeDart»'''
						val onDropCall = '''onDrop«attr.name.fuEscapeDart»'''
						val openFilePickerCall = '''openFilePicker_textfield_«me.typeNameUnderScore»_«attr.name.escapeDart»'''
						val isEmptyCall = '''isEmpty«attr.name.fuEscapeDart»'''
						val removeCall = '''remove«attr.name.fuEscapeDart»'''
						val clearCall = '''clear«attr.name.fuEscapeDart»'''
						val handleCall = '''handle«attr.name.fuEscapeDart»'''
						val types = attr.getFile.value.map['''«it»''']
						val isMulti = '''«IF attr.isList»true«ELSE»false«ENDIF»'''
						'''
							
							void «openFilePickerCall»() {
								var types = js.JsArray.from([
									«IF types.empty»
										"*"
									«ELSE»
										«FOR t : types SEPARATOR ","
											»"«t»"«
										ENDFOR»
									«ENDIF»
								]);
								openFilePicker("«textFieldId»", «isMulti», types, (files) {
								  «handleCall»(files);
								});
							}
							
							void «onDropCall»(dynamic event) {
								parseFile(event, (file) {
								«IF attr.isList»
									currentElement.«attr.name.escapeDart».add(file['filePath']);
									hasChangedSC.add(currentElement);
								«ELSE»
								  «handleCall»([file]);
								«ENDIF»
								});
							}
							
							void «handleCall»(List files) {
								if(!files.isEmpty) {
									print("Callback successfull! - file: ");
									«IF attr.isList»
										for(var i=0; i<files.length; i++) {
											print(files[i]['filePath']);
											currentElement.«attr.name.escapeDart».add(files[i]['filePath']);
										}
									«ELSE»
										print(files[0]['filePath']);
										currentElement.«attr.name.escapeDart» = files[0]['filePath'];
									«ENDIF»
									hasChangedSC.add(currentElement);
								}
							}
							«IF attr.isList»
								
								void «clearCall»() {
									currentElement.«attr.name.escapeDart».clear();
									hasChangedSC.add(currentElement);
								}
							«ENDIF»
							
							void «removeCall»(«IF attr.isList»dynamic i«ENDIF») {
								«IF attr.isList»
									currentElement.«attr.name.escapeDart».removeAt(i);
								«ELSE»
									currentElement.«attr.name.escapeDart» = null;
								«ENDIF»
								hasChangedSC.add(currentElement);
							}
							
							bool «isEmptyCall»() {
								«IF attr.isList»
									return currentElement.«attr.name.escapeDart».isEmpty;
								«ELSE»
									var attr = currentElement.«attr.name.escapeDart»;
									return attr == null || attr == "";
								«ENDIF»
							}
						'''
					}»
				«ENDFOR»
			«ENDIF»
			
			//get for enumeration literals
			«FOR attr:me.attributesExtended.filter[attributeTypeName.getEnum(g)!==null].filter[!isHidden]»
				«attr.dartFQN» parse«attr.name»Enum(String e) {
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
				  currentElement.«attr.name.escapeDart».add(
				  	«IF attr.isFile»
				  		e
				  	«ELSE»
				  		«attr.init(g,'''«g.name.lowEscapeJava».''')»
				  	«ENDIF»
				  );
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
			
			// for each complex list attribute
			«FOR attr : me.attributesExtended.filter[!isPrimitive].filter[isModelElement].filter[isList].filter[!isHidden]»
				void addList«attr.name.escapeDart»(dynamic e) {
				  e.preventDefault();
				  
				  // find first of type as default selected element
				  // (backend generated json-configuration does not support null-values in arrays)
				  var newElem = currentGraphModel.allElements().firstWhere((elem) => 
				    elem != null && 
				      («{
				      	val subtypes = (attr as mgl.ComplexAttribute).type.resolveSubTypesAndType
				      	'''
				      	«FOR subtype:subtypes SEPARATOR "||"
				      	»elem.$type()  == "«subtype.typeName»"«
				      	ENDFOR»
				      	'''
				      }»),
				    orElse: () => null
				  );
				  if(newElem != null) {
				    currentElement.«attr.name.escapeDart».add(newElem);
				    hasChangedSC.add(currentElement);
				  }
				}
				
				void removeList«attr.name.escapeDart»(int index) {
					currentElement.«attr.name.escapeDart».removeAt(index);
					hasChangedSC.add(currentElement);
				}
			«ENDFOR»
			
			// for each complex attribute
			«FOR compAttr:me.attributesExtended.filter(mgl.ComplexAttribute).filter[isModelElement].filter[!isHidden]»
				
				List<«compAttr.dartFQN»> get«compAttr.name.escapeDart»Values() => currentGraphModel.allElements().where((n)=>n is «compAttr.dartFQN»).map((n)=>n as «compAttr.dartFQN»).toList();
				
				bool is«compAttr.name.escapeDart»Selected(«IF compAttr.isList»dynamic elem«ELSE»int id«ENDIF») {
					«IF compAttr.isList»
						if(currentElement.«compAttr.name.escapeDart» == null
						      || elem == null
						      || elem.id == -1
						) {
							return false;
						}
						return currentElement.«compAttr.name.escapeDart».any((e) => e != null && e.id == elem.id);
					«ELSE»
						if(currentElement.«compAttr.name.escapeDart» == null) {
							return false;
						}
						return currentElement.«compAttr.name.escapeDart».id == id;
					«ENDIF»
				}
				
				void selection«compAttr.name.escapeDart»Changed(«IF compAttr.isList»int index, «ENDIF»dynamic e) {
					e.preventDefault();
					int id = int.parse(e.target.selectedOptions[0].value);
					«IF compAttr.isList»
						var changedElement = currentGraphModel.allElements().firstWhere((e) => e.id == id, orElse: null);
						if(changedElement == null) {
						  currentElement.«compAttr.name.escapeDart».removeAt(index);
						} else {
						  currentElement.«compAttr.name.escapeDart»[index] = changedElement;
						}
					«ELSE»
						if(id==-1){
							currentElement.«compAttr.name.escapeDart» = null;
						} else {
							currentElement.«compAttr.name.escapeDart» = 
								get«compAttr.name.escapeDart»Values().firstWhere((n)=>n.id==id);
						}
					«ENDIF»
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
						<label for="«compAttr.name.escapeDart»">«compAttr.name»</label>
						«IF compAttr.isList»
								<a href (click)="addList«compAttr.name.escapeDart»($event)" >
									<i class="fas fa-plus"></i>
								</a>
								<div class="input-group" *ngFor="let i of currentElement.«compAttr.name.escapeDart»; let x = index" style="margin-bottom: 5px;">
									<select class="form-control" (blur)="selection«compAttr.name.escapeDart»Changed(x, $event)"  id="attrnodes" >
										<!--option [selected]="is«compAttr.name.escapeDart»Selected(null)" value="null"></option-->
										<option *ngFor="let e of get«compAttr.name.escapeDart»Values()" [value]="e.id.toString()" [selected]="is«compAttr.name.escapeDart»Selected(e)">
											{{e == null ? "-no reference-" : e.name == null || e.name == "" ? e.id : e.name}}
										</option>
									</select>
									<button (click)="removeList«compAttr.name.escapeDart»(x)" class="btn" type="button">
										<i class="fas fa-times"></i>
									</button>
								</div>
						«ELSE»
							<select (blur)="selection«compAttr.name.escapeDart»Changed($event)" «IF compAttr.readOnly»disabled «ENDIF» id="«compAttr.name.escapeDart»" class="form-control">
								<option [selected]="is«compAttr.name.escapeDart»Selected(-1)" value="-1"></option>
								<option *ngFor="let e of get«compAttr.name.escapeDart»Values()" [value]="e.id.toString()" [selected]="is«compAttr.name.escapeDart»Selected(e.id)">
									{{e.name}}
								</option>
							</select>
						«ENDIF»
					</div>
				«ENDFOR»
				«FOR attr : me.attributesExtended.filter[isPrimitive].filter[!isHidden]»
					«IF attr.attributeTypeName.equals("EBoolean")»
						«IF attr.list»
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
							<div class="checkbox">
								<label>
									<input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" (ngModelChange)="update«attr.name.escapeDart»($event)" [ngModel]="currentElement.«attr.name.escapeDart»" type="checkbox"> «attr.name»
								</label>
							</div>
						«ENDIF»
					«ELSEIF attr.attributeTypeName.getEnum(g)!==null»
						<div class="form-group">
						<label«IF !attr.isList» for="«attr.name.lowEscapeDart»"«ENDIF»>«attr.name»</label>
							«IF attr.isList»
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
							«ELSE»
								<select «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" id="«attr.name.lowEscapeDart»" class="form-control" (ngModelChange)="update«attr.name.escapeDart»(parse«attr.name»Enum($event))" [ngModel]="currentElement.«attr.name.escapeDart»">
									«FOR lit:attr.attributeTypeName.getEnum(g).literals»
										<option value="«attr.attributeTypeName.fuEscapeDart».«lit.escapeDart»">«lit»</option>
									«ENDFOR»
								</select>
							«ENDIF»
						</div>
					«ELSE»
						<div class="form-group">
						<label«IF !attr.isList» for="«attr.name.lowEscapeDart»"«ENDIF»>«attr.name»</label>
						«IF attr.isFile»
							«{
								val textFieldId = '''textfield_«me.typeNameUnderScore»_«attr.name.escapeDart»'''
								val onDropCall = '''onDrop«attr.name.fuEscapeDart»($event)'''
								val openFilePickerCall = '''openFilePicker_textfield_«me.typeNameUnderScore»_«attr.name.escapeDart»()'''
								val isEmptyCall = '''isEmpty«attr.name.fuEscapeDart»()'''
								val removeCall = '''remove«attr.name.fuEscapeDart»(«IF attr.isList»x«ENDIF»)'''
								val clearCall = '''clear«attr.name.fuEscapeDart»'''
								'''
									<!-- theia-based file handling -->
									<div *ngIf="connectedToTheia()">
										<div class="file-drop-area" title="select file" (drop)="«onDropCall»">
											<span class="choose-file-button">Select File«IF attr.list»(s)«ENDIF»</span>
											<span id="«textFieldId»" class="file-message">drag and drop</span>
											<input class="file-input" type="button" (click)="«openFilePickerCall»">
										</div>
										<div class="file-area">
											<div *ngIf="«isEmptyCall»" class="file-message">(no file)</div>
											<div *ngIf="!«isEmptyCall»">
												«IF attr.list»
													<button (click)="«clearCall»()" class="btn btn-delete" type="button" style="margin-bottom: 5px;">
														Clear All
													</button>
													<div *ngFor="let i of currentElement.«attr.name.escapeDart»; let x = index; trackBy: trackPrimitiveValue" style="margin-bottom: 5px;">
														<button (click)="«removeCall»" class="btn btn-delete" type="button">
															<i class="fas fa-times"></i>
														</button>
														<span id="«textFieldId»" class="file-message">{{getTheiaFileReferenceName(currentElement.«attr.name.escapeDart»[x])}}</span>
													</div>
												«ELSE»
													<button (click)="«removeCall»" class="btn btn-delete" type="button">
														<i class="fas fa-times"></i>
													</button>
													<span id="«textFieldId»" class="file-message">{{getTheiaFileReferenceName(currentElement.«attr.name.escapeDart»)}}</span>
												«ENDIF»
											</div>
										</div>
									</div>
								'''
							}»
							<!-- classic file handling -->
							<div *ngIf="!connectedToTheia()">
								<div *ngIf="uploader_«attr.name.escapeDart».isUploading" style="display: inline-flex;">
									<div style="margin-right:10px;" class="dime-file-loader"></div><span style="margin:auto;">Uploading...</span>
								</div>
								<div
								    *ngIf="uploader_«attr.name.escapeDart».hasError()"
								    class="alert alert-danger" role="alert">
								    {{uploader_«attr.name.escapeDart».errorMessage()}}
								</div>
								
								<input class="form-control-file" ng2-file-select «IF !attr.getFile.value.empty»accept="«attr.getFile.value.map['''.«it»'''].join(", ")»" «ENDIF»[uploader]="uploader_«attr.name.escapeDart»" «IF attr.readOnly»disabled«ELSE»[disabled]="uploader_«attr.name.escapeDart».isUploading"«ENDIF» type="file" id="«attr.name.lowEscapeDart»">
								«IF attr.isList»
									<div class="input-group" *ngFor="let i of currentElement.«attr.name.escapeDart»; let x = index; trackBy: trackPrimitiveValue" style="margin-bottom: 5px;">
										<a style="color:#fff" [href]="getDownloadPath(currentElement.«attr.name.escapeDart»[x])">{{currentElement.«attr.name.escapeDart»[x]}}</a>
									    <span class="input-group-btn">
									    	<button (click)="removeList«attr.name.escapeDart»(x)" class="btn" type="button">
									    		<i class="fas fa-times"></i>
									    	</button>
									    </span>
									</div>
								«ELSE»
									<a style="color:#fff" [href]="getDownloadPath(currentElement.«attr.name.escapeDart»)">{{currentElement.«attr.name.escapeDart»}}</a>
								«ENDIF»
							</div>
						«ELSE»
							«IF attr.isList»
								«IF !attr.readOnly»
									<a href (click)="addList«attr.name.escapeDart»($event)" >
									    <i class="fas fa-plus"></i>
									</a>
								«ENDIF»
								<div class="input-group" *ngFor="let i of currentElement.«attr.name.escapeDart»; let x = index; trackBy: trackPrimitiveValue" style="margin-bottom: 5px;">
									<input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" [(ngModel)]="currentElement.«attr.name.escapeDart»[x]" type="«attr.htmlType»" class="form-control">
								    «IF !attr.readOnly»
								    	<span class="input-group-btn">
								    		<button (click)="removeList«attr.name.escapeDart»(x)" class="btn" type="button">
								    			<i class="fas fa-times"></i>
								    		</button>
								    	</span>
								    «ENDIF»
								</div>
							«ELSE»
								«IF attr.multiline»
									<textarea «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" rows="3" (ngModelChange)="update«attr.name.escapeDart»($event)" [ngModel]="currentElement.«attr.name.escapeDart»" type="«attr.htmlType»" class="form-control" id="«attr.name.lowEscapeDart»">
									</textarea>
								«ELSE»
									<input «IF attr.readOnly»disabled «ENDIF»(blur)="valueChanged($event)" (ngModelChange)="update«attr.name.escapeDart»($event)" [ngModel]="currentElement.«attr.name.escapeDart»" type="«attr.htmlType»" class="form-control" id="«attr.name.lowEscapeDart»">
								«ENDIF»
							«ENDIF»
						«ENDIF»
						</div>
					«ENDIF»
				«ENDFOR»
			</form>
		'''
	}
}
