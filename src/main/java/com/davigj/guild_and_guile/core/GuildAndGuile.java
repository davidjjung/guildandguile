package com.davigj.guild_and_guile.core;

import com.davigj.guild_and_guile.client.renderer.entity.PilferingHookRenderer;
import com.davigj.guild_and_guile.common.item.PilferingRodItem;
import com.davigj.guild_and_guile.core.data.client.GAGItemModelProvider;
import com.davigj.guild_and_guile.core.registry.GAGEntityTypes;
import com.davigj.guild_and_guile.core.registry.GAGItems;
import com.davigj.guild_and_guile.core.util.IMixinPlayerInterface;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GuildAndGuile.MOD_ID)
public class GuildAndGuile {
    public static final String MOD_ID = "guild_and_guile";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

    public GuildAndGuile() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        MinecraftForge.EVENT_BUS.register(this);

		REGISTRY_HELPER.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::dataSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::registerRenderers);
        });

        context.registerConfig(ModConfig.Type.COMMON, GAGConfig.COMMON_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerPilferingRodModelProperties(GAGItems.PILFERING_ROD.get());
        });
    }

    private void dataSetup(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        boolean includeClient = event.includeClient();
        generator.addProvider(includeClient, new GAGItemModelProvider(generator, helper));
    }

    @OnlyIn(Dist.CLIENT)
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GAGEntityTypes.PILFERING_BOBBER.get(), PilferingHookRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerPilferingRodModelProperties(Item fishingRod) {
        ItemProperties.register(fishingRod, new ResourceLocation("cast"), (stack, level, entity, i) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                boolean isMainhand = entity.getMainHandItem() == stack;
                boolean isOffHand = entity.getOffhandItem() == stack;
                if (entity.getMainHandItem().getItem() instanceof PilferingRodItem) {
                    isOffHand = false;
                }
                return (isMainhand || isOffHand) && entity instanceof Player player &&
                        ((IMixinPlayerInterface) (Object) player).guildandguile$getPilfering() != null ? 1.0F : 0.0F;
            }
        });
    }
}