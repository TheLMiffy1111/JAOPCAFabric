package thelm.jaopca.fluids;

import thelm.jaopca.api.fluids.FluidInfo;
import thelm.jaopca.api.fluids.MaterialFormBucketItem;
import thelm.jaopca.api.fluids.MaterialFormFluid;
import thelm.jaopca.api.fluids.MaterialFormFluidBlock;

public class FluidInfoImpl implements FluidInfo {

	private final MaterialFormFluid fluid;
	private final MaterialFormFluidBlock fluidBlock;
	private final MaterialFormBucketItem bucketItem;

	FluidInfoImpl(MaterialFormFluid fluid, MaterialFormFluidBlock fluidBlock, MaterialFormBucketItem bucketItem) {
		this.fluid = fluid;
		this.fluidBlock = fluidBlock;
		this.bucketItem = bucketItem;
	}

	@Override
	public MaterialFormFluid getMaterialFormFluid() {
		return fluid;
	}

	@Override
	public MaterialFormFluidBlock getMaterialFormFluidBlock() {
		return fluidBlock;
	}

	@Override
	public MaterialFormBucketItem getMaterialFormBucketItem() {
		return bucketItem;
	}
}
