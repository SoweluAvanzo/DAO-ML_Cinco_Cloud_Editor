package de.jabc.cinco.meta.plugin.pyro.frontend.pages.logout

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class LogoutComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	
	def fileNameTemplate()'''logout_component.html'''
	
	
	def contentTemplate()
	'''
	<div class="container">
		<div class="mt-4 mx-auto" style="max-width: 320px;">
			<div class="text-center my-3">
					«IF gc.cpd.image128.trimQuotes.nullOrEmpty»
					<img src="img/pyro.png">
			        «ELSE»
					<img style="max-width: 300px;max-height: 300px;" src="cpd/«gc.cpd.image128.trimQuotes»">
			        «ENDIF»
			</div>
			<h1 class="text-center">Bye!</h1>
			<p class="text-center">
				You are being logged out and redirected to the start page.
				If this does not work click <a href="#" (click)="logout($event)">here</a>.
			</p>
		</div>
	</div>
	'''
}
