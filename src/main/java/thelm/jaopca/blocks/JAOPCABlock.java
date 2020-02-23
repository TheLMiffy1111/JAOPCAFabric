package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.impl.mining.level.ToolManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.EntityContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import thelm.jaopca.api.blocks.BlockFormSettings;
import thelm.jaopca.api.blocks.MaterialFormBlock;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlock extends Block implements MaterialFormBlock {

	private final Form form;
	private final IMaterial material;
	protected final BlockFormSettings settings;

	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MaterialColor> materialColor = Optional.empty();
	protected boolean blocksMovement;
	protected Optional<BlockSoundGroup> soundType = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalDouble slipperiness = OptionalDouble.empty();
	protected VoxelShape shape;
	protected VoxelShape raytraceShape;

	public JAOPCABlock(Form form, IMaterial material, BlockFormSettings settings) {
		super(FabricBlockSettings.of(Material.METAL).lightLevel(settings.getLightValueFunction().applyAsInt(material)).
				nonOpaque().build());
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		shape = settings.getShape();
		raytraceShape = settings.getRaytraceShape();

		Identifier toolType = settings.getHarvestToolFunction().apply(material);
		if(toolType != null) {
			ToolManager.entry(this).putBreakByTool(new ItemTags.CachingTag(toolType), settings.getHarvestLevelFunction().applyAsInt(material));
		}

		int flammability = settings.getFlammabilityFunction().applyAsInt(material);
		if(flammability > 0) {
			FlammableBlockRegistry.getDefaultInstance().add(this, flammability, settings.getFireSpreadSpeedFunction().applyAsInt(material));
		}
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
	public Material getMaterial(BlockState blockState) {
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.getMaterialFunction().apply(material));
		}
		return blockMaterial.get();
	}

	@Override
	public MaterialColor getMapColor(BlockState blockState, BlockView world, BlockPos pos) {
		if(!materialColor.isPresent()) {
			materialColor = Optional.of(settings.getMaterialColorFunction().apply(material));
		}
		return materialColor.get();
	}

	@Override
	public BlockSoundGroup getSoundGroup(BlockState blockState) {
		if(!soundType.isPresent()) {
			soundType = Optional.of(settings.getSoundTypeFunction().apply(material));
		}
		return soundType.get();
	}

	@Override
	public float getHardness(BlockState blockState, BlockView world, BlockPos pos) {
		if(!blockHardness.isPresent()) {
			blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(material));
		}
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getBlastResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getSlipperiness() {
		if(!slipperiness.isPresent()) {
			slipperiness = OptionalDouble.of(settings.getSlipperinessFunction().applyAsDouble(material));
		}
		return (float)slipperiness.getAsDouble();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView world, BlockPos pos, EntityContext context) {
		return shape;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView world, BlockPos pos, EntityContext context) {
		return blocksMovement ? blockState.getOutlineShape(world, pos) : VoxelShapes.empty();
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView world, BlockPos pos) {
		return raytraceShape;
	}

	@Override
	public Text getName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getTranslationKey());
	}
}
