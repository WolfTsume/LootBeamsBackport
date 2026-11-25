package org.dreamfinity.beamingdrops.mixins.early;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import org.dreamfinity.beamingdrops.BeamRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

    // This dispatch covers blocks, icons, custom item renderers and ItemPhysic alike.
    // At RETURN the item's own matrix stack has been restored. The supplied position
    // is already interpolated and relative to the current rendering origin.
    @Inject(method = "func_147939_a(Lnet/minecraft/entity/Entity;DDDFFZ)Z", at = @At("RETURN"), require = 1)
    private void beamingdrops$renderAfterEntity(Entity entity, double x, double y, double z,
        float yaw, float partialTicks, boolean renderStatic, CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValueZ() && entity instanceof EntityItem && !RenderItem.renderInFrame) {
            BeamRenderer.renderBeam((EntityItem) entity, x, y, z, partialTicks);
        }
    }
}
