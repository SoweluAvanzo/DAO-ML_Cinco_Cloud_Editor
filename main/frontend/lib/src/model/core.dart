import 'dart:convert';
import 'dart:js' as js;
import 'dart:html';

import '../filesupport/fileuploader.dart';

class PyroOrganizationAccessRight {
  static final String CREATE_PROJECTS = "CREATE_PROJECTS";
  static final String EDIT_PROJECTS = "EDIT_PROJECTS";
  static final String DELETE_PROJECTS = "DELETE_PROJECTS";
}

class PyroOrganizationAccessRightVector {
  int id;

  PyroUser user;
  PyroOrganization organization;
  List<String> accessRights;

  PyroOrganizationAccessRightVector({Map cache, dynamic jsog}) {
    accessRights = new List<String>();

    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];

      if (jsog["organization"].containsKey("@ref")) {
        organization = cache[jsog["organization"]["@ref"]];
      } else {
        organization = new PyroOrganization(cache: cache, jsog: jsog["organization"]);
      }

      if (jsog["user"].containsKey("@ref")) {
        user = cache[jsog["user"]["@ref"]];
      } else {
        user = new PyroUser(cache: cache, jsog: jsog["user"]);
      }

      for (var value in jsog["accessRights"]) {
        if (value == PyroOrganizationAccessRight.CREATE_PROJECTS) {
          accessRights.add(PyroOrganizationAccessRight.CREATE_PROJECTS);
        } else if (value == PyroOrganizationAccessRight.EDIT_PROJECTS) {
          accessRights.add(PyroOrganizationAccessRight.EDIT_PROJECTS);
        } else if (value == PyroOrganizationAccessRight.DELETE_PROJECTS) {
          accessRights.add(PyroOrganizationAccessRight.DELETE_PROJECTS);
        }
      }
    } else {
      id = -1;
      accessRights = new List<String>();
    }
  }

  static PyroOrganizationAccessRightVector fromJSON(String s) {
    return fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroOrganizationAccessRightVector fromJSOG({Map cache, dynamic jsog}) {
    return new PyroOrganizationAccessRightVector(cache: cache, jsog: jsog);
  }

  String toJSON() {
    return jsonEncode(toJSOG(new Map()));
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroOrganizationAccessRightVector:${id}")) {
      jsog["@ref"] = cache["core.PyroOrganizationAccessRightVector:${id}"];
    } else {
      cache["core.PyroOrganizationAccessRightVector:${id}"] = (cache.length + 1).toString();
      jsog["@id"] = cache["core.PyroOrganizationAccessRightVector:${id}"];
      jsog["id"] = id;
      jsog["user"] = user.toJSOG(cache);
      jsog["organization"] = organization.toJSOG(cache);
      jsog["accessRights"] = accessRights;
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroOrganizationAccessRightVector";
    }
    return jsog;
  }

}

class PyroProjectDeployment {
  String url;
  String status;

  PyroProjectDeployment({Map cache, dynamic jsog}) {
    if (jsog != null) {
      url = jsog["url"];
      status = jsog["status"];
    } else {
      url = '';
    }
  }

  static PyroProjectDeployment fromJSON(String s) {
    return fromJSOG(new Map(), jsonDecode(s));
  }

  static PyroProjectDeployment fromJSOG(Map cache, dynamic jsog) {
    return new PyroProjectDeployment(cache: cache, jsog: jsog);
  }
}

class PyroSystemRole {
  static final String ADMIN = "ADMIN";
  static final String ORGANIZATION_MANAGER = "ORGANIZATION_MANAGER";
}

class PyroUser {
  int id;
  String username;
  String email;
  String emailHash;
  FileReference profilePicture;

  List<PyroProject> ownedProjects;
  List<String> systemRoles;

  var knownUsers;

  PyroUser({Map cache, dynamic jsog}) {
    ownedProjects = new List<PyroProject>();
    systemRoles = new List<String>();

    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      username = jsog["username"];
      email = jsog["email"];
      emailHash = jsog["emailHash"];

      for (var value in jsog["ownedProjects"]) {
        if (value.containsKey("@ref")) {
          ownedProjects.add(cache[value["@ref"]]);
        } else {
          ownedProjects.add(new PyroProject(cache: cache, jsog: value));
        }
      }

      if (jsog.containsKey("profilePicture") && jsog["profilePicture"] != null) {
        profilePicture = new FileReference(jsog: jsog["profilePicture"]);
      }


      for (var value in jsog["systemRoles"]) {
        if (value == PyroSystemRole.ADMIN) {
          systemRoles.add(PyroSystemRole.ADMIN);
        } else if (value == PyroSystemRole.ORGANIZATION_MANAGER) {
          systemRoles.add(PyroSystemRole.ORGANIZATION_MANAGER);
        }
      }
    }
    else {
      id = -1;
      ownedProjects = new List<PyroProject>();
      systemRoles = new List<String>();
    }
  }

  static PyroUser fromJSON(String s) {
    return fromJSOG(new Map(), jsonDecode(s));
  }

  static PyroUser fromJSOG(Map cache, dynamic jsog) {
    return new PyroUser(cache: cache, jsog: jsog);
  }

  String toJSON() {
    return jsonEncode(toJSOG(new Map()));
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroUser:${id}")) {
      jsog["@ref"] = cache["core.PyroUser:${id}"];
    } else {
      cache["core.PyroUser:${id}"] = (cache.length + 1).toString();
      jsog["@id"] = cache["core.PyroUser:${id}"];
      jsog["id"] = id;
      jsog["username"] = username;
      jsog["email"] = email;
      jsog["emailHash"] = emailHash;
      jsog["ownedProjects"] = ownedProjects.map((n) => n.toJSOG(cache)).toList();
      if (profilePicture != null) {
        jsog["profilePicture"] = profilePicture.toJSOG(cache);
      }
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroUser";
    }
    return jsog;
  }
}

class PyroStyle {
  int id;

  String navBgColor;
  String navTextColor;
  String bodyBgColor;
  String bodyTextColor;
  String primaryBgColor;
  String primaryTextColor;
  FileReference logo;

  PyroStyle({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];

      navBgColor = jsog["navBgColor"];
      navTextColor = jsog["navTextColor"];
      bodyBgColor = jsog["bodyBgColor"];
      bodyTextColor = jsog["bodyTextColor"];
      primaryBgColor = jsog["primaryBgColor"];
      primaryTextColor = jsog["primaryTextColor"];

      if (jsog.containsKey("logo") && jsog["logo"] != null) {
        logo = new FileReference(jsog: jsog["logo"]);
      }
    } else {
      id = -2;
    }
  }

  static PyroStyle fromJSON(String s) {
    return PyroStyle.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroStyle fromJSOG({Map cache, dynamic jsog}) {
    return new PyroStyle(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroStyle:${id}")) {
      jsog["@ref"] = cache["core.PyroStyle:${id}"];
    } else {
      cache["core.PyroStyle:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.PyroStyle:${id}"];
      jsog['id'] = id;

      jsog['navBgColor'] = navBgColor;
      jsog['navTextColor'] = navTextColor;
      jsog['bodyBgColor'] = bodyBgColor;
      jsog['bodyTextColor'] = bodyTextColor;
      jsog['primaryBgColor'] = primaryBgColor;
      jsog['primaryTextColor'] = primaryTextColor;
      if (logo != null) {
        jsog['logo'] = logo.toJSOG(cache);
      }
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroStyle";
    }
    return jsog;
  }
}

class PyroSettings {
  int id;
  PyroStyle style;
  bool globallyCreateOrganizations;

  PyroSettings({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      globallyCreateOrganizations = jsog["globallyCreateOrganizations"];

      if (jsog.containsKey("style")) {
        style = new PyroStyle(cache: cache, jsog: jsog["style"]);
      }
    } else {
      id = -1;
      style = new PyroStyle();
      globallyCreateOrganizations = false;
    }
  }

  static PyroSettings fromJSON(String s) {
    return PyroSettings.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroSettings fromJSOG({Map cache, dynamic jsog}) {
    return new PyroSettings(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroSettings:${id}")) {
      jsog["@ref"] = cache["core.PyroSettings:${id}"];
    } else {
      cache["core.PyroSettings:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.PyroSettings:${id}"];
      jsog['id'] = id;
      jsog['style'] = style.toJSOG(cache);
      jsog['globallyCreateOrganizations'] = globallyCreateOrganizations;
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroSettings";
    }
    return jsog;
  }
}

class WorkspaceImageBuildResult {
  int projectId;
  bool success;
  String message;
  String image;

  WorkspaceImageBuildResult({Map cache, dynamic jsog}) {
    if (jsog != null) {
      projectId = jsog["projectId"];
      success = jsog["success"];
      message = jsog["message"];
      image = jsog["image"];
    } else {
      projectId = -1;
      success = false;
      message = "";
      image = "";
    }
  }

  static WorkspaceImageBuildResult fromJSOG(Map cache, dynamic jsog) {
    return new WorkspaceImageBuildResult(cache: cache, jsog: jsog);
  }
}

class PyroWorkspaceImage {
  int id;
  String name;
  String imageName;
  String imageVersion;
  bool published;
  PyroUser user;
  PyroProject project;

  PyroWorkspaceImage({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;

      id = jsog["id"];
      name = jsog["name"];
      imageName = jsog["imageName"];
      imageVersion = jsog["imageVersion"];
      published = jsog["published"];

      user = _resolveComplexType(cache, jsog, "user", (c, j) => new PyroUser(cache: c, jsog: j));
      project = _resolveComplexType(cache, jsog, "project", (c, j) => new PyroProject(cache: c, jsog: j));
    } else {
      id = -1;
      user = new PyroUser();
    }
  }

  static PyroWorkspaceImage fromJSON(String s) {
    return PyroWorkspaceImage.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroWorkspaceImage fromJSOG({Map cache, dynamic jsog}) {
    return new PyroWorkspaceImage(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroWorkspaceImage:${id}")) {
      jsog["@ref"] = cache["core.PyroWorkspaceImage:${id}"];
    } else {
      cache["core.PyroWorkspaceImage:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.PyroWorkspaceImage:${id}"];
      jsog['id'] = id;
      jsog['name'] = name;
      jsog['imageName'] = imageName;
      jsog['imageVersion'] = imageVersion;
      jsog['published'] = published;
      jsog['user'] = user.toJSOG(cache);
      jsog['project'] = project.toJSOG(cache);
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroWorkspaceImage";
    }
    return jsog;
  }
}

class PyroOrganization {
  int id;
  String name;
  String description;
  PyroStyle style;
  List<PyroUser> owners;
  List<PyroUser> members;
  List<PyroProject> projects;

  PyroOrganization({Map cache, dynamic jsog}) {
    owners = new List<PyroUser>();
    members = new List<PyroUser>();
    projects = new List<PyroProject>();

    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      name = jsog["name"];
      description = jsog["description"];

      if (jsog.containsKey("members")) {
        for (var value in jsog["members"]) {
          if (value.containsKey("@ref")) {
            members.add(cache[value["@ref"]]);
          } else {
            members.add(new PyroUser(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("owners")) {
        for (var value in jsog["owners"]) {
          if (value.containsKey("@ref")) {
            owners.add(cache[value["@ref"]]);
          } else {
            owners.add(new PyroUser(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("projects")) {
        for (var value in jsog["projects"]) {
          if (value.containsKey("@ref")) {
            projects.add(cache[value["@ref"]]);
          } else {
            projects.add(new PyroProject(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("style")) {
        style = new PyroStyle(cache: cache, jsog: jsog["style"]);
      }
    } else {
      id = -1;
      style = new PyroStyle();
    }
  }

  void merge(PyroOrganization other) {
    id = other.id;
    name = other.name;

    projects.removeWhere((n) =>
    other.projects
        .where((g) => n.id == g.id)
        .isEmpty);
    members.removeWhere((n) =>
    other.members
        .where((g) => n.id == g.id)
        .isEmpty);
    owners.removeWhere((n) =>
    other.owners
        .where((g) => n.id == g.id)
        .isEmpty);

    //update files
    projects.forEach((n) {
      n.merge(other.projects
          .where((g) => g.id == n.id)
          .first);
    });

    projects.addAll(other.projects.where((n) =>
    projects
        .where((g) => n.id == g.id)
        .isEmpty));
    members.addAll(other.members.where((n) =>
    members
        .where((g) => n.id == g.id)
        .isEmpty));
    owners.addAll(other.owners.where((n) =>
    owners
        .where((g) => n.id == g.id)
        .isEmpty));
  }

  static PyroOrganization fromJSON(String s) {
    return PyroOrganization.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroOrganization fromJSOG({Map cache, dynamic jsog}) {
    return new PyroOrganization(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroOrganization:${id}")) {
      jsog["@ref"] = cache["core.PyroOrganization:${id}"];
    } else {
      cache["core.PyroOrganization:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.PyroOrganization:${id}"];
      jsog['id'] = id;
      jsog['name'] = name;
      jsog['description'] = description;
      jsog['owners'] = owners.map((n) => n.toJSOG(cache)).toList();
      jsog['members'] = members.map((n) => n.toJSOG(cache)).toList();
      jsog['projects'] = projects.map((n) => n.toJSOG(cache)).toList();
      jsog['style'] = style.toJSOG(cache);
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroOrganization";
    }
    return jsog;
  }

  List<PyroUser> get users =>
      new List.from(owners)
        ..addAll(members);
}

class PyroProjectType {
  static final String LANGUAGE_EDITOR = "LANGUAGE_EDITOR";
  static final String MODEL_EDITOR = "MODEL_EDITOR";
}


T _resolveComplexType<T>(Map cache, dynamic jsog, String key, T Function(Map, dynamic) f) {
  if (jsog != null && jsog.containsKey(key) && jsog[key] != null) {
    if (jsog[key].containsKey("@ref")) {
      return cache[jsog[key]["@ref"]];
    } else {
      return f(cache, jsog[key]);
    }
  } else {
    return null;
  }
}

List<T> _resolveComplexListType<T>(Map cache, dynamic jsog, String key, T Function(Map, dynamic) f) {
  var items = List<T>();
  if (jsog != null && jsog.containsKey(key) && jsog[key] != null) {
    for (var value in jsog[key]) {
      if (value.containsKey("@ref")) {
        items.add(cache[value["@ref"]]);
      } else {
        items.add(f(cache, value));
      }
    }
  }
  return items;
}


class PyroProject extends PyroFolder {
  int id;
  PyroUser owner;
  String type;
  String name;
  String description;
  PyroOrganization organization;
  PyroWorkspaceImage image;
  PyroWorkspaceImage template;
  List<PyroFolder> innerFolders;
  List<PyroFile> files;

  PyroProject({Map cache, dynamic jsog}) {
    innerFolders = new List<PyroFolder>();
    files = new List<PyroFile>();
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      description = jsog["description"];
      name = jsog["name"];
      type = jsog["type"];

      organization = _resolveComplexType(cache, jsog, "organization", (c, j) => new PyroOrganization(cache: c, jsog: j));
      image = _resolveComplexType(cache, jsog, "image", (c, j) => new PyroWorkspaceImage(cache: c, jsog: j));
      template = _resolveComplexType(cache, jsog, "template", (c, j) => new PyroWorkspaceImage(cache: c, jsog: j));
      owner = _resolveComplexType(cache, jsog, "owner", (c, j) => new PyroUser(cache: c, jsog: j));

      if (jsog.containsKey("innerFolders")) {
        for (var value in jsog["innerFolders"]) {
          if (value.containsKey("@ref")) {
            innerFolders.add(cache[value["@ref"]]);
          } else {
            innerFolders.add(new PyroFolder(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("files")) {
        for (var value in jsog["files"]) {
          if (value.containsKey("@ref")) {
            files.add(cache[value["@ref"]]);
          }
        }
      }
    }
    else {
      id = -1;
      innerFolders = new List<PyroFolder>();
      files = new List<PyroFile>();
    }
  }

  static PyroProject fromJSON(String s) {
    return PyroProject.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static PyroProject fromJSOG({Map cache, dynamic jsog}) {
    return new PyroProject(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroProject:${id}")) {
      jsog["@ref"] = cache["core.PyroProject:${id}"];
    } else {
      cache["core.PyroProject:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.PyroProject:${id}"];
      jsog['id'] = id;
      jsog['name'] = name;
      if (owner != null) {
        jsog['owner'] = owner.toJSOG(cache);
      }
      if (organization != null) {
        jsog['organization'] = organization.toJSOG(cache);
      }
      if (image != null) {
        jsog['image'] = image.toJSOG(cache);
      }
      if (template != null) {
        jsog['template'] = template.toJSOG(cache);
      }
      jsog['description'] = description;
      jsog['innerFolders'] = innerFolders.map((n) => n.toJSOG(cache)).toList();
      jsog['files'] = files.map((n) => n.toJSOG(cache)).toList();
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroProject";
    }
    return jsog;
  }

  @override
  String fullPath(PyroFile pf) {
    if (files
        .where((f) => f.id == pf.id)
        .isNotEmpty) {
      return "";
    }

    if (innerFolders.isEmpty) {
      return null;
    }

    List<String> paths = innerFolders.map((f) => f.fullPath(pf)).where((f) => f != null).toList();
    if (paths.isEmpty) {
      return null;
    }
    return paths[0] + "/";
  }

}

class PyroFolder {
  int id;
  String name;
  List<PyroFolder> innerFolders;
  List<PyroFile> files;

  PyroFolder({Map cache, dynamic jsog}) {
    innerFolders = new List<PyroFolder>();
    files = new List<PyroFile>();
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      name = jsog["name"];
      for (var value in jsog["innerFolders"]) {
        if (value.containsKey("@ref")) {
          innerFolders.add(cache[value["@ref"]]);
        } else {
          innerFolders.add(new PyroFolder(cache: cache, jsog: value));
        }
      }
      for (var value in jsog["files"]) {
        if (value.containsKey("@ref")) {
          files.add(cache[value["@ref"]]);
        }
      }
    }
    else {
      id = -1;
      name = "";
      innerFolders = new List<PyroFolder>();
      files = new List<PyroFile>();
    }
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.PyroFolder:${id}")) {
      jsog["@ref"] = cache["core.PyroFolder:${id}"];
    } else {
      cache["core.PyroFolder:${id}"] = (cache.length + 1).toString();
      jsog["@id"] = cache["core.PyroFolder:${id}"];
      jsog['id'] = id;
      jsog['name'] = name;
      jsog['innerFolders'] = innerFolders.map((n) => n.toJSOG(cache)).toList();
      jsog['files'] = files.map((n) => n.toJSOG(cache)).toList();
      jsog['runtimeType'] = "info.scce.pyro.core.rest.types.PyroFolder";
    }
    return jsog;
  }

  static PyroFolder fromJSON(String s) {
    return PyroFolder.fromJSOG(new Map(), jsonDecode(s));
  }

  static PyroFolder fromJSOG(Map cache, dynamic jsog) {
    return new PyroFolder(cache: cache, jsog: jsog);
  }

  List<PyroFile> allFiles() {
    List<PyroFile> gs = new List();
    gs.addAll(this.files.whereType<PyroFile>().toList());
    gs.addAll(this.innerFolders.expand((n) => n.allFiles()).toList());
    return gs;
  }

  void merge(PyroFolder pp) {
    id = pp.id;
    name = pp.name;

    //remove missing files
    files.removeWhere((n) =>
    pp.files
        .where((g) => n.id == g.id)
        .isEmpty);
    //remove missing folders
    innerFolders.removeWhere((n) =>
    pp.innerFolders
        .where((g) => n.id == g.id)
        .isEmpty);

    //update files
    files.forEach((n) {
      n.mergeStructure(pp.files
          .where((g) => g.id == n.id)
          .first);
    });
    //update folders
    innerFolders.forEach((n) {
      n.merge(pp.innerFolders
          .where((g) => g.id == n.id)
          .first);
    });

    //add new files
    files.addAll(pp.files.where((n) =>
    files
        .where((g) => n.id == g.id)
        .isEmpty));
    //add new folder
    innerFolders.addAll(pp.innerFolders.where((n) =>
    innerFolders
        .where((g) => n.id == g.id)
        .isEmpty));
  }

  String fullPath(PyroFile pf) {
    if (files
        .where((f) => f.id == pf.id)
        .isNotEmpty) {
      return this.name;
    }

    if (innerFolders.isEmpty) {
      return null;
    }

    List<String> paths = innerFolders.map((f) => f.fullPath(pf)).where((f) => f != null).toList();
    if (paths.isEmpty) {
      return null;
    }
    return this.name + "/" + paths[0];
  }
}

abstract class PyroFile {
  int id;
  String filename;
  String extension;

  Map toJSOG(Map cache);

  void mergeStructure(PyroFile pf);

  String $type() => "core.PyroFile";

  String getFullName() {
    return filename + (extension == null ? "" : "." + extension);
  }
}
