package thelm.jaopca.registries;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryHandler {

	private RegistryHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Table<MutableRegistry, Identifier, Object> REGISTRY_ENTRIES = Tables.newCustomTable(new HashMap<>(), TreeMap::new);

	public static <T> void registerToRegistry(MutableRegistry<T> registry, Identifier id, T entry) {
		Objects.requireNonNull(registry);
		Objects.requireNonNull(id);
		Objects.requireNonNull(entry);
		REGISTRY_ENTRIES.put(registry, id, entry);
	}
	
	public static void registerToRegistry(Identifier registry, Identifier id, Object entry) {
		Objects.requireNonNull(registry);
		Objects.requireNonNull(id);
		Objects.requireNonNull(entry);
		REGISTRY_ENTRIES.put(Registry.REGISTRIES.get(registry), id, entry);
	}

	public static void registerAll() {
		REGISTRY_ENTRIES.rowMap().forEach((registry, map)->{
			map.forEach(registry::add);
		});
		REGISTRY_ENTRIES.clear();
	}
}
