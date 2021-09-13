import 'package:angular/angular.dart';
import 'dart:async';

import '../../../model/core.dart';
import '../../../model/tree_view.dart';
import '../../../model/message.dart';
import 'tree/tree_component.dart';
import 'property/property_component.dart';

@Component(
    selector: 'properties',
    templateUrl: 'properties_component.html',
    directives: const [coreDirectives,TreeComponent,PropertyComponent],
    styleUrls: const ['../editor_component.css']
)
class PropertiesComponent implements OnInit, OnChanges {

  @Input()
  IdentifiableElement currentGraphElement;
  
  @Input()
  GraphModel currentGraphModel;
  @Input()
  PyroUser user;
  @Input()
  bool isModal = false;

  final hasPropertiesChangedSC = new StreamController();
  @Output() Stream get hasChanged => hasPropertiesChangedSC.stream;
  
  final hasModalClosedSC = new StreamController();
  @Output() Stream get hasClosed => hasModalClosedSC.stream;

  PyroElement currentElement;
  
  bool show = false;

  PropertiesComponent()
  {
  }

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
            if(currentElement!=null) {
            	if(currentElement is IdentifiableElement) {
                IdentifiableElement ce = currentElement;
            		if(ce.$isDirty != null && ce.$isDirty) {
            			_hasChangedValues(ce);
            			ce.$isDirty = false;
            		}
            		if(isModal) {
            			show = true;
            		}
            	}
            	
            } else {
            	show = false;
            }
            currentElement=currentGraphElement;
		}
      if(changes.containsKey('currentGraphModel')) {
	        if(currentElement!=null) {
            	if(currentElement is IdentifiableElement) {
                IdentifiableElement ce = currentElement;
                if(ce.$isDirty != null && ce.$isDirty) {
                  _hasChangedValues(ce);
                  ce.$isDirty = false;
                }
            	}
            	
            }
	        currentElement=currentGraphModel;
      }
  }
  
  void close() {
  	show = false;  	
  }
  
  void showModal() {
  	show = true;
  	
  }

  ///triggered if an element is edited
  void hasChangedValues(PyroElement element)
  {
    //todo persist attributes of currentGraphElement recursive
    PropertyMessage pm = new PropertyMessage(
        currentGraphModel.id,
        currentGraphModel.$type(),
        currentGraphElement,
        user.id
    );
    hasPropertiesChangedSC.add(pm);
  }
  
  ///triggered if an element is edited
  void _hasChangedValues(PyroElement element)
  {
    //todo persist attributes of currentGraphElement recursive
    PropertyMessage pm = new PropertyMessage(
        currentGraphModel.id,
        currentGraphModel.$type(),
        element,
        user.id
    );
    hasPropertiesChangedSC.add(pm);
  }

    /// triggerd if elements are created
    void hasChanges(TreeNode element)
    {
      //todo persist attributes of element recursive
      if(element.parent!=null){
        print(element.parent);
      }
      PropertyMessage pm = new PropertyMessage(
          element.root.id,
          element.root.$type(),
          element.root,
          user.id
      );
      hasPropertiesChangedSC.add(pm);
    }
  
    /// triggerd if elements are removed
    void hasRemoved(PyroElement element)
    {
      if(currentElement==element){
        currentElement=currentGraphElement;
      }
      PropertyMessage pm = new PropertyMessage(
          currentGraphModel.id,
          currentGraphModel.$type(),
          currentGraphElement,
          user.id
      );
      hasPropertiesChangedSC.add(pm);
    }

  /// triggered if a new node is selected
  void hasSelection(TreeNode node)
  {
    print("selectiont ${node}");
    currentGraphElement = node.root;
    currentElement = node.delegate;
  }
}