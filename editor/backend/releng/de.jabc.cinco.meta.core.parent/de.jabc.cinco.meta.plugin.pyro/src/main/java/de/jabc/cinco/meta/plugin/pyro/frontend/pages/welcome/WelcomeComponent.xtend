package de.jabc.cinco.meta.plugin.pyro.frontend.pages.welcome

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class WelcomeComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	

	def fileNameTemplate()'''welcome_component.html'''

	def contentTemplate()
	'''
	<div class="welcome-class" style="margin-top: 100px">
	    <div class="inner cover">
			«IF gc.cpd.image128.replace('"','').nullOrEmpty»
			    <img src="img/pyro.png">
			«ELSE»
				<img style="max-width: 300px;max-height: 300px;" src="cpd/«gc.cpd.image128.replace('"','')»">

			«ENDIF»
	        <h1 class="cover-heading">Welcome to «gc.cpd.name».</h1>
	        <p class="lead">«IF gc.cpd.about === null || gc.cpd.about.aboutText.nullOrEmpty»Create and Share your Models.«ELSE»«gc.cpd.about.aboutText»«ENDIF»</p>
	        <p class="lead">
	            <a href [routerLink]="RoutePaths.login.toUrl()" class="btn btn-primary btn-lg m-auto">Login</a>
	        </p>
	    </div>
	</div>
	'''
}
