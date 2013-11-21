package kifujl;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Applet1 extends Applet implements Callable<Void> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int EDGE_X = 12;
	private static final int EDGE_Y = 12;
	private static final boolean isStandalone = false;
	private static final int koma_size_x = 25;
	private static final int koma_size_y = 26;
	private static final int LEFT_GAP = 4;
	private static final int MOTI_TEXT = 0;

	private static final long replay_speed = 800;
	private static final int RIGHT_GAP = 4;
	private static final int teban_disp_flg = 1;
	private int auto_update_flg;
	private final Button back = new Button();
	private final Button back_rep = new Button();
	private final Button backBra = new Button();
	private Drabod bd;
	private int bdinv_flg;
	private int comment_line = 4;
	private final TextArea commentArea = new TextArea("", 15, 5, 1);
	private int del_flg;
	private KifuFile file;
	private final int FU_PITCH;
	private Future<Void> future;
	private final Button go_rep = new Button();
	private int go_rep_flg;
	private int go_rep_tesuu;
	private final Button hanten = new Button();
	private final Button help = new Button();
	private boolean help_flg;
	private URL help_url;
	private final Button henka = new Button();
	private int henka_no_flg;
	private final Choice HenkaChoice = new Choice();
	private Image im2;
	private final Info info = new Info();
	private final KifuData kifu = new KifuData();
	private final Button kifu_down = new Button();
	private URL kifu_url;
	private final List KifuList = new List();
	private Image komadai_image;
	private Image[][] komaImg;
	private int komaoto_flg = 1;
	private AudioClip komaotoClip;
	private final Button last = new Button();
	private int last_color_b = 100;
	private int last_color_g = 200;
	private int last_color_r = 100;
	private int last_move_no;
	private boolean LAST_SQ_DISP = true;
	private boolean MOKUME = true;
	private final int MOTI_SIZE_X;
	private final int MOTI_SIZE_Y;
	private final Button next = new Button();
	private int no_henka_flg = 2;
	private int no_kifu_flg;
	private Graphics offscreenG;
	private Image offscreenImg;
	private Board rbd;
	private boolean replay_last_flg;
	private int replay_stop_flg;
	private ExecutorService runner;
	private final int SQ_SIZE_X;
	private final int SQ_SIZE_Y;
	private final Board start = new Board();
	private int start_tesuu;
	private final Button stop = new Button();
	private final Button top = new Button();

	private boolean tume_flg;

	public Applet1() {
		super();
		SQ_SIZE_X = koma_size_x + 3;
		SQ_SIZE_Y = koma_size_y + 3;
		final int MOTI_EDGE_X = 2;
		MOTI_SIZE_X = 3 * SQ_SIZE_X + 2 + 2 * MOTI_EDGE_X;

		MOTI_SIZE_Y = 4 * SQ_SIZE_Y + EDGE_Y + MOTI_TEXT;
		FU_PITCH = koma_size_x - 3;
	}

	public void back_mouseClicked() {
		BackMove(1, 1);
	}

	public void back_rep_mouseClicked() {
		go_rep_flg = 2;
		stop.setEnabled(true);
	}

	public void backBra_mouseClicked() {
		do {
			if (kifu.move_no == 0) {
				break;
			}
			BackMove(4, 0);
		} while (kifu.is_bunki_n(kifu.move_no + 1) == 0);
		KifuSetData();
		ViewUpdate();
	}

	@Override
	public Void call() throws IOException {
		int sleep_n = 0;
		do {
			if (go_rep_flg == 1) {
				if (replay_stop_flg != 0 || kifu.can_susumeru(rbd) == 0) {
					replay_stop_flg = 0;
					go_rep_flg = 0;
					continue;
				}
				final int r = ExecMove(0);
				if (r != 0) {
					go_rep_flg = 0;
					stop.setEnabled(false);
				}
			} else if (go_rep_flg == 2) {
				if (replay_stop_flg != 0) {
					replay_stop_flg = 0;
					go_rep_flg = 0;
					continue;
				}
				final int r = BackMove(1, 1);
				if (r != 0) {
					go_rep_flg = 0;
					stop.setEnabled(false);
				}
			}
			try {
				if (go_rep_flg == 0) {
					Thread.sleep(500L);
					sleep_n++;
					if (auto_update_flg == 1) {
						if (sleep_n >= 60) {
							auto_update();
							sleep_n = 0;
						}
					} else if (auto_update_flg == 2) {
						if (sleep_n >= 120) {
							auto_update();
							sleep_n = 0;
						}
					} else if (auto_update_flg == 3 && sleep_n >= 360) {
						auto_update();
						sleep_n = 0;
					}
				} else {
					final String comment = kifu.get_kifu_commentp(kifu.move_no);
					if (comment == null) {
						Thread.sleep(replay_speed);
					} else {
						Thread.sleep(replay_speed * 2);
					}
				}
			} catch (final InterruptedException interruptedexception) {
				Thread.currentThread().interrupt();
			}
		} while (true);
	}

	public void DrawLastMark(final Graphics g) {
		if (kifu.move_no > 0) {
			final Move llast = kifu.get_last_move();
			if (llast.t < 11) {
				return;
			}
			DrawLastPiece(g, llast.t & 0x7f);
		}
	}

	public void DrawLastPiece(final Graphics g, final int s) {
		if (go_rep_flg == 1) {
			if (kifu.move_no < go_rep_tesuu) {
				System.out.println(String.valueOf(new StringBuffer("***DrawLastPiece back move_no=")
						.append(kifu.move_no).append("s=").append(s)));
				return;
			}
			go_rep_tesuu = kifu.move_no;
		}
		g.setColor(new Color(last_color_r, last_color_g, last_color_b));
		int x = s / 10;
		int y = s % 10;
		if (bdinv_flg != 0) {
			x = 10 - x;
			y = 10 - y;
		}
		final int x1 = bd.BD_ORG_X + (9 - x) * SQ_SIZE_X + 2;
		final int y1 = bd.BD_ORG_Y + (y - 1) * SQ_SIZE_Y + 2;
		final int right = x1 + SQ_SIZE_X - 3;
		final int bottom = y1 + SQ_SIZE_Y - 3;
		if (LAST_SQ_DISP) {
			g.fillRect(x1 - 1, y1 - 1, right - x1 + 2, bottom - y1 + 2);
			bd.stars(g);
		}
		DrawPiece(g, s);
	}

	public void DrawPiece(final Graphics g, final int s) {
		if (s <= 0 || s >= 149) {
			System.out.println("***DrawPiece() error s = " + s);
		}
		final int k = rbd.board[s];
		if (k == 0) {
			return;
		}
		if (s >= 11 && s <= 99) {
			DrawPiece(g, s, k);
		}
	}

	public void DrawPiece(final Graphics g, final int s, final int p_k) {
		int x = s / 10;
		int y = s % 10;
		if (bdinv_flg != 0) {
			x = 10 - x;
			y = 10 - y;
		}
		final int px = bd.BD_ORG_X + (9 - x) * bd.SQ_SIZE_X + 2;
		final int py = bd.BD_ORG_Y + (y - 1) * bd.SQ_SIZE_Y + 2;
		int d;
		int k = p_k;
		if ((k & 0x80) == 0) {
			d = 1;
		} else {
			d = 0;
		}
		if (bdinv_flg != 0) {
			d = 1 - d;
		}
		if (!tume_flg && k == 8) {
			k = 0;
		}
		g.drawImage(komaImg[d][k & 0xf], px, py, this);
	}

	public void DrawPiece(final Graphics g, final Point p, final int p_k) {
		int k = p_k;
		int d = (k & 0x80) == 0 ? 1 : 0;
		if (bdinv_flg != 0) {
			d = 1 - d;
		}
		if (!tume_flg && k == 8) {
			k = 0;
		}
		g.drawImage(komaImg[d][k & 0xf], p.x, p.y, this);
	}

	@Override
	public String getAppletInfo() {
		return "Kifu for Java (C) 2001-2005 Kakinoki Yoshikazu";
	}

	public String getParameter(final String key, final String def) {
		return isStandalone ? System.getProperty(key, def) : getParameter(key) == null ? def : getParameter(key);
	}

	@Override
	public String[][] getParameterInfo() {
		return new String[0][0];
	}

	@Override
	public void init() {
		jbInit();

		System.out.println("Kifu for Java Light V2.33 2008.3.1 Copyright (C) Kakinoki Yoshikazu");
		komaImg = new Image[2][16];
		final MediaTracker mt = new MediaTracker(this);
		final Image im = getImage(getCodeBase(), "komas.gif");
		mt.addImage(im, 0);
		try {
			mt.waitForID(0);
		} catch (final InterruptedException e1) {
			e1.printStackTrace();
		}
		for (int d = 0; d <= 1; d++) {
			for (int k = 0; k <= 15; k++) {
				if (k == 13) {
					continue;
				}
				int x;
				if (k == 8 || k == 0) {
					x = 0;
				} else {
					x = (8 - (k & 7)) * koma_size_x;
				}
				int y;
				if (d == 0) {
					if (k <= 7) {
						y = 0;
					} else {
						y = koma_size_y;
					}
				} else if (k <= 7) {
					y = koma_size_y * 2;
				} else {
					y = koma_size_y * 3;
				}
				final ImageFilter filter = new CropImageFilter(x, y, koma_size_x, koma_size_y);
				final FilteredImageSource fis = new FilteredImageSource(im.getSource(), filter);
				komaImg[d][k] = createImage(fis);
				if (komaImg[d][k] == null) {
					System.out.println("***komaImg=null");
				}
			}

		}

		final String NoMokumePara = getParameter("NO_MOKUME");
		if (NoMokumePara != null) {
			MOKUME = false;
		}
		final String NoLastPara = getParameter("NO_LAST");
		if (NoLastPara != null) {
			LAST_SQ_DISP = false;
		}
		final String StartTesuuPara = getParameter("START_TESUU");
		if (StartTesuuPara != null) {
			if ("LAST".equals(StartTesuuPara)) {
				replay_last_flg = true;
			} else {
				start_tesuu = Integer.parseInt(StartTesuuPara);
			}
		}
		final String ReplayLastPara = getParameter("REPLAY_LAST");
		if (ReplayLastPara != null) {
			replay_last_flg = true;
		}
		final String LastColorPara = getParameter("LAST_COLOR");
		if (LastColorPara != null) {
			final StringTokenizer tokens = new StringTokenizer(LastColorPara);
			if (tokens.hasMoreTokens()) {
				try {
					final String rs = tokens.nextToken();
					last_color_r = Integer.parseInt(rs);
				} catch (final NumberFormatException nfe) {
					throw new IllegalArgumentException("Wrong parameter: LAST_COLOR");
				}
			}
			if (tokens.hasMoreTokens()) {
				try {
					final String gs = tokens.nextToken();
					last_color_g = Integer.parseInt(gs);
				} catch (final NumberFormatException nfe) {
					throw new IllegalArgumentException("Wrong parameter: LAST_COLOR");
				}
			}
			if (tokens.hasMoreTokens()) {
				try {
					final String gs = tokens.nextToken();
					last_color_b = Integer.parseInt(gs);
				} catch (final NumberFormatException nfe) {
					throw new IllegalArgumentException("Wrong parameter: LAST_COLOR");
				}
			}
		}
		final String NoKifuPara = getParameter("NO_KIFU");
		if (NoKifuPara != null) {
			no_kifu_flg = Integer.parseInt(NoKifuPara);
			if (no_kifu_flg > 0) {
				DelKifuButtonAnd();
			}
		}
		final String NoKifuSavePara = getParameter("NO_KIFU_SAVE");
		if (NoKifuSavePara != null) {
			DelKifuButton();
		}
		final String NoHenkaPara = getParameter("NO_HENKA");
		if (NoHenkaPara != null) {
			no_henka_flg = Integer.parseInt(NoHenkaPara);
			if (no_henka_flg == 1) {
				delHenkaButton();
			}
		}
		final String NoCommentPara = getParameter("NO_COMMENT");
		if (NoCommentPara != null) {
			delCommentArea();
		}
		final String CommentLinePara = getParameter("COMMENT_LINE");
		if (CommentLinePara != null) {
			comment_line = Integer.parseInt(CommentLinePara);
			if (comment_line == 0) {
				delCommentArea();
			} else {
				int h;
				if (comment_line < 5) {
					h = 14 * comment_line;
				} else if (comment_line == 5) {
					h = 66;
				} else {
					h = 13 * comment_line;
				}
				commentArea.setBounds(new Rectangle(4, 322, 469, h));
			}
		}
		final String AutoUpdatePara = getParameter("AUTO_UPDATE");
		if (AutoUpdatePara != null) {
			auto_update_flg = Integer.parseInt(AutoUpdatePara);
		}
		if (MOKUME) {
			final MediaTracker mt2 = new MediaTracker(this);
			try {
				im2 = getImage(getCodeBase(), "bans.jpg");
				if (im2 == null) {
					System.out.println("*** im2=null");
				}
				mt2.addImage(im2, 0);
				mt2.waitForID(0);
			} catch (final Exception e) {
				MOKUME = false;
				System.out.println("***WARN 105: cannot access ban.jpg");
				e.printStackTrace();
			}
		}
		if (MOKUME) {
			komadai_image = getImage(getCodeBase(), "komadais.jpg");
			if (komadai_image == null) {
				MOKUME = false;
				System.out.println("***WARN 106: cannot access komadai.jpg");
			}
		}
		komaotoClip = getAudioClip(getCodeBase(), "komaoto.au");
		if (komaotoClip == null) {
			System.out.println("***WARN 102: KOMAOTO is null");
			komaoto_flg = 0;
		}
		rbd = new Board();
		rbd.hirate();
		start.hirate();
		bd = new Drabod();
		final String kifuPara = getParameter("KIFU");
		if (kifuPara == null) {
			System.out.println("***ERROR 101: KIFU is null");
		}
		try {
			kifu_url = new URL(getCodeBase() + getParameter("KIFU"));
		} catch (final MalformedURLException e) {
			throw new InternalError("*** kifu_url error");
		}
		final String HelpPara = getParameter("HELP");
		if (HelpPara != null) {
			help_flg = true;
			try {
				help_url = new URL(getCodeBase() + HelpPara);
			} catch (final MalformedURLException e) {
				throw new InternalError("*** help_url error");
			}
		}
		file = new KifuFile();
		showStatus("Loading kifu...");
		try {
			file.load(kifu, rbd, start, kifu_url);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (rbd.square[39] == 0) {
			tume_flg = true;
		}
		showStatus("");
		if (kifu.is_henka_flg == 0 && no_henka_flg == 2) {
			delHenkaButton();
		}
		KifuSetData();
		if (auto_update_flg != 0 && kifu.is_syuukyoku() != 0) {
			System.out.println("\u7D42\u5C40\u6642\u306E\u7279\u6B8A\u51E6\u7406");
			auto_update_flg = 0;
			replay_last_flg = false;
			start_tesuu = 0;
		}
		if (replay_last_flg) {
			ReplayLast();
		} else if (start_tesuu > 0) {
			Jump(start_tesuu);
		}
		last_move_no = kifu.move_no;
		disp_comment();
		if (!help_flg) {
			help.setVisible(false);
		}
		ButtonUpdate();
		stop.setEnabled(false);
		requestFocus();
	}

	@Override
	public void paint(final Graphics g) {
		if (offscreenG == null) {
			DrawBoard(g);
			info.DispAllInfo(g, kifu, rbd, bd, no_henka_flg);
		} else {
			DrawBoard(offscreenG);
			info.DispAllInfo(offscreenG, kifu, rbd, bd, no_henka_flg);
			g.drawImage(offscreenImg, 0, 0, this);
		}
	}

	@Override
	public void start() {
		runner = Executors.newSingleThreadExecutor();
		future = runner.submit(this);
		if (offscreenImg == null) {
			offscreenImg = createImage(getSize().width, getSize().height);
			if (offscreenImg == null) {
				System.out.println("***offscreenImg=null");
			} else {
				offscreenG = offscreenImg.getGraphics();
			}
		}
	}

	@Override
	public void stop() {
		if (runner != null) {
			runner.shutdownNow();
		}
		if (future != null) {
			try {
				future.get(1, TimeUnit.SECONDS);
			} catch (final TimeoutException te) {
				// OK
			} catch (final ExecutionException e) {
				e.getCause().printStackTrace();
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void update(final Graphics g) {
		paint(g);
	}

	void auto_update() throws IOException {
		final int r = load_kifu();
		if (r == 1) {
			return;
		}
		System.out.println("\u81EA\u52D5\u66F4\u65B0 " + kifu.move_no);
		if (kifu.move_no > last_move_no) {
			komaoto();
		}
		last_move_no = kifu.move_no;
	}

	void ButtonUpdate() {
		if (kifu.is_henka_flg == 0) {
			henka.setEnabled(false);
			backBra.setEnabled(false);
		}
		if (kifu.move_no <= 0) {
			back.setEnabled(false);
			back_rep.setEnabled(false);
			top.setEnabled(false);
			backBra.setEnabled(false);
		} else {
			back.setEnabled(true);
			back_rep.setEnabled(true);
			top.setEnabled(true);
			if (kifu.is_henka_flg == 1) {
				backBra.setEnabled(true);
			}
		}
		if (kifu.can_susumeru(rbd) == 0) {
			next.setEnabled(false);
			go_rep.setEnabled(false);
			last.setEnabled(false);
			if (kifu.is_henka_flg == 1) {
				henka.setEnabled(false);
			}
		} else {
			next.setEnabled(true);
			go_rep.setEnabled(true);
			last.setEnabled(true);
			if (kifu.is_henka_flg == 1) {
				henka.setEnabled(true);
			}
		}
		HenkaSetData();
	}

	void clear_rect_mokume(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		final int x = x1 - (bd.BD_ORG_X - bd.EDGE_X);
		final int y = y1 - (bd.BD_ORG_Y - bd.EDGE_Y);
		final int w = x2 - x1;
		final int h = y2 - y1;
		final ImageFilter filter = new CropImageFilter(x, y, w, h);
		if (im2 == null) {
			throw new IllegalStateException("***im2=null");
		}
		final FilteredImageSource fis = new FilteredImageSource(im2.getSource(), filter);
		final Image mokume = createImage(fis);
		if (mokume == null) {
			System.out.println("***mokume=null");
		}
		g.drawImage(mokume, x1, y1, this);
		repaint(x1, y1, w, h);
	}

	void clear_sq(final Graphics g, final int s) {
		if (MOKUME) {
			clear_sq_mokume(g, s);
		} else {
			bd.clear_sq(g, s, bdinv_flg);
		}
	}

	void clear_sq_mokume(final Graphics g, final int s) {
		int x = s / 10;
		int y = s % 10;
		if (bdinv_flg != 0) {
			x = 10 - x;
			y = 10 - y;
		}
		final int y1 = bd.BD_ORG_Y + (y - 1) * SQ_SIZE_Y + 2;
		final int x1 = bd.BD_ORG_X + (9 - x) * SQ_SIZE_X + 2;
		final Point p = new Point();
		p.x = x1;
		p.y = y1;
		clear_xy_mokume(g, p);
	}

	void clear_xy_mokume(final Graphics g, final Point p) {
		final int left = p.x - 1;
		final int right = p.x + SQ_SIZE_X - 2;
		final int bottom = p.y + SQ_SIZE_Y - 2;
		clear_rect_mokume(g, left, p.y - 1, right, bottom);
	}

	void delCommentArea() {
		commentArea.setVisible(false);
	}

	void delHenkaButton() {
		henka.setVisible(false);
		backBra.setVisible(false);
		HenkaChoice.setVisible(false);
		KifuList.setBounds(new Rectangle(4, 167, 93, 128));
	}

	void DelKifuButton() {
		kifu_down.setVisible(false);
	}

	void DelKifuButtonAnd() {
		HenkaChoice.setVisible(false);
		KifuList.setVisible(false);
		if (no_kifu_flg == 1) {
			kifu_down.setLabel("\u68CB\u8B5C\u8868\u793A");
		} else {
			kifu_down.setVisible(false);
		}
		if (no_kifu_flg != 3) {
			henka.setVisible(false);
			backBra.setVisible(false);
			back.setVisible(false);
			back_rep.setVisible(false);
			top.setVisible(false);
			next.setVisible(false);
			go_rep.setVisible(false);
			last.setVisible(false);
			stop.setVisible(false);
		}
	}

	void disp_comment() {
		final String comment = kifu.get_kifu_commentp(kifu.move_no);
		if (comment == null) {
			commentArea.setText("");
		} else {
			commentArea.setText(comment);
		}
	}

	void DispHenkaButton() {
		henka.setVisible(true);
		backBra.setVisible(true);
		HenkaChoice.setVisible(true);
		KifuList.setBounds(new Rectangle(4, 167, 93, 100));
	}

	void DispKifuButton() {
		HenkaChoice.setVisible(true);
		KifuList.setVisible(true);
		kifu_down.setVisible(true);
		henka.setVisible(true);
		backBra.setVisible(true);
		back.setVisible(true);
		back_rep.setVisible(true);
		top.setVisible(true);
		next.setVisible(true);
		go_rep.setVisible(true);
		last.setVisible(true);
		stop.setVisible(true);
	}

	void DrawBoard(final Graphics g) {
		final int x1 = bd.BD_ORG_X - EDGE_X;
		final int y1 = bd.BD_ORG_Y - EDGE_Y;
		final int x2 = bd.BD_ORG_X + 9 * SQ_SIZE_X + EDGE_X;
		final int y2 = bd.BD_ORG_Y + 9 * SQ_SIZE_Y + EDGE_Y;
		if (MOKUME) {
			g.drawImage(im2, x1, y1, this);
		} else {
			bd.clear_rect(g, x1, y1, x2, y2);
		}
		bd.shadow_box2(g, x1, y1, x2, y2);
		g.setColor(Color.black);
		for (int i = 0; i <= 9; i++) {
			final int y = bd.BD_ORG_Y + bd.SQ_SIZE_Y * i;
			g.drawLine(bd.BD_ORG_X, y, bd.BD_ORG_X + bd.SQ_SIZE_X * 9, y);
			final int x = bd.BD_ORG_X + SQ_SIZE_X * i;
			g.drawLine(x, bd.BD_ORG_Y, x, bd.BD_ORG_Y + bd.SQ_SIZE_Y * 9);
		}

		bd.stars(g);
		for (int x = 1; x <= 9; x++) {
			for (int y = 1; y <= 9; y++) {
				final int s = x * 10 + y;
				DrawPiece(g, s);
			}

		}

		DrawKomadai(g, 128);
		DrawKomadai(g, 0);
		bd.draw_coord(g, bdinv_flg);
		DrawLastMark(g);
	}

	void DrawKomadai(final Graphics g, final int teban) {
		int r_teban;
		if (bdinv_flg == 0) {
			r_teban = 128;
		} else {
			r_teban = 0;
		}
		final int size_x = bd.MOTI_SIZE_X;
		final int size_y = bd.MOTI_SIZE_Y;
		int ox;
		int oy;
		if (teban == r_teban) {
			ox = bd.BD_ORG_X + 9 * bd.SQ_SIZE_X + bd.EDGE_X + bd.RIGHT_GAP;
			oy = bd.BD_ORG_Y + 5 * bd.SQ_SIZE_Y - MOTI_TEXT;
		} else {
			ox = bd.BD_ORG_X - bd.EDGE_X - bd.LEFT_GAP - size_x;
			oy = bd.BD_ORG_Y - bd.EDGE_Y;
		}
		final int l_x2 = ox + size_x;
		final int bottom = oy + size_y;
		if (MOKUME) {
			komadai_mokume(g, ox, oy, l_x2, bottom);
		} else {
			bd.clear_hako_rect(g, ox, oy, l_x2, bottom);
		}
		bd.shadow_box2(g, ox, oy, l_x2, bottom);
		DrawKomadaiTeban(g, teban);
		for (int k = 7; k >= 1; k--) {
			final int k1 = k | teban;
			int n = 0;
			for (int s = 111; s < rbd.menup; s++) {
				if (rbd.board[s] == k1) {
					n++;
				}
			}

			if (n != 0) {
				DrawMotiN(g, k1, 0);
			}
		}

	}

	void DrawKomadaiTeban(final Graphics g, final int teban) {
		int enha;
		if (teban == rbd.teban) {
			enha = 1;
		} else {
			enha = 0;
		}
		int r_teban;
		if (bdinv_flg == 0) {
			r_teban = 128;
		} else {
			r_teban = 0;
		}
		final int size_x = MOTI_SIZE_X;
		final int size_y = MOTI_SIZE_Y;
		int ox;
		int oy;
		if (teban == r_teban) {
			ox = bd.BD_ORG_X + 9 * SQ_SIZE_X + EDGE_X + RIGHT_GAP;
			oy = bd.BD_ORG_Y + 5 * SQ_SIZE_Y - MOTI_TEXT;
		} else {
			ox = bd.BD_ORG_X - EDGE_X - LEFT_GAP - MOTI_SIZE_X;
			oy = bd.BD_ORG_Y - EDGE_Y;
		}
		final Font f = new Font("Dialog", 0, 12);
		g.setFont(f);
		int tx = ox + size_x / 2 - 16;
		int ty;
		if (teban == r_teban) {
			ty = oy + 1;
		} else {
			ty = oy + size_y - 13;
		}
		final int w = 27;
		if (enha == 0) {
			bd.clear_hako_rect(g, tx, ty, tx + w, ty + 13);
		} else {
			g.setColor(new Color(255, 255, 200));
			g.fillRect(tx, ty, w, 13);
		}
		g.setColor(Color.black);
		tx += 2;
		ty += 11;
		if (teban == 0) {
			if (rbd.teai == 0) {
				g.drawString("\u5F8C\u624B", tx, ty);
			} else {
				g.drawString("\u4E0A\u624B", tx, ty);
			}
		} else {
			if (rbd.teai == 0) {
				g.drawString("\u5148\u624B", tx, ty);
			} else {
				g.drawString("\u4E0B\u624B", tx, ty);
			}
		}
	}

	void DrawMotiN(final Graphics g, final int k, final int mode) {
		final int teban = k & 0x80;
		int n = 0;
		for (int s = 111; s < rbd.menup; s++) {
			if (rbd.board[s] == k) {
				n++;
			}
		}

		if (mode == 2) {
			n--;
		}
		if (n <= 0) {
			return;
		}
		final Point p = bd.GetMotiXy(rbd, teban, k, bdinv_flg);
		final int x = p.x;
		int pitch;
		if (n <= 1) {
			pitch = 0;
		} else if ((k & 0xf) == 1) {
			if (n <= 3) {
				pitch = FU_PITCH;
			} else {
				pitch = (bd.MOTI_SQ_SIZE_X * 2 - bd.koma_size_x - 4 + bd.MOTI_OVERLAP) / (n - 1);
			}
		} else {
			pitch = (bd.MOTI_SQ_SIZE_X - bd.koma_size_x - 4 + bd.MOTI_OVERLAP) / (n - 1);
		}
		for (int i = n - 1; i >= 0; i--) {
			if (mode == 1 && (i < 1 || ((k & 0xf) == 1 ? n == 4 ? i <= 2 : i == 1 : n < 14 ? i <= 2 : i <= 4))) {
				continue;
			}
			p.x = x + i * pitch;
			DrawPiece(g, p, k);
		}

	}

	void ExcmovDisp(final Graphics g, final int f, final int t, final int dead, final int teban) {
		if (f < 100) {
			clear_sq(g, f);
		}
		if (dead != 0) {
			clear_sq(g, t);
		}
		DrawLastPiece(g, t);
		if (f > 99 || dead != 0) {
			DrawKomadai(g, 128 - teban);
		}
	}

	int ExecMove(final int mode) {
		final Move move = kifu.get_next_move(rbd);
		return ExecMove(mode, move);
	}

	int ExecMove(final int mode, final Move move) {
		if (move.t < 11) {
			return 1;
		}
		final Graphics g = getGraphics();
		if (mode != 4) {
			final Move llast = kifu.get_last_move();
			ReDraLast(g, llast.t & 0x7f);
		}
		final Move new_move = new Move();
		new_move.f = move.f;
		new_move.t = move.t;
		if (move.f > 99) {
			if (rbd.teban == 0) {
				new_move.f = (short) (128 + (rbd.board[move.f] & 0xf));
			} else {
				new_move.f = (short) (144 + (rbd.board[move.f] & 0xf));
			}
		}
		final int r = kifu.current_p_susumeru(new_move);
		if (r != 0) {
			return r;
		}
		rbd.exec_move_l(move);
		kifu.move_no++;
		rbd.teban = (short) (128 - rbd.teban);
		if (mode < 4) {
			ExcmovDisp(g, move.f, move.t & 0x7f, rbd.board[move.t & 0x7f], rbd.teban);
		}
		if (mode != 4) {
			komaoto();
		}
		if (mode != 4) {
			info.DispAllInfo(g, kifu, rbd, bd, no_henka_flg);
			if (teban_disp_flg != 0) {
				DrawKomadaiTeban(g, 0);
				DrawKomadaiTeban(g, 128);
			}
			disp_comment();
			KifuList.select(kifu.move_no);
			ButtonUpdate();
		}
		return 0;
	}

	void go_rep_mouseClicked() {
		go_rep_flg = 1;
		go_rep_tesuu = 0;
		stop.setEnabled(true);
	}

	void hanten_mouseClicked() {
		bdinv_flg = 1 - bdinv_flg;
		ViewUpdate();
	}

	void help_mouseClicked() {
		if (help_flg) {
			getAppletContext().showDocument(help_url, "_blank");
		}
	}

	void henka_mouseClicked() {
		do {
			final Move move = kifu.get_next_move(rbd);
			if (move.f == 0 && move.t == 0 || move.t < 11) {
				break;
			}
			ExecMove(4, move);
		} while (kifu.is_bunki_n(kifu.move_no + 1) == 0);
		ViewUpdate();
	}

	void HenkaChoice_itemStateChanged() {
		final int sel = HenkaChoice.getSelectedIndex();
		if (sel != 0) {
			Jump_henka(kifu.move_no + 1, sel);
			komaoto();
		}
	}

	void HenkaSetData() {
		if (kifu.current_bunki() == 0) {
			if (henka_no_flg == 1) {
				return;
			}
			HenkaChoice.removeAll();
			HenkaChoice.addItem("\u5909\u5316\u306A\u3057");
			henka_no_flg = 1;
			return;
		}
		henka_no_flg = 0;
		HenkaChoice.removeAll();
		Kifup kp = kifu.current_p.next;
		if (kp == null) {
			return;
		}
		String st = String.valueOf(new StringBuffer("1 ").append(kifu.kifu_mov_strm_kp(0, 1, kp, 5)));
		HenkaChoice.addItem(st);
		kp = kp.branch;
		for (int n = 1; kp != null; n++) {
			st = String.valueOf(n + 1) + " " + kifu.kifu_mov_strm_kp(0, 1, kp, 5);
			HenkaChoice.addItem(st);
			kp = kp.branch;
		}

	}

	void Jump(final int n) {
		Jump_sub(n);
		disp_comment();
		repaint();
		KifuList.select(kifu.move_no);
	}

	void Jump_henka(final int n, final int henka_n) {
		Kifup kp;
		if (henka_n == 0 || kifu.is_bunki_n(n) == 0) {
			kp = kifu.get_kifup_n(kifu.move_no);
			final int moto_henka = kifu.is_henka_kp(kp);
			final int gon = n - kifu.move_no;
			Jump_sub(n);
			if (moto_henka != 0 && gon < 0) {
				KifuSetData();
			}
			ViewUpdate();
			return;
		}
		Jump_sub(n - 1);
		kp = kifu.get_kifup_n(n);
		Kifup br = kp.branch;
		if (henka_n == 1) {
			final Move move = new Move();
			move.f = br.f;
			move.t = br.t;
			move.f = kifu.movef_to_old(rbd, move.f);
			ExecMove(4, move);
			KifuSetData();
			ViewUpdate();
			return;
		}
		for (int i = 1; br != null; i++) {
			if (i == henka_n) {
				final Move move = new Move();
				move.f = br.f;
				move.t = br.t;
				move.f = kifu.movef_to_old(rbd, move.f);
				ExecMove(4, move);
				KifuSetData();
				ViewUpdate();
				return;
			}
			br = br.branch;
		}

	}

	void Jump_sub(final int n) {
		if (n == kifu.move_no) {
			return;
		}
		if (n > kifu.move_no) {
			do {
				final Move move = kifu.get_next_move(rbd);
				if (move.f == 0 && move.t == 0 || move.t < 11) {
					break;
				}
				ExecMove(4);
			} while (kifu.move_no < n);
		} else {
			do {
				BackMove(4, 0);
			} while (kifu.move_no > n);
		}
	}

	void kifu_down_mouseClicked() {
		if (no_kifu_flg == 1) {
			kifu_down.setLabel("\u68CB\u8B5C\u4FDD\u5B58");
			DispKifuButton();
			no_kifu_flg = 0;
			final Graphics g = getGraphics();
			info.DispAllInfo(g, kifu, rbd, bd, no_henka_flg);
		} else {
			KifuDown();
		}
	}

	void KifuDown() {
		getAppletContext().showDocument(kifu_url, "_blank");
	}

	void KifuList_itemStateChanged() {
		if (del_flg != 0) {
			return;
		}
		final int sel = KifuList.getSelectedIndex();
		if (sel >= 0) {
			Jump_henka(sel, 0);
		}

	}

	void kifuRemoveAll() {
		del_flg = 1;
		final int n = KifuList.getItemCount();
		for (int i = n - 1; i >= 0; i--) {
			KifuList.remove(i);
		}

		del_flg = 0;
	}

	void KifuSetData() {
		kifuRemoveAll();
		String comment = kifu.get_kifu_commentp(0);
		String coms;
		if (comment == null) {
			coms = " ";
		} else {
			coms = "*";
		}
		final String st = String.valueOf(coms).concat("== \u958B\u59CB\u5C40\u9762 ==");
		KifuList.add(st);
		final int n1 = kifu.get_kifu_size();
		final StringBuilder sth = new StringBuilder();
		for (int i = 1; i <= n1; i++) {
			final Kifup kp = kifu.get_kifup_n(i);
			sth.setLength(0);
			if (kifu.is_henka_kp(kp) != 0) {
				sth.append('|');
			}
			comment = kp.comment;
			if (comment == null) {
				sth.append(' ');
			} else {
				sth.append('*');
			}
			if (kp.t < 11) {
				sth.append("    ");
			} else {
				if (i < 10) {
					sth.append("  ");
				} else if (i < 100) {
					sth.append(' ');
				}
				sth.append(i);
			}
			sth.append(' ');
			sth.append(kifu.kifu_mov_strm_kp(0, 1, kp, 5));
			if (kifu.is_bunki_n(i) != 0) {
				for (Kifup br = kp.branch; br != null; br = br.branch) {
					sth.append(' ');
					sth.append(kifu.kifu_mov_strm_kp(0, 1, br, 5));
				}

			}
			KifuList.add(sth.toString());
		}

		KifuList.select(kifu.move_no);
	}

	void komadai_mokume(final Graphics g, final int x1, final int y1, final int x2, final int y2) {
		if (komadai_image == null) {
			bd.clear_hako_rect(g, x1, y1, x2, y2);
		} else {
			g.drawImage(komadai_image, x1, y1, this);
		}
	}

	void komaoto() {
		if (komaoto_flg != 0) {
			komaotoClip.play();
		}
	}

	void last_mouseClicked() {
		ReplayLast();
	}

	int load_kifu() throws IOException {
		final int r = file.load(kifu, rbd, start, kifu_url);
		if (r == 1) {
			return 1;
		}
		KifuSetData();
		if (replay_last_flg) {
			ReplayLast();
		} else if (start_tesuu > 0) {
			Jump(start_tesuu);
		}
		disp_comment();
		if (no_henka_flg == 2) {
			if (kifu.is_henka_flg == 0) {
				delHenkaButton();
			} else {
				DispHenkaButton();
			}
		}
		ButtonUpdate();
		repaint();
		return 0;
	}

	void next_mouseClicked() {
		ExecMove(0);
	}

	void ReDraLast(final Graphics g, final int last_sq) {
		if (kifu.move_no == 0) {
			return;
		}
		clear_sq(g, last_sq);
		DrawPiece(g, last_sq);
	}

	void ReplayLast() {
		Move move;
		do {
			move = kifu.get_next_move(rbd);
			if (move.f == 0 && move.t == 0 || move.t < 11) {
				break;
			}
			ExecMove(4);
		} while (move.t >= 11);
		ViewUpdate();
	}

	void ReplayTop() {
		final Kifup kp = kifu.get_kifup_n(kifu.move_no);
		final int moto_henka = kifu.is_henka_kp(kp);
		kifu.copy_board_to_board(start, rbd);
		kifu.move_no = 0;
		kifu.current_p = kifu.kifu_root;
		disp_comment();
		if (moto_henka == 0) {
			KifuList.select(kifu.move_no);
		} else {
			KifuSetData();
		}
		ButtonUpdate();
	}

	void showPage(final URL url) {
		getAppletContext().showDocument(url);
	}

	void stop_mouseClicked() {
		replay_stop_flg = 1;
		stop.setEnabled(false);
	}

	void top_mouseClicked() {
		ReplayTop();
		final Graphics g = getGraphics();
		info.DispAllInfo(g, kifu, rbd, bd, no_henka_flg);
		disp_comment();
		repaint();
	}

	void ViewUpdate() {
		disp_comment();
		repaint();
		KifuList.select(kifu.move_no);
		ButtonUpdate();
	}

	private int BackMove(final int mode, final int sound_mode) {
		if (kifu.move_no <= 0) {
			return 1;
		}
		final Graphics g = getGraphics();
		final Kifup kp = kifu.get_kifup_n(kifu.move_no);
		final int moto_henka = kifu.is_henka_kp(kp);
		rbd.teban = (short) (128 - rbd.teban);
		int henka1 = 0;
		if (mode != 4) {
			henka1 = kifu.is_henka_kp(kp);
		}
		final short f = kp.f;
		short t = kp.t;
		final short k = kp.koma;
		final short dead = kp.dead;
		if (t != 0) {
			rbd.back_movel(f, t, k, dead);
			kifu.move_no--;
			kifu.current_p = kifu.find_oya(kifu.current_p);
			if (mode != 4) {
				t &= 0x7f;
				clear_sq(g, t);
				if (dead != 0) {
					DrawKomadai(g, rbd.teban);
					DrawPiece(g, t);
				}
				if (f <= 99) {
					DrawPiece(g, f);
				} else {
					DrawKomadai(g, rbd.teban);
				}
				if (teban_disp_flg != 0) {
					DrawKomadaiTeban(g, 0);
					DrawKomadaiTeban(g, 128);
				}
			}
			if (mode != 4 && sound_mode != 0) {
				komaoto();
			}
		}
		if (mode != 4) {
			DrawLastMark(g);
			info.DispAllInfo(g, kifu, rbd, bd, no_henka_flg);
			disp_comment();
			final int henka2 = kifu.is_henka_kp(kifu.current_p);
			if (henka1 != 0 && henka2 == 0 && moto_henka != 0) {
				KifuSetData();
			}
			KifuList.select(kifu.move_no);
			ButtonUpdate();
		}
		return 0;
	}

	private void jbInit() {
		commentArea.setBounds(new Rectangle(4, 322, 469, 54));
		next.setBounds(new Rectangle(155, 5, 37, 23));
		next.setLabel("\uFF1E");
		next.addMouseListener(new Applet1_next_mouseAdapter(this));
		int x = 194;
		go_rep.setBounds(new Rectangle(x, 5, 37, 23));
		go_rep.setLabel("\uFF1E\uFF1E");
		go_rep.addMouseListener(new Applet1_go_rep_mouseAdapter(this));
		x = 233;
		last.setBounds(new Rectangle(x, 5, 37, 23));
		last.setLabel("\uFF1E\uFF5C");
		last.addMouseListener(new Applet1_last_mouseAdapter(this));
		x = 122;
		stop.setBounds(new Rectangle(x, 5, 31, 23));
		stop.setLabel("\u25A0");
		stop.addMouseListener(new Applet1_stop_mouseAdapter(this));
		x = 83;
		back.setBounds(new Rectangle(x, 5, 37, 23));
		back.setLabel("\uFF1C");
		back.addMouseListener(new Applet1_back_mouseAdapter(this));
		back_rep.setBounds(new Rectangle(44, 5, 37, 23));
		back_rep.setLabel("\uFF1C\uFF1C");
		back_rep.addMouseListener(new Applet1_back_rep_mouseAdapter(this));
		top.setBounds(new Rectangle(5, 5, 37, 23));
		top.setLabel("\uFF5C\uFF1C");
		top.addMouseListener(new Applet1_top_mouseAdapter(this));
		setLayout(null);
		x = 272;
		backBra.setBounds(new Rectangle(x, 5, 37, 23));
		backBra.setLabel("\uFF0B\uFF1C");
		backBra.addMouseListener(new Applet1_backBra_mouseAdapter(this));
		henka.setBounds(new Rectangle(311, 5, 37, 23));
		henka.setLabel("\uFF1E\uFF0B");
		henka.addMouseListener(new Applet1_henka_mouseAdapter(this));
		hanten.setBounds(new Rectangle(350, 5, 37, 23));
		hanten.setLabel("\u53CD\u8EE2");
		hanten.addMouseListener(new Applet1_hanten_mouseAdapter(this));
		help.setBounds(new Rectangle(389, 5, 37, 23));
		help.setLabel("Help");
		help.addMouseListener(new Applet1_help_mouseAdapter(this));
		kifu_down.setBounds(new Rectangle(4, 298, 92, 22));
		kifu_down.setLabel("\u68CB\u8B5C\u4FDD\u5B58");
		kifu_down.addMouseListener(new Applet1_kifu_down_mouseAdapter(this));
		HenkaChoice.setBounds(new Rectangle(4, 270, 93, 23));
		HenkaChoice.addItemListener(new Applet1_HenkaChoice_itemAdapter(this));
		KifuList.setBounds(new Rectangle(4, 167, 93, 100));
		KifuList.addItemListener(new Applet1_KifuList_itemAdapter(this));
		add(commentArea, null);
		add(top, null);
		add(back_rep, null);
		add(back, null);
		add(stop, null);
		add(next, null);
		add(go_rep, null);
		add(last, null);
		add(henka, null);
		add(backBra, null);
		add(hanten, null);
		add(help, null);
		add(KifuList, null);
		add(kifu_down, null);
		add(HenkaChoice, null);
	}
}
