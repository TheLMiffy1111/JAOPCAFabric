package thelm.jaopca.modules.active;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.Identifier;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.BlockInfo;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.blocks.BlockFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class BlockModule implements Module {

	private final Form blockForm = ApiImpl.INSTANCE.newForm(this, "block", BlockFormTypeImpl.INSTANCE);

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public List<FormRequest> getFormRequests() {
		TagFormat tagFormat = ApiImpl.INSTANCE.tagFormat();
		if(tagFormat.isPlural()) {
			blockForm.setSecondaryName("blocks");
		}
		return Collections.singletonList(blockForm.toRequest());
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MiscHelper miscHelper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();
		String block = plural ? "blocks" : "block";
		for(IMaterial material : blockForm.getMaterials()) {
			Identifier materialLocation = miscHelper.getTagIdentifier(material.getType().getFormName(), material.getName());
			BlockInfo storageBlockInfo = BlockFormTypeImpl.INSTANCE.getMaterialFormInfo(blockForm, material);
			Identifier storageBlockLocation = miscHelper.getTagIdentifier(block, material.getName());
			api.registerShapedRecipe(
					new Identifier("jaopca", "block.to_block."+material.getName()),
					storageBlockInfo, 1, new Object[] {
							"MMM",
							"MMM",
							"MMM",
							'M', materialLocation,
					});
			api.registerShapelessRecipe(
					new Identifier("jaopca", "block.to_material."+material.getName()),
					materialLocation, 9, new Object[] {
							storageBlockLocation,
					});
		}
	}
}
