package com.missile;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.SwingAnimationType;

import java.util.Optional;

import static net.minecraft.item.Items.register;

public class CustomSpearItem extends Item {

    public static final Item WOODEN_THROWABLE_SPEAR = register("tob",new Settings().spear(
            ToolMaterial.GOLD,
            0.65F, 0.7F, 0.75F, 5.0F, 14.0F, 6.0F, 5.1F, 15.0F, 4.6F
    ));


    public static void Init(){
        Missile.LOGGER.info("REG");
    }

    public CustomSpearItem(Settings settings) {
        super(settings);
    }

    // 可选：重写其他方法来自定义行为
    public static Item.Settings spear(
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
        return new Settings().maxDamage(material.durability())
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
                .component(
                        DataComponentTypes.PIERCING_WEAPON,
                        new PiercingWeaponComponent(
                                2.0F,
                                4.5F,
                                0.25F,
                                true,
                                false,
                                Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_ATTACK : SoundEvents.ITEM_SPEAR_ATTACK),
                                Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_HIT : SoundEvents.ITEM_SPEAR_HIT)
                        )
                )
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
}