package io.github.zimoyin.zhenfa.block.base;

import com.google.common.base.Supplier;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.utils.ScanResultUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;
import static io.github.zimoyin.zhenfa.item.base.ItemRegterTables.ITEMS;
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

    public static List<BaseBlock.Data> getDataList() {
        return BLOCK_DATA_MAP.values().stream().toList();
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
        List<String> classes = ScanResultUtils.getModClasses(context);
        for (String cls : classes) {
            if (cls.equals(BaseBlock.class.getName())) continue;
            if (cls.equals(BaseEntityBlock.class.getName())) continue;
            Class<?> clazz;
            try {
                clazz = Class.forName(cls);
                if (Block.class.isAssignableFrom(clazz)) {
                    registerBlockAndBlockItem((Class<? extends Block>) clazz);
                    LOGGER.info("register block {}", clazz);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to register block; Class {}", cls, e);
            }
        }
        BLOCKS.register(context.getModEventBus());
        BLOCK_ENTITIES.register(context.getModEventBus());
    }

    public static RegistryObject<BlockEntityType<?>> registerBlockEntity(Class<? extends BlockEntity> cls, RegistryObject<Block> blockRegistryObject, String blockId) {
        // 通过反射获取构造函数
        try {
            // 获取构造函数（参数类型：BlockPos, BlockState）
            Constructor<? extends BlockEntity> constructor = cls.getConstructor(BlockPos.class, BlockState.class);

            // 创建 BlockEntitySupplier
            BlockEntityType.BlockEntitySupplier<BlockEntity> supplier = (pos, state) -> {
                try {
                    return constructor.newInstance(pos, state);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create BlockEntity instance", e);
                }
            };

            // 注册 BlockEntityType
            RegistryObject<BlockEntityType<?>> blockEntity = BLOCK_ENTITIES.register(blockId, () ->
                    BlockEntityType.Builder.of(supplier, blockRegistryObject.get())
                            .build(DSL.remainderType())
            );
            BLOCK_ENTITY_DATA_MAP.put(cls, blockEntity);
            return blockEntity;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("BlockEntity class " + cls.getSimpleName()
                    + " must have a constructor with (BlockPos, BlockState) parameters", e);
        }
    }

    public static void registerBlockAndBlockItem(Class<? extends Block> cls) {
        RegisterBlock annotation = cls.getAnnotation(RegisterBlock.class);
        if (annotation == null) {
            LOGGER.warn("Failed to register block; Class {} has no annotation @RegisterBlock", cls.getName());
            return;
        }

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
            registryBlockEntityType = registerBlockEntity(blockEntity, blockRegistryObject, blockId);
        }
        BaseBlock.Data data = new BaseBlock.Data(blockRegistryObject, itemRegistryObject, registryBlockEntityType,cls, annotation);
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

    private static @NotNull Supplier<BlockItem> getBlockItemSupplier(
            Class<? extends Block> clazz,
            RegistryObject<Block> blockRegistryObject
    ) {
        // 预扫描反射信息
        final Method method = Stream.of(clazz.getDeclaredMethods())
                .filter(m -> {
                    int paramCount = m.getParameterCount();
                    return BlockItem.class.equals(m.getReturnType())
                            && paramCount <= 1
                            && (paramCount == 0 || Block.class.isAssignableFrom(m.getParameterTypes()[0]));
                })
                .min(Comparator.comparingInt(m -> Modifier.isStatic(m.getModifiers()) ? 0 : 1))
                .orElse(Stream.of(clazz.getMethods())
                        .filter(m -> {
                            int paramCount = m.getParameterCount();
                            return BlockItem.class.equals(m.getReturnType())
                                    && paramCount <= 1
                                    && (paramCount == 0 || Block.class.isAssignableFrom(m.getParameterTypes()[0]));
                        })
                        .min(Comparator.comparingInt(m -> Modifier.isStatic(m.getModifiers()) ? 0 : 1))
                        .orElseThrow(() -> new IllegalArgumentException("Class " + clazz.getName() + " must have a static factory method " + "returning BlockItem with: 0 parameters or 1 Block-type parameter"))
                );

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
                return isStatic
                        ? (BlockItem) method.invoke(null, args)
                        : (BlockItem) method.invoke(instance, args);
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
         * 方块实体类
         */
        Class<? extends BlockEntity> blockEntity() default BlockEntity.class;

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
