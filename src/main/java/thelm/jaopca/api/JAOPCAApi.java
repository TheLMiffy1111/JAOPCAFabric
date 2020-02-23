package thelm.jaopca.api;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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

/**
 * The main API class of JAOPCA, which consists of the main methods that modules use to add content to
 * JAOPCA. Most methods here should only be called in modules since most of JAOPCA is initialized in the
 * deferred work queue.
 */
public abstract class JAOPCAApi {

	private static JAOPCAApi instance;

	protected static void setInstance(JAOPCAApi api) {
		if(instance == null) {
			instance = api;
		}
	}

	/**
	 * Returns if the API instance was set or not. Use this if your mod needs to use the API methods that
	 * work before JAOPCA collects data.
	 * @return true if the API instance was set
	 */
	public static boolean initialized() {
		return instance != null;
	}

	/**
	 * Returns the implementation instance of the API.
	 * @return The API instance
	 * @throws IllegalStateException if the API instance was not set
	 */
	public static JAOPCAApi instance() {
		if(instance == null) {
			throw new IllegalStateException("Got API instance before it is set");
		}
		return instance;
	}

	/**
	 * Returns the implementation instance of the {@link BlockFormType}.
	 * @return the block form type instance
	 */
	public abstract BlockFormType blockFormType();

	/**
	 * Returns the implementation instance of {@link ItemFormType}.
	 * @return The item form type instance
	 */
	public abstract ItemFormType itemFormType();

	/**
	 * Returns the implementation instance of {@link FluidFormType}.
	 * @return The fluid form type instance
	 */
	public abstract FluidFormType fluidFormType();

	/**
	 * Returns the implementation instance of {@link EntityTypeFormType}. <b>Not yet implemented!</b>
	 * @return The entity type form type instance
	 */
	public abstract EntityTypeFormType entityTypeFormType();

	/**
	 * Gets an {@link FormType} by name.
	 * @param name The name of the form type
	 * @return The form type with the name provided, null if no form type registered has this name
	 */
	public abstract FormType getFormType(String name);

	/**
	 * Creates a new {@link Form} with the specified module, name, and form type. The form type does not need
	 * to be registered.
	 * @param module The module that the form should be under
	 * @param name The name of the form
	 * @param type The type of the form
	 * @return The form with the provided name and type
	 */
	public abstract Form newForm(Module module, String name, FormType type);

	/**
	 * Creates a form request from a list of forms. Forms that do not have the same module as provided
	 * will not be included.
	 * @param module The module that the form request should be under
	 * @param forms The forms to be put into the form request
	 * @return The form request consisting of the provided valid forms
	 */
	public abstract FormRequest newFormRequest(Module module, Form... forms);

	/**
	 * Returns the implementation instance of {@link MiscHelper}.
	 * @return The misc helper instance
	 */
	public abstract MiscHelper miscHelper();

	/**
	 * Returns the implementation instance of {@link JsonHelper}.
	 * @return The JSON helper instance
	 */
	public abstract JsonHelper jsonHelper();

	public abstract TagFormat tagFormat();

	/**
	 * Returns the enum JSON deserializer instance used by JAOPCA. The deserializer deserializes enums with
	 * case-insensitive names and enum ordinals.
	 * @return The enum deserializer instance
	 */
	public abstract JsonDeserializer<Enum<?>> enumDeserializer();

	/**
	 * Returns the material to enum function JSON deserializer instance used by JAOPCA. The deserializer
	 * creates a {@linkplain it.unimi.dsi.fastutil.objects.Object2ObjectMap Object2ObjectMap} tha may
	 * have its values based on the configuration files, and deserializes default enum values with
	 * case-insensitive names and enum ordinals.
	 * @return The material to enum function deserializer instance
	 */
	public abstract JsonDeserializer<Function<IMaterial, Enum<?>>> materialEnumFunctionDeserializer();

	/**
	 * Creates an instance of a mapped material function JSON deserializer using the provided functions.
	 * The deserializer creates a {@linkplain it.unimi.dsi.fastutil.objects.Object2ObjectMap Object2ObjectMap}
	 * that may have its values based on the configuration files. The deserializer will use the stringToValue
	 * function to read default values and configuration values, and use the valueToString function to
	 * put the default values into configuration files.
	 * @param <T> The type of the value in the function
	 * @param stringToValue The function used to convert a string into a value.
	 * @param valueToString The function used to convert a value into a string.
	 * @return The instance of a mapped material function deserializer using the provided functions
	 */
	public abstract <T> JsonDeserializer<Function<IMaterial, T>> materialMappedFunctionDeserializer(Function<String, T> stringToValue, Function<T, String> valueToString);

	/**
	 * Returns the material function JSON deserializer instance used by JAOPCA. The deserializer creates a
	 * {@linkplain it.unimi.dsi.fastutil.objects.Object2ObjectMap Object2ObjectMap} that will not have
	 * its values based on the configuration files. The values will be deserialized using the type adapters
	 * present.
	 * @return The material function JSON deserializer instance
	 */
	public abstract JsonDeserializer<Function<IMaterial, ?>> materialFunctionDeserializer();

	/**
	 * Returns the registry entry supplier JSON deserializer instance used by JAOPCA. The deserializer
	 * deserializes registry entries with locations.
	 * @return The registry entry deserializer instance
	 */
	public abstract JsonDeserializer<Supplier<?>> registryEntrySupplierDeserializer();

	/**
	 * Gets an {@link Form} by name.
	 * @param name The name of the form
	 * @return The form with the name provided, null if no form registered has this name
	 */
	public abstract Form getForm(String name);

	public abstract Set<Form> getForms();

	/**
	 * Gets an {@link IMaterial} by name.
	 * @param name The name of the material
	 * @return The material with the name provided, null if no material has been found with this name
	 */
	public abstract IMaterial getMaterial(String name);

	public abstract Set<IMaterial> getMaterials();

	/**
	 * Returns the item group used by items added by JAOPCA.
	 * @return The item group used by JAOPCA
	 */
	public abstract ItemGroup itemGroup();

	/**
	 * Returns the set of known block tag locations, which is the union of defined block tag locations
	 * and registered block tag locations. Note that tags added by custom data packs may not be included.
	 * @return The set of block tag locations known by JAOPCA
	 */
	public abstract Set<Identifier> getBlockTags();

	/**
	 * Returns the set of known item tag locations, which is the union of defined item tag locations
	 * and registered item tag locations. Note that tags added by custom data packs may not be included.
	 * @return The set of item tag locations known by JAOPCA
	 */
	public abstract Set<Identifier> getItemTags();

	/**
	 * Returns the set of known fluid tag locations, which is the union of defined fluid tag locations
	 * and registered fluid tag locations. Note that tags added by custom data packs may not be included.
	 * @return The set of fluid tag locations known by JAOPCA
	 */
	public abstract Set<Identifier> getFluidTags();

	/**
	 * Returns the set of known entity type tag locations, which is the union of defined entity type tag
	 * locations and registered entity type tag locations. Note that tags added by custom data packs may
	 * not be included.
	 * @return The set of entity type tag locations known by JAOPCA
	 */
	public abstract Set<Identifier> getEntityTypeTags();

	/**
	 * Returns the set of known tag locations of the supplied type, which is the registered tag locations.
	 * Note that tags added by custom data packs may not be included.
	 * @param type The type of the tag
	 * @return The set of tag locations known by JAOPCA
	 */
	public abstract Set<Identifier> getTags(String type);

	/**
	 * Returns the set of known recipe locations, which is the union of defined recipe locations and
	 * registered recipe locations. Note that recipes added by custom data packs may not be included.
	 * @return The set of recipe locations known by JAOPCA
	 */
	public abstract Set<Identifier> getRecipes();

	/**
	 * Returns the set of known loot table locations, which is the union of defined loot table locations
	 * and registered loot table locations. Note that loot tables added by custom data packs may not be
	 * included.
	 * @return The set of loot table locations known by JAOPCA
	 */
	public abstract Set<Identifier> getLootTables();

	/**
	 * Returns the set of known advancement locations, which is the union of defined advancement locations
	 * and registered advancement locations. Note that advancements added by custom data packs may not be
	 * included.
	 * @return The set of advancement locations known by JAOPCA
	 */
	public abstract Set<Identifier> getAdvancements();

	/**
	 * Returns the current {@link Localizer} based on Minecraft's current language. Will always return the
	 * default localizer on dedicated servers.
	 * @return The current localizer based on Minecraft's current language
	 */
	public abstract Localizer currentLocalizer();

	/**
	 * Registers an {@link FormType} to be used in custom form deserialization.
	 * @param type The form type to be registered
	 * @return true if the form type was successfully registered
	 */
	public abstract boolean registerFormType(FormType type);

	/**
	 * Registers a block tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedBlockTag(Identifier key);

	/**
	 * Registers an item tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedItemTag(Identifier key);

	/**
	 * Registers a fluid tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedFluidTag(Identifier key);

	/**
	 * Registers an entity type tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedEntityTypeTag(Identifier key);

	/**
	 * Registers a block supplier to be added to a tag by JAOPCA's in memory data pack. Suppliers that
	 * return null will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param blockSupplier The block supplier to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerBlockTag(Identifier key, Supplier<? extends Block> blockSupplier);

	/**
	 * Registers a block to be added to a tag by JAOPCA's in memory data pack. Null blocks will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param block The block to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerBlockTag(Identifier key, Block block);

	/**
	 * Registers a block location to be added to a tag by JAOPCA's in memory data pack. Locations that do
	 * not correspond to a block will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param blockKey The block location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerBlockTag(Identifier key, Identifier blockKey);

	/**
	 * Registers an item supplier to be added to a tag by JAOPCA's in memory data pack. Suppliers that
	 * return null will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param itemSupplier The item supplier to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerItemTag(Identifier key, Supplier<? extends Item> itemSupplier);

	/**
	 * Registers an item to be added to a tag by JAOPCA's in memory data pack. Null items will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param item The item to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerItemTag(Identifier key, Item item);

	/**
	 * Registers an item location to be added to a tag by JAOPCA's in memory data pack. Locations that do
	 * not correspond to an item will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param itemKey The item location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerItemTag(Identifier key, Identifier itemKey);

	/**
	 * Registers a fluid supplier to be added to a tag by JAOPCA's in memory data pack. Suppliers that
	 * return null will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param fluidSupplier The fluid supplier to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerFluidTag(Identifier key, Supplier<? extends Fluid> fluidSupplier);

	/**
	 * Registers a fluid to be added to a tag by JAOPCA's in memory data pack. Null fluids will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param fluid The fluid to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerFluidTag(Identifier key, Fluid fluid);

	/**
	 * Registers a fluid location to be added to a tag by JAOPCA's in memory data pack. Locations that do
	 * not correspond to a fluid will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param fluidKey The fluid location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerFluidTag(Identifier key, Identifier fluidKey);

	/**
	 * Registers an entity type supplier to be added to a tag by JAOPCA's in memory data pack. Suppliers that
	 * return null will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param entityTypeSupplier The entity type supplier to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerEntityTypeTag(Identifier key, Supplier<? extends EntityType<?>> entityTypeSupplier);

	/**
	 * Registers an entity type to be added to a tag by JAOPCA's in memory data pack. Null entity types will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param entityType The entity type to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerEntityTypeTag(Identifier key, EntityType<?> entityType);

	/**
	 * Registers an entity type location to be added to a tag by JAOPCA's in memory data pack. Locations that
	 * do not correspond to an entity type will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param entityTypeKey The entity type location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerEntityTypeTag(Identifier key, Identifier entityTypeKey);

	/**
	 * Registers a recipe supplier to be injected by JAOPCA. The returned recipe must have an id that is the
	 * same as the id provided for JAOPCA to inject.
	 * @param key The id of the recipe
	 * @param recipeSupplier The recipe supplier
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerRecipe(Identifier key, Supplier<? extends Recipe<?>> recipeSupplier);

	/**
	 * Registers a recipe to be injected by JAOPCA. The id of the recipe must be the same as the id provided
	 * for JAOPCA to inject.
	 * @param key The id of the recipe
	 * @param recipe The recipe
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerRecipe(Identifier key, Recipe<?> recipe);

	/**
	 * Creates a shaped recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input The input of the recipe in the form of how one would specify inputs in Minecraft versions
	 * prior to 1.12, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapedRecipe(Identifier key, String group, Object output, int count, Object... input);

	/**
	 * Creates a shaped recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input The input of the recipe in the form of how one would specify inputs in Minecraft versions
	 * prior to 1.12, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapedRecipe(Identifier key, Object output, int count, Object... input);

	/**
	 * Creates a shapeless recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input An array of inputs for the recipe, see {@link MiscHelper#getIngredient(Object)} for valid
	 * input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapelessRecipe(Identifier key, String group, Object output, int count, Object... input);

	/**
	 * Creates a shapeless recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input An array of inputs for the recipe, see {@link MiscHelper#getIngredient(Object)} for valid
	 * input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapelessRecipe(Identifier key, Object output, int count, Object... input);

	/**
	 * Creates a furnace recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmeltingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a furnace recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmeltingRecipe(Identifier key, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a blasting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerBlastingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a blasting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerBlastingRecipe(Identifier key, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a smoking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmokingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a smoking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmokingRecipe(Identifier key, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a campfire cooking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerCampfireCookingRecipe(Identifier key, String group, Object input, Object output, int count, int time);

	/**
	 * Creates a campfire cooking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerCampfireCookingRecipe(Identifier key, Object input, Object output, int count, int time);

	/**
	 * Creates a stonecutting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerStonecuttingRecipe(Identifier key, String group, Object input, Object output, int count);

	/**
	 * Creates a stonecutting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link MiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link MiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerStonecuttingRecipe(Identifier key, Object input, Object output, int count);

	/**
	 * Registers a loot table supplier to be added by JAOPCA's in memory data pack.
	 * @param key The id of the advancement
	 * @param advancementBuilder The advancement builder
	 * @return true if the id of the advancement was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerLootTable(Identifier key, Supplier<LootTable> lootTableSupplier);

	/**
	 * Registers a loot table to be added by JAOPCA's in memory data pack.
	 * @param key The id of the advancement
	 * @param advancementBuilder The advancement builder
	 * @return true if the id of the advancement was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerLootTable(Identifier key, LootTable lootTable);

	/**
	 * Registers an advancement builder supplier to be added by JAOPCA's in memory data pack.
	 * @param key The id of the advancement
	 * @param advancementTaskSupplier The advancement task supplier
	 * @return true if the id of the advancement was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerAdvancement(Identifier key, Supplier<Advancement.Task> advancementTaskSupplier);

	/**
	 * Registers an advancement builder to be added by JAOPCA's in memory data pack.
	 * @param key The id of the advancement
	 * @param advancementTask The advancement task
	 * @return true if the id of the advancement was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerAdvancement(Identifier key, Advancement.Task advancementTask);

	/**
	 * Registers an {@link Localizer} to languages for use by JAOPCA.
	 * @param localizer The localizer
	 * @param languages An array of language identifiers that the localizer should be used for
	 */
	public abstract void registerLocalizer(Localizer localizer, String... languages);
}