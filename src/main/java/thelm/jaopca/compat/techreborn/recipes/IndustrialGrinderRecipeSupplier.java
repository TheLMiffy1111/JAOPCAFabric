package thelm.jaopca.compat.techreborn.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import reborncore.common.crafting.ingredient.RebornIngredient;
import reborncore.common.fluid.container.FluidInstance;
import techreborn.api.recipe.recipes.IndustrialGrinderRecipe;
import techreborn.init.ModRecipes;
import thelm.jaopca.compat.techreborn.TechRebornHelper;
import thelm.jaopca.utils.MiscHelperImpl;

public class IndustrialGrinderRecipeSupplier implements Supplier<IndustrialGrinderRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int inputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object[] output;
	public final int power;
	public final int time;

	public IndustrialGrinderRecipeSupplier(Identifier key, Object input, int inputCount, Object fluidInput, int fluidInputAmount, int power, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.power = power;
		this.time = time;
	}

	@Override
	public IndustrialGrinderRecipe get() {
		DefaultedList<RebornIngredient> ings = DefaultedList.of();
		DefaultedList<ItemStack> stacks = DefaultedList.of();
		RebornIngredient ing = TechRebornHelper.INSTANCE.getRebornIngredient(input, inputCount);
		if(ing.getPreviewStacks().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidInstance fluid = TechRebornHelper.INSTANCE.getFluidInstance(fluidInput, fluidInputAmount);
		if(fluid.isEmpty()) {
			fluid = FluidInstance.EMPTY;
		}
		ings.add(ing);
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			stacks.add(stack);
		}
		return new IndustrialGrinderRecipe(ModRecipes.INDUSTRIAL_GRINDER, key, ings, stacks, power, time, fluid);
	}
}
