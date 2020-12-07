
import numpy as np
import pandas as pd
import random
from typing import List

N_ACTIONS = 3
history = []
cur_action = -1
strategy = [0] * N_ACTIONS
policies = [0.3333] * N_ACTIONS
regret = [0] * N_ACTIONS

past_actions = [0] * N_ACTIONS
eps = 0.2

def cfr_agent(observation, configuration):
  global cur_action
  global past_actions
  global N_ACTIONS
  global history
  # Strategy
  global strategy
  # Q table
  global policies
  global regret
  global action_times
  global eps

  def eval_reward(action, op_action):
    if action == -1:
      return 0
    if action == op_action:
      return 0
    else:
      if action - op_action < 0:
        if action + 2 == op_action:
          return 1
      elif action - op_action == 1:
        return 1
    return -1

  def eps_greedy_action():
    # Explore at random:
    if np.random.random() < eps:
      # set cur action
      cur_action = random.randint(0, 2)
    else:
      #Exploit
      cur_action = int(np.argmax(policies))
    past_actions[cur_action] += 1
    return cur_action
      
  def update_policies(action, reward):
    if past_actions[action] == 0:
      policies[action] += 2.0 * (reward - policies[action])
    else:
      policies[action] += 1.0 / past_actions[action] * (reward - policies[action])

  if observation.step == 0:
    return 0
  if cur_action != -1:
    history.append([cur_action, observation.lastOpponentAction])
    # print(history[len(history) - 1], "reward: ", eval_reward(cur_action, observation.lastOpponentAction))

  # set cur action
  if cur_action != -1:  
    update_policies(cur_action, eval_reward(cur_action, observation.lastOpponentAction))
  cur_action = eps_greedy_action()
  past_actions[cur_action] += 1
  
  return cur_action