import 'package:angular/angular.dart';
import 'dart:js' as js;
import 'dart:html' as html;

import 'package:FlowGraphTool/src/model/check.dart';
import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/service/check_service.dart';

import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;


@Component(
    selector: 'check',
    templateUrl: 'check_component.html',
    directives: const [coreDirectives,CheckEntryComponent],
    styleUrls: const ['package:FlowGraphTool/src/pages/editor/editor_component.css'],
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
	template: '''
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
	''',
	directives: const [coreDirectives],
	styleUrls: const ['package:FlowGraphTool/src/pages/editor/editor_component.css']
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
			if(currentGraphModel.$type == "flowgraph.FlowGraphDiagram") {
					methodCall = "flowgraphdiagram_jump";
			}
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



