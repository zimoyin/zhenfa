package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import net.minecraft.data.DataGenerator;

import static io.github.zimoyin.zhenfa.block.base.BlockRegterTables.getDataList;

public class LootTableProvider extends BaseLootTableProvider {

    public LootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        for (BaseBlock.Data data : getDataList()) {
            if (data.isGenerated()) {
                data.getGeneratedData().registerLootTable(this);
            }
        }
    }
}