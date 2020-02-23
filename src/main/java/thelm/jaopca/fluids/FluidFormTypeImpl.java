package thelm.jaopca.fluids;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thelm.jaopca.api.fluids.FluidFormSettings;
import thelm.jaopca.api.fluids.FluidFormType;
import thelm.jaopca.api.fluids.FluidInfo;
import thelm.jaopca.api.fluids.MaterialFormBucketItem;
import thelm.jaopca.api.fluids.MaterialFormFluid;
import thelm.jaopca.api.fluids.MaterialFormFluidBlock;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.FluidFormSettingsDeserializer;
import thelm.jaopca.custom.json.RegistryEntrySupplierDeserializer;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class FluidFormTypeImpl implements FluidFormType {

	private FluidFormTypeImpl() {};

	public static final FluidFormTypeImpl INSTANCE = new FluidFormTypeImpl();
	private static final TreeSet<Form> FORMS = new TreeSet<>();
	private static final TreeBasedTable<Form, IMaterial, MaterialFormFluid> FLUIDS = TreeBasedTable.create();
	private static final TreeBasedTable<Form, IMaterial, MaterialFormFluidBlock> FLUID_BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<Form, IMaterial, MaterialFormBucketItem> BUCKET_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<Form, IMaterial, FluidInfo> FLUID_INFOS = TreeBasedTable.create();

	public static final Type SOUND_EVENT_SUPPLIER_TYPE = new TypeToken<Supplier<SoundEvent>>(){}.getType();

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "fluid";
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
		return !ApiImpl.INSTANCE.getFluidTags().contains(tagLocation);
	}

	@Override
	public FluidInfo getMaterialFormInfo(Form form, IMaterial material) {
		FluidInfo info = FLUID_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new FluidInfoImpl(FLUIDS.get(form, material), FLUID_BLOCKS.get(form, material), BUCKET_ITEMS.get(form, material));
			FLUID_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public FluidFormSettings getNewSettings() {
		return new FluidFormSettingsImpl();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.registerTypeAdapter(SOUND_EVENT_SUPPLIER_TYPE, RegistryEntrySupplierDeserializer.INSTANCE);
	}

	@Override
	public FluidFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return FluidFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	public static void registerEntries() {
		MiscHelperImpl helper = MiscHelperImpl.INSTANCE;
		for(Form form : FORMS) {
			FluidFormSettings settings = (FluidFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				Identifier registryName = new Identifier("jaopca", form.getName()+'.'+material.getName());

				MaterialFormFluid materialFormFluid = settings.getFluidCreator().create(form, material, settings);
				Fluid fluid = materialFormFluid.asFluid();
				FLUIDS.put(form, material, materialFormFluid);
				RegistryHandler.registerToRegistry(Registry.FLUID, registryName, fluid);

				MaterialFormFluidBlock materialFormFluidBlock = settings.getFluidBlockCreator().create(materialFormFluid, settings);
				Block fluidBlock = materialFormFluidBlock.asBlock();
				FLUID_BLOCKS.put(form, material, materialFormFluidBlock);
				RegistryHandler.registerToRegistry(Registry.BLOCK, registryName, fluidBlock);

				MaterialFormBucketItem materialFormBucketItem = settings.getBucketItemCreator().create(materialFormFluid, settings);
				Item bucketItem = materialFormBucketItem.asItem();
				BUCKET_ITEMS.put(form, material, materialFormBucketItem);
				RegistryHandler.registerToRegistry(Registry.ITEM, registryName, bucketItem);

				Supplier<Fluid> fluidSupplier = ()->fluid;
				DataInjector.registerFluidTag(helper.createIdentifier(secondaryName), fluidSupplier);
				DataInjector.registerFluidTag(helper.getTagIdentifier(secondaryName, material.getName()), fluidSupplier);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerFluidTag(helper.getTagIdentifier(secondaryName, alternativeName), fluidSupplier);
				}
			}
		}
	}

	public static Collection<MaterialFormFluid> getFluids() {
		return FLUIDS.values();
	}

	public static Collection<MaterialFormFluidBlock> getFluidBlocks() {
		return FLUID_BLOCKS.values();
	}

	public static Collection<MaterialFormBucketItem> getBucketItems() {
		return BUCKET_ITEMS.values();
	}
}
