package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleListSupplier;
import thelm.jaopca.compat.techreborn.TechRebornCompatModule;
import thelm.jaopca.compat.techreborn.TechRebornCrystalModule;
import thelm.jaopca.compat.techreborn.TechRebornDustModule;
import thelm.jaopca.compat.techreborn.TechRebornModule;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.modules.active.BlockModule;
import thelm.jaopca.modules.passive.DustModule;
import thelm.jaopca.modules.passive.MoltenModule;
import thelm.jaopca.modules.passive.NuggetModule;
import thelm.jaopca.modules.passive.SmallDustModule;
import thelm.jaopca.modules.passive.TinyDustModule;
import thelm.jaopca.modules.tags.VanillaModule;
import thelm.jaopca.utils.MiscHelperImpl;

public class DefaultModuleListSupplier implements ModuleListSupplier {

	@Override
	public List<Module> get() {
		List<Module> modules = new ArrayList<>();

		modules.add(new CustomModule());

		modules.add(new BlockModule());

		modules.add(new DustModule());
		modules.add(new MoltenModule());
		modules.add(new NuggetModule());
		modules.add(new SmallDustModule());
		modules.add(new TinyDustModule());

		modules.add(new VanillaModule());

		MiscHelperImpl.INSTANCE.runIf(()->FabricLoader.getInstance().isModLoaded("techreborn"), ()->()->{
			modules.add(new TechRebornModule());
			modules.add(new TechRebornCrystalModule());
			modules.add(new TechRebornDustModule());
			modules.add(new TechRebornCompatModule());
		});

		return modules;
	}
}
