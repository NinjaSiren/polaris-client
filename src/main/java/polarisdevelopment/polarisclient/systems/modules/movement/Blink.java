/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.modules.movement;

import polarisdevelopment.polarisclient.events.packets.PacketEvent;
import polarisdevelopment.polarisclient.events.world.TickEvent;
import polarisdevelopment.polarisclient.settings.BoolSetting;
import polarisdevelopment.polarisclient.settings.KeybindSetting;
import polarisdevelopment.polarisclient.settings.Setting;
import polarisdevelopment.polarisclient.settings.SettingGroup;
import polarisdevelopment.polarisclient.systems.modules.Categories;
import polarisdevelopment.polarisclient.systems.modules.Module;
import polarisdevelopment.polarisclient.utils.Utils;
import polarisdevelopment.polarisclient.utils.entity.fakeplayer.FakePlayerEntity;
import polarisdevelopment.polarisclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> renderOriginal = sgGeneral.add(new BoolSetting.Builder()
        .name("render-original")
        .description("Renders your player model at the original position.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Keybind> cancelBlink = sgGeneral.add(new KeybindSetting.Builder()
        .name("cancel-blink")
        .description("Cancels sending packets and sends you back to your original position.")
        .defaultValue(Keybind.none())
        .action(() -> {
            cancelled = true;
            if (isActive()) toggle();
        })
        .build()
    );

    private final List<PlayerMoveC2SPacket> packets = new ArrayList<>();
    private FakePlayerEntity model;
    private final Vector3d start = new Vector3d();

    private boolean cancelled = false;
    private int timer = 0;

    public Blink() {
        super(Categories.Movement, "blink", "Allows you to essentially teleport while suspending motion updates.");
    }

    @Override
    public void onActivate() {
        if (renderOriginal.get()) {
            model = new FakePlayerEntity(mc.player, mc.player.getGameProfile().getName(), 20, true);
            model.doNotPush = true;
            model.hideWhenInsideCamera = true;
            model.spawn();
        }

        Utils.set(start, mc.player.getPos());
    }

    @Override
    public void onDeactivate() {
        dumpPackets(!cancelled);
        if (cancelled) mc.player.setPos(start.x, start.y, start.z);
        cancelled = false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        timer++;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerMoveC2SPacket p)) return;
        event.cancel();

        PlayerMoveC2SPacket prev = packets.size() == 0 ? null : packets.get(packets.size() - 1);

        if (prev != null &&
                p.isOnGround() == prev.isOnGround() &&
                p.getYaw(-1) == prev.getYaw(-1) &&
                p.getPitch(-1) == prev.getPitch(-1) &&
                p.getX(-1) == prev.getX(-1) &&
                p.getY(-1) == prev.getY(-1) &&
                p.getZ(-1) == prev.getZ(-1)
        ) return;

        synchronized (packets) {
            packets.add(p);
        }
    }

    @Override
    public String getInfoString() {
        return String.format("%.1f", timer / 20f);
    }

    private void dumpPackets(boolean send) {
        synchronized (packets) {
            if (send) packets.forEach(mc.player.networkHandler::sendPacket);
            packets.clear();
        }

        if (model != null) {
            model.despawn();
            model = null;
        }

        timer = 0;
    }
}
