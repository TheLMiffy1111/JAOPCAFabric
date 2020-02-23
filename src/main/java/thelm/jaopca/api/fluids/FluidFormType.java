package thelm.jaopca.api.fluids;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.materials.IMaterial;

public interface FluidFormType extends FormType {

	@Override
	FluidFormSettings getNewSettings();

	@Override
	FluidFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	FluidInfo getMaterialFormInfo(Form form, IMaterial material);
}
