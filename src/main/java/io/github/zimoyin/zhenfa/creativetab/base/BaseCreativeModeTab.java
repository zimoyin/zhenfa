package io.github.zimoyin.zhenfa.creativetab.base;

import io.github.zimoyin.zhenfa.utils.Lang;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zimo
 * &#064;date : 2025/03/20
 */
public abstract class BaseCreativeModeTab extends CreativeModeTab {

    private final ArrayList<Lang> lang = new ArrayList<>();
    private final String langId;

    public BaseCreativeModeTab(String label, ArrayList<Lang> lang) {
        super(label);
        this.lang.addAll(lang);
        langId = label;
        init();
    }

    public BaseCreativeModeTab(int order, String label, ArrayList<Lang> lang) {
        super(order, label);
        this.lang.addAll(lang);
        langId = label;
        init();
    }

    public BaseCreativeModeTab(String label) {
        super(label);
        langId = label;
        init();
    }

    public BaseCreativeModeTab(int order, String label) {
        super(order, label);
        langId = label;
        init();
    }

    public List<Lang> getLang() {
        return lang;
    }

    public void setLang(Lang... lang) {
        this.lang.addAll(List.of(lang));
    }

    public void init() {
        CreativeModeTabBuilder.TABS.add(this);
    }

    public String getLangId() {
        return "itemGroup." + langId;
    }
}
