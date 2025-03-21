SavedData 是一种在世界中保存信息的方法，原版在地图之类的地方有使用，另外 forge 的 WorldCapabilityData 也是此类的子类。


```java
package io.github.zimoyin.zhenfa.savedata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * SavedData 是一种在世界中保存信息的方法，原版在地图之类的地方有使用，另外 forge 的 WorldCapabilityData 也是此类的子类。
 *
 * 如果你希望记录玩家在一个特定世界中的某些行为（如击杀怪物的数量、解锁成就的状态等），你可以使用 SavedData 来实现
 * @link <a href="https://tt432.github.io/ModdingTutorial118/#/2%E8%BF%9B%E9%98%B6/savedData/SavedData?id=saveddata">文档</a>
 */
public class TutorialSavedData extends SavedData {
    private static final String NAME = "TUTORIAL";

    public static TutorialSavedData get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(TutorialSavedData::read, TutorialSavedData::new, NAME);
        } else {
            throw new RuntimeException("只能在服务端获取 SavedData ！ 出错位置： " + NAME);
        }
    }

    private int count = 0;

    public void click(Player player) {
        count++;
        player.sendMessage(new TextComponent("你点击了： " + count + " 次"), player.getUUID());
    }

    /**
     * 从 tag 中读取 SavedData
     */
    public static TutorialSavedData read(CompoundTag tag) {
        TutorialSavedData result = new TutorialSavedData();
        result.count = tag.contains("count") ? tag.getInt("count") : 0;
        return result;
    }

    /**
     * 保存信息到 Tag 中
     */
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.putInt("count", count);
        return pCompoundTag;
    }
}
```