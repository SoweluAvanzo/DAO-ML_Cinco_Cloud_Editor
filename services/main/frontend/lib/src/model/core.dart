import 'dart:convert';
import 'dart:js' as js;
import 'dart:html';

import '../filesupport/fileuploader.dart';

class OrganizationAccessRight {
  static final String CREATE_PROJECTS = "CREATE_PROJECTS";
  static final String EDIT_PROJECTS = "EDIT_PROJECTS";
  static final String DELETE_PROJECTS = "DELETE_PROJECTS";
}

class OrganizationAccessRightVector {
  int id;

  User user;
  Organization organization;
  List<String> accessRights;

  OrganizationAccessRightVector({Map cache, dynamic jsog}) {
    accessRights = new List<String>();

    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];

      if (jsog["organization"].containsKey("@ref")) {
        organization = cache[jsog["organization"]["@ref"]];
      } else {
        organization = new Organization(cache: cache, jsog: jsog["organization"]);
      }

      if (jsog["user"].containsKey("@ref")) {
        user = cache[jsog["user"]["@ref"]];
      } else {
        user = new User(cache: cache, jsog: jsog["user"]);
      }

      for (var value in jsog["accessRights"]) {
        if (value == OrganizationAccessRight.CREATE_PROJECTS) {
          accessRights.add(OrganizationAccessRight.CREATE_PROJECTS);
        } else if (value == OrganizationAccessRight.EDIT_PROJECTS) {
          accessRights.add(OrganizationAccessRight.EDIT_PROJECTS);
        } else if (value == OrganizationAccessRight.DELETE_PROJECTS) {
          accessRights.add(OrganizationAccessRight.DELETE_PROJECTS);
        }
      }
    } else {
      id = -1;
      accessRights = new List<String>();
    }
  }

  static OrganizationAccessRightVector fromJSON(String s) {
    return fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static OrganizationAccessRightVector fromJSOG({Map cache, dynamic jsog}) {
    return new OrganizationAccessRightVector(cache: cache, jsog: jsog);
  }

  String toJSON() {
    return jsonEncode(toJSOG(new Map()));
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.OrganizationAccessRightVector:${id}")) {
      jsog["@ref"] = cache["core.OrganizationAccessRightVector:${id}"];
    } else {
      cache["core.OrganizationAccessRightVector:${id}"] = (cache.length + 1).toString();
      jsog["@id"] = cache["core.OrganizationAccessRightVector:${id}"];
      jsog["id"] = id;
      jsog["user"] = user.toJSOG(cache);
      jsog["organization"] = organization.toJSOG(cache);
      jsog["accessRights"] = accessRights;
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.OrganizationAccessRightVectorTO";
    }
    return jsog;
  }

}

class ProjectDeployment {
  String url;
  String status;

  ProjectDeployment({Map cache, dynamic jsog}) {
    if (jsog != null) {
      url = jsog["url"];
      status = jsog["status"];
    } else {
      url = '';
    }
  }

  static ProjectDeployment fromJSON(String s) {
    return fromJSOG(new Map(), jsonDecode(s));
  }

  static ProjectDeployment fromJSOG(Map cache, dynamic jsog) {
    return new ProjectDeployment(cache: cache, jsog: jsog);
  }
}

class UpdateCurrentUserInput {
  String name;
  String email;
  FileReference profilePicture;

  String toJSON() {
    var jsog = Map();
    jsog['name'] = name;
    jsog['email'] = email;
    jsog['profilePicture'] = profilePicture?.toJSOG(Map());
    return jsonEncode(jsog);
  }
}

class UpdateCurrentUserPasswordInput {
  String oldPassword;
  String newPassword;

  String toJSON() {
    var jsog = Map();
    jsog['oldPassword'] = oldPassword;
    jsog['newPassword'] = newPassword;
    return jsonEncode(jsog);
  }
}

class UserSystemRole {
  static final String ADMIN = "ADMIN";
  static final String ORGANIZATION_MANAGER = "ORGANIZATION_MANAGER";
}

class User {
  int id;
  String name;
  String username;
  String email;
  String emailHash;
  FileReference profilePicture;

  List<Project> ownedProjects;
  List<String> systemRoles;

  var knownUsers;

  User({Map cache, dynamic jsog}) {
    ownedProjects = new List<Project>();
    systemRoles = new List<String>();

    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      name = jsog["name"];
      username = jsog["username"];
      email = jsog["email"];
      emailHash = jsog["emailHash"];

      for (var value in jsog["ownedProjects"]) {
        if (value.containsKey("@ref")) {
          ownedProjects.add(cache[value["@ref"]]);
        } else {
          ownedProjects.add(new Project(cache: cache, jsog: value));
        }
      }

      if (jsog.containsKey("profilePicture") && jsog["profilePicture"] != null) {
        profilePicture = new FileReference(jsog: jsog["profilePicture"]);
      }


      for (var value in jsog["systemRoles"]) {
        if (value == UserSystemRole.ADMIN) {
          systemRoles.add(UserSystemRole.ADMIN);
        } else if (value == UserSystemRole.ORGANIZATION_MANAGER) {
          systemRoles.add(UserSystemRole.ORGANIZATION_MANAGER);
        }
      }
    }
    else {
      id = -1;
      ownedProjects = new List<Project>();
      systemRoles = new List<String>();
    }
  }

  static User fromJSON(String s) {
    return fromJSOG(new Map(), jsonDecode(s));
  }

  static User fromJSOG(Map cache, dynamic jsog) {
    return new User(cache: cache, jsog: jsog);
  }

  String toJSON() {
    return jsonEncode(toJSOG(new Map()));
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.User:${id}")) {
      jsog["@ref"] = cache["core.User:${id}"];
    } else {
      cache["core.User:${id}"] = (cache.length + 1).toString();
      jsog["@id"] = cache["core.User:${id}"];
      jsog["id"] = id;
      jsog["name"] = name;
      jsog["username"] = username;
      jsog["email"] = email;
      jsog["emailHash"] = emailHash;
      jsog["ownedProjects"] = ownedProjects.map((n) => n.toJSOG(cache)).toList();
      if (profilePicture != null) {
        jsog["profilePicture"] = profilePicture.toJSOG(cache);
      }
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.UserTO";
    }
    return jsog;
  }
}

class Style {
  int id;
  String navBgColor;
  String navTextColor;
  String bodyBgColor;
  String bodyTextColor;
  String primaryBgColor;
  String primaryTextColor;
  FileReference logo;

  Style({Map cache, dynamic jsog}) {
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

  static Style fromJSON(String s) {
    return Style.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static Style fromJSOG({Map cache, dynamic jsog}) {
    return new Style(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.Style:${id}")) {
      jsog["@ref"] = cache["core.Style:${id}"];
    } else {
      cache["core.Style:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.Style:${id}"];
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
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.StyleTO";
    }
    return jsog;
  }
}

class Settings {
  int id;
  Style style;
  bool globallyCreateOrganizations;

  Settings({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      globallyCreateOrganizations = jsog["globallyCreateOrganizations"];

      if (jsog.containsKey("style")) {
        style = new Style(cache: cache, jsog: jsog["style"]);
      }
    } else {
      id = -1;
      style = new Style();
      globallyCreateOrganizations = false;
    }
  }

  static Settings fromJSON(String s) {
    return Settings.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static Settings fromJSOG({Map cache, dynamic jsog}) {
    return new Settings(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.Settings:${id}")) {
      jsog["@ref"] = cache["core.Settings:${id}"];
    } else {
      cache["core.Settings:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.Settings:${id}"];
      jsog['id'] = id;
      jsog['style'] = style.toJSOG(cache);
      jsog['globallyCreateOrganizations'] = globallyCreateOrganizations;
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.SettingsTO";
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

class WorkspaceImage {
  int id;
  String name;
  String imageName;
  String imageVersion;
  bool published;
  User user;
  Project project;

  WorkspaceImage({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;

      id = jsog["id"];
      name = jsog["name"];
      imageName = jsog["imageName"];
      imageVersion = jsog["imageVersion"];
      published = jsog["published"];

      project = _resolveComplexType(cache, jsog, "project", (c, j) => new Project(cache: c, jsog: j));
      user = _resolveComplexType(cache, jsog, "user", (c, j) => new User(cache: c, jsog: j));
    } else {
      id = -1;
      user = new User();
    }
  }

  static WorkspaceImage fromJSON(String s) {
    return WorkspaceImage.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static WorkspaceImage fromJSOG({Map cache, dynamic jsog}) {
    return new WorkspaceImage(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.WorkspaceImage:${id}")) {
      jsog["@ref"] = cache["core.WorkspaceImage:${id}"];
    } else {
      cache["core.WorkspaceImage:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.WorkspaceImage:${id}"];
      jsog['id'] = id;
      jsog['name'] = name;
      jsog['imageName'] = imageName;
      jsog['imageVersion'] = imageVersion;
      jsog['published'] = published;
      jsog['user'] = user.toJSOG(cache);
      jsog['project'] = project.toJSOG(cache);
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.WorkspaceImageTO";
    }
    return jsog;
  }
}

class WorkspaceImageBuildJob {

  int id;
  Project project;
  String status;
  DateTime startedAt;
  DateTime finishedAt;

  WorkspaceImageBuildJob({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      status = jsog["status"];
      startedAt = jsog["startedAt"] != null ? DateTime.parse(jsog["startedAt"]) : null;
      finishedAt = jsog["finishedAt"] != null ? DateTime.parse(jsog["finishedAt"]) : null;
      project = _resolveComplexType(cache, jsog, "project", (c, j) => new Project(cache: c, jsog: j));
    } else {
      id = -1;
      project = new Project();
    }
  }

  static WorkspaceImageBuildJob fromJSON(String s) {
    return WorkspaceImageBuildJob.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static WorkspaceImageBuildJob fromJSOG({Map cache, dynamic jsog}) {
    return new WorkspaceImageBuildJob(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.WorkspaceImageBuildJob:${id}")) {
      jsog["@ref"] = cache["core.WorkspaceImageBuildJob:${id}"];
    } else {
      cache["core.WorkspaceImageBuildJob:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.WorkspaceImageBuildJob:${id}"];
      jsog['id'] = id;
      jsog["status"] = status;
      jsog["startedAt"] = startedAt.toIso8601String();
      jsog["finishedAt"] = finishedAt == null ? null : finishedAt.toIso8601String();
      jsog['project'] = project.toJSOG(cache);
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO";
    }
    return jsog;
  }
}

class Organization {
  int id;
  String name;
  String description;
  Style style;
  List<User> owners;
  List<User> members;
  List<Project> projects;

  Organization({Map cache, dynamic jsog}) {
    owners = new List<User>();
    members = new List<User>();
    projects = new List<Project>();

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
            members.add(new User(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("owners")) {
        for (var value in jsog["owners"]) {
          if (value.containsKey("@ref")) {
            owners.add(cache[value["@ref"]]);
          } else {
            owners.add(new User(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("projects")) {
        for (var value in jsog["projects"]) {
          if (value.containsKey("@ref")) {
            projects.add(cache[value["@ref"]]);
          } else {
            projects.add(new Project(cache: cache, jsog: value));
          }
        }
      }

      if (jsog.containsKey("style")) {
        style = new Style(cache: cache, jsog: jsog["style"]);
      }
    } else {
      id = -1;
      style = new Style();
    }
  }

  void merge(Organization other) {
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

  static Organization fromJSON(String s) {
    return Organization.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static Organization fromJSOG({Map cache, dynamic jsog}) {
    return new Organization(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.Organization:${id}")) {
      jsog["@ref"] = cache["core.Organization:${id}"];
    } else {
      cache["core.Organization:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.Organization:${id}"];
      jsog['id'] = id;
      jsog['name'] = name;
      jsog['description'] = description;
      jsog['owners'] = owners.map((n) => n.toJSOG(cache)).toList();
      jsog['members'] = members.map((n) => n.toJSOG(cache)).toList();
      jsog['projects'] = projects.map((n) => n.toJSOG(cache)).toList();
      jsog['style'] = style.toJSOG(cache);
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.OrganizationTO";
    }
    return jsog;
  }

  List<User> get users =>
      new List.from(owners)
        ..addAll(members);
}

class ProjectType {
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

class Project {
  int id;
  User owner;
  String type;
  String name;
  String description;
  Organization organization;
  WorkspaceImage image;
  WorkspaceImage template;

  Project({Map cache, dynamic jsog}) {
    if (jsog != null) {
      cache[jsog["@id"]] = this;
      id = jsog["id"];
      description = jsog["description"];
      name = jsog["name"];
      type = jsog["type"];

      image = _resolveComplexType(cache, jsog, "image", (c, j) => new WorkspaceImage(cache: c, jsog: j));
      organization = _resolveComplexType(cache, jsog, "organization", (c, j) => new Organization(cache: c, jsog: j));
      owner = _resolveComplexType(cache, jsog, "owner", (c, j) => new User(cache: c, jsog: j));
      template = _resolveComplexType(cache, jsog, "template", (c, j) => new WorkspaceImage(cache: c, jsog: j));
    }
    else {
      id = -1;
    }
  }

  static Project fromJSON(String s) {
    return Project.fromJSOG(cache: new Map(), jsog: jsonDecode(s));
  }

  static Project fromJSOG({Map cache, dynamic jsog}) {
    return new Project(cache: cache, jsog: jsog);
  }

  Map toJSOG(Map cache) {
    Map jsog = new Map();
    if (cache.containsKey("core.Project:${id}")) {
      jsog["@ref"] = cache["core.Project:${id}"];
    } else {
      cache["core.Project:${id}"] = (cache.length + 1).toString();
      jsog['@id'] = cache["core.Project:${id}"];
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
      jsog['runtimeType'] = "info.scce.cincocloud.core.rest.tos.ProjectTO";
    }
    return jsog;
  }
}
