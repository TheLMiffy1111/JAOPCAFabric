package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.Rarity;
import net.minecraft.util.shape.VoxelShape;
import thelm.jaopca.api.blocks.BlockFormSettings;
import thelm.jaopca.api.helpers.JsonHelper;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.JsonHelperImpl;

public class BlockFormSettingsDeserializer implements JsonDeserializer<BlockFormSettings> {

	public static final BlockFormSettingsDeserializer INSTANCE = new BlockFormSettingsDeserializer();

	private BlockFormSettingsDeserializer() {}

	public BlockFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, BlockFormSettings.class, context);
	}

	@Override
	public BlockFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonHelper helper = JsonHelperImpl.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		BlockFormSettings settings = BlockFormTypeImpl.INSTANCE.getNewSettings();
		if(json.has("blockMaterial")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockMaterial");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "iron");
			}
			settings.setMaterialFunction(helper.deserializeType(json, "blockMaterial", context, BlockFormTypeImpl.MATERIAL_FUNCTION_TYPE));
		}
		if(json.has("soundType")) {
			JsonObject functionJson = helper.getJsonObject(json, "soundType");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "metal");
			}
			settings.setSoundTypeFunction(helper.deserializeType(json, "soundType", context, BlockFormTypeImpl.SOUND_TYPE_FUNCTION_TYPE));
		}
		if(json.has("lightValue")) {
			JsonObject functionJson = helper.getJsonObject(json, "lightValue");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setLightValueFunction(helper.deserializeType(json, "lightValue", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("blockHardness")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockHardness");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 5);
			}
			settings.setBlockHardnessFunction(helper.deserializeType(json, "blockHardness", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("explosionResistance")) {
			JsonObject functionJson = helper.getJsonObject(json, "explosionResistance");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 6);
			}
			settings.setExplosionResistanceFunction(helper.deserializeType(json, "explosionResistance", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("slipperiness")) {
			JsonObject functionJson = helper.getJsonObject(json, "slipperiness");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0.6);
			}
			settings.setSlipperinessFunction(helper.deserializeType(json, "slipperiness", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("shape")) {
			settings.setShape(helper.deserializeType(json, "shape", context, VoxelShape.class));
		}
		if(json.has("raytraceShape")) {
			settings.setRaytraceShape(helper.deserializeType(json, "raytraceShape", context, VoxelShape.class));
		}
		if(json.has("harvestTool")) {
			JsonObject functionJson = helper.getJsonObject(json, "harvestTool");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "pickaxe");
			}
			settings.setHarvestToolFunction(helper.deserializeType(json, "harvestTool", context, BlockFormTypeImpl.TOOL_TYPE_FUNCTION_TYPE));
		}
		if(json.has("harvestLevel")) {
			JsonObject functionJson = helper.getJsonObject(json, "harvestLevel");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setHarvestLevelFunction(helper.deserializeType(json, "harvestLevel", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("flammability")) {
			JsonObject functionJson = helper.getJsonObject(json, "flammability");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setFlammabilityFunction(helper.deserializeType(json, "flammability", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("fireSpreadSpeed")) {
			JsonObject functionJson = helper.getJsonObject(json, "fireSpreadSpeed");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setFireSpreadSpeedFunction(helper.deserializeType(json, "fireSpreadSpeed", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
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
