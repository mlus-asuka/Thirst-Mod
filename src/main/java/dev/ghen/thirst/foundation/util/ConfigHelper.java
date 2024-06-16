package dev.ghen.thirst.foundation.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigHelper
{
    /**
     * This class was taken from <a href="https://github.com/Momo-Studios/Cold-Sweat/blob/1.18.x-FG/src/main/java/dev/momostudios/coldsweat/util/config/ConfigHelper.java">Cold Sweat</a>
     */

    public static Map<Item, Number[]> getItemsWithValues(List<? extends List<?>> source)
    {
        Map<Item, Number[]> map = new HashMap<>();
        for (List<?> entry : source)
        {
            String itemID = (String) entry.get(0);

            if (itemID.startsWith("#"))
            {
                final String tagID = itemID.replace("#", "");
                Optional<Pair<TagKey<Item>, HolderSet.Named<Item>>> optionalTag = BuiltInRegistries.ITEM.getTags().filter(tag ->
                        tag.getFirst().location().toString().equals(tagID)).findFirst();
                optionalTag.ifPresent(itemITag ->
                {
                    for (Holder<Item> item : optionalTag.get().getSecond().stream().toList())
                    {
                        map.put(item.value(), new Number[]{(Number) entry.get(1), (Number) entry.get(2)});
                    }
                });
            }
            else
            {
                Item newItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemID));


                if (newItem != null) map.put(newItem, new Number[]{(Number) entry.get(1), (Number) entry.get(2)});
            }
        }
        return map;
    }
}
