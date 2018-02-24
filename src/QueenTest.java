import com.sun.istack.internal.NotNull;
import net.sf.javabdd.*;

import java.io.*;
import java.util.HashSet;

public class QueenTest {
    static BDDFactory B;
    static boolean TRACE;
    static int N;
    static BDD[][] X;
    static BDD queen;
    static BDD solution;
    static int[][] board;
    private HashSet<Integer> queenPositions = new HashSet<Integer>();
    private int size;

    QueenTest(){;
    }
    public int getSize(){
        return 0;
    }

    public void initializeGame(int size) {
        this.size=size;


    }
    public static void main(String[] args) {
        if (false){//args.length != 1) {
            System.err.println("USAGE:  java NQueens N");
        } else {
            N = 4;//Integer.parseInt(args[0]);
            if (N <= 0) {
                System.err.println("USAGE:  java NQueens N");
            } else {
                TRACE = true;
                long time = System.currentTimeMillis();
                runTest();
                long usedBytes = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                //bdd_dot_to_file(queen,"bdd_"+N+"x"+N+".gv");
                freeAll();
                time = System.currentTimeMillis() - time;
                System.out.println("Time: " + (double)time / 1000.0D + " seconds\nMemory: "+ usedBytes/1048576.0D+"Mb");
                BDDFactory.CacheStats cachestats = B.getCacheStats();
                if (cachestats != null && cachestats.uniqueAccess > 0) {
                    System.out.println(cachestats);
                }

                B.done();
                B = null;
            }
        }
    }

    public void bdd_dot_to_file(@NotNull BDD bdd, String filename){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            return;
            //e.printStackTrace();
        }
        //PrintStream ps = null;

        // IMPORTANT: Save the old System.out!
        PrintStream old = System.out;
        // Tell Java to use your special stream
        System.setOut(ps);
        // Print some output: goes to your special stream
        System.out.println("new stream!");
        //System.out.println("Foofoofoo!");
        bdd.printDot();
        // Put things back
        System.out.flush();
        System.setOut(old);
        // Show what happened
        //System.out.println("Here: " + baos.toString());
        System.out.println("str: "+baos.toString());

    }

    public static double runTest() {
        int j;
        if (B == null) {
            String numOfNodes = System.getProperty("bddnodes");
            if (numOfNodes == null) {
                j = (int)Math.pow(4.42D, (double)(N - 6)) * 1000;
            } else {
                j = Integer.parseInt(numOfNodes);
            }

            String cache = System.getProperty("bddcache");
            int cacheSize;
            if (cache == null) {
                cacheSize = 1000;
            } else {
                cacheSize = Integer.parseInt(cache);
            }

            j = Math.max(1000, j);
            B = BDDFactory.init(j,cacheSize);//BDDFactory.init(j, cacheSize);
        }

        if (B.varNum() < N * N) {
            B.setVarNum(N * N);
        }

        queen = B.one();
        X = new BDD[N][N];
        board=new int[N][N];

        int i;
        for(i = 0; i < N; ++i) {
            for(j = 0; j < N; ++j) {
                X[i][j] = B.ithVar(i * N + j);
            }
        }

        for(i = 0; i < N; ++i) {
            BDD e = B.zero();

            for(j = 0; j < N; ++j) {
                e.orWith(X[i][j].id());
            }

            queen.andWith(e);
        }

        for(i = 0; i < N; ++i) {
            for(j = 0; j < N; ++j) {
                if (TRACE) {
                    System.out.print("Adding position " + i + "," + j + "   \r");
                }

                build(i, j);
            }
        }
        solution = queen.satOne();
        double result = queen.satCount();
        if (TRACE) {
            System.out.println("There are " + (long)result + " solutions.");
            double result2 = solution.satCount();
            System.out.println("Here is " + (long)result2 + " solution:");
            solution.printSet();
            System.out.println();
        }
        //printboard();
        return result;
    }
    private BDD getRestrictions() {
        BDD res = B.one();
        for (int q :  queenPositions) {
            res.andWith(B.ithVar(q));
        }

        return res;
    }

    public static void printboard(){
        System.out.print("\nSolution on the board:\n");
        for(int r = 0; r < N; r++) { //foreach column
            for (int c = 0; c < N; c++) { //foreach row
                //if (board[c][r] == 1) continue; //if queen is present, skip this space
                //System.out.println(r+"*x+"+c+", f.ith="+f.ithVar(r * x + c)+" b.restr"+b.restrict(f.ithVar(r * x + c)));
                if (!solution.restrict(B.ithVar(r * N + c)).isZero()) { //if we cannot place a queen here, without making the problem unsolvable, add a red cross to the board
                    board[c][r] = 1;
                    //addQueen(r * x + c);
                    System.out.print("Q");
                }
                else{
                    System.out.print(".");
                    board[c][r]=0;
                }
            }
            System.out.print("\n");
        }


    }

    public static void freeAll() {
        for(int i = 0; i < N; ++i) {
            for(int j = 0; j < N; ++j) {
                X[i][j].free();
            }
        }

        queen.free();
        solution.free();
    }

    static void build(int i, int j) {
        BDD a = B.one();
        BDD b = B.one();
        BDD c = B.one();
        BDD d = B.one();

        BDD u;
        for(int l = 0; l < N; ++l) {
            if (l != j) {
                u = X[i][l].apply(X[i][j], BDDFactory.nand);
                a.andWith(u);
            }
        }

        int k;
        for(k = 0; k < N; ++k) {
            if (k != i) {
                u = X[i][j].apply(X[k][j], BDDFactory.nand);
                b.andWith(u);
            }
        }

        int ll;
        for(k = 0; k < N; ++k) {
            ll = k - i + j;
            if (ll >= 0 && ll < N && k != i) {
                u = X[i][j].apply(X[k][ll], BDDFactory.nand);
                c.andWith(u);
            }
        }

        for(k = 0; k < N; ++k) {
            ll = i + j - k;
            if (ll >= 0 && ll < N && k != i) {
                u = X[i][j].apply(X[k][ll], BDDFactory.nand);
                d.andWith(u);
            }
        }

        c.andWith(d);
        b.andWith(c);
        a.andWith(b);
        queen.andWith(a);
    }
}
