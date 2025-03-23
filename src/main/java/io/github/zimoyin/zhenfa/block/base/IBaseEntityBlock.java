package io.github.zimoyin.zhenfa.block.base;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.utils.ext.ClassUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author : zimo
 * &#064;date : 2025/03/23
 */
public interface IBaseEntityBlock extends EntityBlock, IBaseBlock {
    Logger LOGGER = LogUtils.getLogger();

    default BlockItem getBlockItem(Block block) {
        return new BlockItem(block, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
    }

    default Class<? extends BlockEntity> getEntityClazz() {
        Class<? extends BlockEntity> entityClazz = null;
        BlockRegisterTables.RegisterBlock annotation = this.getClass().getAnnotation(BlockRegisterTables.RegisterBlock.class);
        if (annotation == null) {
            LOGGER.error("The Class {} Not Found @{}", this.getClass().getName(), BlockRegisterTables.RegisterBlock.class.getSimpleName());
            return entityClazz;
        }
        entityClazz = annotation.blockEntity();
        if (entityClazz == BlockEntity.class) {
            LOGGER.error("The @{} Not Found blockEntity field", BlockRegisterTables.RegisterBlock.class.getSimpleName());
            entityClazz = BaseBlockEntity.class;
        }
        return entityClazz;
    }


    @Override
    default BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        BlockEntity blockEntity = null;
        try {
            Constructor<? extends BlockEntity> constructor = getEntityClazz().getConstructor(BlockPos.class, BlockState.class);
            blockEntity = constructor.newInstance(pos, state);
        } catch (Exception e) {
            try {
                Constructor<?> constructor = ClassUtils.findConstructor(getEntityClazz(), BlockEntityType.class, BlockPos.class, BlockState.class);
                blockEntity = (BlockEntity) constructor.newInstance(BaseBlockEntity.getEntityType(getEntityClazz()), pos, state);
            } catch (Exception ex) {
                LOGGER.error("Failed to create BlockEntity; Class {}", getEntityClazz().getName(), ex);
            }
        }
        return blockEntity;
    }

    default Method getClientTickMethod() throws NoSuchMethodException {
        return getEntityClazz().getMethod("clientTick", Level.class, BlockPos.class, BlockState.class, BlockEntity.class);
    }

    default Method getServerTickMethod() throws NoSuchMethodException {
        return getEntityClazz().getMethod("serverTick", Level.class, BlockPos.class, BlockState.class, BlockEntity.class);
    }

    /**
     * 默认情况下，方块实体并不具备跟随游戏刻刷新（亦即方块实体刻）的能力，
     * 若要获得此能力，方块实体所在的那个方块需要明确声明一个所谓的「Ticker」，亦即 BlockEntityTicker<?>。
     * 这通过覆盖 EntityBlock 的 getTicker 方法实现。
     * <p>
     * 此外，你可以根据 Level 是在逻辑服务器上还是逻辑客户端上来返回不同的 Ticker。
     */
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (getEntityClazz() == null) return null;
        BlockEntityType<?> entityType = BaseBlockEntity.getEntityType(getEntityClazz());
        BlockEntityTicker<T> pTicker = pLevel.isClientSide ? this::clientTick : this::serverTick;
        return entityType == pBlockEntityType ? pTicker : null;
    }

    /**
     * 每 tick 都会调用，仅在客户端上执行
     */
    default void clientTick(Level level, BlockPos pos, BlockState state, BlockEntity o) {
        // 执行类实体中的 clientTick 方法
        try {
            BlockEntity entity = BaseBlockEntity.getEntityType(getEntityClazz()).getBlockEntity(level, pos);
            if (entity != null) getClientTickMethod().invoke(entity, level, pos, state, o);
            else LOGGER.warn("BaseBlockEntity serverTick method entity is null in {}", getEntityClazz().getName());
        } catch (Exception e) {
            LOGGER.error("BaseBlockEntity clientTick method exception in {}", getEntityClazz().getName(), e);
        }
    }

    /**
     * 每 tick 都会调用，仅在服务端上执行
     */
    default void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {
        // 执行类实体中的 serverTick 方法
        try {
            BlockEntity entity = BaseBlockEntity.getEntityType(getEntityClazz()).getBlockEntity(level, pos);
            if (entity != null) getServerTickMethod().invoke(entity, level, pos, state, e);
            else LOGGER.warn("BaseBlockEntity serverTick method entity is null in {}", getEntityClazz().getName());
        } catch (Exception ex) {
            LOGGER.error("BaseBlockEntity serverTick method exception in {}", getEntityClazz().getName(), ex);
        }
    }


}
