package thelm.jaopca.api.items;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.common.base.Function;

import net.minecraft.util.Rarity;
import thelm.jaopca.api.blocks.BlockFormSettings;
import thelm.jaopca.api.forms.FormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface ItemFormSettings extends FormSettings {

	ItemFormSettings setItemCreator(ItemCreator itemCreator);

	ItemCreator getItemCreator();

	ItemFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	ItemFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	ItemFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	ItemFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
