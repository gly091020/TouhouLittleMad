package com.gly091020.touhouLittleMad.util;

import com.gly091020.touhouLittleMad.LittleMadMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class AdvancementIconItem {
    private static final DeferredRegister<Item> ITEMS_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, LittleMadMod.ModID);
    private static final List<RegistryObject<Item>> ITEMS = new ArrayList<>();

    public static List<RegistryObject<Item>> getItems() {
        return ITEMS;
    }

    public static void registry(String id){
        ITEMS.add(ITEMS_REGISTRY.register("advancement_" + id, () -> new Item(new Item.Properties())));
    }

    public static void registry(IEventBus bus){
        ITEMS_REGISTRY.register(bus);
    }

    public static void registryBuiltInItem(){
        registry("little_mad");
        registry("maid_gift");
        registry("maid_hurt_by_owner");
        registry("maid_hurt_owner");
        registry("maid_mad");
    }

    public static Item findItem(String id){
        for(RegistryObject<Item> registryObject: ITEMS){
            var item = registryObject.get();
            if(BuiltInRegistries.ITEM.getKey(item).getPath().equals("advancement_" + id)){
                return item;
            }
        }
        return Items.AIR;
    }
}
