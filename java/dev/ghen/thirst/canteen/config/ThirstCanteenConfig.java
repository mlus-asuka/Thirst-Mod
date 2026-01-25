package dev.ghen.thirst.canteen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ThirstCanteenConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<Number> THIRST_RESTORE_EACH_SIP;
    public static final ForgeConfigSpec.ConfigValue<Number> QUENCHED_RESTORE_EACH_SIP;
    public static final ForgeConfigSpec.ConfigValue<Number> LEATHER_CANTEEN_USABLE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Number> MILITARY_BOTTLE_USABLE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Number> DRAGON_BOTTLE_USABLE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Number> DRAGON_BOTTLE_DEFAULT_PURITY;
    private static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Settings");
        THIRST_RESTORE_EACH_SIP = BUILDER.define("ThirstRestoreEachSip", 6);
        QUENCHED_RESTORE_EACH_SIP = BUILDER.define("QuenchedRestoreEachSip", 8);
        DRAGON_BOTTLE_DEFAULT_PURITY = BUILDER.define("DragonBottleDefaultPurity", 2);
        LEATHER_CANTEEN_USABLE_TIME = BUILDER.define("LeatherCanteenUsableTime", 8);
        MILITARY_BOTTLE_USABLE_TIME = BUILDER.define("MilitaryBottleUsableTime", 12);
        DRAGON_BOTTLE_USABLE_TIME = BUILDER.define("DragonBottleUsableTime", 16);
        SPEC = BUILDER.build();
    }

    public static void setup(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, SPEC, "ThirstCanteen.toml");
    }
}
