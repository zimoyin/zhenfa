package io.github.zimoyin.zhenfa.utils;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
// TODO 分支同步
public class PlayerUtils {
    public static void sendMessageTo(Player player, String message){
        player.sendMessage(new TextComponent(message), player.getUUID());
    }
}
