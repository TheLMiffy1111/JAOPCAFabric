package thelm.jaopca.custom.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import thelm.jaopca.api.helpers.JsonHelper;
import thelm.jaopca.utils.JsonHelperImpl;

public class RegistryEntrySupplierDeserializer implements JsonDeserializer<Supplier<?>> {

	public static final RegistryEntrySupplierDeserializer INSTANCE = new RegistryEntrySupplierDeserializer();

	private RegistryEntrySupplierDeserializer() {}

	@Override
	public Supplier<?> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonHelper helper = JsonHelperImpl.INSTANCE;
		Type[] typeArguments = ((ParameterizedType)typeOfT).getActualTypeArguments();
		Type parameterizedType = typeArguments[0];
		if(parameterizedType instanceof Class && helper.isString(jsonElement)) {
			Optional<MutableRegistry<?>> registryOptional = Registry.REGISTRIES.stream().
					filter(registry->((Class<?>)parameterizedType).isAssignableFrom(registry.get(0).getClass())).
					findAny();
			if(registryOptional.isPresent()) {
				MutableRegistry<?> registry = registryOptional.get();
				String valueString = helper.getString(jsonElement, "value");
				return ()->registry.get(new Identifier(valueString));
			}
		}
		throw new JsonParseException("Unable to deserialize "+helper.toSimpleString(jsonElement)+" into a registry entry");
	}
}
