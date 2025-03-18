package io.github.zimoyin.zhenfa.datagen;

import io.github.zimoyin.zhenfa.datagen.provider.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import static io.github.zimoyin.zhenfa.datagen.Config.DataGenModId;

@Mod.EventBusSubscriber(modid = DataGenModId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new TutRecipes(generator));
            generator.addProvider(new LootTableProvider(generator));
            EBlockTagsProvider blockTags = new EBlockTagsProvider(generator, event.getExistingFileHelper());
            generator.addProvider(blockTags);
            generator.addProvider(new ItemTags(generator, blockTags, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
            for (LanguageProvider provider : new LanguageProviders(generator).getLanguageProviders()) {
                generator.addProvider(provider);
            }
        }
    }
}