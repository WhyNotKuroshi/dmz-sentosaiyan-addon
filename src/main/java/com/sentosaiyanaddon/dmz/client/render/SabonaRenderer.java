package com.sentosaiyanaddon.dmz.client.render;

import com.sentosaiyanaddon.dmz.init.entities.MasterSabonaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SabonaRenderer extends GeoEntityRenderer<MasterSabonaEntity> {
    public SabonaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoModel<MasterSabonaEntity>() {
            @Override
            public ResourceLocation getModelResource(MasterSabonaEntity animatable) {
                return ResourceLocation.fromNamespaceAndPath("dragonminez", "geo/entity/npcs/sabona.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(MasterSabonaEntity animatable) {
                return ResourceLocation.fromNamespaceAndPath("dragonminez", "textures/entity/npcs/sabona.png");
            }

            @Override
            public ResourceLocation getAnimationResource(MasterSabonaEntity animatable) {
                return ResourceLocation.fromNamespaceAndPath("dragonminez",
                        "animations/entity/races/movement.animation.json");
            }
        });
    }
}