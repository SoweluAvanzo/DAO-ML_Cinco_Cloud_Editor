package de.jabc.cinco.meta.core.event.test

import de.jabc.cinco.meta.core.event.hub.impl.RegexContext
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link RegexContext}.
 * @author Fabian Storek
 */
class RegexContextTest {
	
	@Test
	def void testMatches() {
		
		val hello_world      = new RegexContext('hello.*world')
		val helloWorld       = new RegexContext('helloworld')
		val helloDisneyWorld = new RegexContext('hello disney-world')
		
		assertTrue (hello_world.matches(helloWorld))
		assertTrue (hello_world.matches(helloDisneyWorld))
		assertFalse(helloWorld.matches(helloDisneyWorld))

		assertTrue (helloWorld.matches(hello_world))
		assertTrue (helloDisneyWorld.matches(hello_world))
		assertFalse(helloDisneyWorld.matches(helloWorld))
		
	}
	
}