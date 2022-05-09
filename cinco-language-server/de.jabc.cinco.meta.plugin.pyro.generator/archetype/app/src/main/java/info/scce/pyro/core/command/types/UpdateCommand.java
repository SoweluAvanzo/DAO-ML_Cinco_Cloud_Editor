package info.scce.pyro.core.command.types;


import info.scce.pyro.core.graphmodel.IdentifiableElement;

/**
 * Author zweihoff
 */

public class UpdateCommand extends Command {
	
    @com.fasterxml.jackson.annotation.JsonProperty("element")
    IdentifiableElement element;

    public IdentifiableElement getElement() {
        return element;
    }

    public void setElement(IdentifiableElement element) {
        this.element = element;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("prevElement")
    IdentifiableElement prevElement;

    public IdentifiableElement getPrevElement() {
        return prevElement;
    }

    public void setPrevElement(IdentifiableElement prevElement) {
        this.prevElement = prevElement;
    }

    @Override
    protected void rewrite(long oldId, long newId) {
        if(element!=null&&element.getId()==oldId) {
            element.setId(newId);
        }
        if(prevElement!=null&&prevElement.getId()==oldId) {
            prevElement.setId(newId);
        }
    }
}