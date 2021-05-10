package info.scce.cinco.product.hierarchy.hierarchy.impl;

import entity.core.PyroFolderDB;
import entity.core.PyroProjectDB;
import info.scce.cinco.product.hierarchy.hierarchy.HierarchyFactory;
import info.scce.cinco.product.hierarchy.hierarchy.Hierarchy;
import info.scce.pyro.core.command.HierarchyCommandExecuter;
import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import info.scce.pyro.core.rest.types.PyroProjectStructure;
import info.scce.pyro.rest.ObjectCache;
import info.scce.pyro.sync.ProjectWebSocket;
import info.scce.pyro.sync.WebSocketMessage;

import java.util.Optional;

/**
 * Author zweihoff
 */
@javax.enterprise.context.RequestScoped
public class HierarchyFactoryImpl implements HierarchyFactory {

    private PyroProjectDB project;
    private ProjectWebSocket projectWebSocket;
    private entity.core.PyroUserDB subject;
    private HierarchyCommandExecuter executer;
    
    public void warmup(PyroProjectDB project,
    	        ProjectWebSocket projectWebSocket,
    	         entity.core.PyroUserDB subject,
    	         HierarchyCommandExecuter executer
    	    ) {
    	        this.project = project;
    	        this.projectWebSocket = projectWebSocket;
    	        this.subject = subject;
    	        this.executer = executer;
   }

    public static HierarchyFactory init() {
    	return new HierarchyFactoryImpl();
    }

    public Hierarchy createHierarchy(String projectRelativePath, String filename)
    {
        String[] folders = projectRelativePath.split("/");
        //create all not present folders
        Object folder = createFolders(project,0,folders);
        //create graphmodel
        final entity.hierarchy.HierarchyDB newGraph =  new entity.hierarchy.HierarchyDB();
        newGraph.filename = filename;
        newGraph.extension = "hierarchy";
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
        	((PyroProjectDB)folder).files_Hierarchy.add(newGraph);
        } else if(folder instanceof PyroFolderDB) {
        	((PyroFolderDB)folder).files_Hierarchy.add(newGraph);
        }
        
        sendProjectUpdate();
        return new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(newGraph,executer);
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
	
	public info.scce.cinco.product.hierarchy.hierarchy.TA createTA() {
		entity.hierarchy.TADB entity = new entity.hierarchy.TADB();
		entity.persist();
			info.scce.cinco.product.hierarchy.hierarchy.TA apiEntity = new info.scce.cinco.product.hierarchy.hierarchy.impl.TAImpl(
			entity,
	    	executer,
	    	null,
	    	null);
	    return apiEntity;
	}
	
	public info.scce.cinco.product.hierarchy.hierarchy.TD createTD() {
		entity.hierarchy.TDDB entity = new entity.hierarchy.TDDB();
		entity.persist();
			info.scce.cinco.product.hierarchy.hierarchy.TD apiEntity = new info.scce.cinco.product.hierarchy.hierarchy.impl.TDImpl(
			entity,
	    	executer,
	    	null,
	    	null);
	    return apiEntity;
	}
}
