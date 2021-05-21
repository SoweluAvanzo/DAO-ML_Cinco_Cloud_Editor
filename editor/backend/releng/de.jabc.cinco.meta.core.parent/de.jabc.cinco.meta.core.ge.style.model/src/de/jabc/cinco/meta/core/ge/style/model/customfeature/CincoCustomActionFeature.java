package de.jabc.cinco.meta.core.ge.style.model.customfeature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import graphmodel.IdentifiableElement;

public class CincoCustomActionFeature<T extends IdentifiableElement> extends AbstractCustomFeature {

    private CincoCustomAction<T> action;

    public CincoCustomActionFeature(IFeatureProvider fp, CincoCustomAction<T> action) {
        super(fp);
        this.action = action;
    }
    
    @Override
    public String getName() {
    	return action.getName();
    }
    
    @Override
    public boolean hasDoneChanges() {
    	return action.hasDoneChanges();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canExecute(ICustomContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        if (pes.length > 1)
            return false;
        if (pes.length == 0)
            return false;
        Object o = getBusinessObjectForPictogramElement(pes[0]);
        T modelElement = null;
        if (o instanceof IdentifiableElement) try {
            modelElement = (T) o;
            modelElement.getId(); // triggers explicit type cast
        } catch (ClassCastException e) {
            return false;
        }
        return action.canExecute(modelElement);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ICustomContext context) {
        if (canExecute(context)) {
            action.execute((T) getBusinessObjectForPictogramElement(context.getPictogramElements()[0]));
        }
    }

}




