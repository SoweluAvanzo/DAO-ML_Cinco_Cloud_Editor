package info.scce.rig.checks;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Map;
import java.util.Objects;

import info.scce.rig.mcam.modules.checks.PipelineCheck;
import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.Target;

public class UniqueNamesCheck extends PipelineCheck {

	@Override
	public void check(Pipeline model) {
		checkTargetNamesUnique(model);
	}

	private void checkTargetNamesUnique(Pipeline model) {
		Map<String, Long> targetNamesFrequency = model.getTargets().stream().map((t) -> t.getName())
				.filter(Objects::nonNull).collect(groupingBy(e -> e, counting()));
		for (Target t : model.getTargets()) {
			String name = t.getName();
			if (targetNamesFrequency.get(name) > 1)
				addError(t, Target.class.getSimpleName() + " has an ambiguous name \"" + name + "\"");
		}
	}
}
