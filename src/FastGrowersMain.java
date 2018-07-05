public class FastGrowersMain {
    public static void main(String[] args){

        //FastGrowerFrame fgFrame = new FastGrowerFrame(); fgFrame.setVisible(true);
        double specific_alpha = Math.log(11.5)/500.;
        BioSystem.exponentialGradient_spatialAndGRateDistributions(specific_alpha);
    }
}
