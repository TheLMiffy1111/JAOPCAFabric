package thelm.jaopca.api.blocks;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.shape.VoxelShape;
import thelm.jaopca.api.forms.FormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface BlockFormSettings extends FormSettings {

	BlockFormSettings setBlockCreator(BlockCreator blockCreator);

	BlockCreator getBlockCreator();

	BlockFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	BlockFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction);

	Function<IMaterial, MaterialColor> getMaterialColorFunction();

	BlockFormSettings setBlocksMovement(boolean blocksMovement);

	boolean getBlocksMovement();

	BlockFormSettings setSoundTypeFunction(Function<IMaterial, BlockSoundGroup> soundTypeFunction);

	Function<IMaterial, BlockSoundGroup> getSoundTypeFunction();

	BlockFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction);

	ToIntFunction<IMaterial> getLightValueFunction();

	BlockFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	BlockFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction);

	ToDoubleFunction<IMaterial> getExplosionResistanceFunction();

	BlockFormSettings setSlipperinessFunction(ToDoubleFunction<IMaterial> slipperinessFunction);

	ToDoubleFunction<IMaterial> getSlipperinessFunction();

	BlockFormSettings setShape(VoxelShape shape);

	VoxelShape getShape();

	BlockFormSettings setRaytraceShape(VoxelShape raytraceShape);

	VoxelShape getRaytraceShape();

	BlockFormSettings setHarvestToolFunction(Function<IMaterial, Identifier> harvestToolFunction);

	Function<IMaterial, Identifier> getHarvestToolFunction();

	BlockFormSettings setHarvestLevelFunction(ToIntFunction<IMaterial> harvestLevelFunction);

	ToIntFunction<IMaterial> getHarvestLevelFunction();

	BlockFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	BlockFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	//IBlockFormSettings setIsWaterLoggable(boolean waterLoggable);

	//boolean getIsWaterLoggable();

	//IBlockFormSettings setIsFallable(boolean fallable);

	//boolean getIsFallable();

	BlockFormSettings setBlockLootTableCreator(BlockLootTableCreator blockLootTableCreator);

	BlockLootTableCreator getBlockLootTableCreator();

	BlockFormSettings setItemBlockCreator(BlockItemCreator itemBlockCreator);

	BlockItemCreator getBlockItemCreator();

	BlockFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	BlockFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	BlockFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	BlockFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
