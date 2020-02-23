package thelm.jaopca.api.modules;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resource.ResourceManager;
import thelm.jaopca.api.config.DynamicSpecConfig;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.resources.InMemoryResourcePack;
import thelm.jaopca.modules.ModuleDataImpl;

public interface Module extends Comparable<Module> {

	String getName();

	default boolean isPassive() {
		return false;
	}

	default void defineModuleConfigPre(ModuleData moduleData, DynamicSpecConfig config) {}

	default void defineMaterialConfigPre(ModuleData moduleData, Map<IMaterial, DynamicSpecConfig> configs) {}

	default Multimap<Integer, String> getModuleDependencies() {
		return ImmutableSetMultimap.of();
	}

	default List<FormRequest> getFormRequests() {
		return Collections.emptyList();
	}

	default Set<MaterialType> getMaterialTypes() {
		return Collections.emptySet();
	}

	default Set<String> getDefaultMaterialBlacklist() {
		return Collections.emptyNavigableSet();
	}

	default void onMainConfigSetupComplete(ModuleData moduleData) {}

	default void defineModuleConfig(ModuleData moduleData, DynamicSpecConfig config) {}

	default void defineMaterialConfig(ModuleData moduleData, Map<IMaterial, DynamicSpecConfig> configs) {}

	default void onMaterialComputeComplete(ModuleData moduleData) {}

	default void onCommonSetup(ModuleData moduleData) {}

	default void onClientSetup(ModuleData moduleData) {}

	default void onCreateResourcePack(ModuleData moduleData, InMemoryResourcePack resourcePack) {}

	default void onCreateDataPack(ModuleData moduleData, InMemoryResourcePack resourcePack) {}

	default void onRecipeInjectComplete(ModuleData moduleData, ResourceManager resourceManager) {}

	@Override
	default int compareTo(Module other) {
		return getName().compareTo(other.getName());
	}
}
