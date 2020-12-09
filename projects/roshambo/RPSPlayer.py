from abc import ABC

class RPSPlayer():
    def __init__(self, n_turns=10):
        pass

    def step(self) -> int:
        return NotImplemented

    def report(self, turn: int, opponent_action: int, reward: int):
        pass

    def terminal(self, win: bool):
        pass

import random

class RandomPlayer(RPSPlayer):

    def __init__(self):
        super().__init__()

    def step(self) -> int:
        return random.randint(0,2)
        # return 1

    def report(self, turn: int, opponent_action: int, reward: int):
        pass

    def terminal(self, win: bool):
        pass

class MimicPlayer(RPSPlayer):

    def __init__(self):
        super().__init__()
        self.cur_action = -1
        self.history = []

    def step(self) -> int:
        if self.history:
            return self.history[len(self.history) - 1][1]
        return random.randint(0, 2)

    def report(self, turn: int, opponent_action: int, reward: int):
        self.history.append((self.cur_action, opponent_action))
        pass

    def terminal(self, win: bool):
        pass

class HistoryPlayer(RPSPlayer):

    def __init__(self):
        super().__init__()
        self.history = []
        self.cur_action = -1

    def step(self) -> int:
        r = random.randint(0, 2)
        self.cur_action = r
        return r

    def report(self, turn: int, opponent_action: int, reward: int):
        self.history.append((cur_action, opponent_action))
        pass

    def terminal(self, win: bool):
        pass

class RepeatingPlayer(RPSPlayer):

    def __init__(self):
        super().__init__()
        self.history = []
        self.cur_action = -1
        self.fixed_action = random.randint(0,2)
    def step(self) -> int:        
        return self.fixed_action

    def report(self, turn: int, opponent_action: int, reward: int):
        self.history.append((self.cur_action, opponent_action))
        pass

    def terminal(self, win: bool):
        pass

from typing import List, Dict
import numpy as np

class CFRPlayer(RPSPlayer):

    N_ACTIONS = 3

    def __init__(self):
        super().__init__()
        self.history = []
        self.cur_action = -1
        self.strategy = [0] * self.N_ACTIONS
        self.policies = [0] * self.N_ACTIONS
        self.regret = [0] * self.N_ACTIONS

    def step(self) -> int:
        strategy = self.match_regret()
        action = self.get_action(strategy)
        self.cur_action = action
        return action

    def get_action(self, strategy: List[int]):
        return np.argmax(strategy)

    def match_regret(self) -> List:
        """ Normalize the regret and put them into strategy matrix

        Returns:
            List: [description]
        """
        sum = 0
        for i in range (self.N_ACTIONS):
            self.strategy[i] = 0 if self.regret[i] <= 0 else self.regret[i]
            sum += self.strategy[i]

        for i in range (self.N_ACTIONS):
            if sum > 0:
                self.strategy[i] /= sum
            else:
                self.strategy[i] = 1 / self.N_ACTIONS
        return self.strategy

    def report(self, turn: int, opponent_action: int, reward: int):
        self.history.append((self.cur_action, opponent_action))
        actionUtility = [0] * 3
        actionUtility[opponent_action] = 0
        actionUtility[0 if opponent_action == self.N_ACTIONS - 1 else opponent_action + 1] = 1
        actionUtility[self.N_ACTIONS - 1 if opponent_action == 0 else opponent_action - 1] = -1

        # accumulate the total regret 
        for i in range(self.N_ACTIONS):
            self.regret[i] += actionUtility[i] - actionUtility[self.cur_action]

        print(str(self.strategy))
        print(str(self.regret))

    def terminal(self, win: bool):
        
        pass


class MDPPlayer(RPSPlayer):

    N_ACTIONS = 3

    def __init__(self):
        super().__init__()
        self.history = []
        self.cur_action = -1
        self.strategy = [0] * self.N_ACTIONS
        self.policies = [0] * self.N_ACTIONS
        self.regret = [0] * self.N_ACTIONS

    def step(self) -> int:
        strategy = self.match_regret()
        action = self.get_action(strategy)
        self.cur_action = action
        return action

    def get_action(self, strategy: List[int]):
        return np.argmax(strategy)

    def match_regret(self) -> List:
        """ Normalize the regret and put them into strategy matrix

        Returns:
            List: [description]
        """
        sum = 0
        for i in range (self.N_ACTIONS):
            self.strategy[i] = 0 if self.regret[i] <= 0 else self.regret[i]
            sum += self.strategy[i]

        for i in range (self.N_ACTIONS):
            if sum > 0:
                self.strategy[i] /= sum
            else:
                self.strategy[i] = 1 / self.N_ACTIONS
        return self.strategy

    def report(self, turn: int, opponent_action: int, reward: int):
        self.history.append((self.cur_action, opponent_action))
        actionUtility = [0] * 3
        actionUtility[opponent_action] = 0
        actionUtility[0 if opponent_action == self.N_ACTIONS - 1 else opponent_action + 1] = 1
        actionUtility[self.N_ACTIONS - 1 if opponent_action == 0 else opponent_action - 1] = -1

        # accumulate the total regret 
        for i in range(self.N_ACTIONS):
            self.regret[i] += actionUtility[i] - actionUtility[self.cur_action]

        print(str(self.strategy))
        print(str(self.regret))

    def terminal(self, win: bool):
        
        pass