package io.github.zimoyin.zhenfa.utils;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 静态导入内部工具方法
import static io.github.zimoyin.zhenfa.utils.ScanResultUtils.Utils.getModContainer;
import static io.github.zimoyin.zhenfa.utils.ScanResultUtils.Utils.getModScanData;

/**
 * ScanResultUtils 类用于获取指定 mod 或当前 mod 的扫描数据，包括类和注解信息
 * 提供了多种重载方法供不同场景下使用，如根据 modId 或 FMLJavaModLoadingContext 获取数据
 *
 * @author : zimo
 * @date : 2025/03/16
 */
public class ScanResultUtils {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 内部字段名称常量，用于反射获取 FMLModContainer 中的私有字段
    private static final String FieldNameScanResults = "scanResults";
    private static final String FieldNameContainer = "container";

    /**
     * 根据指定 modId 获取该 mod 扫描到的所有类的全限定名列表
     * 可能包含来自其他包的类，请注意后续过滤
     *
     * @param modId mod 的 ID
     * @return 类全限定名的集合
     */
    public static List<String> getModClasses(String modId) {
        return getModScanData(modId)
                .map(ModFileScanData::getClasses)
                .map(set -> set.stream()
                        .map(ModFileScanData.ClassData::clazz)  // 获取 Type 对象
                        .map(Type::getClassName)                  // 转换为类的全限定名
                        .toList()
                )
                .orElse(new ArrayList<>());
    }

    /**
     * 根据指定 modId 获取该 mod 扫描到的所有类的 Type 对象集合
     * 可能包含来自其他包的类，请注意后续过滤
     *
     * @param modId mod 的 ID
     * @return 类 Type 对象的集合
     */
    public static List<Type> getModTypeClasses(String modId) {
        return getModScanData(modId)
                .map(ModFileScanData::getClasses)
                .map(set -> set.stream()
                        .map(ModFileScanData.ClassData::clazz)
                        .toList()
                )
                .orElse(new ArrayList<>());
    }

    /**
     * 根据当前 mod 的加载上下文获取当前 mod 扫描到的所有类的全限定名列表
     *
     * @param context FMLJavaModLoadingContext 上下文对象
     * @return 类全限定名的集合
     */
    public static List<String> getModClasses(FMLJavaModLoadingContext context) {
        try {
            // 通过反射获取当前 mod 的 FMLModContainer
            FMLModContainer modContainer = getModContainer(context);
            // 获取 mod 的扫描数据
            ModFileScanData modScanData = getModScanData(modContainer);
            // 遍历扫描到的所有类并转换为全限定名
            return modScanData.getClasses().stream()
                    .map(ModFileScanData.ClassData::clazz)
                    .map(Type::getClassName)
                    .toList();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to get mod scan data", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据当前 mod 的加载上下文获取当前 mod 扫描到的所有类的 Type 对象集合
     *
     * @param context FMLJavaModLoadingContext 上下文对象
     * @return 类 Type 对象的集合
     */
    public static List<Type> getModTypeClasses(FMLJavaModLoadingContext context) {
        try {
            FMLModContainer modContainer = getModContainer(context);
            ModFileScanData modScanData = getModScanData(modContainer);
            return modScanData.getClasses().stream()
                    .map(ModFileScanData.ClassData::clazz)
                    .toList();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to get mod scan data", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据指定 modId 获取该 mod 扫描到的所有注解数据，并封装为 AnnotationData 对象
     *
     * @param modId mod 的 ID
     * @return AnnotationData 对象的集合
     */
    public static List<AnnotationData> getModAnnotations(String modId) {
        return getModScanData(modId)
                .map(ModFileScanData::getAnnotations)
                .map(set -> set.stream()
                        .map(AnnotationData::new)
                        .toList()
                )
                .orElse(new ArrayList<>());
    }

    /**
     * 根据当前 mod 的加载上下文获取当前 mod 扫描到的所有注解数据，并封装为 AnnotationData 对象
     *
     * @param context FMLJavaModLoadingContext 上下文对象
     * @return AnnotationData 对象的集合
     */
    public static List<AnnotationData> getModAnnotations(FMLJavaModLoadingContext context) {
        try {
            FMLModContainer modContainer = getModContainer(context);
            ModFileScanData modScanData = getModScanData(modContainer);
            return modScanData.getAnnotations().stream()
                    .map(AnnotationData::new)
                    .toList();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to get mod scan data", e);
            return new ArrayList<>();
        }
    }

    /**
     * 内部工具类，用于通过反射获取 mod 的扫描数据和容器对象
     */
    public static class Utils {

        /**
         * 根据 modId 获取对应的 ModFileScanData
         *
         * @param modId mod 的 ID
         * @return Optional 包装的 ModFileScanData 对象
         */
        public static Optional<ModFileScanData> getModScanData(String modId) {
            return ModList.get().getModContainerById(modId)
                    .filter(Utils::isFMLModContainer)
                    .map(modContainer -> {
                        try {
                            return getModScanData(modContainer);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        /**
         * 判断传入的 modContainer 是否为 FMLModContainer 类型
         *
         * @param modContainer mod 容器对象
         * @return 如果是 FMLModContainer 则返回 true，否则 false
         */
        public static boolean isFMLModContainer(Object modContainer) {
            return modContainer instanceof FMLModContainer;
        }

        /**
         * 通过 FMLJavaModLoadingContext 获取当前 mod 的 FMLModContainer 对象
         *
         * @param context FMLJavaModLoadingContext 上下文对象
         * @return 当前 mod 的 FMLModContainer 对象
         * @throws NoSuchFieldException   如果找不到字段
         * @throws IllegalAccessException 如果字段不可访问
         */
        public static FMLModContainer getModContainer(FMLJavaModLoadingContext context) throws NoSuchFieldException, IllegalAccessException {
            Field field = FMLJavaModLoadingContext.class.getDeclaredField(FieldNameContainer);
            field.setAccessible(true);
            Object container = field.get(context);
            if (!isFMLModContainer(container))
                throw new IllegalArgumentException("context is not FMLJavaModLoadingContext");
            return (FMLModContainer) container;
        }

        /**
         * 通过反射从 FMLModContainer 中获取 ModFileScanData 数据
         *
         * @param modContainer mod 容器对象
         * @return ModFileScanData 扫描数据
         * @throws NoSuchFieldException   如果找不到字段
         * @throws IllegalAccessException 如果字段不可访问
         */
        public static ModFileScanData getModScanData(ModContainer modContainer) throws NoSuchFieldException, IllegalAccessException {
            if (!isFMLModContainer(modContainer))
                throw new IllegalArgumentException("modContainer is not FMLModContainer");
            Field field = FMLModContainer.class.getDeclaredField(FieldNameScanResults);
            field.setAccessible(true);
            return (ModFileScanData) field.get(modContainer);
        }
    }

    /**
     * AnnotationData 类对 ModFileScanData.AnnotationData 进行封装，提供便捷的方法获取注解信息
     */
    public record AnnotationData(ModFileScanData.AnnotationData annotationData) {

        /**
         * 判断该注解是否与指定的 Class 对象相同
         *
         * @param annotationClass 注解的 Class 对象
         * @return 是否相同
         */
        public boolean isAnnotation(Class<?> annotationClass) {
            return annotationClass.getName().equals(getAnnotationClassName());
        }

        /**
         * 判断该注解是否与指定的注解类名相同
         *
         * @param annotationClass 注解的全限定名
         * @return 是否相同
         */
        public boolean isAnnotation(String annotationClass) {
            return annotationClass.equals(getAnnotationClassName());
        }

        /**
         * 判断注解类是否在指定的包下
         *
         * @param packageName 包名
         * @return 是否在指定包下
         */
        public boolean isPackageAnnotation(String packageName) {
            return getAnnotationClassName().startsWith(packageName);
        }

        /**
         * 判断被注解的类是否在指定的包下
         *
         * @param packageName 包名
         * @return 是否在指定包下
         */
        public boolean isPackageTarget(String packageName) {
            return getAnnotationClassName().startsWith(packageName);
        }


        /**
         * 判断被注解的类是否在指定的类的继承结构中。即是否是指定类的子类或者子孙类
         *
         * @param cls 指定的类
         * @return 是否在指定类的继承结构中
         */
        public boolean isClassAssignableFrom(Class<?> cls) {
            // 通过 ASM Type 获取目标类的内部名称（格式如 "com/example/MyClass"）
            String targetInternalName = getTargetClassType().getInternalName();
            // 使用传入的 cls 所属的类加载器作为查找资源的依据
            ClassLoader loader = cls.getClassLoader();
            // 检查目标类及其父类链中是否存在传入的 cls
            return isAssignableFromInternal(targetInternalName, cls.getName(), loader);
        }

        /**
         * 递归方法：判断 internalName 表示的类是否等于 expectedSuperclassName，
         * 或者其父类链中是否存在 expectedSuperclassName。
         *
         * @param internalName           目标类或当前递归类的内部名称（格式如 "com/example/MyClass"）
         * @param expectedSuperclassName 待比较的父类全限定名（例如 "java.lang.Object"）
         * @param loader                 用于加载类字节码资源的类加载器
         * @return 如果 expectedSuperclassName 在 internalName 表示类的父类链中返回 true，否则 false
         */
        private boolean isAssignableFromInternal(String internalName, String expectedSuperclassName, ClassLoader loader) {
            // 将内部名称转换为全限定类名
            String canonicalName = internalName.replace('/', '.');
            // 如果当前类与待比较的类全限定名一致，则满足要求
            if (canonicalName.equals(expectedSuperclassName)) {
                return true;
            }
            // 构造当前类对应的 .class 资源路径
            String resourcePath = internalName + ".class";
            try (InputStream in = loader.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    // 如果未能找到字节码文件，则无法继续判断，返回 false
                    return false;
                }
                // 使用 ASM 读取类字节码信息，不加载类
                ClassReader reader = new ClassReader(in);
                // 获取直接父类的内部名称
                String superName = reader.getSuperName();
                if (superName == null) {
                    // 已经到达 java.lang.Object 或无父类，则返回 false
                    return false;
                }
                // 递归检查父类链是否包含 expectedSuperclassName
                return isAssignableFromInternal(superName, expectedSuperclassName, loader);
            } catch (IOException e) {
                // 出现 I/O 异常时返回 false
                return false;
            }
        }


        /**
         * 判断该注解是否是类级别的注解
         *
         * @return 如果是类注解返回 true，否则 false
         */
        public boolean isClassAnnotation() {
            return getAnnotationElementType().equals(ElementType.TYPE);
        }

        /**
         * 获取注解的类的全限定名
         *
         * @return 注解类的全限定名
         */
        public String getAnnotationClassName() {
            return annotationData.annotationType().getClassName();
        }

        /**
         * 通过反射获取注解的 Class 对象
         */
        public <T extends Annotation> Annotation getClassAnnotation(Class<T> tClass) throws ClassNotFoundException {
            return getTargetClass().getAnnotation(tClass);
        }

        /**
         * 通过反射获取注解的 Class 对象
         *
         * @return 注解的 Class 对象
         * @throws ClassNotFoundException 如果类未找到
         */
        public Class<?> getAnnotationClass() throws ClassNotFoundException {
            return Class.forName(getAnnotationClassName());
        }

        /**
         * 获取被注解的目标类的全限定名
         *
         * @return 目标类的全限定名
         */
        public String getTargetClassName() {
            return annotationData.clazz().getClassName();
        }

        /**
         * 通过反射获取被注解的目标类的 Class 对象
         *
         * @return 目标类的 Class 对象
         * @throws ClassNotFoundException 如果类未找到
         */
        public Class<?> getTargetClass() throws ClassNotFoundException {
            return Class.forName(getTargetClassName());
        }

        /**
         * 获取注解的成员名称（如方法或属性名称）
         *
         * @return 成员名称
         */
        public String getTargetName() {
            return annotationData.memberName();
        }

        /**
         * 获取注解目标的 ElementType 类型
         *
         * @return ElementType 类型
         */
        public ElementType getAnnotationElementType() {
            return annotationData.targetType();
        }

        /**
         * 获取注解中的数据，以键值对形式返回
         *
         * @return 注解数据的 Map
         */
        public Map<String, Object> getAnnotationData() {
            return annotationData.annotationData();
        }

        /**
         * 获取注解类的 Type 对象
         *
         * @return 注解类的 Type 对象
         */
        public Type getAnnotationClassType() {
            return annotationData.annotationType();
        }

        /**
         * 获取目标类的 Type 对象
         *
         * @return 目标类的 Type 对象
         */
        public Type getTargetClassType() {
            return annotationData.clazz();
        }
    }
}
