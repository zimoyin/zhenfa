package io.github.zimoyin.zhenfa.block;

import net.minecraft.world.level.material.Material;

/**
 * @author : zimo
 * &#064;date : 2025/03/26
 */
public class ZhenfaTargetBlock extends BaseZhenfaActionBlock {
    private TargetType type;

    public ZhenfaTargetBlock(TargetType type, String name) {
        super(Properties.of(Material.STONE).noOcclusion().lightLevel(state -> 9), name);
        this.type = type;
    }

    public TargetType getType() {
        return type;
    }

    public enum TargetType {
        ENTITY,
        BLOCK,
        PLAYER,
        FRIEND_ENTITY,
        MONSTER,
        ONESELF,
        NONE
    }
}
