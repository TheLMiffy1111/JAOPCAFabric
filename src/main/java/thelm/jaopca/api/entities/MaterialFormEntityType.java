package thelm.jaopca.api.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import thelm.jaopca.api.materialforms.MaterialForm;

public interface MaterialFormEntityType<E extends Entity> extends MaterialForm {

	default EntityType<E> asEntityType() {
		return (EntityType<E>)this;
	}
}
