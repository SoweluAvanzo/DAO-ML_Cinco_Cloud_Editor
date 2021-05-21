package de.jabc.cinco.meta.core.event.util

import de.jabc.cinco.meta.core.event.Activator
import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.hub.EventHub
import de.jabc.cinco.meta.core.event.hub.Subscriber
import de.jabc.cinco.meta.core.event.hub.impl.CompositeContext
import de.jabc.cinco.meta.core.event.hub.impl.PayloadContext
import de.jabc.cinco.meta.core.event.hub.impl.PayloadSubscriber
import de.jabc.cinco.meta.core.event.hub.impl.SimpleSubscriber
import de.jabc.cinco.meta.core.event.util.PairwiseIterable.Fill
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * An extension, that provides convenience methods for the classes of the
 * {@link EventHub}.
 * 
 * @author Fabian Storek
 */
@Accessors
class EventCoreExtension {
	
	/**
	 * Dis- or enables logging.
	 * 
	 * @see EventCoreExtension#log(Object, CharSequence) log(Object, CharSequence)
	 * @see EventCoreExtension#err(Object, CharSequence) err(Object, CharSequence)
	 */
	var boolean logging = true
	
	
	
	/*** Plugin ***/
	
	/**
	 * Returns the ID of the Event Core Plug-in. 
	 */
	def getEventCorePluginID() {
		Activator.PLUGIN_ID
	}
	
	
	
	/*** EventHub ***/
	
	/**
	 * Convenience method for {@link EventHub#notify(Context)
	 * EventHub.notify(Context)}.
	 * <p>
	 * It implies the use of the current {@linkplain EventHub#getInstance()
	 * instance} of the {@link EventHub}.
	 * 
	 * @see EventHub#notify(Context) EventHub.notify(Context)
	 * @see EventCoreExtension#notify(String) notify(String)
	 * @see EventCoreExtension#notify(String, P) notify(String, P)
	 */
	def notify(Context context) {
		EventHub.instance.notify(context)
	}
	
	/**
	 * Convenience method for {@link EventHub#notify(Context)
	 * EventHub.notify(Context)}.
	 * <p>
	 * It implies the use of the current {@linkplain EventHub#getInstance()
	 * instance} of the {@link EventHub}. Also, it allows to provide a string
	 * instead of a {@link Context} instance. The string will be used to create
	 * a {@link CompositeContext}.
	 * 
	 * @see EventHub#notify(Context) EventHub.notify(Context)
	 * @see EventCoreExtension#notify(Context) notify(Context)
	 * @see EventCoreExtension#notify(String, P) notify(String, P)
	 */
	def notify(String identifier) {
		EventHub.instance.notify(new CompositeContext(identifier))
	}
	
	/**
	 * Convenience method for {@link EventHub#notify(Context)
	 * EventHub.notify(Context)}.
	 * <p>
	 * It implies the use of the current {@linkplain EventHub#getInstance()
	 * instance} of the {@link EventHub}. Also, it allows to provide a string
	 * instead of a {@link Context} instance. The string will be used to create
	 * a {@link PayloadContext}.
	 * 
	 * @see EventHub#notify(Context) EventHub.notify(Context)
	 * @see EventCoreExtension#notify(Context) notify(Context)
	 * @see EventCoreExtension#notify(String) notify(String)
	 */
	def <P> notify(String identifier, P payload) {
		EventHub.instance.notify(new PayloadContext(identifier, payload))
	}
	
	/**
	 * Convenience method for {@link EventHub#notifyFirst(Context)
	 * EventHub.notifyFirst(Context)}.
	 * <p>
	 * It implies the use of the current {@linkplain EventHub#getInstance()
	 * instance} of the {@link EventHub}.
	 * 
	 * @see EventHub#notifyFirst(Context) EventHub.notifyFirst(Context)
	 * @see EventCoreExtension#notifyFirst(String) notifyFirst(String)
	 * @see EventCoreExtension#notifyFirst(String, P) notifyFirst(String, P)
	 */
	def notifyFirst(Context context) {
		EventHub.instance.notifyFirst(context)
	}
	
	/**
	 * Convenience method for {@link EventHub#notifyFirst(Context)
	 * EventHub.notifyFirst(Context)}.
	 * <p>
	 * It implies the use of the current {@linkplain EventHub#getInstance()
	 * instance} of the {@link EventHub}. Also, it allows to provide a string
	 * instead of a {@link Context} instance. The string will be used to create
	 * a {@link CompositeContext}.
	 * 
	 * @see EventHub#notify(Context) EventHub.notifyFirst(Context)
	 * @see EventCoreExtension#notify(Context) notifyFirst(Context)
	 * @see EventCoreExtension#notify(String, P) notifyFirst(String, P)
	 */
	def notifyFirst(String identifier) {
		EventHub.instance.notifyFirst(new CompositeContext(identifier))
	}
	
	/**
	 * Convenience method for {@link EventHub#notifyFirst(Context)
	 * EventHub.notifyFirst(Context)}.
	 * <p>
	 * It implies the use of the current {@linkplain EventHub#getInstance()
	 * instance} of the {@link EventHub}. Also, it allows to provide a string
	 * instead of a {@link Context} instance. The string will be used to create
	 * a {@link PayloadContext}.
	 * 
	 * @see EventHub#notify(Context) EventHub.notifyFirst(Context)
	 * @see EventCoreExtension#notify(Context) notifyFirst(Context)
	 * @see EventCoreExtension#notify(String) notifyFirst(String)
	 */
	def <P> notifyFirst(String identifier, P payload) {
		EventHub.instance.notifyFirst(new PayloadContext(identifier, payload))
	}
	
	
	
	/*** Context ***/
	
	/**
	 * Syntactic sugar for {@link Context#matches(Context) matches(Context)}.
	 * 
	 * @see Context#matches(Context) matches(Context)
	 */
	def <=> (Context left, Context right) {
		left.matches(right)
	}

	
	
	
	/*** SimpleSubscriber ***/
	
	/**
	 * Creates a new {@link SimpleSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processContext The procedure, that will be run on the received
	 *                       Context. May not be {@code null}.
	 * 
	 * @return the newly created SimpleSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(Context, int, Procedure1)
	 *      SimpleSubscriber(Context, int, (C) => void)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <C extends Context> subscribeSimpleSubscriber(Context context, int priority, (C) => void processContext) {
		val sub = new SimpleSubscriber(context, priority, processContext)
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link SimpleSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param processContext The procedure, that will be run on the received
	 *                       Context. May not be {@code null}.
	 * 
	 * @return the newly created SimpleSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(Context, Procedure1)
	 *      SimpleSubscriber(Context, (C) => void)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <C extends Context> subscribeSimpleSubscriber(Context context, (C) => void processContext) {
		val sub = new SimpleSubscriber(context, processContext)
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link SimpleSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       SimpleSubscriber can be notified.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processContext The procedure, that will be run on the received
	 *                       Context. May not be {@code null}.
	 * 
	 * @return the newly created SimpleSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(String, int, Procedure1)
	 *      SimpleSubscriber(String, int, (C) => void)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <C extends Context> subscribeSimpleSubscriber(String identifier, int priority, (C) => void processContext) {
		val sub = new SimpleSubscriber(identifier, priority, processContext)
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link SimpleSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       SimpleSubscriber can be notified.
	 * @param processContext The procedure, that will be run on the received
	 *                       Context. May not be {@code null}.
	 * 
	 * @return the newly created SimpleSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(String, Procedure1)
	 *      SimpleSubscriber(String, (C) => void)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <C extends Context> subscribeSimpleSubscriber(String identifier, (C) => void processContext) {
		val sub = new SimpleSubscriber(identifier, processContext)
		if (sub.subscribe) sub else null
	}
	
	
	
	/*** PayloadSubscriber<P, Void> ***/
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processPayload The procedure, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, int, Function1)
	 *      PayloadSubscriber(CompositeContext, int, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P> subscribePayloadSubscriber(CompositeContext context, int priority, (P) => void processPayload) {
		val sub = new PayloadSubscriber<P, Void>(context, priority) [ P payload |
			processPayload.apply(payload)
			return null
		]
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param processPayload The procedure, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, Function1)
	 *      PayloadSubscriber(CompositeContext, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P> subscribePayloadSubscriber(CompositeContext context, (P) => void processPayload) {
		val sub = new PayloadSubscriber<P, Void>(context) [ P payload |
			processPayload.apply(payload)
			return null
		]
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       PayloadSubscriber can be notified.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processPayload The procedure, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(String, int, Function1)
	 *      PayloadSubscriber(String, int, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P> subscribePayloadSubscriber(String identifier, int priority, (P) => void processPayload) {
		val sub = new PayloadSubscriber<P, Void>(identifier, priority) [ P payload |
			processPayload.apply(payload)
			return null
		]
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       PayloadSubscriber can be notified.
	 * @param processPayload The procedure, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(String, Function1)
	 *      PayloadSubscriber(String, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P> subscribePayloadSubscriber(String identifier, (P) => void processPayload) {
		val sub = new PayloadSubscriber<P, Void>(identifier) [ P payload |
			processPayload.apply(payload)
			return null
		]
		if (sub.subscribe) sub else null
	}
	
	
	
	/*** PayloadResultSubscriber<P, R> ***/
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, int, Function1)
	 *      PayloadSubscriber(CompositeContext, int, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P ,R> subscribePayloadSubscriber(CompositeContext context, int priority, (P) => R processPayload) {
		val sub = new PayloadSubscriber(context, priority, processPayload)
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, Function1)
	 *      PayloadSubscriber(CompositeContext, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P ,R> subscribePayloadSubscriber(CompositeContext context, (P) => R processPayload) {
		val sub = new PayloadSubscriber(context, processPayload)
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       PayloadSubscriber can be notified.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(String, int, Function1)
	 *      PayloadSubscriber(String, int, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P ,R> subscribePayloadSubscriber(String identifier, int priority, (P) => R processPayload) {
		val sub = new PayloadSubscriber(identifier, priority, processPayload)
		if (sub.subscribe) sub else null
	}
	
	/**
	 * Creates a new {@link PayloadSubscriber} and automatically subscribes it
	 * to the current {@link EventHub} instance. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       PayloadSubscriber can be notified.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @return the newly created PayloadSubscriber, or {@code null} if it could
	 *         not be subscribed to the EventHub.
	 * 
	 * @throws NullPointerException if {@code processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(String, Function1)
	 *      PayloadSubscriber(String, (P) => R)
	 * @see Subscriber#subscribe()
	 *      Subscriber.subscribe()
	 */
	def <P ,R> subscribePayloadSubscriber(String identifier, (P) => R processPayload) {
		val sub = new PayloadSubscriber(identifier, processPayload)
		if (sub.subscribe) sub else null
	}
	
	
	
	/*** Iterables ***/
	
	/**
	 * Creates a new {@link PairwiseIterable} instance.
	 * 
	 * @see PairwiseIterable
	 */
	def <L, R> pairwise(Iterable<L> left, Iterable<R> right, Fill fill) {
		new PairwiseIterable(left, right, fill)
	}
	
	/**
	 * Creates a new {@link PairwiseIterable} instance.
	 * 
	 * @see PairwiseIterable
	 */
	def <L, R> pairwise(Iterable<L> left, Iterable<R> right) {
		new PairwiseIterable(left, right)
	}
	
	/**
	 * Creates a new {@link PairwiseIterable} instance.
	 * 
	 * @see PairwiseIterable
	 */
	def <L, R> <> (Iterable<L> left, Iterable<R> right) {
		new PairwiseIterable(left, right)
	}
	
	/**
	 * Creates a new {@link CrossProductIterable} instance.
	 * 
	 * @see CrossProductIterable
	 */
	def <T> crossProduct(Iterable<T> iterable) {
		new CrossProductIterable(iterable, iterable)
	}
	
	/**
	 * Creates a new {@link CrossProductIterable} instance.
	 * 
	 * @see CrossProductIterable
	 */
	def <L, R> crossProduct(Iterable<L> left, Iterable<R> right) {
		new CrossProductIterable(left, right)
	}
	
	/**
	 * Creates a new {@link CrossProductIterable} instance.
	 * 
	 * @see CrossProductIterable
	 */
	def <L, R> * (Iterable<L> left, Iterable<R> right) {
		new CrossProductIterable(left, right)
	}
	
	
	
	/*** Logging ***/
	
	def log(Object caller, CharSequence message) {
		if (logging) {
			System.out.println('''[«caller.class.simpleName»] «message»''')
		}
	}
	
	def err(Object caller, CharSequence message) {
		System.err.println('''[«caller.class.simpleName»] «message»''')
	}
	
}
