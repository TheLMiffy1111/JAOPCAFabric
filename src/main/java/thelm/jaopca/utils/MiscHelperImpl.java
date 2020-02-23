package thelm.jaopca.utils;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.config.ConfigHandler;

public class MiscHelperImpl implements MiscHelper {

	public static final MiscHelperImpl INSTANCE = new MiscHelperImpl();

	private MiscHelperImpl() {}

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	@Override
	public Identifier createIdentifier(String location, String defaultNamespace) {
		if(StringUtils.contains(location, ':')) {
			return new Identifier(location);
		}
		else {
			return new Identifier(defaultNamespace, location);
		}
	}

	@Override
	public Identifier createIdentifier(String location) {
		return createIdentifier(location, ConfigHandler.defaultNamespace);
	}

	@Override
	public Identifier getTagIdentifier(String form, String material) {
		return ConfigHandler.tagFormat.getTagIdentifier(form, material);
	}

	@Override
	public ItemStack getItemStack(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemStack(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemStack) {
			return ((ItemStack)obj);
		}
		else if(obj instanceof ItemConvertible) {
			return new ItemStack((ItemConvertible)obj, count);
		}
		else if(obj instanceof String) {
			return getPreferredItemStack(makeItemWrapperTag(new Identifier((String)obj)).values(), count);
		}
		else if(obj instanceof Identifier) {
			return getPreferredItemStack(makeItemWrapperTag((Identifier)obj).values(), count);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredItemStack(((Tag<Item>)obj).values(), count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public Ingredient getIngredient(Object obj) {
		if(obj instanceof Supplier<?>) {
			return getIngredient(((Supplier<?>)obj).get());
		}
		else if(obj instanceof Ingredient) {
			return (Ingredient)obj;
		}
		else if(obj instanceof String) {
			return Ingredient.fromTag(makeItemWrapperTag(new Identifier((String)obj)));
		}
		else if(obj instanceof Identifier) {
			return Ingredient.fromTag(makeItemWrapperTag((Identifier)obj));
		}
		else if(obj instanceof Tag<?>) {
			return Ingredient.fromTag((Tag<Item>)obj);
		}
		else if(obj instanceof ItemStack) {
			return Ingredient.ofStacks((ItemStack)obj);
		}
		else if(obj instanceof ItemStack[]) {
			return Ingredient.ofStacks((ItemStack[])obj);
		}
		else if(obj instanceof ItemConvertible) {
			return Ingredient.ofItems((ItemConvertible)obj);
		}
		else if(obj instanceof ItemConvertible[]) {
			return Ingredient.ofItems((ItemConvertible[])obj);
		}
		else if(obj instanceof JsonElement) {
			return Ingredient.fromJson((JsonElement)obj);
		}
		return Ingredient.EMPTY;
	}

	public Tag<Item> makeItemWrapperTag(Identifier location) {
		return new ItemTags.CachingTag(location);
	}

	public ItemStack getPreferredItemStack(Collection<Item> collection, int count) {
		return new ItemStack(collection.stream().findFirst().orElse(Items.AIR), count);
	}

	/*public FluidStack getFluidStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStack) {
			return ((FluidStack)obj);
		}
		else if(obj instanceof Fluid) {
			return new FluidStack((Fluid)obj, amount);
		}
		else if(obj instanceof IFluidProvider) {
			return new FluidStack(((IFluidProvider)obj).asFluid(), amount);
		}
		else if(obj instanceof String) {
			return getPreferredFluidStack(makeFluidWrapperTag(new Identifier((String)obj)).getAllElements(), amount);
		}
		else if(obj instanceof Identifier) {
			return getPreferredFluidStack(makeFluidWrapperTag((Identifier)obj).getAllElements(), amount);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredFluidStack(((Tag<Fluid>)obj).getAllElements(), amount);
		}
		return FluidStack.EMPTY;
	}*/

	public Tag<Fluid> makeFluidWrapperTag(Identifier location) {
		return new FluidTags.CachingTag(location);
	}

	/*public FluidStack getPreferredFluidStack(Collection<Fluid> collection, int amount) {
		return new FluidStack(collection.stream().findFirst().orElse(Fluids.EMPTY), amount);
	}*/

	public <T> Future<T> submitAsyncTask(Callable<T> task) {
		return executor.submit(task);
	}

	public Future<?> submitAsyncTask(Runnable task) {
		return executor.submit(task);
	}

	public int squareColorDifference(int color1, int color2) {
		int diffR = (color1<<16&0xFF)-(color2<<16&0xFF);
		int diffG = (color1<< 8&0xFF)-(color2<< 8&0xFF);
		int diffB = (color1    &0xFF)-(color2    &0xFF);
		return diffR*diffR+diffG*diffG+diffB*diffB;
	}

	public <T> T callIf(BooleanSupplier boolSupplier, Supplier<Callable<T>> toRun) {
		if(boolSupplier.getAsBoolean()) {
			try {
				return toRun.get().call();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public void runIf(BooleanSupplier boolSupplier, Supplier<Runnable> toRun) {
		if(boolSupplier.getAsBoolean()) {
			toRun.get().run();
		}
	}

	public <T> T callWhenOn(EnvType envType, Supplier<Callable<T>> toRun) {
		if(envType == FabricLoader.getInstance().getEnvironmentType()) {
			try {
				return toRun.get().call();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public void runWhenOn(EnvType envType, Supplier<Runnable> toRun) {
		if(envType == FabricLoader.getInstance().getEnvironmentType()) {
			toRun.get().run();
		}
	}

	public <T> T callForEnv(Supplier<Callable<T>> clientTarget, Supplier<Callable<T>> serverTarget) {
		try {
			switch(FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT:
				return clientTarget.get().call();
			case SERVER:
				return serverTarget.get().call();
			default:
				throw new IllegalArgumentException("UNSIDED?");
			}
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
