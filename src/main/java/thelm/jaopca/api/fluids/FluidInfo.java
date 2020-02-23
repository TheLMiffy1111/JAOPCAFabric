package thelm.jaopca.api.fluids;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import thelm.jaopca.api.blocks.BlockProvider;
import thelm.jaopca.api.materialforms.MaterialFormInfo;

public interface FluidInfo extends MaterialFormInfo, FluidProvider, BlockProvider, ItemConvertible {

	MaterialFormFluid getMaterialFormFluid();

	MaterialFormFluidBlock getMaterialFormFluidBlock();

	MaterialFormBucketItem getMaterialFormBucketItem();

	default Fluid getFluid() {
		return getMaterialFormFluid().asFluid();
	}

	default Block getFluidBlock() {
		return getMaterialFormFluidBlock().asBlock();
	}

	default Item getBucketItem() {
		return getMaterialFormBucketItem().asItem();
	}

	@Override
	default MaterialFormFluid getMaterialForm() {
		return getMaterialFormFluid();
	}

	@Override
	default Fluid asFluid() {
		return getFluid();
	}

	@Override
	default Block asBlock() {
		return getFluidBlock();
	}

	@Override
	default Item asItem() {
		return getBucketItem();
	}
}
