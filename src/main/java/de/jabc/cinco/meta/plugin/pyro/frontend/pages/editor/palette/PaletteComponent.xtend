package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.palette

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class PaletteComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNamePaletteComponent()'''palette_component.dart'''
	
	def contentPaletteComponent()
	'''
	import 'package:angular/angular.dart';
	
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart';
	«FOR g:gc.mglModels»
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.name.lowEscapeDart»;
	«ENDFOR»
	«FOR g:gc.concreteGraphModels»
		import 'package:«gc.projectName.escapeDart»/«g.paletteBuilderPath»';
	«ENDFOR»
	
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/list/list_view.dart';
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/list/list_component.dart';
	import 'package:«gc.projectName.escapeDart»/src/utils/graph_model_permission_utils.dart';
	
	@Component(
	    selector: 'palette',
	    templateUrl: 'palette_component.html',
	    directives: const [coreDirectives,ListComponent],
	    styleUrls: const ['package:«gc.projectName.escapeDart»/src/pages/editor/editor_component.css']
	)
	class PaletteComponent implements OnInit, OnChanges {
	
	  @Input()
	  GraphModel currentGraphModel;
	  
	  @Input()
	  List<PyroGraphModelPermissionVector> permissionVectors;
	
	  List<MapList> map;
	  bool canEdit;
	
	  PaletteComponent() {
	    permissionVectors = new List();
	    map = new List<MapList>();
	    canEdit = false;
	  }
	
	  @override
	  void ngOnInit() {
	    buildList();
	  }
	
	  void buildList() {
	  	if(currentGraphModel!=null)
	  	{
	  		«FOR g:gc.concreteGraphModels SEPARATOR " else "
	  		»if(is«g.name.fuEscapeDart»(currentGraphModel)) {
	  			map = «g.name.fuEscapeDart»PaletteBuilder.build(currentGraphModel);
	  			//canEdit = GraphModelPermissionUtils.canUpdate("«g.name.toUnderScoreCase»", permissionVectors);
	  			canEdit = true;
	  		}«
	  		ENDFOR»
	    } else {
	       map = null;
	    }
	  }
	  
	  «FOR g:gc.concreteGraphModels»
	  	/// check graph model type
	  	bool is«g.name.fuEscapeDart»(GraphModel graph) {
	  		return graph.$type()=='«g.typeName»';
	  	}
	  «ENDFOR»
	
	  @override
	  ngOnChanges(Map<String, SimpleChange> changes) {
	    buildList();
	  }
	}
	
	'''
	
}
