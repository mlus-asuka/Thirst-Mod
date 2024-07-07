package dev.ghen.thirst;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.content.registry.ItemInit;
import dev.ghen.thirst.content.registry.ThirstComponent;
import dev.ghen.thirst.content.thirst.PlayerThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.common.loot.ModLootModifiers;
import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.foundation.config.ItemSettingsConfig;
import dev.ghen.thirst.foundation.config.KeyWordConfig;
import dev.ghen.thirst.foundation.gui.ThirstBarRenderer;
import dev.ghen.thirst.foundation.gui.appleskin.HUDOverlayHandler;
import dev.ghen.thirst.foundation.gui.appleskin.OverlayRegister;
import dev.ghen.thirst.foundation.gui.appleskin.TooltipOverlayHandler;
import dev.ghen.thirst.foundation.tab.ThirstTab;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;


@Mod(Thirst.ID)
public class Thirst
{
    public static final String ID = "thirst";

    public Thirst(IEventBus modBus, ModContainer modContainer)
    {

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        ModAttachment.ATTACHMENT_TYPES.register(modBus);
        ThirstComponent.DR.register(modBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modBus);

        if(FMLEnvironment.dist.isClient()){
            if(ModList.get().isLoaded("appleskin"))
            {
                HUDOverlayHandler.init();
                TooltipOverlayHandler.init();
                modBus.addListener(this::onRegisterClientTooltipComponentFactories);
                modBus.addListener(OverlayRegister::onRenderGuiOverlayPost);
            }
        }


        ItemInit.ITEMS.register(modBus);

//        if(ModList.get().isLoaded("create"))
//        {
//            CreateRegistry.register();
//        }

        ThirstTab.register(modBus);

        //configs
        ItemSettingsConfig.setup(modContainer);
        CommonConfig.setup(modContainer);
        ClientConfig.setup(modContainer);
        KeyWordConfig.setup(modContainer);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        WaterPurity.init();

        if(ModList.get().isLoaded("coldsweat"))
            ThirstHelper.shouldUseColdSweatCaps(true);

        if(ModList.get().isLoaded("tombstone"))
            PlayerThirst.checkTombstoneEffects = true;

        if(ModList.get().isLoaded("vampirism"))
            PlayerThirst.checkVampirismEffects = true;

        if(ModList.get().isLoaded("farmersdelight"))
            PlayerThirst.checkFDEffects = true;

        if(ModList.get().isLoaded("bakery"))
            PlayerThirst.checkLetsDoBakeryEffects = true;

        if(ModList.get().isLoaded("brewery"))
            PlayerThirst.checkLetsDoBreweryEffects = true;
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
//        if(ModList.get().isLoaded("create")){
//            event.enqueueWork(ThirstPonders::register);
//        }

        if(ModList.get().isLoaded("vampirism"))
        {
            ThirstBarRenderer.checkIfPlayerIsVampire = true;
        }
    }

    public static ResourceLocation asResource(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    private void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        TooltipOverlayHandler.register(event);
    }
}
