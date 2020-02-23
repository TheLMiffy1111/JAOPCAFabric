package thelm.jaopca.compat.techreborn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import techreborn.init.ModFluids;
import techreborn.init.TRContent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.DynamicSpecConfig;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class TechRebornModule implements Module {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iridium", "iron", "lead", "platinum", "silver", "tin", "tungsten"));

	private Map<IMaterial, DynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "techreborn";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		builder.put(1, "dust");
		builder.put(1, "small_dust");
		builder.put(2, "dust");
		builder.put(2, "small_dust");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onMainConfigSetupComplete(ModuleData moduleData) {
		registerTags();
	}

	@Override
	public void defineMaterialConfig(ModuleData moduleData, Map<IMaterial, DynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		MiscHelper miscHelper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();
		String ore = plural ? "ores" : "ore";
		String dust = plural ? "dusts" : "dust";
		String smallDust = plural ? "small_dusts" : "small_dust";
		for(IMaterial material : moduleData.getMaterials()) {
			Identifier oreLocation = miscHelper.getTagIdentifier(ore, material.getName());
			Identifier dustLocation = miscHelper.getTagIdentifier(dust, material.getName());
			Identifier ex1DustLocation = miscHelper.getTagIdentifier(dust, material.getExtra(1).getName());
			Identifier ex1SmallDustLocation = miscHelper.getTagIdentifier(smallDust, material.getExtra(1).getName());
			Identifier ex2DustLocation = miscHelper.getTagIdentifier(dust, material.getExtra(2).getName());
			Identifier ex2SmallDustLocation = miscHelper.getTagIdentifier(smallDust, material.getExtra(2).getName());

			DynamicSpecConfig config = configs.get(material);

			helper.registerGrinderRecipe(new Identifier("jaopca", "techreborn.ore_to_dust."+material.getName()),
					oreLocation, 1, dustLocation, 2, 2, 270);

			Object[] outputsWater = {
					dustLocation, 2,
					ex1SmallDustLocation, 1
			};
			if(material.hasExtra(2)) {
				outputsWater = ArrayUtils.addAll(outputsWater, ex2SmallDustLocation, 1);
			}
			helper.registerIndustrialGrinderRecipe(new Identifier("jaopca", "techreborn.ore_with_water."+material.getName()),
					oreLocation, 1, Fluids.WATER, 1000, 64, 100, outputsWater);

			//The following is based on copper and gold
			boolean mercury = config.getDefinedBoolean("techreborn.mercury", true, "Should this material be processed with mercury.");
			boolean persulfate = config.getDefinedBoolean("techreborn.persulfate", true, "Should this material be processed with persulfate.");

			if(mercury) {
				Object[] outputsMercury = {
						dustLocation, 3,
						ex1SmallDustLocation, 1
				};
				if(material.hasExtra(2)) {
					outputsMercury = ArrayUtils.addAll(outputsMercury, ex2SmallDustLocation, 1);
				}
				helper.registerIndustrialGrinderRecipe(new Identifier("jaopca", "techreborn.ore_with_mercury."+material.getName()),
						oreLocation, 1, ModFluids.MERCURY, 1000, 64, 100, outputsMercury);
			}

			if(persulfate) {
				Object[] outputsPersulfate = {
						dustLocation, 2,
						ex1DustLocation, 1
				};
				if(material.hasExtra(2)) {
					outputsPersulfate = ArrayUtils.addAll(outputsPersulfate, ex2DustLocation, 1);
				}
				helper.registerIndustrialGrinderRecipe(new Identifier("jaopca", "techreborn.ore_with_persulfate"+material.getName()),
						oreLocation, 1, ModFluids.SODIUM_PERSULFATE, 1000, 64, 100, outputsPersulfate);
			}
		}
	}

	private void registerTags() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MiscHelper helper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();

		//Please tag everything thanks
		String formName = plural ? "ores" : "ore";
		for(TRContent.Ores ore : TRContent.Ores.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, ore.name),
					ore.asItem());
		}
		api.registerItemTag(helper.getTagIdentifier(formName, "platinum"), TRContent.Ores.SHELDONITE.asItem());

		formName = plural ? "blocks" : "block";
		for(TRContent.StorageBlocks block : TRContent.StorageBlocks.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, block.name), block.asItem());
		}

		formName = plural ? "dusts" : "dust";
		for(TRContent.Dusts dust : TRContent.Dusts.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, dust.name), dust.asItem());
		}

		formName = plural ? "small_dusts" : "small_dust";
		for(TRContent.SmallDusts smallDust : TRContent.SmallDusts.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, smallDust.name), smallDust.asItem());
		}

		formName = plural ? "gems" : "gem";
		for(TRContent.Gems gem : TRContent.Gems.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, gem.name), gem.asItem());
		}

		formName = plural ? "ingots" : "ingot";
		for(TRContent.Ingots ingot : TRContent.Ingots.values()) {
			if(ingot == TRContent.Ingots.HOT_TUNGSTENSTEEL) {
				continue;
			}
			api.registerItemTag(helper.getTagIdentifier(formName, ingot.name), ingot.asItem());
		}

		formName = plural ? "hot_ingots" : "hot_ingot";
		api.registerItemTag(helper.getTagIdentifier(formName, "tungstensteel"), TRContent.Ingots.HOT_TUNGSTENSTEEL.asItem());

		formName = plural ? "nuggets" : "nugget";
		for(TRContent.Nuggets nugget : TRContent.Nuggets.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, nugget.name), nugget.asItem());
		}

		formName = plural ? "plates" : "plate";
		for(TRContent.Plates plate : TRContent.Plates.values()) {
			api.registerItemTag(helper.getTagIdentifier(formName, plate.name), plate.asItem());
		}
	}
}
