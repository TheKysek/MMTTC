import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game
{
    Sign[][] board;

    Sign winner;
    Sign currentPlayer = Sign.X;
    int depth;
    boolean over = false;

    Game(Sign[][] initBoard, int initDepth, Sign current)
    {
        depth = initDepth + 1;
        currentPlayer = current;

        board = new Sign[3][3];

        for (int i = 0; i < 3; i++)
        {
            System.arraycopy(initBoard[i], 0, board[i], 0, initBoard.length);
        }
    }

    Game()
    {
        board = new Sign[3][3];
        depth = 0;
    }

    public boolean set(int x, int y, Sign sign)
    {
        if (isGameOver() || sign != currentPlayer)
        {
            return false;
        }

        if (board[x][y] == null)
        {
            board[x][y] = sign;
            currentPlayer = reverse(sign);
            return true;
        } else
        {
            return false;
        }
    }

    public Sign checkWin()
    {
        // RIP best practices
        if (winner != null)
        {
            return winner;
        }
        for (int i = 0; i < 3; i++)
        {
            if (board[i][0] != null && board[i][0] == board[i][1] && board[i][1] == board[i][2])
            {
                winner = board[i][0];
            }
            if (board[0][i] != null && board[0][i] == board[1][i] && board[1][i] == board[2][i])
            {
                winner = board[0][i];
            }
        }
        if (board[0][0] != null && board[0][0] == board[1][1] && board[1][1] == board[2][2])
        {
            winner = board[0][0];
        }
        if (board[0][2] != null && board[0][2] == board[1][1] && board[1][1] == board[2][0])
        {
            winner = board[0][2];
        }
        return winner;
    }

    public Sign getCurrentPlayer()
    {
        return currentPlayer;
    }

    public Sign reverse(Sign sign)
    {
        if (sign == Sign.X)
        {
            return Sign.O;
        } else
        {
            return Sign.X;
        }
    }

    public int score(Sign player)
    {
        if (winner == player)
        {
            return 10 - depth;
        } else if (winner == reverse(player))
        {
            return -10 + depth;
        } else
        {
            return 0;
        }
    }

    public Sign get(int x, int y)
    {
        return board[x][y];
    }

    public int minMax()
    {
        if (isGameOver())
        {
            //System.out.println("Simulation completed");
            return score(Sign.X);
        }

        List<Integer[]> moves = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (board[i][j] == null)
                {
                    Game tmpGame = new Game(board, depth, currentPlayer);
                    tmpGame.set(i, j, currentPlayer);
                    int tmpScore = tmpGame.minMax();

                    Integer[] move = new Integer[2];
                    move[0] = i;
                    move[1] = j;

                    moves.add(move);
                    scores.add(tmpScore);
                }
            }
        }

        int bestScore;

        if(currentPlayer == Sign.X)
        {
            bestScore = Collections.max(scores);
        } else
        {
            bestScore = Collections.min(scores);
        }

        Integer[] bestMove = moves.get(scores.indexOf(bestScore));

        set(bestMove[0], bestMove[1], currentPlayer);
        checkWin();

        return bestScore;
    }

    public boolean isGameOver()
    {
        checkWin();
        if (over || winner != null)
        {
            over = true;
            return true;
        }
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (board[i][j] == null)
                {
                    return false;
                }
            }
        }
        over = true;
        return true;

    }

    public enum Sign
    {
        X, O
    }
}
