package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegterTables;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

/**
 * @author : zimo
 * &#064;date : 2025/03/19
 */
public class LanguageProviders extends LanguageProvider {
    private final Lang.LangType currentFileLang;

    public LanguageProviders(DataGenerator gen, Lang.LangType type) {
        super(gen, DataGenModId, type.getName());
        this.currentFileLang = type;
    }

    @Override
    protected void addTranslations() {
        for (BaseBlock.Data data : BlockRegterTables.getDataList()) {
            if (data.getBlock() instanceof BaseBlock data2) {
                if (Lang.LangType.EN_US.equals(currentFileLang)) add(data.getBlock(), data2.getBlockName());
            }
            if (data.isGenerated()) {
                for (Lang lang : data.getGeneratedData().lang()) {
                    if (lang.getLange().equals(currentFileLang)) {
                        if (lang.getKey() == null) add(data.getBlock(), lang.getName());
                        else add(lang.getKey(), lang.getName());
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
                        if (lang.getKey() == null) add(data.getItem(), lang.getName());
                        else add(lang.getKey(), lang.getName());
                    }
                }
            }
        }
    }
}
