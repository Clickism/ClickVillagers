plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

stonecutter {
    parameters {
        constants.match(
            node.metadata.project.substringAfterLast('-'),
            "fabric", "neoforge"
        )
    }
}
