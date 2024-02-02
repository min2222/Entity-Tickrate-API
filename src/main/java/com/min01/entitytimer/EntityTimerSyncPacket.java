package com.min01.entitytimer;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class EntityTimerSyncPacket 
{
	private final UUID uuid;
	private final float tickRate;
	private final boolean reset;
	
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
	
	public static class Handler 
	{
		public static boolean onMessage(EntityTimerSyncPacket message, Supplier<NetworkEvent.Context> ctx) 
		{
			ctx.get().enqueueWork(() ->
			{
				if(!message.reset)
				{
					if(TimerUtil.CLIENT_TIMER_MAP.containsKey(message.uuid))
					{
						TimerUtil.CLIENT_TIMER_MAP.remove(message.uuid);
					}
					TimerUtil.CLIENT_TIMER_MAP.put(message.uuid, new EntityTimer(message.tickRate, 0));
				}
				else
				{
					if(TimerUtil.CLIENT_TIMER_MAP.containsKey(message.uuid))
					{
						TimerUtil.CLIENT_TIMER_MAP.remove(message.uuid);
					}
				}
			});
			ctx.get().setPacketHandled(true);
			return true;
		}
	}
}
