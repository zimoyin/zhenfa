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
// 没有该注解程序无法扫描到此类就无法加载类无法执行 Static
@BlockRegterTables.RegisterBlock(isRegister = false)
public class Test4Blocks {
    // register 有众多重载，除了 register(Class) 是自动调用以外其他的都是可以调用的
    // 不是通过 register(Class) 注册的无法通过 getData(cls) 获取数据。但是 getDataList 还是会包含
    public static final BaseBlock.Data TEST_BLOCK_DATA = BlockRegterTables.register("ctestblock4");
    // 注册一个带有 generatedData class 的注册方式
    public static final BaseBlock.Data TEST_BLOCK_DATA2 = BlockRegterTables.register("ctestblock5",  Test4GeneratedBlockData.class);
    // 注册一个带有 generatedData instance 的注册方式
    public static final BaseBlock.Data TEST_BLOCK_DATA3 = BlockRegterTables.register("ctestblock6", data -> new BaseGeneratedBlockData(data){
        @Override
        public List<Lang> lang() {
            return List.of(new Lang(Lang.LangType.ZH_CN,"测试独立注册 id3"));
        }
    });



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
