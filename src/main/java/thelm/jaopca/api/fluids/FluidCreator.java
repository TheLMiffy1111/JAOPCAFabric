package thelm.jaopca.api.fluids;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;

public interface FluidCreator {

	MaterialFormFluid create(Form form, IMaterial material, FluidFormSettings settings);
}
