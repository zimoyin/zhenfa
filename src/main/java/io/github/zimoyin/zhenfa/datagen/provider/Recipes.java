package io.github.zimoyin.zhenfa.datagen.provider;

import com.mojang.logging.LogUtils;
import io.github.zimoyin.zhenfa.block.base.BaseBlock;
import io.github.zimoyin.zhenfa.block.base.BlockRegisterTables;
import io.github.zimoyin.zhenfa.item.base.BaseItem;
import io.github.zimoyin.zhenfa.item.base.ItemRegterTables;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static io.github.zimoyin.zhenfa.Zhenfa.MOD_ID;

/**
 * 合成表
 * 调用 Recipes.getConsumer() 获取参数，之后可以通过 RecipeProvider.xxx 来访问需要的方法
 * Recipes 实例只提供简单的方法更多的查看静态方法和子类 Recipe的静态方法。
 * <p>
 * 如果你需要自行构建你可能需要 ShapedRecipeBuilder
 */
public class Recipes extends RecipeProvider {

    private Consumer<FinishedRecipe> consumer;
    private static final Logger LOGGER = LogUtils.getLogger();
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        this.consumer = consumer;
        for (BaseBlock.Data data : BlockRegisterTables.getDataList()) {
            if (data.isGenerated()) {
                try {
                    data.getGeneratedData().registerBlockItemRecipe(this);
                }catch (Exception e){
                    LOGGER.error("Failed registerItemRecipes: {}", e.getMessage(), e);
                }
            }
        }

        for (BaseItem.Data data : ItemRegterTables.getDataList()) {
            if (data.isGenerated()) {
                try {
                    data.getGeneratedData().registerRecipe(this);
                }catch (Exception e){
                    LOGGER.error("Failed registerItemRecipes: {}", e.getMessage(), e);
                }
            }
        }
    }

    public Consumer<FinishedRecipe> getContext() {
        return consumer;
    }

    /**
     * 创建一个物品转换为另一个物品的配方（默认转换数量为1）。
     *
     * @param inputItem  要转换的输入物品
     * @param outputItem 转换后得到的输出物品
     * @param recipeId   可选的配方标识符
     */
    public void oneToOneConversionRecipe(ItemLike inputItem, ItemLike outputItem, @Nullable String recipeId) {
        RecipeProvider.oneToOneConversionRecipe(consumer, inputItem, outputItem, recipeId);
    }

    /**
     * 创建一个物品转换为另一个物品的配方。
     *
     * @param inputItem   要转换的输入物品
     * @param outputItem  转换后得到的输出物品
     * @param recipeId    可选的配方标识符
     * @param outputCount 输出物品的数量
     */
    public void oneToOneConversionRecipe(ItemLike inputItem, ItemLike outputItem, @Nullable String recipeId, int outputCount) {
        RecipeProvider.oneToOneConversionRecipe(consumer, inputItem, outputItem, recipeId, outputCount);
    }

    /**
     * 创建一个无序的配方，将两个物品转换为一个输出物品。
     *
     * @param ingredient1 第一个输入物品
     * @param ingredient2 第二个输入物品
     * @param outputItem  生成的输出物品
     * @param modid
     */
    public void twoToOneRecipe(ItemLike ingredient1, ItemLike ingredient2, ItemLike outputItem, String modid) {
        // 使用 ShapelessRecipeBuilder 创建一个无序配方，指定输出为 outputItem，数量为1
        ShapelessRecipeBuilder.shapeless(outputItem.asItem(), 1)
                // 添加第一个输入物品
                .requires(ingredient1)
                // 添加第二个输入物品
                .requires(ingredient2)
                // 设置配方解锁条件：例如，玩家必须拥有 ingredient1 才能解锁该配方
                .unlockedBy("has_" + ingredient1.asItem().getRegistryName().getPath(), has(ingredient1))
                // 保存配方，构建唯一的 ResourceLocation 作为配方标识符
                .save(consumer, new ResourceLocation(modid,
                        outputItem.asItem().getRegistryName().getPath() + "_from_" +
                                ingredient1.asItem().getRegistryName().getPath() + "_and_" +
                                ingredient2.asItem().getRegistryName().getPath()));
    }

    /**
     * 创建一个可互逆的配方，将九个小物品转换为一个储存方块（通常用于矿石）。
     *
     * @param smallItem    要合成的九个小物品
     * @param storageBlock 生成的储存方块
     */
    public void nineToOneReciprocalRecipes(ItemLike smallItem, ItemLike storageBlock) {
        RecipeProvider.nineBlockStorageRecipes(consumer, smallItem, storageBlock);
    }

    /**
     * 创建一个可互逆的配方，将九个小物品转换为一个储存方块，并自定义配方标识符。
     *
     * @param smallItem         要合成的九个小物品
     * @param storageBlock      生成的储存方块
     * @param packingRecipeId   打包（合成）配方的标识符
     * @param unpackingRecipeId 拆解配方的标识符
     */
    public void nineToOneReciprocalRecipes(ItemLike smallItem, ItemLike storageBlock, String packingRecipeId, String unpackingRecipeId) {
        RecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, smallItem, storageBlock, packingRecipeId, unpackingRecipeId);
    }

    /**
     * 创建通用的熔炼配方。
     *
     * @param inputItem  需要熔炼的物品
     * @param resultItem 熔炼后生成的物品
     */
    public void smeltingRecipe(ItemLike inputItem, ItemLike resultItem) {
        RecipeProvider.smeltingResultFromBase(consumer, inputItem, resultItem);
    }

    /**
     * 创建一个矿石熔炼成锭的配方。
     *
     * @param oreItems    要熔炼的矿石列表（可包含多个变体）
     * @param ingotItem   熔炼后生成的锭
     * @param experience  熔炼时获得的经验值
     * @param cookingTime 熔炼所需的时间（单位视具体实现而定）
     * @param recipeId    配方标识符
     */
    public void oreSmeltingRecipe(List<ItemLike> oreItems, ItemLike ingotItem, float experience, int cookingTime, String recipeId) {
        RecipeProvider.oreSmelting(consumer, oreItems, ingotItem, experience, cookingTime, recipeId);
    }

    /**
     * 创建一个高炉（爆炸炉）矿石熔炼成锭的配方。
     *
     * @param oreItems    要处理的矿石列表（可包含多个变体）
     * @param ingotItem   处理后生成的锭
     * @param experience  处理时获得的经验值
     * @param cookingTime 处理所需的时间（单位视具体实现而定）
     * @param recipeId    配方标识符
     */
    public void oreBlastFurnaceSmelting(List<ItemLike> oreItems, ItemLike ingotItem, float experience, int cookingTime, String recipeId) {
        RecipeProvider.oreBlasting(consumer, oreItems, ingotItem, experience, cookingTime, recipeId);
    }

    /**
     * 为指定的方块系列生成所有相关的配方。
     *
     * @param blockFamily 需要生成配方的方块系列
     */
    public void generateRecipes(BlockFamily blockFamily) {
        RecipeProvider.generateRecipes(consumer, blockFamily);
    }

    /**
     * 生成一个通用的无序合成配方。
     * 该方法通过 {@link ShapelessRecipeBuilder} 构建无序配方，并自动添加所有输入材料及默认解锁条件。
     *
     * @param consumer    消费者，用于接收生成的完整配方
     * @param outputItem  输出物品（生成的结果）
     * @param outputCount 输出物品数量
     * @param recipeId    配方标识符（建议为独一无二的字符串，用于构造 ResourceLocation）
     * @param ingredients 配方的所有输入材料（无序合成所需的材料）
     */
    public static void createShapelessRecipe(Consumer<FinishedRecipe> consumer, ItemLike outputItem, int outputCount, String recipeId, ItemLike... ingredients) {
        // 创建无序合成配方构建器，指定输出物品和数量
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(outputItem.asItem(), outputCount);

        // 添加所有输入材料
        for (ItemLike ingredient : ingredients) {
            builder.requires(ingredient);
        }

        // 设置解锁条件：默认以第一个材料作为解锁条件
        if (ingredients.length > 0) {
            builder.unlockedBy("has_" + ingredients[0].asItem().getRegistryName().getPath(), has(ingredients[0]));
        }

        // 保存生成的配方
        builder.save(consumer, new ResourceLocation(MOD_ID, recipeId));
    }


    public static class Recipe {

        /**
         * 创建一个物品转换为另一个物品的配方（默认转换数量为1）。
         *
         * @param context    上下文
         * @param inputItem  要转换的输入物品
         * @param outputItem 转换后生成的输出物品
         * @param recipeId   可选的配方标识符
         */
        public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> context, ItemLike inputItem, ItemLike outputItem, @Nullable String recipeId) {
            RecipeProvider.oneToOneConversionRecipe(context, inputItem, outputItem, recipeId, 1);
        }

        /**
         * 创建一个物品转换为另一个物品的配方。
         *
         * @param context     上下文
         * @param inputItem   要转换的输入物品
         * @param outputItem  转换后生成的输出物品
         * @param recipeId    可选的配方标识符
         * @param outputCount 输出物品的数量
         */
        public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> context, ItemLike inputItem, ItemLike outputItem, @Nullable String recipeId, int outputCount) {
            RecipeProvider.oneToOneConversionRecipe(context, inputItem, outputItem, recipeId, outputCount);
        }

        /**
         * 创建一个矿石熔炼成锭的配方。
         *
         * @param context     上下文
         * @param oreItems    要熔炼的矿石列表（可包含多个变体）
         * @param ingotItem   熔炼后生成的锭
         * @param experience  熔炼时获得的经验值
         * @param cookingTime 熔炼所需的时间（单位视具体实现而定）
         * @param recipeId    配方标识符
         */
        public static void oreSmelting(Consumer<FinishedRecipe> context, List<ItemLike> oreItems, ItemLike ingotItem, float experience, int cookingTime, String recipeId) {
            RecipeProvider.oreSmelting(context, oreItems, ingotItem, experience, cookingTime, recipeId);
        }

        /**
         * 创建一个高炉矿石熔炼成锭的配方。
         *
         * @param context     上下文
         * @param oreItems    要处理的矿石列表（可包含多个变体）
         * @param ingotItem   处理后生成的锭
         * @param experience  处理时获得的经验值
         * @param cookingTime 处理所需的时间（单位视具体实现而定）
         * @param recipeId    配方标识符
         */
        public static void oreBlasting(Consumer<FinishedRecipe> context, List<ItemLike> oreItems, ItemLike ingotItem, float experience, int cookingTime, String recipeId) {
            RecipeProvider.oreBlasting(context, oreItems, ingotItem, experience, cookingTime, recipeId);
        }

        /**
         * 创建一个矿石加工（熔炼/烹饪）配方。
         *
         * @param context           上下文
         * @param cookingSerializer 采用的烹饪序列化器（例如：熔炉、烟熏炉或高炉）
         * @param oreItems          要加工的矿石列表（可包含多个变体）
         * @param resultItem        加工后生成的物品
         * @param experience        加工时获得的经验值
         * @param cookingTime       加工所需的时间（单位视具体实现而定）
         * @param recipeId          配方标识符
         * @param group             配方所属的组
         */
        public static void oreCooking(Consumer<FinishedRecipe> context, SimpleCookingSerializer<?> cookingSerializer, List<ItemLike> oreItems, ItemLike resultItem, float experience, int cookingTime, String recipeId, String group) {
            RecipeProvider.oreCooking(context, cookingSerializer, oreItems, resultItem, experience, cookingTime, recipeId, group);
        }

        /**
         * 创建一个升级为下界合金的锻造配方。
         *
         * @param context     上下文
         * @param baseItem    需要升级的基础物品
         * @param upgradeItem 用于升级的下界合金物品
         */
        public static void netheriteSmithing(Consumer<FinishedRecipe> context, Item baseItem, Item upgradeItem) {
            RecipeProvider.netheriteSmithing(context, baseItem, upgradeItem);
        }

        /**
         * 创建一个将原木转换为木板的配方。
         *
         * @param context    上下文
         * @param planksItem 转换后生成的木板物品
         * @param logTag     标识可用于转换的原木标签
         */
        public static void planksFromLog(Consumer<FinishedRecipe> context, ItemLike planksItem, TagKey<Item> logTag) {
            RecipeProvider.planksFromLog(context, planksItem, logTag);
        }

        /**
         * 创建一个将原木集合转换为木板的配方。
         *
         * @param context    上下文
         * @param planksItem 转换后生成的木板物品
         * @param logsTag    标识可用于转换的原木集合标签
         */
        public static void planksFromLogs(Consumer<FinishedRecipe> context, ItemLike planksItem, TagKey<Item> logsTag) {
            RecipeProvider.planksFromLogs(context, planksItem, logsTag);
        }

        /**
         * 创建一个从原木制作木制船的配方。
         *
         * @param context  上下文
         * @param boatItem 制作出的木制船物品
         * @param logItem  用于制作木制船的原木物品
         */
        public static void woodFromLogs(Consumer<FinishedRecipe> context, ItemLike boatItem, ItemLike logItem) {
            RecipeProvider.woodenBoat(context, boatItem, logItem);
        }

        /**
         * 创建一个木制船的配方。
         *
         * @param context  上下文
         * @param boatItem 制作出的木制船物品
         * @param logItem  用于制作木制船的原木物品
         */
        public static void woodenBoat(Consumer<FinishedRecipe> context, ItemLike boatItem, ItemLike logItem) {
            RecipeProvider.woodenBoat(context, boatItem, logItem);
        }

        /**
         * 创建一个压力板的配方。
         *
         * @param context       上下文
         * @param pressurePlate 生成的压力板物品
         * @param baseMaterial  制作压力板所使用的原材料
         */
        public static void pressurePlate(Consumer<FinishedRecipe> context, ItemLike pressurePlate, ItemLike baseMaterial) {
            RecipeProvider.pressurePlate(context, pressurePlate, baseMaterial);
        }

        /**
         * 创建一个台阶（半砖）的配方。
         *
         * @param context   上下文
         * @param slabItem  生成的台阶物品
         * @param baseBlock 用于制作台阶的原始方块
         */
        public static void slab(Consumer<FinishedRecipe> context, ItemLike slabItem, ItemLike baseBlock) {
            RecipeProvider.slab(context, slabItem, baseBlock);
        }

        /**
         * 创建一个将白色羊毛与染料合成有色羊毛的配方。
         *
         * @param context     上下文
         * @param coloredWool 生成的有色羊毛物品
         * @param dye         用于染色的染料
         */
        public static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> context, ItemLike coloredWool, ItemLike dye) {
            RecipeProvider.coloredWoolFromWhiteWoolAndDye(context, coloredWool, dye);
        }

        /**
         * 创建一个地毯的配方。
         *
         * @param context      上下文
         * @param carpetItem   生成的地毯物品
         * @param materialItem 制作地毯所使用的原材料（通常为羊毛）
         */
        public static void carpet(Consumer<FinishedRecipe> context, ItemLike carpetItem, ItemLike materialItem) {
            RecipeProvider.carpet(context, carpetItem, materialItem);
        }

        /**
         * 创建一个将白色地毯与染料合成有色地毯的配方。
         *
         * @param context       上下文
         * @param coloredCarpet 生成的有色地毯物品
         * @param dye           用于染色的染料
         */
        public static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> context, ItemLike coloredCarpet, ItemLike dye) {
            RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(context, coloredCarpet, dye);
        }

        /**
         * 创建一个由木板和羊毛合成床的配方。
         *
         * @param context      上下文
         * @param bedItem      生成的床物品
         * @param materialItem 制作床所使用的羊毛
         */
        public static void bedFromPlanksAndWool(Consumer<FinishedRecipe> context, ItemLike bedItem, ItemLike materialItem) {
            RecipeProvider.bedFromPlanksAndWool(context, bedItem, materialItem);
        }

        /**
         * 创建一个将白色床与染料合成有色床的配方。
         *
         * @param context    上下文
         * @param coloredBed 生成的有色床物品
         * @param dye        用于染色的染料
         */
        public static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> context, ItemLike coloredBed, ItemLike dye) {
            RecipeProvider.bedFromWhiteBedAndDye(context, coloredBed, dye);
        }

        /**
         * 创建一个横幅的配方。
         *
         * @param context     上下文
         * @param bannerItem  生成的横幅物品
         * @param patternItem 用于制作横幅的装饰图案物品
         */
        public static void banner(Consumer<FinishedRecipe> context, ItemLike bannerItem, ItemLike patternItem) {
            RecipeProvider.banner(context, bannerItem, patternItem);
        }

        /**
         * 创建一个将玻璃与染料合成有色玻璃的配方。
         *
         * @param context      上下文
         * @param stainedGlass 生成的有色玻璃物品
         * @param dye          用于染色的染料
         */
        public static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> context, ItemLike stainedGlass, ItemLike dye) {
            RecipeProvider.stainedGlassFromGlassAndDye(context, stainedGlass, dye);
        }

        /**
         * 创建一个由有色玻璃制作有色玻璃窗格的配方。
         *
         * @param context          上下文
         * @param stainedGlassPane 生成的有色玻璃窗格物品
         * @param stainedGlass     用于制作窗格的有色玻璃
         */
        public static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> context, ItemLike stainedGlassPane, ItemLike stainedGlass) {
            RecipeProvider.stainedGlassPaneFromStainedGlass(context, stainedGlassPane, stainedGlass);
        }

        /**
         * 创建一个将玻璃窗格与染料合成有色玻璃窗格的配方。
         *
         * @param context          上下文
         * @param stainedGlassPane 生成的有色玻璃窗格物品
         * @param dye              用于染色的染料
         */
        public static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> context, ItemLike stainedGlassPane, ItemLike dye) {
            RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(context, stainedGlassPane, dye);
        }

        /**
         * 创建一个将陶瓦与染料合成有色陶瓦的配方。
         *
         * @param context           上下文
         * @param coloredTerracotta 生成的有色陶瓦物品
         * @param dye               用于染色的染料
         */
        public static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> context, ItemLike coloredTerracotta, ItemLike dye) {
            RecipeProvider.coloredTerracottaFromTerracottaAndDye(context, coloredTerracotta, dye);
        }

        /**
         * 创建一个由成分合成混凝土粉末的配方。
         *
         * @param context        上下文
         * @param concretePowder 生成的混凝土粉末物品
         * @param dye            用于着色的染料
         */
        public static void concretePowder(Consumer<FinishedRecipe> context, ItemLike concretePowder, ItemLike dye) {
            RecipeProvider.concretePowder(context, concretePowder, dye);
        }

        /**
         * 创建一个石切机配方，将基方块转换为目标物品。
         *
         * @param context    上下文
         * @param resultItem 生成的目标物品
         * @param baseBlock  要转换的基方块
         */
        public static void stonecutterResultFromBase(Consumer<FinishedRecipe> context, ItemLike resultItem, ItemLike baseBlock) {
            RecipeProvider.stonecutterResultFromBase(context, resultItem, baseBlock);
        }

        /**
         * 创建一个石切机配方，将基方块转换为多个目标物品。
         *
         * @param context    上下文
         * @param resultItem 生成的目标物品
         * @param baseBlock  要转换的基方块
         * @param count      生成目标物品的数量
         */
        public static void stonecutterResultFromBase(Consumer<FinishedRecipe> context, ItemLike resultItem, ItemLike baseBlock, int count) {
            RecipeProvider.stonecutterResultFromBase(context, resultItem, baseBlock, count);
        }

        /**
         * 创建一个由基方块生成熔炼结果的配方。
         *
         * @param context    上下文
         * @param inputItem  需要熔炼的物品
         * @param resultItem 熔炼后生成的物品
         */
        public static void smeltingResultFromBase(Consumer<FinishedRecipe> context, ItemLike inputItem, ItemLike resultItem) {
            RecipeProvider.smeltingResultFromBase(context, inputItem, resultItem);
        }

        /**
         * 创建一个可互逆的配方，将九个小物品转换为一个储存方块。
         *
         * @param context      上下文
         * @param smallItem    要合成的九个小物品
         * @param storageBlock 生成的储存方块
         */
        public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> context, ItemLike smallItem, ItemLike storageBlock) {
            RecipeProvider.nineBlockStorageRecipes(context, smallItem, storageBlock);
        }

        /**
         * 创建一个带自定义打包与拆解标识符的配方，将九个小物品转换为一个储存方块。
         *
         * @param context           上下文
         * @param smallItem         要合成的九个小物品
         * @param storageBlock      生成的储存方块
         * @param packingRecipeId   打包（合成）配方的标识符
         * @param unpackingRecipeId 拆解配方的标识符
         */
        public static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> context, ItemLike smallItem, ItemLike storageBlock, String packingRecipeId, String unpackingRecipeId) {
            RecipeProvider.nineBlockStorageRecipesWithCustomPacking(context, smallItem, storageBlock, packingRecipeId, unpackingRecipeId);
        }

        /**
         * 创建一个带自定义拆解标识符的配方，将九个小物品转换为一个储存方块。
         *
         * @param context           上下文
         * @param smallItem         要合成的九个小物品
         * @param storageBlock      生成的储存方块
         * @param packingRecipeId   打包（合成）配方的标识符
         * @param unpackingRecipeId 拆解配方的标识符
         */
        public static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> context, ItemLike smallItem, ItemLike storageBlock, String packingRecipeId, String unpackingRecipeId) {
            RecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(context, smallItem, storageBlock, packingRecipeId, unpackingRecipeId);
        }

        /**
         * 创建一个带有自定义配方标识符和可选分组信息的配方，将九个小物品转换为一个储存方块，
         * 同时生成互逆（打包和拆解）配方。
         *
         * @param context              上下文
         * @param smallItem            要合成的九个小物品
         * @param storageBlock         生成的储存方块
         * @param packingRecipeId      打包（合成）配方的标识符
         * @param packingRecipeGroup   可选的打包配方组标识符
         * @param unpackingRecipeId    拆解配方的标识符
         * @param unpackingRecipeGroup 可选的拆解配方组标识符
         */
        public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> context, ItemLike smallItem, ItemLike storageBlock, String packingRecipeId, @Nullable String packingRecipeGroup, String unpackingRecipeId, @Nullable String unpackingRecipeGroup) {
            RecipeProvider.nineBlockStorageRecipes(context, smallItem, storageBlock, packingRecipeId, packingRecipeGroup, unpackingRecipeId, unpackingRecipeGroup);
        }

        /**
         * 创建一组使用指定烹饪序列化器的烹饪配方。
         *
         * @param context           上下文
         * @param recipeId          基础配方标识符
         * @param cookingSerializer 采用的烹饪序列化器
         * @param cookingTime       烹饪所需的时间
         */
        public static void cookRecipes(Consumer<FinishedRecipe> context, String recipeId, SimpleCookingSerializer<?> cookingSerializer, int cookingTime) {
            RecipeProvider.cookRecipes(context, recipeId, cookingSerializer, cookingTime);
        }

        /**
         * 创建一个简单的烹饪配方。
         *
         * @param context           上下文
         * @param recipeId          配方标识符
         * @param cookingSerializer 采用的烹饪序列化器
         * @param cookingTime       烹饪所需的时间
         * @param inputItem         要烹饪的输入物品
         * @param resultItem        烹饪后生成的物品
         * @param experience        烹饪时获得的经验值
         */
        public static void simpleCookingRecipe(Consumer<FinishedRecipe> context, String recipeId, SimpleCookingSerializer<?> cookingSerializer, int cookingTime, ItemLike inputItem, ItemLike resultItem, float experience) {
            RecipeProvider.simpleCookingRecipe(context, recipeId, cookingSerializer, cookingTime, inputItem, resultItem, experience);
        }

        /**
         * 创建与上蜡相关的所有配方。
         *
         * @param context 上下文
         */
        public static void waxRecipes(Consumer<FinishedRecipe> context) {
            RecipeProvider.waxRecipes(context);
        }
    }

}