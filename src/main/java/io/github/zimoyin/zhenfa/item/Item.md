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

## 创建食物
```java
@ItemRegterTables.RegisterItem(value = "food", data = true)
public class TestItem extends BaseItem {
    // 会在合适的适合进行反射注入
    public static BaseItem.Data RegisterItemData = null;

    public TestItem() {
        super(new Item.Properties().food(new FoodProperties.Builder()
                .meat() // 设置是为肉食
                .alwaysEat() // 设置如果饱腹的时候是否可以吃
                .nutrition(10) // 设置饱腹度
                .saturationMod(10) // 设置饱和度
                // 100/20(游戏刻tick) = 5秒 持续时间为 5秒
                // 1 效果等级
                // 1.0F 获得效果的概率
                .effect(()-> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 1), 1.0F) // 设置效果
                .effect(()-> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 1), 1.0F) // 设置效果 可设置多个
                .build())
        );
    }
}
```

## 创建工具
* 工具的公共类是：TieredItem  
  * DiggerItem
    * PickaxeItem
    * ShovelItem
    * AxeItem
    * HoeItem
  * SwordItem

> SwordItem 和 DiggerItem的子类 构造函数需要的参数  
> Tier pTier： 工具等级与其他信息，仿照 Tiers 实现即可。~~如果不想实现可以使用 `Tiers` 枚举类~~  
> int pAttackDamageModifier： 伤害  
> float pAttackSpeedModifier： 攻击速度  
> Item.Properties pProperties： 属性  

需要实现上面工具继承什么类即可。或者直接注册即可  
官方示例  
```
() -> new PickaxeItem(Tiers.IRON, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))
```

示例：
```java
public static final BaseItem.Data a = ItemRegterTables.register(
        "ctest3",
        () -> new PickaxeItem(Tiers.IRON, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS)),
        data -> new BaseGeneratedItemData(data) {
            @Override
            public List<Lang> lang() {
                return List.of(new Lang(Lang.LangType.ZH_CN, "测试4"));
            }
        }
);
```
继承示例:
```java
@ItemRegterTables.RegisterItem(value = "ctestitem1", data = true, generatedData = TestItem.GeneratedItemData.class)
public class TestItem extends PickaxeItem {
    // 会在合适的适合进行反射注入
    public static BaseItem.Data RegisterItemData = null;


    public TestItem() {
        super(Tiers.IRON, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            TutorialSavedData.get(level).click(player);
        }
        return super.use(level, player, hand);
    }

    public static class GeneratedItemData extends BaseGeneratedItemData {
        public GeneratedItemData(BaseItem.Data data) {
            super(data);
        }

        @Override
        public void registerRecipe(Recipes recipes) {
            recipes.oneToOneConversionRecipe(data.getItem(), data.getItem(), String.valueOf(this.hashCode()));
        }
    }
}

```

## 创建盔甲
* 盔甲的公共类是：ArmorItem
  * 可染色盔甲： DyeableArmorItem
* 可穿戴接口： Wearable (ArmorItem 已经实现了该接口)
> 构造函数的参数
> ArmorMaterial pMaterial  盔甲的属性，仿照 ArmorMaterials 实现即可。 ~~同样不想实现复用 ArmorMaterials 即可~~   
> EquipmentSlot pSlot  穿戴位置    
> Item.Properties pProperties  

需要实现继承ArmorItem。或者直接注册即可  
注意：  
* 在 `textures\item` 中需要添加手持盔甲的贴图, 既在物品栏中的贴图模样
  * 文件名称为 [item_id].png
* 在 `textures\models\armor` 中需要添加穿上盔甲后的贴图
  * 文件名称为 [ArmorMaterial.getName()]_layer_1.png
  * 文件名称为 [ArmorMaterial.getName()]_layer_2.png