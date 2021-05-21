package de.jabc.cinco.meta.core.event.test.performance

import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.hub.impl.CompositeContext
import de.jabc.cinco.meta.core.event.hub.impl.RegexContext
import de.jabc.cinco.meta.core.event.util.EventCoreExtension
import java.util.Map
import org.junit.Before
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

/**
 * Unit test (JUnit 5) for comparing performance of {@link CompositeContext},
 * {@link RegexContext} and {@link CompositeRegexContext}.
 * @author Fabian Storek
 */
@Tag('performance')
class ContextPerformanceTest {
	
	extension EventCoreExtension = new EventCoreExtension
	
	val static int noOfTests   = 4
	val static int repetitions = 1000000
	val static int maxProgress = noOfTests * repetitions
	
	var static long   timer
	var static int    progress
	var static String resultText
	
	var static String[] testData
	var static CompositeContext[] compositeContexts
	var static RegexContext[] regexContexts
	
	@BeforeAll
	def static void beforeAll() {
		progress = 0
		resultText = '''
			================================================================
			ContextPerformanceTest results:
		'''
		
		testData = #[
			'hello.world',
			'hello.disney.world',
			'hello.*.world',
			'hello.**.world',
			'hell*.***..world*.**'
		]
		compositeContexts      = testData.map[new CompositeContext(it)]
		regexContexts          = testData.map[new RegexContext(replaceAll('\\*', '\\\\*'))]
		
		print('''
			================================================================
			ContextPerformanceTest progress:
			----------------------------------------------------------------
		''')
	}
	
	@Before
	def void before() {
		resetTimer
	}
	
	@AfterAll
	def static void afterAll() {
		println('''
			«resultText»
			================================================================
		''')
	}
	
	@Test
	def void testConstructor() {
		val map = #{
			repeatConstructor('CompositeContext')      [new CompositeContext(it)],
			repeatConstructor('RegexContext')          [new RegexContext(replaceAll('\\*', '\\\\*'))]
		}
		appendResults('testConstructor', testData.size * repetitions, map)
	}
	
	@Test
	def void testMatches() {
		val map = #{
			repeatMatches('CompositeContext',      compositeContexts),
			repeatMatches('RegexContext',          regexContexts)
		}
		appendResults('testMatches', testData.size * testData.size * repetitions, map)
	}
	
	def private repeatConstructor(String name, (String) => void constructor) {
		resetTimer
		for (var i = 0; i <= repetitions; i++) {
			for (str: testData) {
				constructor.apply('''«i».«str»''')
			}
			printProgress
		}
		return name -> time
	}
	
	def private repeatMatches(String name, Iterable<? extends Context> contexts) {
		resetTimer
		for (var i = 0; i <= repetitions; i++) {
			for (it: contexts.crossProduct) {
				left <=> right
			}
			printProgress
		}
		return name -> time
	}
	
	def private void resetTimer() {
		timer = System.currentTimeMillis
	}
	
	def private long getTime() {
		System.currentTimeMillis - timer
	}
	
	def private void printProgress() {
		val pausedTimer = time // "Pause" timer
		progress += 1
		if ((100 * progress) % maxProgress == 0) {
			val progressPercent = 100 * progress / maxProgress
			print('''«IF progressPercent < 10» «ENDIF»«progressPercent»% ''')
		}
		if ((10 * progress) % maxProgress == 0) {
			println
		}
		timer = System.currentTimeMillis - pausedTimer // "Resume" timer
	}
	
	def private int percent(long a, long b) {
		(a.doubleValue / b.doubleValue * 100.0) as int
	}
	
	def private String justifyBefore(String str, int length) {
		'''«FOR i: 0 ..< length - str.length» «ENDFOR»«str»'''
	}
	
	def private String justifyAfter(String str, int length) {
		'''«str»«FOR i: 0 ..< length - str.length» «ENDFOR»'''
	}
	
	def private void appendResults(String name, int iterations, Map<String, Long> map) {
		val longestName = Math.max(20, map.keySet.map[length].max)
		val longestTime = Math.max(iterations.toString.length, map.values.map[toString.length].max)
		val minRuntime = map.values.min
		resultText += '''
			----------------------------------------------------------------
			«name»:
				«'Iterations per class'.justifyAfter(longestName)» «iterations»
				«FOR it: map.entrySet»
					«key.justifyAfter(longestName)» «value.toString.justifyBefore(longestTime)» ms («percent(value, minRuntime)» %)«IF minRuntime == value» - BEST«ENDIF»
				«ENDFOR»
		'''
	}
	
}
