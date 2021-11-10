package de.jabc.cinco.meta.plugin.pyro.backend

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.plugin.pyro.backend.connector.DataConnector
import de.jabc.cinco.meta.plugin.pyro.backend.connector.PyroGraphModelType
import de.jabc.cinco.meta.plugin.pyro.backend.core.EditorLayoutService
import de.jabc.cinco.meta.plugin.pyro.backend.core.GraphModelControllerGenerator
import de.jabc.cinco.meta.plugin.pyro.backend.core.InitializeSettingsBean
import de.jabc.cinco.meta.plugin.pyro.backend.core.ProjectService
import de.jabc.cinco.meta.plugin.pyro.backend.core.rest.GraphModelPropertyGenerator
import de.jabc.cinco.meta.plugin.pyro.backend.generator.IGeneratorGenerator
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.EcoreElementImplementation
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.EcoreElementInterface
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.GraphModelElementInterface
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.GraphModelFactoryInterface
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.GraphModelSwitch
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.TypeRegistry
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl.GraphModelElementImplementation
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl.GraphModelElementTransientImplementation
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl.GraphModelFactoryImplementation
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl.GraphModelFactoryTransientImplementation
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl.GraphModelInterpreter
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.auth.OAuthController
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.command.GraphModelCommandExecuter
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.controller.EcoreController
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.controller.GraphModelController
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.core.GraphmodelExporterGenerator
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.core.PyroFileControllerGenerator
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.adapter.McamAdapter
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.adapter.McamId
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli.ContainmentCheck
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli.Execution
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli.IncomingCheck
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli.OutgoingCheck
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.modules.checks.Module
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.rest.EcoreRestTO
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.rest.GraphModelRestTO
import de.jabc.cinco.meta.plugin.pyro.backend.service.ProjectServiceController
import de.jabc.cinco.meta.plugin.pyro.backend.service.rest.ServiceRestTO
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRegistry
import de.jabc.cinco.meta.plugin.pyro.util.Escaper
import de.jabc.cinco.meta.plugin.pyro.util.FileGenerator
import de.jabc.cinco.meta.plugin.pyro.util.FileHandler
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import java.io.File
import java.util.HashMap
import mgl.MGLModel
import mgl.ModelElement
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnum

class Generator extends FileGenerator {
	
	protected extension MGLExtension mglExtension
	protected extension Escaper = new Escaper
	
	new(String base) {
		super(base)
	}
	
	def generator(GeneratorCompound gc, String projectLocation) {
		
		mglExtension = gc.mglExtension
		
		val businessPath = "app/"
		val businessBasePath = businessPath+"src/main/java/"
		
		//create data connector
		{
			//entity
			val path = businessBasePath
			val gen = new DataConnector(gc)
			gen.generateFiles
			gen.models.forEach[m|{
				val p = path + m.path
				generateJavaFile(p,
					m.fileNameDataConnector,
					m.content
				)
			}]
			val gen2 = new PyroGraphModelType(gc)
			val entityPath = path + File.separator + "entity" + File.separator + "core" + File.separator;
			generateJavaFile(
				entityPath,
				gen2.fileName,
				gen2.content
			)
		}
		
		// create ProjectService
		{
			val path = businessBasePath+"info/scce/pyro/core"
			val gen = new ProjectService(gc)
			generateJavaFile(path,
				gen.fileNameDispatcher,
				gen.contentDispatcher
			)
		}
		// create PyroFileController
		{
			val path = businessBasePath+"info/scce/pyro/core"
			val gen = new PyroFileControllerGenerator(gc)
			generateJavaFile(path,
				gen.filename,
				gen.content
			)
		}
		// create InitializeSettingsBean
		{
			val path = businessBasePath+"info/scce/pyro/core"
			val gen = new InitializeSettingsBean(gc)
			generateJavaFile(path,
				gen.filename,
				gen.content
			)
		}
		
		// create Project Service Rest TOs
		{
			if(!gc.projectServices.empty) {
				
				val path = businessBasePath+"info/scce/pyro/service/rest"
				val gen = new ServiceRestTO(gc)
				gc.projectServices.forEach[
					generateJavaFile(path,
						gen.filename(it),
						gen.content(it)
					)
				]
				
				val servicePath = businessBasePath+"info/scce/pyro/service"
				val gen2 = new ProjectServiceController(gc)
				generateJavaFile(servicePath,
						gen2.filename(),
						gen2.content()
					)
			}
		}
		
		
		
		// create OAuth Authenticator
		{
			if(gc.authCompound !== null) {
				
				val path = businessBasePath+"info/scce/pyro/auth"
				val gen = new OAuthController(gc)
				generateJavaFile(path,
					gen.filename(),
					gen.content()
				)
			}
		}
		
		// create CINCO Exporter
		{
				
			val path = businessBasePath+"info/scce/pyro/core/export"
			val gen = new GraphmodelExporterGenerator(gc)
			gc.graphMopdels.filter[!isAbstract].forEach[g|{
				
				generateJavaFile(path,
					gen.filename(g),
					gen.content(g)
				)
			}]
		}
		
		// create CINCO Exporter
		
		// create GraphModelController
		{
			val path = businessBasePath+"info/scce/pyro/core"
			val gen = new GraphModelControllerGenerator(gc)
			generateJavaFile(path,
				gen.fileNameDispatcher,
				gen.contentDispatcher
			)
		}
		
		// create PyroFileController
		{
			val path = businessBasePath+"info/scce/pyro/core"
			val gen = new PyroFileControllerGenerator(gc)
			generateJavaFile(path,
				gen.filename,
				gen.content
			)
		}
		
		// create EditorLayoutService
		{
			val path = businessBasePath+"info/scce/pyro/core"
			val gen = new EditorLayoutService(gc)
			generateJavaFile(path,
				gen.fileNameDispatcher,
				gen.contentDispatcher
			)
		}
		
		{
			val path = businessBasePath+"info/scce/pyro/interpreter/"
			val gen = new GraphModelInterpreter(gc)
			gc.graphMopdels.filter[!isAbstract].forEach[g|{
				val graphPath = path + g.modelPackage.name.lowEscapeJava
				clearDirectory(graphPath)
				generateJavaFile(graphPath,
						gen.filename(g),
						gen.content(g)
					)
			}]
		}
		
		//create graphmodel rest TOs
		{
			val path = businessBasePath+"info/scce/pyro/"
			val gen = new GraphModelRestTO(gc)
			gc.mglModels.forEach[m|{
				val styles = CincoUtil.getStyles(m)
				val modelPackagePath = path + m.name+"/rest"
				clearDirectory(modelPackagePath)
				m.elements.forEach[e|
					generateJavaFile(modelPackagePath,
						gen.filename(e),
						gen.content(e,styles)
					)
				]				
			}]
		}
		{
			val path = businessBasePath+"info/scce/pyro/core/rest/types/"
			val gen3 = new GraphModelPropertyGenerator(gc)
			generateJavaFile(path,
						gen3.fileName(),
						gen3.content()
			)
		}
		{
			val path = businessBasePath+"de/jabc/cinco/meta/plugin/generator/runtime/"
			val gen0 = new IGeneratorGenerator(gc)
			generateJavaFile(path,
						gen0.filename(),
						gen0.content()
			)
		}
		
		//create ecore rest TOs
		{
			val path = businessBasePath+"info/scce/pyro/"
			val gen = new EcoreRestTO(gc)
			gc.ecores.forEach[g|{
				val ecorePath = path + g.name+"/rest"
				clearDirectory(ecorePath)
				generateJavaFile(ecorePath,
						gen.filenameEcore(g),
						gen.contentEcore(g)
					)
				generateJavaFile(ecorePath,
						gen.filenamePackage(g),
						gen.contentPackage(g)
					)
				g.elements.forEach[t|{
					generateJavaFile(ecorePath,
						gen.filenameStructural(t),
						gen.contentStructural(t,g)
					)
				}]
				
			}]
		}
		
		{
			val pluginPath = businessBasePath+"info/scce/pyro/plugin"
			clearDirectory(pluginPath)
			val plugins = new EditorViewPluginRegistry().getPlugins(gc)
			
			plugins.forEach[p|
				//generate rest controller java class
				generateJavaFile(pluginPath+"/controller",
					p.restController.filename,
					p.restController.content
				)
				
			]
		}
		
		//create mcam adapter and id
		{
			val path = businessBasePath
			val adapterGen = new McamAdapter(gc)
			val execGen = new Execution(gc)
			val idGen = new McamId(gc)
			val moduleGen = new Module(gc)
			val containmentCheckGen = new ContainmentCheck(gc)
			val incomingCheckGen = new IncomingCheck(gc)
			val outgoingCheckGen = new OutgoingCheck(gc)
			gc.graphMopdels.filter[hasChecks].forEach[g|{
				val mcampath = path+g.MGLModel.packagePath.toString+"/mcam/adapter";
				clearDirectory(mcampath)
				generateJavaFile(mcampath,
					adapterGen.filename(g),
					adapterGen.content(g)
				)
				generateJavaFile(mcampath,
					idGen.filename(g),
					idGen.content(g)
				)
				val execpath = path+g.MGLModel.packagePath.toString+"/mcam/cli";
				clearDirectory(execpath)
				generateJavaFile(execpath,
					execGen.filename(g),
					execGen.content(g)
				)
				val modulepath = path+g.MGLModel.packagePath.toString+"/mcam/modules/checks";
				clearDirectory(modulepath)
				generateJavaFile(modulepath,
					moduleGen.filename(g),
					moduleGen.content(g)
				)
				generateJavaFile(modulepath,
					containmentCheckGen.filename(g),
					containmentCheckGen.content(g)
				)
				generateJavaFile(modulepath,
					incomingCheckGen.filename(g),
					incomingCheckGen.content(g)
				)
				generateJavaFile(modulepath,
					outgoingCheckGen.filename(g),
					outgoingCheckGen.content(g)
				)
				
			}]
		}
		
		//create graph model API Interfaces
		{
			val path = businessBasePath
			val gen = new GraphModelElementInterface(gc)
			gc.mglModels.forEach[m|{
				val modelPackagePath = path+m.apiPath.toString;
				clearDirectory(modelPackagePath)
				//generate all modelelements
				m.elements.forEach[t|
					generateJavaFile(modelPackagePath,
						gen.filename(t),
						gen.content(t,false) 
					)
				]
				//generate graph model enumerations
				m.enumerations.forEach[e|
					generateJavaFile(modelPackagePath,
						gen.filename(e),
						gen.contentEnum(e)
					)
				]
			}]
		}
		
		//create ecore API Interfaces
		{
			val path = businessBasePath
			val gen = new EcoreElementInterface(gc)
			gc.ecores.forEach[g|{
				val ecorePath = path+g.apiPath.toString;
				val types = g.EClassifiers.filter(EClass)
				val enums = g.EClassifiers.filter(EEnum)
				
				clearDirectory(ecorePath)
				(#[g]+types).forEach[t|{
					generateJavaFile(ecorePath,
						gen.filename(t),
						gen.content(t,g)
					)
				}]
				//generate graph model enumerations
				enums.forEach[e|
					generateJavaFile(ecorePath,
						gen.filename(e),
						gen.contentEnum(e,g)
					)
				]
				
			}]
		}
		
		//create ecore API Implementation
		{
			//dywa-app.app-business.target.generated-sources.info.scce.pyro.data
			val path = businessBasePath
			val gen = new EcoreElementImplementation(gc)
			gc.ecores.forEach[g| {
				val ecorePath = path+g.apiImplPath.toString;
				val types = g.EClassifiers.filter(EClass).filter[!isAbstract]
				
				clearDirectory(ecorePath)
				
				(#[g]+types).forEach[t|{
					generateJavaFile(ecorePath,
						gen.filename(t),
						gen.content(t, g)
					)
				}]
			}]
			
		}
		
		
		//create graph model API Implementation
		{
			//dywa-app.app-business.target.generated-sources
			val path = businessBasePath
			val gen = new GraphModelElementImplementation(gc)
			gc.mglModels.forEach[g|{
				val styles = CincoUtil.getStyles(g)
				val graphPath = path+g.apiImplPath.toString;
				clearDirectory(graphPath)
				(#[g]+(g.elementsAndTypes)).filter(ModelElement).filter[!isIsAbstract].forEach[t|{
					generateJavaFile(graphPath,
						gen.filename(t),
						gen.content(t,g,styles)
					)
				}]
				
			}]
		}
		
		/**
		 * Transient API Generation
		 */
		 {
			//dywa-app.app-business.target.generated-sources.info.scce.pyro.data
			val path = businessBasePath
			val gen = new GraphModelElementInterface(gc)
			gc.transientGraphModels.values.forEach[g|{
				val graphPath = path+g.apiPath.toString;
				clearDirectory(graphPath)
				(#[g]+(g.elementsAndTypes)).filter(ModelElement).forEach[t|{
					generateJavaFile(graphPath,
						gen.filename(t),
						gen.content(t,true)
					)
				}]
				//generate graph model enumerations
				(g.modelPackage as MGLModel).enumerations.forEach[e|
					generateJavaFile(graphPath,
						gen.filename(e),
						gen.contentEnum(e)
					)
				]
				
			}]
		}
		
		//create graph model API Implementation
		{
			//dywa-app.app-business.target.generated-sources
			val path = businessBasePath
			val gen = new GraphModelElementTransientImplementation(gc)
			gc.transientGraphModels.values.forEach[e|{
				val styles = CincoUtil.getStyles(e)
				val graphPath = path+e.apiImplPath.toString;
				clearDirectory(graphPath)
				(#[e]+(e.elementsAndTypes)).filter(ModelElement).filter[!isIsAbstract].forEach[t|{
					generateJavaFile(graphPath,
						gen.filename(t),
						gen.content(t,styles)
					)
				}]
				
			}]
		}
		
		//create graph model Factory interface
		{
			//dywa-app.app-business.target.generated-sources
			val path = businessBasePath
			val gen = new GraphModelFactoryInterface(gc)
			gc.transientGraphModels.values.filter[!isAbstract].forEach[g|{
				val graphPath = path+g.apiPath.toString;
				generateJavaFile(graphPath,
					gen.filename(g),
					gen.content(g,true)
				)
			}]
		}
		
		//create graph model Factory impl
		{
			//dywa-app.app-business.target.generated-sources
			val path = businessBasePath
			val gen = new GraphModelFactoryTransientImplementation(gc)
			gc.transientGraphModels.values.filter[!isAbstract].forEach[g|{
				val graphPath = path+g.apiImplPath.toString;
				generateJavaFile(graphPath,
					gen.filename(g),
					gen.content(g)
				)
			}]
		}		
		
		//create graph model switch
		{
			val path = businessBasePath
			val gen = new GraphModelSwitch(gc)
			gc.mglModels.forEach[m|{
				val modelPackagePath = path+m.apiPath.toString+"/util";
				clearDirectory(modelPackagePath)
				m.graphmodels.forEach[g|
					generateJavaFile(modelPackagePath,
						gen.filename(g),
						gen.content(g)
					)
				]
			}]
		}
		
		// create typeRegistry for modelElements
		{
			val path = businessBasePath
			val gen = new TypeRegistry(gc)
			gc.mglModels.forEach[m|{
				val modelPackagePath = path+m.apiPath.toString+"/util";
				generateJavaFile(modelPackagePath,
					gen.filename,
					gen.content(m)
				)
			}]
		}		
		
		//create graph model Factory interface
		{
			val path = businessBasePath
			val gen = new GraphModelFactoryInterface(gc)
			gc.mglModels.forEach[m|{
				val modelPackagePath = path+m.apiPath.toString;
				m.graphModels.filter[!isAbstract].forEach[g|	
					generateJavaFile(modelPackagePath,
						gen.filename(g),
						gen.content(g,false)
					)
				]
			}]
		}
		
		//create graph model Factory impl
		{
			val path = businessBasePath
			val gen = new GraphModelFactoryImplementation(gc)
			gc.mglModels.forEach[m|{
				val modelPackagePath = path+m.apiImplPath.toString;
				m.graphModels.filter[!isAbstract].forEach[g|	
					generateJavaFile(modelPackagePath,
						gen.filename(g),
						gen.content(g)
					)
				]
			}]
		}
		
		//create graphmodel rest controller
		{
			val path = businessBasePath+"info/scce/pyro/core"
			gc.graphMopdels.filter[!isAbstract].forEach[g|{
				val staticResourceFiles = new HashMap
				if(g.hasIncludeResourcesAnnotation) { 
					g.annotations.filter[name.equals("pyroGeneratorResource")&&!value.isEmpty].forEach[ann|{
						ann.value.forEach[v|{
							staticResourceFiles.put(v,FileHandler.getAllFiles(ann,v))
						}]
					}]
				}
				
				val styles = CincoUtil.getStyles(g)
				val gen = new GraphModelController(gc)
				generateJavaFile(path,
					gen.filename(g),
					gen.content(g,styles,staticResourceFiles)
				)
			}]
		}
		
		//create ecore rest controller
		{
			val path = businessBasePath+"info/scce/pyro/core"
			gc.ecores.forEach[g|{
				val gen = new EcoreController(gc)
				generateJavaFile(path,
					gen.filename(g),
					gen.content(g)
				)
			}]
		}
		
		//create executer
		{
			val path = businessBasePath+"info/scce/pyro/core/command"
			gc.graphMopdels.filter[!isAbstract].forEach[g|{
				val styles = CincoUtil.getStyles(g)
				val gen = new GraphModelCommandExecuter(gc)
				generateJavaFile(path,
					gen.filename(g),
					gen.content(g,styles)
				)
			}]
		}
		//copy annotated class
		{
			val path = basePath + "/app/src/main/java/"
			gc.mglModels.map[elements].flatten.map[annotations].flatten.filter[hasClassAnnotation].
				forEach[ann|
					FileHandler.copyAnnotatedClasses(ann,path,projectLocation)
				]
			
			gc.mglModels.map[g|CincoUtil.getStyles(g).styles.filter[!appearanceProvider.nullOrEmpty]].flatten.
			forEach[app|FileHandler.copyAppearanceProviderClasses(app,path,projectLocation)]
		}
		//copy annotated included generator resources
		{
			val path = basePath+"src/main/resources/META-INF/resources/"

			
			gc.graphMopdels.filter[hasIncludeResourcesAnnotation].
			forEach[g|{
				val graphPath = path + g.name.lowEscapeDart+"/"
				g.annotations.filter[name.equals("pyroGeneratorResource")&&!value.isEmpty].forEach[ann|{
					ann.value.forEach[v|{
						FileHandler.copyFileOrFolder(ann,v,graphPath)
					}]
					
				}]
			}]
		}
		//generate app pom
		{
			//app
			val path = businessPath
			val gen = new BusinessPomGenerator(gc)
			generateFile(path,
					gen.filename,
					gen.content
				)
		}
		
		//copy annotated additional JARs
		{
			val path = basePath+"/app/repo/info/scce/pyro/external/"
			gc.graphMopdels.filter[hasIncludeJARAnnotation].
			forEach[g|{
				val graphPath = path + g.name.lowEscapeJava+"/"
				g.annotations.filter[name.equals("pyroAdditionalJAR")&&!value.isEmpty].forEach[ann|{
					ann.value.forEach[v|{
						FileHandler.copyFile(ann,v,graphPath+v.jarFilename+"/1.0/",true,v.jarFilename+"-1.0.jar")
					}]
					
				}]
			}]
		}
	}	
}
