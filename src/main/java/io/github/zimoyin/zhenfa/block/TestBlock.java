package io.github.zimoyin.zhenfa.block;


import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

import java.util.List;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
@BlockRegterTables.RegisterBlock(value = "ctestblock1", data = true,generatedData = TestBlock.GeneratedData.class)
public class TestBlock extends BaseBlock {
    // 会在合适的时间注入
    public static BaseBlock.Data RegisterBlockData = null;

    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
        setBlockName("Test");
//        setHarvestLevel(3);
//        setToolType(ToolType.PICKAXE);
    }


    /**
     * 继承的 JSON 生成器
     */
    public static class GeneratedData extends BaseGeneratedBlockData {
        public GeneratedData(Data data) {
            super(data);
        }

        @Override
        public List<TagKey<Block>> tags() {
            return List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
        }

        @Override
        public List<Lang> lang() {
            return List.of(
                    new Lang(Lang.LangType.ZH_CN,"简单测试方块")
//                    new Lang(Lang.LangType.ZH_CN, data.getBlock().getDescriptionId(), "简单测试方块")
            );
        }
    }
}
