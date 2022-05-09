// package info.scce.rig.generator;

// import java.io.IOException;
// import java.io.PrintWriter;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.SortedSet;
// import java.util.TreeSet;
// import java.util.function.Predicate;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// import org.eclipse.core.runtime.IPath;
// import org.eclipse.core.runtime.IProgressMonitor;

// import com.fasterxml.jackson.annotation.JsonInclude;
// import com.fasterxml.jackson.annotation.JsonInclude.Include;
// import com.fasterxml.jackson.core.JsonGenerator;
// import com.fasterxml.jackson.core.JsonParser;
// import com.fasterxml.jackson.databind.DeserializationFeature;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.databind.json.JsonMapper;
// import com.fasterxml.jackson.databind.node.ArrayNode;
// import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
// import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
// import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

// import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator;
// import info.scce.rig.pipeline.Alias;
// import info.scce.rig.pipeline.AllowFailure;
// import info.scce.rig.pipeline.Artifact;
// import info.scce.rig.pipeline.AutoStopIn;
// import info.scce.rig.pipeline.Cache;
// import info.scce.rig.pipeline.Change;
// import info.scce.rig.pipeline.Command;
// import info.scce.rig.pipeline.Coverage;
// import info.scce.rig.pipeline.Description;
// import info.scce.rig.pipeline.EntryPoint;
// import info.scce.rig.pipeline.EnvAction;
// import info.scce.rig.pipeline.EnvVariable;
// import info.scce.rig.pipeline.Environment;
// import info.scce.rig.pipeline.Except;
// import info.scce.rig.pipeline.Exclude;
// import info.scce.rig.pipeline.Exists;
// import info.scce.rig.pipeline.ExpireIn;
// import info.scce.rig.pipeline.ExposeAs;
// import info.scce.rig.pipeline.File;
// import info.scce.rig.pipeline.Image;
// import info.scce.rig.pipeline.Interruptible;
// import info.scce.rig.pipeline.Job;
// import info.scce.rig.pipeline.Key;
// import info.scce.rig.pipeline.Kubernetes;
// import info.scce.rig.pipeline.Milestone;
// import info.scce.rig.pipeline.Name;
// import info.scce.rig.pipeline.Only;
// import info.scce.rig.pipeline.Parallel;
// import info.scce.rig.pipeline.Path;
// import info.scce.rig.pipeline.Pipeline;
// import info.scce.rig.pipeline.Policy;
// import info.scce.rig.pipeline.Prefix;
// import info.scce.rig.pipeline.RawRef;
// import info.scce.rig.pipeline.Ref;
// import info.scce.rig.pipeline.Release;
// import info.scce.rig.pipeline.ReleasedAt;
// import info.scce.rig.pipeline.Report;
// import info.scce.rig.pipeline.ResourceGroup;
// import info.scce.rig.pipeline.Retry;
// import info.scce.rig.pipeline.RetryWhen;
// import info.scce.rig.pipeline.Rule;
// import info.scce.rig.pipeline.Service;
// import info.scce.rig.pipeline.SimpleExcept;
// import info.scce.rig.pipeline.SimpleImage;
// import info.scce.rig.pipeline.SimpleOnly;
// import info.scce.rig.pipeline.SimpleRetry;
// import info.scce.rig.pipeline.SimpleService;
// import info.scce.rig.pipeline.StringParameter;
// import info.scce.rig.pipeline.Tag;
// import info.scce.rig.pipeline.TagName;
// import info.scce.rig.pipeline.Target;
// import info.scce.rig.pipeline.Timeout;
// import info.scce.rig.pipeline.URL;
// import info.scce.rig.pipeline.Untracked;
// import info.scce.rig.pipeline.VarExp;
// import info.scce.rig.pipeline.When;


// public class GitLabCIGenerator implements IGenerator<Pipeline> {

// 	private static final String GITLAB_CI_FILENAME = "_.gitlab-ci.yml";
	
// 	private ObjectMapper mapper = JsonMapper.builder(
// 				YAMLFactory.builder()
// 					.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
// 					.build())
// 			.addModule(new Jdk8Module())
// 			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
// 			.serializationInclusion(JsonInclude.Include.NON_EMPTY)
// 			.serializationInclusion(Include.NON_ABSENT)
// 			.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
// 			.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
// 			.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
// 			.build();
	
// 	private boolean stageless = false;
	
// 	@Override
// 	public void generate(Pipeline model, IPath path, IProgressMonitor monitor) {
// 		IPath targetFile = path.append(GITLAB_CI_FILENAME);
		
// 		this.stageless = model.isStageless();
		
// 		try (PrintWriter writer = new PrintWriter(targetFile.toFile())) {
// 			// write file header
// 			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
// 			LocalDateTime now = LocalDateTime.now();  
// 			writer.println("# AUTO GENERATED BY RIG ON " + format.format(now));
			
// 			ObjectNode pipeline = mapper.createObjectNode();
			
// 			Map<String, String> variables = model.getEnvironmentVariable().stream()
// 					.collect(Collectors.toMap(var -> var.getName().toUpperCase(), EnvVariable::getValue));
// 			pipeline.set("variables", mapper.valueToTree(variables));
			
// 			SortedSet<String> stages = new TreeSet<>();
// 			for (Target target : model.getTargets()) {
// 				for (Job job : target.getJobPredecessors()) {
// 					process(job, target, pipeline, stages);
// 				}
// 			}
			
// 			if (!stageless)
// 				pipeline.set("stages", mapper.valueToTree(stages));

// 			mapper.writeValue(writer, pipeline);

// 		} catch (IOException e) {
// 			e.printStackTrace();
// 		}
// 	}
	
// 	private String generateJobName(Job job, Target target) {
// 		return job.getName().equals("pages") ? job.getName() :
// 			String.format("%s@%s", job.getName(), target.getName());			
// 	}

// 	private void process(Job job, Target target, ObjectNode pipeline, SortedSet<String> stages) {
// 		for (Job before : job.getJobPredecessors())
// 			process(before, target, pipeline, stages);

// 		pipeline.set(generateJobName(job, target),
// 				generateJob(job, target));
// 		stages.add(String.format("stage-%s", job.getStageIdx()));
// 	}

// 	private JsonNode generateJob(Job job, Target target) {
// 		ObjectNode node = mapper.createObjectNode();

// 		if (!stageless)
// 			node.put("stage", String.format("stage-%s", job.getStageIdx()));

// 		if (job.getScriptArguments().size() > 0) {
// 			node.set("variables", getScriptArguments(job, target));
// 		}

// 		readScript(node, "before_script", job.getBefore_script());
// 		JsonNode script = mapper.valueToTree(listScript(job.getScript()));
// 		node.set("script", script);
// 		readScript(node, "after_script", job.getAfter_script());

// 		PropertyMapper<Job> factory = new PropertyMapper<>(job, target);

// 		// TODO
// //		, SimpleOnly     [0, *]
// //		, SimpleExcept   [0, *]
// //		, ScriptArgument [0, *]

// 		factory.booleanValue(AllowFailure.class)
// 			.ifPresent(value -> node.put("allow_failure", value));
// 		factory.stringValue(Coverage.class)
// 			.ifPresent(value -> node.put("coverage", value));
// 		factory.booleanValue(Interruptible.class)
// 			.ifPresent(value -> node.put("interruptible", value));
// 		factory.intValue(Parallel.class)
// 			.ifPresent(value -> node.put("parallel", value));
// 		factory.stringValue(ResourceGroup.class)
// 			.ifPresent(value -> node.put("resource_group", value));
// 		factory.intValue(SimpleRetry.class)
// 			.ifPresent(value -> node.put("retry", value));
// 		List<String> tags = factory.stringValues(Tag.class);
// 		if (!tags.isEmpty())
// 			node.set("tags", mapper.valueToTree(tags));
// 		factory.stringValue(Timeout.class)
// 			.ifPresent(value -> node.put("timeout", value));
// 		factory.value(When.class, String.class, when -> when.getValue().toString())
// 			.ifPresent(value -> node.put("when", value));

// //		, ExceptOnlyAssignment  [0, *]
// //		, RuleAssignment        [0, *]

// 		// should only be one (per grammar)
// 		job.getArtifactPredecessors().forEach(subject ->
// 			node.set("artifacts", generateArtifact(subject, target)));

// 		// should only be one (per grammar)
// 		job.getCachePredecessors().forEach(subject ->
// 			node.set("cache", generateCache(subject, target)));

// 		// should only be one (per grammar)
// 		job.getReleasePredecessors().forEach(subject ->
// 			node.set("release", generateRelease(subject, target)));

// 		// should only be one (per grammar)
// 		job.getEnvironmentPredecessors().forEach(subject ->
// 			node.set("environment", generateEnvironment(subject, target)));

// 		// should only be one (per grammar)
// 		job.getRetryPredecessors().forEach(subject ->
// 			node.set("retry", generateRetry(subject, target)));


// 		ArrayNode services = generateServices(factory, job, target);
// 		if (services.size() > 0)
// 			node.set("services", services);

// 		generateImages(factory.stringValue(SimpleImage.class), job, target)
// 			.ifPresent(value -> node.set("image", value));

// 		JsonNode except = generateExcept(factory, job, target);
// 		if (!except.isEmpty())
// 			node.set("except", except);

// 		JsonNode only = generateOnly(factory, job, target);
// 		if (!only.isEmpty())
// 			node.set("only", only);

// 		JsonNode rules = generateRules(job, target);
// 		if (!rules.isEmpty())
// 			node.set("rules", rules);

// 		List<String> needs = job.getJobPredecessors().stream()
// 			.map(predecessor -> String.format("%s@%s", predecessor.getName(), target.getName()))
// 			.collect(Collectors.toList());

// 		if (!needs.isEmpty())
// 			node.set("needs", mapper.valueToTree(needs));

// 		return node;
// 	}

// 	private JsonNode generateArtifact(Artifact subject, Target target) {
// 		PropertyMapper<Artifact> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.stringValue(Name.class)
// 			.ifPresent(value -> node.put("name", value));
// 		factory.stringValue(ExposeAs.class)
// 			.ifPresent(value -> node.put("expose_as", value));
// 		factory.stringValue(ExpireIn.class)
// 		.ifPresent(value -> node.put("expire_in", value));

// 		List<String> paths = factory.stringValues(Path.class);
// 		if (!paths.isEmpty())
// 			node.set("paths", mapper.valueToTree(paths));

// 		List<String> exclude = factory.stringValues(Exclude.class);
// 		if (!exclude.isEmpty())
// 			node.set("exclude", mapper.valueToTree(exclude));

// 		factory.value(When.class, String.class,
// 				when -> when.getValue().toString())
// 			.ifPresent(value -> node.put("when", value));

// 		if (!subject.getReports().isEmpty()) {
// 			ObjectNode reportNode = mapper.createObjectNode();
// 			subject.getReports().stream()
// 			.collect(Collectors.groupingBy(
// 				report -> report.getKind(),
// 				Collectors.mapping(Report::getValue, Collectors.toList())))
// 			.forEach((key, val) -> reportNode.set(key.toString(), mapper.valueToTree(val)));
// 			node.set("reports", reportNode);
// 		}

// 		factory.booleanValue(Untracked.class)
// 			.ifPresent(value -> node.put("untracked", value));
// 		return node;
// 	}

// 	private JsonNode generateCache(Cache subject, Target target) {
// 		PropertyMapper<Cache> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.stringValue(Key.class)
// 			.ifPresent(value -> node.put("key", value));

// 		// cache:key:[files,prefix]
// 		if (subject.getKeys().isEmpty()) {
// 			ObjectNode key = mapper.createObjectNode();

// 			factory.stringValue(Prefix.class)
// 			.ifPresent(value -> key.put("prefix", value));

// 			List<String> files = factory.stringValues(File.class);
// 			if (!files.isEmpty())
// 				key.set("files", mapper.valueToTree(files));

// 			if (key.elements().hasNext())
// 				node.set("key", key);
// 		}

// 		List<String> paths = factory.stringValues(Path.class);
// 		if (!paths.isEmpty())
// 			node.set("paths", mapper.valueToTree(paths));

// 		factory.booleanValue(Untracked.class)
// 			.ifPresent(value -> node.put("untracked", value));

// 		factory.value(When.class, String.class, when -> when.getValue().toString())
// 			.ifPresent(value -> node.put("when", value));

// 		factory.value(Policy.class, String.class, policy -> policy.getValue().toString().replace("_", "-"))
// 			.ifPresent(value -> node.put("policy", value));

// 		return node;
// 	}

// 	private JsonNode generateRelease(Release subject, Target target) {
// 		PropertyMapper<Release> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.stringValue(Name.class)
// 			.ifPresent(value -> node.put("name", value));

// 		factory.stringValue(TagName.class)
// 			.ifPresent(value -> node.put("tag_name", value));

// 		factory.stringValue(Description.class)
// 			.ifPresent(value -> node.put("description", value));

// 		factory.stringValue(RawRef.class)
// 			.ifPresent(value -> node.put("ref", value));

// 		List<String> milestones = factory.stringValues(Milestone.class);
// 		if (!milestones.isEmpty())
// 			node.set("milestones", mapper.valueToTree(milestones));


// 		factory.stringValue(ReleasedAt.class)
// 			.ifPresent(value -> node.put("released_at", value));

// 		return node;
// 	}

// 	private JsonNode generateEnvironment(Environment subject, Target target) {
// 		PropertyMapper<Environment> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.stringValue(Name.class)
// 			.ifPresent(value -> node.put("name", value));

// 		factory.stringValue(URL.class)
// 			.ifPresent(value -> node.put("url", value));

// 		factory.stringValue(AutoStopIn.class)
// 			.ifPresent(value -> node.put("auto_stop_in", value));

// 		factory.value(EnvAction.class, String.class, action -> action.getValue().toString())
// 			.ifPresent(value -> node.put("action", value));

// 		return node;
// 	}

// 	private JsonNode generateRetry(Retry subject, Target target) {
// 		PropertyMapper<Retry> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.intValue(SimpleRetry.class)
// 			.ifPresent(value -> node.put("max", value));

// 		factory.value(RetryWhen.class, String.class, when -> when.getValue().toString())
// 			.ifPresent(value -> node.put("when", value));

// 		return node;
// 	}

// 	private JsonNode generateService(Service subject, Target target) {
// 		PropertyMapper<Service> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.stringValue(Name.class)
// 			.ifPresent(value -> node.put("name", value));

// 		factory.stringValue(Alias.class)
// 			.ifPresent(value -> node.put("alias", value));

// 		List<String> commands = factory.stringValues(Command.class);
// 		if (!commands.isEmpty())
// 			node.set("command", mapper.valueToTree(commands));

// 		List<String> entrypoint = factory.stringValues(EntryPoint.class);
// 		if (!entrypoint.isEmpty())
// 			node.set("entrypoint", mapper.valueToTree(entrypoint));

// 		return node;
// 	}

// 	// for target
// 	private JsonNode generateSimpleService(SimpleService subject) {
// 		ObjectNode node = mapper.createObjectNode();
// 		node.put("name", subject.getValue());
// 		return node;
// 	}

// 	private ArrayNode generateServices(PropertyMapper<Job> factory, Job job, Target target) {
// 		ArrayNode services = mapper.createArrayNode();

// 		Stream.concat(Stream.concat(
// 			factory.stringValues(SimpleService.class).stream().map(string -> {
// 				ObjectNode _svc = mapper.createObjectNode();
// 				_svc.put("name", string);
// 				return _svc;
// 			}),
// 			target.getSimpleServices().stream().map(this::generateSimpleService)),
// 			Stream.of(
// 					job.getServicePredecessors().stream(),
// 					target.getServicePredecessors().stream())
// 				.reduce(Stream::concat)
// 				.orElseGet(Stream::empty)
// 				.map(svc -> generateService(svc, target)))
// 				.forEach(services::add);
// 		return services;
// 	}

// 	// for target
// 	private JsonNode generateSimpleImage(SimpleImage subject) {
// 		ObjectNode node = mapper.createObjectNode();
// 		node.put("name", subject.getValue());
// 		return node;
// 	}

// 	private JsonNode generateImage(Image subject, Target target) {
// 		PropertyMapper<Image> factory = new PropertyMapper<>(subject, target);
// 		ObjectNode node = mapper.createObjectNode();

// 		factory.stringValue(Name.class)
// 			.ifPresent(value -> node.put("name", value));

// 		List<String> entrypoint = factory.stringValues(EntryPoint.class);
// 		if (!entrypoint.isEmpty())
// 			node.set("entrypoint", mapper.valueToTree(entrypoint));

// 		return node;
// 	}

// 	public Optional<JsonNode> generateImages(Optional<String> image, Job job, Target target) {

// 		Stream<JsonNode> images =
// 			// Optional.stream() is java 9
// 			(image.isPresent() ? Stream.of(image.get()) : Stream.<String> empty())
// 				.map(string -> {
// 					ObjectNode img = mapper.createObjectNode();
// 					img.put("name", string);
// 					return img;
// 				});

// 		return Stream.concat(images, Stream.concat(
// 			target.getSimpleImages().stream()
// 				.map(this::generateSimpleImage),
// 			Stream.of(
// 					job.getImagePredecessors().stream(),
// 					target.getImagePredecessors().stream())
// 			.reduce(Stream::concat)
// 			.orElseGet(Stream::empty)
// 			.map(img -> generateImage(img, target))
// 		))
// 		.findFirst();
// 	}

// 	private JsonNode generateExcept(PropertyMapper<Job> factory, Job job, Target target) {
// 		ObjectNode node = mapper.createObjectNode();

// 		ArrayNode ref = mapper.createArrayNode();
// 		ArrayNode exp = mapper.createArrayNode();
// 		ArrayNode change = mapper.createArrayNode();

// 		factory.mappedStringValues(SimpleExcept.class, SimpleExcept::getValue)
// 			.forEach(ref::add);

// 		target.getSimpleExcepts().stream()
// 			.map(SimpleExcept::getValue)
// 			.forEach(ref::add);

// 		Stream<Except> excepts = Stream.concat(
// 				job.getExceptPredecessors().stream(),
// 				target.getExceptPredecessors().stream());

// 		excepts.forEach(except -> {
// 			PropertyMapper<Except> subject = new PropertyMapper<>(except, target);
// 			subject.mappedStringValues(Ref.class, Ref::getValue)
// 				.forEach(ref::add);

// 			subject.mappedStringValues(Change.class, Change::getValue)
// 				.forEach(change::add);


// 			subject.mappedStringValues(VarExp.class, VarExp::getValue)
// 				.forEach(exp::add);

// 			// TODO: Conflicting kubernetes are not allowed
// 			subject.booleanValue(Kubernetes.class).ifPresent(value -> {
// 				if (value)
// 					node.put("kubernetes", "active");
// 			});
// 		});

// 		if (ref.size() > 0)
// 			node.set("refs", ref);

// 		if (exp.size() > 0)
// 			node.set("variables", exp);

// 		if(change.size() > 0)
// 			node.set("changes", change);

// 		return node;
// 	}

// 	private JsonNode generateOnly(PropertyMapper<Job> factory, Job job, Target target) {
// 		ObjectNode node = mapper.createObjectNode();

// 		ArrayNode ref = mapper.createArrayNode();
// 		ArrayNode exp = mapper.createArrayNode();
// 		ArrayNode change = mapper.createArrayNode();

// 		factory.mappedStringValues(SimpleOnly.class, SimpleOnly::getValue)
// 			.forEach(ref::add);

// 		target.getSimpleOnlys().stream()
// 			.map(SimpleOnly::getValue)
// 			.forEach(ref::add);

// 		Stream<Only> onlies = Stream.concat(
// 				job.getOnlyPredecessors().stream(),
// 				target.getOnlyPredecessors().stream());

// 		onlies.forEach(only -> {
// 			PropertyMapper<Only> subject = new PropertyMapper<>(only, target);
// 			subject.mappedStringValues(Ref.class, Ref::getValue)
// 				.forEach(ref::add);

// 			subject.mappedStringValues(Change.class, Change::getValue)
// 				.forEach(change::add);


// 			subject.mappedStringValues(VarExp.class, VarExp::getValue)
// 				.forEach(exp::add);

// 			// TODO: Conflicting kubernetes are not allowed
// 			subject.booleanValue(Kubernetes.class).ifPresent(value -> {
// 				if (value)
// 					node.put("kubernetes", "active");
// 			});
// 		});

// 		if (ref.size() > 0)
// 			node.set("refs", ref);

// 		if (exp.size() > 0)
// 			node.set("variables", exp);

// 		if(change.size() > 0)
// 			node.set("changes", change);

// 		return node;
// 	}

// 	private JsonNode generateRules(Job job, Target target) {
// 		ArrayNode array = mapper.createArrayNode();

// 		Stream.concat(
// 			job.getRulePredecessors().stream(),
// 			target.getRulePredecessors().stream()
// 		)
// 		.map(rule -> generateRule(rule, target))
// 		.forEach(array::add);

// 		return array;
// 	}

// 	private JsonNode generateRule(Rule rule, Target target) {
// 		ObjectNode node = mapper.createObjectNode();
// 		PropertyMapper<Rule> factory = new PropertyMapper<Rule>(rule, target);

// 		factory.value(VarExp.class, String.class, p -> p.getValue().toString())
// 			.ifPresent(value -> node.put("if", value));

// 		List<String> changes = factory.values(Change.class, Change::getValue);
// 		if (!changes.isEmpty())
// 			node.set("changes", mapper.valueToTree(changes));

// 		List<String> exists = factory.values(Exists.class, Exists::getValue);
// 		if (!exists.isEmpty())
// 			node.set("exists", mapper.valueToTree(exists));

// 		factory.value(When.class, String.class, p -> p.getValue().toString())
// 			.ifPresent(value -> node.put("when", value));

// 		factory.booleanValue(AllowFailure.class)
// 			.ifPresent(value -> node.put("allow_failure", value));

// 		return node;
// 	}

// 	private JsonNode getScriptArguments (Job job, Target target) {
// 		ObjectNode args = mapper.createObjectNode();
// 		job.getScriptArguments().forEach(arg -> {
// 			String value = arg.getValue();

// 			if (arg.getStringVariablePredecessors().size() == 1)
// 				value = arg.getStringVariablePredecessors().get(0).getValue();
// 			else
// 				value = arg.getStringParameterPredecessors().stream()
// 				.filter(param -> param.getContainer() == target)
// 				.map(StringParameter::getValue)
// 				.findFirst()
// 				.orElse(value);
// 			args.put(arg.getKey(), value);
// 		});
// 		return args;
// 	}

// 	private void readScript(ObjectNode node, String key, String text) {
// 		if (text != null && !text.trim().isEmpty()) {
// 			JsonNode lines = mapper.valueToTree(listScript(text));
// 			node.set(key, lines);
// 		}
// 	}

// 	// Java 11 has Predicate.not()
// 	private static <T> Predicate<T> not(Predicate<T> predicate) {
// 		return predicate.negate();
// 		//return t -> !predicate.test(t);
// 	}

// 	/**
// 	 * Split script into lines and perform cleanup
// 	 * (cf. https://gitlab.com/scce/cinco/-/issues/260)
// 	 *
// 	 * @param script
// 	 * @return
// 	 */
// 	private List<String> listScript(String script) {
// 		return Arrays.stream(script.split("\\R+"))
// 			.map(String::trim)
// 			.filter(not(String::isEmpty))
// 			.collect(Collectors.toList());
// 	}
// }