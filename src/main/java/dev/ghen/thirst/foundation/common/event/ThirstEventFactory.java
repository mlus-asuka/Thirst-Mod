package dev.ghen.thirst.foundation.common.event;

import net.neoforged.neoforge.common.NeoForge;

public class ThirstEventFactory {

    public static void onRegisterThirstValue() {
        NeoForge.EVENT_BUS.post(new RegisterThirstValueEvent());
    }
}
