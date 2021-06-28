package de.jabc.cinco.meta.productdefinition.ide.test;

import java.io.File;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.testing.AbstractLanguageServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.google.inject.Inject;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;
import de.jabc.cinco.meta.productdefinition.ide.endpoint.CincoLanguageServerExtension;

class CustomEndpointTest extends AbstractLanguageServerTest {
	
	@Inject
	CincoLanguageServerExtension cincoLse;
	
	public CustomEndpointTest() {
		super("mgl");
	}

	@Test
	void testWorkspaceContext() {
		URI base = URI.createURI(this.root.toURI().toString());
		base = base.trimSegments(1).appendSegment("resources");
		String baseString = base.toString();
		
		WorkspaceContext workspaceContext = new WorkspaceContext(base, null);
		String result = workspaceContext.getRootURI().toString();
		String expected = base.toString();
		Assertions.assertTrue(result.equals(expected));
		
		URI fileURI = workspaceContext.getFileURI(baseString);
		Assertions.assertTrue(fileURI.toString().equals(baseString));

		File baseFile = workspaceContext.getRootFile();
		File baseFile2 = workspaceContext.getFile(workspaceContext.getRootURI());
		Assertions.assertTrue(baseFile.getPath().equals(baseFile2.getPath()));
		
		String testFileName = "FlowGraph.mgl";
		URI testFileURI = base.appendSegment(testFileName);
		File testFile = workspaceContext.getFile(testFileURI);
		File folder = workspaceContext.getFolder(testFile);
		boolean exists = workspaceContext.fileExists(base);
		boolean testFileExists = workspaceContext.fileExists(testFileURI);
		Assertions.assertTrue(folder.getPath().equals(baseFile.getPath()));

		Assertions.assertTrue(exists);
		Assertions.assertTrue(testFileExists);
		Assertions.assertTrue(workspaceContext.isContainedInRoot(testFile));
		Assertions.assertTrue(workspaceContext.containsOrIsSame(folder, testFile));
		
		String name = workspaceContext.getRootFolderName();
		Assertions.assertTrue(name.equals("resources"));
	}
}