package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;
import static io.github.zimoyin.zhenfa.block.base.BlockRegterTables.getDataList;

/**
 * Tag 的作用类似于矿物辞典，但又不完全相同。
 */
public class EBlockTagsProvider extends BlockTagsProvider {

    public EBlockTagsProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MOD_ID, helper);
    }

    @Override
    protected void addTags() {
        for (BaseBlock.Data data : getDataList()) {
            if (data.isGenerated()) {
                for (TagKey<Block> tag : data.getGeneratedData().tags()) {
                    tag(tag).add(data.getBlock());
                }
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "Tutorial Tags";
    }
}