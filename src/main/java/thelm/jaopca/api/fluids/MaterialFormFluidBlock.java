package thelm.jaopca.api.fluids;

import net.minecraft.block.Block;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormFluidBlock extends MaterialForm {

	default Block asBlock() {
		return (Block)this;
	}
}
