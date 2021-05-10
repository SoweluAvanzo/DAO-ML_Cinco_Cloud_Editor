package info.scce.cinco.product.ha.hooksandactions.impl;

import entity.core.PyroFolderDB;
import entity.core.PyroProjectDB;
import info.scce.cinco.product.ha.hooksandactions.HooksAndActionsFactory;
import info.scce.cinco.product.ha.hooksandactions.HooksAndActions;
import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;
import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import info.scce.pyro.core.rest.types.PyroProjectStructure;
import info.scce.pyro.rest.ObjectCache;
import info.scce.pyro.sync.ProjectWebSocket;
import info.scce.pyro.sync.WebSocketMessage;

import java.util.Optional;

/**
 * Author zweihoff
 */
@javax.enterprise.context.RequestScoped
public class HooksAndActionsFactoryImpl implements HooksAndActionsFactory {

    private PyroProjectDB project;
    private ProjectWebSocket projectWebSocket;
    private entity.core.PyroUserDB subject;
    private HooksAndActionsCommandExecuter executer;
    
    public void warmup(PyroProjectDB project,
    	        ProjectWebSocket projectWebSocket,
    	         entity.core.PyroUserDB subject,
    	         HooksAndActionsCommandExecuter executer
    	    ) {
    	        this.project = project;
    	        this.projectWebSocket = projectWebSocket;
    	        this.subject = subject;
    	        this.executer = executer;
   }

    public static HooksAndActionsFactory init() {
    	return new HooksAndActionsFactoryImpl();
    }

    public HooksAndActions createHooksAndActions(String projectRelativePath, String filename)
    {
        String[] folders = projectRelativePath.split("/");
        //create all not present folders
        Object folder = createFolders(project,0,folders);
        //create graphmodel
        final entity.hooksandactions.HooksAndActionsDB newGraph =  new entity.hooksandactions.HooksAndActionsDB();
        newGraph.filename = filename;
        newGraph.extension = "ha";
        newGraph.scale = 1.0;
        newGraph.connector = "normal";
        newGraph.height = 600L;
        newGraph.width = 2000L;
        newGraph.router = null;
        newGraph.parent = null;
        newGraph.isPublic = false;
        
        //primitive init
        newGraph.attribute = null;
        newGraph.persist();
        if(folder instanceof PyroProjectDB) {
        	((PyroProjectDB)folder).files_HooksAndActions.add(newGraph);
        } else if(folder instanceof PyroFolderDB) {
        	((PyroFolderDB)folder).files_HooksAndActions.add(newGraph);
        }
        
        info.scce.cinco.product.ha.hooksandactions.HooksAndActions ce = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(newGraph,executer);
        info.scce.cinco.product.flowgraph.hooks.PostCreate ca = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
        ca.init(executer);
        info.scce.cinco.product.ha.hooksandactions.HooksAndActions newGraphApi = (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) TypeRegistry.getDBToApi(newGraph, executer);
        ca.postCreate(newGraphApi);
        
        sendProjectUpdate();
        return new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(newGraph,executer);
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
	
	public info.scce.cinco.product.ha.hooksandactions.HookAType createHookAType() {
		entity.hooksandactions.HookATypeDB entity = new entity.hooksandactions.HookATypeDB();
		entity.persist();
			info.scce.cinco.product.ha.hooksandactions.HookAType apiEntity = new info.scce.cinco.product.ha.hooksandactions.impl.HookATypeImpl(
			entity,
	    	executer,
	    	null,
	    	null);
	    return apiEntity;
	}
}
