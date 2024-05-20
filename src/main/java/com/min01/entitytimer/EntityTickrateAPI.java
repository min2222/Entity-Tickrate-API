package com.min01.entitytimer;

import com.min01.entitytimer.config.TimerConfig;
import com.min01.entitytimer.network.TickrateNetwork;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(EntityTickrateAPI.MODID)
public class EntityTickrateAPI 
{
	public static final String MODID = "entitytickrateapi";
	
	public EntityTickrateAPI() 
	{
		TickrateNetwork.registerMessages();
		TimerConfig.loadConfig(TimerConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("entity-tickrate-api.toml").toString());
	}
}
