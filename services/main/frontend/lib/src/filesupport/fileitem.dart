import 'dart:async';
import 'dart:convert';
import 'dart:html';

import 'filelikeobject.dart';
import 'fileuploader.dart';

class FileItem {
  FileLikeObject file;
  File _file;
  FileReference fileReference;
   String alias = 'file';
   String url = '/';
   String method = 'POST';
   List headers = [];
   bool withCredentials = true;
   List formData = [];
   bool isReady = false;
   bool isUploading = false;
   bool isUploaded = false;
   bool isSuccess = false;
   bool isCancel = false;
   bool isError = false;
   num progress = 0;
   int index = null;
   FileUploader uploader;
  HttpRequest xhr;

  FileItem(FileUploader this.uploader, dynamic some, dynamic options) {
  	if(some != null){
	    this.file = new FileLikeObject(some);  		
	    this._file = some;
  	}
    this.url = uploader.url;
  }

  get rawfile => this._file;
  

  void upload() {
    try {
      this.uploader.uploadItem(this);
    } catch (e) {
      this.uploader.onCompleteItem(this, '', 0, []);
      this.uploader.onErrorItem(this, '', 0, []);
    }
  }
  
  void deleteFileOnServer()
  {
  	if(this.fileReference!=null)
  	{
  		return;
  	}
  	var request = new HttpRequest();
	request.open('DELETE', 'rest/files/delete/'+this.fileReference.id.toString());
	request.onLoad.listen((event) {
		this.fileReference = null;
	});
	request.send();
  }

  void cancel() {
  	// Delete File
  	this.deleteFileOnServer();
    this.uploader.cancelItem(this);
  }

  void remove() {
  	// Delete File
  	this.deleteFileOnServer();
    this.uploader.removeFromQueue(this);
  }

  _onBeforeUpload() {
  }

  _onProgress(num progress) {
  }

  _onSuccess(dynamic response,dynamic status,dynamic headers) {
  	this.fileReference = new FileReference(jsog: jsonDecode(response));
  }

  _onError(response, status, headers) {
  	this.fileReference = null;
  }

  _onCancel(response, status, headers) {
  	this.fileReference = null;
  }

  _onComplete(response, status, headers) {
  }

  onBeforeUpload() {
    this.isReady = true;
    this.isUploading = true;
    this.isUploaded = false;
    this.isSuccess = false;
    this.isCancel = false;
    this.isError = false;
    this.progress = 0;
    this._onBeforeUpload();
  }

  onProgress(num progress) {
    this.progress = progress;
    this._onProgress(progress);
  }

  onSuccess(response, status, headers) {
    this.isReady = false;
    this.isUploading = false;
    this.isUploaded = true;
    this.isSuccess = true;
    this.isCancel = false;
    this.isError = false;
    this.progress = 100;
    this.index = null;
    this._onSuccess(response, status, headers);
  }

  onError(response, status, headers) {
    this.isReady = false;
    this.isUploading = false;
    this.isUploaded = true;
    this.isSuccess = false;
    this.isCancel = false;
    this.isError = true;
    this.progress = 0;
    this.index = null;
    this._onError(response, status, headers);
  }

  onCancel(response, status, headers) {
    this.isReady = false;
    this.isUploading = false;
    this.isUploaded = false;
    this.isSuccess = false;
    this.isCancel = true;
    this.isError = false;
    this.progress = 0;
    this.index = null;
    this._onCancel(response, status, headers);
  }

  onComplete(response, status, headers) {
    this._onComplete(response, status, headers);

    if (this.uploader.removeAfterUpload) {
      this.remove();
    }
  }

  prepareToUploading() {
    this.isReady = true;
    this._onBeforeUpload();
  }
}
