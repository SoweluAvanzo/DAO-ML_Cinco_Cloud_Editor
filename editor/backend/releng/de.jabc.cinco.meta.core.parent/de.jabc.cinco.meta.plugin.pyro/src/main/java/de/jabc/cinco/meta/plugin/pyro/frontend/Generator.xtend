package de.jabc.cinco.meta.plugin.pyro.frontend

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.plugin.pyro.frontend.deserializer.Deserializer
import de.jabc.cinco.meta.plugin.pyro.frontend.model.Model
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.EditorComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.canvas.CanvasComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.canvas.graphs.graphmodel.GraphmodelComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.check.CheckComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.explorer.graphentry.FileEntryComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.explorer.graphentry.create.CreateFileComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.map.MapComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.palette.PaletteComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.palette.graphs.graphmodel.PaletteBuilder
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.palette.list.ListComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.graphs.graphmodel.GraphmodelTree
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.graphs.graphmodel.IdentifiableElementPropertyComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.graphs.graphmodel.PropertyComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.tree.TreeComponet
import de.jabc.cinco.meta.plugin.pyro.frontend.service.GraphService
import de.jabc.cinco.meta.plugin.pyro.util.FileGenerator
import de.jabc.cinco.meta.plugin.pyro.util.FileHandler
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import de.jabc.cinco.meta.plugin.pyro.frontend.model.Core
// import de.jabc.cinco.meta.plugin.pyro.util.Escaper
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.login.LoginComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.main.MainComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.menu.MenuComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.welcome.WelcomeComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.admin.user_management.users.UsersComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.logout.LogoutComponent
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.shared.navigation.NavigationComponent
import mgl.GraphicalModelElement

class Generator extends FileGenerator {

	extension MGLExtension mglExtension
	// extension Escaper = new Escaper

	new(String base) {
		super(base)
	}

	def generate(GeneratorCompound gc, String projectLocation) {
		mglExtension = gc.mglExtension

		val mglModels = gc.mglModels

		{
			// web
			val path = "web"
			val gen = new Main(gc)
			generateFile(
				path,
				gen.fileNameMain,
				gen.contentMain
			)
		}

		{
			// web
			val path = "web"
			val gen = new Index(gc)
			generateFile(
				path,
				gen.fileNameIndex,
				gen.contentIndex
			)
		}

		{
			// lib.deserialzer
			val path = "lib/src/deserializer"
			val gen = new Deserializer(gc)
			mglModels.forEach [ g |
				{
					generateFile(
						path,
						gen.fileNameGraphmodelPropertyDeserializer(g.name),
						gen.contentGraphmodelPropertyDeserializer(g)
					)
				}
			]
			generateFile(
				path,
				gen.fileNamePropertyDeserializer(),
				gen.contentPropertyDeserializer()
			)
		}
		{
			// lib.model
			val path = "lib/src/model"
			val gen = new Model(gc)
			generateFile(
				path,
				gen.fileNameDispatcher,
				gen.contentDispatcher
			)
			mglModels.forEach [ m |
				val styles = CincoUtil.getStyles(m)
				generateFile(
					path,
					gen.fileNameGraphModel(m),
					gen.contentGraphmodel(m, styles)
				)
			]
			gc.ecores.forEach [ g |
				{ // NOTE: SAMI: quarkus untested
					generateFile(
						path,
						gen.fileNameEcore(g),
						gen.contentEcore(g)
					)
				}
			]
		}

		{
			val path = "lib/src/model"
			val gen = new Core(gc)
			generateFile(
				path,
				gen.fileNameDispatcher,
				gen.contentDispatcher
			)
		}

		{
		// lib.editor
			val path = "lib/src/pages/editor"
			val gen = new EditorComponent(gc)

			// view plugins
			generateFile(
				path,
				gen.fileNameEditorComponent,
				gen.contentEditorComponent()
			)
			generateFile(
				path,
				gen.fileNameEditorTemplate,
				gen.contentEditorTemplate()
			)
		}

		{
			// lib.editor.menu
			val path = "lib/src/pages/editor/menu"
			val gen = new MenuComponent(gc)

			// view plugins
			generateFile(
				path,
				gen.fileNameMenuComponent,
				gen.contentMenuComponent
			)
			generateFile(
				path,
				gen.fileNameMenuTemplate,
				gen.contentMenuTemplate
			)
		}

		{
			// lib.pages.editor.canvas.graphs.graphmodel
			mglModels.forEach [ m |
				val styles = CincoUtil.getStyles(m)
				m.graphModels.forEach [ g |
					val path = "lib/" + g.componentPackage
					clearDirectory(path)
					val gen = new GraphmodelComponent(gc)
					generateFile(
						path,
						gen.fileNameGraphModelCommandGraph(g),
						gen.contentGraphModelCommandGraph(g, styles)
					)
					generateFile(
						path,
						gen.fileNameGraphModelComponent(g),
						gen.contentGraphModelComponent(g, styles)
					)
					generateFile(
						path,
						gen.fileNameGraphModelComponentTemplate(g),
						gen.contentGraphModelComponentTemplate(g)
					)
				]

			]

		}

		{
			// lib.editor.canvas
			val path = "lib/src/pages/editor/canvas"
			val gen = new CanvasComponent(gc)
			generateFile(
				path,
				gen.fileNameCanvasComponent,
				gen.contentCanvasComponent
			)
			generateFile(
				path,
				gen.fileNameCanvasComponentTemplate,
				gen.contentCanvasComponentTemplate
			)
		}

		{
			// lib.editor.check
			val path = "lib/src/pages/editor/check"
			val gen = new CheckComponent(gc)
			generateFile(
				path,
				gen.fileNameCheckComponent,
				gen.contentCheckComponent
			)
			generateFile(
				path,
				gen.fileNameCheckComponentTemplate,
				gen.contentCheckComponentTemplate
			)
		}

		{
			// lib.pages.login
			val path = "lib/src/pages/login"
			val gen = new LoginComponent(gc)
			generateFile(
				path,
				gen.fileNameLoginComponent,
				gen.contentLoginComponent
			)
			generateFile(
				path,
				gen.fileNameLoginTemplate,
				gen.contentLoginTemplate
			)
		}

		{
			// lib.pages.shared.navigation
			val path = "lib/src/pages/shared/navigation"
			val gen = new NavigationComponent(gc)
			generateFile(
				path,
				gen.fileNameTemplate,
				gen.contentTemplate
			)

		}

		{
			// lib.pages.logout
			val path = "lib/src/pages/logout"
			val gen = new LogoutComponent(gc)
			generateFile(
				path,
				gen.fileNameTemplate,
				gen.contentTemplate
			)
		}

		{
			// lib.pages.welcome
			val path = "lib/src/pages/welcome"
			val gen = new WelcomeComponent(gc)
			generateFile(
				path,
				gen.fileNameTemplate,
				gen.contentTemplate
			)
		}

		{
			// lib.pages.admin.user_management.users
			val path = "lib/src/pages/admin/user-management/users"
			val gen = new UsersComponent(gc)
			generateFile(
				path,
				gen.fileNameTemplate,
				gen.contentTemplate
			)
			generateFile(
				path,
				gen.fileNameComponent,
				gen.contentComponent
			)
		}

		{
			// lib.pages.main
			val path = "lib/src/pages/main"
			val gen = new MainComponent(gc)
			generateFile(
				path,
				gen.fileNameTemplate,
				gen.contentTemplate
			)
		}

		{
			// lib.editor.explorer.graph_entry
			val path = "lib/src/pages/editor/explorer/graph_entry"
			val gen = new FileEntryComponent(gc)
			generateFile(
				path,
				gen.fileNameFileEntryComponent,
				gen.contentFileEntryComponent
			)
		}

		{
			// lib.editor.explorer.graph_entry.create
			val path = "lib/src/pages/editor/explorer/graph_entry/create"
			val gen = new CreateFileComponent(gc)
			generateFile(
				path,
				gen.fileNameCreateFileComponent,
				gen.contentCreateFileComponent
			)
			generateFile(
				path,
				gen.fileNameCreateFileComponentTemplate,
				gen.contentCreateFileComponentTemplate
			)
		}

		{
			// lib.editor.map
			val path = "lib/src/pages/editor/map"
			val gen = new MapComponent(gc)
			generateFile(
				path,
				gen.fileNameMapComponent,
				gen.contentMapComponent
			)
			generateFile(
				path,
				gen.fileNameMapComponentTemplate,
				gen.contentMapComponentTemplate
			)
		}

		{
			// lib.editor.palette
			val path = "lib/src/pages/editor/palette"
			val gen = new PaletteComponent(gc)
			generateFile(
				path,
				gen.fileNamePaletteComponent,
				gen.contentPaletteComponent
			)
		}

		{
			// lib.editor.palette.graphs.graphmodel
			mglModels.forEach [ m |
				m.graphModels.forEach [ g |

					val path = "lib/" + g.paletteBuilderPackage
					val gen = new PaletteBuilder(gc)
					generateFile(
						path,
						gen.fileNamePaletteBuilder,
						gen.contentPaletteBuilder(g)
					)
				]
			]

		}

		{
			// lib.editor.palette.list
			val path = "lib/src/pages/editor/palette/list"
			val gen = new ListComponent(gc)
			generateFile(
				path,
				gen.fileNameListComponent,
				gen.contentListComponent
			)
		}

		{
			// lib.editor.properties.graphs.graphmodel
			mglModels.forEach [ m |
				val path = "lib/" + m.propertyPackagePath

				m.graphModels.forEach [ g |
					val treeGen = new GraphmodelTree(gc)
					generateFile(
						path,
						treeGen.fileNameGraphmodelTree(g),
						treeGen.contentGraphmodelTree(g)
					)

					val propGen = new PropertyComponent(gc)
					val graphModelPath = "lib/" + g.propertyGraphModelPath
					generateFile(
						graphModelPath,
						propGen.fileNamePropertyComponent,
						propGen.contentPropertyComponent(g)
					)
					generateFile(
						graphModelPath,
						propGen.fileNamePropertyComponentTemplate,
						propGen.contentPropertyComponentTemplate(g)
					)
				]

				val propertyTypes = m.elements
				propertyTypes.forEach [ t |
					{
						val gen = new IdentifiableElementPropertyComponent(gc)
						generateFile(
							path,
							gen.fileNameIdentifiableElementPropertyComponent(t),
							gen.contentIdentifiableElementPropertyComponent(t)
						)
						generateFile(
							path,
							gen.fileNameIdentifiableElementPropertyComponentTemplate(t),
							gen.contentIdentifiableElementPropertyComponentTemplate(t)
						)
					}
				]
			]
		}
		{
			// lib.editor.properties.property
			val path = "lib/src/pages/editor/properties/property"
			val gen = new de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.properties.property.PropertyComponent(gc)
			generateFile(
				path,
				gen.fileNamePropertyComponent,
				gen.contentPropertyComponent
			)
			generateFile(
				path,
				gen.fileNamePropertyComponentTemplate,
				gen.contentPropertyComponentTemplate
			)
		}
		{
			// lib.editor.properties.tree
			val path = "lib/src/pages/editor/properties/tree"
			val gen = new TreeComponet(gc)
			generateFile(
				path,
				gen.fileNameTreeComponent,
				gen.contentTreeComponent
			)
		}

		{
			// lib.service
			val path = "lib/src/service"
			val gen = new GraphService(gc)
			generateFile(
				path,
				gen.fileNameGraphServcie,
				gen.contentGraphService
			)
		}
		{
			// .
			val path = ""
			val gen = new Pubspec(gc)
			generateFile(
				path,
				gen.fileNamePubspec,
				gen.contentPubspec
			)
		}

		// copy icons
		gc.graphMopdels.forEach [ g |
			{
				if (!g.iconPath.nullOrEmpty) {
					FileHandler.copyFile(g, g.iconPath,
						basePath + "/web/" + g.iconPath(false).toString.toLowerCase, true, projectLocation)
				}
				g.elements.filter(GraphicalModelElement).filter[hasIcon].forEach [ e |
					FileHandler.copyFile(e, e.eclipseIconPath,
						basePath + "/web/" + e.iconPath(false).toString.toLowerCase, true, projectLocation)
				]
			}
		]

		if (!gc.cpd.image128.nullOrEmpty) {
			FileHandler.copyFile(gc.cpd, gc.cpd.image128.toString, basePath + "/web/" + gc.cpd.image128.cpdImagePath,
				true, projectLocation)
		}
		if (!gc.cpd.image64.nullOrEmpty) {
			FileHandler.copyFile(gc.cpd, gc.cpd.image64.toString, basePath + "/web/" + gc.cpd.image64.cpdImagePath,
				true, projectLocation)
		}
		if (!gc.cpd.image32.nullOrEmpty) {
			FileHandler.copyFile(gc.cpd, gc.cpd.image32.toString, basePath + "/web/" + gc.cpd.image32.cpdImagePath,
				true, projectLocation)
		}
		if (!gc.cpd.image16.nullOrEmpty) {
			FileHandler.copyFile(gc.cpd, gc.cpd.image16.toString, basePath + "/web/" + gc.cpd.image16.cpdImagePath,
				true, projectLocation)
		}

	}
}
