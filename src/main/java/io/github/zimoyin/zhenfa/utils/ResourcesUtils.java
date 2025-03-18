package io.github.zimoyin.zhenfa.utils;

import java.io.InputStream;
import java.net.URL;

/**
 * @author : zimo
 * &#064;date : 2025/03/18
 */
public class ResourcesUtils {
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
