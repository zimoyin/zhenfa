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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实体方块基础类，该类描述一个确实存在的方块
 */
public abstract class BaseEntityBlock extends net.minecraft.world.level.block.BaseEntityBlock {


    public BaseEntityBlock(Material material) {
        super(Properties.of(material).strength(1.5F, 6.0F));
    }

    public BaseEntityBlock(Properties properties) {
        super(properties);
    }

    @Deprecated
    private String blockName;
    @Deprecated
    private int harvestLevel = Integer.MAX_VALUE;
    @Deprecated
    private BaseBlock.ToolType toolType = null;

    private Class<? extends BlockEntity> entityClazz = null;
    private Method serverTickMethod = null;
    private Method clientTickMethod = null;

    private static final Logger LOGGER = LogUtils.getLogger();


    private Class<? extends BlockEntity> getEntityClazz() {
        if (entityClazz == null) {
            BlockRegterTables.RegisterBlock annotation = this.getClass().getAnnotation(BlockRegterTables.RegisterBlock.class);
            if (annotation == null) {
                LOGGER.error("The Class {} Not Found @{}", this.getClass().getName(), BlockRegterTables.RegisterBlock.class.getSimpleName());
                return entityClazz;
            }
            entityClazz = annotation.blockEntity();
            if (entityClazz == BlockEntity.class) {
                LOGGER.error("The @{} Not Found blockEntity field", BlockRegterTables.RegisterBlock.class.getSimpleName());
                entityClazz = BaseBlockEntity.class;
            }
        }
        return entityClazz;
    }

    private Method getClientTickMethod() throws NoSuchMethodException {
        if (clientTickMethod == null) {
            clientTickMethod = getEntityClazz().getMethod("clientTick", Level.class, BlockPos.class, BlockState.class, BlockEntity.class);
            clientTickMethod.setAccessible(true);
        }
        return clientTickMethod;
    }

    private Method getServerTickMethod() throws NoSuchMethodException {
        if (serverTickMethod == null) {
            serverTickMethod = getEntityClazz().getMethod("serverTick", Level.class, BlockPos.class, BlockState.class, BlockEntity.class);
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

    @Deprecated
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

    /// ///////////////////////////////////////

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        // 注意这一方法的返回值被 BaseEntityBlock 覆盖成了 INVISIBLE，
        // 这代表其无法使用 Minecraft 自带的方块模型的方式渲染。
        // 为保证正常渲染应将这一方法的返回值覆盖回来。
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        BlockEntity blockEntity = null;
        try {
            Constructor<? extends BlockEntity> constructor = getEntityClazz().getConstructor(BlockPos.class, BlockState.class);
            blockEntity = constructor.newInstance(pos, state);
        } catch (Exception e) {
            try {
                Constructor<?> constructor = ClassUtils.findConstructor(getEntityClazz(), BlockEntityType.class, BlockPos.class, BlockState.class);
                blockEntity = (BlockEntity) constructor.newInstance(BaseBlockEntity.getEntityType(getEntityClazz()), pos, state);
            } catch (Exception ex) {
                LOGGER.error("Failed to create BlockEntity; Class {}", getEntityClazz().getName(), ex);
            }
        }
        return blockEntity;
    }

    /**
     * 默认情况下，方块实体并不具备跟随游戏刻刷新（亦即方块实体刻）的能力，
     * 若要获得此能力，方块实体所在的那个方块需要明确声明一个所谓的「Ticker」，亦即 BlockEntityTicker<?>。
     * 这通过覆盖 EntityBlock 的 getTicker 方法实现。
     * <p>
     * 此外，你可以根据 Level 是在逻辑服务器上还是逻辑客户端上来返回不同的 Ticker。
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (getEntityClazz() == null) return super.getTicker(pLevel, pState, pBlockEntityType);
        BlockEntityType<?> entityType = BaseBlockEntity.getEntityType(getEntityClazz());
        return BaseEntityBlock.createTickerHelper(pBlockEntityType, entityType, pLevel.isClientSide ? this::clientTick : this::serverTick);
    }

    /**
     * 每 tick 都会调用，仅在客户端上执行
     */
    public void clientTick(Level level, BlockPos pos, BlockState state, BlockEntity o) {
        // 执行类实体中的 clientTick 方法
        try {
            BlockEntity entity = BaseBlockEntity.getEntityType(getEntityClazz()).getBlockEntity(level, pos);
            if (entity != null) getClientTickMethod().invoke(entity, level, pos, state, o);
            else LOGGER.warn("BaseBlockEntity serverTick method entity is null in {}", getEntityClazz().getName());
        } catch (Exception e) {
            LOGGER.error("BaseBlockEntity clientTick method exception in {}", getEntityClazz().getName(), e);
        }
    }

    /**
     * 每 tick 都会调用，仅在服务端上执行
     */
    public void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {
        // 执行类实体中的 serverTick 方法
        try {
            BlockEntity entity = BaseBlockEntity.getEntityType(getEntityClazz()).getBlockEntity(level, pos);
            if (entity != null) getServerTickMethod().invoke(entity, level, pos, state, e);
            else LOGGER.warn("BaseBlockEntity serverTick method entity is null in {}", getEntityClazz().getName());
        } catch (Exception ex) {
            LOGGER.error("BaseBlockEntity serverTick method exception in {}", getEntityClazz().getName(), ex);
        }
    }
}
