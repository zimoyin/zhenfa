package io.github.zimoyin.zhenfa.utils.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * @author : zimo
 * &#064;date : 2025/03/22
 */
public class ParticlesUtils {

    /**
     * 在方块上方生成粒子
     * @param level
     * @param pos
     */
    public static void spawnParticlesAboveBlock(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        // 在服务端调用
        ((ServerLevel) level).sendParticles(
                ParticleTypes.PORTAL,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                10, // 粒子数量
                0.2, 0.5, 0.2, // X/Y/Z 随机偏移
                0.01 // 运动速度
        );
    }
}
