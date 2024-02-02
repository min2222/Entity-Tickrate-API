package com.min01.entitytimer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.min01.entitytimer.command.SetTickrateCommand;
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
	public static final Map<UUID, EntityTimer> TIMER_MAP = new HashMap<>();
	public static final Map<UUID, EntityTimer> CLIENT_TIMER_MAP = new HashMap<>();
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
        	
    		if(TIMER_MAP.containsKey(entity.getUUID()))
    		{
    			TIMER_MAP.remove(entity.getUUID());
    		}
    		
			TIMER_MAP.put(entity.getUUID(), new EntityTimer(tickrate, 0));
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
        	
    		if(TIMER_MAP.containsKey(entity.getUUID()))
    		{
    			TIMER_MAP.remove(entity.getUUID());
    		}
    	}
    }
	
	public static boolean isModLoaded(String modid)
	{
		return ModList.get().isLoaded(modid);
	}
}
