package thelm.jaopca.api.entities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import thelm.jaopca.api.forms.FormType;

public interface EntityTypeFormType extends FormType {

	@Override
	EntityTypeFormSettings getNewSettings();

	@Override
	EntityTypeFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);
}
