package dev.sterner.malum.common.blockentity.crucible;

import dev.sterner.lodestone.systems.sound.LodestoneBlockEntitySoundInstance;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;

public class CrucibleSoundInstance extends LodestoneBlockEntitySoundInstance<SpiritCrucibleCoreBlockEntity> {
    public CrucibleSoundInstance(SpiritCrucibleCoreBlockEntity blockEntity, float volume, float pitch, Random randomSource) {
        super(blockEntity, MalumSoundRegistry.CRUCIBLE_LOOP, volume, pitch, randomSource);
        this.x = blockEntity.getPos().getX() + 0.5f;
        this.y = blockEntity.getPos().getY() + 0.5f;
        this.z = blockEntity.getPos().getZ() + 0.5f;
    }

    @Override
    public void tick() {
        if (blockEntity.focusingRecipe != null || blockEntity.repairRecipe != null) {
            super.tick();
            return;
        }
        setDone();
    }

    public static void playSound(SpiritCrucibleCoreBlockEntity tileEntity) {
        MinecraftClient.getInstance().getSoundManager().playNextTick(new CrucibleSoundInstance(tileEntity, 1, 1, Random.create()));
    }
}
