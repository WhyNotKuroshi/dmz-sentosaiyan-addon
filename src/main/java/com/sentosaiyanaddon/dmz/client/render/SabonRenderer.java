package com.sentosaiyanaddon.dmz.client.render;

import com.sentosaiyanaddon.dmz.init.entities.MasterSabonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SabonRenderer extends GeoEntityRenderer<MasterSabonEntity> {
    public SabonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoModel<MasterSabonEntity>() {
            @Override
            public ResourceLocation getModelResource(MasterSabonEntity animatable) {
                return ResourceLocation.fromNamespaceAndPath("dragonminez", "geo/entity/races/sabona.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(MasterSabonEntity animatable) {
                return ResourceLocation.fromNamespaceAndPath("dragonminez", "textures/entity/races/sabona.png");
            }

            @Override
            public ResourceLocation getAnimationResource(MasterSabonEntity animatable) {
                return ResourceLocation.fromNamespaceAndPath("dragonminez",
                        "animations/entity/races/movement.animation.json");
            }
        });
    }
}