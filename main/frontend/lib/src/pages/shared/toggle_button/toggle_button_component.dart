import 'dart:async';

import 'package:ng_bootstrap/ng_bootstrap.dart';
import 'package:angular/angular.dart';

@Component(
  selector: 'toggle-button',
  templateUrl: 'toggle_button_component.html',
  styleUrls: const ['toggle_button_component.css'],
  directives: const [coreDirectives, BsTooltipComponent],
)
class ToggleButtonComponent {
  
  @Input("enabled")
  bool enabled;
  
  final changedSC = new StreamController();
  @Output() Stream get changed => changedSC.stream;
  
  ToggleButtonComponent() {
  	enabled = false;
  }
}
