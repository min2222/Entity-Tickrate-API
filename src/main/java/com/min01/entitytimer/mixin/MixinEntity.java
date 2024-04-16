package com.min01.entitytimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.min01.entitytimer.EntityTimer;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity
{
	@Redirect(at = @At(value = "INVOKE", target = "addAdditionalSaveData"), method = "saveWithoutId")
	public void addAdditionalSaveData(Entity instance, CompoundTag tag)
	{
		if(TimerUtil.hasTimer(instance))
		{
			EntityTimer timer = TimerUtil.getTimer(instance);
			tag.putFloat(TimerUtil.TICKRATE, timer.tickrate);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "readAdditionalSaveData"), method = "load")
	public void readAdditionalSaveData(Entity instance, CompoundTag tag)
	{
		if(tag.contains(TimerUtil.TICKRATE))
		{
			TimerUtil.setTickrate(instance, tag.getFloat(TimerUtil.TICKRATE));
		}
	}
}
