package de.jabc.cinco.meta.plugin.pyro.frontend.pages.shared.navigation

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class NavigationComponent extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	

	def fileNameTemplate()'''navigation_component.html'''

	def contentTemplate()
	'''
		<header class="mb-3">
			<nav class="navbar navbar-static-top navbar-expand-lg navbar-dark org-nav-bg-color">
		        
		        <a class="navbar-brand" style="color: white;" *ngIf="style == null || style.logo == null">
					«IF gc.cpd.image32.replace('"','').nullOrEmpty»
						<img alt="Brand" src="img/pyro_flame_white.svg" style="height: 30px; margin-top: -10px">
					«ELSE»
						<img alt="Brand" src="cpd/«gc.cpd.image32.replace('"','')»" style="max-height: 40px;max-width: 30px;">

					«ENDIF»
		            «gc.cpd.name»
		        </a>
		        
		        <a class="mr-2" *ngIf="style != null && style.logo != null">
		            <img alt="Brand" src="{{style.logo.downloadPath}}" style="height: 30px; width: auto">
		        </a>
		        
		        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarToggler" aria-controls="navbarToggler" aria-expanded="false">
		    	  <i class="fas fa-fw fa-bars"></i>
		  		</button>
		        
		        <div class="collapse navbar-collapse" *ngIf="currentUser != null" id="navbarToggler">
		        	<ul class="navbar-nav">
				      <li class="nav-item" *ngIf="isAdmin">
				        <a class="nav-link org-nav-text-color" [routerLinkActive]="'active'" [routerLink]="top_routes.RoutePaths.admin.toUrl()">Admin</a>
				      </li>
				      <li class="nav-item">
				        <a class="nav-link org-nav-text-color" [routerLinkActive]="'active'" [routerLink]="top_routes.RoutePaths.organizations.toUrl()">Organizations</a>
				      </li>
			        </ul>
			        
			        <div class="w-100">
			        	<ng-content></ng-content>
			        </div>
			        
			        <ul class="navbar-nav">
				      <li class="nav-item dropdown">
			              <a href="#" class="py-lg-0 nav-link org-nav-text-color dropdown-toggle d-flex flex-row align-items-center" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
			              	<div class="d-none d-lg-block" style="height: 30px; width: 30px; overflow: hidden">
			              	  <profile-image [user]="currentUser"></profile-image>
			              	</div>
			              	<span class="d-inline d-lg-none">{{currentUser.username}}</span>
			              	<span class="caret ml-2"></span>
			              </a>
			              <div class="dropdown-menu dropdown-menu-right">
			                  <a class="dropdown-item" [routerLink]="top_routes.RoutePaths.profile.toUrl()">
			                  	<i class="fas fa-fw fa-user"></i> Profile
			                  </a>
			                  <div role="separator" class="dropdown-divider"></div>
			                  <a class="dropdown-item" [routerLink]="top_routes.RoutePaths.logout.toUrl()">
			                    <i class="fas fa-fw fa-sign-out-alt"></i> Logout
			                  </a>
			              </div>
			          </li>
			        </ul>
		        </div>
		    </nav>
		</header>
	'''
}
