package thelm.jaopca.client.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.fluids.MaterialFormBucketItem;
import thelm.jaopca.api.fluids.MaterialFormFluidBlock;
import thelm.jaopca.api.items.MaterialFormItem;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.fluids.FluidFormTypeImpl;
import thelm.jaopca.items.ItemFormTypeImpl;

public class ModelHandler {

	private static final Multimap<ModelIdentifier, ModelIdentifier> REMAPS = LinkedHashMultimap.create();

	public static void registerModels() {
		ModelLoadingRegistry modelLoadingRegistry = ModelLoadingRegistry.INSTANCE;
		modelLoadingRegistry.registerAppender((resourceManager, out)->{
			for(MaterialFormBlock materialFormBlock : BlockFormTypeImpl.getBlocks()) {
				Block block = materialFormBlock.asBlock();
				Identifier location = Registry.BLOCK.getId(block);
				location = new Identifier(location.getNamespace(), "blockstates/"+location.getPath()+".json");
				if(false || resourceManager.containsResource(location)) {
					continue;
				}
				block.getStateManager().getStates().forEach((state)->{
					String propertyMapString = BlockModels.propertyMapToString(state.getEntries());
					ModelIdentifier modelLocation = new ModelIdentifier(Registry.BLOCK.getId(block), propertyMapString);
					ModelIdentifier defaultModelLocation = new ModelIdentifier(
							"jaopca:"+materialFormBlock.getMaterial().getModelType()+'/'+materialFormBlock.getForm().getName(),
							propertyMapString);
					REMAPS.put(defaultModelLocation, modelLocation);
				});
			}
			for(MaterialFormBlockItem materialFormBlockItem : BlockFormTypeImpl.getBlockItems()) {
				BlockItem blockItem = materialFormBlockItem.asBlockItem();
				Identifier location = Registry.ITEM.getId(blockItem);
				location = new Identifier(location.getNamespace(), "item/models/"+location.getPath()+".json");
				if(false || resourceManager.containsResource(location)) {
					continue;
				}
				ModelIdentifier modelLocation = new ModelIdentifier(Registry.ITEM.getId(blockItem), "inventory");
				ModelIdentifier defaultModelLocation = new ModelIdentifier(
						"jaopca:"+materialFormBlockItem.getMaterial().getModelType()+'/'+materialFormBlockItem.getForm().getName(),
						"inventory");
				REMAPS.put(defaultModelLocation, modelLocation);
			}
			for(MaterialFormItem materialFormItem : ItemFormTypeImpl.getItems()) {
				Item item = materialFormItem.asItem();
				Identifier location = Registry.ITEM.getId(item);
				location = new Identifier(location.getNamespace(), "item/models/"+location.getPath()+".json");
				if(false || resourceManager.containsResource(location)) {
					continue;
				}
				ModelIdentifier modelLocation = new ModelIdentifier(Registry.ITEM.getId(item), "inventory");
				ModelIdentifier defaultModelLocation = new ModelIdentifier(
						"jaopca:"+materialFormItem.getMaterial().getModelType()+'/'+materialFormItem.getForm().getName(),
						"inventory");
				REMAPS.put(defaultModelLocation, modelLocation);
			}
			for(MaterialFormFluidBlock materialFormFluidBlock : FluidFormTypeImpl.getFluidBlocks()) {
				Block fluidBlock = materialFormFluidBlock.asBlock();
				Identifier location = Registry.BLOCK.getId(fluidBlock);
				location = new Identifier(location.getNamespace(), "blockstates/"+location.getPath()+".json");
				if(false || resourceManager.containsResource(location)) {
					continue;
				}
				fluidBlock.getStateManager().getStates().forEach((state)->{
					String propertyMapString = BlockModels.propertyMapToString(state.getEntries());
					ModelIdentifier modelLocation = new ModelIdentifier(Registry.BLOCK.getId(fluidBlock), propertyMapString);
					ModelIdentifier defaultModelLocation = new ModelIdentifier(
							"jaopca:"+materialFormFluidBlock.getMaterial().getModelType()+'/'+materialFormFluidBlock.getForm().getName(),
							propertyMapString);
					REMAPS.put(defaultModelLocation, modelLocation);
				});
			}
			for(MaterialFormBucketItem materialFormBucketItem : FluidFormTypeImpl.getBucketItems()) {
				Item bucketItem = materialFormBucketItem.asItem();
				Identifier location = Registry.ITEM.getId(bucketItem);
				location = new Identifier(location.getNamespace(), "item/models/"+location.getPath()+".json");
				if(false || resourceManager.containsResource(location)) {
					continue;
				}
				ModelIdentifier modelLocation = new ModelIdentifier(Registry.ITEM.getId(bucketItem), "inventory");
				ModelIdentifier defaultModelLocation = new ModelIdentifier(
						"jaopca:"+materialFormBucketItem.getMaterial().getModelType()+'/'+materialFormBucketItem.getForm().getName(),
						"inventory");
				REMAPS.put(defaultModelLocation, modelLocation);
			}
			REMAPS.keySet().forEach(out);
		});
	}

	public static void remapModels(Map<Identifier, BakedModel> models) {
		ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
		BakedModel missingModel = models.get(ModelLoader.MISSING);
		for(Map.Entry<ModelIdentifier, Collection<ModelIdentifier>> entry : REMAPS.asMap().entrySet()) {
			BakedModel defaultModel = models.getOrDefault(entry.getKey(), missingModel);
			for(Identifier modelLocation : entry.getValue()) {
				models.put(modelLocation, defaultModel);
			}
		}
	}
}
