package thelm.jaopca.fluids;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Rarity;
import thelm.jaopca.api.fluids.BucketItemCreator;
import thelm.jaopca.api.fluids.FluidBlockCreator;
import thelm.jaopca.api.fluids.FluidCreator;
import thelm.jaopca.api.fluids.FluidFormSettings;
import thelm.jaopca.api.forms.FormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelperImpl;

public class FluidFormSettingsImpl implements FluidFormSettings {

	FluidFormSettingsImpl() {}

	private FluidCreator fluidCreator = JAOPCAFluid::new;
	private ToIntFunction<IMaterial> maxLevelFunction = material->8;
	private ToIntFunction<IMaterial> tickRateFunction = material->5;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->100;
	private Predicate<IMaterial> canSourcesMultiplyFunction = material->false;
	private Supplier<SoundEvent> fillSoundSupplier = ()->null;
	private Supplier<SoundEvent> emptySoundSupplier = ()->null;
	private ToIntFunction<IMaterial> densityFunction = material->1000;
	private ToIntFunction<IMaterial> viscosityFunction = material->tickRateFunction.applyAsInt(material)*200;
	private ToIntFunction<IMaterial> temperatureFunction = material->300;
	private Function<IMaterial, Rarity> displayRarityFunction = material->Rarity.COMMON;
	private FluidBlockCreator fluidBlockCreator = JAOPCAFluidBlock::new;
	private ToIntFunction<IMaterial> levelDecreasePerBlockFunction = material->1;
	private Function<IMaterial, Material> materialFunction = material->Material.WATER;
	private Function<IMaterial, MaterialColor> materialColorFunction = material->{
		int color = material.getColor();
		return Arrays.stream(MaterialColor.COLORS).filter(Objects::nonNull).
				min((matColor1, matColor2)->Integer.compare(
						MiscHelperImpl.INSTANCE.squareColorDifference(color, matColor1.color),
						MiscHelperImpl.INSTANCE.squareColorDifference(color, matColor2.color))).
				orElse(MaterialColor.IRON);
	};
	private ToIntFunction<IMaterial> lightValueFunction = material->0;
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->100;
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private BucketItemCreator bucketItemCreator = JAOPCABucketItem::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->64;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

	@Override
	public FormType getType() {
		return FluidFormTypeImpl.INSTANCE;
	}

	@Override
	public FluidFormSettings setFluidCreator(FluidCreator fluidCreator) {
		this.fluidCreator = fluidCreator;
		return this;
	}

	@Override
	public FluidCreator getFluidCreator() {
		return fluidCreator;
	}

	@Override
	public FluidFormSettings setMaxLevelFunction(ToIntFunction<IMaterial> maxLevelFunction) {
		this.maxLevelFunction = maxLevelFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getMaxLevelFunction() {
		return maxLevelFunction;
	}

	@Override
	public FluidFormSettings setTickRateFunction(ToIntFunction<IMaterial> tickRateFunction) {
		this.tickRateFunction = tickRateFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getTickRateFunction() {
		return tickRateFunction;
	}

	@Override
	public FluidFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction) {
		this.explosionResistanceFunction = explosionResistanceFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getExplosionResistanceFunction() {
		return explosionResistanceFunction;
	}

	@Override
	public FluidFormSettings setCanSourcesMultiplyFunction(Predicate<IMaterial> canSourcesMultiplyFunction) {
		this.canSourcesMultiplyFunction = canSourcesMultiplyFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanSourcesMultiplyFunction() {
		return canSourcesMultiplyFunction;
	}

	@Override
	public FluidFormSettings setFillSoundSupplier(Supplier<SoundEvent> fillSoundSupplier) {
		this.fillSoundSupplier = fillSoundSupplier;
		return this;
	}

	@Override
	public Supplier<SoundEvent> getFillSoundSupplier() {
		return fillSoundSupplier;
	}

	@Override
	public FluidFormSettings setEmptySoundSupplier(Supplier<SoundEvent> emptySoundSupplier) {
		this.emptySoundSupplier = emptySoundSupplier;
		return this;
	}

	@Override
	public Supplier<SoundEvent> getEmptySoundSupplier() {
		return emptySoundSupplier;
	}

	@Override
	public FluidFormSettings setDensityFunction(ToIntFunction<IMaterial> densityFunction) {
		this.densityFunction = densityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getDensityFunction() {
		return densityFunction;
	}

	@Override
	public FluidFormSettings setViscosityFunction(ToIntFunction<IMaterial> viscosityFunction) {
		this.viscosityFunction = viscosityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getViscosityFunction() {
		return viscosityFunction;
	}

	@Override
	public FluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction) {
		this.temperatureFunction = temperatureFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getTemperatureFunction() {
		return temperatureFunction;
	}

	@Override
	public FluidFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Rarity> getDisplayRarityFunction() {
		return displayRarityFunction;
	}

	@Override
	public FluidFormSettings setFluidBlockCreator(FluidBlockCreator fluidBlockCreator) {
		this.fluidBlockCreator = fluidBlockCreator;
		return this;
	}

	@Override
	public FluidBlockCreator getFluidBlockCreator() {
		return fluidBlockCreator;
	}

	@Override
	public FluidFormSettings setLevelDecreasePerBlockFunction(ToIntFunction<IMaterial> levelDecreasePerBlockFunction) {
		this.levelDecreasePerBlockFunction = levelDecreasePerBlockFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLevelDecreasePerBlockFunction() {
		return levelDecreasePerBlockFunction;
	}

	@Override
	public FluidFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction) {
		this.materialFunction = materialFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Material> getMaterialFunction() {
		return materialFunction;
	}

	@Override
	public FluidFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction) {
		this.materialColorFunction = materialColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MaterialColor> getMaterialColorFunction() {
		return materialColorFunction;
	}

	@Override
	public FluidFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction) {
		this.lightValueFunction = lightValueFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLightValueFunction() {
		return lightValueFunction;
	}

	@Override
	public FluidFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction) {
		this.blockHardnessFunction = blockHardnessFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getBlockHardnessFunction() {
		return blockHardnessFunction;
	}

	@Override
	public FluidFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction) {
		this.flammabilityFunction = flammabilityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFlammabilityFunction() {
		return flammabilityFunction;
	}

	@Override
	public FluidFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction) {
		this.fireSpreadSpeedFunction = fireSpreadSpeedFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFireSpreadSpeedFunction() {
		return fireSpreadSpeedFunction;
	}

	@Override
	public FluidFormSettings setBucketItemCreator(BucketItemCreator bucketItemCreator) {
		this.bucketItemCreator = bucketItemCreator;
		return this;
	}

	@Override
	public BucketItemCreator getBucketItemCreator() {
		return bucketItemCreator;
	}

	@Override
	public FluidFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction) {
		this.itemStackLimitFunction = itemStackLimitFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getItemStackLimitFunction() {
		return itemStackLimitFunction;
	}

	@Override
	public FluidFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction) {
		this.hasEffectFunction = hasEffectFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getHasEffectFunction() {
		return hasEffectFunction;
	}

	@Override
	public FluidFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction) {
		this.burnTimeFunction = burnTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getBurnTimeFunction() {
		return burnTimeFunction;
	}
}
