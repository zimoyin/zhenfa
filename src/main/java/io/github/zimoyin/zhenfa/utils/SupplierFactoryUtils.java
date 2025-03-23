package io.github.zimoyin.zhenfa.utils;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author : zimo
 * @date : 2025/03/18
 */
public class SupplierFactoryUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static <I> Supplier<? extends I> createFactory(Class<? extends I> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Failed to create factory; Class is null");
        }
        if (Arrays.stream(cls.getConstructors()).noneMatch(constructor -> constructor.getParameterCount() == 0)) {
            throw new IllegalArgumentException("Failed to create factory; Class " + cls.getName() + " has no empty constructor");
        }
        return () -> {
            try {
                return cls.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <I> Supplier<? extends I> createFactory(Method method, Object instance, Object... args) {
        if (method == null) {
            throw new IllegalArgumentException("Failed to create factory; Method  is null");
        }
        return () -> {
            try {
                if (method.getParameterCount() == 0) {
                    return (I) method.invoke(instance);
                } else {
                    return (I) method.invoke(instance, args);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
