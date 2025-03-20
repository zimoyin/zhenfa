package io.github.zimoyin.zhenfa.item.base;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
public class BaseItem extends Item {
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
        private BaseGeneratedItemData data;
        private String itemId;

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
            if (annotation != null && annotation.value() != null && !annotation.value().isEmpty())
                return annotation.value();
            if (cls != null) return cls.getSimpleName().toLowerCase();
            return Objects.requireNonNullElseGet(itemId, () -> itemRegistryObject.get().getRegistryName().getPath());
        }

        public boolean isGenerated() {
            if (annotation != null) return annotation.isGenerated();
            return data != null;
        }


        public BaseGeneratedItemData getGeneratedData() {
            try {
                if (data == null) {
                    if (annotation != null) {
                        data = annotation.generatedData().getConstructor(BaseItem.Data.class).newInstance(this);
                    }
                }
            } catch (Exception e) {
                data = new BaseGeneratedItemData(this);
                LOGGER.error("\n!!!!! ERROR !!!!!!\nFailed to create generated data for block {}\n@See: Please set it as public static\n", annotation.generatedData(), e);
            }
            return data;
        }


        public Data setGeneratedDataClass(Class<? extends BaseGeneratedItemData> dataClass) {
            if (dataClass == null) return this;
            try {
                if (data == null) {
                    if (annotation != null) {
                        data = annotation.generatedData().getConstructor(BaseItem.Data.class).newInstance(this);
                    } else {
                        data = dataClass.getConstructor(BaseItem.Data.class).newInstance(this);
                    }
                }
            } catch (Exception e) {
                data = new BaseGeneratedItemData(this);
                LOGGER.error("\n!!!!! ERROR !!!!!!\nFailed to create generated data for block {}\n@See: Please set it as public static\n", annotation.generatedData(), e);
            }
            return this;
        }

        public Data setGeneratedData(BaseGeneratedItemData data) {
            if (annotation == null && data != null && this.data == null) this.data = data;
            return this;
        }

        public Data setItemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Item getItem() {
            return itemRegistryObject.get();
        }
    }
}
