package com.min01.entitytimer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.min01.entitytimer.command.SetTickrateCommand;
import com.min01.entitytimer.network.EntityTimerSyncPacket;
import com.min01.entitytimer.network.TickrateNetwork;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@EventBusSubscriber(modid = EntityTickrateAPI.MODID, bus = Bus.GAME)
public class TimerUtil 
{
	private static final Map<UUID, EntityTimer> TIMER_MAP = new HashMap<>();
	private static final Map<UUID, EntityTimer> CLIENT_TIMER_MAP = new HashMap<>();

	public static final String TICKRATE = "Tickrate";
	public static final Map<Integer, UUID> ENTITY_MAP = new HashMap<>();
	public static final Map<Integer, UUID> ENTITY_MAP2 = new HashMap<>();
	
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
    	SetTickrateCommand.register(event.getDispatcher());
    }
    
	@SubscribeEvent
	public static void onEntityLeaveLevel(EntityLeaveLevelEvent event)
	{
		TimerUtil.resetTickrate(event.getEntity());
	}
	
	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event)
	{
		ENTITY_MAP.put(event.getEntity().getClass().hashCode(), event.getEntity().getUUID());
		for(Class<?> clazz : event.getEntity().getClass().getDeclaredClasses())
		{
			ENTITY_MAP2.put(clazz.hashCode(), event.getEntity().getUUID());
		}
	}
	
	public static boolean isNotReplay()
	{
		return true;
	}
	
    public static void setTickrate(Entity entity, float tickrate)
    {
    	Level level = entity.level();
    	if(!level.isClientSide)
    	{
    		TickrateNetwork.sendToAll(new EntityTimerSyncPacket(entity.getUUID(), tickrate, false));
        	
			if(!hasTimer(entity))
			{
				setTimer(entity, tickrate);
			}
			else
			{
				EntityTimer timer = getTimer(entity);
				if(timer.tickrate != tickrate)
				{
					setTimer(entity, tickrate);
				}
			}
    	}
    }
    
    public static void resetTickrate(Entity entity)
    {
    	Level level = entity.level();
    	if(!level.isClientSide)
    	{
    		TickrateNetwork.sendToAll(new EntityTimerSyncPacket(entity.getUUID(), 0, true));
        	
    		if(hasTimer(entity))
    		{
    			removeTimer(entity);
    		}
    	}
    }
    
    public static void removeTimer(Entity entity)
    {
    	removeTimer(entity.getUUID());
    }
    
    public static void removeTimer(UUID uuid)
    {
		TIMER_MAP.remove(uuid);
    }
    
    public static void removeClientTimer(Entity entity)
    {
    	removeClientTimer(entity.getUUID());
    }
    
    public static void removeClientTimer(UUID uuid)
    {
		CLIENT_TIMER_MAP.remove(uuid);
    }
    
    public static void setTimer(Entity entity, float tickrate)
    {
    	setTimer(entity.getUUID(), tickrate);
    }
    
    public static void setTimer(UUID uuid, float tickrate)
    {
		TIMER_MAP.put(uuid, new EntityTimer(tickrate, 0));
    }
    
    public static void setClientTimer(Entity entity, float tickrate)
    {
    	setClientTimer(entity.getUUID(), tickrate);
    }
    
    public static void setClientTimer(UUID uuid, float tickrate)
    {
    	CLIENT_TIMER_MAP.put(uuid, new EntityTimer(tickrate, 0));
    }
    
    public static EntityTimer getClientTimer(Entity entity)
    {
    	return getClientTimer(entity.getUUID());
    }
    
    public static EntityTimer getClientTimer(UUID uuid)
    {
    	return CLIENT_TIMER_MAP.get(uuid);
    }
    
    public static EntityTimer getTimer(Entity entity)
    {
    	return getTimer(entity.getUUID());
    }
    
    public static EntityTimer getTimer(UUID uuid)
    {
    	return TIMER_MAP.get(uuid);
    }
    
    public static boolean hasClientTimer(Entity entity)
    {
    	return hasClientTimer(entity.getUUID());
    }
    
    public static boolean hasClientTimer(UUID uuid)
    {
    	return CLIENT_TIMER_MAP.containsKey(uuid);
    }
    
    public static boolean hasTimer(Entity entity)
    {
    	return hasTimer(entity.getUUID());
    }
    
    public static boolean hasTimer(UUID uuid)
    {
    	return TIMER_MAP.containsKey(uuid);
    }
}
