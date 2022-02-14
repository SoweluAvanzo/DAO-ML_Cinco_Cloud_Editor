import 'fileuploader.dart';
import 'dart:html';

import 'package:angular/angular.dart';
import 'package:angular/core.dart';

@Directive(selector: '[ng2-file-select]')
class FileSelect {

  Element element;

  FileSelect(this.element) {
  }

  @Input('uploader')
  FileUploader uploader;

  getOptions() {
    return this.uploader.options;
  }

  getFilters() {
  }

  bool isEmptyAfterSelection() {
    return !!this.element.attributes.containsKey('multiple');
  }

  @HostListener('change')
  void onChange() {
    InputElement input = this.element;
    List<File> files = input.files;
    var options = this.getOptions();
    var filters = this.getFilters();
    this.uploader.addToQueue(files, options, filters);
  }
}
