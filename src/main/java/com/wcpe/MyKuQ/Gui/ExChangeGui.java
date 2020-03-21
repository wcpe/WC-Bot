package com.wcpe.MyKuQ.Gui;

import java.util.List;

import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.window.FormWindowCustom;
import com.wcpe.MyKuQ.Gui.InterFace.Gui;

public class ExChangeGui extends FormWindowCustom implements Gui {
	public ExChangeGui(String title, List<Element> contents) {
		super(title, contents);
	}
}
