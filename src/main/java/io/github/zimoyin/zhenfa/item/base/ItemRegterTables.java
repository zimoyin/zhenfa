package io.github.zimoyin.zhenfa.item.base;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.utils.ScanResultUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;
import static io.github.zimoyin.zhenfa.utils.SupplierFactoryUtils.createFactory;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public class ItemRegterTables {
    private ItemRegterTables() {
    }


    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ArrayList<BaseItem.Data> DATA_LIST = new ArrayList<>();
    private static final HashMap<Class<?>, BaseItem.Data> DATA_MAP = new HashMap<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);


    /**
     * 获取自动注册的物品数据
     * @param cls   物品类
     */
    public static BaseItem.Data getData(Class<?> cls) {
        return DATA_MAP.get(cls);
    }

    /**
     * 获取所有注册的物品数据
     */
    public static List<BaseItem.Data> getDataList() {
        ArrayList<BaseItem.Data> list = new ArrayList<>();
        list.addAll(DATA_LIST);
        list.addAll(DATA_MAP.values());
        return list;
    }

    /**
     * 自动注册物品，他会将扫描复合要求的类并放入 ITEMS。之后会将 ITEMS 进行注册
     * @param context       获取当前加载的mod
     */
    public static void autoRegisterAll(FMLJavaModLoadingContext context) {
        ScanResultUtils.getModAnnotations(context)
                .stream()
                .filter(ScanResultUtils.AnnotationData::isClassAnnotation)
                .filter(a -> a.isAnnotation(BlockRegterTables.RegisterBlock.class)).forEach(annotation -> {
                    try {
                        Class<?> clazz = annotation.getTargetClass();
                        if (Item.class.isAssignableFrom(clazz)) {
                            register((Class<? extends Item>) clazz);
                            LOGGER.info("register item {}", clazz);
                        } else {
                            LOGGER.error("Failed to register item; Class {} Not extends {}", annotation.getTargetClassName(), Item.class.getName());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to register item; Class {}", annotation.getTargetClassName(), e);
                    }
                });
        ITEMS.register(context.getModEventBus());
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id) {
        return register(id, new Item.Properties().tab(CreativeModeTab.TAB_MISC), (Class<? extends BaseGeneratedItemData>) null);
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @param cls          自动生成JSON数据的类
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id,Class<? extends BaseGeneratedItemData> cls) {
        return register(id, new Item.Properties().tab(CreativeModeTab.TAB_MISC), cls);
    }


    /**
     * 注册物品
     * @param id            物品ID
     * @param properties    物品属性
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, Item.Properties properties) {
        return register(id, properties, (Class<? extends BaseGeneratedItemData>) null);
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @param tab           物品所在的分类
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, CreativeModeTab tab) {
        return register(id, tab, (Class<? extends BaseGeneratedItemData>) null);
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @param tab           物品所在的分类
     * @param cls           自动生成JSON数据的类
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, CreativeModeTab tab,Class<? extends BaseGeneratedItemData> cls) {
        return register(id, new Item.Properties().tab(tab), cls);
    }


    /**
     * 注册物品
     * @param id            物品ID
     * @param properties    物品属性
     * @param cls           自动生成JSON数据的类
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, Item.Properties properties, Class<? extends BaseGeneratedItemData> cls) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (properties == null) properties = new Item.Properties().tab(CreativeModeTab.TAB_MISC);
        if (cls == null) cls = BaseGeneratedItemData.class;

        Item.Properties finalProperties = properties;
        RegistryObject<Item> itemRegistryObject = ITEMS.register(id, () -> new BaseItem(finalProperties));
        BaseItem.Data data = new BaseItem.Data(itemRegistryObject, null, null).setGeneratedDataClass(cls).setItemId(id);
        DATA_LIST.add(data);
        return data;
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @param function      数据生成器，用于生成适用于当前物品的数据
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, Function<BaseItem.Data, BaseGeneratedItemData> function) {
        return register(id, (CreativeModeTab) null, function);
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @param tab           物品所在的分类
     * @param function      数据生成器，用于生成适用于当前物品的数据
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, CreativeModeTab tab , Function<BaseItem.Data, BaseGeneratedItemData> function) {
        return register(id, new Item.Properties().tab(tab), function);
    }

    /**
     * 注册物品
     * @param id            物品ID
     * @param properties    物品属性
     * @param function      数据生成器，用于生成适用于当前物品的数据
     * @return              返回注册的数据
     */
    public static BaseItem.Data register(String id, Item.Properties properties, Function<BaseItem.Data, BaseGeneratedItemData> function) {
        BaseGeneratedItemData gdata;
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (properties == null) properties = new Item.Properties().tab(CreativeModeTab.TAB_MISC);

        Item.Properties finalProperties = properties;
        RegistryObject<Item> itemRegistryObject = ITEMS.register(id, () -> new BaseItem(finalProperties));
        BaseItem.Data data = new BaseItem.Data(itemRegistryObject, null, null).setItemId(id);
        if (function != null) data.setGeneratedData(function.apply(data));
        DATA_LIST.add(data);
        return data;
    }

    /**
     * 自动注册物品，只注册带有 RegisterItem 注解的物品
     */
    public static void register(Class<? extends Item> cls) {
        RegisterItem annotation = cls.getAnnotation(RegisterItem.class);
        if (annotation == null) {
            LOGGER.warn("Failed to register item; Class {} has no annotation @RegisterItem", cls.getName());
            return;
        }

        String itemId = annotation.value();
        boolean isInjectData = annotation.data();
        Field dataField = getDataField(cls);
        if (itemId == null || itemId.isEmpty()) {
            itemId = cls.getSimpleName().toLowerCase();
            LOGGER.warn("Class {} has no annotation @RegisterItem: itemId", cls.getName());
        }

        RegistryObject<Item> itemRegistryObject = ITEMS.register(itemId, createFactory(cls));
        BaseItem.Data data = new BaseItem.Data(itemRegistryObject, cls, annotation);
        DATA_MAP.put(cls, data);

        if (isInjectData) {
            if (dataField == null) {
                LOGGER.warn("The `RegisterItem` annotation on the item registration class has the `data` property set to `true`, but the `{}` does not include `public static BaseItem.Data RegisterItemData = null;`.", cls.getName());
                return;
            }
            try {
                dataField.set(cls, data);
            } catch (IllegalAccessException e) {
                LOGGER.warn("Failed to inject data into block; {}", cls.getName(), e);
            }
        }
    }

    private static Field getDataField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            // 检查类型是否为Data且是静态字段
            if (BaseItem.Data.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
                return field;
            }
        }
        return null;
    }

    /**
     * 注册物品注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RegisterItem {
        /**
         * 注册的物品的ID
         */
        String value() default "";

        /**
         * 是否注入Data 他是 Data(RegistryObject<Block> blockObj, RegistryObject<Item> itemObj)
         * 如果为true 则在注解下 Data 类型的静态变量(该字段必须是公开的)
         */
        boolean data() default false;

        /**
         * 是否生成物品数据
         */
        boolean isGenerated() default true;

        /**
         * 生成物品物品数据的描述类
         */
        Class<? extends BaseGeneratedItemData> generatedData() default BaseGeneratedItemData.class;
    }
}
