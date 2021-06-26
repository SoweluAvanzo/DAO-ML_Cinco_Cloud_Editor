package de.jabc.cinco.meta.plugin.pyro.frontend.pages.main

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class MainComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	
	def fileNameTemplate()'''main_component.html'''
	
	
	def contentTemplate()
	'''
	<div class="main-class">
	    <div class="site-wrapper">
	        <div class="site-wrapper-inner">
	            <div class="cover-container">
	                      
	    			<nav class="navbar navbar-expand-lg org-nav-bg-color">
					  <a class="navbar-brand" href="https://pyro.scce.info">
						«IF gc.cpd.image32.replace('"','').nullOrEmpty»
							<img alt="Brand" src="img/pyro_flame_white.svg" style="height: 30px; margin-top: -10px">
						«ELSE»
							<img alt="Brand" src="cpd/«gc.cpd.image32.replace('"','')»" style="max-height: 40px;max-width: 30px;">

						«ENDIF»
						«gc.cpd.name»
					  </a>
					    <ul class="navbar-nav ml-auto masthead-nav">
					      <li class="nav-item">
					        <a class="nav-link org-nav-text-color" [routerLink]="RoutePaths.welcome.toUrl()">Home</a>
					      </li>
					      <li class="nav-item">
					        <a class="nav-link org-nav-text-color" [routerLink]="RoutePaths.login.toUrl()">Login</a>
					      </li>
					      «IF gc.authCompound === null»
					      <li class="nav-item">
					        <a class="nav-link org-nav-text-color" [routerLink]="RoutePaths.registration.toUrl()">Register</a>
					      </li>
					      «ENDIF»
					    </ul>
					</nav>
	            		                          
					<router-outlet [routes]="Routes.all"></router-outlet>
	                
	                <div>
	                    <p>Created with <a class="org-body-text-color" href="https://pyro.scce.info/">Pyro</a></p>
	                    «IF gc.cpd.annotations.exists[name.equals("pyroImpressum")]»
	                    	<p><a class="org-body-text-color" href="«gc.cpd.annotations.findFirst[name.equals("pyroImpressum")].value.get(0)»">Impressum</a></p>
	                    «ENDIF»
	                </div>
	                
	            </div>
	        </div>
	    </div>
	</div>

	'''
}
