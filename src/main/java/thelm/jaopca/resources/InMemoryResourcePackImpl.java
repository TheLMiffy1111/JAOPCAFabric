package thelm.jaopca.resources;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import thelm.jaopca.api.resources.InMemoryResourcePack;

public class InMemoryResourcePackImpl implements InMemoryResourcePack {

	private static final Gson GSON = new GsonBuilder().create();
	private final String name;
	private final JsonObject metadata = (JsonObject)new JsonParser().parse("{\"pack_format\":4,\"description\":\"JAOPCA In Memory Resources\"}");
	private final TreeMap<String, Supplier<? extends InputStream>> root = new TreeMap<>();
	private final TreeMap<Identifier, Supplier<? extends InputStream>> assets = new TreeMap<>();
	private final TreeMap<Identifier, Supplier<? extends InputStream>> data = new TreeMap<>();

	public InMemoryResourcePackImpl(String name) {
		this.name = name;
	}

	@Override
	public InMemoryResourcePack putInputStream(ResourceType type, Identifier location, Supplier<? extends InputStream> streamSupplier) {
		switch(type) {
		case CLIENT_RESOURCES:
			assets.put(location, streamSupplier);
			break;
		case SERVER_DATA:
			data.put(location, streamSupplier);
			break;
		default:
			break;
		}
		return this;
	}

	@Override
	public InMemoryResourcePack putInputStreams(ResourceType type, Map<Identifier, Supplier<? extends InputStream>> map) {
		switch(type) {
		case CLIENT_RESOURCES:
			assets.putAll(map);
			break;
		case SERVER_DATA:
			data.putAll(map);
			break;
		default:
			break;
		}
		return this;
	}

	@Override
	public InMemoryResourcePack putByteArray(ResourceType type, Identifier location, byte[] file) {
		return putInputStream(type, location, ()->new ByteArrayInputStream(file));
	}

	@Override
	public InMemoryResourcePack putByteArrays(ResourceType type, Map<Identifier, byte[]> map) {
		return putInputStreams(type, Maps.transformValues(map, file->()->new ByteArrayInputStream(file)));
	}

	@Override
	public InMemoryResourcePack putString(ResourceType type, Identifier location, String str) {
		return putByteArray(type, location, str.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public InMemoryResourcePack putStrings(ResourceType type, Map<Identifier, String> map) {
		return putByteArrays(type, Maps.transformValues(map, str->str.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public InMemoryResourcePack putJson(ResourceType type, Identifier location, JsonElement json) {
		return putString(type, location, GSON.toJson(json));
	}

	@Override
	public InMemoryResourcePack putJsons(ResourceType type, Map<Identifier, ? extends JsonElement> map) {
		return putStrings(type, Maps.transformValues(map, json->GSON.toJson(json)));
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		if(fileName.contains("/") || fileName.contains("\\")) {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
		if(root.containsKey(fileName)) {
			return root.get(fileName).get();
		}
		throw new FileNotFoundException(fileName);
	}

	@Override
	public InputStream open(ResourceType type, Identifier location) throws IOException {
		Map<Identifier, Supplier<? extends InputStream>> map = type == ResourceType.CLIENT_RESOURCES ? assets : data;
		if(map.containsKey(location)) {
			return map.get(location).get();
		}
		throw new FileNotFoundException(location.toString());
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String pathIn, int maxDepth, Predicate<String> filter) {
		Map<Identifier, Supplier<? extends InputStream>> map = type == ResourceType.CLIENT_RESOURCES ? assets : data;
		return map.keySet().stream().filter(rl->rl.getNamespace().equals(namespace)).
				filter(rl->StringUtils.countMatches(rl.getPath(), '/') < maxDepth).
				filter(rl->rl.getPath().startsWith(pathIn)).filter(rl->{
					String path = rl.getPath();
					int lastSlash = path.lastIndexOf('/');
					return filter.test(path.substring(lastSlash < 0 ? 0 : lastSlash, path.length()));
				}).collect(Collectors.toList());
	}

	@Override
	public boolean contains(ResourceType type, Identifier location) {
		Map<Identifier, Supplier<? extends InputStream>> map = type == ResourceType.CLIENT_RESOURCES ? assets : data;
		return map.containsKey(location);
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Map<Identifier, Supplier<? extends InputStream>> map = type == ResourceType.CLIENT_RESOURCES ? assets : data;
		return map.keySet().stream().map(rl->rl.getNamespace()).collect(Collectors.toSet());
	}

	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> deserializer) throws IOException {
		return deserializer.fromJson(metadata);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void close() throws IOException {}
}
