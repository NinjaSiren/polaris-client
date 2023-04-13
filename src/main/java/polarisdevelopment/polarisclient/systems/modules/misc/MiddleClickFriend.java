/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.modules.misc;

import polarisdevelopment.polarisclient.events.meteor.MouseButtonEvent;
import polarisdevelopment.polarisclient.settings.BoolSetting;
import polarisdevelopment.polarisclient.settings.Setting;
import polarisdevelopment.polarisclient.settings.SettingGroup;
import polarisdevelopment.polarisclient.systems.friends.Friend;
import polarisdevelopment.polarisclient.systems.friends.Friends;
import polarisdevelopment.polarisclient.systems.modules.Categories;
import polarisdevelopment.polarisclient.systems.modules.Module;
import polarisdevelopment.polarisclient.utils.misc.input.KeyAction;
import polarisdevelopment.polarisclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriend extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> message = sgGeneral.add(new BoolSetting.Builder()
        .name("message")
        .description("Sends a message to the player when you add them as a friend.")
        .defaultValue(false)
        .build()
    );

    public MiddleClickFriend() {
        super(Categories.Misc, "middle-click-friend", "Adds or removes a player as a friend via middle click.");
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity player)) return;

        if (!Friends.get().isFriend(player)) {
            Friends.get().add(new Friend(player));
            info("Added %s to friends", player.getEntityName());
            if (message.get()) ChatUtils.sendPlayerMsg("/msg " + player.getEntityName() + " I just friended you on Meteor.");
        }
        else {
            Friends.get().remove(Friends.get().get(player));
            info("Removed %s from friends", player.getEntityName());
        }
    }
}
