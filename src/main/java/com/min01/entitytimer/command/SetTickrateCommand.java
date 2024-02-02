package com.min01.entitytimer.command;

import java.util.Collection;

import com.min01.entitytimer.TimerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class SetTickrateCommand 
{
	public static void register(CommandDispatcher<CommandSourceStack> p_214446_)
	{
		p_214446_.register(Commands.literal("setTickrate").requires((p_137777_) -> 
		{
			return p_137777_.hasPermission(2);
		}).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("tickRate", FloatArgumentType.floatArg()).executes((p_137810_) ->
		{
			return setTickrate(p_137810_.getSource(), EntityArgument.getEntities(p_137810_, "targets"), FloatArgumentType.getFloat(p_137810_, "tickRate"));
		}))).then(Commands.argument("targets", EntityArgument.entities()).executes((p_137810_) ->
		{
			return resetTickrate(p_137810_.getSource(), EntityArgument.getEntities(p_137810_, "targets"));
		})));
	}
	
	private static int resetTickrate(CommandSourceStack p_137814_, Collection<? extends Entity> p_137815_) 
	{
		for(Entity entity : p_137815_) 
		{
			TimerUtil.resetTickrate(entity);
			p_137814_.sendSuccess(Component.literal("Reseted Tickrate of " + entity.getDisplayName().getString()), true);
		}
		return p_137815_.size();
	}
	
	private static int setTickrate(CommandSourceStack p_137814_, Collection<? extends Entity> p_137815_, float tickRate) 
	{
		for(Entity entity : p_137815_) 
		{
			TimerUtil.setTickrate(entity, tickRate);
			p_137814_.sendSuccess(Component.literal("Changed Tickrate of " + entity.getDisplayName().getString() + " to " + tickRate), true);
		}
		return p_137815_.size();
	}
}
