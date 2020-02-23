package thelm.jaopca.items;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.common.base.Function;

import net.minecraft.util.Rarity;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.items.ItemCreator;
import thelm.jaopca.api.items.ItemFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public class ItemFormSettingsImpl implements ItemFormSettings {

	ItemFormSettingsImpl() {}

	private ItemCreator itemCreator = JAOPCAItem::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->64;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private Function<IMaterial, Rarity> displayRarityFunction = material->material.getDisplayRarity();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

	@Override
	public FormType getType() {
		return ItemFormTypeImpl.INSTANCE;
	}

	@Override
	public ItemFormSettings setItemCreator(ItemCreator itemCreator) {
		this.itemCreator = itemCreator;
		return this;
	}

	@Override
	public ItemCreator getItemCreator() {
		return itemCreator;
	}

	@Override
	public ItemFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction) {
		this.itemStackLimitFunction = itemStackLimitFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getItemStackLimitFunction() {
		return itemStackLimitFunction;
	}

	@Override
	public ItemFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction) {
		this.hasEffectFunction = hasEffectFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getHasEffectFunction() {
		return hasEffectFunction;
	}

	@Override
	public ItemFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Rarity> getDisplayRarityFunction() {
		return displayRarityFunction;
	}

	@Override
	public ItemFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction) {
		this.burnTimeFunction = burnTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getBurnTimeFunction() {
		return burnTimeFunction;
	}
}
