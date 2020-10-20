import gym
from gym import error, spaces, utils
from gym.utils import seeding
import random

# Additional imports from FrozenLake
import sys
from contextlib import closing
from six import StringIO

HOLD = 0
ROLL = 1

class ApproachNEnv(gym.Env): # See Env in https://github.com/openai/gym/blob/master/gym/core.py
    """Approach n OpenAI Gym Environment

    The dice game "Approach n" is played with 2 players and a single standard 6-sided die (d6). The goal is to approach a total of n without exceeding it.  The first player roll a die until they either (1) "hold" (i.e. end their turn) with a roll sum less than or equal to n, or (2) exceed n and lose.  If the first player holds at exactly n, the first player wins immediately. If the first player holds with less than n, the second player must roll until the second player's roll sum (1) exceeds the first player's roll sum without exceeding n and wins, or (2) exceeds n and loses.  Note that the first player is the only player with a choice of play policy.  For n >= 10, the game is nearly fair, i.e. can be won approximately half of the time with optimal decisions.

    - Non-terminal states: Player 1 turn totals less than n, state n indicates a terminal state (win/loss)
    - Actions: 0 (HOLD), 1 (ROLL)
    - Rewards: +1 for transition to terminal state with win, 
               -1 for transition to terminal state with loss,
                0 otherwise
    - Transitions: Each die roll of 1 - 6 is equiprobable

    Assign environment field n directly to change the default from 10
    """
    
    metadata = {'render.modes': ['human', 'ansi']}
    reward_range = (float('0'), float('1'))
    n = 10 # assign directly to change n for Approach n
    
    def __init__(self):
        self.p1Rolls = []
        self.p2Rolls = []
        self.done = False


    def step(self, action):
        reward = 0
        newState = 0
        p1Total = sum(self.p1Rolls)

        if self.done: # From Env: "further step() calls will return undefined results"
            raise RuntimeError('step already returned done == True, so subsequent step call -> undefined result')

        elif action == ROLL:
            roll = random.randint(1,6)
            self.p1Rolls.append(roll)
            p1Total += roll
            if p1Total > self.n: # bust
                newState = self.n
                self.done = True
                #reward = 0
                reward = -1 # new in v1
            elif p1Total == self.n: # immediate win
                newState = self.n
                self.done = True
                reward = 1
            else:
                newState = p1Total

        elif action == HOLD:
            p2Total = 0
            while p2Total <= p1Total:
                 roll = random.randint(1,6)
                 self.p2Rolls.append(roll)
                 p2Total += roll
            newState = self.n
            #reward = 0 if p2Total <= self.n else 1
            reward = -1 if p2Total <= self.n else 1 # new in v1
            self.done = True

        return (newState, reward, self.done, {})
            
        
    def reset(self):
        self.p1Rolls = []
        self.p2Rolls = []
        self.done = False
        return 0

    
    def render(self, mode='human', close=False):
        outfile = StringIO() if mode == 'ansi' else sys.stdout

        p1Total = sum(self.p1Rolls)
        p2Total = sum(self.p2Rolls)
        outfile.write("Player 1 hasn't taken a turn yet\n" if (p1Total == 0 and not self.done) else ('Player 1 rolls: ' + '+'.join(str(i) for i in self.p1Rolls) + ' = {}\n'.format(p1Total)))
        if len(self.p2Rolls) > 0:
            outfile.write('Player 2 rolls: ' + '+'.join(str(i) for i in self.p2Rolls) + ' = {}\n'.format(p2Total))
        outfile.write('Player 1 to HOLD(0)/ROLL(1)\n' if not self.done else \
                      ('Player 1 wins.\n' if (p1Total == self.n or p2Total > self.n) \
                       else 'Player 2 wins.\n'))
        if mode != 'human':
            with closing(outfile):
                return outfile.getvalue()

    
    def seed(self, seed=None):
        if seed:
            random.seed(seed)
        return seed
