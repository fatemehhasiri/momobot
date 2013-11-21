package net.mauhiz.board.impl.shogi;

public enum JapaneseNumeral {
	一(1), 七(7), 三(3), 九(9), 二(2), 五(5), 八(8), 六(6), 四(4);

	private int value;

	private JapaneseNumeral(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
