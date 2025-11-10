package de.clickism.clickvillagers.util;

import io.papermc.lib.PaperLib;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class SpigotAdapter {
    public static BlockState[] getTileEntities(Chunk chunk, boolean useSnapshot) {
        return !PaperLib.isPaper()
                ? chunk.getTileEntities()
                : chunk.getTileEntities(useSnapshot);
    }

    public static Collection<BlockState> getTileEntities(Chunk chunk, Predicate<Block> blockPredicate, boolean useSnapshot) {
        return !PaperLib.isPaper()
                ? List.of(chunk.getTileEntities())
                : chunk.getTileEntities(blockPredicate, useSnapshot);
    }
}
