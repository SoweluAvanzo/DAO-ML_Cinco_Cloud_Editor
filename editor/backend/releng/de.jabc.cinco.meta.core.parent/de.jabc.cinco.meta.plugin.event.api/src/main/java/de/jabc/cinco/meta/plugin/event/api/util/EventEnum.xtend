// Unit tests: de.jabc.cinco.meta.plugin.event.api.test.EventEnumTest

package de.jabc.cinco.meta.plugin.event.api.util

import de.jabc.cinco.meta.core.event.hub.EventHub
import de.jabc.cinco.meta.core.event.hub.impl.PayloadContext
import de.jabc.cinco.meta.plugin.event.api.event.EdgeEvent
import de.jabc.cinco.meta.plugin.event.api.event.GraphModelEvent
import de.jabc.cinco.meta.plugin.event.api.event.IdentifiableElementEvent
import de.jabc.cinco.meta.plugin.event.api.event.ModelElementEvent
import de.jabc.cinco.meta.plugin.event.api.event.NodeEvent
import de.jabc.cinco.meta.plugin.event.api.payload.PostAttributeChangePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostCreatePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostDeletePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostDoubleClickPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostMovePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostReconnectPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostResizePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostSavePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostSelectPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreAttributeChangePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreCreateEdgePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreCreateGraphModelPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreCreateNodePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreDeletePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreDoubleClickPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreMovePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreReconnectPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreResizePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreSavePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreSelectPayload
import graphmodel.IdentifiableElement
import java.lang.reflect.Type
import java.util.List
import mgl.ModelElement
import org.eclipse.xtend.lib.annotations.Accessors

import static de.jabc.cinco.meta.core.event.hub.impl.CompositeContext.WILDCARD
import static de.jabc.cinco.meta.plugin.event.api.util.EventEnum.JavaDialect.*

@Accessors
class EventEnum {
	
	/*** Extensions ***/
	
	extension EventApiExtension = new EventApiExtension
	
	
	
	/*** Static fields ***/
	
	val public static String ELEMENT_TYPE_PARAMETER = 'Element'
	
	val public static String EVENT                  = 'event'
	
	val public static String PRE                    = 'pre'
	val public static String POST                   = 'post'
	
	val public static String ATTRIBUTE_CHANGE       = 'attributeChange'
	val public static String CREATE                 = 'create'
	val public static String DELETE                 = 'delete'
	val public static String DOUBLE_CLICK           = 'doubleClick'
	val public static String MOVE                   = 'move'
	val public static String RECONNECT              = 'reconnect'
	val public static String RESIZE                 = 'resize'
	val public static String SAVE                   = 'save'
	val public static String SELECT                 = 'select'
	
	val public static List<EventEnum> EVENTS = newArrayList
	
	val public static EventEnum PRE_ATTRIBUTE_CHANGE   = new EventEnum( PRE, ATTRIBUTE_CHANGE, IdentifiableElementEvent,  PreAttributeChangePayload, false)
	val public static EventEnum PRE_CREATE_GRAPH_MODEL = new EventEnum( PRE,           CREATE,          GraphModelEvent, PreCreateGraphModelPayload)
	val public static EventEnum PRE_CREATE_NODE        = new EventEnum( PRE,           CREATE,                NodeEvent,       PreCreateNodePayload)
	val public static EventEnum PRE_CREATE_EDGE        = new EventEnum( PRE,           CREATE,                EdgeEvent,       PreCreateEdgePayload)
	val public static EventEnum PRE_DELETE             = new EventEnum( PRE,           DELETE,        ModelElementEvent,           PreDeletePayload)
	val public static EventEnum PRE_DOUBLE_CLICK       = new EventEnum( PRE,     DOUBLE_CLICK,        ModelElementEvent,      PreDoubleClickPayload, false)
	val public static EventEnum PRE_MOVE               = new EventEnum( PRE,             MOVE,                NodeEvent,             PreMovePayload)
	val public static EventEnum PRE_RECONNECT          = new EventEnum( PRE,        RECONNECT,                EdgeEvent,        PreReconnectPayload)
	val public static EventEnum PRE_RESIZE             = new EventEnum( PRE,           RESIZE,                NodeEvent,           PreResizePayload)
	val public static EventEnum PRE_SAVE               = new EventEnum( PRE,             SAVE,          GraphModelEvent,             PreSavePayload)
	val public static EventEnum PRE_SELECT             = new EventEnum( PRE,           SELECT,        ModelElementEvent,           PreSelectPayload, false)
	
	val public static EventEnum POST_ATTRIBUTE_CHANGE  = new EventEnum(POST, ATTRIBUTE_CHANGE, IdentifiableElementEvent, PostAttributeChangePayload)
	val public static EventEnum POST_CREATE            = new EventEnum(POST,           CREATE, IdentifiableElementEvent,          PostCreatePayload)
	val public static EventEnum POST_DELETE            = new EventEnum(POST,           DELETE,        ModelElementEvent,          PostDeletePayload)
	val public static EventEnum POST_DOUBLE_CLICK      = new EventEnum(POST,     DOUBLE_CLICK,        ModelElementEvent,     PostDoubleClickPayload)
	val public static EventEnum POST_MOVE              = new EventEnum(POST,             MOVE,                NodeEvent,            PostMovePayload)
	val public static EventEnum POST_RECONNECT         = new EventEnum(POST,        RECONNECT,                EdgeEvent,       PostReconnectPayload)
	val public static EventEnum POST_RESIZE            = new EventEnum(POST,           RESIZE,                NodeEvent,          PostResizePayload)
	val public static EventEnum POST_SAVE              = new EventEnum(POST,             SAVE,          GraphModelEvent,            PostSavePayload)
	val public static EventEnum POST_SELECT            = new EventEnum(POST,           SELECT,        ModelElementEvent,          PostSelectPayload)
	
	
	
	/*** Attributes ***/
	
	val String prefix
	val String suffix
	val Class<?> eventClass
	val Class<?> payloadClass
	val boolean implemented
	
	
	
	/*** Constructor ***/
	
	private new (String prefix, String suffix, Class<?> eventClass, Class<?> payloadClass) {
		this.prefix       = prefix
		this.suffix       = suffix
		this.eventClass   = eventClass
		this.payloadClass = payloadClass
		this.implemented  = true
		EVENTS.add(this)
	}
	
	private new (String prefix, String suffix, Class<?> eventClass, Class<?> payloadClass, boolean implemented) {
		this.prefix       = prefix
		this.suffix       = suffix
		this.eventClass   = eventClass
		this.payloadClass = payloadClass
		this.implemented  = implemented
		EVENTS.add(this)
	}
	
	
	
	/*** Derived values ***/
	
	// Event methods
	
	def String getMethodName() {
		'''«prefix»«suffix.toFirstUpper»'''
	}
	
	def String getMethodParameterNames() {
		payloadClass.declaredFields.join(', ') [ name ]
	}
	
	def String getMethodParameterDeclarations((Fqn) => String fqnShortener) {
		getMethodParameterDeclarations(elementTypeFqn, fqnShortener)
	}
	
	def String getMethodParameterDeclarations(Fqn elementType, (Fqn) => String fqnShortener) {
		payloadClass.declaredFields.join(', ') [
			val type = new Fqn(genericType)
			type.replaceAll(ELEMENT_TYPE_PARAMETER, elementType.fullyQualifiedName)
			'''«fqnShortener.apply(type)» «name»'''
		]
	}
	
	def Class<?> getMethodReturnType() {
		eventClass.methods.findFirst[ name == methodName ].returnType
	}
	
	def Fqn getMethodReturnTypeFqn() {
		getMethodReturnType.toFqn
	}
	
	def boolean isMethodReturnTypeVoid() {
		methodReturnType === void || methodReturnType === Void
	}
	
	// Event context identifier
	
	def String getContextIdentifierPrefix() {
		'''«EVENT».«prefix».«suffix»'''
	}
	
	def String getContextIdentifier() {
		'''«contextIdentifierPrefix».«WILDCARD»'''
	}
	
	def String getContextIdentifier(ModelElement element) {
		'''«contextIdentifierPrefix».«element.elementFqn.fullyQualifiedName.replaceAll('''\.''', '_')»'''
	}
	
	// Element type
	
	def Type getElementType() {
		eventClass.typeParameters.head.bounds.head
	}
	
	def Fqn getElementTypeFqn() {
		elementType.toFqn
	}
	
	// EventHub notify call
	
	def String getNotifyCall(JavaDialect dialect, ModelElement element, String ... payloadParameters) {
		
		if (!element.isEventEnabled || !accepts(element)) {
			return ''
		}
			
		val hubFqn     = EventHub.name
		val elementFqn = element.elementFqn.fullyQualifiedName
		val payloadFqn = '''«payloadClass.name»<«elementFqn»>'''
		val resultFqn  = if (isMethodReturnTypeVoid) Void.name else methodReturnTypeFqn.fullyQualifiedNameWithGenerics
		val contextFqn = '''«PayloadContext.name»<«payloadFqn», «resultFqn»>'''
		
		switch (dialect) {
			
			case JAVA:
				return '''
					// «element.contextIdentifier»
					«payloadFqn» eventPayload = new «payloadFqn»(«if (!payloadParameters.nullOrEmpty) payloadParameters.join(', ')»);
					«contextFqn» eventContext = new «contextFqn»("«element.contextIdentifier»", eventPayload);
					«hubFqn».getInstance().notifyFirst(eventContext);
					«IF !isMethodReturnTypeVoid»
						«resultFqn» eventResult = eventContext.getFirstResult();
					«ENDIF»
				'''
				
			case XTEND:
				return '''
					// «element.contextIdentifier»
					val eventPayload = new «payloadFqn»(«if (!payloadParameters.nullOrEmpty) payloadParameters.join(', ')»)
					val eventContext = new «contextFqn»('«element.contextIdentifier»', eventPayload)
					«hubFqn».instance.notifyFirst(eventContext)
					«IF !isMethodReturnTypeVoid»
						val eventResult = eventContext.firstResult
					«ENDIF»
				'''
				
			default:
				throw new IllegalArgumentException
			
		}
		
	}
	
	def getNotifyCallJava(ModelElement element, String ... payloadParameters) {
		getNotifyCall(JAVA, element, payloadParameters)
	}
	
	def getNotifyCallXtend(ModelElement element, String ... payloadParameters) {
		getNotifyCall(XTEND, element, payloadParameters)
	}
	
	
	
	/*** Methods ***/
	
	def boolean accepts(Class<?> elementClass) {
		elementClass.graphmodelClass.implementsOrExtends(elementType)
	}
	
	def boolean accepts(IdentifiableElement element) {
		element.graphmodelClass.implementsOrExtends(elementType)
	}
	
	def boolean accepts(ModelElement element) {
		element.graphmodelClass.implementsOrExtends(elementType)
	}
	
	
	
	/*** Inner classes ***/
	
	/**
	 * Enum of Java dialects:
	 * <ul>
	 * <li>{@code JAVA}</li>
	 * <li>{@code XTEND}</li>
	 * </ul>
	 */
	static enum JavaDialect {
		JAVA,
		XTEND
	}
	
}
