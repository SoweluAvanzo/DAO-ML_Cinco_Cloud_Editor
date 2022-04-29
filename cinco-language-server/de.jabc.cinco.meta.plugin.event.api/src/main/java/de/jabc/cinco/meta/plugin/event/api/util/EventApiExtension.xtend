package de.jabc.cinco.meta.plugin.event.api.util

import de.jabc.cinco.meta.core.event.util.EventCoreExtension
import de.jabc.cinco.meta.plugin.event.api.Activator
import de.jabc.cinco.meta.plugin.event.api.event.ContainerEvent
import de.jabc.cinco.meta.plugin.event.api.event.EdgeEvent
import de.jabc.cinco.meta.plugin.event.api.event.Event
import de.jabc.cinco.meta.plugin.event.api.event.GraphModelEvent
import de.jabc.cinco.meta.plugin.event.api.event.NodeEvent
import de.jabc.cinco.meta.plugin.event.api.payload.Payload
import graphmodel.Container
import graphmodel.IdentifiableElement
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.regex.Pattern
import mgl.Annotation
import mgl.Edge
import mgl.GraphModel
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.UserDefinedType

/**
 * @author Fabian Storek
 */
class EventApiExtension extends EventCoreExtension {
	
	val public static String EVENT_ANNOTATION_NAME      = 'event'
	val public static String EVENT_API_PACKAGE_SUFFIX   = 'event'
	val public static String PAYLOAD_API_PACKAGE_SUFFIX = 'payload'
	val static Pattern MGL_FILE_NAME_PATTERN            = Pattern.compile('''^.*?(?<fileName>[\w\-. ]+)\.mgl$''')
	
	
	
	/*** Plugin IDs ***/
	
	def String getEventApiPluginID() {
		Activator.PLUGIN_ID
	}
	
	
	
	/*** Annotation ***/
	
	def String getEventAnnotationName() {
		EVENT_ANNOTATION_NAME
	}
	
	def boolean isEventAnnotation(Annotation annotation) {
		annotation.name == EVENT_ANNOTATION_NAME && !annotation.value.nullOrEmpty
	}
	
	def boolean hasEventAnnotation(ModelElement element) {
		element.annotations.exists[isEventAnnotation]
	}
	
	def Annotation getEventAnnotation(ModelElement element) {
		element.annotations.findFirst[isEventAnnotation]
	}
	
	def boolean isEventEnabled(ModelElement element) {
		if (element.hasEventAnnotation) {
			return true
		}
		val superElement = element.superElement
		if (superElement === null) {
			return false
		}
		return superElement.isEventEnabled
	}
	
	
	
	/*** Payload API ***/
	
	def String getPayloadApiProjectName() {
		eventApiPluginID
	}
		
	def Class<?> getPayloadApiClass() {
		Payload
	}
	
	def Fqn getPayloadApiFqn() {
		payloadApiClass.toFqn
	}
	
	
	
	/*** Event API ***/
	
	def String getEventApiProjectName() {
		eventApiPluginID
	}
	
	def Class<?> getEventApiClass() {
		Event
	}
	
	def Class<?> getEventApiClass(IdentifiableElement element) {
		switch element {
			graphmodel.GraphModel: GraphModelEvent
			Container:  ContainerEvent
			graphmodel.Node:       NodeEvent
			graphmodel.Edge:       EdgeEvent
			default:               throw new IllegalArgumentException
		}
	}
	
	def Class<?> getEventApiClass(ModelElement element) {
		switch element {
			GraphModel:    GraphModelEvent
			NodeContainer: ContainerEvent
			Node:          NodeEvent
			Edge:          EdgeEvent
			default:       throw new IllegalArgumentException
		}
	}
	
	def Fqn getEventApiFqn() {
		eventApiClass.toFqn
	}
	
	def Fqn getEventApiFqn(IdentifiableElement element) {
		element.eventApiClass.toFqn
	}
	
	def Fqn getEventApiFqn(ModelElement element) {
		element.eventApiClass.toFqn
	}
	
	
	
	/*** Model element ***/
	
	def ModelElement getSuperElement(ModelElement element) {
		switch it: element {
			GraphModel:   	 extends
			NodeContainer: 	 extends
			Node:          	 extends
			Edge:          	 extends
			UserDefinedType: extends
			default:       throw new IllegalArgumentException
		}
	}
	
	
	
	/*** EventEnum ***/
	
	def Iterable<EventEnum> getEvents() {
		EventEnum.EVENTS
	}
	
	def Iterable<EventEnum> getEvents(ModelElement element) {
		events.filter [ accepts(element) ]
	}
	
	def Iterable<EventEnum> getEvents(IdentifiableElement element) {
		events.filter [ accepts(element) ]
	}
	
	
	
	/*** General ***/
	
	def Fqn toFqn(CharSequence fqn) {
		new Fqn(fqn)
	}
	
	def Fqn toFqn(Type type) {
		new Fqn(type)
	}
	
	def Class<?> getGraphmodelClass(Class<?> mglClass) {
		switch it: mglClass {
			case implementsOrExtends(GraphModel):            graphmodel.GraphModel
			case implementsOrExtends(NodeContainer):         Container
			case implementsOrExtends(Node):                  graphmodel.Node
			case implementsOrExtends(Edge):                  graphmodel.Edge
			case implementsOrExtends(graphmodel.GraphModel): graphmodel.GraphModel
			case implementsOrExtends(Container):  Container
			case implementsOrExtends(graphmodel.Node):       graphmodel.Node
			case implementsOrExtends(graphmodel.Edge):       graphmodel.Edge
			default: throw new IllegalArgumentException
		}
	}
	
	def Class<?> getGraphmodelClass(IdentifiableElement element) {
		element.class.graphmodelClass
	}
	
	def Class<?> getGraphmodelClass(ModelElement element) {
		element.class.graphmodelClass
	}
	
	def Class<?> getMglClass(Class<?> graphmodelClass) {
		switch it: graphmodelClass {
			case implementsOrExtends(graphmodel.GraphModel): GraphModel
			case implementsOrExtends(Container):  NodeContainer
			case implementsOrExtends(graphmodel.Node):       Node
			case implementsOrExtends(graphmodel.Edge):       Edge
			case implementsOrExtends(GraphModel):            GraphModel
			case implementsOrExtends(NodeContainer):         NodeContainer
			case implementsOrExtends(Node):                  Node
			case implementsOrExtends(Edge):                  Edge
			default: throw new IllegalArgumentException
		}
	}
	
	def Class<?> getMglClass(IdentifiableElement element) {
		element.class.mglClass
	}
	
	def Class<?> getMglClass(ModelElement element) {
		element.class.mglClass
	}
	
	def boolean implementsOrExtends(Class<?> classA, Type classB) {
		if (classA == classB) {
			return true
		}
		val interfaces = classA.interfaces
		if (!interfaces.nullOrEmpty && interfaces.exists[implementsOrExtends(classB)]) {
			return true
		}
		val superclass = classA.superclass
		if (superclass !== null && superclass.implementsOrExtends(classB)) {
			return true
		}
		return false
	}
	
	
	
	/*** MGL model ***/
	
	def String getModelPackageName(MGLModel model) {
		'''«model.package».«model.modelFileName.toLowerCase»'''
	}
	
	def String getModelFileName(MGLModel model) {
		val modelResourceUri = model.eResource.URI
		val platformString = modelResourceUri.toPlatformString(true)
		val uriString = platformString?: modelResourceUri.toFileString
		val matcher = MGL_FILE_NAME_PATTERN.matcher(uriString)
		if (matcher.matches) {
			return matcher.group('fileName')
		}
		else {
			throw new IllegalStateException('''The name of the MGL model "«model.package»" could not be resolved properly.''')
		}
	}
	
	def MGLModel getModel(ModelElement element) {
		switch it: element {
			GraphModel:    eContainer as MGLModel
			NodeContainer: eContainer as MGLModel
			Node:          eContainer as MGLModel
			Edge:          eContainer as MGLModel
			default:       throw new RuntimeException('''Can not determine MGLModel for «it»''')
		}
	}
	
	
	
	/*** Model element ***/
	
	def String getElementPackageName(ModelElement element) {
		element.model.modelPackageName
	}
	
	def String getElementClassName(ModelElement element) {
		element.name.toFirstUpper
	}
	
	def Fqn getElementFqn(ModelElement element) {
		'''«element.elementPackageName».«element.elementClassName»'''.toFqn
	}
	
	def Iterable<ModelElement> getEventEnabledElements(MGLModel model) {
		(
			model.nodes +
			model.edges +
			model.graphModels
		)
		.filter [ isEventEnabled ]
	}
	
	def Iterable<ModelElement> getEventEnabledElements(GraphModel graphModel) {
		val mglModel = getModel(graphModel)
		(
			mglModel.nodes +
			mglModel.edges
		)
		.filter [ isEventEnabled ]
		.map [ it as ModelElement ]
	}
	
	def Iterable<ModelElement> getEventEnabledElements(MGLModel model, EventEnum event) {
		model.eventEnabledElements.filter [ element | event.accepts(element) ]
	}
	
	def Iterable<ModelElement> getEventEnabledElements(GraphModel graphModel, EventEnum event) {
		graphModel.eventEnabledElements.filter [ element | event.accepts(element) ]
	}
	
	def sortByInheritance(Iterable<ModelElement> elements) {
		val remaining = elements.toList
		val sorted = new ArrayList<ModelElement>(remaining.size)
		while (!remaining.empty) {
			val current = remaining.remove(0)
			val superElement = current.superElement
			if (superElement === null || !remaining.contains(superElement)) {
				sorted.add(current)
			}
			else {
				remaining.add(current)
			}
		}
		return sorted.reverse
	}
	
	def <T> CharSequence ifElseCascade(Iterable<T> it, (T) => CharSequence condition, (T) => CharSequence elseIfBody, () => CharSequence elseBody) {
		if (nullOrEmpty) {
			return ''
		}
		val elseBodyResult = elseBody.apply?.toString
		val elseBlock = if (elseBodyResult.nullOrEmpty) {
			null
		}
		else {
			'''
				else {
					«elseBodyResult»
				}
			'''
		}
		return join(
			'if ',
			'else if ',
			elseBlock,
			[ obj |
				'''
					(«condition.apply(obj)») {
						«elseIfBody.apply(obj)»
					}
				'''
			]
		)
	}
	
	def <T> CharSequence ifElseCascade(Iterable<T> it, (T) => CharSequence condition, (T) => CharSequence elseIfBody) {
		ifElseCascade(condition, elseIfBody, [])
	}
	
}
