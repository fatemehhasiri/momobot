// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Board.java

package kifujl;

import java.io.Serializable;

// Referenced classes of package kifujl:
//            Move, KifuData, Kifup

public class Board implements Serializable {

	static final short BDN = 149;
	static final short FF = 255;
	static final short FU = 1;
	static final short GI = 4;
	static final int GOMAIOTI = 9;
	static final int GOMAIOTI_HIDARI = 10;
	static final short GOTE = 0;
	static final short GY = 8;
	static final int HATIMAIOTI = 12;
	static final short HI = 7;
	static final int HIKYOTI = 5;
	static final int HIOTI = 4;
	static final int HIRATE = 0;
	static final int JYUUMAIOTI = 13;
	static final short KA = 6;
	static final int KAOTI = 3;
	static final short KE = 3;
	static final short KI = 5;
	static final short KY = 2;
	static final int KYOTI = 1;
	static final short MOTI_ST = 111;
	static final int NIMAIOTI = 6;
	static short[][] pice_no = { { 0, 0 }, { 1, 18 }, { 19, 22 }, { 23, 26 }, { 27, 30 }, { 31, 34 }, { 35, 36 },
			{ 37, 38 }, { 39, 40 } };
	static final int ROKUMAIOTI = 11;
	static final int SANMAIOTI = 7;
	static final short SENTE = 128;
	static final short SFU = 129;
	static final short SGI = 132;
	static final short SGY = 136;
	static final short SHI = 135;
	static final short SKA = 134;
	static final short SKE = 131;
	static final short SKI = 133;
	static final short SKY = 130;
	static final int SONOTA = 14;
	static final int UKYOTI = 2;
	static final int YOMAIOTI = 8;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	short[] board;
	short menup;
	short[] piece;
	short[] square;
	final short[] ssqr = { 0, 17, 93, 97, 13, 27, 83, 87, 23, 37, 73, 77, 33, 47, 63, 67, 43, 57, 53, 19, 91, 99, 11,
			29, 81, 89, 21, 39, 71, 79, 31, 49, 61, 69, 41, 88, 22, 28, 82, 59, 51 };
	short teai;
	short teban;
	private final short[] sboard = { 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 2, 0, 1, 0, 0, 0, 129, 0,
			130, 255, 3, 6, 1, 0, 0, 0, 129, 135, 131, 255, 4, 0, 1, 0, 0, 0, 129, 0, 132, 255, 5, 0, 1, 0, 0, 0, 129,
			0, 133, 255, 8, 0, 1, 0, 0, 0, 129, 0, 136, 255, 5, 0, 1, 0, 0, 0, 129, 0, 133, 255, 4, 0, 1, 0, 0, 0, 129,
			0, 132, 255, 3, 7, 1, 0, 0, 0, 129, 134, 131, 255, 2, 0, 1, 0, 0, 0, 129, 0, 130 };

	public Board() {
		square = new short[41];
		piece = new short[149];
		board = new short[149];
		teban = 128;
	}

	public void hirate() {
		menup = 111;
		for (int s = 0; s <= 99; s++) {
			board[s] = sboard[s];
		}

		for (int s = 100; s <= 110; s++) {
			board[s] = 255;
		}

		for (int s = 0; s < 149; s++) {
			piece[s] = 0;
		}

		for (short p = 0; p <= 40; p++) {
			final int s = square[p] = ssqr[p];
			piece[s] = p;
		}

		teban = 128;
	}

	void back_move(final KifuData kifu) {
		if (kifu.move_no <= 0) {
			return;
		}
		final int n = kifu.move_no;
		final Kifup kp = kifu.get_kifup_n(n);
		if (kp == null) {
			System.out.println("back_move error  kifup=NULL move_no=" + kifu.move_no);
			return;
		}
		back_movel(kp.f, kp.t, kp.koma, kp.dead);
		final Kifup oya = kifu.find_oya(kifu.current_p);
		kifu.current_p = oya;
		kifu.move_no--;
		teban = (short) (128 - teban);
	}

	int back_movel(final short p_f, final short p_t, final short pk, final short dead) {
		int err = 0;
		if (p_t <= 10) {
			return 0;
		}
		short k = pk;
		short t = p_t;
		if ((t & 0x80) != 0) {
			t &= 0x7f;
			k &= 0x87;
		}
		short f = p_f;
		if (f < 100) {
			board[f] = k;
		}
		board[t] = dead;
		short pt = 0;
		if (dead != 0) {
			int dead1;
			if ((dead & 0xf) == 8) {
				dead1 = dead ^ 0x80;
			} else {
				dead1 = dead & 0x87 ^ 0x80;
			}
			int find = 0;
			int i = menup - 1;
			do {
				if (i < 111) {
					break;
				}
				if (board[i] == dead1) {
					board[i] = 0;
					find = 1;
					break;
				}
				i--;
			} while (true);
			pt = piece[i];
			piece[i] = 0;
			if (find == 0) {
				err = 1;
			}
		}
		if (f > 99) {
			if (f >= menup || board[f] != 0) {
				int find = 0;
				f = 111;
				do {
					if (f >= menup) {
						break;
					}
					if (board[f] == 0) {
						find = 1;
						break;
					}
					f++;
				} while (true);
				if (find == 0) {
					f = menup;
					menup++;
				}
			}
			board[f] = k;
		}
		final int pf = piece[f] = piece[t];
		piece[t] = pt;
		square[pf] = f;
		if (dead != 0) {
			square[pt] = t;
		}
		return err == 0 ? 0 : -1;
	}

	void clr_bd_data() {
		for (int y = 1; y <= 9; y++) {
			for (int x = 1; x <= 9; x++) {
				board[x * 10 + y] = 0;
			}

		}

		menup = 111;
	}

	void erak(final int s) {
		final int n = piece[s];
		square[n] = 0;
		piece[s] = board[s] = 0;
	}

	void exec_move_l(final Move move) {
		if (move.t <= 10) {
			return;
		}
		short t = move.t;
		if ((move.t & 0x80) != 0) {
			t &= 0x7f;
			board[move.f] |= 8;
		}
		if (board[t] > 0) {
			move_data_to_moti(t);
		}
		move_data(move.f, t);
	}

	void getk(final int k) {
		final short ps = pice_no[k & 7][0];
		final short pe = pice_no[k & 7][1];
		short p = ps;
		short s;
		for (s = 11; p <= pe && s < menup; s++) {
			if ((board[s] & 7) == k && board[s] != 255) {
				square[p] = s;
				piece[s] = p;
				p++;
			}
		}

		for (; p <= pe; p++) {
			square[p] = 0;
		}

		for (; s < menup; s++) {
			if (board[s] != 255 && (board[s] & 7) == k) {
				board[s] = 0;
			}
		}

		for (s = (short) (menup - 1); s >= 111 && board[s] == 0; s--) {
			menup--;
		}

	}

	void init_teai_board(final int lteai) {
		if (lteai >= 14) {
			return;
		}
		hirate();
		if (lteai == 0) {
			teban = 128;
		} else {
			teban = 0;
		}
		if (lteai == 1) {
			erak(11);
		} else if (lteai == 2) {
			erak(91);
		} else if (lteai == 3) {
			erak(22);
		} else if (lteai == 4) {
			erak(82);
		} else if (lteai == 5) {
			erak(82);
			erak(11);
		} else if (lteai >= 6) {
			erak(22);
			erak(82);
			if (lteai >= 7) {
				erak(91);
				if (lteai >= 6) {
					erak(11);
					if (lteai == 9) {
						erak(81);
					} else if (lteai == 10) {
						erak(21);
					} else if (lteai >= 11) {
						erak(21);
						erak(81);
						if (lteai >= 12) {
							erak(31);
							erak(71);
							if (lteai == 13) {
								erak(41);
								erak(61);
							}
						}
					}
				}
			}
		}
	}

	void jump_back(final KifuData kifu, final int jn) {
		while (kifu.move_no > jn && kifu.move_no > 0) {
			back_move(kifu);
		}
	}

	void move_data(final short f, final short t) {
		if (board[f] == 0) {
			System.out.println("***move_data error k=0 f=" + f);
		}
		final short pf = piece[f];
		square[pf] = t;
		board[t] = board[f];
		board[f] = 0;
		piece[f] = 0;
		piece[t] = pf;
	}

	void move_data_to_moti(final short t0) {
		final short ps = piece[t0];
		piece[t0] = 0;
		final short k = board[t0];
		int find = 0;
		short ms = 111;
		do {
			if (ms >= menup) {
				break;
			}
			if (board[ms] == 0) {
				find = 1;
				break;
			}
			ms++;
		} while (true);
		if (find == 0) {
			ms = menup++;
		}
		square[ps] = ms;
		piece[ms] = ps;
		if ((k & 0xf) == 8) {
			board[ms] = (short) (k ^ 0x80);
		} else {
			board[ms] = (short) ((k ^ 0x80) & 0xf7);
		}
		board[t0] = 0;
	}

	void setbd() {
		final int oti[] = new int[9];
		for (int i = 0; i < 149; i++) {
			piece[i] = 0;
		}

		for (int k = 1; k <= 7; k++) {
			getk(k);
		}

		square[39] = 0;
		int s = 11;
		do {
			if (s > 99) {
				break;
			}
			if (board[s] == 136) {
				square[39] = (short) s;
				piece[s] = 39;
				break;
			}
			s++;
		} while (true);
		square[40] = 0;
		s = 11;
		do {
			if (s > 99) {
				break;
			}
			if (board[s] == 8) {
				square[40] = (short) s;
				piece[s] = 40;
				break;
			}
			s++;
		} while (true);
		for (int i = 0; i <= 8; i++) {
			oti[i] = 0;
		}

		for (int p = 1; p <= 40; p++) {
			s = square[p];
			if (s != 0) {
				continue;
			}
			int k;
			for (k = 1; k <= 8 && (p < pice_no[k][0] || p > pice_no[k][1]); k++) {
				// guruguru
			}
			oti[k]++;
			oti[0]++;
		}

		if (oti[0] == 0 || oti[0] == 1 && oti[8] == 1) {
			teai = 0;
		} else if (oti[0] == 1) {
			if (oti[2] == 1) {
				if (teai == 2 && board[11] == 2) {
					teai = 2;
				} else {
					teai = 1;
				}
			} else if (oti[6] == 1) {
				teai = 3;
			} else if (oti[7] == 1) {
				teai = 4;
			} else {
				teai = 14;
			}
		} else if (oti[0] == 2) {
			if (oti[2] == 1 && oti[7] == 1) {
				teai = 5;
			} else if (oti[7] == 1 && oti[6] == 1) {
				teai = 6;
			} else {
				teai = 14;
			}
		} else if (oti[0] == 3) {
			if (oti[7] == 1 && oti[6] == 1 && oti[2] == 1) {
				teai = 7;
			} else {
				teai = 14;
			}
		} else if (oti[0] == 4) {
			if (oti[2] == 2 && oti[7] == 1 && oti[6] == 1) {
				teai = 8;
			} else {
				teai = 14;
			}
		} else if (oti[0] == 5) {
			if (oti[2] == 2 && oti[7] == 1 && oti[6] == 1 && oti[3] == 1) {
				if (teai == 10 && board[81] == 3) {
					teai = 10;
				} else {
					teai = 9;
				}
			} else {
				teai = 14;
			}
		} else if (oti[0] == 6) {
			if (oti[2] == 2 && oti[3] == 2 && oti[7] == 1 && oti[6] == 1) {
				teai = 11;
			} else {
				teai = 14;
			}
		} else if (oti[0] == 8) {
			if (oti[2] == 2 && oti[3] == 2 && oti[4] == 2 && oti[7] == 1 && oti[6] == 1) {
				teai = 12;
			} else {
				teai = 14;
			}
		} else if (oti[0] == 10) {
			if (oti[2] == 2 && oti[3] == 2 && oti[4] == 2 && oti[5] == 2 && oti[7] == 1 && oti[6] == 1) {
				teai = 13;
			} else {
				teai = 14;
			}
		} else {
			teai = 14;
		}
	}

}
