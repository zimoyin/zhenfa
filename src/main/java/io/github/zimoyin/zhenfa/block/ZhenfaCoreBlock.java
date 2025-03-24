package io.github.zimoyin.zhenfa.block;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseBlockEntity;
import io.github.zimoyin.zhenfa.block.base.BaseEntityBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegisterTables;
import io.github.zimoyin.zhenfa.utils.ext.CompoundTagUtils;
import io.github.zimoyin.zhenfa.utils.ext.ParticlesUtils;
import io.github.zimoyin.zhenfa.utils.ext.PlayerUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
@BlockRegisterTables.RegisterBlock(value = "core", data = true, blockEntity = ZhenfaCoreBlock.ZhenfaCoreBlockEntity.class)
public class ZhenfaCoreBlock extends BaseEntityBlock {
    public static BaseBlock.Data data;

    private static final Logger LOGGER = LogUtils.getLogger();

    public ZhenfaCoreBlock() {
        super(Properties.of(Material.METAL));
        setBlockName("Zhenfa Core");
    }

    public Optional<ZhenfaCoreBlockEntity> getBlockEntity(Level level, BlockPos pos) {
        return super.getBlockEntity(ZhenfaCoreBlockEntity.class, level, pos);
    }


    public RenderType getRenderType() {
        return RenderType.translucent();
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        getBlockEntity(pLevel, pPos).ifPresent(blockEntity -> {
            blockEntity.setLivingEntity(pPlacer);
        });
    }

    /**
     * 核心方块 TileEntity
     * <p>
     * 玩家右键核心方块时，将以该方块为起点进行洪水填充检测（仅四个正方向），判断周围是否由标记方块构成有效的封闭边界。
     * 检测规则：
     * <ul>
     *   <li>以核心方块为填充起点，向四个方向（水平方向：东、西、南、北）扩散。</li>
     *   <li>如果扩散过程中遇到标记方块，则记录该位置为边界，但不继续填充进入该块内。</li>
     *   <li>若填充过程中任一水域（内部区域）距离核心方块达到预设最大距离，则认为水域外渗，
     *       区域不被有效包围；</li>
     *   <li>最终只有当填充的所有位置均在最大距离内，且周围的边界块为连续有效的封闭障碍时，
     *       才认为核心方块被有效围住。</li>
     * </ul>
     * 成功检测后，将记录内部水域和边界块位置，并计算区域范围（AABB）。同时提供碰撞检测方法，
     * 检测区域内是否存在方块、掉落物、实体、箭、雨滴、雪花、流体等对象。
     * </p>
     */
    public static class ZhenfaCoreBlockEntity extends BaseBlockEntity {
        private static final Logger LOGGER = LogUtils.getLogger();

        // 记录作为边界的标记方块位置（仅当其直接阻隔水域填充时才算有效）
        private final Set<BlockPos> boundaryPositions = new HashSet<>();
        // 记录内部水域（填充区域）位置
        private final Set<BlockPos> filledPositions = new HashSet<>();
        // 阵法区域范围
        // 最大检测距离（单位：方块），超过该距离认为区域未被封闭
        private static final int MAX_DISTANCE = 16;

        private final Block BOUNDARY_MARKER = BoundaryBlock.data.getBlock();
        public final Block CORE_BLOCK = data.getBlock();
        private UUID playerUUID = null;

        public ZhenfaCoreBlockEntity(BlockPos worldPosition, BlockState blockState) {
            super(getEntityType(ZhenfaCoreBlockEntity.class), worldPosition, blockState);
        }

        /**
         * 检测阵法是否完整包围核心方块。
         * <p>
         * 采用洪水填充算法（仅向四个正方向扩散）：
         * <ul>
         *   <li>以核心方块位置为起点进行填充。</li>
         *   <li>遇到标记方块时，不进入填充，但记录该标记块为边界，
         *       同时只有当该边界块正好阻隔了内部水域的进一步扩散时，才视为有效。</li>
         *   <li>若任一填充位置距离核心方块达到或超过 MAX_DISTANCE，则认为区域未封闭。</li>
         * </ul>
         * 成功检测后记录内部与边界位置，并计算区域 AABB 范围。
         * </p>
         *
         * @param level 当前世界（建议在服务端执行）
         * @return
         */
        public boolean detectFormation(Level level) {
            // 清空之前的数据
            boundaryPositions.clear();
            filledPositions.clear();

            BlockPos corePos = this.worldPosition;

            // 使用栈实现深度优先搜索（DFS），仅向四个正方向扩散
            Stack<BlockPos> stack = new Stack<>();
            stack.push(corePos);
            filledPositions.add(corePos);

            boolean isEnclosed = true;

            // 四个正方向（东、西、南、北）
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

            while (!stack.isEmpty()) {
                BlockPos current = stack.pop();

                // 如果当前填充位置距离核心过远，则视为水域外渗
                if (euclideanDistance(corePos, current) >= MAX_DISTANCE) {
                    isEnclosed = false;
                    break;
                }

                for (int[] offset : directions) {
                    BlockPos neighbor = current.offset(offset[0], 0, offset[1]);

                    // 如果已经访问过，跳过
                    if (filledPositions.contains(neighbor) || boundaryPositions.contains(neighbor)) {
                        continue;
                    }

                    // 如果邻居为标记方块，则视为边界，但只记录该块
                    if (isBoundaryBlock(level, neighbor)) {
                        // 检查有效性：该标记块必须正好阻隔水域扩散（即其一侧与当前水域相邻）
                        // 此处简单认为：只要是从内部填充过程中遇到的标记块，都算有效边界
                        boundaryPositions.add(neighbor);
                    } else {
                        // 非边界块视为内部水域，继续填充
                        filledPositions.add(neighbor);
                        stack.push(neighbor);
                    }
                }
            }

            // 若水域扩散未达到最大距离，说明核心方块被边界完整围住
            if (isEnclosed) {
                // 保存检测结果到 NBT
                setChanged();
                // 为边界方块添加主方块
                for (BlockPos pos : boundaryPositions) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity != null && entity instanceof BoundaryBlock.BoundaryBlockEntity e0)
                        e0.setCoreBlockPos(corePos);
                }
            }

            return isEnclosed;
        }

        /**
         * 计算核心方块在 XZ 平面上的直线距离
         *
         * @param core 核心方块位置
         * @param pos  目标位置
         * @return 直线距离（浮点数）
         */
        private double euclideanDistance(BlockPos core, BlockPos pos) {
            int dx = core.getX() - pos.getX();
            int dz = core.getZ() - pos.getZ();
            return Math.sqrt(dx * dx + dz * dz); // 使用勾股定理
        }

        /**
         * 判断指定位置是否为阵法标记方块。
         * <p>
         * 该方法根据方块类型或状态进行判断，此处示例假设标记方块为 ModBlocks.BOUNDARY_MARKER。
         * </p>
         *
         * @param level 当前世界
         * @param pos   需要检测的坐标
         * @return 如果该位置为标记方块返回 true，否则 false
         */
        private boolean isBoundaryBlock(Level level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            return state.getBlock() == BOUNDARY_MARKER;
        }

        /**
         * 根据给定的所有位置（包括内部水域与边界）计算区域的 AABB。
         * AABB 是一个规则矩形
         *
         * @param positions 坐标集合
         * @return 计算得到的 AABB 范围
         */
        private AABB calculateFormationAABB(Set<BlockPos> positions) {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (BlockPos pos : positions) {
                int x = pos.getX();
                int z = pos.getZ();
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
            }
            // Y 轴固定为核心方块所在层，高度为 1
            int y = worldPosition.getY();
            return new AABB(minX, y, minZ, maxX + 1, y + 1, maxZ + 1);
        }


        @Override
        @NotNull
        public CompoundTag getUpdateTag() {
            CompoundTag tag = super.getUpdateTag();
//            CompoundTagUtils.putObject(tag, "boundaryPositions", boundaryPositions);
//            CompoundTagUtils.putObject(tag, "filledPositions", filledPositions);
            CompoundTagUtils.putObject(tag, "player", playerUUID);
            return tag;
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
//            boundaryPositions.clear();
//            filledPositions.clear();
//            boundaryPositions.addAll(CompoundTagUtils.getListObject(tag, "boundaryPositions", BlockPos.class));
//            filledPositions.addAll(CompoundTagUtils.getListObject(tag, "filledPositions", BlockPos.class));
            playerUUID = CompoundTagUtils.getObject(tag, UUID.class, "player");
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag tag) {
            super.saveAdditional(tag);
//            CompoundTagUtils.putObject(tag, "boundaryPositions", boundaryPositions);
//            CompoundTagUtils.putObject(tag, "filledPositions", filledPositions);
            CompoundTagUtils.putObject(tag, "player", playerUUID);
        }

        public Player getOwner() {
            if (playerUUID == null) return null;
            return PlayerUtils.getPlayerByUUID(playerUUID);
        }

        // 粒子提示
        public void spawnParticlesAboveBlock(Level level, BlockPos pos) {
            Player livingEntity = getOwner();
            if (livingEntity == null) return;
            Item item = livingEntity.getMainHandItem().getItem();
            if (item.equals(CORE_BLOCK.asItem())) {
                ParticlesUtils.spawnParticlesAboveBlock(level, pos);
            }

            if (item.equals(BOUNDARY_MARKER.asItem())) {
                for (BlockPos bs : boundaryPositions) {
                    ParticlesUtils.spawnParticlesAboveBlock(level, bs);
                }
            }

            if (item.equals(Items.WATER_BUCKET)) {
                for (BlockPos bs : filledPositions) {
                    ParticlesUtils.spawnParticlesAboveBlock(level, bs);
                }
            }
        }

        @Override
        public void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {
            super.serverTick(level, pos, state, e);
            detectFormation(level);
            spawnParticlesAboveBlock(level, pos);
        }

        public void setLivingEntity(@Nullable LivingEntity pPlacer) {
            this.playerUUID = pPlacer.getUUID();
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }
    }
}
