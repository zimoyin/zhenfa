package io.github.zimoyin.zhenfa.block.base;

import com.google.common.base.Supplier;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.utils.ScanResultUtils;
import io.github.zimoyin.zhenfa.utils.ext.ClassUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;
import static io.github.zimoyin.zhenfa.item.base.ItemRegterTables.ITEMS;
import static io.github.zimoyin.zhenfa.utils.ResourcesUtils.validateResourcePath;
import static io.github.zimoyin.zhenfa.utils.SupplierFactoryUtils.createFactory;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
public class BlockRegterTables {
    private BlockRegterTables() {
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<Class<?>, BaseBlock.Data> BLOCK_DATA_MAP = new HashMap<>();
    private static final ArrayList<BaseBlock.Data> BLOCK_DATA_LIST = new ArrayList<>();
    private static final HashMap<Class<?>, RegistryObject<BlockEntityType<?>>> BLOCK_ENTITY_DATA_MAP = new HashMap<>();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);


    /**
     * 获取注册的方块和方块物品的注册对象
     *
     * @param cls 方块类
     * @return 注册对象
     */
    public static BaseBlock.Data getData(Class<? extends Block> cls) {
        return BLOCK_DATA_MAP.get(cls);
    }

    /**
     * 获取所有注册的方块数据.
     * 一部分数据来自于 BLOCK_DATA_MAP
     * 另一部分数据来自于 BLOCK_DATA_LIST
     */
    public static List<BaseBlock.Data> getDataList() {
        ArrayList<BaseBlock.Data> objects = new ArrayList<>();
        objects.addAll(BLOCK_DATA_LIST);
        objects.addAll(BLOCK_DATA_MAP.values());
        return objects;
    }

    /**
     * 设置方块数据
     *
     * @param data 注册对象
     * @return 注册对象
     */
    public static BaseBlock.Data setData(BaseBlock.Data data) {
        return BLOCK_DATA_MAP.put(data.getCls(), data);
    }


    public static RegistryObject<BlockEntityType<?>> getEntityRegistryObject(Class<? extends BlockEntity> cls) {
        return BLOCK_ENTITY_DATA_MAP.get(cls);
    }


    public static void autoRegisterAll(FMLJavaModLoadingContext context) {
        ScanResultUtils.getModAnnotations(context)
                .stream()
                .filter(ScanResultUtils.AnnotationData::isClassAnnotation)
                .filter(a -> a.isAnnotation(RegisterBlock.class)).forEach(annotation -> {
                    try {
                        Class<?> clazz = annotation.getTargetClass();
                        if (Block.class.isAssignableFrom(clazz)) {
                            register((Class<? extends Block>) clazz);
                        } else if (clazz.getAnnotation(RegisterBlock.class).isRegister()) {
                            LOGGER.error("Failed to register block; Class {} Not extends {}", annotation.getTargetClassName(), Block.class, new IllegalArgumentException());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to register block; Class {}", annotation.getTargetClassName(), e);
                    }
                });
        BLOCKS.register(context.getModEventBus());
        BLOCK_ENTITIES.register(context.getModEventBus());
    }

    /**
     * 注册方块和物品（带数据生成器）
     *
     * @param id       方块的注册ID，同时作为物品ID
     * @param function 数据生成器，用于生成方块附加数据。需显式传入{@code (Function)null}表示不使用
     * @return 包含注册信息的数据容器
     */
    public static BaseBlock.Data register(String id, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        return register(id, (BlockBehaviour.Properties) null, null, function);
    }

    /**
     * 注册方块（自定义属性+数据生成器）
     *
     * @param id         方块注册ID
     * @param properties 方块物理属性配置
     * @param function   数据生成器，需显式传入{@code (Function)null}表示不使用
     * @return 注册数据容器
     */
    public static BaseBlock.Data register(String id, BlockBehaviour.Properties properties, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        return register(id, properties, null, function);
    }

    /**
     * 注册方块（自定义物品栏+数据生成器）
     *
     * @param id       方块注册ID
     * @param tab      创造模式物品栏分类
     * @param function 数据生成器，需显式传入{@code (Function)null}表示不使用
     * @return 注册数据容器
     */
    public static BaseBlock.Data register(String id, CreativeModeTab tab, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        return register(id, null, tab, function);
    }


    /**
     * 注册方块和物品
     *
     * @param id         方块的 ID，他将和方块物品的ID一致
     * @param properties 方块属性 默认： BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6)
     * @param tab        物品栏 默认： CreativeModeTab.TAB_BUILDING_BLOCKS
     * @param function   数据生成器，用于生成适用于当前方块的数据
     */
    public static BaseBlock.Data register(String id, BlockBehaviour.Properties properties, CreativeModeTab tab, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (properties == null) properties = BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6f);
        if (tab == null) tab = CreativeModeTab.TAB_BUILDING_BLOCKS;

        BlockBehaviour.Properties finalProperties = properties;
        CreativeModeTab finalTab = tab;
        RegistryObject<Block> blockRegistryObject = BLOCKS.register(id, () -> new BaseBlock(finalProperties));
        RegistryObject<BlockItem> itemRegistryObject = ITEMS.register(id, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties().tab(finalTab)));
        BaseBlock.Data data = new BaseBlock.Data(blockRegistryObject, itemRegistryObject, null, null, null).setBlockId(id);
        if (function != null) data.setGeneratedData(function.apply(data));
        BLOCK_DATA_LIST.add(data);
        return data;
    }

    /**
     * 注册方块和物品
     *
     * @param id            方块的 ID，他将和方块物品的ID一致
     * @param supplierBlock 方块工厂
     * @param function      数据生成器，用于生成适用于当前方块的数据
     */
    public static BaseBlock.Data register(String id, Supplier<? extends Block> supplierBlock, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        return register(id, (CreativeModeTab) null, supplierBlock, function);
    }


    /**
     * 注册方块和物品
     *
     * @param id            方块的 ID，他将和方块物品的ID一致
     * @param tab           物品栏 默认： CreativeModeTab.TAB_BUILDING_BLOCKS
     * @param supplierBlock 方块工厂
     * @param function      数据生成器，用于生成适用于当前方块的数据
     */
    public static BaseBlock.Data register(String id, CreativeModeTab tab, Supplier<? extends Block> supplierBlock, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        return register(id, new Item.Properties().tab(tab), supplierBlock, function);
    }

    /**
     * 注册方块和物品
     *
     * @param id            方块的 ID，他将和方块物品的ID一致
     * @param properties    方块物品属性 默认： BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6)
     * @param supplierBlock 方块工厂
     * @param function      数据生成器，用于生成适用于当前方块的数据
     */
    public static BaseBlock.Data register(String id, Item.Properties properties, Supplier<? extends Block> supplierBlock, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (supplierBlock == null) throw new IllegalArgumentException("sup cannot be null");
        if (properties == null) properties = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);

        Item.Properties finalProperties = properties;
        RegistryObject<Block> blockRegistryObject = BLOCKS.register(id, supplierBlock);
        RegistryObject<BlockItem> itemRegistryObject = ITEMS.register(id, () -> new BlockItem(blockRegistryObject.get(), finalProperties));
        BaseBlock.Data data = new BaseBlock.Data(blockRegistryObject, itemRegistryObject, null, null, null).setBlockId(id);
        if (function != null) data.setGeneratedData(function.apply(data));
        BLOCK_DATA_LIST.add(data);
        return data;
    }

    /**
     * 注册方块和物品
     *
     * @param id            方块的 ID，他将和方块物品的ID一致
     * @param supplierBlock 方块工厂
     * @param supplierItem  方块物品工厂
     * @param function      数据生成器，用于生成适用于当前方块的数据
     */
    public static BaseBlock.Data register(String id, Supplier<? extends Block> supplierBlock, Supplier<? extends BlockItem> supplierItem, Function<BaseBlock.Data, BaseGeneratedBlockData> function) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (supplierBlock == null) throw new IllegalArgumentException("sup cannot be null");
        if (supplierItem == null) throw new IllegalArgumentException("sup cannot be null");

        RegistryObject<Block> blockRegistryObject = BLOCKS.register(id, supplierBlock);
        RegistryObject<BlockItem> itemRegistryObject = ITEMS.register(id, supplierItem);
        BaseBlock.Data data = new BaseBlock.Data(blockRegistryObject, itemRegistryObject, null, null, null).setBlockId(id);
        if (function != null) data.setGeneratedData(function.apply(data));
        BLOCK_DATA_LIST.add(data);
        return data;
    }


    /**
     * 快速注册方块（使用全部默认配置）
     * <p>
     * 默认配置：
     * - 属性：石材质，硬度1.5，爆炸抗性6
     * - 物品栏：建筑方块标签
     * - 数据类：基础方块数据生成器
     *
     * @param id 方块/物品的注册ID
     * @return 包含默认配置的注册数据容器
     */
    public static BaseBlock.Data register(String id) {
        return register(id, null, null, (Class<? extends BaseGeneratedBlockData>) null);
    }

    /**
     * 注册方块（自定义属性，其他使用默认）
     *
     * @param id         方块/物品的注册ID
     * @param properties 方块物理属性配置
     * @return 包含自定义属性的注册数据容器
     */
    public static BaseBlock.Data register(String id, BlockBehaviour.Properties properties) {
        return register(id, properties, null, (Class<? extends BaseGeneratedBlockData>) null);
    }

    /**
     * 注册方块（自定义物品栏，其他使用默认）
     *
     * @param id  方块/物品的注册ID
     * @param tab 创造模式物品栏分类
     * @return 包含自定义物品栏的注册数据容器
     */
    public static BaseBlock.Data register(String id, CreativeModeTab tab) {
        return register(id, null, tab, (Class<? extends BaseGeneratedBlockData>) null);
    }

    /**
     * 注册方块（自定义数据生成类，其他使用默认）
     *
     * @param id       方块/物品的注册ID
     * @param dataClas 自定义数据生成类类型
     * @return 包含自定义数据生成器的注册数据容器
     */
    public static BaseBlock.Data register(String id, Class<? extends BaseGeneratedBlockData> dataClas) {
        return register(id, null, null, dataClas);
    }

    /**
     * 注册方块（自定义属性+物品栏）
     *
     * @param id         方块/物品的注册ID
     * @param properties 方块物理属性配置
     * @param tab        创造模式物品栏分类
     * @return 包含自定义属性和物品栏的注册数据容器
     */
    public static BaseBlock.Data register(String id, BlockBehaviour.Properties properties, CreativeModeTab tab) {
        return register(id, properties, tab, (Class<? extends BaseGeneratedBlockData>) null);
    }

    /**
     * 注册方块（自定义属性+数据生成类）
     *
     * @param id         方块/物品的注册ID
     * @param properties 方块物理属性配置
     * @param dataClas   自定义数据生成类类型
     * @return 包含自定义属性和数据生成器的注册数据容器
     */
    public static BaseBlock.Data register(String id, BlockBehaviour.Properties properties, Class<? extends BaseGeneratedBlockData> dataClas) {
        return register(id, properties, null, dataClas);
    }

    /**
     * 注册方块（自定义物品栏+数据生成类）
     *
     * @param id       方块/物品的注册ID
     * @param tab      创造模式物品栏分类
     * @param dataClas 自定义数据生成类类型
     * @return 包含自定义物品栏和数据生成器的注册数据容器
     */
    public static BaseBlock.Data register(String id, CreativeModeTab tab, Class<? extends BaseGeneratedBlockData> dataClas) {
        return register(id, null, tab, dataClas);
    }

    /**
     * 注册方块和物品
     *
     * @param id         方块的 ID，他将和方块物品的ID一致
     * @param properties 方块属性 默认： BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6)
     * @param tab        物品栏 默认： CreativeModeTab.TAB_BUILDING_BLOCKS
     * @param dataClas   数据生成类 默认：BaseGeneratedBlockData
     */
    public static BaseBlock.Data register(String id, BlockBehaviour.Properties properties, CreativeModeTab tab, Class<? extends BaseGeneratedBlockData> dataClas) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (properties == null) properties = BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6f);
        if (tab == null) tab = CreativeModeTab.TAB_BUILDING_BLOCKS;
        if (dataClas == null) dataClas = BaseGeneratedBlockData.class;
        BlockBehaviour.Properties finalProperties = properties;
        CreativeModeTab finalTab = tab;
        RegistryObject<Block> blockRegistryObject = BLOCKS.register(id, () -> new BaseBlock(finalProperties));
        RegistryObject<BlockItem> itemRegistryObject = ITEMS.register(id, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties().tab(finalTab)));
        BaseBlock.Data data = new BaseBlock.Data(blockRegistryObject, itemRegistryObject, null, null, null).setGeneratedDataClass(dataClas).setBlockId(id);
        BLOCK_DATA_LIST.add(data);
        return data;
    }

    /**
     * 自动注册方块和方块物品。该注册方法需要 cls 上具有 RegisterBlock 注解
     */
    public static void register(Class<? extends Block> cls) {
        RegisterBlock annotation = cls.getAnnotation(RegisterBlock.class);
        if (annotation == null) {
            LOGGER.error("Failed to register block; Class {} has no annotation @RegisterBlock", cls.getName());
            return;
        }
        if (!annotation.isRegister()) return;

        String blockId = annotation.value();
        String itemId = annotation.itemId();
        Class<? extends BlockEntity> blockEntity = annotation.blockEntity();
        boolean isInjectData = annotation.data();
        Field dataField = getDataField(cls);
        if (itemId == null || itemId.isEmpty()) itemId = blockId;
        if (blockId == null || blockId.isEmpty()) {
            blockId = cls.getSimpleName().toLowerCase();
            LOGGER.warn("Class {} has no annotation @RegisterBlock: blockId", cls.getName());
        }

        RegistryObject<BlockEntityType<?>> registryBlockEntityType = null;
        RegistryObject<Block> blockRegistryObject = BLOCKS.register(blockId, createFactory(cls));
        RegistryObject<BlockItem> itemRegistryObject = ITEMS.register(itemId, getBlockItemSupplier(cls, blockRegistryObject));
        if (BlockEntity.class.isAssignableFrom(blockEntity) && blockEntity != BlockEntity.class) {
            if (!BaseEntityBlock.class.isAssignableFrom(cls)) throw new IllegalArgumentException(cls+" must be a subclass of EntityBlock");
            registryBlockEntityType = registerBlockEntity(blockEntity, blockRegistryObject, blockId);
        }
        BaseBlock.Data data = new BaseBlock.Data(blockRegistryObject, itemRegistryObject, registryBlockEntityType, cls, annotation);
        setData(data);

        if (isInjectData) {
            if (dataField == null) {
                LOGGER.warn("The `RegisterBlock` annotation on the block registration class has the `data` property set to `true`, but the `{}` does not include `public static BaseBlock.Data RegisterBlockData = null;`.", cls.getName());
                return;
            }
            try {
                dataField.set(cls, data);
            } catch (IllegalAccessException e) {
                LOGGER.warn("Failed to inject data into block; {}", cls.getName(), e);
            }
        }
    }

    private static RegistryObject<BlockEntityType<?>> registerBlockEntity(Class<? extends BlockEntity> cls, RegistryObject<Block> blockRegistryObject, String blockId) {
        // 通过反射获取构造函数
        try {
            BlockEntityType.BlockEntitySupplier<BlockEntity> supplier = getBlockEntitySupplier(cls);

            // 注册 BlockEntityType
            RegistryObject<BlockEntityType<?>> blockEntity = BLOCK_ENTITIES.register(blockId, () -> BlockEntityType.Builder.of(supplier, blockRegistryObject.get()).build(DSL.remainderType()));
            BLOCK_ENTITY_DATA_MAP.put(cls, blockEntity);
            return blockEntity;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("BlockEntity class " + cls.getSimpleName() + " must have a constructor with (BlockPos, BlockState) parameters", e);
        }
    }

    /**
     * 获取BlockEntitySupplier <br>
     * 最先通过反射获取最需要的 Constructor(BlockPos.class, BlockState.class) <br>
     * 如果没有或者获取失败则调用 Constructor(BlockEntityType.class, BlockPos.class, BlockState.class)<br>
     * 对于 BlockEntityType.class 这个参数将会从 BLOCK_ENTITY_DATA_MAP 中获取
     *
     * @param cls
     * @return
     * @throws NoSuchMethodException
     */
    @NotNull
    private static BlockEntityType.BlockEntitySupplier<BlockEntity> getBlockEntitySupplier(Class<? extends BlockEntity> cls) throws NoSuchMethodException {
        // 优先尝试获取主构造函数(BlockPos, BlockState)
        Constructor<? extends BlockEntity> primaryConstructor = getConstructorQuietly(cls, BlockPos.class, BlockState.class);

        if (primaryConstructor != null) {
            return (pos, state) -> instantiateBlockEntity(cls, primaryConstructor, pos, state);
        } else {
            // 主构造函数不可用时，尝试备用构造函数(BlockEntityType, BlockPos, BlockState)
            Constructor<?> fallbackConstructor = ClassUtils.findConstructor(cls, BlockEntityType.class, BlockPos.class, BlockState.class);
            if (fallbackConstructor == null) {
                throw new RuntimeException("No suitable constructor found for class " + cls.getName());
            }
            return (pos, state) -> {
                try {
                    return (BlockEntity) fallbackConstructor.newInstance(BaseBlockEntity.getEntityType(cls), pos, state);
                } catch (Exception ex) {
                    LOGGER.error("Failed to create BlockEntity instance using fallback constructor for class {}", cls.getName(), ex);
                    throw new RuntimeException("Failed to create BlockEntity instance", ex);
                }
            };
        }
    }

    /**
     * 尝试获取构造函数，遇到异常时记录调试日志并返回null。
     */
    private static <T> Constructor<? extends T> getConstructorQuietly(Class<? extends T> cls, Class<?>... parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (Exception e) {
            LOGGER.debug("Constructor {} not found for class {}. Exception: {}", Arrays.toString(parameterTypes), cls.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 使用给定构造函数实例化BlockEntity，如果遇到特定异常则尝试备用构造函数。
     */
    private static BlockEntity instantiateBlockEntity(Class<? extends BlockEntity> cls, Constructor<? extends BlockEntity> constructor, BlockPos pos, BlockState state) {
        try {
            return constructor.newInstance(pos, state);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Failed to create entity type; Entity type is null")) {
                LOGGER.debug("Primary constructor failed for class {} due to null entity type. Attempting fallback constructor.", cls.getName(), e);
                try {
                    Constructor<? extends BlockEntity> fallback = cls.getConstructor(BlockEntityType.class, BlockPos.class, BlockState.class);
                    return fallback.newInstance(BaseBlockEntity.getEntityType(cls), pos, state);
                } catch (Exception fallbackEx) {
                    LOGGER.error("Failed to instantiate BlockEntity using fallback constructor for class {}", cls.getName(), fallbackEx);
                    throw new RuntimeException("Failed to create BlockEntity instance", fallbackEx);
                }
            } else {
                throw new RuntimeException("Failed to create BlockEntity instance", e);
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error instantiating BlockEntity for class {}", cls.getName(), e);
            throw new RuntimeException("Failed to create BlockEntity instance", e);
        }
    }


    private static @NotNull Supplier<BlockItem> getBlockItemSupplier(Class<? extends Block> clazz, RegistryObject<Block> blockRegistryObject) {
        // 预扫描反射信息
        final Method method = Stream.of(clazz.getDeclaredMethods()).filter(m -> {
            int paramCount = m.getParameterCount();
            return BlockItem.class.equals(m.getReturnType()) && paramCount <= 1 && (paramCount == 0 || Block.class.isAssignableFrom(m.getParameterTypes()[0]));
        }).min(Comparator.comparingInt(m -> Modifier.isStatic(m.getModifiers()) ? 0 : 1)).orElse(Stream.of(clazz.getMethods()).filter(m -> {
            int paramCount = m.getParameterCount();
            return BlockItem.class.equals(m.getReturnType()) && paramCount <= 1 && (paramCount == 0 || Block.class.isAssignableFrom(m.getParameterTypes()[0]));
        }).min(Comparator.comparingInt(m -> Modifier.isStatic(m.getModifiers()) ? 0 : 1)).orElseThrow(() -> new IllegalArgumentException("Class " + clazz.getName() + " must have a static factory method " + "returning BlockItem with: 0 parameters or 1 Block-type parameter")));

        final boolean isStatic = Modifier.isStatic(method.getModifiers());
        final int paramCount = method.getParameterCount();
        method.setAccessible(true);

        return () -> {
            final Block instance = blockRegistryObject.get();
            if (instance instanceof BaseBlock) {
                return ((BaseBlock) instance).getBlockItem();
            }

            try {
                final Object[] args = paramCount > 0 ? new Object[]{instance} : null;
                return isStatic ? (BlockItem) method.invoke(null, args) : (BlockItem) method.invoke(instance, args);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Access failed for method: " + method, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Factory method threw exception", e.getTargetException());
            }
        };
    }

    private static Field getDataField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            // 检查类型是否为Data且是静态字段
            if (BaseBlock.Data.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
                return field;
            }
        }
        return null;
    }


    /**
     * 被标注了该注解的类将会被注册到方块以及物品中。
     * 该标记也会自动注册 BlockItem 但是需要被标记的类中有一个方法能够返回 BlockItem。方法如果存在 Block 参数的话则传入Block的实例。
     *
     * @author : zimo
     * &#064;date  : 2025/03/17
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RegisterBlock {
        /**
         * 注册的方块的ID
         */
        String value() default "";

        /**
         * 注册的方块物品的ID，如果为空字符串则与方块ID一致
         */
        String itemId() default "";


        /**
         * 是否注册
         */
        boolean isRegister() default true;

        /**
         * 方块实体类
         */
        Class<? extends BlockEntity> blockEntity() default BaseBlockEntity.class;

        /**
         * 是否注入Data 他是 Data(RegistryObject<Block> blockObj, RegistryObject<Item> itemObj)
         * 如果为true 则在注解下 Data 类型的静态变量(该字段必须是公开的)
         */
        boolean data() default false;

        /**
         * 是否生成方块数据
         */
        boolean isGenerated() default true;

        /**
         * 生成方块数据的描述类
         */
        Class<? extends BaseGeneratedBlockData> generatedData() default BaseGeneratedBlockData.class;
    }
}
