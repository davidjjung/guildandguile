package com.davigj.guild_and_guile.core.data.client;

import com.davigj.guild_and_guile.core.GuildAndGuile;
import com.davigj.guild_and_guile.core.registry.GAGItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class GAGItemModelProvider extends ItemModelProvider {
    public GAGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, GuildAndGuile.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        this.generatedItem((ItemLike) GAGItems.PILFERING_ROD.get());
    }

    private void generatedItem(ItemLike item) {
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item.asItem());
        ((ItemModelBuilder)this.withExistingParent(itemName.getPath(), "item/" + "generated")).texture("layer0", new ResourceLocation(this.modid, "item/" + itemName.getPath()));
    }
}
