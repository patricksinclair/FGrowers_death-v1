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
        int w = getWidth()/L;
        int h = getHeight()/K;

        for(int l = 0; l < L; l++) {
            if(bioSys.getMicrohabitat(l).getN() > 0) {

                int nBacs = 0;
                for(int sp = 0; sp < bioSys.getMicrohabitat(l).getMultiSpecPops().length; sp++){

                    if(sp == 0) g.setColor(Color.RED);
                    else if(sp==1) g.setColor(Color.orange);
                    else if(sp==2) g.setColor(Color.yellow);
                    else if(sp==3) g.setColor(Color.green);
                    else if(sp==4) g.setColor(Color.blue);
                    else if(sp==5) g.setColor(Color.MAGENTA);
                    else if(sp==6) g.setColor(Color.cyan);
                    else if(sp==7) g.setColor(Color.GRAY);
                    else if(sp==8) g.setColor(Color.PINK);
                    else if(sp==9) g.setColor(new Color(153, 153, 0));
                    else if(sp==10) g.setColor(new Color(127, 0, 255));

                    g.fillRect(w*l, h*nBacs, w, h*bioSys.getMicrohabitat(l).getMultiSpecPops()[sp]);
                    nBacs += bioSys.getMicrohabitat(l).getMultiSpecPops()[sp];
                }

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
        bioSys = new BioSystem(bioSys.getL(), bioSys.getK(), bioSys.getnSpecies(), newAlpha);
        repaint();
    }


}


public class FastGrowerFrame extends JFrame {

    int L = 500, K = 256, nSpecies = 11;
    double alpha = 0.02;

    BioPanel bioPan;
    BioSystem bioSys;
    Timer monteTimer;

    JButton goButton = new JButton("Go");

    //stuff for allowing GUI variance of alpha
    JLabel alphaLabel = new JLabel("alpha: ");
    JTextField alphaField = new JTextField(String.valueOf(alpha), 10);

    public BioFrame(){

        bioSys = new BioSystem(L, K, nSpecies, alpha);

        bioPan = new BioPanel(bioSys);
        bioPan.setPreferredSize(new Dimension(1000, 512));

        JPanel controlPanel = new JPanel();
        controlPanel.add(goButton);
        controlPanel.add(alphaLabel);
        controlPanel.add(alphaField);

        getContentPane().add(bioPan, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        setTitle("Multispecies simulation");
        setLocation(100, 20);
        setVisible(true);
        setBackground(Color.LIGHT_GRAY);

        monteAnimate();
        updateAlpha();
    }



    public void monteAnimate(){
        monteTimer = new Timer(0, (e)->{bioPan.monteAnimate();});

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
            bioPan.updateAlpha(alpha);
        });
    }

}