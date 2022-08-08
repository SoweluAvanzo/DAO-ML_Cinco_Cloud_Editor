package de.jabc.cinco.meta.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.ILanguageServerAccess.Context;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.XtextPlatformResourceURIHandler;
import org.eclipse.xtext.resource.XtextResourceSet;

public class ParserHelper {

	/**
	 * Contains all resources that are supported by this language-server and
	 * also can be resolved through cross-references. Does not contain the files
	 * that are just resources, e.g. text-files and images.
	 *  
	 * @param languageServerAccess
	 * @param resourceSet an XtextResourceSet provided by an injected "Provider<XtextResourceSet>"
	 * @param projectURI the uri to the workspaceFolder, which represents the project base
	 * @return
	 */
	public static Map<String, Resource> getAllResources(ILanguageServerAccess languageServerAccess, XtextResourceSet resourceSet, URI projectURI) {
		List<URI> uris = getURIs(languageServerAccess);
		return getAllResources(uris, resourceSet, projectURI);
	}
	
	/**
	 * Contains all resources that are supported by this language-server and
	 * also can be resolved through cross-references. Does not contain the files
	 * that are just resources, e.g. text-files and images.
	 *  
	 * @param uris to parse
	 * @param resourceSet an XtextResourceSet provided by an injected "Provider<XtextResourceSet>"
	 * @param projectURI the uri to the workspaceFolder, which represents the project base
	 * @return
	 */
	public static Map<String, Resource> getAllResources(List<URI> uris, XtextResourceSet resourceSet, URI projectURI) {
		Map<String, Resource> parsedResources = new HashMap<>();
		
		// parse and resolve resources
		XtextPlatformResourceURIHandler handler = (XtextPlatformResourceURIHandler) resourceSet.getLoadOptions().get("URI_HANDLER");
		handler.setBaseURI(projectURI);
		for(URI uri:uris) {
			try {
				Resource resource = resourceSet.getResource(uri, true);
				parsedResources.put(uri.devicePath(), resource);
			} catch(Exception e) {
				// e.printStackTrace();
			}
		}
		EcoreUtil2.resolveAll(resourceSet);
		
		// collect imported-resources
		for(Resource resource : resourceSet.getResources()) {
			if(!(resource instanceof LazyLinkingResource)) {
				if(!parsedResources.containsKey(resource.getURI().devicePath()))
					parsedResources.put(resource.getURI().devicePath(), resource);
			}
		}	
		return parsedResources;
	}
	
	/**
	 *  fetch URIs of documents from workspace which are supported by this language-server.
	 */
	public static List<URI> getURIs(ILanguageServerAccess languageServerAccess) {
		try {
			return languageServerAccess.doReadIndex((context) -> getURIList(context)).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Delivers the AstRootElement of the parsed document, specified by the URI, iff it is supported by the
	 * language-server, else null. May not resolve cross-references.
	 * @param languageServerAccess
	 * @param uri the uri to the resource
	 * */
	public static EObject getRootElement(ILanguageServerAccess languageServerAccess, String uri) {
		Resource res = getResource(languageServerAccess, uri);
		return res.getContents().get(0);
	}


	/**
	 * Delivers the resource of the document, specified by the URI, iff it is supported by the
	 * language-server, else null.
	 * @param languageServerAccess
	 * @param uri the device-uri as string to the resource
	 */
	public static Resource getResource(ILanguageServerAccess languageServerAccess, String uri) {
		try {
			ResourceSet resourceSet = (ResourceSet) languageServerAccess.doRead(uri,
					(context) -> context.getResource().getResourceSet()).get();
			EcoreUtil.resolveAll(resourceSet);
			Resource res = (Resource) resourceSet.getResource(URI.createURI(uri), false);
			return res;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Delivers the resource of the document, specified by the URI, iff it is supported by the
	 * language-server, else null.
	 * @param languageServerAccess
	 * @param uri the uri to the resource
	 * @param clazz the class-type the resource has and will be parsed to
	 */
	public static <T> T getResource(ILanguageServerAccess languageServerAccess, String uri, Class<T> clazz) {
		try {
			return (T) languageServerAccess.doRead(uri, (context) -> getResource(context, clazz)).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<URI> getURIList(ILanguageServerAccess.IndexContext context) {
		List<URI> uriList = new ArrayList<>();
		Iterable<IResourceDescription> descriptions = context.getIndex().getAllResourceDescriptions();
		for(IResourceDescription d : descriptions) {
			URI uri = d.getURI();
			uriList.add(uri);
		}
		return uriList;
	}
	
	private static <T> T getResource(Context context, Class<T> clazz) {
		return clazz.cast(
				context.getResource().getContents().get(0)
			);
	}
}
