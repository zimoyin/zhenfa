package io.github.zimoyin.zhenfa.world.generation;

import io.github.zimoyin.zhenfa.Zhenfa;
import io.github.zimoyin.zhenfa.block.TestBlock;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.github.zimoyin.zhenfa.world.generation.Test.GOLDEN_TREE;
import static io.github.zimoyin.zhenfa.world.generation.Test.MY_ORE;

/**
 * 矿物与花的生成。这里只实现了矿物的生成
 * 树木生成
 *
 * @author : zimo
 * &#064;date : 2025/03/19
 */
@Mod.EventBusSubscriber(modid = Zhenfa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Test {
    public static Holder<PlacedFeature> MY_ORE;
    public static Holder<ConfiguredFeature<OreConfiguration, ?>> MY_ORE_FEATURE;

    // 矿石生成
    public static void registerFeatures() {
        // RandomPatchFeature: 花也是一种地物。原版使用 RandomPatchFeature 来实现平原、向日葵平原、森林、繁花森林中的野花生成。
        // -- RandomPatchConfiguration: 花的配置。
        // OreFeature: 矿物生成器。原版使用 OreFeature 来实现矿物生成。
        // -- OreConfiguration: 矿物配置。

        MY_ORE_FEATURE = FeatureUtils.register(
                // 我们这个生成器的名字是 zhenfa:underground_ice，
                "zhenfa:underground_ice",
                // 并基于 Feature.ORE
                Feature.ORE,
                // 这个生成器的配置如下：
                new OreConfiguration(
                        // 将原版的「石头」（同时包括了安山岩、闪长岩、和花岗岩）……
                        OreFeatures.STONE_ORE_REPLACEABLES,
                        // ……替换为原版的蓝冰，默认状态，一批最多 60 块。
//                        Blocks.BLUE_ICE.defaultBlockState(), 60)
                        TestBlock.RegisterBlockData.getBlock().defaultBlockState(), 60)
        );

        MY_ORE = PlacementUtils.register(
                "zhenfa:underground_ice",
                MY_ORE_FEATURE,
                List.of(CountPlacement.of(30),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.bottom(),
                                VerticalAnchor.top())));

    }

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> GOLDEN_TREE_FEATURE;
    public static Holder<PlacedFeature> GOLDEN_TREE;

    /**
     * 树也是一种地物，但树的生成较为复杂，因为原版将树的生成切割成了三部分：
     *
     * 树干/树桩。
     * 树冠/树叶。
     * 树上的装饰，例如丛林及沼泽中树上的藤蔓，森林中树上的蜂巢等）。
     * 这些属性共同组成了 TreeConfiguration，用于和 TreeFeature 搭配使用。
     *
     * 原版为我们提供了 TreeConfiguration.TreeConfigurationBuilder 以方便我们配置全新树木地物。 我们可以像下面这样创建一个全新的 TreeFeature
     */
    public static void registerFeatures2() {
        GOLDEN_TREE_FEATURE = FeatureUtils.register(
                "zhenfa:golden_tree",
                Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(Blocks.GOLD_BLOCK),
                        new StraightTrunkPlacer(8, 12, 0),
                        BlockStateProvider.simple(Blocks.GLOWSTONE),
                        new BlobFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), 4),
                        new TwoLayersFeatureSize(1,1,1)
                ).build()
        );
        GOLDEN_TREE = PlacementUtils.register(
                "zhenfa:golden_tree",
                GOLDEN_TREE_FEATURE,
                PlacementUtils.countExtra(6, 0.1F, 1),
                InSquarePlacement.spread(),
                VegetationPlacements.TREE_THRESHOLD,
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING),
                BiomeFilter.biome()
        );
    }



    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        // 推迟调用此方法
        event.enqueueWork(Test::registerFeatures);
        event.enqueueWork(Test::registerFeatures2);
    }
}

// Forge总线事件（如BiomeLoadingEvent）
@Mod.EventBusSubscriber(modid = Zhenfa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ForgeEventHandler {
    /**
     * 最后，我们要将 PlacedFeature 加入世界生成。
     * <p>
     * 原版的每一个生物群系都对应一套生物群系内特有的生成规则，其中包括「待使用的 PlacedFeature 列表」，
     * 那么我们的目标就是要把我们的 MY_ORE 塞进所有生物群系的这个表里。
     */
    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        // 向当前正在加载的生物群系生成规则中，添加：在生成地下矿石这一步时，使用我们的 MY_ORE
        // 因为每一个生物群系加载时都会过一遍此事件，因此这样做相当于向所有生物群系添加此生成器。
        event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MY_ORE);

        event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, GOLDEN_TREE);
    }
}

