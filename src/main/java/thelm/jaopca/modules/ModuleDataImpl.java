package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultiset;

import it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap;
import thelm.jaopca.api.config.DynamicSpecConfig;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.materials.MaterialHandler;

public class ModuleDataImpl implements ModuleData {

	private final Module module;
	private DynamicSpecConfig config = null;
	private final TreeSet<String> configMaterialBlacklist = new TreeSet<>();
	private final TreeSet<String> configPassiveMaterialWhitelist = new TreeSet<>();
	private final Object2BooleanRBTreeMap<IMaterial> dependencyValidMaterials = new Object2BooleanRBTreeMap<>();
	private final TreeSet<IMaterial> rejectedMaterials = new TreeSet<>();
	private final TreeSet<IMaterial> requestedMaterials = new TreeSet<>();
	private final TreeSet<IMaterial> materials = new TreeSet<>();

	public ModuleDataImpl(Module module) {
		this.module = module;
	}

	@Override
	public Module getModule() {
		return module;
	}

	@Override
	public Set<String> getConfigMaterialBlacklist() {
		return Collections.unmodifiableNavigableSet(configMaterialBlacklist);
	}

	@Override
	public Set<String> getConfigPassiveMaterialWhitelist() {
		if(module.isPassive()) {
			return Collections.unmodifiableNavigableSet(configPassiveMaterialWhitelist);
		}
		return MaterialHandler.getMaterials().stream().map(IMaterial::getName).collect(ImmutableSortedSet.toImmutableSortedSet(Ordering.natural()));
	}

	@Override
	public Set<IMaterial> getMaterials() {
		return Collections.unmodifiableNavigableSet(materials);
	}

	public void setConfig(DynamicSpecConfig config) {
		this.config = config;

		TreeMultiset<String> blacklist = TreeMultiset.create(config.getDefinedStringList("general.materialBlacklist", new ArrayList<>(),
				s->"*".equals(s) || MaterialHandler.containsMaterial(s), "The material blacklist of this module."));
		int blacklistCount = blacklist.count("*");
		MaterialHandler.getMaterialMap().keySet().forEach(s->blacklist.add(s, blacklistCount));
		blacklist.remove("*", blacklistCount);
		configMaterialBlacklist.addAll(blacklist.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(e->e.getElement()).collect(Collectors.toList()));

		if(module.isPassive()) {
			TreeMultiset<String> whitelist = TreeMultiset.create(config.getDefinedStringList("general.passiveMaterialWhitelist", new ArrayList<>(),
					s->"*".equals(s) || MaterialHandler.containsMaterial(s), "The materials to force generate passive forms for this module."));
			int whitelistCount = whitelist.count("*");
			MaterialHandler.getMaterialMap().keySet().forEach(s->whitelist.add(s, whitelistCount));
			whitelist.remove("*", whitelistCount);
			configPassiveMaterialWhitelist.addAll(whitelist.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(e->e.getElement()).collect(Collectors.toList()));
		}
	}

	public boolean isMaterialConfigValid(IMaterial material) {
		return !material.getConfigModuleBlacklist().contains(module.getName()) &&
				!configMaterialBlacklist.contains(material.getName());
	}

	public boolean isMaterialDependencyValid(IMaterial material, Set<Pair<ModuleDataImpl, IMaterial>> prev) {
		return dependencyValidMaterials.computeBooleanIfAbsent(material, mat->{
			if(prev.contains(Pair.of(ModuleDataImpl.this, mat))) {
				return true;
			}
			if(!isMaterialConfigValid(mat)) {
				return false;
			}
			prev.add(Pair.of(ModuleDataImpl.this, mat));
			for(Map.Entry<Integer, Collection<String>> entry : module.getModuleDependencies().asMap().entrySet()) {
				IMaterial extraMaterial = mat.getExtra(entry.getKey());
				for(String requestedModule : entry.getValue()) {
					ModuleDataImpl requestedData = ModuleHandler.getModuleData(requestedModule);
					if(!requestedData.isMaterialDependencyValid(extraMaterial, prev)) {
						return false;
					}
				}
			}
			return true;
		});
	}

	public boolean isMaterialModuleValid(IMaterial material) {
		return isMaterialDependencyValid(material, new HashSet<>()) &&
				module.getMaterialTypes().contains(material.getType()) &&
				!module.getDefaultMaterialBlacklist().contains(material.getName());
	}

	public boolean isMaterialValid(IMaterial material) {
		return isMaterialModuleValid(material) &&
				!rejectedMaterials.contains(material) &&
				(!module.isPassive() ? true :
					configPassiveMaterialWhitelist.contains(material.getName()) ||
					requestedMaterials.contains(material));
	}

	public void addRejectedMaterial(IMaterial material) {
		rejectedMaterials.add(material);
	}

	public void addDependencyRequestedMaterial(IMaterial material) {
		for(Map.Entry<Integer, Collection<String>> entry : module.getModuleDependencies().asMap().entrySet()) {
			IMaterial extraMaterial = material.getExtra(entry.getKey());
			for(String requestedModule : entry.getValue()) {
				ModuleDataImpl requestedData = ModuleHandler.getModuleData(requestedModule);
				if(!requestedData.isMaterialDependencyValid(extraMaterial, new HashSet<>())) {
					throw new IllegalStateException("Module "+module.getName()+" has wrongly accepted material "+material.getName());
				}
				requestedData.addRequestedMaterial(extraMaterial);
			}
		}
	}

	public void addRequestedMaterial(IMaterial material) {
		if(getModule().isPassive()) {
			requestedMaterials.add(material);
			addDependencyRequestedMaterial(material);
		}
	}

	public Set<IMaterial> getRejectedMaterials() {
		return rejectedMaterials;
	}

	public Set<IMaterial> getRequestedMaterials() {
		return requestedMaterials;
	}

	public void setMaterials(Collection<IMaterial> materials) {
		this.materials.clear();
		this.materials.addAll(materials);
	}
}
