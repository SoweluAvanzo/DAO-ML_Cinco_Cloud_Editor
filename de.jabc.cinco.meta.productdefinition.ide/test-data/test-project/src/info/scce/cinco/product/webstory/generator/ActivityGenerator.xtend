package info.scce.cinco.product.webstory.generator

import graphmodel.IdentifiableElement
import info.scce.cinco.product.webstory.webstory.Activity
import info.scce.cinco.product.webstory.webstory.Area
import info.scce.cinco.product.webstory.webstory.ClickArea
import info.scce.cinco.product.webstory.webstory.Color
import info.scce.cinco.product.webstory.webstory.Condition
import info.scce.cinco.product.webstory.webstory.EllipseClickArea
import info.scce.cinco.product.webstory.webstory.FalseTransition
import info.scce.cinco.product.webstory.webstory.FontSize
import info.scce.cinco.product.webstory.webstory.ModifyVariable
import info.scce.cinco.product.webstory.webstory.RectangleClickArea
import info.scce.cinco.product.webstory.webstory.Screen
import info.scce.cinco.product.webstory.webstory.StartMarker
import info.scce.cinco.product.webstory.webstory.TextArea
import info.scce.cinco.product.webstory.webstory.TrueTransition
import info.scce.cinco.product.webstory.webstory.WebStory
import info.scce.cinco.product.webstory.webstory.util.WebStorySwitch
import java.io.File
import java.util.HashMap
import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator
import info.scce.pyro.core.FileController
import java.io.IOException

class ActivityGenerator extends WebStorySwitch<CharSequence> {
	
	var generator = null as IGenerator<WebStory>;
	val activityNumbers = new HashMap<String, Integer>()
	
	new(IGenerator<WebStory> generator) {
		this.generator = generator
	}
	
	private def activityNumber(IdentifiableElement element) {
		val it = activityNumbers				
		get(element.id) ?: {
			put(element.id, size)
			size-1
		}			
	}
	
	def generate(WebStory webStory) {
		caseWebStory(webStory)
	}
	
	override caseWebStory(WebStory webStory) '''
		function getActivities() {
			return [
				«FOR activity: webStory.activitys SEPARATOR ',' »
					«doSwitch(activity)»
				«ENDFOR»
			]
		}
		«doSwitch(webStory.startMarkers.head)»
	'''

	override caseStartMarker(StartMarker startMarker) '''
		globalActivity = «startMarker.successor.activityNumber»;
	'''
	
	private def successor(StartMarker startMarker) {
		startMarker.successors.head
	} 

	override caseScreen(Screen screen) '''
		{
			type: "screen",
			activity: «screen.activityNumber»,
			imagePath: "«screen.absoluteBackgroundImage.jsonEscape»",
			clickAreas: [
				«FOR clickArea: screen.clickAreas SEPARATOR ','»
					«doSwitch(clickArea)»
				«ENDFOR»
			],
			textAreas : [
				«FOR textArea: screen.textAreas SEPARATOR ','»
					«doSwitch(textArea)»
				«ENDFOR»
			]
		}
	'''

	private def absoluteBackgroundImage(Screen screen) {
		val imageName = screen.backgroundImage
			.replace("/private", "")
			.replace(File.separator, "_")
			.replace("/", "_");
		val imagePath = ("./images/"+imageName+".jpg")
			.removeWindowsBackslashes.htmlEscape
		
		// create resource
		val file = FileController.loadFile(
			Long.parseLong(imageName)
		)
		
		try{
			generator.copyResource(file, imagePath);
		} catch(IOException e) {
			e.printStackTrace
		}
		
		imagePath
	}
	
	private def removeWindowsBackslashes(String text) {
		text.replace(File.separator, "/")
	}

	private def htmlEscape(String text) {
		text.replace(" ", "%20").replace("\"", "&quot;")
	}
	
	override caseModifyVariable(ModifyVariable modifyVariable) '''
		{
			type: "assignment",
			activity: «modifyVariable.activityNumber»,
			assignee: "«modifyVariable.variableSuccessors.head.name»",
			assignment: "«modifyVariable.value»",
			successor: «modifyVariable.getSuccessors(Activity).head.activityNumber»
		}
	'''
	
	override caseCondition(Condition condition) '''
		{
			type: "condition",
			activity: «condition.activityNumber»,
			condition: "«condition.variablePredecessors.head.name»",
			trueSuccessor: «condition.getOutgoing(TrueTransition).head.targetElement.activityNumber»,
			falseSuccessor: «condition.getOutgoing(FalseTransition).head.targetElement.activityNumber»,
		}
	'''

	override caseEllipseClickArea(EllipseClickArea area) {
		generateClickArea(area)
	}
	override caseRectangleClickArea(RectangleClickArea area) {
		generateClickArea(area)
	}

	private def generateClickArea(ClickArea area) '''
		{
			type: "«area.type»",
			nextActivity: «area.successor.activityNumber»,
			x: «area.relativeX»,
			y: «area.relativeY»,
			width: «area.relativeWidth»,
			height: «area.relativeHeight»,
		}
	'''
	
	private def successor(ClickArea area) {
		area.successors.head
	}
	
	
	override caseTextArea(TextArea area) '''
		{
			x: «area.relativeX»,
			y: «area.relativeY»,
			width: «area.relativeWidth»,
			height: «area.relativeHeight»,
			text: "«area.text.jsonEscape»",
			color: "«area.color.rgbCode»",
			size: «area.fontSize.vwValue»
		}
	'''

	private def jsonEscape(String text) {
		text.replace('"', "\\\"")
	}

	private def double relativeX(Area area) {
		area.x	as double / 360
	}
	
	private def double relativeWidth(Area area) {
		area.width	as double / 360
	}

	private def double relativeY(Area area) {
		area.y	as double / 270
	}

	private def double relativeHeight(Area area) {
		area.height as double / 270
	}

	
	private def type(ClickArea area) {
		switch area {
			RectangleClickArea: "rectangle"
			EllipseClickArea: "ellipse"
			default: "unknown"
		}
	}

	private def rgbCode(Color color) {
		switch (color) {
			case BLACK: "#000000"
			case WHITE: "#FFFFFF"
		}	
	}
	
	private def vwValue(FontSize size) {
		switch(size){
			case LARGE: 6
			case MEDIUM: 4
			case SMALL: 2
		}
	}
}
