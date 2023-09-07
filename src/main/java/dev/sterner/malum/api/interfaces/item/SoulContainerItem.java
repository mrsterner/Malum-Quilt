package dev.sterner.malum.api.interfaces.item;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.sterner.malum.common.entity.spirit.SoulEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public interface SoulContainerItem {

    TypedActionResult<ItemStack> interactWithSoul(PlayerEntity player, Hand hand, SoulEntity soul);

    default TypedActionResult<ItemStack> fetchSoul(PlayerEntity player, Hand hand) {

        ArrayList<SoulEntity> entities = new ArrayList<>(player.getWorld().getNonSpectatingEntities(SoulEntity.class, player.getBoundingBox().expand(player.getAttributeValue(ReachEntityAttributes.REACH)*0.4f)));
        double biggestAngle = 0;
        SoulEntity chosenEntity = null;
        for (SoulEntity entity : entities) {
            Vec3d lookDirection = player.getRotationVector();
            Vec3d directionToEntity = entity.getPos().subtract(player.getPos()).normalize();
            double angle = lookDirection.dotProduct(directionToEntity);
            if (angle > biggestAngle && angle > 0.1f) {
                biggestAngle = angle;
                chosenEntity = entity;
            }
        }
        if (chosenEntity != null)
        {
            return interactWithSoul(player, hand, chosenEntity);
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }


}
