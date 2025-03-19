package io.github.zimoyin.zhenfa.block;


import io.github.zimoyin.zhenfa.block.base.BaseBlockEntity;
import io.github.zimoyin.zhenfa.block.base.BaseEntityBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
@BlockRegterTables.RegisterBlock(value = "test2", blockEntity = Test2Block.Test2BlockEntity.class)
public class Test2Block extends BaseEntityBlock {

    public Test2Block() {
        super(Properties.of(Material.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
    }


    /**
     * 右键这个方块的时候会调用
     */
    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof Test2BlockEntity tbe) {
            tbe.use(pPlayer);
            pPlayer.sendMessage(new TextComponent("使用方块的手：" + pHand.name()), pPlayer.getUUID());
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    /**
     * 方块实体
     */
    public static class Test2BlockEntity extends BaseBlockEntity {
        /**
         * 必须要有这个构造方法
         */
        public Test2BlockEntity(BlockPos worldPosition, BlockState blockState) {
            // getEntityType(Test2BlockEntity.class) 是必须的
            super(getEntityType(Test2BlockEntity.class), worldPosition, blockState);
        }

        int count;

        boolean initialized = false;
        int serverMsg;
        int clientMsg;

        public void use(Player player) {
            if (getLevel().isClientSide()) {
                if (!initialized) {
                    clientMsg = 1001;
                    initialized = true;
                }

                // 调试信息，展示了不同步状态下的信息
                player.sendMessage(new TextComponent("仅在客户端的内容：" + clientMsg), player.getUUID());
                player.sendMessage(new TextComponent("双端内容客户端：" + count), player.getUUID());
            } else {
                if (!initialized) {
                    serverMsg = 2332;
                    initialized = true;
                }

                // 调试信息，展示了不同步状态下的信息
                player.sendMessage(new TextComponent("仅在服务器端的内容：" + serverMsg), player.getUUID());

                count++;
                player.sendMessage(new TextComponent("双端内容服务端：" + count), player.getUUID());

                sync();
            }
        }

        /**
         * 同步的方法，看不懂就照抄，调用它会调用下面的 getUpdateTag
         * 同步操作 ：
         *      调用 sync() 方法发送数据包到客户端，更新客户端的数据（如 count）。
         *      sync() 内部通过 ClientboundBlockEntityDataPacket 实现网络同步。
         * 状态重置 ：同步完成后将 needSync 设为 false，避免重复同步。
         */
        protected void sync() {
            if (level != null && !level.isClientSide) {
                ClientboundBlockEntityDataPacket p = ClientboundBlockEntityDataPacket.create(this);
                ((ServerLevel) this.level).getChunkSource().chunkMap.getPlayers(new ChunkPos(getBlockPos()), false)
                        .forEach(k -> k.connection.send(p));

                // 用来告知 mc，“这个方块得保存”的东西，你也可以在其他地方调用
                setChanged();
            }
        }

        /**
         * 网络包会调用这个方法（服务端）
         */
        @Override
        public CompoundTag getUpdateTag() {
            CompoundTag result = new CompoundTag();

            result.putInt("count", count);

            return result;
        }

        /**
         * 持久化会调用这个方法：也就是说，你退出游戏再进入游戏，数据不会消失就是它的作用（服务端）
         */
        @Override
        protected void saveAdditional(CompoundTag pTag) {
            pTag.putInt("count", count);
        }

        /**
         * 不论是网络包还是持久化都会调用这个方法，从 nbt 加载数据（服务端（持久化）/客户端（网络包））
         * 注意，此时的 BlockEntity *没有* 初始化 level，所以此时 getLevel 一定会报错
         * 但服务端的内容不会自动同步到客户端，怎么办呢？见下方的解决方法
         */
        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);

            count = pTag.getInt("count");

            // 虽然此时的客户端上的 needSync 也被修改了，但由于这个字段不会在客户端使用所以无需担心
            needSync = true;
        }


        /**
         * 每 tick 都会调用，仅在服务端上执行
         */
        @Override
        public void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {
            asEntity(this.getClass(),e).ifPresent(Test2BlockEntity::syncTick);
        }


        // 同步的解决方案

        boolean needSync;

        /**
         * 数据同步需求 ：服务端数据（如 count 字段）需要同步到客户端，但直接操作可能引发线程安全或状态不一致的问题。
         * needSync 标志 ：用于标记是否需要触发同步。
         */
        void syncTick() {
            if (needSync) {
                sync();
                needSync = false;
            }
        }
    }
}
