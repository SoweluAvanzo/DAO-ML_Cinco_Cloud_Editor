import 'dart:html';

dynamic isElement(dynamic node) {
  return !!(node && (node.nodeName || node.prop && node.attr && node.find));
}

class FileLikeObject {
  dynamic lastModifiedDate;
  dynamic size;
  String type;
  String name;

  FileLikeObject(dynamic fileOrInput) {
    var isInput = !(fileOrInput is File);
    var fakePathOrObject = isInput ? fileOrInput.value : fileOrInput;
    var postfix = (fakePathOrObject is String) ? 'FakePath' : 'Object';
    var method = '_createFrom' + postfix;
    if(postfix == 'Object')
    {
    		this.createFromObject(fakePathOrObject);
    }
    else
    {
    		this.createFromFakePath(fakePathOrObject);
    }
  }

  createFromFakePath(String path) {
    this.lastModifiedDate = null;
    this.size = null;
    this.type = 'like/' + path.substring(path.lastIndexOf('.') + 1).toLowerCase();
    this.name = path.substring(path.lastIndexOf('/') + path.lastIndexOf('\\') + 2);
  }

   createFromObject(dynamic object) {
    this.size = object.size;
    this.type = object.type;
    this.name = object.name;
  }
  String toString()
  {
    return this.name;
  }
}
