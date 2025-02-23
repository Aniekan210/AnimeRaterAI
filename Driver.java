
/************************************************************
 * Write a description of class Driver here.
 *
 * @author (your name)
 * @version (a version number or a date)
 ************************************************************/
public class Driver
{
    public static void main(String[] args)
    {
        AnimeRater rater = new AnimeRater();
        rater.train("anime.csv");
        
        System.out.println(rater.predict(new double[]{1,0.3,0.1,0.3,0,1,0.8,0.5,0.8,0,0,0.1,0}));
        System.out.println(rater.predict(new double[]{0,0,0,0.3,0,0.5,1,0,0,0.7,0.6,0.4,0.8}));
    }
}
