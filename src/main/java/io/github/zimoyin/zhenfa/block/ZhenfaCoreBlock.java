package io.github.zimoyin.zhenfa.block;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlockEntity;
import io.github.zimoyin.zhenfa.block.base.BaseEntityBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.utils.ext.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
@BlockRegterTables.RegisterBlock(value = "core", blockEntity = ZhenfaCoreBlock.ZhenfaCoreBlockEntity.class)
public class ZhenfaCoreBlock extends BaseEntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ZhenfaCoreBlock() {
        super(Material.STONE);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            PlayerUtils.sendMessageTo(pPlayer, "你好世界");
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    public static class ZhenfaCoreBlockEntity extends BaseBlockEntity {
        public ZhenfaCoreBlockEntity(BlockPos worldPosition, BlockState blockState) {
            super(worldPosition, blockState);
        }
    }
}
