package info.scce.cinco.product.flowgraph.flowgraph.impl;

import info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagramFactory;
import info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram;
import info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter;
import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import info.scce.pyro.rest.ObjectCache;
import info.scce.pyro.sync.WebSocketMessage;

import java.util.Optional;

/**
 * Author zweihoff
 */
@javax.enterprise.context.RequestScoped
public class FlowGraphDiagramFactoryImpl implements FlowGraphDiagramFactory {

    private entity.core.PyroUserDB subject;
    private FlowGraphDiagramCommandExecuter executer;
    
    public void warmup(
    	         FlowGraphDiagramCommandExecuter executer
    	    ) {
    	        this.subject = executer.getBatch().getUser();
    	        this.executer = executer;
   }

    public static FlowGraphDiagramFactory init() {
    	return new FlowGraphDiagramFactoryImpl();
    }

    public FlowGraphDiagram createFlowGraphDiagram(String projectRelativePath, String filename)
    {
        final entity.flowgraph.FlowGraphDiagramDB newGraph =  new entity.flowgraph.FlowGraphDiagramDB();
        newGraph.filename = filename;
        newGraph.extension = "flowgraph";
        newGraph.scale = 1.0;
        newGraph.connector = "normal";
        newGraph.height = 600L;
        newGraph.width = 2000L;
        newGraph.router = null;
        newGraph.isPublic = false;
        
        //primitive init
        newGraph.modelName = null;
        newGraph.persist();
        
        
        info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram ce = new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(newGraph,executer);
        info.scce.cinco.product.flowgraph.hooks.InitializeFlowGraphModel ca = new info.scce.cinco.product.flowgraph.hooks.InitializeFlowGraphModel();
        ca.init(executer);
        info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram newGraphApi = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) TypeRegistry.getDBToApi(newGraph, executer);
        ca.postCreate(newGraphApi);
        
        return new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(newGraph,executer);
    }

}
