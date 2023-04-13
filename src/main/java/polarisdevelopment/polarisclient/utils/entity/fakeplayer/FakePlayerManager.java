/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.utils.entity.fakeplayer;

import polarisdevelopment.polarisclient.utils.Utils;
import polarisdevelopment.polarisclient.MeteorClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FakePlayerManager {
    private static final List<FakePlayerEntity> ENTITIES = new ArrayList<>();

    public static List<FakePlayerEntity> getFakePlayers() {
        return ENTITIES;
    }

    public static FakePlayerEntity get(String name) {
        for (FakePlayerEntity fp : ENTITIES) {
            if (fp.getEntityName().equals(name)) return fp;
        }

        return null;
    }

    public static void add(String name, float health, boolean copyInv) {
        if (!Utils.canUpdate()) return;

        FakePlayerEntity fakePlayer = new FakePlayerEntity(MeteorClient.mc.player, name, health, copyInv);
        fakePlayer.spawn();
        ENTITIES.add(fakePlayer);
    }

    public static void remove(FakePlayerEntity fp) {
        ENTITIES.removeIf(fp1 -> {
            if (fp1.getEntityName().equals(fp.getEntityName())) {
                fp1.despawn();
                return true;
            }

            return false;
        });
    }

    public static void clear() {
        if (ENTITIES.isEmpty()) return;
        ENTITIES.forEach(FakePlayerEntity::despawn);
        ENTITIES.clear();
    }

    public static void forEach(Consumer<FakePlayerEntity> action) {
        for (FakePlayerEntity fakePlayer : ENTITIES) {
            action.accept(fakePlayer);
        }
    }

    public static int count() {
        return ENTITIES.size();
    }

    public static Stream<FakePlayerEntity> stream() {
        return ENTITIES.stream();
    }

    public static boolean contains(FakePlayerEntity fp) {
        return ENTITIES.contains(fp);
    }
}
