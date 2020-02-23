package thelm.jaopca.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import thelm.jaopca.api.materialforms.MaterialForm;
import thelm.jaopca.api.materialforms.MaterialFormInfo;

public interface BlockInfo extends MaterialFormInfo, BlockProvider, ItemConvertible {

	MaterialFormBlock getMaterialFormBlock();

	MaterialFormBlockItem getMaterialFormBlockItem();

	default Block getBlock() {
		return getMaterialFormBlock().asBlock();
	}

	default BlockItem getBlockItem() {
		return getMaterialFormBlockItem().asBlockItem();
	}

	@Override
	default MaterialForm getMaterialForm() {
		return getMaterialFormBlock();
	}

	@Override
	default Item asItem() {
		return getBlockItem();
	}

	@Override
	default Block asBlock() {
		return getMaterialFormBlock().asBlock();
	}
}
