package de.jabc.cinco.meta.core.event.hub.impl

import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.hub.Subscriber
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

/**
 * An implementation of the abstract {@link Subscriber}, that allows easier
 * definitions of Subscribers.
 * <p>
 * The SimpleSubscriber uses a {@link Procedure1 Procedure1&lt;P&gt;}
 * ({@code (C) => void}) {@link SimpleSubscriber#processContext
 * processContext} to implement the {@link Subscriber#execute(Context)
 * execute(Context)} method. This allows the use of lambda expressions for more
 * compact readable code.
 * <p>
 * Only if the received Context is  of type {@code <C>}, the {@code
 * processContext} procedure will be run.
 * <p>
 * <b>Xtend example:</b>
 * <p>
 * The following two Subscribers accomplish the same task:
 * <pre>
 * new SimpleSubscriber('hello.world') [ CompositeContext receivedContext |
 *     println(receivedContext.identifier)
 * ]
 * </pre>
 * <pre>
 * new Subscriber(new CompositeContext('hello.world')) {
 *     override execute(Context receivedContext) {
 *         if (receivedContext instanceof CompositeContext) {
 *             println(receivedContext.identifier)
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * <b>Java example:</b>
 * <p>
 * The following two Subscribers accomplish the same task:
 * <pre>
 * new SimpleSubscriber&lt;&gt;("hello.world", (CompositeContext receivedContext) -> {
 *     System.out.println(receivedContext.getIdentifier());
 * });
 * </pre>
 * <pre>
 * new Subscriber(new CompositeContext("hello.world")) {
 *     &#64;Override
 *     public void execute(Context receivedContext) {
 *         if (receivedContext instanceof CompositeContext) {
 *             CompositeContext context = (CompositeContext) receivedContext;
 *             System.out.println(context.getIdentifier());
 *         }
 *     }
 * };
 * </pre>
 * 
 * @author Fabian Storek
 */
class SimpleSubscriber<C extends Context> extends Subscriber {
	
	/**
	 * The procedure, that will be run on the received Context.
	 */
	val (C) => void processContext
	
	/**
	 * SimpleSubscriber constructor.
	 * 
	 * Creates a new instance of a SimpleSubscriber. The SimpleSubscriber is
	 * not subscribed to any EventHub.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processContext The procedure, that will be run on the received
	 *                       Context. May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(Context, Procedure1)
	 *      SimpleSubscriber(Context, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(String, int, Procedure1)
	 *      SimpleSubscriber(String, int, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(Sting, Procedure1)
	 *      SimpleSubscriber(String, (C) => void)
	 */
	new (Context context, int priority, (C) => void processContext) {
		super(context, priority)
		if (processContext === null) {
			throw new NullPointerException('processContext may not be null.')
		}
		this.processContext = processContext
	}
	
	/**
	 * SimpleSubscriber constructor.
	 * 
	 * Creates a new instance of a SimpleSubscriber. The SimpleSubscriber is
	 * not subscribed to any EventHub. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param context        The Context, by which this Subscriber can be
	 *                       notified. May not be {@code null}.
	 * @param processContext The procedure, that will be run on the received
	 *                       Context. May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code context} or {@code
	 *                              processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(Context, int, Procedure1)
	 *      SimpleSubscriber(Context, int, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(String, int, Procedure1)
	 *      SimpleSubscriber(String, int, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(Sting, Procedure1)
	 *      SimpleSubscriber(String, (C) => void)
	 */
	new (Context context, (C) => void processContext) {
		super(context)
		if (processContext === null) {
			throw new NullPointerException('processContext may not be null.')
		}
		this.processContext = processContext
	}
	
	/**
	 * SimpleSubscriber constructor.
	 * 
	 * Creates a new instance of a SimpleSubscriber. The SimpleSubscriber is
	 * not subscribed to any EventHub.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       SimpleSubscriber can be notified.
	 * @param priority       The Priority of this Subscriber. Defines the
	 *                       execution order of Subscribers, that are notified
	 *                       by the same Context; higher Priority first.
	 * @param processContext The procedure, that will be run on the received
	 *                       {@link processContext}. May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(Context, int, Procedure1)
	 *      SimpleSubscriber(Context, int, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(Context, Procedure1)
	 *      SimpleSubscriber(Context, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(Sting, Procedure1)
	 *      SimpleSubscriber(String, (C) => void)
	 */
	new (String identifier, int priority, (C) => void processContext) {
		super(new CompositeContext(identifier), priority)
		if (processContext === null) {
			throw new NullPointerException('processContext may not be null.')
		}
		this.processContext = processContext
	}
	
	/**
	 * SimpleSubscriber constructor.
	 * 
	 * Creates a new instance of a SimpleSubscriber. The SimpleSubscriber is
	 * not subscribed to any EventHub. The {@linkplain
	 * Subscriber#DEFAULT_PRIORITY default} priority will be used.
	 * 
	 * @param identifier     A String of identifiers, that will be used to
	 *                       crate a {@link CompositeContext}, by which this
	 *                       SimpleSubscriber can be notified.
	 * @param processContext The procedure, that will be run on the received
	 *                       {@link processContext}. May not be {@code null}.
	 * 
	 * @throws NullPointerException if {@code processContext} is {@code null}.
	 * 
	 * @see SimpleSubscriber#SimpleSubscriber(Context, int, Procedure1)
	 *      SimpleSubscriber(Context, int, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(Context, Procedure1)
	 *      SimpleSubscriber(Context, (C) => void)
	 * @see SimpleSubscriber#SimpleSubscriber(String, int, Procedure1)
	 *      SimpleSubscriber(String, int, (C) => void)
	 */
	new (String identifier, (C) => void processContext) {
		super(new CompositeContext(identifier))
		if (processContext === null) {
			throw new NullPointerException('processContext may not be null.')
		}
		this.processContext = processContext
	}
	
	/**
	 * This method is executed, if the {@link EventHub} is notified with a
	 * Context, that {@linkplain Context#matches(Context) matches} this
	 * SimpleSubscriber's {@link Subscriber#context context}.
	 * <p>
	 * This Subscriber uses the {@link SimpleSubscriber#processContext
	 * processContext} procedure to implement the {@code execute(Context)}
	 * method. Only if the received Context is of type {@code <C>}, the {@code
	 * processContext} procedure will be run.	
	 * 
	 * @param receivedContext The Context, that notified this subscriber.
	 */
	override execute(Context receivedContext) {
		try {
			val context = receivedContext as C
			processContext.apply(context)
		}
		catch (ClassCastException e) {
			// Do nothing
		}
	}
	
}
