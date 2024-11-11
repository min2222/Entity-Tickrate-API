package com.min01.entitytimer;

import com.min01.entitytimer.config.TimerConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;

@Mod(EntityTickrateAPI.MODID)
public class EntityTickrateAPI 
{
	public static final String MODID = "entitytickrateapi";
	
	public EntityTickrateAPI(IEventBus bus, ModContainer container) 
	{
		container.registerConfig(Type.COMMON, TimerConfig.CONFIG_SPEC, "entity-tickrate-api.toml");
	}
}
