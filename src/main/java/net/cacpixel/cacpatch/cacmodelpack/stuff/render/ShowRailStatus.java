package net.cacpixel.cacpatch.cacmodelpack.stuff.render;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailPosition;

import java.util.List;

public class ShowRailStatus {

	public static void scriptRenderRailStatic(TileEntityLargeRailCore tileEntity) {
		String languageCode = NGTUtilClient.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		RailMap[] rm = tileEntity.getAllRailMaps();
		if (rm == null) return;
		double[] max = new double[rm.length];
		for (int i = 0; i < rm.length; i++) {
			max[i] = (rm[i].getLength() * 2.0);
		}
		RailPosition[] rp = tileEntity.getRailPositions();
		double x = rp[0].posX - rp[0].blockX;
		double y = rp[0].posY - rp[0].blockY - 0.0625;
		double z = rp[0].posZ - rp[0].blockZ;

		List<ResourceStateRail> subRails = tileEntity.subRails;
		String modelName = tileEntity.getResourceState().getResourceSet().getConfig().getName();
		boolean isSwitchRail = tileEntity.getAllRailMaps().length > 1;

		if (languageCode.equals("zh_cn")) {
			NGTLog.showChatMessage("§m§f------------------------------");
			NGTLog.showChatMessage("§l§6轨道参数：");
			NGTLog.showChatMessage("§f主轨道：§7 " + modelName);
			NGTLog.showChatMessage("§f子轨道：§7 ");
			for (int i = 0; i < subRails.size(); i++) {
				NGTLog.showChatMessage("§e" + (i + 1) + ": §7" + subRails.get(i).getResourceSet().getConfig().getName());
			}

			NGTLog.showChatMessage("§f起点位置：§7 %.2f %.2f %.2f", rp[0].posX + 0.00001, rp[0].posY + 0.00001 - 0.0625, rp[0].posZ + 0.00001);
			if (!isSwitchRail) {
				NGTLog.showChatMessage("§f终点位置：§7 %.2f %.2f %.2f", rp[1].posX + 0.00001, rp[1].posY + 0.00001 - 0.0625, rp[1].posZ + 0.00001);
				NGTLog.showChatMessage("§f轨道长度：§7 %.3f", max[0] / 2 + 0.0001);
			} else {
				NGTLog.showChatMessage("§f副支端点位置：");
				for (int i = 1; i < rp.length; i++) {
					NGTLog.showChatMessage("§e" + i + ": §7%.2f %.2f %.2f", rp[i].posX + 0.00001, rp[i].posY + 0.00001 - 0.0625, rp[i].posZ + 0.00001);
				}
				NGTLog.showChatMessage("§f主支轨道长度：§7 %.3f ", max[0] / 2 + 0.0001);
				NGTLog.showChatMessage("§f副支轨道长度：");
				for (int i = 1; i < max.length; i++) {
					NGTLog.showChatMessage("§e%d:§7 %.3f", i, max[i] / 2 + 0.0001);
				}
			}
			NGTLog.showChatMessage("§m§f------------------------------");
			NGTLog.showChatMessage("§n§c*请再次右键点击铁轨以去除该子轨道(Tool_ShowRailStatus)。");
		}

	}

	public static void selfRemove(TileEntityLargeRailCore tileEntity) {
		List<ResourceStateRail> subRails = tileEntity.subRails;
		//tileEntity.addSubRail(subRails.get(subRails.size() - 1));
		//tileEntity.updateResourceState();
		tileEntity.subRails.remove(subRails.get(subRails.size() - 1));
		//RTMCore.NETWORK_WRAPPER.sendToAll(new PacketLargeRailCore(tileEntity, tileEntity.getPacketType()));
		//RTMCore.NETWORK_WRAPPER.sendToServer(new PacketLargeRailCore(tileEntity, tileEntity.getPacketType()));
		tileEntity.sendPacket();
	}

}
