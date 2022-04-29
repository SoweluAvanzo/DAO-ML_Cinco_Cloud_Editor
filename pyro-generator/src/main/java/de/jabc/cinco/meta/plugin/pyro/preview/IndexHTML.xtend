package de.jabc.cinco.meta.plugin.pyro.preview

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.Node

class IndexHTML extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename()'''index.html'''
	
	def content()
	'''
	<!DOCTYPE html>
	<html><head>
	    <title>Pyro</title>
	    <meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <link rel="stylesheet" href="vendor/web/css/bootstrap.min.css">
	    <link rel="stylesheet" href="vendor/web/css/joint.css">
	    <script type="application/javascript" src="vendor/web/js/jquery.min.js"></script>
	    <script type="application/javascript" src="vendor/web/js/bootstrap.min.js"></script>
	    <script type="application/javascript" src="vendor/web/js/lodash.js"></script>
	    <script type="application/javascript" src="vendor/web/js/backbone.js"></script>
	    <script type="application/javascript" src="vendor/web/js/joint.js"></script>
	    <script type="application/javascript" src="vendor/web/js/pyro_core.js"></script>	    
	    «FOR g:gc.mglModels»
	    	<script type="application/javascript" src="js/«g.name.lowEscapeDart»_shapes.js"></script>
		«ENDFOR»
	    <style>
	      body {
	        padding: 50px;
	        color: white;
	        background-color: #333;
	        font-family: Roboto, Helvetica, Arial, sans-serif;
	      }
	    </style>
	  </head>
	  <body>
	  «FOR g:gc.mglModels»
	  <h1>«g.name.fuEscapeDart»</h1>
	  <div class="row">
	  	«FOR e:g.elements.filter[!isIsAbstract]»
	  	<div class="col-3">
	  		<h4>«e.name.escapeDart»</h4>
	  		<div id="paper_«g.name.lowEscapeDart»_«e.name.lowEscapeDart»" style="border: 2px solid gray; width: 200px; height: 200px;" class="joint-theme-default joint-paper"></div>
	  	</div>
	  	«ENDFOR»
	  </div>
	  «ENDFOR»
	  </body>
	  <script>
		$( document ).ready(function() {
			«FOR g:gc.mglModels»
				«FOR e:g.elements.filter[!isIsAbstract]»
					var $graph_«g.name.lowEscapeDart»_«e.name.lowEscapeDart» = new joint.dia.Graph;
					var $paper_«g.name.lowEscapeDart»_«e.name.lowEscapeDart» = new joint.dia.Paper({
						el: document.getElementById('paper_«g.name.lowEscapeDart»_«e.name.lowEscapeDart»'),
						width: 200,
						height: 200,
						gridSize: 1,
						model: $graph_«g.name.lowEscapeDart»_«e.name.lowEscapeDart»
					});
					«IF e instanceof Node»
						var elem = new «e.shapeFQN»({
							        position: {
							            x: 50,
							            y: 50
							        }
						});
						$graph_«g.name.lowEscapeDart»_«e.name.lowEscapeDart».addCells([ elem ]);
						$paper_«g.name.lowEscapeDart»_«e.name.lowEscapeDart».scaleContentToFit({padding:10});
					«ELSE»
						var link = new «e.shapeFQN»({
						        source: { x: 20, y: 50 },
						        target: { x: 180, y: 50 }
						 });
						 link.addTo($graph_«g.name.lowEscapeDart»_«e.name.lowEscapeDart»).reparent();
					«ENDIF»
				«ENDFOR»
			«ENDFOR»
		});
	</script>
	</html>
	'''
	
}
