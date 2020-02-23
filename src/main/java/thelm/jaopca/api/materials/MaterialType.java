package thelm.jaopca.api.materials;

import java.util.Arrays;
import java.util.Locale;

import thelm.jaopca.api.JAOPCAApi;

public enum MaterialType {

	INGOT("ingot", "ingots"),
	GEM("gem", "gems"),
	CRYSTAL("crystal", "crystals"),
	DUST("dust", "dusts"),
	INGOT_PLAIN("ingot", "ingots"),
	GEM_PLAIN("gem", "gems"),
	CRYSTAL_PLAIN("crystal", "crystals"),
	DUST_PLAIN("dust", "dusts");

	public static final MaterialType[] INGOTS = {INGOT, INGOT_PLAIN};
	public static final MaterialType[] GEMS = {GEM, GEM_PLAIN};
	public static final MaterialType[] CRYSTALS = {CRYSTAL, CRYSTAL_PLAIN};
	public static final MaterialType[] DUSTS = {DUST, DUST_PLAIN};
	public static final MaterialType[] NON_DUSTS = {INGOT, GEM, CRYSTAL, INGOT_PLAIN, GEM_PLAIN, CRYSTAL_PLAIN};
	public static final MaterialType[] ORE = {INGOT, GEM, CRYSTAL, DUST};

	private final String formName;
	private final String formNamePlural;

	MaterialType(String formName, String formNamePlural) {
		this.formName = formName;
		this.formNamePlural = formNamePlural;
	}

	public String getName() {
		return name().toLowerCase(Locale.US);
	}

	public String getFormName() {
		return JAOPCAApi.instance().tagFormat().isPlural() ? formNamePlural : formName;
	}

	public static MaterialType fromName(String name) {
		return Arrays.stream(values()).filter(t->t.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}
}
