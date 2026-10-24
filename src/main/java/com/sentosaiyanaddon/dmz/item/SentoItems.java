package com.sentosaiyanaddon.dmz.item;

import com.sentosaiyanaddon.dmz.SentoSaiyanAddon;
import com.sentosaiyanaddon.dmz.block.SentoFluids;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SentoItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, SentoSaiyanAddon.MOD_ID);

    public static final RegistryObject<Item> HOT_SPRING_BUCKET = ITEMS.register("hot_spring_bucket",
        () -> new BucketItem(SentoFluids.HOT_SPRING,
            new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    
    private SentoItems() {}
}