package com.missile.mixin;

import com.missile.KWC;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.KineticWeaponComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ExampleMixin {
	@Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/component/type/KineticWeaponComponent;usageTick(Lnet/minecraft/item/ItemStack;ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V"), method = "usageTick",
			cancellable = true)
	public void usageTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
		ItemStack self = (ItemStack)(Object)this;
		ConsumableComponent consumableComponent = self.get(DataComponentTypes.CONSUMABLE);
		if (consumableComponent != null && consumableComponent.shouldSpawnParticlesAndPlaySounds(remainingUseTicks)) {
			consumableComponent.spawnParticlesAndPlaySound(user.getRandom(), user, self, 5);
		}

		KineticWeaponComponent kineticWeaponComponent = self.get(DataComponentTypes.KINETIC_WEAPON);
		KWC kwc = new KWC(
				2.0F,
				4.5F,
				0.125F,
				10,
				(int)(0.5 * 20.0F),
				KineticWeaponComponent.Condition.ofMinSpeed((int)(1 * 20.0F), 1),
				KineticWeaponComponent.Condition.ofMinSpeed((int)(1 * 20.0F), 1),
				KineticWeaponComponent.Condition.ofMinRelativeSpeed((int)(1 * 20.0F), 1),
				0.38F,
				1,
				Optional.of(true ? SoundEvents.ITEM_SPEAR_WOOD_USE : SoundEvents.ITEM_SPEAR_USE),
				Optional.of(true ? SoundEvents.ITEM_SPEAR_WOOD_HIT : SoundEvents.ITEM_SPEAR_HIT)
		);
		if (kineticWeaponComponent != null && !world.isClient()) {
			kwc.usageTick(self, remainingUseTicks, user, user.getActiveHand().getEquipmentSlot());
		} else {
			self.getItem().usageTick(world, user, self, remainingUseTicks);
		}
	}
}