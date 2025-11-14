package com.missile;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.Optional;

public class SpearItemClass extends Item implements ProjectileItem {
    public SpearItemClass(ToolMaterial material,
                          float swingAnimationSeconds,
                          float chargeDamageMultiplier,
                          float chargeDelaySeconds,
                          float maxDurationForDismountSeconds,
                          float minSpeedForDismount,
                          float maxDurationForChargeKnockbackInSeconds,
                          float minSpeedForChargeKnockback,
                          float maxDurationForChargeDamageInSeconds,
                          float minRelativeSpeedForChargeDamage,
                          Item.Settings s) {
        super(spear(s,material,swingAnimationSeconds,chargeDamageMultiplier,chargeDelaySeconds,maxDurationForDismountSeconds,minSpeedForDismount,maxDurationForChargeKnockbackInSeconds,
                minSpeedForChargeKnockback,maxDurationForChargeDamageInSeconds,minRelativeSpeedForChargeDamage));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {




        return super.use(world, user, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {

        Missile.LOGGER.info("USE!");
        //user.addVelocity(user.getRotationVector().normalize());
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Missile.LOGGER.info("Hit!!!!");
    }

    public static Item.Settings spear(Item.Settings s,
            ToolMaterial material,
            float swingAnimationSeconds,
            float chargeDamageMultiplier,
            float chargeDelaySeconds,
            float maxDurationForDismountSeconds,
            float minSpeedForDismount,
            float maxDurationForChargeKnockbackInSeconds,
            float minSpeedForChargeKnockback,
            float maxDurationForChargeDamageInSeconds,
            float minRelativeSpeedForChargeDamage
    ) {

        return s.maxDamage(material.durability())
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .component(DataComponentTypes.DAMAGE_TYPE, new LazyRegistryEntryReference<>(DamageTypes.SPEAR))
                .component(
                        DataComponentTypes.KINETIC_WEAPON,
                        new KineticWeaponComponent(
                                2.0F,
                                4.5F,
                                0.125F,
                                10,
                                (int)(chargeDelaySeconds * 20.0F),
                                KineticWeaponComponent.Condition.ofMinSpeed((int)(maxDurationForDismountSeconds * 20.0F), minSpeedForDismount),
                                KineticWeaponComponent.Condition.ofMinSpeed((int)(maxDurationForChargeKnockbackInSeconds * 20.0F), minSpeedForChargeKnockback),
                                KineticWeaponComponent.Condition.ofMinRelativeSpeed((int)(maxDurationForChargeDamageInSeconds * 20.0F), minRelativeSpeedForChargeDamage),
                                0.38F,
                                chargeDamageMultiplier,
                                Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_USE : SoundEvents.ITEM_SPEAR_USE),
                                Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_HIT : SoundEvents.ITEM_SPEAR_HIT)
                        )
                )
//                .component(
//                        DataComponentTypes.PIERCING_WEAPON,
//                        new PiercingWeaponComponent(
//                                2.0F,
//                                4.5F,
//                                0.25F,
//                                true,
//                                false,
//                                Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_ATTACK : SoundEvents.ITEM_SPEAR_ATTACK),
//                                Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_HIT : SoundEvents.ITEM_SPEAR_HIT)
//                        )
//                )
                .component(DataComponentTypes.MINIMUM_ATTACK_CHARGE, 1.0F)
                .component(DataComponentTypes.SWING_ANIMATION, new SwingAnimationComponent(SwingAnimationType.STAB, (int)(swingAnimationSeconds * 20.0F)))
                .attributeModifiers(
                        AttributeModifiersComponent.builder()
                                .add(
                                        EntityAttributes.ATTACK_DAMAGE,
                                        new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 0.0F + material.attackDamageBonus(), EntityAttributeModifier.Operation.ADD_VALUE),
                                        AttributeModifierSlot.MAINHAND
                                )
                                .add(
                                        EntityAttributes.ATTACK_SPEED,
                                        new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, 1.0F / swingAnimationSeconds - 4.0, EntityAttributeModifier.Operation.ADD_VALUE),
                                        AttributeModifierSlot.MAINHAND
                                )
                                .build()
                )
                .component(DataComponentTypes.USE_EFFECTS, new UseEffectsComponent(true, false, 1.0F))
                .component(DataComponentTypes.WEAPON, new WeaponComponent(1));
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        TridentEntity tridentEntity = new TridentEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1));
        tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        return tridentEntity;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Missile.LOGGER.info("STOP");
        if (world instanceof ServerWorld serverWorld) {
            ItemStack itemStack = stack.splitUnlessCreative(1, user);
            TridentEntity tridentEntity = ProjectileEntity.spawnWithVelocity(TridentEntity::new, serverWorld, itemStack, user, 0.0F, 2.5F, 1.0F);
        }
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Missile.LOGGER.info("Finish");
        return super.finishUsing(stack, world, user);
    }
}
