package com.sentosaiyanaddon.dmz.block;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class HotSpringFluidType extends FluidType {
    private static final ResourceLocation STILL   = ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "block/hotspring_still");
    private static final ResourceLocation FLOW    = ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "block/hotspring_flow");
    private static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath("sentosaiyanaddon", "block/hotspring_overlay");
    private static final int TINT = 0xD04EC9B8; // alpha default was 0xB1 + #4ec9b8
    private static final Vector3f FOG = new Vector3f(0.306f, 0.788f, 0.722f);

    public HotSpringFluidType(Properties properties) { super(properties); }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture()   { return STILL; }
            @Override public ResourceLocation getFlowingTexture() { return FLOW; }
            @Override public ResourceLocation getOverlayTexture() { return OVERLAY; }
            @Override public int getTintColor() { return TINT; }
            @Override public Vector3f modifyFogColor(Camera c, float pt, ClientLevel l, int rd, float d, Vector3f f) { return FOG; }
            @Override public void modifyFogRender(Camera c, FogRenderer.FogMode m, float rd, float pt, float nd, float fd, FogShape s) {
                RenderSystem.setShaderFogStart(-8);
                RenderSystem.setShaderFogEnd(48);
            }
        });
    }
}