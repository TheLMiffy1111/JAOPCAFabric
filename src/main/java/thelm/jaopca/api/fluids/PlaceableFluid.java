package thelm.jaopca.api.fluids;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.FluidStateImpl;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class PlaceableFluid extends Fluid {

	public static final float EIGHT_NINTHS = 8/9F;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> CACHE = ThreadLocal.withInitial(()->{
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			@Override
			protected void rehash(int newN) {}
		};
		object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
		return object2bytelinkedopenhashmap;
	});

	protected final StateManager<Fluid, FluidState> stateManager;
	private final Map<FluidState, VoxelShape> shapeMap = new IdentityHashMap<>();

	protected final int maxLevel;
	protected final IntProperty levelProperty;

	public PlaceableFluid(int maxLevel) {
		this.maxLevel = maxLevel;
		levelProperty = IntProperty.of("level", 1, maxLevel+1);

		StateManager.Builder<Fluid, FluidState> builder = new StateManager.Builder<>(this);
		appendProperties(builder);
		stateManager = builder.build(FluidStateImpl::new);
		setDefaultState(stateManager.getDefaultState());
	}

	public IntProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateManager<Fluid, FluidState> getStateManager() {
		return stateManager;
	}

	protected abstract boolean isInfinite();

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockView world, BlockPos pos, Fluid fluid, Direction face) {
		return face == Direction.DOWN && !matchesType(fluid);
	}

	protected abstract int getLevelDecreasePerBlock(WorldView world);

	@Override
	protected BlockState toBlockState(FluidState fluidState) {
		PlaceableFluidBlock block = getFluidBlock();
		IntProperty blockLevelProperty = block.getLevelProperty();
		int fluidLevel = fluidState.get(levelProperty);
		int blockLevel = fluidLevel > maxLevel ? maxLevel : maxLevel-fluidLevel;
		return block.getDefaultState().with(blockLevelProperty, blockLevel);
	}

	protected abstract PlaceableFluidBlock getFluidBlock();

	@Override
	protected Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
		double x = 0;
		double y = 0;
		try(BlockPos.PooledMutable mutablePos = BlockPos.PooledMutable.get()) {
			for(Direction offset : Direction.Type.HORIZONTAL) {
				mutablePos.set(pos).setOffset(offset);
				FluidState offsetState = world.getFluidState(mutablePos);
				if(!isSameOrEmpty(offsetState)) {
					continue;
				}
				float offsetHeight = offsetState.getHeight();
				float heightDiff = 0;
				if(offsetHeight == 0) {
					if(!world.getBlockState(mutablePos).getMaterial().blocksMovement()) {
						BlockPos posDown = mutablePos.down();
						final FluidState belowState = world.getFluidState(posDown);
						if(isSameOrEmpty(belowState)) {
							offsetHeight = belowState.getHeight();
							if(offsetHeight > 0) {
								heightDiff = state.getHeight()-(offsetHeight-EIGHT_NINTHS);
							}
						}
					}
				}
				else if(offsetHeight > 0) {
					heightDiff = state.getHeight() - offsetHeight;
				}
				if(heightDiff == 0) {
					continue;
				}
				x += offset.getOffsetX()*heightDiff;
				y += offset.getOffsetZ()*heightDiff;
			}
			Vec3d flow = new Vec3d(x, 0, y);
			if(state.get(levelProperty).intValue() == 0) {
				for(Direction offset : Direction.Type.HORIZONTAL) {
					mutablePos.set(pos).setOffset(offset);
					if(causesDownwardCurrent(world, mutablePos, offset) || causesDownwardCurrent(world, mutablePos.up(), offset)) {
						flow = flow.normalize().add(0, -6, 0);
						break;
					}
				}
			}
			return flow.normalize();
		}
	}

	private boolean isSameOrEmpty(FluidState otherState) {
		return otherState.isEmpty() || otherState.getFluid().matchesType(this);
	}

	protected boolean causesDownwardCurrent(BlockView world, BlockPos pos, Direction face) {
		BlockState blockState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);
		return !fluidState.getFluid().matchesType(this) && (face == Direction.UP ||
				(blockState.getMaterial() != Material.ICE &&
				blockState.isSideSolidFullSquare(world, pos, face)));
	}

	protected void flowAround(IWorld world, BlockPos pos, FluidState fluidState) {
		if(!fluidState.isEmpty()) {
			BlockState blockState = world.getBlockState(pos);
			BlockPos downPos = pos.down();
			BlockState downBlockState = world.getBlockState(downPos);
			FluidState newFluidState = getUpdatedState(world, downPos, downBlockState);
			if(canFlow(world, pos, blockState, Direction.DOWN, downPos, downBlockState, world.getFluidState(downPos), newFluidState.getFluid())) {
				flow(world, downPos, downBlockState, Direction.DOWN, newFluidState);
				if(getAdjacentSourceCount(world, pos) >= 3) {
					flowAdjacent(world, pos, fluidState, blockState);
				}
			}
			else if(fluidState.isStill() || !canFlowDown(world, newFluidState.getFluid(), pos, blockState, downPos, downBlockState)) {
				flowAdjacent(world, pos, fluidState, blockState);
			}
		}
	}

	protected void flowAdjacent(IWorld world, BlockPos pos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.getLevel() - getLevelDecreasePerBlock(world);
		if(i > 0) {
			Map<Direction, FluidState> map = getSpread(world, pos, blockState);
			for(Map.Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = entry.getKey();
				FluidState offsetFluidState = entry.getValue();
				BlockPos offsetPos = pos.offset(direction);
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				if(canFlow(world, pos, blockState, direction, offsetPos, offsetBlockState, world.getFluidState(offsetPos), offsetFluidState.getFluid())) {
					flow(world, offsetPos, offsetBlockState, direction, offsetFluidState);
				}
			}
		}
	}

	protected FluidState getUpdatedState(WorldView world, BlockPos pos, BlockState blockState) {
		int i = 0;
		int j = 0;
		for(Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(direction);
			BlockState offsetBlockState = world.getBlockState(offsetPos);
			FluidState offsetFluidState = offsetBlockState.getFluidState();
			if(offsetFluidState.getFluid().matchesType(this) && receivesFlow(direction, world, pos, blockState, offsetPos, offsetBlockState)) {
				if(offsetFluidState.isStill()) {
					++j;
				}
				i = Math.max(i, offsetFluidState.getLevel());
			}
		}
		if(isInfinite() && j >= 2) {
			BlockState blockstate1 = world.getBlockState(pos.down());
			FluidState fluidState1 = blockstate1.getFluidState();
			if(blockstate1.getMaterial().isSolid() || isMatchingAndStill(fluidState1)) {
				return getDefaultState().with(levelProperty, maxLevel);
			}
		}
		BlockPos upPos = pos.up();
		BlockState upBlockState = world.getBlockState(upPos);
		FluidState upFluidState = upBlockState.getFluidState();
		if(!upFluidState.isEmpty() && upFluidState.getFluid().matchesType(this) && receivesFlow(Direction.UP, world, pos, blockState, upPos, upBlockState)) {
			return getDefaultState().with(levelProperty, maxLevel+1);
		}
		else {
			int k = i - getLevelDecreasePerBlock(world);
			if(k <= 0) {
				return Fluids.EMPTY.getDefaultState();
			}
			else {
				return getDefaultState().with(levelProperty, k);
			}
		}
	}

	protected boolean receivesFlow(Direction direction, BlockView world, BlockPos fromPos, BlockState fromBlockState, BlockPos toPos, BlockState toBlockState) {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> cache;
		if(!fromBlockState.getBlock().hasDynamicBounds() && !toBlockState.getBlock().hasDynamicBounds()) {
			cache = CACHE.get();
		}
		else {
			cache = null;
		}
		Block.NeighborGroup cacheKey;
		if(cache != null) {
			cacheKey = new Block.NeighborGroup(fromBlockState, toBlockState, direction);
			byte b0 = cache.getAndMoveToFirst(cacheKey);
			if(b0 != 127) {
				return b0 != 0;
			}
		}
		else {
			cacheKey = null;
		}
		VoxelShape fromShape = fromBlockState.getCollisionShape(world, fromPos);
		VoxelShape toShape = toBlockState.getCollisionShape(world, toPos);
		boolean flag = !VoxelShapes.adjacentSidesCoverSquare(fromShape, toShape, direction);
		if(cache != null) {
			if(cache.size() == 200) {
				cache.removeLastByte();
			}
			cache.putAndMoveToFirst(cacheKey, (byte)(flag ? 1 : 0));
		}
		return flag;
	}

	protected void flow(IWorld world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
		if(blockState.getBlock() instanceof FluidFillable) {
			((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, pos, blockState, fluidState);
		}
		else {
			if(!blockState.isAir()) {
				beforeBreakingBlock(world, pos, blockState);
			}
			world.setBlockState(pos, fluidState.getBlockState(), 3);
		}
	}

	protected void beforeBreakingBlock(IWorld world, BlockPos pos, BlockState blockState) {
		BlockEntity tile = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(blockState, world.getWorld(), pos, tile);
	}

	protected static short getPosKey(BlockPos pos, BlockPos otherPos) {
		int dx = otherPos.getX() - pos.getX();
		int dz = otherPos.getZ() - pos.getZ();
		return (short)((dx + 128 & 255) << 8 | dz + 128 & 255);
	}

	protected Map<Direction, FluidState> getSpread(WorldView world, BlockPos pos, BlockState blockState) {
		int i = 1000;
		Map<Direction, FluidState> map = new EnumMap(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> stateMap = new Short2ObjectOpenHashMap();
		Short2BooleanMap canFlowDownMap = new Short2BooleanOpenHashMap();
		for(Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(direction);
			short key = getPosKey(pos, offsetPos);
			Pair<BlockState, FluidState> offsetState = stateMap.computeIfAbsent(key, k->{
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
			});
			BlockState offsetBlockState = offsetState.getLeft();
			FluidState offsetFluidState = offsetState.getRight();
			FluidState newOffsetFluidState = this.getUpdatedState(world, offsetPos, offsetBlockState);
			if(canFlowThrough(world, newOffsetFluidState.getFluid(), pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidState)) {
				boolean flag = canFlowDownMap.computeIfAbsent(key, k->{
					BlockPos offsetDownPos = offsetPos.down();
					BlockState offsetDownState = world.getBlockState(offsetDownPos);
					return canFlowDown(world, this, offsetPos, offsetBlockState, offsetDownPos, offsetDownState);
				});
				int j = 0;
				if(!flag) {
					j = getFlowDistance(world, offsetPos, 1, direction.getOpposite(), offsetBlockState, pos, stateMap, canFlowDownMap);
				}
				if(j < i) {
					map.clear();
				}
				if(j <= i) {
					map.put(direction, newOffsetFluidState);
					i = j;
				}
			}
		}
		return map;
	}

	protected int getFlowDistance(WorldView world, BlockPos pos, int distance, Direction fromDirection, BlockState blockState, BlockPos startPos, Short2ObjectMap<Pair<BlockState, FluidState>> stateMap, Short2BooleanMap canFlowDownMap) {
		int i = 1000;
		for(Direction direction : Direction.Type.HORIZONTAL) {
			if(direction != fromDirection) {
				BlockPos offsetPos = pos.offset(direction);
				short key = getPosKey(startPos, offsetPos);
				Pair<BlockState, FluidState> pair = stateMap.computeIfAbsent(key, k->{
					BlockState offsetBlockState = world.getBlockState(offsetPos);
					return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
				});
				BlockState offsetBlockState = pair.getLeft();
				FluidState offsetFluidstate = pair.getRight();
				if(canFlowThrough(world, this, pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidstate)) {
					boolean flag = canFlowDownMap.computeIfAbsent(key, k->{
						BlockPos offsetDownPos = offsetPos.down();
						BlockState offsetDownState = world.getBlockState(offsetDownPos);
						return canFlowDown(world, this, offsetPos, offsetBlockState, offsetDownPos, offsetDownState);
					});
					if(flag) {
						return distance;
					}
					if(distance < getSlopeFindDistance(world)) {
						int j = getFlowDistance(world, offsetPos, distance+1, direction.getOpposite(), offsetBlockState, startPos, stateMap, canFlowDownMap);
						if(j < i) {
							i = j;
						}
					}
				}
			}
		}
		return i;
	}

	protected boolean isMatchingAndStill(FluidState fluidState) {
		return fluidState.getFluid().matchesType(this) && fluidState.isStill();
	}

	protected int getSlopeFindDistance(WorldView world) {
		return ceilDiv(ceilDiv(maxLevel, getLevelDecreasePerBlock(world)), 2);
	}

	protected int getAdjacentSourceCount(WorldView world, BlockPos pos) {
		int count = 0;
		for(Direction offset : Direction.Type.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(offset);
			FluidState offsetState = world.getFluidState(offsetPos);
			if(isMatchingAndStill(offsetState)) {
				++count;
			}
		}
		return count;
	}

	protected boolean canFill(BlockView world, BlockPos pos, BlockState blockState, Fluid fluid) {
		Block block = blockState.getBlock();
		if(block instanceof FluidFillable) {
			return ((FluidFillable)block).canFillWithFluid(world, pos, blockState, fluid);
		}
		if(block instanceof DoorBlock || block.matches(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE ||
				block == Blocks.BUBBLE_COLUMN) {
			return false;
		}
		Material blockMaterial = blockState.getMaterial();
		return blockMaterial != Material.PORTAL && blockMaterial != Material.STRUCTURE_VOID && blockMaterial != Material.UNDERWATER_PLANT
				&& blockMaterial != Material.SEAGRASS && !blockMaterial.blocksMovement();
	}

	protected boolean canFlow(BlockView world, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid) {
		return toFluidState.method_15764(world, toPos, fluid, direction) && this.receivesFlow(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canFill(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowThrough(BlockView world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState) {
		return !isMatchingAndStill(toFluidState) && receivesFlow(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canFill(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowDown(BlockView world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, BlockPos downPos, BlockState downState) {
		return receivesFlow(Direction.DOWN, world, fromPos, fromBlockState, downPos, downState)
				&& (downState.getFluidState().getFluid().matchesType(this) || canFill(world, downPos, downState, fluid));
	}

	protected int getNextTickDelay(World world, BlockPos pos, FluidState fluidState, FluidState newFluidState) {
		return getTickRate(world);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, FluidState fluidState) {
		if(!fluidState.isStill()) {
			FluidState newFluidState = getUpdatedState(world, pos, world.getBlockState(pos));
			int delay = getNextTickDelay(world, pos, fluidState, newFluidState);
			if(newFluidState.isEmpty()) {
				fluidState = newFluidState;
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			}
			else if(!newFluidState.equals(fluidState)) {
				fluidState = newFluidState;
				BlockState blockState = fluidState.getBlockState();
				world.setBlockState(pos, blockState, 2);
				world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), delay);
				world.updateNeighborsAlways(pos, blockState.getBlock());
			}
		}
		flowAround(world, pos, fluidState);
	}

	protected int getBlockLevelFromState(FluidState fluidState) {
		int level = fluidState.get(levelProperty);
		if(level > maxLevel) {
			return maxLevel;
		}
		return maxLevel - Math.min(fluidState.getLevel(), maxLevel);
	}

	protected static boolean isFluidAboveEqual(FluidState fluidState, BlockView world, BlockPos pos) {
		return fluidState.getFluid().matchesType(world.getFluidState(pos.up()).getFluid());
	}

	@Override
	public float getHeight(FluidState fluidState, BlockView world, BlockPos pos) {
		return fluidState.getHeight();
	}

	@Override
	public float getHeight(FluidState fluidState) {
		return 0.9F*fluidState.getLevel()/maxLevel;
	}

	@Override
	public boolean isStill(FluidState fluidState) {
		return fluidState.get(levelProperty).intValue() == maxLevel;
	}

	@Override
	public int getLevel(FluidState fluidState) {
		return Math.min(maxLevel, fluidState.get(levelProperty));
	}

	@Override
	public VoxelShape getShape(FluidState fluidState, BlockView world, BlockPos pos) {
		return shapeMap.computeIfAbsent(fluidState, s->VoxelShapes.cuboid(0, 0, 0, 1, s.getHeight(world, pos), 1));
	}

	public static int ceilDiv(int x, int y) {
		int r = x/y;
		if((x^y) >= 0 && (r*y != x)) {
			r++;
		}
		return r;
	}
}
