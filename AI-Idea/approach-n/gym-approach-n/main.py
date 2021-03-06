# %%

import gym
import gym_approach_n
import random
import time
import numpy as np
from IPython.display import clear_output

# %%

env = gym.make("approach-n-v1")

env.n = n = 10 # You can try out different value of n here.
action_space_size = 2 # 0=HOLD, 1=ROLL
state_space_size = n + 1 # nonterminal Player 1 totals 0 through
                         # (n - 1); terminal state is n


q_table = np.zeros((state_space_size, action_space_size))

print(q_table)


# %%



num_episodes = 10000
max_steps_per_episode = 100

learning_rate = 0.1
discount_rate = 0.99

# exploration_rate = 1
# max_exploration_rate = 1
# min_exploration_rate = 0.01
# exploration_decay_rate = 0.001

exploration_rate = 1
max_exploration_rate = 0.00001
min_exploration_rate = 0.000001
exploration_decay_rate = 0.001

# %%%

rewards_all_episodes = []

# Q-learning algorithm
for episode in range(num_episodes):
    # initialize new episode params
    state = env.reset()
    done = False
    rewards_current_episode = 0
    for step in range(max_steps_per_episode): 
        # Exploration-exploitation trade-off
        exploration_rate_threshold = random.uniform(0, 1)
        if exploration_rate_threshold > exploration_rate:
            action = np.argmax(q_table[state,:]) 
        else:
            action = random.randint(0,1)
        # Take new action
        new_state, reward, done, info = env.step(action)
        # Update Q-table
        q_table[state, action] = q_table[state, action] * (1 - learning_rate) + \
        learning_rate * (reward + discount_rate * np.max(q_table[new_state, :]))
        # Set new state
        state = new_state
        rewards_current_episode += reward 
        if done == True: 
            break
        # Add new reward

    # Exploration rate decay   
    exploration_rate = min_exploration_rate + \
        (max_exploration_rate - min_exploration_rate) * np.exp(-exploration_decay_rate*episode)
    # Add current episode reward to total rewards list
    rewards_all_episodes.append(rewards_current_episode)


# %%

# Calculate and print the average reward per thousand episodes
rewards_per_thousand_episodes = np.split(np.array(rewards_all_episodes),num_episodes/1000)
count = 1000

print("********Average reward per thousand episodes********\n")
for r in rewards_per_thousand_episodes:
    print(count, ": ", str(sum(r/1000)))
    count += 1000

# %%

# Print updated Q-table
print("\n\n********Q-table********\n")
print(q_table)


# %%
# Watch our agent play Frozen Lake by playing the best action 
# from each state according to the Q-table
# input()
for episode in range(10):
    # initialize new episode params
    state = env.reset()
    done = False
    print("*****EPISODE ", episode+1, "*****\n\n\n\n")
    time.sleep(1)
    for step in range(max_steps_per_episode):  

        clear_output(wait=True)
        env.render()
        time.sleep(0.3)

        # Show current state of environment on screen
        # Choose action with highest Q-value for current state       
        # Take new action
        action = np.argmax(q_table[state,:])        
        new_state, reward, done, info = env.step(action)

        if done:
            clear_output(wait=True)
            env.render()
            if reward == 1:
                print("****You won!****")
                time.sleep(3)
            else:
                print("****You lost!****")
                time.sleep(3)
                clear_output(wait=True)
            break

        # Set new state
        state = new_state
env.close()




# %%


print('Policy: Hold at {}\n'.format(', '.join([str(i) for i in range(n) if q_table[i,0] > q_table[i,1]])))

