package org.dreamfinity.beamingdrops;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class BeamRenderer {
    private static final ResourceLocation LOOT_BEAM_TEXTURE = new ResourceLocation("lootbeamsbackport:textures/entity/loot_beam.png");
    private static final ResourceLocation LOOT_BEAM_HIGH_TEXTURE = new ResourceLocation("lootbeamsbackport:textures/entity/loot_beam_high_res.png");

    public static void renderBeam(EntityItem entityItem, double x, double y, double z, float partialTicks) {
        World world = entityItem.worldObj;
        ItemStack itemStack = entityItem.getEntityItem();

        if (world == null || itemStack == null || itemStack.getItem() == null || !Config.needRenderBeam
            || RenderManager.instance.renderEngine == null
            || !BeamFilters.allows(ItemHelper.isStackInArray(itemStack, Config.itemBlackList), Config.isWhitelist)) {
            return;
        }

        Color beamColor = getItemColor(itemStack);
        if (!BeamFilters.allows(BeamColors.isColorInArray(beamColor, Config.colorBlackList), Config.isColorWhitelist)) {
            return;
        }

        // Do not inherit a custom item's centering, scale, or rotation. Restore all
        // GL state touched by the beam before the next entity or render pass runs.
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
            | GL11.GL_CURRENT_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_TRANSFORM_BIT);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            drawBeam(entityItem.age + partialTicks, beamColor);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void drawBeam(float age, Color beamColor) {
        Tessellator tessellator = Tessellator.instance;
        RenderManager.instance.renderEngine.bindTexture(Config.useHighResBeam ? LOOT_BEAM_HIGH_TEXTURE : LOOT_BEAM_TEXTURE);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(Config.useDepthMask);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        double beamHeight = Config.beamHeight;
        float rotationTime = Config.useRotatingBeam ? age : 0;
        double rotationAngle = rotationTime / (beamHeight + 4) * 0.05 * (1.0 - 2.5);
        double heightOffset = Config.useFloatingBeam ? Math.cos(age * 0.05) / beamHeight : 0.2;

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(beamColor.getRed(), beamColor.getGreen(), beamColor.getBlue(), beamColor.getAlpha());
        tessellator.setBrightness(15728880);

        BeamGeometry.emit(Config.beamDiameter, beamHeight, rotationAngle, 0.2 + heightOffset,
            tessellator::addVertexWithUV);
        tessellator.draw();
    }

    public static Color getItemColor(ItemStack itemStack) {
        Color rarityColor = BeamColors.fromFormattingCode(
            itemStack.getItem().getRarity(itemStack).rarityColor.getFormattingCode());
        return BeamColors.fromDisplayName(itemStack.getDisplayName(), rarityColor);
    }
}
