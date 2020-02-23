package thelm.jaopca.api.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Rarity;
import thelm.jaopca.api.forms.FormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface FluidFormSettings extends FormSettings {

	FluidFormSettings setFluidCreator(FluidCreator fluidCreator);

	FluidCreator getFluidCreator();

	FluidFormSettings setMaxLevelFunction(ToIntFunction<IMaterial> maxLevelFunction);

	ToIntFunction<IMaterial> getMaxLevelFunction();

	FluidFormSettings setTickRateFunction(ToIntFunction<IMaterial> tickRateFunction);

	ToIntFunction<IMaterial> getTickRateFunction();

	FluidFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction);

	ToDoubleFunction<IMaterial> getExplosionResistanceFunction();

	FluidFormSettings setCanSourcesMultiplyFunction(Predicate<IMaterial> canSourcesMultiplyFunction);

	Predicate<IMaterial> getCanSourcesMultiplyFunction();

	FluidFormSettings setFillSoundSupplier(Supplier<SoundEvent> fillSoundSupplier);

	Supplier<SoundEvent> getFillSoundSupplier();

	FluidFormSettings setEmptySoundSupplier(Supplier<SoundEvent> emptySoundSupplier);

	Supplier<SoundEvent> getEmptySoundSupplier();

	FluidFormSettings setDensityFunction(ToIntFunction<IMaterial> densityFunction);

	ToIntFunction<IMaterial> getDensityFunction();

	FluidFormSettings setViscosityFunction(ToIntFunction<IMaterial> viscosityFunction);

	ToIntFunction<IMaterial> getViscosityFunction();

	FluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction);

	ToIntFunction<IMaterial> getTemperatureFunction();

	FluidFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	FluidFormSettings setFluidBlockCreator(FluidBlockCreator fluidBlockCreator);

	FluidBlockCreator getFluidBlockCreator();

	FluidFormSettings setLevelDecreasePerBlockFunction(ToIntFunction<IMaterial> levelDecreasePerBlockFunction);

	ToIntFunction<IMaterial> getLevelDecreasePerBlockFunction();

	FluidFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	FluidFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction);

	Function<IMaterial, MaterialColor> getMaterialColorFunction();

	FluidFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction);

	ToIntFunction<IMaterial> getLightValueFunction();

	FluidFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	FluidFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	FluidFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	FluidFormSettings setBucketItemCreator(BucketItemCreator bucketItemCreator);

	BucketItemCreator getBucketItemCreator();

	FluidFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	FluidFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	FluidFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
