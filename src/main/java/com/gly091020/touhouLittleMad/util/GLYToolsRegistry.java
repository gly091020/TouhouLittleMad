package com.gly091020.touhouLittleMad.util;

import com.gly091020.touhouLittleMad.LittleMadMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GLYToolsRegistry {
    private static final DeferredRegister<Item> ITEMS_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, LittleMadMod.ModID);
    public static final Map<String, RegistryObject<Item>> items = new HashMap<>();
    public static void registry(IEventBus bus){
        registry("player_not_die", () -> simpleItem("防死亡", "防止玩家死亡"));

        if(!FMLEnvironment.production){
            ITEMS_REGISTRY.register(bus);
        }
    }
    private static void registry(String name, Supplier<Item> supplier){
        items.put(name, ITEMS_REGISTRY.register(name, supplier));
    }

    public static Item simpleItem(Component name, Component tip){
        return new Item(new Item.Properties()){
            @Override
            public @NotNull Component getName(@NotNull ItemStack stack) {
                return name;
            }

            @Override
            public @NotNull Component getHighlightTip(@NotNull ItemStack item, @NotNull Component displayName) {
                return tip;
            }
        };
    }

    public static Item simpleItem(String name, String tip){
        return simpleItem(Component.literal(name), Component.literal(tip));
    }
}
