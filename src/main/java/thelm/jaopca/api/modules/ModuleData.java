package thelm.jaopca.api.modules;

import java.util.Set;

import thelm.jaopca.api.materials.IMaterial;

public interface ModuleData {

	Module getModule();

	Set<String> getConfigMaterialBlacklist();

	Set<String> getConfigPassiveMaterialWhitelist();

	Set<IMaterial> getMaterials();
}
