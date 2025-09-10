package jp.mtjp.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class CarEntity extends Entity {
    protected boolean leftDoorOpen = false;
    protected boolean rightDoorOpen = false;

    public float leftDoorProgress = 0.0f;
    public float rightDoorProgress = 0.0f;

    protected float wheelRotation = 0.0f;

    public CarEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        leftDoorProgress = updateProgress(leftDoorOpen, leftDoorProgress);
        rightDoorProgress = updateProgress(rightDoorOpen, rightDoorProgress);
        wheelRotation += 5f; // 仮: 回転アニメーション
    }

    private float updateProgress(boolean open, float progress) {
        float speed = 0.05f;
        if (open && progress < 1f) progress += speed;
        if (!open && progress > 0f) progress -= speed;
        return Math.max(0f, Math.min(1f, progress));
    }

    public boolean isDoorLeftOpen() { return leftDoorOpen; }
    public boolean isDoorRightOpen() { return rightDoorOpen; }
    public float getWheelRotation() { return wheelRotation; }
}
