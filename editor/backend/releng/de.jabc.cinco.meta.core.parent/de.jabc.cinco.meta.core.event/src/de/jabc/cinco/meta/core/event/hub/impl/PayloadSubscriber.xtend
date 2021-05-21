package de.jabc.cinco.meta.core.event.hub.impl

import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.hub.Subscriber
import org.eclipse.xtext.xbase.lib.Functions.Function1

/**
 * An implementation of the abstract {@link Subscriber}, that allows easier
 * processing of {@link PayloadContext PayloadContexts}.
 * <p>
 * The PayloadSubscriber uses a {@link Function1 Function1&lt;P, R&gt;}
 * ({@code (P) => R}) {@link PayloadSubscriber#processPayload processPayload}
 * to implement the {@link Subscriber#execute(Context) execute(Context)}
 * method. This allows the use of lambda expressions for more compact readable
 * code.
 * <p>
 * Only if the received Context is a {@link PayloadContext} and its {@link
 * PayloadContext#payload payload} is of type {@code <P>}, the {@code
 * processPayload} function will be run.
 * <p>
 * <b>Xtend Example:</b>
 * <p>
 * The following two Subscribers accomplish the same task:
 * <pre>
 * new PayloadSubscriber('hello.world') [ Integer payload |
 *     return payload.toString
 * ]
 * </pre>
 * <pre>
 * new Subscriber(new CompositeContext('hello.world')) {
 *     override execute(Context receivedContext) {
 *         try {
 *             val context = receivedContext as PayloadContext&lt;Integer, String&gt;
 *             val result =  context.payload.toString
 *             context.addResult(this, result)
 *         }
 *         catch (ClassCastException e) {
 *             // Do nothing
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * <b>Java Example:</b>
 * <p>
 * The following two Subscribers accomplish the same task:
 * <pre>
 * new PayloadSubscriber&lt;&gt;("hello.world", (Integer payload) -> {
 *     return payload.toString();
 * });
 * </pre>
 * <pre>
 * new Subscriber(new CompositeContext("hello.world")) {
 *     &#64;Override
 *     public void execute(Context receivedContext) {
 *         try {
 *             &#64;SuppressWarnings("unchecked")
 *             PayloadContext&lt;Integer, String&gt; context = (PayloadContext&lt;Integer, String&gt;) receivedContext;
 *             String result = context.getPayload().toString();
 *             context.addResult(this, result);
 *         }
 *         catch (ClassCastException e) {
 *             // Do nothing
 *         }
 *     }
 * };
 * </pre>
 * 
 * @author Fabian Storek
 */
class PayloadSubscriber<P, R> extends Subscriber {
	
	/**
	 * The function, that will be run on the received {@link PayloadContext}.
	 */
	val (P) => R processPayload
	
	/**
	 * PayloadSubscriber constructor.
	 * 
	 * Creates a new instance of a PayloadSubscriber. The PayloadSubscriber is
	 * not subscribed to any EventHub.
	 * 
	 * @param context        The CompositeContext, by which this Subscriber can
	 *                       be notified. May not be {@code null}.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, Function1)
	 *      PayloadSubscriber(CompositeContext, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(String, int, Function1)
	 *      PayloadSubscriber(String, int, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(String, Function1)
	 *      PayloadSubscriber(String, (P) => R)
	 */
	new (CompositeContext context, int priority, (P) => R processPayload) {
		super(context, priority)
		if (processPayload === null) {
			throw new NullPointerException('processPayload may not be null.')
		}
		this.processPayload = processPayload
	}
	
	/**
	 * PayloadSubscriber constructor.
	 * 
	 * Creates a new instance of a PayloadSubscriber. The PayloadSubscriber is
	 * not subscribed to any EventHub. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param context        The CompositeContext, by which this Subscriber can
	 *                       be notified. May not be {@code null}.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, int, Function1)
	 *      PayloadSubscriber(CompositeContext, int, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(String, int, Function1)
	 *      PayloadSubscriber(String, int, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(String, Function1)
	 *      PayloadSubscriber(String, (P) => R)
	 */
	new (CompositeContext context, (P) => R processPayload) {
		super(context)
		if (processPayload === null) {
			throw new NullPointerException('processPayload may not be null.')
		}
		this.processPayload = processPayload
	}
	
	/**
	 * PayloadSubscriber constructor.
	 * 
	 * Creates a new instance of a PayloadSubscriber. The PayloadSubscriber is
	 * not subscribed to any EventHub.
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
	 * @throws NullPointerException if {@code processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, int, Function1)
	 *      PayloadSubscriber(CompositeContext, int, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, Function1)
	 *      PayloadSubscriber(CompositeContext, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(String, Function1)
	 *      PayloadSubscriber(String, (P) => R)
	 */
	new (String identifier, int priority, (P) => R processPayload) {
		super(new CompositeContext(identifier), priority)
		if (processPayload === null) {
			throw new NullPointerException('processPayload may not be null.')
		}
		this.processPayload = processPayload
	}
	
	/**
	 * PayloadSubscriber constructor.
	 * 
	 * Creates a new instance of a PayloadSubscriber. The PayloadSubscriber is
	 * not subscribed to any EventHub. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       PayloadSubscriber can be notified.
	 * @param processPayload The function, that will be run on the received
	 *                       Context's {@link PayloadContext#payload payload}.
	 *                       May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code processPayload} is {@code null}.
	 * 
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, int, Function1)
	 *      PayloadSubscriber(CompositeContext, int, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(CompositeContext, Function1)
	 *      PayloadSubscriber(CompositeContext, (P) => R)
	 * @see PayloadSubscriber#PayloadSubscriber(String, int, Function1)
	 *      PayloadSubscriber(String, int, (P) => R)
	 */
	new (String identifier, (P) => R processPayload) {
		super(new CompositeContext(identifier))
		if (processPayload === null) {
			throw new NullPointerException('processPayload may not be null.')
		}
		this.processPayload = processPayload
	}
	
	/**
	 * This method is executed, if the {@link EventHub} is notified with a
	 * Context, that {@linkplain Context#matches(Context) matches} this
	 * PayloadSubscriber's {@link Subscriber#context context}.
	 * <p>
	 * This Subscriber uses the {@link PayloadSubscriber#processPayload
	 * processPayload} function to implement the {@code execute(Context)}
	 * method. Only if the received Context is a {@code PayloadContext} and its
	 * payload is of type {@code <P>}, the {@code processPayload} function
	 * will be run.	
	 * 
	 * @param receivedContext The Context, that notified this subscriber.
	 */
	override execute(Context receivedContext) {
		try {
			val context = receivedContext as PayloadContext<P, R>
			val result = processPayload.apply(context.payload)
			context.addResult(this, result)
		}
		catch (ClassCastException e) {
			// Do nothing
		}
	}
	
}
