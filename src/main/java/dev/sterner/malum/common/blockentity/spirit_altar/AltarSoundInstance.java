package dev.sterner.malum.common.blockentity.spirit_altar;

import dev.sterner.lodestone.systems.sound.LodestoneBlockEntitySoundInstance;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;

public class AltarSoundInstance extends LodestoneBlockEntitySoundInstance<SpiritAltarBlockEntity> {
    public AltarSoundInstance(SpiritAltarBlockEntity blockEntity, float volume, float pitch, Random randomSource) {
        super(blockEntity, MalumSoundRegistry.ALTAR_LOOP, volume, pitch, randomSource);
        this.x = blockEntity.getPos().getX() + 0.5f;
        this.y = blockEntity.getPos().getY() + 0.5f;
        this.z = blockEntity.getPos().getZ() + 0.5f;
        this.pitch = 0.8f;
    }

    @Override
    public void tick() {
        if (blockEntity.possibleRecipes.isEmpty()) {
            setDone();
        }
        super.tick();
    }

    public static void playSound(SpiritAltarBlockEntity tileEntity) {
        MinecraftClient.getInstance().getSoundManager().playNextTick(new AltarSoundInstance(tileEntity, 1, 1, tileEntity.getWorld().getRandom()));
    }
}
