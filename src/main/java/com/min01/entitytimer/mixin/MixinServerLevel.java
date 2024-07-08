package com.min01.entitytimer.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.entitytimer.TimerUtil;
import com.min01.entitytimer.config.TimerConfig;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level
{
	protected MixinServerLevel(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, RegistryAccess p_270200_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_)
	{
		super(p_220352_, p_220353_, p_270200_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
	}
	
	@Inject(at = @At("HEAD"), method = "addFreshEntity")
	private void addFreshEntity(Entity p_8837_, CallbackInfoReturnable<Boolean> ci)
	{
		MySecurityManager manager = new MySecurityManager();
		Entity entity1 = null;
		Entity entity2 = null;
		Class<?>[] ctx = manager.getContext();
		int i = 0;
		int i2 = 0;
		do
		{
			entity1 = ServerLevel.class.cast(this).getEntity(TimerUtil.ENTITY_MAP.get(ctx[i].hashCode()));
			i++;
		}
		while(entity1 == null && i < ctx.length);
		do
		{
			entity2 = ServerLevel.class.cast(this).getEntity(TimerUtil.ENTITY_MAP2.get(ctx[i2].hashCode()));
			i2++;
		}
		while(entity2 == null && i2 < ctx.length);
		Entity entity = entity1 != null ? entity1 : entity2;
		if(entity != null)
		{
			if(TimerUtil.hasTimer(entity))
			{
				TimerUtil.setTickrate(p_8837_, TimerUtil.getTimer(entity).tickrate);
			}
		}
	}
	
	private static class MySecurityManager extends SecurityManager
	{
		public Class<?>[] getContext()
		{
			return this.getClassContext();
		}
	}
	
	@Inject(at = @At("HEAD"), method = "tickNonPassenger", cancellable = true)
	private void tickNonPassenger(Entity p_8648_, CallbackInfo ci) 
	{
		if(TimerUtil.isNotReplay() && TimerUtil.hasTimer(p_8648_))
		{
			ci.cancel();
			int j = TimerUtil.getTimer(p_8648_).advanceTimeEntity(Util.getMillis());
			for(int k = 0; k < Math.min(TimerConfig.disableTickrateLimit.get() ? 500 : 10, j); ++k)
			{
				this.tickEntities(p_8648_);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Unique
	private void tickEntities(Entity p_8648_)
	{
		p_8648_.setOldPosAndRot();
		ProfilerFiller profilerfiller = this.getProfiler();
		++p_8648_.tickCount;
		this.getProfiler().push(() -> 
		{
			return BuiltInRegistries.ENTITY_TYPE.getKey(p_8648_.getType()).toString();
		});
		profilerfiller.incrementCounter("tickNonPassenger");
		p_8648_.tick();
		this.getProfiler().pop();

		for(Entity entity : p_8648_.getPassengers())
		{
			this.tickPassenger(p_8648_, entity);
		}
	}
	
	@Shadow
	private void tickPassenger(Entity p_104642_, Entity p_104643_) 
	{
		
	}
}
