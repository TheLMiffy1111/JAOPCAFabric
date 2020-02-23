package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.Rarity;
import thelm.jaopca.api.helpers.JsonHelper;
import thelm.jaopca.api.items.ItemFormSettings;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.utils.JsonHelperImpl;

public class ItemFormSettingsDeserializer implements JsonDeserializer<ItemFormSettings> {

	public static final ItemFormSettingsDeserializer INSTANCE = new ItemFormSettingsDeserializer();

	private ItemFormSettingsDeserializer() {}

	public ItemFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, ItemFormSettings.class, context);
	}

	@Override
	public ItemFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonHelper helper = JsonHelperImpl.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		ItemFormSettings settings = ItemFormTypeImpl.INSTANCE.getNewSettings();
		if(json.has("itemStackLimit")) {
			JsonObject functionJson = helper.getJsonObject(json, "itemStackLimit");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 64);
			}
			settings.setItemStackLimitFunction(helper.deserializeType(json, "itemStackLimit", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("hasEffect")) {
			boolean hasEffect = helper.getBoolean(json, "hasEffect");
			settings.setHasEffectFunction(m->m.hasEffect() || hasEffect);
		}
		if(json.has("rarity")) {
			Rarity rarity = helper.deserializeType(json, "rarity", context, Rarity.class);
			settings.setDisplayRarityFunction(m->rarity);
		}
		if(json.has("burnTime")) {
			JsonObject functionJson = helper.getJsonObject(json, "burnTime");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", -1);
			}
			settings.setBurnTimeFunction(helper.deserializeType(json, "burnTime", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		return settings;
	}
}
