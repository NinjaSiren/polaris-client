/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.dupes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ItemFrame extends Module {

    private int timer = 0;
    private boolean attacked = false;
    private Vec3d lastPos = null;
    private Direction lastDirection = null;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> ticks = sgGeneral
        .add((Setting<Integer>)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
            .name("ticks"))
            .description("Ticks"))
            .defaultValue(12))
            .range(0, 100)
            .sliderMax(100)
            .build()
        );

    public ItemFrame() { super(Categories.Dupes, "6b6t Item Frame", "Item Frame dupe by Colonizadores & Carlox, reversed by NinjaSiren"); }

    public void placeNearestSide(BlockPos blockPos, Direction side) {
        if (side != null) {
            Vec3d blockHit = new Vec3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
            BlockPos neighbor = blockPos.offset(side.getOpposite());
            blockHit.add(side.getOffsetX() * 0.5D, side.getOffsetY() * 0.5D, side.getOffsetZ() * 0.5D);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float)Rotations.getYaw(blockHit), (float) Rotations.getPitch(blockHit), mc.player.isOnGround()));
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(blockHit, side, neighbor, false));
        }
    }

    private void place(BlockPos blockPos) {
        Direction nearestSide = null;
        BlockPos nearesBlockPos = null;
        double nearestSideInt = 100.0D;
        for (Direction side : Direction.values()) {
            if (mc.player.squaredDistanceTo(new Vec3d((blockPos.getX() + side.getOffsetX()), blockPos.getY(), (blockPos.getZ() + side.getOffsetZ()))) < nearestSideInt) {
                nearestSideInt = mc.player.squaredDistanceTo(new Vec3d((blockPos.getX() + side.getOffsetX()), blockPos.getY(), (blockPos.getZ() + side.getOffsetZ())));
                nearestSide = side;
                nearesBlockPos = blockPos;
            }
        }
        placeNearestSide(nearesBlockPos.offset(nearestSide), nearestSide);
    }

    public Entity getEntity() {
        for (Entity entity : mc.world.getEntities()) {
            if (mc.player.distanceTo(entity) < 4.0F && entity instanceof ItemFrameEntity)
                return entity;
        }
        return null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        timer++;
        Entity frame = getEntity();
        if (frame == null && lastPos != null && timer > 10) {
            FindItemResult contemporaneity = InvUtils.findInHotbar(Items.ITEM_FRAME, Items.GLOW_ITEM_FRAME);
            if (contemporaneity.found()) {
                (mc.player.getInventory()).selectedSlot = contemporaneity.slot();
                int locX = lastDirection.getOffsetX(),
                    locY = lastDirection.getOffsetY(),
                    locZ = lastDirection.getOffsetZ();
                place(new BlockPos(locX, locY, locZ));
                timer = 0;
                attacked = true;
            }
        }

        if (frame == null) return;
        lastPos = frame.getPos();
        lastDirection = frame.getHorizontalFacing().getOpposite();
        if ((!((ItemFrameEntity)frame).getHeldItemStack().isEmpty() && !attacked) || (!((ItemFrameEntity)frame).getHeldItemStack().isEmpty() && timer > ((Integer)ticks.get()).intValue())) {
            mc.interactionManager.attackEntity((PlayerEntity)mc.player, frame);
            attacked = true;
            timer = 0;
        }

        if (((ItemFrameEntity)frame).getHeldItemStack().isEmpty()) {
            (mc.player.getInventory()).selectedSlot = 0;
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
            mc.player.getInventory().updateItems();
            mc.interactionManager.interactEntity((PlayerEntity)mc.player, frame, Hand.MAIN_HAND);
            attacked = false;
        }
    }
}

