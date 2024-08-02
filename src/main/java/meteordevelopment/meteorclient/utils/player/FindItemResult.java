/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.player;

import net.minecraft.util.Hand;

import static meteordevelopment.meteorclient.MeteorClient.mc;

import java.util.Objects;

public class FindItemResult {
    private final int slot;
    private final int count;

    public FindItemResult(int slot, int count) {
        this.slot = slot;
        this.count = count;
    }

    public int slot() {
        return slot;
    }

    public int count() {
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        FindItemResult that = (FindItemResult) obj;
        return this.slot == that.slot &&
            this.count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, count);
    }

    @Override
    public String toString() {
        return "FindItemResult[" +
            "slot=" + slot + ", " +
            "count=" + count + ']';
    }

    public boolean found() {
        return slot != -1;
    }

    public Hand getHand() {
        if (slot == SlotUtils.OFFHAND) return Hand.OFF_HAND;
        if (slot == mc.player.getInventory().selectedSlot) return Hand.MAIN_HAND;
        return null;
    }

    public boolean isMainHand() {
        return getHand() == Hand.MAIN_HAND;
    }

    public boolean isOffhand() {
        return getHand() == Hand.OFF_HAND;
    }

    public boolean isHotbar() {
        return slot >= SlotUtils.HOTBAR_START && slot <= SlotUtils.HOTBAR_END;
    }

    public boolean isMain() {
        return slot >= SlotUtils.MAIN_START && slot <= SlotUtils.MAIN_END;
    }

    public boolean isArmor() {
        return slot >= SlotUtils.ARMOR_START && slot <= SlotUtils.ARMOR_END;
    }
}
