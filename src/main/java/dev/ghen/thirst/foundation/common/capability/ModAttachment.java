package dev.ghen.thirst.foundation.common.capability;


import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.content.thirst.PlayerThirst;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachment
{
//    public static final EntityCapability<IThirst,Void> PLAYER_THIRST = EntityCapability.createVoid(Thirst.asResource("thirst"), IThirst.class);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Thirst.ID);
    public static final Supplier<AttachmentType<PlayerThirst>> PLAYER_THIRST = ATTACHMENT_TYPES.register(
            "player_thirst", () -> AttachmentType.serializable(PlayerThirst::new).copyOnDeath().build()
    );

}
