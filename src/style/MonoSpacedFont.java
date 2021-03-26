package style;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MonoSpacedFont implements FontStyle{
	@Override
	public Font getFont() {
		return Font.font("MONOSPACED", FontWeight.NORMAL,14);
	}
}
