package de.jabc.cinco.meta.core.utils;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

public class URIHandler extends URIHandlerImpl.PlatformSchemeAware{
	EPackage ePack = null;
	HashMap<String,String>uriMap = null;
	
	public URIHandler(EPackage ePack){
		this.ePack = ePack;
		this.uriMap = new HashMap<String,String>();
		
		for(EClassifier eClassifier: ePack.getEClassifiers()){
			if(eClassifier instanceof EClass){
				EClass eClass = (EClass)eClassifier;
				for(EClass superType: eClass.getESuperTypes()){
					if(!ePack.eContents().contains(superType)){
						String superTypeURIFragment = superType.eResource().getURIFragment(superType);
						String from = superType.eResource().getURI()+"#"+superTypeURIFragment;
						String to = superType.getEPackage().getNsURI()+"#"+superTypeURIFragment;
						uriMap.put(from, to);
					}
				}
				
			}
		}
	}

	@Override
	public URI deresolve(URI uri) {
		URI nURI = null;
		if(uri.isPlatform()){
			String uri2 = uriMap.get(uri.toString());
			if(uri2!=null)
				nURI =URI.createURI( uri2);
			else
				nURI = super.deresolve(uri);
		}
		if(nURI==null)
			 nURI = super.deresolve(uri);
		return nURI;
	}

}
