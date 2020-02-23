package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.helpers.JsonHelper;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.forms.FormImpl;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.JsonHelperImpl;

public class FormDeserializer implements JsonDeserializer<Form> {

	public static final FormDeserializer INSTANCE = new FormDeserializer();

	private FormDeserializer() {}

	@Override
	public Form deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonHelper helper = JsonHelperImpl.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "element");
		String name = helper.getString(json, "name");
		FormType type = FormTypeHandler.getFormType(helper.getString(json, "type"));
		Form form = new FormImpl(CustomModule.instance, name, type);
		if(json.has("secondaryName")) {
			form.setSecondaryName(helper.getString(json, "secondaryName"));
		}
		if(json.has("materialTypes")) {
			form.setMaterialTypes(helper.<MaterialType[]>deserializeType(json, "materialTypes", context, MaterialType[].class));
		}
		if(json.has("defaultMaterialBlacklist")) {
			form.setDefaultMaterialBlacklist(helper.<String[]>deserializeType(json, "defaultMaterialBlacklist", context, String[].class));
		}
		if(json.has("skipGroupedCheck")) {
			form.setSkipGroupedCheck(helper.getBoolean(json, "skipGroupedCheck"));
		}
		if(json.has("settings")) {
			form.setSettings(type.deserializeSettings(json.get("settings"), context));
		}
		return form;
	}
}
