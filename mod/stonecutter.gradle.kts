import org.gradle.kotlin.dsl.replace

plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "26.1-fabric+noremap" /* [SC] DO NOT EDIT */

stonecutter parameters {
    constants.match(
        node.metadata.project.substringAfterLast('-').substringBeforeLast('+'),
        "fabric", "neoforge"
    )
    replacements {
        string(current.parsed < "26.1") {
            replace("SavedDataStorage", "DimensionDataStorage")
            replace("eu.pb4.sgui", "de.clickism.fgui")
            replace("GuiLike", "GuiInterface")
        }
        string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
            replace("net.minecraft.world.entity.npc.villager", "net.minecraft.world.entity.npc")
            replace("net.minecraft.world.entity.monster.zombie", "net.minecraft.world.entity.monster")
        }
    }
}
