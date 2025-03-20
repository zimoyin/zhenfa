## item 层
item 层是用于定义各种物品的。类似于 block 如果你想要更多信息参考 Block层  

这里仅提供几个示例  

* 单物品注册
```java
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

```
* 多物品注册
```java
@ItemRegterTables.RegisterItem(isRegister = false)
public class Test2Item {
    // register 有众多重载，除了 register(Class) 是自动调用以外其他的都是可以调用的
    // 不是通过 register(Class) 注册的无法通过 getData(cls) 获取数据。但是 getDataList 还是会包含
    public static final BaseItem.Data ITEM_2 = ItemRegterTables.register("ctest2");
    // 注册物品并指定数据类
    public static final BaseItem.Data ITEM_3 = ItemRegterTables.register("ctest3", TestItem.GeneratedItemData.class);
    // 注册物品并生成数据类
    public static final BaseItem.Data ITEM_4 = ItemRegterTables.register("ctest4", data -> new BaseGeneratedItemData(data) {
        @Override
        public List<Lang> lang() {
            return List.of(new Lang(Lang.LangType.ZH_CN, "测试4"));
        }
    });

    public static final BaseItem.Data ITEM_5 = ItemRegterTables.register("ctest5", CreativeTabs.TestCreativeTab, data -> new BaseGeneratedItemData(data) {
        @Override
        public void registerRecipe(Recipes recipes) {
            recipes.oneToOneConversionRecipe(data.getItem(), data.getItem(), "ctest5");
        }
    });
    
    // 如果你需要更深的定制，也提供了注册方法。参考以下
    // public static BaseItem.Data register(String id, Supplier<? extends Item> itemFactory)
}
```