package io.github.zimoyin.zhenfa.creativetab;

import io.github.zimoyin.zhenfa.creativetab.base.CreativeModeTabBuilder;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.world.item.CreativeModeTab;

/**
 * 用于声明测试标签
 */
public class CreativeTabs {
    public static final CreativeModeTab ZHENFA = CreativeModeTabBuilder.builder("zhenfa")
            .lang(Lang.of(Lang.LangType.ZH_CN, "阵法"))
            .build();
}