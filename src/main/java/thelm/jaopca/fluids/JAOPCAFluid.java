package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.world.WorldView;
import thelm.jaopca.api.fluids.FluidFormSettings;
import thelm.jaopca.api.fluids.MaterialFormFluid;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluid extends PlaceableFluid implements MaterialFormFluid {

	private final Form form;
	private final IMaterial material;
	protected final FluidFormSettings settings;

	protected OptionalInt tickRate = OptionalInt.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected Optional<Boolean> canSourcesMultiply = Optional.empty();
	protected Optional<Boolean> canFluidBeDisplaced = Optional.empty();
	protected OptionalInt levelDecreasePerBlock = OptionalInt.empty();

	public JAOPCAFluid(Form form, IMaterial material, FluidFormSettings settings) {
		super(settings.getMaxLevelFunction().applyAsInt(material));
		this.form = form;
		this.material = material;
		this.settings = settings;
	}

	@Override
	public Form getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public int getTickRate(WorldView world) {
		if(!tickRate.isPresent()) {
			tickRate = OptionalInt.of(settings.getTickRateFunction().applyAsInt(material));
		}
		return tickRate.getAsInt();
	}

	@Override
	protected float getBlastResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	protected boolean isInfinite() {
		if(!canSourcesMultiply.isPresent()) {
			canSourcesMultiply = Optional.of(settings.getCanSourcesMultiplyFunction().test(material));
		}
		return canSourcesMultiply.get();
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView world) {
		if(!levelDecreasePerBlock.isPresent()) {
			levelDecreasePerBlock = OptionalInt.of(settings.getLevelDecreasePerBlockFunction().applyAsInt(material));
		}
		return levelDecreasePerBlock.getAsInt();
	}

	@Override
	public Item getBucketItem() {
		return FluidFormTypeImpl.INSTANCE.getMaterialFormInfo(form, material).getBucketItem();
	}

	@Override
	protected PlaceableFluidBlock getFluidBlock() {
		return (PlaceableFluidBlock)FluidFormTypeImpl.INSTANCE.getMaterialFormInfo(form, material).getMaterialFormFluidBlock().asBlock();
	}

	@Override
	public FluidState getSourceState() {
		return getDefaultState().with(levelProperty, maxLevel);
	}
}
