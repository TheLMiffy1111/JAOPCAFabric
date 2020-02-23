package thelm.jaopca.api.fluids;

import net.minecraft.item.Item;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormBucketItem extends MaterialForm {

	default Item asItem() {
		return (Item)this;
	}
}
