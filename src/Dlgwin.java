import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Dlgwin extends Frame implements ActionListener {

    int size;

    String param="8";

    Button bexit=new Button("Ok"); TextField tfi=new TextField();
    JLabel lable;
    JLabel wait;

    public Dlgwin()

    {
        setLayout(null);
        add(bexit);
        add(tfi);
        lable=new JLabel("Введите размер доски от 4 до 11:");
        add(lable);
        lable.setBounds(50,40,250,200);
        wait=new JLabel("Пожалуйста, подождите...");
        add(wait);
        wait.setBounds(50,30,250,200);
        wait.setVisible(false);
        bexit.addActionListener(this);
        tfi.setBounds(50, 160, 200, 20);
        tfi.setText(param);
        bexit.setBounds(100, 190, 100, 20);
        setBackground(new Color(200, 190, 190));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });

    }

    public void actionPerformed (ActionEvent e)

    {

        if (e.getSource()==bexit)
        {
            try {
                size = Integer.parseInt(tfi.getText());
            }catch (Exception a){
                size=8;
            }
            if (size<4 || size>11)
                size=11;
            //int size = 12;
            tfi.setVisible(false);
            bexit.setVisible(false);
            lable.setVisible(false);

            QueensLogic l = new QueensLogic(size);
            //l.initializeGame(size);

            QueensGUI g = new QueensGUI(l);

            // Setup of the frame containing the game
            JFrame f = new JFrame();
            f.setSize(160 + size * 50, 125 + size * 50);
            f.setTitle("Queens Problem");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(g);
            this.setVisible(false);
            f.setVisible(true);
        }

    }
    public static void main(String[] arg) {

        Dlgwin dlg=new Dlgwin();
        dlg.setSize(300,300);
        dlg.setVisible(true);

    }

}