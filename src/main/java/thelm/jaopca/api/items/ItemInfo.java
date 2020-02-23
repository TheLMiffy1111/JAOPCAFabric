package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import thelm.jaopca.api.materialforms.MaterialForm;
import thelm.jaopca.api.materialforms.MaterialFormInfo;

public interface ItemInfo extends MaterialFormInfo, ItemConvertible {

	MaterialFormItem getMaterialFormItem();

	@Override
	default Item asItem() {
		return getMaterialFormItem().asItem();
	}

	@Override
	default MaterialForm getMaterialForm() {
		return getMaterialFormItem();
	}
}
