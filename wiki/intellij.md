---
outline: deep
---

# Setting Up IntelliJ to Work Properly

As a general rule, you should be using the [Minecraft Dev plugin](https://plugins.jetbrains.com/plugin/8327-minecraft-development), like, for everything.

## Manifold Plugin

You're also going to need to download the [**Manifold IntelliJ plugin**](https://plugins.jetbrains.com/plugin/10057-manifold), as it is required for highlighting to
work on the Manifold preprocessor directives. If you're not using IntelliJ, you're using the wrong editor and are on your own :3

## Stonecutter Dev Plugin

If you're using Manifold, the [Stonecutter Dev plugin](https://plugins.jetbrains.com/plugin/25044-stonecutter-dev/features) isn't too useful, but if you plan on using versioned preprocessing in `.json5` files,
you will want to install it for syntax highlighting on the comments.

## Fixing Auto Imports

If you use auto import, loader & version specific code will usually need to be put in an `#IF` block. This can 
screw things up when IntelliJ automatically assumes it can operate on code inside preprocessor blocks.

To make this easier for yourself, I would highly recommend **turning off "Add unambiguous imports on the fly"**, which
will tell IntelliJ not to do this.

## Manifold Live Templates

You will also probably want to set up Live Templates for the preprocessor directives, as it makes it so much easier with tab-completion. You can just type #if and hit tab, and it will generate the whole expression. I've also added an #ifinline and #ifelse. Make sure you make a new template category and enable it everywhere.

![image](https://github.com/anthxnymc/SoundPhysicsReverberated/assets/67132971/a73da597-a0c3-48bb-9ff4-be010022eed0)

```
#if $expression$
$SELECTION$$END$ 
#endif
```
```
#if $expression$ $SELECTION$$END$ #endif
```
```
#if $expression$
$SELECTION$$END$
#else

#endif
```

## Troubleshooting

Sometimes IntelliJ likes to put you in the **process lock of doom**---where it locks up the loom mappings file handle with an error saying "process cannot access the file", which persists across reloads.

This seems to be an issue in Loom, but until it's fixed, you have a few options:
- Close IntelliJ and run `./gradlew` in PowerShell to rebuild.
- Close IntelliJ and clear caches
- Close IntelliJ and restart PC 
- Make a bargain with an e̷x̸t̷r̸a̵d̸i̷m̸e̸n̴s̶i̶o̸n̴a̷l̶ ̸e̵n̶t̸i̶t̶y̷ for it to start working again
- Give up and cry yourself to sleep