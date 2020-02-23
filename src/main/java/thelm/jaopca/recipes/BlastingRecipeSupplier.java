package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import thelm.jaopca.utils.MiscHelperImpl;

public class BlastingRecipeSupplier implements Supplier<BlastingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final float experience;
	public final int time;

	public BlastingRecipeSupplier(Identifier key, Object input, Object output, int count, float experience, int time) {
		this(key, "", input, output, count, experience, time);
	}

	public BlastingRecipeSupplier(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.count = count;
		this.experience = experience;
		this.time = time;
	}

	@Override
	public BlastingRecipe get() {
		Ingredient ing = MiscHelperImpl.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new BlastingRecipe(key, group, ing, stack, experience, time);
	}
}
