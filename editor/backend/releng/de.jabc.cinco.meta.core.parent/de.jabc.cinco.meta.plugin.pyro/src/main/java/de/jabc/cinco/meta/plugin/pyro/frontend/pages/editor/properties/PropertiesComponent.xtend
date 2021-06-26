package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

@Deprecated
class PropertiesComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNamePropertiesComponent()'''properties_component.dart'''
	
	def contentPropertiesComponent()
	'''
	import 'package:angular/angular.dart';
	import 'dart:async';
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	import 'package:«gc.projectName.escapeDart»/src/model/tree_view.dart';
	import 'package:«gc.projectName.escapeDart»/src/model/message.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/properties/tree/tree_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/properties/property/property_component.dart';
	
	@Component(
	    selector: 'properties',
	    templateUrl: 'properties_component.html',
	    directives: const [coreDirectives,TreeComponent,PropertyComponent],
	    styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css']
	)
	class PropertiesComponent implements OnInit, OnChanges {
	
	  @Input()
	  IdentifiableElement currentGraphElement;
	  @Input()
	  GraphModel currentGraphModel;
	
	  final hasPropertiesChangedSC = new StreamController();
	  @Output() Stream get hasChanged => hasPropertiesChangedSC.stream;
	
	  PyroElement currentElement;
	
	
	  @override
	  void ngOnInit()
	  {
		if(currentGraphElement!=null) {
	      currentElement = currentGraphElement;
	    }
	  }
	  
	  @override
	  ngOnChanges(Map<String, SimpleChange> changes) {
	      if(changes.containsKey('currentGraphElement')) {
	            currentElement=currentGraphElement;
			}
	  }
	
	  ///triggered if an element is edited
	  void hasChangedValues(PyroElement element)
	  {
	    //todo persist attributes of currentGraphElement recursive
	    PropertyMessage pm = new PropertyMessage();
	    pm.graphModelId = currentGraphElement.id;
	    pm.delegate = currentGraphElement;
	    hasPropertiesChangedSC.add(pm);
	  }
	
	    /// triggerd if elements are created
	    void hasChanged(TreeNode element)
	    {
	        //todo persist attributes of element recursive
	      if(element.parent!=null){
	        print(element.parent);
	      }
	      print(element);
	      PropertyMessage pm = new PropertyMessage();
	      pm.graphModelId = currentGraphElement.id;
	      pm.delegate = currentGraphElement;
	      hasPropertiesChangedSC.add(pm);
	  
	    }
	  
	    /// triggerd if elements are removed
	    void hasRemoved(PyroElement element)
	    {
	      if(currentElement==element){
	        currentElement = currentGraphElement;
	      }
	      PropertyMessage pm = new PropertyMessage();
	      pm.graphModelId = currentGraphElement.id;
	      pm.delegate = currentGraphElement;
	      hasPropertiesChangedSC.add(pm);
	  
	    }
	
	  /// triggered if a new node is selected
	  void hasSelection(TreeNode node)
	  {
	    print("selectiont ${node}");
	    currentElement = node.delegate;
	  }
	
	}
	
	'''
	
}
