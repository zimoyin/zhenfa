package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;


/**
 * 用于生成物品模型
 */
public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DataGenModId, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (BaseBlock.Data data : BlockRegterTables.getDataList()) {
            if (data.isGenerated()) data.getGeneratedData().registerItemModel(this);
        }
    }
}