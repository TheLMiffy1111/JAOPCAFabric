package thelm.jaopca.custom.utils;

import java.util.Locale;

import com.google.common.collect.HashBiMap;

import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class BlockDeserializationHelper {

	public static final BlockDeserializationHelper INSTANCE = new BlockDeserializationHelper();

	private BlockDeserializationHelper() {}

	private static final HashBiMap<String, Material> BLOCK_MATERIALS = HashBiMap.create();
	private static final HashBiMap<String, BlockSoundGroup> SOUND_TYPES = HashBiMap.create();

	public Material getBlockMaterial(String name) {
		return BLOCK_MATERIALS.get(name.toLowerCase(Locale.US));
	}

	public String getBlockMaterialName(Material material) {
		return BLOCK_MATERIALS.inverse().get(material);
	}

	public void putBlockMaterial(String name, Material material) {
		BLOCK_MATERIALS.put(name.toLowerCase(Locale.US), material);
	}

	public BlockSoundGroup getBlockSoundGroup(String name) {
		return SOUND_TYPES.get(name.toLowerCase(Locale.US));
	}

	public String getBlockSoundGroupName(BlockSoundGroup sound) {
		return SOUND_TYPES.inverse().get(sound);
	}

	public void putBlockSoundGroup(String name, BlockSoundGroup sound) {
		SOUND_TYPES.put(name.toLowerCase(Locale.US), sound);
	}

	static {
		INSTANCE.putBlockMaterial("air", Material.AIR);
		INSTANCE.putBlockMaterial("structure_void", Material.STRUCTURE_VOID);
		INSTANCE.putBlockMaterial("portal", Material.PORTAL);
		INSTANCE.putBlockMaterial("carpet", Material.CARPET);
		INSTANCE.putBlockMaterial("plant", Material.PLANT);
		INSTANCE.putBlockMaterial("underwater_plant", Material.UNDERWATER_PLANT);
		INSTANCE.putBlockMaterial("replaceable_plant", Material.REPLACEABLE_PLANT);
		INSTANCE.putBlockMaterial("seagrass", Material.SEAGRASS);
		INSTANCE.putBlockMaterial("water", Material.WATER);
		INSTANCE.putBlockMaterial("bubble_column", Material.BUBBLE_COLUMN);
		INSTANCE.putBlockMaterial("lava", Material.LAVA);
		INSTANCE.putBlockMaterial("snow", Material.SNOW);
		INSTANCE.putBlockMaterial("fire", Material.FIRE);
		INSTANCE.putBlockMaterial("part", Material.PART);
		INSTANCE.putBlockMaterial("cobweb", Material.COBWEB);
		INSTANCE.putBlockMaterial("redstone_lamp", Material.REDSTONE_LAMP);
		INSTANCE.putBlockMaterial("clay", Material.CLAY);
		INSTANCE.putBlockMaterial("earth", Material.EARTH);
		INSTANCE.putBlockMaterial("organic", Material.ORGANIC);
		INSTANCE.putBlockMaterial("packed_ice", Material.PACKED_ICE);
		INSTANCE.putBlockMaterial("sand", Material.SAND);
		INSTANCE.putBlockMaterial("sponge", Material.SPONGE);
		INSTANCE.putBlockMaterial("shulker_box", Material.SHULKER_BOX);
		INSTANCE.putBlockMaterial("wood", Material.WOOD);
		INSTANCE.putBlockMaterial("bamboo_sapling", Material.BAMBOO_SAPLING);
		INSTANCE.putBlockMaterial("bamboo", Material.BAMBOO);
		INSTANCE.putBlockMaterial("wool", Material.WOOL);
		INSTANCE.putBlockMaterial("tnt", Material.TNT);
		INSTANCE.putBlockMaterial("leaves", Material.LEAVES);
		INSTANCE.putBlockMaterial("glass", Material.GLASS);
		INSTANCE.putBlockMaterial("ice", Material.ICE);
		INSTANCE.putBlockMaterial("cactus", Material.CACTUS);
		INSTANCE.putBlockMaterial("stone", Material.STONE);
		INSTANCE.putBlockMaterial("metal", Material.METAL);
		INSTANCE.putBlockMaterial("snow_block", Material.SNOW_BLOCK);
		INSTANCE.putBlockMaterial("anvil", Material.ANVIL);
		INSTANCE.putBlockMaterial("barrier", Material.BARRIER);
		INSTANCE.putBlockMaterial("piston", Material.PISTON);
		INSTANCE.putBlockMaterial("coral", Material.UNUSED_PLANT);
		INSTANCE.putBlockMaterial("gourd", Material.PUMPKIN);
		INSTANCE.putBlockMaterial("dragon_egg", Material.EGG);
		INSTANCE.putBlockMaterial("cake", Material.CAKE);

		INSTANCE.putBlockSoundGroup("wood", BlockSoundGroup.WOOD);
		INSTANCE.putBlockSoundGroup("gravel", BlockSoundGroup.GRAVEL);
		INSTANCE.putBlockSoundGroup("plant", BlockSoundGroup.GRASS);
		INSTANCE.putBlockSoundGroup("stone", BlockSoundGroup.STONE);
		INSTANCE.putBlockSoundGroup("metal", BlockSoundGroup.METAL);
		INSTANCE.putBlockSoundGroup("glass", BlockSoundGroup.GLASS);
		INSTANCE.putBlockSoundGroup("cloth", BlockSoundGroup.WOOL);
		INSTANCE.putBlockSoundGroup("sand", BlockSoundGroup.SAND);
		INSTANCE.putBlockSoundGroup("snow", BlockSoundGroup.SNOW);
		INSTANCE.putBlockSoundGroup("ladder", BlockSoundGroup.LADDER);
		INSTANCE.putBlockSoundGroup("anvil", BlockSoundGroup.ANVIL);
		INSTANCE.putBlockSoundGroup("slime", BlockSoundGroup.SLIME);
		INSTANCE.putBlockSoundGroup("wet_grass", BlockSoundGroup.WET_GRASS);
		INSTANCE.putBlockSoundGroup("coral", BlockSoundGroup.CORAL);
		INSTANCE.putBlockSoundGroup("bamboo", BlockSoundGroup.BAMBOO);
		INSTANCE.putBlockSoundGroup("bamboo_sapling", BlockSoundGroup.BAMBOO_SAPLING);
		INSTANCE.putBlockSoundGroup("scaffolding", BlockSoundGroup.SCAFFOLDING);
		INSTANCE.putBlockSoundGroup("sweet_berry_bush", BlockSoundGroup.SWEET_BERRY_BUSH);
		INSTANCE.putBlockSoundGroup("crop", BlockSoundGroup.CROP);
		INSTANCE.putBlockSoundGroup("stem", BlockSoundGroup.STEM);
		INSTANCE.putBlockSoundGroup("nether_wart", BlockSoundGroup.NETHER_WART);
		INSTANCE.putBlockSoundGroup("lantern", BlockSoundGroup.LANTERN);
	}
}
