package io.github.zimoyin.zhenfa.utils.ext;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author : zimo
 * &#064;date : 2025/03/22
 */
public class CompoundTagUtils {
    public static void putBlockPos(CompoundTag tag, String id, BlockPos pos) {
        if (pos == null) return;
        tag.putInt(id + "x", pos.getX());
        tag.putInt(id + "y", pos.getY());
        tag.putInt(id + "z", pos.getZ());
    }

    public static BlockPos getBlockPos(CompoundTag tag, String id) {
        if (tag.get(id + "x") == null || tag.get(id + "y") == null || tag.get(id + "z") == null){
            return null;
        }
        return new BlockPos(tag.getInt(id + "x"), tag.getInt(id + "y"), tag.getInt(id + "z"));
    }

    public static void putSerializableObject(CompoundTag tag, String key, Serializable value) throws IOException {
        tag.putByteArray(key, SerializableUtils.serialize(value));
    }

    public static <T> T getSerializableObject(CompoundTag tag, Class<T> cls, String key) throws IOException, ClassNotFoundException {
        if (tag.get(key) == null) return null;
        return SerializableUtils.deserialize(cls, tag.getByteArray(key));
    }

    public static JsonArray getJsonArray(CompoundTag tag, String key) {
        if (tag.get(key) == null) return null;
        return SerializableUtils.toJsonArray(tag.getString(key));
    }

    public static <T> Collection<T> getListObject(CompoundTag tag, String key, Class<T> clazz) {
        if (tag.get(key) == null) return null;
        String json = tag.getString(key);
        JsonArray array = SerializableUtils.toJsonArray(json);
        Gson gson = new Gson();
        Collection<T> list = new ArrayList<>();

        for (JsonElement element : array) {
            T obj = gson.fromJson(element, clazz);
            list.add(obj);
        }
        return list;
    }


    public static <T> void putObject(CompoundTag tag, String key, T e) {
        tag.putString(key, SerializableUtils.toJsonString(e));
    }


    public static <T> T getObject(CompoundTag tag, Class<T> cls, String key) {
        if (tag.get(key) == null) return null;
        return SerializableUtils.jsonTo(cls, tag.getString(key));
    }

    public static String getString(CompoundTag tag, String key) {
        if (tag.get(key) == null) return null;
        return tag.getString(key);
    }
}
