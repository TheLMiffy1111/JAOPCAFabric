package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import thelm.jaopca.api.fluids.FluidFormSettings;
import thelm.jaopca.api.fluids.MaterialFormFluid;
import thelm.jaopca.api.fluids.MaterialFormFluidBlock;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluidBlock extends PlaceableFluidBlock implements MaterialFormFluidBlock {

	private final MaterialFormFluid fluid;
	protected final FluidFormSettings settings;

	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MaterialColor> materialColor = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();

	public JAOPCAFluidBlock(MaterialFormFluid fluid, FluidFormSettings settings) {
		super(FabricBlockSettings.of(Material.WATER).lightLevel(settings.getLightValueFunction().applyAsInt(fluid.getMaterial())).
				noCollision().ticksRandomly().dropsNothing().nonOpaque().build(), (PlaceableFluid)fluid.asFluid(),
				settings.getMaxLevelFunction().applyAsInt(fluid.getMaterial()));

		this.fluid = fluid;
		this.settings = settings;

		int flammability = settings.getFlammabilityFunction().applyAsInt(fluid.getMaterial());
		if(flammability > 0) {
			FlammableBlockRegistry.getDefaultInstance().add(this, flammability, settings.getFireSpreadSpeedFunction().applyAsInt(fluid.getMaterial()));
		}
	}

	@Override
	public Form getForm() {
		return fluid.getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return fluid.getMaterial();
	}

	@Override
	public Material getMaterial(BlockState blockState) {
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.getMaterialFunction().apply(getMaterial()));
		}
		return blockMaterial.get();
	}

	@Override
	public MaterialColor getMapColor(BlockState blockState, BlockView world, BlockPos pos) {
		if(!materialColor.isPresent()) {
			materialColor = Optional.of(settings.getMaterialColorFunction().apply(getMaterial()));
		}
		return materialColor.get();
	}

	@Override
	public float getHardness(BlockState blockState, BlockView world, BlockPos pos) {
		if(!blockHardness.isPresent()) {
			blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(getMaterial()));
		}
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getBlastResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(getMaterial()));
		}
		return (float)explosionResistance.getAsDouble();
	}
}
