package io.github.zimoyin.zhenfa.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 战利品表基类，用于生成方块的掉落规则。
 * 通过继承此类并实现 {@link #addTables()} 方法，可以自定义方块的掉落行为。
 */
public abstract class BaseLootTableProvider extends LootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * 存储方块与对应战利品表构建器的映射关系。
     */
    private final Map<Block, LootTable.Builder> blockLootTables = new HashMap<>();
    private final DataGenerator generator;

    /**
     * 构造方法，初始化数据生成器。
     *
     * @param dataGeneratorIn 数据生成器实例
     */
    public BaseLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.generator = dataGeneratorIn;
    }

    /**
     * 抽象方法，子类需在此方法中注册所有方块的掉落规则。
     * 通过调用 {@link #createStandardTable}, {@link #createSimpleTable} 或 {@link #createSilkTouchTable} 方法实现。
     */
    protected abstract void addTables();

    /**
     * 创建带有 NBT 数据保留的战利品表（适用于箱子、机器等方块实体）。
     *
     * @param name  战利品表名称（需与方块注册名一致）
     * @param block 要注册的方块
     * @param type  方块实体类型（用于 NBT 数据复制）
     * @return 战利品表构建器
     * @注意 确保方块已正确注册 {@link BlockEntityType}，否则 NBT 数据无法保存
     */
    public LootTable.Builder createStandardTable(String name, Block block, BlockEntityType<?> type) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)
                        // 复制方块实体的名称到掉落物
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        // 复制指定 NBT 数据到掉落物的 BlockEntityTag
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                .copy("Info", "BlockEntityTag.Info", CopyNbtFunction.MergeStrategy.REPLACE)
                                .copy("Inventory", "BlockEntityTag.Inventory", CopyNbtFunction.MergeStrategy.REPLACE)
                                .copy("Energy", "BlockEntityTag.Energy", CopyNbtFunction.MergeStrategy.REPLACE))
                        // 设置容器内容（如箱子的物品）
                        .apply(SetContainerContents.setContents(type)
                                .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                );
        LootTable.Builder result = LootTable.lootTable().withPool(builder);
        blockLootTables.put(block, result);
        return result;
    }

    /**
     * 创建基础战利品表（直接掉落方块自身）。
     *
     * @param name  战利品表名称（需与方块注册名一致）
     * @param block 要注册的方块
     * @return 战利品表构建器
     * @注意 适用于无特殊逻辑的普通方块（如石头、木头等）
     */
    public LootTable.Builder createSimpleTable(String name, Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)); // 直接掉落方块
        LootTable.Builder result = LootTable.lootTable().withPool(builder);
        blockLootTables.put(block, result);
        return result;
    }

    /**
     * 创建精准采集战利品表（支持精准采集和时运附魔）。
     *
     * @param name     战利品表名称（需与方块注册名一致）
     * @param block    要注册的方块
     * @param lootItem 非精准采集时掉落的物品
     * @param min      最小掉落数量（例如 2）
     * @param max      最大掉落数量（例如 5）
     * @return 战利品表构建器
     * @注意 参数 min/max 为浮点数，但实际掉落会取整数部分（如 2.5f 会被视为 2）
     */
    public LootTable.Builder createSilkTouchTable(String name, Block block, Item lootItem, float min, float max) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(AlternativesEntry.alternatives(
                        // 精准采集条件：掉落方块自身
                        LootItem.lootTableItem(block)
                                .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                        .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))),
                        // 非精准采集条件：掉落指定物品
                        LootItem.lootTableItem(lootItem)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))) // 数量随机
                                .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1)) // 时运加成
                                .apply(ApplyExplosionDecay.explosionDecay()) // 爆炸衰减
                ));
        LootTable.Builder result = LootTable.lootTable().withPool(builder);
        blockLootTables.put(block, result);
        return result;
    }

    /**
     * 执行数据生成，将注册的战利品表写入文件。
     *
     * @param cache 哈希缓存（用于检测文件是否已更新）
     * @throws IOException 文件写入异常
     */
    @Override
    public void run(@NotNull HashCache cache) {
        addTables(); // 调用子类实现注册逻辑

        // 将 Map<Block, Builder> 转换为 Map<ResourceLocation, LootTable>
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : blockLootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue()
                    .setParamSet(LootContextParamSets.BLOCK) // 设置上下文参数为方块类型
                    .build());
        }
        writeTables(cache, tables); // 写入文件
    }


    /**
     * 将生成的战利品表写入到指定目录。
     *
     * @param cache  哈希缓存
     * @param tables 要写入的战利品表集合
     */
    private void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            // 生成文件路径：data/modid/loot_tables/blocks/block_name.json
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                // 序列化并保存 JSON 文件
                DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("无法写入战利品表 {}", path, e);
            }
        });
    }

    @Override
    public String getName() {
        return "MyTutorial LootTables";
    }
}