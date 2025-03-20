package io.github.zimoyin.zhenfa.datagen.provider;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

/**
 * 方块状态
 */
public class BlockStates extends BlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, DataGenModId, helper);
    }
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    protected void registerStatesAndModels() {
        for (BaseBlock.Data data : BlockRegterTables.getDataList()) {
           if (data.isGenerated()){
               try {
                   data.getGeneratedData().registerStatesAndModel(this);
               }catch (Exception e){
                   if (e.getMessage().contains("does not exist in any known resource pack")){
                       LOGGER.error("Failed registerStatesAndModels: {}",e.getMessage());
                   }else {
                       LOGGER.error("Failed registerStatesAndModels: {}",e.getMessage(),e);
                   }
               }
           }
        }
    }
}