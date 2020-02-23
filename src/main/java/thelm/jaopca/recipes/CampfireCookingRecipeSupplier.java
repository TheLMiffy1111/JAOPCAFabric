package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import thelm.jaopca.utils.MiscHelperImpl;

public class CampfireCookingRecipeSupplier implements Supplier<CampfireCookingRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;
	public final int time;

	public CampfireCookingRecipeSupplier(Identifier key, Object input, Object output, int count, int time) {
		this(key, "", input, output, count, time);
	}

	public CampfireCookingRecipeSupplier(Identifier key, String group, Object input, Object output, int count, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.count = count;
		this.time = time;
	}

	@Override
	public CampfireCookingRecipe get() {
		Ingredient ing = MiscHelperImpl.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		return new CampfireCookingRecipe(key, group, ing, stack, 0, time);
	}
}
