package thelm.jaopca.client.colors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.fluids.MaterialFormBucketItem;
import thelm.jaopca.api.fluids.MaterialFormFluidBlock;
import thelm.jaopca.api.items.MaterialFormItem;
import thelm.jaopca.api.materialforms.MaterialForm;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.client.extensions.SpriteExtension;
import thelm.jaopca.client.mixin.BakedQuadAccessor;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.fluids.FluidFormTypeImpl;
import thelm.jaopca.items.ItemFormTypeImpl;

public class ColorHandler {

	public static final BlockColorProvider BLOCK_COLOR = (state, world, pos, tintIndex)->{
		if(tintIndex == 0) {
			Block block = state.getBlock();
			if(block instanceof MaterialForm) {
				MaterialForm materialForm = (MaterialForm)block;
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static final ItemColorProvider ITEM_COLOR = (stack, tintIndex)->{
		if(tintIndex == 0 || tintIndex == 2) {
			Item item = stack.getItem();
			if(item instanceof MaterialForm) {
				MaterialForm materialForm = (MaterialForm)item;
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static void setup() {
		ColorProviderRegistry<Block, BlockColorProvider> blockColors = ColorProviderRegistry.BLOCK;
		ColorProviderRegistry<ItemConvertible, ItemColorProvider> itemColors = ColorProviderRegistry.ITEM;
		for(MaterialFormBlock block : BlockFormTypeImpl.getBlocks()) {
			blockColors.register(BLOCK_COLOR, block.asBlock());
		}
		for(MaterialFormBlockItem blockItem : BlockFormTypeImpl.getBlockItems()) {
			itemColors.register(ITEM_COLOR, blockItem.asBlockItem());
		}
		for(MaterialFormItem item : ItemFormTypeImpl.getItems()) {
			itemColors.register(ITEM_COLOR, item.asItem());
		}
		for(MaterialFormFluidBlock fluidBlock : FluidFormTypeImpl.getFluidBlocks()) {
			blockColors.register(BLOCK_COLOR, fluidBlock.asBlock());
		}
		for(MaterialFormBucketItem bucketItem : FluidFormTypeImpl.getBucketItems()) {
			itemColors.register(ITEM_COLOR, bucketItem.asItem());
		}
	}

	public static int getAverageColor(Tag<Item> tag) {
		Vector4f color = weightedAverageColor(tag.values(), ConfigHandler.gammaValue);
		return toColorInt(color);
	}

	public static Vector4f weightedAverageColor(Collection<Item> items, double gammaValue) {
		if(items.isEmpty()) {
			return new Vector4f(1, 1, 1, 0);
		}
		List<Vector4f> colors = items.stream().map(ItemStack::new).
				map(stack->weightedAverageColor(stack, gammaValue)).
				collect(Collectors.toList());
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(ItemStack stack, double gammaValue) {
		List<BakedQuad> quads = getBakedQuads(stack);
		if(quads.isEmpty()) {
			return new Vector4f(1, 1, 1, 0);
		}
		List<Vector4f> colors = new ArrayList<>();
		for(BakedQuad quad : quads) {
			Vector4f color = weightedAverageColor(((BakedQuadAccessor)quad).getSprite(), gammaValue);
			color = tintColor(color, getTint(stack, quad));
			colors.add(color);
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(Sprite texture, double gammaValue) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		int frameCount = texture.getFrameCount();
		if(width <= 0 || height <= 0 || frameCount <= 0) {
			return new Vector4f(1, 1, 1, 0);
		}
		List<Vector4f> colors = new ArrayList<>();
		for(int frameIndex = 0; frameIndex < frameCount; ++frameIndex) {
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					int color = ((SpriteExtension)texture).jaopca_getPixelRGBA(frameIndex, x, y);
					colors.add(toColorTuple(color));
				}
			}
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(List<Vector4f> colors, double gammaValue) {
		if(colors.isEmpty()) {
			return new Vector4f(1, 1, 1, 0);
		}
		double weight, r = 0, g = 0, b = 0, totalWeight = 0;
		if(gammaValue == 0) {
			r = 1;
			g = 1;
			b = 1;
			for(Vector4f color : colors) {
				totalWeight += weight = color.getW();
				r *= color.getX()*weight;
				g *= color.getY()*weight;
				b *= color.getZ()*weight;
			}
			r = Math.pow(r, 1/totalWeight);
			g = Math.pow(g, 1/totalWeight);
			b = Math.pow(b, 1/totalWeight);
		}
		else {
			for(Vector4f color : colors) {
				totalWeight += weight = color.getW();
				r += Math.pow(color.getX(), gammaValue)*weight;
				g += Math.pow(color.getY(), gammaValue)*weight;
				b += Math.pow(color.getZ(), gammaValue)*weight;
			}
			r = Math.pow(r/totalWeight, 1/gammaValue);
			g = Math.pow(g/totalWeight, 1/gammaValue);
			b = Math.pow(b/totalWeight, 1/gammaValue);
		}
		return new Vector4f(
				(float)MathHelper.clamp(r, 0, 1),
				(float)MathHelper.clamp(g, 0, 1),
				(float)MathHelper.clamp(b, 0, 1),
				(float)MathHelper.clamp(totalWeight/colors.size(), 0, 1)
				);
	}

	public static Vector4f toColorTuple(int color) {
		return new Vector4f(
				(color    &0xFF)/255F,
				(color>> 8&0xFF)/255F,
				(color>>16&0xFF)/255F,
				(color>>24&0xFF)/255F
				);
	}

	public static Vector4f tintColor(Vector4f color, int tint) {
		return new Vector4f(
				color.getX()*(tint>>16&0xFF)/255F,
				color.getY()*(tint>> 8&0xFF)/255F,
				color.getZ()*(tint    &0xFF)/255F,
				color.getW()
				);
	}

	public static int toColorInt(Vector4f color) {
		int ret = 0;
		ret |= (Math.round(MathHelper.clamp(color.getX()*255, 0, 255))&0xFF)<<16;
		ret |= (Math.round(MathHelper.clamp(color.getY()*255, 0, 255))&0xFF)<< 8;
		ret |= (Math.round(MathHelper.clamp(color.getZ()*255, 0, 255))&0xFF);
		return ret;
	}

	public static List<BakedQuad> getBakedQuads(ItemStack stack) {
		List<BakedQuad> quads = new ArrayList<>();
		BakedModel model = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(stack, null, null);
		model.getQuads(null, null, new Random(0)).stream().filter(quad->quad.getFace() == Direction.SOUTH).forEach(quads::add);
		for(Direction facing : Direction.values()) {
			model.getQuads(null, facing, new Random(0)).stream().filter(quad->quad.getFace() == Direction.SOUTH).forEach(quads::add);
		}
		return quads;
	}

	public static int getTint(ItemStack stack, BakedQuad quad) {
		ItemColorProvider itemColor = ColorProviderRegistry.ITEM.get(stack.getItem());
		return itemColor != null ? itemColor.getColor(stack, quad.getColorIndex()) : 0xFFFFFF;
	}
}
