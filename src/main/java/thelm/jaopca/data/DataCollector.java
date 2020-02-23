package thelm.jaopca.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.TreeMultimap;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class DataCollector {

	private DataCollector() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int TAGS_PATH_LENGTH = "tags/".length();
	private static final int RECIPES_PATH_LENGTH = "recipes/".length();
	private static final int LOOT_TABLES_PATH_LENGTH = "loot_tables/".length();
	private static final int ADVANCEMENTS_PATH_LENGTH = "advancements/".length();
	private static final int JSON_EXTENSION_LENGTH = ".json".length();
	private static final List<Supplier<ResourcePack>> RESOURCE_PACK_SUPPLIERS = new ArrayList<>();
	private static final List<ResourcePack> RESOURCE_PACKS = new ArrayList<>();
	private static final TreeMultimap<String, Identifier> DEFINED_TAGS = TreeMultimap.create();
	private static final TreeSet<Identifier> DEFINED_RECIPES = new TreeSet<>();
	private static final TreeSet<Identifier> DEFINED_LOOT_TABLES = new TreeSet<>();
	private static final TreeSet<Identifier> DEFINED_ADVANCEMENTS = new TreeSet<>();

	public static void collectData() {
		DEFINED_TAGS.clear();
		DEFINED_RECIPES.clear();
		DEFINED_ADVANCEMENTS.clear();
		if(RESOURCE_PACKS.isEmpty()) {
			RESOURCE_PACKS.add(new DefaultResourcePack("minecraft"));
			ModResourcePackUtil.appendModResourcePacks(RESOURCE_PACKS, ResourceType.SERVER_DATA);
			for(Supplier<ResourcePack> supplier : RESOURCE_PACK_SUPPLIERS) {
				RESOURCE_PACKS.add(supplier.get());
			}
		}
		for(Identifier location : getAllDataIdentifiers("tags", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			path = path.substring(TAGS_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
			String[] split = path.split("/", 2);
			if(split.length == 2) {
				String type = split[0];
				path = split[1];
				DEFINED_TAGS.put(type, new Identifier(namespace, path));
			}
			else {
				LOGGER.error("Tag {} in namespace {} has no type", path, namespace);
			}
		}
		LOGGER.info("Found {} unique defined tags", DEFINED_TAGS.size());
		for(Identifier location : getAllDataIdentifiers("recipes", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			if(!path.equals("recipes/_constants.json") && !path.equals("recipes/_factories.json")) {
				path = path.substring(RECIPES_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
				DEFINED_RECIPES.add(new Identifier(namespace, path));
			}
		}
		LOGGER.info("Found {} unique defined recipes", DEFINED_RECIPES.size());
		for(Identifier location : getAllDataIdentifiers("loot_tables", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			path = path.substring(LOOT_TABLES_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
			DEFINED_LOOT_TABLES.add(new Identifier(namespace, path));
		}
		LOGGER.info("Found {} unique defined loot tables", DEFINED_LOOT_TABLES.size());
		for(Identifier location : getAllDataIdentifiers("advancements", name->name.endsWith(".json"))) {
			String namespace = location.getNamespace();
			String path = location.getPath();
			path = path.substring(ADVANCEMENTS_PATH_LENGTH, path.length()-JSON_EXTENSION_LENGTH);
			DEFINED_ADVANCEMENTS.add(new Identifier(namespace, path));
		}
		LOGGER.info("Found {} unique defined advancements", DEFINED_ADVANCEMENTS.size());
	}

	public static Set<Identifier> getDefinedTags(String type) {
		return DEFINED_TAGS.get(type);
	}

	public static Set<Identifier> getDefinedRecipes() {
		return DEFINED_RECIPES;
	}

	public static Set<Identifier> getDefinedLootTables() {
		return DEFINED_LOOT_TABLES;
	}

	public static Set<Identifier> getDefinedAdvancements() {
		return DEFINED_ADVANCEMENTS;
	}

	static Collection<Identifier> getAllDataIdentifiers(String pathIn, Predicate<String> filter) {
		Set<Identifier> set = new TreeSet<>();
		for(ResourcePack resourcePack : RESOURCE_PACKS) {
			for(String namespace : resourcePack.getNamespaces(ResourceType.SERVER_DATA)) {
				set.addAll(resourcePack.findResources(ResourceType.SERVER_DATA, namespace, pathIn, Integer.MAX_VALUE, filter));
			}
		}
		return set;
	}
}
