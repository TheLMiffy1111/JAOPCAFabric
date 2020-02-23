package thelm.jaopca.api.blocks;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.materials.IMaterial;

public interface BlockFormType extends FormType {

	@Override
	BlockFormSettings getNewSettings();

	@Override
	BlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	BlockInfo getMaterialFormInfo(Form form, IMaterial material);
}
