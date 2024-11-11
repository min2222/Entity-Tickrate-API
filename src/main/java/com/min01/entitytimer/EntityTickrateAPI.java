package com.min01.entitytimer;

import com.min01.entitytimer.config.TimerConfig;
import com.min01.entitytimer.network.TickrateNetwork;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

@Mod(EntityTickrateAPI.MODID)
public class EntityTickrateAPI 
{
	public static final String MODID = "entitytickrateapi";
	
	public EntityTickrateAPI() 
	{
		ModLoadingContext ctx = ModLoadingContext.get();
		TickrateNetwork.registerMessages();
		ctx.registerConfig(Type.COMMON, TimerConfig.CONFIG_SPEC, "entity-tickrate-api.toml");
	}
}
