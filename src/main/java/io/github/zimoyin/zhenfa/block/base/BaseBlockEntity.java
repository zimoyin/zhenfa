package io.github.zimoyin.zhenfa.block.base;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 方块实体基础类, 该类描述一个确实存在的实体。理解为一个方块的数据集并非是一个具体的方块
 *
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public abstract class BaseBlockEntity extends BlockEntity {
    public BaseBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public BaseBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(null, worldPosition, blockState);
    }

    /**
     * 获取实体类型。用于作为 BaseBlockEntity 的构造函数参数。
     */
    public static BlockEntityType<?> getEntityType(Class<? extends BlockEntity> cls) {
        return BlockRegterTables.getEntityRegistryObject(cls).get();
    }
}
