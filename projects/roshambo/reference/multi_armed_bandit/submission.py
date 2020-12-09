
import pandas as pd
import numpy as np
import json


# base class for all agents, random agent
class agent():
    def initial_step(self):
        return np.random.randint(3)
    
    def history_step(self, history):
        return np.random.randint(3)
    
    def step(self, history):
        if len(history) == 0:
            return self.initial_step()
        else:
            return self.history_step(history)
    
# agent that returns (previousCompetitorStep + shift) % 3
class mirror_shift(agent):
    def __init__(self, shift=0):
        self.shift = shift
    
    def history_step(self, history):
        return (history[-1]['competitorStep'] + self.shift) % 3
    
    
# agent that returns (previousPlayerStep + shift) % 3
class self_shift(agent):
    def __init__(self, shift=0):
        self.shift = shift
    
    def history_step(self, history):
        return (history[-1]['step'] + self.shift) % 3    


# agent that beats the most popular step of competitor
class popular_beater(agent):
    def history_step(self, history):
        counts = np.bincount([x['competitorStep'] for x in history])
        return int(np.argmax(counts))

agents = {
    'mirror_0': mirror_shift(0),
    'mirror_1': mirror_shift(1),  
    'mirror_2': mirror_shift(2),
    'self_1': self_shift(1),  
    'self_2': self_shift(2),
    'popular_beater': popular_beater()
}

    
def multi_armed_bandit_agent (observation, configuration):
    
    # I don't see how to use any global variables, so will save everything to a CSV file
    # Using pandas for this is too much, but it can be useful later and it is convinient to analyze
    def save_history(history, file = 'history.csv'):
        pd.DataFrame(history).to_csv(file, index = False)

    def load_history(file = 'history.csv'):
        return pd.read_csv(file).to_dict('records')
    
    
    def log_step(step = None, history = None, agent = None, competitorStep = None):
        if step is None:
            step = np.random.randint(3)
        if history is None:
            history = []
        history.append({'step': step, 'competitorStep': competitorStep, 'agent': agent})
        save_history(history)
        return step
    
    def update_competitor_step(history, competitorStep):
        history[-1]['competitorStep'] = competitorStep
        return history
        
    
    # load history
    if observation.step == 0:
        history = []
        bandit_state = {k:[1,1] for k in agents.keys()}
    else:
        history = update_competitor_step(load_history(), observation.lastOpponentAction)
        
        # load the state of the bandit
        with open('bandit.json') as json_file:
            bandit_state = json.load(json_file)
        
        # updating bandit_state using the result of the previous step
        if (history[-1]['competitorStep'] - history[-1]['step']) % 3 == 1:
            bandit_state[history[-1]['agent']][1] += 1
        elif (history[-1]['competitorStep'] - history[-1]['step']) % 3 == 2:
            bandit_state[history[-1]['agent']][0] += 1
        else:
            bandit_state[history[-1]['agent']][0] += 0.5
            bandit_state[history[-1]['agent']][1] += 0.5
            
    with open('bandit.json', 'w') as outfile:
        json.dump(bandit_state, outfile)
    
    
    # generate random number from Beta distribution for each agent and select the most lucky one
    best_proba = -1
    best_agent = None
    for k in bandit_state.keys():
        proba = np.random.beta(bandit_state[k][0],bandit_state[k][1])
        if proba > best_proba:
            best_proba = proba
            best_agent = k
        
    step = agents[best_agent].step(history)
    
    return log_step(step, history, best_agent)
