package com.min01.entitytimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.entitytimer.TimerUtil;

import net.minecraft.client.DeltaTracker;

@Mixin(DeltaTracker.Timer.class)
public class MixinDeltaTrackerTimer 
{
	@Shadow
    private boolean paused;

	@Shadow
    private boolean frozen;
	
	@Unique
    private float pausedDeltaTickResidualEntity;
	
    @Inject(at = @At("HEAD"), method = "getGameTimeDeltaTicks", cancellable = true)
    private void getGameTimeDeltaTicks(CallbackInfoReturnable<Float> cir)
    {
		if(TimerUtil.isNotReplay())
		{
			cir.setReturnValue(TimerUtil.ENTITY_TIMER.partialTickEntity);
		}
    }
    
    @Inject(at = @At("HEAD"), method = "advanceGameTime", cancellable = true)
    private void advanceGameTime(long pTime, CallbackInfoReturnable<Integer> cir)
    {
    	if(TimerUtil.isNotReplay())
    	{
    		cir.setReturnValue(TimerUtil.ENTITY_TIMER.advanceTimeEntity(pTime));
    	}
    }

    @Inject(at = @At("HEAD"), method = "getGameTimeDeltaPartialTick", cancellable = true)
    private void getGameTimeDeltaPartialTick(boolean pRunsNormally, CallbackInfoReturnable<Float> cir) 
    {
    	if(TimerUtil.isNotReplay())
    	{
    		cir.setReturnValue(this.getGameTimeDeltaPartialTickEntity(pRunsNormally));
    	}
    }

    @Inject(at = @At("HEAD"), method = "pause", cancellable = true)
    private void pause(CallbackInfo ci) 
    {
    	if(TimerUtil.isNotReplay())
    	{
    		ci.cancel();
            if(!this.paused)
            {
                this.pausedDeltaTickResidualEntity = TimerUtil.ENTITY_TIMER.partialTickEntity;
            }
            this.paused = true;
    	}
    }
    
    @Unique
    private float getGameTimeDeltaPartialTickEntity(boolean pRunsNormally) 
    {
        if(!pRunsNormally && this.frozen) 
        {
            return 1.0F;
        }
        else 
        {
            return this.paused ? this.pausedDeltaTickResidualEntity : TimerUtil.ENTITY_TIMER.partialTickEntity;
        }
    }
}
