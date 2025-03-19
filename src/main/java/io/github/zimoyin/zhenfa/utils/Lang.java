package io.github.zimoyin.zhenfa.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 方块的语言描述
 */
public class Lang {
    LangType lange;
    String name;
    String groupPrefix;
    String groupId;
    String groupName;
    String key;

    /**
     * @param lange 语言 如 en_us, zh_cn
     * @param name  方块名称
     */
    public Lang(LangType lange, String name) {
        this.lange = lange;
        this.name = name;
        this.groupPrefix = "itemGroup.";
        this.groupId = null;
        this.groupName = null;
        this.key = null;
    }

    public Lang(LangType lange, String key, String name) {
        this.key = key;
        this.name = name;
        this.lange = lange;
        this.groupId = null;
        this.groupName = null;
        this.groupPrefix = "itemGroup.";
    }

    public Lang(LangType lange, Item key, String name) {
        this.key = key.getDescriptionId();
        this.name = name;
        this.lange = lange;
        this.groupId = null;
        this.groupName = null;
        this.groupPrefix = "itemGroup.";
    }


    public Lang(LangType lange, Block key, String name) {
        this.key = key.getDescriptionId();
        this.name = name;
        this.lange = lange;
        this.groupId = null;
        this.groupName = null;
        this.groupPrefix = "itemGroup.";
    }

    /**
     * 带分组的方块语言描述
     *
     * @param groupId   物品组的ID
     * @param groupName 物品组的名称
     */
    public Lang(LangType lange, String name, String groupId, String groupName) {
        this.lange = lange;
        this.name = name;
        this.groupPrefix = "itemGroup.";
        this.groupId = groupId;
        this.groupName = groupName;
        this.key = null;
    }

    public Lang(LangType lange, String name, String groupPrefix, String groupId, String groupName) {
        this.lange = lange;
        this.name = name;
        this.groupPrefix = groupPrefix;
        this.groupId = groupId;
        this.groupName = groupName;
        this.key = null;
    }

    public LangType getLange() {
        return lange;
    }

    public String getName() {
        return name;
    }

    public String getGroupPrefix() {
        return groupPrefix;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupIdWithPrefix() {
        return groupPrefix + groupId;
    }

    public String getGroupName() {
        return groupName;
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

    public void setGroupPrefix(String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setKey(String key) {
        this.key = key;
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