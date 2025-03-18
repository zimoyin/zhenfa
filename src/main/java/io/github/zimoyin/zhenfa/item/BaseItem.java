package io.github.zimoyin.zhenfa.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
public abstract class BaseItem extends Item {

    public String itemId(){
        return this.getClass().getName()+"_item";
    }

    public BaseItem(Properties properties) {
        super(properties);
    }
}
