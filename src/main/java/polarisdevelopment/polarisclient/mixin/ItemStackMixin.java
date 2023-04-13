/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.mixin;

import polarisdevelopment.polarisclient.MeteorClient;
import polarisdevelopment.polarisclient.events.entity.player.FinishUsingItemEvent;
import polarisdevelopment.polarisclient.events.entity.player.StoppedUsingItemEvent;
import polarisdevelopment.polarisclient.events.game.ItemStackTooltipEvent;
import polarisdevelopment.polarisclient.events.game.SectionVisibleEvent;
import polarisdevelopment.polarisclient.utils.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("TAIL"), cancellable = true)
    private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info) {
        if (Utils.canUpdate()) {
            ItemStackTooltipEvent event = MeteorClient.EVENT_BUS.post(ItemStackTooltipEvent.get((ItemStack) (Object) this, info.getReturnValue()));
            info.setReturnValue(event.list);
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user == MeteorClient.mc.player) {
            MeteorClient.EVENT_BUS.post(FinishUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
        if (user == MeteorClient.mc.player) {
            MeteorClient.EVENT_BUS.post(StoppedUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(method = "isSectionVisible", at = @At("RETURN"), cancellable = true)
    private static void onSectionVisible(int flags, ItemStack.TooltipSection tooltipSection, CallbackInfoReturnable<Boolean> info) {
        SectionVisibleEvent event = MeteorClient.EVENT_BUS.post(SectionVisibleEvent.get(tooltipSection, info.getReturnValueZ()));
        info.setReturnValue(event.visible);
    }
}
