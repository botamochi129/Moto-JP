package jp.mtjp.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RX7Entity extends CarEntity {

    public RX7Entity(EntityType<? extends RX7Entity> type, World world) {
        super(type, world);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            if (player.isSneaking()) {
                leftDoorOpen = !leftDoorOpen;
            } else {
                rightDoorOpen = !rightDoorOpen;
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void initDataTracker() {

    }

    // --- NBTデータ同期用 ---
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        leftDoorOpen = nbt.getBoolean("LeftDoorOpen");
        rightDoorOpen = nbt.getBoolean("RightDoorOpen");
        wheelRotation = nbt.getFloat("WheelRotation");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("LeftDoorOpen", leftDoorOpen);
        nbt.putBoolean("RightDoorOpen", rightDoorOpen);
        nbt.putFloat("WheelRotation", wheelRotation);
    }

    // --- スポーンパケット ---
    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
