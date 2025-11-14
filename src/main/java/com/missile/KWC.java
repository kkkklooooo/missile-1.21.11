package com.missile;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.PiercingWeaponComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record KWC(
        float minReach,
        float maxReach,
        float hitboxMargin,
        int contactCooldownTicks,
        int delayTicks,
        Optional<net.minecraft.component.type.KineticWeaponComponent.Condition> dismountConditions,
        Optional<net.minecraft.component.type.KineticWeaponComponent.Condition> knockbackConditions,
        Optional<net.minecraft.component.type.KineticWeaponComponent.Condition> damageConditions,
        float forwardMovement,
        float damageMultiplier,
        Optional<RegistryEntry<SoundEvent>> sound,
        Optional<RegistryEntry<SoundEvent>> hitSound
) {
    public static final Codec<net.minecraft.component.type.KineticWeaponComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.rangedInclusiveFloat(0.0F, 128.0F).optionalFieldOf("min_reach", 0.0F).forGetter(net.minecraft.component.type.KineticWeaponComponent::minReach),
                            Codecs.rangedInclusiveFloat(0.0F, 128.0F).optionalFieldOf("max_reach", 3.0F).forGetter(net.minecraft.component.type.KineticWeaponComponent::maxReach),
                            Codecs.rangedInclusiveFloat(0.0F, 1.0F).optionalFieldOf("hitbox_margin", 0.3F).forGetter(net.minecraft.component.type.KineticWeaponComponent::hitboxMargin),
                            Codecs.NON_NEGATIVE_INT.optionalFieldOf("contact_cooldown_ticks", 10).forGetter(net.minecraft.component.type.KineticWeaponComponent::contactCooldownTicks),
                            Codecs.NON_NEGATIVE_INT.optionalFieldOf("delay_ticks", 0).forGetter(net.minecraft.component.type.KineticWeaponComponent::delayTicks),
                            net.minecraft.component.type.KineticWeaponComponent.Condition.CODEC.optionalFieldOf("dismount_conditions").forGetter(net.minecraft.component.type.KineticWeaponComponent::dismountConditions),
                            net.minecraft.component.type.KineticWeaponComponent.Condition.CODEC.optionalFieldOf("knockback_conditions").forGetter(net.minecraft.component.type.KineticWeaponComponent::knockbackConditions),
                            net.minecraft.component.type.KineticWeaponComponent.Condition.CODEC.optionalFieldOf("damage_conditions").forGetter(net.minecraft.component.type.KineticWeaponComponent::damageConditions),
                            Codec.FLOAT.optionalFieldOf("forward_movement", 0.0F).forGetter(net.minecraft.component.type.KineticWeaponComponent::forwardMovement),
                            Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter(net.minecraft.component.type.KineticWeaponComponent::damageMultiplier),
                            SoundEvent.ENTRY_CODEC.optionalFieldOf("sound").forGetter(net.minecraft.component.type.KineticWeaponComponent::sound),
                            SoundEvent.ENTRY_CODEC.optionalFieldOf("hit_sound").forGetter(net.minecraft.component.type.KineticWeaponComponent::hitSound)
                    )
                    .apply(instance, net.minecraft.component.type.KineticWeaponComponent::new)
    );
    public static final PacketCodec<RegistryByteBuf, net.minecraft.component.type.KineticWeaponComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT,
            net.minecraft.component.type.KineticWeaponComponent::minReach,
            PacketCodecs.FLOAT,
            net.minecraft.component.type.KineticWeaponComponent::maxReach,
            PacketCodecs.FLOAT,
            net.minecraft.component.type.KineticWeaponComponent::hitboxMargin,
            PacketCodecs.VAR_INT,
            net.minecraft.component.type.KineticWeaponComponent::contactCooldownTicks,
            PacketCodecs.VAR_INT,
            net.minecraft.component.type.KineticWeaponComponent::delayTicks,
            net.minecraft.component.type.KineticWeaponComponent.Condition.PACKET_CODEC.collect(PacketCodecs::optional),
            net.minecraft.component.type.KineticWeaponComponent::dismountConditions,
            net.minecraft.component.type.KineticWeaponComponent.Condition.PACKET_CODEC.collect(PacketCodecs::optional),
            net.minecraft.component.type.KineticWeaponComponent::knockbackConditions,
            net.minecraft.component.type.KineticWeaponComponent.Condition.PACKET_CODEC.collect(PacketCodecs::optional),
            net.minecraft.component.type.KineticWeaponComponent::damageConditions,
            PacketCodecs.FLOAT,
            net.minecraft.component.type.KineticWeaponComponent::forwardMovement,
            PacketCodecs.FLOAT,
            net.minecraft.component.type.KineticWeaponComponent::damageMultiplier,
            SoundEvent.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional),
            net.minecraft.component.type.KineticWeaponComponent::sound,
            SoundEvent.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional),
            net.minecraft.component.type.KineticWeaponComponent::hitSound,
            net.minecraft.component.type.KineticWeaponComponent::new
    );

    public static Vec3d getAmplifiedMovement(Entity entity) {
        if (!(entity instanceof PlayerEntity) && entity.hasVehicle()) {
            entity = entity.getRootVehicle();
        }

        return entity.method_76333().multiply(20.0);
    }

    public void playSound(Entity entity) {
        this.sound
                .ifPresent(sound -> entity.getEntityWorld().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundCategory(), 1.0F, 1.0F));
    }

    public void playHitSound(Entity entity) {
        this.hitSound
                .ifPresent(sound -> entity.getEntityWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundCategory(), 1.0F, 1.0F));
    }

    public int getUseTicks() {
        return this.delayTicks + (Integer)this.damageConditions.map(net.minecraft.component.type.KineticWeaponComponent.Condition::maxDurationTicks).orElse(0);
    }

    public void usageTick(LivingEntity user, EquipmentSlot slot, ProjectileEntity bullet,boolean isHit,EntityHitResult EHT) {
        //int i = stack.getMaxUseTime(user) - remainingUseTicks;
        if (true) {
            //i -= this.delayTicks;
            Vec3d vec3d = user.getRotationVector();
            double d = vec3d.dotProduct(getAmplifiedMovement(bullet));
            float f = user instanceof PlayerEntity ? 1.0F : 0.2F;
            float g = user instanceof PlayerEntity ? 1.0F : 0.5F;
            double e = user.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
            boolean bl = false;

            if(isHit) {
                Entity entity = EHT.getEntity();
                boolean bl2 = user.isInPiercingCooldown(entity, this.contactCooldownTicks);
                user.startPiercingCooldown(entity);
                if (!bl2) {
                    double h = vec3d.dotProduct(getAmplifiedMovement(entity));
                    double j = Math.max(0.0, d - h);
//                    boolean bl3 = this.dismountConditions.isPresent() && ((net.minecraft.component.type.KineticWeaponComponent.Condition)this.dismountConditions.get()).isSatisfied(i, d, j, f);
//                    boolean bl4 = this.knockbackConditions.isPresent() && ((net.minecraft.component.type.KineticWeaponComponent.Condition)this.knockbackConditions.get()).isSatisfied(i, d, j, f);
//                    boolean bl5 = this.damageConditions.isPresent() && ((net.minecraft.component.type.KineticWeaponComponent.Condition)this.damageConditions.get()).isSatisfied(i, d, j, f);
                    if (true) {
                        float k = (float)e + MathHelper.floor(j * this.damageMultiplier);
                        bl |= user.pierce(slot, entity, k, true , true, true);
                    }
                }
            }

            if (bl) {
                this.playHitSound(user);
                user.getEntityWorld().sendEntityStatus(user, EntityStatuses.KINETIC_ATTACK);
                if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                    Criteria.field_64256.method_76461(serverPlayerEntity, user.method_76444());
                }
            }
        }
    }

    public record Condition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
        public static final Codec<net.minecraft.component.type.KineticWeaponComponent.Condition> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codecs.NON_NEGATIVE_INT.fieldOf("max_duration_ticks").forGetter(net.minecraft.component.type.KineticWeaponComponent.Condition::maxDurationTicks),
                                Codec.FLOAT.optionalFieldOf("min_speed", 0.0F).forGetter(net.minecraft.component.type.KineticWeaponComponent.Condition::minSpeed),
                                Codec.FLOAT.optionalFieldOf("min_relative_speed", 0.0F).forGetter(net.minecraft.component.type.KineticWeaponComponent.Condition::minRelativeSpeed)
                        )
                        .apply(instance, net.minecraft.component.type.KineticWeaponComponent.Condition::new)
        );
        public static final PacketCodec<ByteBuf, net.minecraft.component.type.KineticWeaponComponent.Condition> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_INT,
                net.minecraft.component.type.KineticWeaponComponent.Condition::maxDurationTicks,
                PacketCodecs.FLOAT,
                net.minecraft.component.type.KineticWeaponComponent.Condition::minSpeed,
                PacketCodecs.FLOAT,
                net.minecraft.component.type.KineticWeaponComponent.Condition::minRelativeSpeed,
                net.minecraft.component.type.KineticWeaponComponent.Condition::new
        );

        public boolean isSatisfied(int durationTicks, double speed, double relativeSpeed, double minSpeedMultiplier) {
            return durationTicks <= this.maxDurationTicks && speed >= this.minSpeed * minSpeedMultiplier && relativeSpeed >= this.minRelativeSpeed * minSpeedMultiplier;
        }

        public static Optional<net.minecraft.component.type.KineticWeaponComponent.Condition> ofMinSpeed(int maxDurationTicks, float minSpeed) {
            return Optional.of(new net.minecraft.component.type.KineticWeaponComponent.Condition(maxDurationTicks, minSpeed, 0.0F));
        }

        public static Optional<net.minecraft.component.type.KineticWeaponComponent.Condition> ofMinRelativeSpeed(int maxDurationTicks, float minRelativeSpeed) {
            return Optional.of(new net.minecraft.component.type.KineticWeaponComponent.Condition(maxDurationTicks, 0.0F, minRelativeSpeed));
        }
    }
}

