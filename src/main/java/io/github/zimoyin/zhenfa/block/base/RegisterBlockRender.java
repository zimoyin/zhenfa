package io.github.zimoyin.zhenfa.block.base;

import com.google.common.base.Supplier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.Zhenfa;
import io.github.zimoyin.zhenfa.block.ZhenfaCoreBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * @author : zimo
 * &#064;date : 2025/03/23
 */
@Mod.EventBusSubscriber(modid = Zhenfa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegisterBlockRender {
    private static final Logger LOGGER = LogUtils.getLogger();

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

    @SubscribeEvent
    public static void registerRender(EntityRenderersEvent.RegisterRenderers event) {
        for (BaseBlock.Data data : BlockRegisterTables.getDataList()) {
            if (data.getBlockEntityTypeObj() == null) continue;
            BlockEntityType entityType = data.getBlockEntityTypeObj().get();
            data.getBlockObj().ifPresent(block -> {
                if (block instanceof IBaseBlock iblock) {
                    BlockEntityRendererProvider render = iblock.getBlockEntityRender();
                    if (render != null) {
                        BlockEntityRenderers.register(entityType, render);
                    }
                }
            });
        }
    }
}
