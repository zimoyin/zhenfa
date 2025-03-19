package io.github.zimoyin.zhenfa.item.base;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.datagen.provider.LootTableProvider;
import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.utils.Lang;
import io.github.zimoyin.zhenfa.utils.ResourcesUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;

import java.util.List;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

/**
 * 用于生成物品数据的类
 * 你需要在你继承了 BaseItem 或者 Item 的类上使用 @RegisterItem 注解并指定 generatedData 的字段。
 * 你需要继承该类，并根据需要重写以下的方法。并将你创建的类添加到 @RegisterItem 注解中。
 * 如果你需要生成 JSON 文件需要使用 gradle:runData 以此启动 DataGenerators.class
 *
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public class BaseGeneratedItemData {
    public BaseItem.Data data;

    public BaseGeneratedItemData(BaseItem.Data data) {
        this.data = data;
    }

    /**
     * 物品的语言描述
     */
    public List<Lang> lang() {
        return List.of();
    }

    /**
     * 物品的标签
     * 比如的 ItemTags.xxx
     */
    public List<TagKey<Item>> tags() {
        return List.of();
    }


    /**
     * 注册方块物品的模型
     * 默认情况下会寻找 /assets/[modid]/textures/item/[itemid.png] 是否存在如果不存在则使用 block/[blockid.png] 的模型
     */
    public void registerItemModel(ItemModelProvider provider) {
        String itemPath = "item/" + data.getItemId();
        provider.singleTexture(data.getItemId(), provider.mcLoc("item/generated"), "layer0", provider.modLoc(itemPath));
    }

    /**
     * 合成表
     * @param recipes 只提供了简单的合成表，如果需要更多的合成表请使用静态方法
     *
     */
    public void registerRecipe(Recipes recipes) {

    }
}
