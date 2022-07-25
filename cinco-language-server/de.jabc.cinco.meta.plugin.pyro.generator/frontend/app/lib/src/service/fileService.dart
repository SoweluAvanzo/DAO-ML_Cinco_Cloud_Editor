import 'base_service.dart';

import 'dart:html' as html;

class FileService {

  static String UPLOAD_FOLDER = null;
  static String WORKSPACE_ROOT = null;

  static init() {
    if(UPLOAD_FOLDER == null) {
      FileService.getUploadFolder().then((upload_folder) => UPLOAD_FOLDER = FileService.sanitizePath(upload_folder, false));
    }
    if(WORKSPACE_ROOT == null) {
      FileService.getWorkspaceRoot().then((wr) => WORKSPACE_ROOT = FileService.sanitizePath(wr, true));
    }
  }

	static Future<String> getWorkspaceRoot() async{
		return html.HttpRequest.request("${BaseService.baseUrl()}/files/read/root/private",
			method: "GET",
			requestHeaders: BaseService.REQUEST_HEADERS,
			withCredentials: true
		).then((response){
		  return response.responseText;
		});
	}
  
	static Future<String> getUploadFolder() async{
		return html.HttpRequest.request("${BaseService.baseUrl()}/files/read/upload_folder/private",
			method: "GET",
			requestHeaders: BaseService.REQUEST_HEADERS,
			withCredentials: true
		).then((response){
		  return response.responseText;
		});
	}

  static String sanitizePath(String path, bool absolute) {
    var uri = Uri.file(path);
    path = uri.toFilePath();
    path = path.replaceAll('\\\\', '\\').replaceAll('\\', '/');
    if(absolute) {
      if(path[0] != '/') {
        path = '/'  + path;
      }
      uri = Uri.parse(path);
      path = uri.toFilePath();
      var driveLetter = path[1];
      var isDriveLetter = isLetter(driveLetter) && path[2] == ':';
      if(isDriveLetter) {
        // normalize Driveletter
        path = path.substring(0, 2).toLowerCase() + path.substring(2);
      }
    }
    return path;
  }

  static RegExp _isLowLetterRegExp = RegExp(r'[a-z]', caseSensitive: false);
  static bool isLetter(String letter) => _isLowLetterRegExp.hasMatch(letter);
}