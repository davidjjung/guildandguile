package com.davigj.guild_and_guile.core.mixin;

import com.davigj.guild_and_guile.common.entity.projectile.PilferingHook;
import com.davigj.guild_and_guile.core.util.IMixinPlayerInterface;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(Player.class)
public class PlayerMixin implements IMixinPlayerInterface {
    @Unique
    public PilferingHook guildandguile$pilfering; // Instance field

    @Nullable
    @Override
    public PilferingHook guildandguile$getPilfering() {
        return guildandguile$pilfering;
    }

    @Override
    public void guildandguile$setPilfering(@Nullable PilferingHook pilfering) {
        this.guildandguile$pilfering = pilfering;
    }
}
