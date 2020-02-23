package thelm.jaopca.modules.tags;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class VanillaModule implements Module {

	@Override
	public String getName() {
		return "vanilla";
	}

	@Override
	public void onMainConfigSetupComplete(ModuleData moduleData) {
		registerTags();
	}

	private void registerTags() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MiscHelper helper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();

		String formName = plural ? "ores" : "ore";
		api.registerItemTag(helper.getTagIdentifier(formName, "coal"), Items.COAL_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "diamond"), Items.DIAMOND_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "emerald"), Items.EMERALD_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "gold"), Items.GOLD_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "iron"), Items.IRON_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "lapis"), Items.LAPIS_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "quartz"), Items.NETHER_QUARTZ_ORE);
		api.registerItemTag(helper.getTagIdentifier(formName, "redstone"), Items.REDSTONE_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "coal"), Blocks.COAL_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "diamond"), Blocks.DIAMOND_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "emerald"), Blocks.EMERALD_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "gold"), Blocks.GOLD_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "iron"), Blocks.IRON_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "lapis"), Blocks.LAPIS_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "quartz"), Blocks.NETHER_QUARTZ_ORE);
		api.registerBlockTag(helper.getTagIdentifier(formName, "redstone"), Blocks.REDSTONE_ORE);

		formName = plural ? "ingots" : "ingot";
		api.registerItemTag(helper.getTagIdentifier(formName, "brick"), Items.BRICK);
		api.registerItemTag(helper.getTagIdentifier(formName, "gold"), Items.GOLD_INGOT);
		api.registerItemTag(helper.getTagIdentifier(formName, "iron"), Items.IRON_INGOT);
		api.registerItemTag(helper.getTagIdentifier(formName, "nether_brick"), Items.NETHER_BRICK);

		formName = plural ? "gems" : "gem";
		api.registerItemTag(helper.getTagIdentifier(formName, "diamond"), Items.DIAMOND);
		api.registerItemTag(helper.getTagIdentifier(formName, "emerald"), Items.EMERALD);
		api.registerItemTag(helper.getTagIdentifier(formName, "prismarine"), Items.PRISMARINE_SHARD);
		api.registerItemTag(helper.getTagIdentifier(formName, "quartz"), Items.QUARTZ);

		formName = plural ? "crystals" : "crystal";
		api.registerItemTag(helper.getTagIdentifier(formName, "coal"), Items.COAL);

		formName = plural ? "dusts" : "dust";
		api.registerItemTag(helper.getTagIdentifier(formName, "glowstone"), Items.GLOWSTONE_DUST);
		api.registerItemTag(helper.getTagIdentifier(formName, "prismarine"), Items.PRISMARINE_CRYSTALS);
		api.registerItemTag(helper.getTagIdentifier(formName, "redstone"), Items.REDSTONE);

		formName = plural ? "blocks" : "block";
		api.registerItemTag(helper.getTagIdentifier(formName, "coal"), Items.COAL_BLOCK);
		api.registerItemTag(helper.getTagIdentifier(formName, "diamond"), Items.DIAMOND_BLOCK);
		api.registerItemTag(helper.getTagIdentifier(formName, "emerald"), Items.EMERALD_BLOCK);
		api.registerItemTag(helper.getTagIdentifier(formName, "gold"), Items.GOLD_BLOCK);
		api.registerItemTag(helper.getTagIdentifier(formName, "iron"), Items.IRON_BLOCK);
		api.registerItemTag(helper.getTagIdentifier(formName, "lapis"), Items.LAPIS_BLOCK);
		api.registerItemTag(helper.getTagIdentifier(formName, "prismarine"), Items.PRISMARINE_BRICKS);
		api.registerItemTag(helper.getTagIdentifier(formName, "redstone"), Items.REDSTONE_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "coal"), Blocks.COAL_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "diamond"), Blocks.DIAMOND_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "emerald"), Blocks.EMERALD_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "gold"), Blocks.GOLD_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "iron"), Blocks.IRON_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "lapis"), Blocks.LAPIS_BLOCK);
		api.registerBlockTag(helper.getTagIdentifier(formName, "prismarine"), Blocks.PRISMARINE_BRICKS);
		api.registerBlockTag(helper.getTagIdentifier(formName, "redstone"), Blocks.REDSTONE_BLOCK);

		formName = plural ? "nuggets" : "nugget";
		api.registerItemTag(helper.getTagIdentifier(formName, "gold"), Items.GOLD_NUGGET);
		api.registerItemTag(helper.getTagIdentifier(formName, "iron"), Items.IRON_NUGGET);
	}
}
