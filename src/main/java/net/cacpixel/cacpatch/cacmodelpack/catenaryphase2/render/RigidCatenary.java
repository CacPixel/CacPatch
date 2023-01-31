package net.cacpixel.cacpatch.cacmodelpack.catenaryphase2.render;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.render.Parts;
import jp.ngt.rtm.render.PartsRenderer;
import jp.ngt.rtm.render.RailPartsRenderer;
import jp.ngt.rtm.render.TileEntityPartsRenderer;
import net.cacpixel.cacpatch.render.LibRenderRail;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RigidCatenary extends TileEntityPartsRenderer<ModelSetRail> {
	/*
	public static void scriptInit(PartsRenderer renderer, ModelSetRail par1, ModelObject par2) {
		rail0 = renderer.registerParts(new Parts("rail0"));
		railSide = renderer.registerParts(new Parts("railSide"));
		ins0 = renderer.registerParts(new Parts("ins0"));
		bracket = renderer.registerParts(new Parts("bracket", "bracketBar", "bracketTop"));
		bracket0 = renderer.registerParts(new Parts("bracket0", "bracketBar", "bracketTop"));

		endStart = renderer.registerParts(new Parts("endStart"));
		endEnd = renderer.registerParts(new Parts("endEnd"));

		electricCon1 = renderer.registerParts(new Parts("electricCon1"));
		electricCon2 = renderer.registerParts(new Parts("electricCon2"));

		gndWireCon = renderer.registerParts(new Parts("gndWireCon"));

		railgap = renderer.registerParts(new Parts("fishplate", "railgap", "railgap-cw"));
	}
	 */

    public static void scriptRenderRailStatic(RailPartsRenderer renderer, TileEntityLargeRailCore tileEntity, double posX, double posY, double posZ, Parts... pArray) {

        Parts rail0 = pArray[0];
        //Parts railSide = pArray[1];
        Parts ins0 = pArray[2];
        Parts bracket = pArray[3];
        Parts bracket0 = pArray[4];
        Parts endStart = pArray[5];
        Parts endEnd = pArray[6];
        Parts electricCon1 = pArray[7];
        Parts electricCon2 = pArray[8];
        Parts gndWireCon = pArray[9];
        Parts railgap = pArray[10];
        Parts ins1 = pArray[11];
        Parts insb1 = pArray[12];
        Parts midPointAnchor = pArray[13];

        RailMap rm = tileEntity.getRailMap(null);
        int max = (int) Math.floor(rm.getLength() * 2.0);
        RailPosition[] rp = tileEntity.getRailPositions();
        double x = rp[0].posX - rp[0].blockX;
        double y = rp[0].posY - rp[0].blockY - 0.0625;
        double z = rp[0].posZ - rp[0].blockZ;
        List<ResourceStateRail> subRails = tileEntity.subRails;

		float cwHeight = getCWHeight(subRails);  //height of contact wire
		int ot = getOt(subRails);             //overlap type, 0=no overlap, 1=overlap, 2=insulated overlap
		float od = getOd(ot);        //overlap distance
		float op = getOp(subRails);        //overlap position
		float oStart = getOStart(subRails);       //catenary offset start
		float oEnd = getOEnd(subRails);         //catenary offset end
		boolean hasMidPointAnchor = getMidPointAnchor(subRails);

        int bd = 15;            //bracket distance
        boolean hasCant = true;
        float cant;
        float bracketMoveX = 0, bracketMoveY = cwHeight;

        float zigzagMove = oStart - oEnd;
        float[] oArray = new float[max + 1];

        for (int j = 0; j <= max; j++) {
            oArray[j] = getOffset(oStart, calcOD(oEnd, oStart, oEnd, ot, od), max, j);
        }

        float[] oArray2 = new float[max + 1];
        if (ot != 0) {
            for (int j = 0; j <= max; j++) {
                oArray2[j] = calcODReversed(oArray[j], oStart, oEnd, ot, od);
            }
        }

        float lengthOfARail = (((max + 1) / 2.0F) / max);
        float angleHorizonal = (float) -NGTMath.toDegrees(Math.atan(calcODReversed(zigzagMove, oStart, oEnd, ot, od) / (lengthOfARail * max)));
        int opPos = (int) Math.floor(op * max);

        int startIndex;
        int endIndex;
        int endIndex2;
        int startIndex2;
        if (ot == 0) {
            startIndex = 0;
            endIndex = max;
            startIndex2 = 0;
            endIndex2 = -1; //skip render of the 2nd branch
        } else {
            startIndex = 0;
            endIndex = opPos + 3;
            startIndex2 = opPos - 3;
            endIndex2 = max;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (posX + x), (float) (posY + y + 0.25), (float) (posZ + z));
        renderer.bindTexture(renderer.getModelObject().textures[0].material.texture);

        /*
         * START RENDERING
         */

        if (ot == 0) {
            for (int i = startIndex; i <= endIndex - 3; i++) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray[i], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, rail0);
            }
            for (int i = endIndex - 1; i <= endIndex; i++) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray[i], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, rail0);
            }
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, endIndex - 2, endIndex - 2, hasCant, cwHeight,
                    oArray[endIndex - 2], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, railgap);
        } else {
            for (int i = startIndex; i <= endIndex + 1; i++) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray[i], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, rail0);
            }
            for (int i = startIndex2 - 1; i <= endIndex2; i++) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray2[i], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, rail0);
            }
        }

        //ins0
        if (ot != 0) {
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, max, max, hasCant, cwHeight,
                    oArray2[max], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, 0, 0, hasCant, cwHeight,
                    oArray[0], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
            for (int i = endIndex - bd; i >= startIndex + 1; i -= bd) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
            }
            for (int i = startIndex2 + bd; i <= endIndex2 - 1; i += bd) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray2[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
            }
            //4 insulators in overlap
            for (int i = startIndex2; i <= endIndex; i += 4) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray2[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins1);
            }
            for (int i = startIndex2 + 2; i <= endIndex; i += 4) {
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins1);
            }
        } else {
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, max, max, hasCant, cwHeight,
                    oArray[max], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
            for (int i = startIndex; i <= endIndex - 1; i += bd) {
                if (hasMidPointAnchor && opPos - bd / 2 <= i && i <= opPos + bd / 2)
                    continue;
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                        oArray[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
            }
        }

        //bracket
        if ((cant = rm.getRailRoll(max, max)) != 0.0F) {
            bracketMoveX = -cwHeight * NGTMath.sin(cant);
            bracketMoveY = cwHeight * NGTMath.cos(cant);
        }
        renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, max, max, false, cwHeight,
                bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket0);
        if ((cant = rm.getRailRoll(max, 0)) != 0.0F) {
            bracketMoveX = -cwHeight * NGTMath.sin(cant);
            bracketMoveY = cwHeight * NGTMath.cos(cant);
        }
        renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, 0, 0, false, cwHeight,
                bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket0);
        if (ot != 0) {

            //normal
            for (int i = endIndex - bd; i >= startIndex + 1; i -= bd) {
                if ((cant = rm.getRailRoll(max, i)) != 0.0F) {
                    bracketMoveX = -cwHeight * NGTMath.sin(cant);
                    bracketMoveY = cwHeight * NGTMath.cos(cant);
                }
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, false, cwHeight,
                        bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket);
            }
            for (int i = startIndex2 + bd; i <= endIndex2 - 1; i += bd) {
                if ((cant = rm.getRailRoll(max, i)) != 0.0F) {
                    bracketMoveX = -cwHeight * NGTMath.sin(cant);
                    bracketMoveY = cwHeight * NGTMath.cos(cant);
                }
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, false, cwHeight,
                        bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket);
            }

            //4 brackets for overlap
            for (int i = startIndex2; i <= endIndex; i += 2) {
                if ((cant = rm.getRailRoll(max, i)) != 0.0F) {
                    bracketMoveX = -cwHeight * NGTMath.sin(cant);
                    bracketMoveY = cwHeight * NGTMath.cos(cant);
                }
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, false, cwHeight,
                        (oArray2[i] + oArray[i]) / 2.0F + bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, insb1);
            }
            for (int i = startIndex2; i <= endIndex; i += 2) {
                if ((cant = rm.getRailRoll(max, i)) != 0.0F) {
                    bracketMoveX = -cwHeight * NGTMath.sin(cant);
                    bracketMoveY = cwHeight * NGTMath.cos(cant);
                }
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, false, cwHeight,
                        bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket);
            }
        } else {
            for (int i = startIndex + bd; i <= endIndex - 1; i += bd) {
                if (hasMidPointAnchor && opPos - bd / 2 <= i && i <= opPos + bd / 2)
                    continue;
                if ((cant = rm.getRailRoll(max, i)) != 0.0F) {
                    bracketMoveX = -cwHeight * NGTMath.sin(cant);
                    bracketMoveY = cwHeight * NGTMath.cos(cant);
                }
                renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, false, cwHeight,
                        bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket);
            }
        }


        /*
        renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, 0, 0, hasCant, hasCant,
            [oArray[0], cwHeight, 0.0F], [0.0F, angleHorizonal, 0.0F], [1.0F, 1.0F, 1.0F], endStart);
        renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, max, max, hasCant, hasCant,
            [oArray2[max], cwHeight, 0.0F], [0.0F, angleHorizonal, 0.0F], [1.0F, 1.0F, 1.0F], endEnd);
        */
        //endStart endEnd
        if (ot != 0) {
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, endIndex + 1, endIndex + 1, hasCant, cwHeight,
                    oArray[endIndex + 1], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, endEnd);
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, startIndex2 - 1, startIndex2 - 1, hasCant, cwHeight,
                    oArray2[startIndex2 - 1], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, endStart);
        }

        //gndWireCon
        renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, 0, 0, hasCant, cwHeight,
                oArray[0], cwHeight, 0.0F, 0.0F, angleHorizonal, 0.0F, 1.0F, 1.0F, 1.0F, gndWireCon);

        //electricCon
        if (ot == 1) {
            for (int i = startIndex2 - 1; i <= endIndex + 2; i += 2) {
                if (oArray[i] <= oArray2[i]) {
                    renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                            oArray[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, electricCon1);
                    renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                            oArray2[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, electricCon2);
                } else {
                    renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                            oArray2[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, electricCon1);
                    renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, i, i, hasCant, cwHeight,
                            oArray[i], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, electricCon2);
                }
            }
        }

        //mid-point anchor
        if (ot == 0 && hasMidPointAnchor) {
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, opPos, opPos, hasCant, cwHeight,
                    oArray[opPos], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, midPointAnchor);
            if ((cant = rm.getRailRoll(max, opPos)) != 0.0F) {
                bracketMoveX = -cwHeight * NGTMath.sin(cant);
                bracketMoveY = cwHeight * NGTMath.cos(cant);
            }
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, opPos, opPos, false, cwHeight,
                    bracketMoveX, bracketMoveY, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, bracket);
            renderRailMapStaticForNormRails(renderer, tileEntity, rm, max, opPos, opPos, hasCant, cwHeight,
                    oArray[opPos], cwHeight, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, ins0);
        }

        GL11.glPopMatrix();
    }


    public static void renderRailMapStaticForNormRails(PartsRenderer renderer, TileEntityLargeRailCore tileEntity, RailMap rm, int max,
                                                       int startIndex, int endIndex, boolean enableCant, float cwHeight,
                                                       float translateX, float translateY, float translateZ,
                                                       float rotateX, float rotateY, float rotateZ,
                                                       float scaleX, float scaleY, float scaleZ,
                                                       Parts pArray) {
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

            GLHelper.setBrightness(LibRenderRail.getBrightness(tileEntity.getWorld(),
                    NGTMath.floor(origPos[1] + x0), tileEntity.getPos().getY(), NGTMath.floor(origPos[0] + z0)));
            GL11.glPushMatrix();
            GL11.glTranslatef(x0, y0, z0);
            GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
            if (enableCant)
                GL11.glRotatef(cant, 0.0F, 0.0F, 1.0F);

            if (translateX != 0.0F || translateY != 0.0F || translateZ != 0.0F) {
                GL11.glTranslatef(translateX, translateY, translateZ);
            }
            if (rotateX != 0.0F || rotateY != 0.0F || rotateZ != 0.0F) {
                GL11.glRotatef(rotateX, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(rotateY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(rotateZ, 0.0F, 0.0F, 1.0F);
            }
            if (scaleX != 1.0F || scaleY != 1.0F || scaleZ != 1.0F) {
                GL11.glScalef(scaleX, scaleY, scaleZ);
            }

            GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
			/*for (int j = 0; j < pArray.length; ++j) {
				pArray[j].render(renderer);
			}*/
            pArray.render(renderer);
            GL11.glPopMatrix();
        }
    }

    private static float getOffset(float oStart, float oEnd, int max, int pos) {
        if (oStart != oEnd) {
            if (max == pos)
                return oEnd;
            else
                return oStart - ((oStart - oEnd) / max * pos);
        } else
            return oStart;
    }

    private static float calcOD(float par1, float oStart, float oEnd, int ot, float od) {
        if (ot == 0)
            return par1;
        if (oStart > oEnd)
            return par1 - od;
        else
            return par1 + od;
    }

    private static float calcODReversed(float par1, float oStart, float oEnd, int ot, float od) {
        if (ot == 0)
            return par1;
        if (oStart > oEnd)
            return par1 + od;
        else
            return par1 - od;
    }

    private static float getCWHeight(List<ResourceStateRail> subRails) {
        float cwHeight = 4.1F;
        int k = subRails.size() - 1;
        String modelName = "";
        for (; k >= 0; k--) {
            if (subRails.get(k).getResourceSet().getConfig().getName().contains("DC1500V_RigidCatenary_style1")) {
                modelName = subRails.get(k).getResourceSet().getConfig().getName();
                break;
            }
        }
        if (modelName.contains("w51"))
            cwHeight = 5.1F;
        else
            cwHeight = 5.685F;
        if (modelName.contains("-1m"))
            cwHeight -= 1.0F;
        return cwHeight;
    }

    private static int getOt(List<ResourceStateRail> subRails) {
        int ot = 0;
        int k = subRails.size() - 1;
        String modelName = "";
        for (; k >= 0; k--) {
            if (subRails.get(k).getResourceSet().getConfig().getName().contains("DC1500V_RigidCatenary_style1")) {
                modelName = subRails.get(k).getResourceSet().getConfig().getName();
                break;
            }
        }
        if (modelName.contains("insulated-overlap")) {
            ot = 2;
        } else if (modelName.contains("overlap"))
            ot = 1;
        return ot;
    }

    private static float getOp(List<ResourceStateRail> subRails) {
        float op = 0.5F;
        int k = subRails.size() - 1;
        String modelName = "";
        for (; k >= 0; k--) {
            if (subRails.get(k).getResourceSet().getConfig().getName().contains("DC1500V_RigidCatenary_settings_overlap-position-")) {
                modelName = subRails.get(k).getResourceSet().getConfig().getName();
                break;
            }
        }
        if (modelName.contains("0.125"))
            op = 0.125F;
        else if (modelName.contains("0.250"))
            op = 0.250F;
        else if (modelName.contains("0.375"))
            op = 0.375F;
        else if (modelName.contains("0.500"))
            op = 0.500F;
        else if (modelName.contains("0.625"))
            op = 0.625F;
        else if (modelName.contains("0.750"))
            op = 0.750F;
        else if (modelName.contains("0.875"))
            op = 0.875F;
        return op;
    }

    private static float getOd(int ot) {
        float od = 0.2F;
        if (ot == 2)
            od = 0.3F;
        return od;
    }

    private static float getOStart(List<ResourceStateRail> subRails) {
        float oStart = 0;
        int k = subRails.size() - 1;
        String modelName = "";
        for (; k >= 0; k--) {
            if (subRails.get(k).getResourceSet().getConfig().getName().contains("DC1500V_RigidCatenary_settings_start-pos-")) {
                modelName = subRails.get(k).getResourceSet().getConfig().getName();
                break;
            }
        }
        if (modelName.contains("L20"))
            oStart = -0.20F;
        else if (modelName.contains("L10"))
            oStart = -0.10F;
        else if (modelName.contains("Center"))
            oStart = 0;
        else if (modelName.contains("R10"))
            oStart = 0.10F;
        else if (modelName.contains("R20"))
            oStart = 0.20F;
        else if (modelName.contains("L6.7"))
            oStart = -6.6667F / 100;
        else if (modelName.contains("R6.7"))
            oStart = 6.6667F / 100;
        return oStart;
    }

    private static float getOEnd(List<ResourceStateRail> subRails) {
        float oEnd = 0;
        int k = subRails.size() - 1;
        String modelName = "";
        for (; k >= 0; k--) {
            if (subRails.get(k).getResourceSet().getConfig().getName().contains("DC1500V_RigidCatenary_settings_end-pos-")) {
                modelName = subRails.get(k).getResourceSet().getConfig().getName();
                break;
            }
        }
        if (modelName.contains("L20"))
            oEnd = 0.20F;
        else if (modelName.contains("L10"))
            oEnd = 0.10F;
        else if (modelName.contains("Center"))
            oEnd = 0;
        else if (modelName.contains("R10"))
            oEnd = -0.10F;
        else if (modelName.contains("R20"))
            oEnd = -0.20F;
        else if (modelName.contains("L6.7"))
            oEnd = 6.6667F / 100;
        else if (modelName.contains("R6.7"))
            oEnd = -6.6667F / 100;
        return oEnd;
    }

    private static boolean getMidPointAnchor(List<ResourceStateRail> subRails) {
        int k = subRails.size() - 1;
        for (; k >= 0; k--) {
            if (subRails.get(k).getResourceSet().getConfig().getName().contains("DC1500V_MidpointAnchor")) {
                return true;
            }
        }
        return false;
    }
}
