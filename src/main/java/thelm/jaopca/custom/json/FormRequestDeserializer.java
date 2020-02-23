package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.forms.FormRequestImpl;
import thelm.jaopca.utils.JsonHelperImpl;

public class FormRequestDeserializer implements JsonDeserializer<FormRequest> {

	public static final FormRequestDeserializer INSTANCE = new FormRequestDeserializer();

	private FormRequestDeserializer() {}

	@Override
	public FormRequest deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(jsonElement.isJsonObject()) {
			return context.<Form>deserialize(jsonElement, Form.class).toRequest();
		}
		else if(jsonElement.isJsonArray()) {
			return new FormRequestImpl(CustomModule.instance, context.<Form[]>deserialize(jsonElement, Form.class)).setGrouped(true);
		}
		throw new JsonParseException("Unable to deserialize "+JsonHelperImpl.INSTANCE.toSimpleString(jsonElement)+" into a form request");
	}
}
