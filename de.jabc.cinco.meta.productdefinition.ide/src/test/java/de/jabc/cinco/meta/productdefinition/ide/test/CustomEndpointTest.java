package de.jabc.cinco.meta.productdefinition.ide.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import de.jabc.cinco.meta.plugin.pyro.generator.Generator;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.testing.AbstractLanguageServerTest;
import org.eclipse.xtext.util.Files;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.google.inject.Inject;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.CincoLanguageServerExtension;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.messages.GenerateRequest;

class CustomEndpointTest extends AbstractLanguageServerTest {
	
	@Inject
	CincoLanguageServerExtension cincoLse;
	
	public CustomEndpointTest() {
		super("mgl");
	}

	@Test
	void testWorkspaceContext() {
		URI base = URI.createURI(this.root.toURI().toString());
		String baseString = base.toString();
		
		// fallback, since no real workspace is present
		WorkspaceContext.setFallbackURI(base);
		
		WorkspaceContext workspaceContext = new WorkspaceContext(base, null);
		String result = workspaceContext.getRootURI().toString();
		String expected = base.toString();
		Assertions.assertTrue(result.equals(expected));
		
		URI fileURI = workspaceContext.getFileURI(baseString);
		Assertions.assertTrue(fileURI.toString().equals(baseString));

		File baseFile = workspaceContext.getRootFile();
		File baseFile2 = workspaceContext.getFile(workspaceContext.getRootURI());
		Assertions.assertTrue(baseFile.getPath().equals(baseFile2.getPath()));
		
		String testFileName = "Test.cpd";
		URI testFileURI = base.appendSegment("model").appendSegment(testFileName);
		File testFile = workspaceContext.getFile(testFileURI);
		File folder = workspaceContext.getFolder(testFile);
		boolean exists = workspaceContext.fileExists(base);
		boolean testFileExists = workspaceContext.fileExists(testFileURI);
		Assertions.assertTrue(!folder.getPath().equals(baseFile.getPath()));

		Assertions.assertTrue(exists);
		Assertions.assertTrue(testFileExists);
		Assertions.assertTrue(workspaceContext.isContainedInRoot(testFile));
		Assertions.assertTrue(workspaceContext.containsOrIsSame(folder, testFile));
		
		String name = workspaceContext.getRootFolderName();
		Assertions.assertTrue(name.equals("test-project"));
		
		this.initialize();
		Generator.deleteSources = false;
		this.languageServer.request("cinco/generate", new GenerateRequest(testFileURI.devicePath(), baseString));

	}
	
	@After @AfterEach
	@Override
	public void cleanup() {
		String rootPath = this.root.toURI().toString();
		URI root = URI.createURI(rootPath);
		URI pyro = root.appendSegment("pyro");
		while(root.lastSegment().isEmpty()) {
			root = root.trimSegments(1);
		}
		URI pyroBackup = root.trimSegments(1).appendSegment("pyro");
		WorkspaceContext workspaceContextPyro = new WorkspaceContext(pyro, null);
		WorkspaceContext workspaceContextPyroBackup = new WorkspaceContext(pyroBackup, null);
		File pyroFolder = workspaceContextPyro.getRootFile();
		File pyroBackupFolder = workspaceContextPyroBackup.getRootFile();
		
		// cleanup backup
		if(pyroBackupFolder.exists()) {
			try {
				Files.cleanFolder(pyroBackupFolder, null, true, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// copy
		if(pyroFolder.exists()) {
			try {
				FileUtils.copyDirectory(pyroFolder, pyroBackupFolder);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		// cleanup
		if (pyroFolder.exists()) {
			try {
				Files.cleanFolder(pyroFolder, null, true, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
