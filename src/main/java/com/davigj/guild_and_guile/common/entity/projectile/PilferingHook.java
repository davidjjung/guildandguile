package com.davigj.guild_and_guile.common.entity.projectile;

import com.davigj.guild_and_guile.core.registry.GAGEntityTypes;
import com.davigj.guild_and_guile.core.registry.GAGItems;
import com.davigj.guild_and_guile.core.util.IMixinPlayerInterface;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class PilferingHook extends Projectile {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RandomSource syncronizedRandom = RandomSource.create();
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(PilferingHook.class, EntityDataSerializers.INT);
    @Nullable
    private Entity hookedIn;
    private int life;
    private PilferingHook.PilferingHookState currentState = PilferingHookState.FLYING;

    public PilferingHook(EntityType<? extends PilferingHook> type, Level level) {
        super(type, level);
        this.noCulling = true;
        // TODO: add more ints to the constructor corresponding to enchantment levels as needed
    }

    public PilferingHook(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(GAGEntityTypes.PILFERING_BOBBER.get(), world);
    }

    public PilferingHook(Player player, Level level) {
        // used for initializing a pilfering hook entity in-world
        this(GAGEntityTypes.PILFERING_BOBBER.get(), level);
        this.setOwner(player);
        float f = player.getXRot();
        float f1 = player.getYRot();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        double d0 = player.getX() - (double)f3 * 0.3D;
        double d1 = player.getEyeY();
        double d2 = player.getZ() - (double)f2 * 0.3D;
        this.moveTo(d0, d1, d2, f1, f);
        Vec3 vec3 = new Vec3((double)(-f3), (double)Mth.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
        double d3 = vec3.length();
        vec3 = vec3.multiply(0.6D / d3 + this.random.triangle(0.5D, 0.0103365D), 0.6D / d3 + this.random.triangle(0.5D, 0.0103365D), 0.6D / d3 + this.random.triangle(0.5D, 0.0103365D));
        this.setDeltaMovement(vec3);
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entity) {
        if (DATA_HOOKED_ENTITY.equals(entity)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
        }
        super.onSyncedDataUpdated(entity);
    }

    public boolean shouldRenderAtSqrDistance(double p_37125_) {
        double d0 = 64.0D;
        return p_37125_ < 4096.0D;
    }

    public void lerpTo(double p_37127_, double p_37128_, double p_37129_, float p_37130_, float p_37131_, int p_37132_, boolean p_37133_) {
    }

    private void updateOwnerInfo(@Nullable PilferingHook hook) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            ((IMixinPlayerInterface) (Object) player).guildandguile$setPilfering(hook);
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    public void addAdditionalSaveData(CompoundTag p_37161_) {
    }

    public void readAdditionalSaveData(CompoundTag p_37151_) {
    }

    public void handleEntityEvent(byte flag) {
        if (flag == 31 && this.level.isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer()) {
            this.pullEntity(this.hookedIn);
        }
        super.handleEntityEvent(flag);
    }

    public int retrieve(ItemStack stack) {
        Player player = this.getPlayerOwner();
        if (!this.level.isClientSide && player != null && !this.shouldStopFishing(player)) {
            int i = 0;
//            net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
            if (this.hookedIn != null) {
                this.pullEntity(this.hookedIn);
                // TODO: advancement criteria for using a pilfering rod successfully
//                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, p_37157_, this, Collections.emptyList());
                this.level.broadcastEntityEvent(this, (byte)31);
                i = this.hookedIn instanceof ItemEntity ? 3 : 5;
            }
            if (this.onGround) {
                i = 2;
            }
            this.discard();
            return i;
        } else {
            return 0;
        }
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void remove(Entity.RemovalReason reason) {
        this.updateOwnerInfo((PilferingHook) null);
        super.remove(reason);
    }

    public void onClientRemoval() {
        this.updateOwnerInfo((PilferingHook)null);
    }

    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        this.updateOwnerInfo(this);
    }

    private boolean shouldStopFishing(Player player) {
        ItemStack itemstack = player.getMainHandItem();
        ItemStack itemstack1 = player.getOffhandItem();
        boolean flag = itemstack.is(GAGItems.PILFERING_ROD.get());
        boolean flag1 = itemstack1.is(GAGItems.PILFERING_ROD.get());
        if (!player.isRemoved() && player.isAlive() && (flag || flag1) && !(this.distanceToSqr(player) > 1024.0D)) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    protected void pullEntity(Entity victim) {
        Entity entity = this.getOwner();
        if (entity != null) {
            Vec3 vec3 = (new Vec3(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).scale(0.1D);
            victim.setDeltaMovement(victim.getDeltaMovement().add(vec3));
        }
    }

    public void tick() {
        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level.getGameTime());
        super.tick();
        Player player = this.getPlayerOwner();
        if (player == null) {
            this.discard();
        } else if (this.level.isClientSide || !this.shouldStopFishing(player)) {
            if (this.onGround) {
                ++this.life;
                if (this.life >= 1200) {
                    this.discard();
                    return;
                }
            } else {
                this.life = 0;
            }

            BlockPos blockpos = this.blockPosition();
            FluidState fluidstate = this.level.getFluidState(blockpos);

            // server side logic? entity stuff
            if (this.currentState == PilferingHook.PilferingHookState.FLYING) {
                if (this.hookedIn != null) {
                    this.setDeltaMovement(Vec3.ZERO);
                    this.currentState = PilferingHook.PilferingHookState.HOOKED_IN_ENTITY;
                    return;
                }
                this.checkCollision();
            } else {
                if (this.currentState == PilferingHook.PilferingHookState.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (!this.hookedIn.isRemoved() && this.hookedIn.level.dimension() == this.level.dimension()) {
                            this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8D), this.hookedIn.getZ());
                        } else {
                            this.setHookedEntity((Entity)null);
                            this.currentState = PilferingHook.PilferingHookState.FLYING;
                        }
                    }
                    return;
                }
            }

            if (!fluidstate.is(FluidTags.WATER)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            this.updateRotation();

            // block collision
            if (this.currentState == PilferingHook.PilferingHookState.FLYING && (this.onGround || this.horizontalCollision)) {
                this.setDeltaMovement(Vec3.ZERO);
            }
            this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
            this.reapplyPosition();
        }
    }

    private void setHookedEntity(@Nullable Entity entity) {
        this.hookedIn = entity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, entity == null ? 0 : entity.getId() + 1);
    }

    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }

    public boolean canChangeDimensions() {
        return false;
    }

    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? this.getId() : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_150150_) {
        super.recreateFromPacket(p_150150_);
        if (this.getPlayerOwner() == null) {
            int i = p_150150_.getData();
            LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level.getEntity(i), i);
            this.kill();
        }
    }

    private void checkCollision() {
        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (hitresult.getType() == HitResult.Type.MISS || !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) this.onHit(hitresult);
    }

    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level.isClientSide) {
            this.setHookedEntity(hitResult.getEntity());
        }
    }

    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(hitResult.distanceTo(this)));
    }

    static enum PilferingHookState {
        FLYING,
        HOOKED_IN_ENTITY,
    }
}