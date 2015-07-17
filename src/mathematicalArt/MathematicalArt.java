package mathematicalArt;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class MathematicalArt
{
    //variables
        static final int sizeX = 256;
        static final int sizeY = 256;
        static BufferedImage image = new BufferedImage(sizeX,sizeY,BufferedImage.TYPE_INT_RGB);
        static Scanner kb = new Scanner(System.in);
        static Random rand = new Random();
        static final double maxDistance = Math.sqrt(Math.pow(sizeX, 2)+Math.pow(sizeY,2));
        static final double maxRadius = maxDistance/2;

        static double initialPaintProportion = 0.1;
        static int blurRadius = 10;

        static int rMax = 255;
        static int gMax = 255;
        static int bMax = 255;

    //colors
        static int r;
        static int g;
        static int b;
        static int color;
        
    //runs selected image generating algorithm and then exports the image
    public static void main(String[] args) throws IOException
    {
        functionPaint();
        //blur(blurRadius);
        export();
    }

    //blurs the image, takes a blur radius as parameter
    public static void blur(int blurRadius)
    {
        boolean[][] isBlurred = new boolean[sizeX][sizeY];
        int numBlurred = 0;
        while(numBlurred < (sizeX*sizeY))
        {
            int rAccum = 0, gAccum = 0, bAccum = 0, counter = 0, i = rand.nextInt(sizeX), j = rand.nextInt(sizeY);
            if(!isBlurred[i][j])
            {
                for(int x=i-blurRadius;x<=i+blurRadius;x++)
                {
                    for(int y=j-blurRadius;y<=j+blurRadius;y++)
                    {
                        try
                        {
                            Color adjacentColor = new Color(image.getRGB(x, y));
                            counter++;
                            rAccum += adjacentColor.getRed();
                            gAccum += adjacentColor.getGreen();
                            bAccum += adjacentColor.getBlue();
                        }
                        catch(Exception ArrayIndexOutOfBoundsException)
                        {
                            
                        }
                    }
                }
                int rAvg = rAccum/counter, gAvg = gAccum/counter, bAvg = bAccum/counter;
                int colorAvg = toRGB(rAvg,gAvg,bAvg);
                image.setRGB(i, j, colorAvg);
                isBlurred[i][j] = true;
                numBlurred++;
            }
        }
    }

    //generates image based on mathematical functions for r,g,b values
    public static void functionPaint()
    {
        for(int j=0;j<sizeY;j++)
        {
            for(int i=0;i<sizeX;i++)
            {
                r = (int)(Math.sqrt((double)(Math.pow((i-sizeX/2),2))*Math.pow((j-sizeY/2),2)*2));
                g = (int)Math.sqrt((double)((int)(Math.pow((i-sizeX/2),2))|(int)(Math.pow((j-sizeX/2),2))*(int)(Math.pow(((i-sizeX)/2),2))&(int)(Math.pow((j-sizeX/2),2))));
                b = (int)Math.sqrt((double)((int)(Math.pow((i-sizeX/2),2))&(int)(Math.pow((j-(sizeX/2)),2)*2.0)));
                color = toRGB(r,g,b);
                image.setRGB(i, j, color);
            }
        }
    }

    //generates image based on radius of pixel from center
    public static void circlePaint()
    {
        for(int i=0;i<sizeX;i++)
        {
            for(int j=0;j<sizeY;j++)
            {
                double radius = getRadius(i,j);
                double radiusProportion = radius/maxRadius;
                r = (int)radius;
                g = (int)(maxRadius-radius);
                b = 0;
                color = toRGB(r,g,b);
                image.setRGB(i,j,color);
            }
        }
    }

    //calculates the radius of the pixel from the center of the image
    public static double getRadius(int i, int j)
    {
        int xDist = Math.abs(i-(sizeX/2));
        int yDist = Math.abs(j-(sizeY/2));
        return Math.sqrt(Math.pow(xDist,2)+Math.pow(yDist,2));
    }

    //generates a simple radial gradient from top right of image
    public static void gradientPaint()
    {
        for(int x=0;x<sizeX;x++)
        {
            for(int y=0;y<sizeY;y++)
            {
                double value = Math.sqrt(Math.pow(x, 2)+Math.pow(y,2));
                double valueProportion = value/maxDistance;
                r = (int)((1-valueProportion)*255);
                g = (int)(valueProportion*255);
                b = (int)((1-(valueProportion%0.5))*255);
                color = toRGB(r,g,b);
                image.setRGB(x, y, color);
            }
        }
    }

    //paints a bunch of random blotches on the screen
    public static void blotchPaint() throws IOException
    {
        //boolean arrray that keeps track of which pixels have been painted
        boolean[][] isPainted = new boolean[sizeX][sizeY];
        int numPainted = 0;
        //paint number of pixels specified by initialPaintProportion randomly
        for(int i=0;i<(int)(initialPaintProportion*sizeX*sizeY);i++)
        {
            int x = rand.nextInt(sizeX);
            int y = rand.nextInt(sizeY);
            while(isPainted[x][y])
            {
                x = rand.nextInt(sizeX);
                y = rand.nextInt(sizeY);
            }
            r = rand.nextInt(rMax);
            g = rand.nextInt(gMax);
            b = rand.nextInt(bMax);
            color = toRGB(r,g,b);
            image.setRGB(x, y, color);
            isPainted[x][y] = true;
            numPainted++;
        }
        //randomly paint rest of pixels same color as adjacent pixel until all are painted
        while(numPainted < (sizeX*sizeY))
        {
            int x = rand.nextInt(sizeX);
            int y = rand.nextInt(sizeY);
            if(!isPainted[x][y] && paintedAdjacent(x,y,isPainted))
            {
                image.setRGB(x,y,copyColor(x,y,isPainted));
                isPainted[x][y] = true;
                numPainted++;
            }
        }
    }

    //creates a pattern of streaks
    public static void streakPaint() throws IOException
    {
        //boolean arrray that keeps track of which pixels have been painted
        boolean[][] isPainted = new boolean[sizeX][sizeY];
        int numPainted = 0;
        //paint number of pixels specified by initialPaintProportion randomly
        for(int i=0;i<(int)(initialPaintProportion*(sizeX*sizeY));i++)
        {
            int x = rand.nextInt(sizeX);
            int y = rand.nextInt(sizeY);
            while(isPainted[x][y])
            {
                x = rand.nextInt(sizeX);
                y = rand.nextInt(sizeY);
            }
            r = rand.nextInt(rMax);
            g = rand.nextInt(gMax);
            b = rand.nextInt(bMax);
            color = toRGB(r,g,b);
            image.setRGB(x, y, color);
            isPainted[x][y] = true;
            numPainted++;
        }
        //paint rest of pixels same color as adjacent pixels until all are painted,
        //moving from top left to bottom right, creating diagonal streaks
        while(numPainted < (sizeX*sizeY))
        {
            for(int i=0;i<sizeX;i++)
            {
                for(int j=0;j<sizeY;j++)
                {
                    if(!isPainted[i][j] && paintedAdjacent(i,j, isPainted))
                    {
                        image.setRGB(i,j,copyColor(i,j,isPainted));
                        isPainted[i][j] = true;
                        numPainted++;
                    }
                }
            }
        }
    }
    
    /*
    //doesn't actually work yet
    //paints streaks in a circular pattern
    public static void circleStreakPaint() throws IOException
    {
        //boolean arrray that keeps track of which pixels have been painted
        boolean[][] isPainted = new boolean[sizeX][sizeY];
        int numPainted = 0;
        //paint number of pixels specified by initialPaintProportion randomly
        for(int i=0;i<(int)(initialPaintProportion*(sizeX*sizeY));i++)
        {
            int x = rand.nextInt(sizeX);
            int y = rand.nextInt(sizeY);
            while(isPainted[x][y])
            {
                x = rand.nextInt(sizeX);
                y = rand.nextInt(sizeY);
            }
            r = rand.nextInt(rMax);
            g = rand.nextInt(gMax);
            b = rand.nextInt(bMax);
            color = toRGB(r,g,b);
            image.setRGB(x, y, color);
            isPainted[x][y] = true;
            numPainted++;
        }
        if(!isPainted[sizeX/2][sizeY/2])
        {
            r = rand.nextInt(rMax);
            g = rand.nextInt(gMax);
            b = rand.nextInt(bMax);
            color = toRGB(r,g,b);
            image.setRGB(sizeX/2,sizeY/2,color);
            isPainted[sizeX/2][sizeY/2] = true;
            numPainted++;
        }
        //paint rest of pixels same color as adjacent pixels until all are painted,
        //moving from top left to bottom right, creating diagonal streaks
        int currentRadius = 0;
        while(numPainted < (sizeX*sizeY))
        {
            currentRadius = 0;
            while(currentRadius < maxRadius)
            {
                for(int i=0; i<sizeX; i++)
                {
                    for(int j=0; j<sizeY; j++)
                    {
                        try
                        {
                            if(getRadius(i,j) == currentRadius && !isPainted[i][j] && paintedAdjacent(i,j,isPainted))
                            {
                                image.setRGB(i,j,copyColor(i,j,isPainted));
                                isPainted[i][j] = true;
                                numPainted++;
                                System.out.println(numPainted);
                            }
                        }
                        catch(Exception ArrayIndexOutOfBoundsException)
                        {
                            
                        }                        
                    }
                }
                currentRadius++;
            }
        }
    }
    */
    
    //converts r,g,b, values to usable integer value
    public static int toRGB(int r, int g, int b)
    {
        int red = r&255;
        int green = g&255;
        int blue = b&255;
        return (red << 16) | (green << 8) | blue;
    }

    //checks if a pixel has any pixels adjacent to it that are painted
    public static boolean paintedAdjacent(int i, int j, boolean[][] paintedInput)
    {
        for(int x=i-1;x<=i+1;x++)
        {
            for(int y=j-1;y<=j+1;y++)
            {
                if(x==i && y==j)
                    continue;
                try
                {
                    if(paintedInput[x][y])
                        return true;
                }
                catch(Exception ArrayIndexOutOfBoundsException)
                {
                
                }
            }
        }
        return false;
    }

    //averages the colors of all pixels adjacent to a pixel and then paints that
    //pixel the averaged color
    public static int copyColor(int i, int j, boolean[][] paintedInput)
    {
        int counter = 0;
        double rAccum = 0;
        double gAccum = 0;
        double bAccum = 0;
        for(int x=i-1;x<=i+1;x++)
        {
            for(int y=j-1;y<=j+1;y++)
            {
                if(x==i && y==j)
                    continue;
                try
                {
                    if(paintedInput[x][y])
                    {
                        counter++;
                        Color adjacentColor = new Color(image.getRGB(x,y));
                        rAccum += adjacentColor.getRed();
                        gAccum += adjacentColor.getGreen();
                        bAccum += adjacentColor.getBlue();
                    }
                }
                catch(Exception ArrayIndexOutOfBoundsException)
                {
                    
                }
            }
        }
        int rAvg = (int)(rAccum/counter);
        int gAvg = (int)(gAccum/counter);
        int bAvg = (int)(bAccum/counter);
        return toRGB(rAvg,gAvg,bAvg);
    }

    //exports the image
    public static void export() throws IOException
    {
        System.out.print("File name: ");
        String fileNameInput = kb.nextLine();
        String fileName = "generated_images/" + fileNameInput + ".png";
        File f = new File(fileName);
        ImageIO.write(image,"PNG",f);
    }
}