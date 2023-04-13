/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.modules.render;

import polarisdevelopment.polarisclient.settings.IntSetting;
import polarisdevelopment.polarisclient.settings.Setting;
import polarisdevelopment.polarisclient.settings.SettingGroup;
import polarisdevelopment.polarisclient.systems.modules.Categories;
import polarisdevelopment.polarisclient.systems.modules.Module;

public class UnfocusedCPU extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public final Setting<Integer> fps = sgGeneral.add(new IntSetting.Builder()
        .name("target-fps")
        .description("Target FPS to set as the limit when the window is not focused.")
        .min(1)
        .defaultValue(1)
        .sliderRange(1, 20)
        .build()
    );

    public UnfocusedCPU() {
        super(Categories.Render, "unfocused-cpu", "Limits FPS when your Minecraft window is not focused.");
    }
}
