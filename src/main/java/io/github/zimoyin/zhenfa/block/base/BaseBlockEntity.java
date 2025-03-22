package io.github.zimoyin.zhenfa.block.base;

import io.github.zimoyin.zhenfa.utils.ext.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

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
     * 必须由子类重写的构造方法，需调用 {@code super(getEntityType(子类.class), worldPosition, blockState)}。
     * 若未重写，框架会尝试动态解析类型，但可能因注册时机问题导致空指针。
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
     * 服务端每 tick 调用（仅在服务端执行）。
     * 触发数据同步检查，需确保子类逻辑通过 {@link #needSynchronizeTick(BaseBlockEntity)} 执行同步。
     */
    public void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {
        asEntity(this.getClass(), e).ifPresent(this::needSynchronizeTick);
    }

    /**
     * 同步标志位。当为 {@code true} 时，下一游戏刻会强制同步数据到客户端。
     * 避免在未初始化完成（如 {@link #load(CompoundTag)}）时直接操作网络同步。
     */
    protected boolean isNeedSynchronizeFlag = false;


    /**
     * 检查是否需要同步数据到客户端。若标志位为 {@code true}，执行同步并重置标志。
     * 通过 {@link #serverTick} 每 tick 调用，确保数据可以正确同步
     *
     * @param baseBlockEntity 当前方块实体实例（通过反射安全转换类型）
     */
    protected void needSynchronizeTick(BaseBlockEntity baseBlockEntity) {
        if (!isNeedSynchronizeFlag) return;
        if (synchronizeNow()) { // 仅在同步成功时重置标志位
            isNeedSynchronizeFlag = false;
        }
    }

    /**
     * 立即同步数据到客户端。包含以下操作：
     * 1. 调用 {@link #setChanged()} 标记数据已修改，确保持久化到磁盘。
     * 2. 生成 {@link ClientboundBlockEntityDataPacket} 数据包。
     * 3. 发送给同一区块内的所有玩家（通过 {@link ChunkMap} 管理玩家列表）。
     * <p>
     * 注意：必须在服务端主线程调用，且确保 {@link #level} 已初始化。
     *
     * @return 是否成功执行同步操作。如果 {@link #level} 为空或客户端，则返回 {@code false}。
     */
    protected boolean synchronizeNow() {
        if (level != null && !level.isClientSide) {
            // 延迟发包，即 当其他玩家进去区块加载到本方块时，立即发送数据包
            setChanged();
            // 立即发包
            ClientboundBlockEntityDataPacket p = ClientboundBlockEntityDataPacket.create(this);
            PlayerUtils.getPlayers(level, getBlockPos()).forEach(k -> k.connection.send(p));
            return true;
        }
        return false;
    }

    /**
     * 从 NBT 加载数据时触发（服务端加载存档/客户端接收网络包）。
     * 此时 {@link #level} 可能未初始化，因此通过 {@link #isNeedSynchronizeFlag} 标记延迟同步。
     * 如果不想在本类中进行同步，重写 needSynchronizeTick 后即可对此产生影响
     */
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        isNeedSynchronizeFlag = true; // 标记需要同步，由后续 tick 处理
    }

    /**
     * 通过反射获取方块实体的注册类型。
     * 需确保 {@link BlockRegterTables} 已正确注册该类型。
     *
     * @param cls 方块实体类（如 {@code MyBlockEntity.class}）
     * @return 对应的 {@link BlockEntityType}
     */
    public static BlockEntityType<?> getEntityType(Class<? extends BlockEntity> cls) {
        return BlockRegterTables.getEntityRegistryObject(cls).get();
    }

    /**
     * 持久化时触发（服务端存档/客户端发送网络包）。
     */
    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }

    /**
     * 安全转换方块实体类型。
     *
     * @param cls 要转换的目标类
     * @param e   原始方块实体
     * @return 转换成功返回 {@link Optional#of(Object)}，否则 {@link Optional#empty()}
     */
    public static <T extends BlockEntity> Optional<T> asEntity(Class<T> cls, BlockEntity e) {
        if (cls.isInstance(e)) {
            return Optional.of(cls.cast(e));
        }
        return Optional.empty();
    }
}
