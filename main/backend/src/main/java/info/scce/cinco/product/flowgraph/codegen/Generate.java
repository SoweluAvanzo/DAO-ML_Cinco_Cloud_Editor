package info.scce.cinco.product.flowgraph.codegen;

import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator;
import info.scce.cinco.product.flowgraph.flowgraph.Activity;
import info.scce.cinco.product.flowgraph.flowgraph.FlowGraph;
import java.util.List;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * Example class that generates code for a given FlowGraph model. As different
 *  feature examples might or might not be included (e.g. the external component
 *  library or swimlanes), this generator only does stupidly enumerate all
 *  nodes and prints some general information about them.
 */
@SuppressWarnings("all")
public class Generate extends IGenerator<FlowGraph> {
  @Override
  public void generate(final FlowGraph model) {
    boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(model.getModelName());
    if (_isNullOrEmpty) {
      throw new RuntimeException("Model\'s name must be set.");
    }
    String _modelName = model.getModelName();
    String _plus = ("generated_" + _modelName);
    final String fileName = (_plus + ".test");
    final String code = this.generateCode(model).toString();
    this.createFile(fileName, code);
  }
  
  private CharSequence generateCode(final FlowGraph model) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("=== ");
    String _modelName = model.getModelName();
    _builder.append(_modelName);
    _builder.append(" ===");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("The model contains ");
    int _size = model.getActivitys().size();
    _builder.append(_size);
    _builder.append(" activities. Here\'s some general information about them:");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    {
      List<Activity> _activitys = model.getActivitys();
      for(final Activity node : _activitys) {
        _builder.append("* node ");
        String _id = node.getId();
        _builder.append(_id);
        _builder.append(" of type \'");
        String _string = node.getClass().toString();
        _builder.append(_string);
        _builder.append("\' with ");
        int _size_1 = node.getActivitySuccessors().size();
        _builder.append(_size_1);
        _builder.append(" successors and ");
        int _size_2 = node.getActivityPredecessors().size();
        _builder.append(_size_2);
        _builder.append(" predecessors");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
}
