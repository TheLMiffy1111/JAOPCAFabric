package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.Identifier;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.TagFormat;
import thelm.jaopca.api.forms.Form;
import thelm.jaopca.api.forms.FormRequest;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.Module;
import thelm.jaopca.api.modules.ModuleData;
import thelm.jaopca.items.ItemFormTypeImpl;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelperImpl;

public class TinyDustModule implements Module {

	private final Form tinyDustForm = ApiImpl.INSTANCE.newForm(this, "tiny_dust", ItemFormTypeImpl.INSTANCE);

	@Override
	public String getName() {
		return "tiny_dust";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		return builder.build();
	}

	@Override
	public List<FormRequest> getFormRequests() {
		TagFormat tagFormat = ApiImpl.INSTANCE.tagFormat();
		if(tagFormat.isPlural()) {
			tinyDustForm.setSecondaryName("tiny_dusts");
		}
		return Collections.singletonList(tinyDustForm.toRequest());
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MiscHelper miscHelper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();
		String dust = plural ? "dusts" : "dust";
		String tinyDust = plural ? "tiny_dusts" : "tiny_dust";
		for(IMaterial material : tinyDustForm.getMaterials()) {
			Identifier dustLocation = miscHelper.getTagIdentifier(dust, material.getName());
			Identifier tinyDustLocation = miscHelper.getTagIdentifier(tinyDust, material.getName());
			ApiImpl.INSTANCE.registerShapelessRecipe(
					new Identifier("jaopca", "tiny_dust.to_dust."+material.getName()),
					dustLocation, 1, new Object[] {
							tinyDustLocation, tinyDustLocation, tinyDustLocation,
							tinyDustLocation, tinyDustLocation, tinyDustLocation,
							tinyDustLocation, tinyDustLocation, tinyDustLocation,
					});
		}
	}
}
