package thelm.jaopca.api.forms;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;

public interface FormRequest {

	Module getModule();

	List<Form> getForms();

	boolean isGrouped();

	FormRequest setGrouped(boolean grouped);

	boolean isMaterialGroupValid(IMaterial material);
}
