package io.github.zimoyin.zhenfa.block;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData;
import io.github.zimoyin.zhenfa.datagen.provider.BlockStates;
import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.item.ColorableItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.List;

/**
 * @author : zimo
 * &#064;date : 2025/03/24
 */
public class BaseZhenfaActionBlock extends BaseBlock {
    private static final String BLOCK_NAME = "boundary";

    public BaseZhenfaActionBlock(String name) {
        super(Properties.of(Material.STONE).noOcclusion().lightLevel(state -> 9));
        setBlockName(name);
    }

    public BaseZhenfaActionBlock(Properties properties, String name) {
        super(properties.noOcclusion());
        setBlockName(name);
    }

    // 允许透明度计算
    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.45f;  // 完全透光（可调）
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;  // 允许天光穿透
    }

    @Override
    public boolean isSlabBlock() {
        return true;
    }


    @Override
    public BlockItem getBlockItem() {
        return new ColorableItem(this, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
    }

    /**
     * 跳过连接处渲染
     */
    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
        return pAdjacentBlockState.is(this);
    }

    public void action(Level level, List<BlockState> states, List<ZhenfaTargetBlock.TargetType> targetTypes, ZhenfaCoreBlock.ZhenfaCoreBlockEntity coreBlockEntity) {

    }

    public RenderType getRenderType() {
        return RenderType.translucent();
    }


    public void registerBlockItemRecipe(Recipes recipes) {

    }

    public static class ActionBlockGeneratedData extends BaseGeneratedBlockData {
        public ActionBlockGeneratedData(Data data) {
            super(data);
        }

        @Override
        public void registerStatesAndModel(BlockStates provider) {
            provider.simpleSlabBlock(data.getBlock(), provider.modLoc("block/" + BLOCK_NAME));
        }

        @Override
        public void registerBlockItemRecipe(Recipes recipes) {
            if (data.getBlock() instanceof BaseZhenfaActionBlock block) {
                block.registerBlockItemRecipe(recipes);
            }
        }
    }
}
