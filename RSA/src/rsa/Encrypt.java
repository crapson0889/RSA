/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 *
 * @author crapson
 */
public class Encrypt {
    
    private NotificationFrame nf = new NotificationFrame();
    
    private int ep = 0;
    private int eq = 0;
    private int pinv = 0;
    private int dp = 0;
    private int dq = 0;
    private int qinv = 0;
    
    public Encrypt(String sourceFile, int p, int q, int n, int e, int d, boolean encrypt, boolean crt)
    {
        long startTime = System.nanoTime();
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(sourceFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String outputFile = "";
            if(encrypt)
            {
                String f = sourceFile.split("/")[sourceFile.split("/").length - 1];
                outputFile = sourceFile.substring(0, sourceFile.length() - f.length()) + f + ".myRSA";
                System.out.println(outputFile);
            }
            else
            {
                String f = sourceFile.split("/")[sourceFile.split("/").length - 1];
                f = f.substring(0, f.length() - 6);
                outputFile = sourceFile.substring(0, sourceFile.length() - (f.length() + 6)) + "D" + f;
                System.out.println(outputFile);
            }
            
            FileOutputStream out = new FileOutputStream(outputFile);
            
            //Current source bytes
            int[] b = new int[3];
            //Encrypted bytes
            int[] eb = new int[3];
            //Count bytes in case number of bytes in file is 
            int byteCount;
            
            /*p = 61;
            q = 53;
            d = 2753;*/
            
            if(crt)
            {
                CRTPrecompile(p, q, e, d);
            }
            
            //Read File character By character
            while (in.available() != 0)   {
                byteCount = 0;
                for(int i = 0; i < 3; i++)
                {
                    if(in.available() != 0)
                    {
                        b[i] = in.readUnsignedByte();
                        byteCount++;
                    }
                    else
                    {
                        b[i] = 0;
                    }
                }
                
                if(encrypt)
                {
                    if(crt)
                    {
                        eb = CRTEncrypt(b, p, q, n, e, d);
                    }
                    else
                    {
                        eb = encryptBytes(b, p, q, n, e, d);
                    }
                }
                else
                {
                    if(crt)
                    {
                        eb = CRTDecrypt(b, p, q, n, e, d);
                    }
                    else
                    {
                        eb = decryptBytes(b, p, q, n, e, d);
                    }
                }
                
                for(int i = 0; i < byteCount; i++)
                {
                    //System.out.print(eb[i] + " ");
                    out.write((int)eb[i]);
                }
                //System.out.println("\n");
            }
            //Close the input stream
            in.close();
            out.close();
            
            long endTime = System.nanoTime();
            
            String notification = "Execution time: " + ((endTime - startTime)/ 1000000000.0) + " seconds\n";
            nf.setNotification(notification);
        }
        catch (Exception exception){//Catch exception if any
            System.err.println("Error: " + exception);
            System.err.println("Error: " + exception.getMessage());
        }
    }
    
    private int[] encryptBytes(int[] b, int p, int q, int n, int e, int d)
    {
        int[] eb = new int[3];
        
        int source = (b[0] << 16) + (b[1] << 8) + b[2];
        //System.out.println("source:    " + source);
        //System.out.println("source:    " + String.format("%24s",Integer.toBinaryString(source)).replace(' ','0'));
        
        //System.out.println(source);
        
        int encrypted = modPower(source, e, n);
        //System.out.println("encrypted: " + encrypted);
        //System.out.println("encrypted: " + String.format("%24s",Integer.toBinaryString(encrypted)).replace(' ','0'));
        
        //System.out.println(encrypted + "\n");
        
        eb[2] = /*(byte)*/ (encrypted % 256);
        encrypted = encrypted >> 8;
        eb[1] = /*(byte)*/ (encrypted % 256);
        encrypted = encrypted >> 8;
        eb[0] = /*(byte)*/ encrypted;
        
        
        /*for(int i = 0; i < 3; i ++)
        {
            //System.out.print(String.format("%8s",Integer.toBinaryString(eb[i])).replace(' ','0'));
            System.out.print(eb[i] + " ");
        }
        System.out.println();*/
        
        return eb;
    }
    
    private int[] decryptBytes(int[] b, int p, int q, int n, int e, int d)
    {
        int[] db = new int[3];
        
        int source = (b[0] << 16) + (b[1] << 8) + b[2];
        
        //System.out.println("source:    " + source);
        //System.out.println("source:    " + String.format("%24s",Integer.toBinaryString(source)).replace(' ','0'));
        
        int decrypted = modPower(source, d, n);
        //System.out.println("decrypted: " + decrypted);
        //System.out.println(/*"decrypted: " + */String.format("%24s",Integer.toBinaryString(decrypted)).replace(' ','0'));
        
        db[2] = /*(byte)*/ (decrypted % 256);
        decrypted = decrypted >> 8;
        db[1] = /*(byte)*/ (decrypted % 256);
        decrypted = decrypted >> 8;
        db[0] = /*(byte)*/ decrypted;
        
        /*for(int i = 0; i < 3; i ++)
        {
            //System.out.print(String.format("%8s",Integer.toBinaryString(db[i])).replace(' ','0'));
            //System.out.print(db[i] + " ");
            System.out.print((char)db[i]);
        }
        System.out.println();*/
        
        
        
        return db;
    }
    
    private void CRTPrecompile(int p, int q, int e, int d)
    {
        dp = d % (p - 1);
        dq = d % (q - 1);
        BigInteger bigP = new BigInteger(p+"");
        BigInteger bigQ = new BigInteger(q+"");
        qinv = bigQ.modInverse(bigP).intValue();
        
        ep = e % (p - 1);
        eq = e % (q - 1);
        pinv = bigP.modInverse(bigQ).intValue();
    }
    
    private int[] CRTEncrypt(int[] b, int p, int q, int n, int e, int d)
    {
        int[] db = new int[3];
        
        int source = (b[0] << 16) + (b[1] << 8) + b[2];
        
        int c1 = modPower(source, ep, p);
        int c2 = modPower(source, eq, q);
        int h = (pinv * (c1 + p - c2)) % p;
        int encrypted = c2 + h * q; 
        
        db[2] = /*(byte)*/ (encrypted % 256);
        encrypted = encrypted >> 8;
        db[1] = /*(byte)*/ (encrypted % 256);
        encrypted = encrypted >> 8;
        db[0] = /*(byte)*/ encrypted;
        
        return db;
    }
    
    private int[] CRTDecrypt(int[] b, int p, int q, int n, int e, int d)
    {
        int[] db = new int[3];
        
        int source = (b[0] << 16) + (b[1] << 8) + b[2];
        
        int m1 = modPower(source, dp, p);
        int m2 = modPower(source, dq, q);
        int h = (qinv * (m1 + p - m2)) % p;
        System.out.println(-304 % 61);
        int decrypted = m2 + h * q; 
        
        db[2] = /*(byte)*/ (decrypted % 256);
        decrypted = decrypted >> 8;
        db[1] = /*(byte)*/ (decrypted % 256);
        decrypted = decrypted >> 8;
        db[0] = /*(byte)*/ decrypted;
        
        return db;
    }
    
    private int modPower(int base, int exponent, int modulus)
    {
        double result = 1;
        for(int exp = 1; exp <= exponent; exp++)
        {
            result = (result * base);
            result = result % modulus;
        }
        
        return (int) result;
    }
    
}
