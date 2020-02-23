package thelm.jaopca.api.blocks;

import net.minecraft.loot.LootTable;

public interface BlockLootTableCreator {

	LootTable create(MaterialFormBlock block, BlockFormSettings settings);
}
