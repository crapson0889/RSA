package rsa;

import java.math.BigInteger;
import java.util.Random;

public class GetKeys {
    
    private static GetKeys instance = null;
    private int p;
    private int q;
    private int e;
    private int product;
    
    public GetKeys (int newP, int newQ, int newE, String sourceFile)
    {   
        boolean pIsPrime = true;
        boolean qIsPrime = true;
        
        p = newP;
        q = newQ;
        e = newE;
        
        SelectFrame eSelectFrame = new SelectFrame();
        
        if(Integer.toBinaryString(p).length() > 12 && Integer.toBinaryString(q).length() > 12)
        {
            if(Integer.toBinaryString(p).length() > 12)
            {
                System.out.println("p is too many bits");
            }
            if(Integer.toBinaryString(q).length() > 12)
            {
                System.out.println("q is too many bits");
            }
        }
        else
        {

            System.out.println("p: " + String.format("%12s",Integer.toBinaryString(p)).replace(' ','0'));
            System.out.println("q: " + String.format("%12s",Integer.toBinaryString(q)).replace(' ','0'));

            if(p % 2 == 0)
            {
                System.out.println("p NOT PRIME! Divisible by 2");
                pIsPrime = false;
            }
            else
            {
                for(int i = 3; i < p; i = i + 2)
                {
                    if(p % i == 0)
                    {
                        System.out.println("p NOT PRIME! Divisible by " + i);
                        pIsPrime = false;
                        break;
                    }
                }
            }
            if(q % 2 == 0)
            {
                System.out.println("q NOT PRIME! Divisible by 2");
                qIsPrime = false;
            }
            else
            {
                for(int i = 3; i < q; i = i + 2)
                {
                    if(q % i == 0)
                    {
                        System.out.println("q NOT PRIME! Divisible by " + i);
                        qIsPrime = false;
                        break;
                    }
                }
            }
        }
        
        if(pIsPrime && qIsPrime)
        {
            System.out.println("Good to go");
            
            product = (p - 1)*(q - 1);
            System.out.println("(p-1)(q-1) = " + product);
            
            //This was an idea I had going to let the user select an e for the
            //  key but it was taking too long to run on bigger numbers...
            //  Now I just select the first e that works starting from 2048
            /*
            int[] e = new int[product];     //Array of possible e's
            int index = 0;                  //Current index in array of possible e's
            
            //Find possible e's
            //e must have no common divisors as product
            boolean relativelyPrime;
            for(int i = 3; i <= product; i ++)
            {
                relativelyPrime = true;
                //System.out.println("i: " + i + "; product % i: " + product % i);
                if(product % i == 0)
                {
                    relativelyPrime = false;
                }
                if(i % 2 == 0 && product % 2 == 0)
                {
                    relativelyPrime = false;
                }
                for(int j = 3; j < i; j = j+2)
                {
                    if(i % j == 0 && product % j == 0)
                    {
                        relativelyPrime = false;
                    }
                }
                if(relativelyPrime)
                {
                    e[index] = i;
                    index++;
                }
            }
            
            //Print possible e's
            for(int i = 0; i < e.length; i++)
            {
                if(e[i] != 0)
                {
                    //System.out.print(e[i] + " ");
                }
                else
                {
                    System.out.println();
                    break;
                }
            }
            
            instance = this;
           
            //Need to select one of the given e's
            eSelectFrame.setData(e);
            eSelectFrame.setVisible(true);
            */
            
            //Find possible e's
            //e must have no common divisors as product 
            if(e != -1)
            {
                boolean relativelyPrime = true;
                if(product % e == 0)
                {
                    relativelyPrime = false;
                }
                if(e % 2 == 0 && product % 2 == 0)
                {
                    relativelyPrime = false;
                }
                for(int i = 3; i < e; i = i+2)
                {
                    if(e % i == 0 && product % i == 0)
                    {
                        relativelyPrime = false;
                    }
                }
                if(relativelyPrime)
                {
                    setKey(p, q, e);
                }
                else
                {
                    System.out.println("e is not relatively prime");
                }
            }
            else
            {
                boolean relativelyPrime;
                Random rand = new Random();
                for(int i = rand.nextInt(100); i <= product; i ++)
                {
                    relativelyPrime = true;
                    if(product % i == 0)
                    {
                        relativelyPrime = false;
                    }
                    if(i % 2 == 0 && product % 2 == 0)
                    {
                        relativelyPrime = false;
                    }
                    for(int j = 3; j < i; j = j+2)
                    {
                        if(i % j == 0 && product % j == 0)
                        {
                            relativelyPrime = false;
                        }
                    }
                    if(relativelyPrime)
                    {
                        setKey(p, q, i);
                        break;
                    }
                }
            }
        }
    }    
    
    public static GetKeys getInstance()
    {
        if(instance == null) {
            //instance = new GetKeys();
        }
        return instance;
    }
    
    private void setKey(int p, int q, int e)
    {
        int n = p*q;
       
        BigInteger bigE = new BigInteger(e+"");
        BigInteger bigProduct = new BigInteger(product+"");
        int d = bigE.modInverse(bigProduct).intValue();
        System.out.println("e: " + e);
        System.out.println("d: " + d);
        PrimaryFrame.getInstance().setValues(p, q, n, e, d);
    }
}
