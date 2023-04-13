/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.modules.render;

import polarisdevelopment.polarisclient.settings.ColorSetting;
import polarisdevelopment.polarisclient.settings.ItemListSetting;
import polarisdevelopment.polarisclient.settings.Setting;
import polarisdevelopment.polarisclient.settings.SettingGroup;
import polarisdevelopment.polarisclient.systems.modules.Categories;
import polarisdevelopment.polarisclient.systems.modules.Module;
import polarisdevelopment.polarisclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemHighlight extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Items to highlight.")
            .build()
    );

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
            .name("color")
            .description("The color to highlight the items with.")
            .defaultValue(new SettingColor(225, 25, 255, 50))
            .build()
    );

    public ItemHighlight() {
        super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
    }

    public int getColor(ItemStack stack) {
        if (stack != null && items.get().contains(stack.getItem()) && isActive()) return color.get().getPacked();
        return -1;
    }
}
