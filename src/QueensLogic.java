import com.sun.istack.internal.NotNull;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.CUDDFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class QueensLogic {
    static BDDFactory B;
    static boolean TRACE;
    private int N;
    private BDD[][] X;
    private BDD rules;
    //private BDD solution;
    private int[][] board;
    //private HashSet<Integer> queenPositions = new HashSet<Integer>();
    private Random rnd = new Random();
    //private BDD restricted;
    private int queenCount=0;
    private boolean mayBeSolbe=true;



    public boolean insertQueen(int column, int row) {

        //System.out.println("insert "+column+" "+ row);
        if (board[column][row] != 0) { //if queen is already here, remove it
            board[column][row] = 0;
            queenCount--;
            //removeQueen(row * x + column);
        } else {
            board[column][row] = 1; //not a queen, place one
            queenCount++;
            //addQueen(row * x + column);
        }
        //printboard();
        return true;
    }
    private BDD getRestrictions() {
        BDD res = B.one();
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                if(board[i][j]==1 || board[i][j]==2)
                    res.andWith(B.ithVar(i*N+j));
        return res;
    }

    public void solve(){
        if (!mayBeSolbe)
            return;
        //updateRestrictions();
        BDD b=null;
        if (queenCount==0)
        {
            //int r=rnd.nextInt(63);
            int r=rnd.nextInt(N);
            if (board[r%N][(int)r/N]!=1) {
                board[r % N][(int) r / N] = 2;
            }
            if (rules.restrict(getRestrictions()).pathCount()==0){
                board[r % N][(int) r / N] = 0;
            }
            //System.out.print("cast:"+a.get(rnd.nextInt(a.size())));
            //return;
        }
        b = rules.restrict(getRestrictions()).satOne();

        //System.out.println("all sat:"+b.pathCount());
        //b.printSet();
        if (b==null || b.pathCount()==0)
            return;
        for(int r = 0; r < N; r++) { //foreach column
            for(int c = 0; c < N; c++) { //foreach row
                //if(board[c][r] == 1) continue; //if queen is present, skip this space
                //System.out.println(r+"*x+"+c+", f.ith="+f.ithVar(r * x + c)+" b.restr"+b.restrict(f.ithVar(r * x + c)));
                if(!b.restrict(B.ithVar(r * N + c)).isZero()) { //if we cannot place a queen here, without making the problem unsolvable, add a red cross to the board

                    if (board[r][c] != 1)
                        board[r][c] = 2;
                }
                else{
                    board[r][c]=0;
                }
            }
        }
        recountQueens();


    }

    public void recountQueens(){
        queenCount=0;
        for(int r=0;r<N;r++){
            for(int c=0;c<N;c++){
                if(board[c][r] !=0)
                    queenCount++;
            }
        }
    }

    public void clear_board(){
        for(int r=0;r<N;r++){
            for(int c=0;c<N;c++){
                board[c][r] = 0;
            }
        }
        queenCount=0;

    }
    public void updateBoard(){
        int size=N;
        boolean flag=true;
        for (int x=0;x<size;x++){
            for (int y=0;y<size;y++){
                if (board[x][y]!=0)
                    if (check(x,y)) {
                        if (board[x][y]==1 || board[x][y]==-1)
                            board[x][y] = -1;
                        else
                            board[x][y]=-2;
                        flag=false;
                    }
                    else {
                        if (board[x][y] == 1 || board[x][y] == -1)
                            board[x][y] = 1;
                        else
                            board[x][y] = 2;
                    }
            }
        }
        mayBeSolbe=flag;
    }

    private boolean check(int x,int y){
        int size=N;
        for(int i=0;i<size; i++){
            if (i!=y && board[x][i]!=0)
                return true;
            if (i!=x && board[i][y]!=0)
                return true;
        }
        int[][] directions = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

        for (int[] vector : directions) {
            int row = x + vector[0];
            int col = y + vector[1];
            while (row >= 0 && row < size && col >= 0 && col < size){
                if (board[row][col]!=0)
                    return true;
                row += vector[0];
                col += vector[1];
                //System.out.println(x+" "+y);
            }
        }
        return false;

    }


    QueensLogic(int size){
        initializeFactory();
        initializeGame(size);
    }
    public int getSize(){
        return N;
    }


    public void free(){
        freeAll();
        B.done();
        B = null;

    }
    public static void main(String[] args) {
        TRACE = true;
        int N=8;
        QueensLogic l=new QueensLogic(N);
        l.initializeFactory();
        long time = System.currentTimeMillis();
        l.bdd_dot_to_file(l.rules,"bdd_"+N+"x"+N+".gv");
        time = System.currentTimeMillis() - time;
        System.out.println("Time: " + (double)time / 1000.0D + " seconds");
        BDDFactory.CacheStats cachestats = l.getFactory().getCacheStats();
        if (cachestats != null && cachestats.uniqueAccess > 0) {
            System.out.println(cachestats);
        }
        l.free();
    }
    public void bdd_dot_to_file(@NotNull BDD bdd, String filename){

        //ByteArrayOutputStream baos = new ByteArrayOutputStream();

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
        //System.out.println("new stream!");
        //System.out.println("Foofoofoo!");
        bdd.printDot();
        // Put things back
        System.out.flush();
        System.setOut(old);
        // Show what happened
        //System.out.println("Here: " + baos.toString());
        //System.out.println("str: "+baos.toString());

    }

    public BDDFactory getFactory(){
        return B;
    }
    private void initializeFactory(){
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
    }

    private void initializeGame(int size) {
        this.N=size;
        int j;
        if (B.varNum() < N * N) {
            B.setVarNum(N * N);
        }

        rules = B.one();
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

            rules.andWith(e);
        }

        for(i = 0; i < N; ++i) {
            for(j = 0; j < N; ++j) {
                if (TRACE) {
                    System.out.print("Adding position " + i + "," + j + "   \r");
                }

                build(i, j);
            }
        }

        //return result;
    }

    public void printboard(){
        System.out.print("\nboard:\n");
        for(int r = 0; r < N; r++) { //foreach column
            for (int c = 0; c < N; c++) { //foreach row
                //if (board[c][r] == 1) continue; //if rules is present, skip this space
                //System.out.println(r+"*x+"+c+", f.ith="+f.ithVar(r * x + c)+" b.restr"+b.restrict(f.ithVar(r * x + c)));
                if (board[c][r] == 1) { //if we cannot place a rules here, without making the problem unsolvable, add a red cross to the board
                    //addQueen(r * x + c);
                    System.out.print("Q");
                }
                else{
                    System.out.print(".");
                }
            }
            System.out.print("\n");
        }


    }
    public int[][] getGameBoard() {
        return board;
    }

    public void freeAll() {
        for(int i = 0; i < N; ++i) {
            for(int j = 0; j < N; ++j) {
                X[i][j].free();
            }
        }

        rules.free();
        //solution.free();
    }

    private void build(int i, int j) {
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
        rules.andWith(a);
    }
}
