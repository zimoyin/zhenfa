package io.github.zimoyin.zhenfa.datagen.provider;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;


/**
 * 用于生成物品模型
 */
public class ItemModels extends ItemModelProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DataGenModId, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (BaseBlock.Data data : BlockRegterTables.getDataList()) {
            if (data.isGenerated()) {
                try {
                    data.getGeneratedData().registerItemModel(this);
                }catch (Exception e){
                    LOGGER.error("Failed registerItemModel: {}",e.getMessage(),e);
                }
            }
        }

        for (BaseItem.Data data : ItemRegterTables.getDataList()) {
            if (data.isGenerated()){
                try {
                    data.getGeneratedData().registerItemModel(this);
                }catch (Exception e){
                    LOGGER.error("Failed registerItemModel: {}",e.getMessage(),e);
                }
            }
        }
    }
}