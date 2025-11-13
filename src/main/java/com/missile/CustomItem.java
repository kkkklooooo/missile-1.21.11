package com.missile;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.world.World;

import java.util.Optional;

import static net.minecraft.item.Items.register;

public class CustomItem{

    public static final Item WOODEN_THROWABLE_SPEAR = register("tob",(s)->{
        return new SpearItemClass(ToolMaterial.WOOD, 0.65F, 0.7F, 0.75F, 5.0F, 14.0F, 6.0F, 5.1F, 15.0F, 4.6F,s);
    });


    public static void Init(){
        Missile.LOGGER.info("REG");

    }



    // 可选：重写其他方法来自定义行为

}