package info.scce.rig.hooks;

import static info.scce.rig.graphmodel.controller.UpdateStageIdx.updateStageIdxPerJob;

import java.util.Random;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import graphmodel.ModelElementContainer;
import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.Pipeline;


public class PostCreateJob extends CincoPostCreateHook<Job> {
	
	private static final String[] VERBS = {
			"Build", "Test", "Lint", "Scan", "Analyze", 
			"Benchmark", "Deploy", "Generate", "Upload"
		};
		
	private static final String[] NOUNS = {
		"REST API", "SPA", "Website", "Client", "Desktop", "Server", "Services", 
		"Service", "Documentation", "Backend", "Infrastructure", 
		"Awesomeness", "Internet", "Unicorn"
	};
	
	private static final Random random = new Random();
	
	public static String randomJobName() {
		return String.format("%s %s", 
				VERBS[random.nextInt(VERBS.length)],
				NOUNS[random.nextInt(NOUNS.length)]);
	}
	
	@Override
	public void postCreate(Job job) {
		
		/* Update stage index */
		ModelElementContainer container = job.getContainer();
			Pipeline model = (Pipeline) container;
			updateStageIdxPerJob(model);
		
		job.setName(randomJobName());
	}
}
