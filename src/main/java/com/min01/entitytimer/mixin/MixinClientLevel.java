package com.min01.entitytimer.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.entitytimer.TimerUtil;
import com.min01.entitytimer.config.TimerConfig;

import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level
{
	@Shadow
	@Final EntityTickList tickingEntities;
	
	protected MixinClientLevel(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) 
	{
		super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
	}

	@SuppressWarnings("deprecation")
	@Inject(at = @At("HEAD"), method = "tickNonPassenger", cancellable = true)
	private void tickNonPassenger(Entity p_104640_, CallbackInfo ci) 
	{
		if(TimerUtil.isNotReplay() && TimerUtil.hasClientTimer(p_104640_))
		{
			ci.cancel();
			this.getProfiler().push("tickEntities");
			int j = TimerUtil.getClientTimer(p_104640_).advanceTimeEntity(Util.getMillis());
			int i = TimerConfig.disableTickrateLimit.get() ? j : Math.min(10, j);
			for(int k = 0; k < i; ++k)
			{
				this.getProfiler().incrementCounter("entityTick");
				p_104640_.setOldPosAndRot();
				++p_104640_.tickCount;
				this.getProfiler().push(() -> 
				{
					return Registry.ENTITY_TYPE.getKey(p_104640_.getType()).toString();
				});
				if (p_104640_.canUpdate())
				{
					p_104640_.tick();
				}
				this.getProfiler().pop();

				for(Entity entity : p_104640_.getPassengers())
				{
					this.tickPassenger(p_104640_, entity);
				}
			}
			this.getProfiler().pop();
		}
	}
	
	@Shadow
	private void tickPassenger(Entity p_104642_, Entity p_104643_) 
	{
		   
	}
}
