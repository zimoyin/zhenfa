package io.github.zimoyin.zhenfa.creativetab.base;

import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static io.github.zimoyin.zhenfa.utils.ResourcesUtils.validateResourcePath;

/**
 * @author : zimo
 * &#064;date : 2025/03/20
 */
public class CreativeModeTabBuilder {
    private final String label; // 标签的注册名（小写和下划线）
    private Component displayName; // 本地化显示名称
    private ItemStack icon; // 默认图标
    private final List<Supplier<? extends Item>> supplierItems = new ArrayList<>(); // 待添加的物品
    private final List<Item> items = new ArrayList<>(); // 待添加的物品
    private int order = -1;
    private ArrayList<Lang> lang = new ArrayList<>();

    public static final NonNullList<CreativeModeTab> TABS = NonNullList.create();

    // 构造函数（必须指定标签名）
    public CreativeModeTabBuilder(String label) {
        this.label = label;
        validateResourcePath(label);
    }

    public CreativeModeTabBuilder lang(Lang... lang) {
        this.lang.addAll(Arrays.asList(lang));
        return this;
    }

    /**
     * ID Name
     */
    public CreativeModeTabBuilder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public CreativeModeTabBuilder order(int order) {
        this.order = order;
        return this;
    }

    /**
     * ID Name
     */
    public CreativeModeTabBuilder displayName(String displayName) {
        this.displayName = new TranslatableComponent("itemGroup." + displayName);
        return this;
    }

    // 设置图标（传入物品）
    public CreativeModeTabBuilder icon(Item iconItem) {
        this.icon = new ItemStack(iconItem);
        return this;
    }

    // 设置图标（传入物品）
    public CreativeModeTabBuilder icon(ItemStack itemStack) {
        this.icon = itemStack;
        return this;
    }

    // 添加多个物品到标签
    public CreativeModeTabBuilder addItem(Supplier<? extends Item>... itemSuppliers) {
        this.supplierItems.addAll(Arrays.asList(itemSuppliers));
        return this;
    }

    public CreativeModeTabBuilder addItem(Item... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }


    // 构建并注册 CreativeModeTab
    public CreativeModeTab build() {
        if (label == null || label.isEmpty()) {
            throw new IllegalStateException("CreativeModeTab label cannot be null or empty!");
        }

        // 创建 CreativeModeTab
        return new BaseCreativeModeTab(order, label, lang) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return icon != null ? icon : new ItemStack(ItemRegterTables.getDataList().stream().findFirst().map(BaseItem.Data::getItem).orElse(Items.DIAMOND));
            }

            @Override
            public @NotNull Component getDisplayName() {
                return displayName != null ? displayName : super.getDisplayName();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> itemList) {
                super.fillItemList(itemList);
                // 添加默认物品（如果有的话）
                supplierItems.forEach(itemSupplier -> itemList.add(new ItemStack(itemSupplier.get())));
                items.forEach(item -> itemList.add(new ItemStack(item)));
            }
        };
    }

    // 静态方法创建实例（推荐）
    public static CreativeModeTabBuilder builder(String label) {
        return new CreativeModeTabBuilder(label);
    }

    public static CreativeModeTab create(String label) {
        return new CreativeModeTabBuilder(label).build();
    }

    public static CreativeModeTab create(String label, BaseItem.Data data) {
        return new CreativeModeTabBuilder(label).icon(data.getItem()).build();
    }

    public static CreativeModeTab create(String label, Item item) {
        return new CreativeModeTabBuilder(label).icon(item).build();
    }

    public static CreativeModeTab create(String label, ItemStack item) {
        return new CreativeModeTabBuilder(label).icon(item).build();
    }
}
