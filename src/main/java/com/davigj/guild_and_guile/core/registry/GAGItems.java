package com.davigj.guild_and_guile.core.registry;

import com.davigj.guild_and_guile.common.item.PilferingRodItem;
import com.davigj.guild_and_guile.core.GuildAndGuile;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = GuildAndGuile.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GAGItems {
    public static final ItemSubRegistryHelper HELPER = GuildAndGuile.REGISTRY_HELPER.getItemSubHelper();

    public static final RegistryObject<Item> PILFERING_ROD = HELPER.createItem("pilfering_rod", () -> new PilferingRodItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
}
