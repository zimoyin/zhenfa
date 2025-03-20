package io.github.zimoyin.zhenfa.item;

import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.item.base.BaseGeneratedItemData;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import io.github.zimoyin.zhenfa.savedata.TutorialSavedData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zimo
 * &#064;date : 2025/03/19
 */
@ItemRegterTables.RegisterItem(value = "ctestitem1", data = true, generatedData = TestItem.GeneratedItemData.class)
public class TestItem extends BaseItem {
    // 会在合适的适合进行反射注入
    public static BaseItem.Data RegisterItemData = null;

    public TestItem() {
        setItemName("Test3");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            TutorialSavedData.get(level).click(player);
        }
        return super.use(level, player, hand);
    }

    public static class GeneratedItemData extends BaseGeneratedItemData {
        public GeneratedItemData(Data data) {
            super(data);
        }

        @Override
        public void registerRecipe(Recipes recipes) {
            recipes.oneToOneConversionRecipe(data.getItem(), data.getItem(), String.valueOf(this.hashCode()));
        }
    }
}
