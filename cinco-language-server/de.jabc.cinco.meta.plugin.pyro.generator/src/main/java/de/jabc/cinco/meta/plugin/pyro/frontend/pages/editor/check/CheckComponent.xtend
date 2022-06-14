package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.check

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class CheckComponent extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameCheckComponent() '''check_component.dart'''

	def contentCheckComponent() '''
		import 'package:angular/angular.dart';
		import 'dart:js' as js;
		import 'dart:html' as html;
		
		import 'package:«gc.projectName.escapeDart»/src/model/check.dart';
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
		import 'package:«gc.projectName.escapeDart»/src/service/check_service.dart';
		
		«FOR g:gc.mglModels»
			import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
		«ENDFOR»
		
		
		@Component(
		    selector: 'check',
		    templateUrl: 'check_component.html',
		    directives: const [coreDirectives,CheckEntryComponent],
		    styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css'],
		)
		class CheckComponent implements OnInit, OnChanges {
		
		  @Input()
		  GraphModel currentGraphModel;
		  
		  final CheckService _checkService;
		  
		  List<CheckResult> checkResults = new List();
		  
		  bool loading = false;
		  bool isError = true;
		  bool isWarning = true;
		  bool isInfo = true;
		  
		  CheckComponent(CheckService this._checkService){}
		
		  @override
		     void ngOnInit()
		     {
				if(html.window.localStorage.containsKey('PYRO_CHECK_IS_ERROR')){
					this.isError = html.window.localStorage['PYRO_CHECK_IS_ERROR']=='true';
				} else {
					this.isError = true;
				}
				if(html.window.localStorage.containsKey('PYRO_CHECK_IS_WARNING')){
					this.isWarning = html.window.localStorage['PYRO_CHECK_IS_WARNING']=='true';
				} else {
					this.isWarning = true;
				}
				if(html.window.localStorage.containsKey('PYRO_CHECK_IS_INFO')){
					this.isInfo = html.window.localStorage['PYRO_CHECK_IS_INFO']=='true';
				} else {
					this.isInfo = true;
				}
		       if(currentGraphModel!=null){
		         triggerMap(currentGraphModel);
		         _checkService.listen(currentGraphModel.id).listen((CheckResults crs){
		         	checkResults = crs.results;
		         });
		       }
		     }
		     
		     void toggleError() {
		     	isError = !isError;
		     	html.window.localStorage['PYRO_CHECK_IS_ERROR'] = isError?'true':'false';
		     }
		     void toggleWarning() {
			 	isWarning = !isWarning;
			 	html.window.localStorage['PYRO_CHECK_IS_WARNING'] = isWarning?'true':'false';
			 }
			 void toggleInfo() {
			 	isInfo = !isInfo;
			 	html.window.localStorage['PYRO_CHECK_IS_INFO'] = isInfo?'true':'false';
			 }
		 
			 @override
			 ngOnChanges(Map<String, SimpleChange> changes) {
			   if(changes.containsKey("currentGraphModel")){
			     var value = changes["currentGraphModel"].currentValue;
			     if(value!=null){
			      triggerMap(value);
			      _checkService.listen(currentGraphModel.id).listen((CheckResults crs){
			     	checkResults = crs.results;
			     });
			   }
			 }
		   }
		   
		   void triggerMap(GraphModel g) {
			if(loading || g == null) {
				return;
			}
			loading = true;
			«FOR g : gc.graphMopdels.filter[it.hasChecks] SEPARATOR " else "
			»if(g.$type() == "«g.typeName»"){
				//read checks for «g.name.escapeDart»
				checkResults.clear();
				_checkService.read(g).then((cr){checkResults=cr==null?[]:cr.results;}).whenComplete((){loading=false;});
			}«
			ENDFOR»
		  }
		
			updateChecks() {
				triggerMap(currentGraphModel);
			}
			
			List<CheckResult> getCheckResults() {
				return checkResults.where((cr){
					return (isWarningPresent(cr)&&isWarning) || (isErrorPresent(cr)&&isError) || (isInfoPresent(cr)&&isInfo);
				}).toList();
			}
			
			bool isWarningPresent(CheckResult r) => r.results.where((n)=>n.type=='warning').isNotEmpty;
			bool isErrorPresent(CheckResult r) => r.results.where((n)=>n.type=='error').isNotEmpty;
			bool isInfoPresent(CheckResult r) => r.results.where((n)=>n.type=='info').isNotEmpty;
		}
		
		@Component(
			selector: 'check-entry',
			template: «"'''"»
			<div class="card text-white rounded-0 {{getContextualClass()}}">
				<div class="card-header" style="border-bottom: 0">
					<strong (click)="collapse(\$event)" class="cursor-pointer" >
						<i class="fas fa-fw" [class.fa-chevron-down]="isOpen" [class.fa-chevron-right]="!isOpen"></i> 
				<i class="fas fa-fw"
						  	[class.fa-times-circle]="isErrorPresent()"
						  	[class.fa-exclamation-circle]="isWarningPresent()&&!isErrorPresent()"
						  	[class.fa-info-circle]="!isWarningPresent()&&!isErrorPresent()"
				></i> {{result.name}}
			 </strong>
					 <strong *ngIf="currentGraphModel.id!=result.id" class="cursor-pointer pull-right" (click)="jumpToElement(\$event)"><i class="fas fa-crosshairs"></i></strong>
			</div>
			  	<div class="card-body p-0" *ngIf="isOpen">
				<ul class="list-group list-group-flush">
				  <li class="list-group-item"
				  	*ngFor="let res of getResults()"
				  	[class.list-group-item-info]="res.type=='info'"
				  	[class.list-group-item-warning]="res.type=='warning'"
				  	[class.list-group-item-danger]="res.type=='error'"
				  	><i class="fas fa-fw"
				  	[class.fa-times-circle]="res.type=='error'"
				  	[class.fa-exclamation-circle]="res.type=='warning'"
				  	[class.fa-info-circle]="res.type=='info'"
				  	></i> {{res.message}}</li>
				</ul>
				</div>
			</div>
			«"'''"»,
			directives: const [coreDirectives],
			styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css']
		)
		class CheckEntryComponent {
			
			@Input()
			CheckResult result;
			
			@Input()
			GraphModel currentGraphModel;
			
			@Input()
			bool isError;
			@Input()
			bool isWarning;
			@Input()
			bool isInfo;
			
			bool isOpen = false;
			
			void collapse(e) {
				e.preventDefault();
				isOpen = !isOpen;
			}
			
			void jumpToElement(e) {
				e.preventDefault();
				if(currentGraphModel.id!=result.id) {
					var methodCall = null;
					«FOR g : gc.graphMopdels.filter[!isAbstract] SEPARATOR " else "
					»if(currentGraphModel.$type() == "«g.typeName»") {
							methodCall = "«g.jsCall»_jump";
					}«
					ENDFOR»
					js.context.callMethod(methodCall, [
						result.id
					]);
				}
			}
			
			String getContextualClass() {
				if (!isWarningPresent()&&!isErrorPresent()) {
					return "bg-info";
				} else if (isWarningPresent()&&!isErrorPresent()) {
					return "bg-warning";
				} else if (isErrorPresent()) {
					return "bg-danger";
				}
			}
			
			List<Result> getResults() {
				return result.results.where((n){
					return (n.type=='warning'&&isWarning)||
					(n.type=='error'&&isError) ||
					(n.type=='info'&&isInfo);
				}).toList();
			}
			
			isWarningPresent() => getResults().where((n)=>n.type=='warning').isNotEmpty;
			isErrorPresent() => getResults().where((n)=>n.type=='error').isNotEmpty;
			isInfoPresent() => getResults().where((n)=>n.type=='info').isNotEmpty;
		}
		

		
	'''

	def fileNameCheckComponentTemplate() '''check_component.html'''

	def contentCheckComponentTemplate() '''
		<div class="card pyro-panel d-flex flex-column h-100" *ngIf="currentGraphModel!=null">
		    <div class="card-header d-flex flex-row align-items-center pyro-panel-heading">
		    	<div class="btn-group btn-group-sm">
		    		<button type="button" (click)="toggleError()" [class.active]="isError" class="btn btn-sm btn-outline-danger">Errors</button>
		    		<button type="button" (click)="toggleWarning()" [class.active]="isWarning" class="btn btn-sm btn-outline-warning">Warnings</button>
		    		<button type="button" (click)="toggleInfo()" [class.active]="isInfo" class="btn btn-sm btn-outline-info">Infos</button>
		    	</div>
		        <button [disabled]="loading" data-toggle="tooltip" data-placement="bottom" title="Refresh checks" class="ml-auto btn btn-sm" (click)="updateChecks()">
		        	<i class="fas fa-sync-alt"></i>
				</button>
		    </div>
		    <div class="card-body pyro-panel-body p-0" style="overflow:auto;">
		    	<div *ngIf="loading" class="progress">
		    	  <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
		    	  </div>
		    	</div>
				<template [ngIf]="currentGraphModel!=null&&!loading">
					<check-entry
						*ngFor="let check of getCheckResults()"
						[result]="check"
						[isError]="isError"
						[isWarning]="isWarning"
						[isInfo]="isInfo"
						[currentGraphModel]="currentGraphModel"
					>
					</check-entry>
				</template>
				<div *ngIf="!loading&&checkResults.isEmpty" class="alert alert-success mb-0 rounded-0"><strong>No Errors</strong></div>
			</div>
		</div>
	'''

}
