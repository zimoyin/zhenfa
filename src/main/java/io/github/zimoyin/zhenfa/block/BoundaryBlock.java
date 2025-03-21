package io.github.zimoyin.zhenfa.block;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import net.minecraft.world.level.material.Material;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
@BlockRegterTables.RegisterBlock("boundary")
public class BoundaryBlock extends BaseBlock {
    public BoundaryBlock() {
        super(Material.STONE);
    }
}
