/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.mixin;

import polarisdevelopment.polarisclient.systems.modules.Modules;
import polarisdevelopment.polarisclient.systems.modules.misc.AntiPacketKick;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @ModifyArg(method = "readNbt()Lnet/minecraft/nbt/NbtCompound;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readNbt(Lnet/minecraft/nbt/NbtTagSizeTracker;)Lnet/minecraft/nbt/NbtCompound;"))
    private NbtTagSizeTracker xlPackets(NbtTagSizeTracker sizeTracker) {
        return Modules.get().isActive(AntiPacketKick.class) ? NbtTagSizeTracker.EMPTY : sizeTracker;
    }
}
