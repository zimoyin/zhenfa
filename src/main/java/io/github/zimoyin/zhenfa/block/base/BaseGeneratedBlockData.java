package io.github.zimoyin.zhenfa.block.base;

import io.github.zimoyin.zhenfa.datagen.provider.LootTableProvider;
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
 * 用于生成方块数据的类
 * 你需要在你继承了 BaseBlock 或者 Block 的类上使用 @Blocks.RegisterBlock 注解并指定 generatedData 的字段。
 * 你需要继承该类，并根据需要重写以下的方法。并将你创建的类添加到 @Blocks.RegisterBlock 注解中。
 * 如果你需要生成 JSON 文件需要使用 gradle:runData 以此启动 DataGenerators.class
 *
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public class BaseGeneratedBlockData {
    public BaseBlock.Data data;

    public BaseGeneratedBlockData(BaseBlock.Data data) {
        this.data = data;
    }

    /**
     * 方块的语言描述
     */
    public List<Lang> lang() {
        return List.of();
    }

    /**
     * 方块的标签
     * Tag 的作用类似于矿物辞典，但又不完全相同。
     * 由于新版本的变化，挖掘的工具由 tag 进行控制,
     * 比如的 BlockTags.MINEABLE_WITH_PICKAXE 的意思就是这个方块可以由镐子挖，BlockTags.NEEDS_IRON_TOOL 的意思则是挖掘等级至少在铁以上。
     */
    public List<TagKey<Block>> tags() {
        return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
    }


    /**
     * 方块物品的标签
     */
    public List<TagKey<Item>> itemTags() {
        return List.of();
    }

    /**
     * 注册方块物品的模型
     * 默认情况下会寻找 /assets/[modid]/textures/item/[itemid.png] 是否存在如果不存在则使用 block/[blockid.png] 的模型
     */
    public void registerItemModel(ItemModelProvider provider) {
        String itemPath = "item/" + data.getItemId();
        if (ResourcesUtils.getResource("/assets/" + DataGenModId + "/textures/" + itemPath+".png") != null) {
            provider.singleTexture(data.getItemId(), provider.mcLoc("item/generated"), "layer0", provider.modLoc(itemPath));
        } else {
            provider.withExistingParent(data.getBlockId(), provider.modLoc("block/" + data.getBlockId()));
        }
    }

    /**
     * 注册方块的方向和模型
     * 默认使用  provider.simpleBlock 注册无方向的模型
     */
    public void registerStatesAndModel(BlockStateProvider provider) {
        provider.simpleBlock(data.getBlock());
    }

    /**
     * 注册方块的掉落
     *
     * 注册能保修NBT的凋落物
     * createStandardTable(
     *             "inventory_block",
     *             ModBlocks.INVENTORY_BLOCK.get(),
     *             ModBlockEntities.INVENTORY_BLOCK_ENTITY.get()
     *         )
     */
    public LootTable.Builder registerLootTable(LootTableProvider provider) {
        return provider.createSimpleTable(data.getBlockId(), data.getBlock());
    }

    /**
     * 方块的语言描述
     *
     */
    public static class Lang {
        final String lange;
        final String name;
        final String groupPrefix;
        final String groupId;
        final String groupName;

        /**
         *
         * @param lange 语言 如 en_us, zh_cn
         * @param name  方块名称
         */
        public Lang(String lange, String name) {
            this.lange = lange;
            this.name = name;
            this.groupPrefix = "itemGroup.";
            this.groupId = null;
            this.groupName = null;
        }

        /**
         * 带分组的方块语言描述
         * @param groupId 物品组的ID
         * @param groupName 物品组的名称
         */
        public Lang(String lange, String name, String groupId, String groupName) {
            this.lange = lange;
            this.name = name;
            this.groupPrefix = "itemGroup.";
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public Lang(String lange, String name, String groupPrefix, String groupId, String groupName) {
            this.lange = lange;
            this.name = name;
            this.groupPrefix = groupPrefix;
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public String getLange() {
            return lange;
        }

        public String getName() {
            return name;
        }

        public String getGroupPrefix() {
            return groupPrefix;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupIdWithPrefix() {
            return groupPrefix + groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public static final String en_us = "en_us";
        public static final String zh_cn = "zh_cn";
    }
}
