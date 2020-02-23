package thelm.jaopca.api.blocks;

import net.minecraft.block.Block;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormBlock extends MaterialForm {

	default Block asBlock() {
		return (Block)this;
	}
}
