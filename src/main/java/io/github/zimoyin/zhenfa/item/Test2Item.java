package io.github.zimoyin.zhenfa.item;

import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.item.base.BaseGeneratedItemData;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import io.github.zimoyin.zhenfa.savedata.TutorialSavedData;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.tags.ItemTags;
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
// 该注解是不会生效的，仅仅告诉开发者这是需要进行自动注册的类
@ItemRegterTables.RegisterItem
public class Test2Item extends BaseItem {
    // register 有众多重载，除了 register(Class) 是自动调用以外其他的都是可以调用的
    // 不是通过 register(Class) 注册的无法通过 getData(cls) 获取数据。但是 getDataList 还是会包含
    public static final Data ITEM_2 = ItemRegterTables.register("ctest2");
    // 注册物品并指定数据类
    public static final Data ITEM_3 = ItemRegterTables.register("ctest3", TestItem.GeneratedItemData.class);
    // 注册物品并生成数据类
    public static final Data ITEM_4 = ItemRegterTables.register("ctest4", data -> new BaseGeneratedItemData(data) {
        @Override
        public void registerRecipe(Recipes recipes) {
            recipes.oneToOneConversionRecipe(data.getItem(), data.getItem(), "ctest4");
        }
    });
}

