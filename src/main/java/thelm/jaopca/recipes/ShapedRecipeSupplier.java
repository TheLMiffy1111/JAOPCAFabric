package thelm.jaopca.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import thelm.jaopca.utils.MiscHelperImpl;

public class ShapedRecipeSupplier implements Supplier<ShapedRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final String group;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapedRecipeSupplier(Identifier key, Object output, int count, Object... input) {
		this(key, "", output, count, input);
	}

	public ShapedRecipeSupplier(Identifier key, String group, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public ShapedRecipe get() {
		ItemStack stack = MiscHelperImpl.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		int width = 0, height = 0;
		String shape = "";
		int idx = 0;
		if(input[idx] instanceof String[]) {
			String[] parts = ((String[])input[idx++]);
			for(String s : parts) {
				width = s.length();
				shape += s;
			}
			height = parts.length;
		}
		else {
			while(input[idx] instanceof String) {
				String s = (String)input[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}
		if(width * height != shape.length() || shape.length() == 0) {
			throw new IllegalArgumentException("Invalid shape in recipe "+key+": "+shape+","+width+"x"+height);
		}
		Map<Character, Ingredient> itemMap = new HashMap<>();
		itemMap.put(' ', Ingredient.EMPTY);
		for(; idx < input.length; idx += 2)  {
			Character chr = (Character)input[idx];
			Object in = input[idx+1];
			Ingredient ing = MiscHelperImpl.INSTANCE.getIngredient(in);
			if(' ' == chr.charValue()) {
				throw new IllegalArgumentException("Invalid key entry in recipe "+key+": ' ' is a reserved symbol");
			}
			if(ing.isEmpty()) {
				LOGGER.warn("Empty ingredient in recipe {}: {}", key, in);
			}
			itemMap.put(chr, ing);
		}
		DefaultedList<Ingredient> inputList = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
		int x = 0;
		for(char chr : shape.toCharArray()) {
			Ingredient ing = itemMap.get(chr);
			if(ing == null) {
				throw new IllegalArgumentException("Pattern in recipe "+key+" references symbol '"+chr+"' but it's not defined in the key");
			}
			inputList.set(x++, ing);
		}
		return new ShapedRecipe(key, group, width, height, inputList, stack);
	}
}
