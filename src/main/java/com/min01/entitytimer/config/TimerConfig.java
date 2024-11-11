package com.min01.entitytimer.config;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TimerConfig
{
	public static final TimerConfig CONFIG;
	public static final ModConfigSpec CONFIG_SPEC;

	public static ModConfigSpec.BooleanValue disableTickrateLimit;
    
    static 
    {
    	Pair<TimerConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(TimerConfig::new);
    	CONFIG = pair.getLeft();
    	CONFIG_SPEC = pair.getRight();
    }
	
    public TimerConfig(ModConfigSpec.Builder config) 
    {
    	config.push("Timer Settings");
    	TimerConfig.disableTickrateLimit = config.comment("define whether remove tickrate limit of entities").define("disableTickrateLimit", false);
        config.pop();
    }
}
