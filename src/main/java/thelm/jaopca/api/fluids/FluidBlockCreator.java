package thelm.jaopca.api.fluids;

public interface FluidBlockCreator {

	MaterialFormFluidBlock create(MaterialFormFluid fluid, FluidFormSettings settings);
}
