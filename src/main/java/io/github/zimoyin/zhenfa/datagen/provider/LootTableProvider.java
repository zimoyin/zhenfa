package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.zimoyin.zhenfa.block.base.BlockRegterTables.getDataList;

public class LootTableProvider extends BaseLootTableProvider {

    public LootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void addTables() {
        for (BaseBlock.Data data : getDataList()) {
            if (data.isGenerated()) {
                try {
                    data.getGeneratedData().registerLootTable(this);
                } catch (Exception e) {
                    LOGGER.error("Failed registerLootTable: {}", e.getMessage(), e);
                 }
            }
        }
    }
}