package com.min01.entitytimer.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.entitytimer.IEntityTicker;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@Mixin(Minecraft.class)
public class MixinMinecraft implements IEntityTicker
{
	@Shadow
	private @Final Timer timer;
	
	@Shadow
	private volatile boolean pause;

	@Nullable
	@Shadow
	public ClientLevel level;
	
	@Shadow
	private float pausePartialTick;
	
	int advanceTime;
	
	@Inject(at = @At("HEAD"), method = "getFrameTime", cancellable = true)
	private void getFrameTime(CallbackInfoReturnable<Float> ci) 
	{
		if(TimerUtil.isNotReplay())
		{
			ci.setReturnValue(TimerUtil.ENTITY_TIMER.partialTickEntity);
		}
		else
		{
			ci.setReturnValue(this.timer.partialTick);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getDeltaFrameTime", cancellable = true)
	private void getDeltaFrameTime(CallbackInfoReturnable<Float> ci) 
	{
		if(TimerUtil.isNotReplay()) 
		{
			ci.setReturnValue(TimerUtil.ENTITY_TIMER.tickDeltaEntity);
		}
		else
		{
			ci.setReturnValue(this.timer.tickDelta);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "runTick", cancellable = true)
	private void runTick(boolean p_91384_, CallbackInfo ci) 
	{
		if(TimerUtil.isNotReplay())
		{
			if (p_91384_) 
			{
				int j = TimerUtil.ENTITY_TIMER.advanceTimeEntity(Util.getMillis());
				this.advanceTime = j;
				
				for(int k = 0; k < Math.min(10, j); ++k) 
				{
					this.tick();
				}
				
				if(this.level != null)
				{
					if(!this.pause)
					{
						this.level.tickEntities();
					}
				}
			}
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;tick()V"), method = "runTick")
	public void tick(Minecraft instance)
	{
		if(!TimerUtil.isNotReplay())
		{
			instance.tick();
		}
	}
	
	@Shadow
	private void tick()
	{
		
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
			instance.render(this.pause ? this.pausePartialTick : this.timer.partialTick, crashreport, crashreportcategory);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickStart(F)V"), method = "runTick")
	public void onRenderTickStart(float timer)
	{
		if(TimerUtil.isNotReplay())
		{
			MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.START, this.pause ? this.pausePartialTick : TimerUtil.ENTITY_TIMER.partialTickEntity));
		}
		else
		{
			MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.START, this.pause ? this.pausePartialTick : this.timer.partialTick));
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickEnd(F)V"), method = "runTick")
	public void onRenderTickEnd(float timer)
	{
		if(TimerUtil.isNotReplay())
		{
			MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.END, this.pause ? this.pausePartialTick : TimerUtil.ENTITY_TIMER.partialTickEntity));
		}
		else
		{
			MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.END, this.pause ? this.pausePartialTick : this.timer.partialTick));
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickEntities()V"), method = "tick")
	public void tick(ClientLevel instance)
	{
		if(!TimerUtil.isNotReplay())
		{
			instance.tickEntities();
		}
	}

	@Override
	public int getAdvanceTime() 
	{
		return this.advanceTime;
	}
}
