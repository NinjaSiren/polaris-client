/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.modules.render.blockesp;

import polarisdevelopment.polarisclient.gui.GuiTheme;
import polarisdevelopment.polarisclient.gui.WidgetScreen;
import polarisdevelopment.polarisclient.gui.utils.IScreenFactory;
import polarisdevelopment.polarisclient.renderer.ShapeMode;
import polarisdevelopment.polarisclient.settings.BlockDataSetting;
import polarisdevelopment.polarisclient.settings.IBlockData;
import polarisdevelopment.polarisclient.utils.misc.IChangeable;
import polarisdevelopment.polarisclient.utils.misc.ICopyable;
import polarisdevelopment.polarisclient.utils.misc.ISerializable;
import polarisdevelopment.polarisclient.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;

public class ESPBlockData implements ICopyable<ESPBlockData>, ISerializable<ESPBlockData>, IChangeable, IBlockData<ESPBlockData>, IScreenFactory {
    public ShapeMode shapeMode;
    public SettingColor lineColor;
    public SettingColor sideColor;

    public boolean tracer;
    public SettingColor tracerColor;

    private boolean changed;

    public ESPBlockData(ShapeMode shapeMode, SettingColor lineColor, SettingColor sideColor, boolean tracer, SettingColor tracerColor) {
        this.shapeMode = shapeMode;
        this.lineColor = lineColor;
        this.sideColor = sideColor;

        this.tracer = tracer;
        this.tracerColor = tracerColor;
    }

    @Override
    public WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<ESPBlockData> setting) {
        return new ESPBlockDataScreen(theme, this, block, setting);
    }

    @Override
    public WidgetScreen createScreen(GuiTheme theme) {
        return new ESPBlockDataScreen(theme, this, null, null);
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    public void changed() {
        changed = true;
    }

    public void tickRainbow() {
        lineColor.update();
        sideColor.update();
        tracerColor.update();
    }

    @Override
    public ESPBlockData set(ESPBlockData value) {
        shapeMode = value.shapeMode;
        lineColor.set(value.lineColor);
        sideColor.set(value.sideColor);

        tracer = value.tracer;
        tracerColor.set(value.tracerColor);

        changed = value.changed;

        return this;
    }

    @Override
    public ESPBlockData copy() {
        return new ESPBlockData(shapeMode, new SettingColor(lineColor), new SettingColor(sideColor), tracer, new SettingColor(tracerColor));
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("shapeMode", shapeMode.name());
        tag.put("lineColor", lineColor.toTag());
        tag.put("sideColor", sideColor.toTag());

        tag.putBoolean("tracer", tracer);
        tag.put("tracerColor", tracerColor.toTag());

        tag.putBoolean("changed", changed);

        return tag;
    }

    @Override
    public ESPBlockData fromTag(NbtCompound tag) {
        shapeMode = ShapeMode.valueOf(tag.getString("shapeMode"));
        lineColor.fromTag(tag.getCompound("lineColor"));
        sideColor.fromTag(tag.getCompound("sideColor"));

        tracer = tag.getBoolean("tracer");
        tracerColor.fromTag(tag.getCompound("tracerColor"));

        changed = tag.getBoolean("changed");

        return this;
    }
}
