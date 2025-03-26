package io.github.zimoyin.zhenfa.block.base;

import io.github.zimoyin.zhenfa.datagen.provider.BlockStates;
import io.github.zimoyin.zhenfa.datagen.provider.LootTableProvider;
import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.utils.Lang;
import io.github.zimoyin.zhenfa.utils.ResourcesUtils;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
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
     * Lang(LangType.ZH_CN, "测试方块") // 为方块添加名称
     * Lang("ID","描述",LangType.ZH_CN) // 为描述添加
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
        // 优先使用指定的 item 模型，如果没有这使用系统生成的 blockItem 模型
        if (ResourcesUtils.getResource("/assets/" + DataGenModId + "/textures/" + itemPath+".png") != null) {
            provider.singleTexture(data.getItemId(), provider.mcLoc("item/generated"), "layer0", provider.modLoc(itemPath));
        } else {
            provider.withExistingParent(data.getBlockId(), provider.modLoc("block/" + data.getBlockId()));
        }
    }

    /**
     * 注册方块的方向和模型
     * 默认使用  provider.simpleBlock 注册无方向的模型
     *
     * models	获取BlockModelProvider用于生成物品块模型。
     * itemModels	获取ItemModelProvider用于生成物品块模型。
     * modLoc	ResourceLocation为给定 mod id 的命名空间中的路径创建一个。
     * mcLoc	ResourceLocation为命名空间中的路径创建minecraft。
     * blockTexture	textures/block引用与块同名的纹理。
     * simpleBlockItem	根据给定的模型文件为某个块创建一个物品模型。
     * simpleBlockWithItem	为一个块模型和一个物品模型创建单个块状态，并使用块模型作为其父模型。
     */
    public void registerStatesAndModel(BlockStates provider) {
        if (data.getBlock() instanceof SlabBlock block){
            provider.slabBlock(block);
            return;
        }
        if (data.getBlock() instanceof IBaseBlock block && block.isSlabBlock()){
            provider.simpleSlabBlock(data.getBlock());
            return;
        }
        provider.simpleBlock(data.getBlock());
    }

    /**
     * 注册方块的掉落
     * <p>
     * 注册能保修NBT的凋落物
     * createStandardTable(
     * "inventory_block",
     * ModBlocks.INVENTORY_BLOCK.get(),
     * ModBlockEntities.INVENTORY_BLOCK_ENTITY.get()
     * )
     */
    public void registerLootTable(LootTableProvider provider) {
        provider.createSimpleTable(data.getBlockId(), data.getBlock());
    }

    /**
     * 合成表
     * @param recipes 只提供了简单的合成表，如果需要更多的合成表请使用静态方法
     *
     */
    public void registerBlockItemRecipe(Recipes recipes) {

    }
}
