/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.mixin;

import polarisdevelopment.polarisclient.systems.modules.Modules;
import polarisdevelopment.polarisclient.systems.modules.render.NoRender;
import polarisdevelopment.polarisclient.utils.misc.EmptyIterator;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapRenderer.MapTexture.class)
public class MapRendererMixin {
    @Redirect(method = "draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;getIcons()Ljava/lang/Iterable;"))
    private Iterable<MapIcon> getIconsProxy(MapState state) {
        if (Modules.get().get(NoRender.class).noMapMarkers()) return EmptyIterator::new;
        return state.getIcons();
    }
}
