import 'package:angular/angular.dart';

import 'dart:async';
import 'dart:convert';
import 'dart:html';
import 'dart:math';

import 'filelikeobject.dart';
import 'fileitem.dart';
import '../service/base_service.dart';

bool isFile(value) {
  return (value is File);
}

isFileLikeObject(value) {
  return value is FileLikeObject;
}

class FileUploader {
  String url;
  String authToken;
  bool isUploading = false;
  List<FileItem> queue;
  num progress = 0;
  bool autoUpload = false;
  bool isHTML5 = true;
  bool removeAfterUpload = false;
  int queueLimit;
  List<FileReference> fileReferences;
  num nextIndex = 0;
  List filters;
  num _failFilterIndex;
  Map options;
  bool _error;
  String _errorMessage;
  String accept;
  StreamController<FileReference> newFileStreamCtrl = new StreamController();
  Stream<FileReference> get newFileStream => newFileStreamCtrl.stream;

  FileUploader(Map this.options,
      {bool this.autoUpload: false, String this.accept: null}) {
    this._error = false;
    this.queueLimit = -1;
    if (options.containsKey('url')) {
      this.url = options['url'];
    }
    this.fileReferences = new List<FileReference>();
    if (options.containsKey('authToken')) {
      this.authToken = options['authToken'];
    }
    this.queue = new List();
    this.filters = new List();
  }

  String get percentProgress {
    return '${this.progress.toString()}%';
  }

  addAlreadyUploadedFiles(List<FileReference> files) {
    for (var file in files) {
      this.addAlreadyUploadedFile(file);
    }
    this.progress = 100;
    this._render();
  }

  List<FileReference> getFileReferences() {
    List<FileReference> fileReferences = new List<FileReference>();
    for (var file in this.queue) {
      if (file.fileReference != null) {
        fileReferences.add(file.fileReference);
      }
    }
    return fileReferences;
  }

  addAlreadyUploadedFile(FileReference file) {
    FileItem fileItem = new FileItem(this, null, null);
    fileItem.fileReference = file;
    fileItem.isUploaded = true;
    fileItem.isSuccess = true;
    fileItem.progress = 100;
    this.queue.add(fileItem);
    this.progress = 100;
    this._render();
  }

  addToQueue(List<File> files, dynamic options, dynamic filters) {
    List list = new List();
    list.addAll(files);
    _error = false;

    if (this.queueLimit >= this.queue.length) {
      this.clearQueue();
    }

    var ListOfFilters = this._getFilters(filters);
    int count = this.queue.length;
    List addedFileItems = new List();

    list.forEach((some) {
      FileLikeObject temp = new FileLikeObject(some);

      if (this._isValidFile(temp, [], options)) {
        FileItem fileItem = new FileItem(this, some, options);
        fileItem.file = temp;
        addedFileItems.add(fileItem);
        this.queue.add(fileItem);
        this._onAfterAddingFile(fileItem);
      } else {
        var filter = ListOfFilters[this._failFilterIndex];
        this.onWhenAddingFileFailed(temp, filter, options);
      }
    });

    if (this.queue.length != count) {
      this._onAfterAddingAll(addedFileItems);
      this.progress = this._getTotalProgress(this.progress);
    }

    this._render();

    if (this.autoUpload) {
      this.uploadAll();
    }
  }

  removeFromQueue(FileItem value) {
    var index = this.getIndexOfItem(value);
    var item = this.queue[index];
    if (item.isUploading) {
      item.cancel();
    }

    this.queue.removeAt(index);
    this.progress = this._getTotalProgress(this.progress);
    this._render();
  }

  clearQueue() {
    this._error = false;
    this.queue.forEach((item) => item.deleteFileOnServer());
    this.queue.clear();
    this.fileReferences.clear();
    this.progress = 0;
    this._render();
  }

  uploadItem(FileItem value) {
    this.isUploading = true;
    this._xhrTransport(value);
  }

  cancelItem(FileItem value) {
    if (value != null && value.isUploading) {
      if (this.isHTML5) {
        value.xhr.abort();
        this._render();
      }
    }
  }

  uploadAll() {
    this._error = false;
    List<FileItem> items =
        this.getNotUploadedItems().where((item) => !item.isUploading).toList();
    items.forEach((item) => item.prepareToUploading());
    items.forEach((item) => item.upload());
  }

  cancelAll() {
    this._error = false;
    List items = this.getNotUploadedItems();
    items.map((item) => item.cancel());
  }

  isFile(value) {
    return isFile(value);
  }

  isFileLikeObject(value) {
    return value is FileLikeObject;
  }

  getIndexOfItem(value) {
    return value is num ? value : this.queue.indexOf(value);
  }

  List<FileItem> getNotUploadedItems() {
    if (this.queue == null) return new List<FileItem>();
    return this.queue.where((item) => !item.isUploaded).toList();
  }

  getReadyItems() {
    return this
        .queue
        .where((item) => (item.isReady && !item.isUploading))
        .toList();
  }

  _getTotalProgress(value) {
    if (this.removeAfterUpload) {
      return value;
    }

    int notUploaded = this.getNotUploadedItems().toList().length;
    int uploaded = this.queue.length - notUploaded;
    double ratio = 0.0;
    if (this.queue.length > 0) {
      ratio = 100 / this.queue.length;
    }
    double current = value * ratio / 100;
    return (uploaded * ratio + current).round();
  }

  _getFilters(List filters) {
    if (filters == null) {
      return null;
    }
    if (!filters.isEmpty) {
      return this.filters;
    }

    if (filters is List) {
      return filters;
    }

    String names = "";
    return this.filters.where((filter) => names.indexOf(filter.name) != -1);
  }

  _render() {}

  _isValidFile(file, List filters, options) {
    this._failFilterIndex = -1;
    if (filters == null) return true;
    if (filters.isEmpty) return true;
    return filters.every((filter) {
      this._failFilterIndex++;
      return filter.fn.call(this, file, options);
    });
  }

  _isSuccessCode(status) {
    return (status >= 200 && status < 300) || status == 304;
  }

  _transformResponse(response, headers) {
    return response;
  }

  _parseHeaders(headers) {
    var parsed;
    var key;
    var val;
    var i;

    if (headers == null || (headers is String && headers.trim() != "")) {
      return parsed;
    }

    headers.split('\n').map((line) {
      i = line.indexOf(':');
      key = line.slice(0, i).trim().toLowerCase();
      val = line.slice(i + 1).trim();

      if (key) {
        parsed[key] = parsed[key] ? parsed[key] + ', ' + val : val;
      }
    });

    return parsed;
  }

  bool hasError() {
    return this._error;
  }

  String errorMessage() {
    return _errorMessage;
  }

  _xhrTransport(FileItem item) {
    HttpRequest xhr = new HttpRequest();
    item.xhr = xhr;
    var form = new FormData();
    form.appendBlob('file', item.rawfile, item.file.name);

    this._onBeforeUploadItem(item);

    if (item.file.size is! num) {
      throw new StateError('The file specified is no longer valid');
    }
    //check file size
    if (item.file.size / 1000000 > 100) {
      this.queue.remove(item);
      this.isUploading = false;
      this.progress = 0;
      this._errorMessage = "file exceeded the file limit of 100 MB";
      this._error = true;
      return;
    }
    //check file type
    if (this.accept != null) {
      var fileEx = item.file.name.substring(item.file.name.indexOf("\.") + 1);
      if (this
          .accept
          .split(",")
          .map((s) => s.replaceAll("\.", "").trim())
          .where((s) => fileEx.contains(s))
          .isEmpty) {
        this.queue.remove(item);
        this.isUploading = false;
        this.progress = 0;
        this._errorMessage =
            "file has invalid file extension. Accepted: ${accept}";
        this._error = true;
        return;
      }
    }

    xhr.onProgress.listen((event) {
      num progress =
          (event.lengthComputable ? event.loaded * 100 / event.total : 0)
              .round();
      this._onProgressItem(item, progress);
      this._render();
    });

    xhr.onLoad.listen((event) {
      var headers = this._parseHeaders(xhr.getAllResponseHeaders());
      var response = this._transformResponse(xhr.response, headers);
      HttpRequest request = event.currentTarget;
      if (request.status != 200) {
        this._errorMessage = "uploaded file exceeded the file limit of 100 MB";
        this._error = true;
        this.isUploading = false;
      } else {
        var gist = this._isSuccessCode(xhr.status) ? 'Success' : 'Error';
        if (gist == 'Error') {
          this.onErrorItem(item, response, xhr.status, headers);
        } else {
          this._onSuccessItem(item, response, xhr.status, headers);
        }
        this.isUploading = false;
      }
      this.onCompleteItem(item, response, xhr.status, headers);
      this._render();
    });

    xhr.onError.listen((event) {
      var headers = this._parseHeaders(xhr.getAllResponseHeaders());
      var response = this._transformResponse(xhr.response, headers);
      HttpRequest request = event.currentTarget;
      if (request.status != 200) {
        this._errorMessage = 'uploaded file exceeded the file limit of 100 MB';

        this._error = true;
        this.isUploading = false;
      }

      this.onErrorItem(item, response, xhr.status, headers);
      //this.onCompleteItem(item, response, xhr.status, headers);
    });

    xhr.onAbort.listen((event) {
      var headers = this._parseHeaders(xhr.getAllResponseHeaders());
      var response = this._transformResponse(xhr.response, headers);
      HttpRequest request = event.currentTarget;
      if (request.status != 200) {
        this._errorMessage = 'file upload has been aborted';

        this._error = true;
      }
      this.isUploading = false;
      this._onCancelItem(item, response, xhr.status, headers);
      //this.onCompleteItem(item, response, xhr.status, headers);
    });

    xhr.open(item.method, this.url, async: true);
    xhr.withCredentials = item.withCredentials;
    // xhr.setRequestHeader('enctype','multipart/form-data');

    if (this.authToken != null) {
      xhr.setRequestHeader('Authorization', this.authToken);
    }
    xhr.send(form);
    this._render();
  }

  onWhenAddingFileFailed(item, filter, options) {}

  _onAfterAddingFile(item) {}

  _onAfterAddingAll(items) {}

  _onBeforeUploadItem(FileItem item) {
    item.onBeforeUpload();
  }

  _onProgressItem(FileItem item, progress) {
    var total = this._getTotalProgress(progress);
    this.progress = total;
    item.onProgress(progress);
    this._render();
  }

  _onSuccessItem(FileItem item, response, status, headers) {
    item.onSuccess(response, status, headers);
  }

  onErrorItem(FileItem item, response, status, headers) {
    item.onError(response, status, headers);
    this.queue.remove(item);
    this.isUploading = false;
    this.progress = 0;
  }

  _onCancelItem(FileItem item, response, status, headers) {
    item.onCancel(response, status, headers);
  }

  onCompleteItem(FileItem item, response, status, headers) {
    item.onComplete(response, status, headers);
    this.progress = this._getTotalProgress(this.progress);
    if (this.progress >= 100.0) {
      this.isUploading = false;
      newFileStreamCtrl.add(item.fileReference);
    }
    this._render();
  }
}

class FileReference {
  int id;
  String fileName;
  String contentType;

  String get path => '${BaseService.getUrl()}/files/read/${id}/private';
  String get downloadPath =>
      '${BaseService.getUrl()}/files/download/${id}/private';

  FileReference({jsog}) {
    // default constructor
    if (jsog == null) {
      this.id = -1;
      this.fileName = "";
      this.contentType = "";
    }
    // from jsog
    else {
      this.id = jsog["id"];
      this.fileName = jsog["fileName"];
      this.contentType = jsog["contentType"];
    }
  }

  Map toJSOG(Map objects) {
    return {
      'id': this.id,
      'fileName': this.fileName,
      'contentType': this.contentType
    };
  }

  String toJSON() {
    return jsonEncode(this.toJSOG(new Map()));
  }

  @override
  String toString() {
    return this.fileName;
  }
}
