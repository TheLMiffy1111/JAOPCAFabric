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

public class ImplosionCompressorRecipeSupplier implements Supplier<RebornRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int inputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int power;
	public final int time;

	public ImplosionCompressorRecipeSupplier(Identifier key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int power, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
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
		RebornIngredient secondIng = TechRebornHelper.INSTANCE.getRebornIngredient(secondInput, secondInputCount);
		if(secondIng.getPreviewStacks().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+secondInput);
		}
		ings.add(ing);
		ings.add(secondIng);
		ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, secondOutput);
		}
		ItemStack secondStack = MiscHelperImpl.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		if(secondStack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, secondOutput);
		}
		stacks.add(stack);
		stacks.add(secondStack);
		return new RebornRecipe(ModRecipes.IMPLOSION_COMPRESSOR, key, ings, stacks, power, time);
	}
}
