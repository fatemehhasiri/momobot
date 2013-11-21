// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Drabod.java

package kifujl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

// Referenced classes of package kifujl:
//            Board

public class Drabod implements Serializable {

	static final int FU = 1;

	static final int GOTE = 0;

	static final int MOTI_TEXT = 0;

	static final int SENTE = 128;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int BD_ORG_X;

	int BD_ORG_Y;

	int EDGE_X;

	int EDGE_Y;

	int HAKO_SMALL_Y;

	int koma_size_x;

	int koma_size_y;

	int KSQ_SIZE_Y;

	int LEFT_GAP;

	int MOTI_EDGE_X;

	int MOTI_EDGE_Y;

	int MOTI_OVERLAP;

	int MOTI_SIZE_X;

	int MOTI_SIZE_Y;

	int MOTI_SQ_SIZE_X;

	int RIGHT_GAP;
	int SQ_SIZE_X;
	int SQ_SIZE_Y;

	public Drabod() {
		koma_size_x = 25;
		koma_size_y = 26;
		BD_ORG_X = 110;
		BD_ORG_Y = 45;
		SQ_SIZE_X = koma_size_x + 3;
		SQ_SIZE_Y = koma_size_y + 3;
		EDGE_X = 12;
		EDGE_Y = 12;
		LEFT_GAP = 4;
		RIGHT_GAP = 4;
		KSQ_SIZE_Y = SQ_SIZE_Y - 1;
		MOTI_EDGE_X = 2;
		MOTI_SQ_SIZE_X = SQ_SIZE_X * 3 / 2 + 1;
		MOTI_EDGE_Y = 3;
		MOTI_SIZE_X = 3 * SQ_SIZE_X + 2 + 2 * MOTI_EDGE_X;
		MOTI_SIZE_Y = 4 * SQ_SIZE_Y + EDGE_Y + MOTI_TEXT;
		MOTI_OVERLAP = 3;
		HAKO_SMALL_Y = 3;
	}

	public void stars(final Graphics g) {
		g.setColor(Color.black);
		star(g, 3, 3);
		star(g, 6, 3);
		star(g, 3, 6);
		star(g, 6, 6);
	}

	void box(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		g.setColor(Color.black);
		g.drawRect(x1, y1, x2 - x1, y2 - y1);
	}

	void clear_hako_rect(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		g.setColor(new Color(188, 126, 79));
		g.fillRect(x1, y1, x2 - x1, y2 - y1);
	}

	void clear_rect(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		g.setColor(new Color(242, 192, 119));
		g.fillRect(x1, y1, x2 - x1, y2 - y1);
		stars(g);
	}

	void clear_sq(final Graphics g, final int s, final int bdinv_flg) {
		int x = s / 10;
		int y = s % 10;
		if (bdinv_flg != 0) {
			x = 10 - x;
			y = 10 - y;
		}
		final int y1 = BD_ORG_Y + (y - 1) * SQ_SIZE_Y + 2;
		final int x1 = BD_ORG_X + (9 - x) * SQ_SIZE_X + 2;
		final Point p = new Point();
		p.x = x1;
		p.y = y1;
		clear_xy(g, p);
	}

	void clear_xy(final Graphics g, final Point p) {
		final int left = p.x - 1;
		final int top = p.y - 1;
		final int right = p.x + SQ_SIZE_X - 2;
		final int bottom = p.y + SQ_SIZE_Y - 2;
		clear_rect(g, left, top, right, bottom);
	}

	void draw_coord(final Graphics g, final int bdinv_flg) {
		final String num[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		final String knum[] = { "\u96F6", "\u4E00", "\u4E8C", "\u4E09", "\u56DB", "\u4E94", "\u516D", "\u4E03",
				"\u516B", "\u4E5D" };
		final Font f = new Font("Dialog", 0, 11);
		g.setFont(f);
		if (bdinv_flg == 0) {
			int x;
			for (int i = 1; i <= 9; i++) {
				x = BD_ORG_X + (9 - i) * SQ_SIZE_X + SQ_SIZE_X / 2 - 4;
				g.drawString(num[i], x, BD_ORG_Y - 1);
			}

			x = BD_ORG_X + 9 * SQ_SIZE_X;
			for (int i = 1; i <= 9; i++) {
				final int y = BD_ORG_Y + (i - 1) * SQ_SIZE_Y + SQ_SIZE_Y / 2 + 4;
				g.drawString(knum[i], x + 2, y);
			}

		} else {
			int y = BD_ORG_Y + SQ_SIZE_Y * 9 + 10;
			int x;
			for (int i = 1; i <= 9; i++) {
				x = BD_ORG_X + (i - 1) * SQ_SIZE_X + SQ_SIZE_X / 2 - 4;
				g.drawString(num[i], x, y);
			}

			x = BD_ORG_X - EDGE_X;
			for (int i = 1; i <= 9; i++) {
				y = BD_ORG_Y + (i - 1) * SQ_SIZE_Y + SQ_SIZE_Y / 2 - 4;
				g.drawString(knum[10 - i], x + 2, y);
			}

		}
	}

	int get_moti_l_yo() {
		return BD_ORG_Y - EDGE_Y + MOTI_EDGE_Y;
	}

	int get_moti_r_yo() {
		return BD_ORG_Y + 9 * SQ_SIZE_Y + EDGE_Y - 4 * KSQ_SIZE_Y - MOTI_EDGE_Y;
	}

	int get_moti_x_ll() {
		final int ox = BD_ORG_X - EDGE_X - LEFT_GAP - MOTI_SIZE_X;
		return ox + MOTI_EDGE_X;
	}

	int get_moti_x_lr() {
		final int ox = BD_ORG_X - EDGE_X - LEFT_GAP - MOTI_SIZE_X;
		return ox + MOTI_EDGE_X + MOTI_SQ_SIZE_X - MOTI_OVERLAP;
	}

	int get_moti_x_rl() {
		return BD_ORG_X + 9 * SQ_SIZE_X + EDGE_X + RIGHT_GAP + MOTI_EDGE_X;
	}

	int get_moti_x_rr() {
		return BD_ORG_X + 9 * SQ_SIZE_X + EDGE_X + RIGHT_GAP + MOTI_EDGE_X + MOTI_SQ_SIZE_X - MOTI_OVERLAP;
	}

	Point GetMotiXy(final Board rbd, final int teban, final int k, final int bdinv_flg) {
		int side;
		if (teban == 128) {
			side = 0;
		} else {
			side = 1;
		}
		if (bdinv_flg != 0) {
			side = 1 - side;
		}
		int no = 0;
		int k1 = 7;
		label0: do {
			if (k1 < 1) {
				break;
			}
			final int k2 = k1 | teban;
			int s = 111;
			do {
				if (s >= rbd.menup) {
					break;
				}
				if (rbd.board[s] == k2) {
					no++;
					if (k1 == (k & 0xf)) {
						break label0;
					}
					break;
				}
				s++;
			} while (true);
			if (k1 == (k & 0xf)) {
				no++;
				break;
			}
			k1--;
		} while (true);
		if (k1 == 1) {
			if (side == 0) {
				if ((no & 1) == 0) {
					no++;
				}
			} else if ((no & 1) == 0) {
				no += 2;
			} else {
				no++;
			}
		}
		return no_to_p(side, no);
	}

	Point no_to_p(final int side, final int no) {
		final Point p = new Point();
		final int i = no - 1;
		int x;
		int y;
		if (side == 0) {
			final int xo1 = get_moti_x_rl();
			final int xo2 = get_moti_x_rr();
			if ((i & 1) == 0) {
				x = xo1;
			} else {
				x = xo2;
			}
			final int yo = get_moti_r_yo();
			y = yo + i / 2 * KSQ_SIZE_Y;
		} else {
			final int xo2 = get_moti_x_ll();
			final int xo1 = get_moti_x_lr();
			if ((i & 1) == 0) {
				x = xo1;
			} else {
				x = xo2;
			}
			final int yo = get_moti_l_yo();
			final int yi = 3 - i / 2;
			y = yo + yi * KSQ_SIZE_Y;
		}
		p.x = x + 2;
		p.y = y + 2;
		return p;
	}

	void shadow_box(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		g.setColor(Color.black);
		g.drawRect(x1, y1, x2 - x1, y2 - y1);
		g.drawRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	void shadow_box2(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		g.setColor(new Color(66, 66, 66));
		g.drawLine(x1, y2, x2, y2);
		g.drawLine(x1, y2 + 1, x2, y2 + 1);
		g.drawLine(x2, y1, x2, y2);
		g.drawLine(x2 + 1, y1, x2 + 1, y2);
	}

	void star(final Graphics g, final int x0, final int y0) {
		final int x = BD_ORG_X + SQ_SIZE_X * x0;
		final int y = BD_ORG_Y + SQ_SIZE_Y * y0;
		box(g, x - 1, y - 2, x + 1, y + 2);
		box(g, x - 2, y - 1, x + 2, y + 1);
	}
}
