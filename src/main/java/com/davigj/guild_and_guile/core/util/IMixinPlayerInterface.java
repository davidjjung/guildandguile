package com.davigj.guild_and_guile.core.util;

import com.davigj.guild_and_guile.common.entity.projectile.PilferingHook;

import javax.annotation.Nullable;

public interface IMixinPlayerInterface {
    @Nullable
    PilferingHook guildandguile$getPilfering();
    void guildandguile$setPilfering(@Nullable PilferingHook pilfering);
}
