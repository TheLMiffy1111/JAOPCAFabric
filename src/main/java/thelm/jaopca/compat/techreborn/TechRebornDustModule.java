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

public class TechRebornDustModule implements Module {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"bauxite", "cinnabar", "galena", "pyrite", "redstone", "sodalite", "sphalerite"));

	private Map<IMaterial, DynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "techreborn_dust";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(1, "dust");
		builder.put(2, "dust");
		builder.put(2, "small_dust");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
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
			Identifier materialLocation = miscHelper.getTagIdentifier(material.getType().getFormName(), material.getName());
			Identifier ex1DustLocation = miscHelper.getTagIdentifier(dust, material.getExtra(1).getName());
			Identifier ex2DustLocation = miscHelper.getTagIdentifier(dust, material.getExtra(2).getName());
			Identifier ex2SmallDustLocation = miscHelper.getTagIdentifier(smallDust, material.getExtra(2).getName());

			DynamicSpecConfig config = configs.get(material);

			helper.registerGrinderRecipe(new Identifier("jaopca", "techreborn.ore_to_material."+material.getName()),
					oreLocation, 1, materialLocation, 2, 2, 220);

			//Based on sphalerite
			Object[] outputsWater = {
					materialLocation, 5,
					ex1DustLocation, 1
			};
			if(material.hasExtra(2)) {
				outputsWater = ArrayUtils.addAll(outputsWater, ex2SmallDustLocation, 1);
			}
			helper.registerIndustrialGrinderRecipe(new Identifier("jaopca", "techreborn.ore_with_water."+material.getName()),
					oreLocation, 1, Fluids.WATER, 1000, 64, 100, outputsWater);

			boolean persulfate = config.getDefinedBoolean("techreborn.persulfate", true, "Should this material be processed with persulfate.");

			if(persulfate) {
				Object[] outputsPersulfate = {
						materialLocation, 5,
						ex1DustLocation, 3,
				};
				if(material.hasExtra(2)) {
					outputsPersulfate = ArrayUtils.addAll(outputsPersulfate, ex2DustLocation, 1);
				}
				helper.registerIndustrialGrinderRecipe(new Identifier("jaopca", "techreborn.ore_with_mercury."+material.getName()),
						oreLocation, 1, ModFluids.SODIUM_PERSULFATE, 1000, 64, 100, outputsPersulfate);
			}
		}
	}
}
