package thelm.jaopca.blocks;

import thelm.jaopca.api.blocks.BlockInfo;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;

public class BlockInfoImpl implements BlockInfo {

	private final MaterialFormBlock block;
	private final MaterialFormBlockItem blockItem;

	BlockInfoImpl(MaterialFormBlock block, MaterialFormBlockItem blockItem) {
		this.block = block;
		this.blockItem = blockItem;
	}

	@Override
	public MaterialFormBlock getMaterialFormBlock() {
		return block;
	}

	@Override
	public MaterialFormBlockItem getMaterialFormBlockItem() {
		return blockItem;
	}
}
