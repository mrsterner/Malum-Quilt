package dev.sterner.malum.common.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.sterner.malum.common.spirit.MalumEntitySpiritData;
import dev.sterner.malum.common.spirit.SpiritHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpiritLivingEntityComponent implements AutoSyncedComponent {
    private final LivingEntity obj;

    private MalumEntitySpiritData spiritData;
	public List<ItemStack> soulsToApplyToDrops;

    public UUID killerUUID;

    public SpiritLivingEntityComponent(LivingEntity livingEntity) {
        obj = livingEntity;
        spiritData = SpiritHelper.getEntitySpiritData(obj);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (killerUUID != null) {
            tag.putUuid("killerUUID", killerUUID);
        }
		if (soulsToApplyToDrops != null) {
			NbtList souls = new NbtList();
			for (ItemStack soul : soulsToApplyToDrops) {
				souls.add(soul.serializeNBT());
			}
			tag.put("soulsToApplyToDrops", souls);
		}
        if(spiritData != null)
            spiritData.saveTo(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (tag.contains("killerUUID")) {
            killerUUID = tag.getUuid("killerUUID");
        } else {
            killerUUID = null;
        }
		if (tag.contains("soulsToApplyToDrops", NbtElement.LIST_TYPE)) {
			soulsToApplyToDrops = new ArrayList<>();
			NbtList souls = tag.getList("soulsToApplyToDrops", NbtElement.COMPOUND_TYPE);
			for (int i = 0; i < souls.size(); i++) {
				soulsToApplyToDrops.add(ItemStack.fromNbt(souls.getCompound(i)));
			}
		} else {
			soulsToApplyToDrops = null;
		}
        if(MalumEntitySpiritData.load(tag) != null)
            spiritData = MalumEntitySpiritData.load(tag);
    }
}
