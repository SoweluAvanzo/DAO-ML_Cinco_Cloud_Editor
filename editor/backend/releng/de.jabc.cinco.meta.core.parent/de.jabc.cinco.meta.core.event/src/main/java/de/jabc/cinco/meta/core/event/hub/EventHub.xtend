// Unit tests: de.jabc.cinco.meta.core.event.test.EventHubTest

package de.jabc.cinco.meta.core.event.hub

import de.jabc.cinco.meta.core.event.util.EventCoreExtension
import java.util.SortedSet

/**
 * A centralized hub for triggering arbitrary events.
 * <p>
 * This class is a singleton. Use {@link EventHub#getInstance()
 * EventHub.getInstance()} to obtain access to the EventHub instance.
 * <p>
 * Create a {@link Subscriber} and subscribe it to the EventHub with {@link
 * EventHub#subscribe(Subscriber) subscribe(Subscriber)}. Then notify
 * your Subscriber with {@link EventHub#notify(Context) notify(Context)}. Any
 * Subscriber, whose {@link Context} matches the one in the notification, will
 * be {@linkplain Subscriber#execute(Object) executed}.
 * <p>
 * You may want to use the {@link EventCoreExtension} for convenience methods
 * like:
 * <ul>
 * <li>{@link EventCoreExtension#subscribeSimpleSubscriber(String,
 * org.eclipse.xtext.xbase.lib.Procedures.Procedure1)
 * subscribeSimpleSubscriber(...)}: Creates a new Subscriber and automatically
 * subscribes it to the EventHub.</li>
 * <li>{@link EventCoreExtension#notify(String, P) notify(String, P)}:
 * Creates a new PayloadContext notifies Subscribers.</li>
 * </ul>
 * 
 * @author Fabian Storek
 */
class EventHub {
	
	extension EventCoreExtension = new EventCoreExtension
	
	/**
	 * The EventHub instance.
	 */
	var static EventHub instance
	
	/**
	 * The set of currently subscribed {@link Subscriber Subscribers}.
	 */
	val SortedSet<Subscriber> subscribers
	
	/**
	 * EventHub constructor.
	 */
	private new () {
		// Reverse order: Highest priority first
		this.subscribers = newTreeSet(Subscriber.priorityComparator.reversed)
		logging = false
	}
	
	/**
	 * Returns the EventHub instance.
	 */
	def static EventHub getInstance() {
		if (instance === null) {
			instance = new EventHub
		}
		return instance
	}
	
	/**
	 * Returns an array of all currently subscribed Subscribers.
	 */
	def Subscriber[] getSubscribers() {
		subscribers
	}
	
	/**
	 * Whether or not a subscriber is subscribed to this EventHub instance.
	 */
	def hasSubscriber(Subscriber subscriber) {
		subscribers.contains(subscriber)
	}
	
	/**
	 * Subscribes the {@code subscriber} to this EventHub.
	 * 
	 * @param subscriber The <i>subscriber</i>, that will be subscribed to this
	 *                   EventHub.
	 * 
	 * @return {@code true}, iff the {@code subscriber} was is not already
	 *         subscribed and now has been successfully subscribed.
	 */
	def boolean subscribe(Subscriber subscriber) {
		if (hasSubscriber(subscriber)) {
			err('''Subscriber already subscribed: «subscriber.context»''')
			return false
		}
		// Try at most 1.000.000 times to add the subscriber
		for (i: 0 ..< 1000000) {
			if (subscribers.add(subscriber)) {
				log('''New subscriber: «subscriber.context»''')
				return true
			}
			else {
				subscriber.priority.shuffle
				// ... and try again
			}
		}
		err('''Failed to subscribe: «subscriber.context»''')
		return false
	}
		
	/**
	 * Unsubscribes the {@code subscriber} from this EventHub.
	 * 
	 * @param subscriber The <i>subscriber</i>, that will be unsubscribed from
	 *                   this EventHub.
	 * 
	 * @return {@code true}, iff the {@code subscriber} was subscribed and now
	 *         has been successfully unsubscribed.
	 */
	def boolean unsubscribe(Subscriber subscriber) {
		if (!hasSubscriber(subscriber)) {
			return false
		}
		if (subscribers.remove(subscriber)) {
			return true
		}
		return false
	}
	
	/**
	 * Notify all {@link Subscriber Subscribers} with matching {@link Context
	 * Contexts} and {@linkplain Subscriber#execute(Object)
	 * execute} them.
	 * <p>
	 * You may want to consider using the convenience methods {@link
	 * EventCoreExtension#notify(String, P) notify(...)} of the {@link
	 * EventCoreExtension}.
	 * 
	 * @param context defines the subset of Subscribers, that will be notified.
	 *                The {@code context} will be passed to the {@link
	 *                Subscriber#execute(Context) execute(Context)} method of
	 *                the subscribers.
	 * 
	 * @return {@code true}, iff at least one subscriber was notified.
	 * 
	 * @see EventHub#notifyFirst(Context) EventHub.notifyFirst(Context)
	 * @see EventCoreExtension#notify(String, P)
	 *      EventCoreExtension.notify(String, P)
	 */
	def boolean notify(Context context) {
		log('''Notifying: «context»''')
		val notifiedSubscribers = subscribers.filter [ isNotifiedBy(context) ]
		if (notifiedSubscribers.nullOrEmpty) {
			log('''No subscriber was notified''')
			return false
		}
		else {
			log('''«notifiedSubscribers.size» subscriber«IF notifiedSubscribers.size != 1»s«ENDIF» will be notified''')
			notifiedSubscribers.forEach [ execute(context) ]
			return true
		}
	}
	
	/**
	 * Notify the {@link Subscriber} with the highest priority and matching
	 * {@link Context Context} and {@linkplain Subscriber#execute(Object)
	 * execute} them.
	 * 
	 * @param context defines the subset of Subscribers, that can be notified.
	 *                The {@code context} will be passed to the {@link
	 *                Subscriber#execute(Context) execute(Context)} method of
	 *                the subscriber.
	 * 
	 * @return {@code true}, iff at least one subscriber was notified.
	 * 
	 * @see EventHub#notify(Context) EventHub.notify(Context)
	 */
	def boolean notifyFirst(Context context) {
		log('''Notifying first: «context»''')
		val notifiedSubscriber = subscribers.findFirst [ isNotifiedBy(context) ]
		if (notifiedSubscriber === null) {
			log('''No subscriber was notified''')
			return false
		}
		else {
			log('''1 subscriber will be notified''')
			notifiedSubscriber.execute(context)
			return true
		}
	}
	
	/**
	 * Returns a String representation of the EventHub instance.
	 */
	override toString() {
		if (subscribers.nullOrEmpty) {
			'''«class.simpleName»()'''
		}
		else {
			'''
				«class.simpleName»(
					«FOR sub: subscribers SEPARATOR ','»
						«sub»
					«ENDFOR»
				)
			'''
		}
	}
	
}
