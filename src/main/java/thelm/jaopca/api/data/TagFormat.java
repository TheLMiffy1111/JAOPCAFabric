package thelm.jaopca.api.data;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.Identifier;
import thelm.jaopca.api.helpers.MiscHelper;
import thelm.jaopca.utils.MiscHelperImpl;

public enum TagFormat {

	FORGE("c:ingots/(.+)", "c:gems/(.+)", "c:crystals/(.+)", "c:dusts/(.+)", "c:ores/%s"),
	COTTON("c:(.+)_ingot", "c:(.+)_gem", "c:(.+)_crystal", "c:(.+)_dust", "c:%s_ore"),
	FORGE_SINGULAR("c:ingot/(.+)", "c:gem/(.+)", "c:crystal/(.+)", "c:dust/(.+)", "c:ore/%s"),
	;

	private final String ingotPattern;
	private final String gemPattern;
	private final String crystalPattern;
	private final String dustPattern;
	private final String oreFormat;

	TagFormat(String ingotPattern, String gemPattern, String crystalPattern, String dustPattern, String oreFormat) {
		this.ingotPattern = ingotPattern;
		this.gemPattern = gemPattern;
		this.crystalPattern = crystalPattern;
		this.dustPattern = dustPattern;
		this.oreFormat = oreFormat;
	}

	public String getIngotPattern() {
		return ingotPattern;
	}

	public String getGemPattern() {
		return gemPattern;
	}

	public String getCrystalPattern() {
		return crystalPattern;
	}

	public String getDustPattern() {
		return dustPattern;
	}

	public String getOreFormat() {
		return oreFormat;
	}

	public boolean isPlural() {
		return this == FORGE;
	}

	public Identifier getTagIdentifier(String form, String material) {
		MiscHelper helper = MiscHelperImpl.INSTANCE;
		switch(this) {
		case FORGE_SINGULAR:
		case FORGE:
			return helper.createIdentifier(form+(StringUtils.isEmpty(material) ? "" : '/'+material));
		case COTTON:
		default:
			String[] split = StringUtils.split(form, ':');
			if(split.length > 1) {
				return helper.createIdentifier((StringUtils.isEmpty(material) ? "" : material+'_')+split[1], split[0]);
			}
			else {
				return helper.createIdentifier((StringUtils.isEmpty(material) ? "" : material+'_')+form);
			}
		}
	}
}
