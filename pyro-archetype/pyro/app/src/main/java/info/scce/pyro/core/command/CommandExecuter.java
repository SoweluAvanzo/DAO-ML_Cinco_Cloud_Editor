package info.scce.pyro.core.command;
import entity.core.*;

import style.Appearance;
import style.BooleanEnum;
import style.LineStyle;
import graphmodel.*;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.core.command.types.HighlightCommand;
import info.scce.pyro.core.command.types.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author zweihoff
 */

abstract public class CommandExecuter {
	
	@javax.inject.Inject
	info.scce.pyro.core.FileController fileController;
	
    protected final GraphModelWebSocket graphModelWebSocket;
    protected BatchExecution batch;
    protected List<HighlightCommand> highlightings;
    protected OpenFileCommand openFileCommand;
    
    public void openFile(io.quarkus.hibernate.orm.panache.PanacheEntity file) {
        openFileCommand = new OpenFileCommand();
        openFileCommand.setId(file.id);
    }
    
    public OpenFileCommand getOpenFileCommand() {
        return openFileCommand;
    }
    
    public List<HighlightCommand> getHighlightings() {
        return highlightings;
    }

    public void setHighlightings(List<HighlightCommand> highlightings) {
        this.highlightings = highlightings;
    }

    CommandExecuter(GraphModelWebSocket graphModelWebSocket,List<HighlightCommand> highlightings) {
        this.graphModelWebSocket = graphModelWebSocket;
        this.highlightings = highlightings;
    }
    
    public GraphModelWebSocket getGraphModelWebSocket() {
        return graphModelWebSocket;
    }

    protected void createNode(String type,Node node, ModelElementContainer modelElementContainer, String containerType, long x, long y,long width, long height,info.scce.pyro.core.graphmodel.Node prev){
    	createNode(type, node, modelElementContainer, containerType, x, y,width, height,null,prev);
    }

    protected void createNode(String type, Node node, ModelElementContainer modelElementContainer, String containerType, long x, long y, long width, long height, info.scce.pyro.core.graphmodel.PyroElement primeElement, info.scce.pyro.core.graphmodel.Node prev){

        info.scce.pyro.core.command.types.CreateNodeCommand cmd = new CreateNodeCommand();
        cmd.setDelegateId(node.getDelegateId());
        cmd.setContainerId(modelElementContainer.getDelegateId());
        cmd.setContainerType(containerType);
        cmd.setWidth(width);
        cmd.setHeight(height);
        cmd.setX(x);
        cmd.setY(y);
        cmd.setType(type);
        if(primeElement!=null){
            cmd.setPrimeId(primeElement.getId());
            cmd.setPrimeElement(primeElement);
        }
        cmd.setElement(prev);
        batch.add(cmd);

    }

    public void moveNode(String type, Node node, ModelElementContainer modelElementContainer, String containerType, String oldContainerType, long x, long y){
        info.scce.pyro.core.command.types.MoveNodeCommand cmd = new MoveNodeCommand();
        cmd.setDelegateId(node.getDelegateId());
        cmd.setType(type);
        cmd.setContainerId(modelElementContainer.getDelegateId());
        cmd.setContainerType(containerType);
        cmd.setX(x);
        cmd.setY(y);
        cmd.setOldContainerId(node.getContainer().getDelegateId());
        cmd.setOldContainerType(oldContainerType);
        cmd.setOldX(node.getX());
        cmd.setOldY(node.getY());
        batch.add(cmd);
    }

    public void resizeNode(String type,Node node, long width, long height){
        info.scce.pyro.core.command.types.ResizeNodeCommand cmd = new ResizeNodeCommand();
        cmd.setDelegateId(node.getDelegateId());
        cmd.setOldHeight(node.getHeight());
        cmd.setOldWidth(node.getWidth());
        cmd.setWidth(width);
        cmd.setHeight(height);
        cmd.setType(type);
        batch.add(cmd);
    }

    protected void removeNode(String type, Node node, String containerType, info.scce.pyro.core.graphmodel.PyroElement primeNode,info.scce.pyro.core.graphmodel.Node prev){
        info.scce.pyro.core.command.types.RemoveNodeCommand cmd = new RemoveNodeCommand();
        cmd.setDelegateId(node.getDelegateId());
        cmd.setContainerId(node.getContainer().getDelegateId());
        cmd.setContainerType(containerType);
        cmd.setWidth(node.getWidth());
        cmd.setHeight(node.getHeight());
        cmd.setX(node.getX());
        cmd.setY(node.getY());
        cmd.setType(type);
        if(primeNode != null){
            cmd.setPrimeId(primeNode.getId());
            cmd.setPrimeElement(primeNode);
        }
        cmd.setElement(prev);

        //remove highlighting
        Optional<HighlightCommand> he = getHighlightings().stream().filter(n -> n.getId() == node.getDelegateId()).findAny();
        if(he.isPresent()) {
            getHighlightings().remove(he.get());
        }
        
        batch.add(cmd);
    }

    protected GraphModel getRootModel(IdentifiableElement modelElement){
        if(modelElement instanceof GraphModel){
            return (GraphModel) modelElement;
        }
        if(modelElement instanceof ModelElement){
            if((((ModelElement) modelElement).getContainer())!=null){
                return getRootModel((((ModelElement) modelElement).getContainer()));
            }
        }
        return null;
    }

    protected void createEdge(String type,Edge edge, Node source, String sourceType, Node target, String targetType, Collection<BendingPointDB> positions, info.scce.pyro.core.graphmodel.Edge prev){
        info.scce.pyro.core.command.types.CreateEdgeCommand cmd = new CreateEdgeCommand();
        cmd.setDelegateId(edge.getDelegateId());
        cmd.setType(type);
        cmd.setSourceId(source.getDelegateId());
        cmd.setTargetId(target.getDelegateId());
        cmd.setSourceType(sourceType);
        cmd.setTargetType(targetType);
        cmd.setPositions(positions.stream().map(info.scce.pyro.core.graphmodel.BendingPoint::fromEntity).collect(Collectors.toList()));
        cmd.setElement(prev);
        batch.add(cmd);
    }
    
    // NOTE: changed
    public void reconnectEdge(String type,Edge edge, Node source, Node target, String sourceType, String targetType, String oldSourceType, String oldTargetType){
        info.scce.pyro.core.command.types.ReconnectEdgeCommand cmd = new ReconnectEdgeCommand();
        cmd.setDelegateId(edge.getDelegateId());
        cmd.setType(type);
        cmd.setOldSourceType(oldSourceType);
        cmd.setOldTargetType(oldTargetType);
        cmd.setOldSourceId(edge.getSourceElement().getDelegateId());
        cmd.setOldTargetId(edge.getTargetElement().getDelegateId());
        cmd.setSourceType(sourceType);
        cmd.setTargetType(targetType);
        cmd.setSourceId(source.getDelegateId());
        cmd.setTargetId(target.getDelegateId());
        batch.add(cmd);
    }

    protected void updateBendingPoints(String type, Edge edge, List<info.scce.pyro.core.graphmodel.BendingPoint> points){
    	info.scce.pyro.core.command.types.UpdateBendPointCommand cmd = new UpdateBendPointCommand();
        cmd.setDelegateId(edge.getDelegateId());
        cmd.setType(type);
        cmd.setOldPositions(edge.getBendingPoints().stream()
        		.map(n->info.scce.pyro.core.graphmodel.BendingPoint.fromEntity((entity.core.BendingPointDB) n))
        		.collect(Collectors.toList()));
        cmd.setPositions(points);
        batch.add(cmd);
        
        List<BendingPointDB> cpPoints = edge.getBendingPoints().stream()
	        			.map(entity.core.BendingPointDB.class::cast)
	        			.collect(Collectors.toList());
        edge.clearBendingPoints();
        cpPoints.forEach(b->b.delete());
        points.forEach(b->{
            edge.addBendingPoint(b.getx(), b.gety());
        });
    }

    protected void removeEdge(String type, Edge edge, info.scce.pyro.core.graphmodel.Edge prev, String sourceType, String targetType){
        info.scce.pyro.core.command.types.RemoveEdgeCommand cmd = new RemoveEdgeCommand();
        cmd.setDelegateId(edge.getDelegateId());
        cmd.setSourceId(edge.getSourceElement().getDelegateId());
        cmd.setTargetId(edge.getTargetElement().getDelegateId());
        cmd.setSourceType(sourceType);
        cmd.setTargetType(targetType);
        cmd.setPositions(edge.getBendingPoints().stream()
    			.map(entity.core.BendingPointDB.class::cast)
        		.map(info.scce.pyro.core.graphmodel.BendingPoint::fromEntity)
        		.collect(Collectors.toList()));
        cmd.setType(type);
        cmd.setElement(prev);
        batch.add(cmd);

        //remove highlighting
        Optional<HighlightCommand> he = getHighlightings().stream().filter(n -> n.getId() == edge.getDelegateId()).findAny();
        if(he.isPresent()) {
            getHighlightings().remove(he.get());
        }

        List<BendingPointDB> points = edge.getBendingPoints().stream()
    			.map(entity.core.BendingPointDB.class::cast)
    			.collect(Collectors.toList());
        edge.clearBendingPoints();
        points.forEach((b)->b.delete());
    }
    
    protected void updatePropertiesReNew(String type, info.scce.pyro.core.graphmodel.IdentifiableElement element,info.scce.pyro.core.graphmodel.IdentifiableElement prevElement) {
        Optional<UpdateCommand> uc = batch
                .getCommands()
                .stream()
                .filter(n->n instanceof UpdateCommand)
                .map(n->(UpdateCommand)n)
                .filter(n->n.getElement().getId()==element.getId())
                .findFirst();
        if(uc.isPresent()){
            batch.getCommands().remove(uc.get());
        }
        updateProperties(type,element,prevElement);
    }
    
    protected void updateProperties(String type, info.scce.pyro.core.graphmodel.IdentifiableElement element,info.scce.pyro.core.graphmodel.IdentifiableElement prevElement){
        info.scce.pyro.core.command.types.UpdateCommand cmd = new UpdateCommand();
        cmd.setDelegateId(element.getId());
        cmd.setType(type);
        cmd.setElement(element);
        cmd.setPrevElement(prevElement);
        batch.add(cmd);
    }

    protected Appearance mergeAppearance(Appearance defaultAppearance, Appearance calculated){
        if(calculated.getForeground()!=null){
            defaultAppearance.setForeground(calculated.getForeground());
        }
        if(calculated.getBackground()!=null){
            defaultAppearance.setBackground(calculated.getBackground());
        }
        if(calculated.getLineStyle()!= LineStyle.UNSPECIFIED) {
            defaultAppearance.setLineStyle(calculated.getLineStyle());
        }
        if(calculated.getLineWidth()!= -1) {
            defaultAppearance.setLineWidth(calculated.getLineWidth());
        }
        if(calculated.getLineInVisible()!= null) {
            defaultAppearance.setLineInVisible(calculated.getLineInVisible());
        }
        if(calculated.getTransparency()!= -1.0) {
            defaultAppearance.setTransparency(calculated.getTransparency());
        }
        if(calculated.getAngle()!= -1.0F) {
            defaultAppearance.setAngle(calculated.getAngle());
        }
        if(calculated.getFont()!= null) {
            if(calculated.getFont().getFontName()!=null) {
                defaultAppearance.getFont().setFontName(calculated.getFont().getFontName());
            }
            if(calculated.getFont().getSize()>0) {
                defaultAppearance.getFont().setSize(calculated.getFont().getSize());
            }
            defaultAppearance.getFont().setIsBold(calculated.getFont().isIsBold());
            defaultAppearance.getFont().setIsItalic(calculated.getFont().isIsItalic());
        }
        if(calculated.getFilled()!= BooleanEnum.UNDEF) {
            defaultAppearance.setFilled(calculated.getFilled());
        }
        if(calculated.getImagePath()!= null) {
            defaultAppearance.setImagePath(calculated.getImagePath());
        }
        return defaultAppearance;
        
    }

    protected void updateAppearance(String type, graphmodel.IdentifiableElement element, Appearance appearance){
        final AppearanceCommand result = new AppearanceCommand();
        result.setType(type);
        result.setDelegateId(element.getDelegateId());
        result.setAppearance(info.scce.pyro.core.graphmodel.Appearance.fromAppearance(appearance));
        batch.add(result);
    }

    public BatchExecution getBatch(){
        return batch;
    }

    public List<ModelElement> getAllModelElements() {
        return getAllModelElements(getBatch().getGraphModel());
    }

    private List<ModelElement> getAllModelElements(ModelElementContainer mec) {
        List<ModelElement> result = new LinkedList<>();
        result.addAll(mec.getModelElements());
        mec.getModelElements().stream().filter(n->n instanceof ModelElementContainer).forEach(n->result.addAll(getAllModelElements((ModelElementContainer) n)));
        return result;
    }

    public void updateAppearance() {}
    
    public java.io.InputStream loadFile(final entity.core.BaseFileDB identifier) {
    	return fileController.loadFile(identifier);
    }
}