package info.scce.cinco.product.ml.process;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.cinco.product.ml.process.mlprocess.MLProcess;

public class MLProcessPostCreate extends CincoPostCreateHook<MLProcess>{

	@Override
	public void postCreate(MLProcess object) {
		object.setName(object.getFileName());
	}
}