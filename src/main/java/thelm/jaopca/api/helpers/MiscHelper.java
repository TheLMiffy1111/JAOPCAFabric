package thelm.jaopca.api.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public interface MiscHelper {

	Identifier createIdentifier(String location, String defaultNamespace);

	Identifier createIdentifier(String location);

	Identifier getTagIdentifier(String form, String material);

	ItemStack getItemStack(Object obj, int count);

	Ingredient getIngredient(Object obj);
}
