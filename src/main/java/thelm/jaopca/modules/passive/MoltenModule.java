package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.fluids.FluidFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;

public class MoltenModule implements Module {

	private final Form moltenForm = ApiImpl.INSTANCE.newForm(this, "molten", FluidFormTypeImpl.INSTANCE).
			setMaterialTypes(MaterialType.INGOTS).setSettings(FluidFormTypeImpl.INSTANCE.getNewSettings().
					setTickRateFunction(material->50).setDensityFunction(material->2000).
					setTemperatureFunction(material->1000).setLightValueFunction(material->10).
					setMaterialFunction(material->Material.LAVA));

	@Override
	public String getName() {
		return "molten";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<FormRequest> getFormRequests() {
		return Collections.singletonList(moltenForm.toRequest());
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		for(IMaterial material : moltenForm.getMaterials()) {
			ApiImpl.INSTANCE.registerFluidTag(new Identifier("lava"), FluidFormTypeImpl.INSTANCE.getMaterialFormInfo(moltenForm, material).getFluid());
		}
	}
}
