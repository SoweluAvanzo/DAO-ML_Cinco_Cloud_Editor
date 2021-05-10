package info.scce.cinco.product.empty.empty.impl;

import entity.core.PyroFolderDB;
import entity.core.PyroProjectDB;
import info.scce.cinco.product.empty.empty.EmptyFactory;
import info.scce.cinco.product.empty.empty.Empty;
import info.scce.pyro.core.command.EmptyCommandExecuter;
import info.scce.cinco.product.empty.empty.util.TypeRegistry;
import info.scce.pyro.core.rest.types.PyroProjectStructure;
import info.scce.pyro.rest.ObjectCache;
import info.scce.pyro.sync.ProjectWebSocket;
import info.scce.pyro.sync.WebSocketMessage;

import java.util.Optional;

/**
 * Author zweihoff
 */
@javax.enterprise.context.RequestScoped
public class EmptyFactoryImpl implements EmptyFactory {

    private PyroProjectDB project;
    private ProjectWebSocket projectWebSocket;
    private entity.core.PyroUserDB subject;
    private EmptyCommandExecuter executer;
    
    public void warmup(PyroProjectDB project,
    	        ProjectWebSocket projectWebSocket,
    	         entity.core.PyroUserDB subject,
    	         EmptyCommandExecuter executer
    	    ) {
    	        this.project = project;
    	        this.projectWebSocket = projectWebSocket;
    	        this.subject = subject;
    	        this.executer = executer;
   }

    public static EmptyFactory init() {
    	return new EmptyFactoryImpl();
    }

    public Empty createEmpty(String projectRelativePath, String filename)
    {
        String[] folders = projectRelativePath.split("/");
        //create all not present folders
        Object folder = createFolders(project,0,folders);
        //create graphmodel
        final entity.empty.EmptyDB newGraph =  new entity.empty.EmptyDB();
        newGraph.filename = filename;
        newGraph.extension = "empty";
        newGraph.scale = 1.0;
        newGraph.connector = "normal";
        newGraph.height = 600L;
        newGraph.width = 2000L;
        newGraph.router = null;
        newGraph.parent = null;
        newGraph.isPublic = false;
        
        //primitive init
        newGraph.modelName = null;
        newGraph.persist();
        if(folder instanceof PyroProjectDB) {
        	((PyroProjectDB)folder).files_Empty.add(newGraph);
        } else if(folder instanceof PyroFolderDB) {
        	((PyroFolderDB)folder).files_Empty.add(newGraph);
        }
        
        sendProjectUpdate();
        return new info.scce.cinco.product.empty.empty.impl.EmptyImpl(newGraph,executer);
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
}
