package io.github.zimoyin.zhenfa.block;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import io.github.zimoyin.zhenfa.block.base.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.HashMap;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
@BlockRegisterTables.RegisterBlock(isRegister = false)
public class ZhenfaBlocks {
    public static final BaseBlock.Data ZhenfaEffectActionBlock_1 =
            registerEffectActionBlock("base_effect_block_1", "测试效果方块_1", MobEffects.FIRE_RESISTANCE);
    public static final BaseBlock.Data ZhenfaEffectActionBlock_2 =
            registerEffectActionBlock("base_effect_block_2", "测试效果方块_2", MobEffects.MOVEMENT_SPEED);



    public static BaseBlock.Data registerEffectActionBlock(String id, String name, MobEffect effect) {
        return BlockRegisterTables.register(
                BlockRegisterTables.builder()
                        .appendBlockId(id)
                        .appendBlockSupplier(() -> new ZhenfaEffectActionBlock(effect, name))
                        .appendGeneratedDataClass(BaseZhenfaActionBlock.ActionBlockGeneratedData.class)
                        .appendBuildedCallback((data) -> {
                            ZhenfaCoreBlock.ZhenfaCoreBlockEntity.AffectsBlocks.add(data.getBlockObj());
                        })
        );
    }

    public static BaseBlock.Data registerTatgetActionBlock(String id, String name, ZhenfaTargetBlock.TargetType type) {
        return BlockRegisterTables.register(
                BlockRegisterTables.builder()
                        .appendBlockId(id)
                        .appendBlockSupplier(() -> new ZhenfaTargetBlock(type, name))
                        .appendGeneratedDataClass(BaseZhenfaActionBlock.ActionBlockGeneratedData.class)
                        .appendBuildedCallback((data) -> {
                            ZhenfaCoreBlock.ZhenfaCoreBlockEntity.AffectsBlocks.add(data.getBlockObj());
                        })
        );
    }
}
