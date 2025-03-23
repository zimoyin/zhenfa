package io.github.zimoyin.zhenfa.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;

import static io.github.zimoyin.zhenfa.block.base.IBaseBlock.ToolType.*;

/**
 * @author : zimo
 * &#064;date : 2025/03/23
 */
public interface IBaseBlock extends IForgeBlock {

    /**
     * 重写该代码以声明为半砖方块
     */
    default boolean isSlabBlock() {
        return false;
    }

    /**
     * 覆盖重写了 canHarvestBlock 以支持 getToolType() | getHarvestLevel() 来判断是否可以挖掘。<br/>
     * 如果 getToolType() | getHarvestLevel() 任意一个都未被赋值则使用 tags 判断是否可以挖掘
     */
    @Override
    default boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        if (getToolType() == null || getHarvestLevel() == Integer.MAX_VALUE) {
            return IForgeBlock.super.canHarvestBlock(state, level, pos, player);
        }

        if (getHarvestLevel() <= -1) return false;
        if (getToolType() == BaseBlock.ToolType.NONE) return true;

        return switch (getToolType()) {
            case PICKAXE, AXE, SHOVEL, HOE, SWORD -> {
                if (!(player.getMainHandItem().getItem() instanceof TieredItem digger)) yield false;
                yield digger.getTier().getLevel() >= getHarvestLevel();
            }
            default -> false;
        };
    }


    /**
     * 设置方块物品。重写该方法可以修改方块物品的属性
     */
    default BlockItem getBlockItem(BaseBlock block) {
        return new BlockItem(block, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
    }

    default String getBlockName() {
        return null;
    }

    default int getHarvestLevel() {
        return Integer.MAX_VALUE;
    }


    default ToolType getToolType(){
        return null;
    }

    enum ToolType {
        /**
         * 镐子
         */
        PICKAXE,
        /**
         * 斧子
         */
        AXE,
        /**
         * 铲子
         */
        SHOVEL,
        /**
         * 斧头
         */
        HOE,
        /**
         * 剑
         */
        SWORD,
        NONE
    }
}
