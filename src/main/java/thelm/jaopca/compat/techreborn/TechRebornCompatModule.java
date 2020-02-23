package thelm.jaopca.compat.techreborn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import techreborn.init.TRContent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class TechRebornCompatModule implements Module {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "brass", "bronze", "charcoal", "chrome", "clay", "coal", "copper", "diamond", "emerald",
			"ender_eye", "ender_pearl", "gold", "invar", "iron", "lead", "nickel", "peridot", "platinum", "prismarine",
			"quartz", "red_garnet", "ruby", "sapphrie", "silver", "steel", "tin", "titanium", "tungsten",
			"yellow_garnet", "zinc"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"charcoal", "coal", "diamond", "emerald", "lapis", "peridot", "prismarine", "quartz", "red_garnet", "ruby",
			"sapphire", "yellow_garnet"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"advanced_alloy", "aluminum", "brass", "bronze", "carbon", "chrome", "coal", "copper", "diamond",
			"electrum", "emerald", "gold", "invar", "iridium", "iron", "lapis", "lazurite", "lead", "nickel",
			"obsidian", "peridot", "platinum", "quartz", "red_garnet", "redstone", "refined_iron", "ruby", "sapphire",
			"silver", "steel", "tin", "titanium", "tungsten", "tungstensteel", "yellow_garnet", "zinc"));
	private static final Set<String> BLOCK_TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "brass", "bronze", "carbon", "chrome", "coal", "copper", "diamond", "electrum", "emerald",
			"gold", "invar", "iridium", "iron", "lapis", "lead", "nickel", "obsidian", "peridot", "platinum", "quartz",
			"red_garnet", "redstone", "refined_iron", "ruby", "sapphire", "silver", "steel", "tin", "titanium",
			"tungsten", "tungstensteel", "yellow_garnet", "zinc"));

	@Override
	public String getName() {
		return "techreborn_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		MiscHelper miscHelper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();
		String dust = plural ? "dusts" : "dust";
		String plate = plural ? "plates" : "plate";
		String block = plural ? "blocks" : "block";
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_DUST_BLACKLIST.contains(material.getName())) {
				Identifier materialLocation = miscHelper.getTagIdentifier(material.getType().getFormName(), material.getName());
				Identifier dustLocation = miscHelper.getTagIdentifier(dust, material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerGrinderRecipe(new Identifier("jaopca", "techreborn.material_to_dust."+material.getName()),
							materialLocation, 1, dustLocation, 1, 2, 200);
				}
			}
			if((ArrayUtils.contains(MaterialType.GEMS, type) || ArrayUtils.contains(MaterialType.CRYSTALS, type)) &&
					!TO_CRYSTAL_BLACKLIST.contains(material.getName())) {
				Identifier dustLocation = miscHelper.getTagIdentifier(dust, material.getName());
				Identifier materialLocation = miscHelper.getTagIdentifier(material.getType().getFormName(), material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerImplosionCompressorRecipe(new Identifier("jaopca", "techreborn.dust_to_material."+material.getName()),
							Items.TNT, 16, dustLocation, 4, materialLocation, 3, TRContent.Dusts.DARK_ASHES, 12, 30, 2000);
				}
			}
			if((ArrayUtils.contains(MaterialType.INGOTS, type)) && !TO_PLATE_BLACKLIST.contains(material.getName())) {
				Identifier materialLocation = miscHelper.getTagIdentifier(material.getType().getFormName(), material.getName());
				Identifier plateLocation = miscHelper.getTagIdentifier(plate, material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerCompressorRecipe(new Identifier("jaopca", "material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1, 10, 300);
				}
			}
			if((ArrayUtils.contains(MaterialType.GEMS, type) || ArrayUtils.contains(MaterialType.CRYSTALS, type))
					|| ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_PLATE_BLACKLIST.contains(material.getName())) {
				Identifier dustLocation = miscHelper.getTagIdentifier(dust, material.getName());
				Identifier plateLocation = miscHelper.getTagIdentifier(plate, material.getName());
				if(api.getItemTags().contains(dustLocation) && api.getItemTags().contains(plateLocation)) {
					helper.registerCompressorRecipe(new Identifier("jaopca", "dust_to_plate."+material.getName()),
							dustLocation, 1, plateLocation, 1, 10, 250);
				}
			}
			if(!BLOCK_TO_PLATE_BLACKLIST.contains(material.getName())) {
				Identifier blockLocation = miscHelper.getTagIdentifier(block, material.getName());
				Identifier plateLocation = miscHelper.getTagIdentifier(plate, material.getName());
				if(api.getItemTags().contains(blockLocation) && api.getItemTags().contains(plateLocation)) {
					helper.registerCompressorRecipe(new Identifier("jaopca", "block_to_plate."+material.getName()),
							blockLocation, 1, plateLocation, 9, 10, 300);
				}
			}
		}
	}
}
