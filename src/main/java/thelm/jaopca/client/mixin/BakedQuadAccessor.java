package thelm.jaopca.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;

@Mixin(BakedQuad.class)
public interface BakedQuadAccessor {

	@Accessor
	Sprite getSprite();
}
