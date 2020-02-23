package thelm.jaopca.items;

import java.util.Optional;
import java.util.OptionalInt;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.items.ItemFormSettings;
import thelm.jaopca.api.items.MaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCAItem extends Item implements MaterialFormItem {

	private final Form form;
	private final IMaterial material;
	protected final ItemFormSettings settings;

	protected OptionalInt itemStackLimit = OptionalInt.empty();
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<Rarity> rarity = Optional.empty();

	public JAOPCAItem(Form form, IMaterial material, ItemFormSettings settings) {
		super(new Item.Settings().
				maxCount(settings.getItemStackLimitFunction().applyAsInt(material)).
				group(ItemFormTypeImpl.getItemGroup()));
		this.form = form;
		this.material = material;
		this.settings = settings;

		int burnTime = settings.getBurnTimeFunction().applyAsInt(material);
		if(burnTime >= 0) {
			FuelRegistry.INSTANCE.add(this, burnTime);
		}
	}

	@Override
	public Form getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(material));
		}
		return hasEffect.get() || super.hasEnchantmentGlint(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(material));
		}
		return rarity.get();
	}

	@Override
	public Text getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+form.getName(), material, getTranslationKey());
	}
}
