package info.scce.cinco.product.webstory.action
import de.jabc.cinco.meta.runtime.action.CincoCustomAction
import info.scce.cinco.product.webstory.webstory.ModifyVariable

class ToggleModifyVariable extends CincoCustomAction<ModifyVariable> {

	override boolean canExecute(ModifyVariable node) throws ClassCastException {
		true
	}

	override void execute(ModifyVariable node) {
		node.value = !node.value
	}
}
