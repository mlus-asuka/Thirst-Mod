package dev.ghen.thirst.compat.supernatural;

import net.salju.supernatural.init.SupernaturalItems;
import net.salju.supernatural.init.SupernaturalTags;
import net.salju.supernatural.events.SupernaturalManager;
import dev.ghen.thirst.Thirst;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class SupernaturalHelper {
    public static ResourceLocation VAMPIRE_THIRST_ICONS = ResourceLocation.fromNamespaceAndPath("supernatural", "textures/gui/thirst_icons.png");
    private static final ResourceLocation appleskinIcons = ResourceLocation.fromNamespaceAndPath("supernatural", "textures/gui/appleskin_icons.png");

    public static boolean canDrinkItem(ItemStack stack, Player player) {
        if (isVampireCheck(player)) {
            return isBloodCheck(stack);
        } else {
            return true;
        }
    }

    //Checks for Persistent NBT Player Data for Vampirism. Serverside only.
    public static boolean isVampireCheck(Player player) {
        return SupernaturalManager.isVampire(player);
    }

    //Checks if the Player has the Vampirism MobEffect. Works on Clientside & Serverside.
    public static boolean hasVampirismCheck(Player player) {
        return SupernaturalManager.hasVampirism(player);
    }

    //Checks if the item is tagged as Blood. By default, it is just the Blood o' Blood item.
    public static boolean isBloodCheck(ItemStack stack) {
        return stack.is(SupernaturalTags.BLOOD);
    }

    //Grabs the Bottle o' Blood item.
    public static Item getBloodBottle() {
        return SupernaturalItems.BLOOD.get();
    }

    //Grabs new Vampire Thirst Icons.
    public static ResourceLocation getVampireIcons(ResourceLocation original, Player player) {
        if (hasVampirismCheck(player) {
            return VAMPIRE_THIRST_ICONS;
        }
        return original;
    }

    //Grabs new Vampire Thirst Appleskin Icons.
    public static ResourceLocation getVampireAppleskinIcons(ResourceLocation original, Player player) {
        if (hasVampirismCheck(player) {
            return appleskinIcons;
        }
        return original;
    }
}
