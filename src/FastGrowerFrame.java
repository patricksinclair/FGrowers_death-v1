import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class FastGrowerPanel extends JPanel {

    BioSystem bioSys;

    public FastGrowerPanel(BioSystem bioSys){
        this.bioSys = bioSys;
        setBackground(Color.BLACK);
    }


    @Override
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        int L = bioSys.getL();
        //int K = bioSys.getK();
        int H = 700;
        int w = getWidth()/L;
        int h = getHeight()/H;

        for(int l = 0; l < L; l++) {
            if(bioSys.getMicrohabitats(l).getN_tot() > 0) {

                g.setColor(Color.RED);
                g.fillRect(w*l, 0, w, h*bioSys.getMicrohabitats(l).getN_dead());

                g.setColor(Color.GREEN);
                g.fillRect(w*l, bioSys.getMicrohabitats(l).getN_dead(), w,
                        h*bioSys.getMicrohabitats(l).getN_alive());


            }
        }
    }



    public void monteAnimate(){
        for(int i = 0; i < 1000; i++) {
            bioSys.performAction();
        }
        repaint();
    }

    public void updateAlpha(double newAlpha){
        bioSys = new BioSystem(bioSys.getL(), bioSys.getS(), newAlpha);
        repaint();
    }


}


public class FastGrowerFrame extends JFrame {

    int L = 500, S = 500;
    double alpha = 0.02;

    FastGrowerPanel fgPan;
    BioSystem bioSys;
    Timer monteTimer;

    JButton goButton = new JButton("Go");

    //stuff for allowing GUI variance of alpha
    JLabel alphaLabel = new JLabel("alpha: ");
    JTextField alphaField = new JTextField(String.valueOf(alpha), 10);

    public FastGrowerFrame(){

        bioSys = new BioSystem(L, S, alpha);

        fgPan = new FastGrowerPanel(bioSys);
        fgPan.setPreferredSize(new Dimension(1000, 700));

        JPanel controlPanel = new JPanel();
        controlPanel.add(goButton);
        controlPanel.add(alphaLabel);
        controlPanel.add(alphaField);

        getContentPane().add(fgPan, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        setTitle("Fast Growers w. death simulation");
        setLocation(100, 20);
        setVisible(true);
        setBackground(Color.LIGHT_GRAY);

        monteAnimate();
        updateAlpha();
    }



    public void monteAnimate(){
        monteTimer = new Timer(0, (e)->{fgPan.monteAnimate();});

        goButton.addActionListener((e)->{
            if(!monteTimer.isRunning()) {
                monteTimer.start();
                goButton.setText("Stop");
            }
            else {
                monteTimer.stop();
                goButton.setText("Go");
            }
        });
    }

    public void updateAlpha(){
        alphaField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                alphaField.setText("");
            }
        });

        alphaField.addActionListener((e)->{
            double alpha = Double.parseDouble(alphaField.getText());
            fgPan.updateAlpha(alpha);
        });
    }

}