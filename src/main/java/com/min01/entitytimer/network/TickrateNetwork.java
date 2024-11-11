package com.min01.entitytimer.network;

import com.min01.entitytimer.EntityTickrateAPI;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EntityTickrateAPI.MODID, bus = Bus.MOD)
public class TickrateNetwork
{
	@SubscribeEvent
	public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) 
	{
	    PayloadRegistrar registrar = event.registrar("1");
	    registrar.playToClient(EntityTimerSyncPacket.TYPE, EntityTimerSyncPacket.STREAM_CODEC, EntityTimerSyncPacket::handle);
	}
	
    public static void sendToAll(CustomPacketPayload message) 
    {
    	PacketDistributor.sendToAllPlayers(message);
    }
}
