package com.min01.entitytimer.network;

import java.util.UUID;

import com.min01.entitytimer.EntityTickrateAPI;
import com.min01.entitytimer.EntityTimer;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EntityTimerSyncPacket implements CustomPacketPayload
{
	private final UUID uuid;
	private final float tickRate;
	private final boolean reset;
	
    public static final CustomPacketPayload.Type<EntityTimerSyncPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EntityTickrateAPI.MODID, "timer_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityTimerSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(EntityTimerSyncPacket::encode, EntityTimerSyncPacket::new);
	
	public EntityTimerSyncPacket(UUID uuid, float tickRate, boolean reset) 
	{
		this.uuid = uuid;
		this.tickRate = tickRate;
		this.reset = reset;
	}

	public EntityTimerSyncPacket(FriendlyByteBuf buf)
	{
		this.uuid = buf.readUUID();
		this.tickRate = buf.readFloat();
		this.reset = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.uuid);
		buf.writeFloat(this.tickRate);
		buf.writeBoolean(this.reset);
	}
	
	public static void handle(EntityTimerSyncPacket message, IPayloadContext ctx) 
	{
		ctx.enqueueWork(() ->
		{
			if(!message.reset)
			{
				if(!TimerUtil.hasClientTimer(message.uuid))
				{
					TimerUtil.setClientTimer(message.uuid, message.tickRate);
				}
				else
				{
					EntityTimer timer = TimerUtil.getClientTimer(message.uuid);
					if(timer.tickrate != message.tickRate)
					{
						TimerUtil.setClientTimer(message.uuid, message.tickRate);
					}
				}
			}
			else
			{
				if(TimerUtil.hasClientTimer(message.uuid))
				{
					TimerUtil.removeClientTimer(message.uuid);
				}
			}
		});
	}
	
    @Override
    public Type<? extends CustomPacketPayload> type() 
    {
        return TYPE;
    }
}
