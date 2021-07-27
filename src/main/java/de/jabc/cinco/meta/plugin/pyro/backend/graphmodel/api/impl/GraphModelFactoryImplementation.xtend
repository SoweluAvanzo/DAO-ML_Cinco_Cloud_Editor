package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.command.GraphModelCommandExecuter
import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel
import mgl.MGLModel
import mgl.UserDefinedType
import mgl.ModelElement

class GraphModelFactoryImplementation extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)
	'''«g.apiFactoryImpl».java'''
	
	def content(GraphModel g) {
		val modelPackage = g.modelPackage as MGLModel
		'''
			package «modelPackage.apiImplFQNBase»;
			
			import entity.core.PyroFolderDB;
			import entity.core.PyroProjectDB;
			import «g.apiFactoryFQN»;
			import «g.apiFQN»;
			import «g.commandExecuterFQN»;
			import «modelPackage.typeRegistryFQN»;
			import info.scce.pyro.core.rest.types.PyroProjectStructure;
			import info.scce.pyro.rest.ObjectCache;
			import info.scce.pyro.sync.ProjectWebSocket;
			import info.scce.pyro.sync.WebSocketMessage;
			
			import java.util.Optional;
			
			/**
			 * Author zweihoff
			 */
			@javax.enterprise.context.RequestScoped
			public class «g.apiFactoryImpl» implements «g.apiFactory» {
			
			    private PyroProjectDB project;
			    private ProjectWebSocket projectWebSocket;
			    private entity.core.PyroUserDB subject;
			    private «g.commandExecuter» executer;
			    
			    public void warmup(PyroProjectDB project,
			    	        ProjectWebSocket projectWebSocket,
			    	         entity.core.PyroUserDB subject,
			    	         «g.commandExecuter» executer
			    	    ) {
			    	        this.project = project;
			    	        this.projectWebSocket = projectWebSocket;
			    	        this.subject = subject;
			    	        this.executer = executer;
			   }
			
			    public static «g.apiFactory» init() {
			    	return new «g.apiFactoryImpl»();
			    }
			
			    public «g.name.fuEscapeJava» create«g.name.fuEscapeJava»(String projectRelativePath, String filename)
			    {
			        String[] folders = projectRelativePath.split("/");
			        //create all not present folders
			        Object folder = createFolders(project,0,folders);
			        //create graphmodel
			        final «g.entityFQN» newGraph =  new «g.entityFQN»();
			        newGraph.filename = filename;
			        newGraph.extension = "«g.fileExtension»";
			        «new GraphModelCommandExecuter(gc).setDefault('''newGraph''', g, true)»
			        newGraph.persist();
			        if(folder instanceof PyroProjectDB) {
			        	((PyroProjectDB)folder).files_«g.name.escapeJava».add(newGraph);
			        } else if(folder instanceof PyroFolderDB) {
			        	((PyroFolderDB)folder).files_«g.name.escapeJava».add(newGraph);
			        }
			        «IF (g as ModelElement).hasPostCreateHook»
			        	
			        	«g.apiFQN» ce = new «g.apiImplFQN»(newGraph,executer);
			        	«(g as ModelElement).postCreateHook» ca = new «(g as ModelElement).postCreateHook»();
			        	ca.init(executer);
			        	«g.apiFQN» newGraphApi = («g.apiFQN») «typeRegistryName».getDBToApi(newGraph, executer);
			        	ca.postCreate(newGraphApi);
			        «ENDIF»
			        
			        sendProjectUpdate();
			        return new «g.apiImplFQN»(newGraph,executer);
			    }
			
			    private PyroFolderDB createFolders(PyroFolderDB parentFolder,int index,String[] folders) {
				    if((folders.length==1&&folders[0].isEmpty())||index>=folders.length){
				        return parentFolder;
				    }
				    String newFolderName = folders[index];
				    if(newFolderName.isEmpty()) {
				        return createFolders(parentFolder,index+1,folders);
				    }
				    Optional<PyroFolderDB> folder = parentFolder.innerFolders.stream().filter(n -> n.name.equals(newFolderName)).findAny();
				    if(folder.isPresent()){
				        //folder is already present
				        //continue
				        return createFolders(folder.get(),index+1,folders);
				    } else {
				        //create new folder
				        final PyroFolderDB newPF = new PyroFolderDB();
				        newPF.name = newFolderName;
				        parentFolder.innerFolders.add(newPF);
				        newPF.persist();
				        //and continue
				        return createFolders(newPF,index+1,folders);
				    }
				}
			    
			    private Object createFolders(PyroProjectDB parentFolder,int index,String[] folders) {
			        if((folders.length==1&&folders[0].isEmpty())||index>=folders.length){
			            return parentFolder;
			        }
			        String newFolderName = folders[index];
			        if(newFolderName.isEmpty()) {
			            return createFolders(parentFolder,index+1,folders);
			        }
			        Optional<PyroFolderDB> folder = parentFolder.innerFolders.stream().filter(n -> n.name.equals(newFolderName)).findAny();
			        if(folder.isPresent()){
			            //folder is already present
			            //continue
			            return createFolders(folder.get(),index+1,folders);
			        } else {
			            //create new folder
			            final PyroFolderDB newPF = new PyroFolderDB();
			            newPF.name = newFolderName;
			            parentFolder.innerFolders.add(newPF);
			            newPF.persist();
			            //and continue
			            return createFolders(newPF,index+1,folders);
			        }
			    }
			
			    private void sendProjectUpdate(){
			        projectWebSocket.send(project.id, WebSocketMessage.fromEntity(-1, PyroProjectStructure.fromEntity(project,new ObjectCache())));
			    }
				«FOR udt:g.elementsAndTypes.filter(UserDefinedType).filter[!isAbstract]»
					
					public «udt.apiFQN» create«udt.name.fuEscapeJava»() {
						«udt.entityFQN» entity = new «udt.entityFQN»();
						entity.persist();
							«udt.apiFQN» apiEntity = new «udt.apiImplFQN»(
							entity,
					    	executer,
					    	null,
					    	null);
					    return apiEntity;
					}
				«ENDFOR»
			}
		'''
	}
	
}
