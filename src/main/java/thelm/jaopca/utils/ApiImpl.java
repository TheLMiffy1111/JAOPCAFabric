package thelm.jaopca.utils;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializer;

import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.BlockFormType;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.entities.EntityTypeFormType;
import thelm.jaopca.api.fluids.FluidFormType;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.helpers.JsonHelper;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.items.ItemFormType;
import thelm.jaopca.api.localization.Localizer;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.MaterialEnumFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.custom.json.RegistryEntrySupplierDeserializer;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.fluids.FluidFormTypeImpl;
import thelm.jaopca.forms.FormImpl;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormRequestImpl;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.localization.LocalizationHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.recipes.BlastingRecipeSupplier;
import thelm.jaopca.recipes.CampfireCookingRecipeSupplier;
import thelm.jaopca.recipes.ShapedRecipeSupplier;
import thelm.jaopca.recipes.ShapelessRecipeSupplier;
import thelm.jaopca.recipes.SmeltingRecipeSupplier;
import thelm.jaopca.recipes.SmokingRecipeSupplier;
import thelm.jaopca.recipes.StonecuttingRecipeSupplier;

public class ApiImpl extends JAOPCAApi {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final ApiImpl INSTANCE = new ApiImpl();

	private ApiImpl() {}

	public void init() {
		JAOPCAApi.setInstance(this);
	}

	@Override
	public BlockFormType blockFormType() {
		return BlockFormTypeImpl.INSTANCE;
	}

	@Override
	public ItemFormType itemFormType() {
		return ItemFormTypeImpl.INSTANCE;
	}

	@Override
	public FluidFormType fluidFormType() {
		return FluidFormTypeImpl.INSTANCE;
	}

	@Override
	public EntityTypeFormType entityTypeFormType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormType getFormType(String name) {
		return FormTypeHandler.getFormType(name);
	}

	@Override
	public Form newForm(Module module, String name, FormType type) {
		return new FormImpl(module, name, type);
	}

	@Override
	public FormRequest newFormRequest(Module module, Form... forms) {
		FormRequest request = new FormRequestImpl(module, forms);
		return request;
	}

	@Override
	public MiscHelper miscHelper() {
		return MiscHelperImpl.INSTANCE;
	}

	@Override
	public JsonHelper jsonHelper() {
		return JsonHelperImpl.INSTANCE;
	}

	@Override
	public TagFormat tagFormat() {
		return ConfigHandler.tagFormat;
	}

	@Override
	public JsonDeserializer<Enum<?>> enumDeserializer() {
		return EnumDeserializer.INSTANCE;
	}

	@Override
	public JsonDeserializer<Function<IMaterial, Enum<?>>> materialEnumFunctionDeserializer() {
		return MaterialEnumFunctionDeserializer.INSTANCE;
	}

	@Override
	public <T> JsonDeserializer<Function<IMaterial, T>> materialMappedFunctionDeserializer(Function<String, T> stringToValue, Function<T, String> valueToString) {
		return new MaterialMappedFunctionDeserializer<>(stringToValue, valueToString);
	}

	@Override
	public JsonDeserializer<Function<IMaterial, ?>> materialFunctionDeserializer() {
		return MaterialFunctionDeserializer.INSTANCE;
	}

	@Override
	public JsonDeserializer<Supplier<?>> registryEntrySupplierDeserializer() {
		return RegistryEntrySupplierDeserializer.INSTANCE;
	}

	@Override
	public Form getForm(String name) {
		return FormHandler.getForm(name);
	}

	@Override
	public Set<Form> getForms() {
		return ImmutableSortedSet.copyOf(FormHandler.getForms());
	}

	@Override
	public IMaterial getMaterial(String name) {
		return MaterialHandler.getMaterial(name);
	}

	@Override
	public Set<IMaterial> getMaterials() {
		return ImmutableSortedSet.copyOf(MaterialHandler.getMaterials());
	}

	@Override
	public ItemGroup itemGroup() {
		return ItemFormTypeImpl.getItemGroup();
	}

	@Override
	public Set<Identifier> getBlockTags() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedTags("blocks"), DataInjector.getInjectBlockTags()));
	}

	@Override
	public Set<Identifier> getItemTags() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedTags("items"), DataInjector.getInjectItemTags()));
	}

	@Override
	public Set<Identifier> getFluidTags() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedTags("fluids"), DataInjector.getInjectFluidTags()));
	}

	@Override
	public Set<Identifier> getEntityTypeTags() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedTags("entity_types"), DataInjector.getInjectEntityTypeTags()));
	}

	@Override
	public Set<Identifier> getTags(String type) {
		return ImmutableSortedSet.copyOf(DataCollector.getDefinedTags(type));
	}

	@Override
	public Set<Identifier> getRecipes() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedRecipes(), DataInjector.getInjectRecipes()));
	}

	@Override
	public Set<Identifier> getLootTables() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedAdvancements(), DataInjector.getInjectAdvancements()));
	}

	@Override
	public Set<Identifier> getAdvancements() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedAdvancements(), DataInjector.getInjectAdvancements()));
	}

	@Override
	public Localizer currentLocalizer() {
		return LocalizationHandler.getCurrentLocalizer();
	}

	@Override
	public boolean registerFormType(FormType type) {
		return FormTypeHandler.registerFormType(type);
	}

	@Override
	public boolean registerDefinedBlockTag(Identifier key) {
		return DataCollector.getDefinedTags("blocks").add(key);
	}

	@Override
	public boolean registerDefinedItemTag(Identifier key) {
		return DataCollector.getDefinedTags("items").add(key);
	}

	@Override
	public boolean registerDefinedFluidTag(Identifier key) {
		return DataCollector.getDefinedTags("fluids").add(key);
	}

	@Override
	public boolean registerDefinedEntityTypeTag(Identifier key) {
		return DataCollector.getDefinedTags("entity_types").add(key);
	}

	@Override
	public boolean registerBlockTag(Identifier key, Supplier<? extends Block> blockSupplier) {
		if(ConfigHandler.BLOCK_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerBlockTag(key, blockSupplier);
	}

	@Override
	public boolean registerBlockTag(Identifier key, Block block) {
		return registerBlockTag(key, ()->block);
	}

	@Override
	public boolean registerBlockTag(Identifier key, Identifier blockKey) {
		return registerBlockTag(key, ()->Registry.BLOCK.get(blockKey));
	}

	@Override
	public boolean registerItemTag(Identifier key, Supplier<? extends Item> itemSupplier) {
		if(ConfigHandler.ITEM_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerItemTag(key, itemSupplier);
	}

	@Override
	public boolean registerItemTag(Identifier key, Item item) {
		return registerItemTag(key, ()->item);
	}

	@Override
	public boolean registerItemTag(Identifier key, Identifier itemKey) {
		return registerItemTag(key, ()->Registry.ITEM.get(itemKey));
	}

	@Override
	public boolean registerFluidTag(Identifier key, Supplier<? extends Fluid> fluidSupplier) {
		if(ConfigHandler.FLUID_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerFluidTag(key, fluidSupplier);
	}

	@Override
	public boolean registerFluidTag(Identifier key, Fluid fluid) {
		return registerFluidTag(key, ()->fluid);
	}

	@Override
	public boolean registerFluidTag(Identifier key, Identifier fluidKey) {
		return registerFluidTag(key, ()->Registry.FLUID.get(fluidKey));
	}

	@Override
	public boolean registerEntityTypeTag(Identifier key, Supplier<? extends EntityType<?>> entityTypeSupplier) {
		if(ConfigHandler.ENTITY_TYPE_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerEntityTypeTag(key, entityTypeSupplier);
	}

	@Override
	public boolean registerEntityTypeTag(Identifier key, EntityType<?> entityType) {
		return registerEntityTypeTag(key, ()->entityType);
	}

	@Override
	public boolean registerEntityTypeTag(Identifier key, Identifier entityTypeKey) {
		return registerEntityTypeTag(key, ()->Registry.ENTITY_TYPE.get(entityTypeKey));
	}

	@Override
	public boolean registerRecipe(Identifier key, Supplier<? extends Recipe<?>> recipeSupplier) {
		if(DataCollector.getDefinedRecipes().contains(key) || ConfigHandler.RECIPE_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerRecipe(key, recipeSupplier);
	}

	@Override
	public boolean registerRecipe(Identifier key, Recipe<?> recipe) {
		return registerRecipe(key, ()->recipe);
	}

	@Override
	public boolean registerShapedRecipe(Identifier key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSupplier(key, group, output, count, input));
	}

	@Override
	public boolean registerShapedRecipe(Identifier key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSupplier(key, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(Identifier key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSupplier(key, group, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(Identifier key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSupplier(key, output, count, input));
	}

	@Override
	public boolean registerSmeltingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmeltingRecipe(Identifier key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSupplier(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(Identifier key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSupplier(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(Identifier key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSupplier(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(Identifier key, String group, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSupplier(key, group, input, output, count, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(Identifier key, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSupplier(key, input, output, count, time));
	}

	@Override
	public boolean registerStonecuttingRecipe(Identifier key, String group, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSupplier(key, group, input, output, count));
	}

	@Override
	public boolean registerStonecuttingRecipe(Identifier key, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSupplier(key, input, output, count));
	}

	@Override
	public boolean registerLootTable(Identifier key, Supplier<LootTable> lootTableSupplier) {
		if(DataCollector.getDefinedLootTables().contains(key) || ConfigHandler.LOOT_TABLE_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerLootTable(key, lootTableSupplier);
	}

	@Override
	public boolean registerLootTable(Identifier key, LootTable lootTable) {
		return registerLootTable(key, ()->lootTable);
	}

	@Override
	public boolean registerAdvancement(Identifier key, Supplier<Advancement.Task> advancementTaskSupplier) {
		if(DataCollector.getDefinedAdvancements().contains(key) || ConfigHandler.ADVANCEMENT_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerAdvancement(key, advancementTaskSupplier);
	}

	@Override
	public boolean registerAdvancement(Identifier key, Advancement.Task advancementTask) {
		return registerAdvancement(key, ()->advancementTask);
	}

	@Override
	public void registerLocalizer(Localizer translator, String... languages) {
		LocalizationHandler.registerLocalizer(translator, languages);
	}
}
