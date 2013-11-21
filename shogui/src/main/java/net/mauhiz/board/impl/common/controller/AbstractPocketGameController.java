package net.mauhiz.board.impl.common.controller;

import net.mauhiz.board.model.BoardIO;
import net.mauhiz.board.model.PocketGameController;

public abstract class AbstractPocketGameController extends AbstractGameController implements PocketGameController {
	protected AbstractPocketGameController(final BoardIO display) {
		super(display);
	}
}
