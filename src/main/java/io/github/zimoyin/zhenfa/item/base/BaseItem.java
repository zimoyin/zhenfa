package io.github.zimoyin.zhenfa.item.base;

import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BaseGeneratedBlockData;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
public abstract class BaseItem extends Item {
    private String itemName;


    public BaseItem() {
        super(new Properties().tab(CreativeModeTab.TAB_MISC));
    }

    public BaseItem(CreativeModeTab tab) {
        super(new Properties().tab(tab));
    }

    public BaseItem(String itemName, CreativeModeTab tab) {
        super(new Properties().tab(tab));
        setItemName(itemName);
    }

    public BaseItem(Properties properties) {
        super(properties);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public static class Data {
        private final RegistryObject<Item> itemRegistryObject;
        private final ItemRegterTables.RegisterItem annotation;
        private final Class<? extends Item> cls;

        private final Logger LOGGER = LogManager.getLogger();
        public Data(RegistryObject<Item> itemRegistryObject, Class<? extends Item> cls, ItemRegterTables.RegisterItem annotation) {
            this.itemRegistryObject = itemRegistryObject;
            this.cls = cls;
            this.annotation = annotation;
        }

        public RegistryObject<Item> getItemRegistryObject() {
            return itemRegistryObject;
        }

        public ItemRegterTables.RegisterItem getAnnotation() {
            return annotation;
        }

        public String getItemId() {
            if (annotation.value() != null && !annotation.value().isEmpty()) return annotation.value();
            return cls.getSimpleName().toLowerCase();
        }

        public boolean isGenerated() {
            return annotation.isGenerated();
        }

        private BaseGeneratedItemData data;
        public BaseGeneratedItemData getGeneratedData() {
            try {
                if (data == null) {
                    data = annotation.generatedData().getConstructor(BaseItem.Data.class).newInstance(this);
                }
            } catch (Exception e) {
                data = new BaseGeneratedItemData(this);
                LOGGER.error("\n!!!!! ERROR !!!!!!\nFailed to create generated data for block {}\n@See: Please set it as public static\n", annotation.generatedData(), e);
            }
            return data;
        }

        public Item getItem() {
            return itemRegistryObject.get();
        }
    }
}
