package io.github.zimoyin.zhenfa.datagen.provider;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
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
            if (data.isGenerated()) {
                try {
                    data.getGeneratedData().registerStatesAndModel(this);
                } catch (Exception e) {
                    if (e.getMessage().contains("does not exist in any known resource pack")) {
                        LOGGER.error("Failed registerStatesAndModels: {}", e.getMessage());
                    } else {
                        LOGGER.error("Failed registerStatesAndModels: {}", e.getMessage(), e);
                    }
                }
            }
        }
    }

    public void slabBlock(SlabBlock block) {
        slabBlock(block, blockTexture(block), blockTexture(block));
    }

    /**
     * 使用该方法是有风险的，如果你需要使用该方法那么一定要参考 SlabBlock 实现
     */
    public void slabBlock(Block block) {
        slabBlock(block, blockTexture(block), blockTexture(block));
    }

    /**
     * 使用该方法是有风险的，如果你需要使用该方法那么一定要参考 SlabBlock 实现
     */
    public void slabBlock(Block block, ResourceLocation doubleslab, ResourceLocation texture) {
        slabBlock(block, doubleslab, texture, texture, texture);
    }

    /**
     * 使用该方法是有风险的，如果你需要使用该方法那么一定要参考 SlabBlock 实现
     */
    public void slabBlock(Block block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        slabBlock(block, models().slab(name(block), side, bottom, top), models().slabTop(name(block) + "_top", side, bottom, top), models().getExistingFile(doubleslab));
    }

    /**
     * 使用该方法是有风险的，如果你需要使用该方法那么一定要参考 SlabBlock 实现
     */
    public void slabBlock(Block block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
        getVariantBuilder(block)
                .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom))
                .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top))
                .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
    }

    private String name(Block block) {
        return block.getRegistryName().getPath();
    }
}