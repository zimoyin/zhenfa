package io.github.zimoyin.zhenfa.datagen.provider;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.zimoyin.zhenfa.block.base.BlockRegterTables.getDataList;
import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

/**
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public class LanguageProviders {

    private final DataGenerator generator;

    public LanguageProviders(DataGenerator generator) {
        this.generator = generator;
    }

    public List<LanguageProvider> getLanguageProviders() {
        ArrayList<LanguageProvider> providers = new ArrayList<>();
        for (BaseBlock.Data data : getDataList()) {
            int size = providers.size();
            if (data.isGenerated()) {
                for (BaseGeneratedBlockData.Lang lang : data.getGeneratedData().lang()) providers.add(getProvider(data, lang));
            }
            if (providers.size() == size){
                if (data.getBlock() instanceof BaseBlock){
                    providers.add(getProvider(data, new BaseGeneratedBlockData.Lang(BaseGeneratedBlockData.Lang.en_us,((BaseBlock) data.getBlock()).getBlockName())));
                }
            }
        }
        return providers;
    }

    private @NotNull LanguageProvider getProvider(BaseBlock.Data data, BaseGeneratedBlockData.Lang lang) {
        return new LanguageProvider(generator, DataGenModId, lang.getLange()) {
            @Override
            protected void addTranslations() {
                if (lang.getGroupId() != null) add(lang.getGroupIdWithPrefix(), lang.getGroupName());
                add(data.getBlock(), lang.getName());
            }
        };
    }
}
