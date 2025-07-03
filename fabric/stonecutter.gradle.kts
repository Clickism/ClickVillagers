plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.7" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("publishAllVersions", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}
