package com.min01.entitytimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.min01.entitytimer.EntityTimer;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"), method = "renderLevel")
	private void setup(Camera instance, BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_)
	{
		if(TimerUtil.hasClientTimer(p_90577_))
		{
			EntityTimer timer = TimerUtil.getClientTimer(p_90577_);
			float partialTick = timer.partialTickEntity;
			instance.setup(p_90576_, p_90577_, p_90578_, p_90579_, partialTick);
		}
		else
		{
			instance.setup(p_90576_, p_90577_, p_90578_, p_90579_, p_90580_);
		}
	}
}
