package io.github.zimoyin.zhenfa.block;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseEntityBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.utils.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
@BlockRegterTables.RegisterBlock("core")
public class ZhenfaCoreBlock extends BaseEntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public ZhenfaCoreBlock() {
        super(Material.STONE);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide){
            LOGGER.info("ni");
            PlayerUtils.sendMessageTo(pPlayer,"你好世界");
            return InteractionResult.SUCCESS;
        }else{
            return InteractionResult.CONSUME;
        }
    }


}
