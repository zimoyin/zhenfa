package io.github.zimoyin.zhenfa.utils.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : zimo
 * @date   : 2025/03/23
 */
public class AABBUtils {

    /**
     * 计算给定的范围内的 AABB，即 四个三个点确定一个 AABB
     */
    public static AABB calculateFormationAABB(Set<BlockPos> positions) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        for (BlockPos pos : positions) {
            int x = pos.getX();
            int z = pos.getZ();
            int y = pos.getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
            if (y< minY) minY = y;
            if (y > maxY) maxY = y;
        }
        return new AABB(minX,minY , minZ, maxX + 1, maxY + 1, maxZ + 1);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // 实体相关方法
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取阵法区域内的实体列表。（包括掉落物、箭、雨滴、雪花等）
     * 注意检测实体有效性 entity.isAlive() && !entity.isSpectator() 以确定是否已被移除或玩家是否可见
     */
    public static List<Entity> getEntitiesWithinAABB(Level level, AABB formationAABB) {
        List<Entity> detected = new ArrayList<>();
        if (formationAABB == null || level == null) {
            return detected;
        }
        detected.addAll(level.getEntities(null, formationAABB));
        return detected;
    }

    /**
     * 获取每个点位上的实体
     * 基于 Vec3i 列表，支持自定义 Y 轴检测范围，避免重复检测同一实体。
     */
    public static List<Entity> getEntitiesWithinVec3i(Level level, List<Vec3i> vec3i, int minY, int maxY) {
        List<Entity> detected = new ArrayList<>();
        if (vec3i == null || level == null || level.isClientSide()) {
            return detected; // 修复空值检查并跳过客户端
        }
        Set<Entity> uniqueEntities = new HashSet<>();
        vec3i.forEach(vec -> {
            BlockPos pos = new BlockPos(vec);
            AABB aabb = new AABB(pos).setMaxY(maxY).setMinY(minY); // 默认检测单个方块范围（1x1x1）
            List<Entity> entities = level.getEntities(null, aabb);
            uniqueEntities.addAll(entities);
        });
        detected.addAll(uniqueEntities);
        return detected;
    }

    /**
     * 获取每个点位上的实体
     * 基于 Vec3 列表，检测范围默认为单个方块（1x1x1）
     */
    public static List<Entity> getEntitiesWithinVec3i(Level level, List<Vec3i> vec3) {
        List<Entity> detected = new ArrayList<>();
        if (vec3 == null || level == null || level.isClientSide()) {
            return detected; // 修复空值检查并跳过客户端
        }
        Set<Entity> uniqueEntities = new HashSet<>();
        vec3.forEach(vec -> {
            BlockPos pos = new BlockPos(vec);
            AABB aabb = new AABB(pos);
            List<Entity> entities = level.getEntities(null, aabb);
            uniqueEntities.addAll(entities);
        });
        detected.addAll(uniqueEntities);
        return detected;
    }

    /**
     * 获取每个点位上的实体
     * 基于 Vec3 列表，支持自定义 Y 轴检测范围
     */
    public static List<Entity> getEntitiesWithinVec3(Level level, List<Vec3> vec3, int minY, int maxY) {
        List<Entity> detected = new ArrayList<>();
        if (vec3 == null || level == null || level.isClientSide()) {
            return detected; // 修复空值检查并跳过客户端
        }
        Set<Entity> uniqueEntities = new HashSet<>();
        vec3.forEach(vec -> {
            BlockPos pos = new BlockPos(vec);
            AABB aabb = new AABB(pos).setMinY(minY).setMaxY(maxY);
            List<Entity> entities = level.getEntities(null, aabb);
            uniqueEntities.addAll(entities);
        });
        detected.addAll(uniqueEntities);
        return detected;
    }

    /**
     * 获取每个点位上的实体
     * 基于 Vec3 列表，检测范围默认为单个方块（1x1x1）
     */
    public static List<Entity> getEntitiesWithinVec3(Level level, List<Vec3> vec3) {
        List<Entity> detected = new ArrayList<>();
        if (vec3 == null || level == null || level.isClientSide()) {
            return detected; // 修复空值检查并跳过客户端
        }
        Set<Entity> uniqueEntities = new HashSet<>();
        vec3.forEach(vec -> {
            BlockPos pos = new BlockPos(vec);
            AABB aabb = new AABB(pos);
            List<Entity> entities = level.getEntities(null, aabb);
            uniqueEntities.addAll(entities);
        });
        detected.addAll(uniqueEntities);
        return detected;
    }


    ////////////////////////////////////////////////////////////////////////////////
    // 方块位置相关方法
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取阵法区域内的方块位置列表。（排除核心和边界标记方块）
     */
    public static List<BlockPos> getBlockPosWithinAABB(Level level, AABB formationAABB) {
        List<BlockPos> detected = new ArrayList<>();
        if (formationAABB == null || level == null) {
            return detected;
        }
        // 遍历 AABB 内的所有方块位置
        for (BlockPos pos : BlockPos.betweenClosed(
                (int) formationAABB.minX, (int) formationAABB.minY, (int) formationAABB.minZ,
                (int) formationAABB.maxX - 1, (int) formationAABB.maxY - 1, (int) formationAABB.maxZ - 1)) {
            detected.add(pos);
        }
        return detected;
    }

    /**
     * 获取每个点位上的方块
     */
    public static List<BlockPos> getBlockPosWithinVec3(Level level, List<? extends Vec3> vecList) {
        List<BlockPos> detected = new ArrayList<>();
        if (vecList == null || level == null) {
            return detected;
        }
        Set<BlockPos> uniquePos = new HashSet<>();
        for (Vec3 vec : vecList) {
            BlockPos pos = new BlockPos(vec);
            uniquePos.add(pos);
        }
        detected.addAll(uniquePos);
        return detected;
    }

    /**
     * 获取每个点位上的方块
     */
    public static List<BlockPos> getBlockPosWithinVec3i(Level level, List<Vec3i> vecList) {
        List<BlockPos> detected = new ArrayList<>();
        if (vecList == null || level == null) {
            return detected;
        }
        Set<BlockPos> uniquePos = new HashSet<>();
        for (Vec3i vec : vecList) {
            BlockPos pos = new BlockPos(vec);
            uniquePos.add(pos);
        }
        detected.addAll(uniquePos);
        return detected;
    }
}
