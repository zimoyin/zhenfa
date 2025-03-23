package io.github.zimoyin.zhenfa.block.base;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.utils.ext.ClassUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static net.minecraft.world.level.block.SculkSensorBlock.WATERLOGGED;
import static net.minecraft.world.level.block.SlabBlock.TYPE;

/**
 * 实体方块基础类，该类描述一个确实存在的方块
 */
public abstract class BaseEntityBlock extends net.minecraft.world.level.block.BaseEntityBlock implements IBaseEntityBlock, IBaseBlock {


    public BaseEntityBlock(Material material) {
        super(Properties.of(material).strength(1.5F, 6.0F));
        if (isSlabBlock())
            this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.FALSE));

    }

    public BaseEntityBlock(Properties properties) {
        super(properties);
        if (isSlabBlock())
            this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.FALSE));
    }

    private String blockName;
    @Deprecated
    private int harvestLevel = Integer.MAX_VALUE;
    @Deprecated
    private BaseBlock.ToolType toolType = null;

    private Class<? extends BlockEntity> entityClazz = null;
    private Method serverTickMethod = null;
    private Method clientTickMethod = null;

    private static final Logger LOGGER = LogUtils.getLogger();


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        if (isSlabBlock()) pBuilder.add(TYPE, WATERLOGGED);
    }


    @NotNull
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (isSlabBlock()) {
            final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

            SlabType slabtype = pState.getValue(TYPE);
            return switch (slabtype) {
                case DOUBLE -> Shapes.block();
                case TOP -> TOP_AABB;
                default -> BOTTOM_AABB;
            };
        } else {
            return super.getShape(pState, pLevel, pPos, pContext);
        }
    }

    @Override
    public Class<? extends BlockEntity> getEntityClazz() {
        if (entityClazz == null) {
            entityClazz = IBaseEntityBlock.super.getEntityClazz();
        }
        return entityClazz;
    }

    @Override
    public Method getClientTickMethod() throws NoSuchMethodException {
        if (clientTickMethod == null) {
            clientTickMethod = IBaseEntityBlock.super.getClientTickMethod();
            clientTickMethod.setAccessible(true);
        }
        return clientTickMethod;
    }

    @Override
    public Method getServerTickMethod() throws NoSuchMethodException {
        if (serverTickMethod == null) {
            serverTickMethod = IBaseEntityBlock.super.getServerTickMethod();
            serverTickMethod.setAccessible(true);
        }
        return serverTickMethod;
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
     * @see BaseGeneratedBlockData#lang
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
    public BaseBlock.ToolType getToolType() {
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
    public void setToolType(BaseBlock.ToolType toolType) {
        this.toolType = toolType;
    }


    /**
     * 覆盖重写了 canHarvestBlock 以支持 toolType | harvestLevel 来判断是否可以挖掘。<br/>
     * 如果 toolType | harvestLevel 任意一个都未被赋值则使用 tags 判断是否可以挖掘
     */
    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        if (toolType == null || harvestLevel == Integer.MAX_VALUE) {
            return super.canHarvestBlock(state, level, pos, player);
        }

        if (harvestLevel <= -1) return false;
        if (toolType == BaseBlock.ToolType.NONE) return true;

        return switch (toolType) {
            case PICKAXE, AXE, SHOVEL, HOE, SWORD -> {
                if (!(player.getMainHandItem().getItem() instanceof TieredItem digger)) yield false;
                yield digger.getTier().getLevel() >= harvestLevel;
            }
            default -> false;
        };
    }

    @Override
    public float defaultDestroyTime() {
        if (toolType == null || harvestLevel == Integer.MAX_VALUE) {
            return super.defaultDestroyTime();
        }
        return super.defaultDestroyTime() / 1.25f;
    }


    /**
     * 获取方块实体
     */
    public <T> Optional<T> getBlockEntity(Class<T> clazz, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return Optional.empty();
        if (clazz.isInstance(blockEntity)) return Optional.of(clazz.cast(blockEntity));
        return Optional.empty();
    }

    /// ///////////////////////////////////////

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        // 注意这一方法的返回值被 BaseEntityBlock 覆盖成了 INVISIBLE，
        // 这代表其无法使用 Minecraft 自带的方块模型的方式渲染。
        // 为保证正常渲染应将这一方法的返回值覆盖回来。
        return RenderShape.MODEL;
    }
}
