package de.jabc.cinco.meta.core.utils;

import java.io.File;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

public class PathValidator {

	/**
	 * Returns whether the provided <code>path</code> is relative or not.
	 * <br/>
	 * A path is considered relative if it uses no scheme (e.g.: "file:", "platform:" or "plugin:")
	 * 
	 * @param path	the {@link String} to check whether it represents a relative path
	 * @return	<code>true</code> if the provided <code>path</code> is relative, <code>false</code>
	 * 			otherwise
	 */
	public static boolean isRelativePath(String path) {
		URI uri = URI.createURI(path, true);
		return uri.isRelative();
	}
	
	/**
	 * Checks whether the provided <code>path</code> leads to a resource, file, or folder.
	 * <br/>
	 * If the <code>path</code> is valid an empty {@link String} is returned, if not an error
	 * message is returned.
	 * <br/>
	 * The provided <code>object</code> is used as a helping {@link EObject} that helps to retrieve
	 * the resource of the provided <code>path</code> if it cannot be found right away.
	 * In this case a lookup in <code>object</code>'s project will be made.
	 * 
	 * @param object	a helping {@link EObject} to retrieve a project in which the resource
	 * 					referenced by <code>path</code> should be found in
	 * @param path		a {@link String} to check whether it points to a valid resource, file,
	 * 					or folder
	 * @return a {@link Boolean} that is true if the <code>path</code> is valid, a false if
	 * 			<code>path</code> is invalid
	 */
	public static boolean checkPath(EObject object, String path) {
		File file = IWorkspaceContext.getLocalInstance().getFile(path);
		if(!file.exists()) {
			//check for folder
			File folder = getFolder(file.getAbsolutePath());
			if(!folder.exists()) {
				return false;
			}
			return true;
		}
		return true;
	}
	
	/**
	 * Returns an {@link URI} for the provided <code>path</code> and returns it if a resource, file
	 * or folder exists at the <code>path</code>.
	 * <br/>
	 * The provided <code>object</code> is used as a helping {@link EObject} that helps to retrieve
	 * the resource of the provided <code>path</code> if it cannot be found right away.
	 * In this case a lookup in <code>object</code>'s project will be made.
	 * 
	 * @param path	a {@link String} that points to a file, resource or folder for which an 
	 * 				{@link URI} should be returned
	 * @return the {@link URI} of the file, resource or folder present at the provided
	 * 			<code>path</code>, returns <code>null</code> if the <code>path</code> is invalid or
	 * 			if no file, resource or folder is present under the provided <code>path</code>
	 * @see #getURLForString(EObject, String)
	 */
	public static URI getURIForString(EObject object, String path) {
		File resource = IWorkspaceContext.getLocalInstance().getFile(path);
		if(!resource.exists()) {
			//try folder
			resource = getFolder(resource.getAbsolutePath());
		}
		return URI.createFileURI(resource.getAbsolutePath());
	}
	
	/**
	 * Returns the id of the bundle the <code>uri</code>'s target is located in.
	 * 
	 * @param uri the {@link URI} for which the bundle ID should be returned
	 * @return	a {@link String} that represents the ID of the bundle {@link URI}'s target is
	 * 			located in
	 * @throws RuntimeException if the provided </code>uri</code> is not pointing to a platform
	 * 							resource, file or folder
	 */
	@Deprecated
	public static String getBundleID(URI uri) {
		URI trimmed = null;
		if (uri.isPlatformPlugin() || uri.isPlatformResource()) {
			trimmed = URI.createURI(uri.toPlatformString(true));
		}
		if (uri.isRelative()) {
			trimmed = uri;
		}
		if (uri.isPlatform()){
			trimmed = uri.deresolve(URI.createURI("platform:/"));
		}
		if (trimmed != null)
			return trimmed.segment(0);
		throw new RuntimeException("The uri: \"" + uri +"\" could not be recognized");
	}
	
	public static boolean checkSameProjects(String relativePath) {
		URI uri = IWorkspaceContext.getLocalInstance().getFileURI(relativePath);
		return IWorkspaceContext.getLocalInstance().isContainedInRoot(uri);
	}
	
	@Deprecated
	private static String checkPlatformResourceURI(URI uri) {
		throw new RuntimeException("Plattform resource is not supported.");
	}
	
	private static boolean checkRelativePath(String relativePath) {
        File file = IWorkspaceContext.getLocalInstance().getFile(relativePath);
        return file.exists();
	}
	
	private static boolean checkFileExists(File file) {
		return file.exists();
	}
	
	private static boolean checkFolderExists(File folder) {
		return folder.exists();
	}
	
	private static File getFolder(String absolutePath) {
        return IWorkspaceContext.getLocalInstance().getFolder(absolutePath);
	}
}
