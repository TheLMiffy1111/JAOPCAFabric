package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormItem extends MaterialForm {

	default Item asItem() {
		return (Item)this;
	}
}
