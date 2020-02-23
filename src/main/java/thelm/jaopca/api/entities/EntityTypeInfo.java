package thelm.jaopca.api.entities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.SpawnEggItem;
import thelm.jaopca.api.materialforms.MaterialFormInfo;

public interface EntityTypeInfo extends MaterialFormInfo, ItemConvertible {

	MaterialFormEntityType<?> getEntityType();

	SpawnEggItem getSpawnEggItem();

	@Override
	default MaterialFormEntityType<?> getMaterialForm() {
		return getEntityType();
	}

	@Override
	default Item asItem() {
		return getSpawnEggItem();
	}
}
