package de.jabc.cinco.meta.core.event.hub.impl

import org.eclipse.xtend.lib.annotations.Accessors
import java.util.Map
import de.jabc.cinco.meta.core.event.hub.Subscriber

/**
 * A subclass of {@link CompositeContext}, that provides and collects data
 * to/from {@link Subscriber Subscribers}.
 * <p>
 * This kind of {@link de.jabc.cinco.meta.core.event.hub.Context Context} is
 * intended to deliver additional information to {@link Subscriber
 * Subscribers} via the {@link PayloadContext#payload payload} field.
 * Subscribers, that produce a result can add it to the {@link
 * PayloadContext#results results} map.
 * 
 * @param P Type of the payload that can be used by the receiving Subscriber.
 * @param R Type of the result that a receiving Subscriber can produce.
 * 
 * @author Fabian Storek
 */
@Accessors(PUBLIC_GETTER)
class PayloadContext<P, R> extends CompositeContext {
	
	/**
	 * The payload.
	 */
	val P payload
	
	/**
	 * The results.
	 */
	var Map<Subscriber, R> results
	
	/**
	 * PayloadContext constructor.
	 * <p>
	 * A String will represent this PayloadContext. It may be a composite of
	 * String segments (using dot-syntax). The {@link PayloadContext#payload
	 * payload} can be an arbitrary object.
	 * <p>
	 * PayloadContext:
	 * <pre>
	 * new PayloadContext("hello.disney.world", payload)
	 *  -> "hello.disney.world"
	 * </pre>
	 * Superfluous dots ({@code .}) will be removed:
	 * <pre>
	 * new PayloadContext("hello..disney...world.", payload)
	 *  -> "hello.disney.world"
	 * </pre>
	 * 
	 * @param identifiers A Strings, that will represent this PayloadContext.
	 *                    It may be a composite of String segments (using
	 *                    dot-syntax).
	 * @param payload     An optional payload, that can be accessed by a
	 *                    notified Subscriber.
	 * 
	 * @see PayloadContext#PayloadContext(String[]) PayloadContext(String ...)
	 * @see PayloadContext#PayloadContext(String[], P) PayloadContext(String[], P)
	 */
	new (String identifiers, P payload) {
		super(identifiers)
		this.payload = payload
	}
	
	/**
	 * PayloadContext constructor.
	 * <p>
	 * A collection of Strings will represent this PayloadContext. Each String
	 * may be a composite of String segments (using dot-syntax). The {@link
	 * PayloadContext#payload payload} can be an arbitrary object.
	 * <p>
	 * If multiple Strings are provided, they will be compounded into a single
	 * PayloadContext:
	 * <pre>
	 * new PayloadContext(#["hello", "disney.world"], payload)
	 *  -> "hello.disney.world"
	 * </pre>
	 * Superfluous dots ({@code .}) will be removed:
	 * <pre>
	 * new PayloadContext(#["hello.", "", ".", ".disney...world."], payload)
	 *  -> "hello.disney.world"
	 * </pre>
	 * 
	 * @param identifiers A collection of Strings, that will represent this
	 *                    PayloadContext. Each String may be a composite of
	 *                    String segments (using dot-syntax).
	 * @param payload     An optional payload, that can be accessed by a
	 *                    notified Subscriber.
	 * 
	 * @see PayloadContext#PayloadContext(String[]) PayloadContext(String ...)
	 * @see PayloadContext#PayloadContext(String, P) PayloadContext(String, P)
	 */
	new (String[] identifiers, P payload) {
		super(identifiers)
		this.payload = payload
	}
	
	/**
	 * Whether or not this PayloadConext actually has a payload.
	 */
	def boolean hasPayload() {
		payload !== null
	}
	
	/**
	 * Whether or not this PayloadConext has one ore more results.
	 */
	def boolean hasResult() {
		results !== null && !results.empty
	}
	
	/**
	 * Adds a result to the {@link PayloadContext#results results} map.
	 * 
	 * @param sub    The Subscriber, that produced the result.
	 * @param result The actual result.
	 * 
	 * @return {@code true} if the result was added successfully.<br>
	 *         {@code false} if the Subscriber already added a result.
	 */
	def boolean addResult(Subscriber sub, R result) {
		if (results === null) {
			results = newLinkedHashMap
		}
		if (results.containsKey(sub)) {
			return false
		}
		else {
			results.put(sub, result)
			return true
		}
	}
	
	/**
	 * Returns the result of the Subscriber with the highest {@link
	 * Subscriber#priority priority}. Returns {@code null} if no Subscriber
	 * has added any results or the result type is {@link Void}.
	 */
	def R getFirstResult() {
		if (hasResult) {
			val highestPrioSub = results.keySet.max(Subscriber.priorityComparator)
			return results.get(highestPrioSub)
		}
		else {
			return null
		}
	}
	
	/**
	 * Returns a String representation of the PayloadConext object.
	 */
	override toString() {
		'''«class.simpleName»(«identifier.debugString», «payload.debugString»)'''
	}
	
	/**
	 * Creates a short debug String for any object.
	 */
	def private String debugString(Object obj) {
		switch obj {
			case null: 'null'
			String:    '''"«obj»"'''
			Character: '''«"'"»«obj»«"'"»'''
			Integer,
			Long,
			Double,
			Float,
			Short,
			Boolean:   '''«obj»'''
			default:   '''«obj.class.simpleName»(«obj.toString»)'''
		}
	}
	
}
