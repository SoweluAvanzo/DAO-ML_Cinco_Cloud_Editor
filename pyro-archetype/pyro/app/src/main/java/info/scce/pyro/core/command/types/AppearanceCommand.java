package info.scce.pyro.core.command.types;


import info.scce.pyro.core.graphmodel.Appearance;

/**
 * Author zweihoff
 */

public class AppearanceCommand extends Command {

    private Appearance appearance;

    @com.fasterxml.jackson.annotation.JsonProperty("appearance")
    public Appearance getAppearance() {
        return appearance;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("appearance")
    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    @Override
    protected void rewrite(long oldId, long newId) {
       
    }
}