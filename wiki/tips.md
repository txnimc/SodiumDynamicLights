---
outline: deep
---

# Multiversion Gradle Crash Course

The template is set up to be easy to add dependencies and work without in-depth Gradle knowledge, but if you'd like
to understand how this whole setup works from top to bottom, this page is for you. Otherwise, you're free to skip it!

First off, you will want to read the [Stonecutter wiki!](https://stonecutter.kikugie.dev/stonecutter/tips) This is the backbone
of this template, and has a lot of quirks. For the most part though, you can ignore the parts about the versioned comments,
unless you want to use them in `.json5` resource files.

## Why Do You Have Three Gradle Files?

Because of the way the Stonecutter plugin works, a few different Gradle files are needed. If you cloned the template only
to find yourself asking "wtf is a stonecutter.gradle," it's actually pretty simple.

`settings.gradle.kts` is the root of the project. This is the first thing loaded by your IDE, and is usually used for
Maven repositories, but in this project it also is used to bootstrap Stonecutter. You'll see here below that, among other things,
the Stonecutter plugin is applied and configured. This is also where you will add or remove supported versions and loaders:

::: code-group
```kts [settings.gradle.kts]
plugins {
	id("dev.kikugie.stonecutter") version "0.5-alpha.4"
}

extensions.configure<StonecutterSettings> {
	kotlinController = true
	centralScript = "build.gradle.kts"
	shared {
		fun mc(version: String, vararg loaders: String) {
			for (it in loaders) vers("$version-$it", version)
		}

		mc("1.20.1", "fabric" , "forge")
		mc("1.21.1", "fabric" , "neoforge")
	}
	create(rootProject)
}

rootProject.name = "TxniTemplate"
``` 
:::

`stonecutter.gradle.kts` is the main controller, which handles the multiversion builds by creating a new project for
each version registered with Stonecutter. You can also register your normal plugins here, though you don't need to apply 
all of them since this file doesn't do the actual build. You won't see most of the magic in this file, since it's happening in the Stonecutter plugin, but you can
do some pretty advanced [configuration stuff](https://stonecutter.kikugie.dev/stonecutter/configuration) here.

::: code-group
```kts [stonecutter.gradle.kts]
plugins {
    id("dev.kikugie.stonecutter")
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.serialization") version "2.0.0" apply false
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.5.+" apply false
    id("systems.manifold.manifold-gradle-plugin") version "0.0.2-alpha" apply false
}

stonecutter active "1.20.1-forge" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}
``` 
:::

`build.gradle.kts` is the one you're probably familiar with. It's a fairly standard Architectury Loom setup, and what you will
be modifying the most, usually to add your own dependencies.

## Versioned Dependencies

The main difference from an Architectury setup is that the main `build.gradle.kts` Gradle script handles both Fabric 
and Forge builds for every version. This is one of the main hurdles of multiversion---managing dependencies for every
target in one script.

You can handle this in code by accessing the `mcVersion` from Stonecutter's current project, 
and the `loader` from the Loom platform:

::: code-group
```kts [build.gradle.kts]
val mcVersion = stonecutter.current.project.substringBeforeLast('-')

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeo = loader == "neoforge"
```
:::

Stonecutter also provides a simple way to create versioned `gradle.properties`, which is actually where the Loom platform is configured.
In the root project's `gradle.properties`, set any property you want to be versioned to `[VERSIONED]`:

::: code-group
```md [gradle.properties]
# ----------Dependencies------------#
deps.fapi=[VERSIONED]
# ----------------------------------#
```
:::

Then, you will need to define each versioned property you want to use in a separate file under `./versions/1.20.1-fabric/gradle.properties` (as well as configuring the Loom platform)

::: code-group 
```md [versions/1.20.1-fabric/gradle.properties]
loom.platform=fabric

# ----------Dependencies------------#
deps.fapi=0.99.4+1.21
# ----------------------------------#
```
:::

Then, you can access it in `build.gradle.kts` using `property("name")`, and it will be automatically replaced with the right version:

::: code-group
```kts [build.gradle.kts]
modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
```
:::