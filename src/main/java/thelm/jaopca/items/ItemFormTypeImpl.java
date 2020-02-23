package thelm.jaopca.items;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.items.ItemFormSettings;
import thelm.jaopca.api.items.ItemFormType;
import thelm.jaopca.api.items.ItemInfo;
import thelm.jaopca.api.items.MaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.ItemFormSettingsDeserializer;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class ItemFormTypeImpl implements ItemFormType {

	private ItemFormTypeImpl() {};

	public static final ItemFormTypeImpl INSTANCE = new ItemFormTypeImpl();
	private static final TreeSet<Form> FORMS = new TreeSet<>();
	private static final TreeBasedTable<Form, IMaterial, MaterialFormItem> ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<Form, IMaterial, ItemInfo> ITEM_INFOS = TreeBasedTable.create();
	private static ItemGroup itemGroup;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "item";
	}

	@Override
	public void addForm(Form form) {
		FORMS.add(form);
	}

	@Override
	public Set<Form> getForms() {
		return Collections.unmodifiableNavigableSet(FORMS);
	}

	@Override
	public boolean shouldRegister(Form form, IMaterial material) {
		Identifier tagLocation = MiscHelperImpl.INSTANCE.getTagIdentifier(form.getSecondaryName(), material.getName());
		return !ApiImpl.INSTANCE.getItemTags().contains(tagLocation);
	}

	@Override
	public ItemInfo getMaterialFormInfo(Form form, IMaterial material) {
		ItemInfo info = ITEM_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new ItemInfoImpl(ITEMS.get(form, material));
			ITEM_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public ItemFormSettings getNewSettings() {
		return new ItemFormSettingsImpl();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.registerTypeAdapter(Rarity.class, EnumDeserializer.INSTANCE);
	}

	@Override
	public ItemFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return ItemFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	public static void registerEntries() {
		MiscHelperImpl helper = MiscHelperImpl.INSTANCE;
		for(Form form : FORMS) {
			ItemFormSettings settings = (ItemFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				Identifier registryName = new Identifier("jaopca", form.getName()+'.'+material.getName());

				MaterialFormItem materialFormItem = settings.getItemCreator().create(form, material, settings);
				Item item = materialFormItem.asItem();
				ITEMS.put(form, material, materialFormItem);
				RegistryHandler.registerToRegistry(Registry.ITEM, registryName, item);

				Supplier<Item> itemSupplier = ()->item;
				DataInjector.registerItemTag(helper.createIdentifier(secondaryName), itemSupplier);
				DataInjector.registerItemTag(helper.getTagIdentifier(secondaryName, material.getName()), itemSupplier);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerItemTag(helper.getTagIdentifier(secondaryName, alternativeName), itemSupplier);
				}
			}
		}
	}

	public static ItemGroup getItemGroup() {
		if(itemGroup == null) {
			itemGroup = FabricItemGroupBuilder.
					build(new Identifier("jaopca", "items"), ()->new ItemStack(Items.GLOWSTONE_DUST));
		}
		return itemGroup;
	}

	public static Collection<MaterialFormItem> getItems() {
		return ITEMS.values();
	}
}
