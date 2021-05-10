import 'dart:html';

import 'package:angular_router/angular_router.dart';

import '../service/settings_service.dart';
import '../model/core.dart';
import '../utils/hsl_value.dart';

class StyleService {

  PyroStyle _globalStyle;
  PyroStyle _style;
  String _css;
  
  SettingsService _settingsService;
  
  StyleService(this._settingsService) {
  	this._settingsService.get().then((settings){
  		updateGlobal(settings.style);
  	}).catchError((e)=>print(e));
  }
  
  void update(PyroStyle style) {
  	_style = style;
  	_updateStyle();
  }
  
  void updateGlobal(PyroStyle style) {
  	_globalStyle = style;
  	update(style);
  	window.localStorage["pyroGlobalStyle"] = _css;
  }
  
  void handleOnDeactivate(RouterState next) {
  	if (!next.path.startsWith(new RegExp(r'/organization/[1-9]+'))) {
  	  _style = _globalStyle;
  	  _updateStyle();
  	}
  }
  
  void _updateStyle() {
    _removeStyleSheet();
  	if (_style != null) {
  	  _applyStyleSheet();
  	}
  }
  
  void _removeStyleSheet() {
  	var headNode = window.document.querySelector("head");
  	var styleNode = window.document.querySelector("#organization-stylesheet");  	
  	if (styleNode != null) {
  	  headNode.children.remove(styleNode);
  	}
  }
  
  void _applyStyleSheet() { 
	String css = "";
	
	if (_isValidColor(_style.navBgColor)) {
	  String darker = HSLValue.fromHexValue(_style.navBgColor).darken(10).toHexValue();
	  css += """
	    .org-nav-bg-color {
	      background-color: #${_style.navBgColor} !important;
	    }
	  	.grid-stack-item-header {
	  	  background-color: #${darker} !important;
	  	}
	  	ul.nav.nav-tabs:not(.left-tabs) > li.nav-item {
	  	  background-color: #${darker} !important;
	  	}
	  """;
	}
	
	if (_isValidColor(_style.navTextColor)) {	 
		String darker = HSLValue.fromHexValue(_style.navTextColor).darken(33).toHexValue(); 	
	  	css += """
	  	  .org-nav-text-color,
	  	  .grid-stack-item-header {
		    color: #${_style.navTextColor} !important;
	  	  }
	  	  ul.navbar-nav > li.nav-item > a.nav-link {
	  	  	color: #${_style.navTextColor} !important;
	  	  }
	  	  ul.nav.nav-tabs > li.nav-item.active > a.nav-link {
	  	  	color: #${darker}!important;
	  	  }
	  	  ul.nav.nav-tabs > li.nav-item:not(.active) > a.nav-link {
	  	  	color: #${_style.navTextColor}!important;
	  	  }
	  	""";
	}
	
	if (_isValidColor(_style.bodyBgColor)) {
	  css += "body, .org-body-bg-color {background-color: #${_style.bodyBgColor} !important;}";
	}
	
	if (_isValidColor(_style.bodyTextColor)) {
	  String darker = HSLValue.fromHexValue(_style.bodyTextColor).darken(33).toHexValue();
	  String lighter = HSLValue.fromHexValue(_style.bodyTextColor).lighten(33).toHexValue();
	  
	  css += """
	    body, 
	    .org-body-text-color {
	      color: #${_style.bodyTextColor} !important;
	    }
	    
	    .text-muted, 
	    .sidebar-section-item:not(.active) {
	      color: #${darker} !important;
	    }
	    
	    .grid-stack-item .nav-tabs li:not(.active) a {
	      color: #${lighter} !important;
	    }
	    .context-menu-item-text {
	      color: #${darker} !important;
	    }
	  """;
	}
	
	if (_isValidColor(_style.primaryBgColor)) {
		css += """
		  .btn.btn-primary,
		  .sidebar-section-item.active,
		  .badge.badge-primary,
		  .dropdown-item.active {
		  	background: #${_style.primaryBgColor} !important;
		  }
		  .pyro-micor-menu {
		  	border-color: #${_style.primaryBgColor} 2px solid!important;
		  }
		""";
		
		String darker = HSLValue.fromHexValue(_style.primaryBgColor).darken(10).toHexValue();
		css += """
		  .btn.btn-primary {
		  	border-color: #${darker} !important;
		  }
		  
		  .btn.btn-primary::hover,
		  .btn.btn-primary::focus,
		  .btn.btn-primary::active {
		  	background: #${darker} !important;
		  }
		""";
	}
	
	if (_isValidColor(_style.primaryTextColor)) {
		css += """
		  .btn.btn-primary,
		  .sidebar-section-item.active,
		  .dropdown-item.active {
		  	color: #${_style.primaryTextColor} !important;
		  }
		""";
	}
		
	var styleNode = document.createElement("style");
	styleNode.attributes["id"] = "organization-stylesheet";
	styleNode.innerHtml = css;
	
	var headerNode = window.document.querySelector("head");
	headerNode.insertBefore(styleNode, headerNode.querySelector('#default-organization-stylesheet').nextNode);
	
	// change favicon
	var faviconNode = window.document.querySelector("link[rel='icon']");
	if (faviconNode != null) {
	  faviconNode.attributes["href"] = _style.logo != null ? _style.logo.downloadPath : "img/pyro.png";
	}
	
	_css = css;
  }
  
  bool _isValidColor(String value) {
    RegExp exp = new RegExp("[a-f0-9]{6,6}");
  	return !(value == null || value.trim() == "") && exp.hasMatch(value);
  }
  
  PyroStyle get style => _style;
}
