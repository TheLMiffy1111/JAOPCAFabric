package thelm.jaopca.compat.techreborn.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import reborncore.common.crafting.RebornRecipe;
import reborncore.common.crafting.ingredient.RebornIngredient;
import techreborn.init.ModRecipes;
import thelm.jaopca.compat.techreborn.TechRebornHelper;
import thelm.jaopca.utils.MiscHelperImpl;

public class GrinderRecipeSupplier implements Supplier<RebornRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int power;
	public final int time;

	public GrinderRecipeSupplier(Identifier key, Object input, int inputCount, Object output, int outputCount, int power, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.power = power;
		this.time = time;
	}

	@Override
	public RebornRecipe get() {
		DefaultedList<RebornIngredient> ings = DefaultedList.of();
		DefaultedList<ItemStack> stacks = DefaultedList.of();
		RebornIngredient ing = TechRebornHelper.INSTANCE.getRebornIngredient(input, inputCount);
		if(ing.getPreviewStacks().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ings.add(ing);
		ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		stacks.add(stack);
		return new RebornRecipe(ModRecipes.GRINDER, key, ings, stacks, power, time);
	}
}
