package io.github.zimoyin.zhenfa.item;

import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.item.base.BaseGeneratedItemData;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import io.github.zimoyin.zhenfa.savedata.TutorialSavedData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zimo
 * &#064;date : 2025/03/19
 */
@ItemRegterTables.RegisterItem
public class Test2Item extends BaseItem {

    public static final Data ITEM_1 = ItemRegterTables.register("test2", new Properties(), null);
}
