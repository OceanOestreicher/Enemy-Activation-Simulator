import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeyboundButton extends JButton {

    public KeyboundButton(String name, String[]keysToBindTo){
        super(name);
        for(String s: keysToBindTo){
            this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s),"Pressed");
        }
        this.getActionMap().put("Pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ActionListener a: KeyboundButton.this.getActionListeners()){
                    if(this.isEnabled()){
                        a.actionPerformed(new ActionEvent(KeyboundButton.this,ActionEvent.ACTION_PERFORMED,"Key_Pressed"));
                        KeyboundButton.this.getModel().setArmed(true);
                        KeyboundButton.this.getModel().setPressed(true);
                        Timer t = new Timer(200, e1 -> {
                            KeyboundButton.this.getModel().setArmed(false);
                            KeyboundButton.this.getModel().setPressed(false);
                        });
                        t.setRepeats(false);
                        t.start();
                    }
                }
            }
        });
    }
}
