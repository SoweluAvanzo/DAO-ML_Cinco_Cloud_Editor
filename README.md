## Develop
1. To build the server run:
 `mvn clean install`

2. The folder containing the exectuable will be in `de.jabc.cinco.meta.core.ide/target/language-server`.

3. If you want to tes the pyro-generator endpoint, go into `de.jabc.cinco.meta.productdefinition.ide` and run the test `CustomEndpointTest`.
The test will use the files located inside `de.jabc.cinco.meta.productdefinition.ide/test-data/test-project`. If the test is successful, the final generated product will be backed-up in `de.jabc.cinco.meta.productdefinition.ide/test-data/pyro`.

# MIGRATION-NOTES
- e.g. `de.jabc.cinco.meta.core.mgl` Validator is not yet fully-functional until `de.jabc.cinco.meta.core.utils` is fully integrated
- `de.jabc.cinco.meta.core.utils` is wip and not yet integrated, until it is independent from the eclipse-framework
	- cleaned away all methods concerning platform-uri (since it is only relevant inside eclipse)
	- introduced IWorkspaceContext as interface for WorkspaceDependent information and methods such as WorkspaceRoot-Path

## Utils-Package
The following packages and classes are either missing an integration or a deprecated,
because they are either too platform-dependent to eclipse, or just not needed anymore.
### SUPPORT ENDED: (since it's uncertain if the following are needed in the future)
- `job`
- `messages`
- `projects`
- `xtext`
- `BuildProperties.java`
- `PluginXMLEditor.xtend`
- `GeneratorHelper.xtend`
- `EclipseFileUtils.java`
- `Activator.java`
### PENDING:
- `mwe2`
- `projects`

