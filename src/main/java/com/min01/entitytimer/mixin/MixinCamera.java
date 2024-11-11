package com.min01.entitytimer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.entitytimer.EntityTimer;
import com.min01.entitytimer.TimerUtil;

import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;

@Mixin(Camera.class)
public class MixinCamera 
{
	@Shadow
	private boolean initialized;

	@Shadow
	private BlockGetter level;

	@Shadow
	private Entity entity;

	@Shadow
	private boolean detached;

	@Shadow
	private float xRot;

	@Shadow
	private float yRot;

	@Shadow
	private float eyeHeight;

	@Shadow
	private float eyeHeightOld;

	@Shadow
	private float partialTickTime;
	
	@Shadow
    private float roll;

	@Inject(at = @At(value = "HEAD"), method = "setup", cancellable = true)
	private void setup(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_, CallbackInfo ci)
	{
		if(TimerUtil.hasClientTimer(p_90577_)) 
		{
			ci.cancel();
			EntityTimer timer = TimerUtil.getClientTimer(p_90577_);
			float partialTick = timer.partialTickEntity;
			this.setupOriginal(p_90576_, p_90577_, p_90578_, p_90579_, partialTick);
		}
	}

	@Unique
	private void setupOriginal(BlockGetter pLevel, Entity pEntity, boolean pDetached, boolean pThirdPersonReverse, float pPartialTick)
	{
		this.initialized = true;
		this.level = pLevel;
		this.entity = pEntity;
		this.detached = pDetached;
		this.partialTickTime = pPartialTick;
		var cameraSetup = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles(Camera.class.cast(this), pPartialTick, pEntity.getViewYRot(pPartialTick), pEntity.getViewXRot(pPartialTick), 0));
		this.setRotation(cameraSetup.getYaw(), cameraSetup.getPitch(), cameraSetup.getRoll());
		this.setPosition(Mth.lerp((double) pPartialTick, pEntity.xo, pEntity.getX()), Mth.lerp((double) pPartialTick, pEntity.yo, pEntity.getY()) + (double) Mth.lerp(pPartialTick, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double) pPartialTick, pEntity.zo, pEntity.getZ()));
		if(pDetached)
		{
			if(pThirdPersonReverse)
			{
				this.setRotation(this.yRot + 180.0F, -this.xRot, -this.roll);
			}

			float f = pEntity instanceof LivingEntity livingentity ? livingentity.getScale() : 1.0F;
			this.move(-this.getMaxZoom(net.neoforged.neoforge.client.ClientHooks.getDetachedCameraDistance(Camera.class.cast(this), pThirdPersonReverse, f, 4.0F) * f), 0.0F, 0.0F);
		} 
		else if(pEntity instanceof LivingEntity && ((LivingEntity) pEntity).isSleeping()) 
		{
			Direction direction = ((LivingEntity) pEntity).getBedOrientation();
			this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
			this.move(0.0F, 0.3F, 0.0F);
		}
	}

	@Shadow
	protected void move(float pZoom, float pDy, float pDx) 
	{

	}

	@Shadow
	private float getMaxZoom(float pMaxZoom) 
	{
		throw new IllegalStateException();
	}

	@Shadow
	protected void setRotation(float pYRot, float pXRot) 
	{

	}

	@Shadow
	protected void setRotation(float pYRot, float pXRot, float roll)
	{

	}

	@Shadow
	protected void setPosition(double pX, double pY, double pZ)
	{

	}
}