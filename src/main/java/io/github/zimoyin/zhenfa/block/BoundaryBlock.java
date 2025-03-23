package io.github.zimoyin.zhenfa.block;

import io.github.zimoyin.zhenfa.block.base.*;
import io.github.zimoyin.zhenfa.datagen.provider.BlockStates;
import io.github.zimoyin.zhenfa.utils.ext.CompoundTagUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.SlabBlock.TYPE;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
@BlockRegisterTables.RegisterBlock(value = "boundary",
        data = true,
        blockEntity = BoundaryBlock.BoundaryBlockEntity.class,
        generatedData = BoundaryBlock.BoundaryBlockGenerator.class
)
public class BoundaryBlock extends BaseBlock implements IBaseEntityBlock {
    public static BaseBlock.Data data;

    public BoundaryBlock() {
        super(Properties.of(Material.STONE).noOcclusion().lightLevel(state -> 9));
        setBlockName("Boundary Block");
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

    /**
     * 跳过连接处渲染
     */
    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
        return pAdjacentBlockState.is(this) ? true : super.skipRendering(pState, pAdjacentBlockState, pSide);
    }

    public RenderType getRenderType(){
        return RenderType.translucent();
    }


    public static class BoundaryBlockEntity extends BaseBlockEntity {
        public BoundaryBlockEntity(BlockPos worldPosition, BlockState blockState) {
            super(getEntityType(BoundaryBlockEntity.class), worldPosition, blockState);
        }

        private BlockPos coreBlockPos;

        public void setCoreBlockPos(BlockPos coreBlockPos) {
            if (coreBlockPos == this.coreBlockPos) return;
            synchronizeNow();
            this.coreBlockPos = coreBlockPos;
        }

        public BlockPos getCoreBlockPos() {
            return coreBlockPos;
        }

        /**
         * 不论是网络包还是持久化都会调用这个方法，从 nbt 加载数据（服务端（持久化）/客户端（网络包））
         */
        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);
            coreBlockPos = CompoundTagUtils.getBlockPos(pTag, "coreBlockPos");
        }


        /**
         * 持久化会调用这个方法：也就是说，你退出游戏再进入游戏，数据不会消失就是它的作用（服务端）
         */
        @Override
        protected void saveAdditional(@NotNull CompoundTag pTag) {
            super.saveAdditional(pTag);
            CompoundTagUtils.putBlockPos(pTag, "coreBlockPos", coreBlockPos);
        }

        /**
         * 网络包会调用这个方法（服务端）
         */
        @Override
        public @NotNull CompoundTag getUpdateTag() {
            CompoundTag tag = new CompoundTag();
            CompoundTagUtils.putBlockPos(tag, "coreBlockPos", coreBlockPos);
            return tag;
        }
    }

    public static class BoundaryBlockGenerator extends BaseGeneratedBlockData {
        public BoundaryBlockGenerator(BaseBlock.Data data) {
            super(data);
        }

        @Override
        public void registerStatesAndModel(BlockStates provider) {
//            provider.slabBlock(data.getBlock());
            provider.slabBlock(data.getBlock());
        }

    }
}
