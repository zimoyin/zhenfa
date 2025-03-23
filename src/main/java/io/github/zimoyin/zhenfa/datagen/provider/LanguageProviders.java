package io.github.zimoyin.zhenfa.datagen.provider;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegisterTables;
import io.github.zimoyin.zhenfa.block.base.IBaseBlock;
import io.github.zimoyin.zhenfa.creativetab.base.BaseCreativeModeTab;
import io.github.zimoyin.zhenfa.creativetab.base.CreativeModeTabBuilder;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.data.LanguageProvider;
import org.slf4j.Logger;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

/**
 * @author : zimo
 * &#064;date : 2025/03/19
 */
public class LanguageProviders extends LanguageProvider {
    private final Lang.LangType currentFileLang;
    private static final Logger LOGGER = LogUtils.getLogger();

    public LanguageProviders(DataGenerator gen, Lang.LangType type) {
        super(gen, DataGenModId, type.getName());
        this.currentFileLang = type;
    }

    @Override
    protected void addTranslations() {
        for (BaseBlock.Data data : BlockRegisterTables.getDataList()) {
            if (data.getBlock() instanceof IBaseBlock data2) {
                if (Lang.LangType.EN_US.equals(currentFileLang) && data2.getBlockName() != null)
                    add(data.getBlock(), data2.getBlockName());
            }
            if (data.isGenerated()) {
                for (Lang lang : data.getGeneratedData().lang()) {
                    if (lang.getLange().equals(currentFileLang)) {
                        try {
                            if (lang.getKey() == null) add(data.getBlock(), lang.getName());
                            else add(lang.getKey(), lang.getName());
                        } catch (IllegalStateException e) {
                            if (e.getMessage().contains("Duplicate translation key")) {
                                LOGGER.warn("Failed to add language information:{}", e.getMessage());
                            } else {
                                LOGGER.error("Failed to add language information:{}", e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
        // 添加物品语言文件
        for (BaseItem.Data data : ItemRegterTables.getDataList()) {
            if (data.getItem() instanceof BaseItem data2) {
                if (Lang.LangType.EN_US.equals(currentFileLang)) add(data.getItem(), data2.getItemName());
            }
            if (data.isGenerated()) {
                for (Lang lang : data.getGeneratedData().lang()) {
                    if (lang.getLange().equals(currentFileLang)) {
                        try {
                            if (lang.getKey() == null) add(data.getItem(), lang.getName());
                            else add(lang.getKey(), lang.getName());
                        } catch (IllegalStateException e) {
                            if (e.getMessage().contains("Duplicate translation key")) {
                                LOGGER.warn("Failed to add language information:{}", e.getMessage());
                            } else {
                                LOGGER.error("Failed to add language information:{}", e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
        // 创造标签页
        for (CreativeModeTab tab : CreativeModeTabBuilder.TABS) {
            if (tab instanceof BaseCreativeModeTab bcmb) {
                for (Lang lang : bcmb.getLang()) {
                    if (lang.getLange().equals(currentFileLang)) {
                        try {
                            add(bcmb.getLangId(), lang.getName());
                        } catch (IllegalStateException e) {
                            if (e.getMessage().contains("Duplicate translation key")) {
                                LOGGER.warn("Failed to add language information:{}", e.getMessage());
                            } else {
                                LOGGER.error("Failed to add language information:{}", e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }
}
