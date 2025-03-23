package io.github.zimoyin.zhenfa.block.base;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;

import static net.minecraft.world.level.block.SculkSensorBlock.WATERLOGGED;
import static net.minecraft.world.level.block.SlabBlock.TYPE;

/**
 * 这是一个基础方块类，继承本类后，在子类上创建一个@RegisterBlock注解，即可自动注册方块和方块物品。<br/>
 * 关于方块的材质和方块物品的材质问题，查阅文档即可。推荐使用 BaseGeneratedBlockData 进行代码生成
 *
 * @author : zimo
 * &#064;date  : 2025/03/16
 * @see BaseGeneratedBlockData
 */
public class BaseBlock extends Block implements IBaseBlock {

    public BaseBlock(Material material) {
        super(BlockBehaviour.Properties.of(material).strength(1.5F, 6.0F));
        if (isSlabBlock())
            this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.FALSE));
    }

    public BaseBlock(Properties properties) {
        super(properties);
        if (isSlabBlock())
            this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.FALSE));
    }

    private String blockName;
    @Deprecated
    private int harvestLevel = Integer.MAX_VALUE;
    @Deprecated
    private ToolType toolType = null;


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        if (isSlabBlock()) pBuilder.add(TYPE, WATERLOGGED);
    }


    @NotNull
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (isSlabBlock()){
            final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

            SlabType slabtype = pState.getValue(TYPE);
            return switch (slabtype) {
                case DOUBLE -> Shapes.block();
                case TOP -> TOP_AABB;
                default -> BOTTOM_AABB;
            };
        }else {
            return super.getShape(pState, pLevel, pPos, pContext);
        }
    }

    /**
     * 设置方块物品。重写该方法可以修改方块物品的属性
     */
    public BlockItem getBlockItem() {
        return new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
    }

    @Override
    public String getBlockName() {
        return blockName;
    }

    /**
     * 设置方块名称。设置后运行 gradle:runData 后生成 en_us lang 文件。如果设置了 BaseGeneratedBlockData 则不生效
     *
     * @see io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData#lang
     * @deprecated 请使用国际化的语言文件对其进行修改。或者请看能自动生成 json 的工具
     */
    @Deprecated
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    @Deprecated
    public int getHarvestLevel() {
        return harvestLevel;
    }

    /**
     * 设置挖掘等级, 只有符合该挖掘等级才能挖掘<br/>
     * 注意： 你还需要设置 setToolType
     *
     * @see BaseGeneratedBlockData#tags
     * @deprecated 新版本开始将使用 tags 来判断是否可以挖掘
     */
    @Deprecated
    public void setHarvestLevel(int harvestLevel) {
        this.harvestLevel = harvestLevel;
    }

    @Deprecated
    public ToolType getToolType() {
        return toolType;
    }

    /**
     * 设置挖掘工具，只有符合该工具的才能进行挖掘<br/>
     * 注意： 你还需要设置 setHarvestLevel
     *
     * @see BaseGeneratedBlockData#tags
     * @deprecated 新版本开始将使用 tags 来判断是否可以挖掘
     */
    @Deprecated
    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }


    /**
     * 覆盖重写了 canHarvestBlock 以支持 toolType | harvestLevel 来判断是否可以挖掘。<br/>
     * 如果 toolType | harvestLevel 任意一个都未被赋值则使用 tags 判断是否可以挖掘
     */
    @Override
    public float defaultDestroyTime() {
        if (toolType == null || harvestLevel == Integer.MAX_VALUE) {
            return super.defaultDestroyTime();
        }
        return super.defaultDestroyTime() / 1.25f;
    }

    /**
     * 注册方块数据完成后的数据
     */
    public static class Data {
        private static final Logger LOGGER = LogUtils.getLogger();
        private BaseGeneratedBlockData data;
        private String blockId;
        private final RegistryObject<Block> blockObj;
        private final RegistryObject<BlockItem> itemObj;
        private final RegistryObject<BlockEntityType<?>> blockEntityTypeObj;
        private final Class<? extends Block> cls;
        private final BlockRegisterTables.RegisterBlock annotation;

        public Data(RegistryObject<Block> blockObj, RegistryObject<BlockItem> itemObj, RegistryObject<BlockEntityType<?>> registryBlockEntityType, Class<? extends Block> cls, BlockRegisterTables.RegisterBlock annotation) {
            this.blockObj = blockObj;
            this.itemObj = itemObj;
            this.cls = cls;
            this.blockEntityTypeObj = registryBlockEntityType;
            this.annotation = annotation;
        }

        public Block getBlock() {
            return blockObj.get();
        }

        public BlockItem getBlockItem() {
            return itemObj.get();
        }

        public String getBlockId() {
            if (annotation != null) {
                String annotationValue = annotation.value();
                if (annotationValue != null && !annotationValue.isEmpty()) {
                    return annotationValue;
                }
            }
            if (cls != null) {
                return cls.getSimpleName().toLowerCase();
            }
            return Objects.requireNonNullElseGet(blockId, () -> blockObj.get().getRegistryName().getPath());
        }

        public String getItemId() {
            if (annotation == null || annotation.itemId() == null || annotation.itemId().isEmpty()) return getBlockId();
            return annotation.itemId();
        }

        public boolean isGenerated() {
            if (annotation != null) return annotation.isGenerated();
            return data != null;
        }

        public BaseGeneratedBlockData getGeneratedData() {
            try {
                if (data == null) {
                    if (annotation != null) {
                        data = annotation.generatedData().getConstructor(Data.class).newInstance(this);
                    }
                }
            } catch (Exception e) {
                data = new BaseGeneratedBlockData(this);
                LOGGER.error("\n!!!!! ERROR !!!!!!\nFailed to create generated data for block {}\n@See: Please set it as public static\n", annotation.generatedData(), e);
            }
            return data;
        }

        public Data setGeneratedDataClass(Class<? extends BaseGeneratedBlockData> dataClas) {
            if (dataClas == null) return this;
            try {
                if (data == null) {
                    data = dataClas.getConstructor(Data.class).newInstance(this);
                }
            } catch (Exception e) {
                data = new BaseGeneratedBlockData(this);
                LOGGER.error("\n!!!!! ERROR !!!!!!\nFailed to create generated data for block {}\n@See: Please set it as public static\n", annotation.generatedData(), e);
            }
            return this;
        }

        public Data setGeneratedData(BaseGeneratedBlockData apply) {
            if (annotation == null && apply != null && data == null) this.data = apply;
            return this;
        }

        /**
         * 后设置 id 仅在不使用反射注册的时候进行传入
         */
        public Data setBlockId(String blockId) {
            if (blockId == null) return this;
            this.blockId = blockId;
            return this;
        }

        public RegistryObject<Block> getBlockObj() {
            return blockObj;
        }

        public RegistryObject<BlockItem> getItemObj() {
            return itemObj;
        }

        public Class<? extends Block> getCls() {
            return cls;
        }

        public BlockRegisterTables.RegisterBlock getAnnotation() {
            return annotation;
        }

        public RegistryObject<BlockEntityType<?>> getBlockEntityTypeObj() {
            return blockEntityTypeObj;
        }
    }
}
