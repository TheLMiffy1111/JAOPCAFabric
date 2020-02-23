package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.Identifier;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.items.ItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class DustModule implements Module {

	private final Form dustForm = ApiImpl.INSTANCE.newForm(this, "dust", ItemFormTypeImpl.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS);

	@Override
	public String getName() {
		return "dust";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<FormRequest> getFormRequests() {
		TagFormat tagFormat = ApiImpl.INSTANCE.tagFormat();
		if(tagFormat.isPlural()) {
			dustForm.setSecondaryName("dusts");
		}
		return Collections.singletonList(dustForm.toRequest());
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		for(IMaterial material : dustForm.getMaterials()) {
			if(ArrayUtils.contains(MaterialType.INGOTS, material.getType())) {
				ItemInfo dustInfo = ItemFormTypeImpl.INSTANCE.getMaterialFormInfo(dustForm, material);
				Identifier materialLocation = MiscHelperImpl.INSTANCE.getTagIdentifier(material.getType().getFormName(), material.getName());
				api.registerSmeltingRecipe(
						new Identifier("jaopca", "dust.to_material."+material.getName()),
						dustInfo, materialLocation, 1, 0.7F, 200);
				api.registerBlastingRecipe(
						new Identifier("jaopca", "dust.to_material_blasting."+material.getName()),
						dustInfo, materialLocation, 1, 0.7F, 100);
			}
		}
	}
}
