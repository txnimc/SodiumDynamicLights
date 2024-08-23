---
outline: deep
---

# Getting Started Using This Template

## Configuring Gradle

The very first thing you're going to want to do after forking and importing the Gradle project is rename the template.
There is a built in helper task for this: `renameExampleProject`, which will use the following values from
your `gradle.properties` file. Make sure you set all of them, they are used elsewhere!

::: code-group
```md [gradle.properties]
# ----------Mod Properties----------#
# Make sure you edit these before running renameExampleMod!
mod.version=1.0.0
mod.license=ARR
# Root Folder Path (/java/group/namespace/)
mod.group=toni
mod.namespace=examplemod
# Mod ID and Main Class Name
mod.id=example_mod
mod.name=ExampleMod
# English Display Info
mod.author=Toni
mod.github=anthxnymc
mod.display_name=Example Mod
mod.description=Yet Another Example Mod
# ----------------------------------#
```

You'll also of course want to set the Curseforge and Modrinth deployment IDs, but that isn't necessary right away. 
Just set the above values, run the `renameExampleProject` Gradle task, and you're good to go!

## Using Stonecutter

You will only get IntelliSense for one version at a time, but this can be changed by using the `Set active project to version-loader` 
gradle helper tasks under the `stonecutter` folder in the root project.


## Using Manifold To Implement Version-Specific Code

You're going to need to download the [**Manifold IntelliJ plugin**](https://plugins.jetbrains.com/plugin/10057-manifold), as it is required for highlighting to
work on the Manifold preprocessor directives ([more on IntelliJ config here](/intellij)). 

<sub>*If you're not using IntelliJ, you're using the wrong editor and are on your own :3*</sub>

This essentially replaces the patterns of the default [Stonecutter preprocessor](https://stonecutter.kikugie.dev/stonecutter/comments), 
which you are still free to use if you wish. Manifold being an actual compiler plugin has a number of advantages, including inline
preprocessor directives, easier editing, and the most important part---not having to mutate the source files
when changing the main project version.

The Manifold scripts will generate **FABRIC** and **FORGE** markers for separating loader-specific code.
Also, for each Minecraft version, five markers are created, using the minor and patch versions of the mcVersionStr:
- **BEFORE_20_1** (exclusive, <)
- **UPTO_20_1** (inclusive, <=)
- **NEWER_THAN_20_1** (exclusive, >)
- **AFTER_20_1** (inclusive, >=)
- **CURRENT_20_1**, ==, a standalone marker that will single out that version.

You can use `#IF` blocks to separate code between modloaders and game versions.

![preprocessor.png](assets/preprocessor.png)

For example, lets say a breaking change was introduced in 1.20.1. You could use `#IF BEFORE_20_1` for all code
without the change, and `#IF AFTER_20_1` for all code including the change.

This template still uses the structure and per-version specific files of the ReplayMod preprocessor, so if you need to
override a file for a specific version, you can do so by putting it in the `versions/` folder under the same path as the original.
