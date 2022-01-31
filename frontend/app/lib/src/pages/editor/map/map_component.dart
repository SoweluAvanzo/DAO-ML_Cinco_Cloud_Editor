import 'package:angular/angular.dart';
import '../../../model/core.dart';
import 'dart:js' as js;
		
@Component(
    selector: 'map',
    templateUrl: 'map_component.html',
    directives: const [coreDirectives],
    styleUrls: const ['../editor_component.css']
)
class MapComponent implements OnChanges, AfterViewInit {

  @Input()
  GraphModel currentGraphModel;
  
  @override
  ngOnChanges(Map<String, SimpleChange> changes) {
    if(changes.containsKey("currentGraphModel")) {
      var value = changes["currentGraphModel"].currentValue;
      if(value!=null) {
        triggerMap(value);
      }
    }
  }
  
  @override
  ngAfterViewInit() {
    triggerMap(currentGraphModel);
  }
    
  void triggerMap(GraphModel g) {
    if(g == null)
      return;
    var graphModelType = g.$lower_type();
    js.context.callMethod('create_${graphModelType}_map', []);
  }
    
  bool check() {
    return currentGraphModel!=null;
  }
}
