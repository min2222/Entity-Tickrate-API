package com.min01.entitytimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.entitytimer.EntityTimer;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity
{
	@Inject(at = @At(value = "HEAD"), method = "saveWithoutId")
	private void saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir)
	{
		if(TimerUtil.hasTimer(Entity.class.cast(this)))
		{
			EntityTimer timer = TimerUtil.getTimer(Entity.class.cast(this));
			tag.putFloat(TimerUtil.TICKRATE, timer.tickrate);
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "load")
	private void load(CompoundTag tag, CallbackInfo ci)
	{
		if(tag.contains(TimerUtil.TICKRATE))
		{
			TimerUtil.setTickrate(Entity.class.cast(this), tag.getFloat(TimerUtil.TICKRATE));
		}
	}
}
