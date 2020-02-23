package thelm.jaopca.items;

import thelm.jaopca.api.items.ItemInfo;
import thelm.jaopca.api.items.MaterialFormItem;

public class ItemInfoImpl implements ItemInfo {

	private final MaterialFormItem item;

	ItemInfoImpl(MaterialFormItem item) {
		this.item = item;
	}

	@Override
	public MaterialFormItem getMaterialFormItem() {
		return item;
	}
}
