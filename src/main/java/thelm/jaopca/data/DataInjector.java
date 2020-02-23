package thelm.jaopca.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePackImpl;

public class DataInjector {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ListMultimap<Identifier, Supplier<? extends Block>> BLOCK_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<Identifier, Supplier<? extends Item>> ITEM_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<Identifier, Supplier<? extends Fluid>> FLUID_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<Identifier, Supplier<? extends EntityType<?>>> ENTITY_TYPE_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final TreeMap<Identifier, Supplier<? extends Recipe<?>>> RECIPES_INJECT = new TreeMap<>();
	private static final TreeMap<Identifier, Supplier<LootTable>> LOOT_TABLES_INJECT = new TreeMap<>();
	private static final TreeMap<Identifier, Supplier<Advancement.Task>> ADVANCEMENTS_INJECT = new TreeMap<>();
	private static final Gson GSON = new GsonBuilder().
			registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer()).
			registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer()).
			registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer()).
			registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer()).
			registerTypeAdapter(LootPool.class, new LootPool.Serializer()).
			registerTypeAdapter(LootTable.class, new LootTable.Serializer()).
			registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer()).
			registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory()).
			registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory()).
			registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).
			create();

	public static boolean registerBlockTag(Identifier location, Supplier<? extends Block> blockSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(blockSupplier);
		return BLOCK_TAGS_INJECT.put(location, blockSupplier);
	}

	public static boolean registerItemTag(Identifier location, Supplier<? extends Item> itemSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(itemSupplier);
		return ITEM_TAGS_INJECT.put(location, itemSupplier);
	}

	public static boolean registerFluidTag(Identifier location, Supplier<? extends Fluid> fluidSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(fluidSupplier);
		return FLUID_TAGS_INJECT.put(location, fluidSupplier);
	}

	public static boolean registerEntityTypeTag(Identifier location, Supplier<? extends EntityType<?>> entityTypeSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(entityTypeSupplier);
		return ENTITY_TYPE_TAGS_INJECT.put(location, entityTypeSupplier);
	}

	public static boolean registerRecipe(Identifier location, Supplier<? extends Recipe<?>> recipeSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(recipeSupplier);
		return RECIPES_INJECT.putIfAbsent(location, recipeSupplier) == null;
	}

	public static boolean registerLootTable(Identifier location, Supplier<LootTable> lootTableSupplier) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(lootTableSupplier);
		return LOOT_TABLES_INJECT.putIfAbsent(location, lootTableSupplier) == null;
	}

	public static boolean registerAdvancement(Identifier location, Supplier<Advancement.Task> advancementTask) {
		Objects.requireNonNull(location);
		Objects.requireNonNull(advancementTask);
		return ADVANCEMENTS_INJECT.putIfAbsent(location, advancementTask) == null;
	}

	public static Set<Identifier> getInjectBlockTags() {
		return BLOCK_TAGS_INJECT.keySet();
	}

	public static Set<Identifier> getInjectItemTags() {
		return ITEM_TAGS_INJECT.keySet();
	}

	public static Set<Identifier> getInjectFluidTags() {
		return FLUID_TAGS_INJECT.keySet();
	}

	public static Set<Identifier> getInjectEntityTypeTags() {
		return ENTITY_TYPE_TAGS_INJECT.keySet();
	}

	public static Set<Identifier> getInjectRecipes() {
		return RECIPES_INJECT.navigableKeySet();
	}

	public static Set<Identifier> getInjectLootTables() {
		return LOOT_TABLES_INJECT.navigableKeySet();
	}

	public static Set<Identifier> getInjectAdvancements() {
		return ADVANCEMENTS_INJECT.navigableKeySet();
	}

	public static DataInjector getNewInstance(RecipeManager recipeManager) {
		return new DataInjector(recipeManager);
	}

	private final RecipeManager recipeManager;

	private DataInjector(RecipeManager recipeManager) {
		this.recipeManager = recipeManager;
	}

	public void injectRecipes(ResourceManager resourceManager) {
		List<Recipe<?>> recipesToInject = new ArrayList<>();
		for(Map.Entry<Identifier, Supplier<? extends Recipe<?>>> entry : RECIPES_INJECT.entrySet()) {
			Recipe recipe = null;
			try {
				recipe = entry.getValue().get();
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Recipe with ID {} received invalid arguments: {}", entry.getKey(), e.getMessage());
				continue;
			}
			if(recipe == null) {
				LOGGER.warn("Recipe with ID {} returned null", entry.getKey());
			}
			else if(!recipe.getId().equals(entry.getKey())) {
				LOGGER.warn("Recipe ID {} and registry key {} do not match", recipe.getId(), entry.getKey());
			}
			else if(recipeManager.keys().anyMatch(entry.getKey()::equals)) {
				LOGGER.warn("Duplicate recipe ignored with ID {}", entry.getKey());
			}
			else {
				recipesToInject.add(recipe);
			}
		}
		try {
	        Field recipeMapField = Arrays.stream(RecipeManager.class.getDeclaredFields()).filter(field->field.getType() == Map.class).findFirst().get();
	        recipeMapField.setAccessible(true);
	        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap = (Map<RecipeType<?>, Map<Identifier, Recipe<?>>>)recipeMapField.get(recipeManager);
	        Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> copyMap = 
	                recipeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry->ImmutableMap.<Identifier, Recipe<?>>builder().putAll(entry.getValue())));
	        for(Recipe<?> recipe : recipesToInject) {
	            copyMap.computeIfAbsent(recipe.getType(), type->{
	                return ImmutableMap.builder();
	            }).put(recipe.getId(), recipe);
	        }
	        recipeMap = copyMap.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry->entry.getValue().build()));
	        recipeMapField.set(recipeManager, recipeMap);
	        LOGGER.info("Injected {} recipes, {} recipes total", recipesToInject.size(), recipeManager.keys().count());
			ModuleHandler.onRecipeInjectComplete(resourceManager);
	    }
	    catch(IllegalArgumentException | IllegalAccessException e) {
	        LOGGER.warn("Unable to inject recipes.", e);
	    }
	}

	public static class PackProvider implements ResourcePackProvider {

		public static final PackProvider INSTANCE = new PackProvider();

		@Override
		public <T extends ResourcePackProfile> void register(Map<String, T> packList, ResourcePackProfile.Factory<T> factory) {
			T packInfo = ResourcePackProfile.of("inmemory:jaopca", true, ()->{
				InMemoryResourcePackImpl pack = new InMemoryResourcePackImpl("inmemory:jaopca");
				BLOCK_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Block[] blocks = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(Block[]::new);
					Tag<Block> tag = Tag.Builder.<Block>create().add(blocks).build(location);
					pack.putJson(ResourceType.SERVER_DATA, new Identifier(location.getNamespace(), "tags/blocks/"+location.getPath()+".json"), tag.toJson(Registry.BLOCK::getId));
				});
				ITEM_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Item[] items = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(Item[]::new);
					Tag<Item> tag = Tag.Builder.<Item>create().add(items).build(location);
					pack.putJson(ResourceType.SERVER_DATA, new Identifier(location.getNamespace(), "tags/items/"+location.getPath()+".json"), tag.toJson(Registry.ITEM::getId));
				});
				FLUID_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					Fluid[] fluids = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(Fluid[]::new);
					Tag<Fluid> tag = Tag.Builder.<Fluid>create().add(fluids).build(location);
					pack.putJson(ResourceType.SERVER_DATA, new Identifier(location.getNamespace(), "tags/fluids/"+location.getPath()+".json"), tag.toJson(Registry.FLUID::getId));
				});
				ENTITY_TYPE_TAGS_INJECT.asMap().forEach((location, suppliers)->{
					EntityType<?>[] entityTypes = suppliers.stream().map(Supplier::get).distinct().filter(Objects::nonNull).toArray(EntityType<?>[]::new);
					Tag<EntityType<?>> tag = Tag.Builder.<EntityType<?>>create().add(entityTypes).build(location);
					pack.putJson(ResourceType.SERVER_DATA, new Identifier(location.getNamespace(), "tags/entity_types/"+location.getPath()+".json"), tag.toJson(Registry.ENTITY_TYPE::getId));
				});
				LOOT_TABLES_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourceType.SERVER_DATA, new Identifier(location.getNamespace(), "loot_tables/"+location.getPath()+".json"), GSON.toJsonTree(supplier.get()));
				});
				ADVANCEMENTS_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourceType.SERVER_DATA, new Identifier(location.getNamespace(), "advancements/"+location.getPath()+".json"), supplier.get().toJson());
				});
				ModuleHandler.onCreateDataPack(pack);
				return pack;
			}, factory, ResourcePackProfile.InsertionPosition.BOTTOM);
			if(packInfo != null) {
				packList.put("inmemory:jaopca", packInfo);
			}
		}
	}
}
