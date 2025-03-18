package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import static io.github.zimoyin.zhenfa.block.base.BlockRegterTables.getDataList;
import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

public class ItemTags extends ItemTagsProvider {

    public ItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(generator, blockTags, DataGenModId, helper);
    }

    @Override
    protected void addTags() {
        for (BaseBlock.Data data : getDataList()) {
            if (data.isGenerated()) {
                for (TagKey<Item> itemTag : data.getGeneratedData().itemTags()) {
                    tag(itemTag).add(data.getBlockItem());
                }
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "Tutorial Tags";
    }
}