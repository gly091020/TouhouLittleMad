package com.gly091020.touhouLittleMad.datagen;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.MaidEventTrigger;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.util.AdvancementIconItem;
import com.gly091020.touhouLittleMad.util.AdvancementTriggerKeys;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DataGenerators {
    // 去nm的数据包和资源包
    public static class LootTableGen extends LootTableProvider {
        public LootTableGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(LootTableGen::giftGen, LootContextParamSets.GIFT)), registries);
        }

        public static LootTableSubProvider giftGen(HolderLookup.Provider provider){
            final Map<Item, Integer> items = new HashMap<>();
            items.put(Items.COAL, 120);
            items.put(Items.IRON_INGOT, 100);
            items.put(Items.GOLD_INGOT, 60);
            items.put(Items.DIAMOND, 15);
            items.put(Items.EMERALD, 5);
            items.put(Items.BONE, 100);
            items.put(Items.FEATHER, 80);
            items.put(Items.SPIDER_EYE, 70);
            items.put(Items.ENDER_PEARL, 40);
            items.put(Items.BLAZE_ROD, 10);
            items.put(Items.BREAD, 150);
            items.put(Items.APPLE, 100);
            items.put(Items.COOKED_BEEF, 80);
            items.put(Items.PUMPKIN_PIE, 50);
            items.put(Items.CAKE, 15);
            items.put(Items.ENCHANTED_GOLDEN_APPLE, 5);
            items.put(InitItems.GARAGE_KIT.asItem(), 1);

            var pool = LootPool.lootPool();
            pool.setRolls(ConstantValue.exactly(1));
            items.forEach((item, weight) ->{
                pool.add(LootItem.lootTableItem(item).setWeight(weight).apply(
                        SetItemCountFunction.setCount(ConstantValue.exactly(1))));
            });

            return biConsumer -> biConsumer.accept(
                    ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(LittleMadMod.ModID, "maid_gift")),
                    new LootTable.Builder().withPool(pool));
        }
    }

    public static class AllAdvancementProvider extends AdvancementProvider{

        public AllAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
            super(output, registries, existingFileHelper, List.of(new AllAdvancementGenerator()));
        }

        public static class AllAdvancementGenerator implements AdvancementProvider.AdvancementGenerator{
            @Override
            public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
                var root = genRoot();
                consumer.accept(root);
                consumer.accept(genGift(root));
                var hurt1 = genHurtByOwner(root);
                consumer.accept(hurt1);
                var hurt2 = genHurtOwner(hurt1);
                consumer.accept(hurt2);
                consumer.accept(genMad(hurt2));
            }

            private Component getTitle(String id){
                return Component.translatable(String.format("advancements.touhou_little_mad.%s.title", id));
            }

            private Component getDescription(String id){
                return Component.translatable(String.format("advancements.touhou_little_mad.%s.description", id));
            }

            private AdvancementHolder genRoot(){
                return genBase(AdvancementIconItem.findItem("little_mad"), "little_mad", "tamed_maid",
                        ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/advancements/backgrounds/stone.png"))
                        .build(ResourceLocation.fromNamespaceAndPath(LittleMadMod.ModID, "little_mad"));
            }

            private Advancement.Builder genBase(ItemStack item, String name, String triggerName, @Nullable ResourceLocation bg,
                                                AdvancementType type, boolean showToast, boolean hidden){
                return new Advancement.Builder()
                        .display(new DisplayInfo(
                                item,
                                getTitle(name),
                                getDescription(name),
                                Optional.ofNullable(bg),
                                type,
                                showToast,
                                showToast,
                                hidden
                        )).addCriterion(triggerName, MaidEventTrigger.create(triggerName));
            }

            private Advancement.Builder genBase(Item item, String name, String triggerName, @Nullable ResourceLocation bg){
                return genBase(new ItemStack(item, 1), name, triggerName, bg, AdvancementType.TASK, false, false);
            }

            private Advancement.Builder genBase(Item item, String name, String triggerName, AdvancementType type, boolean showToast, boolean hidden){
                return genBase(new ItemStack(item, 1), name, triggerName, null, type, showToast, hidden);
            }

            private Advancement.Builder genBase(Item item, String name, String triggerName){
                return genBase(new ItemStack(item, 1), name, triggerName, null, AdvancementType.TASK, true, false);
            }

            private AdvancementHolder genGift(AdvancementHolder parent){
                return genBase(AdvancementIconItem.findItem("maid_gift"), "maid_gift", AdvancementTriggerKeys.SEND_GIFT)
                        .parent(parent)
                        .build(ResourceLocation.fromNamespaceAndPath(LittleMadMod.ModID, "maid_gift"));
            }

            private AdvancementHolder genHurtByOwner(AdvancementHolder parent){
                return genBase(AdvancementIconItem.findItem("maid_hurt_by_owner"),
                        "maid_hurt_by_owner", AdvancementTriggerKeys.HURT_BY_OWNER)
                        .parent(parent)
                        .build(ResourceLocation.fromNamespaceAndPath(LittleMadMod.ModID, "maid_hurt_by_owner"));
            }

            private AdvancementHolder genHurtOwner(AdvancementHolder parent){
                return genBase(AdvancementIconItem.findItem("maid_hurt_owner"),
                        "maid_hurt_owner", AdvancementTriggerKeys.HURT_OWNER)
                        .parent(parent)
                        .build(ResourceLocation.fromNamespaceAndPath(LittleMadMod.ModID, "maid_hurt_owner"));
            }

            private AdvancementHolder genMad(AdvancementHolder parent){
                return genBase(AdvancementIconItem.findItem("maid_mad"),
                        "maid_mad", AdvancementTriggerKeys.MAD, AdvancementType.CHALLENGE, true, true)
                        .parent(parent)
                        .build(ResourceLocation.fromNamespaceAndPath(LittleMadMod.ModID, "maid_mad"));
            }
        }
    }

    public static class AllItemModelProvider extends ItemModelProvider{

        public AllItemModelProvider(PackOutput output, String modID, ExistingFileHelper existingFileHelper) {
            super(output, modID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            AdvancementIconItem.getItems().forEach(itemDeferredItem ->
                    basicItem(itemDeferredItem.asItem()));
        }
    }
}
