package io.github.zimoyin.zhenfa.block.base;

import io.github.zimoyin.zhenfa.Zhenfa;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author : zimo
 * &#064;date : 2025/03/23
 */
@Mod.EventBusSubscriber(modid = Zhenfa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegisterBlockRenderTypes {
    @SubscribeEvent
    public static void registerRenderLayers(EntityRenderersEvent.RegisterRenderers event) {
        for (BaseBlock.Data data : BlockRegisterTables.getDataList()) {
            data.getBlockObj().ifPresent(block -> {
                if (block instanceof IBaseBlock iblock) {
                    RenderType type = iblock.getRenderType();
                    if (type != null) {
                        ItemBlockRenderTypes.setRenderLayer(block, type);
                    }
                }
            });
        }
    }
}
