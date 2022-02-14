import 'package:angular/angular.dart';

@Pipe("normalizeEnumString")
class NormalizeEnumStringPipe implements PipeTransform {
  String transform(String value) {
    return value.split("_").map((v) {
    	return v.substring(0, 1) + v.substring(1, v.length).toLowerCase();
    }).join(" ");
  }
}
