package info.scce.pyro.core.export;

import entity.empty.EmptyDB;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class EmptyExporter {
  public EmptyExporter() {
  }
  
  public String getContent(final EmptyDB graph) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("No Content in here");
    _builder.newLine();
    return _builder.toString();
  }
}
