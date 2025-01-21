package com.csmcclain.minecraftDrain;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class MinecraftDrain extends JavaPlugin {

    final String fillCommand = "fill %d %d %d %d %d %d air replace water";
    @Override
    public void onEnable() {
        // Plugin startup logic
        Objects.requireNonNull(this.getCommand("drain"))
                .setExecutor((commandSender, command, s, strings) -> {
                    if (!(commandSender instanceof Player player)) {
                        return false;
                    }

                    List<Chunk> chunksToDrain = getChunksToDrain(player);
                    Iterator<Chunk> chunkIterator = chunksToDrain.iterator();

                    player.sendMessage(Component.text(
                            "Draining chunks surrounding you. This may take a while....."
                    ));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            drainChunk(chunkIterator.next());
                            if (!chunkIterator.hasNext()) {
                                player.sendMessage(Component.text(
                                        "Finished draining chunks surrounding you."
                                ));
                                cancel();
                            }
                        }
                    }.runTaskTimer(this, 0l, 1l);

                    return true;
                });
    }

    private List<Chunk> getChunksToDrain(Player player) {
        List<Chunk> chunksToDrain = new ArrayList<>();
        World world = player.getWorld();
        Chunk currentChunk = player.getChunk();

        for (int xCord = currentChunk.getX() - 1; xCord < currentChunk.getX() + 2; xCord++) {
            for (int zCord = currentChunk.getZ() - 1; zCord < currentChunk.getZ() + 2; zCord++) {
                chunksToDrain.add(world.getChunkAt(xCord, zCord, true));
            }
        }

        return chunksToDrain;
    }

    private void drainChunk(Chunk chunk)
    {
        Block startBlock = chunk.getBlock(0, -64, 0);
        Block endBlock = chunk.getBlock(15, 63, 15);

        if (chunk.load(true)) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    String.format(fillCommand,
                            startBlock.getX(), startBlock.getY(), startBlock.getZ(),
                            endBlock.getX(), endBlock.getY(), endBlock.getZ())
            );
        }
    }

}
