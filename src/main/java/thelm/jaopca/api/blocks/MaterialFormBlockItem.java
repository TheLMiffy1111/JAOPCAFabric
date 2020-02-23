package thelm.jaopca.api.blocks;

import net.minecraft.item.BlockItem;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormBlockItem extends MaterialForm {

	default BlockItem asBlockItem() {
		return (BlockItem)this;
	}
}
