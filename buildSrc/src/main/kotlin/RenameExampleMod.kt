import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class RenameExampleMod @Inject constructor(private val dir: File, private val modId: String, private val modName: String, private val modDisplayName: String, private val rootNS: String, private val authorID: String) : DefaultTask()
{
    @TaskAction
    fun update() {
        dir.walk()
            .filter { it.name.endsWith(".java") || it.name.endsWith(".json") }
            .forEach {
                val text = it.readText()
                    .replace("example_mod", modId)
                    .replace("ExampleMod", modName)
                    .replace("Example Mod", modDisplayName)
                    .replace("toni.examplemod", "$group.$rootNS")

                it.writeText(text)
            }

        val javaDir = File(dir, "src/main/java/")
        val resourcesDir = File(dir, "src/main/resources/")
        val modDir = File(javaDir, "toni/examplemod/")

        rename(resourcesDir, "mixins.example_mod.json", "mixins.$modId.json")
        rename(resourcesDir, "example_mod.accesswidener", "$modId.accesswidener")

        rename(resourcesDir, "assets/example_mod", "assets/$modId")
        rename(resourcesDir, "data/example_mod", "data/$modId")

        rename(modDir, "ExampleMod.java", "$modName.java")

        rename(javaDir, "toni/examplemod/", "toni/$rootNS/")
        rename(javaDir, "toni/", "$authorID/")
    }

    private fun rename(targetDir: File, from: String, to: String) {
        File(targetDir, from).renameTo(File(targetDir, to))
    }
}