package io.github.zimoyin.zhenfa.utils.ext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;

/**
 * @author : zimo
 * &#064;date : 2025/03/22
 */
public class SerializableUtils {

    /**
     * 将 Serializable 对象序列化为字节数组
     *
     * @param serializable 要序列化的对象
     * @return 序列化后的字节数组
     * @throws IOException 如果序列化过程中发生错误
     */
    public static byte[] serialize(Serializable serializable) throws IOException {
        if (serializable == null) {
            throw new IllegalArgumentException("不能序列化空对象");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(serializable); // 将对象写入字节流
        }
        return byteArrayOutputStream.toByteArray(); // 返回字节数组
    }

    /**
     * 将字节数组反序列化为对象
     *
     * @param bytes 序列化后的字节数组
     * @param <T>   反序列化后的对象类型
     * @return 反序列化后的对象
     * @throws IOException            如果反序列化过程中发生错误
     * @throws ClassNotFoundException 如果找不到对应的类
     */
    public static <T> T deserialize(Class<T> cls, byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes is null!");
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return cls.cast(objectInputStream.readObject());// 从字节流中读取对象
        }
    }


    /**
     * 使用 Gson 实例，支持格式化输出
     */
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // 格式化 JSON（可选）
            .serializeNulls()    // 序列化 null 值（可选）
            .create();

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param obj 要序列化的对象（需为 POJO 或包含无参构造器的类）
     * @return 格式化的 JSON 字符串
     * @throws RuntimeException 如果序列化失败（如对象包含无法转换的类型）
     */
    public static String toJsonString(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型的 Class 对象（如 {@code User.class}）
     * @param <T>   目标类型
     * @return 反序列化后的对象
     * @throws RuntimeException 如果 JSON 格式错误或字段不匹配
     */
    public static <T> T jsonTo(Class<T> clazz, String json) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 将 JSON 对象解析为指定类型的对象
     */
    public static <T> T jsonTo(Class<T> cls, JsonObject json) {
        return GSON.fromJson(json, cls);
    }

    /**
     * 将字符串解析为 JSON 对象
     *
     * @param jsonStr JSON 格式的字符串（如 "{\"name\":\"Alice\",\"age\":30}"）
     * @return {@link JsonObject} 实例
     * @throws RuntimeException 如果字符串格式非法
     */
    public static JsonObject toJsonObject(String jsonStr) {
        return GSON.fromJson(jsonStr, JsonObject.class);
    }

    /**
     * 将字符串解析为 JSON 对象
     *
     * @param jsonStr JSON 格式的字符串（如 "{\"name\":\"Alice\",\"age\":30}"）
     * @return {@link JsonObject} 实例
     * @throws RuntimeException 如果字符串格式非法
     */
    public static JsonArray toJsonArray(String jsonStr) {
        return GSON.fromJson(jsonStr, JsonArray.class);
    }

    /**
     * 将 JSON 对象转换为格式化的字符串
     *
     * @param jsonObject {@link JsonObject} 实例
     * @return 格式化的 JSON 字符串
     */
    public static String toString(JsonObject jsonObject) {
        return GSON.toJson(jsonObject);
    }
}
