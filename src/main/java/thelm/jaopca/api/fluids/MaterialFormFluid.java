package thelm.jaopca.api.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormFluid extends MaterialForm {

	default Fluid asFluid() {
		return (Fluid)this;
	}

	FluidState getSourceState();
}
