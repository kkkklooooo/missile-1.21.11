package com.missile;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Missile implements ModInitializer {
	public static final String MOD_ID = "missile";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<SpearEntity> s = Registry.register(Registries.ENTITY_TYPE, Identifier.of("se"),
			EntityType.Builder.<SpearEntity>create(SpearEntity::new, SpawnGroup.MISC)
					.dropsNothing()
					.dimensions(0.5F, 0.5F)
					.eyeHeight(0.13F)
					.maxTrackingRange(4)
					.trackingTickInterval(20).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,Identifier.of("se"))));


	@Override
	public void onInitialize() {

		CustomItem.Init();
		EntityRendererFactories.register(s,SpearRenderer::new);
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}