/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerOnline extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private Map<UUID, String> playerCache = new HashMap<>();
    private final List<MutableText> messageCache = new ArrayList<>();
    private List<String> detectList = new ArrayList<>();
    MutableText noNameErr = Text.literal("You did not placed any names whatsoever").formatted(Formatting.RED).formatted(Formatting.ITALIC);
    MutableText playerFound;
    private int timer = 0;
    private final Timer mTime = new Timer();

    private final Setting<List<String>> pNames = sgGeneral.add(new StringListSetting.Builder()
        .name("name")
        .description("Insert who ever you want to be detected online.")
        .defaultValue("INSERT PLAYER NAMES (commas , as spaces)")
        .build()
    );

    public final Setting<Boolean> fNames = sgGeneral.add(new BoolSetting.Builder()
        .name("friends-include")
        .description("Also includes Meteor/Polaris friend lists.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("interval")
        .description("Set player online check interval.")
        .defaultValue(100)
        .min(0)
        .sliderMax(300)
        .build()
    );

    public final Setting<Boolean> repeat = sgGeneral.add(new BoolSetting.Builder()
        .name("repeat-check")
        .description("Enable repeat notifications.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> rInterval = sgGeneral.add(new IntSetting.Builder()
        .name("repeat-check-interval")
        .description("Set time between notifications in minutes.")
        .defaultValue(30)
        .min(1)
        .sliderMax(360)
        .build()
    );

    public PlayerOnline() { super(Categories.Misc, "detect-player-online", "Detects if a list of players are online, including friends"); }

    @Override
    public void onActivate() {
        detectList = pNames.get();
        if (pNames.get().isEmpty() || pNames.get().toString().equals("INSERT PLAYER NAMES (commas , as spaces)")) ChatUtils.sendMsg(noNameErr);
        if (fNames.get()) {
            for (int size = 0; size < Friends.get().count(); size++) {
                detectList.add(Friends.get().iterator().next().name);
            }
        }
    }

    @Override
    public void onDeactivate() {
        timer = 0;
        playerCache.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        timer++;
        if (timer < interval.get()) return;
        playerCache = mc.getNetworkHandler().getPlayerList().stream().collect(Collectors.toMap(e -> e.getProfile().getId(), e -> e.getProfile().getName()));
        for (int size = 0; detectList.size() > size; size++) {
            String name = detectList.get(size);
            if (playerCache.containsValue(name)) {
                if(Objects.equals(Friends.get().iterator().next(), name)) {
                    playerFound = Text.literal("Friend " + name + " is ONLINE").formatted(Formatting.GREEN).formatted(Formatting.ITALIC);
                    messageCache.add(playerFound);
                } else {
                    playerFound = Text.literal("Player " + name + " is ONLINE").formatted(Formatting.BLUE).formatted(Formatting.ITALIC);
                    messageCache.add(playerFound);
                }
                for (int msgCnt = 0; messageCache.size() > msgCnt; msgCnt++) ChatUtils.sendMsg(messageCache.get(msgCnt));
            }
        }

        if (repeat.get()) {
            mTime.schedule(new TimerTask() {
                @Override
                public void run() { toggle(); }
            },0, 60000L * rInterval.get());
        } else {
            mTime.cancel();
            toggle();
        }
        messageCache.clear();
        timer = 0;
    }
}
