public class Microhabitat {

    //no. of bacteria in the microhabitat
    private int N_alive, N_dead;
    private double c;
    int S, S_max;

    //death and migration rates
    private double d = 0., b = 0.1;
    private double K_prime = 33.;

    public Microhabitat(int S, double c){
        this.S = S;
        this.S_max = S;
        this.c = c;
        this.N_alive = 0;
        this.N_dead = 0;
    }

    public int getS(){return S;}
    public double getC(){return c;}
    public int getN_alive(){return N_alive;}
    public int getN_dead(){return N_dead;}
    public int getN_tot(){return N_alive+N_dead;}
    public double getD(){return d;}
    public double getB(){return b;}


    public double beta(){

        double mu = S/(K_prime+S);
        double mu_max = S_max/(K_prime+S_max);
        return 10. - 9.*mu/mu_max;
    }

    public double phi_c(){
        return 1. - (6.*(c/beta())*(c/beta()))/(5. + (c/beta())*(c/beta()));
    }

    public double replication_or_death_rate(){

        double phi_c = phi_c();
        return (phi_c >= 0) ? phi_c*(S/(K_prime + S)) : phi_c;
    }


    public void fillWithWildType(int initalPop){
        N_alive+=initalPop;
    }

    public void consumeANutrient(){
        if(S > 0) S--;
    }

    public void addABacterium(){N_alive++;}
    public void removeABacterium(){N_alive--;}

    public void replicateABacterium(){N_alive++;}

    public void killABacterium(){
        if(N_alive > 0){
            N_alive--;
            N_dead++;
        }
    }



}
