val settings = object : TxniTemplateSettings {

	// -------------------- Dependencies ---------------------- //
	override val depsHandler: DependencyHandler get() = object : DependencyHandler {
		override fun addGlobal(deps: DependencyHandlerScope) {

		}

		override fun addFabric(deps: DependencyHandlerScope) {

		}

		override fun addForge(deps: DependencyHandlerScope) {

		}

		override fun addNeo(deps: DependencyHandlerScope) {

		}
	}


	// ---------- Curseforge/Modrinth Configuration ----------- //
	// For configuring the dependecies that will show up on your mod page.
	override val publishHandler: PublishDependencyHandler get() = object : PublishDependencyHandler {
		override fun addShared(deps: DependencyContainer) {
			if (isFabric) {
				deps.requires("fabric-api")
			}
		}

		override fun addCurseForge(deps: DependencyContainer) {

		}

		override fun addModrinth(deps: DependencyContainer) {

		}
	}
}


// ---------------TxniTemplate Build Script---------------- //
//   (only edit below this if you know what you're doing)
// -------------------------------------------------------- //

plugins {
	`maven-publish`
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("dev.kikugie.j52j") version "1.0"
	id("dev.architectury.loom")
	id("me.modmuss50.mod-publish-plugin")
	id("systems.manifold.manifold-gradle-plugin")
}

// The manifold Gradle plugin version. Update this if you update your IntelliJ Plugin!
manifold { manifoldVersion = "2024.1.30" }

// Variables
class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name").toString()
	val version = property("mod.version").toString()
	val group = property("mod.group").toString()
	val author = property("mod.author").toString()
	val namespace = property("mod.namespace").toString()
	val displayName = property("mod.display_name").toString()
	val description = property("mod.description").toString()
	val mcDep = property("mod.mc_dep").toString()
	val license = property("mod.license").toString()
	val github = property("mod.github").toString()
}

val mod = ModData()

val mcVersion = stonecutter.current.project.substringBeforeLast('-')

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeo = loader == "neoforge"

version = "${mod.version}-$mcVersion"
group = mod.group
base { archivesName.set("${mod.id}-$loader") }

// Dependencies
repositories {
	fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
		forRepository { maven(url) }
		filter { groups.forEach(::includeGroup) }
	}
	strictMaven("https://www.cursemaven.com", "curse.maven")
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
	strictMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
	maven("https://maven.kikugie.dev/releases")
	maven("https://jitpack.io")
	maven("https://maven.neoforged.net/releases/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
	maven("https://maven.parchmentmc.org")
}

dependencies {
	minecraft("com.mojang:minecraft:${mcVersion}")

	// apply the Manifold processor, do not remove this unless you want to swap back to Stonecutter preprocessor
	implementation(annotationProcessor("systems.manifold:manifold-preprocessor:${manifold.manifoldVersion.get()}")!!)

	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		officialMojangMappings()
		val parchmentVersion = when (mcVersion) {
			"1.18.2" -> "1.18.2:2022.11.06"
			"1.19.2" -> "1.19.2:2022.11.27"
			"1.20.1" -> "1.20.1:2023.09.03"
			"1.21.1" -> "1.21:2024.07.28"
			else -> ""
		}
		parchment("org.parchmentmc.data:parchment-$parchmentVersion@zip")
	})

	settings.depsHandler.addGlobal(this)

	if (isFabric) {
		settings.depsHandler.addFabric(this)
		modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

		when (mcVersion) {
			"1.19.2" -> modApi("net.minecraftforge:forgeconfigapiport-fabric:${property("deps.forgeconfigapi")}")
			else -> modApi("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${property("deps.forgeconfigapi")}")
		}
	}

	if (isForge) {
		settings.depsHandler.addForge(this)
		"forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
	}

	if (isNeo) {
		settings.depsHandler.addNeo(this)
		"neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
	}

	vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.1")
}

// Loom config
loom {
	accessWidenerPath.set(rootProject.file("src/main/resources/${mod.id}.accesswidener"))

	if (loader == "forge") forge {
		convertAccessWideners.set(true)
		mixinConfigs("mixins.${mod.id}.json")
	} else if (loader == "neoforge") neoForge {

	}

	runConfigs["client"].apply {
		ideConfigGenerated(true)
		vmArgs("-Dmixin.debug.export=true")
		programArgs("--username=nthxny") // Mom look I'm in the codebase!
		runDir = "../../run/${stonecutter.current.project}/"
	}

	decompilers {
		get("vineflower").apply {
			options.put("mark-corresponding-synthetics", "1")
		}
	}
}

// Tasks
tasks.withType<JavaCompile>() {
	options.compilerArgs.add("-Xplugin:Manifold")
	// modify the JavaCompile task and inject our auto-generated Manifold symbols
	if(!this.name.startsWith("_")) { // check the name, so we don't inject into Forge internal compilation
		ManifoldMC.setupPreprocessor(options.compilerArgs, loader, projectDir, mcVersion, stonecutter.active.project == stonecutter.current.project, false)
	}
}

project.tasks.register("setupManifoldPreprocessors") {
	ManifoldMC.setupPreprocessor(ArrayList(), loader, projectDir, mcVersion, stonecutter.active.project == stonecutter.current.project, true)
}

tasks.setupChiseledBuild { finalizedBy("setupManifoldPreprocessors") }

tasks.register<RenameExampleMod>("renameExampleMod", rootDir, mod.id, mod.name, mod.displayName, mod.namespace, mod.author).configure {
	group = "build helpers"
	description = "Renames the example mod to match the mod ID, name, and display name in gradle.properties"
}




val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.remapJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
	dependsOn("build")
}

if (stonecutter.current.isActive) {
	rootProject.tasks.register("buildActive") {
		group = "project"
		dependsOn(buildAndCollect)
	}

	rootProject.tasks.register("runActive") {
		group = "project"
		dependsOn(tasks.named("runClient"))
	}
}

// Resources
tasks.processResources {
//	inputs.property("version", mod.version)
//	inputs.property("mc", mod.mcDep)

	val map = mapOf(
		"version" to mod.version,
		"mc" to mod.mcDep,
		"id" to mod.id,
		"group" to mod.group,
		"author" to mod.author,
		"namespace" to mod.namespace,
		"description" to mod.description,
		"name" to mod.name,
		"license" to mod.license,
		"github" to mod.github,
		"display_name" to mod.displayName,
		"fml" to if (loader == "neoforge") "1" else "45",
		"mnd" to if (loader == "neoforge") "" else "mandatory = true"
	)

	filesMatching("fabric.mod.json") { expand(map) }
	filesMatching("META-INF/mods.toml") { expand(map) }
	filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
}

stonecutter {
	val j21 = eval(mcVersion, ">=1.20.6")
	java {
		withSourcesJar()
		sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
		targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	}

	kotlin {
		jvmToolchain(if (j21) 21 else 17)
	}
}

// Publishing
publishMods {
	file = tasks.remapJar.get().archiveFile
	additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
	displayName = "${mod.name} ${loader.replaceFirstChar { it.uppercase() }} ${mod.version} for ${property("mod.mc_title")}"
	version = mod.version
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE
	modLoaders.add(loader)

	val targets = property("mod.mc_targets").toString().split(' ')

	dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
			providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

	modrinth {
		projectId = property("publish.modrinth").toString()
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		targets.forEach(minecraftVersions::add)
		val deps = DependencyContainer(null, this)
 		settings.publishHandler.addModrinth(deps)
		settings.publishHandler.addShared(deps)
	}

	curseforge {
		projectId = property("publish.curseforge").toString()
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		targets.forEach(minecraftVersions::add)
		val deps = DependencyContainer(this, null)
		settings.publishHandler.addCurseForge(deps)
		settings.publishHandler.addShared(deps)
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "${property("mod.group")}.${mod.id}"
			artifactId = mod.version
			version = mcVersion

			from(components["java"])
		}
	}
}