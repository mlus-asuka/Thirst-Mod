package dev.ghen.thirst.compat.create.ponder;

import dev.ghen.thirst.Thirst;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ThirstPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() {
        return Thirst.ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ThirstPonders.registerScenes(helper);
    }

    @Override
    public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        ThirstPonders.registerTags(helper);
    }
}
