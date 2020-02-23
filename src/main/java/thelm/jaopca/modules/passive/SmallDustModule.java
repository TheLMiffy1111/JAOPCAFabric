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

public class SmallDustModule implements Module {

	private final Form smallDustForm = ApiImpl.INSTANCE.newForm(this, "small_dust", ItemFormTypeImpl.INSTANCE);

	@Override
	public String getName() {
		return "small_dust";
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
			smallDustForm.setSecondaryName("small_dusts");
		}
		return Collections.singletonList(smallDustForm.toRequest());
	}

	@Override
	public void onCommonSetup(ModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MiscHelper miscHelper = MiscHelperImpl.INSTANCE;
		TagFormat tagFormat = api.tagFormat();
		boolean plural = tagFormat.isPlural();
		String dust = plural ? "dusts" : "dust";
		String smallDust = plural ? "small_dusts" : "small_dust";
		for(IMaterial material : smallDustForm.getMaterials()) {
			Identifier dustLocation = miscHelper.getTagIdentifier(dust, material.getName());
			Identifier smallDustLocation = miscHelper.getTagIdentifier(smallDust, material.getName());
			ApiImpl.INSTANCE.registerShapelessRecipe(
					new Identifier("jaopca", "small_dust.to_dust."+material.getName()),
					dustLocation, 1, new Object[] {
							smallDustLocation, smallDustLocation,
							smallDustLocation, smallDustLocation,
					});
		}
	}
}
