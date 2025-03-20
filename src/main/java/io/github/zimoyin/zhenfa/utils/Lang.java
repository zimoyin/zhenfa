package io.github.zimoyin.zhenfa.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 方块的语言描述
 */
public class Lang {
    LangType lange;
    String name;
    String key;

    /**
     * @param lange 语言 如 en_us, zh_cn
     * @param name  方块名称
     */
    public Lang(LangType lange, String name) {
        this.lange = lange;
        this.name = name;
        this.key = null;
    }

    public Lang(LangType lange, String key, String name) {
        this.key = key;
        this.name = name;
        this.lange = lange;
    }

    public Lang(LangType lange, Item key, String name) {
        this.key = key.getDescriptionId();
        this.name = name;
        this.lange = lange;
    }


    public Lang(LangType lange, Block key, String name) {
        this.key = key.getDescriptionId();
        this.name = name;
        this.lange = lange;
    }


    public LangType getLange() {
        return lange;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public void setLange(LangType lange) {
        this.lange = lange;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static Lang of(String name) {
        return new Lang(LangType.EN_US, name);
    }


    public static Lang of(String key, String name) {
        return new Lang(LangType.EN_US, key, name);
    }

    public static Lang of(LangType type, String key, String name) {
        return new Lang(type, key, name);
    }

    public static Lang of(LangType type, String name) {
        return new Lang(type, name);
    }

    public static enum LangType {
        EN_US,      // 英语（美国
        ZH_CN,      // 简体中文（中国）
        ZH_TW,      // 繁体中文（台湾）
        ZH_HK,      // 繁体中文（香港）
        JA_JP;      // 日语（日本）

        public String getName() {
            return name().toLowerCase();
        }

        public boolean equals(String name) {
            return this.getName().equals(name);
        }
    }
}