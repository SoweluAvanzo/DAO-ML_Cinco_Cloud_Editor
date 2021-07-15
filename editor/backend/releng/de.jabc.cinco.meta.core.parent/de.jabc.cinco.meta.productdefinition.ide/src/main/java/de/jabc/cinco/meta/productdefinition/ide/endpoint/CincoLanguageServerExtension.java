package de.jabc.cinco.meta.productdefinition.ide.endpoint;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.ILanguageServerExtension;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateRequest;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateResponse;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.parser.ParserHelper;
import mgl.MGLModel;
import productDefinition.CincoProduct;
import de.jabc.cinco.meta.core.mgl.MGLRuntimeModule;
import de.jabc.cinco.meta.core.utils.IWorkspaceContext;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;
import de.jabc.cinco.meta.plugin.pyro.CreatePyroPlugin;
import de.jabc.cinco.meta.productdefinition.ide.communication.LogHelper;
import de.jabc.cinco.meta.productdefinition.ide.communication.StaticMessages;

/**
 * This class represents the languageServerExtension, that contains all
 * additional Endpoints.
 * 
 * @author sami
 *
 */
public class CincoLanguageServerExtension implements ILanguageServerExtension, GenerationEndpoint {

	ILanguageServerAccess access;

	@Inject
	protected Provider<XtextResourceSet> resourceSetProvider;

	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;

	@Override
	public void initialize(ILanguageServerAccess access) {
		this.access = access;
	}

	public Injector createInjector() {
		return Guice.createInjector(new MGLRuntimeModule());
	}

	/**
	 * This method gets called, if the language-client sends a request to
	 * "cinco/generate". It returns a ASTResponse, that contains the ASTs of the
	 * specified URIs or, if their are no, of all URIs related to the given
	 * FileExtension.
	 * 
	 * @return
	 */
	@Override
	public CompletableFuture<GenerateResponse> requestGeneration(GenerateRequest request) {
		// notify ide that generation-process starts
		access.getLanguageClient().showMessage(StaticMessages.getGenerateMessage());

		// resolve project URI
		List<WorkspaceFolder> workspaceFolders = this.getWorkspaceFolders(this.access);
		Optional<WorkspaceFolder> optionalTargetFolder = workspaceFolders.stream().filter((w) -> {
			return compare(w.getUri(), request.getTargetURI());
		}).findFirst();
		if (!optionalTargetFolder.isPresent()) {
			String message = "TargetPath is no workspaceFolder!";
			LogHelper.logError(access, message);
			throw new RuntimeException(message);
		}
		WorkspaceFolder targetFolder = optionalTargetFolder.get();
		URI projectURI = getURI(targetFolder.getUri() + "/");
		String projectLocation = projectURI.devicePath();
		IWorkspaceContext.setLocalInstance(new WorkspaceContext(projectURI, null));

		// parse resources
		LogHelper.log(access, "parsing resources...");
		XtextResourceSet resourceSet = resourceSetProvider.get();
		Map<String, Resource> parsedResources = ParserHelper.getAllResources(this.access, resourceSet, projectURI);

		// prepare generator input

		// cpd:
		String cpdPath = request.getSourceURI();
		Optional<CincoProduct> optionalCpd = parsedResources.values().stream()
				.map((Resource r) -> r.getContents().get(0)).filter((EObject e) -> e instanceof CincoProduct)
				.map((m) -> (CincoProduct) m).filter((CincoProduct cpd) -> {
					String resourcePath = cpd.eResource().getURI().devicePath();
					return compare(resourcePath, cpdPath);
				}).findFirst();
		if (!optionalCpd.isPresent()) {
			String message = "CPD could not be resolved!";
			LogHelper.logError(access, message);
			throw new RuntimeException(message);
		}
		CincoProduct cpd = optionalCpd.get();
		IWorkspaceContext.setLocalInstance(new WorkspaceContext(projectURI, cpd.eResource().getResourceSet()));

		// mgls:
		Set<MGLModel> mgls = parsedResources.values().stream().map((Resource r) -> r.getContents().get(0))
				.filter((EObject e) -> e instanceof MGLModel).map((m) -> (MGLModel) m).filter((m) ->
				// only take imported mgls from the set of all mgls in the workspace
				cpd.getMgls().stream().anyMatch((mglDescriptor) -> {
					String relativePath = mglDescriptor.getMglPath();
					URI mglUri = IWorkspaceContext.getLocalInstance().getFileURI(relativePath);
					String importPath = mglUri.devicePath();
					String mglPath = m.eResource().getURI().devicePath();
					return compare(importPath, mglPath);
				})).collect(Collectors.toSet());

		// execute generation
		LogHelper.log(access, "starting pyro-generator...");
		CreatePyroPlugin pyro = new CreatePyroPlugin();
		try {
			pyro.execute(mgls, projectLocation, cpd);
		} catch (Exception e) {
			e.printStackTrace();
			LogHelper.logError(access, "An error occured during generation!");
		}

		// respond information after generation
		GenerateResponse generateResponse = new GenerateResponse();
		URI outputURI = projectURI.appendSegment("pyro.zip");
		String outputPath = outputURI.devicePath();
		generateResponse.setTargetURI(outputPath);
		return CompletableFuture.completedFuture(generateResponse);
	}

	public List<WorkspaceFolder> getWorkspaceFolders(ILanguageServerAccess access) {
		return access.getInitializeParams().getWorkspaceFolders();
	}

	/**
	 * Path-Methods
	 */

	public static boolean compare(String pathA, String pathB) {
		// normalize the two paths and equalize them
		Path a = normalize(pathA);
		Path b = normalize(pathB);
		return a.equals(b);
	}

	public static String cleanPlatform(String uri) {
		return uri.replace("\\", "/").replace("%3A", ":").replace("%5C", "/");
	}

	public static Path normalize(String pathString) {
		// System.out.println("normalizing: "+pathString);
		String cleaned = cleanPlatform(pathString);
		// System.out.println("CLEANED: "+cleaned);
		URI uri = URI.createURI(cleaned);
		// System.out.println("URI-PATH: "+uri.path());
		Path path = Paths.get(uri.path());
		return path;
	}

	public static URI getURI(String path) {
		return URI.createURI(cleanPlatform(path).toString());
	}
}
