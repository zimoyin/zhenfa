package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

/**
 * 方块状态
 */
public class BlockStates extends BlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, DataGenModId, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (BaseBlock.Data data : BlockRegterTables.getDataList()) {
           if (data.isGenerated()) data.getGeneratedData().registerStatesAndModel(this);
        }
    }
}