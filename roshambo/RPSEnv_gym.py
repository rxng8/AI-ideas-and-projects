# %%

import gym
from gym import error, spaces, utils
from gym.utils import seeding
import random
import numpy as np

# Additional imports from FrozenLake
import sys
from contextlib import closing
from six import StringIO


class RPSEnv(gym.Env): # See Env in https://github.com/openai/gym/blob/master/gym/core.py
    """RPS OpenAI Gym Environment

    - Non-terminal states: Player 1 turn totals less than n, state n indicates a terminal state (win/loss)
    - Actions: 0 (Rock), 1 (Paper), 2 (Scissor)
    - Rewards: +1 for transition to terminal state with win 
               -1 for transition to terminal state with loss
    - Transitions: transition in each move.
    - Terminal state: After complete k-th number of games
    """
    
    metadata = {'render.modes': ['human', 'ansi']}
    reward_range = (float('0'), float('1'))
    
    ROCK = 0
    PAPER = 1
    SCISSOR = 2

    def __init__(self, n: int=1000):
        """[summary]

        Args:
            n (int, optional): [description]. Defaults to 1000.
        """
        super().__init__()
        self.n = n
        self.turn = 0
        # Define action and observation space
        n_actions = 3
        self.action_space = spaces.Discrete(n_actions)
        self.observation_space = spaces.Box(low=0, high=2,
                                        shape=(1,), dtype=np.int32)

        self.done = False
        self.p1 = 0
        self.p2 = 0
        


    def step(self, action):
        if action == self.ROCK:
            pass
        pass
        
    def reset(self):
        self.turn = 0
        self.done = False
        self.p1 = 0
        self.p2 = 0


    def render(self, mode='human', close=False):
        pass

    def seed(self, seed=None):
        pass