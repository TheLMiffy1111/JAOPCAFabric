package thelm.jaopca.modules;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.fabricmc.loader.util.version.VersionPredicateParser;
import net.minecraft.resource.ResourceManager;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleListSupplier;
import thelm.jaopca.api.resources.InMemoryResourcePack;
import thelm.jaopca.materials.MaterialHandler;

public class ModuleHandler {

	private ModuleHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<String, Module> MODULES = new TreeMap<>();
	private static final TreeMap<Module, ModuleDataImpl> MODULE_DATAS = new TreeMap<>();

	public static Map<String, Module> getModuleMap() {
		return MODULES;
	}

	public static Collection<Module> getModules() {
		return MODULES.values();
	}

	public static Map<Module, ModuleDataImpl> getModuleDataMap() {
		return MODULE_DATAS;
	}

	public static ModuleDataImpl getModuleData(String name) {
		return MODULE_DATAS.get(MODULES.get(name));
	}

	public static ModuleDataImpl getModuleData(Module module) {
		return MODULE_DATAS.get(module);
	}

	public static Collection<ModuleDataImpl> getModuleDatas() {
		return MODULE_DATAS.values();
	}

	public static void findModules() {
		MODULES.clear();
		for(ModuleListSupplier mSupplier : FabricLoader.getInstance().getEntrypoints("jaopca", ModuleListSupplier.class)) {
			for(Module module : mSupplier.get()) {
				ModuleDataImpl mData = new ModuleDataImpl(module);
				if(MODULES.putIfAbsent(module.getName(), module) != null) {
					//throw new IllegalStateException(String.format("Module name conflict: %s for %s and %s", module.getName(), MODULES.get(module.getName()).getClass(), module.getClass()));
					LOGGER.fatal("Module name conflict: {} for {} and {}", module.getName(), MODULES.get(module.getName()).getClass(), module.getClass());
					continue;
				}
				MODULE_DATAS.put(module, mData);
			}
		};
	}

	static boolean isModVersionNotLoaded(String dep) {
		FabricLoader loader = FabricLoader.getInstance();
		int separatorIndex = dep.lastIndexOf('@');
		String modId = dep.substring(0, separatorIndex == -1 ? dep.length() : separatorIndex);
		String spec = separatorIndex == -1 ? "0" : dep.substring(separatorIndex+1, dep.length());
		VersionPredicateParser versionPredicateParser;
		if(loader.isModLoaded(modId)) {
			try {
				return VersionPredicateParser.matches(loader.getModContainer(modId).get().getMetadata().getVersion(), spec);
			}
			catch(VersionParsingException e) {
				LOGGER.warn("Unable to parse version spec {} for mod id {}", spec, modId, e);
				return true;
			}
		}
		return true;
	}

	public static void computeValidMaterials() {
		for(ModuleDataImpl data : getModuleDatas()) {
			List<IMaterial> materials = MaterialHandler.getMaterials().stream().
					filter(data.getModule().isPassive() ?
							material->data.getConfigPassiveMaterialWhitelist().contains(material.getName()) :
								data::isMaterialModuleValid).
					collect(Collectors.toList());
			for(IMaterial material : materials) {
				if(data.isMaterialDependencyValid(material, new HashSet<>())) {
					data.addDependencyRequestedMaterial(material);
				}
				else {
					data.addRejectedMaterial(material);
				}
			}
		}
		for(ModuleDataImpl data : getModuleDatas()) {
			List<IMaterial> materials = MaterialHandler.getMaterials().stream().filter(data::isMaterialValid).collect(Collectors.toList());
			data.setMaterials(materials);
		}
	}

	public static void onMainConfigSetupComplete() {
		for(Module module : getModules()) {
			module.onMainConfigSetupComplete(getModuleData(module));
		}
	}

	public static void onMaterialComputeComplete() {
		for(Module module : getModules()) {
			module.onMaterialComputeComplete(getModuleData(module));
		}
	}

	public static void onCommonSetup() {
		for(Module module : getModules()) {
			module.onCommonSetup(getModuleData(module));
		}
	}

	public static void onClientSetup() {
		for(Module module : getModules()) {
			module.onClientSetup(getModuleData(module));
		}
	}

	public static void onCreateResourcePack(InMemoryResourcePack resourcePack) {
		for(Module module : getModules()) {
			module.onCreateResourcePack(getModuleData(module), resourcePack);
		}
	}

	public static void onCreateDataPack(InMemoryResourcePack resourcePack) {
		for(Module module : getModules()) {
			module.onCreateDataPack(getModuleData(module), resourcePack);
		}
	}

	public static void onRecipeInjectComplete(ResourceManager resourceManager) {
		for(Module module : getModules()) {
			module.onRecipeInjectComplete(getModuleData(module), resourceManager);
		}
	}
}
