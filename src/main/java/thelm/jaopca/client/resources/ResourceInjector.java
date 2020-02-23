package thelm.jaopca.client.resources;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePackImpl;

public class ResourceInjector {

	private static final Logger LOGGER = LogManager.getLogger();

	public static class PackProvider implements ResourcePackProvider {

		public static final PackProvider INSTANCE = new PackProvider();

		@Override
		public <T extends ResourcePackProfile> void register(Map<String, T> packList, ResourcePackProfile.Factory<T> factory) {
			T packInfo = ResourcePackProfile.of("inmemory:jaopca", true, ()->{
				InMemoryResourcePackImpl pack = new InMemoryResourcePackImpl("inmemory:jaopca");
				ModuleHandler.onCreateResourcePack(pack);
				return pack;
			}, factory, ResourcePackProfile.InsertionPosition.BOTTOM);
		}
	}
}
