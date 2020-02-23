package thelm.jaopca.api.fluids;

public interface BucketItemCreator {

	MaterialFormBucketItem create(MaterialFormFluid fluid, FluidFormSettings settings);
}
