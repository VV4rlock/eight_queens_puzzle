import java.util.Random;

public class recursiveQueens {
    int SIZE = 11; // Размер.

    int[][] board=new int[SIZE][SIZE];
    int results_count = 0; // Количество решений.
    private Random rnd = new Random();
    //private BDD restricted;
    private int queenCount=0;
    private boolean mayBeSolbe=true;

    // Функция showBoard() - отображает доску.
    void showBoard()
    {
        for(int a = 0; a < SIZE; ++a)
        {
            for(int b = 0; b < SIZE; ++b)
            {
                System.out.print((board[a][b]!=0) ? "Q " : ". ");
            }
            System.out.println();
        }
    }

    recursiveQueens(){

    }
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
    public void solve(){
        setQueen(0);

    }

    // Функция tryQueen() - проверяет нет ли уже установленных ферзей,
// по вертикали, диагоналям.
    boolean tryQueen(int x, int y)
    {
        int size=SIZE;
        for(int i=0;i<size; i++){
            if (i!=y && board[x][i]!=0)
                return false;
            if (i!=x && board[i][y]!=0)
                return false;
        }
        int[][] directions = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

        for (int[] vector : directions) {
            int row = x + vector[0];
            int col = y + vector[1];
            while (row >= 0 && row < size && col >= 0 && col < size){
                if (board[row][col]!=0)
                    return false;
                row += vector[0];
                col += vector[1];
                //System.out.println(x+" "+y);
            }
        }
        return true;
    }

    // Функция setQueen() - пробует найти результаты решений.
    void setQueen(int a) // a - номер очередной строки в которую нужно поставить очередного ферзя.
    {
        if(a == SIZE)
        {
            //showBoard();
            System.out.println( "Result #" + (++results_count));
            return; // Опционально.
        }

        for(int i = 0; i < SIZE; ++i)
        {
            // Здесь проверяем, что если поставим в board[a][i] ферзя (единицу),
            // то он будет единственным в этой строке, столбце и диагоналях.
            if(board[a][i]==1)
                setQueen(a+1);
            if(tryQueen(a, i))
            {
                board[a][i] = 1;
                setQueen(a+1);
                board[a][i] = 0;
            }
        }

        return; // Опционально.
    }

    public static void main(String[] args)
    {
        long time = System.currentTimeMillis();
        recursiveQueens r=new recursiveQueens();
        r.setQueen(0);
        time = System.currentTimeMillis() - time;
        System.out.println("\nTime: " + (double)time / 1000.0D + " seconds\n");


    }
}
