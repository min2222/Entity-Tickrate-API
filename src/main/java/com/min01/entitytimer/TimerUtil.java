package com.min01.entitytimer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.min01.entitytimer.command.SetTickrateCommand;
import com.min01.entitytimer.network.EntityTimerSyncPacket;
import com.min01.entitytimer.network.TickrateNetwork;
import com.replaymod.replay.ReplayModReplay;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = EntityTickrateAPI.MODID, bus = Bus.FORGE)
public class TimerUtil 
{
	private static final Map<UUID, EntityTimer> TIMER_MAP = new HashMap<>();
	private static final Map<UUID, EntityTimer> CLIENT_TIMER_MAP = new HashMap<>();
	public static final EntityTimer ENTITY_TIMER = new EntityTimer(20.0F, 0L);

	public static final String REPLAYMOD = "replaymod";
	public static final Map<Class<? extends Entity>, Object> ENTITY_MAP = new HashMap<>();
	
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
		ENTITY_MAP.put(event.getEntity().getClass(), event.getEntity());
	}
	
	public static boolean isNotReplay()
	{
		if(isModLoaded(REPLAYMOD))
		{
			if(ReplayModReplay.instance.getReplayHandler() == null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
    public static void setTickrate(Entity entity, float tickrate)
    {
    	if(!entity.level.isClientSide)
    	{
        	for(ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) 
        	{
        		TickrateNetwork.CHANNEL.sendTo(new EntityTimerSyncPacket(entity.getUUID(), tickrate, false), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        	}
        	
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
    	if(!entity.level.isClientSide)
    	{
        	for(ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) 
        	{
        		TickrateNetwork.CHANNEL.sendTo(new EntityTimerSyncPacket(entity.getUUID(), 0, true), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        	}
        	
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
	
	public static boolean isModLoaded(String modid)
	{
		return ModList.get().isLoaded(modid);
	}
}
