package de.jabc.cinco.meta.core.event.test

import de.jabc.cinco.meta.core.event.hub.Priority
import java.util.List
import java.util.concurrent.ThreadLocalRandom
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link Priority}.
 * @author Fabian Storek
 */
class PriorityTest {

	val static RANGE = 100 / 3
	
	@Test
	def void testOrder() {
		
		val List<Priority> list = newArrayList
		
		list.addRandom
		list.add(new Priority(Integer.MAX_VALUE))
		list.addRandom
		list.add(new Priority(Integer.MIN_VALUE))
		list.addRandom
		
		val sortedList = list.sort
		
		for (i: 0 ..< sortedList.size - 1) {
			
			val current = sortedList.get(i)
			val next    = sortedList.get(i + 1)
			
			assertTrue(
				(current.major < next.major) ||
				(current.major == next.major && current.minor <= next.minor)
			)
			
		}
		
	}
	
	def private void addRandom(List<Priority> list) {
		for (i: 0 ..< RANGE) {
			val random = ThreadLocalRandom.current.nextInt(-RANGE, RANGE)
			list.add(new Priority(random))
		}
	}
		
}