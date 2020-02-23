package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalInt;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import thelm.jaopca.api.fluids.FluidFormSettings;
import thelm.jaopca.api.fluids.MaterialFormBucketItem;
import thelm.jaopca.api.fluids.MaterialFormFluid;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABucketItem extends Item implements MaterialFormBucketItem {

	private final MaterialFormFluid fluid;
	private final FluidFormSettings settings;

	private OptionalInt itemStackLimit = OptionalInt.empty();
	private Optional<Boolean> beaconPayment = Optional.empty();
	private Optional<Boolean> hasEffect = Optional.empty();
	private Optional<Rarity> rarity = Optional.empty();
	private OptionalInt burnTime = OptionalInt.empty();

	public JAOPCABucketItem(MaterialFormFluid fluid, FluidFormSettings settings) {
		super(new Item.Settings().recipeRemainder(Items.BUCKET).
				maxCount(settings.getItemStackLimitFunction().applyAsInt(fluid.getMaterial())).
				group(ItemFormTypeImpl.getItemGroup()));
		this.fluid = fluid;
		this.settings = settings;

		int burnTime = settings.getBurnTimeFunction().applyAsInt(fluid.getMaterial());
		if(burnTime >= 0) {
			FuelRegistry.INSTANCE.add(this, burnTime);
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
	public boolean hasEnchantmentGlint(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(getMaterial()));
		}
		return hasEffect.get() || super.hasEnchantmentGlint(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(getMaterial()));
		}
		return rarity.get();
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidHandling.NONE);
		if(rayTraceResult.getType() == HitResult.Type.MISS) {
			return new TypedActionResult<>(ActionResult.PASS, stack);
		}
		else if(rayTraceResult.getType() != HitResult.Type.BLOCK) {
			return new TypedActionResult<>(ActionResult.PASS, stack);
		}
		else {
			BlockHitResult blockRayTraceResult = (BlockHitResult)rayTraceResult;
			BlockPos resultPos = blockRayTraceResult.getBlockPos();
			if(world.canPlayerModifyAt(player, resultPos) && player.canPlaceOn(resultPos, blockRayTraceResult.getSide(), stack)) {
				BlockPos pos = blockRayTraceResult.getBlockPos().offset(blockRayTraceResult.getSide());
				if(placeFluid(player, world, pos, blockRayTraceResult)) {
					onLiquidPlaced(world, stack, pos);
					if(player instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
					}
					player.incrementStat(Stats.USED.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, emptyBucket(stack, player));
				}
				else {
					return new TypedActionResult<>(ActionResult.FAIL, stack);
				}
			}
			else {
				return new TypedActionResult<>(ActionResult.FAIL, stack);
			}
		}
	}

	protected ItemStack emptyBucket(ItemStack stack, PlayerEntity player) {
		return !player.abilities.creativeMode ? new ItemStack(Items.BUCKET) : stack;
	}

	public void onLiquidPlaced(World world, ItemStack stack, BlockPos pos) {}

	public boolean placeFluid(PlayerEntity player, World world, BlockPos pos, BlockHitResult rayTraceResult) {
		BlockState blockState = world.getBlockState(pos);
		Material blockMaterial = blockState.getMaterial();
		boolean flag = !blockMaterial.isSolid();
		boolean flag1 = blockMaterial.isReplaceable();
		if(world.isAir(pos) || flag || flag1) {
			if(world.dimension.doesWaterVaporize() && fluid.asFluid().matches(FluidTags.WATER)) {
				int i = pos.getX();
				int j = pos.getY();
				int k = pos.getZ();
				world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F+(world.random.nextFloat()-world.random.nextFloat())*0.8F);
				for(int l = 0; l < 8; ++l) {
					world.addParticle(ParticleTypes.LARGE_SMOKE, i+Math.random(), j+Math.random(), k+Math.random(), 0, 0, 0);
				}
			}
			else {
				if(!world.isClient && (flag || flag1) && !blockMaterial.isLiquid()) {
					world.breakBlock(pos, true);
				}
				playEmptySound(player, world, pos);
				world.setBlockState(pos, fluid.asFluid().getDefaultState().getBlockState(), 11);
			}
			return true;
		}
		else {
			return rayTraceResult == null ? false : placeFluid(player, world, rayTraceResult.getBlockPos().offset(rayTraceResult.getSide()), null);
		}
	}

	protected void playEmptySound(PlayerEntity player, IWorld world, BlockPos pos) {
		SoundEvent soundEvent = settings.getEmptySoundSupplier().get();
		if(soundEvent == null) {
			soundEvent = fluid.asFluid().matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		}
		world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1, 1);
	}

	@Override
	public Text getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+getForm().getName(), getMaterial(), getTranslationKey());
	}
}
