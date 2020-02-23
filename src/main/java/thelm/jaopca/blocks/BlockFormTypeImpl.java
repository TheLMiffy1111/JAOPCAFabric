package thelm.jaopca.blocks;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import thelm.jaopca.api.blocks.BlockFormSettings;
import thelm.jaopca.api.blocks.BlockFormType;
import thelm.jaopca.api.blocks.BlockInfo;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.BlockFormSettingsDeserializer;
import thelm.jaopca.custom.json.MaterialFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.custom.json.VoxelShapeDeserializer;
import thelm.jaopca.custom.utils.BlockDeserializationHelper;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class BlockFormTypeImpl implements BlockFormType {

	private BlockFormTypeImpl() {};

	public static final BlockFormTypeImpl INSTANCE = new BlockFormTypeImpl();
	private static final TreeSet<Form> FORMS = new TreeSet<>();
	private static final TreeBasedTable<Form, IMaterial, MaterialFormBlock> BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<Form, IMaterial, MaterialFormBlockItem> BLOCK_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<Form, IMaterial, BlockInfo> BLOCK_INFOS = TreeBasedTable.create();

	public static final Type MATERIAL_FUNCTION_TYPE = new TypeToken<Function<IMaterial, Material>>(){}.getType();
	public static final Type SOUND_TYPE_FUNCTION_TYPE = new TypeToken<Function<IMaterial, BlockSoundGroup>>(){}.getType();
	public static final Type TOOL_TYPE_FUNCTION_TYPE = new TypeToken<Function<IMaterial, Identifier[]>>(){}.getType();

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "block";
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
	public BlockInfo getMaterialFormInfo(Form form, IMaterial material) {
		BlockInfo info = BLOCK_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new BlockInfoImpl(BLOCKS.get(form, material), BLOCK_ITEMS.get(form, material));
			BLOCK_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public BlockFormSettings getNewSettings() {
		return new BlockFormSettingsImpl();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.
				registerTypeAdapter(MATERIAL_FUNCTION_TYPE,
						new MaterialMappedFunctionDeserializer<>(BlockDeserializationHelper.INSTANCE::getBlockMaterial,
								BlockDeserializationHelper.INSTANCE::getBlockMaterialName)).
				registerTypeAdapter(SOUND_TYPE_FUNCTION_TYPE,
						new MaterialMappedFunctionDeserializer<>(BlockDeserializationHelper.INSTANCE::getBlockSoundGroup,
								BlockDeserializationHelper.INSTANCE::getBlockSoundGroupName)).
				registerTypeAdapter(TOOL_TYPE_FUNCTION_TYPE, MaterialFunctionDeserializer.INSTANCE).
				registerTypeAdapter(VoxelShape.class, VoxelShapeDeserializer.INSTANCE);
	}

	@Override
	public BlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return BlockFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	public static void registerEntries() {
		MiscHelperImpl helper = MiscHelperImpl.INSTANCE;
		for(Form form : FORMS) {
			BlockFormSettings settings = (BlockFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				Identifier registryName = new Identifier("jaopca", form.getName()+'.'+material.getName());

				MaterialFormBlock materialFormBlock = settings.getBlockCreator().create(form, material, settings);
				Block block = materialFormBlock.asBlock();
				BLOCKS.put(form, material, materialFormBlock);
				RegistryHandler.registerToRegistry(Registry.BLOCK, registryName, block);

				MaterialFormBlockItem materialFormBlockItem = settings.getBlockItemCreator().create(materialFormBlock, settings);
				BlockItem blockItem = materialFormBlockItem.asBlockItem();
				BLOCK_ITEMS.put(form, material, materialFormBlockItem);
				RegistryHandler.registerToRegistry(Registry.ITEM, registryName, blockItem);

				Supplier<Block> blockSupplier = ()->block;
				DataInjector.registerBlockTag(helper.createIdentifier(secondaryName), blockSupplier);
				DataInjector.registerBlockTag(helper.getTagIdentifier(secondaryName, material.getName()), blockSupplier);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerBlockTag(helper.getTagIdentifier(secondaryName, alternativeName), blockSupplier);
				}

				Supplier<Item> itemSupplier = ()->blockItem;
				DataInjector.registerItemTag(helper.createIdentifier(secondaryName), itemSupplier);
				DataInjector.registerItemTag(helper.getTagIdentifier(secondaryName, material.getName()), itemSupplier);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerItemTag(helper.getTagIdentifier(secondaryName, alternativeName), itemSupplier);
				}
			}
		}
	}

	public static Collection<MaterialFormBlock> getBlocks() {
		return BLOCKS.values();
	}

	public static Collection<MaterialFormBlockItem> getBlockItems() {
		return BLOCK_ITEMS.values();
	}
}
