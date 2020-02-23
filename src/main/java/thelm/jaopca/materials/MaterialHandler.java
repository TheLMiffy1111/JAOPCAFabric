package thelm.jaopca.materials;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.ApiImpl;

public class MaterialHandler {

	private MaterialHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<String, MaterialImpl> MATERIALS = new TreeMap<>();
	private static final TreeSet<String> SUFFIX_BLACKLIST = new TreeSet<>();
	
	static {
		SUFFIX_BLACKLIST.add("_hot");
		SUFFIX_BLACKLIST.add("_small");
		SUFFIX_BLACKLIST.add("_tiny");
		SUFFIX_BLACKLIST.add("_dirty");
	}

	public static Map<String, MaterialImpl> getMaterialMap() {
		return MATERIALS;
	}

	public static Collection<MaterialImpl> getMaterials() {
		return MATERIALS.values();
	}

	public static MaterialImpl getMaterial(String name) {
		return MATERIALS.get(name);
	}

	public static boolean containsMaterial(String name) {
		return MATERIALS.containsKey(name);
	}

	public static void findMaterials() {
		MATERIALS.clear();
		TagFormat tagFormat = ConfigHandler.tagFormat;

		Set<String> allMaterials = new TreeSet<>();

		Set<String> ingots = ConfigHandler.ingot ? findItemTagNamesWithPatterns(tagFormat.getIngotPattern(), tagFormat.getOreFormat()) : new LinkedHashSet<>();
		ingots.removeAll(ConfigHandler.GEM_OVERRIDES);
		ingots.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		ingots.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(ingots);

		Set<String> gems = ConfigHandler.gem ? findItemTagNamesWithPatterns(tagFormat.getGemPattern(), tagFormat.getOreFormat()) : new LinkedHashSet<>();
		gems.removeAll(allMaterials);
		gems.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		gems.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(gems);

		Set<String> crystals = ConfigHandler.crystal ? findItemTagNamesWithPatterns(tagFormat.getCrystalPattern(), tagFormat.getOreFormat()) : new LinkedHashSet<>();
		crystals.removeAll(allMaterials);
		crystals.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(crystals);

		Set<String> dusts = ConfigHandler.dust ? findItemTagNamesWithPatterns(tagFormat.getDustPattern(), tagFormat.getOreFormat()) : new LinkedHashSet<>();
		dusts.removeAll(allMaterials);
		allMaterials.addAll(dusts);

		Set<String> ingotsPlain = ConfigHandler.ingotPlain ? findItemTagNamesWithPatterns(tagFormat.getIngotPattern()) : new LinkedHashSet<>();
		ingotsPlain.removeAll(allMaterials);
		ingotsPlain.removeAll(ConfigHandler.GEM_OVERRIDES);
		ingotsPlain.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		ingotsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(ingotsPlain);

		Set<String> gemsPlain = ConfigHandler.gemPlain ? findItemTagNamesWithPatterns(tagFormat.getGemPattern()) : new LinkedHashSet<>();
		gemsPlain.removeAll(allMaterials);
		gemsPlain.removeAll(ConfigHandler.CRYSTAL_OVERRIDES);
		gemsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(gemsPlain);

		Set<String> crystalsPlain = ConfigHandler.crystalPlain ? findItemTagNamesWithPatterns(tagFormat.getCrystalPattern()) : new LinkedHashSet<>();
		crystalsPlain.removeAll(allMaterials);
		crystalsPlain.removeAll(ConfigHandler.DUST_OVERRIDES);
		allMaterials.addAll(crystalsPlain);

		Set<String> dustsPlain = ConfigHandler.dustPlain ? findItemTagNamesWithPatterns(tagFormat.getDustPattern()) : new LinkedHashSet<>();
		dustsPlain.removeAll(allMaterials);
		allMaterials.addAll(dustsPlain);

		for(String name : ingots) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.INGOT);
			MATERIALS.put(name, material);
			LOGGER.debug("Added ingot material {}", name);
		}
		for(String name : gems) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.GEM);
			MATERIALS.put(name, material);
			LOGGER.debug("Added gem material {}", name);
		}
		for(String name : crystals) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.CRYSTAL);
			MATERIALS.put(name, material);
			LOGGER.debug("Added crystal material {}", name);
		}
		for(String name : dusts) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.DUST);
			MATERIALS.put(name, material);
			LOGGER.debug("Added dust material {}", name);
		}
		for(String name : ingotsPlain) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.INGOT_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain ingot material {}", name);
		}
		for(String name : gemsPlain) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.GEM_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain gem material {}", name);
		}
		for(String name : crystalsPlain) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.CRYSTAL_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain crystal material {}", name);
		}
		for(String name : dustsPlain) {
			MaterialImpl material = new MaterialImpl(name, MaterialType.DUST_PLAIN);
			MATERIALS.put(name, material);
			LOGGER.debug("Added plain dust material {}", name);
		}
		LOGGER.info("Added {} materials", MATERIALS.size());
	}

	protected static Set<String> findItemTagNamesWithPatterns(String regex) {
		Set<String> ret = new TreeSet<>();
		Set<String> tags = ApiImpl.INSTANCE.getItemTags().stream().map(Identifier::toString).collect(Collectors.toCollection(LinkedHashSet::new));
		Pattern pattern = Pattern.compile(regex);
		for(String tag : tags) {
			Matcher matcher = pattern.matcher(tag);
			if(matcher.matches()) {
				String name = matcher.group(1);
				if(ConfigHandler.tagFormat == TagFormat.COTTON && SUFFIX_BLACKLIST.stream().anyMatch(name::endsWith)) {
					continue;
				}
				ret.add(name);
			}
		}
		return ret;
	}

	protected static Set<String> findItemTagNamesWithPatterns(String regex, String format) {
		Set<String> ret = new TreeSet<>();
		Set<String> tags = ApiImpl.INSTANCE.getItemTags().stream().map(Identifier::toString).collect(Collectors.toCollection(LinkedHashSet::new));
		Pattern pattern = Pattern.compile(regex);
		for(String tag : tags) {
			Matcher matcher = pattern.matcher(tag);
			if(matcher.matches()) {
				String name = matcher.group(1);
				if(tags.contains(String.format(Locale.US, format, name))) {
					ret.add(name);
				}
			}
		}
		return ret;
	}
}
