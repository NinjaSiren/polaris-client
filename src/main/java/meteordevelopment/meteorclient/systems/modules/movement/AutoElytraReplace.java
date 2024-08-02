/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

/*
 * Module by NinjaSiren
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoElytraReplace extends Module  {

    protected final MinecraftClient mc;


    private final SettingGroup sgInventory = settings.createGroup("Inventory");

    public final Setting<Integer> replaceDurability = sgInventory.add(new IntSetting.Builder()
        .name("replace-durability")
        .description("The durability threshold your elytra will be replaced at.")
        .defaultValue(2)
        .range(1, Items.ELYTRA.getMaxCount() - 1)
        .sliderRange(1, Items.ELYTRA.getMaxCount() - 1)
        .build()
    );

    public AutoElytraReplace() {
        super(Categories.Movement, "auto-elytra-replace", "Automates used elytra replacement (use with Vanilla-only Elytra fly)");
        this.mc = MinecraftClient.getInstance();
    }

    @Override
    public void onActivate() {
    }

    @Override
    public void onDeactivate() {
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        this.onTick();
    }

    public void onTick() {
        if(this.isActive()) {
            ItemStack chestStack = mc.player.getInventory().getArmorStack(2);

            if (chestStack.getItem() == Items.ELYTRA) {
                if (chestStack.getMaxDamage() - chestStack.getDamage() <= this.replaceDurability.get()) {
                    FindItemResult elytra = InvUtils.find(stack -> stack.getMaxDamage() - stack.getDamage() > this.replaceDurability.get() && stack.getItem() == Items.ELYTRA);
                    InvUtils.move().from(elytra.slot()).toArmor(2);
                }
            }
        }
    }
}
