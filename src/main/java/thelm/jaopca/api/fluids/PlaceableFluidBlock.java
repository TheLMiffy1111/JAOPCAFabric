package thelm.jaopca.api.fluids;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class PlaceableFluidBlock extends Block implements FluidDrainable {

	protected final StateManager<Block, BlockState> stateManager;

	protected final PlaceableFluid fluid;
	protected final int maxLevel;
	protected final IntProperty levelProperty;

	public PlaceableFluidBlock(Block.Settings settings, PlaceableFluid fluid, int maxLevel) {
		super(settings);

		this.fluid = fluid;
		this.maxLevel = maxLevel;
		levelProperty = IntProperty.of("level", 0, maxLevel);

		StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
		appendProperties(builder);
		stateManager = builder.build(BlockState::new);
		setDefaultState(stateManager.getDefaultState());
	}

	public IntProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	public void randomTick(BlockState blockState, ServerWorld world, BlockPos pos, Random random) {
		world.getFluidState(pos).onRandomTick(world, pos, random);
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView reader, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView world, BlockPos pos, BlockPlacementEnvironment type) {
		return !fluid.matches(FluidTags.LAVA);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		IntProperty fluidLevelProperty = fluid.getLevelProperty();
		int blockLevel = blockState.get(levelProperty);
		int fluidLevel = blockLevel >= maxLevel ? maxLevel+1 : maxLevel-blockLevel;
		return fluid.getDefaultState().with(fluidLevelProperty, fluidLevel);
	}

	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getFluidState().getFluid().matchesType(fluid) || super.isOpaque(blockState);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public int getTickRate(WorldView world) {
		return fluid.getTickRate(world);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos pos, BlockState oldBlockState, boolean isMoving) {
		if(receiveNeighborFluids(world, pos, blockState)) {
			world.getFluidTickScheduler().schedule(pos, blockState.getFluidState().getFluid(), getTickRate(world));
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if(blockState.getFluidState().isStill() || facingState.getFluidState().isStill()) {
			world.getFluidTickScheduler().schedule(currentPos, blockState.getFluidState().getFluid(), getTickRate(world));
		}
		return super.getStateForNeighborUpdate(blockState, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if(receiveNeighborFluids(world, pos, blockState)) {
			world.getFluidTickScheduler().schedule(pos, blockState.getFluidState().getFluid(), getTickRate(world));
		}
	}

	public boolean receiveNeighborFluids(World world, BlockPos pos, BlockState blockState) {
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateManager<Block, BlockState> getStateManager() {
		return stateManager;
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState blockState) {
		if(blockState.get(levelProperty) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			return fluid;
		}
		else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
		if(fluid.matches(FluidTags.LAVA)) {
			entity.setInLava();
		}
	}
}
