package io.github.zimoyin.zhenfa.utils;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : zimo
 * @date : 2025/03/16
 */
public class ScanResultUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String FieldNameScanResults = "scanResults";
    private static final String FieldNameContainer = "container";


    /**
     * 获取指定modId的下的所有的类，注意可能掺杂其他地方的类注意过滤
     * @param modId mod 的ID
     * @return 类的集合
     */
    public static List<String> getModClasses(String modId) {
        return getModScanData(modId)
                .map(ModFileScanData::getClasses)
                .map(set -> set.stream()
                        .map(ModFileScanData.ClassData::clazz)
                        .map(Type::getClassName)
                        .toList()
                )
                .orElse(new ArrayList<>());
    }

    /**
     * 获取当前mod下的所有类
     * @param context 上下文 FMLJavaModLoadingContext.get()
     * @return 类的集合
     */
    public static List<String> getModClasses(FMLJavaModLoadingContext context) {
        try {
            FMLModContainer modContainer = getModContainer(context);
            ModFileScanData modScanData = getModScanData(modContainer);
            return modScanData.getClasses().stream().map(ModFileScanData.ClassData::clazz).map(Type::getClassName).toList();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to get mod scan data", e);
            return new ArrayList<>();
        }
    }

    public static Optional<ModFileScanData> getModScanData(String modId) {
        return ModList.get().getModContainerById(modId)
                .filter(ScanResultUtils::isFMLModContainer)
                .map(modContainer -> {
                    try {
                        return getModScanData(modContainer);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    public static boolean isFMLModContainer(Object modContainer) {
        return modContainer instanceof FMLModContainer;
    }

    public static FMLModContainer getModContainer(FMLJavaModLoadingContext context) throws NoSuchFieldException, IllegalAccessException {
        Field field = FMLJavaModLoadingContext.class.getDeclaredField(FieldNameContainer);
        field.setAccessible(true);
        Object container = field.get(context);
        if (!isFMLModContainer(container))
            throw new IllegalArgumentException("context is not FMLJavaModLoadingContext");
        return (FMLModContainer) field.get(context);
    }


    public static ModFileScanData getModScanData(ModContainer modContainer) throws NoSuchFieldException, IllegalAccessException {
        if (!isFMLModContainer(modContainer)) throw new IllegalArgumentException("modContainer is not FMLModContainer");
        Field field = FMLModContainer.class.getDeclaredField(FieldNameScanResults);
        field.setAccessible(true);
        return (ModFileScanData) field.get(modContainer);
    }
}
