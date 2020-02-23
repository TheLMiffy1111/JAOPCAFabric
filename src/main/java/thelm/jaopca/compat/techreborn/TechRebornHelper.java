package thelm.jaopca.compat.techreborn;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import reborncore.common.crafting.ingredient.DummyIngredient;
import reborncore.common.crafting.ingredient.IngredientManager;
import reborncore.common.crafting.ingredient.RebornIngredient;
import reborncore.common.crafting.ingredient.StackIngredient;
import reborncore.common.crafting.ingredient.TagIngredient;
import reborncore.common.crafting.ingredient.WrappedIngredient;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import thelm.jaopca.api.fluids.FluidProvider;
import thelm.jaopca.compat.techreborn.recipes.CompressorRecipeSupplier;
import thelm.jaopca.compat.techreborn.recipes.GrinderRecipeSupplier;
import thelm.jaopca.compat.techreborn.recipes.ImplosionCompressorRecipeSupplier;
import thelm.jaopca.compat.techreborn.recipes.IndustrialGrinderRecipeSupplier;
import thelm.jaopca.compat.techreborn.recipes.ScrapboxRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class TechRebornHelper {

	public static final TechRebornHelper INSTANCE = new TechRebornHelper();

	private TechRebornHelper() {}

	public RebornIngredient getRebornIngredient(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getRebornIngredient(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof RebornIngredient) {
			return (RebornIngredient)obj;
		}
		else if(obj instanceof Ingredient) {
			return new WrappedIngredient((Ingredient)obj);
		}
		else if(obj instanceof String) {
			Identifier id = new Identifier((String)obj);
			return new TagIngredient(id, MiscHelperImpl.INSTANCE.makeItemWrapperTag(id), Optional.of(count));
		}
		else if(obj instanceof Identifier) {
			Identifier id = (Identifier)obj;
			return new TagIngredient(id, MiscHelperImpl.INSTANCE.makeItemWrapperTag(id), Optional.of(count));
		}
		else if(obj instanceof Tag<?>) {
			Tag<Item> tag = (Tag<Item>)obj;
			return new TagIngredient(tag.getId(), tag, Optional.of(count));
		}
		else if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			return new StackIngredient(Arrays.asList(stack), Optional.of(stack.getCount()), Optional.ofNullable(stack.getTag()), false);
		}
		else if(obj instanceof ItemStack[]) {
			return new StackIngredient(Arrays.asList((ItemStack[])obj), Optional.of(count), Optional.empty(), false);
		}
		else if(obj instanceof ItemConvertible) {
			return new StackIngredient(Arrays.asList(new ItemStack((ItemConvertible)obj)), Optional.of(count), Optional.empty(), false);
		}
		else if(obj instanceof ItemConvertible[]) {
			return new StackIngredient(Arrays.stream((ItemConvertible[])obj).map(ItemStack::new).collect(Collectors.toList()), Optional.of(count), Optional.empty(), false);
		}
		else if(obj instanceof JsonElement) {
			return IngredientManager.deserialize((JsonElement)obj);
		}
		return new DummyIngredient();
	}

	public FluidInstance getFluidInstance(Object obj, int amount) {
		return getFluidInstance(obj, FluidValue.fromRaw(amount));
	}

	public FluidInstance getFluidInstance(Object obj, FluidValue amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidInstance(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidInstance) {
			return ((FluidInstance)obj);
		}
		else if(obj instanceof Fluid) {
			return new FluidInstance((Fluid)obj, amount);
		}
		else if(obj instanceof FluidProvider) {
			return new FluidInstance(((FluidProvider)obj).asFluid(), amount);
		}
		else if(obj instanceof String) {
			return getPreferredFluidInstance(MiscHelperImpl.INSTANCE.makeFluidWrapperTag(new Identifier((String)obj)).values(), amount);
		}
		else if(obj instanceof Identifier) {
			return getPreferredFluidInstance(MiscHelperImpl.INSTANCE.makeFluidWrapperTag((Identifier)obj).values(), amount);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredFluidInstance(((Tag<Fluid>)obj).values(), amount);
		}
		return FluidInstance.EMPTY;
	}

	public FluidInstance getPreferredFluidInstance(Collection<Fluid> collection, FluidValue amount) {
		return new FluidInstance(collection.stream().findFirst().orElse(Fluids.EMPTY), amount);
	}

	public boolean registerCompressorRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeSupplier(key, input, inputCount, output, outputCount, power, time));
	}

	public boolean registerGrinderRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSupplier(key, input, inputCount, output, outputCount, power, time));
	}

	public boolean registerImplosionCompressorRecipe(Identifier key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressorRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, secondOutput, secondOutputCount, power, time));
	}

	public boolean registerIndustrialGrinderRecipe(Identifier key, Object input, int inputCount, Object fluidInput, int fluidInputAmount, int power, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IndustrialGrinderRecipeSupplier(key, input, inputCount, fluidInput, fluidInputAmount, power, time, output));
	}

	public boolean registerScrapboxRecipe(Identifier key, Object output, int outputCount, int power, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ScrapboxRecipeSupplier(key, output, outputCount, power, time));
	}
}
