package com.davigj.guild_and_guile.common.item;

import com.davigj.guild_and_guile.common.entity.projectile.PilferingHook;
import com.davigj.guild_and_guile.core.util.IMixinPlayerInterface;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class PilferingRodItem extends Item implements Vanishable {
    public PilferingRodItem(Properties p_41383_) {
        super(p_41383_);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (((IMixinPlayerInterface) (Object) player).guildandguile$getPilfering() != null) {
            if (!level.isClientSide) {
                int i = ((IMixinPlayerInterface) (Object) player).guildandguile$getPilfering().retrieve(itemstack);
                itemstack.hurtAndBreak(i, player, (p_41288_) -> {
                    p_41288_.broadcastBreakEvent(hand);
                });
            }

            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!level.isClientSide) {
                // TODO: add ints that correspond to enchantment levels as needed to the constructor. Something like itemstack.getEnchantmentLevel(whatever)
                level.addFreshEntity(new PilferingHook(player, level));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public int getEnchantmentValue() {
        return 1;
    }
}
