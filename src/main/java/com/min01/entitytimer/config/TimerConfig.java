package com.min01.entitytimer.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class TimerConfig
{
    private static ForgeConfigSpec.Builder BUILDER;
    public static ForgeConfigSpec CONFIG;

	public static ForgeConfigSpec.BooleanValue disableTickrateLimit;
    
    public static void loadConfig(ForgeConfigSpec config, String path) 
    {
        CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
    
    static 
    {
    	BUILDER = new ForgeConfigSpec.Builder();
    	TimerConfig.init(TimerConfig.BUILDER);
    	CONFIG = TimerConfig.BUILDER.build();
    }
	
    public static void init(ForgeConfigSpec.Builder config) 
    {
    	config.push("Timer Settings");
    	TimerConfig.disableTickrateLimit = config.comment("define whether remove tickrate limit of entities").define("disableTickrateLimit", false);
        config.pop();
    }
}
