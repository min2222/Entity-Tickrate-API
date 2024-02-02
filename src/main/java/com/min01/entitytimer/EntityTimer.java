package com.min01.entitytimer;

public class EntityTimer
{
	public float tickDeltaEntity;
	private float msPerTickEntity;
	public float partialTickEntity;
	private long lastMsEntity;
	public float tickrate;

	public EntityTimer(float p_92523_, long p_92524_)
	{
		this.tickrate = p_92523_;
		this.msPerTickEntity = 1000.0F / p_92523_;
		this.lastMsEntity = p_92524_;
	}

	public int advanceTimeEntity(long p_92526_) 
	{
		this.tickDeltaEntity = (float)(p_92526_ - this.lastMsEntity) / this.msPerTickEntity;
		this.lastMsEntity = p_92526_;
		this.partialTickEntity += this.tickDeltaEntity;
		int i = (int)this.partialTickEntity;
		this.partialTickEntity -= (float)i;
		return i;
	}
}
