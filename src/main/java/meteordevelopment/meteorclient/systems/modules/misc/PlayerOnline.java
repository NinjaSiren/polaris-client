/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerOnline extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private List<String> detectList = new ArrayList<>();
    private Map<UUID, String> playerCache = new HashMap<>();
    MutableText noNameErr = Text.literal("You did not placed any names whatsoever");
    MutableText playerFound;
    private int timer = 0;
    private final Setting<List<String>> pNames = sgGeneral.add(new StringListSetting.Builder()
        .name("name")
        .description("Insert who ever you want to be detected online.")
        .defaultValue("INSERT PLAYER NAMES (commas , as spaces)")
        .build()
    );

    public final Setting<Boolean> reset = sgGeneral.add(new BoolSetting.Builder()
        .name("reset-list")
        .description("Resets the name list.")
        .defaultValue(false)
        .build()
    );

    public PlayerOnline() {
        super(Categories.Misc, "detect-player-online", "Detects if a or a list of players are online");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        timer++;
        if (timer < 100) return;
        detectList = pNames.get();
        playerCache = mc.getNetworkHandler().getPlayerList().stream().collect(Collectors.toMap(e -> e.getProfile().getId(), e -> e.getProfile().getName()));

        if (pNames.get().isEmpty() || pNames.get().toString().equals("INSERT PLAYER NAMES (commas , as spaces)")) { ChatUtils.sendMsg(noNameErr); }
        else {
            for (int size = detectList.size() - 1; size > 0; size--) {
                if (playerCache.containsValue(detectList.get(size))) {
                    playerFound = Text.literal("Player " + detectList.get(size) + " is ONLINE");
                    ChatUtils.sendMsg(playerFound);
                }
            }
        }
        timer = 0;
        playerCache.clear();
    }
}
