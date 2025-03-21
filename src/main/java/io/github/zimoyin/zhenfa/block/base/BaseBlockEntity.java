package io.github.zimoyin.zhenfa.block.base;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 方块实体基础类, 该类描述一个确实存在的实体。理解为一个方块的数据集并非是一个具体的方块
 *
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public class BaseBlockEntity extends BlockEntity {

    public BaseBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos worldPosition, BlockState blockState) {
        super(checkEntityType(type), worldPosition, blockState);
    }

    /**
     * 每一个继承的类, 必须要重写这个构造方法，必须调用 super(getEntityType(当前类.class), worldPosition, blockState)
     * 如果你不重写该构造那么框架会自行搜索，但是搜索时机无法确定，可能会导致null
     */
    public BaseBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(checkEntityType(null), worldPosition, blockState);
    }


    private static BlockEntityType<? extends BlockEntity> checkEntityType(BlockEntityType<? extends BlockEntity> type) {
        if (type != null) return type;
        throw new IllegalArgumentException("Failed to create entity type; Entity type is null");
    }

    /**
     * 每 tick 都会调用，仅在客户端上执行
     */
    public void clientTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {

    }

    /**
     * 每 tick 都会调用，仅在服务端上执行
     */
    public void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {

    }

    /**
     * 获取实体类型。用于作为 BaseBlockEntity 的构造函数参数。
     */
    public static BlockEntityType<?> getEntityType(Class<? extends BlockEntity> cls) {
        return BlockRegterTables.getEntityRegistryObject(cls).get();
    }

    public static <T extends BlockEntity> Optional<T> asEntity(Class<T> cls, BlockEntity e) {
        if (cls.isInstance(e)) {
            return Optional.of(cls.cast(e));
        }
        return Optional.empty();
    }
}
