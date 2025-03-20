package io.github.zimoyin.zhenfa.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public class ResourcesUtils {
    /**
     * 校验字符串是否符合 Minecraft 资源路径规范
     *
     * @param input 要校验的字符串
     * @throws IllegalArgumentException 如果包含非法字符
     */
    public static void validateResourcePath(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        final Pattern INVALID_PATTERN = Pattern.compile("[^a-z0-9/._-]");

        Matcher matcher = INVALID_PATTERN.matcher(input);
        if (matcher.find()) {
            // 获取非法字符的位置和具体字符
            int errorIndex = matcher.start();
            char invalidChar = input.charAt(errorIndex);
            throw new IllegalArgumentException(
                    String.format("Invalid (Non [a-z0-9/._-] character) character '%s' (Unicode: %d) at index %d in path: %s",
                            invalidChar, (int) invalidChar, errorIndex, input)
            );
        }
    }


    public static InputStream getResource(String path) {
        InputStream resource = null;

        // Try class-based resource loading
        Class<?> clazz = ResourcesUtils.class;
        resource = clazz.getResourceAsStream(path);
        if (resource != null) return resource;

        resource = clazz.getResourceAsStream("/" + path);
        if (resource != null) return resource;

        resource = clazz.getResourceAsStream("./" + path);
        if (resource != null) return resource;

        // Try thread context class loader
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            resource = contextClassLoader.getResourceAsStream(path);
            if (resource != null) return resource;

            resource = contextClassLoader.getResourceAsStream("/" + path);
            if (resource != null) return resource;

            resource = contextClassLoader.getResourceAsStream("./" + path);
            if (resource != null) return resource;
        }

        // Try path's class loader (String.class loader)
        ClassLoader pathClassLoader = path.getClass().getClassLoader();
        if (pathClassLoader != null) {
            resource = pathClassLoader.getResourceAsStream(path);
            if (resource != null) return resource;

            resource = pathClassLoader.getResourceAsStream("/" + path);
            if (resource != null) return resource;

            resource = pathClassLoader.getResourceAsStream("./" + path);
            if (resource != null) return resource;
        }

        // Try system class loader
        resource = ClassLoader.getSystemResourceAsStream(path);
        if (resource != null) return resource;

        resource = ClassLoader.getSystemResourceAsStream("/" + path);
        if (resource != null) return resource;

        resource = ClassLoader.getSystemResourceAsStream("./" + path);
        if (resource != null) return resource;

        // Try direct URL loading
        try {
            return new URL(path).openStream();
        } catch (Exception ignored) {
            // Ignore exceptions
        }

        return null;
    }

}
