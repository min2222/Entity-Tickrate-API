package com.min01.entitytimer.network;

import com.min01.entitytimer.EntityTickrateAPI;
import com.min01.entitytimer.EntityTimerSyncPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TickrateNetwork
{
	public static int ID;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = 
    		NetworkRegistry.newSimpleChannel(new ResourceLocation(EntityTickrateAPI.MODID, "tickrate_channel"), 
    				() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	public static void registerMessages()
	{
		CHANNEL.registerMessage(ID++, EntityTimerSyncPacket.class, EntityTimerSyncPacket::encode, EntityTimerSyncPacket::new, EntityTimerSyncPacket.Handler::onMessage);
	}
	
    public static <MSG> void sendToAll(MSG message) 
    {
    	for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) 
    	{
    		CHANNEL.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    	}
    }
}
