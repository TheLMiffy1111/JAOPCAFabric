package thelm.jaopca.api.resources;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public interface InMemoryResourcePack extends ResourcePack {

	InMemoryResourcePack putInputStream(ResourceType type, Identifier location, Supplier<? extends InputStream> streamSupplier);

	InMemoryResourcePack putInputStreams(ResourceType type, Map<Identifier, Supplier<? extends InputStream>> map);

	InMemoryResourcePack putByteArray(ResourceType type, Identifier location, byte[] file);

	InMemoryResourcePack putByteArrays(ResourceType type, Map<Identifier, byte[]> map);

	InMemoryResourcePack putString(ResourceType type, Identifier location, String str);

	InMemoryResourcePack putStrings(ResourceType type, Map<Identifier, String> map);

	InMemoryResourcePack putJson(ResourceType type, Identifier location, JsonElement json);

	InMemoryResourcePack putJsons(ResourceType type, Map<Identifier, ? extends JsonElement> map);
}
