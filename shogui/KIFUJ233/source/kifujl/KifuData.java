// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   KifuData.java

package kifujl;

import java.io.Serializable;

// Referenced classes of package kifujl:
//            Move, Kifup, Board

public class KifuData implements Serializable {

	static final int AGARU = 5;

	static final short CHUDAN = 1;

	static final boolean DEBUG = false;

	static final short FU = 1;

	static final short FUZUMI = 8;

	static final short GI = 4;

	static final short GOTE = 0;

	static final short GY = 8;

	static final short HANSOKU = 4;

	static final short HANSOKU_MAKE = 2;

	static final short HI = 7;

	static final int HIDARI = 8;

	static final int HIDARIAGARU = 14;

	static final int HIDARIHIKI = 12;

	static final int HIDARIYORI = 13;

	static final int HIKI = 4;

	static final int IKU = 2;

	static final short JISHOGI = 5;

	static final short KA = 6;

	static final short KE = 3;

	static final short KI = 5;

	static final short KIREMAKE = 9;

	static final short KY = 2;

	static final int MIGI = 7;

	static final int MIGIAGARU = 11;

	static final int MIGIHIKI = 9;

	static final int MIGIYORI = 10;

	static final short NE = 11;

	static final short NG = 12;

	static final short NY = 10;

	static final short RY = 15;

	static final short SENNITITE = 7;

	static final short SENTE = 128;

	static final short SFU = 129;

	static final short SGI = 132;

	static final short SGY = 136;
	static final short SHI = 135;
	static final short SKA = 134;
	static final short SKE = 131;
	static final short SKI = 133;
	static final short SKY = 130;
	static final int SUGU = 3;
	static final short TO = 9;
	static final short TORYO = 0;
	static final short TUMI = 6;
	static final short UM = 14;
	static final int YORI = 6;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int caldir(final int f, final int p_t) {
		int t = p_t;
		t &= 0x7f;
		final int d = t - f;
		int ad;
		if (d >= 0) {
			ad = d;
		} else {
			ad = -d;
		}
		if (ad == 12) {
			return d;
		}
		final int fx = f / 10;
		final int tx = t / 10;
		if (fx == tx) {
			return d <= 0 ? -1 : 1;
		}
		if (ad == 8) {
			return d;
		}
		final int fy = f % 10;
		final int ty = t % 10;
		if (fy == ty) {
			return d <= 0 ? -10 : 10;
		}
		if (fx + fy == tx + ty) {
			return d <= 0 ? -9 : 9;
		}
		if (fx - fy == tx - ty) {
			return d <= 0 ? -11 : 11;
		}
		return 100;
	}

	private static int promote(final int f, final int t, final int k0) {
		if (f > 99 || (t & 0x80) != 0) {
			return 0;
		}
		final int k = k0 & 0xf;
		if (k >= 8 || k == 5) {
			return 0;
		}
		final int ty = t % 10;
		final int fy = f % 10;
		if ((k0 & 0x80) == 0) {
			if (ty < 7 && fy < 7) {
				return 0;
			}
			if (k <= 2) {
				if (ty == 9) {
					return 2;
				}
			} else if (k == 3 && ty > 7) {
				return 2;
			}
		} else {
			if (ty > 3 && fy > 3) {
				return 0;
			}
			if (k <= 2) {
				if (ty == 1) {
					return 2;
				}
			} else if (k == 3 && ty < 3) {
				return 2;
			}
		}
		return 1;
	}

	public Kifup current_p;
	public Kifup kifu_root;
	int abnormal_flg;
	String date;
	String gote_name = "";
	String happyou_nengetu;
	int is_henka_flg;
	byte kdirs[][] = { { -12, -11, -10, -9, -1, 1, 8, 9, 10, 11 }, { 0 }, { -11, -1, 9, 0 }, { -1, 0 },
			{ 11, 1, -9, 0 }, { -11, -1, 9, 0 }, { -10, 10, 0 }, { 8, 9, 10, 11, -1, 1, 0 },
			{ -9, -10, -11, -12, -1, 1, 0 }, { 11, 0 }, { 10, 0 }, { 9, 0 }, { -9, 0 }, { -10, 0 }, { -11, 0 } };
	Kifup[] kifu;
	int kifu_max = 3000;
	int kiki[][][] = { { { 0 }, { 0 } }, { { 1 }, { 0 } }, { { 1 }, { 1 } }, { { -8, 12 }, { 0, 0 } },
			{ { -11, -9, 1, 9, 11 }, { 0, 0, 0, 0, 0 } }, { { -10, -9, -1, 1, 10, 11 }, { 0, 0, 0, 0, 0, 0 } },
			{ { -11, -9, 9, 11 }, { 1, 1, 1, 1 } }, { { -10, -1, 1, 10 }, { 1, 1, 1, 1 } },
			{ { -11, -10, -9, -1, 1, 9, 10, 11 }, { 0, 0, 0, 0, 0, 0, 0, 0 } },
			{ { -10, -9, -1, 1, 10, 11 }, { 0, 0, 0, 0, 0, 0 } }, { { -10, -9, -1, 1, 10, 11 }, { 0, 0, 0, 0, 0, 0 } },
			{ { -10, -9, -1, 1, 10, 11 }, { 0, 0, 0, 0, 0, 0 } }, { { -10, -9, -1, 1, 10, 11 }, { 0, 0, 0, 0, 0, 0 } },
			{ { -10, -9, -1, 1, 10, 11 }, { 0, 0, 0, 0, 0, 0 } },
			{ { -11, -10, -9, -1, 1, 9, 10, 11 }, { 1, 0, 1, 0, 0, 1, 0, 1 } },
			{ { -11, -10, -9, -1, 1, 9, 10, 11 }, { 0, 1, 0, 1, 1, 0, 1, 0 } } };
	int kiki2n[] = { 0, 1, 1, 2, 5, 6, 4, 4, 8, 6, 6, 6, 6, 6, 8, 8 };
	String kisen;
	String knum[] = { "\u96F6", "\u4E00", "\u4E8C", "\u4E09", "\u56DB", "\u4E94", "\u516D", "\u4E03", "\u516B",
			"\u4E5D", "\u5341" };
	String motijikan;
	int move_no;
	String sakuhinmei;
	String sakusya;
	String sente_name = "";
	String site;
	String sj_koma[] = { "\u3000", "\u6B69", "\u9999", "\u6842", "\u9280", "\u91D1", "\u89D2", "\u98DB", "\u7389",
			"\u3068", "\u674F", "\u572D", "\u5168", "\u91D1", "\u99AC", "\u9F8D" };

	String syutten;

	String zen[] = { "\uFF10", "\uFF11", "\uFF12", "\uFF13", "\uFF14", "\uFF15", "\uFF16", "\uFF17", "\uFF18", "\uFF19" };

	public KifuData() {
		kifu = new Kifup[kifu_max];
		clear_kifu();
	}

	public int can_susumeru(final Board rbd) {
		final Move next_move = get_next_move(rbd);
		return next_move.t < 11 ? 0 : 1;
	}

	public final void clear_kifu() {
		for (int i = 0; i < kifu_max; i++) {
			kifu[i] = new Kifup();
			kifu[i].f = 0;
			kifu[i].t = 0;
		}

		kifu_root = kifu[0];
		current_p = kifu[0];
		move_no = 0;
	}

	public void copy_board_to_board(final Board s, final Board d) {
		System.arraycopy(s.board, 0, d.board, 0, 149);
		System.arraycopy(s.piece, 0, d.piece, 0, 149);
		System.arraycopy(s.square, 0, d.square, 0, 41);

		d.menup = s.menup;
		d.teban = s.teban;
	}

	public int current_bunki() {
		if (is_henka_flg == 0) {
			return 0;
		}
		if (current_p == null) {
			return 0;
		}
		final Kifup next = current_p.next;
		if (next == null) {
			return 0;
		}
		return next.branch == null ? 0 : 1;
	}

	public int current_p_susumeru(final Move move) {
		Kifup kp = current_p.next;
		if (move.f == kp.f && move.t == kp.t) {
			current_p = kp;
			return 0;
		}
		kp = kp.branch;
		do {
			if (move.f == kp.f && move.t == kp.t) {
				current_p = kp;
				return 0;
			}
			kp = kp.branch;
		} while (true);
	}

	public Kifup find_oya(final Kifup kp) {
		if (kp == kifu_root || kp == null) {
			return null;
		}
		for (int i = 0; i < kifu_max; i++) {
			final Kifup p = kifu[i];
			if (p.next == kp) {
				return p;
			}
			if (p.branch == kp) {
				return find_oya(p);
			}
		}

		return null;
	}

	public Move get_next_move(final Board rbd) {
		final Move move = new Move();
		if (move_no > 0) {
			final Move last_move = get_last_move();
			if (last_move.t <= 1) {
				move.f = 0;
				move.t = 1;
				return move;
			}
		}
		if (current_p == null) {
			move.f = 0;
			move.t = 0;
			return move;
		}
		final Kifup next = current_p.next;
		if (next == null) {
			move.f = 0;
			move.t = 0;
			return move;
		}
		move.f = movef_to_old(rbd, next.f);
		move.t = next.t;
		return move;
	}

	public int get_sasite_n() {
		int n = 0;
		if (current_p == null) {
			return 0;
		}
		Kifup kp = current_p;
		do {
			kp = find_oya(kp);
			if (kp == null) {
				break;
			}
			n++;
		} while (true);
		kp = current_p;
		do {
			kp = kp.next;
			if (kp != null && kp.t >= 11) {
				n++;
			} else {
				return n;
			}
		} while (true);
	}

	public int get_total_sasite_n() {
		int n = 0;
		if (kifu_root == null) {
			return 0;
		}
		for (int i = 1; i < kifu_max; i++) {
			final Kifup kp = kifu[i];
			if (kp.f > 0 && kp.t >= 11) {
				n++;
			}
		}

		return n;
	}

	public int is_bunki_n(final int mn) {
		if (is_henka_flg == 0) {
			return 0;
		}
		final Kifup kp = get_kifup_n(mn);
		if (kp == null) {
			return 0;
		}
		if (kp.branch != null) {
			return 1;
		}
		final Kifup oya = find_oya(kp);
		if (oya == null) {
			return 0;
		}
		if (oya.next == kp) {
			return 0;
		}
		return oya.next.branch == null ? 0 : 1;
	}

	public int is_henka_kp(final Kifup p) {
		if (p == null) {
			return 0;
		}
		Kifup kp = p;
		do {
			final Kifup oyap = find_oya(kp);
			if (oyap == null) {
				break;
			}
			if (oyap.next != kp) {
				return 1;
			}
			if (oyap == kifu_root) {
				break;
			}
			kp = oyap;
		} while (true);
		return 0;
	}

	public String kifu_mov_strm(final int mode, final int dir, final int n, final int len) {
		final Kifup kp = get_kifup_n(n);
		if (kp == null) {
			return "";
		}
		return kifu_mov_strm_kp(mode, dir, kp, len);
	}

	public String kifu_mov_strm_kp(final int mode, final int dir, final Kifup kp, final int len) {
		if (kp == null) {
			return "";
		}
		final int f = kp.f;
		final int t = kp.t;
		final int k = kp.koma;
		final int m = kp.modify;
		final Kifup oya = find_oya(kp);
		int tb;
		if (oya == null) {
			tb = 0;
		} else {
			tb = oya.t;
		}
		return mov_strm_sub(f, t, k, m, tb, mode, dir, len);
	}

	public short movef_to_old(final Board rbd, final short pf) {
		short f = pf;
		if (f > 99) {
			final int f1 = f;
			int k = f1 & 0xf;
			if ((f & 0x10) != 0) {
				k |= 0x80;
			}
			f = 0;
			short n = 111;
			do {
				if (n >= rbd.menup) {
					break;
				}
				if (rbd.board[n] == k) {
					f = n;
					break;
				}
				n++;
			} while (true);
		}
		return f;
	}

	int add_comment(final int n, final String comm) {
		final Kifup kp = get_kifup_n(n);
		if (kp == null) {
			return -4;
		}
		final String cp = kp.comment;
		if (cp == null) {
			kp.comment = comm;
		} else {
			kp.comment += "\n" + comm;
		}
		return 0;
	}

	int genmovq(final Board rbd, final int f, final int t) {
		if (f > 99) {
			return 0;
		}
		final int k = rbd.board[f];
		final int at = k & 0x80;
		final int kc = k & 0xf;
		final int kn = kiki2n[kc];
		label0: for (int n = 0; n < kn; n++) {
			int vec = kiki[kc][0][n];
			if (at != 0) {
				vec = -vec;
			}
			int t1 = f + vec;
			if (t1 == t) {
				return 1;
			}
			if (kiki[kc][1][n] == 0 || rbd.board[t1] != 0) {
				continue;
			}
			t1 += vec;
			do {
				if (t1 > 99 || t1 < 11) {
					continue label0;
				}
				if (t1 == t) {
					return 1;
				}
				if (rbd.board[t1] != 0) {
					continue label0;
				}
				t1 += vec;
			} while (true);
		}

		return 0;
	}

	Move get_henka_move(final Board rbd) {
		final Move move = new Move();
		final Kifup next = current_p.next;
		if (next == null) {
			move.t = 0;
			return move;
		}
		if (next.branch == null) {
			move.t = 0;
			return move;
		}
		move.f = movef_to_old(rbd, next.branch.f);
		move.t = next.branch.t;
		return move;
	}

	String get_kifu_commentp(final int n) {
		final Kifup kp = get_kifup_n(n);
		if (kp == null) {
			return null;
		}
		return kp.comment;
	}

	int get_kifu_size() {
		if (current_p == null) {
			return 0;
		}
		Kifup kp = current_p;
		int n = 0;
		while (true) {
			kp = find_oya(kp);
			if (kp == null) {
				break;
			}
			n++;
		}
		kp = current_p;
		while (true) {
			kp = kp.next;
			if (kp == null) {
				return n;
			}
			n++;
		}
	}

	Kifup get_kifup_n(final int n) {
		if (n == 0) {
			return kifu_root;
		} else if (n == move_no) {
			return current_p;
		} else if (current_p == null) {
			return null;
		}
		Kifup p = current_p;
		int move_no2 = move_no;
		if (n > move_no) {
			do {
				p = p.next;
				if (p == null) {
					return null;
				}
				move_no2++;
			} while (n != move_no2);
			return p;
		}
		do {
			p = find_oya(p);
			if (p == null) {
				return null;
			}
			move_no2--;
		} while (n != move_no2);
		return p;
	}

	Move get_last_move() {
		final Move move = new Move();
		if (current_p == null || current_p == kifu_root) {
			move.f = 0;
			move.t = 0;
		} else {
			move.f = current_p.f;
			move.t = current_p.t;
		}
		return move;
	}

	int get_modify(final Board rbd, final Move move) {
		final int kg[] = new int[20];
		int mn = 0;
		final int f = move.f;
		final int k = rbd.board[f];
		int big;
		if ((k & 7) == 7 || (k & 7) == 6) {
			big = 1;
		} else {
			big = 0;
		}
		final int teban = k & 0x80;
		final int t = move.t;
		final int t1 = t & 0x7f;
		if (promote(f, t1, k) != 0 && (t & 0x80) == 0) {
			mn = 64;
		}
		final int kn = gen_kiki(rbd, t1, teban, 10, kg);
		int k1;
		if ((t & 0x80) == 0) {
			k1 = k;
		} else {
			k1 = k & 0x87;
		}
		int n1 = 0;
		int i = 1;
		do {
			if (i > kn) {
				break;
			}
			final int f1 = kg[i];
			if (f1 != f && rbd.board[f1] == k1) {
				n1++;
				break;
			}
			i++;
		} while (true);
		if (n1 == 0) {
			return 1 | mn;
		}
		if (f > 99) {
			return 0 | mn;
		}
		final int dir = caldir(f, t1);
		for (int m1 = 2;; m1++) {
			m1 = get_modify_sub(teban, big, dir, m1);
			if (m1 == 0) {
				return 0 | mn;
			}
			final int r = uniq(rbd, teban, f, t1, k, m1, kg, big);
			if (r != 0) {
				return r | mn;
			}
		}
	}

	int get_modify_sub(final int teban, final int big, final int dir, final int p_m1) {
		int m1 = p_m1;
		label0: for (; m1 <= 14; m1++) {
			if (big == 0 ? m1 == 2 : m1 == 3) {
				continue;
			}
			int j = 0;
			while (true) {
				if (j >= 10) {
					continue label0;
				}
				int d = kdirs[m1][j];
				if (d == 0 || big == 0 && (m1 == 7 || m1 == 8) && d == -1) {
					continue label0;
				}
				if (teban == 0) {
					d = -d;
				}
				if (d == dir) {
					return m1;
				}
				j++;
			}
		}

		return 0;
	}

	Kifup get_new_kp() {
		for (int i = 1; i < kifu_max; i++) {
			if (kifu[i].f == 0 && kifu[i].t == 0) {
				return kifu[i];
			}
		}
		return null;
	}

	int is_syuukyoku() {
		for (Kifup kp = kifu_root; kp != null; kp = kp.next) {
			if (kp == kifu_root) {
				continue;
			}
			if (kp.f == 0 && kp.t == 0) {
				break;
			}
			if (kp.f == 0 || kp.t >= 11) {
				continue;
			}
			return kp.t == 1 ? 0 : 1;
		}

		return 0;
	}

	int store_kifu1(final Board rbd, final Board start, final int mn, final short p_f, final short t, final short p_k) {
		short k = p_k;
		if (start.teban == 128) {
			if ((mn & 1) != 0) {
				k |= 0x80;
			}
		} else if ((mn & 1) == 0) {
			k |= 0x80;
		}
		final Move move = new Move();
		int r1;
		short f = p_f;
		if (t < 11) {
			move.f = f;
			move.t = t;
			r1 = store(rbd, start, move);
			return r1;
		}
		if (f == 0) {
			short i = 111;
			do {
				if (i >= rbd.menup) {
					break;
				}
				if (rbd.board[i] == k) {
					f = i;
					break;
				}
				i++;
			} while (true);
			if (i == rbd.menup) {
				return -1;
			}
		}
		final int kt = rbd.board[t & 0x7f];
		if (f > 99) {
			if (kt != 0) {
				return -1;
			}
			if ((t & 0x80) != 0) {
				return -1;
			}
		} else if (t > 10 && kt != 0 && (rbd.board[f] & 0x80) == (kt & 0x80)) {
			return -1;
		}
		final int r = 0;
		move.f = f;
		move.t = t;
		r1 = store(rbd, start, move);
		if (r1 != 0) {
			return -2;
		}
		rbd.exec_move_l(move);
		if (t >= 11) {
			move_no++;
			rbd.teban = (short) (128 - rbd.teban);
		}
		return r;
	}

	int uniq(final Board rbd, final int teban, final int f, final int t1, final int k, final int m1, final int kg[],
			final int big) {
		final int kn = kg[0];
		label0: for (int j = 1; j <= kn; j++) {
			final int f1 = kg[j];
			if (f1 == f || rbd.board[f1] != k) {
				continue;
			}
			final int d2 = caldir(f1, t1);
			int a = 0;
			do {
				if (a >= 10) {
					continue label0;
				}
				int d = kdirs[m1][a];
				if (d == 0 || big == 0 && (m1 == 7 || m1 == 8) && d == -1) {
					continue label0;
				}
				if (teban == 0) {
					d = -d;
				}
				if (d == d2) {
					return 0;
				}
				a++;
			} while (true);
		}

		return m1;
	}

	private int gen_kiki(final Board rbd, final int s, final int teban, final int limit, final int kg[]) {
		final int sx = s / 10;
		final int sy = s % 10;
		int st;
		if (teban == 0) {
			st = s - 1;
		} else {
			st = s + 1;
		}
		int n = 0;
		for (int p = 1; p <= 40; p++) {
			final int s1 = rbd.square[p];
			if (s1 == 0 || s1 == s || s1 > 99) {
				continue;
			}
			final int k1 = rbd.board[s1];
			if ((k1 & 0x80) != teban) {
				continue;
			}
			final int k = k1 & 0xf;
			final int x = s1 / 10;
			final int y = s1 % 10;
			final int xsa = Math.abs(x - sx);
			if (k == 1) {
				if (s1 == st) {
					n++;
					kg[n] = s1;
				}
				continue;
			}
			if (k == 2) {
				if (x != sx || (teban == 0 ? y >= sy : y <= sy)) {
					continue;
				}
			} else if (k == 7) {
				if (x != sx && y != sy) {
					continue;
				}
			} else if (k == 15) {
				if (xsa <= 1 && Math.abs(y - sy) <= 1) {
					n++;
					kg[n] = s1;
					continue;
				}
				if (x != sx && y != sy) {
					continue;
				}
			} else if (k == 6) {
				if (x - sx != y - sy && x - sx != sy - y) {
					continue;
				}
			} else if (k == 14) {
				if (xsa <= 1 && Math.abs(y - sy) <= 1) {
					n++;
					kg[n] = s1;
					continue;
				}
				if (x - sx != y - sy && x - sx != sy - y) {
					continue;
				}
			} else {
				if (k == 3) {
					if (xsa == 1 && (teban == 0 ? sy - y == 2 : y - sy == 2)) {
						n++;
						kg[n] = s1;
					}
					continue;
				}
				if (xsa > 1 || Math.abs(y - sy) > 1) {
					continue;
				}
			}
			final int can = genmovq(rbd, s1, s);
			if (can == 0) {
				continue;
			}
			n++;
			kg[n] = s1;
			if (n >= limit) {
				break;
			}
		}

		return kg[0] = n;
	}

	private String mov_strm_sub(final int f, final int t, final int p_k, final int m, final int tb, final int mode,
			final int dir, final int len) {
		final String modify_s[] = { "", "", "\u884C", "\u76F4", "\u5F15", "\u4E0A", "\u5BC4", "\u53F3", "\u5DE6",
				"\u53F3\u5F15", "\u53F3\u5BC4", "\u53F3\u4E0A", "\u5DE6\u5F15", "\u5DE6\u5BC4", "\u5DE6\u4E0A" };
		String buf1;
		int k = p_k;
		if (dir == 0) {
			buf1 = "";
		} else if ((k & 0x80) == 0) {
			if (dir == 1) {
				buf1 = "\u25B3";
			} else {
				buf1 = "\u25BD";
			}
		} else {
			buf1 = "\u25B2";
		}
		k &= 0xf;
		final int s = t & 0x7f;
		String moves = "";
		if (t == 0) {
			moves = "\u6295\u4E86";
		} else if (t == 1) {
			moves = "\u4E2D\u65AD";
		} else if (t == 5) {
			moves = "\u6301\u5C06\u68CB";
		} else if (t == 6) {
			moves = "\u8A70\u307F";
		} else if (t == 8) {
			moves = "\u4E0D\u8A70";
		} else if (t == 7) {
			moves = "\u5343\u65E5\u624B";
		} else if (t == 9) {
			moves = "\u5207\u308C\u8CA0\u3051";
		} else if (t == 4) {
			moves = "\u53CD\u5247\u52DD\u3061";
		} else if (t == 2) {
			moves = "\u53CD\u5247\u8CA0\u3051";
		} else {
			if (mode == 0 && s == (tb & 0x7f)) {
				moves = "\u540C\u3000";
			} else if (mode == 2 && s == (tb & 0x7f)) {
				moves += zen[s / 10] + knum[s % 10] + "\u540C";
			} else if (mode == 3 && (t & 0x7f) == (tb & 0x7f)) {
				moves = "\u540C";
			} else {
				moves += zen[s / 10] + knum[s % 10];
			}
			if (k == 10) {
				moves += "\u6210\u9999";
			} else if (k == 11) {
				moves += "\u6210\u6842";
			} else if (k == 12) {
				moves += "\u6210\u9280";
			} else {
				moves = moves + sj_koma[k];
			}
			if (f < 100 && f != 0) {
				moves = moves + modify_s[m & 0x3f];
				if ((t & 0x80) == 0) {
					if ((m & 0x40) != 0) {
						moves += "\u4E0D\u6210";
					}
				} else {
					moves += "\u6210";
				}
			} else if (mode == 2 || m == 0) {
				moves += "\u6253";
			}
		}
		final String top = moves;
		if (top.startsWith("\u540C") && top.length() >= 4) {
			final String koma = top.substring(2);
			moves = "\u540C" + koma;
		}
		moves = buf1 + moves;
		for (int l = moves.length(); l < len; l++) {
			moves += "\u3000";
		}

		return moves;
	}

	private void set_modify_kp(final Board rbd, final Kifup kp, final Move move) {
		kp.modify = (short) get_modify(rbd, move);
	}

	private int store(final Board rbd, final Board start, final Move move) {
		short new_f = move.f;
		if (move.f > 99) {
			final short k = rbd.board[move.f];
			if ((k & 0x80) == 0) {
				new_f = (short) (0x80 | k & 0xf);
			} else {
				new_f = (short) (0x90 | k & 0xf);
			}
		}
		if (move_no == 0 && move.t == 1) {
			return 0;
		}
		final int n = move_no + 1;
		if (current_p == null) {
			return -1;
		}
		final Kifup np = current_p.next;
		Kifup kp;
		if (np == null) {
			kp = get_new_kp();
			if (kp == null) {
				return -1;
			}
			current_p.next = kp;
		} else if (np.t == 1) {
			kp = np;
		} else if (np.t < 11 && move.t < 11) {
			kp = np;
		} else if (np.f == new_f && np.t == move.t) {
			kp = np;
		} else {
			if (move.t == 1) {
				return 0;
			}
			Kifup bp = np;
			do {
				if (bp.branch == null) {
					kp = get_new_kp();
					if (kp == null) {
						return -1;
					}
					bp.branch = kp;
					break;
				}
				bp = bp.branch;
				if (bp.f != new_f || bp.t != move.t) {
					continue;
				}
				kp = bp;
				break;
			} while (true);
			is_henka_flg = 1;
		}
		if (move.t == 0 && new_f == 0) {
			new_f = 1;
		}
		kp.f = new_f;
		kp.t = move.t;
		if (move.t > 10) {
			kp.koma = rbd.board[move.f];
			kp.dead = rbd.board[move.t & 0x7f];
			current_p = kp;
		} else {
			kp.dead = 0;
			short k = 0;
			if (start.teban == 128) {
				if ((n & 1) != 0) {
					k |= 0x80;
				}
			} else if ((n & 1) == 0) {
				k |= 0x80;
			}
			kp.koma = k;
		}
		set_modify_kp(rbd, kp, move);
		if (move.t > 10 && new_f < 11) {
			System.out.println("***store_kifu error");
		}
		return 0;
	}
}
