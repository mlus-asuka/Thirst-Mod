package dev.ghen.thirst.compat.create.ponder;


import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.compat.create.CreateRegistry;
import dev.ghen.thirst.compat.create.ponder.scene.SandFilterScene;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;


public class ThirstPonders {
    public static final ResourceLocation PURIFICATION = Thirst.asResource("purification");

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<Block> HELPER = helper.withKeyFunction(BuiltInRegistries.BLOCK::getKey);

        HELPER.registerTag(PURIFICATION)
                .addToIndex()
                .item(CreateRegistry.SAND_FILTER_BLOCK.get(), true, false)
                .title("Purification")
                .description("Components which purifying water")
                .register();
    }

    public static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<Block> HELPER = helper.withKeyFunction(BuiltInRegistries.BLOCK::getKey);

        HELPER.addStoryBoard(
                CreateRegistry.SAND_FILTER_BLOCK.get(),
                "sand_filter",
                SandFilterScene::filtering,
                AllCreatePonderTags.FLUIDS,
                PURIFICATION
        );

    }


}
