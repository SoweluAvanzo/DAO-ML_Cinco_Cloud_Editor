package de.jabc.cinco.meta.plugin.pyro.frontend

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class Index extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameIndex()'''index.html'''
	
	def contentIndex()
	'''
	<!DOCTYPE html>
	<html>
	  <head>
	  	<base href="/">
	    <title>Pyro</title>
	    <meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
		«IF gc.cpd.image32.nullOrEmpty»
			<link rel="icon"
			      type="image/png"
			      href="img/pyro.png" />
		«ELSE»
			<link rel="icon"
				          type="image/png"
				          href="cpd/«gc.cpd.image32.trimQuotes»" />
		«ENDIF»
	    <link rel="stylesheet" href="css/bootstrap.min.css" />
	    <link rel="stylesheet" href="css/joint.css" />
	    <link rel="stylesheet" href="css/pyro_core.css" />
	    <link rel="stylesheet" href="css/fontawesome.all.css" />
	    <link rel="stylesheet" href="css/ng_bootstrap_all.css" />
	    <link rel="stylesheet" href="css/overwrites.css" />
	    <link rel="stylesheet" href="css/gridstack.min.css" />
	    <link rel="stylesheet" href="css/b4vtabs.min.css">
	    
		<style id="default-organization-stylesheet">
		  body {
		    margin: 0 auto;
		    font-family: Roboto, Helvetica, Arial, sans-serif;
		  }
		 
		  /** default organization styles */ 
		  .org-nav-bg-color {
		  	background-color: #525252;
		  }
		  .org-nav-text-color: {
		  	
		  }
		  .org-body-bg-color {
		  	background-color: #333;
		  }
		  .org-body-text-color {
		    color: #fff;
		  }
		</style>
		<script>
		    /* load global pyro theme */
			var css = localStorage.getItem('pyroGlobalStyle');
			if (css != null) {
				var styleNode = document.createElement("style");
				styleNode.setAttribute("id", "organization-stylesheet");
				styleNode.innerText = css.split('\n').join('').split('\t').join('');    			
				var head = document.querySelector("head");
				head.insertBefore(styleNode, head.querySelector('#default-organization-stylesheet').nextSibling);
			}		
		</script>
		<script>
		if (typeof window.MemoryInfo == "undefined") {
		  if (typeof window.performance.memory != "undefined") {
		    window.MemoryInfo = function () {};
		    window.MemoryInfo.prototype = window.performance.memory.__proto__;
		  }
		}
		</script>
	    
	    <script type="application/javascript" src="js/jquery.min.js"></script>
	    <script type="application/javascript" src="js/jquery-ui.min.js"></script>
	    <script type="application/javascript" src="js/popper.js"></script>
	    <script type="application/javascript" src="js/bootstrap.min.js"></script>
	    <script type="application/javascript" src="js/lodash.js"></script>
	    <script type="application/javascript" src="js/backbone.js"></script>
	    <script type="application/javascript" src="js/joint.js"></script>
	    <script type="application/javascript" src="js/gridstack.min.js"></script>
	    <script type="application/javascript" src="js/gridstack.jQueryUI.min.js"></script>
	    <script type="application/javascript" src="js/gluelines.js"></script>
	    <script type="application/javascript" src="js/svgsaver.js"></script>
	    <script type="application/javascript" src="js/sprintf.min.js"></script>
	    <script type="application/javascript" src="js/pyro_core.js"></script>
	    <script type="application/javascript" src="js/pyro_editor_grid.js"></script>
	    <script type="application/javascript" src="js/pyro_micro.js"></script>
	    
	«FOR g:gc.concreteGraphModels»
		<script type="application/javascript" src="«g.shapePath»"></script>
		<script type="application/javascript" src="«g.controllerPath»"></script>
	«ENDFOR»
	    <script type="module" defer src="main.dart.js"></script>
	    	</head>
		  	<body class="org-body-bg-color org-body-text-color">
			<pyro-app>
				<div style="max-width: 320px; margin: auto; height:100%;text-align:center;padding-top: 5%;">
					«IF gc.cpd.image128.nullOrEmpty»
						<img style="top: 10%;left:50%" src="img/pyro.png">
					«ELSE»
						<img style="max-width: 300px;max-height: 300px;top: 10%;left:50%" src="cpd/«gc.cpd.image128.trimQuotes»">
					«ENDIF»
					<h3 class="org-body-text-color">Loading «gc.cpd.name»..</h3>
					<div class="progress" style="width: 100%; margin-top: 20px;">
				    	<div class="org-nav-bg-color org-body-text-color progress-bar progress-bar-striped active" style="width: 100%;"></div>
					</div>
				</div>
			</pyro-app>
	  </body>
	</html>
	
	'''
}
