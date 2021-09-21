package de.jabc.cinco.meta.core.event.hub

import org.eclipse.xtend.lib.annotations.Accessors
import java.util.Comparator

/**
 * A Subscriber to the {@link EventHub}.
 * <p>
 * If notified by a {@linkplain Context#matches(Context) matching} {@link
 * Context}, the {@link Subscriber#execute(Context) execute(Context)} method will
 * be run.
 * 
 * @author Fabian Storek
 */
@Accessors(PUBLIC_GETTER)
abstract class Subscriber {
	
	/**
	 * The default priority for new Subscribers, for which there was not set a
	 * specific priority value.
	 */
	val public static DEFAULT_PRIORITY = 0
	
	/**
	 * The Context, by which this Subscriber will be notified.
	 */
	val Context context
	
	/**
	 * The Priority of this Subscriber. This defines the execution order of
	 * Subscribers, that are notified by the same Context; higher Priority first.
	 */
	val Priority priority
	
	/**
	 * Subscriber constructor.
	 * <p>
	 * Creates a new instance of a Subscriber. The Subscriber is not subscribed
	 * to any EventHub.
	 * 
	 * @param context  The Context, by which this Subscriber can be notified.
	 *                 May not be {@code null}.
	 * @param priority The Priority of this Subscriber. Defines the execution
	 *                 order of Subscribers, that are notified by the same
	 *                 Context; higher Priority first.
	 * 
	 * @throws NullPointerException if {@code context} is {@code null}.
	 * 
	 * @see Subscriber#Subscriber(String, int) Subscriber(String, int)
	 */
	new (Context context, int priority) {
		if (context === null) {
			throw new NullPointerException('context may not be null.')
		}
		this.context  = context
		this.priority = new Priority(priority)
	}
	
	/**
	 * Subscriber constructor.
	 * <p>
	 * Creates a new instance of a Subscriber. The Subscriber is not subscribed
	 * to any EventHub. The {@linkplain Subscriber#DEFAULT_PRIORITY default}
	 * priority will be used.
	 * 
	 * @param context The Context, by which this Subscriber can be notified.
	 *                May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code context} is {@code null}.
	 * 
	 * @see Subscriber#Subscriber(String, int) Subscriber(String, int)
	 */
	new (Context context) {
		if (context === null) {
			throw new NullPointerException('context may not be null.')
		}
		this.context  = context
		this.priority = new Priority(DEFAULT_PRIORITY)
	}
	
	/**
	 * Whether or not this Subscriber is subscribed to any {@link EventHub}.
	 * 
	 * @see Subscriber#isSubscribedTo(EventHub) isSubscribedTo(EventHub)
	 */
	def boolean isSubscribed() {
		EventHub.instance.hasSubscriber(this)
	}
	
	/**
	 * Subscribes this Subscriber to the {@linkplain EventHub#getInstance()
	 * EventHub instance}.
	 * 
	 * @return {@code true}, iff the Subscriber was is not already subscribed
	 *         and now has been successfully subscribed.
	 * 
	 * @see Subscriber#subscribeTo(EventHub) subscribeTo(EventHub)
	 * @see Subscriber#unsubscribe() unsubscribe()
	 */
	def boolean subscribe() {
		EventHub.instance.subscribe(this)
	}
	
	/**
	 * Unsubscribes this Subscriber from its {@link EventHub}.
	 * 
	 * @return {@code true}, iff the Subscriber was subscribed and now has been
	 *         successfully unsubscribed.
	 * 
	 * @see Subscriber#subscribe() subscribe()
	 * @see Subscriber#subscribeTo(EventHub) subscribeTo(EventHub)
	 */
	def boolean unsubscribe() {
		EventHub.instance.unsubscribe(this)
	}
	
	/**
	 * Whether this Subscriber is notified by a particular {@link Context}.
	 * 
	 * @return {@code true}, if the provided {@code context} {@linkplain
	 *         Context#matches(Context) matches} the {@link Subscriber#context
	 *         context} of this Subscriber.
	 */
	def boolean isNotifiedBy(Context receivedContext) {
		receivedContext.matches(context)
	}
	
	/**
	 * This method is executed, if the {@link EventHub} is notified with a
	 * Context, that {@linkplain Context#matches(Context) matches} this
	 * Subscriber's {@link Subscriber#context context}.
	 * 
	 * @param receivedContext The Context, that notified this subscriber.
	 */
	def void execute(Context receivedContext)
	
	/**
	 * Returns a {@link Comparator}, that compares Subscribers based on their
	 * {@link Subscriber#priority priority}.
	 */
	def static Comparator<Subscriber> getPriorityComparator() {
		[ left, right | left.priority.compareTo(right.priority) ]
	}
	
	/**
	 * Returns a String representation of the Subscriber object.
	 */
	override toString() {
		'''«className»(«context», «priority»)'''
	}
	
	/**
	 * Returns the class name of this instance.
	 */
	def private String getClassName() {
		if (class.simpleName.nullOrEmpty) {
			'''anonymous «class.superclass.simpleName»'''
		}
		else {
			class.simpleName
		}
	}
	
}
