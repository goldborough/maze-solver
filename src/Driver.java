/**
 * @(#)Driver.java
 * @author Michael Goldborough
 * @version
 */

import java.awt.event.*;

public class Driver {
    public static void main(String[] args) {
		BaseUI guiFrame=new BaseUI();

		guiFrame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing (WindowEvent e)
				{
					System.exit(0);
				}
			}
		);
    }
}