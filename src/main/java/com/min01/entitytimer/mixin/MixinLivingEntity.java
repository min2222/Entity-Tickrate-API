package com.min01.entitytimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.entitytimer.EntityTimer;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity
{
	@Inject(at = @At(value = "HEAD"), method = "addAdditionalSaveData")
	private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
	{
		LivingEntity living = LivingEntity.class.cast(this);
		if(TimerUtil.hasTimer(living))
		{
			EntityTimer timer = TimerUtil.getTimer(living);
			tag.putFloat(TimerUtil.TICKRATE, timer.tickrate);
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "readAdditionalSaveData")
	private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
	{
		if(tag.contains(TimerUtil.TICKRATE))
		{
			LivingEntity living = LivingEntity.class.cast(this);
			TimerUtil.setTickrate(living, tag.getFloat(TimerUtil.TICKRATE));
		}
	}
}