package io.github.zimoyin.zhenfa.block;

import io.github.zimoyin.zhenfa.utils.ext.AABBUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author : zimo
 * &#064;date : 2025/03/26
 */
public class ZhenfaEffectActionBlock extends BaseZhenfaActionBlock {
    private final MobEffect effect;

    public ZhenfaEffectActionBlock(MobEffect effect, String name) {
        super(name);
        this.effect = effect;
    }

    @Override
    public void action(Level level, List<BlockState> states, List<ZhenfaTargetBlock.TargetType> targetTypes, ZhenfaCoreBlock.ZhenfaCoreBlockEntity coreBlockEntity) {
        int effectLevel = states.size() - 1;
        if (effectLevel >= 9) effectLevel = 9;
        int y = coreBlockEntity.getBlockPos().getY();
        List<Vec3> vecList = Stream.of(coreBlockEntity.getBoundaryPositions(), coreBlockEntity.getFilledPositions()).flatMap(Set::stream).map(pos -> new Vec3(pos.getX(), pos.getY(), pos.getZ())).toList();
        List<Entity> entities = AABBUtils.getEntitiesWithinVec3(level, vecList, y - 30, y + 30);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(effect, 100, effectLevel), livingEntity);
            }
        }
    }
}
