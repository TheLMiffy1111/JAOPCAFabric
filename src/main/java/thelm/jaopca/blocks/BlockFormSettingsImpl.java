package thelm.jaopca.blocks;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import thelm.jaopca.api.blocks.BlockCreator;
import thelm.jaopca.api.blocks.BlockFormSettings;
import thelm.jaopca.api.blocks.BlockItemCreator;
import thelm.jaopca.api.blocks.BlockLootTableCreator;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelperImpl;

public class BlockFormSettingsImpl implements BlockFormSettings {

	BlockFormSettingsImpl() {}

	private BlockCreator blockCreator = JAOPCABlock::new;
	private Function<IMaterial, Material> materialFunction = material->Material.METAL;
	private Function<IMaterial, MaterialColor> materialColorFunction = material->{
		int color = material.getColor();
		return Arrays.stream(MaterialColor.COLORS).filter(Objects::nonNull).
				min((matColor1, matColor2)->Integer.compare(
						MiscHelperImpl.INSTANCE.squareColorDifference(color, matColor1.color),
						MiscHelperImpl.INSTANCE.squareColorDifference(color, matColor2.color))).
				orElse(MaterialColor.IRON);
	};
	private boolean blocksMovement = true;
	private Function<IMaterial, BlockSoundGroup> soundTypeFunction = material->BlockSoundGroup.METAL;
	private ToIntFunction<IMaterial> lightValueFunction = material->0;
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->5;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->6;
	private ToDoubleFunction<IMaterial> slipperinessFunction = material->0.6;
	private boolean isFull = true;
	private VoxelShape shape = VoxelShapes.fullCube();
	private VoxelShape raytraceShape = VoxelShapes.empty();
	private Function<IMaterial, Identifier> harvestToolFunction = material->FabricToolTags.PICKAXES.getId();
	private ToIntFunction<IMaterial> harvestLevelFunction = material->0;
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private BlockLootTableCreator blockLootTableCreator = (block, settings)->{
		return LootTable.builder().withType(LootContextTypes.BLOCK).
				withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).
						withEntry(ItemEntry.builder(block.asBlock())).
						withCondition(SurvivesExplosionLootCondition.builder())).create();
	};

	private BlockItemCreator itemBlockCreator = JAOPCABlockItem::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->64;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private Function<IMaterial, Rarity> displayRarityFunction = material->material.getDisplayRarity();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

	@Override
	public FormType getType() {
		return BlockFormTypeImpl.INSTANCE;
	}

	@Override
	public BlockFormSettings setBlockCreator(BlockCreator blockCreator) {
		this.blockCreator = blockCreator;
		return this;
	}

	@Override
	public BlockCreator getBlockCreator() {
		return blockCreator;
	}

	@Override
	public BlockFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction) {
		this.materialFunction = materialFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Material> getMaterialFunction() {
		return materialFunction;
	}

	@Override
	public BlockFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction) {
		this.materialColorFunction = materialColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MaterialColor> getMaterialColorFunction() {
		return materialColorFunction;
	}

	@Override
	public BlockFormSettings setBlocksMovement(boolean blocksMovement) {
		this.blocksMovement = blocksMovement;
		return this;
	}

	@Override
	public boolean getBlocksMovement() {
		return blocksMovement;
	}

	@Override
	public BlockFormSettings setSoundTypeFunction(Function<IMaterial, BlockSoundGroup> soundTypeFunction) {
		this.soundTypeFunction = soundTypeFunction;
		return this;
	}

	@Override
	public Function<IMaterial, BlockSoundGroup> getSoundTypeFunction() {
		return soundTypeFunction;
	}

	@Override
	public BlockFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction) {
		this.lightValueFunction = lightValueFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLightValueFunction() {
		return lightValueFunction;
	}

	@Override
	public BlockFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction) {
		this.blockHardnessFunction = blockHardnessFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getBlockHardnessFunction() {
		return blockHardnessFunction;
	}

	@Override
	public BlockFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction) {
		this.explosionResistanceFunction = explosionResistanceFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getExplosionResistanceFunction() {
		return explosionResistanceFunction;
	}

	@Override
	public BlockFormSettings setSlipperinessFunction(ToDoubleFunction<IMaterial> slipperinessFunction) {
		this.slipperinessFunction = slipperinessFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getSlipperinessFunction() {
		return slipperinessFunction;
	}

	@Override
	public BlockFormSettings setShape(VoxelShape shape) {
		this.shape = shape;
		return this;
	}

	@Override
	public VoxelShape getShape() {
		return shape;
	}

	@Override
	public BlockFormSettings setRaytraceShape(VoxelShape raytraceShape) {
		this.raytraceShape = raytraceShape;
		return this;
	}

	@Override
	public VoxelShape getRaytraceShape() {
		return raytraceShape;
	}

	@Override
	public BlockFormSettings setHarvestToolFunction(Function<IMaterial, Identifier> harvestToolFunction) {
		this.harvestToolFunction = harvestToolFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Identifier> getHarvestToolFunction() {
		return harvestToolFunction;
	}

	@Override
	public BlockFormSettings setHarvestLevelFunction(ToIntFunction<IMaterial> harvestLevelFunction) {
		this.harvestLevelFunction = harvestLevelFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getHarvestLevelFunction() {
		return harvestLevelFunction;
	}

	@Override
	public BlockFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction) {
		this.flammabilityFunction = flammabilityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFlammabilityFunction() {
		return flammabilityFunction;
	}

	@Override
	public BlockFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction) {
		this.fireSpreadSpeedFunction = fireSpreadSpeedFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFireSpreadSpeedFunction() {
		return fireSpreadSpeedFunction;
	}

	@Override
	public BlockFormSettings setBlockLootTableCreator(BlockLootTableCreator blockLootTableCreator) {
		this.blockLootTableCreator = blockLootTableCreator;
		return this;
	}

	@Override
	public BlockLootTableCreator getBlockLootTableCreator() {
		return blockLootTableCreator;
	}

	@Override
	public BlockFormSettings setItemBlockCreator(BlockItemCreator itemBlockCreator) {
		this.itemBlockCreator = itemBlockCreator;
		return this;
	}

	@Override
	public BlockItemCreator getBlockItemCreator() {
		return itemBlockCreator;
	}

	@Override
	public BlockFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction) {
		this.itemStackLimitFunction = itemStackLimitFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getItemStackLimitFunction() {
		return itemStackLimitFunction;
	}

	@Override
	public BlockFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction) {
		this.hasEffectFunction = hasEffectFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getHasEffectFunction() {
		return hasEffectFunction;
	}

	@Override
	public BlockFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Rarity> getDisplayRarityFunction() {
		return displayRarityFunction;
	}

	@Override
	public BlockFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction) {
		this.burnTimeFunction = burnTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getBurnTimeFunction() {
		return burnTimeFunction;
	}
}
