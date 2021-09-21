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