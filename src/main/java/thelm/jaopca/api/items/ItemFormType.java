package thelm.jaopca.api.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.materials.IMaterial;

public interface ItemFormType extends FormType {

	@Override
	ItemFormSettings getNewSettings();

	@Override
	ItemFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	@Override
	ItemInfo getMaterialFormInfo(Form form, IMaterial material);
}
