package thelm.jaopca;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.fluids.FluidFormTypeImpl;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.mixin.ReloadableResourceManagerImplAccessor;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCA implements ModInitializer {

	public static final JAOPCA INSTANCE = new JAOPCA();
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		ApiImpl.INSTANCE.init();

		BlockFormTypeImpl.init();
		ItemFormTypeImpl.init();
		FluidFormTypeImpl.init();
		DataCollector.collectData();
		ModuleHandler.findModules();
		ConfigHandler.setupMainConfig();
		ModuleHandler.onMainConfigSetupComplete();
		MaterialHandler.findMaterials();
		ConfigHandler.setupMaterialConfigs();
		FormTypeHandler.setupGson();
		ConfigHandler.setupCustomFormConfig();
		ConfigHandler.setupModuleConfigsPre();
		FormHandler.collectForms();
		ModuleHandler.computeValidMaterials();
		FormHandler.computeValidMaterials();
		ConfigHandler.setupModuleConfigs();
		BlockFormTypeImpl.registerEntries();
		ItemFormTypeImpl.registerEntries();
		FluidFormTypeImpl.registerEntries();
		ModuleHandler.onMaterialComputeComplete();

		RegistryHandler.registerAll();

		ModuleHandler.onCommonSetup();
	}

	public void onStartServer(MinecraftServer server) {
		List<ResourceReloadListener> reloadListeners = ((ReloadableResourceManagerImplAccessor)server.getDataManager()).getListeners();
		DataInjector instance = DataInjector.getNewInstance(server.getRecipeManager());
		reloadListeners.add(reloadListeners.indexOf(server.getRecipeManager())+1, new SinglePreparationResourceReloadListener<Object>() {
			@Override
			protected Object prepare(ResourceManager resourceManager, Profiler profiler) {
				return null;
			}
			@Override
			protected void apply(Object splashList, ResourceManager resourceManager, Profiler profiler) {
				instance.injectRecipes(resourceManager);
			}
		});
		server.getDataPackManager().registerProvider(DataInjector.PackProvider.INSTANCE);
	}
}
