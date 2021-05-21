package de.jabc.cinco.meta.productdefinition.ide.endpoint.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.ILanguageServerAccess.Context;
import org.eclipse.xtext.resource.IResourceDescription;

public class ParserHelper {

	/**
	 * Contains all resources that are supported by this language-server, but
	 * not the files that are just resources, e.g. text-files and images.
	 *  
	 * @param languageServerAccess
	 * @return
	 */
	public static Map<String, EObject> getAllResources(ILanguageServerAccess languageServerAccess) {
		List<URI> uris = getURIs(languageServerAccess);
		Map<String, EObject> parsedResources = new HashMap<>();
		for(URI uri : uris) {
			try {
				String path = uri.devicePath();
				EObject root = getRootElement(languageServerAccess, path);
				parsedResources.put(path, root);
			} catch(Exception e) {
				e.printStackTrace();
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
	 * language-server, else null.
	 */
	public static EObject getRootElement(ILanguageServerAccess languageServerAccess, String uri) {
		return getResource(languageServerAccess, uri).getContents().get(0);
	}


	/**
	 * Delivers the resource of the document, specified by the URI, iff it is supported by the
	 * language-server, else null.
	 */
	public static Resource getResource(ILanguageServerAccess languageServerAccess, String uri) {
		try {
			return (Resource) languageServerAccess.doRead(uri, (context) -> getResource(context)).get();
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
	
	private static Resource getResource(Context context) {
		return context.getResource();
	}
}
