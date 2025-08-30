package com.gly091020.touhouLittleMad.util;

import com.gly091020.touhouLittleMad.LittleMadMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class AdvancementIconItem {
    private static final DeferredRegister.Items ITEMS_REGISTRY = DeferredRegister.createItems(LittleMadMod.ModID);
    private static final List<DeferredItem<Item>> ITEMS = new ArrayList<>();

    public static List<DeferredItem<Item>> getItems() {
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
        for(DeferredItem<Item> deferredItem: ITEMS){
            var item = deferredItem.asItem();
            if(BuiltInRegistries.ITEM.getKey(item).getPath().equals("advancement_" + id)){
                return item;
            }
        }
        return Items.AIR;
    }
}
