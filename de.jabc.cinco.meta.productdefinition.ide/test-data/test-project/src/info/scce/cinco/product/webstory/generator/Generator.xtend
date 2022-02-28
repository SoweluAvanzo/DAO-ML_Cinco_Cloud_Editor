package info.scce.cinco.product.webstory.generator;

import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator
import info.scce.cinco.product.webstory.webstory.WebStory

import info.scce.cinco.product.webstory.mcam.cli.WebStoryExecution

class Generator extends IGenerator<WebStory> {

    final static String jsGenFolder = "js_generated"

    override void generate(WebStory story) {
        if (story.hasErrors)
            throw new RuntimeException("Model has errors. Please fix before generating")

        val CharSequence generatedDeclarations = new VariableDeclarationsGenerator().generate(story)
        createFile(jsGenFolder+"/declarations.js", generatedDeclarations)

        val CharSequence generatedActivities = new ActivityGenerator(this).generate(story)
        createFile(jsGenFolder+"/activities.js", generatedActivities)

        copyStaticResources
    }

    /**
     *  Performs all available checks on the given web story
     */
    static def hasErrors(WebStory story) {
        val wse = new WebStoryExecution()
        val checkAdapter = wse.initApiAdapter(story)
        wse.executeCheckPhase(checkAdapter).values.map[errors].flatten.length > 0
    }
}