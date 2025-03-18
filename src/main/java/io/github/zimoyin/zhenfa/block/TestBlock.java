package io.github.zimoyin.zhenfa.block;


import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

import java.util.List;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
@BlockRegterTables.RegisterBlock(value = "test", generatedData = TestBlock.GeneratedData.class)
public class TestBlock extends BaseBlock {

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
    }
}
