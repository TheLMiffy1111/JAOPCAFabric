package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import thelm.jaopca.utils.MiscHelperImpl;

public class ShapelessRecipeSupplier implements Supplier<ShapelessRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final String group;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapelessRecipeSupplier(Identifier key, Object output, int count, Object... input) {
		this(key, "", output, count, input);
	}

	public ShapelessRecipeSupplier(Identifier key, String group, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public ShapelessRecipe get() {
		ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		DefaultedList<Ingredient> inputList = DefaultedList.of();
		for(Object in : input){
			Ingredient ing = MiscHelperImpl.INSTANCE.getIngredient(in);
			if(ing.isEmpty()) {
				LOGGER.warn("Empty ingredient in recipe {}: {}", key, in);
			}
			else {
				inputList.add(ing);
			}
		}
		return new ShapelessRecipe(key, group, stack, inputList);
	}
}
