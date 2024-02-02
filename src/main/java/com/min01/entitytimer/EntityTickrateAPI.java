package com.min01.entitytimer;

import com.min01.entitytimer.network.TickrateNetwork;

import net.minecraftforge.fml.common.Mod;

@Mod(EntityTickrateAPI.MODID)
public class EntityTickrateAPI 
{
	public static final String MODID = "entitytickrateapi";
	
	public EntityTickrateAPI() 
	{
		TickrateNetwork.registerMessages();
	}
}
