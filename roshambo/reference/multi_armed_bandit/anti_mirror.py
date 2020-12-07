
import pandas as pd
    
def anti_copy_opponent_agent (observation, configuration):
    
    # I don't see how to use any global variables, so will save everything to a CSV file
    # Using pandas for this is too much, but it can be useful later
    def save_history(history, file = 'history2.csv'):
        pd.DataFrame(history).to_csv(file, index = False)

    def load_history(file = 'history2.csv'):
        return pd.read_csv(file).to_dict('records')
    
    if observation.step == 0:
        history = [{'step': 1, 'competitorStep': None}]
        save_history(history)
        return 1
    else:
        history = load_history()
        history[-1]['competitorStep'] = observation.lastOpponentAction
        step = (history[-1]['step'] + 1) % 3
        history.append({'step': step, 'competitorStep': None})
        save_history(history)
        return step
