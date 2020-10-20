# Approach n OpenAI Gym Environment

The dice game "Approach n" is played with 2 players and a single standard 6-sided die (d6). The goal is to approach a total of n without exceeding it.  The first player roll a die until they either (1) "hold" (i.e. end their turn) with a roll sum less than or equal to n, or (2) exceed n and lose.  If the first player holds at exactly n, the first player wins immediately. If the first player holds with less than n, the second player must roll until the second player's roll sum (1) exceeds the first player's roll sum without exceeding n and wins, or (2) exceeds n and loses.  Note that the first player is the only player with a choice of play policy.  For n >= 10, the game is nearly fair, i.e. can be won approximately half of the time with optimal decisions.

- Non-terminal states: Player 1 turn totals less than n (n is a terminal state)
- Actions: 0 (HOLD), 1 (ROLL)
- Rewards: +1 for transition to terminal state with win, 
           -1 for transition to terminal state with loss,
            0 otherwise
- Transitions: Each die roll of 1 - 6 is equiprobable

