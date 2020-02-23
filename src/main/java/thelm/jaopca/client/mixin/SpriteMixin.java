package thelm.jaopca.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import thelm.jaopca.client.extensions.SpriteExtension;

@Mixin(Sprite.class)
public abstract class SpriteMixin implements SpriteExtension {

	@Shadow
	protected NativeImage[] images;
	@Shadow
	protected int[] frameXs;
	@Shadow
	protected int[] frameYs;

	@Shadow
	public abstract int getWidth();
	@Shadow
	public abstract int getHeight();

	@Override
	public int jaopca_getPixelRGBA(int frameIndex, int x, int y) {
		return images[0].getPixelRgba(x+frameXs[frameIndex]*getWidth(), y+frameYs[frameIndex]*getHeight());
	}
}
