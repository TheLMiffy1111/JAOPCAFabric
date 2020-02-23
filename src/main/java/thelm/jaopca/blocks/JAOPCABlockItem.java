package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalInt;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import thelm.jaopca.api.blocks.BlockFormSettings;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materialforms.MaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlockItem extends BlockItem implements MaterialFormBlockItem {

	protected final BlockFormSettings settings;

	protected OptionalInt itemStackLimit = OptionalInt.empty();
	protected Optional<Boolean> beaconPayment = Optional.empty();
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<Rarity> rarity = Optional.empty();
	protected OptionalInt burnTime = OptionalInt.empty();

	public JAOPCABlockItem(MaterialFormBlock block, BlockFormSettings settings) {
		super(block.asBlock(), new Item.Settings().
				maxCount(settings.getItemStackLimitFunction().applyAsInt(block.getMaterial())).
				group(ItemFormTypeImpl.getItemGroup()));
		this.settings = settings;

		int burnTime = settings.getBurnTimeFunction().applyAsInt(block.getMaterial());
		if(burnTime >= 0) {
			FuelRegistry.INSTANCE.add(this, burnTime);
		}
	}

	@Override
	public Form getForm() {
		return ((MaterialForm)getBlock()).getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return ((MaterialForm)getBlock()).getMaterial();
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(getMaterial()));
		}
		return hasEffect.get() || super.hasEnchantmentGlint(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(getMaterial()));
		}
		return rarity.get();
	}

	@Override
	public Text getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+getForm().getName(), getMaterial(), getTranslationKey());
	}
}
