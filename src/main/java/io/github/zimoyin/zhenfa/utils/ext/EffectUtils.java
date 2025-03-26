package io.github.zimoyin.zhenfa.utils.ext;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/**
 * @author : zimo
 * &#064;date : 2025/03/26
 */
public class EffectUtils {
    public static void addEffect(LivingEntity livingEntity, MobEffect effect, int effectTime, int effectLevel) {
        livingEntity.addEffect(new MobEffectInstance(effect, effectTime, effectLevel));
    }

    public static Supplier<MobEffectInstance> getMobEffectInstanceSupplier(MobEffect effect, int effectTime, int effectLevel) {
        return () -> getMobEffectInstance(effect, effectTime, effectLevel);
    }


    public static MobEffectInstance getMobEffectInstance(MobEffect effect, int effectTime, int effectLevel) {
        return new MobEffectInstance(effect, effectTime, effectLevel);
    }
}
