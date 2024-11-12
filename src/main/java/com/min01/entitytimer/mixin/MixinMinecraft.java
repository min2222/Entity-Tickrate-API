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
import net.minecraft.client.player.LocalPlayer;
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
	
	@Nullable
	@Shadow
	public LocalPlayer player;
	
	@Inject(at = @At("HEAD"), method = "getFrameTime", cancellable = true)
	private void getFrameTime(CallbackInfoReturnable<Float> ci) 
	{
		if(this.player != null && TimerUtil.hasClientTimer(this.player))
		{
			ci.setReturnValue(TimerUtil.getClientTimer(this.player).partialTickEntity);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getDeltaFrameTime", cancellable = true)
	private void getDeltaFrameTime(CallbackInfoReturnable<Float> ci) 
	{
		if(this.player != null && TimerUtil.hasClientTimer(this.player))
		{
			ci.setReturnValue(TimerUtil.getClientTimer(this.player).tickDeltaEntity);
		}
	}
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Timer;advanceTime(J)I"), method = "runTick")
	private int advanceTime(Timer instance, long p_92526_)
	{
		if(this.player != null)
		{
			if(TimerUtil.hasClientTimer(this.player))
			{
				return TimerUtil.getClientTimer(this.player).advanceTimeEntity(p_92526_);
			}
			else
			{
				return instance.advanceTime(p_92526_);
			}
		}
		else
		{
			return instance.advanceTime(p_92526_);
		}
	}
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"), method = "runTick")
	private void render(GameRenderer instance, float f1, long crashreport, boolean crashreportcategory)
	{
		if(this.player != null)
		{
			if(TimerUtil.hasClientTimer(this.player))
			{
				instance.render(this.pause ? this.pausePartialTick : TimerUtil.getClientTimer(this.player).partialTickEntity, crashreport, crashreportcategory);
			}
			else
			{
				instance.render(f1, crashreport, crashreportcategory);
			}
		}
		else
		{
			instance.render(f1, crashreport, crashreportcategory);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickStart(F)V"), method = "runTick", remap = false)
	private void onRenderTickStart(float timer)
	{
		if(this.player != null)
		{
			if(TimerUtil.hasClientTimer(this.player))
			{
				ForgeEventFactory.onRenderTickStart(this.pause ? this.pausePartialTick : TimerUtil.getClientTimer(this.player).partialTickEntity);
			}
			else
			{
				ForgeEventFactory.onRenderTickStart(timer);
			}
		}
		else
		{
			ForgeEventFactory.onRenderTickStart(timer);
		}
	}
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickEnd(F)V"), method = "runTick", remap = false)
	private void onRenderTickEnd(float timer)
	{
		if(this.player != null)
		{
			if(TimerUtil.hasClientTimer(this.player))
			{
				ForgeEventFactory.onRenderTickEnd(this.pause ? this.pausePartialTick : TimerUtil.getClientTimer(this.player).partialTickEntity);
			}
			else
			{
				ForgeEventFactory.onRenderTickEnd(timer);
			}
		}
		else
		{
			ForgeEventFactory.onRenderTickEnd(timer);
		}
	}
}