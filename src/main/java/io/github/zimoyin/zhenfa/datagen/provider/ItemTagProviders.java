package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

public class ItemTagProviders extends ItemTagsProvider {

    public ItemTagProviders(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(generator, blockTags, DataGenModId, helper);
    }
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void addTags() {
        for (BaseBlock.Data data : BlockRegterTables.getDataList()) {
            if (data.isGenerated()) {
                for (TagKey<Item> itemTag : data.getGeneratedData().itemTags()) {
                    try {
                        tag(itemTag).add(data.getBlockItem());
                    }catch (Exception e){
                        LOGGER.error("Failed addTags: {}", e.getMessage(), e);
                    }
                }
            }
        }
        for (BaseItem.Data data : ItemRegterTables.getDataList()) {
            if (data.isGenerated()) {
                for (TagKey<Item> itemTag : data.getGeneratedData().tags()) {
                    try {
                        tag(itemTag).add(data.getItem());
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