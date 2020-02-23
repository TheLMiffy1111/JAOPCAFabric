package thelm.jaopca.client;

import java.util.Map;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.fluids.MaterialFormFluid;
import thelm.jaopca.api.fluids.MaterialFormFluidBlock;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.client.models.ModelHandler;
import thelm.jaopca.client.resources.ResourceInjector;
import thelm.jaopca.fluids.FluidFormTypeImpl;
import thelm.jaopca.modules.ModuleHandler;

public class JAOPCAClient implements ClientModInitializer {

	public static final JAOPCAClient INSTANCE = new JAOPCAClient();

	@Override
	public void onInitializeClient() {
		MinecraftClient.getInstance().getResourcePackManager().registerProvider(ResourceInjector.PackProvider.INSTANCE);

		BlockRenderLayerMap renderLayerMap = BlockRenderLayerMap.INSTANCE;
		for(MaterialFormBlock block : BlockFormTypeImpl.getBlocks()) {
			renderLayerMap.putBlock(block.asBlock(), RenderLayer.getTranslucent());
		}
		for(MaterialFormFluid fluid : FluidFormTypeImpl.getFluids()) {
			renderLayerMap.putFluid(fluid.asFluid(), RenderLayer.getTranslucent());
		}
		for(MaterialFormFluidBlock fluidBlock : FluidFormTypeImpl.getFluidBlocks()) {
			renderLayerMap.putBlock(fluidBlock.asBlock(), RenderLayer.getTranslucent());
		}

		ModuleHandler.onClientSetup();

		ModelHandler.registerModels();

		ColorHandler.setup();
	}

	public void onModelBake(Map<Identifier, BakedModel> models) {
		ModelHandler.remapModels(models);
	}
}
