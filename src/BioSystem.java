import java.util.Random;

public class BioSystem {

    private int L, K, s, s_max;
    private double c, alpha, timeElapsed;

    private Microhabitat[] microhabitats;
    Random rand = new Random();
    private int initialPop = 100;

    public BioSystem(int L, int S, double alpha){

        this.L = L;
        this.s = S;
        this.s_max = S;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++){
            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(S, c_i);
        }
        microhabitats[0].fillWithWildType(initialPop);
    }

    public int getL(){
        return L;
    }
    public int getS(){return s;}
    public double getTimeElapsed(){
        return timeElapsed;
    }

    public Microhabitat getMicrohabitats(int i){
        return microhabitats[i];
    }

    public int getCurrentLivePopulation(){
        int runningTotal = 0;
        for(Microhabitat m : microhabitats) {
            runningTotal += m.getN_alive();
        }
        return runningTotal;
    }

    public int[] getLiveSpatialDistributionArray(){
        int[] mh_pops = new int[L];
        for(int i = 0; i < L; i++){
            mh_pops[i] = microhabitats[i].getN_alive();
        }
        return mh_pops;
    }

    public int[] getDeadSpatialDistributionArray(){
        int[] mh_pops = new int[L];
        for(int i = 0; i < L; i++){
            mh_pops[i] = microhabitats[i].getN_dead();
        }
        return mh_pops;
    }


    public double[] getGrowthRatesArray(){
        double[] mh_gRates = new double[L];
        for(int i = 0; i < L; i++){
            mh_gRates[i] = microhabitats[i].replication_or_death_rate();
        }
        return mh_gRates;
    }


    public int getRandMicrohabIndex(int randBacIndex){

        int bacCounter = 0;
        int mh_index = 0;

        forloop:
        for(int i = 0; i < L; i++){
            if(bacCounter+microhabitats[i].getN_alive() <= randBacIndex){
                bacCounter+=microhabitats[i].getN_alive();
                continue forloop;

            }else{
                mh_index = i;
                break forloop;
            }
        }
        return mh_index;
    }


    public void migrate(int microhab_index){
        //handles boundary conditions
        if(microhab_index == 0){
            microhabitats[0].removeABacterium();
            microhabitats[1].addABacterium();

        }else if(microhab_index == L-1){
            microhabitats[L-1].removeABacterium();
            microhabitats[L-2].addABacterium();
            //randomly moves the bacteria forward or backward
        }else{
            if(rand.nextBoolean()){
                microhabitats[microhab_index].removeABacterium();
                microhabitats[microhab_index+1].addABacterium();

            }else{
                microhabitats[microhab_index].removeABacterium();
                microhabitats[microhab_index-1].addABacterium();
            }
        }
    }


    public void death(int mh_index){
        microhabitats[mh_index].killABacterium();
    }

    public void replicate(int mh_index){

        microhabitats[mh_index].consumeANutrient();
        microhabitats[mh_index].replicateABacterium();
    }


    public void performAction(){

        int N = getCurrentLivePopulation();
        int randBacIndex = rand.nextInt(N);
        int mh_index = getRandMicrohabIndex(randBacIndex);
        Microhabitat rand_mh = microhabitats[mh_index];

        double migrate_rate = rand_mh.getB();
        double death_rate = rand_mh.getD();

        double R_max = 5.2;
        double life_or_death = rand_mh.replication_or_death_rate();

        if(life_or_death >=0){
            double rand_chance = rand.nextDouble()*R_max;

            if(rand_chance <= migrate_rate) migrate(mh_index);
            else if(rand_chance > migrate_rate && rand_chance <= life_or_death+migrate_rate) replicate(mh_index);

        }else{
            double rand_chance = rand.nextDouble()*R_max;
            life_or_death*=-1;

            if(rand_chance <= migrate_rate) migrate(mh_index);
            else if(rand_chance > migrate_rate && rand_chance <= life_or_death+migrate_rate) death(mh_index);
        }

        timeElapsed += 1./((double)N*R_max);
    }



    public static void exponentialGradient_spatialAndGRateDistributions(double input_alpha){

        int L = 500, nReps = 20;
        int nTimeMeasurements = 20;

        double duration = 2000., interval = duration/(double)nTimeMeasurements;
        double preciseDuration = duration/5., preciseInterval = preciseDuration/(double)nTimeMeasurements;

        double alpha = input_alpha;
        int S = 500;

        String filename_alive = "fastGrowers_death-alpha="+String.valueOf(alpha)+"-aliveSpatialDistribution-FINAL";
        String filename_dead = "fastGrowers_death-alpha="+String.valueOf(alpha)+"-deadSpatialDistribution-Final";
        String filename_gRate = "fastGrowers_death-alpha="+String.valueOf(alpha)+"-gRateDistribution-FINAL";

        String filename_alive_precise = "fastGrowers_death-alpha="+String.valueOf(alpha)
                +"-aliveSpatialDistribution_precise-FINAL";
        String filename_dead_precise = "fastGrowers_death-alpha="+String.valueOf(alpha)
                +"-deadSpatialDistribution_precise-FINAL";
        String filename_gRate_precise = "fastGrowers_death-alpha="+String.valueOf(alpha)
                +"-gRateDistribution_precise-FINAL";

        int[][][] allN_alive = new int[nReps][][];
        int[][][] allN_dead = new int[nReps][][];
        double[][][] allGRates = new double[nReps][][];

        int[][][] allPreciseN_alive = new int[nReps][][];
        int[][][] allPreciseN_dead = new int[nReps][][];
        double[][][] allPreciseGRates = new double[nReps][][];

        for(int r = 0; r < nReps; r++){

            boolean alreadyRecorded = false, alreadyPreciselyRecorded = false;

            int[][] alivePopsOverTime = new int[nTimeMeasurements+1][];
            int[][] deadPopsOverTime = new int[nTimeMeasurements+1][];
            double[][] gRatesOverTime = new double[nTimeMeasurements+1][];
            int timerCounter = 0;

            int[][] preciseAlivePopsOverTime = new int[nTimeMeasurements+1][];
            int[][] preciseDeadPopsOverTime = new int[nTimeMeasurements+1][];
            double[][] preciseGRatesOverTime = new double[nTimeMeasurements+1][];
            int preciseTimerCounter = 0;


            BioSystem bs = new BioSystem(L, S, alpha);

            while(bs.timeElapsed <= duration){

                bs.performAction();

                if((bs.getTimeElapsed()%preciseInterval >= 0. && bs.getTimeElapsed()%preciseInterval <= 0.01)
                        && !alreadyPreciselyRecorded && preciseTimerCounter <= nTimeMeasurements){

                    System.out.println("rep: "+r+"\ttime elapsed: "+String.valueOf(bs.getTimeElapsed())+"\tPRECISE");
                    preciseAlivePopsOverTime[preciseTimerCounter] = bs.getLiveSpatialDistributionArray();
                    preciseDeadPopsOverTime[preciseTimerCounter] = bs.getDeadSpatialDistributionArray();
                    preciseGRatesOverTime[preciseTimerCounter] = bs.getGrowthRatesArray();

                    alreadyPreciselyRecorded = true;
                    preciseTimerCounter++;
                }
                if(bs.getTimeElapsed()%preciseInterval >= 0.1) alreadyPreciselyRecorded = false;


                if((bs.getTimeElapsed()%interval >= 0. && bs.getTimeElapsed()%interval <= 0.01) && !alreadyRecorded){

                    System.out.println("rep: "+r+"\ttime elapsed: "+String.valueOf(bs.getTimeElapsed()));
                    alivePopsOverTime[timerCounter] = bs.getLiveSpatialDistributionArray();
                    deadPopsOverTime[timerCounter] = bs.getDeadSpatialDistributionArray();
                    gRatesOverTime[timerCounter] = bs.getGrowthRatesArray();

                    alreadyRecorded = true;
                    timerCounter++;
                }
                if(bs.getTimeElapsed()%interval >= 0.1) alreadyRecorded = false;
            }

            allN_alive[r] = alivePopsOverTime;
            allN_dead[r] = deadPopsOverTime;
            allGRates[r] = gRatesOverTime;
            allPreciseN_alive[r] = preciseAlivePopsOverTime;
            allPreciseGRates[r] = preciseGRatesOverTime;
        }

        double[][] averagedAlivePopDistributions = Toolbox.averagedResults(allN_alive);
        double[][] averagedDeadPopDistributions = Toolbox.averagedResults(allN_dead);
        double[][] averagedGRateDistributions = Toolbox.averagedResults(allGRates);

        double[][] averagedPreciseAlivePopDistributions = Toolbox.averagedResults(allPreciseN_alive);
        double[][] averagedPreciseDeadPopDistributions = Toolbox.averagedResults(allPreciseN_dead);
        double[][] averagedPreciseGRateDistributions = Toolbox.averagedResults(allPreciseGRates);

        //print the live and dead results to two separate files, then just join them together in
        //gnuplot or something
        Toolbox.printAveragedResultsToFile(filename_alive, averagedAlivePopDistributions);
        Toolbox.printAveragedResultsToFile(filename_dead, averagedDeadPopDistributions);
        Toolbox.printAveragedResultsToFile(filename_gRate, averagedGRateDistributions);

        Toolbox.printAveragedResultsToFile(filename_alive_precise, averagedPreciseAlivePopDistributions);
        Toolbox.printAveragedResultsToFile(filename_dead_precise, averagedPreciseDeadPopDistributions);
        Toolbox.printAveragedResultsToFile(filename_gRate_precise, averagedPreciseGRateDistributions);
    }





}
