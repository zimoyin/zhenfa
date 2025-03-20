package io.github.zimoyin.zhenfa.datagen.provider;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;
import static io.github.zimoyin.zhenfa.block.base.BlockRegterTables.getDataList;

/**
 * Tag 的作用类似于矿物辞典，但又不完全相同。
 */
public class BlockTagProviders extends BlockTagsProvider {

    public BlockTagProviders(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MOD_ID, helper);
    }
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    protected void addTags() {
        for (BaseBlock.Data data : getDataList()) {
            if (data.isGenerated()) {
                for (TagKey<Block> tag : data.getGeneratedData().tags()) {
                    try {
                        tag(tag).add(data.getBlock());
                    }catch (Exception e){
                        LOGGER.error("Failed addTags: {}", e.getMessage(), e);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "Tutorial Tags";
    }
}