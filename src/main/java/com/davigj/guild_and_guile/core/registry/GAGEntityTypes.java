package com.davigj.guild_and_guile.core.registry;

import com.davigj.guild_and_guile.common.entity.projectile.PilferingHook;
import com.davigj.guild_and_guile.core.GuildAndGuile;
import com.teamabnormals.blueprint.core.util.registry.EntitySubRegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = GuildAndGuile.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GAGEntityTypes {

    public static final EntitySubRegistryHelper HELPER = GuildAndGuile.REGISTRY_HELPER.getEntitySubHelper();

    public static final RegistryObject<EntityType<PilferingHook>> PILFERING_BOBBER = HELPER.createEntity(
            "pilfering_bobber", PilferingHook::new, PilferingHook::new, MobCategory.MISC, 0.25F, 0.25F);

}
