package de.jabc.cinco.meta.core.event.test

import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.hub.EventHub
import de.jabc.cinco.meta.core.event.hub.Subscriber
import de.jabc.cinco.meta.core.event.hub.impl.CompositeContext
import de.jabc.cinco.meta.core.event.hub.impl.PayloadContext
import de.jabc.cinco.meta.core.event.util.EventCoreExtension
import java.util.ArrayList
import java.util.concurrent.ThreadLocalRandom
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link EventHub}.
 * @author Fabian Storek
 */
class EventHubTest {
	
	extension EventCoreExtension = new EventCoreExtension
	
	var static ArrayList<Subscriber> expectedOrder
	var static ArrayList<Subscriber> actualOrder
	var static int expectedPayload
	
	@Test
	def void testSubscribeNotifyUnsubscribe() {
		
		testSubscribe(null)
		testSubscribe('')
		
		val abc  = testSubscribe('a.b.c')
		val abcd = testSubscribe('a.b.c.d')
		val abx  = testSubscribe('a.b.x')
		val ab_  = testSubscribe('a.b.*')
		val a_c  = testSubscribe('a.*.c')
		
		testNotify(null)
		testNotify('')
		testNotify('hello.world')
		
		testNotify('a.b.c',   abc, ab_, a_c)
		testNotify('a.b.c.d', abcd)
		testNotify('a.b.x',   abx, ab_)
		testNotify('a.b.*',   abc, abx, ab_, a_c)
		testNotify('a.*.c',   abc, ab_, a_c)
		
		testUnsubscribe(abc)
		testUnsubscribe(abx)
		
		testNotify(null)
		testNotify('')
		testNotify('hello.world')
		
		testNotify('a.b.c',   ab_, a_c)
		testNotify('a.b.c.d', abcd)
		testNotify('a.b.x',   ab_)
		testNotify('a.b.*',   ab_, a_c)
		testNotify('a.*.c',   ab_, a_c)
		
	}
	
	@Test
	def void testNotifyFirst() {
		
		actualOrder = newArrayList
		
		val context = new CompositeContext('hello.world')
		var highestPrioritiy = Integer.MIN_VALUE
		var Subscriber highestSub = null
		
		for (i: 0 ..< 100) {
			val priority = ThreadLocalRandom.current.nextInt
			val sub = new Subscriber(context, priority) {
				override execute(Context context) {
					actualOrder.add(this)
				}
			}
			sub.subscribe
			if (priority > highestPrioritiy) {
				highestPrioritiy = priority
				highestSub = sub
			}
		}
		
		assertFalse(notifyFirst('bye.bye'))
		assertTrue(notifyFirst(context))
		assertEquals(1, actualOrder.size)
		assertEquals(highestSub, actualOrder.head)
		
	}
	
	def private Subscriber testSubscribe(String context) {
		
		val compositeContext = new CompositeContext(context)
		val randomPriority   = ThreadLocalRandom.current.nextInt
		
		val sub = new Subscriber(compositeContext, randomPriority) {
			override execute(Context context) {
				assertNotNull(context)
				assertTrue(context instanceof PayloadContext<?, ?>)
				val payloadContext = context as PayloadContext<?, ?>
				assertTrue(payloadContext.hasPayload)
				assertTrue(payloadContext.payload instanceof Integer)
				assertEquals(expectedPayload, payloadContext.payload as Integer)
				actualOrder.add(this)
			}
		}
		
		assertFalse(sub.isSubscribed)
		assertFalse(EventHub.instance.hasSubscriber(sub))
		
		assertTrue(sub.subscribe)
		
		assertTrue(sub.isSubscribed)
		assertTrue(EventHub.instance.hasSubscriber(sub))
		
		return sub
		
	}
	
	def private void testUnsubscribe(Subscriber sub) {
		
		assertTrue(sub.isSubscribed)
		assertTrue(EventHub.instance.hasSubscriber(sub))
		
		assertTrue(sub.unsubscribe)
		
		assertFalse(sub.isSubscribed)
		assertFalse(EventHub.instance.hasSubscriber(sub))
		
	}
	
	private def void testNotify(String context, Subscriber ... expectedSubscribers) {
		
		expectedOrder   = newArrayList
		actualOrder     = newArrayList
		expectedPayload = ThreadLocalRandom.current.nextInt
		
		assertEquals(
			!expectedSubscribers.nullOrEmpty,
			notify(context, expectedPayload)
		)
		
		expectedSubscribers.forEach [ sub |
			expectedOrder.add(sub)
		]
		
		expectedOrder.sort[ left, right |
			// Reverse order: Highest priority first
			right.priority.compareTo(left.priority)
		]
		
		assertEquals(expectedOrder.size, actualOrder.size)
		assertEquals(expectedOrder, actualOrder)
		
	}
	
}