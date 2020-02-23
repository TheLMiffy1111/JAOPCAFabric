package thelm.jaopca.api.forms;

import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.materialforms.MaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;

public interface FormType extends Comparable<FormType> {

	String getName();

	void addForm(Form form);

	Set<Form> getForms();

	boolean shouldRegister(Form form, IMaterial material);

	FormSettings getNewSettings();

	GsonBuilder configureGsonBuilder(GsonBuilder builder);

	FormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	MaterialFormInfo getMaterialFormInfo(Form form, IMaterial material);

	@Override
	default int compareTo(FormType other) {
		return getName().compareTo(other.getName());
	}
}
