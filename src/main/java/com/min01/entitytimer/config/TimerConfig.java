package com.min01.entitytimer.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class TimerConfig
{
	public static final TimerConfig CONFIG;
	public static final ForgeConfigSpec CONFIG_SPEC;

	public static ForgeConfigSpec.BooleanValue disableTickrateLimit;
    
    static 
    {
    	Pair<TimerConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(TimerConfig::new);
    	CONFIG = pair.getLeft();
    	CONFIG_SPEC = pair.getRight();
    }
	
    public TimerConfig(ForgeConfigSpec.Builder config) 
    {
    	config.push("Timer Settings");
    	TimerConfig.disableTickrateLimit = config.comment("define whether remove tickrate limit of entities").define("disableTickrateLimit", false);
        config.pop();
    }
}