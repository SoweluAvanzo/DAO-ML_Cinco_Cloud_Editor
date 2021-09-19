package de.jabc.cinco.meta.plugin.pyro.canvas

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.LinkedHashMap
import java.util.LinkedList
import java.util.List
import java.util.Map
import mgl.Edge
import mgl.GraphicalModelElement
import mgl.Node
import org.eclipse.emf.ecore.EObject
import style.AbsolutPosition
import style.AbstractPosition
import style.AbstractShape
import style.Alignment
import style.BooleanEnum
import style.Color
import style.ConnectionDecorator
import style.ContainerShape
import style.DecoratorShapes
import style.EdgeStyle
import style.Ellipse
import style.Font
import style.GraphicsAlgorithm
import style.HAlignment
import style.Image
import style.LineStyle
import style.MultiText
import style.NodeStyle
import style.Polygon
import style.Polyline
import style.PredefinedDecorator
import style.Rectangle
import style.RoundedRectangle
import style.Shape
import style.Size
import style.Styles
import style.Text
import style.VAlignment
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import mgl.MGLModel

class Shapes extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameShapes(MGLModel g)'''«g.name.lowEscapeDart»_shapes.js'''
	
	
	def createNode(Node node, Styles styles)
	{
		val modelPackage = node.modelPackage
		val styleForNode = node.styling(styles) as NodeStyle
		val markups = styleForNode.mainShape.collectMarkupTags("x",0).entrySet
		val markupCSS = styleForNode.mainShape.collectMarkupCSSTags("x",0,null).entrySet
		val scalables = new LinkedList
		for(it:markups){
			if(!key.getIsNotScalable){
				scalables.add(it)
			}
		}
		//val noneTextual = markups.filter[!key.isTextual].toList
		val notScalables = markups.filter[key.getIsNotScalable].toList
		'''
		«node.shapeFQN» = joint.shapes.pyro.ToolElement.extend({
			
			    markup: '<g class="rotatable"><g class="scalable">«scalables.map[value].join»</g>«notScalables.map[value].join»</g>',
			    defaults: _.defaultsDeep({
			
			        type: '«modelPackage.name.lowEscapeDart».«node.name.fuEscapeDart»',
			        size: {
			            width: «IF styleForNode.mainShape.size ===null»«MGLExtension.DEFAULT_WIDTH»«ELSE»«styleForNode.mainShape.size.width»«ENDIF»,
			            height: «IF styleForNode.mainShape.size ===null»«MGLExtension.DEFAULT_HEIGHT»«ELSE»«styleForNode.mainShape.size.height»«ENDIF»
			        },
			        attrs: {
			            '.': {
			                magnet: 'passive'
			            },
			            «node.disableFeatures»
			            «markupCSS.map[value].join(",\n")»
			        }
			    }, joint.shapes.pyro.ToolElement.prototype.defaults)
			});
		'''
	}
	
	def CharSequence disableFeatures(GraphicalModelElement node)
	'''
	hasInformation:«IF !node.information»true«ELSE»false«ENDIF»,
	«IF node instanceof Node»
	editLabel:«IF !node.directlyEditable»true«ELSE»false«ENDIF»,
	«ENDIF»
	disableResize:«IF !node.resizable»true«ELSE»false«ENDIF»,
	disableRemove:«IF !node.removable»true«ELSE»false«ENDIF»,
	disableSelect:«IF !node.selectbale»true«ELSE»false«ENDIF»,
	disableEdge:«IF node instanceof Node && !(node as Node).connectable»true«ELSE»false«ENDIF»,
	'''
	
	
	def String markupChildren(ContainerShape a,String s){
		var result = ""
		for(var i=0;i<a.children.length;i++){
			result += a.children.get(i).markup(s+"x",i)
		}
		result
	}
	
	def String markupCSSChildren(ContainerShape a,String s,String ref){
		var result = ""
		for(var i=0;i<a.children.length;i++){
			result += a.children.get(i).markupCSS(s+"x",i,ref)
		}
		result
	}
	
	
	def tagClass(String s,int i)'''pyro«s»«i»tag'''
	
	
	def Map<AbstractShape,CharSequence> collectMarkupTags(AbstractShape shape,String prefix,int i){
		val l = new LinkedHashMap
		l.put(shape,shape.markup(prefix,i))
		if(shape instanceof ContainerShape) {
			shape.children.forEach[n,idx|l.putAll(n.collectMarkupTags(i+"x",idx))]			
		}
		return l
	}
	
	def Map<AbstractShape,CharSequence> collectSelectorTags(AbstractShape shape,String prefix,int i){
		val l = new LinkedHashMap
		l.put(shape,shape.selector(prefix,i))
		if(shape instanceof ContainerShape) {
			shape.children.forEach[n,idx|l.putAll(n.collectSelectorTags(i+"x",idx))]			
		}
		return l
	}
	
	def Map<EObject,CharSequence> collectMarkupTags(GraphicsAlgorithm shape,String prefix,int i){
		val l = new LinkedHashMap
		l.put(shape,shape.markup(prefix,i))
		if(shape instanceof ContainerShape) {
			shape.children.forEach[n,idx|l.putAll(n.collectMarkupTags(i+"x",idx))]			
		}
		return l
	}
	
	
	def Map<AbstractShape,CharSequence> collectMarkupCSSTags(AbstractShape shape,String prefix,int i,String ref){
		val l = new LinkedHashMap
		l.put(shape,shape.markupCSS(prefix,i,ref))
		if(shape instanceof ContainerShape) {
			shape.children.forEach[n,idx|l.putAll(n.collectMarkupCSSTags(i+"x",idx,'''«shape.tagName».«prefix.tagClass(i)»'''))]			
		}
		return l
	}
	
	def Map<EObject,CharSequence> collectMarkupCSSTags(GraphicsAlgorithm shape,String prefix,int i,String ref){
		val l = new LinkedHashMap
		l.put(shape,shape.markupCSS(prefix,i,ref))
		if(shape instanceof ContainerShape) {
			shape.children.forEach[n,idx|l.putAll(n.collectMarkupCSSTags(i+"x",idx,'''«shape.tagName».«prefix.tagClass(i)»'''))]			
		}
		return l
	}
	
	def dispatch markup(Rectangle shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»" />'''
	
	def dispatch markup(Text shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»"/>'''
	
	
	def dispatch markup(MultiText shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»"/>'''
	
	def dispatch markup(Ellipse shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»" />'''
	
	def dispatch markup(Polyline shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»"/>'''
	
	def dispatch markup(Polygon shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»"/>'''
	
	def dispatch markup(Image shape,String s,int i)
	'''<«shape.tagName»«IF shape.size!==null» preserveAspectRatio="none"«ENDIF» class="«s.tagClass(i)»"/>'''
	
	def dispatch markup(RoundedRectangle shape,String s,int i)
	'''<«shape.tagName» class="«s.tagClass(i)»" />'''
	
	def dispatch tagName(Rectangle shape)
	'''rect'''
	
	def dispatch tagName(Text shape)
	'''text'''
	
	
	def dispatch tagName(MultiText shape)
	'''text'''
	
	def dispatch tagName(Ellipse shape)
	'''ellipse'''
	
	def dispatch tagName(Polyline shape)
	'''polyline'''
	
	def dispatch tagName(Polygon shape)
	'''polygon'''
	
	def dispatch tagName(Image shape)
	'''image'''
	
	def dispatch tagName(RoundedRectangle shape)
	'''rect'''
	
	
	def dispatch selector(Rectangle shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(Text shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(MultiText shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(Ellipse shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(Polyline shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(Polygon shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(Image shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	def dispatch selector(RoundedRectangle shape,String s,int i)
	'''«shape.tagName».«s.tagClass(i)»'''
	
	
	def dispatch markupCSS(Rectangle shape,String s,int i,String ref)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		«shape.shapeCSS»
	}
	'''
	def dispatch markupCSS(RoundedRectangle shape,String s,int i,String ref)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		rx:«shape.cornerWidth»,
		ry:«shape.cornerHeight»,
		«shape.shapeCSS»
	}
	'''
	
	def getRef(String string) {
		if(string.nullOrEmpty){
			return ""
		}
		'''
		'ref':'«string»',
		'''
	}
	
	def dispatch markupCSS(Text shape,String s,int i,String ref)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		'text':'',
		«shape.shapeCSS»
	}
	'''
	
	def dispatch markupCSS(MultiText shape,String s,int i,String ref)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		'text':'',
		«shape.shapeCSS»
	}
	'''
	
	def dispatch markupCSS(Ellipse shape,String s,int i,String ref)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		rx:«IF shape.size===null»«MGLExtension.DEFAULT_WIDTH/2»«ELSE»«shape.size.width/2»«ENDIF»,
		ry:«IF shape.size===null»«MGLExtension.DEFAULT_WIDTH/2»«ELSE»«shape.size.height/2»«ENDIF»,
		«shape.shapeCSS»
	}
	'''
	
	def dispatch markupCSS(Polyline shape,String s,int i,String ref)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		points: '«FOR p:shape.points SEPARATOR " "»«p.x»,«p.y»«ENDFOR»',
		«shape.shapeCSS»
	}
	'''
	
	def dispatch markupCSS(Polygon shape,String s,int i,String ref, MGLModel modelPackage)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		points: '«FOR p:shape.points SEPARATOR " "»«p.x»,«p.y»«ENDFOR»',
		«shape.shapeCSS»
	}
	'''
	
	def dispatch markupCSS(Image shape,String s,int i,String ref, MGLModel modelPackage)
	'''
	'«shape.tagName».«s.tagClass(i)»':{
		«ref.ref»
		'xlink:href':'img/«modelPackage.name.lowEscapeDart»/«shape.path.fileName»',
		«shape.shapeCSS»
	}
	'''
	
	def fileName(String path) {
		if(path.lastIndexOf("/")<0) {
			return path
		}
		path.substring(path.lastIndexOf("/")+1,path.length)
	}
	
	def shapeCSS(AbstractShape shape)
	'''
	«shape.size.size»
	«shape.appearance»
	«shape.position.position(shape)»
	'''
	
	def double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	
	    var bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	def getVerticalRef(Alignment ap,AbstractShape ash) {
		var res = switch(ap.vertical){
			case MIDDLE: 0.5
			case BOTTOM: 1
			case TOP: 0
			case UNDEFINED:0
		}
//		if(ap.YMargin!=0&&ash.size!=null){
//			res += round(100/ash.size.height*ap.YMargin,2)
//		}

		/* TODO: NOTE: SAMI: Ellipse is pivot in the middle, thats why this
		 * does not seem correct, since the relative start is the upper left corner
		if(ash.eContainer instanceof Ellipse) {
			res += 0.5
		}
		*/

		return '''«res*100»%'''
	}
	
	def getHorizontalRef(Alignment ap,AbstractShape ash) {
		var res = switch(ap.horizontal){
			case CENTER: 0.5
			case RIGHT: 1
			case LEFT: 0
			default: 0
		}
//		if(ap.XMargin!=0&&ash.size!=null){
//			res += round(100/ash.size.height*ap.XMargin,2)
//		}
		return '''«res*100»%'''
	}

	
	def appearance(AbstractShape shape){
		if(shape.referencedAppearance!==null){
			return new PyroAppearance(shape.referencedAppearance).appearanceCSS(shape)+","
		}
		if(shape.inlineAppearance!==null){
			return new PyroAppearance(shape.inlineAppearance).appearanceCSS(shape)+","
		}
		return new PyroAppearance().appearanceCSS(shape)+","
	}
	
	def appearance(EdgeStyle es){
		if(es.referencedAppearance!==null){
			return new PyroAppearance(es.referencedAppearance).appearanceCSS(null)
		}
		if(es.inlineAppearance!==null){
			return new PyroAppearance(es.inlineAppearance).appearanceCSS(null)
		}
		return new PyroAppearance().appearanceCSS(null)
		
	}
	
	def appearance(PredefinedDecorator pd){
		if(pd.referencedAppearance!==null){
			return new PyroAppearance(pd.referencedAppearance).appearanceCSS(null)
		}
		if(pd.inlineAppearance!==null){
			return new PyroAppearance(pd.inlineAppearance).appearanceCSS(null)
		}
		return new PyroAppearance().appearanceCSS(null)
	}
	
	def appearanceCSS(PyroAppearance app,AbstractShape shape){
		'''
		«IF app.angle!=0»transform: 'rotate(«app.angle»)',«ENDIF»
		«IF app.background!==null && (app.filled==BooleanEnum.TRUE ||app.filled==BooleanEnum.UNDEF)»
			fill: «IF shape.isTextual»«app.foreground.color»«ELSE»«app.background.color»«ENDIF»,
		«ENDIF»
		stroke: «IF app.foreground===null || shape.isTextual»'none'«ELSE»«app.foreground.color»«ENDIF»,
		«IF app.lineInVisible»'stroke-opacity':0.0,«ENDIF»
		«IF app.font!==null»«app.font.font»,«ENDIF»
		«IF app.lineStyle!=LineStyle.UNSPECIFIED&&app.lineStyle!=LineStyle.SOLID»«app.lineStyle.lineStyle»,«ENDIF»
		«IF app.transparency>0»
			'fill-opacity':«1.0-app.transparency»,
			'opacity':«1.0-app.transparency»,
			«IF !app.lineInVisible»'stroke-opacity':«1.0-app.transparency»,«ENDIF»
		«ENDIF»
		'stroke-width':«IF shape.isTextual»1«ELSE»«app.lineWidth»«ENDIF»
		'''
	}
	
	def boolean getIsNotScalable(AbstractShape shape){
		if(shape===null){
			return false
		}
		shape instanceof Text || shape instanceof MultiText || shape instanceof Image || shape instanceof Polyline
	}
	
	def boolean getIsTextual(AbstractShape shape){
		if(shape===null){
			return false
		}
		shape instanceof Text || shape instanceof MultiText
	}
	
	def lineStyle(LineStyle ls){
		if(ls==LineStyle.SOLID)return ""
		val r = switch(ls){
			case DASH: "10, 5"
			case DASHDOT:"5, 5, 1, 5"
			case DOT:"1, 5"
			case DASHDOTDOT: "5, 5, 1, 5, 1, 5"
			default: ""
		}
		'''
		'stroke-dasharray':'«r»'
		'''
	}
	
	def font(Font f)
	'''
	'font-family':'«f.fontName»',
	'font-size':'«f.size»px',
	'font-weight':'«IF f.isIsBold»bold«ELSE»normal«ENDIF»',
	'font-style':'«IF f.isIsItalic»italic«ELSE»normal«ENDIF»'
	'''
	
	def color(Color color)
	'''
	'rgb(«color.r»,«color.g»,«color.b»)'
	'''
	
	def size(Size size)
	'''
	«IF size === null || size.heightFixed || size.parentShape === null»
	height: «size.heightOrDefault»,
	«ELSE»
	refHeight: '«100/size.parentShape.size.heightOrDefault*size.heightOrDefault»%',
	«ENDIF»
	«IF size === null || size.widthFixed || size.parentShape === null»
	width: «size.widthOrDefault»,
	«ELSE»
	refWidth: '«100/size.parentShape.size.widthOrDefault*size.widthOrDefault»%',
	«ENDIF»
	'''
	
	def int getHeightOrDefault(Size size) {
		if(size === null) {
			return MGLExtension.DEFAULT_HEIGHT
		}
		return size.height
	}
	
	def int getWidthOrDefault(Size size) {
		if(size === null) {
			return MGLExtension.DEFAULT_WIDTH
		}
		return size.width
	}
	
	def AbstractShape getParentShape(Size size) {
		if(size.eContainer.eContainer instanceof AbstractShape) {
			return size.eContainer.eContainer as AbstractShape
		}
		null
	}
	
	def position(AbstractPosition pos,AbstractShape shape)
	'''
	«IF pos instanceof Alignment»
	«pos.horizontal.anchor»
	«pos.vertical.anchor(shape)»
	«IF pos.XMargin!=0»
	'refX2':«pos.XMargin»,
	«ENDIF»
	«IF pos.YMargin!=0»
	'refY2':«pos.YMargin»,
	«ENDIF»
	'ref-x': "«pos.getHorizontalRef(shape)»",
	'ref-y': "«pos.getVerticalRef(shape)»"
	«ENDIF»
	«IF pos instanceof AbsolutPosition»
	'x':«pos.XPos»,
	'y':«pos.YPos»
	«ENDIF»
	'''
	
	def anchor(HAlignment position) {
		if(position==HAlignment.UNDEFINED)return ""
		var r = switch(position) {
			case CENTER: "middle"
			case LEFT: "left"
			case RIGHT: "right"
			default: "middle"
		}
		'''
		'text-anchor': '«r»',
		'x-alignment': '«r»',
		'''
	}
	
	def anchor(VAlignment position,AbstractShape shape){
		if(position==VAlignment.UNDEFINED)return ""
		val r = switch(position){
			case TOP: "0em"
			case MIDDLE: "-0.5em"
			case BOTTOM: "-1em"
			default: "0em"
		}
		var a = switch(position) {
			case MIDDLE: "middle"
			case BOTTOM: "bottom"
			case TOP: "top"
			default: "middle"
		}
		'''
		«IF !(shape instanceof Text || shape instanceof MultiText ||shape instanceof Image)»
		dy:'«r»',
		«ELSE»
		'y-alignment': '«a»',
		«ENDIF»
		'''
		
	}
	
	
	
	def getMarkup(List<AbstractShape> shapes,String prefix){
		var res = ""
		for(var i = 0;i<shapes.length;i++){
			res += shapes.get(i).markup(prefix,i)
		}
	}
	
	def getMarkup(ConnectionDecorator cd){
		cd.decoratorShape.markup("",0)
	}
	
	def dispatch getMarkupCSS(NodeStyle style){
		style.mainShape.markupCSS("",0,"")
	}
	
	def dispatch getMarkupCSS(ConnectionDecorator style){
		style.decoratorShape.markupCSS("",0,"")
	}
	
	def createEdge(Edge e,Styles styles){
		val modelPackage = e.modelPackage
		val styleForEdge = e.styling(styles) as EdgeStyle
		'''
		«e.shapeFQN» = joint.dia.Link.extend({
				markup: '<path class="connection"/>'+
				'<path class="marker-source"/>'+
				'<path class="marker-target"/>'+
				'<path class="connection-wrap"/>'+
				'<g class="labels"></g>'+
				'<g class="marker-vertices"/>'+
				'<g class="marker-arrowheads"/>'+
				'<g class="link-tools"«IF !e.removable» pyro-remove-disbaled=""«ENDIF» />',
			    defaults: {
			        type: '«modelPackage.name.lowEscapeDart».«e.name.fuEscapeDart»',
			        attrs: {
			        	«e.disableFeatures»
			            '.connection': {
			                «styleForEdge.appearance»
			            },
			            '.marker-target': { 
			            	«IF styleForEdge.targetMarker !== null»
			            	«styleForEdge.targetMarker.markerCSS»
			            	«ELSE»
			            	fill: '#000', stroke: '#000'
			            	«ENDIF»
			            },
			            '.marker-source': { 
			            	«IF styleForEdge.sourceMarker !== null»
			            	«styleForEdge.sourceMarker.markerCSS»
			            	«ELSE»
			            	fill: '#000', stroke: '#000'
			            	«ENDIF»
			            }
			        },
			        labels:[
			        	«styleForEdge.decorator.decoratorCSS»
			        ]
			    }
			});
		'''
	}
	
	def getTargetMarker(EdgeStyle style){
		style.decorator.findFirst[n|n.location==1.0]
	}
	
	def getSourceMarker(EdgeStyle style){
		style.decorator.findFirst[n|n.location==0.0]
	}
	
	def decoratorMarkups(List<ConnectionDecorator> cds){
		var result = ""
		for(var i = 0;i<cds.length;i++){
			result += cds.get(i).decoratorMarkup("x",i)+"\n"
		}
		result
	}
	
	def decoratorCSS(List<ConnectionDecorator> cds)
	'''
	«FOR cd:cds.filter[n|n.decoratorShape instanceof Text ||n.decoratorShape instanceof MultiText].indexed SEPARATOR ","»
	{
		position: «cd.value.location»,
		markup:'<text class="pyro«cd.key»link"/>',
	    attrs: {
			'text.pyro«cd.key»link': {
				«{
					val shape = cd.value.decoratorShape as Shape
					'''
					text:'«shape.getText»',
					«shape.shapeCSS»
					'''
				}»
			}
	     }
	}
	«ENDFOR»
	'''
	
	def String getText(Shape shape){
		if(shape instanceof Text){
			return shape.value
		}
		if(shape instanceof MultiText){
			return shape.value
		}
		return ""
	}
	
//	def decoratorMarkupsCSS(List<ConnectionDecorator> cds){
//		var result = ""
//		for(var i = 0;i<cds.length;i++){
//			result += cds.get(i).decoratorCSS("x",i)+"\n"
//		}
//		result
//	}
	
	def decoratorMarkup(ConnectionDecorator cd,String s,int i)
	'''
	<g class="label positional-«s»«i»">
	«IF cd.predefinedDecorator !== null»
	<path class="predefined-«s»«i»"/>
	«ELSE»
	«cd.decoratorShape.collectMarkupTags(s,i).entrySet.map[value].join»
	«ENDIF»
	</g>
	'''
		
		
	def markerCSS(ConnectionDecorator cd)
	'''
	«IF cd.predefinedDecorator !== null»
		d: '«cd.predefinedDecorator.shape.polyline»',
		«cd.predefinedDecorator.appearance»,
		markerWidth:4,
		markerHeight:4
	«ENDIF»
	'''
	
//	def decoratorCSS(ConnectionDecorator cd,String s,int i)
//	'''
//	'.positional-«s»«i»':{
//		position: «cd.location»
//	},
//	«IF cd.predefinedDecorator!=null»
//	'.predefined-«s»«i»' : {
//		d: '«cd.predefinedDecorator.shape.polyline»',
//		«cd.predefinedDecorator.appearance»,
//		markerWidth:4,
//		markerHeight:4,
//	},
//	«ELSE»
//	«cd.decoratorShape.collectMarkupCSSTags(s,i,'''positional-«s»«i»''').entrySet.map[value].join(",")»
//	«ENDIF»
//
//	'''
	
	
	def polyline(DecoratorShapes ds){
		switch(ds){
			case ARROW:return "M 0,0 L 5,-5 M 0,0 L 5,5"
			case CIRCLE:return "M 100, 100 m -75, 0 a 75,75 0 1,0 150,0 a 75,75 0 1,0 -150,0"
			case DIAMOND:return "M 50 0 100 100 50 200 0 100 Z"
			case TRIANGLE:return "M 26 0 L 0 13 L 26 26 z"
		}
	}
	
	
	def contentShapes(Styles styles, MGLModel modelPackage)
	'''
	«modelPackage.shapeFQN» = {};
	
	«modelPackage.nodesTopologically.filter[!isIsAbstract].map[createNode(styles)].join("\n")»
	
	«modelPackage.edgesTopologically.filter[!isIsAbstract].map[createEdge(styles)].join("\n")»
	
	'''
}
