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
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ItemFrame extends Module {

    private final Setting<Integer> ticks;

    private int timer;

    private boolean attacked;

    private Vec3d lastPos;

    private Direction lastDirection;

    public ItemFrame() {
        super(Categories.Dupes, "ToroDupe v2", "ToroDupe v2 by Colonizadores and Carlox");
        SettingGroup sgGeneral = this.settings.getDefaultGroup();

        this.ticks = sgGeneral
            .add((Setting<Integer>)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)
                (new IntSetting.Builder())
                    .name("ticks"))
                    .description("Ticks"))
                    .defaultValue(6))
                    .range(0, 100)
                    .sliderMax(100)
                    .build());

        this.timer = 0;
        this.attacked = false;
        this.lastPos = null;
        this.lastDirection = null;
    }

    public void placeNearestSide(BlockPos blockPos, Direction side) {
        if (side != null) {
            Vec3d blockHit = new Vec3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
            BlockPos neighbor = blockPos.offset(side.getOpposite());
            blockHit.add(side.getOffsetX() * 0.5D, side.getOffsetY() * 0.5D, side.getOffsetZ() * 0.5D);
            this.mc.player.swingHand(Hand.MAIN_HAND);
            this.mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float)Rotations.getYaw(blockHit), (float) Rotations.getPitch(blockHit), this.mc.player.isOnGround()));
            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, new BlockHitResult(blockHit, side, neighbor, false));
        }
    }

    private void place(BlockPos blockPos) {
        Direction nearestSide = null;
        BlockPos nearesBlockPos = null;
        double nearestSideInt = 100.0D;
        for (Direction side : Direction.values()) {
            if (this.mc.player.squaredDistanceTo(new Vec3d((blockPos.getX() + side.getOffsetX()), blockPos.getY(), (blockPos.getZ() + side.getOffsetZ()))) < nearestSideInt) {
                nearestSideInt = this.mc.player.squaredDistanceTo(new Vec3d((blockPos.getX() + side.getOffsetX()), blockPos.getY(), (blockPos.getZ() + side.getOffsetZ())));
                nearestSide = side;
                nearesBlockPos = blockPos;
            }
        }
        placeNearestSide(nearesBlockPos.offset(nearestSide), nearestSide);
    }

    public Entity getEntity() {
        for (Entity entity : this.mc.world.getEntities()) {
            if (this.mc.player.distanceTo(entity) < 4.0F && entity instanceof ItemFrameEntity)
                return entity;
        }
        return null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        this.timer++;
        Entity frame = getEntity();
        if (frame == null && this.lastPos != null && this.timer > 10) {
            FindItemResult itemframeitem = InvUtils.findInHotbar(new Item[] { Items.ITEM_FRAME, Items.GLOW_ITEM_FRAME });
            if (itemframeitem.found()) {
                (this.mc.player.getInventory()).selectedSlot = itemframeitem.slot();
                place(new BlockPos(this.lastPos.add(this.lastDirection.getOffsetX(), this.lastDirection.getOffsetY(), this.lastDirection.getOffsetZ())));
                this.timer = 0;
                this.attacked = true;
            }
        }
        if (frame == null)
            return;
        this.lastPos = frame.getPos();
        this.lastDirection = frame.getHorizontalFacing().getOpposite();
            if ((!((ItemFrameEntity)frame).getHeldItemStack().isEmpty() && !this.attacked) || (!((ItemFrameEntity)frame).getHeldItemStack().isEmpty() && this.timer > ((Integer)this.ticks.get()).intValue())) {
            this.mc.interactionManager.attackEntity((PlayerEntity)this.mc.player, frame);
            this.attacked = true;
            this.timer = 0;
        }
        if (((ItemFrameEntity)frame).getHeldItemStack().isEmpty()) {
            (this.mc.player.getInventory()).selectedSlot = 0;
                this.mc.interactionManager.clickSlot(this.mc.player.currentScreenHandler.syncId, 36, 0, SlotActionType.PICKUP, (PlayerEntity)this.mc.player);
            this.mc.player.getInventory().updateItems();
            this.mc.interactionManager.interactEntity((PlayerEntity)this.mc.player, frame, Hand.MAIN_HAND);
            this.attacked = false;
        }
    }
}

