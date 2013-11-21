// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Info.java

package kifujl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

// Referenced classes of package kifujl:
//            Drabod, Kifup, KifuData, Board

public class Info implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int info_pitch_y = 15;
	private final Drabod bd = new Drabod();
	private final int OX;
	private final int OX1;
	private final int OY;
	private final int OY1;
	private final int SIZE_Y;

	public Info() {
		OX = bd.BD_ORG_X + 9 * bd.SQ_SIZE_X + bd.EDGE_X + bd.RIGHT_GAP;
		OY = bd.BD_ORG_Y - bd.EDGE_Y;
		SIZE_Y = bd.MOTI_SIZE_Y + bd.SQ_SIZE_Y - bd.HAKO_SMALL_Y;
		OX1 = OX + 3;
		OY1 = OY + 3 + 11;
	}

	void disp_kifu_date(final Graphics g, final KifuData kifu) {
		int y = 2;
		String date = "";
		if (kifu.date != null) {
			date = kifu.date;
			int index = date.indexOf(" ");
			if (index > 0) {
				date = date.substring(0, index);
			}
			index = date.indexOf("(");
			if (index > 0) {
				date = date.substring(0, index);
			}
			final String s = date;
			g.drawString(s, OX1, OY1 + y * info_pitch_y);
			y++;
		}
		if (kifu.kisen != null) {
			final String s = kifu.kisen;
			g.drawString(s, OX1, OY1 + y * info_pitch_y);
			y++;
		}
		if (kifu.site != null) {
			final String s = kifu.site;
			g.drawString(s, OX1, OY1 + y * info_pitch_y);
			if (y >= 4) {
				return;
			}
			y++;
		}
		if (kifu.motijikan != null) {
			final String s = "\u6301\u3061\u6642\u9593\uFF1A" + kifu.motijikan;
			g.drawString(s, OX1, OY1 + y * info_pitch_y);
		}
	}

	void disp_kifu_henka(final Graphics g, final KifuData kifu) {
		final Kifup kp = kifu.get_kifup_n(kifu.move_no);
		String s;
		if (kifu.is_henka_kp(kp) != 0) {
			g.setColor(Color.blue);
			s = "\u5909\u5316\u624B\u9806";
		} else {
			g.setColor(Color.black);
			s = "\u672C\u624B\u9806\u3000";
		}
		g.drawString(s, OX1, OY1 + 9 * info_pitch_y);
		g.setColor(Color.black);
	}

	void disp_kifu_size(final Graphics g, final KifuData kifu) {
		final int n = kifu.get_sasite_n();
		final int tn = kifu.get_total_sasite_n();
		String s;
		if (n >= tn) {
			s = String.valueOf(new StringBuffer("\u68CB\u8B5C\uFF1A").append(tn).append("\u624B"));
		} else {
			s = String.valueOf(new StringBuffer("\u68CB\u8B5C ").append(n).append("/").append(tn).append("\u624B"));
		}
		g.drawString(s, OX1, OY1 + 8 * info_pitch_y);
	}

	void disp_tume_info(final Graphics g, final KifuData kifu) {
		int y = 0;
		String s = "\u4F5C\u8005\uFF1A" + kifu.sakusya;
		g.drawString(s, OX1, OY1);
		y = 1;
		if (kifu.sakuhinmei != null) {
			g.drawString(kifu.sakuhinmei, OX1, OY1 + info_pitch_y);
			y++;
		}
		if (kifu.syutten != null) {
			s = "\u51FA\u5178\uFF1A" + kifu.syutten;
			g.drawString(s, OX1, OY1 + info_pitch_y * y);
			y++;
		}
		if (kifu.happyou_nengetu == null) {
			return;
		}
		s = "\u767A\u8868\uFF1A" + kifu.happyou_nengetu;
		g.drawString(s, OX1, OY1 + info_pitch_y * y);
		return;
	}

	void DispAllInfo(final Graphics g, final KifuData kifu, final Board rbd, final Drabod pbd, final int no_henka_flg) {
		final Font f = new Font("Dialog", 0, 11);
		g.setFont(f);
		g.setClip(0, 0, OX + pbd.MOTI_SIZE_X + 2, pbd.BD_ORG_Y + 9 * pbd.SQ_SIZE_Y + pbd.EDGE_Y + 3);
		DispInfoBox(g, pbd);
		DispTeai(g, rbd, kifu);
		disp_kifu_size(g, kifu);
		if (no_henka_flg == 0) {
			disp_kifu_henka(g, kifu);
		} else if (no_henka_flg == 2 && kifu.is_henka_flg > 0) {
			disp_kifu_henka(g, kifu);
		}
		DispLastMove(g, kifu);
		if (kifu.sakusya != null) {
			disp_tume_info(g, kifu);
		} else {
			DispPlayers(g, kifu);
			disp_kifu_date(g, kifu);
		}
	}

	void DispInfoBox(final Graphics g, final Drabod lbd) {
		int left = OX;
		final int top = OY;
		final int right = OX + lbd.MOTI_SIZE_X;
		final int bottom = OY + SIZE_Y;
		g.setColor(Color.white);
		g.fillRect(left, top, right - left, bottom - top);
		lbd.shadow_box(g, left, top, right, bottom);
		left = lbd.get_moti_x_rl();
		g.drawString("V2.33", left + 50, 25);
	}

	void DispLastMove(final Graphics g, final KifuData kifu) {
		DispTesuu(g, kifu);
		if (kifu.move_no > 0) {
			final String moves = kifu.kifu_mov_strm(2, 1, kifu.move_no, 5);
			final String s = " " + moves + " \u307E\u3067";
			g.drawString(s, OX1, OY1 + 7 * info_pitch_y);
		}
	}

	void DispPlayers(final Graphics g, final KifuData kifu) {
		String s = "\u25B2" + kifu.sente_name;
		g.drawString(s, OX1, OY1);
		s = "\u25B3" + kifu.gote_name;
		g.drawString(s, OX1, OY1 + info_pitch_y);
	}

	void DispTeai(final Graphics g, final Board rbd, final KifuData kifu) {
		if (kifu.sakusya == null) {
			final String teai_name[] = { "\u5E73\u624B", "\u9999\u843D\u3061", "\u53F3\u9999\u843D\u3061",
					"\u89D2\u843D\u3061", "\u98DB\u8ECA\u843D\u3061", "\u98DB\u9999\u843D\u3061",
					"\u4E8C\u679A\u843D\u3061", "\u4E09\u679A\u843D\u3061", "\u56DB\u679A\u843D\u3061",
					"\u4E94\u679A\u843D\u3061", "\u5DE6\u4E94\u679A\u843D\u3061", "\u516D\u679A\u843D\u3061",
					"\u516B\u679A\u843D\u3061", "\u5341\u679A\u843D\u3061", "\u305D\u306E\u4ED6" };
			final String s = "\u624B\u5408\u5272\uFF1A" + teai_name[rbd.teai];
			g.drawString(s, OX1, OY1 + 5 * info_pitch_y);
		} else if (rbd.teai != 0) {
			g.setColor(Color.blue);
			g.drawString("\u6301\u99D2\u5236\u9650", OX1, OY1 + 5 * info_pitch_y);
			g.setColor(Color.black);
		}
	}

	void DispTesuu(final Graphics g, final KifuData kifu) {
		final String s = "\u624B\u3000\u6570\uFF1A" + kifu.move_no;
		g.drawString(s, OX1, OY1 + 6 * info_pitch_y);
	}

}
