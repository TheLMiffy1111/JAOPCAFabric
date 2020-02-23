package thelm.jaopca.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.Lists;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import thelm.jaopca.api.config.DynamicSpecConfig;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.materials.MaterialImpl;
import thelm.jaopca.modules.ModuleDataImpl;
import thelm.jaopca.modules.ModuleHandler;

public class ConfigHandler {

	private ConfigHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static File configDir;
	private static File customFormConfigFile;
	private static File materialConfigDir;
	private static File moduleConfigDir;
	private static DynamicSpecConfig mainConfig;
	private static final TreeMap<IMaterial, DynamicSpecConfig> MATERIAL_CONFIGS = new TreeMap<>();
	private static final TreeMap<Module, DynamicSpecConfig> MODULE_CONFIGS = new TreeMap<>();

	public static boolean ingot = true;
	public static boolean gem = true;
	public static boolean crystal = true;
	public static boolean dust = true;
	public static boolean ingotPlain = true;
	public static boolean gemPlain = true;
	public static boolean crystalPlain = true;
	public static boolean dustPlain = true;

	public static TagFormat tagFormat = TagFormat.COTTON;
	public static String defaultNamespace = "c";

	private static final List<String> DEFAULT_GEM_OVERRIDES = Lists.newArrayList("diamond", "emerald", "lapis", "prismarine", "quartz");
	private static final List<String> DEFAULT_CRYSTAL_OVERRIDES = Lists.newArrayList();
	private static final List<String> DEFAULT_DUST_OVERRIDES = Lists.newArrayList("glowstone", "redstone");
	public static final Set<String> GEM_OVERRIDES = new TreeSet<>();
	public static final Set<String> CRYSTAL_OVERRIDES = new TreeSet<>();
	public static final Set<String> DUST_OVERRIDES = new TreeSet<>();

	public static final Set<Identifier> BLOCK_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<Identifier> ITEM_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<Identifier> FLUID_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<Identifier> ENTITY_TYPE_TAG_BLACKLIST = new TreeSet<>();

	public static final Set<Identifier> RECIPE_BLACKLIST = new TreeSet<>();

	public static final Set<Identifier> LOOT_TABLE_BLACKLIST = new TreeSet<>();

	public static final Set<Identifier> ADVANCEMENT_BLACKLIST = new TreeSet<>();

	public static double gammaValue = 2;

	public static void setupMainConfig() {
		configDir = new File(FabricLoader.getInstance().getConfigDirectory(), "jaopca");
		if(!configDir.exists() || !configDir.isDirectory()) {
			try {
				if(configDir.exists() && !configDir.isDirectory()) {
					LOGGER.warn("Config directory {} is a file, deleting", materialConfigDir);
					configDir.delete();
				}
				if(!configDir.mkdir()) {
					throw new Error("Could not create config directory "+configDir);
				}
			}
			catch(SecurityException e) {
				throw new Error("Could not create config directory "+configDir, e);
			}
		}

		mainConfig = new DynamicSpecConfigImpl(CommentedFileConfig.builder(new File(configDir, "main.toml")).sync().backingMapCreator(LinkedHashMap::new).autosave().build());

		mainConfig.setComment("materials", "Configurations related to materials.");
		ingot = mainConfig.getDefinedBoolean("materials.ingot", ingot, "Should the mod find ingot materials with ores.");
		gem = mainConfig.getDefinedBoolean("materials.gem", gem, "Should the mod find gem materials with ores.");
		crystal = mainConfig.getDefinedBoolean("materials.crystal", crystal, "Should the mod find crystal materials with ores.");
		dust = mainConfig.getDefinedBoolean("materials.dust", dust, "Should the mod find dust materials with ores.");
		ingotPlain = mainConfig.getDefinedBoolean("materials.ingotPlain", ingotPlain, "Should the mod find ingot materials without ores.");
		gemPlain = mainConfig.getDefinedBoolean("materials.gemPlain", gemPlain, "Should the mod find gem materials without ores.");
		crystalPlain = mainConfig.getDefinedBoolean("materials.crystalPlain", crystalPlain, "Should the mod find crystal materials without ores.");
		dustPlain = mainConfig.getDefinedBoolean("materials.dustPlain", dustPlain, "Should the mod find dust materials without ores.");

		tagFormat = mainConfig.getDefinedEnum("tags.tagFormat", TagFormat.class, tagFormat, "The tag format that the mod should use.");
		defaultNamespace = mainConfig.getDefinedString("tags.defaultNamespace", defaultNamespace, "The default namespace that the mod should use.");

		mainConfig.setComment("materialOverrides", "Configurations related to material overrides.");
		GEM_OVERRIDES.addAll(mainConfig.getDefinedStringList("materialOverrides.gem", DEFAULT_GEM_OVERRIDES, "List of materials that should be gems."));
		CRYSTAL_OVERRIDES.addAll(mainConfig.getDefinedStringList("materialOverrides.crystal", DEFAULT_CRYSTAL_OVERRIDES, "List of materials that should be crystals."));
		DUST_OVERRIDES.addAll(mainConfig.getDefinedStringList("materialOverrides.dust", DEFAULT_DUST_OVERRIDES, "List of materials that should be dusts."));

		mainConfig.setComment("blockTags", "Configurations related to block tags.");
		BLOCK_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("blockTags.blacklist", new ArrayList<>(),
				"List of block tags that should not be added."), Identifier::new));
		DataCollector.getDefinedTags("blocks").addAll(Lists.transform(mainConfig.getDefinedStringList("blockTags.customDefined", new ArrayList<>(),
				"List of block tags that should be considered as defined."), Identifier::new));

		mainConfig.setComment("itemTags", "Configurations related to item tags.");
		ITEM_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("itemTags.blacklist", new ArrayList<>(),
				"List of item tags that should not be added."), Identifier::new));
		DataCollector.getDefinedTags("items").addAll(Lists.transform(mainConfig.getDefinedStringList("itemTags.customDefined", new ArrayList<>(),
				"List of item tags that should be considered as defined."), Identifier::new));

		mainConfig.setComment("fluidTags", "Configurations related to fluid tags.");
		FLUID_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("fluidTags.blacklist", new ArrayList<>(),
				"List of fluid tags that should not be added."), Identifier::new));
		DataCollector.getDefinedTags("fluids").addAll(Lists.transform(mainConfig.getDefinedStringList("fluidTags.customDefined", new ArrayList<>(),
				"List of fluid tags that should be considered as defined."), Identifier::new));

		mainConfig.setComment("entityTypeTags", "Configurations related to entity type tags.");
		ENTITY_TYPE_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("entityTypeTags.blacklist", new ArrayList<>(),
				"List of entity type tags that should not be added."), Identifier::new));
		DataCollector.getDefinedTags("entity_types").addAll(Lists.transform(mainConfig.getDefinedStringList("entityTypeTags.customDefined", new ArrayList<>(),
				"List of entity type tags that should be considered as defined."), Identifier::new));

		mainConfig.setComment("recipes", "Configurations related to recipes.");
		RECIPE_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("recipes.blacklist", new ArrayList<>(),
				"List of recipes that should not be added."), Identifier::new));

		mainConfig.setComment("lootTables", "Configurations related to loot tables.");
		LOOT_TABLE_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("lootTables.blacklist", new ArrayList<>(),
				"List of loot tables that should not be added."), Identifier::new));

		mainConfig.setComment("advancements", "Configurations related to advancements.");
		ADVANCEMENT_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("advancements.blacklist", new ArrayList<>(),
				"List of advancements that should not be added."), Identifier::new));

		mainConfig.setComment("colors", "Configurations related to color generation.");
		gammaValue = mainConfig.getDefinedDouble("colors.gammaValue", gammaValue, "The gamma value used to blend colors.");
	}

	public static void setupCustomFormConfig() {
		customFormConfigFile = new File(configDir, "custom_forms.json");
		try {
			if(!customFormConfigFile.exists()) {
				FileWriter writer = new FileWriter(customFormConfigFile);
				writer.close();
			}
		}
		catch(IOException e) {
			throw new RuntimeException("Could not create config file "+customFormConfigFile, e);
		}
		CustomModule.instance.setCustomFormConfigFile(customFormConfigFile);
	}

	public static void setupMaterialConfigs() {
		materialConfigDir = new File(configDir, "materials");
		if(!materialConfigDir.exists() || !materialConfigDir.isDirectory()) {
			if(materialConfigDir.exists() && !materialConfigDir.isDirectory()) {
				LOGGER.warn("Config directory {} is a file, deleting", materialConfigDir);
				materialConfigDir.delete();
			}
			if(!materialConfigDir.mkdir()) {
				throw new RuntimeException("Could not create config directory "+materialConfigDir);
			}
		}
		MATERIAL_CONFIGS.clear();
		for(MaterialImpl material : MaterialHandler.getMaterials()) {
			DynamicSpecConfig config = new DynamicSpecConfigImpl(CommentedFileConfig.builder(new File(materialConfigDir, material.getName()+".toml")).sync().backingMapCreator(LinkedHashMap::new).autosave().build());
			MATERIAL_CONFIGS.put(material, config);
			material.setConfig(config);
		}
	}

	public static void setupModuleConfigsPre() {
		moduleConfigDir = new File(configDir, "modules");
		if(!moduleConfigDir.exists() || !moduleConfigDir.isDirectory()) {
			if(moduleConfigDir.exists() && !moduleConfigDir.isDirectory()) {
				LOGGER.warn("Config directory {} is a file, deleting", moduleConfigDir);
				moduleConfigDir.delete();
			}
			if(!moduleConfigDir.mkdir()) {
				throw new Error("Could not create config directory "+moduleConfigDir);
			}
		}
		for(Module module : ModuleHandler.getModules()) {
			DynamicSpecConfig config = new DynamicSpecConfigImpl(CommentedFileConfig.builder(new File(moduleConfigDir, module.getName()+".toml")).sync().backingMapCreator(LinkedHashMap::new).autosave().build());
			MODULE_CONFIGS.put(module, config);
			ModuleDataImpl data = ModuleHandler.getModuleData(module);
			data.setConfig(config);
			module.defineModuleConfigPre(data, config);
			module.defineMaterialConfigPre(data, Collections.unmodifiableNavigableMap(MATERIAL_CONFIGS));
		}
	}

	public static void setupModuleConfigs() {
		for(Module module : ModuleHandler.getModules()) {
			DynamicSpecConfig config = MODULE_CONFIGS.get(module);
			ModuleDataImpl data = ModuleHandler.getModuleData(module);
			module.defineModuleConfig(data, config);
			module.defineMaterialConfig(data, Collections.unmodifiableNavigableMap(MATERIAL_CONFIGS));
		}
	}
}
