package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.Identifier;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.items.ItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class NuggetModule implements Module {

	private final Form nuggetForm = ApiImpl.INSTANCE.newForm(this, "nugget", ItemFormTypeImpl.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS);

	@Override
	public String getName() {
		return "nugget";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<FormRequest> getFormRequests() {
		TagFormat tagFormat = ApiImpl.INSTANCE.tagFormat();
		if(tagFormat.isPlural()) {
			nuggetForm.setSecondaryName("nuggets");
		}
		return Collections.singletonList(nuggetForm.toRequest());
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MiscHelper miscHelper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();
		String nugget = plural ? "nuggets" : "bugget";
		for(IMaterial material : nuggetForm.getMaterials()) {
			Identifier materialLocation = miscHelper.getTagIdentifier(material.getType().getFormName(), material.getName());
			ItemInfo nuggetInfo = ItemFormTypeImpl.INSTANCE.getMaterialFormInfo(nuggetForm, material);
			Identifier nuggetLocation = miscHelper.getTagIdentifier(nugget, material.getName());
			api.registerShapelessRecipe(
					new Identifier("jaopca", "nugget.to_material."+material.getName()),
					materialLocation, 1, new Object[] {
							nuggetLocation, nuggetLocation, nuggetLocation,
							nuggetLocation, nuggetLocation, nuggetLocation,
							nuggetLocation, nuggetLocation, nuggetLocation,
					});
			api.registerShapelessRecipe(
					new Identifier("jaopca", "nugget.to_nugget."+material.getName()),
					nuggetInfo, 9, new Object[] {
							materialLocation,
					});
		}
	}
}
