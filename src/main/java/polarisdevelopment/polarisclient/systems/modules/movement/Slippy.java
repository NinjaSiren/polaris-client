/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.modules.movement;

import polarisdevelopment.polarisclient.settings.BlockListSetting;
import polarisdevelopment.polarisclient.settings.DoubleSetting;
import polarisdevelopment.polarisclient.settings.Setting;
import polarisdevelopment.polarisclient.settings.SettingGroup;
import polarisdevelopment.polarisclient.systems.modules.Categories;
import polarisdevelopment.polarisclient.systems.modules.Module;
import net.minecraft.block.Block;

import java.util.List;

public class Slippy extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> friction = sgGeneral.add(new DoubleSetting.Builder()
        .name("friction")
        .description("The base friction level.")
        .range(0.01, 1.10)
        .sliderRange(0.01, 1.10)
        .defaultValue(1)
        .build()
    );

    public final Setting<List<Block>> ignoredBlocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("ignored-blocks")
        .description("Decide which blocks not to slip on")
        .build()
    );

    public Slippy() {
        super(Categories.Movement, "slippy", "Changes the base friction level of blocks.");
    }
}
