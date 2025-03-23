package io.github.zimoyin.zhenfa.utils;

import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import java.util.function.Predicate;

/**
 * RayTraceUtils 工具类
 * <p>
 * 提供常用的射线检测方法：
 * <ul>
 *   <li>检测方块：{@link #rayTraceBlock(Level, Entity, double)}</li>
 *   <li>检测流体：{@link #rayTraceFluid(Level, Entity, double)}</li>
 *   <li>检测实体（通用）：{@link #rayTraceEntity(Level, Entity, double, Predicate)}</li>
 *   <li>检测掉落物（ItemEntity）：{@link #rayTraceDroppedItem(Level, Entity, double)}</li>
 *   <li>检测其他玩家：{@link #rayTraceOtherPlayer(Level, Entity, double)}</li>
 *   <li>检测结构方块：{@link #rayTraceStructure(Level, Entity, double)}</li>
 *   <li>自定义射线检测：{@link #rayTraceCustom(Level, Vec3, Vec3, double, Entity, ClipContext.Block, ClipContext.Fluid)}</li>
 * </ul>
 * 
 * 注意：射线检测的发射者（shooter）可以是玩家、实体、掉落物或其它对象，
 * 该参数不仅用于获取视角（起点与方向），还可能用于权限检查等。
 * 
 * 版本: Minecraft 1.18.2, Forge 40.2.21
 * </p>
 */
public class RayTraceUtils {

    /**
     * 使用射线检测方块（默认不检测流体）。
     * <p>
     * 该方法通过射线检测返回第一个被检测到的方块。
     * </p>
     * 
     * @param level    游戏世界
     * @param shooter  射线发射者（例如玩家、实体、掉落物等），用于获取眼睛位置和视线方向
     * @param distance 射线检测的最大距离（单位：方块）
     * @return BlockHitResult 检测结果，如果未命中则返回类型不为 BLOCK 的结果
     */
    public static BlockHitResult rayTraceBlock(Level level, Entity shooter, double distance) {
        Vec3 start = shooter.getEyePosition(1.0f);
        Vec3 look = shooter.getViewVector(1.0f);
        Vec3 end = start.add(look.scale(distance));
        // 使用 COLLIDER 模式检测方块，不检测流体
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter);
        return level.clip(context);
    }

    /**
     * 使用射线检测流体。
     * <p>
     * 与射线检测方块类似，但设置流体检测模式为 ANY 以检测所有流体。
     * </p>
     * 
     * @param level    游戏世界
     * @param shooter  射线发射者
     * @param distance 射线检测的最大距离
     * @return BlockHitResult 流体检测结果
     */
    public static BlockHitResult rayTraceFluid(Level level, Entity shooter, double distance) {
        Vec3 start = shooter.getEyePosition(1.0f);
        Vec3 look = shooter.getViewVector(1.0f);
        Vec3 end = start.add(look.scale(distance));
        // Fluid.ANY 表示检测所有流体类型
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, shooter);
        return level.clip(context);
    }

    /**
     * 使用射线检测实体（通用方法）。
     * <p>
     * 该方法通过传入自定义过滤器 {@code predicate}，可以检测满足条件的目标实体。
     * </p>
     * 
     * @param level     游戏世界
     * @param shooter   射线发射者
     * @param distance  射线检测的最大距离
     * @param predicate 实体过滤器，用于筛选目标实体（例如：排除旁观者、只检测掉落物等）
     * @return EntityHitResult 检测到的实体结果，如果未命中则返回 null
     */
    public static EntityHitResult rayTraceEntity(Level level, Entity shooter, double distance, Predicate<Entity> predicate) {
        Vec3 start = shooter.getEyePosition(1.0f);
        Vec3 look = shooter.getViewVector(1.0f);
        Vec3 end = start.add(look.scale(distance));
        // 构造一个包围盒，扩展射线范围并适当膨胀，防止漏检
        AABB aabb = shooter.getBoundingBox().expandTowards(look.scale(distance)).inflate(1.0D);
        return ProjectileUtil.getEntityHitResult(level, shooter, start, end, aabb, predicate);
    }

    /**
     * 使用射线检测掉落物（ItemEntity）。
     * <p>
     * 该方法利用 {@link #rayTraceEntity(Level, Entity, double, Predicate)} 过滤出掉落物实体。
     * </p>
     * 
     * @param level    游戏世界
     * @param shooter  射线发射者
     * @param distance 射线检测的最大距离
     * @return EntityHitResult 检测到的掉落物结果，如果未命中则返回 null
     */
    public static EntityHitResult rayTraceDroppedItem(Level level, Entity shooter, double distance) {
        return rayTraceEntity(level, shooter, distance, entity -> entity instanceof ItemEntity);
    }

    /**
     * 使用射线检测其他玩家（排除射线发射者自身）。
     * <p>
     * 该方法用于检测射线是否击中其他玩家，避免检测到发射者本人。
     * </p>
     * 
     * @param level    游戏世界
     * @param shooter  射线发射者（通常为玩家）
     * @param distance 射线检测的最大距离
     * @return EntityHitResult 检测到的其他玩家结果，如果未命中则返回 null
     */
    public static EntityHitResult rayTraceOtherPlayer(Level level, Entity shooter, double distance) {
        return rayTraceEntity(level, shooter, distance, entity -> (entity instanceof Player && entity != shooter));
    }

    /**
     * 使用射线检测结构方块（Structure Block）。
     * <p>
     * 该方法先进行常规方块检测，然后判断命中的方块是否为结构方块。
     * </p>
     * 
     * @param level    游戏世界
     * @param shooter  射线发射者
     * @param distance 射线检测的最大距离
     * @return BlockHitResult 检测到的结构方块结果，如果未命中或命中非结构方块，则返回原始检测结果
     */
    public static BlockHitResult rayTraceStructure(Level level, Entity shooter, double distance) {
        BlockHitResult result = rayTraceBlock(level, shooter, distance);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockState state = level.getBlockState(result.getBlockPos());
            // 检查命中方块是否为结构方块
            if (state.is(Blocks.STRUCTURE_BLOCK)) {
                return result;
            }
        }
        // 未检测到结构方块则返回原始结果（可能是其他方块或者 MISS）
        return result;
    }

    /**
     * 通用射线检测方法，允许自定义起点和方向。
     * <p>
     * 使用该方法可以实现更灵活的射线检测，例如由方块或其他实体触发时，
     * 不使用默认的“眼睛位置”作为起点。
     * </p>
     * 
     * @param level      游戏世界
     * @param start      射线起点
     * @param direction  射线方向（请确保已标准化）
     * @param distance   射线检测的最大距离
     * @param shooter    射线发射者（用于权限检测等，可为 null，但建议传入有效实体）
     * @param blockMode  方块检测模式（例如 ClipContext.Block.COLLIDER）
     * @param fluidMode  流体检测模式（例如 ClipContext.Fluid.NONE 或 ClipContext.Fluid.ANY）
     * @return BlockHitResult 射线检测结果
     */
    public static BlockHitResult rayTraceCustom(Level level, Vec3 start, Vec3 direction, double distance, Entity shooter,
            ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        Vec3 end = start.add(direction.scale(distance));
        ClipContext context = new ClipContext(start, end, blockMode, fluidMode, shooter);
        return level.clip(context);
    }
}
