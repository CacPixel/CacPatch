package net.cacpixel.cacpatch.render;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.render.Parts;
import jp.ngt.rtm.render.PartsRenderer;
import jp.ngt.rtm.render.TileEntityPartsRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class LibRenderRail extends TileEntityPartsRenderer<ModelSetRail> {

	public static void renderRailMapStaticForNormRails(PartsRenderer renderer, TileEntityLargeRailCore tileEntity, RailMap rm,
	                                                   int max, int startIndex, int endIndex, boolean enableCant,
	                                                   float[] translate, float[] rotate, float[] scale, Parts... pArray) {
		double[] origPos = rm.getRailPos(max, 0);
		double origHeight = rm.getRailHeight(max, 0);
		int[] startPos = tileEntity.getStartPoint();
		float[] revXZ = RailPosition.REVISION[tileEntity.getRailPositions()[0].direction];

		float moveX = (float) (origPos[1] - ((double) startPos[0] + 0.5D + (double) revXZ[0]));
		float moveZ = (float) (origPos[0] - ((double) startPos[2] + 0.5D + (double) revXZ[1]));

		for (int i = startIndex; i <= endIndex; ++i) {
			double[] p1 = rm.getRailPos(max, i);
			double h = rm.getRailHeight(max, i);
			float x0 = moveX + (float) (p1[1] - origPos[1]);
			float y0 = (float) (h - origHeight);
			float z0 = moveZ + (float) (p1[0] - origPos[0]);
			float yaw = rm.getRailYaw(max, i);
			float pitch = rm.getRailPitch(max, i);
			float cant = rm.getRailRoll(max, i);
			//float cantRaiseUp = (float) Math.abs(0.5F * Math.tan(cant * Math.PI / 180));

			GLHelper.setBrightness(getBrightness(tileEntity.getWorld(),
					NGTMath.floor(origPos[1] + x0), tileEntity.getPos().getY(), NGTMath.floor(origPos[0] + z0)));
			GL11.glPushMatrix();
			GL11.glTranslatef(x0, y0, z0);
			GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
			if (enableCant)
				GL11.glRotatef(cant, 0.0F, 0.0F, 1.0F);

			if (translate[0] != 0.0F || translate[1] != 0.0F || translate[2] != 0.0F) {
				GL11.glTranslatef(translate[0], translate[1], translate[2]);
			}
			if (rotate[0] != 0.0F || rotate[1] != 0.0F || rotate[2] != 0.0F) {
				GL11.glRotatef(rotate[0], 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(rotate[1], 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rotate[2], 0.0F, 0.0F, 1.0F);
			}
			if (scale[0] != 1.0F || scale[1] != 1.0F || scale[2] != 1.0F) {
				GL11.glScalef(scale[0], scale[1], scale[2]);
			}

			GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
			for (int j = 0; j < pArray.length; ++j) {
				pArray[j].render(renderer);
			}
			//pArray.render(renderer);

			GL11.glPopMatrix();
		}
	}

	public static int getBrightness(World world, int x, int y, int z) {
		int i = getWorldBrightness(world, x, y, z);
		if (i <= 0) {
			i = getWorldBrightness(world, x, y + 1, z);
		}
		return i;
	}

	private static int getWorldBrightness(World world, int x, int y, int z) {
		BlockPos blockpos = new BlockPos(x, y, z);
		return world.isBlockLoaded(blockpos) ? world.getCombinedLight(blockpos, 0) : 0;
	}

}
