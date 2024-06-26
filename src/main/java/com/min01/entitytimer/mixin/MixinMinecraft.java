package com.min01.entitytimer.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.entitytimer.TimerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.event.ForgeEventFactory;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
	@Shadow
	private volatile boolean pause;

	@Shadow
	private float pausePartialTick;
	
	@Nullable
	@Shadow
	private ClientLevel level;
	
	@Inject(at = @At("HEAD"), method = "getFrameTime", cancellable = true)
	private void getFrameTime(CallbackInfoReturnable<Float> ci) 
	{
		if(TimerUtil.isNotReplay())
		{
			ci.setReturnValue(TimerUtil.ENTITY_TIMER.partialTickEntity);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getDeltaFrameTime", cancellable = true)
	private void getDeltaFrameTime(CallbackInfoReturnable<Float> ci) 
	{
		if(TimerUtil.isNotReplay()) 
		{
			ci.setReturnValue(TimerUtil.ENTITY_TIMER.tickDeltaEntity);
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Timer;advanceTime(J)I"), method = "runTick")
	public int advanceTime(Timer instance, long p_92526_)
	{
		if(TimerUtil.isNotReplay())
		{
			return TimerUtil.ENTITY_TIMER.advanceTimeEntity(p_92526_);
		}
		return instance.advanceTime(p_92526_);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"), method = "runTick")
	public void render(GameRenderer instance, float f1, long crashreport, boolean crashreportcategory)
	{
		if(TimerUtil.isNotReplay())
		{
			instance.render(this.pause ? this.pausePartialTick : TimerUtil.ENTITY_TIMER.partialTickEntity, crashreport, crashreportcategory);
		}
		else
		{
			instance.render(f1, crashreport, crashreportcategory);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickStart(F)V"), method = "runTick", remap = false)
	public void onRenderTickStart(float timer)
	{
		if(TimerUtil.isNotReplay())
		{
			ForgeEventFactory.onRenderTickStart(this.pause ? this.pausePartialTick : TimerUtil.ENTITY_TIMER.partialTickEntity);
		}
		else
		{
			ForgeEventFactory.onRenderTickStart(timer);
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickEnd(F)V"), method = "runTick", remap = false)
	public void onRenderTickEnd(float timer)
	{
		if(TimerUtil.isNotReplay())
		{
			ForgeEventFactory.onRenderTickEnd(this.pause ? this.pausePartialTick : TimerUtil.ENTITY_TIMER.partialTickEntity);
		}
		else
		{
			ForgeEventFactory.onRenderTickEnd(timer);
		}
	}
}
