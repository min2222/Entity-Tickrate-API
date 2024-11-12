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
import net.minecraft.client.Minecraft;

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
    	Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.player != null && TimerUtil.hasClientTimer(minecraft.player))
		{
			cir.setReturnValue(TimerUtil.getClientTimer(minecraft.player).tickDeltaEntity);
		}
    }
    
    @Inject(at = @At("HEAD"), method = "advanceGameTime", cancellable = true)
    private void advanceGameTime(long pTime, CallbackInfoReturnable<Integer> cir)
    {
    	Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.player != null && TimerUtil.hasClientTimer(minecraft.player))
		{
    		cir.setReturnValue(TimerUtil.getClientTimer(minecraft.player).advanceTimeEntity(pTime));
		}
    }

    @Inject(at = @At("HEAD"), method = "getGameTimeDeltaPartialTick", cancellable = true)
    private void getGameTimeDeltaPartialTick(boolean pRunsNormally, CallbackInfoReturnable<Float> cir) 
    {
    	Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.player != null && TimerUtil.hasClientTimer(minecraft.player))
		{
    		cir.setReturnValue(this.getGameTimeDeltaPartialTickEntity(pRunsNormally));
    	}
    }

    @Inject(at = @At("HEAD"), method = "pause", cancellable = true)
    private void pause(CallbackInfo ci) 
    {
    	Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.player != null && TimerUtil.hasClientTimer(minecraft.player))
		{
    		ci.cancel();
            if(!this.paused)
            {
                this.pausedDeltaTickResidualEntity = TimerUtil.getClientTimer(minecraft.player).partialTickEntity;
            }
            this.paused = true;
    	}
    }
    
    @Unique
    private float getGameTimeDeltaPartialTickEntity(boolean pRunsNormally) 
    {
    	Minecraft minecraft = Minecraft.getInstance();
        if(!pRunsNormally && this.frozen) 
        {
            return 1.0F;
        }
        else 
        {
            return this.paused ? this.pausedDeltaTickResidualEntity : TimerUtil.getClientTimer(minecraft.player).partialTickEntity;
        }
    }
}
