plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "26.1-fabric+noremap" /* [SC] DO NOT EDIT */

stonecutter {
    parameters {
        constants.match(
            node.metadata.project.substringAfterLast('-').substringBeforeLast('+'),
            "fabric", "neoforge"
        )
    }
}
