package io.github.zimoyin.zhenfa.item;

import io.github.zimoyin.zhenfa.datagen.provider.Recipes;
import io.github.zimoyin.zhenfa.item.base.BaseGeneratedItemData;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;

/**
 * @author : zimo
 * &#064;date : 2025/03/19
 */
@ItemRegterTables.RegisterItem(value = "test3", data = true, generatedData = TestItem.GeneratedItemData.class)
public class TestItem extends BaseItem {
    // 会在合适的适合进行反射注入
    public static BaseItem.Data RegisterItemData = null;

    public TestItem() {
        setItemName("Test3");
    }

    public static class GeneratedItemData extends BaseGeneratedItemData {
        public GeneratedItemData(Data data) {
            super(data);
        }

        @Override
        public void registerRecipe(Recipes recipes) {
            recipes.oneToOneConversionRecipe(data.getItem(), data.getItem(), "test3");
        }
    }
}
