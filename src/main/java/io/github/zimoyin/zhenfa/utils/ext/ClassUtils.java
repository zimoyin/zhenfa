package io.github.zimoyin.zhenfa.utils.ext;

import java.lang.reflect.Constructor;

/**
 * @author : zimo
 * &#064;date : 2025/03/21
 */
public class ClassUtils {
    /**
     * 查找指定类及其父类中符合条件的构造方法
     *
     * @param clazz          起始类的 Class 对象
     * @param parameterTypes 构造方法的参数类型
     * @return 符合条件的构造方法
     * @throws NoSuchMethodException 如果在整个继承链中未找到符合条件的构造方法
     */
    public static Constructor<?> findConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        // 递归终止条件：clazz 为 null（到达 Object 的父类）
        if (clazz == null) {
            throw new NoSuchMethodException("No matching constructor found in the inheritance chain.");
        }

        try {
            // 尝试获取当前类的构造方法
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            // 如果当前类没有找到，递归查找父类
            return findConstructor(clazz.getSuperclass(), parameterTypes);
        }
    }
}
