package thelm.jaopca.api.forms;

import java.util.Collection;
import java.util.Set;

import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.Module;

public interface Form extends Comparable<Form> {

	String getName();

	FormType getType();

	Module getModule();

	Form setSecondaryName(String secondaryName);

	String getSecondaryName();

	Form setMaterialTypes(Collection<MaterialType> materialTypes);

	Form setMaterialTypes(MaterialType... materialTypes);

	Set<MaterialType> getMaterialTypes();

	Form setDefaultMaterialBlacklist(Collection<String> defaultMaterialBlacklist);

	Form setDefaultMaterialBlacklist(String... defaultMaterialBlacklist);

	Set<String> getDefaultMaterialBlacklist();

	Form setSettings(FormSettings settings);

	FormSettings getSettings();

	Form setSkipGroupedCheck(boolean skipGroupCheck);

	boolean skipGroupedCheck();

	Set<IMaterial> getMaterials();

	Form lock();

	FormRequest toRequest();

	Form setRequest(FormRequest request);

	boolean isMaterialValid(IMaterial material);

	void setMaterials(Collection<IMaterial> materials);

	@Override
	default int compareTo(Form other) {
		return getName().compareTo(other.getName());
	}
}
