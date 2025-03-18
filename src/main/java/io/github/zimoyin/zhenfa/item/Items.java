package io.github.zimoyin.zhenfa.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public class Items {
    private Items() {
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
}
