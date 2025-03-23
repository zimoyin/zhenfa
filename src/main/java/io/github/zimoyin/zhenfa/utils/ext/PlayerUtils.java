package io.github.zimoyin.zhenfa.utils.ext;

import ca.weblite.objc.Client;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
// TODO 分支同步
public class PlayerUtils {
    public static void sendMessageTo(Player player, String message) {
        player.sendMessage(new TextComponent(message), player.getUUID());
    }

    public static void sendMessageTo(Level level, String message) {
        getPlayers(level).stream().findAny().ifPresent(player -> player.sendMessage(new TextComponent(message), player.getUUID()));
    }

    public static List<? extends Player> getPlayers(Level level) {
        return level.players();
    }


    /**
     * 获取区块内玩家
     * @param level
     * @param blockPos 方块所在的区块
     */
    public static List<ServerPlayer> getPlayers(Level level, BlockPos blockPos) {
        if (level.isClientSide) return List.of();
        return ((ServerLevel) level).getChunkSource().chunkMap
                .getPlayers(new ChunkPos(blockPos), false);
    }
}
