package io.github.zimoyin.zhenfa.block;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * 所有基础方块的集合类，对于使用注解方式注册普通方块太过分散和麻烦。因此可以在一个类中进行注册多个方块
 *
 * @author : zimo
 * &#064;date : 2025/03/19
 */
// 该注解仅仅是让框架将类读取到内存，从而让类自动去执行静态方法进行自己的初始化
@BlockRegterTables.RegisterBlock
public class Test4Blocks {

    public static final BaseBlock.Data TEST_BLOCK_DATA = BlockRegterTables.register("id", null, null, Test4GeneratedBlockData.class);
    public static final BaseBlock.Data TEST_BLOCK_DATA2 = BlockRegterTables.register("id2", null, null, Test4GeneratedBlockData.class);

    public static class Test4GeneratedBlockData extends BaseGeneratedBlockData {
        public Test4GeneratedBlockData(BaseBlock.Data data) {
            super(data);
        }

        @Override
        public List<Lang> lang() {
            return List.of(
                    new Lang(Lang.LangType.ZH_CN, TEST_BLOCK_DATA.getBlock(), "测试集体注册 id1"),
                    new Lang(Lang.LangType.ZH_CN, TEST_BLOCK_DATA2.getBlock(), "测试集体注册 id2")
            );
        }

        @Override
        public List<TagKey<Block>> tags() {
            // 注册标签需要石头稿子及以上才能挖掘
            return List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        }
    }
}
