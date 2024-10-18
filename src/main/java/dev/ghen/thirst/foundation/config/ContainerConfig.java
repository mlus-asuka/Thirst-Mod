package dev.ghen.thirst.foundation.config;


import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ContainerConfig {
    private static final ModConfigSpec SPEC;
    public static final  ModConfigSpec.Builder BUILDER = new  ModConfigSpec.Builder();
    public static final  ModConfigSpec.ConfigValue<List<String>> CONTAINERS;

    static {
        BUILDER.push("Container");

        CONTAINERS = BUILDER.comment("Defineds drinks will be influenced by purity"
                        ,"Format: [\"examplemod:example_item_1\", \"examplemod:example_item_2\"]")
                .define("Containers", Arrays.asList(
                        "collectorsreap:pomegranate_black_tea",
                        "collectorsreap:lime_green_tea",
                        "create:builders_tea"
                ));

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void setup(ModContainer modContainer)
    {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path configFolder = Paths.get(configPath.toAbsolutePath().toString(), "thirst");

        try
        {
            Files.createDirectory(configFolder);
        }
        catch (Exception ignored) {}

        modContainer.registerConfig(ModConfig.Type.COMMON, SPEC, "thirst/container.toml");
    }
}
