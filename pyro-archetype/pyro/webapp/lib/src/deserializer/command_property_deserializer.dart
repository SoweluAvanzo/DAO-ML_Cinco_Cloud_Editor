import '../model/command.dart';
class CommandPropertyDeserializer
{
  static Command deserialize(dynamic jsog, Map<String, dynamic> cache)
  {
    switch(jsog['runtimeType']) {
      case 'info.scce.pyro.core.command.types.CreateNodeCommand': return CreateNodeCommand.fromJSOG(jsog,cache);
      case 'info.scce.pyro.core.command.types.MoveNodeCommand': return MoveNodeCommand.fromJSOG(jsog);
      case 'info.scce.pyro.core.command.types.RemoveNodeCommand': return RemoveNodeCommand.fromJSOG(jsog,cache);
      case 'info.scce.pyro.core.command.types.ResizeNodeCommand': return ResizeNodeCommand.fromJSOG(jsog);
      case 'info.scce.pyro.core.command.types.RotateNodeCommand': return RotateNodeCommand.fromJSOG(jsog);

      case 'info.scce.pyro.core.command.types.CreateEdgeCommand': return CreateEdgeCommand.fromJSOG(jsog);
      case 'info.scce.pyro.core.command.types.ReconnectEdgeCommand': return ReconnectEdgeCommand.fromJSOG(jsog);
      case 'info.scce.pyro.core.command.types.RemoveEdgeCommand': return RemoveEdgeCommand.fromJSOG(jsog);
      case 'info.scce.pyro.core.command.types.UpdateBendPointCommand': return UpdateBendPointCommand.fromJSOG(jsog);

      case 'info.scce.pyro.core.command.types.UpdateCommand': return UpdateCommand.fromJSOG(jsog,cache);
      
      case 'info.scce.pyro.core.command.types.AppearanceCommand': return AppearanceCommand.fromJSOG(jsog);

    }
    throw new Exception("Unknown command: ${jsog}");
  }


}
