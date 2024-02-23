package com.davigj.guild_and_guile.core.registry;

import com.davigj.guild_and_guile.core.GuildAndGuile;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GuildAndGuile.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GAGBlocks {
    public static final BlockSubRegistryHelper HELPER = GuildAndGuile.REGISTRY_HELPER.getBlockSubHelper();
    //	public static final RegistryObject<Block> TEMPLATE_BLOCK = HELPER.createBlock("template_block", () -> new Block(Block.Properties.copy(Blocks.STONE)), CreativeModeTab.TAB_BUILDING_BLOCKS);
}