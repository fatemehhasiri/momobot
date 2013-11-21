// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   KifuFile.java

package kifujl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

// Referenced classes of package kifujl:
//            Board, KifuData, Kifup

public class KifuFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int last_move_sub(final String move) {
		if (move.startsWith("\u6295\u4E86")) {
			return 0;
		}
		if (move.startsWith("\u6295\u3000\u4E86")) {
			return 0;
		}
		if (move.startsWith("\u6301\u5C06\u68CB")) {
			return 5;
		}
		if (move.startsWith("\u8A70")) {
			return 6;
		}
		if (move.startsWith("\u4E0D\u8A70")) {
			return 8;
		}
		if (move.startsWith("\u5343\u65E5\u624B")) {
			return 7;
		}
		if (move.startsWith("\u5207\u308C\u8CA0\u3051")) {
			return 9;
		}
		if (move.startsWith("\u53CD\u5247\u52DD\u3061")) {
			return 4;
		}
		if (move.startsWith("\u53CD\u5247\u8CA0\u3051")) {
			return 2;
		}
		if (move.startsWith("\u307E\u3067")) {
			final String move1 = move.substring(2);
			if (move1.indexOf("\u53CD\u5247\u52DD\u3061") >= 0) {
				return 4;
			}
			if (move1.indexOf("\u53CD\u5247\u8CA0\u3051") >= 0) {
				return 2;
			}
			if (move1.indexOf("\u52DD\u3061") >= 0) {
				return 0;
			}
			if (move1.indexOf("\u6301\u5C06\u68CB") >= 0) {
				return 5;
			}
			if (move1.indexOf("\u4E0D\u8A70") >= 0) {
				return 8;
			}
			if (move1.indexOf("\u8A70") >= 0) {
				return 6;
			}
			if (move1.indexOf("\u5343\u65E5\u624B") >= 0) {
				return 7;
			}
			if (move1.indexOf("\u5207\u308C\u8CA0\u3051") >= 0) {
				return 9;
			}
		}
		return -1;
	}

	private final String kteai[] = { "\u5E73\u624B", "\u9999\u843D\u3061", "\u53F3\u9999\u843D\u3061",
			"\u89D2\u843D\u3061", "\u98DB\u8ECA\u843D\u3061", "\u98DB\u9999\u843D\u3061", "\u4E8C\u679A\u843D\u3061",
			"\u4E09\u679A\u843D\u3061", "\u56DB\u679A\u843D\u3061", "\u4E94\u679A\u843D\u3061",
			"\u5DE6\u4E94\u679A\u843D\u3061", "\u516D\u679A\u843D\u3061", "\u516B\u679A\u843D\u3061",
			"\u5341\u679A\u843D\u3061", "\u305D\u306E\u4ED6\u3000" };

	public int load(final KifuData kifu, final Board rbd, final Board start, final URL kifu_name_url)
			throws IOException {
		final URLConnection uc = kifu_name_url.openConnection();
		uc.setUseCaches(false);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(
				uc.getInputStream()), "SJIS"))) {
			rbd.teai = 0;
			kifu.clear_kifu();
			kifu.sente_name = "";
			kifu.gote_name = "";
			rbd.teban = 128;
			int bd_set = 0;
			int y = 1;
			rbd.clr_bd_data();
			int last = 0;
			while (true) {
				final String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.length() <= 0 || line.startsWith("#")) {
					continue;
				}
				if (kifu.move_no > 0
						&& (line.startsWith("\u5206\u5C90\uFF1A") || line.startsWith("\u5909\u5316\uFF1A"))) {
					final StringTokenizer tokens = new StringTokenizer(line.substring(3));
					if (tokens.hasMoreTokens()) {
						try {
							final String ns = tokens.nextToken();
							final int t = ns.indexOf("\u624B");
							if (t > 0) {
								final int jn = Integer.parseInt(ns.substring(0, t));
								rbd.jump_back(kifu, jn - 1);
								last = 0;
							}
						} catch (final NumberFormatException e) {
							System.out.println("***\u5206\u5C90error1");
						}
					} else {
						System.out.println("***\u5206\u5C90error2");
					}
					continue;
				}
				if (load_kifu_info(kifu, line) != 0 || load_teai_sub(rbd, start, line) != 0) {
					continue;
				}
				if (line.startsWith("*")) {
					load_comment(kifu, line);
					continue;
				}
				if (last != 0) {
					continue;
				}
				int r = load_bod_sub2(rbd, line, y);
				if (r == 1) {
					y++;
					kifu.abnormal_flg = 1;
					continue;
				}
				if (r == -1) {
					break;
				}
				if (load_moti_sub(rbd, line) != 0) {
					continue;
				}
				if (bd_set == 0 && kifu_line(line) != 0 || line.startsWith("\u624B\u6570---")) {
					if (kifu.abnormal_flg == 0) {
						game_init(kifu, rbd, start);
					} else {
						sort_board(kifu, rbd, start, rbd.teban);
					}
					bd_set = 1;
				}
				r = load_move_sub(kifu, rbd, start, line);
				if (r == -2) {
					break;
				}
				if (r == 2) {
					last = 1;
				}
			}
			if (bd_set == 0 && kifu.abnormal_flg != 0) {
				sort_board(kifu, rbd, start, rbd.teban);
			}
			if (kifu.abnormal_flg == 0) {
				game_init(kifu, rbd, start);
			} else {
				kifu.copy_board_to_board(start, rbd);
			}
			kifu.move_no = 0;
			kifu.current_p = kifu.kifu_root;
		} catch (final FileNotFoundException e) {
			System.out.println("***ERROR 103: kifu file not found: " + kifu_name_url);
			return -1;
		} catch (final IOException e) {
			System.out.println("***IOException " + kifu_name_url);
			System.err.println("***IOException " + e);
		}
		return 0;
	}

	int check_abnormal(final KifuData kifu, final Board rbd, final int teai, final int teban) {
		kifu.abnormal_flg = 1;
		final Board normal = new Board();
		normal.init_teai_board(teai);
		if (comp_bd(rbd, normal) == 0) {
			if (teai == 0) {
				if (teban == 128) {
					kifu.abnormal_flg = 0;
				}
			} else if (teban == 0) {
				kifu.abnormal_flg = 0;
			}
		}
		return kifu.abnormal_flg;
	}

	int comp_bd(final Board rbd, final Board normal) {
		for (int s = 11; s <= 99; s++) {
			if (rbd.board[s] != normal.board[s]) {
				return 1;
			}
		}

		return 0;
	}

	void game_init(final KifuData kifu, final Board rbd, final Board start) {
		rbd.init_teai_board(rbd.teai);
		kifu.copy_board_to_board(rbd, start);
		kifu.move_no = 0;
		kifu.current_p = kifu.kifu_root;
		kifu.abnormal_flg = 0;
	}

	int get_kan_num(final String s) {
		final String knum[] = { "\u96F6", "\u4E00", "\u4E8C", "\u4E09", "\u56DB", "\u4E94", "\u516D", "\u4E03",
				"\u516B", "\u4E5D", "\u5341" };
		for (int i = 1; i <= 10; i++) {
			if (s.startsWith(knum[i])) {
				return i;
			}
		}

		return 0;
	}

	short get_koma(final String s) {
		if (s.startsWith("\u738B")) {
			return 8;
		}
		if (s.startsWith("\u6210\u9999")) {
			return 10;
		}
		if (s.startsWith("\u6210\u6842")) {
			return 11;
		}
		if (s.startsWith("\u6210\u9280")) {
			return 12;
		}
		if (s.startsWith("\u7ADC")) {
			return 15;
		}
		final String sj_koma[] = { "\u3000", "\u6B69", "\u9999", "\u6842", "\u9280", "\u91D1", "\u89D2", "\u98DB",
				"\u7389", "\u3068", "\u674F", "\u572D", "\u5168", "\u91D1", "\u99AC", "\u9F8D" };
		for (short k = 1; k <= 15; k++) {
			if (s.startsWith(sj_koma[k])) {
				return k;
			}
		}

		return 0;
	}

	short get_teai(final String s) {
		for (int i = 0; i <= 14; i++) {
			if (s.startsWith(kteai[i])) {
				return (short) i;
			}
		}
		return 14;
	}

	int get_zen_num(final String s) {
		final String zen[] = { "\uFF10", "\uFF11", "\uFF12", "\uFF13", "\uFF14", "\uFF15", "\uFF16", "\uFF17",
				"\uFF18", "\uFF19" };
		for (int i = 0; i <= 9; i++) {
			if (s.startsWith(zen[i])) {
				return i;
			}
		}

		return -1;
	}

	int kifu_line(final String buf) {
		if (sasite_mark(buf) != 0) {
			return 1;
		}
		if (buf.startsWith("   1 ")) {
			return 1;
		}
		return !buf.startsWith("1 ") ? 0 : 1;
	}

	int load_bod_sub2(final Board rbd, final String buf, final int y) {
		if (y > 9) {
			return 0;
		}
		int p = 0;
		int c = buf.codePointAt(p);
		p++;
		if (c == ':') {
			c = buf.codePointAt(p);
			p++;
		}
		if (c != '|') {
			return 0;
		}
		for (int x = 9; x >= 1; x--) {
			c = buf.codePointAt(p);
			p++;
			if (c == '|') {
				continue;
			}
			short teban;
			if (c == 'v' || c == '-' || c == 'V') {
				teban = 0;
			} else if (c == ' ' || c == '^' || c == '+') {
				teban = 128;
			} else {
				System.out.println(new StringBuffer("***load_bod_sub2 error ").append(buf).append("c=").append(c)
						.append("p=").append(p));
				return -1;
			}
			final int k = get_koma(buf.substring(p));
			if (k > 0) {
				rbd.board[x * 10 + y] = (short) (k | teban);
			}
			p++;
		}

		return 1;
	}

	int load_comment(final KifuData kifu, final String buf) {
		return kifu.add_comment(kifu.move_no, buf.substring(1));
	}

	int load_kifu_info(final KifuData kifu, final String buf) {
		if (buf.startsWith("\u5148\u624B\uFF1A") || buf.startsWith("\u4E0B\u624B\uFF1A")) {
			kifu.sente_name = buf.substring(3);
			return 1;
		}
		if (buf.startsWith("\u5F8C\u624B\uFF1A") || buf.startsWith("\u4E0A\u624B\uFF1A")) {
			kifu.gote_name = buf.substring(3);
			return 1;
		}
		if (buf.startsWith("\u5BFE\u5C40\u65E5\uFF1A")) {
			kifu.date = buf.substring(4);
			return 1;
		}
		if (buf.startsWith("\u958B\u59CB\u65E5\u6642\uFF1A")) {
			kifu.date = buf.substring(5);
			return 1;
		}
		if (buf.startsWith("\u4F5C\u8005\uFF1A")) {
			kifu.sakusya = buf.substring(3);
			return 1;
		}
		if (buf.startsWith("\u4F5C\u54C1\u540D\uFF1A")) {
			kifu.sakuhinmei = buf.substring(4);
			return 1;
		}
		if (buf.startsWith("\u767A\u8868\u8A8C\uFF1A")) {
			kifu.syutten = buf.substring(4);
			return 1;
		}
		if (buf.startsWith("\u51FA\u5178\uFF1A")) {
			kifu.syutten = buf.substring(3);
			return 1;
		}
		if (buf.startsWith("\u767A\u8868\u5E74\u6708\uFF1A")) {
			kifu.happyou_nengetu = buf.substring(5);
			return 1;
		}
		if (buf.startsWith("\u68CB\u6226\uFF1A")) {
			kifu.kisen = buf.substring(3);
			return 1;
		}
		if (buf.startsWith("\u6301\u3061\u6642\u9593\uFF1A")) {
			kifu.motijikan = buf.substring(5);
			return 1;
		}
		if (buf.startsWith("\u5834\u6240\uFF1A")) {
			kifu.site = buf.substring(3);
			return 1;
		}
		return 0;
	}

	int load_moti_sub(final Board rbd, final String buf) {
		if (buf.startsWith("\u5148\u624B\u306E\u6301\u99D2\uFF1A")
				|| buf.startsWith("\u4E0B\u624B\u306E\u6301\u99D2\uFF1A")) {
			moti_sub(rbd, buf.substring(6), 128);
			return 1;
		}
		if (buf.startsWith("\u5148\u624B\u6301\u99D2\uFF1A") || buf.startsWith("\u4E0B\u624B\u6301\u99D2\uFF1A")) {
			moti_sub(rbd, buf.substring(5), 128);
			return 1;
		}
		if (buf.startsWith("\u6301\u99D2\uFF1A")) {
			moti_sub(rbd, buf.substring(3), 128);
			return 1;
		}
		if (buf.startsWith("\u5F8C\u624B\u306E\u6301\u99D2\uFF1A")
				|| buf.startsWith("\u4E0A\u624B\u306E\u6301\u99D2\uFF1A")) {
			moti_sub(rbd, buf.substring(6), 0);
			return 1;
		}
		if (buf.startsWith("\u5F8C\u624B\u6301\u99D2\uFF1A") || buf.startsWith("\u4E0A\u624B\u6301\u99D2\uFF1A")) {
			moti_sub(rbd, buf.substring(5), 0);
			return 1;
		}
		return 0;
	}

	int load_move_sub(final KifuData kifu, final Board rbd, final Board start, final String buf) {
		String move = "";
		int r = 0;
		int n = 0;
		final StringTokenizer tokens = new StringTokenizer(buf);
		if (tokens.hasMoreTokens()) {
			try {
				final String ns = tokens.nextToken();
				n = Integer.parseInt(ns);
				r++;
			} catch (final NumberFormatException e) {
				final int i = 0;
				return i;
			}
		}
		if (tokens.hasMoreTokens()) {
			move = tokens.nextToken();
			r++;
		}
		if (r < 2) {
			return 0;
		}
		final int mn = kifu.move_no + 1;
		if (mn != n && mn + 1 != n) {
			return 0;
		}
		final int t = store_move(kifu, rbd, start, mn, move);
		if (t <= 0) {
			return t;
		}
		return t >= 11 ? 1 : 2;
	}

	int load_teai_sub(final Board rbd, final Board start, final String buf) {
		if (buf.startsWith("\u624B\u5408\u5272\uFF1A")) {
			rbd.teai = get_teai(buf.substring(4));
			if (rbd.teai > 0) {
				start.teban = rbd.teban = 0;
			}
			return 1;
		}
		if (buf.startsWith("\u5F8C\u624B\u756A") || buf.startsWith("\u4E0A\u624B\u756A")
				|| buf.startsWith("\u624B\u756A\uFF1A\u5F8C\u624B") || buf.startsWith("\u624B\u756A\uFF1A\u4E0A\u624B")) {
			start.teban = rbd.teban = 0;
		} else if (buf.startsWith("\u5148\u624B\u756A") || buf.startsWith("\u4E0B\u624B\u756A")
				|| buf.startsWith("\u624B\u756A\uFF1A\u5148\u624B") || buf.startsWith("\u624B\u756A\uFF1A\u4E0B\u624B")) {
			start.teban = rbd.teban = 128;
		}
		return 0;
	}

	int moti_sub(final Board rbd, final String moti, final int teban) {
		if (moti.startsWith("\u306A\u3057")) {
			return 0;
		}
		int p = 0;
		do {
			if (p >= moti.length()) {
				break;
			}
			final int c = moti.codePointAt(p);
			if (c == 44 || c == 32) {
				p++;
				continue;
			}
			if (c == 12288) {
				p++;
				continue;
			}
			final int k = get_koma(moti.substring(p));
			if ((k & 0xf) >= 8) {
				break;
			}
			p++;
			if (k == 0) {
				continue;
			}
			if (p >= moti.length()) {
				break;
			}
			int alabia1_flg = 0;
			int n = get_kan_num(moti.substring(p));
			if (n == 0) {
				n = get_zen_num(moti.substring(p));
				if (n == 1) {
					alabia1_flg = 1;
				}
				if (n <= 0) {
					n = 1;
				} else {
					p++;
				}
			} else {
				p++;
			}
			if (n == 10 || alabia1_flg != 0) {
				if (p >= moti.length()) {
					break;
				}
				int n2 = get_kan_num(moti.substring(p));
				if (n2 == 0) {
					n2 = get_zen_num(moti.substring(p));
					if (n2 >= 0) {
						n = 10;
					} else {
						n2 = 0;
					}
				}
				n += n2;
				p++;
			}
			int i = 0;
			while (i < n) {
				rbd.board[rbd.menup] = (short) (k | teban);
				rbd.menup++;
				i++;
			}
		} while (true);
		return 0;
	}

	int sasite_mark(final String s) {
		return s.indexOf("\u25B2") < 0 && s.indexOf("\u25B3") < 0 && s.indexOf("\u25BD") < 0 ? 0 : 1;
	}

	void sort_board(final KifuData kifu, final Board rbd, final Board start, final int teban) {
		rbd.setbd();
		kifu.copy_board_to_board(rbd, start);
		check_abnormal(kifu, rbd, rbd.teai, teban);
	}

	int store_move(final KifuData kifu, final Board rbd, final Board start, final int mn, final String move) {
		String move1 = move;
		while (move1.startsWith("\u3000")) {
			move1 = move1.substring(1);
		}
		final int mr = last_move_sub(move1);
		int r;
		short f;
		short t;
		short k;
		if (mr >= 0) {
			t = (short) mr;
			f = 1;
			k = 0;
			r = store_move_set(kifu, rbd, start, mn, f, t, k);
			return r;
		}
		final String tos = move1;
		if (tos.startsWith("\u5148") || tos.startsWith("\u5F8C")) {
			return 0;
		}
		String ks = "";
		int hankaku_flg = 0;
		int dou_flg;
		if (tos.startsWith("\u540C")) {
			final Kifup last = kifu.get_kifup_n(mn - 1);
			if (last == null) {
				t = 0;
			} else {
				t = (short) (last.t & 0x7f);
			}
			dou_flg = 1;
		} else {
			dou_flg = 0;
			int tx;
			int ty;
			if (tos.codePointAt(0) >= '1' && tos.codePointAt(0) <= '9') {
				hankaku_flg = 1;
				tx = tos.codePointAt(0) - 48;
				int hh;
				if (tos.codePointAt(1) >= '1' && tos.codePointAt(1) <= '9') {
					ty = tos.codePointAt(1) - 48;
					hh = 1;
				} else {
					ty = get_kan_num(tos.substring(1));
					hh = 0;
				}
				if (ty == 0) {
					return -1;
				}
				t = (short) (tx * 10 + ty);
				if (hh != 0) {
					ks = move1.substring(2);
				} else {
					ks = move1.substring(2);
				}
			} else {
				tx = get_zen_num(tos);
				ty = get_kan_num(tos.substring(1));
				if (ty == 0) {
					ty = get_zen_num(tos.substring(1));
				}
			}
			if (tx == 0 || ty == 0) {
				return -1;
			}
			t = (short) (tx * 10 + ty);
		}
		if (hankaku_flg == 0) {
			final String tos2 = tos.substring(1);
			if (dou_flg != 0 && tos2.startsWith("  ")) {
				ks = move1.substring(3);
			} else if (dou_flg != 0 && tos2.startsWith(" ")) {
				ks = move1.substring(2);
			} else if (dou_flg != 0 && !tos2.startsWith("\u3000")) {
				ks = move1.substring(1);
			} else {
				final String move4 = move1.substring(2);
				if (move4.startsWith("\u540C")) {
					ks = move1.substring(3);
				} else {
					ks = move1.substring(2);
				}
			}
		}
		k = get_koma(ks);
		if (k == 0) {
			return -1;
		}
		f = 1;
		final String ks2 = ks.substring(1);
		if (ks2.startsWith("\u6253")) {
			return store_move_set(kifu, rbd, start, mn, (short) 0, t, k);
		}
		String from_str;
		if (ks2.startsWith("\u6210")) {
			t |= 0x80;
			from_str = ks.substring(3);
		} else if (ks.startsWith("\u6210")) {
			from_str = ks.substring(3);
		} else {
			final String ks3 = ks.substring(1);
			if (ks3.startsWith("(")) {
				from_str = ks3.substring(1);
			} else {
				from_str = ks.substring(1);
			}
		}
		final short fx = (short) (from_str.codePointAt(0) - 48);
		final short fy = (short) (from_str.codePointAt(1) - 48);
		f = (short) (fx * 10 + fy);
		if (f < 11) {
			t &= 0x7f;
		}
		r = store_move_set(kifu, rbd, start, mn, f, t, k);
		return r;
	}

	int store_move_set(final KifuData kifu, final Board rbd, final Board start, final int mn, final short f,
			final short t, final short k) {
		final int r = kifu.store_kifu1(rbd, start, mn, f, t, k);
		if (r == -2) {
			return -2;
		}
		if (r == -1) {
			return -1;
		}
		if (t < 11) {
			return 2;
		}
		return t;
	}
}
