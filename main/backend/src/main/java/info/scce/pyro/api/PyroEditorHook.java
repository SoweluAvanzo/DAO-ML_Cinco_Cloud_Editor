package info.scce.pyro.api;

import info.scce.pyro.core.EditorLayoutService;

/**
 * Author zweihoff
 */
public abstract class PyroEditorHook {

    private EditorLayoutService editorLayoutService;

    public final void init(EditorLayoutService editorLayoutService) {
        this.editorLayoutService = editorLayoutService;
    }
    
    protected EditorLayoutService getEditorLayoutService() { return editorLayoutService; }

    public abstract void execute(entity.core.PyroEditorGridDB grid);
}
