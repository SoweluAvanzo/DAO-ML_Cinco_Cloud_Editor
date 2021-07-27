package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.palette.list

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class ListComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameListComponent()'''list_component.dart'''
	
	def contentListComponent()
	'''
	import 'package:angular/angular.dart';
	import 'dart:html';
	import 'dart:js' as js;
	
	import 'list_view.dart';
	
	
	@Component(
	    selector: 'list',
	    templateUrl: 'list_component.html',
	    directives: const [coreDirectives],
	    styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css']
	)
	class ListComponent implements OnInit, AfterViewChecked, OnChanges {
	
	  @Input()
	  List<MapList> map;
	  
	  @Input()
	  bool canEdit = false;
	  
	  bool isDefault = true;
	  
	  bool loaded = false;
	  
	  void setDefault(bool d) {
	  	loaded = false;
	  	isDefault = d;
	  	window.localStorage['PYRO_PALETTE_DEFAULT'] = d?'true':'false';
	  }
	  void ngOnInit() {
	  	if(window.localStorage.containsKey('PYRO_PALETTE_DEFAULT')) {
		  	isDefault = window.localStorage['PYRO_PALETTE_DEFAULT']=='true';
		} else {
		  	isDefault = true;
		}
	  }
	  void ngAfterViewChecked() {
	      if(!loaded&&
	          map.every((MapList m)=>m.values.every((MapListValue mlv)=>querySelector('#wysiwig${mlv.identifier.replaceAll('\.','_')}')!=null))
	      )
	      {
	          loaded = true;
	          //trigger
	          map.forEach((m)=>m.values.forEach((mlv){
	            js.context.callMethod('build_palette_${mlv.identifier.replaceAll('\.','_')}',[]);
	          }));
	      }
	  }
	  
	  void ngOnChanges(Map<String, SimpleChange> changes) {
	      if(changes.containsKey('map')) {
	      	isDefault = true;
	        loaded = false;
	      }
	  }
	
	  void openEntry(MapList mapList,dynamic e)
	  {
	    e.preventDefault();
	    mapList.open = !mapList.open;
	  }
	
	  void expandAll(dynamic e)
	  {
	    e.preventDefault();
	    map.forEach((n) => n.open=true);
	  }
	
	  void collapseAll(dynamic e)
	  {
	    e.preventDefault();
	    map.forEach((n) => n.open=false);
	  }

	}
	
	'''
}
