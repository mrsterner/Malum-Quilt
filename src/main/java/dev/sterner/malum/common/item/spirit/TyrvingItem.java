package dev.sterner.malum.common.item.spirit;

import dev.sterner.lodestone.systems.item.tools.LodestoneSwordItem;
import dev.sterner.malum.common.network.packet.s2c.entity.MajorEntityEffectParticlePacket;
import dev.sterner.malum.common.registry.MalumDamageSourceRegistry;
import dev.sterner.malum.common.registry.MalumSoundRegistry;
import dev.sterner.malum.common.registry.MalumSpiritTypeRegistry;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;


public class TyrvingItem extends LodestoneSwordItem {
    public TyrvingItem(ToolMaterial material, float damage, float speed, Item.Settings settings) {
        super(material, (int) damage, speed, settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.getWorld() instanceof ServerWorld) {
			float spiritCount = SpiritHelper.getEntitySpiritCount(target) * 2f;
			if (target instanceof PlayerEntity) {
				spiritCount = 4 * Math.max(1, (1 + target.getArmor() / 12f) * (1 + (1 - 1 / (float)target.getArmor())) / 12f);
			}

			if (target.isAlive()) {
				target.timeUntilRegen = 0;
				target.damage(MalumDamageSourceRegistry.create(attacker.getWorld(), MalumDamageSourceRegistry.VOODOO, attacker), spiritCount);
			}
			attacker.getWorld().playSound(null, target.getBlockPos(), MalumSoundRegistry.VOID_SLASH, SoundCategory.PLAYERS, 1, 1f + target.getWorld().random.nextFloat() * 0.25f);
			PlayerLookup.tracking(target).forEach(track -> MajorEntityEffectParticlePacket.send(track, MalumSpiritTypeRegistry.ELDRITCH_SPIRIT.getColor(), target.getX(), target.getY() + target.getHeight() / 2, target.getZ()));
		}
        return super.postHit(stack, target, attacker);
    }
}
