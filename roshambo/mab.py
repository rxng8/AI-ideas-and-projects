
from typing import List, Tuple
import numpy as np
import random

class PlayData():
  def __init__ (history: List[Tuple]=None):
    """
      List of Tuple. Sequence of turn action, one turn action include (our action, opponent action)
    """

class Strategy():

  def __init__():
    pass

  @staticmethod
  def get_counter_action(action: int) -> int:
    counter = [0] * 3
    counter[action + 1 if action < 2 else 0] = 1
    return int(np.argmax(counter))
  
  @staticmethod
  def get_reward(action: int, op_action: int):
    if action == op_action:
      return 0
    if Strategy.get_counter_action(action) == op_action:
      return - 1
    return 1

  @staticmethod
  def mimic(observation, configuration):
    if observation.step == 0:
      return random.randint(0,2)
    else:
      return observation.lastOpponentAction

  @staticmethod
  def counteract_1st(observation, configuration):
    if observation.step == 0:
      return random.randint(0,2)
    else:
      return Strategy.get_counter_action(observation.lastOpponentAction)

  @staticmethod
  def counteract_2nd(observation, configuration):
    if observation.step == 0:
      return random.randint(0,2)
    else:
      return Strategy.get_counter_action(Strategy.get_counter_action(observation.lastOpponentAction))

  @staticmethod
  def counteract_3rd(observation, configuration):
    if observation.step == 0:
      return random.randint(0,2)
    else:
      return Strategy.get_counter_action(\
              Strategy.get_counter_action(\
                Strategy.get_counter_action(\
                  observation.lastOpponentAction)))

############

pool = {0: Strategy.mimic,
        1: Strategy.counteract_1st,
        2: Strategy.counteract_2nd,
        3: Strategy.counteract_3rd}
arm_weights = [0] * len(pool)
eps = 0.2
cur_action = -1
cur_arm = -1
def multi_armed_bandit(observation, configuration):
  global pool
  global eps
  global arm_weights
  global cur_action
  global cur_arm

  if cur_action != -1:
    # calculate rewards and update weights
    reward = Strategy.get_reward(cur_action, observation.lastOpponentAction)
    # update policies
    arm_weights[cur_arm] += reward

  random_explore = np.random.random()
  if random_explore < eps:
    cur_arm = random.randint(0, len(pool) - 1)
    cur_action = pool[cur_arm](observation, configuration)
    return cur_action
  else:
    cur_arm = int(np.argmax(arm_weights))
    cur_action = pool[cur_arm](observation, configuration)
    return cur_action
  