package de.jabc.cinco.meta.core.event.test

import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.hub.EventHub
import de.jabc.cinco.meta.core.event.hub.Subscriber
import de.jabc.cinco.meta.core.event.hub.impl.CompositeContext
import de.jabc.cinco.meta.core.event.hub.impl.PayloadSubscriber
import de.jabc.cinco.meta.core.event.hub.impl.SimpleSubscriber
import java.util.List
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link Subscriber}, {@link SimpleSubscriber} and
 * {@link PayloadSubscriber}
 * 
 * @author Fabian Storek
 */
class SubscriberTest {
		
	val static IDENTIFIERS = #[
		'*',
		'hello',
		'hello.*',
		'hello.world',
		'hello.*.*',
		'hello.*.world',
		'hello.my.world',
		'hello.disney.world'
	]
	
	var List<Subscriber> subscribers
		
	@BeforeEach
	def void before() {
		EventHub.instance.subscribers.forEach [ unsubscribe ]
		subscribers = newArrayList
		IDENTIFIERS.forEach [ value |
			subscribers.add(
				new Subscriber(new CompositeContext(value)) {
					override execute(Context receivedContext) {
						println('''anonomous Subscriber("«value»") received «receivedContext»''')
					}
				}
			)
			subscribers.add(
				new SimpleSubscriber(value) [ receivedContext |
					println('''    SimpleSubscriber("«value»") received «receivedContext»''')
				]
			)
			subscribers.add(
				new PayloadSubscriber(value) [ receivedPayload |
					println('''   PayloadSubscriber("«value»") received «receivedPayload»''')
				]
			)
		]
	}
	
	@Test
	def void testSubscribeAndUnsubscribe() {
		val hub = EventHub.instance
		subscribers.forEach [ sub |
			
			assertFalse(sub.isSubscribed)
			assertFalse(hub.hasSubscriber(sub))
			
			assertTrue(sub.subscribe)
			assertFalse(sub.subscribe)
			
			assertTrue(sub.isSubscribed)
			assertTrue(hub.hasSubscriber(sub))
			
			assertTrue(sub.unsubscribe)
			assertFalse(sub.unsubscribe)
			
			assertFalse(sub.isSubscribed)
			assertFalse(hub.hasSubscriber(sub))
			
		]
	}
	
}